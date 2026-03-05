import http from '@/utils/http'
import type {
  ProjectTypeDetailResp,
  ProjectTypeQuery,
  ProjectTypeReq,
  ProjectTypeResp,
  TypeApprovalConfigReq,
  TypeFormMappingReq,
  TypePersonnelConfigReq,
  TypeProcessConfigReq,
} from './type'

const BASE_URL = '/review/type'

/** @desc 分页查询项目类型列表 */
export function listProjectType(query: ProjectTypeQuery) {
  return http.get<PageRes<ProjectTypeResp[]>>(`${BASE_URL}`, query)
}

/** @desc 查询项目类型完整配置详情 */
export function getProjectType(id: string | number) {
  return http.get<ProjectTypeDetailResp>(`${BASE_URL}/${id}`)
}

/** @desc 创建项目类型基本信息 */
export function createProjectType(data: ProjectTypeReq) {
  return http.post<number>(`${BASE_URL}`, data)
}

/** @desc 修改项目类型基本信息（typeCode 不可修改） */
export function updateProjectType(id: string | number, data: ProjectTypeReq) {
  return http.put(`${BASE_URL}/${id}`, data)
}

/** @desc 批量删除项目类型 */
export function deleteProjectType(ids: (string | number)[]) {
  return http.del(`${BASE_URL}`, ids)
}

/** @desc 生成类型编码（格式：TYPE_ + 时间戳） */
export function generateTypeCode() {
  return http.get<string>(`${BASE_URL}/generate-code`)
}

/** @desc 步骤2：保存流程模板选择（REVIEW + MANAGE 各一条） */
export function saveProcess(id: string | number, reqs: TypeProcessConfigReq[]) {
  return http.put(`${BASE_URL}/${id}/process`, reqs)
}

/** @desc 步骤3：保存表单映射（全量替换） */
export function saveFormMapping(id: string | number, reqs: TypeFormMappingReq[]) {
  return http.put(`${BASE_URL}/${id}/form`, reqs)
}

/** @desc 步骤4：保存人员范围配置（5类角色，全量替换） */
export function savePersonnel(id: string | number, reqs: TypePersonnelConfigReq[]) {
  return http.put(`${BASE_URL}/${id}/personnel`, reqs)
}

/** @desc 步骤5：保存审批规则配置（全量替换） */
export function saveApproval(id: string | number, reqs: TypeApprovalConfigReq[]) {
  return http.put(`${BASE_URL}/${id}/approval`, reqs)
}

/** @desc 启用类型（触发第3层全量验证） */
export function enableProjectType(id: string | number) {
  return http.put(`${BASE_URL}/${id}/enable`)
}

/** @desc 禁用类型 */
export function disableProjectType(id: string | number) {
  return http.put(`${BASE_URL}/${id}/disable`)
}

/** @desc 查询业务角色编码→ID映射（供节点人员范围配置按角色过滤用户，权限：review:type:query） */
export function getTypeRoleMap() {
  return http.get<Record<string, string>>(`${BASE_URL}/role-map`)
}

/** @desc 按角色搜索人员（返回已拥有该角色的用户，与系统 listUser 语义相反） */
export function searchReviewPersons(params: { roleId?: string; keyword?: string; limit?: number }) {
  return http.get<{ id: string; nickname: string; username: string }[]>(`${BASE_URL}/person-search`, params)
}

/** @desc 根据 ID 列表回显已选人员 */
export function getReviewPersonsByIds(ids: (string | number)[]) {
  return http.get<{ id: string; nickname: string; username: string }[]>(`${BASE_URL}/person-by-ids`, { ids })
}

/** @desc 查询当前用户部门及子部门树（用于 DEPT 范围配置） */
export function getReviewDeptTree() {
  return http.get<{ key: string; title: string; children?: unknown[] }[]>(`${BASE_URL}/dept-tree`)
}

export interface CountScopeRule {
  scopeType: string
  scopeConfig: Record<string, unknown>
}

/** @desc 统计指定范围内拥有指定角色的人员数量（多条规则取并集） */
export function countScope(data: { roleId?: string; rules: CountScopeRule[] }) {
  return http.post<number>(`${BASE_URL}/count-scope`, data)
}
