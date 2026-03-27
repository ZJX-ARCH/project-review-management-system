/** 轮次类型枚举 */
export enum RoundType {
  /** 审核 */
  AUDIT = 'AUDIT',
  /** 评审 */
  REVIEW = 'REVIEW',
  /** 决策 */
  DECISION = 'DECISION',
}

/** 阶段类型枚举 */
export enum StageType {
  /** 立项 */
  KICKOFF = 'KICKOFF',
  /** 执行 */
  EXECUTION = 'EXECUTION',
  /** 验收 */
  ACCEPTANCE = 'ACCEPTANCE',
}

/** 轮次名称请求参数 */
export interface RoundNameReq {
  /** 轮次类型 */
  roundType: RoundType
  /** 轮次序号 */
  roundSequence: number
  /** 轮次名称 */
  roundName: string
}

/** 轮次名称响应数据 */
export interface RoundNameResp extends RoundNameReq {
  /** ID */
  id?: number
  /** 模板ID */
  templateId?: number
}

/** 阶段请求参数 */
export interface StageReq {
  /** 阶段名称 */
  stageName: string
  /** 阶段类型 */
  stageType: StageType
  /** 阶段顺序 */
  stageOrder: number
  /** 是否必须 */
  isRequired: boolean
}

/** 阶段响应数据 */
export interface StageResp extends StageReq {
  /** ID */
  id?: number
  /** 模板ID */
  templateId?: number
}

/** 评审流程模板查询参数 */
export interface ProcessTemplateQuery extends PageQuery {
  /** 模板名称 */
  templateName?: string
  /** 模板编码 */
  templateCode?: string
  /** 启用状态 */
  status?: number
}

/** 评审流程模板请求参数 */
export interface ProcessTemplateReq {
  /** 模板名称 */
  templateName: string
  /** 模板编码 */
  templateCode?: string
  /** 模板描述 */
  description?: string
  /** 审核轮次数量 */
  auditRounds: number
  /** 评审轮次数量 */
  reviewRounds: number
  /** 决策轮次数量 */
  decisionRounds: number
  /** 轮次名称列表 */
  roundNames?: RoundNameReq[]
}

/** 评审流程模板响应数据 */
export interface ProcessTemplateResp extends ProcessTemplateReq {
  /** ID */
  id: number
  /** 模板编码 */
  templateCode: string
  /** 启用状态 */
  status: number
  /** 轮次名称列表 */
  roundNames: RoundNameResp[]
  /** 创建时间 */
  createTime?: string
  /** 修改时间 */
  updateTime?: string
  /** 创建人 */
  createUserString?: string
  /** 修改人 */
  updateUserString?: string
}

/** 管理流程模板查询参数 */
export interface ManagementTemplateQuery extends PageQuery {
  /** 模板名称 */
  templateName?: string
  /** 模板编码 */
  templateCode?: string
  /** 启用状态 */
  status?: number
}

/** 管理流程模板请求参数 */
export interface ManagementTemplateReq {
  /** 模板名称 */
  templateName: string
  /** 模板编码 */
  templateCode?: string
  /** 模板描述 */
  description?: string
  /** 阶段列表 */
  stages: StageReq[]
}

/** 管理流程模板响应数据 */
export interface ManagementTemplateResp extends ManagementTemplateReq {
  /** ID */
  id: number
  /** 模板编码 */
  templateCode: string
  /** 启用状态 */
  status: number
  /** 阶段列表 */
  stages: StageResp[]
  /** 创建时间 */
  createTime?: string
  /** 修改时间 */
  updateTime?: string
  /** 创建人 */
  createUserString?: string
  /** 修改人 */
  updateUserString?: string
}

/** 字段类型枚举 */
export enum FieldType {
  /** 单行文本 */
  TEXT = 'TEXT',
  /** 多行文本 */
  TEXTAREA = 'TEXTAREA',
  /** 数字 */
  NUMBER = 'NUMBER',
  /** 日期 */
  DATE = 'DATE',
  /** 下拉选择 */
  SELECT = 'SELECT',
  /** 单选 */
  RADIO = 'RADIO',
  /** 多选 */
  CHECKBOX = 'CHECKBOX',
  /** 评分 */
  SCORE = 'SCORE',
  /** 文件 */
  FILE = 'FILE',
  /** 文件模板 */
  FILE_TEMPLATE = 'FILE_TEMPLATE',
  /** 表格 */
  TABLE = 'TABLE',
  /** 评分表 */
  SCORE_TABLE = 'SCORE_TABLE',
}

