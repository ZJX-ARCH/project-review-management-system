package top.continew.admin.review.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.context.UserContextHolder;
import top.continew.admin.review.common.enums.TaskType;
import top.continew.admin.review.form.enums.FieldTypeEnum;
import top.continew.admin.review.form.model.resp.FormFieldResp;
import top.continew.admin.review.form.model.resp.FormTemplateResp;
import top.continew.admin.review.form.service.FormTemplateService;
import top.continew.admin.review.project.enums.TaskDecisionEnum;
import top.continew.admin.review.project.enums.TaskStatusEnum;
import top.continew.admin.review.project.engine.TaskAssignmentEngine;
import top.continew.admin.review.project.event.TaskCompletedEvent;
import top.continew.admin.review.project.mapper.ReviewProjectMapper;
import top.continew.admin.review.project.mapper.ReviewProjectStageMapper;
import top.continew.admin.review.project.mapper.ReviewTaskMapper;
import top.continew.admin.review.project.model.entity.ProjectTypeSnapshot;
import top.continew.admin.review.project.model.entity.ReviewProjectDO;
import top.continew.admin.review.project.model.entity.ReviewProjectStageDO;
import top.continew.admin.review.project.model.entity.ReviewTaskDO;
import top.continew.admin.review.project.model.query.TaskQuery;
import top.continew.admin.review.project.model.req.TaskSaveReq;
import top.continew.admin.review.project.model.req.TaskSubmitReq;
import top.continew.admin.review.project.model.req.TaskTransferReq;
import top.continew.admin.review.project.model.resp.ProjectStageResp;
import top.continew.admin.review.project.model.resp.TaskCandidateResp;
import top.continew.admin.review.project.model.resp.TaskDetailResp;
import top.continew.admin.review.project.model.resp.TaskListResp;
import top.continew.admin.review.project.service.TaskService;
import top.continew.admin.system.mapper.user.UserMapper;
import top.continew.admin.system.model.entity.user.UserDO;
import top.continew.starter.core.exception.BadRequestException;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

