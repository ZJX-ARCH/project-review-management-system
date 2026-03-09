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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        // 申请人只查自己的项目
        wrapper.eq(ReviewProjectDO::getApplicantId, currentUserId);

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
        List<ProjectTypeDO> types = typeMapper.selectList(
                new LambdaQueryWrapper<ProjectTypeDO>().in(ProjectTypeDO::getId, typeIds));
        return types.stream().collect(Collectors.toMap(ProjectTypeDO::getId, ProjectTypeDO::getTypeName));
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
}
