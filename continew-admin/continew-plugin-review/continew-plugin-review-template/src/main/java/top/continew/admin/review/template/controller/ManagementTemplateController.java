package top.continew.admin.review.template.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.review.template.model.query.ManagementTemplateQuery;
import top.continew.admin.review.template.model.req.ManagementTemplateReq;
import top.continew.admin.review.template.model.resp.ManagementTemplateResp;
import top.continew.admin.review.template.service.ManagementTemplateService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.web.model.R;

import java.util.List;

/**
 * 管理流程模板 API
 *
 * @author zjx
 * @since 2026-01-29
 */
@Tag(name = "管理流程模板 API")
@RestController
@RequestMapping("/review/template/management")
@RequiredArgsConstructor
public class ManagementTemplateController {

    private final ManagementTemplateService managementTemplateService;

    /**
     * 创建管理模板
     */
    @PostMapping
    @Operation(summary = "创建管理模板")
    @SaCheckRole("FLOW_ADMIN")
    public R<Long> create(@Valid @RequestBody ManagementTemplateReq req) {
        Long id = managementTemplateService.create(req);
        return R.ok(id);
    }

    /**
     * 修改管理模板
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改管理模板")
    @SaCheckRole("FLOW_ADMIN")
    public R<Void> update(@PathVariable Long id,
                          @Valid @RequestBody ManagementTemplateReq req) {
        managementTemplateService.update(id, req);
        return R.ok();
    }

    /**
     * 删除管理模板
     */
    @DeleteMapping
    @Operation(summary = "删除管理模板")
    @SaCheckRole("FLOW_ADMIN")
    public R<Void> delete(@RequestBody List<Long> ids) {
        managementTemplateService.delete(ids);
        return R.ok();
    }

    /**
     * 查询模板详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询模板详情")
    @SaCheckRole({"FLOW_ADMIN", "TYPE_ADMIN"})
    public R<ManagementTemplateResp> getDetail(@PathVariable Long id) {
        ManagementTemplateResp detail = managementTemplateService.getDetail(id);
        return R.ok(detail);
    }

    /**
     * 分页查询模板列表
     */
    @GetMapping
    @Operation(summary = "分页查询模板列表")
    @SaCheckRole({"FLOW_ADMIN", "TYPE_ADMIN"})
    public R<PageResp<ManagementTemplateResp>> page(@Valid ManagementTemplateQuery query,
                                                      @Valid PageQuery pageQuery) {
        PageResp<ManagementTemplateResp> page = managementTemplateService.page(query, pageQuery);
        return R.ok(page);
    }

    /**
     * 启用/禁用模板
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "启用/禁用模板")
    @SaCheckRole("FLOW_ADMIN")
    public R<Void> updateStatus(@PathVariable Long id,
                                 @RequestParam Integer status) {
        managementTemplateService.updateStatus(id, status);
        return R.ok();
    }

    /**
     * 生成模板编码
     */
    @GetMapping("/generate-code")
    @Operation(summary = "生成模板编码")
    @SaCheckRole("FLOW_ADMIN")
    public R<String> generateCode() {
        String code = managementTemplateService.generateCode();
        return R.ok(code);
    }
}