/** 模板类型枚举 */
export enum TemplateType {
  /** 申请表单 */
  APPLICATION = 1,
  /** 审核表单 */
  AUDIT = 2,
  /** 评审表单 */
  REVIEW = 3,
  /** 决策表单 */
  DECISION = 4,
  /** 立项阶段管理表单 */
  KICKOFF = 5,
  /** 执行阶段管理表单 */
  EXECUTION = 6,
  /** 验收阶段管理表单 */
  ACCEPTANCE = 7,
}

/** 表单字段请求参数 */
export interface FormFieldReq {
  /** 字段名称 */
  fieldName: string
  /** 字段编码 */
  fieldCode: string
  /** 字段类型 */
  fieldType: FieldType
  /** 栅格占位 */
  span: number
  /** 是否必填 */
  isRequired: boolean
  /** 排序 */
  sort: number
  /** 字段配置(JSON) */
  fieldConfig?: any
}

/** 表单字段响应数据 */
export interface FormFieldResp extends FormFieldReq {
  /** ID */
  id?: number
  /** 模板ID */
  templateId?: number
}

/** 表单模板文件响应数据 */
export interface FormTemplateFileResp {
  /** ID */
  id: number
  /** 模板ID */
  templateId: number
  /** 字段ID */
  fieldId?: number
  /** 文件ID */
  fileId: number
  /** 文件类型 */
  fileType: string
  /** 文件说明 */
  description?: string
  /** 排序 */
  sort: number
  /** 文件名称 */
  fileName?: string
  /** 文件URL */
  fileUrl?: string
}

/** 表单模板查询参数 */
export interface FormTemplateQuery extends PageQuery {
  /** 模板名称 */
  templateName?: string
  /** 模板编码 */
  templateCode?: string
  /** 模板类型 */
  templateType?: number
  /** 启用状态 */
  status?: number
}

/** 表单模板请求参数 */
export interface FormTemplateReq {
  /** 模板名称 */
  templateName: string
  /** 模板编码 */
  templateCode?: string
  /** 模板类型 */
  templateType: number
  /** 模板描述 */
  description?: string
  /** 布局配置(JSON) */
  layoutConfig?: any
  /** 排序 */
  sort?: number
  /** 字段列表 */
  fields?: FormFieldReq[]
}

/** 表单模板响应数据 */
export interface FormTemplateResp extends FormTemplateReq {
  /** ID */
  id: number
  /** 模板编码 */
  templateCode: string
  /** 启用状态 */
  status: number
  /** 字段列表 */
  fields: FormFieldResp[]
  /** 附件文件列表 */
  files?: FormTemplateFileResp[]
  /** 字段数量 */
  fieldCount?: number
  /** 创建时间 */
  createTime?: string
  /** 修改时间 */
  updateTime?: string
  /** 创建人 */
  createUserString?: string
  /** 修改人 */
  updateUserString?: string
}

// ============ 项目类型模块 ============

/** 项目类型状态枚举 */
export enum ProjectTypeStatus {
  /** 草稿 */
  DRAFT = 0,
  /** 已启用 */
  ENABLED = 1,
  /** 已禁用 */
  DISABLED = 2,
}

/** 流程类型枚举 */
export enum ProcessType {
  /** 评审流程 */
  REVIEW = 'REVIEW',
  /** 管理流程 */
  MANAGE = 'MANAGE',
}

/** 人员范围类型枚举 */
export enum ScopeType {
  /** 指定用户 */
  USER = 'USER',
  /** 按部门 */
  DEPT = 'DEPT',
}

/** 审批模式枚举 */
export enum ApprovalMode {
  /** 全部通过 */
  VOTE_ALL_PASS = 'VOTE_ALL_PASS',
  /** 多数通过 */
  VOTE_MAJORITY_PASS = 'VOTE_MAJORITY_PASS',
  /** 一票通过 */
  VOTE_ONE_PASS = 'VOTE_ONE_PASS',
  /** 评分通过 */
  SCORE_PASS = 'SCORE_PASS',
}

