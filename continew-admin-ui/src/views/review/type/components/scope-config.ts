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

export function deserializeScopeConfig(scopeConfig: string | Record<string, unknown>, scopeType: ScopeType): ScopeConfig['parsed'] {
  const parsed: ScopeConfig['parsed'] = {
    userIds: [],
    deptIds: [],
    includeSub: false,
  }
  try {
    // 后端直接返回 JSON Object，兼容字符串格式（向后兼容）
    const obj: Record<string, unknown> = typeof scopeConfig === 'string' ? JSON.parse(scopeConfig) : scopeConfig
    switch (scopeType) {
      case 'USER':
        parsed.userIds = ((obj.userIds as unknown[]) || []).map(String)
        break
      case 'DEPT':
        parsed.deptIds = ((obj.deptIds as unknown[]) || []).map(String)
        parsed.includeSub = (obj.includeSub as boolean) ?? false
        break
    }
  } catch {
    // 解析失败时保留默认值
  }
  return parsed
}
