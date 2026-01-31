import http from '@/utils/http'
import type {
  FormTemplateFileResp,
  FormTemplateQuery,
  FormTemplateReq,
  FormTemplateResp,
} from './type'

const BASE_URL = '/review/template/form'

/** @desc 分页查询表单模板列表 */
export function listFormTemplate(query: FormTemplateQuery) {
  return http.get<PageRes<FormTemplateResp[]>>(`${BASE_URL}`, query)
}

/** @desc 查询表单模板详情(含字段配置) */
export function getFormTemplate(id: string | number) {
  return http.get<FormTemplateResp>(`${BASE_URL}/${id}`)
}

/** @desc 创建表单模板 */
export function createFormTemplate(data: FormTemplateReq) {
  return http.post<number>(`${BASE_URL}`, data)
}

/** @desc 修改表单模板 */
export function updateFormTemplate(id: string | number, data: FormTemplateReq) {
  return http.put(`${BASE_URL}/${id}`, data)
}

/** @desc 删除表单模板 */
export function deleteFormTemplate(ids: (string | number)[]) {
  return http.del(`${BASE_URL}`, ids)
}

/** @desc 启用/禁用表单模板 */
export function updateFormTemplateStatus(id: string | number, status: number) {
  return http.put(`${BASE_URL}/${id}/status`, null, { params: { status } })
}

/** @desc 生成模板编码 */
export function generateFormTemplateCode() {
  return http.get<string>(`${BASE_URL}/generate-code`)
}

/** @desc 上传模板文件 */
export function uploadTemplateFile(templateId: string | number, fieldId: string | number, file: File) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('fieldId', String(fieldId))
  return http.post<number>(`${BASE_URL}/${templateId}/upload-template`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

/** @desc 删除模板文件 */
export function deleteTemplateFile(fileId: string | number) {
  return http.del(`${BASE_URL}/template-file/${fileId}`)
}

/** @desc 查询所有启用的模板列表(用于下拉选择) */
export function listEnabledFormTemplate(templateType?: number) {
  return http.get<FormTemplateResp[]>(`${BASE_URL}/list-enabled`, { params: { templateType } })
}