/** 权重模式枚举 */
export enum WeightMode {
  /** 等权重 */
  EQUAL = 'EQUAL',
  /** 预设权重 */
  PRESET = 'PRESET',
}

/** 评分计算方式枚举 */
export enum ScoreCalcMethod {
  /** 加权平均 */
  WEIGHTED_AVG = 'WEIGHTED_AVG',
  /** 算术平均 */
  AVG = 'AVG',
  /** 取最低分 */
  MIN = 'MIN',
}

/** 项目类型查询参数 */
export interface ProjectTypeQuery extends PageQuery {
  /** 类型名称 */
  typeName?: string
  /** 类型编码 */
  typeCode?: string
  /** 状态 0=草稿 1=已启用 2=已禁用 */
  status?: number
}

/** 项目类型请求参数 */
export interface ProjectTypeReq {
  /** 类型名称 */
  typeName: string
  /** 类型编码（可选，不填自动生成） */
  typeCode?: string
  /** 描述 */
  description?: string
  /** 排序 */
  sortOrder?: number
}

/** 项目类型列表响应 */
export interface ProjectTypeResp {
  /** ID */
  id: number
  /** 类型名称 */
  typeName: string
  /** 类型编码 */
  typeCode: string
  /** 描述 */
  description?: string
  /** 排序 */
  sortOrder?: number
  /** 状态 0=草稿 1=已启用 2=已禁用 */
  status: number
  /** 所属部门ID */
  deptId?: number
  /** 创建时间 */
  createTime?: string
  /** 修改时间 */
  updateTime?: string
  /** 创建人 */
  createUserString?: string
  /** 修改人 */
  updateUserString?: string
}

/** 评审人预设权重请求 */
export interface TypeReviewerWeightReq {
  /** 用户ID */
  userId: number
  /** 权重(0~1，同节点所有人之和=1.0) */
  weight: number
}

/** 评审人预设权重响应 */
export interface TypeReviewerWeightResp {
  id?: number
  typeId?: number
  nodeScope?: string
  userId: number
  userName?: string
  weight: number
}

/** 类型流程配置请求 */
export interface TypeProcessConfigReq {
  /** 流程类型 REVIEW/MANAGE */
  processType: ProcessType
  /** 模板ID */
  templateId: number
}

/** 类型流程配置响应 */
export interface TypeProcessConfigResp {
  id?: number
  typeId?: number
  processType: ProcessType
  templateId: number
  templateName?: string
  isPrimary?: boolean
}

/** 类型表单映射请求 */
export interface TypeFormMappingReq {
  /** 映射类型 REVIEW/MANAGE */
  mappingType: string
  /** 节点类型 APPLICATION/AUDIT/REVIEW/DECISION/STAGE */
  nodeType: string
  /** 节点序号（APPLICATION为null，其他从1开始） */
  nodeSequence?: number
  /** 表单模板ID */
  formTemplateId: number
}

/** 类型表单映射响应 */
export interface TypeFormMappingResp {
  id?: number
  typeId?: number
  mappingType: string
  nodeType: string
  nodeSequence?: number
  formTemplateId: number
  formTemplateName?: string
}

/** 类型人员范围配置请求 */
export interface TypePersonnelConfigReq {
  /** 节点类型：APPLICATION/AUDIT/REVIEW/DECISION/STAGE */
  nodeType: string
  /** 节点序号（APPLICATION时为undefined） */
  nodeSequence?: number
  /** 范围类型 */
  scopeType: ScopeType
  /** 范围配置(JSON字符串) */
  scopeConfig: string
  /** 备注 */
  remark?: string
}

/** 类型人员范围配置响应 */
export interface TypePersonnelConfigResp {
  id?: number
  typeId?: number
  nodeType: string
  nodeSequence?: number
  scopeType: ScopeType
  /** 范围配置（后端返回 JSON Object，非字符串） */
  scopeConfig: Record<string, unknown>
  remark?: string
}

