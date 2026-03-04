package top.continew.admin.review.type.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.review.type.model.query.ProjectTypeQuery;
import top.continew.admin.review.type.model.req.*;
import top.continew.admin.review.type.model.resp.ProjectTypeDetailResp;
import top.continew.admin.review.type.model.resp.ProjectTypeResp;
import top.continew.admin.review.type.service.ProjectTypeService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.web.model.R;

import java.util.List;

/**
 * 项目类型管理 API
 *
 * @author zjx
 * @since 2026-03-02
 */
@Tag(name = "项目类型管理 API")
@RestController
@RequestMapping("/review/type")
@RequiredArgsConstructor
public class ProjectTypeController {

    private final ProjectTypeService projectTypeService;

    // ===== 基本信息 CRUD =====

    /**
     * 创建类型基本信息
     */
    @PostMapping
    @Operation(summary = "创建类型基本信息")
    @SaCheckPermission("review:type:create")
    public R<Long> create(@Valid @RequestBody ProjectTypeReq req) {
        Long id = projectTypeService.create(req);
        return R.ok(id);
    }

    /**
     * 修改类型基本信息（type_code 不可修改）
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改类型基本信息")
    @SaCheckPermission("review:type:update")
    public R<Void> update(@PathVariable Long id,
                          @Valid @RequestBody ProjectTypeReq req) {
        projectTypeService.update(id, req);
        return R.ok();
    }

    /**
     * 批量删除类型（有进行中项目则返回错误）
     */
    @DeleteMapping
    @Operation(summary = "批量删除类型")
    @SaCheckPermission("review:type:delete")
    public R<Void> delete(@RequestBody List<Long> ids) {
        projectTypeService.delete(ids);
        return R.ok();
    }

    /**
     * 分页查询类型列表（TYPE_ADMIN 视角）
     */
    @GetMapping
    @Operation(summary = "分页查询类型列表")
    @SaCheckPermission("review:type:query")
    public R<PageResp<ProjectTypeResp>> page(@Valid ProjectTypeQuery query,
                                              @Valid PageQuery pageQuery) {
        PageResp<ProjectTypeResp> page = projectTypeService.page(query, pageQuery);
        return R.ok(page);
    }

    /**
     * 查询类型完整配置详情（含所有子配置）
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询类型完整配置详情")
    @SaCheckPermission("review:type:query")
    public R<ProjectTypeDetailResp> getDetail(@PathVariable Long id) {
        ProjectTypeDetailResp detail = projectTypeService.getDetail(id);
        return R.ok(detail);
    }

    /**
     * 生成类型编码（格式：TYPE_ + 时间戳，用于前端自动填充）
     */
    @GetMapping("/generate-code")
    @Operation(summary = "生成类型编码")
    @SaCheckPermission("review:type:create")
    public R<String> generateCode() {
        String code = projectTypeService.generateCode();
        return R.ok(code);
    }

    // ===== 分步配置（均为全量替换） =====

    /**
     * 步骤2：保存流程模板选择（每类型 2 条：REVIEW + MANAGE）
     */
    @PutMapping("/{id}/process")
    @Operation(summary = "保存流程模板选择")
    @SaCheckPermission("review:type:update")
    public R<Void> saveProcess(@PathVariable Long id,
                                @Valid @RequestBody List<TypeProcessConfigReq> reqs) {
        projectTypeService.saveProcess(id, reqs);
        return R.ok();
    }

    /**
     * 步骤3：保存表单映射（每节点 1 条，全量替换）
     */
    @PutMapping("/{id}/form")
    @Operation(summary = "保存表单映射")
    @SaCheckPermission("review:type:update")
    public R<Void> saveFormMapping(@PathVariable Long id,
                                    @Valid @RequestBody List<TypeFormMappingReq> reqs) {
        projectTypeService.saveFormMapping(id, reqs);
        return R.ok();
    }

    /**
     * 步骤4：保存人员范围配置（5 类角色全量替换）
     */
    @PutMapping("/{id}/personnel")
    @Operation(summary = "保存人员范围配置")
    @SaCheckPermission("review:type:update")
    public R<Void> savePersonnel(@PathVariable Long id,
                                  @Valid @RequestBody List<TypePersonnelConfigReq> reqs) {
        projectTypeService.savePersonnel(id, reqs);
        return R.ok();
    }

    /**
     * 步骤5：保存审批规则配置（每需判定节点 1 条，全量替换）
     */
    @PutMapping("/{id}/approval")
    @Operation(summary = "保存审批规则配置")
    @SaCheckPermission("review:type:update")
    public R<Void> saveApproval(@PathVariable Long id,
                                 @Valid @RequestBody List<TypeApprovalConfigReq> reqs) {
        projectTypeService.saveApproval(id, reqs);
        return R.ok();
    }

    // ===== 状态管理 =====

    /**
     * 启用类型（触发第3层全量验证：流程/表单/人员/审批规则均须完整）
     */
    @PutMapping("/{id}/enable")
    @Operation(summary = "启用类型")
    @SaCheckPermission("review:type:status")
    public R<Void> enable(@PathVariable Long id) {
        projectTypeService.enable(id);
        return R.ok();
    }

    /**
     * 禁用类型
     */
    @PutMapping("/{id}/disable")
    @Operation(summary = "禁用类型")
    @SaCheckPermission("review:type:status")
    public R<Void> disable(@PathVariable Long id) {
        projectTypeService.disable(id);
        return R.ok();
    }

}