/**
 * 任务管理 Service 实现
 *
 * @author zjx
 * @since 2026-03-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl extends ServiceImpl<ReviewTaskMapper, ReviewTaskDO>
        implements TaskService {

    /** 最大转办次数 */
    private static final int MAX_TRANSFER_COUNT = 3;

    private final ReviewTaskMapper taskMapper;
    private final ReviewProjectMapper projectMapper;
    private final ReviewProjectStageMapper stageMapper;
    private final FormTemplateService formTemplateService;
    private final UserMapper userMapper;
    private final TaskAssignmentEngine assignmentEngine;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    private final top.continew.admin.review.project.engine.NodeHistoryBuilder nodeHistoryBuilder;

    @Override
    public PageResp<TaskListResp> myTasks(TaskQuery query, PageQuery pageQuery) {
        Long currentUserId = UserContextHolder.getContext().getId();

        // I6: 项目名关键词过滤先在 SQL 层查出匹配的 projectId，再用 IN 条件过滤任务
        // 避免先分页后内存过滤导致分页结果不准确
        // 使用自定义 @Select 方法绕过 @DataPermission（评审人非项目创建者，SELF 范围会过滤掉所有项目）
        List<Long> matchingProjectIds = null;
        if (query.getProjectName() != null && !query.getProjectName().isBlank()) {
            matchingProjectIds = projectMapper.selectIdsByNameLike(query.getProjectName());
            if (matchingProjectIds.isEmpty()) {
                PageResp<TaskListResp> empty = new PageResp<>();
                empty.setList(Collections.emptyList());
                empty.setTotal(0L);
                return empty;
            }
        }

        Page<ReviewTaskDO> pageParam = new Page<>(pageQuery.getPage(), pageQuery.getSize());
        LambdaQueryWrapper<ReviewTaskDO> wrapper = new LambdaQueryWrapper<ReviewTaskDO>()
                .eq(ReviewTaskDO::getAssigneeId, currentUserId)
                .eq(ReviewTaskDO::getDeleted, 0);

        if (matchingProjectIds != null) {
            wrapper.in(ReviewTaskDO::getProjectId, matchingProjectIds);
        }
        if (query.getTaskType() != null) {
            wrapper.eq(ReviewTaskDO::getTaskType, query.getTaskType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(ReviewTaskDO::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(ReviewTaskDO::getAssignTime);

        Page<ReviewTaskDO> result = taskMapper.selectPage(pageParam, wrapper);

        // 批量查关联项目（仅当前页数据）
        // 使用 selectBatchIds 绕过 @DataPermission（评审人非项目创建者，SELF 范围会过滤掉所有关联项目）
        List<Long> projectIds = result.getRecords().stream()
                .map(ReviewTaskDO::getProjectId).distinct().collect(Collectors.toList());
        Map<Long, ReviewProjectDO> projectMap = projectIds.isEmpty() ? new HashMap<>() :
                projectMapper.selectBatchIds(projectIds)
                        .stream().collect(Collectors.toMap(ReviewProjectDO::getId, p -> p));

        // 批量查申请人姓名
        Set<Long> applicantIds = projectMap.values().stream()
                .map(ReviewProjectDO::getApplicantId).collect(Collectors.toSet());
        Map<Long, String> nameMap = fetchNicknameMap(applicantIds);

        List<TaskListResp> list = result.getRecords().stream().map(t -> {
            TaskListResp resp = BeanUtil.copyProperties(t, TaskListResp.class);
            ReviewProjectDO project = projectMap.get(t.getProjectId());
            if (project != null) {
                resp.setProjectName(project.getProjectName());
                resp.setApplicantName(nameMap.get(project.getApplicantId()));
                resp.setNodeName(resolveNodeName(project, t.getTaskType(), t.getNodeSequence()));
            }
            return resp;
        }).collect(Collectors.toList());

        PageResp<TaskListResp> pageResp = new PageResp<>();
        pageResp.setList(list);
        pageResp.setTotal(result.getTotal());
        return pageResp;
    }

    @Override
    public TaskDetailResp getDetail(Long taskId) {
        ReviewTaskDO task = getTaskAndCheckAssignee(taskId);
        ReviewProjectDO project = projectMapper.selectById(task.getProjectId());
        if (project == null) {
            throw new BusinessException("关联项目不存在");
        }

        TaskDetailResp resp = new TaskDetailResp();
        resp.setTaskId(task.getId());
        resp.setTaskType(task.getTaskType());
        resp.setNodeSequence(task.getNodeSequence());
        resp.setNodeName(resolveNodeName(project, task.getTaskType(), task.getNodeSequence()));
        resp.setTaskStatus(task.getStatus());
        resp.setAssignTime(task.getAssignTime());

        // 已暂存的表单数据
        if (task.getFormData() instanceof Map<?, ?> map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> formData = (Map<String, Object>) map;
            resp.setSavedFormData(formData);
        }

        // 当前节点表单模板
        FormTemplateResp taskFormTemplate = resolveTaskFormTemplate(project, task.getTaskType(), task.getNodeSequence());
        resp.setTaskFormTemplate(taskFormTemplate);

        // 项目基本信息
        resp.setProjectId(project.getId());
        resp.setProjectName(project.getProjectName());
        resp.setDescription(project.getDescription());
        resp.setApplicantId(project.getApplicantId());
        // 手动填充申请人姓名（crane4j @Assemble 需 @AutoOperate 触发，此处直接 selectById 绕过 @DataPermission）
        UserDO applicant = userMapper.selectById(project.getApplicantId());
        resp.setApplicantName(applicant != null ? applicant.getNickname() : null);
        resp.setSubmittedTime(project.getSubmittedTime());
        if (project.getApplicationFormData() instanceof Map<?, ?> appFormMap) {
            @SuppressWarnings("unchecked")
            Map<String, Object> appFormData = (Map<String, Object>) appFormMap;
            resp.setApplicationFormData(appFormData);
        }

        // 申请表单模板（只读）
        FormTemplateResp appFormTemplate = resolveApplicationFormTemplate(project);
        resp.setApplicationFormTemplate(appFormTemplate);

        // 前序节点汇总（当前节点之前所有节点的历史记录）
        resp.setPreviousNodes(buildPreviousNodes(project, task));

        // 阶段成果（MANAGEMENT 查当前阶段，ACCEPTANCE 查全部阶段）
        if (task.getTaskType() == TaskType.MANAGEMENT) {
            ReviewProjectStageDO stage = stageMapper.selectOne(
                    new LambdaQueryWrapper<ReviewProjectStageDO>()
                            .eq(ReviewProjectStageDO::getProjectId, project.getId())
                            .eq(ReviewProjectStageDO::getStageOrder, task.getNodeSequence())
                            .eq(ReviewProjectStageDO::getDeleted, 0));
            if (stage != null) {
                resp.setCurrentStage(toStageResp(stage));
            }
        } else if (task.getTaskType() == TaskType.ACCEPTANCE) {
            List<ReviewProjectStageDO> stages = stageMapper.selectList(
                    new LambdaQueryWrapper<ReviewProjectStageDO>()
                            .eq(ReviewProjectStageDO::getProjectId, project.getId())
                            .eq(ReviewProjectStageDO::getDeleted, 0)
                            .orderByAsc(ReviewProjectStageDO::getStageOrder));
            resp.setAllStages(stages.stream().map(this::toStageResp).collect(Collectors.toList()));
        }

        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Long taskId, TaskSaveReq req) {
        ReviewTaskDO task = getTaskAndCheckAssignee(taskId);
        if (!task.getStatus().isActive()) {
            throw new BusinessException("任务已完成或已取消，无法暂存");
        }
        task.setFormData(req.getFormData());
        task.setStatus(TaskStatusEnum.SAVED);
        taskMapper.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(Long taskId, TaskSubmitReq req) {
        ReviewTaskDO task = getTaskAndCheckAssignee(taskId);
        if (!task.getStatus().isActive()) {
            throw new BusinessException("任务已完成或已取消，无法提交");
        }

        // 验收任务 REJECT 时，rejectBackToStageOrder 必填
        if (task.getTaskType() == TaskType.ACCEPTANCE
                && req.getDecision() == TaskDecisionEnum.REJECT
                && req.getRejectBackToStageOrder() == null) {
            throw new BadRequestException("验收驳回时必须指定回退目标阶段序号");
        }

        // 评审阶段仅允许 PASS/REJECT
        if (isReviewTask(task.getTaskType())
                && req.getDecision() != TaskDecisionEnum.PASS
                && req.getDecision() != TaskDecisionEnum.REJECT) {
            throw new BadRequestException("评审任务只能选择通过或驳回");
        }

        // SCORE_PASS 模式：从 formData 自动计算归一化分数，决策由汇总引擎判定
        if (isReviewTask(task.getTaskType()) && req.getFormData() != null) {
            BigDecimal calculatedScore = calculateScoreFromFormData(task, req.getFormData());
            if (calculatedScore != null) {
                req.setScore(calculatedScore);
                req.setDecision(TaskDecisionEnum.PASS);
            }
        }

        // 更新任务
        task.setDecision(req.getDecision());
        task.setScore(req.getScore());
        task.setFormData(req.getFormData());
        task.setRejectBackToStageOrder(req.getRejectBackToStageOrder());
        task.setStatus(TaskStatusEnum.COMPLETED);
        task.setCompleteTime(LocalDateTime.now());
        taskMapper.updateById(task);

        // 发布完成事件，触发结果汇总
        eventPublisher.publishEvent(
                new TaskCompletedEvent(this, task.getProjectId(), task.getTaskType(), task.getNodeSequence()));
        log.info("[Task] 任务{} 已提交决策：{}", taskId, req.getDecision());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transfer(Long taskId, TaskTransferReq req) {
        ReviewTaskDO task = getTaskAndCheckAssignee(taskId);
        if (!task.getStatus().isActive()) {
            throw new BusinessException("任务已完成或已取消，无法转办");
        }
        if (task.getTransferCount() != null && task.getTransferCount() >= MAX_TRANSFER_COUNT) {
            throw new BusinessException("任务已达最大转办次数（" + MAX_TRANSFER_COUNT + "次）");
        }
        if (req.getTargetUserId().equals(task.getAssigneeId())) {
            throw new BadRequestException("不能转办给自己");
        }

        // C4: 校验目标用户存在且处于启用状态
        UserDO targetUser = userMapper.selectById(req.getTargetUserId());
        if (targetUser == null || targetUser.getDeleted() != 0) {
            throw new BadRequestException("转办目标用户不存在");
        }

        // 原任务标记为 TRANSFERRED
        ReviewProjectDO project = projectMapper.selectById(task.getProjectId());
        if (project == null || project.getDeleted() != 0) {
            throw new BadRequestException("Related project not found");
        }
        Set<Long> candidatePool = assignmentEngine.findCandidates(project.getTypeId(), task.getTaskType(), task.getNodeSequence());
        if (!candidatePool.contains(req.getTargetUserId())) {
            throw new BadRequestException("Target user is not eligible for the current node");
        }
        task.setStatus(TaskStatusEnum.TRANSFERRED);
        task.setTransferRemark(req.getTransferRemark());
        task.setCompleteTime(LocalDateTime.now());
        taskMapper.updateById(task);

        // 创建新任务给目标用户，保留已暂存的表单数据（避免填写内容丢失）
        ReviewTaskDO newTask = new ReviewTaskDO();
        newTask.setProjectId(task.getProjectId());
        newTask.setTaskType(task.getTaskType());
        newTask.setNodeSequence(task.getNodeSequence());
        newTask.setAssigneeId(req.getTargetUserId());
        newTask.setStatus(TaskStatusEnum.PENDING);
        newTask.setTransferCount((task.getTransferCount() == null ? 0 : task.getTransferCount()) + 1);
        newTask.setAssignTime(LocalDateTime.now());
        // C4: 复制已暂存的表单数据，新受理人可在此基础上继续填写
        newTask.setFormData(task.getFormData());
        taskMapper.insert(newTask);
        log.info("[Task] 任务{} 已转办给用户{}", taskId, req.getTargetUserId());
    }

    @Override
    public List<ReviewTaskDO> listByNode(Long projectId, String taskType, Integer nodeSequence) {
        return taskMapper.selectList(new LambdaQueryWrapper<ReviewTaskDO>()
                .eq(ReviewTaskDO::getProjectId, projectId)
                .eq(ReviewTaskDO::getTaskType, TaskType.valueOf(taskType))
                .eq(ReviewTaskDO::getNodeSequence, nodeSequence)
                .eq(ReviewTaskDO::getDeleted, 0));
    }

    // ==================== 辅助方法 ====================

    private ReviewTaskDO getTaskAndCheckAssignee(Long taskId) {
        ReviewTaskDO task = taskMapper.selectById(taskId);
        if (task == null || task.getDeleted() != 0) {
            throw new BadRequestException("任务不存在");
        }
        Long currentUserId = UserContextHolder.getContext().getId();
        if (!currentUserId.equals(task.getAssigneeId())) {
            throw new BusinessException("无权操作他人任务");
        }
        return task;
    }

    private boolean isReviewTask(TaskType taskType) {
        return taskType == TaskType.AUDIT || taskType == TaskType.REVIEW || taskType == TaskType.DECISION;
    }

    /** SCORE_PASS 模式下从 formData 解析评分表并计算归一化分数（0~100）。逻辑与前端 FormRenderer.calcPercentage() 一致。*/
    private BigDecimal calculateScoreFromFormData(ReviewTaskDO task, Map<String, Object> formData) {
        ReviewProjectDO project = projectMapper.selectById(task.getProjectId());
        if (project == null) return null;
        ProjectTypeSnapshot snapshot;
        try {
            snapshot = objectMapper.convertValue(project.getSnapshotConfig(), ProjectTypeSnapshot.class);
        } catch (Exception e) {
            log.warn("[Score] snapshot parse failed taskId={}", task.getId());
            return null;
        }
        if (snapshot == null || snapshot.getApprovalRules() == null || snapshot.getFormMappings() == null) return null;
        String nodeScope = task.getTaskType().getValue() + "_" + task.getNodeSequence();
        ProjectTypeSnapshot.ApprovalRuleInfo rule = snapshot.getApprovalRules().get(nodeScope);
        if (rule == null || !"SCORE_PASS".equals(rule.getApprovalMode())) return null;
        Long formTemplateId = snapshot.getFormMappings().get(nodeScope);
        if (formTemplateId == null) {
            log.warn("[Score] no form template for nodeScope={}", nodeScope);
            return null;
        }
        FormTemplateResp template;
        try {
            template = formTemplateService.getDetail(formTemplateId);
        } catch (Exception e) {
            log.warn("[Score] load form template failed templateId={}", formTemplateId);
            return null;
        }
        if (template == null || template.getFields() == null) return null;
        List<FormFieldResp> scoreTableFields = template.getFields().stream()
                .filter(f -> FieldTypeEnum.SCORE_TABLE.equals(f.getFieldType()))
                .collect(Collectors.toList());
        if (scoreTableFields.isEmpty()) return null;
        Map<String, BigDecimal> tableScores = new LinkedHashMap<>();
        for (FormFieldResp field : scoreTableFields) {
            BigDecimal pct = calcTablePercentage(field, formData);
            if (pct != null) tableScores.put(field.getFieldCode(), pct);
        }
        if (tableScores.isEmpty()) return null;
        if (tableScores.size() == 1) return tableScores.values().iterator().next().setScale(4, RoundingMode.HALF_UP);
        Map<String, BigDecimal> weights = rule.getScoreTableWeights();
        if (weights == null || weights.isEmpty()) {
            BigDecimal sum = tableScores.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            return sum.divide(BigDecimal.valueOf(tableScores.size()), 4, RoundingMode.HALF_UP);
        }
        BigDecimal finalScore = BigDecimal.ZERO;
        for (Map.Entry<String, BigDecimal> entry : tableScores.entrySet()) {
            BigDecimal w = weights.getOrDefault(entry.getKey(), BigDecimal.ZERO);
            finalScore = finalScore.add(entry.getValue().multiply(w));
        }
        return finalScore.setScale(4, RoundingMode.HALF_UP);
    }

    /** 计算单张评分表的归一化分数（0~100），逻辑同前端 calcPercentage() */
    @SuppressWarnings("unchecked")
    private BigDecimal calcTablePercentage(FormFieldResp field, Map<String, Object> formData) {
        if (field.getFieldConfig() == null) return null;
        Object rawVal = formData.get(field.getFieldCode());
        if (!(rawVal instanceof Map)) return null;
        Map<String, Object> tableVal = (Map<String, Object>) rawVal;
        Object rawScores = tableVal.get("scores");
        if (!(rawScores instanceof Map)) return null;
        Map<String, Object> scores = (Map<String, Object>) rawScores;
        JsonNode cfg = field.getFieldConfig();
        String scoreMode = cfg.path("scoreMode").asText("WEIGHTED");
        if ("WEIGHTED".equals(scoreMode)) {
            double totalScore = cfg.path("totalScore").asDouble(100);
            if (totalScore <= 0) return BigDecimal.ZERO;
            double sum = 0;
            JsonNode items = cfg.path("scoreItems");
            if (items.isArray()) {
                for (JsonNode item : items) {
                    sum += toDouble(scores.get(item.path("itemCode").asText())) * item.path("weight").asDouble(0);
                }
            }
            return BigDecimal.valueOf(sum / totalScore * 100).setScale(4, RoundingMode.HALF_UP);
        } else {
            double totalMax = 0, totalRaw = 0;
            JsonNode items = cfg.path("scoreItems");
            if (items.isArray()) {
                for (JsonNode item : items) {
                    totalMax += item.path("maxScore").asDouble(0);
                    totalRaw += toDouble(scores.get(item.path("itemCode").asText()));
                }
            }
            if (totalMax <= 0) return BigDecimal.ZERO;
            return BigDecimal.valueOf(totalRaw / totalMax * 100).setScale(4, RoundingMode.HALF_UP);
        }
    }

    private double toDouble(Object val) {
        if (val == null) return 0;
        if (val instanceof Number) return ((Number) val).doubleValue();
        try { return Double.parseDouble(val.toString()); } catch (Exception e) { return 0; }
    }

    /**
     * 解析节点名称（从快照中读取）
     */
    private String resolveNodeName(ReviewProjectDO project, TaskType taskType, Integer nodeSequence) {
        try {
            ProjectTypeSnapshot snapshot = objectMapper.convertValue(project.getSnapshotConfig(), ProjectTypeSnapshot.class);
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
            log.warn("[Task] 解析节点名称失败：{}", e.getMessage());
        }
        return taskType.getDescription() + "-" + nodeSequence;
    }

    /**
     * 查当前节点的任务表单模板
     */
    private FormTemplateResp resolveTaskFormTemplate(ReviewProjectDO project, TaskType taskType, Integer nodeSequence) {
        try {
            ProjectTypeSnapshot snapshot = objectMapper.convertValue(project.getSnapshotConfig(), ProjectTypeSnapshot.class);
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
            log.warn("[Task] 解析任务表单模板失败：{}", e.getMessage());
        }
        return null;
    }

    /**
     * 查申请表单模板（只读，供处理人参考）
     */
    private FormTemplateResp resolveApplicationFormTemplate(ReviewProjectDO project) {
        try {
            ProjectTypeSnapshot snapshot = objectMapper.convertValue(project.getSnapshotConfig(), ProjectTypeSnapshot.class);
            if (snapshot == null || snapshot.getFormMappings() == null) {
                return null;
            }
            Long formTemplateId = snapshot.getFormMappings().get("APPLICATION");
            if (formTemplateId != null) {
                return formTemplateService.getDetail(formTemplateId);
            }
        } catch (Exception e) {
            log.warn("[Task] 解析申请表单模板失败：{}", e.getMessage());
        }
        return null;
    }

    /**
     * 构建前序节点汇总列表
     */
    private List<TaskDetailResp.NodeSummaryResp> buildPreviousNodes(ReviewProjectDO project, ReviewTaskDO currentTask) {
        // 查同一项目所有 COMPLETED 任务（不含当前任务）
        List<ReviewTaskDO> completedTasks = taskMapper.selectList(
                new LambdaQueryWrapper<ReviewTaskDO>()
                        .eq(ReviewTaskDO::getProjectId, project.getId())
                        .eq(ReviewTaskDO::getStatus, TaskStatusEnum.COMPLETED)
                        .ne(ReviewTaskDO::getId, currentTask.getId())
                        .eq(ReviewTaskDO::getDeleted, 0));

        // 按 taskType + nodeSequence 分组汇总
        Map<String, List<ReviewTaskDO>> grouped = completedTasks.stream()
                .collect(Collectors.groupingBy(t -> t.getTaskType().getValue() + "_" + t.getNodeSequence()));

        List<TaskDetailResp.NodeSummaryResp> summaries = new ArrayList<>();
        for (Map.Entry<String, List<ReviewTaskDO>> entry : grouped.entrySet()) {
            List<ReviewTaskDO> tasks = entry.getValue();
            ReviewTaskDO sample = tasks.get(0);

            TaskDetailResp.NodeSummaryResp summary = new TaskDetailResp.NodeSummaryResp();
            summary.setNodeType(sample.getTaskType().getValue());
            summary.setNodeSequence(sample.getNodeSequence());
            summary.setNodeName(resolveNodeName(project, sample.getTaskType(), sample.getNodeSequence()));

            long passCount = tasks.stream().filter(t -> t.getDecision() == TaskDecisionEnum.PASS).count();
            summary.setPassCount((int) passCount);
            summary.setTotalCount(tasks.size());

            // 平均分
            OptionalDouble avgScore = tasks.stream()
                    .filter(t -> t.getScore() != null)
                    .mapToDouble(t -> t.getScore().doubleValue())
                    .average();
            if (avgScore.isPresent()) {
                summary.setAverageScore(java.math.BigDecimal.valueOf(avgScore.getAsDouble())
                        .setScale(2, java.math.RoundingMode.HALF_UP));
            }

            // 汇总结果（基于多数通过判断，简化：passCount == totalCount 则 PASS）
            summary.setResult(passCount == tasks.size() ? TaskDecisionEnum.PASS : TaskDecisionEnum.REJECT);

            summaries.add(summary);
        }

        // 按轮次顺序排列
        summaries.sort(Comparator.comparing(TaskDetailResp.NodeSummaryResp::getNodeType)
                .thenComparingInt(TaskDetailResp.NodeSummaryResp::getNodeSequence));
        return summaries;
    }

    private ProjectStageResp toStageResp(ReviewProjectStageDO stage) {
        ProjectStageResp resp = BeanUtil.copyProperties(stage, ProjectStageResp.class);
        resp.setStageType(stage.getStageType() != null ? stage.getStageType().getValue() : null);
        if (stage.getStageFormData() instanceof Map<?, ?> map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> formData = (Map<String, Object>) map;
            resp.setStageFormData(formData);
        }
        return resp;
    }

    private Map<Long, String> fetchNicknameMap(Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return new HashMap<>();
        }
        // 使用 selectBatchIds 绕过 @DataPermission（评审人没有申请人的数据权限，SELF 范围会过滤掉）
        return userMapper.selectBatchIds(userIds)
                .stream().collect(Collectors.toMap(UserDO::getId, UserDO::getNickname));
    }

    @Override
    public List<TaskCandidateResp> listCandidates(Long taskId) {
        ReviewTaskDO task = getTaskAndCheckAssignee(taskId);
        ReviewProjectDO project = projectMapper.selectById(task.getProjectId());
        if (project == null) {
            throw new BusinessException("关联项目不存在");
        }

        // 查当前节点候选人池
        Set<Long> candidatePool = assignmentEngine.findCandidates(
                project.getTypeId(), task.getTaskType(), task.getNodeSequence());

        // 排除自己
        candidatePool.remove(task.getAssigneeId());

        if (candidatePool.isEmpty()) {
            return Collections.emptyList();
        }

        // 查用户信息（selectBatchIds 绕过 @DataPermission）
        return userMapper.selectBatchIds(candidatePool).stream()
                .map(u -> {
                    TaskCandidateResp resp = new TaskCandidateResp();
                    resp.setUserId(u.getId());
                    resp.setNickname(u.getNickname());
                    resp.setUsername(u.getUsername());
                    return resp;
                })
                .sorted(Comparator.comparing(TaskCandidateResp::getNickname,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    @Override
    public List<top.continew.admin.review.project.model.resp.NodeHistoryResp> getNodeHistory(Long taskId) {
        ReviewTaskDO task = getTaskAndCheckAssignee(taskId);
        ReviewProjectDO project = projectMapper.selectById(task.getProjectId());
        if (project == null) {
            throw new BusinessException("关联项目不存在");
        }

        List<top.continew.admin.review.project.model.resp.NodeHistoryResp> result = new ArrayList<>();

        // 1. 申请表单节点（固定第一个）
        top.continew.admin.review.project.model.resp.NodeHistoryResp appNode =
                nodeHistoryBuilder.buildApplicationNode(project);
        result.add(appNode);

        // 2. 前序已完成节点（不含当前节点的同轮任务）
        // 查所有 COMPLETED 任务，排除当前节点（同 taskType + nodeSequence）
        List<ReviewTaskDO> completedTasks = taskMapper.selectList(
                new LambdaQueryWrapper<ReviewTaskDO>()
                        .eq(ReviewTaskDO::getProjectId, project.getId())
                        .eq(ReviewTaskDO::getStatus, TaskStatusEnum.COMPLETED)
                        .eq(ReviewTaskDO::getDeleted, 0)
                        // 排除当前节点的所有任务（同轮互不可见）
                        .not(w -> w.eq(ReviewTaskDO::getTaskType, task.getTaskType())
                                .eq(ReviewTaskDO::getNodeSequence, task.getNodeSequence())));

        result.addAll(nodeHistoryBuilder.buildNodeHistoryList(project, completedTasks));
        return result;
    }
}