/** 类型审批规则配置请求 */
export interface TypeApprovalConfigReq {
  /** 节点标识 AUDIT_1/REVIEW_1/DECISION_1/ACCEPTANCE */
  nodeScope: string
  /** 审批模式 */
  approvalMode: ApprovalMode
  /** 所需审批人数 */
  requiredReviewerCount?: number
  /** 多数通过比例(0.01~1.00)，仅 VOTE_MAJORITY_PASS 时用 */
  majorityRatio?: number
  /** 评分通过阈值，SCORE_PASS 必填 */
  passThreshold?: number
  /** 评分表字段权重 {"fieldCode1": 0.4, "fieldCode2": 0.6}，多个 SCORE_TABLE 且加权时使用 */
  scoreTableWeights?: Record<string, number>
}

/** 类型审批规则配置响应 */
export interface TypeApprovalConfigResp {
  id?: number
  typeId?: number
  nodeScope: string
  approvalMode: ApprovalMode
  requiredReviewerCount?: number
  majorityRatio?: number
  passThreshold?: number
  scoreTableWeights?: Record<string, number>
}

/** 项目类型完整配置详情响应 */
export interface ProjectTypeDetailResp extends ProjectTypeResp {
  /** 流程配置列表（REVIEW + MANAGE 各一条） */
  processConfigs: TypeProcessConfigResp[]
  /** 表单映射列表（按流程节点展开） */
  formMappings: TypeFormMappingResp[]
  /** 人员范围配置列表（5类角色） */
  personnelConfigs: TypePersonnelConfigResp[]
  /** 审批规则配置列表 */
  approvalConfigs: TypeApprovalConfigResp[]
}

// ============ 项目管理模块 ============

/** 项目状态枚举（与后端 ProjectStatus 对应） */
export enum ProjectStatus {
  DRAFT = 1,
  SUBMITTED = 2,
  // 审核流程
  AUDITING = 10,
  AUDIT_PASSED = 11,
  NEEDS_REVISION_AUDIT = 12,
  AUDIT_REJECTED = 13,
  WAITING_AUDIT = 14,
  // 评审流程
  REVIEWING = 20,
  REVIEW_PASSED = 21,
  NEEDS_REVISION_REVIEW = 22,
  REVIEW_REJECTED = 23,
  WAITING_REVIEW = 24,
  // 决策流程
  DECIDING = 30,
  DECISION_PASSED = 31,
  NEEDS_REVISION_DECISION = 32,
  DECISION_REJECTED = 33,
  WAITING_DECISION = 34,
  // 终止
  TERMINATED = 40,
  // 执行阶段
  EXECUTING = 50,
  OVERTIME = 51,
  SUSPENDED = 52,
  ACCEPTING = 53,
  ACCEPTANCE_PASSED = 54,
  ACCEPTANCE_FAILED = 55,
  // 归档
  ARCHIVED_COMPLETED = 90,
  ARCHIVED_UNQUALIFIED = 91,
  ARCHIVED_CANCELLED = 92,
  VOIDED = 99,
}

/** 项目状态标签与颜色映射 */
export const PROJECT_STATUS_MAP: Record<number, { label: string; color: string }> = {
  1:  { label: '草稿',       color: 'gray'   },
  2:  { label: '已提交',     color: 'blue'   },
  10: { label: '审核中',     color: 'orange' },
  11: { label: '审核通过',   color: 'green'  },
  12: { label: '需修改',     color: 'gray'   },
  13: { label: '审核驳回',   color: 'red'    },
  14: { label: '等待中',     color: 'orange' },
  20: { label: '评审中',     color: 'orange' },
  21: { label: '评审通过',   color: 'green'  },
  22: { label: '需修改',     color: 'gray'   },
  23: { label: '评审驳回',   color: 'red'    },
  24: { label: '等待中',     color: 'orange' },
  30: { label: '决策中',     color: 'orange' },
  31: { label: '决策通过',   color: 'green'  },
  32: { label: '需修改',     color: 'gray'   },
  33: { label: '决策驳回',   color: 'red'    },
  34: { label: '等待中',     color: 'orange' },
  40: { label: '已终止',     color: 'red'    },
  50: { label: '执行中',     color: 'blue'   },
  51: { label: '执行超时',   color: 'red'    },
  52: { label: '项目暂停',   color: 'orange' },
  53: { label: '验收中',     color: 'purple' },
  54: { label: '验收通过',   color: 'green'  },
  55: { label: '验收不通过', color: 'red'    },
  90: { label: '已完成',     color: 'green'  },
  91: { label: '不合格',     color: 'red'    },
  92: { label: '已取消',     color: 'gray'   },
  99: { label: '已作废',     color: 'gray'   },
}

