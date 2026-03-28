package top.continew.admin.review.project.engine;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import top.continew.admin.review.common.enums.ProjectStatus;
import top.continew.admin.review.common.enums.TaskType;
import top.continew.admin.review.project.enums.StageStatusEnum;
import top.continew.admin.review.project.enums.TaskDecisionEnum;
import top.continew.admin.review.project.enums.TaskStatusEnum;
import top.continew.admin.review.project.event.NodeResultEvent;
import top.continew.admin.review.project.event.ProjectSubmittedEvent;
import top.continew.admin.review.project.event.StageFormSubmittedEvent;
import top.continew.admin.review.project.event.TaskCompletedEvent;
import top.continew.admin.review.project.mapper.ReviewProjectMapper;
import top.continew.admin.review.project.mapper.ReviewProjectStageHistoryMapper;
import top.continew.admin.review.project.mapper.ReviewProjectStageMapper;
import top.continew.admin.review.project.mapper.ReviewTaskMapper;
import top.continew.admin.review.project.model.entity.ProjectTypeSnapshot;
import top.continew.admin.review.project.model.entity.ReviewProjectDO;
import top.continew.admin.review.project.model.entity.ReviewProjectStageDO;
import top.continew.admin.review.project.model.entity.ReviewProjectStageHistoryDO;
import top.continew.admin.review.project.model.entity.ReviewTaskDO;
import top.continew.starter.core.exception.BusinessException;

