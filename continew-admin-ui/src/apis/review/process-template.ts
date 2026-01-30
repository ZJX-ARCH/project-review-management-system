import http from '@/utils/http'
import type {
  ProcessTemplateQuery,
  ProcessTemplateReq,
  ProcessTemplateResp,
} from './type'

const BASE_URL = '/review/template/process'

/** @desc 查询评审流程模板列表 */
export function listProcessTemplate(query: ProcessTemplateQuery) {
  return http.get<PageRes<ProcessTemplateResp[]>>(`${BASE_URL}`, query)
}

/** @desc 查询评审流程模板详情 */
export function getProcessTemplate(id: string | number) {
  return http.get<ProcessTemplateResp>(`${BASE_URL}/${id}`)
}

/** @desc 创建评审流程模板 */
export function createProcessTemplate(data: ProcessTemplateReq) {
  return http.post<number>(`${BASE_URL}`, data)
}

/** @desc 修改评审流程模板 */
export function updateProcessTemplate(id: string | number, data: ProcessTemplateReq) {
  return http.put(`${BASE_URL}/${id}`, data)
}

/** @desc 删除评审流程模板 */
export function deleteProcessTemplate(ids: (string | number)[]) {
  return http.del(`${BASE_URL}`, { ids })
}

/** @desc 启用/禁用评审流程模板 */
export function updateProcessTemplateStatus(id: string | number, status: number) {
  return http.put(`${BASE_URL}/${id}/status`, null, { params: { status } })
}

/** @desc 生成模板编码 */
export function generateProcessTemplateCode() {
  return http.get<string>(`${BASE_URL}/generate-code`)
}
