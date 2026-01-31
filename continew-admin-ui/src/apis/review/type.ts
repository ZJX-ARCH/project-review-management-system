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
  /** 表格 */
  TABLE = 'TABLE',
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
