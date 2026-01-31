package top.continew.admin.review.form.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.review.form.model.query.FormTemplateQuery;
import top.continew.admin.review.form.model.req.FormTemplateReq;
import top.continew.admin.review.form.model.resp.FormTemplateResp;
import top.continew.admin.review.form.service.FormTemplateService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.web.model.R;

import java.io.IOException;
import java.util.List;

/**
 * 表单模板 API
 *
 * @author zjx
 * @since 2026-01-31
 */
@Tag(name = "表单模板 API")
@RestController
@RequestMapping("/review/template/form")
@RequiredArgsConstructor
public class FormTemplateController {

    private final FormTemplateService formTemplateService;

    /**
     * 创建表单模板
     */
    @PostMapping
    @Operation(summary = "创建表单模板")
    @SaCheckPermission("review:template:form:create")
    public R<Long> create(@Valid @RequestBody FormTemplateReq req) {
        Long id = formTemplateService.create(req);
        return R.ok(id);
    }

    /**
     * 修改表单模板
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改表单模板")
    @SaCheckPermission("review:template:form:update")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody FormTemplateReq req) {
        formTemplateService.update(id, req);
        return R.ok();
    }

    /**
     * 删除表单模板
     */
    @DeleteMapping
    @Operation(summary = "删除表单模板")
    @SaCheckPermission("review:template:form:delete")
    public R<Void> delete(@RequestBody List<Long> ids) {
        formTemplateService.delete(ids);
        return R.ok();
    }

    /**
     * 查询模板详情(含字段配置)
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询模板详情")
    @SaCheckPermission("review:template:form:query")
    public R<FormTemplateResp> getDetail(@PathVariable Long id) {
        FormTemplateResp detail = formTemplateService.getDetail(id);
        return R.ok(detail);
    }

    /**
     * 分页查询模板列表
     */
    @GetMapping
    @Operation(summary = "分页查询模板列表")
    @SaCheckPermission("review:template:form:query")
    public R<PageResp<FormTemplateResp>> page(@Valid FormTemplateQuery query,
                                               @Valid PageQuery pageQuery) {
        PageResp<FormTemplateResp> page = formTemplateService.page(query, pageQuery);
        return R.ok(page);
    }

    /**
     * 上传模板文件
     */
    @PostMapping("/{templateId}/upload-template")
    @Operation(summary = "上传模板文件")
    @SaCheckPermission("review:template:form:update")
    public R<Long> uploadTemplateFile(@PathVariable Long templateId,
                                       @RequestParam Long fieldId,
                                       @RequestParam MultipartFile file) throws IOException {
        Long fileId = formTemplateService.uploadTemplateFile(templateId, fieldId, file);
        return R.ok(fileId);
    }

    /**
     * 删除模板文件
     */
    @DeleteMapping("/template-file/{fileId}")
    @Operation(summary = "删除模板文件")
    @SaCheckPermission("review:template:form:update")
    public R<Void> deleteTemplateFile(@PathVariable Long fileId) {
        formTemplateService.deleteTemplateFile(fileId);
        return R.ok();
    }

    /**
     * 启用/禁用模板
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "启用/禁用模板")
    @SaCheckPermission("review:template:form:status")
    public R<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        formTemplateService.updateStatus(id, status);
        return R.ok();
    }
}
