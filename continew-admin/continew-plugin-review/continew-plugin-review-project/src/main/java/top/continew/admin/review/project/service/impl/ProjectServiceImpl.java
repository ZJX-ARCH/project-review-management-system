package top.continew.admin.review.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.context.UserContextHolder;
import top.continew.admin.review.type.enums.TypeStatusEnum;
import top.continew.admin.review.common.enums.ProjectStatus;
import top.continew.admin.review.common.enums.TaskType;
import top.continew.admin.review.form.model.resp.FormTemplateResp;
import top.continew.admin.review.form.service.FormTemplateService;
import top.continew.admin.review.project.enums.StageStatusEnum;
import top.continew.admin.review.project.enums.TaskDecisionEnum;
import top.continew.admin.review.project.event.ProjectSubmittedEvent;
import top.continew.admin.review.project.event.StageFormSubmittedEvent;
import top.continew.admin.review.project.engine.TaskAssignmentEngine;
import top.continew.admin.review.project.mapper.ReviewProjectMapper;
import top.continew.admin.review.project.mapper.ReviewProjectModifyLogMapper;
import top.continew.admin.review.project.mapper.ReviewProjectStageMapper;
import top.continew.admin.review.project.mapper.ReviewTaskMapper;
import top.continew.admin.review.project.model.entity.ProjectTypeSnapshot;
import top.continew.admin.review.project.model.entity.ReviewProjectDO;
import top.continew.admin.review.project.model.entity.ReviewProjectModifyLogDO;
import top.continew.admin.review.project.model.entity.ReviewProjectStageDO;
import top.continew.admin.review.project.model.entity.ReviewTaskDO;
import top.continew.admin.review.project.enums.TaskStatusEnum;
import top.continew.admin.review.project.model.query.ProjectQuery;
import top.continew.admin.review.project.model.req.ProjectCreateReq;
import top.continew.admin.review.project.model.req.ProjectSubmitReq;
import top.continew.admin.review.project.model.req.ProjectUpdateFormReq;
import top.continew.admin.review.project.model.req.StageFormSubmitReq;
import top.continew.admin.review.project.model.resp.ProjectDetailResp;
import top.continew.admin.review.project.model.resp.ProjectListResp;
import top.continew.admin.review.project.model.resp.ProjectStageResp;
import top.continew.admin.review.project.service.ProjectService;
import top.continew.admin.review.template.mapper.ManagementStageMapper;
import top.continew.admin.review.template.mapper.ProcessTemplateRoundNameMapper;
import top.continew.admin.review.template.model.entity.ManagementStageDO;
import top.continew.admin.review.template.model.entity.ProcessTemplateRoundNameDO;
import top.continew.admin.review.type.mapper.ProjectTypeMapper;
import top.continew.admin.review.type.mapper.TypeApprovalConfigMapper;
import top.continew.admin.review.type.mapper.TypeFormMappingMapper;
import top.continew.admin.review.type.mapper.TypeProcessConfigMapper;
import top.continew.admin.review.type.model.entity.ProjectTypeDO;
import top.continew.admin.review.type.model.entity.TypeApprovalConfigDO;
import top.continew.admin.review.type.model.entity.TypeFormMappingDO;
import top.continew.admin.review.type.model.entity.TypeProcessConfigDO;
import top.continew.starter.core.exception.BadRequestException;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.dev33.satoken.stp.StpUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/**
 * 项目管理 Service 实现
 *
 * @author zjx
 * @since 2026-03-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl extends ServiceImpl<ReviewProjectMapper, ReviewProjectDO>
        implements ProjectService {

    private final ReviewProjectMapper projectMapper;
    private final ReviewProjectStageMapper stageMapper;
    private final ReviewProjectModifyLogMapper modifyLogMapper;
    private final ReviewTaskMapper taskMapper;
    private final ProjectTypeMapper typeMapper;
    private final TypeProcessConfigMapper processConfigMapper;
    private final TypeFormMappingMapper formMappingMapper;
    private final TypeApprovalConfigMapper approvalConfigMapper;
    private final ProcessTemplateRoundNameMapper roundNameMapper;
    private final ManagementStageMapper managementStageMapper;
    private final FormTemplateService formTemplateService;
    private final TaskAssignmentEngine assignmentEngine;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    private final top.continew.admin.review.project.engine.NodeHistoryBuilder nodeHistoryBuilder;

    @Override
    public FormTemplateResp getApplicationForm(Long typeId) {
        // 查 APPLICATION 节点对应的表单模板ID
        QueryWrapper<TypeFormMappingDO> wrapper = new QueryWrapper<>();
        wrapper.eq("type_id", typeId)
                .eq("node_type", "APPLICATION")
                .isNull("node_sequence")
                .eq("deleted", 0);
        TypeFormMappingDO mapping = formMappingMapper.selectOne(wrapper);
        if (mapping == null || mapping.getFormTemplateId() == null) {
            throw new BusinessException("该项目类型未配置申请表单");
        }
        return formTemplateService.getDetail(mapping.getFormTemplateId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ProjectCreateReq req) {
        // 校验类型是否启用
        ProjectTypeDO type = typeMapper.selectById(req.getTypeId());
        if (type == null || type.getDeleted() != 0) {
            throw new BadRequestException("项目类型不存在");
        }
        if (type.getStatus() == TypeStatusEnum.DISABLED) {
            throw new BusinessException("项目类型已禁用，无法申请");
        }

        Long userId = UserContextHolder.getContext().getId();
        Long deptId = UserContextHolder.getContext().getDeptId();
        assignmentEngine.ensureApplicantEligible(req.getTypeId(), userId);

        ReviewProjectDO project = new ReviewProjectDO();
        project.setTypeId(req.getTypeId());
        project.setProjectName(req.getProjectName());
        project.setDescription(req.getDescription());
        project.setApplicantId(userId);
        project.setDeptId(deptId);
        project.setStatus(ProjectStatus.DRAFT);
        projectMapper.insert(project);
        log.info("[Project] 用户{} 创建项目草稿，ID={}", userId, project.getId());
        return project.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(Long projectId, ProjectSubmitReq req) {
        ReviewProjectDO project = getProjectAndCheckOwner(projectId);
        if (project.getStatus() != ProjectStatus.DRAFT) {
            throw new BusinessException("只有草稿状态的项目才能提交");
        }

        // 构建并固化快照
        ProjectTypeSnapshot snapshot = buildSnapshot(project.getTypeId());

        // C5: 提前校验所有节点人员配置充足性，避免流程启动后因人员为空报错
        assignmentEngine.ensureApplicantEligible(project.getTypeId(), project.getApplicantId());
        assignmentEngine.validatePersonnelSufficiency(project.getTypeId(), snapshot);

        project.setStatus(ProjectStatus.SUBMITTED);
        project.setApplicationFormData(req.getFormData());
        project.setSnapshotConfig(snapshot);
        project.setSubmittedTime(LocalDateTime.now());
        projectMapper.updateById(project);

        // 发布提交事件，触发工作流引擎启动首节点
        eventPublisher.publishEvent(new ProjectSubmittedEvent(this, projectId));
        log.info("[Project] 项目{} 已提交，快照已固化", projectId);
    }

    @Override
    public PageResp<ProjectListResp> page(ProjectQuery query, PageQuery pageQuery) {
        Page<ReviewProjectDO> pageParam = new Page<>(pageQuery.getPage(), pageQuery.getSize());
        LambdaQueryWrapper<ReviewProjectDO> wrapper = new LambdaQueryWrapper<>();

        Long currentUserId = UserContextHolder.getContext().getId();
        // 有"查看全部项目"权限（PROJECT_ADMIN）时不限制申请人过滤
        if (!StpUtil.hasPermission("review:project:list:all")) {
            wrapper.eq(ReviewProjectDO::getApplicantId, currentUserId);
        }

        if (query.getProjectName() != null) {
            wrapper.like(ReviewProjectDO::getProjectName, query.getProjectName());
        }
        if (query.getTypeId() != null) {
            wrapper.eq(ReviewProjectDO::getTypeId, query.getTypeId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(ReviewProjectDO::getStatus, query.getStatus());
        }
        wrapper.eq(ReviewProjectDO::getDeleted, 0)
                .orderByDesc(ReviewProjectDO::getCreateTime);

        Page<ReviewProjectDO> result = projectMapper.selectPage(pageParam, wrapper);

        // 批量查类型名称
        Map<Long, String> typeNameMap = buildTypeNameMap(result.getRecords());

        List<ProjectListResp> list = result.getRecords().stream().map(p -> {
            ProjectListResp resp = BeanUtil.copyProperties(p, ProjectListResp.class);
            resp.setApplicantId(p.getApplicantId());
            resp.setTypeName(typeNameMap.get(p.getTypeId()));
            return resp;
        }).collect(Collectors.toList());

        PageResp<ProjectListResp> pageResp = new PageResp<>();
        pageResp.setList(list);
        pageResp.setTotal(result.getTotal());
        return pageResp;
    }

    @Override
    public ProjectDetailResp getDetail(Long projectId) {
        ReviewProjectDO project = projectMapper.selectById(projectId);
        if (project == null || project.getDeleted() != 0) {
            throw new BadRequestException("项目不存在");
        }

        ProjectDetailResp resp = BeanUtil.copyProperties(project, ProjectDetailResp.class);
        resp.setApplicantId(project.getApplicantId());

        // 填充类型名称
        ProjectTypeDO type = typeMapper.selectById(project.getTypeId());
        if (type != null) {
            resp.setTypeName(type.getTypeName());
        }

        // 填充 applicationFormData
        if (project.getApplicationFormData() instanceof Map<?, ?> map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> formData = (Map<String, Object>) map;
            resp.setApplicationFormData(formData);
        }

        // 填充阶段列表
        List<ReviewProjectStageDO> stages = stageMapper.selectList(
                new LambdaQueryWrapper<ReviewProjectStageDO>()
                        .eq(ReviewProjectStageDO::getProjectId, projectId)
                        .eq(ReviewProjectStageDO::getDeleted, 0)
                        .orderByAsc(ReviewProjectStageDO::getStageOrder));
        if (!stages.isEmpty()) {
            resp.setStages(stages.stream().map(this::toStageResp).collect(Collectors.toList()));
        }

        // 从快照构建申请表单模板和进度信息
        if (project.getSnapshotConfig() != null) {
            try {
                ProjectTypeSnapshot snapshot = objectMapper.convertValue(
                        project.getSnapshotConfig(), ProjectTypeSnapshot.class);
                if (snapshot != null) {
                    // 填充申请表单模板
                    if (snapshot.getFormMappings() != null) {
                        Long formTemplateId = snapshot.getFormMappings().get("APPLICATION");
                        if (formTemplateId != null) {
                            try {
                                resp.setApplicationFormTemplate(formTemplateService.getDetail(formTemplateId));
                            } catch (Exception e) {
                                log.warn("[Project] 查询申请表单模板失败：{}", e.getMessage());
                            }
                        }
                    }
                    // 构建评审阶段进度
                    resp.setReviewProgress(buildReviewProgress(snapshot, project));
                    // 构建管理阶段进度
                    resp.setStageProgress(buildStageProgress(stages));
                }
            } catch (Exception e) {
                log.warn("[Project] 解析快照失败：{}", e.getMessage());
            }
        }

        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateForm(Long projectId, ProjectUpdateFormReq req) {
        ReviewProjectDO project = getProjectAndCheckOwner(projectId);
        // 仅允许在评审阶段（状态 >= SUBMITTED && <= DECIDING）时修改
        if (!project.getStatus().isReviewPhase()
                || project.getStatus() == ProjectStatus.DRAFT) {
            throw new BusinessException("当前项目状态不允许修改申请表单");
        }

        // 记录修改日志（旧数据 → 新数据）
        ReviewProjectModifyLogDO log = new ReviewProjectModifyLogDO();
        log.setProjectId(projectId);
        log.setModifiedBy(UserContextHolder.getContext().getId());
        log.setModifyReason(req.getModifyReason());
        log.setOldFormData(project.getApplicationFormData());
        log.setNewFormData(req.getFormData());
        modifyLogMapper.insert(log);

        // 覆盖表单数据
        project.setApplicationFormData(req.getFormData());
        projectMapper.updateById(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revoke(Long projectId) {
        ReviewProjectDO project = getProjectAndCheckOwner(projectId);
        // 允许在评审阶段撤销（SUBMITTED 状态为瞬态，实际运行时已是 AUDITING 等）
        if (project.getStatus() == ProjectStatus.DRAFT
                || project.getStatus() == ProjectStatus.TERMINATED
                || project.getStatus().isArchived()) {
            throw new BusinessException("当前项目状态不允许撤销申请");
        }
        // 已有评审人提交决策则不可撤销
        boolean anyCompleted = taskMapper.exists(new LambdaQueryWrapper<ReviewTaskDO>()
                .eq(ReviewTaskDO::getProjectId, projectId)
                .eq(ReviewTaskDO::getStatus, TaskStatusEnum.COMPLETED)
                .eq(ReviewTaskDO::getDeleted, 0));
        if (anyCompleted) {
            throw new BusinessException("已有评审人提交决策，无法撤销申请");
        }

        // I4: 撤销时清理节点状态、快照和所有待处理任务
        project.setStatus(ProjectStatus.DRAFT);
        project.setSubmittedTime(null);
        project.setCurrentNodeType(null);
        project.setCurrentNodeSequence(null);
        project.setSnapshotConfig(null);
        projectMapper.updateById(project);
        cancelAllPendingTasks(projectId);
        log.info("[Project] 项目{} 已撤销回草稿", projectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void terminate(Long projectId) {
        ReviewProjectDO project = projectMapper.selectById(projectId);
        if (project == null || project.getDeleted() != 0) {
            throw new BadRequestException("项目不存在");
        }
        if (project.getStatus().isArchived()) {
            throw new BusinessException("项目已归档，无法终止");
        }
        project.setStatus(ProjectStatus.TERMINATED);
        project.setCurrentNodeType(null);
        project.setCurrentNodeSequence(null);
        projectMapper.updateById(project);
        // I8: 终止时取消所有待处理任务，避免僵尸任务残留
        cancelAllPendingTasks(projectId);
        log.info("[Project] 管理员强制终止项目{}", projectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitStageForm(Long projectId, Long stageId, StageFormSubmitReq req) {
        ReviewProjectDO project = getProjectAndCheckOwner(projectId);
        if (project.getStatus() != ProjectStatus.EXECUTING
                && project.getStatus() != ProjectStatus.OVERTIME
                && project.getStatus() != ProjectStatus.ACCEPTING) {
            throw new BusinessException("当前项目状态不允许提交阶段成果");
        }

        ReviewProjectStageDO stage = stageMapper.selectById(stageId);
        if (stage == null || !stage.getProjectId().equals(projectId) || stage.getDeleted() != 0) {
            throw new BadRequestException("阶段不存在");
        }
        if (stage.getStatus() != StageStatusEnum.IN_PROGRESS
                && stage.getStatus() != StageStatusEnum.REJECTED) {
            throw new BusinessException("当前阶段状态不允许提交成果（需处于进行中或被驳回状态）");
        }

        // 保存阶段成果表单数据（触发事件前先存储，保证数据可见）
        stage.setStageFormData(req.getFormData());
        stageMapper.updateById(stage);

        // 发布事件，由 WorkflowEngine 负责更新状态和分配任务
        eventPublisher.publishEvent(new StageFormSubmittedEvent(this, projectId, stageId, stage.getStageOrder()));
        log.info("[Project] 项目{} 阶段{} 成果已提交", projectId, stageId);
    }

    // ==================== 快照构建 ====================

    /**
     * 构建类型配置快照（申请提交时一次性固化）
     */
    private ProjectTypeSnapshot buildSnapshot(Long typeId) {
        ProjectTypeDO type = typeMapper.selectById(typeId);
        if (type == null) {
            throw new BusinessException("项目类型不存在：" + typeId);
        }

        ProjectTypeSnapshot snapshot = new ProjectTypeSnapshot();
        snapshot.setTypeId(typeId);
        snapshot.setTypeName(type.getTypeName());

        // 查流程配置
        QueryWrapper<TypeProcessConfigDO> processWrapper = new QueryWrapper<>();
        processWrapper.eq("type_id", typeId).eq("deleted", 0);
        List<TypeProcessConfigDO> processConfigs = processConfigMapper.selectList(processWrapper);

        Long reviewTemplateId = null;
        Long manageTemplateId = null;
        for (TypeProcessConfigDO cfg : processConfigs) {
            if ("REVIEW".equals(cfg.getProcessType().getValue())) {
                reviewTemplateId = cfg.getTemplateId();
                snapshot.setReviewProcessTemplateId(reviewTemplateId);
            } else if ("MANAGE".equals(cfg.getProcessType().getValue())) {
                manageTemplateId = cfg.getTemplateId();
                snapshot.setManagementTemplateId(manageTemplateId);
            }
        }

        // 查评审轮次列表（来自流程模板的轮次名称配置）
        if (reviewTemplateId != null) {
            QueryWrapper<ProcessTemplateRoundNameDO> roundWrapper = new QueryWrapper<>();
            roundWrapper.eq("template_id", reviewTemplateId).eq("deleted", 0)
                    .orderByAsc("round_type", "round_sequence");
            List<ProcessTemplateRoundNameDO> rounds = roundNameMapper.selectList(roundWrapper);
            List<ProjectTypeSnapshot.RoundInfo> roundInfos = rounds.stream().map(r -> {
                ProjectTypeSnapshot.RoundInfo info = new ProjectTypeSnapshot.RoundInfo();
                info.setRoundType(r.getRoundType().getValue());
                info.setRoundSequence(r.getRoundSequence());
                info.setRoundName(r.getRoundName());
                return info;
            }).collect(Collectors.toList());
            snapshot.setRounds(roundInfos);
        }

        // 查管理阶段列表（来自管理流程模板的阶段配置）
        if (manageTemplateId != null) {
            QueryWrapper<ManagementStageDO> stageWrapper = new QueryWrapper<>();
            stageWrapper.eq("template_id", manageTemplateId).eq("deleted", 0)
                    .orderByAsc("stage_order");
            List<ManagementStageDO> stages = managementStageMapper.selectList(stageWrapper);
            List<ProjectTypeSnapshot.StageInfo> stageInfos = stages.stream().map(s -> {
                ProjectTypeSnapshot.StageInfo info = new ProjectTypeSnapshot.StageInfo();
                info.setStageType(s.getStageType().getValue());
                info.setStageOrder(s.getStageOrder());
                info.setStageName(s.getStageName());
                // plannedDays 在模板模块暂无，设为 null
                info.setPlannedDays(null);
                return info;
            }).collect(Collectors.toList());
            snapshot.setStages(stageInfos);
        }

        // 查表单映射（key 格式：APPLICATION / AUDIT_1 / REVIEW_1 / DECISION_1 / STAGE_1）
        QueryWrapper<TypeFormMappingDO> formWrapper = new QueryWrapper<>();
        formWrapper.eq("type_id", typeId).eq("deleted", 0);
        List<TypeFormMappingDO> formMappings = formMappingMapper.selectList(formWrapper);
        Map<String, Long> formMap = new HashMap<>();
        for (TypeFormMappingDO fm : formMappings) {
            String key;
            if (fm.getNodeSequence() == null) {
                key = fm.getNodeType().getValue();
            } else {
                key = fm.getNodeType().getValue() + "_" + fm.getNodeSequence();
            }
            formMap.put(key, fm.getFormTemplateId());
        }
        snapshot.setFormMappings(formMap);

        // 查审批规则（key 格式：AUDIT_1 / REVIEW_1 / DECISION_1 / ACCEPTANCE）
        QueryWrapper<TypeApprovalConfigDO> approvalWrapper = new QueryWrapper<>();
        approvalWrapper.eq("type_id", typeId).eq("deleted", 0);
        List<TypeApprovalConfigDO> approvalConfigs = approvalConfigMapper.selectList(approvalWrapper);
        Map<String, ProjectTypeSnapshot.ApprovalRuleInfo> approvalMap = new HashMap<>();
        for (TypeApprovalConfigDO ac : approvalConfigs) {
            ProjectTypeSnapshot.ApprovalRuleInfo info = new ProjectTypeSnapshot.ApprovalRuleInfo();
            info.setApprovalMode(ac.getApprovalMode().getValue());
            info.setRequiredReviewerCount(ac.getRequiredReviewerCount());
            info.setMajorityRatio(ac.getMajorityRatio());
            info.setPassThreshold(ac.getPassThreshold());
            info.setScoreCalcMethod(ac.getScoreCalcMethod() != null ? ac.getScoreCalcMethod().getValue() : null);
            info.setWeightMode(ac.getWeightMode() != null ? ac.getWeightMode().getValue() : null);
            info.setScoreTableWeights(ac.getScoreTableWeights());
            approvalMap.put(ac.getNodeScope(), info);
        }
        snapshot.setApprovalRules(approvalMap);

        return snapshot;
    }

    // ==================== 辅助方法 ====================

    private ReviewProjectDO getProjectAndCheckOwner(Long projectId) {
        ReviewProjectDO project = projectMapper.selectById(projectId);
        if (project == null || project.getDeleted() != 0) {
            throw new BadRequestException("项目不存在");
        }
        Long currentUserId = UserContextHolder.getContext().getId();
        if (!currentUserId.equals(project.getApplicantId())) {
            throw new BusinessException("无权操作他人项目");
        }
        return project;
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

    private Map<Long, String> buildTypeNameMap(List<ReviewProjectDO> projects) {
        if (projects.isEmpty()) {
            return new HashMap<>();
        }
        List<Long> typeIds = projects.stream().map(ReviewProjectDO::getTypeId).distinct().collect(Collectors.toList());
        // 使用 selectBatchIds 绕过 @DataPermission（类型由管理员创建，申请人 SELF 范围会过滤掉）
        return typeMapper.selectBatchIds(typeIds)
                .stream().collect(Collectors.toMap(ProjectTypeDO::getId, ProjectTypeDO::getTypeName));
    }

    /**
     * 取消项目的所有 PENDING/SAVED 任务（撤销/终止时调用）
     */
    private void cancelAllPendingTasks(Long projectId) {
        ReviewTaskDO update = new ReviewTaskDO();
        update.setStatus(TaskStatusEnum.CANCELLED);
        taskMapper.update(update, new LambdaQueryWrapper<ReviewTaskDO>()
                .eq(ReviewTaskDO::getProjectId, projectId)
                .in(ReviewTaskDO::getStatus, List.of(TaskStatusEnum.PENDING, TaskStatusEnum.SAVED))
                .eq(ReviewTaskDO::getDeleted, 0));
    }

    /**
     * 从快照构建评审阶段进度列表
     * 轮次顺序：AUDIT → REVIEW → DECISION，每类按 roundSequence 升序
     */
    private List<ProjectDetailResp.NodeProgressItem> buildReviewProgress(
            ProjectTypeSnapshot snapshot, ReviewProjectDO project) {
        if (snapshot.getRounds() == null || snapshot.getRounds().isEmpty()) {
            return new ArrayList<>();
        }
        // 执行阶段或归档阶段时，所有评审轮次均已完成
        boolean allCompleted = project.getStatus() != null && project.getStatus().getValue() >= 50;
        boolean isTerminated = project.getStatus() == ProjectStatus.TERMINATED;

        // 查询已完成任务的节点结果
        Map<String, NodeResult> nodeResultMap = buildNodeResultMap(project.getId());

        // currentNodeType 为 null 且非终止/全完成时（草稿/已提交等），所有节点均为 PENDING
        if (!allCompleted && !isTerminated && project.getCurrentNodeType() == null) {
            return snapshot.getRounds().stream().map(round -> {
                ProjectDetailResp.NodeProgressItem item = new ProjectDetailResp.NodeProgressItem();
                item.setNodeType(round.getRoundType());
                item.setNodeSequence(round.getRoundSequence());
                item.setNodeName(round.getRoundName());
                item.setNodeStatus("PENDING");
                return item;
            }).collect(Collectors.toList());
        }
        // 轮次类型顺序
        List<String> typeOrder = Arrays.asList("AUDIT", "REVIEW", "DECISION");

        return snapshot.getRounds().stream()
                .sorted((a, b) -> {
                    int ta = typeOrder.indexOf(a.getRoundType());
                    int tb = typeOrder.indexOf(b.getRoundType());
                    if (ta != tb) return Integer.compare(ta, tb);
                    return Integer.compare(a.getRoundSequence(), b.getRoundSequence());
                })
                .map(round -> {
                    ProjectDetailResp.NodeProgressItem item = new ProjectDetailResp.NodeProgressItem();
                    item.setNodeType(round.getRoundType());
                    item.setNodeSequence(round.getRoundSequence());
                    item.setNodeName(round.getRoundName());
                    if (allCompleted) {
                        item.setNodeStatus("COMPLETED");
                    } else if (isTerminated) {
                        // 终止时：有已完成结果记录的节点按实际结果显示，无记录的节点显示 PENDING
                        String key = round.getRoundType() + "_" + round.getRoundSequence();
                        NodeResult terminatedNr = nodeResultMap.get(key);
                        if (terminatedNr != null && terminatedNr.isFinished) {
                            item.setNodeStatus("PASS".equals(terminatedNr.result) ? "COMPLETED" : "REJECTED");
                        } else {
                            item.setNodeStatus("PENDING");
                        }
                    } else if (round.getRoundType().equals(project.getCurrentNodeType())) {
                        int cmp = Integer.compare(round.getRoundSequence(),
                                project.getCurrentNodeSequence() != null ? project.getCurrentNodeSequence() : 0);
                        if (cmp < 0) {
                            item.setNodeStatus("COMPLETED");
                        } else if (cmp == 0) {
                            item.setNodeStatus("ACTIVE");
                        } else {
                            item.setNodeStatus("PENDING");
                        }
                    } else {
                        // 判断该轮次类型是否在当前节点类型之前
                        int tRound = typeOrder.indexOf(round.getRoundType());
                        int tCurrent = typeOrder.indexOf(project.getCurrentNodeType());
                        item.setNodeStatus(tRound < tCurrent ? "COMPLETED" : "PENDING");
                    }
                    // 填充节点结果和进度
                    String key = round.getRoundType() + "_" + round.getRoundSequence();
                    NodeResult nr = nodeResultMap.get(key);
                    if (nr != null) {
                        item.setCompletedCount(nr.completedCount);
                        item.setTotalCount(nr.totalCount);
                        if (nr.isFinished) {
                            item.setPassCount(nr.passCount);
                            item.setAverageScore(nr.averageScore);
                            item.setNodeResult(nr.result);
                            // 用实际结果覆盖推断的 nodeStatus（仅 COMPLETED 状态时）
                            if ("COMPLETED".equals(item.getNodeStatus())) {
                                item.setNodeStatus("PASS".equals(nr.result) ? "COMPLETED" : "REJECTED");
                            }
                        }
                        // 从快照读取 requiredCount
                        if (snapshot.getApprovalRules() != null) {
                            ProjectTypeSnapshot.ApprovalRuleInfo rule = snapshot.getApprovalRules().get(key);
                            if (rule != null && rule.getRequiredReviewerCount() != null) {
                                item.setRequiredCount(rule.getRequiredReviewerCount());
                            }
                        }
                        // requiredCount 兜底用 totalCount
                        if (item.getRequiredCount() == null) {
                            item.setRequiredCount(nr.totalCount);
                        }
                    }
                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * 查询项目评审任务（非 CANCELLED），按节点分组汇总结果
     * key 格式：taskType_nodeSequence（如 AUDIT_1）
     */
    private Map<String, NodeResult> buildNodeResultMap(Long projectId) {
        List<ReviewTaskDO> allTasks = taskMapper.selectList(
                new LambdaQueryWrapper<ReviewTaskDO>()
                        .eq(ReviewTaskDO::getProjectId, projectId)
                        .in(ReviewTaskDO::getTaskType, List.of(TaskType.AUDIT, TaskType.REVIEW, TaskType.DECISION))
                        .ne(ReviewTaskDO::getStatus, TaskStatusEnum.CANCELLED)
                        .eq(ReviewTaskDO::getDeleted, 0));

        Map<String, List<ReviewTaskDO>> grouped = allTasks.stream()
                .collect(Collectors.groupingBy(t -> t.getTaskType().getValue() + "_" + t.getNodeSequence()));

        Map<String, NodeResult> result = new HashMap<>();
        for (Map.Entry<String, List<ReviewTaskDO>> entry : grouped.entrySet()) {
            List<ReviewTaskDO> tasks = entry.getValue();
            // 已完成任务（COMPLETED 或 TRANSFERRED）
            List<ReviewTaskDO> completedTasks = tasks.stream()
                    .filter(t -> t.getStatus() == TaskStatusEnum.COMPLETED
                            || t.getStatus() == TaskStatusEnum.TRANSFERRED)
                    .toList();
            // 节点是否已全部完成（可以汇总）
            boolean isFinished = !tasks.isEmpty() && tasks.stream()
                    .allMatch(t -> t.getStatus() == TaskStatusEnum.COMPLETED
                            || t.getStatus() == TaskStatusEnum.TRANSFERRED);

            long passCount = completedTasks.stream()
                    .filter(t -> t.getDecision() == TaskDecisionEnum.PASS).count();
            OptionalDouble avg = completedTasks.stream()
                    .filter(t -> t.getScore() != null)
                    .mapToDouble(t -> t.getScore().doubleValue())
                    .average();

            NodeResult nr = new NodeResult();
            nr.completedCount = completedTasks.size();
            nr.totalCount = tasks.size();
            nr.isFinished = isFinished;
            if (isFinished && !completedTasks.isEmpty()) {
                nr.passCount = (int) passCount;
                nr.averageScore = avg.isPresent()
                        ? BigDecimal.valueOf(avg.getAsDouble()).setScale(2, RoundingMode.HALF_UP) : null;
                nr.result = passCount == completedTasks.size() ? "PASS" : "REJECT";
            }
            result.put(entry.getKey(), nr);
        }
        return result;
    }

    /** 节点汇总结果内部数据类 */
    private static class NodeResult {
        int passCount;
        int totalCount;
        int completedCount;
        boolean isFinished;
        BigDecimal averageScore;
        String result; // PASS / REJECT（节点已汇总时才有）
    }

    /**
     * 从 DB 阶段列表构建管理阶段进度
     */
    private List<ProjectDetailResp.StageProgressItem> buildStageProgress(List<ReviewProjectStageDO> stages) {
        return stages.stream().map(s -> {
            ProjectDetailResp.StageProgressItem item = new ProjectDetailResp.StageProgressItem();
            item.setStageOrder(s.getStageOrder());
            item.setStageName(s.getStageName());
            item.setStageType(s.getStageType() != null ? s.getStageType().getValue() : null);
            item.setStageStatus(s.getStatus() != null ? s.getStatus().getValue() : null);
            item.setIsOverdue(s.getIsOverdue());
            return item;
        }).collect(Collectors.toList());
    }

    @Override
    public List<top.continew.admin.review.project.model.resp.NodeHistoryResp> getReviewHistory(Long projectId) {
        ReviewProjectDO project = getProjectAndCheckOwner(projectId);

        List<top.continew.admin.review.project.model.resp.NodeHistoryResp> result = new ArrayList<>();

        // 1. 申请表单节点
        result.add(nodeHistoryBuilder.buildApplicationNode(project));

        // 2. 所有已完成的评审任务（申请人可看全部）
        List<ReviewTaskDO> completedTasks = taskMapper.selectList(
                new LambdaQueryWrapper<ReviewTaskDO>()
                        .eq(ReviewTaskDO::getProjectId, projectId)
                        .eq(ReviewTaskDO::getStatus, TaskStatusEnum.COMPLETED)
                        .in(ReviewTaskDO::getTaskType,
                                List.of(TaskType.AUDIT, TaskType.REVIEW, TaskType.DECISION))
                        .eq(ReviewTaskDO::getDeleted, 0));

        result.addAll(nodeHistoryBuilder.buildNodeHistoryList(project, completedTasks));
        return result;
    }
}
