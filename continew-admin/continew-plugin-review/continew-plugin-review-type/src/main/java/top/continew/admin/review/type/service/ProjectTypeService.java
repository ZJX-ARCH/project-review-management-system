package top.continew.admin.review.type.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.continew.admin.review.type.model.entity.ProjectTypeDO;
import top.continew.admin.review.type.model.query.ProjectTypeQuery;
import top.continew.admin.review.type.model.req.*;
import top.continew.admin.review.type.model.resp.ProjectTypeDetailResp;
import top.continew.admin.review.type.model.resp.ProjectTypeResp;
import top.continew.admin.review.type.model.resp.ReviewPersonResp;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.util.List;
import java.util.Map;

/**
 * 项目类型 Service
 *
 * @author zjx
 * @since 2026-03-02
 */
public interface ProjectTypeService extends IService<ProjectTypeDO> {

    // ===== 基本信息 CRUD =====

    /**
     * 创建类型基本信息
     *
     * @param req 请求参数
     * @return 新建类型 ID
     */
    Long create(ProjectTypeReq req);

    /**
     * 修改类型基本信息（type_code 不可修改）
     *
     * @param id  类型 ID
     * @param req 请求参数
     */
    void update(Long id, ProjectTypeReq req);

    /**
     * 批量删除类型（有进行中项目则抛 BusinessException）
     *
     * @param ids ID 列表
     */
    void delete(List<Long> ids);

    /**
     * 分页查询类型列表（TYPE_ADMIN 视角，按 dept_id 数据权限过滤）
     *
     * @param query     查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResp<ProjectTypeResp> page(ProjectTypeQuery query, PageQuery pageQuery);

    /**
     * 查询类型完整配置详情（含所有子配置）
     *
     * @param id 类型 ID
     * @return 完整配置详情
     */
    ProjectTypeDetailResp getDetail(Long id);

    /**
     * 生成类型编码（格式：TYPE_ + 时间戳）
     *
     * @return 生成的类型编码
     */
    String generateCode();

    // ===== 分步配置（均为全量替换） =====

    /**
     * 步骤2：保存流程模板选择（全量替换，每类型2条：REVIEW + MANAGE）
     *
     * @param id   类型 ID
     * @param reqs 流程配置列表
     */
    void saveProcess(Long id, List<TypeProcessConfigReq> reqs);

    /**
     * 步骤3：保存表单映射（全量替换，每节点1条）
     *
     * @param id   类型 ID
     * @param reqs 表单映射列表
     */
    void saveFormMapping(Long id, List<TypeFormMappingReq> reqs);

    /**
     * 步骤4：保存人员范围配置（全量替换，5类角色均需填写）
     *
     * @param id   类型 ID
     * @param reqs 人员配置列表
     */
    void savePersonnel(Long id, List<TypePersonnelConfigReq> reqs);

    /**
     * 步骤5：保存审批规则配置（全量替换，每需判定节点1条）
     *
     * @param id   类型 ID
     * @param reqs 审批规则列表
     */
    void saveApproval(Long id, List<TypeApprovalConfigReq> reqs);

    // ===== 状态管理 =====

    /**
     * 启用类型（触发第3层全量验证，验证不通过抛 BusinessException）
     *
     * @param id 类型 ID
     */
    void enable(Long id);

    /**
     * 禁用类型
     *
     * @param id 类型 ID
     */
    void disable(Long id);

    // ===== 辅助查询 =====

    /**
     * 查询业务角色编码 → ID 映射（供前端节点配置人员范围时按角色过滤用户）
     *
     * @param codes 角色编码列表
     * @return Map&lt;code, id&gt;
     */
    Map<String, Long> getRoleMap(List<String> codes);

    /**
     * 搜索拥有指定角色的人员（USER 范围选择器）
     * 与系统 listUser(roleId) 语义相反：此处返回"已拥有该角色"的用户
     *
     * @param roleId  角色 ID（null 时不按角色过滤）
     * @param keyword 昵称/用户名关键词（null 时不过滤）
     * @param limit   最大返回条数
     * @return 人员列表
     */
    List<ReviewPersonResp> searchPersons(Long roleId, String keyword, int limit);

    /**
     * 根据 ID 列表批量查询人员（用于回显已选用户）
     *
     * @param ids 用户 ID 列表
     * @return 人员列表
     */
    List<ReviewPersonResp> getPersonsByIds(List<Long> ids);

    /**
     * 查询当前用户所在部门及其全部子孙部门树（DEPT 范围选择器）
     *
     * @return 部门树节点列表（key=id, title=name）
     */
    List<Map<String, Object>> getDeptTree();

    /**
     * 查询当前用户有资格申请的项目类型列表（已启用 + APPLICATION 节点人员范围包含当前用户）
     *
     * @return 有权申请的类型列表
     */
    List<ProjectTypeResp> listEnabledForApplicant();

    /**
     * 统计指定范围内拥有指定角色的人员数量（多条规则取并集）
     *
     * @param req 范围请求（roleId + 多条规则）
     * @return 符合条件的人员数量
     */
    int countScope(CountScopeReq req);

}
