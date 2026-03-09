package top.continew.admin.review.project.service;

import top.continew.admin.review.project.model.entity.ReviewTaskDO;
import top.continew.admin.review.project.model.query.TaskQuery;
import top.continew.admin.review.project.model.req.TaskSaveReq;
import top.continew.admin.review.project.model.req.TaskSubmitReq;
import top.continew.admin.review.project.model.req.TaskTransferReq;
import top.continew.admin.review.project.model.resp.TaskDetailResp;
import top.continew.admin.review.project.model.resp.TaskListResp;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.util.List;

/**
 * 任务管理 Service
 *
 * @author zjx
 * @since 2026-03-07
 */
public interface TaskService {

    /**
     * 分页查询我的任务列表（当前登录用户的任务）
     *
     * @param query     查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResp<TaskListResp> myTasks(TaskQuery query, PageQuery pageQuery);

    /**
     * 获取任务详情（含项目信息、历史节点、阶段成果、表单模板）
     *
     * @param taskId 任务ID
     * @return 任务详情
     */
    TaskDetailResp getDetail(Long taskId);

    /**
     * 暂存任务（保存填写进度，不推进流程）
     *
     * @param taskId 任务ID
     * @param req    表单数据
     */
    void save(Long taskId, TaskSaveReq req);

    /**
     * 提交任务决策（触发 TaskCompletedEvent，引擎检查节点是否全部完成）
     *
     * @param taskId 任务ID
     * @param req    决策内容
     */
    void submit(Long taskId, TaskSubmitReq req);

    /**
     * 转办任务（原任务 TRANSFERRED，创建新任务给目标用户）
     *
     * @param taskId 任务ID
     * @param req    转办目标
     */
    void transfer(Long taskId, TaskTransferReq req);

    /**
     * 查询某节点的所有任务（结果引擎汇总用）
     *
     * @param projectId    项目ID
     * @param taskType     任务类型
     * @param nodeSequence 节点序号
     * @return 任务列表
     */
    List<ReviewTaskDO> listByNode(Long projectId, String taskType, Integer nodeSequence);
}
