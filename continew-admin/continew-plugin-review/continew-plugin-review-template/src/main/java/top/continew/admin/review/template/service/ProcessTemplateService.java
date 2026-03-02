package top.continew.admin.review.template.service;

import top.continew.admin.review.template.model.entity.ProcessTemplateDO;
import top.continew.admin.review.template.model.query.ProcessTemplateQuery;
import top.continew.admin.review.template.model.req.ProcessTemplateReq;
import top.continew.admin.review.template.model.resp.ProcessTemplateResp;
import top.continew.starter.data.service.IService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.util.List;

public interface ProcessTemplateService extends IService<ProcessTemplateDO> {

    /**
     * 创建流程模板
     *
     * @param req 创建请求
     * @return 模板ID
     */
    Long create(ProcessTemplateReq req);

    /**
     * 修改流程模板
     *
     * @param id  模板ID
     * @param req 修改请求
     */
    void update(Long id, ProcessTemplateReq req);

    /**
     * 删除流程模板
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
    ProcessTemplateResp getDetail(Long id);

    /**
     * 分页查询模板列表
     *
     * @param query     查询条件
     * @param pageQuery 分页查询条件
     * @return 分页列表信息
     */
    PageResp<ProcessTemplateResp> page(ProcessTemplateQuery query, PageQuery pageQuery);

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
    ProcessTemplateDO getByCode(String templateCode);

    /**
     * 生成模板编码
     *
     * @return 模板编码（格式：PROCESS_ + 时间戳）
     */
    String generateCode();

    /**
     * 查询当前用户权限范围内所有已启用的模板列表（用于类型配置向导下拉选择，含轮次结构）
     *
     * @return 已启用模板列表
     */
    List<ProcessTemplateResp> listEnabled();
}
