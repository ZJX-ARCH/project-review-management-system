<script setup lang="ts">
import type { TreeNodeData } from '@arco-design/web-vue'
import { type ScopeConfig, defaultScopeConfig } from './scope-config'
import { getReviewDeptTree, getReviewPersonsByIds, searchReviewPersons } from '@/apis/review/project-type'

type PersonOption = { label: string; value: string }
type PersonResp = { id: string; nickname: string; username: string }

export interface ScopeFieldErrors {
  scopeType?: string
  data?: string
}

const props = defineProps<{
  modelValue: ScopeConfig
  disabled?: boolean
  roleId?: string
  errors?: ScopeFieldErrors
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', v: ScopeConfig): void
}>()

const local = reactive<ScopeConfig>(defaultScopeConfig())

const userOptions = ref<PersonOption[]>([])
const userOptionsLoading = ref(false)

const deptTreeData = ref<TreeNodeData[]>([])
const deptLoading = ref(false)

function mergeUserOptions(persons: PersonResp[]) {
  const existing = new Map(userOptions.value.map((o) => [o.value, o]))
  for (const u of persons) {
    existing.set(u.id, { label: `${u.nickname}（${u.username}）`, value: u.id })
  }
  userOptions.value = Array.from(existing.values())
}

/** 加载初始用户列表（组件挂载 或 scopeType 切换为 USER 时调用） */
async function loadInitialUsers() {
  userOptionsLoading.value = true
  try {
    const res = await searchReviewPersons({
      roleId: props.roleId || undefined,
      limit: 20,
    })
    mergeUserOptions(res.data || [])
  }
  catch { /* ignore */ }
  finally {
    userOptionsLoading.value = false
  }
}

let searchTimer: ReturnType<typeof setTimeout> | null = null

const handleUserSearch = (value: string) => {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(async () => {
    userOptionsLoading.value = true
    try {
      const res = await searchReviewPersons({
        roleId: props.roleId || undefined,
        keyword: value || undefined,
        limit: 20,
      })
      mergeUserOptions(res.data || [])
    }
    catch { /* ignore */ }
    finally {
      userOptionsLoading.value = false
    }
  }, 300)
}

onUnmounted(() => {
  if (searchTimer) {
    clearTimeout(searchTimer)
    searchTimer = null
  }
})

async function loadDeptTree() {
  deptLoading.value = true
  try {
    const res = await getReviewDeptTree()
    deptTreeData.value = (res.data || []) as TreeNodeData[]
  }
  finally {
    deptLoading.value = false
  }
}

// 回显已选用户：补全 userOptions 中缺少的条目
watch(
  () => local.parsed.userIds,
  async (ids) => {
    if (!ids || ids.length === 0) return
    const existingIds = new Set(userOptions.value.map((o) => o.value))
    const missingIds = ids.filter((id) => !existingIds.has(id))
    if (missingIds.length === 0) return
    try {
      const res = await getReviewPersonsByIds(missingIds)
      mergeUserOptions(res.data || [])
    }
    catch {
      // 加载失败时忽略
    }
  },
  { immediate: true },
)

onMounted(async () => {
  await loadDeptTree()
  // 初始加载用户选项（避免 USER 范围打开时一片空白）
  if (local.scopeType === 'USER') {
    await loadInitialUsers()
  }
})

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

const onScopeTypeChange = async () => {
  local.parsed = {
    userIds: [],
    deptIds: [],
    includeSub: false,
  }
  // 切换到 USER 范围时立即加载初始用户列表
  if (local.scopeType === 'USER') {
    await loadInitialUsers()
  }
}
</script>

<template>
  <div class="scope-config-form">
    <a-form layout="vertical">
      <a-row :gutter="16">
        <a-col :span="8">
          <a-form-item :class="{ 'has-error': !!props.errors?.scopeType }">
            <template #label>
              <span>范围类型<span class="required-star"> *</span></span>
            </template>
            <a-select
              v-model="local.scopeType"
              :disabled="disabled"
              :status="props.errors?.scopeType ? 'error' : undefined"
              placeholder="请选择范围类型"
              style="width: 100%"
              @change="onScopeTypeChange"
            >
              <a-option value="USER">指定用户</a-option>
              <a-option value="DEPT">按部门</a-option>
            </a-select>
            <div v-if="props.errors?.scopeType" class="field-error">{{ props.errors.scopeType }}</div>
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
        <a-form-item :class="{ 'has-error': !!props.errors?.data }">
          <template #label>
            <span>用户范围<span class="required-star"> *</span></span>
          </template>
          <a-select
            v-model="local.parsed.userIds"
            multiple
            allow-search
            allow-clear
            :disabled="disabled"
            :options="userOptions"
            :loading="userOptionsLoading"
            :status="props.errors?.data ? 'error' : undefined"
            placeholder="搜索并选择用户（按昵称/用户名）"
            :filter-option="false"
            style="width: 100%"
            @search="handleUserSearch"
          />
          <div v-if="props.errors?.data" class="field-error">{{ props.errors.data }}</div>
        </a-form-item>
      </template>

      <!-- DEPT 范围 -->
      <template v-else-if="local.scopeType === 'DEPT'">
        <a-row :gutter="16">
          <a-col :span="16">
            <a-form-item :class="{ 'has-error': !!props.errors?.data }">
              <template #label>
                <span>部门范围<span class="required-star"> *</span></span>
              </template>
              <a-tree-select
                v-model="local.parsed.deptIds"
                :data="deptTreeData"
                :loading="deptLoading"
                multiple
                allow-clear
                allow-search
                :disabled="disabled"
                :status="props.errors?.data ? 'error' : undefined"
                placeholder="请选择部门"
                style="width: 100%"
              />
              <div v-if="props.errors?.data" class="field-error">{{ props.errors.data }}</div>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="包含子部门">
              <a-switch v-model="local.parsed.includeSub" :disabled="disabled" />
            </a-form-item>
          </a-col>
        </a-row>
      </template>
    </a-form>
  </div>
</template>

<style scoped>
.scope-config-form {
  width: 100%;
}

.required-star {
  color: rgb(var(--red-6));
}

.field-error {
  color: rgb(var(--red-6));
  font-size: 12px;
  line-height: 1.5;
  margin-top: 2px;
}
</style>
