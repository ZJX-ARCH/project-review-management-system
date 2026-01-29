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
import top.continew.admin.review.template.mapper.ManagementStageMapper;
import top.continew.admin.review.template.mapper.ManagementTemplateMapper;
import top.continew.admin.review.template.model.entity.ManagementStageDO;
import top.continew.admin.review.template.model.entity.ManagementTemplateDO;
import top.continew.admin.review.template.model.query.ManagementTemplateQuery;
import top.continew.admin.review.template.model.req.ManagementStageReq;
import top.continew.admin.review.template.model.req.ManagementTemplateReq;
import top.continew.admin.review.template.model.resp.ManagementStageResp;
import top.continew.admin.review.template.model.resp.ManagementTemplateResp;
import top.continew.admin.review.template.service.ManagementTemplateService;
import top.continew.starter.core.exception.BadRequestException;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.data.service.impl.ServiceImpl;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 管理流程模板业务实现
 *
 * @author zjx
 * @since 2026-01-29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ManagementTemplateServiceImpl extends ServiceImpl<ManagementTemplateMapper, ManagementTemplateDO> implements ManagementTemplateService {

    private final ManagementStageMapper stageMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ManagementTemplateReq req) {
        // 步骤1: 模板编码处理 - 如果未提供编码则自动生成
        String templateCode = req.getTemplateCode();
        if (StrUtil.isBlank(templateCode)) {
            templateCode = this.generateCode();
            log.debug("自动生成模板编码: {}", templateCode);
        }

        // 步骤2: 数据唯一性校验
        // 2.1 校验模板编码是否已存在
        ManagementTemplateDO existingByCode = this.getByCode(templateCode);
        if (ObjectUtil.isNotNull(existingByCode)) {
            throw new BusinessException("模板编码已存在");
        }

        // 2.2 校验模板名称是否已存在
        QueryWrapper<ManagementTemplateDO> nameWrapper = new QueryWrapper<>();
        nameWrapper.eq("template_name", req.getTemplateName());
        nameWrapper.eq("deleted", 0);
        ManagementTemplateDO existingByName = baseMapper.selectOne(nameWrapper);
        if (ObjectUtil.isNotNull(existingByName)) {
            throw new BusinessException("模板名称已存在");
        }

        // 步骤3: 阶段配置验证（重要业务规则）
        List<ManagementStageReq> stages = req.getStages();
        if (CollUtil.isNotEmpty(stages)) {
            this.validateStageOrder(stages);
        }

        // 步骤4: 保存主表数据
        ManagementTemplateDO entity = BeanUtil.toBean(req, ManagementTemplateDO.class);
        entity.setTemplateCode(templateCode);
        entity.setStatus(DisEnableStatusEnum.ENABLE); // 默认启用
        // createUser、createTime由框架自动填充
        baseMapper.insert(entity);
        Long templateId = entity.getId();

        // 步骤5: 保存阶段配置子表数据
        if (CollUtil.isNotEmpty(stages)) {
            List<ManagementStageDO> stageEntities = new ArrayList<>(stages.size());
            for (ManagementStageReq stageReq : stages) {
                ManagementStageDO stageEntity = BeanUtil.toBean(stageReq, ManagementStageDO.class);
                stageEntity.setTemplateId(templateId);
                stageEntities.add(stageEntity);
            }
            // 批量插入阶段配置
            stageMapper.insertBatch(stageEntities);
        }

        // 步骤6: 记录日志并返回ID
        log.info("创建管理流程模板成功，ID={}, 编码={}, 名称={}", templateId, templateCode,
            req.getTemplateName());
        return templateId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ManagementTemplateReq req) {
        // 步骤1: 验证模板存在性
        ManagementTemplateDO existingEntity = baseMapper.selectById(id);
        if (ObjectUtil.isNull(existingEntity)) {
            throw new BusinessException("模板不存在");
        }
        String oldTemplateCode = existingEntity.getTemplateCode();

        // 步骤2: 检查模板是否被项目类型引用（如果阶段配置发生变化）
        // TODO: 等type模块实现后，需要查询project_type表检查是否被引用
        // 如果阶段数量、顺序或类型发生变化且被引用，应抛出异常
        log.warn("模板ID={} 正在更新，TODO: 需要检查项目类型引用关系", id);

        // 步骤3: 模板编码唯一性校验
        String newTemplateCode = req.getTemplateCode();
        if (StrUtil.isNotBlank(newTemplateCode) && !newTemplateCode.equals(oldTemplateCode)) {
            ManagementTemplateDO existingByCode = this.getByCode(newTemplateCode);
            if (ObjectUtil.isNotNull(existingByCode) && !existingByCode.getId().equals(id)) {
                throw new BusinessException("模板编码已存在");
            }
        } else if (StrUtil.isBlank(newTemplateCode)) {
            newTemplateCode = oldTemplateCode;
        }

        // 步骤4: 模板名称唯一性校验
        if (!existingEntity.getTemplateName().equals(req.getTemplateName())) {
            QueryWrapper<ManagementTemplateDO> nameWrapper = new QueryWrapper<>();
            nameWrapper.eq("template_name", req.getTemplateName());
            nameWrapper.eq("deleted", 0);
            ManagementTemplateDO existingByName = baseMapper.selectOne(nameWrapper);
            if (ObjectUtil.isNotNull(existingByName) && !existingByName.getId().equals(id)) {
                throw new BusinessException("模板名称已存在");
            }
        }

        // 步骤5: 阶段配置验证
        List<ManagementStageReq> stages = req.getStages();
        if (CollUtil.isNotEmpty(stages)) {
            this.validateStageOrder(stages);
        }

        // 步骤6: 更新主表数据
        BeanUtil.copyProperties(req, existingEntity, "id", "createUser", "createTime",
            "deleted");
        existingEntity.setTemplateCode(newTemplateCode);
        baseMapper.updateById(existingEntity);

        // 步骤7: 更新阶段配置子表数据（DELETE + INSERT策略）
        QueryWrapper<ManagementStageDO> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("template_id", id);
        stageMapper.delete(deleteWrapper);

        if (CollUtil.isNotEmpty(stages)) {
            List<ManagementStageDO> stageEntities = new ArrayList<>(stages.size());
            for (ManagementStageReq stageReq : stages) {
                ManagementStageDO stageEntity = BeanUtil.toBean(stageReq, ManagementStageDO.class);
                stageEntity.setTemplateId(id);
                stageEntities.add(stageEntity);
            }
            stageMapper.insertBatch(stageEntities);
        }

        // 步骤8: 记录日志
        log.info("更新管理流程模板成功，ID={}, 编码={}, 名称={}", id, newTemplateCode,
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
            ManagementTemplateDO entity = baseMapper.selectById(id);
            if (ObjectUtil.isNull(entity)) {
                throw new BusinessException(StrUtil.format("模板ID={} 不存在", id));
            }
        }

        // 步骤3: 检查模板是否被项目类型引用
        // TODO: 等type模块实现后，需要查询project_type表检查是否被引用
        log.debug("删除管理流程模板，IDs={}，TODO: 需要检查项目类型引用关系", ids);

        // 步骤4: 逻辑删除主表数据
        baseMapper.deleteBatchIds(ids);

        // 步骤5: 逻辑删除阶段配置子表数据
        QueryWrapper<ManagementStageDO> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.in("template_id", ids);
        stageMapper.delete(deleteWrapper);

        // 步骤6: 记录日志
        log.info("删除管理流程模板成功，IDs={}", ids);
    }

    @Override
    public ManagementTemplateResp getDetail(Long id) {
        // 步骤1: 查询主表数据
        ManagementTemplateDO entity = baseMapper.selectById(id);
        if (ObjectUtil.isNull(entity)) {
            throw new BusinessException("模板不存在");
        }

        // 步骤2: 查询阶段配置数据
        QueryWrapper<ManagementStageDO> stageWrapper = new QueryWrapper<>();
        stageWrapper.eq("template_id", id);
        stageWrapper.eq("deleted", 0);
        // 按阶段顺序排序
        stageWrapper.orderByAsc("stage_order");
        List<ManagementStageDO> stageEntities = stageMapper.selectList(stageWrapper);

        // 将阶段实体转换为响应对象
        List<ManagementStageResp> stageList = BeanUtil.copyToList(stageEntities,
            ManagementStageResp.class);

        // 步骤3: 组装响应对象
        ManagementTemplateResp resp = BeanUtil.toBean(entity, ManagementTemplateResp.class);
        resp.setStages(stageList);

        // 步骤4: 返回结果
        return resp;
    }

    @Override
    public PageResp<ManagementTemplateResp> page(ManagementTemplateQuery query, PageQuery pageQuery) {
        // 步骤1: 构建查询条件
        QueryWrapper<ManagementTemplateDO> wrapper = new QueryWrapper<>();

        if (StrUtil.isNotBlank(query.getTemplateName())) {
            wrapper.like("template_name", query.getTemplateName());
        }

        if (StrUtil.isNotBlank(query.getTemplateCode())) {
            wrapper.like("template_code", query.getTemplateCode());
        }

        if (ObjectUtil.isNotNull(query.getStatus())) {
            wrapper.eq("status", query.getStatus());
        }

        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("create_time");

        // 步骤2: 应用数据权限过滤
        // TODO: 等权限模块完善后实现数据权限过滤逻辑
        log.debug("执行管理流程模板分页查询，TODO: 需要实现数据权限过滤");

        // 步骤3: 执行分页查询
        Page<ManagementTemplateDO> page = new Page<>(pageQuery.getPage(), pageQuery.getSize());
        Page<ManagementTemplateDO> resultPage = baseMapper.selectPage(page, wrapper);

        // 步骤4: 转换为响应对象列表
        List<ManagementTemplateResp> respList = BeanUtil.copyToList(resultPage.getRecords(),
            ManagementTemplateResp.class);

        // 步骤5: 组装分页响应对象
        PageResp<ManagementTemplateResp> pageResp = new PageResp<>();
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
        ManagementTemplateDO existingEntity = baseMapper.selectById(id);
        if (ObjectUtil.isNull(existingEntity)) {
            throw new BusinessException("模板不存在");
        }

        // 步骤3: 检查是否被项目类型使用
        if (statusEnum == DisEnableStatusEnum.DISABLE) {
            // TODO: 等type模块实现后，查询project_type表检查引用关系
            log.warn("模板ID={} 被设置为禁用状态，TODO: 需要检查项目类型引用关系", id);
        }

        // 步骤4: 更新状态字段
        ManagementTemplateDO updateEntity = new ManagementTemplateDO();
        updateEntity.setId(id);
        updateEntity.setStatus(statusEnum);
        baseMapper.updateById(updateEntity);

        // 步骤5: 记录日志
        log.info("更新管理流程模板状态成功，ID={}，状态={}", id, statusEnum.getDescription());
    }

    @Override
    public ManagementTemplateDO getByCode(String templateCode) {
        // 根据编码查询模板（包含逻辑删除条件）
        QueryWrapper<ManagementTemplateDO> wrapper = new QueryWrapper<>();
        wrapper.eq("template_code", templateCode);
        wrapper.eq("deleted", 0);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public String generateCode() {
        // 步骤1: 生成基础编码
        String code;
        int retryCount = 0;
        final int maxRetries = 3;

        // 步骤2: 循环检查编码唯一性
        while (retryCount < maxRetries) {
            code = "MGMT_" + System.currentTimeMillis();

            ManagementTemplateDO existing = this.getByCode(code);
            if (ObjectUtil.isNull(existing)) {
                log.debug("生成模板编码成功: {}", code);
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

        // 步骤3: 重试次数用尽
        throw new BusinessException("生成模板编码失败，请重试");
    }

    /**
     * 验证阶段顺序的连续性
     *
     * @param stages 阶段配置列表
     */
    private void validateStageOrder(List<ManagementStageReq> stages) {
        // 提取所有阶段顺序并排序
        List<Integer> orders = stages.stream()
            .map(ManagementStageReq::getStageOrder)
            .sorted()
            .collect(Collectors.toList());

        // 使用Set检查是否有重复序号
        Set<Integer> orderSet = new HashSet<>(orders);
        if (orderSet.size() != orders.size()) {
            throw new BusinessException("阶段顺序不能有重复");
        }

        // 验证序号从1开始且连续
        if (orders.get(0) != 1) {
            throw new BusinessException("阶段顺序必须从1开始");
        }

        for (int i = 0; i < orders.size(); i++) {
            int expectedOrder = i + 1;
            if (!orders.get(i).equals(expectedOrder)) {
                throw new BusinessException("阶段顺序必须连续，不能跳号");
            }
        }
    }
}
