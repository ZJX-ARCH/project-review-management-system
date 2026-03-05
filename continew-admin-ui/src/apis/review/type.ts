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
  scopeConfig: string
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
  /** 验收不通过回退目标（仅ACCEPTANCE节点），FIRST_EXECUTION 或阶段序号如 "3" */
  rejectBackTo?: string
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
  rejectBackTo?: string
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
