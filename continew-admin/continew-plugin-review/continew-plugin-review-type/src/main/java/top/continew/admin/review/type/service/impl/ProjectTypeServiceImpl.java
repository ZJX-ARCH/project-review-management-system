package top.continew.admin.review.type.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.context.UserContextHolder;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.review.form.model.entity.FormTemplateDO;
import top.continew.admin.review.form.service.FormTemplateService;
import top.continew.admin.system.mapper.DeptMapper;
import top.continew.admin.system.mapper.RoleMapper;
import top.continew.admin.system.mapper.UserRoleMapper;
import top.continew.admin.system.model.entity.DeptDO;
import top.continew.admin.system.model.entity.RoleDO;
import top.continew.admin.system.model.entity.UserRoleDO;
import top.continew.admin.system.model.entity.user.UserDO;
import top.continew.admin.system.mapper.user.UserMapper;
import top.continew.admin.review.template.mapper.ManagementStageMapper;
import top.continew.admin.review.template.model.entity.ManagementStageDO;
import top.continew.admin.review.template.model.entity.ManagementTemplateDO;
import top.continew.admin.review.template.model.entity.ProcessTemplateDO;
import top.continew.admin.review.template.service.ManagementTemplateService;
import top.continew.admin.review.template.service.ProcessTemplateService;
import top.continew.admin.review.type.enums.*;
import top.continew.admin.review.type.mapper.*;
import top.continew.admin.review.type.model.entity.*;
import top.continew.admin.review.type.model.query.ProjectTypeQuery;
import top.continew.admin.review.type.model.req.*;
import top.continew.admin.review.type.model.resp.*;
import top.continew.admin.review.type.service.ProjectTypeService;
import top.continew.starter.core.exception.BadRequestException;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 项目类型 Service 实现
 *
 * @author zjx
 * @since 2026-03-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectTypeServiceImpl extends ServiceImpl<ProjectTypeMapper, ProjectTypeDO>
        implements ProjectTypeService {

    private final TypeProcessConfigMapper processConfigMapper;
    private final TypeFormMappingMapper formMappingMapper;
    private final TypePersonnelConfigMapper personnelConfigMapper;
    private final TypeApprovalConfigMapper approvalConfigMapper;
    private final TypeReviewerWeightMapper reviewerWeightMapper;
    private final ProcessTemplateService processTemplateService;
    private final ManagementTemplateService managementTemplateService;
    private final FormTemplateService formTemplateService;
    private final ManagementStageMapper stageMapper;
    private final RoleMapper roleMapper;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final DeptMapper deptMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ProjectTypeReq req) {
        // 步骤1：编码处理 - 为空则自动生成
        String typeCode = req.getTypeCode();
        if (StrUtil.isBlank(typeCode)) {
            typeCode = this.generateCode();
            log.debug("自动生成类型编码: {}", typeCode);
        }

        // 步骤2：全局唯一性校验 - typeCode
        QueryWrapper<ProjectTypeDO> codeWrapper = new QueryWrapper<>();
        codeWrapper.eq("type_code", typeCode).eq("deleted", 0);
        if (ObjectUtil.isNotNull(baseMapper.selectOne(codeWrapper))) {
            throw new BusinessException("类型编码已存在");
        }

        // 步骤3：同部门 typeName 唯一性校验
        Long deptId = UserContextHolder.getContext().getDeptId();
        QueryWrapper<ProjectTypeDO> nameWrapper = new QueryWrapper<>();
        nameWrapper.eq("type_name", req.getTypeName()).eq("dept_id", deptId).eq("deleted", 0);
        if (ObjectUtil.isNotNull(baseMapper.selectOne(nameWrapper))) {
            throw new BusinessException("类型名称在当前部门已存在");
        }

        // 步骤4：构建并保存主表实体
        ProjectTypeDO entity = BeanUtil.toBean(req, ProjectTypeDO.class);
        entity.setTypeCode(typeCode);
        entity.setDeptId(deptId);
        entity.setStatus(TypeStatusEnum.DRAFT);
        // createUser/createTime 由框架自动填充
        baseMapper.insert(entity);

        log.info("创建项目类型成功，ID={}, 编码={}, 名称={}", entity.getId(), typeCode, req.getTypeName());
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ProjectTypeReq req) {
        // 步骤1：校验存在
        ProjectTypeDO existing = requireType(id);

        // 步骤2：typeCode 不可修改
        String newCode = req.getTypeCode();
        if (StrUtil.isNotBlank(newCode) && !newCode.equals(existing.getTypeCode())) {
            throw new BadRequestException("类型编码创建后不可修改");
        }

        // 步骤3：typeName 重复校验（同 dept_id 排除自身 id）
        if (!existing.getTypeName().equals(req.getTypeName())) {
            QueryWrapper<ProjectTypeDO> nameWrapper = new QueryWrapper<>();
            nameWrapper.eq("type_name", req.getTypeName())
                    .eq("dept_id", existing.getDeptId())
                    .eq("deleted", 0)
                    .ne("id", id);
            if (ObjectUtil.isNotNull(baseMapper.selectOne(nameWrapper))) {
                throw new BusinessException("类型名称在当前部门已存在");
            }
        }

        // 步骤4：更新主表（保护不可变字段）
        BeanUtil.copyProperties(req, existing, "id", "typeCode", "deptId", "status",
                "createUser", "createTime", "deleted");
        // updateUser/updateTime 由框架自动填充
        baseMapper.updateById(existing);

        log.info("更新项目类型成功，ID={}, 名称={}", id, req.getTypeName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        // 步骤1：参数非空校验
        if (CollUtil.isEmpty(ids)) {
            throw new BadRequestException("删除ID列表不能为空");
        }

        // 步骤2：逐个存在性 + 状态校验
        for (Long id : ids) {
            ProjectTypeDO entity = baseMapper.selectById(id);
            if (ObjectUtil.isNull(entity)) {
                throw new BusinessException(StrUtil.format("类型ID={} 不存在", id));
            }
            if (TypeStatusEnum.ENABLED == entity.getStatus()) {
                throw new BusinessException(StrUtil.format("类型[{}]已启用，请先禁用后再删除", entity.getTypeName()));
            }
        }

        // 步骤3：TODO - 等 project 模块完成后检查进行中项目引用
        log.debug("删除项目类型，IDs={}，TODO: 需要检查进行中的项目引用关系", ids);

        // 步骤4：级联逻辑删除（从最深子表到主表）
        QueryWrapper<TypeReviewerWeightDO> weightWrapper = new QueryWrapper<>();
        weightWrapper.in("type_id", ids);
        reviewerWeightMapper.delete(weightWrapper);

        QueryWrapper<TypeApprovalConfigDO> approvalWrapper = new QueryWrapper<>();
        approvalWrapper.in("type_id", ids);
        approvalConfigMapper.delete(approvalWrapper);

        QueryWrapper<TypePersonnelConfigDO> personnelWrapper = new QueryWrapper<>();
        personnelWrapper.in("type_id", ids);
        personnelConfigMapper.delete(personnelWrapper);

        QueryWrapper<TypeFormMappingDO> formWrapper = new QueryWrapper<>();
        formWrapper.in("type_id", ids);
        formMappingMapper.delete(formWrapper);

        QueryWrapper<TypeProcessConfigDO> processWrapper = new QueryWrapper<>();
        processWrapper.in("type_id", ids);
        processConfigMapper.delete(processWrapper);

        baseMapper.deleteBatchIds(ids);

        log.info("删除项目类型成功，IDs={}", ids);
    }

    @Override
    public PageResp<ProjectTypeResp> page(ProjectTypeQuery query, PageQuery pageQuery) {
        // 步骤1：构建查询条件
        QueryWrapper<ProjectTypeDO> wrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(query.getTypeName())) {
            wrapper.like("type_name", query.getTypeName());
        }
        if (StrUtil.isNotBlank(query.getTypeCode())) {
            wrapper.like("type_code", query.getTypeCode());
        }
        if (ObjectUtil.isNotNull(query.getStatus())) {
            wrapper.eq("status", query.getStatus());
        }
        wrapper.eq("deleted", 0);
        // TODO 数据权限过滤（待权限模块统一落地）：wrapper.eq("dept_id", UserContextHolder.getContext().getDeptId())
        wrapper.orderByAsc("sort_order").orderByDesc("create_time");

        // 步骤2：执行分页查询
        Page<ProjectTypeDO> page = new Page<>(pageQuery.getPage(), pageQuery.getSize());
        Page<ProjectTypeDO> resultPage = baseMapper.selectPage(page, wrapper);

        // 步骤3：组装响应
        List<ProjectTypeResp> respList = BeanUtil.copyToList(resultPage.getRecords(), ProjectTypeResp.class);
        PageResp<ProjectTypeResp> pageResp = new PageResp<>();
        pageResp.setList(respList);
        pageResp.setTotal(resultPage.getTotal());
        return pageResp;
    }

    @Override
    public ProjectTypeDetailResp getDetail(Long id) {
        // 步骤1：查主表
        ProjectTypeDO entity = baseMapper.selectById(id);
        if (ObjectUtil.isNull(entity)) {
            throw new BusinessException("类型不存在");
        }
        ProjectTypeDetailResp detail = BeanUtil.toBean(entity, ProjectTypeDetailResp.class);

        // 步骤2a：流程配置
        QueryWrapper<TypeProcessConfigDO> processWrapper = new QueryWrapper<>();
        processWrapper.eq("type_id", id).eq("deleted", 0);
        List<TypeProcessConfigDO> processConfigs = processConfigMapper.selectList(processWrapper);
        detail.setProcessConfigs(BeanUtil.copyToList(processConfigs, TypeProcessConfigResp.class));

        // 步骤2b：表单映射（按 mappingType + nodeSequence 排序）
        QueryWrapper<TypeFormMappingDO> formWrapper = new QueryWrapper<>();
        formWrapper.eq("type_id", id).eq("deleted", 0)
                .orderByAsc("mapping_type", "node_sequence");
        List<TypeFormMappingDO> formMappings = formMappingMapper.selectList(formWrapper);
        detail.setFormMappings(BeanUtil.copyToList(formMappings, TypeFormMappingResp.class));

        // 步骤2c：人员范围配置
        QueryWrapper<TypePersonnelConfigDO> personnelWrapper = new QueryWrapper<>();
        personnelWrapper.eq("type_id", id).eq("deleted", 0);
        List<TypePersonnelConfigDO> personnelConfigs = personnelConfigMapper.selectList(personnelWrapper);
        detail.setPersonnelConfigs(BeanUtil.copyToList(personnelConfigs, TypePersonnelConfigResp.class));

        // 步骤2d：审批规则配置（嵌套查询评审人权重）
        QueryWrapper<TypeApprovalConfigDO> approvalWrapper = new QueryWrapper<>();
        approvalWrapper.eq("type_id", id).eq("deleted", 0);
        List<TypeApprovalConfigDO> approvalConfigs = approvalConfigMapper.selectList(approvalWrapper);
        List<TypeApprovalConfigResp> approvalRespList = BeanUtil.copyToList(approvalConfigs, TypeApprovalConfigResp.class);
        for (TypeApprovalConfigResp approvalResp : approvalRespList) {
            QueryWrapper<TypeReviewerWeightDO> weightWrapper = new QueryWrapper<>();
            weightWrapper.eq("type_id", id).eq("node_scope", approvalResp.getNodeScope()).eq("deleted", 0);
            List<TypeReviewerWeightDO> weights = reviewerWeightMapper.selectList(weightWrapper);
            approvalResp.setReviewerWeights(BeanUtil.copyToList(weights, TypeReviewerWeightResp.class));
        }
        detail.setApprovalConfigs(approvalRespList);

        return detail;
    }

    @Override
    public String generateCode() {
        int retryCount = 0;
        final int maxRetries = 3;
        while (retryCount < maxRetries) {
            String code = "TYPE_" + System.currentTimeMillis();
            QueryWrapper<ProjectTypeDO> wrapper = new QueryWrapper<>();
            wrapper.eq("type_code", code).eq("deleted", 0);
            if (ObjectUtil.isNull(baseMapper.selectOne(wrapper))) {
                log.debug("生成类型编码成功: {}", code);
                return code;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BusinessException("生成编码过程中断");
            }
            retryCount++;
        }
        throw new BusinessException("生成类型编码失败，请重试");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveProcess(Long id, List<TypeProcessConfigReq> reqs) {
        // 步骤1：存在 + 非启用状态校验
        ProjectTypeDO existing = requireType(id);
        if (TypeStatusEnum.ENABLED == existing.getStatus()) {
            throw new BusinessException("已启用的类型不允许直接修改流程配置，请先禁用");
        }

        // 步骤2：业务规则 - 必须 REVIEW + MANAGE 各一条
        if (CollUtil.isEmpty(reqs) || reqs.size() != 2) {
            throw new BadRequestException("流程配置必须包含评审流程(REVIEW)和管理流程(MANAGE)各一条");
        }
        Map<ProcessTypeEnum, TypeProcessConfigReq> reqMap = reqs.stream()
                .collect(Collectors.toMap(TypeProcessConfigReq::getProcessType, r -> r,
                        (a, b) -> { throw new BadRequestException("流程类型不能重复"); }));
        if (!reqMap.containsKey(ProcessTypeEnum.REVIEW) || !reqMap.containsKey(ProcessTypeEnum.MANAGE)) {
            throw new BadRequestException("流程配置必须同时包含 REVIEW 和 MANAGE 两种类型");
        }

        // 步骤3：校验模板启用状态并取冗余字段 templateName
        TypeProcessConfigReq reviewReq = reqMap.get(ProcessTypeEnum.REVIEW);
        ProcessTemplateDO reviewTemplate = processTemplateService.getById(reviewReq.getTemplateId());
        if (ObjectUtil.isNull(reviewTemplate) || reviewTemplate.getStatus() != DisEnableStatusEnum.ENABLE) {
            throw new BadRequestException("评审流程模板不存在或未启用");
        }

        TypeProcessConfigReq manageReq = reqMap.get(ProcessTypeEnum.MANAGE);
        ManagementTemplateDO manageTemplate = managementTemplateService.getById(manageReq.getTemplateId());
        if (ObjectUtil.isNull(manageTemplate) || manageTemplate.getStatus() != DisEnableStatusEnum.ENABLE) {
            throw new BadRequestException("管理流程模板不存在或未启用");
        }

        // 步骤4：全量替换 process_config
        QueryWrapper<TypeProcessConfigDO> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("type_id", id);
        processConfigMapper.delete(deleteWrapper);

        List<TypeProcessConfigDO> entities = new ArrayList<>(2);
        for (TypeProcessConfigReq req : reqs) {
            TypeProcessConfigDO entity = BeanUtil.toBean(req, TypeProcessConfigDO.class);
            entity.setTypeId(id);
            entity.setTemplateName(ProcessTypeEnum.REVIEW == req.getProcessType()
                    ? reviewTemplate.getTemplateName() : manageTemplate.getTemplateName());
            entities.add(entity);
        }
        processConfigMapper.insertBatch(entities);

        // 步骤5：联动清空 form_mapping（流程模板变更后节点结构可能失效）
        QueryWrapper<TypeFormMappingDO> formDeleteWrapper = new QueryWrapper<>();
        formDeleteWrapper.eq("type_id", id);
        formMappingMapper.delete(formDeleteWrapper);
        log.info("流程配置已更新，关联表单映射已清空（type_id={}），请重新配置表单映射", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFormMapping(Long id, List<TypeFormMappingReq> reqs) {
        // 步骤1：存在 + 非启用状态校验
        ProjectTypeDO existing = requireType(id);
        if (TypeStatusEnum.ENABLED == existing.getStatus()) {
            throw new BusinessException("已启用的类型不允许直接修改表单映射，请先禁用");
        }

        if (CollUtil.isEmpty(reqs)) {
            // 允许清空表单映射，直接走全量替换即可
            formMappingMapper.delete(new QueryWrapper<TypeFormMappingDO>().eq("type_id", id));
            log.info("清空表单映射，type_id={}", id);
            return;
        }

        // 步骤2：(mappingType, nodeType, nodeSequence) 三元组唯一性校验
        Set<String> tripleSet = new HashSet<>();
        for (TypeFormMappingReq req : reqs) {
            String key = req.getMappingType() + "_" + req.getNodeType() + "_" + req.getNodeSequence();
            if (!tripleSet.add(key)) {
                throw new BadRequestException(StrUtil.format("表单映射中存在重复节点：{}", key));
            }
        }

        // 步骤3：逐条校验表单模板存在且启用，并填充冗余名称
        List<TypeFormMappingDO> entities = new ArrayList<>(reqs.size());
        for (TypeFormMappingReq req : reqs) {
            FormTemplateDO formTemplate = formTemplateService.getById(req.getFormTemplateId());
            if (ObjectUtil.isNull(formTemplate) || formTemplate.getStatus() != DisEnableStatusEnum.ENABLE) {
                throw new BadRequestException(StrUtil.format("表单模板[ID={}]不存在或未启用", req.getFormTemplateId()));
            }
            TypeFormMappingDO entity = BeanUtil.toBean(req, TypeFormMappingDO.class);
            entity.setTypeId(id);
            entity.setFormTemplateName(formTemplate.getTemplateName());
            entities.add(entity);
        }

        // 步骤4：全量替换 form_mapping
        formMappingMapper.delete(new QueryWrapper<TypeFormMappingDO>().eq("type_id", id));
        formMappingMapper.insertBatch(entities);

        log.info("保存表单映射成功，type_id={}, 共{}条", id, reqs.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePersonnel(Long id, List<TypePersonnelConfigReq> reqs) {
        // 步骤1：存在 + 非启用状态校验
        ProjectTypeDO existing = requireType(id);
        if (TypeStatusEnum.ENABLED == existing.getStatus()) {
            throw new BusinessException("已启用的类型不允许直接修改人员范围配置，请先禁用");
        }

        // reqs 为空时，允许清空人员配置（与 saveFormMapping 保持一致）

        // 步骤2：业务规则校验
        Set<String> nodeKeySet = new HashSet<>();
        for (TypePersonnelConfigReq req : reqs) {
            String nodeKey = buildNodeKey(req.getNodeType(), req.getNodeSequence());
            if (!nodeKeySet.add(nodeKey)) {
                throw new BadRequestException(StrUtil.format("节点[{}]重复配置人员范围", nodeKey));
            }
            validateScopeConfig(req);
        }

        // 步骤3：全量替换 personnel_config
        personnelConfigMapper.delete(new QueryWrapper<TypePersonnelConfigDO>().eq("type_id", id));
        if (CollUtil.isNotEmpty(reqs)) {
            List<TypePersonnelConfigDO> entities = new ArrayList<>(reqs.size());
            for (TypePersonnelConfigReq req : reqs) {
                TypePersonnelConfigDO entity = BeanUtil.toBean(req, TypePersonnelConfigDO.class);
                entity.setTypeId(id);
                entities.add(entity);
            }
            personnelConfigMapper.insertBatch(entities);
        }

        log.info("保存人员范围配置成功，type_id={}, 共{}条", id, reqs.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveApproval(Long id, List<TypeApprovalConfigReq> reqs) {
        // 步骤1：存在 + 非启用状态校验
        ProjectTypeDO existing = requireType(id);
        if (TypeStatusEnum.ENABLED == existing.getStatus()) {
            throw new BusinessException("已启用的类型不允许直接修改审批规则配置，请先禁用");
        }

        // 步骤2：业务规则校验
        Set<String> nodeScopeSet = new HashSet<>();
        for (TypeApprovalConfigReq req : reqs) {
            // nodeScope 不重复
            if (!nodeScopeSet.add(req.getNodeScope())) {
                throw new BadRequestException(StrUtil.format("节点标识[{}]重复配置", req.getNodeScope()));
            }
            // approvalMode 分支合法性校验
            validateApprovalReq(req);
        }

        // 步骤3：全量替换 approval_config
        approvalConfigMapper.delete(new QueryWrapper<TypeApprovalConfigDO>().eq("type_id", id));

        if (CollUtil.isNotEmpty(reqs)) {
            List<TypeApprovalConfigDO> entities = new ArrayList<>(reqs.size());
            for (TypeApprovalConfigReq req : reqs) {
                TypeApprovalConfigDO entity = BeanUtil.toBean(req, TypeApprovalConfigDO.class);
                entity.setTypeId(id);
                entities.add(entity);
            }
            approvalConfigMapper.insertBatch(entities);
        }

        log.info("保存审批规则配置成功，type_id={}, 共{}条", id, reqs.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enable(Long id) {
        // 步骤1：存在性校验，状态不能已是 ENABLED
        ProjectTypeDO entity = requireType(id);
        if (TypeStatusEnum.ENABLED == entity.getStatus()) {
            throw new BadRequestException("类型已启用");
        }

        // 步骤2：第3层全量验证（收集全部错误后一次性抛出，不提前短路）
        List<String> errors = new ArrayList<>();

        // 验证A - 流程配置：必须 REVIEW + MANAGE 各一条且模板已启用
        QueryWrapper<TypeProcessConfigDO> processWrapper = new QueryWrapper<>();
        processWrapper.eq("type_id", id).eq("deleted", 0);
        List<TypeProcessConfigDO> processConfigs = processConfigMapper.selectList(processWrapper);
        ProcessTemplateDO reviewTemplate = null;
        ManagementTemplateDO manageTemplate = null;
        if (processConfigs.size() != 2) {
            errors.add("流程配置缺失或不完整，需配置评审流程(REVIEW)和管理流程(MANAGE)各一条");
        } else {
            for (TypeProcessConfigDO pc : processConfigs) {
                if (ProcessTypeEnum.REVIEW == pc.getProcessType()) {
                    reviewTemplate = processTemplateService.getById(pc.getTemplateId());
                    if (ObjectUtil.isNull(reviewTemplate)
                            || reviewTemplate.getStatus() != DisEnableStatusEnum.ENABLE) {
                        errors.add("评审流程模板[" + pc.getTemplateName() + "]未启用");
                    }
                } else if (ProcessTypeEnum.MANAGE == pc.getProcessType()) {
                    manageTemplate = managementTemplateService.getById(pc.getTemplateId());
                    if (ObjectUtil.isNull(manageTemplate)
                            || manageTemplate.getStatus() != DisEnableStatusEnum.ENABLE) {
                        errors.add("管理流程模板[" + pc.getTemplateName() + "]未启用");
                    }
                }
            }
        }

        // 验证B - 表单映射完整性（依赖流程配置正常才能推算节点）
        // 提前查询 stages，验证B和验证C共用
        List<ManagementStageDO> stages = Collections.emptyList();
        if (ObjectUtil.isNotNull(manageTemplate)) {
            QueryWrapper<ManagementStageDO> stageWrapper = new QueryWrapper<>();
            stageWrapper.eq("template_id", manageTemplate.getId())
                    .eq("deleted", 0).orderByAsc("stage_order");
            stages = stageMapper.selectList(stageWrapper);
        }
        if (ObjectUtil.isNotNull(reviewTemplate) && ObjectUtil.isNotNull(manageTemplate)) {
            QueryWrapper<TypeFormMappingDO> formWrapper = new QueryWrapper<>();
            formWrapper.eq("type_id", id).eq("deleted", 0);
            List<TypeFormMappingDO> formMappings = formMappingMapper.selectList(formWrapper);
            // 将已配置的节点索引为 Set：processType_nodeType_nodeSequence
            Set<String> mappedNodes = formMappings.stream()
                    .map(m -> m.getMappingType().name() + "_" + m.getNodeType().name() + "_"
                            + m.getNodeSequence())
                    .collect(Collectors.toSet());

            // REVIEW 侧：申请(APPLICATION) + 审核×N + 评审×N + 决策×N
            checkNodeFormMapping(errors, mappedNodes, "REVIEW", "APPLICATION", null);
            if (reviewTemplate.getAuditRounds() != null && reviewTemplate.getAuditRounds() > 0) {
                for (int i = 1; i <= reviewTemplate.getAuditRounds(); i++) {
                    checkNodeFormMapping(errors, mappedNodes, "REVIEW", "AUDIT", i);
                }
            }
            if (reviewTemplate.getReviewRounds() != null && reviewTemplate.getReviewRounds() > 0) {
                for (int i = 1; i <= reviewTemplate.getReviewRounds(); i++) {
                    checkNodeFormMapping(errors, mappedNodes, "REVIEW", "REVIEW", i);
                }
            }
            if (reviewTemplate.getDecisionRounds() != null && reviewTemplate.getDecisionRounds() > 0) {
                for (int i = 1; i <= reviewTemplate.getDecisionRounds(); i++) {
                    checkNodeFormMapping(errors, mappedNodes, "REVIEW", "DECISION", i);
                }
            }

            // MANAGE 侧：每个阶段对应一个 STAGE 节点（按 stageOrder）
            for (ManagementStageDO stage : stages) {
                checkNodeFormMapping(errors, mappedNodes, "MANAGE", "STAGE", stage.getStageOrder());
            }

            // 校验已配置的表单模板是否仍处于启用状态
            Set<Long> formTemplateIds = formMappings.stream()
                    .map(TypeFormMappingDO::getFormTemplateId).collect(Collectors.toSet());
            for (Long formTemplateId : formTemplateIds) {
                FormTemplateDO ft = formTemplateService.getById(formTemplateId);
                if (ObjectUtil.isNull(ft) || ft.getStatus() != DisEnableStatusEnum.ENABLE) {
                    errors.add("表单模板[ID=" + formTemplateId + "]未启用或已被删除");
                }
            }
        }

        // 验证C - 人员范围：所有流程节点（评审轮次+管理阶段）必须配置人员范围
        // 注：若 reviewTemplate 或 manageTemplate 未配置，相关节点键不进入期望集（验证A/B已报错）
        QueryWrapper<TypePersonnelConfigDO> personnelWrapper = new QueryWrapper<>();
        personnelWrapper.eq("type_id", id).eq("deleted", 0);
        List<TypePersonnelConfigDO> personnelConfigs = personnelConfigMapper.selectList(personnelWrapper);
        Set<String> configuredPersonnelKeys = personnelConfigs.stream()
                .map(p -> buildNodeKey(p.getNodeType(), p.getNodeSequence()))
                .collect(Collectors.toSet());

        // 构建期望节点键（与 formMapping 验证保持一致）
        Set<String> expectedPersonnelKeys = new HashSet<>();
        expectedPersonnelKeys.add(buildNodeKey("APPLICATION", null));
        if (ObjectUtil.isNotNull(reviewTemplate)) {
            if (reviewTemplate.getAuditRounds() != null && reviewTemplate.getAuditRounds() > 0) {
                for (int i = 1; i <= reviewTemplate.getAuditRounds(); i++) {
                    expectedPersonnelKeys.add(buildNodeKey("AUDIT", i));
                }
            }
            if (reviewTemplate.getReviewRounds() != null && reviewTemplate.getReviewRounds() > 0) {
                for (int i = 1; i <= reviewTemplate.getReviewRounds(); i++) {
                    expectedPersonnelKeys.add(buildNodeKey("REVIEW", i));
                }
            }
            if (reviewTemplate.getDecisionRounds() != null && reviewTemplate.getDecisionRounds() > 0) {
                for (int i = 1; i <= reviewTemplate.getDecisionRounds(); i++) {
                    expectedPersonnelKeys.add(buildNodeKey("DECISION", i));
                }
            }
        }
        for (ManagementStageDO stage : stages) {
            expectedPersonnelKeys.add(buildNodeKey("STAGE", stage.getStageOrder()));
        }

        for (String key : expectedPersonnelKeys) {
            if (!configuredPersonnelKeys.contains(key)) {
                errors.add("节点[" + key + "]未配置人员范围");
            }
        }

        // 验证D - 审批规则：AUDIT/REVIEW/DECISION 各轮 + ACCEPTANCE 节点必须全部配置
        if (ObjectUtil.isNotNull(reviewTemplate)) {
            QueryWrapper<TypeApprovalConfigDO> approvalWrapper = new QueryWrapper<>();
            approvalWrapper.eq("type_id", id).eq("deleted", 0);
            List<TypeApprovalConfigDO> approvalConfigs = approvalConfigMapper.selectList(approvalWrapper);
            Set<String> configuredNodes = approvalConfigs.stream()
                    .map(TypeApprovalConfigDO::getNodeScope).collect(Collectors.toSet());

            if (reviewTemplate.getAuditRounds() != null && reviewTemplate.getAuditRounds() > 0) {
                for (int i = 1; i <= reviewTemplate.getAuditRounds(); i++) {
                    String node = "AUDIT_" + i;
                    if (!configuredNodes.contains(node)) {
                        errors.add("节点[" + node + "]未配置审批规则");
                    }
                }
            }
            if (reviewTemplate.getReviewRounds() != null && reviewTemplate.getReviewRounds() > 0) {
                for (int i = 1; i <= reviewTemplate.getReviewRounds(); i++) {
                    String node = "REVIEW_" + i;
                    if (!configuredNodes.contains(node)) {
                        errors.add("节点[" + node + "]未配置审批规则");
                    }
                }
            }
            if (reviewTemplate.getDecisionRounds() != null && reviewTemplate.getDecisionRounds() > 0) {
                for (int i = 1; i <= reviewTemplate.getDecisionRounds(); i++) {
                    String node = "DECISION_" + i;
                    if (!configuredNodes.contains(node)) {
                        errors.add("节点[" + node + "]未配置审批规则");
                    }
                }
            }
            // ACCEPTANCE 节点来自管理流程的验收阶段
            if (!configuredNodes.contains("ACCEPTANCE")) {
                errors.add("节点[ACCEPTANCE]未配置审批规则");
            }
        }

        // 汇总错误，一次性抛出
        if (!errors.isEmpty()) {
            throw new BusinessException("启用校验失败：\n" + String.join("\n", errors));
        }

        // 步骤3：所有验证通过，更新状态为 ENABLED
        ProjectTypeDO updateEntity = new ProjectTypeDO();
        updateEntity.setId(id);
        updateEntity.setStatus(TypeStatusEnum.ENABLED);
        baseMapper.updateById(updateEntity);

        log.info("启用项目类型成功，ID={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disable(Long id) {
        // 步骤1：存在 + 状态校验
        ProjectTypeDO entity = requireType(id);
        if (TypeStatusEnum.ENABLED != entity.getStatus()) {
            throw new BadRequestException("类型当前不是启用状态");
        }

        // 步骤2：更新状态为 DISABLED
        ProjectTypeDO updateEntity = new ProjectTypeDO();
        updateEntity.setId(id);
        updateEntity.setStatus(TypeStatusEnum.DISABLED);
        baseMapper.updateById(updateEntity);

        log.info("禁用项目类型成功，ID={}", id);
    }

    // ======================== 私有辅助方法 ========================

    /**
     * 查询类型，不存在则抛出异常
     */
    private ProjectTypeDO requireType(Long id) {
        ProjectTypeDO entity = baseMapper.selectById(id);
        if (ObjectUtil.isNull(entity)) {
            throw new BusinessException("类型不存在");
        }
        return entity;
    }

    /**
     * 校验 scopeConfig 结构合法性（按 scopeType 分支校验）
     */
    private void validateScopeConfig(TypePersonnelConfigReq req) {
        Map<String, Object> scopeConfig = req.getScopeConfig();
        if (ObjectUtil.isNull(scopeConfig)) {
            throw new BadRequestException(StrUtil.format("节点[{}]的范围配置不能为空",
                    req.getNodeType()));
        }
        switch (req.getScopeType()) {
            case DEPT:
                if (!scopeConfig.containsKey("deptIds")
                        || ObjectUtil.isEmpty(scopeConfig.get("deptIds"))) {
                    throw new BadRequestException("DEPT类型的范围配置必须包含非空的 deptIds 字段");
                }
                break;
            case USER:
                if (!scopeConfig.containsKey("userIds")
                        || ObjectUtil.isEmpty(scopeConfig.get("userIds"))) {
                    throw new BadRequestException("USER类型的范围配置必须包含非空的 userIds 字段");
                }
                break;
            case ROLE:
                if (!scopeConfig.containsKey("businessRoles")
                        || ObjectUtil.isEmpty(scopeConfig.get("businessRoles"))) {
                    throw new BadRequestException("ROLE类型的范围配置必须包含非空的 businessRoles 字段");
                }
                break;
            case COMBINED:
                if (!scopeConfig.containsKey("rule")
                        || StrUtil.isBlank(String.valueOf(scopeConfig.get("rule")))) {
                    throw new BadRequestException("COMBINED类型的范围配置必须包含非空的 rule 字段");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 按 approvalMode 分支校验审批规则请求参数合法性
     */
    private void validateApprovalReq(TypeApprovalConfigReq req) {
        switch (req.getApprovalMode()) {
            case VOTE_MAJORITY_PASS:
                if (ObjectUtil.isNull(req.getMajorityRatio())) {
                    throw new BadRequestException(StrUtil.format(
                            "节点[{}] VOTE_MAJORITY_PASS 模式下 majorityRatio 不能为空", req.getNodeScope()));
                }
                break;
            case SCORE_PASS:
                if (ObjectUtil.isNull(req.getPassThreshold())) {
                    throw new BadRequestException(StrUtil.format(
                            "节点[{}] SCORE_PASS 模式下 passThreshold 不能为空", req.getNodeScope()));
                }
                // 多个 SCORE_TABLE 字段间权重之和必须为 1.0
                if (CollUtil.isNotEmpty(req.getScoreTableWeights())
                        && req.getScoreTableWeights().size() > 1) {
                    BigDecimal total = req.getScoreTableWeights().values().stream()
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    if (total.subtract(BigDecimal.ONE).abs().compareTo(new BigDecimal("0.0001")) > 0) {
                        throw new BadRequestException(StrUtil.format(
                                "节点[{}]评分表字段权重之和必须为1.0（当前={}）", req.getNodeScope(), total));
                    }
                }
                break;
            default:
                break;
        }
        // ACCEPTANCE 节点必须配置 rejectBackTo
        if ("ACCEPTANCE".equals(req.getNodeScope()) && StrUtil.isBlank(req.getRejectBackTo())) {
            throw new BadRequestException("ACCEPTANCE 节点必须配置 rejectBackTo（驳回回退目标）");
        }
    }

    /**
     * 检查指定节点是否已配置表单映射，缺失则向 errors 列表添加提示
     */
    private void checkNodeFormMapping(List<String> errors, Set<String> mappedNodes,
                                       String processType, String nodeType, Integer nodeSequence) {
        String key = processType + "_" + nodeType + "_" + nodeSequence;
        if (!mappedNodes.contains(key)) {
            errors.add(StrUtil.format("节点[{}-{}-{}]未配置表单", processType, nodeType,
                    nodeSequence == null ? "N/A" : nodeSequence));
        }
    }

    /**
     * 构建人员配置节点键（与表单映射节点键格式一致）
     *
     * @param nodeType     节点类型（APPLICATION/AUDIT/REVIEW/DECISION/STAGE）
     * @param nodeSequence 节点序号，APPLICATION 时为 null
     * @return 节点键，如 "APPLICATION"、"AUDIT_1"、"STAGE_2"
     */
    private static String buildNodeKey(String nodeType, Integer nodeSequence) {
        return nodeType + (nodeSequence != null ? "_" + nodeSequence : "");
    }

    @Override
    public Map<String, Long> getRoleMap(List<String> codes) {
        if (CollUtil.isEmpty(codes)) {
            return Collections.emptyMap();
        }
        QueryWrapper<RoleDO> wrapper = new QueryWrapper<>();
        wrapper.in("code", codes).eq("deleted", 0);
        List<RoleDO> roles = roleMapper.selectList(wrapper);
        return roles.stream().collect(Collectors.toMap(RoleDO::getCode, RoleDO::getId));
    }

    @Override
    public List<ReviewPersonResp> searchPersons(Long roleId, String keyword, int limit) {
        // 步骤1：若指定角色，先查出拥有该角色的 userId 集合
        Set<Long> roleUserIds = null;
        if (roleId != null) {
            QueryWrapper<UserRoleDO> urWrapper = new QueryWrapper<>();
            urWrapper.eq("role_id", roleId).select("user_id");
            List<UserRoleDO> userRoles = userRoleMapper.selectList(urWrapper);
            roleUserIds = userRoles.stream().map(UserRoleDO::getUserId).collect(Collectors.toSet());
            if (roleUserIds.isEmpty()) {
                return Collections.emptyList();
            }
        }

        // 步骤2：查询用户表（不走 @DataPermission，避免权限拦截）
        QueryWrapper<UserDO> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0).eq("status", DisEnableStatusEnum.ENABLE.getValue());
        if (roleUserIds != null) {
            wrapper.in("id", roleUserIds);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like("nickname", keyword).or().like("username", keyword));
        }
        wrapper.last("LIMIT " + limit);

        List<UserDO> users = userMapper.selectList(wrapper);
        return users.stream()
                .map(u -> new ReviewPersonResp(String.valueOf(u.getId()), u.getNickname(), u.getUsername()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewPersonResp> getPersonsByIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        QueryWrapper<UserDO> wrapper = new QueryWrapper<>();
        wrapper.in("id", ids).eq("deleted", 0);
        List<UserDO> users = userMapper.selectList(wrapper);
        return users.stream()
                .map(u -> new ReviewPersonResp(String.valueOf(u.getId()), u.getNickname(), u.getUsername()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getDeptTree() {
        Long currentDeptId = UserContextHolder.getContext().getDeptId();
        if (currentDeptId == null) {
            return Collections.emptyList();
        }

        // 查询当前部门及其全部子孙部门（通过 ancestors 字段中包含 currentDeptId 判断是否为后代）
        // 注意：不按 status 过滤——禁用状态的部门仍需显示，与系统 listChildren 行为保持一致
        QueryWrapper<DeptDO> wrapper = new QueryWrapper<>();
        wrapper.and(w -> w.eq("id", currentDeptId)
                        .or().apply("FIND_IN_SET({0}, ancestors)", currentDeptId))
                .eq("deleted", 0)
                .orderByAsc("parent_id", "sort");
        List<DeptDO> depts = deptMapper.selectList(wrapper);

        return buildDeptTree(depts);
    }

    /**
     * 将扁平部门列表构建为树形结构
     */
    private List<Map<String, Object>> buildDeptTree(List<DeptDO> depts) {
        Set<Long> allIds = depts.stream().map(DeptDO::getId).collect(Collectors.toSet());
        Map<Long, List<DeptDO>> childrenMap = new HashMap<>();
        for (DeptDO dept : depts) {
            childrenMap.computeIfAbsent(dept.getParentId(), k -> new ArrayList<>()).add(dept);
        }
        // 根节点：parentId 不在结果集内（即当前用户所在部门自身）
        List<Map<String, Object>> roots = new ArrayList<>();
        for (DeptDO dept : depts) {
            if (!allIds.contains(dept.getParentId())) {
                roots.add(toDeptNode(dept, childrenMap));
            }
        }
        return roots;
    }

    @Override
    public int countScope(CountScopeReq req) {
        if (CollUtil.isEmpty(req.getRules())) {
            return 0;
        }

        // 步骤1：收集所有规则的候选用户ID（并集）
        Set<Long> candidateUserIds = collectCandidateUserIds(req.getRules());
        if (candidateUserIds.isEmpty()) {
            return 0;
        }

        // 步骤2：若指定角色，与角色用户集取交集
        if (req.getRoleId() != null) {
            QueryWrapper<UserRoleDO> urWrapper = new QueryWrapper<>();
            urWrapper.eq("role_id", req.getRoleId()).select("user_id");
            Set<Long> roleUserIds = userRoleMapper.selectList(urWrapper)
                    .stream().map(UserRoleDO::getUserId).collect(Collectors.toSet());
            if (roleUserIds.isEmpty()) {
                return 0;
            }
            candidateUserIds.retainAll(roleUserIds);
            if (candidateUserIds.isEmpty()) {
                return 0;
            }
        }

        // 步骤3：过滤活跃状态并计数
        QueryWrapper<UserDO> wrapper = new QueryWrapper<>();
        wrapper.in("id", candidateUserIds)
                .eq("deleted", 0)
                .eq("status", DisEnableStatusEnum.ENABLE.getValue());
        return userMapper.selectCount(wrapper).intValue();
    }

    /**
     * 收集所有范围规则中的候选用户ID（并集，不过滤状态，DEPT规则已在查询时过滤 deleted）
     */
    private Set<Long> collectCandidateUserIds(List<CountScopeReq.ScopeRuleItem> rules) {
        Set<Long> result = new HashSet<>();
        for (CountScopeReq.ScopeRuleItem rule : rules) {
            if ("USER".equals(rule.getScopeType())) {
                Object userIdsObj = rule.getScopeConfig().get("userIds");
                if (userIdsObj instanceof List) {
                    for (Object id : (List<?>) userIdsObj) {
                        if (id != null) {
                            try {
                                result.add(Long.parseLong(String.valueOf(id)));
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
                }
            } else if ("DEPT".equals(rule.getScopeType())) {
                Object deptIdsObj = rule.getScopeConfig().get("deptIds");
                boolean includeSub = Boolean.TRUE.equals(rule.getScopeConfig().get("includeSub"));
                if (deptIdsObj instanceof List) {
                    Set<Long> expandedDeptIds = expandDeptIds((List<?>) deptIdsObj, includeSub);
                    if (!expandedDeptIds.isEmpty()) {
                        QueryWrapper<UserDO> deptUserWrapper = new QueryWrapper<>();
                        deptUserWrapper.in("dept_id", expandedDeptIds).eq("deleted", 0).select("id");
                        userMapper.selectList(deptUserWrapper).stream()
                                .map(UserDO::getId).forEach(result::add);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 展开部门ID集合（若 includeSub=true 则递归包含所有子孙部门）
     */
    private Set<Long> expandDeptIds(List<?> deptIdList, boolean includeSub) {
        Set<Long> result = new HashSet<>();
        for (Object id : deptIdList) {
            if (id == null) {
                continue;
            }
            try {
                long deptId = Long.parseLong(String.valueOf(id));
                result.add(deptId);
                if (includeSub) {
                    QueryWrapper<DeptDO> subWrapper = new QueryWrapper<>();
                    subWrapper.apply("FIND_IN_SET({0}, ancestors)", deptId).eq("deleted", 0);
                    deptMapper.selectList(subWrapper).stream().map(DeptDO::getId).forEach(result::add);
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return result;
    }

    private Map<String, Object> toDeptNode(DeptDO dept, Map<Long, List<DeptDO>> childrenMap) {
        Map<String, Object> node = new LinkedHashMap<>();
        node.put("key", String.valueOf(dept.getId()));
        node.put("title", dept.getName());
        List<DeptDO> children = childrenMap.getOrDefault(dept.getId(), Collections.emptyList());
        if (!children.isEmpty()) {
            node.put("children", children.stream()
                    .map(c -> toDeptNode(c, childrenMap))
                    .collect(Collectors.toList()));
        }
        return node;
    }
}
