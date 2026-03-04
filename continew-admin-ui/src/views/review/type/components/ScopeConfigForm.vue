<script setup lang="ts">
import type { TreeNodeData } from '@arco-design/web-vue'
import { type ScopeConfig, defaultScopeConfig } from './scope-config'
import { listAllUser, listUser } from '@/apis/system/user'
import { listDeptDictTree } from '@/apis/system/dept'
import type { UserResp } from '@/apis/system/user'

const props = defineProps<{
  modelValue: ScopeConfig
  disabled?: boolean
  roleId?: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', v: ScopeConfig): void
}>()

const local = reactive<ScopeConfig>(defaultScopeConfig())

const userOptions = ref<{ label: string, value: string }[]>([])
const userOptionsLoading = ref(false)

const deptTreeData = ref<TreeNodeData[]>([])
const deptLoading = ref(false)

function mergeUserOptions(users: UserResp[]) {
  const existing = new Map(userOptions.value.map((o) => [o.value, o]))
  for (const u of users) {
    existing.set(u.id, { label: `${u.nickname}（${u.username}）`, value: u.id })
  }
  userOptions.value = Array.from(existing.values())
}

let searchTimer: ReturnType<typeof setTimeout> | null = null

const handleUserSearch = (value: string) => {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(async () => {
    userOptionsLoading.value = true
    try {
      const res = await listUser({
        description: value || undefined,
        roleId: props.roleId,
        sort: ['t1.createTime,desc'],
        pageNum: 1,
        pageSize: 20,
      })
      mergeUserOptions((res.data?.list || []) as UserResp[])
    } catch { /* ignore */ } finally {
      userOptionsLoading.value = false
    }
  }, 300)
}

async function loadDeptTree() {
  deptLoading.value = true
  try {
    const res = await listDeptDictTree({ description: '' })
    deptTreeData.value = res.data || []
  } finally {
    deptLoading.value = false
  }
}

watch(
  () => local.parsed.userIds,
  async (ids) => {
    if (!ids || ids.length === 0) return
    const existingIds = new Set(userOptions.value.map((o) => o.value))
    const missingIds = ids.filter((id) => !existingIds.has(id))
    if (missingIds.length === 0) return
    try {
      const res = await listAllUser({ userIds: missingIds })
      mergeUserOptions(res.data || [])
    } catch {
      // 加载失败时忽略
    }
  },
  { immediate: true },
)

onMounted(async () => {
  await loadDeptTree()
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

const onScopeTypeChange = () => {
  local.parsed = {
    userIds: [],
    deptIds: [],
    includeSub: false,
  }
}
</script>

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
        <a-form-item label="用户范围">
          <a-select
            v-model="local.parsed.userIds"
            multiple
            allow-search
            allow-clear
            :disabled="disabled"
            :options="userOptions"
            :loading="userOptionsLoading"
            placeholder="搜索并选择用户（按昵称/用户名）"
            :filter-option="false"
            style="width: 100%"
            @search="handleUserSearch"
          />
        </a-form-item>
      </template>

      <!-- DEPT 范围 -->
      <template v-else-if="local.scopeType === 'DEPT'">
        <a-row :gutter="16">
          <a-col :span="16">
            <a-form-item label="部门范围">
              <a-tree-select
                v-model="local.parsed.deptIds"
                :data="deptTreeData"
                :loading="deptLoading"
                multiple
                allow-clear
                allow-search
                :disabled="disabled"
                placeholder="请选择部门"
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
    </a-form>
  </div>
</template>

<style scoped>
.scope-config-form {
  width: 100%;
}
</style>
