package top.continew.admin.review.project.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.continew.admin.review.common.enums.TaskType;
import top.continew.admin.review.form.model.resp.FormTemplateResp;
import top.continew.admin.review.form.service.FormTemplateService;
import top.continew.admin.review.project.enums.TaskDecisionEnum;
import top.continew.admin.review.project.mapper.ReviewTaskMapper;
import top.continew.admin.review.project.model.entity.ProjectTypeSnapshot;
import top.continew.admin.review.project.model.entity.ReviewProjectDO;
import top.continew.admin.review.project.model.entity.ReviewTaskDO;
import top.continew.admin.review.project.model.resp.NodeHistoryResp;
import top.continew.admin.system.mapper.user.UserMapper;
import top.continew.admin.system.model.entity.user.UserDO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 节点历史构建器（DRY：TaskService 和 ProjectService 共享）
 *
 * @author zjx
 * @since 2026-03-25
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NodeHistoryBuilder {

    private final ReviewTaskMapper taskMapper;
    private final UserMapper userMapper;
    private final FormTemplateService formTemplateService;
    private final ObjectMapper objectMapper;

    /**
     * 构建申请表单节点
     */
    public NodeHistoryResp buildApplicationNode(ReviewProjectDO project) {
        NodeHistoryResp node = new NodeHistoryResp();
        node.setNodeType("APPLICATION");
        node.setNodeSequence(0);
        node.setNodeName("申请表单");

        // 申请人信息
        NodeHistoryResp.PersonEntryResp entry = new NodeHistoryResp.PersonEntryResp();
        UserDO applicant = userMapper.selectById(project.getApplicantId());
        entry.setAssigneeName(applicant != null ? applicant.getNickname() : "申请人");
        entry.setFormData(project.getApplicationFormData() instanceof Map
                ? (Map<String, Object>) project.getApplicationFormData() : null);

        // 申请表单模板
        try {
            Object config = project.getSnapshotConfig();
            ProjectTypeSnapshot snapshot = config instanceof String
                ? objectMapper.readValue((String) config, ProjectTypeSnapshot.class)
                : objectMapper.convertValue(config, ProjectTypeSnapshot.class);
            if (snapshot != null && snapshot.getFormMappings() != null) {
                Long tplId = snapshot.getFormMappings().get("APPLICATION");
                if (tplId != null) {
                    entry.setFormTemplate(formTemplateService.getDetail(tplId));
                }
            }
        } catch (Exception ignored) {
        }

        node.setEntries(List.of(entry));
        return node;
    }

    /**
     * 构建节点历史列表（按节点分组，填充每人内容）
     */
    public List<NodeHistoryResp> buildNodeHistoryList(
            ReviewProjectDO project, List<ReviewTaskDO> tasks) {
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }

        // 按 taskType_nodeSequence 分组
        Map<String, List<ReviewTaskDO>> grouped = tasks.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTaskType().getValue() + "_" + t.getNodeSequence()));

        // 查所有处理人姓名
        Set<Long> assigneeIds = tasks.stream()
                .map(ReviewTaskDO::getAssigneeId).collect(Collectors.toSet());
        Map<Long, String> nameMap = userMapper.selectBatchIds(assigneeIds).stream()
                .collect(Collectors.toMap(UserDO::getId, UserDO::getNickname));

        List<NodeHistoryResp> nodes = new ArrayList<>();
        for (Map.Entry<String, List<ReviewTaskDO>> entry : grouped.entrySet()) {
            List<ReviewTaskDO> nodeTasks = entry.getValue();
            ReviewTaskDO sample = nodeTasks.get(0);

            NodeHistoryResp node = new NodeHistoryResp();
            node.setNodeType(sample.getTaskType().getValue());
            node.setNodeSequence(sample.getNodeSequence());
            node.setNodeName(resolveNodeName(project, sample.getTaskType(), sample.getNodeSequence()));

            // 汇总（转办场景下可能有多条记录，按不重复处理人统计）
            // 按处理人分组，每人只取最后一条任务的决策
            Map<Long, ReviewTaskDO> latestByAssignee = nodeTasks.stream()
                    .collect(Collectors.toMap(
                            ReviewTaskDO::getAssigneeId,
                            t -> t,
                            (t1, t2) -> t1.getCompleteTime().isAfter(t2.getCompleteTime()) ? t1 : t2));

            List<ReviewTaskDO> completedTasks = new ArrayList<>(latestByAssignee.values());
            long passCount = completedTasks.stream()
                    .filter(t -> t.getDecision() == TaskDecisionEnum.PASS).count();
            node.setPassCount((int) passCount);
            node.setTotalCount(completedTasks.size());

            // 使用与 ResultAggregationEngine 相同的判定逻辑
            String nodeResult = calculateNodeResult(project, sample.getTaskType(),
                    sample.getNodeSequence(), completedTasks, nodeTasks);
            node.setNodeResult(nodeResult);

            log.info("[NodeHistory] 节点{}-{}: 原始任务数={}, 去重后处理人数={}, 通过人数={}, 节点结果={}",
                    sample.getTaskType(), sample.getNodeSequence(), nodeTasks.size(),
                    completedTasks.size(), passCount, nodeResult);
            OptionalDouble avg = nodeTasks.stream()
                    .filter(t -> t.getScore() != null)
                    .mapToDouble(t -> t.getScore().doubleValue()).average();
            if (avg.isPresent()) {
                node.setAverageScore(
                        BigDecimal.valueOf(avg.getAsDouble()).setScale(2, RoundingMode.HALF_UP));
            }

            // 每人填写
            List<NodeHistoryResp.PersonEntryResp> entries = nodeTasks.stream().map(t -> {
                NodeHistoryResp.PersonEntryResp pe = new NodeHistoryResp.PersonEntryResp();
                pe.setAssigneeName(nameMap.getOrDefault(t.getAssigneeId(), "处理人"));
                pe.setDecision(t.getDecision() != null ? t.getDecision().getValue() : null);
                pe.setScore(t.getScore());
                pe.setCompleteTime(t.getCompleteTime());
                if (t.getFormData() instanceof Map) {
                    pe.setFormData((Map<String, Object>) t.getFormData());
                }
                // 加载表单模板
                pe.setFormTemplate(resolveTaskFormTemplate(project,
                        sample.getTaskType(), sample.getNodeSequence()));
                return pe;
            }).collect(Collectors.toList());
            node.setEntries(entries);
            nodes.add(node);
        }

        // 按 AUDIT→REVIEW→DECISION + sequence 排序
        List<String> typeOrder = List.of("AUDIT", "REVIEW", "DECISION");
        nodes.sort(Comparator
                .comparingInt((NodeHistoryResp n) -> typeOrder.indexOf(n.getNodeType()))
                .thenComparingInt(NodeHistoryResp::getNodeSequence));
        return nodes;
    }

    /**
     * 解析节点名称（从快照中读取）
     */
    private String resolveNodeName(ReviewProjectDO project, TaskType taskType, Integer nodeSequence) {
        try {
            Object config = project.getSnapshotConfig();
            ProjectTypeSnapshot snapshot = config instanceof String
                ? objectMapper.readValue((String) config, ProjectTypeSnapshot.class)
                : objectMapper.convertValue(config, ProjectTypeSnapshot.class);
            if (snapshot == null) {
                return taskType.getDescription() + "-" + nodeSequence;
            }
            if (isReviewTask(taskType)) {
                // 从 rounds 找对应轮次名称
                if (snapshot.getRounds() != null) {
                    return snapshot.getRounds().stream()
                            .filter(r -> r.getRoundType().equals(taskType.getValue())
                                    && r.getRoundSequence().equals(nodeSequence))
                            .map(ProjectTypeSnapshot.RoundInfo::getRoundName)
                            .findFirst()
                            .orElse(taskType.getDescription() + " 第" + nodeSequence + "轮");
                }
            } else {
                // 从 stages 找对应阶段名称
                if (snapshot.getStages() != null) {
                    return snapshot.getStages().stream()
                            .filter(s -> s.getStageOrder().equals(nodeSequence))
                            .map(ProjectTypeSnapshot.StageInfo::getStageName)
                            .findFirst()
                            .orElse(taskType.getDescription() + " 第" + nodeSequence + "阶段");
                }
            }
        } catch (Exception e) {
            log.warn("[NodeHistory] 解析节点名称失败：{}", e.getMessage());
        }
        return taskType.getDescription() + "-" + nodeSequence;
    }

    /**
     * 查当前节点的任务表单模板
     */
    private FormTemplateResp resolveTaskFormTemplate(ReviewProjectDO project, TaskType taskType, Integer nodeSequence) {
        try {
            Object config = project.getSnapshotConfig();
            ProjectTypeSnapshot snapshot = config instanceof String
                ? objectMapper.readValue((String) config, ProjectTypeSnapshot.class)
                : objectMapper.convertValue(config, ProjectTypeSnapshot.class);
            if (snapshot == null || snapshot.getFormMappings() == null) {
                return null;
            }
            // key 格式：AUDIT_1 / REVIEW_1 / DECISION_1 / STAGE_1 / ACCEPTANCE
            String key = taskType == TaskType.ACCEPTANCE ? "ACCEPTANCE"
                    : (taskType == TaskType.MANAGEMENT ? "STAGE_" + nodeSequence
                    : taskType.getValue() + "_" + nodeSequence);
            Long formTemplateId = snapshot.getFormMappings().get(key);
            if (formTemplateId != null) {
                return formTemplateService.getDetail(formTemplateId);
            }
        } catch (Exception e) {
            log.warn("[NodeHistory] 解析任务表单模板失败：{}", e.getMessage());
        }
        return null;
    }

    private boolean isReviewTask(TaskType taskType) {
        return taskType == TaskType.AUDIT || taskType == TaskType.REVIEW || taskType == TaskType.DECISION;
    }

    /**
     * 计算节点实际结果（使用与 ResultAggregationEngine 相同的逻辑）
     */
    private String calculateNodeResult(ReviewProjectDO project, TaskType taskType, Integer nodeSequence,
                                        List<ReviewTaskDO> completedTasks, List<ReviewTaskDO> allTasks) {
        try {
            Object config = project.getSnapshotConfig();
            ProjectTypeSnapshot snapshot = config instanceof String
                ? objectMapper.readValue((String) config, ProjectTypeSnapshot.class)
                : objectMapper.convertValue(config, ProjectTypeSnapshot.class);

            if (snapshot == null || snapshot.getApprovalRules() == null) {
                log.warn("[NodeHistory] 快照或审批规则为空，使用默认投票逻辑");
                return completedTasks.stream().allMatch(t -> t.getDecision() == TaskDecisionEnum.PASS) ? "PASS" : "REJECT";
            }

            String nodeScope = taskType == TaskType.ACCEPTANCE ? "ACCEPTANCE" : taskType.getValue() + "_" + nodeSequence;
            ProjectTypeSnapshot.ApprovalRuleInfo rule = snapshot.getApprovalRules().get(nodeScope);
            log.info("[NodeHistory] 节点{}-{} 审批规则: mode={}, threshold={}",
                    taskType, nodeSequence, rule != null ? rule.getApprovalMode() : "null",
                    rule != null ? rule.getPassThreshold() : "null");

            // 计算原始需要人数
            long originalRequiredCount = allTasks.stream()
                .filter(t -> t.getTransferCount() == null || t.getTransferCount() == 0)
                .count();
            log.info("[NodeHistory] 完成人数={}, 原始需要人数={}", completedTasks.size(), originalRequiredCount);

            if (rule == null || "VOTE_ALL_PASS".equals(rule.getApprovalMode())) {
                boolean allPass = completedTasks.stream().allMatch(t -> t.getDecision() == TaskDecisionEnum.PASS);
                boolean allRequired = completedTasks.size() == originalRequiredCount;
                return (allPass && allRequired) ? "PASS" : "REJECT";
            }

            return switch (rule.getApprovalMode()) {
                case "VOTE_MAJORITY_PASS" -> {
                    if (completedTasks.size() != originalRequiredCount) yield "REJECT";
                    long passCount = completedTasks.stream().filter(t -> t.getDecision() == TaskDecisionEnum.PASS).count();
                    BigDecimal ratio = rule.getMajorityRatio() != null ? rule.getMajorityRatio() : new BigDecimal("0.67");
                    BigDecimal actualRatio = BigDecimal.valueOf(passCount).divide(BigDecimal.valueOf(completedTasks.size()), 4, RoundingMode.HALF_UP);
                    yield actualRatio.compareTo(ratio) >= 0 ? "PASS" : "REJECT";
                }
                case "VOTE_ONE_PASS" -> completedTasks.stream().anyMatch(t -> t.getDecision() == TaskDecisionEnum.PASS) ? "PASS" : "REJECT";
                case "SCORE_PASS" -> {
                    if (completedTasks.size() != originalRequiredCount || rule.getPassThreshold() == null) yield "REJECT";
                    List<BigDecimal> scores = completedTasks.stream().filter(t -> t.getScore() != null).map(ReviewTaskDO::getScore).toList();
                    if (scores.isEmpty()) yield "REJECT";
                    BigDecimal finalScore = switch (rule.getScoreCalcMethod() != null ? rule.getScoreCalcMethod() : "SIMPLE_AVG") {
                        case "MAX_SCORE" -> scores.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
                        case "MIN_SCORE" -> scores.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
                        default -> scores.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(scores.size()), 4, RoundingMode.HALF_UP);
                    };
                    yield finalScore.compareTo(rule.getPassThreshold()) >= 0 ? "PASS" : "REJECT";
                }
                default -> "REJECT";
            };
        } catch (Exception e) {
            log.warn("[NodeHistory] 计算节点结果失败：{}", e.getMessage());
            return completedTasks.stream().allMatch(t -> t.getDecision() == TaskDecisionEnum.PASS) ? "PASS" : "REJECT";
        }
    }
}
