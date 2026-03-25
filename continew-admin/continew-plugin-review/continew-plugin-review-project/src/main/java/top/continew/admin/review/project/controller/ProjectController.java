package top.continew.admin.review.project.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.review.form.model.resp.FormTemplateResp;
import top.continew.admin.review.project.model.query.ProjectQuery;
import top.continew.admin.review.project.model.req.ProjectCreateReq;
import top.continew.admin.review.project.model.req.ProjectSubmitReq;
import top.continew.admin.review.project.model.req.ProjectUpdateFormReq;
import top.continew.admin.review.project.model.req.StageFormSubmitReq;
import top.continew.admin.review.project.model.resp.ProjectDetailResp;
import top.continew.admin.review.project.model.resp.ProjectListResp;
import top.continew.admin.review.project.service.ProjectService;
import top.continew.admin.review.type.model.resp.ProjectTypeResp;
import top.continew.admin.review.type.service.ProjectTypeService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.web.model.R;

import java.util.List;

/**
 * 项目管理 API（申请人视角）
 *
 * @author zjx
 * @since 2026-03-07
 */
@Tag(name = "项目管理 API")
@RestController
@RequestMapping("/review/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectTypeService projectTypeService;

    /**
     * 查询已启用的项目类型列表（申请人创建项目时选类型用，无需 type:query 权限）
     */
    @GetMapping("/types")
    @Operation(summary = "查询已启用的项目类型列表")
    @SaCheckPermission("review:project:add")
    public R<List<ProjectTypeResp>> listEnabledTypes() {
        return R.ok(projectTypeService.listEnabledForApplicant());
    }

    /**
     * 获取申请表单模板（申请人选好类型后调用）
     */
    @GetMapping("/type-form/{typeId}")
    @Operation(summary = "获取申请表单模板")
    @SaCheckPermission("review:project:query")
    public R<FormTemplateResp> getApplicationForm(@PathVariable Long typeId) {
        return R.ok(projectService.getApplicationForm(typeId));
    }

    /**
     * 创建项目草稿
     */
    @PostMapping
    @Operation(summary = "创建项目草稿")
    @SaCheckPermission("review:project:add")
    public R<Long> create(@Valid @RequestBody ProjectCreateReq req) {
        return R.ok(projectService.create(req));
    }

    /**
     * 提交申请（草稿 → 正式提交，触发工作流）
     */
    @PostMapping("/{projectId}/submit")
    @Operation(summary = "提交申请")
    @SaCheckPermission("review:project:submit")
    public R<Void> submit(@PathVariable Long projectId,
                          @Valid @RequestBody ProjectSubmitReq req) {
        projectService.submit(projectId, req);
        return R.ok();
    }

    /**
     * 分页查询我的项目列表
     */
    @GetMapping
    @Operation(summary = "分页查询项目列表")
    @SaCheckPermission("review:project:query")
    public R<PageResp<ProjectListResp>> page(ProjectQuery query, @Valid PageQuery pageQuery) {
        return R.ok(projectService.page(query, pageQuery));
    }

    /**
     * 获取项目详情
     */
    @GetMapping("/{projectId}")
    @Operation(summary = "获取项目详情")
    @SaCheckPermission("review:project:query")
    public R<ProjectDetailResp> getDetail(@PathVariable Long projectId) {
        return R.ok(projectService.getDetail(projectId));
    }

    /**
     * 有痕修改申请表单
     */
    @PutMapping("/{projectId}/form")
    @Operation(summary = "修改申请表单（有痕）")
    @SaCheckPermission("review:project:update")
    public R<Void> updateForm(@PathVariable Long projectId,
                              @Valid @RequestBody ProjectUpdateFormReq req) {
        projectService.updateForm(projectId, req);
        return R.ok();
    }

    /**
     * 撤销申请（已提交 → 草稿）
     */
    @PostMapping("/{projectId}/revoke")
    @Operation(summary = "撤销申请")
    @SaCheckPermission("review:project:revoke")
    public R<Void> revoke(@PathVariable Long projectId) {
        projectService.revoke(projectId);
        return R.ok();
    }

    /**
     * 管理员强制终止项目
     */
    @PostMapping("/{projectId}/terminate")
    @Operation(summary = "强制终止项目")
    @SaCheckPermission("review:project:terminate")
    public R<Void> terminate(@PathVariable Long projectId) {
        projectService.terminate(projectId);
        return R.ok();
    }

    /**
     * 申请人提交阶段成果表单
     */
    @PostMapping("/{projectId}/stage/{stageId}/form")
    @Operation(summary = "提交阶段成果表单")
    @SaCheckPermission("review:project:submit")
    public R<Void> submitStageForm(@PathVariable Long projectId,
                                   @PathVariable Long stageId,
                                   @Valid @RequestBody StageFormSubmitReq req) {
        projectService.submitStageForm(projectId, stageId, req);
        return R.ok();
    }

    /**
     * 获取评审历史（申请人视角）
     */
    @GetMapping("/{projectId}/review-history")
    @Operation(summary = "获取评审历史（申请人视角）")
    @SaCheckPermission("review:project:query")
    public R<java.util.List<top.continew.admin.review.project.model.resp.NodeHistoryResp>> getReviewHistory(@PathVariable Long projectId) {
        return R.ok(projectService.getReviewHistory(projectId));
    }
}
