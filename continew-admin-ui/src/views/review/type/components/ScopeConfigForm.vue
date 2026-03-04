<template>
  <div class="scope-config-form">
    <a-form layout="vertical">
      <a-row :gutter="16">
        <a-col :span="8">
          <a-form-item label="范围类型">
            <a-select
              v-model="local.scopeType"
              :disabled="disabled"
              placeholder="请选择范围类型"
              style="width: 100%"
              @change="onScopeTypeChange"
            >
              <a-option value="USER">指定用户</a-option>
              <a-option value="ROLE">按系统角色</a-option>
              <a-option value="DEPT">按部门</a-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="16">
          <a-form-item label="备注">
            <a-input
              v-model="local.remark"
              :disabled="disabled"
              placeholder="可选备注信息"
              :max-length="200"
            />
          </a-form-item>
        </a-col>
      </a-row>

      <!-- USER 范围 -->
      <template v-if="local.scopeType === 'USER'">
        <a-form-item label="用户ID列表">
          <a-input-tag
            v-model="local.parsed.userIds"
            :disabled="disabled"
            placeholder="输入用户ID后按回车添加"
            unique-value
            style="width: 100%"
          />
          <div class="hint">输入系统用户ID，多个ID逐个添加</div>
        </a-form-item>
      </template>

      <!-- DEPT 范围 -->
      <template v-else-if="local.scopeType === 'DEPT'">
        <a-row :gutter="16">
          <a-col :span="16">
            <a-form-item label="部门ID列表">
              <a-input-tag
                v-model="local.parsed.deptIds"
                :disabled="disabled"
                placeholder="输入部门ID后按回车添加"
                unique-value
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="包含子部门">
              <a-switch v-model="local.parsed.includeSub" :disabled="disabled" />
            </a-form-item>
          </a-col>
        </a-row>
      </template>

      <!-- ROLE 范围 -->
      <template v-else-if="local.scopeType === 'ROLE'">
        <a-form-item label="业务角色">
          <a-input-tag
            v-model="local.parsed.businessRoles"
            :disabled="disabled"
            placeholder="输入业务角色名后按回车添加，如：REVIEWER"
            unique-value
            style="width: 100%"
          />
          <div class="hint">填写系统中定义的业务角色标识</div>
        </a-form-item>
      </template>

    </a-form>
  </div>
</template>

<!-- 导出供外部使用的接口和工具函数（<script setup> 不支持 export，须放独立 <script> 块） -->
<script lang="ts">
import { ScopeType } from '@/apis/review'

export interface ScopeConfig {
  scopeType: ScopeType | undefined
  remark: string
  parsed: {
    userIds: string[]
    deptIds: string[]
    includeSub: boolean
    businessRoles: string[]
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
      businessRoles: [],
    },
  }
}

export function serializeScopeConfig(config: ScopeConfig): string {
  if (!config.scopeType) return '{}'
  switch (config.scopeType) {
    case 'USER':
      return JSON.stringify({ userIds: config.parsed.userIds.map(Number).filter(n => !Number.isNaN(n)) })
    case 'DEPT':
      return JSON.stringify({ deptIds: config.parsed.deptIds.map(Number).filter(n => !Number.isNaN(n)), includeSub: config.parsed.includeSub })
    case 'ROLE':
      return JSON.stringify({ businessRoles: config.parsed.businessRoles })
    default:
      return '{}'
  }
}

export function deserializeScopeConfig(scopeConfig: string, scopeType: ScopeType): ScopeConfig['parsed'] {
  const parsed: ScopeConfig['parsed'] = {
    userIds: [],
    deptIds: [],
    includeSub: false,
    businessRoles: [],
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
      case 'ROLE':
        parsed.businessRoles = obj.businessRoles || []
        break
    }
  }
  catch {
    // 解析失败时保留默认值
  }
  return parsed
}
</script>

<script setup lang="ts">
// ScopeConfig、defaultScopeConfig 由上方 <script> 块导出，此处直接使用

const props = defineProps<{
  modelValue: ScopeConfig
  disabled?: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', v: ScopeConfig): void
}>()

const local = reactive<ScopeConfig>(defaultScopeConfig())

watch(
  () => props.modelValue,
  (v) => {
    // 值比对防护，阻断 watch 循环
    if (
      local.scopeType === v.scopeType
      && local.remark === v.remark
      && JSON.stringify(local.parsed) === JSON.stringify(v.parsed)
    ) {
      return
    }
    Object.assign(local, {
      scopeType: v.scopeType,
      remark: v.remark,
      parsed: { ...v.parsed },
    })
  },
  { immediate: true, deep: true },
)

watch(
  local,
  () => {
    emit('update:modelValue', {
      ...local,
      parsed: { ...local.parsed },
    })
  },
  { deep: true },
)

const onScopeTypeChange = () => {
  local.parsed = {
    userIds: [],
    deptIds: [],
    includeSub: false,
    businessRoles: [],
  }
}
</script>

<style scoped>
.scope-config-form {
  width: 100%;
}

.hint {
  color: var(--color-text-3);
  font-size: 12px;
  margin-top: 4px;
}
</style>