/** 任务类型枚举 */
export enum ProjectTaskType {
  AUDIT      = 'AUDIT',
  REVIEW     = 'REVIEW',
  DECISION   = 'DECISION',
  MANAGEMENT = 'MANAGEMENT',
  ACCEPTANCE = 'ACCEPTANCE',
}

/** 任务类型中文映射 */
export const PROJECT_TASK_TYPE_MAP: Record<string, string> = {
  AUDIT:            '审核',
  REVIEW:           '评审',
  DECISION:         '决策',
  MANAGEMENT:       '管理',
  ACCEPTANCE:       '验收',
  STAGE_SUBMISSION: '阶段提交',
}

/** 任务状态枚举 */
export enum ProjectTaskStatus {
  PENDING     = 'PENDING',
  SAVED       = 'SAVED',
  COMPLETED   = 'COMPLETED',
  TRANSFERRED = 'TRANSFERRED',
  CANCELLED   = 'CANCELLED',
}

/** 任务状态标签与颜色映射 */
export const PROJECT_TASK_STATUS_MAP: Record<string, { label: string; color: string }> = {
  PENDING:     { label: '待处理', color: 'orange' },
  SAVED:       { label: '暂存中', color: 'blue'   },
  COMPLETED:   { label: '已完成', color: 'green'  },
  TRANSFERRED: { label: '已转办', color: 'gray'   },
  CANCELLED:   { label: '已取消', color: 'gray'   },
}

/** 任务决策枚举 */
export enum TaskDecision {
  PASS        = 'PASS',
  REJECT      = 'REJECT',
  UNQUALIFIED = 'UNQUALIFIED',
  WITHDRAW    = 'WITHDRAW',
}

/** 项目阶段状态枚举 */
export enum ProjectStageStatus {
  PENDING     = 'PENDING',
  IN_PROGRESS = 'IN_PROGRESS',
  SUBMITTED   = 'SUBMITTED',
  COMPLETED   = 'COMPLETED',
  REJECTED    = 'REJECTED',
}

/** 项目阶段状态标签与颜色映射 */
export const PROJECT_STAGE_STATUS_MAP: Record<string, { label: string; color: string }> = {
  PENDING:     { label: '待开始', color: 'gray'   },
  IN_PROGRESS: { label: '进行中', color: 'blue'   },
  SUBMITTED:   { label: '已提交', color: 'orange' },
  COMPLETED:   { label: '已完成', color: 'green'  },
  REJECTED:    { label: '已驳回', color: 'red'    },
}

/** 项目查询参数 */
export interface ProjectQuery extends PageQuery {
  /** 项目名称（模糊） */
  projectName?: string
  /** 项目类型ID */
  typeId?: number
  /** 项目状态 */
  status?: number
}

/** 项目列表响应 */
export interface ProjectListResp {
  id: number
  projectName: string
  typeName: string
  typeId: number
  status: number
  applicantId: number
  currentNodeType?: string
  currentNodeSequence?: number
  submittedTime?: string
  createTime: string
}

/** 项目阶段响应（项目详情中） */
export interface ProjectStageResp {
  id: number
  stageType: string
  stageOrder: number
  stageName: string
  plannedDays?: number
  startDate?: string
  deadline?: string
  status: ProjectStageStatus
  isOverdue: boolean
  stageFormData?: Record<string, unknown>
  stageFormTemplate?: ProjectFormTemplateResp
}

/** 项目详情响应 */
export interface ProjectDetailResp {
  id: number
  projectName: string
  description?: string
  typeId: number
  typeName: string
  status: number
  applicantId: number
  currentNodeType?: string
  currentNodeSequence?: number
  submittedTime?: string
  applicationFormData?: Record<string, unknown>
  applicationFormTemplate?: ProjectFormTemplateResp
  stages?: ProjectStageResp[]
  createTime: string
  reviewProgress?: NodeProgressItem[]
  stageProgress?: StageProgressItem[]
}

