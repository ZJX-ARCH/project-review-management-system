package top.continew.admin.review.project.service;

import top.continew.admin.review.form.model.resp.FormTemplateResp;
import top.continew.admin.review.project.model.query.ProjectQuery;
import top.continew.admin.review.project.model.req.ProjectCreateReq;
import top.continew.admin.review.project.model.req.ProjectSubmitReq;
import top.continew.admin.review.project.model.req.ProjectUpdateFormReq;
import top.continew.admin.review.project.model.req.StageFormSubmitReq;
import top.continew.admin.review.project.model.resp.ProjectDetailResp;
import top.continew.admin.review.project.model.resp.ProjectListResp;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

/**
 * 项目管理 Service
 *
 * @author zjx
 * @since 2026-03-07
 */
public interface ProjectService {

    /**
     * 获取申请表单模板（申请人选好类型后调用，引擎从快照读 APPLICATION 节点的表单模板）
     *
     * @param typeId 项目类型ID
     * @return 申请表单模板
     */
    FormTemplateResp getApplicationForm(Long typeId);

    /**
     * 创建项目草稿
     *
     * @param req 请求参数
     * @return 项目ID
     */
    Long create(ProjectCreateReq req);

    /**
     * 提交申请（草稿 → 提交，触发工作流引擎启动）
     *
     * @param projectId 项目ID
     * @param req       申请表单数据
     */
    void submit(Long projectId, ProjectSubmitReq req);

    /**
     * 分页查询项目列表
     *
     * @param query     查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResp<ProjectListResp> page(ProjectQuery query, PageQuery pageQuery);

    /**
     * 获取项目详情（申请人视角）
     *
     * @param projectId 项目ID
     * @return 项目详情
     */
    ProjectDetailResp getDetail(Long projectId);

    /**
     * 有痕修改申请表单（写修改日志，覆盖 application_form_data）
     *
     * @param projectId 项目ID
     * @param req       修改内容和原因
     */
    void updateForm(Long projectId, ProjectUpdateFormReq req);

    /**
     * 撤销申请（已提交 → 草稿，仅在未开始审核前可操作）
     *
     * @param projectId 项目ID
     */
    void revoke(Long projectId);

    /**
     * 管理员强制终止项目
     *
     * @param projectId 项目ID
     */
    void terminate(Long projectId);

    /**
     * 申请人提交阶段成果表单（触发 StageFormSubmittedEvent，引擎分配管理任务）
     *
     * @param projectId 项目ID
     * @param stageId   阶段ID
     * @param req       阶段成果表单数据
     */
    void submitStageForm(Long projectId, Long stageId, StageFormSubmitReq req);

    /**
     * 获取项目完整评审历史（申请人视角）
     * 返回：申请表单节点 + 所有已完成节点每人内容
     *
     * @param projectId 项目ID
     * @return 节点历史列表
     */
    java.util.List<top.continew.admin.review.project.model.resp.NodeHistoryResp> getReviewHistory(Long projectId);
}
