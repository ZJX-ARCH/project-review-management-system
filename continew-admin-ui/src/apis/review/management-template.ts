import http from '@/utils/http'
import type {
  ManagementTemplateQuery,
  ManagementTemplateReq,
  ManagementTemplateResp,
} from './type'

const BASE_URL = '/review/template/management'

/** @desc 查询管理流程模板列表 */
export function listManagementTemplate(query: ManagementTemplateQuery) {
  return http.get<PageRes<ManagementTemplateResp[]>>(`${BASE_URL}`, query)
}

/** @desc 查询管理流程模板详情 */
export function getManagementTemplate(id: string | number) {
  return http.get<ManagementTemplateResp>(`${BASE_URL}/${id}`)
}

/** @desc 创建管理流程模板 */
export function createManagementTemplate(data: ManagementTemplateReq) {
  return http.post<number>(`${BASE_URL}`, data)
}

/** @desc 修改管理流程模板 */
export function updateManagementTemplate(id: string | number, data: ManagementTemplateReq) {
  return http.put(`${BASE_URL}/${id}`, data)
}

/** @desc 删除管理流程模板 */
export function deleteManagementTemplate(ids: (string | number)[]) {
  return http.del(`${BASE_URL}`, ids)
}

/** @desc 启用/禁用管理流程模板 */
export function updateManagementTemplateStatus(id: string | number, status: number) {
  return http.put(`${BASE_URL}/${id}/status`, null, { params: { status } })
}

/** @desc 生成模板编码 */
export function generateManagementTemplateCode() {
  return http.get<string>(`${BASE_URL}/generate-code`)
}