/** 评审节点进度项 */
export interface NodeProgressItem {
  nodeType: string
  nodeSequence: number
  nodeName: string
  /** PENDING / ACTIVE / COMPLETED / REJECTED */
  nodeStatus: string
  /** 通过人数（已完成节点才有） */
  passCount?: number
  /** 总人数（已分配人数） */
  totalCount?: number
  /** 平均分（SCORE_PASS 模式才有） */
  averageScore?: number
  /** 节点结果（PASS/REJECT，已完成节点才有） */
  nodeResult?: string
  /** 已完成人数（进行中节点用于展示进度） */
  completedCount?: number
  /** 需要人数（从审批规则读取） */
  requiredCount?: number
}

/** 管理阶段进度项 */
export interface StageProgressItem {
  stageOrder: number
  stageName: string
  stageType: string
  /** PENDING / IN_PROGRESS / SUBMITTED / COMPLETED / REJECTED */
  stageStatus: string
  isOverdue: boolean
}

/** 项目表单模板（用于项目模块，避免与 form-template 模块类型混淆） */
export interface ProjectFormField {
  fieldCode: string
  fieldName: string
  fieldType: string
  isRequired: boolean
  span?: number
  fieldConfig?: unknown
  options?: unknown
}

export interface ProjectFormTemplateResp {
  id: number
  templateName: string
  fields: ProjectFormField[]
}

/** 创建草稿请求 */
export interface ProjectCreateReq {
  typeId: number
  projectName: string
  description?: string
}

/** 提交申请请求 */
export interface ProjectSubmitReq {
  formData: Record<string, unknown>
}

/** 修改申请表单请求（有痕） */
export interface ProjectUpdateFormReq {
  formData: Record<string, unknown>
  modifyReason?: string
}

/** 提交阶段成果请求 */
export interface StageFormSubmitReq {
  formData: Record<string, unknown>
}

/** 任务查询参数 */
export interface TaskQuery extends PageQuery {
  projectName?: string
  taskType?: string
  status?: string
}

/** 任务列表响应 */
export interface TaskListResp {
  id: number
  projectId: number
  projectName: string
  applicantName: string
  taskType: string
  nodeSequence: number
  nodeName: string
  status: string
  assignTime: string
  completeTime?: string
}

/** 前序节点汇总 */
export interface NodeSummaryResp {
  nodeType: string
  nodeSequence: number
  nodeName: string
  passCount: number
  totalCount: number
  averageScore?: number
  result: string
}

/** 任务详情响应 */
export interface TaskDetailResp {
  taskId: number
  taskType: string
  nodeSequence: number
  nodeName: string
  taskStatus: string
  assignTime: string
  savedFormData?: Record<string, unknown>
  taskFormTemplate?: ProjectFormTemplateResp
  projectId: number
  projectName: string
  description?: string
  applicantId: number
  submittedTime?: string
  applicationFormData?: Record<string, unknown>
  applicationFormTemplate?: ProjectFormTemplateResp
  previousNodes?: NodeSummaryResp[]
  currentStage?: ProjectStageResp
  allStages?: ProjectStageResp[]
}

/** 任务提交请求 */
export interface TaskSubmitReq {
  decision: string
  score?: number
  rejectBackToStageOrder?: number
  formData?: Record<string, unknown>
}

/** 任务暂存请求 */
export interface TaskSaveReq {
  formData: Record<string, unknown>
}

/** 转办请求 */
export interface TaskTransferReq {
  targetUserId: string
  transferRemark?: string
}

/** 转办候选人 */
export interface TaskCandidateResp {
  userId: string
  nickname: string
  username: string
}

/** 节点历史中每人的填写记录 */
export interface PersonEntryResp {
  assigneeName: string
  decision?: string
  score?: number
  formData?: Record<string, unknown>
  formTemplate?: FormTemplateResp
  completeTime?: string
}

/** 节点历史（含每人填写） */
export interface NodeHistoryResp {
  nodeType: string        // APPLICATION / AUDIT / REVIEW / DECISION
  nodeSequence: number
  nodeName: string
  nodeResult?: string     // PASS / REJECT
  passCount?: number
  totalCount?: number
  averageScore?: number
  entries: PersonEntryResp[]
}
