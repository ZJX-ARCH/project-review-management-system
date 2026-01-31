package top.continew.admin.review.form.service;

import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.review.form.model.entity.FormTemplateDO;
import top.continew.admin.review.form.model.query.FormTemplateQuery;
import top.continew.admin.review.form.model.req.FormTemplateReq;
import top.continew.admin.review.form.model.resp.FormTemplateResp;
import top.continew.starter.data.service.IService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.io.IOException;
import java.util.List;

/**
 * 表单模板业务接口
 *
 * @author zjx
 * @since 2026-01-31
 */
public interface FormTemplateService extends IService<FormTemplateDO> {

    /**
     * 创建表单模板
     *
     * @param req 创建请求
     * @return 模板ID
     */
    Long create(FormTemplateReq req);

    /**
     * 修改表单模板
     *
     * @param id  模板ID
     * @param req 修改请求
     */
    void update(Long id, FormTemplateReq req);

    /**
     * 删除表单模板
     *
     * @param ids 模板ID列表
     */
    void delete(List<Long> ids);

    /**
     * 查询模板详情(含字段配置)
     *
     * @param id 模板ID
     * @return 模板详情
     */
    FormTemplateResp getDetail(Long id);

    /**
     * 分页查询模板列表
     *
     * @param query     查询条件
     * @param pageQuery 分页条件
     * @return 分页结果
     */
    PageResp<FormTemplateResp> page(FormTemplateQuery query, PageQuery pageQuery);

    /**
     * 上传模板文件
     *
     * @param templateId 模板ID
     * @param fieldId    字段ID
     * @param file       文件
     * @return 文件ID
     */
    Long uploadTemplateFile(Long templateId, Long fieldId, MultipartFile file) throws IOException;

    /**
     * 删除模板文件
     *
     * @param fileId 文件ID
     */
    void deleteTemplateFile(Long fileId);

    /**
     * 启用/禁用模板
     *
     * @param id     模板ID
     * @param status 状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 根据编码查询模板
     *
     * @param templateCode 模板编码
     * @return 模板实体
     */
    FormTemplateDO getByCode(String templateCode);

    /**
     * 生成模板编码
     *
     * @return 模板编码
     */
    String generateCode();

    /**
     * 查询所有启用的模板列表(用于下拉选择)
     *
     * @param templateType 模板类型(可选)
     * @return 模板列表
     */
    List<FormTemplateResp> listEnabled(Integer templateType);
}
