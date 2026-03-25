package top.continew.admin.review.project.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.review.project.model.query.TaskQuery;
import top.continew.admin.review.project.model.req.TaskSaveReq;
import top.continew.admin.review.project.model.req.TaskSubmitReq;
import top.continew.admin.review.project.model.req.TaskTransferReq;
import top.continew.admin.review.project.model.resp.TaskDetailResp;
import top.continew.admin.review.project.model.resp.TaskListResp;
import top.continew.admin.review.project.model.resp.TaskCandidateResp;
import top.continew.admin.review.project.service.TaskService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.starter.web.model.R;

import java.util.List;

/**
 * 任务管理 API（处理人视角）
 *
 * @author zjx
 * @since 2026-03-07
 */
@Tag(name = "任务管理 API")
@RestController
@RequestMapping("/review/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * 分页查询我的任务列表
     */
    @GetMapping("/my")
    @Operation(summary = "分页查询我的任务列表")
    @SaCheckPermission("review:task:query")
    public R<PageResp<TaskListResp>> myTasks(TaskQuery query, @Valid PageQuery pageQuery) {
        return R.ok(taskService.myTasks(query, pageQuery));
    }

    /**
     * 获取任务详情（含项目信息、历史节点、阶段成果、表单模板）
     */
    @GetMapping("/{taskId}")
    @Operation(summary = "获取任务详情")
    @SaCheckPermission("review:task:query")
    public R<TaskDetailResp> getDetail(@PathVariable Long taskId) {
        return R.ok(taskService.getDetail(taskId));
    }

    /**
     * 暂存任务表单（保存填写进度，不推进流程）
     */
    @PutMapping("/{taskId}/save")
    @Operation(summary = "暂存任务")
    @SaCheckPermission("review:task:save")
    public R<Void> save(@PathVariable Long taskId,
                        @RequestBody TaskSaveReq req) {
        taskService.save(taskId, req);
        return R.ok();
    }

    /**
     * 提交任务决策（触发结果汇总与流程推进）
     */
    @PostMapping("/{taskId}/submit")
    @Operation(summary = "提交任务决策")
    @SaCheckPermission("review:task:submit")
    public R<Void> submit(@PathVariable Long taskId,
                          @Valid @RequestBody TaskSubmitReq req) {
        taskService.submit(taskId, req);
        return R.ok();
    }

    /**
     * 转办任务
     */
    @PostMapping("/{taskId}/transfer")
    @Operation(summary = "转办任务")
    @SaCheckPermission("review:task:transfer")
    public R<Void> transfer(@PathVariable Long taskId,
                            @Valid @RequestBody TaskTransferReq req) {
        taskService.transfer(taskId, req);
        return R.ok();
    }

    /**
     * 查询转办候选人列表
     */
    @GetMapping("/{taskId}/candidates")
    @Operation(summary = "查询转办候选人列表")
    @SaCheckPermission("review:task:transfer")
    public R<List<TaskCandidateResp>> listCandidates(@PathVariable Long taskId) {
        return R.ok(taskService.listCandidates(taskId));
    }
}
