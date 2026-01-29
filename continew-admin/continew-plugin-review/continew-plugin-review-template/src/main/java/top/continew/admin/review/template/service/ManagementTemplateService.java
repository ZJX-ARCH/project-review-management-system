package top.continew.admin.review.template.service;

import top.continew.admin.review.template.model.entity.ManagementTemplateDO;
import top.continew.admin.review.template.model.query.ManagementTemplateQuery;
import top.continew.admin.review.template.model.req.ManagementTemplateReq;
import top.continew.admin.review.template.model.resp.ManagementTemplateResp;
import top.continew.starter.data.service.IService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.util.List;

/**
 * 管理流程模板业务接口
 *
 * @author zjx
 * @since 2026-01-29
 */
public interface ManagementTemplateService extends IService<ManagementTemplateDO> {

    /**
     * 创建管理模板
     *
     * @param req 创建请求
     * @return 模板ID
     */
    Long create(ManagementTemplateReq req);

    /**
     * 修改管理模板
     *
     * @param id  模板ID
     * @param req 修改请求
     */
    void update(Long id, ManagementTemplateReq req);

    /**
     * 删除管理模板
     *
     * @param ids 模板ID列表
     */
    void delete(List<Long> ids);

    /**
     * 查询模板详情
     *
     * @param id 模板ID
     * @return 模板详情
     */
    ManagementTemplateResp getDetail(Long id);

    /**
     * 分页查询模板列表
     *
     * @param query     查询条件
     * @param pageQuery 分页查询条件
     * @return 分页列表信息
     */
    PageResp<ManagementTemplateResp> page(ManagementTemplateQuery query, PageQuery pageQuery);

    /**
     * 启用/禁用模板
     *
     * @param id     模板ID
     * @param status 状态（1=启用，2=禁用）
     */
    void updateStatus(Long id, Integer status);

    /**
     * 根据编码查询模板
     *
     * @param templateCode 模板编码
     * @return 模板信息，不存在则返回null
     */
    ManagementTemplateDO getByCode(String templateCode);

    /**
     * 生成模板编码
     *
     * @return 模板编码（格式：MGMT_ + 时间戳）
     */
    String generateCode();
}
