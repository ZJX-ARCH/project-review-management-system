import type { ScopeType } from '@/apis/review'

export interface ScopeConfig {
  scopeType: ScopeType | undefined
  remark: string
  parsed: {
    userIds: string[]
    deptIds: string[]
    includeSub: boolean
  }
}

export function defaultScopeConfig(): ScopeConfig {
  return {
    scopeType: undefined,
    remark: '',
    parsed: {
      userIds: [],
      deptIds: [],
      includeSub: false,
    },
  }
}

export function serializeScopeConfig(config: ScopeConfig): string {
  if (!config.scopeType) return '{}'
  switch (config.scopeType) {
    // 注意：雪花ID超过 JS Number 精度（53位），必须保持字符串传输，不可转 Number
    case 'USER':
      return JSON.stringify({ userIds: config.parsed.userIds.filter(Boolean) })
    case 'DEPT':
      return JSON.stringify({ deptIds: config.parsed.deptIds.filter(Boolean), includeSub: config.parsed.includeSub })
    default:
      return '{}'
  }
}

export function deserializeScopeConfig(scopeConfig: string, scopeType: ScopeType): ScopeConfig['parsed'] {
  const parsed: ScopeConfig['parsed'] = {
    userIds: [],
    deptIds: [],
    includeSub: false,
  }
  try {
    const obj = JSON.parse(scopeConfig)
    switch (scopeType) {
      case 'USER':
        parsed.userIds = (obj.userIds || []).map(String)
        break
      case 'DEPT':
        parsed.deptIds = (obj.deptIds || []).map(String)
        parsed.includeSub = obj.includeSub ?? false
        break
    }
  } catch {
    // 解析失败时保留默认值
  }
  return parsed
}
