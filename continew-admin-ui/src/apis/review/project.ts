import http from '@/utils/http'
import type {
  ProjectQuery,
  ProjectListResp,
  ProjectDetailResp,
  ProjectCreateReq,
  ProjectSubmitReq,
  ProjectUpdateFormReq,
  StageFormSubmitReq,
  ProjectFormTemplateResp,
  ProjectTypeResp,
  NodeHistoryResp,
} from './type'

const BASE = '/review/project'

/** 上传评审相关文件（使用公共上传接口，无需 system:file:upload 权限） */
export function uploadReviewFile(data: FormData, parentPath?: string) {
  return http.post('/system/common/file', data, { params: { parentPath } })
}

/** 查询已启用的项目类型列表（申请人视角，无需 type:query 权限） */
export function listEnabledTypes() {
  return http.get<ProjectTypeResp[]>(`${BASE}/types`)
}

/** 获取申请表单模板（选完类型后调用） */
export function getApplicationForm(typeId: number | string) {
  return http.get<ProjectFormTemplateResp>(`${BASE}/type-form/${typeId}`)
}

/** 创建项目草稿，返回项目 ID（字符串，防止雪花 ID 精度丢失） */
export function createProject(data: ProjectCreateReq) {
  return http.post<string>(BASE, data)
}

/** 提交申请（草稿 → 正式提交） */
export function submitProject(id: number | string, data: ProjectSubmitReq) {
  return http.post<void>(`${BASE}/${id}/submit`, data)
}

/** 分页查询我的项目列表 */
export function getProjectPage(params: ProjectQuery) {
  return http.get<PageRes<ProjectListResp[]>>(BASE, params)
}

/** 获取项目详情 */
export function getProjectDetail(id: number | string) {
  return http.get<ProjectDetailResp>(`${BASE}/${id}`)
}

/** 有痕修改申请表单 */
export function updateProjectForm(id: number | string, data: ProjectUpdateFormReq) {
  return http.put<void>(`${BASE}/${id}/form`, data)
}

/** 撤销申请 */
export function revokeProject(id: number | string) {
  return http.post<void>(`${BASE}/${id}/revoke`)
}

/** 管理员强制终止 */
export function terminateProject(id: number | string) {
  return http.post<void>(`${BASE}/${id}/terminate`)
}

/** 申请人提交阶段成果 */
export function submitStageForm(projectId: number | string, stageId: number | string, data: StageFormSubmitReq) {
  return http.post<void>(`${BASE}/${projectId}/stage/${stageId}/form`, data)
}

/** 获取项目评审历史（申请人视角） */
export function getProjectReviewHistory(id: number | string) {
  return http.get<NodeHistoryResp[]>(`${BASE}/${id}/review-history`)
}
