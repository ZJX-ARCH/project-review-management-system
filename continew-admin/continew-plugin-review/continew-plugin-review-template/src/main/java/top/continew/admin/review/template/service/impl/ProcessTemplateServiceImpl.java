package top.continew.admin.review.template.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.review.template.mapper.ProcessTemplateMapper;
import top.continew.admin.review.template.model.entity.ProcessTemplateDO;
import top.continew.admin.review.template.model.query.ProcessTemplateQuery;
import top.continew.admin.review.template.model.req.ProcessTemplateReq;
import top.continew.admin.review.template.model.resp.ProcessTemplateResp;
import top.continew.admin.review.template.service.ProcessTemplateService;
import top.continew.starter.data.service.impl.ServiceImpl;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.util.List;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ProcessTemplateReq req) {
        // TODO: 实现创建逻辑
        // 1. 如果templateCode为空，生成：PROCESS_ + System.currentTimeMillis()
        // 2. 校验模板编码唯一性（调用getByCode方法）
        // 3. 保存模板信息
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ProcessTemplateReq req) {
        // TODO: 实现更新逻辑
        // 1. 校验模板是否存在
        // 2. 如果修改了编码，校验新编码唯一性
        // 3. 更新模板信息
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        // TODO: 实现删除逻辑
        // 1. 校验模板是否存在
        // 2. 校验模板是否被使用（后续实现）
        // 3. 删除模板
    }

    @Override
    public ProcessTemplateResp getDetail(Long id) {
        // TODO: 实现详情查询逻辑
        // 1. 查询模板信息
        // 2. 转换为响应对象
        return null;
    }

    @Override
    public PageResp<ProcessTemplateResp> page(ProcessTemplateQuery query, PageQuery pageQuery) {
        // TODO: 实现分页查询逻辑
        // 1. 构建查询条件（根据templateName、templateCode、status模糊查询）
        // 2. 分页查询
        // 3. 转换为响应对象
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        // TODO: 实现状态更新逻辑
        // 1. 校验模板是否存在
        // 2. 更新状态字段
    }

    @Override
    public ProcessTemplateDO getByCode(String templateCode) {
        // TODO: 实现根据编码查询逻辑
        return baseMapper.selectOne(new QueryWrapper<ProcessTemplateDO>().eq("template_code", templateCode));
    }
}
