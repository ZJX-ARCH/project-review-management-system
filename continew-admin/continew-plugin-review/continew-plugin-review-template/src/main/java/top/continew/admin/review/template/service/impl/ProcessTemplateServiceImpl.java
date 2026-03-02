package top.continew.admin.review.template.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.review.template.enums.RoundType;
import top.continew.admin.review.template.mapper.ProcessTemplateMapper;
import top.continew.admin.review.template.mapper.ProcessTemplateRoundNameMapper;
import top.continew.admin.review.template.mapper.TypeProcessConfigRefMapper;
import top.continew.admin.review.template.model.entity.ProcessTemplateDO;
import top.continew.admin.review.template.model.entity.ProcessTemplateRoundNameDO;
import top.continew.admin.review.template.model.query.ProcessTemplateQuery;
import top.continew.admin.review.template.model.req.ProcessTemplateReq;
import top.continew.admin.review.template.model.req.RoundNameReq;
import top.continew.admin.review.template.model.resp.ProcessTemplateResp;
import top.continew.admin.review.template.model.resp.RoundNameResp;
import top.continew.admin.review.template.service.ProcessTemplateService;
import top.continew.starter.core.exception.BadRequestException;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.data.service.impl.ServiceImpl;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评审流程模板业务实现
 *
 * @author zjx
 * @since 2026-01-29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessTemplateServiceImpl extends ServiceImpl<ProcessTemplateMapper, ProcessTemplateDO> implements ProcessTemplateService {

    private final ProcessTemplateRoundNameMapper roundNameMapper;
    private final TypeProcessConfigRefMapper typeProcessConfigRefMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ProcessTemplateReq req) {
        // 步骤1: 模板编码处理 - 如果未提供编码则自动生成
        String templateCode = req.getTemplateCode();
        if (StrUtil.isBlank(templateCode)) {
            templateCode = this.generateCode();
            log.debug("自动生成模板编码: {}", templateCode);
        }

        // 步骤2: 数据唯一性校验
        // 2.1 校验模板编码是否已存在
        ProcessTemplateDO existingByCode = this.getByCode(templateCode);
        if (ObjectUtil.isNotNull(existingByCode)) {
            throw new BusinessException("模板编码已存在");
        }

        // 2.2 校验模板名称是否已存在
        QueryWrapper<ProcessTemplateDO> nameWrapper = new QueryWrapper<>();
        nameWrapper.eq("template_name", req.getTemplateName());
        nameWrapper.eq("deleted", 0);
        ProcessTemplateDO existingByName = baseMapper.selectOne(nameWrapper);
        if (ObjectUtil.isNotNull(existingByName)) {
            throw new BusinessException("模板名称已存在");
        }

        // 步骤3: 轮次名称配置验证（重要业务规则）
        List<RoundNameReq> roundNames = req.getRoundNames();
        if (CollUtil.isNotEmpty(roundNames)) {
            // 3.1 验证总数量匹配
            int totalRounds = req.getAuditRounds() + req.getReviewRounds() + req.getDecisionRounds();
            if (roundNames.size() != totalRounds) {
                throw new BusinessException(
                    StrUtil.format("轮次名称数量({})与配置的轮次总数({})不匹配",
                        roundNames.size(), totalRounds));
            }

            // 3.2 按类型分组统计并验证
            Map<RoundType, List<RoundNameReq>> roundNamesByType = roundNames.stream()
                .collect(Collectors.groupingBy(RoundNameReq::getRoundType));

            // 验证AUDIT轮次数量和序号连续性
            this.validateRoundNamesByType(roundNamesByType, RoundType.AUDIT,
                req.getAuditRounds(), "审核");

            // 验证REVIEW轮次数量和序号连续性
            this.validateRoundNamesByType(roundNamesByType, RoundType.REVIEW,
                req.getReviewRounds(), "评审");

            // 验证DECISION轮次数量和序号连续性
            this.validateRoundNamesByType(roundNamesByType, RoundType.DECISION,
                req.getDecisionRounds(), "决策");
        }

        // 步骤4: 保存主表数据
        ProcessTemplateDO entity = BeanUtil.toBean(req, ProcessTemplateDO.class);
        entity.setTemplateCode(templateCode);
        entity.setStatus(DisEnableStatusEnum.ENABLE); // 默认启用
        // createUser、createTime由框架自动填充
        baseMapper.insert(entity);
        Long templateId = entity.getId();

        // 步骤5: 保存轮次名称子表数据
        if (CollUtil.isNotEmpty(roundNames)) {
            List<ProcessTemplateRoundNameDO> roundNameEntities = new ArrayList<>(roundNames.size());
            for (RoundNameReq roundNameReq : roundNames) {
                ProcessTemplateRoundNameDO roundNameEntity = BeanUtil.toBean(roundNameReq,
                    ProcessTemplateRoundNameDO.class);
                roundNameEntity.setTemplateId(templateId);
                roundNameEntities.add(roundNameEntity);
            }
            // 批量插入轮次名称
            roundNameMapper.insertBatch(roundNameEntities);
        }

        // 步骤6: 记录日志并返回ID
        log.info("创建评审流程模板成功，ID={}, 编码={}, 名称={}", templateId, templateCode,
            req.getTemplateName());
        return templateId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ProcessTemplateReq req) {
        // 步骤1: 验证模板存在性
        ProcessTemplateDO existingEntity = baseMapper.selectById(id);
        if (ObjectUtil.isNull(existingEntity)) {
            throw new BusinessException("模板不存在");
        }
        String oldTemplateCode = existingEntity.getTemplateCode();

        // 步骤2: 检查模板是否被项目类型引用（如果轮次配置发生变化）
        boolean roundsChanged = !existingEntity.getAuditRounds().equals(req.getAuditRounds())
            || !existingEntity.getReviewRounds().equals(req.getReviewRounds())
            || !existingEntity.getDecisionRounds().equals(req.getDecisionRounds());

        if (roundsChanged) {
            long refCount = typeProcessConfigRefMapper.countByProcessTemplateId(id);
            if (refCount > 0) {
                throw new BusinessException(StrUtil.format(
                    "评审流程模板已被 {} 个项目类型引用，修改轮次配置后需重新配置表单映射和审批规则，" +
                    "请先在项目类型中解除引用或禁用相关项目类型后再操作", refCount));
            }
        }

        // 步骤3: 模板编码唯一性校验
        String newTemplateCode = req.getTemplateCode();
        if (StrUtil.isNotBlank(newTemplateCode) && !newTemplateCode.equals(oldTemplateCode)) {
            // 编码发生变化，检查新编码是否已存在
            ProcessTemplateDO existingByCode = this.getByCode(newTemplateCode);
            if (ObjectUtil.isNotNull(existingByCode) && !existingByCode.getId().equals(id)) {
                throw new BusinessException("模板编码已存在");
            }
        } else if (StrUtil.isBlank(newTemplateCode)) {
            // 不允许清空编码，使用旧编码
            newTemplateCode = oldTemplateCode;
        }

        // 步骤4: 模板名称唯一性校验
        if (!existingEntity.getTemplateName().equals(req.getTemplateName())) {
            QueryWrapper<ProcessTemplateDO> nameWrapper = new QueryWrapper<>();
            nameWrapper.eq("template_name", req.getTemplateName());
            nameWrapper.eq("deleted", 0);
            ProcessTemplateDO existingByName = baseMapper.selectOne(nameWrapper);
            if (ObjectUtil.isNotNull(existingByName) && !existingByName.getId().equals(id)) {
                throw new BusinessException("模板名称已存在");
            }
        }

        // 步骤5: 轮次名称配置验证（与create方法相同的验证逻辑）
        List<RoundNameReq> roundNames = req.getRoundNames();
        if (CollUtil.isNotEmpty(roundNames)) {
            // 验证总数量匹配
            int totalRounds = req.getAuditRounds() + req.getReviewRounds() + req.getDecisionRounds();
            if (roundNames.size() != totalRounds) {
                throw new BusinessException(
                    StrUtil.format("轮次名称数量({})与配置的轮次总数({})不匹配",
                        roundNames.size(), totalRounds));
            }

            // 按类型分组统计并验证
            Map<RoundType, List<RoundNameReq>> roundNamesByType = roundNames.stream()
                .collect(Collectors.groupingBy(RoundNameReq::getRoundType));

            this.validateRoundNamesByType(roundNamesByType, RoundType.AUDIT,
                req.getAuditRounds(), "审核");
            this.validateRoundNamesByType(roundNamesByType, RoundType.REVIEW,
                req.getReviewRounds(), "评审");
            this.validateRoundNamesByType(roundNamesByType, RoundType.DECISION,
                req.getDecisionRounds(), "决策");
        }

        // 步骤6: 更新主表数据
        BeanUtil.copyProperties(req, existingEntity, "id", "createUser", "createTime",
            "deleted");
        existingEntity.setTemplateCode(newTemplateCode);
        // updateUser、updateTime由框架自动填充
        baseMapper.updateById(existingEntity);

        // 步骤7: 更新轮次名称子表数据（DELETE + INSERT策略）
        // 7.1 删除旧的轮次名称数据
        QueryWrapper<ProcessTemplateRoundNameDO> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("template_id", id);
        roundNameMapper.delete(deleteWrapper);

        // 7.2 插入新的轮次名称数据
        if (CollUtil.isNotEmpty(roundNames)) {
            List<ProcessTemplateRoundNameDO> roundNameEntities = new ArrayList<>(roundNames.size());
            for (RoundNameReq roundNameReq : roundNames) {
                ProcessTemplateRoundNameDO roundNameEntity = BeanUtil.toBean(roundNameReq,
                    ProcessTemplateRoundNameDO.class);
                roundNameEntity.setTemplateId(id);
                roundNameEntities.add(roundNameEntity);
            }
            roundNameMapper.insertBatch(roundNameEntities);
        }

        // 步骤8: 记录日志
        log.info("更新评审流程模板成功，ID={}, 编码={}, 名称={}", id, newTemplateCode,
            req.getTemplateName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        // 步骤1: 参数验证
        if (CollUtil.isEmpty(ids)) {
            throw new BadRequestException("删除ID列表不能为空");
        }

        // 步骤2: 逐个验证模板是否存在
        for (Long id : ids) {
            ProcessTemplateDO entity = baseMapper.selectById(id);
            if (ObjectUtil.isNull(entity)) {
                throw new BusinessException(StrUtil.format("模板ID={} 不存在", id));
            }
        }

        // 步骤3: 检查模板是否被项目类型引用
        List<Long> referencedIds = typeProcessConfigRefMapper.findReferencedProcessTemplateIds(ids);
        if (CollUtil.isNotEmpty(referencedIds)) {
            throw new BusinessException(StrUtil.format(
                "以下评审流程模板已被项目类型引用，不允许删除：{}，请先在项目类型中解除引用后再操作",
                referencedIds));
        }

        // 步骤4: 逻辑删除主表数据
        // MyBatis Plus的deleteBatchIds会自动进行逻辑删除（如果配置了逻辑删除字段）
        baseMapper.deleteBatchIds(ids);

        // 步骤5: 逻辑删除轮次名称子表数据
        QueryWrapper<ProcessTemplateRoundNameDO> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.in("template_id", ids);
        roundNameMapper.delete(deleteWrapper);

        // 步骤6: 记录日志
        log.info("删除评审流程模板成功，IDs={}", ids);
    }

    @Override
    public ProcessTemplateResp getDetail(Long id) {
        // 步骤1: 查询主表数据
        ProcessTemplateDO entity = baseMapper.selectById(id);
        if (ObjectUtil.isNull(entity)) {
            throw new BusinessException("模板不存在");
        }

        // 步骤2: 查询轮次名称数据
        QueryWrapper<ProcessTemplateRoundNameDO> roundNameWrapper = new QueryWrapper<>();
        roundNameWrapper.eq("template_id", id);
        roundNameWrapper.eq("deleted", 0);
        // 按轮次类型和序号排序，确保返回顺序正确
        roundNameWrapper.orderByAsc("round_type", "round_sequence");
        List<ProcessTemplateRoundNameDO> roundNameEntities = roundNameMapper.selectList(
            roundNameWrapper);

        // 将轮次名称实体转换为响应对象
        List<RoundNameResp> roundNameList = BeanUtil.copyToList(roundNameEntities,
            RoundNameResp.class);

        // 步骤3: 组装响应对象
        ProcessTemplateResp resp = BeanUtil.toBean(entity, ProcessTemplateResp.class);
        resp.setRoundNames(roundNameList);

        // 步骤4: 返回结果
        return resp;
    }

    @Override
    public PageResp<ProcessTemplateResp> page(ProcessTemplateQuery query, PageQuery pageQuery) {
        // 步骤1: 构建查询条件
        QueryWrapper<ProcessTemplateDO> wrapper = new QueryWrapper<>();

        // 模板名称模糊查询
        if (StrUtil.isNotBlank(query.getTemplateName())) {
            wrapper.like("template_name", query.getTemplateName());
        }

        // 模板编码模糊查询
        if (StrUtil.isNotBlank(query.getTemplateCode())) {
            wrapper.like("template_code", query.getTemplateCode());
        }

        // 状态精确查询
        if (ObjectUtil.isNotNull(query.getStatus())) {
            wrapper.eq("status", query.getStatus());
        }

        // 逻辑删除条件（框架通常会自动处理，这里显式添加确保正确）
        wrapper.eq("deleted", 0);

        // 按创建时间降序排序（最新的在前面）
        wrapper.orderByDesc("create_time");

        // 步骤2: 应用数据权限过滤
        // TODO: 等权限模块完善后实现数据权限过滤逻辑
        // 目前暂不添加数据权限过滤，所有FLOW_ADMIN角色用户可以看到所有模板
        // 实现时需要根据用户角色和数据范围配置添加过滤条件
        // 示例：
        // if (用户角色为FLOW_ADMIN) {
        //     根据data_scope配置添加create_user过滤条件
        // } else if (用户角色为SUPER_ADMIN) {
        //     不添加过滤条件
        // } else {
        //     wrapper.eq("create_user", 当前用户ID);
        // }
        log.debug("执行评审流程模板分页查询，TODO: 需要实现数据权限过滤");

        // 步骤3: 执行分页查询
        Page<ProcessTemplateDO> page = new Page<>(pageQuery.getPage(), pageQuery.getSize());
        Page<ProcessTemplateDO> resultPage = baseMapper.selectPage(page, wrapper);

        // 步骤4: 转换为响应对象列表并查询轮次数据
        List<ProcessTemplateResp> respList = BeanUtil.copyToList(resultPage.getRecords(),
            ProcessTemplateResp.class);

        // 为每个模板查询轮次数据
        for (ProcessTemplateResp resp : respList) {
            QueryWrapper<ProcessTemplateRoundNameDO> roundNameWrapper = new QueryWrapper<>();
            roundNameWrapper.eq("template_id", resp.getId());
            roundNameWrapper.eq("deleted", 0);
            roundNameWrapper.orderByAsc("round_type", "round_sequence");
            List<ProcessTemplateRoundNameDO> roundNameEntities = roundNameMapper.selectList(
                roundNameWrapper);
            List<RoundNameResp> roundNameList = BeanUtil.copyToList(roundNameEntities,
                RoundNameResp.class);
            resp.setRoundNames(roundNameList);
        }

        // 步骤5: 组装分页响应对象
        PageResp<ProcessTemplateResp> pageResp = new PageResp<>();
        pageResp.setList(respList);
        pageResp.setTotal(resultPage.getTotal());

        // 步骤6: 返回结果
        return pageResp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        // 步骤1: 参数验证 - 转换为枚举
        DisEnableStatusEnum statusEnum;
        if (status == 1) {
            statusEnum = DisEnableStatusEnum.ENABLE;
        } else if (status == 2) {
            statusEnum = DisEnableStatusEnum.DISABLE;
        } else {
            throw new BadRequestException("状态值不合法，必须为1(启用)或2(禁用)");
        }

        // 步骤2: 验证模板存在性
        ProcessTemplateDO existingEntity = baseMapper.selectById(id);
        if (ObjectUtil.isNull(existingEntity)) {
            throw new BusinessException("模板不存在");
        }

        // 步骤3: 禁用时检查引用关系（阻止操作：禁用被引用的模板会导致已配置的项目类型无法启用）
        if (statusEnum == DisEnableStatusEnum.DISABLE) {
            long refCount = typeProcessConfigRefMapper.countByProcessTemplateId(id);
            if (refCount > 0) {
                throw new BusinessException(StrUtil.format(
                    "评审流程模板已被 {} 个项目类型引用，禁用后将导致这些项目类型无法启用，" +
                    "请先在项目类型中解除引用后再操作", refCount));
            }
        }

        // 步骤4: 更新状态字段
        ProcessTemplateDO updateEntity = new ProcessTemplateDO();
        updateEntity.setId(id);
        updateEntity.setStatus(statusEnum);
        // updateUser、updateTime由框架自动填充
        baseMapper.updateById(updateEntity);

        // 步骤5: 记录日志
        log.info("更新评审流程模板状态成功，ID={}，状态={}", id, statusEnum.getDescription());
    }

    @Override
    public ProcessTemplateDO getByCode(String templateCode) {
        // 根据编码查询模板（包含逻辑删除条件）
        QueryWrapper<ProcessTemplateDO> wrapper = new QueryWrapper<>();
        wrapper.eq("template_code", templateCode);
        wrapper.eq("deleted", 0);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public String generateCode() {
        // 步骤1: 生成基础编码（PROC_前缀 + 时间戳 = 18位，符合20位限制）
        String code;
        int retryCount = 0;
        final int maxRetries = 3;

        // 步骤2: 循环检查编码唯一性（防止极少数情况下的时间戳重复）
        while (retryCount < maxRetries) {
            code = "PROC_" + System.currentTimeMillis();

            // 检查编码是否已存在
            ProcessTemplateDO existing = this.getByCode(code);
            if (ObjectUtil.isNull(existing)) {
                // 编码唯一，返回
                log.debug("生成模板编码成功: {}", code);
                return code;
            }

            // 编码重复，等待1毫秒后重试
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BusinessException("生成编码过程中断");
            }
            retryCount++;
        }

        // 步骤3: 重试次数用尽仍然重复
        throw new BusinessException("生成模板编码失败，请重试");
    }

    @Override
    public List<ProcessTemplateResp> listEnabled() {
        // 查询所有已启用的模板
        QueryWrapper<ProcessTemplateDO> wrapper = new QueryWrapper<>();
        wrapper.eq("status", DisEnableStatusEnum.ENABLE);
        wrapper.eq("deleted", 0);
        // TODO 数据权限过滤（待权限模块统一落地）：wrapper.eq("dept_id", 当前用户 deptId)
        wrapper.orderByDesc("create_time");
        List<ProcessTemplateDO> entities = baseMapper.selectList(wrapper);

        // 转换并填充轮次数据（复用 page() 已有逻辑）
        List<ProcessTemplateResp> respList = BeanUtil.copyToList(entities, ProcessTemplateResp.class);
        for (ProcessTemplateResp resp : respList) {
            QueryWrapper<ProcessTemplateRoundNameDO> roundWrapper = new QueryWrapper<>();
            roundWrapper.eq("template_id", resp.getId());
            roundWrapper.eq("deleted", 0);
            roundWrapper.orderByAsc("round_type", "round_sequence");
            List<ProcessTemplateRoundNameDO> roundEntities = roundNameMapper.selectList(roundWrapper);
            resp.setRoundNames(BeanUtil.copyToList(roundEntities, RoundNameResp.class));
        }
        return respList;
    }

    /**
     * 验证指定类型的轮次名称数量和序号连续性
     *
     * @param roundNamesByType 按类型分组的轮次名称
     * @param roundType        要验证的轮次类型
     * @param expectedCount    期望的轮次数量
     * @param typeName         类型名称（用于错误提示）
     */
    private void validateRoundNamesByType(Map<RoundType, List<RoundNameReq>> roundNamesByType,
                                          RoundType roundType,
                                          Integer expectedCount,
                                          String typeName) {
        List<RoundNameReq> roundNamesOfType = roundNamesByType.getOrDefault(roundType,
            new ArrayList<>());

        // 验证数量匹配
        if (roundNamesOfType.size() != expectedCount) {
            throw new BusinessException(
                StrUtil.format("{}轮次名称数量({})与配置的{}轮次数量({})不匹配",
                    typeName, roundNamesOfType.size(), typeName, expectedCount));
        }

        // 如果期望数量为0，无需验证序号连续性
        if (expectedCount == 0) {
            return;
        }

        // 验证序号连续性：按序号排序后检查是否从1开始连续
        List<Integer> sequences = roundNamesOfType.stream()
            .map(RoundNameReq::getRoundSequence)
            .sorted()
            .collect(Collectors.toList());

        for (int i = 0; i < sequences.size(); i++) {
            int expectedSequence = i + 1;
            if (!sequences.get(i).equals(expectedSequence)) {
                throw new BusinessException(
                    StrUtil.format("{}轮次序号必须从1开始连续，当前序号不连续或重复", typeName));
            }
        }
    }
}
