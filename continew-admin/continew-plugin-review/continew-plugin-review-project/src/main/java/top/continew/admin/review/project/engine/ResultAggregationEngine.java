package top.continew.admin.review.project.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import top.continew.admin.review.common.enums.TaskType;
import top.continew.admin.review.project.enums.TaskDecisionEnum;
import top.continew.admin.review.project.enums.TaskStatusEnum;
import top.continew.admin.review.project.event.NodeResultEvent;
import top.continew.admin.review.project.mapper.ReviewProjectMapper;
import top.continew.admin.review.project.mapper.ReviewTaskMapper;
import top.continew.admin.review.project.model.entity.ProjectTypeSnapshot;
import top.continew.admin.review.project.model.entity.ReviewProjectDO;
import top.continew.admin.review.project.model.entity.ReviewTaskDO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 结果汇总引擎（投票/评分判定，并发安全）
 *
 * @author zjx
 * @since 2026-03-07
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResultAggregationEngine {

    private final ReviewProjectMapper projectMapper;
    private final ReviewTaskMapper taskMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    /**
     * 汇总指定节点结果（并发安全：SELECT FOR UPDATE 防重复汇总）
     *
     * @param projectId    项目ID
     * @param taskType     任务类型
     * @param nodeSequence 节点序号
     */
    public void aggregate(Long projectId, TaskType taskType, Integer nodeSequence) {
        // 行锁：防止同节点最后两人并发提交时重复汇总
        ReviewProjectDO project = projectMapper.selectByIdForUpdate(projectId);
        if (project == null) {
            log.warn("[Aggregation] 项目不存在：{}", projectId);
            return;
        }

        // 幂等检查：若项目当前节点已推进，说明另一并发已处理
        if (!taskType.getValue().equals(project.getCurrentNodeType())
                || !nodeSequence.equals(project.getCurrentNodeSequence())) {
            log.info("[Aggregation] 项目{} 节点{}-{} 已被推进，跳过重复汇总", projectId, taskType, nodeSequence);
            return;
        }

        // 查本节点所有有效任务（非 CANCELLED）
        List<ReviewTaskDO> tasks = taskMapper.selectList(
                new LambdaQueryWrapper<ReviewTaskDO>()
                        .eq(ReviewTaskDO::getProjectId, projectId)
                        .eq(ReviewTaskDO::getTaskType, taskType)
                        .eq(ReviewTaskDO::getNodeSequence, nodeSequence)
                        .ne(ReviewTaskDO::getStatus, TaskStatusEnum.CANCELLED)
                        .eq(ReviewTaskDO::getDeleted, 0));

        // 检查是否所有任务都完成（COMPLETED 或 TRANSFERRED）
        boolean allDone = tasks.stream()
                .allMatch(t -> t.getStatus() == TaskStatusEnum.COMPLETED
                        || t.getStatus() == TaskStatusEnum.TRANSFERRED);
        if (!allDone) {
            log.info("[Aggregation] 项目{} 节点{}-{} 还有未完成任务，暂不汇总", projectId, taskType, nodeSequence);
            return;
        }

        // 仅取 COMPLETED 的任务参与决策计算
        // 转办场景：同一处理人可能有多条记录，只取最后一条
        List<ReviewTaskDO> completedTasks = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatusEnum.COMPLETED)
                .collect(Collectors.toMap(
                        ReviewTaskDO::getAssigneeId,
                        t -> t,
                        (t1, t2) -> t1.getCompleteTime().isAfter(t2.getCompleteTime()) ? t1 : t2))
                .values().stream().toList();
        log.info("[Aggregation] 项目{} 节点{}-{} 任务统计: 总任务数={}, COMPLETED任务数={}, 去重后处理人数={}",
                projectId, taskType, nodeSequence, tasks.size(),
                tasks.stream().filter(t -> t.getStatus() == TaskStatusEnum.COMPLETED).count(),
                completedTasks.size());
        completedTasks.forEach(t -> log.info("[Aggregation] 处理人ID={}, 决策={}, 状态={}",
                t.getAssigneeId(), t.getDecision(), t.getStatus()));
        if (completedTasks.isEmpty()) {
            log.warn("[Aggregation] 项目{} 节点{}-{} 无已完成任务，无法汇总", projectId, taskType, nodeSequence);
            return;
        }

        // 计算原本需要的人数：初始分配的任务数（transferCount=0或null的任务）
        long originalRequiredCount = tasks.stream()
                .filter(t -> t.getTransferCount() == null || t.getTransferCount() == 0)
                .count();

        // 特殊处理：MANAGEMENT 任务中的 UNQUALIFIED/WITHDRAW 直接透传（不经过投票/评分聚合）
        if (taskType == TaskType.MANAGEMENT) {
            for (ReviewTaskDO task : completedTasks) {
                if (task.getDecision() == TaskDecisionEnum.UNQUALIFIED
                        || task.getDecision() == TaskDecisionEnum.WITHDRAW) {
                    log.info("[Aggregation] 项目{} MANAGEMENT 节点{} 检测到 {} 决策，直接透传",
                            projectId, nodeSequence, task.getDecision());
                    eventPublisher.publishEvent(
                            new NodeResultEvent(this, projectId, taskType, nodeSequence, task.getDecision(), null));
                    return;
                }
            }
        }

        // 读取快照中的审批规则
        ProjectTypeSnapshot snapshot = parseSnapshot(project);
        String nodeScope = buildNodeScope(taskType, nodeSequence);
        ProjectTypeSnapshot.ApprovalRuleInfo rule = snapshot != null
                ? snapshot.getApprovalRules().get(nodeScope) : null;

        TaskDecisionEnum result;
        Integer rejectBackToStageOrder = null;

        if (rule == null || "VOTE_ALL_PASS".equals(rule.getApprovalMode())) {
            // 默认：全部通过才算通过
            // 转办场景：需要检查完成人数是否等于原始需要人数
            boolean allPass = completedTasks.stream().allMatch(t -> t.getDecision() == TaskDecisionEnum.PASS);
            boolean allRequired = completedTasks.size() == originalRequiredCount;
            log.info("[Aggregation] 项目{} 节点{}-{} VOTE_ALL_PASS: allPass={}, allRequired={}, completedSize={}, originalRequired={}",
                    projectId, taskType, nodeSequence, allPass, allRequired, completedTasks.size(), originalRequiredCount);
            result = (allPass && allRequired) ? TaskDecisionEnum.PASS : TaskDecisionEnum.REJECT;
        } else {
            result = switch (rule.getApprovalMode()) {
                case "VOTE_MAJORITY_PASS" -> aggregateByMajority(completedTasks, rule, originalRequiredCount);
                case "VOTE_ONE_PASS" -> aggregateByOnePass(completedTasks);
                case "SCORE_PASS" -> aggregateByScore(completedTasks, rule, originalRequiredCount);
                default -> TaskDecisionEnum.REJECT;
            };
        }

        // 验收节点 REJECT 时，取最后一个 REJECT 任务的 rejectBackToStageOrder
        if (taskType == TaskType.ACCEPTANCE && result == TaskDecisionEnum.REJECT) {
            rejectBackToStageOrder = completedTasks.stream()
                    .filter(t -> t.getDecision() == TaskDecisionEnum.REJECT
                            && t.getRejectBackToStageOrder() != null)
                    .mapToInt(ReviewTaskDO::getRejectBackToStageOrder)
                    .max()
                    .stream().boxed().findFirst().orElse(null);
        }

        log.info("[Aggregation] 项目{} 节点{}-{} 汇总结果：{}", projectId, taskType, nodeSequence, result);
        eventPublisher.publishEvent(
                new NodeResultEvent(this, projectId, taskType, nodeSequence, result, rejectBackToStageOrder));
    }

    private TaskDecisionEnum aggregateByMajority(List<ReviewTaskDO> tasks, ProjectTypeSnapshot.ApprovalRuleInfo rule, long originalRequiredCount) {
        // 转办场景：完成人数必须等于需要人数
        if (tasks.size() != originalRequiredCount) {
            return TaskDecisionEnum.REJECT;
        }
        long passCount = tasks.stream().filter(t -> t.getDecision() == TaskDecisionEnum.PASS).count();
        BigDecimal ratio = rule.getMajorityRatio() != null ? rule.getMajorityRatio() : new BigDecimal("0.67");
        BigDecimal actualRatio = BigDecimal.valueOf(passCount)
                .divide(BigDecimal.valueOf(tasks.size()), 4, RoundingMode.HALF_UP);
        return actualRatio.compareTo(ratio) >= 0 ? TaskDecisionEnum.PASS : TaskDecisionEnum.REJECT;
    }

    private TaskDecisionEnum aggregateByOnePass(List<ReviewTaskDO> tasks) {
        return tasks.stream().anyMatch(t -> t.getDecision() == TaskDecisionEnum.PASS)
                ? TaskDecisionEnum.PASS : TaskDecisionEnum.REJECT;
    }

    private TaskDecisionEnum aggregateByScore(List<ReviewTaskDO> tasks, ProjectTypeSnapshot.ApprovalRuleInfo rule, long originalRequiredCount) {
        // 转办场景：完成人数必须等于需要人数
        if (tasks.size() != originalRequiredCount) {
            return TaskDecisionEnum.REJECT;
        }
        if (rule.getPassThreshold() == null) {
            return TaskDecisionEnum.REJECT;
        }
        List<BigDecimal> scores = tasks.stream()
                .filter(t -> t.getScore() != null)
                .map(ReviewTaskDO::getScore)
                .toList();
        if (scores.isEmpty()) {
            return TaskDecisionEnum.REJECT;
        }
        BigDecimal finalScore = switch (rule.getScoreCalcMethod() != null ? rule.getScoreCalcMethod() : "SIMPLE_AVG") {
            case "MAX_SCORE" -> scores.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            case "MIN_SCORE" -> scores.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            case "WEIGHTED_AVG", "SIMPLE_AVG" -> scores.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(scores.size()), 4, RoundingMode.HALF_UP);
            default -> BigDecimal.ZERO;
        };
        return finalScore.compareTo(rule.getPassThreshold()) >= 0
                ? TaskDecisionEnum.PASS : TaskDecisionEnum.REJECT;
    }

    private ProjectTypeSnapshot parseSnapshot(ReviewProjectDO project) {
        try {
            Object config = project.getSnapshotConfig();
            if (config instanceof String) {
                return objectMapper.readValue((String) config, ProjectTypeSnapshot.class);
            }
            return objectMapper.convertValue(config, ProjectTypeSnapshot.class);
        } catch (Exception e) {
            log.warn("[Aggregation] 解析项目快照失败：{}", e.getMessage());
            return null;
        }
    }

    private String buildNodeScope(TaskType taskType, Integer nodeSequence) {
        if (taskType == TaskType.ACCEPTANCE) {
            return "ACCEPTANCE";
        }
        return taskType.getValue() + "_" + nodeSequence;
    }
}
