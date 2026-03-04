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
    case 'USER':
      return JSON.stringify({ userIds: config.parsed.userIds.map(Number).filter((n) => !Number.isNaN(n)) })
    case 'DEPT':
      return JSON.stringify({ deptIds: config.parsed.deptIds.map(Number).filter((n) => !Number.isNaN(n)), includeSub: config.parsed.includeSub })
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
