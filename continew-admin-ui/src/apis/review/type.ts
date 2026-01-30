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
