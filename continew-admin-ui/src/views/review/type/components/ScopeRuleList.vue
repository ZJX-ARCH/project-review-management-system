<template>
  <div class="scope-rule-list">
    <a-collapse v-if="localRules.length > 0" :bordered="false">
      <a-collapse-item
        v-for="(rule, idx) in localRules"
        :key="idx"
      >
        <template #header>
          <span>规则 {{ idx + 1 }}：{{ scopeTypeLabel(rule.scopeType) }}</span>
          <a-tag v-if="ruleErrors[idx]" color="red" size="small" style="margin-left: 8px">校验未通过</a-tag>
        </template>
        <template #extra>
          <a-button
            v-if="!disabled"
            type="text"
            status="danger"
            size="mini"
            @click.stop="removeRule(idx)"
          >
            <template #icon><icon-delete /></template>
          </a-button>
        </template>
        <ScopeConfigForm
          v-model="localRules[idx]"
          :disabled="disabled"
          :role-id="roleId"
          :errors="ruleErrors[idx]"
        />
      </a-collapse-item>
    </a-collapse>

    <a-empty v-if="localRules.length === 0" description="暂无范围规则，点击下方添加" style="padding: 16px 0" />

    <a-button
      v-if="!disabled"
      type="dashed"
      long
      style="margin-top: 8px"
      @click="addRule"
    >
      <template #icon><icon-plus /></template>
      添加规则
    </a-button>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import ScopeConfigForm, { type ScopeFieldErrors } from './ScopeConfigForm.vue'
import { type ScopeConfig, defaultScopeConfig } from './scope-config'

const props = defineProps<{
  modelValue: ScopeConfig[]
  disabled?: boolean
  roleId?: string
  ruleErrors?: Record<number, ScopeFieldErrors>
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', v: ScopeConfig[]): void
}>()

const ruleErrors = computed(() => props.ruleErrors ?? {})

const localRules = ref<ScopeConfig[]>([])

// 同步 props → local（仅在外部修改时，防御 undefined 初始值）
watch(
  () => props.modelValue,
  (v) => {
    const val = v ?? []
    if (JSON.stringify(val) !== JSON.stringify(localRules.value)) {
      localRules.value = val.map((r) => ({ ...r, parsed: { ...r.parsed } }))
    }
  },
  { immediate: true, deep: true },
)

// 同步 local → parent
watch(
  localRules,
  (v) => {
    emit('update:modelValue', v.map((r) => ({ ...r, parsed: { ...r.parsed } })))
  },
  { deep: true },
)

const addRule = () => {
  localRules.value.push(defaultScopeConfig())
}

const removeRule = (idx: number) => {
  localRules.value.splice(idx, 1)
}

const scopeTypeLabel = (t?: string) =>
  ({ USER: '指定用户', DEPT: '按部门' }[t ?? ''] ?? '未设置')
</script>

<style scoped>
.scope-rule-list {
  width: 100%;
}
</style>
