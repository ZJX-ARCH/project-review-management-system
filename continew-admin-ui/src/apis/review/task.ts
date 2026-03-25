import http from '@/utils/http'
import type {
  TaskQuery,
  TaskListResp,
  TaskDetailResp,
  TaskSubmitReq,
  TaskSaveReq,
  TaskTransferReq,
  TaskCandidateResp,
  NodeHistoryResp,
} from './type'

const BASE = '/review/task'

/** 分页查询我的任务列表 */
export function getMyTasks(params: TaskQuery) {
  return http.get<PageRes<TaskListResp[]>>(`${BASE}/my`, params)
}

/** 获取任务详情 */
export function getTaskDetail(id: number | string) {
  return http.get<TaskDetailResp>(`${BASE}/${id}`)
}

/** 暂存任务表单 */
export function saveTask(id: number | string, data: TaskSaveReq) {
  return http.put<void>(`${BASE}/${id}/save`, data)
}

/** 提交任务决策 */
export function submitTask(id: number | string, data: TaskSubmitReq) {
  return http.post<void>(`${BASE}/${id}/submit`, data)
}

/** 转办任务 */
export function transferTask(id: number | string, data: TaskTransferReq) {
  return http.post<void>(`${BASE}/${id}/transfer`, data)
}

/** 查询转办候选人列表 */
export function getTaskCandidates(id: number | string) {
  return http.get<TaskCandidateResp[]>(`${BASE}/${id}/candidates`)
}

/** 获取任务节点历史（处理人视角） */
export function getTaskNodeHistory(id: number | string) {
  return http.get<NodeHistoryResp[]>(`${BASE}/${id}/history`)
}
