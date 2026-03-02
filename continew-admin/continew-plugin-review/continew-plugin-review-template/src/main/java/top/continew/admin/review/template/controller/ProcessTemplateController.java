package top.continew.admin.review.template.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.review.template.model.query.ProcessTemplateQuery;
import top.continew.admin.review.template.model.req.ProcessTemplateReq;
import top.continew.admin.review.template.model.resp.ProcessTemplateResp;
import top.continew.admin.review.template.service.ProcessTemplateService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.web.model.R;

import java.util.List;

/**
 * 评审流程模板 API
 *
 * @author zjx
 * @since 2026-01-29
 */
@Tag(name = "评审流程模板 API")
@RestController
@RequestMapping("/review/template/process")
@RequiredArgsConstructor
public class ProcessTemplateController {

    private final ProcessTemplateService processTemplateService;

    /**
     * 创建流程模板
     */
    @PostMapping
    @Operation(summary = "创建流程模板")
    @SaCheckPermission("review:template:process:create")
    public R<Long> create(@Valid @RequestBody ProcessTemplateReq req) {
        Long id = processTemplateService.create(req);
        return R.ok(id);
    }

    /**
     * 修改流程模板
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改流程模板")
    @SaCheckPermission("review:template:process:update")
    public R<Void> update(@PathVariable Long id,
                          @Valid @RequestBody ProcessTemplateReq req) {
        processTemplateService.update(id, req);
        return R.ok();
    }

    /**
     * 删除流程模板
     */
    @DeleteMapping
    @Operation(summary = "删除流程模板")
    @SaCheckPermission("review:template:process:delete")
    public R<Void> delete(@RequestBody List<Long> ids) {
        processTemplateService.delete(ids);
        return R.ok();
    }

    /**
     * 查询模板详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询模板详情")
    @SaCheckPermission("review:template:process:query")
    public R<ProcessTemplateResp> getDetail(@PathVariable Long id) {
        ProcessTemplateResp detail = processTemplateService.getDetail(id);
        return R.ok(detail);
    }

    /**
     * 分页查询模板列表
     */
    @GetMapping
    @Operation(summary = "分页查询模板列表")
    @SaCheckPermission("review:template:process:query")
    public R<PageResp<ProcessTemplateResp>> page(@Valid ProcessTemplateQuery query,
                                                   @Valid PageQuery pageQuery) {
        PageResp<ProcessTemplateResp> page = processTemplateService.page(query, pageQuery);
        return R.ok(page);
    }

    /**
     * 启用/禁用模板
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "启用/禁用模板")
    @SaCheckPermission("review:template:process:status")
    public R<Void> updateStatus(@PathVariable Long id,
                                 @RequestParam Integer status) {
        processTemplateService.updateStatus(id, status);
        return R.ok();
    }

    /**
     * 生成模板编码
     */
    @GetMapping("/generate-code")
    @Operation(summary = "生成模板编码")
    @SaCheckPermission("review:template:process:create")
    public R<String> generateCode() {
        String code = processTemplateService.generateCode();
        return R.ok(code);
    }

    /**
     * 查询当前用户权限范围内已启用的模板列表（用于类型配置向导下拉选择，含轮次结构）
     */
    @GetMapping("/list-enabled")
    @Operation(summary = "查询已启用的评审流程模板列表")
    @SaCheckPermission("review:template:process:query")
    public R<List<ProcessTemplateResp>> listEnabled() {
        List<ProcessTemplateResp> list = processTemplateService.listEnabled();
        return R.ok(list);
    }
}
