package top.continew.admin.review.form.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.review.form.mapper.FormFieldMapper;
import top.continew.admin.review.form.mapper.FormTemplateFileMapper;
import top.continew.admin.review.form.mapper.FormTemplateMapper;
import top.continew.admin.review.form.model.entity.FormTemplateDO;
import top.continew.admin.review.form.model.query.FormTemplateQuery;
import top.continew.admin.review.form.model.req.FormTemplateReq;
import top.continew.admin.review.form.model.resp.FormTemplateResp;
import top.continew.admin.review.form.service.FormTemplateService;
import top.continew.admin.system.service.FileService;
import top.continew.starter.data.service.impl.ServiceImpl;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.io.IOException;
import java.util.List;

/**
 * 表单模板业务实现
 *
 * @author zjx
 * @since 2026-01-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FormTemplateServiceImpl extends ServiceImpl<FormTemplateMapper, FormTemplateDO>
    implements FormTemplateService {

    private final FormTemplateMapper formTemplateMapper;
    private final FormFieldMapper formFieldMapper;
    private final FormTemplateFileMapper formTemplateFileMapper;
    private final FileService fileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(FormTemplateReq req) {
        // TODO: 实现创建逻辑
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, FormTemplateReq req) {
        // TODO: 实现修改逻辑
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        // TODO: 实现删除逻辑
    }

    @Override
    public FormTemplateResp getDetail(Long id) {
        // TODO: 实现查询详情逻辑
        return null;
    }

    @Override
    public PageResp<FormTemplateResp> page(FormTemplateQuery query, PageQuery pageQuery) {
        // TODO: 实现分页查询逻辑
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long uploadTemplateFile(Long templateId, Long fieldId, MultipartFile file) throws IOException {
        // TODO: 实现文件上传逻辑
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplateFile(Long fileId) {
        // TODO: 实现文件删除逻辑
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        // TODO: 实现状态更新逻辑
    }
}