import java.time.LocalDateTime;
import java.util.List;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * 工作流引擎（事件驱动，监听4类核心事件，驱动项目全生命周期流转）
 *
 * <p>事件→处理对照：
 * <ul>
 *   <li>{@link ProjectSubmittedEvent} → 启动首节点任务分配</li>
 *   <li>{@link TaskCompletedEvent}    → 触发节点汇总（AFTER_COMMIT + REQUIRES_NEW，保证 FOR UPDATE 生效）</li>
 *   <li>{@link NodeResultEvent}       → 根据结果推进或终止流程</li>
 *   <li>{@link StageFormSubmittedEvent} → 阶段成果提交，分配管理/验收任务</li>
 * </ul>
 *
 * @author zjx
 * @since 2026-03-07
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowEngine {

    private final ReviewProjectMapper projectMapper;
    private final ReviewTaskMapper taskMapper;
    private final ReviewProjectStageMapper stageMapper;
    private final ReviewProjectStageHistoryMapper stageHistoryMapper;
    private final TaskAssignmentEngine assignmentEngine;
    private final ResultAggregationEngine aggregationEngine;
    private final ObjectMapper objectMapper;

    // ==================== 事件监听 ====================

    /**
     * 项目提交事件：启动首节点任务分配（同步，与 submit() 同一事务保原子性）
     */
    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void onProjectSubmitted(ProjectSubmittedEvent event) {
        Long projectId = event.getProjectId();
        ReviewProjectDO project = projectMapper.selectById(projectId);
        if (project == null) {
            log.error("[Workflow] 项目不存在：{}", projectId);
            return;
        }
        ProjectTypeSnapshot snapshot = parseSnapshot(project);
        if (snapshot == null || CollUtil.isEmpty(snapshot.getRounds())) {
            throw new BusinessException("项目类型快照缺少轮次配置，无法启动流程");
        }

        // 按 AUDIT→REVIEW→DECISION 顺序排列轮次
        List<ProjectTypeSnapshot.RoundInfo> rounds = snapshot.getRounds();
        rounds.sort(Comparator.<ProjectTypeSnapshot.RoundInfo, Integer>comparing(r -> roundOrder(r.getRoundType()))
                .thenComparingInt(ProjectTypeSnapshot.RoundInfo::getRoundSequence));

        ProjectTypeSnapshot.RoundInfo first = rounds.get(0);
        TaskType taskType = TaskType.valueOf(first.getRoundType());
        Integer nodeSequence = first.getRoundSequence();

        // 更新项目当前节点
        project.setCurrentNodeType(taskType.getValue());
        project.setCurrentNodeSequence(nodeSequence);
        project.setStatus(statusForReviewNode(taskType));
        projectMapper.updateById(project);

        // 分配任务
        assignmentEngine.assignTasks(projectId, taskType, nodeSequence);
        log.info("[Workflow] 项目{} 首节点 {}-{} 任务已分配", projectId, taskType, nodeSequence);
    }

    /**
     * 任务完成事件：触发节点结果汇总
     * 使用 AFTER_COMMIT + REQUIRES_NEW 确保：
     * 1. 任务状态已提交后才汇总（防读到旧状态）
     * 2. 新事务中执行 SELECT FOR UPDATE（防并发重复汇总）
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void onTaskCompleted(TaskCompletedEvent event) {
        aggregationEngine.aggregate(event.getProjectId(), event.getTaskType(), event.getNodeSequence());
    }

    /**
     * 节点结果事件：根据结果推进或终止流程（同步，与 aggregate() 同一事务）
     */
    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void onNodeResult(NodeResultEvent event) {
        Long projectId = event.getProjectId();
        TaskType taskType = event.getTaskType();
        Integer nodeSequence = event.getNodeSequence();
        TaskDecisionEnum result = event.getResult();

        log.info("[Workflow] 项目{} 节点{}-{} 结果={}", projectId, taskType, nodeSequence, result);

        ReviewProjectDO project = projectMapper.selectById(projectId);
        if (project == null) {
            log.error("[Workflow] 项目不存在：{}", projectId);
            return;
        }
        ProjectTypeSnapshot snapshot = parseSnapshot(project);

        switch (taskType) {
            case AUDIT, REVIEW, DECISION -> handleReviewNodeResult(project, snapshot, taskType, nodeSequence, result);
            case MANAGEMENT -> handleManagementNodeResult(project, snapshot, nodeSequence, result,
                                                           event.getRejectBackToStageOrder());
            case ACCEPTANCE -> handleAcceptanceNodeResult(project, nodeSequence, result, event.getRejectBackToStageOrder());
        }
    }

    /**
     * 阶段成果提交事件：更新阶段状态为 SUBMITTED，分配管理/验收任务
     * （同步，与 submitStageForm() 同一事务）
     */
    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void onStageFormSubmitted(StageFormSubmittedEvent event) {
        Long projectId = event.getProjectId();
        Long stageId = event.getStageId();
        Integer stageOrder = event.getStageOrder();

        ReviewProjectStageDO stage = stageMapper.selectById(stageId);
        if (stage == null) {
            log.error("[Workflow] 阶段不存在：stageId={}", stageId);
            return;
        }

        // 阶段状态 → SUBMITTED
        stage.setStatus(StageStatusEnum.SUBMITTED);
        stageMapper.updateById(stage);

        // 根据阶段类型决定任务类型（ACCEPTANCE 阶段分配 ACCEPTANCE 任务，其余分配 MANAGEMENT 任务）
        TaskType assignType = stage.getStageType() == TaskType.ACCEPTANCE
                ? TaskType.ACCEPTANCE : TaskType.MANAGEMENT;
        // 人员配置基于原始阶段序号，任务的 nodeSequence 使用实际 stageOrder
        Integer originalOrder = stage.getOriginalStageOrder() != null ? stage.getOriginalStageOrder() : stageOrder;
        assignmentEngine.assignTasks(projectId, assignType, originalOrder, stageOrder);
        log.info("[Workflow] 项目{} 阶段{} 成果已提交，分配 {} 任务（人员配置查 originalOrder={}）",
                projectId, stageOrder, assignType, originalOrder);
    }

    // ==================== 评审阶段推进 ====================

    private void handleReviewNodeResult(ReviewProjectDO project, ProjectTypeSnapshot snapshot,
                                         TaskType taskType, Integer nodeSequence, TaskDecisionEnum result) {
        Long projectId = project.getId();

        log.info("[Workflow] handleReviewNodeResult: 项目={}, 节点={}-{}, 结果={}", projectId, taskType, nodeSequence, result);
        if (result == TaskDecisionEnum.REJECT) {
            // 驳回 → 终止
            log.info("[Workflow] 评审节点驳回，即将终止项目{}", projectId);
            terminateProject(project, "评审节点 " + taskType + "-" + nodeSequence + " 驳回");
            return;
        }

        // PASS → 查下一节点
        List<ProjectTypeSnapshot.RoundInfo> rounds = snapshot.getRounds();
        rounds.sort(Comparator.<ProjectTypeSnapshot.RoundInfo, Integer>comparing(r -> roundOrder(r.getRoundType()))
                .thenComparingInt(ProjectTypeSnapshot.RoundInfo::getRoundSequence));

        int currentIdx = -1;
        for (int i = 0; i < rounds.size(); i++) {
            ProjectTypeSnapshot.RoundInfo r = rounds.get(i);
            if (r.getRoundType().equals(taskType.getValue()) && r.getRoundSequence().equals(nodeSequence)) {
                currentIdx = i;
                break;
            }
        }

        if (currentIdx < 0) {
            log.error("[Workflow] 项目{} 未在快照中找到当前节点 {}-{}", projectId, taskType, nodeSequence);
            return;
        }

        if (currentIdx + 1 < rounds.size()) {
            // 推进到下一轮次
            ProjectTypeSnapshot.RoundInfo next = rounds.get(currentIdx + 1);
            TaskType nextType = TaskType.valueOf(next.getRoundType());
            project.setCurrentNodeType(nextType.getValue());
            project.setCurrentNodeSequence(next.getRoundSequence());
            project.setStatus(statusForReviewNode(nextType));
            projectMapper.updateById(project);
            assignmentEngine.assignTasks(projectId, nextType, next.getRoundSequence());
            log.info("[Workflow] 项目{} 推进至节点 {}-{}", projectId, nextType, next.getRoundSequence());
        } else {
            // 所有轮次结束 → 进入执行阶段
            startExecutionPhase(project, snapshot);
        }
    }

    // ==================== 管理阶段推进 ====================

    private void handleManagementNodeResult(ReviewProjectDO project, ProjectTypeSnapshot snapshot,
                                             Integer stageOrder, TaskDecisionEnum result,
                                             Integer rejectBackToStageOrder) {
        Long projectId = project.getId();

        // 查当前阶段实例
        ReviewProjectStageDO currentStage = findStageByOrder(projectId, stageOrder);
        if (currentStage == null) {
            log.error("[Workflow] 项目{} 阶段{} 不存在", projectId, stageOrder);
            return;
        }

        switch (result) {
            case PASS -> {
                // 当前阶段完成 → 推进下一阶段
                currentStage.setStatus(StageStatusEnum.COMPLETED);
                stageMapper.updateById(currentStage);

                // 找下一阶段
                ReviewProjectStageDO nextStage = findNextStage(projectId, stageOrder);
                if (nextStage != null) {
                    activateStage(nextStage);
                    // 若下一阶段是 MANAGEMENT 类型，分配 STAGE_SUBMISSION 给申请人
                    if (nextStage.getStageType() != TaskType.ACCEPTANCE) {
                        assignStageSubmissionTask(projectId, project.getApplicantId(), nextStage);
                    }
                    // 根据阶段类���更新项目状态
                    TaskType nextTaskType = nextStage.getStageType() == TaskType.ACCEPTANCE
                            ? TaskType.ACCEPTANCE : TaskType.MANAGEMENT;
                    project.setCurrentNodeType(nextTaskType.getValue());
                    project.setCurrentNodeSequence(nextStage.getStageOrder());
                    project.setStatus(nextStage.getStageType() == TaskType.ACCEPTANCE
                            ? ProjectStatus.ACCEPTING : ProjectStatus.EXECUTING);
                    projectMapper.updateById(project);
                    log.info("[Workflow] 项目{} 阶段{} 完成，下一阶段{}（{}）激活",
                            projectId, stageOrder, nextStage.getStageOrder(), nextStage.getStageType());
                } else {
                    // 无下一阶段（不应发生：验收阶段完成通过 ACCEPTANCE 处理）
                    log.warn("[Workflow] 项目{} MANAGEMENT 阶段{} 通过但无下一阶段，归档为已完成", projectId, stageOrder);
                    archiveProject(project, ProjectStatus.ARCHIVED_COMPLETED, "管理阶段全部完成，无验收阶段");
                }
            }
            case REJECT -> {
                // 标记当前阶段为已驳回（保留数据）
                currentStage.setStatus(StageStatusEnum.REJECTED);
                stageMapper.updateById(currentStage);

                Integer backTo = (rejectBackToStageOrder != null && rejectBackToStageOrder < stageOrder)
                        ? rejectBackToStageOrder : stageOrder;
                // 获取回退目标的 originalStageOrder
                Integer backToOriginal;
                if (backTo < stageOrder) {
                    ReviewProjectStageDO targetStage = findStageByOrder(projectId, backTo);
                    if (targetStage == null) {
                        log.error("[Workflow] 项目{} 管理驳回但目标阶段{}不存在", projectId, backTo);
                        return;
                    }
                    // 防御性校验：不允许回退到 ACCEPTANCE 阶段
                    if (targetStage.getStageType() == TaskType.ACCEPTANCE) {
                        log.error("[Workflow] 项目{} 管理驳回目标阶段{}为 ACCEPTANCE 类型，降级为重做当前阶段", projectId, backTo);
                        backToOriginal = currentStage.getOriginalStageOrder();
                    } else {
                        backToOriginal = targetStage.getOriginalStageOrder();
                    }
                } else {
                    backToOriginal = currentStage.getOriginalStageOrder();
                }

                // 把从回退目标到当前阶段之间的所有活跃阶段也标记为 REJECTED
                markIntermediateStagesRejected(projectId, backToOriginal, stageOrder);

                // 取消所有待处理的管理阶段任务
                cancelAllPendingTasks(projectId);

                // 创建新阶段实例（从回退目标到验收，全部重建）
                recreateStagesFrom(projectId, backToOriginal, snapshot);

                // 查找新创建的第一个阶段并激活
                ReviewProjectStageDO newFirstStage = findLatestStageByOriginalOrder(projectId, backToOriginal);
                if (newFirstStage == null) {
                    log.error("[Workflow] 项目{} 重建阶段后未找到 originalOrder={} 的新阶段", projectId, backToOriginal);
                    return;
                }
                activateStage(newFirstStage);
                assignStageSubmissionTask(projectId, project.getApplicantId(), newFirstStage);

                project.setCurrentNodeType(TaskType.MANAGEMENT.getValue());
                project.setCurrentNodeSequence(newFirstStage.getStageOrder());
                project.setStatus(ProjectStatus.EXECUTING);
                projectMapper.updateById(project);
                log.info("[Workflow] 项目{} 管理阶段{}驳回，重建阶段从 original={} 开始，新阶段{}已激活",
                        projectId, stageOrder, backToOriginal, newFirstStage.getStageOrder());
            }
            case UNQUALIFIED -> {
                // 不合格 → 归档
                archiveProject(project, ProjectStatus.ARCHIVED_UNQUALIFIED, "管理阶段评定为不合格");
            }
            case WITHDRAW -> {
                // 撤销 → 归档已取消
                archiveProject(project, ProjectStatus.ARCHIVED_CANCELLED, "管理阶段项目撤销");
            }
            default -> log.warn("[Workflow] 项目{} MANAGEMENT 节点{} 未处理的结果：{}", projectId, stageOrder, result);
        }
    }

    // ==================== 验收阶段推进 ====================

    /**
     * 处理 ACCEPTANCE 任务完成事件
     *
     * <p>任务分配逻辑说明：
     * <ul>
     *   <li>PASS：项目归档为已完成，不再分配新任务</li>
     *   <li>REJECT：回退到指定阶段（默认为上一阶段），分配 STAGE_SUBMISSION 任务给申请人重新提交阶段成果</li>
     * </ul>
     *
     * @param project 项目实体
     * @param stageOrder 验收阶段序号
     * @param result 验收结果（PASS/REJECT）
     * @param rejectBackToStageOrder 驳回回退目标阶段序号（null 则回退到上一阶段）
     */
    private void handleAcceptanceNodeResult(ReviewProjectDO project, Integer stageOrder,
                                             TaskDecisionEnum result, Integer rejectBackToStageOrder) {
        Long projectId = project.getId();

        ReviewProjectStageDO acceptanceStage = findStageByOrder(projectId, stageOrder);
        if (acceptanceStage == null) {
            log.error("[Workflow] 项目{} 验收阶段{} 不存在", projectId, stageOrder);
            return;
        }

        if (result == TaskDecisionEnum.PASS) {
            acceptanceStage.setStatus(StageStatusEnum.COMPLETED);
            stageMapper.updateById(acceptanceStage);
            archiveProject(project, ProjectStatus.ARCHIVED_COMPLETED, "验收通过");
        } else if (result == TaskDecisionEnum.REJECT) {
            // 验收阶段标记为 REJECTED
            acceptanceStage.setStatus(StageStatusEnum.REJECTED);
            stageMapper.updateById(acceptanceStage);

            Integer backTo = rejectBackToStageOrder != null ? rejectBackToStageOrder : stageOrder - 1;
            // 获取回退目标的 originalStageOrder
            ReviewProjectStageDO targetStage = findStageByOrder(projectId, backTo);
            Integer backToOriginal = targetStage != null ? targetStage.getOriginalStageOrder() : backTo;

            // 把从回退目标到当前阶段之间的所有活跃阶段也标记为 REJECTED
            markIntermediateStagesRejected(projectId, backToOriginal, stageOrder);

            // 取消所有待处理任务
            cancelAllPendingTasks(projectId);

            // 解析快照用于重建阶段
            ProjectTypeSnapshot snapshot = parseSnapshot(project);

            // 创建新阶段实例（从回退目标到验收，全部重建）
            recreateStagesFrom(projectId, backToOriginal, snapshot);

            // 查找新创建的第一个阶段并激活
            ReviewProjectStageDO newFirstStage = findLatestStageByOriginalOrder(projectId, backToOriginal);
            if (newFirstStage == null) {
                log.error("[Workflow] 项目{} 验收驳回重建阶段后未找到 originalOrder={} 的新阶段", projectId, backToOriginal);
                return;
            }
            activateStage(newFirstStage);
            assignStageSubmissionTask(projectId, project.getApplicantId(), newFirstStage);

            project.setCurrentNodeType(TaskType.MANAGEMENT.getValue());
            project.setCurrentNodeSequence(newFirstStage.getStageOrder());
            project.setStatus(ProjectStatus.EXECUTING);
            projectMapper.updateById(project);
            log.info("[Workflow] 项目{} 验收驳回，重建阶段从 original={} 开始，新阶段{}已激活",
                    projectId, backToOriginal, newFirstStage.getStageOrder());
        } else {
            log.warn("[Workflow] 项目{} 验收阶段{} 未处理的结果：{}", projectId, stageOrder, result);
        }
    }

    // ==================== 执行阶段初始化 ====================

    private void startExecutionPhase(ReviewProjectDO project, ProjectTypeSnapshot snapshot) {
        Long projectId = project.getId();
        List<ProjectTypeSnapshot.StageInfo> stageInfos = snapshot.getStages();
        if (CollUtil.isEmpty(stageInfos)) {
            // 无执行阶段配置 → 直接归档（极端情况）
            log.warn("[Workflow] 项目{} 无阶段配置，决策通过后直接归档完成", projectId);
            archiveProject(project, ProjectStatus.ARCHIVED_COMPLETED, "决策通过，无执行阶段");
            return;
        }

        // 按 stageOrder 排序
        stageInfos.sort(Comparator.comparingInt(ProjectTypeSnapshot.StageInfo::getStageOrder));

        // 删除旧阶段（重跑时幂等）并批量创建新阶段实例
        stageMapper.delete(new LambdaQueryWrapper<ReviewProjectStageDO>()
                .eq(ReviewProjectStageDO::getProjectId, projectId));

        List<ReviewProjectStageDO> stageDOs = stageInfos.stream().map(info -> {
            ReviewProjectStageDO s = new ReviewProjectStageDO();
            s.setProjectId(projectId);
            s.setStageType("ACCEPTANCE".equals(info.getStageType())
                    ? TaskType.ACCEPTANCE : TaskType.MANAGEMENT);
            s.setStageOrder(info.getStageOrder());
            s.setOriginalStageOrder(info.getStageOrder());
            s.setExecutionSequence(1);
            s.setStageName(info.getStageName());
            s.setPlannedDays(info.getPlannedDays());
            s.setStatus(StageStatusEnum.PENDING);
            s.setIsOverdue(false);
            return s;
        }).toList();
        stageMapper.insertBatch(stageDOs);
        log.info("[Workflow] 项目{} 创建 {} 个阶段实例", projectId, stageDOs.size());

        // 激活第一个阶段
        // 重新 selectList 获取带 ID 的阶段（insertBatch 后 ID 已回填）
        List<ReviewProjectStageDO> savedStages = stageMapper.selectList(
                new LambdaQueryWrapper<ReviewProjectStageDO>()
                        .eq(ReviewProjectStageDO::getProjectId, projectId)
                        .orderByAsc(ReviewProjectStageDO::getStageOrder));

        ReviewProjectStageDO firstStage = savedStages.get(0);
        activateStage(firstStage);

        TaskType firstTaskType = firstStage.getStageType() == TaskType.ACCEPTANCE
                ? TaskType.ACCEPTANCE : TaskType.MANAGEMENT;
        project.setCurrentNodeType(firstTaskType.getValue());
        project.setCurrentNodeSequence(firstStage.getStageOrder());
        project.setStatus(firstStage.getStageType() == TaskType.ACCEPTANCE
                ? ProjectStatus.ACCEPTING : ProjectStatus.EXECUTING);
        projectMapper.updateById(project);

        // 分配 STAGE_SUBMISSION 任务给申请人（申请人在"我的任务"中提交阶段成果）
        assignStageSubmissionTask(projectId, project.getApplicantId(), firstStage);
        log.info("[Workflow] 项目{} 进入执行阶段，首阶段{}（{}）已激活，STAGE_SUBMISSION 任务已分配",
                projectId, firstStage.getStageOrder(), firstStage.getStageType());
    }

    // ==================== 通用工具方法 ====================

    private void terminateProject(ReviewProjectDO project, String reason) {
        project.setStatus(ProjectStatus.TERMINATED);
        project.setCurrentNodeType(null);
        project.setCurrentNodeSequence(null);
        projectMapper.updateById(project);
        cancelAllPendingTasks(project.getId());
        log.info("[Workflow] 项目{} 已终止，原因：{}", project.getId(), reason);
    }

    private void archiveProject(ReviewProjectDO project, ProjectStatus archiveStatus, String reason) {
        project.setStatus(archiveStatus);
        project.setCurrentNodeType(null);
        project.setCurrentNodeSequence(null);
        projectMapper.updateById(project);
        cancelAllPendingTasks(project.getId());
        log.info("[Workflow] 项目{} 归档为 {}，原因：{}", project.getId(), archiveStatus, reason);
    }

    private void activateStage(ReviewProjectStageDO stage) {
        StageStatusEnum oldStatus = stage.getStatus();
        stage.setStatus(StageStatusEnum.IN_PROGRESS);
        stage.setStartDate(LocalDate.now());
        if (stage.getPlannedDays() != null && stage.getPlannedDays() > 0) {
            stage.setDeadline(LocalDate.now().plusDays(stage.getPlannedDays()));
        }
        stageMapper.updateById(stage);
        // 记录状态变更历史（仅当原状态不为 PENDING 时，如 REJECTED → IN_PROGRESS）
        if (oldStatus != null && oldStatus != StageStatusEnum.PENDING) {
            ReviewProjectStageHistoryDO history = new ReviewProjectStageHistoryDO();
            history.setStageId(stage.getId());
            history.setOldStatus(oldStatus.getValue());
            history.setNewStatus(StageStatusEnum.IN_PROGRESS.getValue());
            history.setChangeTime(LocalDateTime.now());
            history.setRemark(oldStatus + " → IN_PROGRESS");
            stageHistoryMapper.insert(history);
        }
    }

    /**
     * 取消指定阶段序号（含）之后的所有 MANAGEMENT 和 ACCEPTANCE 待处理任务
     * 用于验收驳回回退时清理后续阶段的已分配任务
     */
    private void cancelPendingTasksFromStage(Long projectId, Integer fromStageOrder) {
        LambdaQueryWrapper<ReviewTaskDO> wrapper = new LambdaQueryWrapper<ReviewTaskDO>()
                .eq(ReviewTaskDO::getProjectId, projectId)
                .in(ReviewTaskDO::getTaskType, List.of(TaskType.MANAGEMENT, TaskType.ACCEPTANCE))
                .ge(ReviewTaskDO::getNodeSequence, fromStageOrder)
                .in(ReviewTaskDO::getStatus, List.of(TaskStatusEnum.PENDING, TaskStatusEnum.SAVED))
                .eq(ReviewTaskDO::getDeleted, 0);
        ReviewTaskDO update = new ReviewTaskDO();
        update.setStatus(TaskStatusEnum.CANCELLED);
        taskMapper.update(update, wrapper);
    }

    private void cancelPendingTasks(Long projectId, TaskType taskType, Integer nodeSequence) {
        LambdaQueryWrapper<ReviewTaskDO> wrapper = new LambdaQueryWrapper<ReviewTaskDO>()
                .eq(ReviewTaskDO::getProjectId, projectId)
                .eq(ReviewTaskDO::getTaskType, taskType)
                .eq(ReviewTaskDO::getNodeSequence, nodeSequence)
                .in(ReviewTaskDO::getStatus, List.of(TaskStatusEnum.PENDING, TaskStatusEnum.SAVED))
                .eq(ReviewTaskDO::getDeleted, 0);
        ReviewTaskDO update = new ReviewTaskDO();
        update.setStatus(TaskStatusEnum.CANCELLED);
        taskMapper.update(update, wrapper);
    }

    private void cancelAllPendingTasks(Long projectId) {
        LambdaQueryWrapper<ReviewTaskDO> wrapper = new LambdaQueryWrapper<ReviewTaskDO>()
                .eq(ReviewTaskDO::getProjectId, projectId)
                .in(ReviewTaskDO::getStatus, List.of(TaskStatusEnum.PENDING, TaskStatusEnum.SAVED))
                .eq(ReviewTaskDO::getDeleted, 0);
        ReviewTaskDO update = new ReviewTaskDO();
        update.setStatus(TaskStatusEnum.CANCELLED);
        taskMapper.update(update, wrapper);
    }

    private void resetStagesFrom(Long projectId, Integer fromStageOrder) {
        // 保存驳回前的阶段快照到历史表
        List<ReviewProjectStageDO> stagesToReset = stageMapper.selectList(
                new LambdaQueryWrapper<ReviewProjectStageDO>()
                        .eq(ReviewProjectStageDO::getProjectId, projectId)
                        .ge(ReviewProjectStageDO::getStageOrder, fromStageOrder)
                        .ne(ReviewProjectStageDO::getStatus, StageStatusEnum.PENDING)
                        .eq(ReviewProjectStageDO::getDeleted, 0));
        for (ReviewProjectStageDO stage : stagesToReset) {
            saveStageSnapshot(stage, "REJECTED");
        }

        // 重置阶段状态
        LambdaQueryWrapper<ReviewProjectStageDO> wrapper = new LambdaQueryWrapper<ReviewProjectStageDO>()
                .eq(ReviewProjectStageDO::getProjectId, projectId)
                .ge(ReviewProjectStageDO::getStageOrder, fromStageOrder);
        ReviewProjectStageDO update = new ReviewProjectStageDO();
        update.setStatus(StageStatusEnum.PENDING);
        update.setStartDate(null);
        update.setDeadline(null);
        update.setStageFormData(null);
        stageMapper.update(update, wrapper);
    }

    /**
     * 保存阶段快照到历史表（驳回前调用）
     */
    private void saveStageSnapshot(ReviewProjectStageDO stage, String newStatus) {
        ReviewProjectStageHistoryDO history = new ReviewProjectStageHistoryDO();
        history.setProjectId(stage.getProjectId());
        history.setStageId(stage.getId());
        history.setStageType(stage.getStageType());
        history.setStageOrder(stage.getStageOrder());
        history.setStageName(stage.getStageName());
        history.setOldStatus(stage.getStatus() != null ? stage.getStatus().getValue() : null);
        history.setNewStatus(newStatus);
        history.setStartDate(stage.getStartDate());
        history.setDeadline(stage.getDeadline());
        history.setStageFormData(stage.getStageFormData());
        history.setChangeTime(LocalDateTime.now());
        history.setRemark(stage.getStageName() + " " + history.getOldStatus() + " → " + newStatus);
        stageHistoryMapper.insert(history);
        log.info("[Workflow] 保存阶段快照：项目{} 阶段{} {} → {}", stage.getProjectId(), stage.getStageOrder(), history.getOldStatus(), newStatus);
    }

    private ReviewProjectStageDO findStageByOrder(Long projectId, Integer stageOrder) {
        return stageMapper.selectOne(new LambdaQueryWrapper<ReviewProjectStageDO>()
                .eq(ReviewProjectStageDO::getProjectId, projectId)
                .eq(ReviewProjectStageDO::getStageOrder, stageOrder)
                .eq(ReviewProjectStageDO::getDeleted, 0));
    }

    private ReviewProjectStageDO findNextStage(Long projectId, Integer currentStageOrder) {
        return stageMapper.selectOne(new LambdaQueryWrapper<ReviewProjectStageDO>()
                .eq(ReviewProjectStageDO::getProjectId, projectId)
                .gt(ReviewProjectStageDO::getStageOrder, currentStageOrder)
                .ne(ReviewProjectStageDO::getStatus, StageStatusEnum.REJECTED)
                .eq(ReviewProjectStageDO::getDeleted, 0)
                .orderByAsc(ReviewProjectStageDO::getStageOrder)
                .last("LIMIT 1"));
    }

    /**
     * 查找指定 originalStageOrder 的最新阶段实例（executionSequence 最大的那个）
     */
    private ReviewProjectStageDO findLatestStageByOriginalOrder(Long projectId, Integer originalStageOrder) {
        return stageMapper.selectOne(new LambdaQueryWrapper<ReviewProjectStageDO>()
                .eq(ReviewProjectStageDO::getProjectId, projectId)
                .eq(ReviewProjectStageDO::getOriginalStageOrder, originalStageOrder)
                .eq(ReviewProjectStageDO::getDeleted, 0)
                .orderByDesc(ReviewProjectStageDO::getExecutionSequence)
                .last("LIMIT 1"));
    }

    /**
     * 把从回退目标到当前阶段之间的所有活跃阶段标记为 REJECTED
     * （不含当前阶段本身，因为已经在调用前标记了）
     */
    private void markIntermediateStagesRejected(Long projectId, Integer backToOriginalOrder, Integer currentStageOrder) {
        List<ReviewProjectStageDO> intermediateStages = stageMapper.selectList(
                new LambdaQueryWrapper<ReviewProjectStageDO>()
                        .eq(ReviewProjectStageDO::getProjectId, projectId)
                        .ge(ReviewProjectStageDO::getOriginalStageOrder, backToOriginalOrder)
                        .lt(ReviewProjectStageDO::getStageOrder, currentStageOrder)
                        .notIn(ReviewProjectStageDO::getStatus,
                                List.of(StageStatusEnum.REJECTED, StageStatusEnum.PENDING))
                        .eq(ReviewProjectStageDO::getDeleted, 0));
        for (ReviewProjectStageDO stage : intermediateStages) {
            stage.setStatus(StageStatusEnum.REJECTED);
            stageMapper.updateById(stage);
            log.info("[Workflow] 项目{} 中间阶段{} (original={}) 标记为 REJECTED",
                    projectId, stage.getStageOrder(), stage.getOriginalStageOrder());
        }
    }

    /**
     * 驳回后重建阶段实例（从 fromOriginalOrder 开始到验收阶段，全部创建新实例）
     */
    private void recreateStagesFrom(Long projectId, Integer fromOriginalOrder, ProjectTypeSnapshot snapshot) {
        if (snapshot == null || CollUtil.isEmpty(snapshot.getStages())) {
            log.error("[Workflow] 项目{} 快照无阶段配置，无法重建", projectId);
            return;
        }

        // 软删除旧的 PENDING 阶段（originalStageOrder >= fromOriginalOrder），避免重复显示
        List<ReviewProjectStageDO> pendingStages = stageMapper.selectList(
                new LambdaQueryWrapper<ReviewProjectStageDO>()
                        .eq(ReviewProjectStageDO::getProjectId, projectId)
                        .ge(ReviewProjectStageDO::getOriginalStageOrder, fromOriginalOrder)
                        .eq(ReviewProjectStageDO::getStatus, StageStatusEnum.PENDING)
                        .eq(ReviewProjectStageDO::getDeleted, 0));
        for (ReviewProjectStageDO ps : pendingStages) {
            ps.setDeleted(ps.getId());
            stageMapper.updateById(ps);
        }

        // 查询当前最大 stageOrder
        Integer maxOrder = stageMapper.selectMaxStageOrder(projectId);

        // 从快照中找到需要重建的阶段模板（>= fromOriginalOrder 的所有阶段）
        List<ProjectTypeSnapshot.StageInfo> stagesToRecreate = snapshot.getStages().stream()
                .filter(s -> s.getStageOrder() >= fromOriginalOrder)
                .sorted(Comparator.comparingInt(ProjectTypeSnapshot.StageInfo::getStageOrder))
                .toList();

        // 查询每个原始阶段的最大执行序号
        List<ReviewProjectStageDO> existingStages = stageMapper.selectList(
                new LambdaQueryWrapper<ReviewProjectStageDO>()
                        .eq(ReviewProjectStageDO::getProjectId, projectId)
                        .ge(ReviewProjectStageDO::getOriginalStageOrder, fromOriginalOrder)
                        .eq(ReviewProjectStageDO::getDeleted, 0));
        java.util.Map<Integer, Integer> maxExecSeq = new java.util.HashMap<>();
        for (ReviewProjectStageDO s : existingStages) {
            maxExecSeq.merge(s.getOriginalStageOrder(), s.getExecutionSequence(), Math::max);
        }

        // 创建新阶段实例
        List<ReviewProjectStageDO> newStages = new java.util.ArrayList<>();
        for (ProjectTypeSnapshot.StageInfo info : stagesToRecreate) {
            ReviewProjectStageDO newStage = new ReviewProjectStageDO();
            newStage.setProjectId(projectId);
            newStage.setStageOrder(++maxOrder);
            newStage.setOriginalStageOrder(info.getStageOrder());
            newStage.setExecutionSequence(maxExecSeq.getOrDefault(info.getStageOrder(), 0) + 1);
            newStage.setStageType("ACCEPTANCE".equals(info.getStageType())
                    ? TaskType.ACCEPTANCE : TaskType.MANAGEMENT);
            newStage.setStageName(info.getStageName());
            newStage.setPlannedDays(info.getPlannedDays());
            newStage.setStatus(StageStatusEnum.PENDING);
            newStage.setIsOverdue(false);
            newStages.add(newStage);
        }

        stageMapper.insertBatch(newStages);
        log.info("[Workflow] 项目{} 重建 {} 个阶段实例（从 originalOrder={} 开始）",
                projectId, newStages.size(), fromOriginalOrder);
    }

    private ProjectTypeSnapshot parseSnapshot(ReviewProjectDO project) {
        try {
            Object config = project.getSnapshotConfig();
            if (config instanceof String) {
                return objectMapper.readValue((String) config, ProjectTypeSnapshot.class);
            }
            return objectMapper.convertValue(config, ProjectTypeSnapshot.class);
        } catch (Exception e) {
            log.error("[Workflow] 解析快照失败，projectId={}：{}", project.getId(), e.getMessage());
            return null;
        }
    }

    private ProjectStatus statusForReviewNode(TaskType taskType) {
        return switch (taskType) {
            case AUDIT -> ProjectStatus.AUDITING;
            case REVIEW -> ProjectStatus.REVIEWING;
            case DECISION -> ProjectStatus.DECIDING;
            default -> ProjectStatus.EXECUTING;
        };
    }

    /** 轮次排序权重（AUDIT < REVIEW < DECISION） */
    private int roundOrder(String roundType) {
        return switch (roundType) {
            case "AUDIT" -> 1;
            case "REVIEW" -> 2;
            case "DECISION" -> 3;
            default -> 99;
        };
    }

    /**
     * 取消指定阶段序号（含）之后的所有 MANAGEMENT、ACCEPTANCE、STAGE_SUBMISSION 待处理任务
     */
    private void cancelPendingTasksFromStageAll(Long projectId, Integer fromStageOrder) {
        LambdaQueryWrapper<ReviewTaskDO> wrapper = new LambdaQueryWrapper<ReviewTaskDO>()
                .eq(ReviewTaskDO::getProjectId, projectId)
                .in(ReviewTaskDO::getTaskType,
                        List.of(TaskType.MANAGEMENT, TaskType.ACCEPTANCE, TaskType.STAGE_SUBMISSION))
                .ge(ReviewTaskDO::getNodeSequence, fromStageOrder)
                .in(ReviewTaskDO::getStatus, List.of(TaskStatusEnum.PENDING, TaskStatusEnum.SAVED))
                .eq(ReviewTaskDO::getDeleted, 0);
        ReviewTaskDO update = new ReviewTaskDO();
        update.setStatus(TaskStatusEnum.CANCELLED);
        taskMapper.update(update, wrapper);
    }

    /**
     * 为申请人创建 STAGE_SUBMISSION 任务（阶段激活后由引擎调用，无须查询人员配置）
     */
    private void assignStageSubmissionTask(Long projectId, Long applicantId,
                                            ReviewProjectStageDO stage) {
        // 幂等：若已有 PENDING/SAVED/COMPLETED 的 STAGE_SUBMISSION 任务则跳过
        long existing = taskMapper.selectCount(new LambdaQueryWrapper<ReviewTaskDO>()
                .eq(ReviewTaskDO::getProjectId, projectId)
                .eq(ReviewTaskDO::getTaskType, TaskType.STAGE_SUBMISSION)
                .eq(ReviewTaskDO::getNodeSequence, stage.getStageOrder())
                .in(ReviewTaskDO::getStatus, List.of(TaskStatusEnum.PENDING, TaskStatusEnum.SAVED, TaskStatusEnum.COMPLETED))
                .eq(ReviewTaskDO::getDeleted, 0));
        if (existing > 0) {
            log.info("[Workflow] 项目{} 阶段{} STAGE_SUBMISSION 任务已存在，跳过重复分配",
                    projectId, stage.getStageOrder());
            return;
        }
        ReviewTaskDO task = new ReviewTaskDO();
        task.setProjectId(projectId);
        task.setTaskType(TaskType.STAGE_SUBMISSION);
        task.setNodeSequence(stage.getStageOrder());
        task.setAssigneeId(applicantId);
        task.setStatus(TaskStatusEnum.PENDING);
        task.setTransferCount(0);
        task.setAssignTime(LocalDateTime.now());
        taskMapper.insert(task);
        log.info("[Workflow] 项目{} 阶段{} STAGE_SUBMISSION 任务已分配给申请人{}",
                projectId, stage.getStageOrder(), applicantId);
    }
}
