<template>
  <a-modal
    v-model:visible="visible"
    title="分配部门角色"
    :mask-closable="false"
    :esc-to-close="false"
    :width="width >= 600 ? 600 : '100%'"
    draggable
    @before-ok="save"
    @close="reset"
  >
    <a-form ref="formRef" :model="form" layout="vertical">
      <a-form-item label="选择部门" field="selectedDepts" :rules="[{ required: true, message: '请选择部门' }]">
        <a-tree-select
          v-model="form.selectedDepts"
          :data="deptList"
          :field-names="{ key: 'key', title: 'title', children: 'children' }"
          placeholder="请选择部门"
          allow-search
          allow-clear
          multiple
        />
      </a-form-item>
    </a-form>

    <!-- 部门角色配置区 -->
    <div v-if="form.selectedDepts.length > 0" class="dept-roles-section">
      <a-divider>部门角色配置</a-divider>
      <a-space direction="vertical" :size="16" fill>
        <a-card
          v-for="deptId in form.selectedDepts"
          :key="deptId"
          :title="getDeptName(deptId)"
          :bordered="true"
          size="small"
          class="dept-card"
        >
          <template #extra>
            <a-radio
              v-model="form.deptId"
              :value="deptId"
            >
              主部门
            </a-radio>
          </template>
          <a-select
            v-model="deptRolesMap[String(deptId)]"
            :options="roleList"
            multiple
            allow-clear
            allow-search
            placeholder="请选择该部门的角色"
          />
        </a-card>
      </a-space>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import type { TreeNodeData } from '@arco-design/web-vue'
import { Message } from '@arco-design/web-vue'
import { useWindowSize } from '@vueuse/core'
import { getUser, updateUserRole } from '@/apis/system/user'
import { useDept, useRole } from '@/hooks/app'

const emit = defineEmits<{
  (e: 'save-success'): void
}>()

const { width } = useWindowSize()
const dataId = ref('')
const visible = ref(false)
const formRef = ref()

const { deptList, getDeptList } = useDept()
const { roleList, getRoleList } = useRole()

const form = reactive<{
  deptId?: number | string
  selectedDepts: Array<number | string>
}>({
  deptId: undefined,
  selectedDepts: [],
})
const deptRolesMap = reactive<Record<string, number[]>>({})

// 获取部门名称
const getDeptName = (deptId: number | string): string => {
  const findDept = (nodes: TreeNodeData[]): string | undefined => {
    for (const node of nodes) {
      const nodeId = node.key || node.value
      if (String(nodeId) === String(deptId)) {
        return node.title as string
      }
      if (node.children && node.children.length > 0) {
        const found = findDept(node.children)
        if (found)
          return found
      }
    }
    return undefined
  }
  return findDept(deptList.value) || `部门${deptId}`
}

// 监听部门选择变化，同步 deptRolesMap
watch(() => form.selectedDepts, (newDepts, oldDepts) => {
  // 移除未选中部门的角色配置
  if (oldDepts) {
    oldDepts.forEach((deptId: number | string) => {
      if (!newDepts.includes(deptId)) {
        delete deptRolesMap[String(deptId)]
      }
    })
  }

  // 为新选中的部门初始化空角色列表
  newDepts.forEach((deptId: number | string) => {
    const key = String(deptId)
    if (!deptRolesMap[key]) {
      deptRolesMap[key] = []
    }
  })

  // 如果主部门不在选中列表中，清空主部门
  if (form.deptId && !newDepts.some((id: number | string) => String(id) === String(form.deptId))) {
    form.deptId = undefined
  }

  // 如果只有一个部门，自动设为主部门
  if (newDepts.length === 1 && !form.deptId) {
    form.deptId = newDepts[0]
  }
})

// 重置
const reset = () => {
  formRef.value?.resetFields()
  form.selectedDepts = []
  Object.keys(deptRolesMap).forEach(key => delete deptRolesMap[key])
  form.deptId = undefined
}

// 保存
const save = async () => {
  try {
    const isInvalid = await formRef.value?.validate()
    if (isInvalid)
      return false

    // 检查是否所有部门都配置了角色
    const hasEmptyRoles = form.selectedDepts.some((deptId: number | string) => {
      const key = String(deptId)
      return !deptRolesMap[key] || deptRolesMap[key].length === 0
    })
    if (hasEmptyRoles) {
      Message.error('请为所有部门配置角色')
      return false
    }

    // 检查是否设置了主部门
    if (!form.deptId) {
      Message.error('请选择主部门')
      return false
    }

    // 构建deptRoles数组
    const deptRoles = form.selectedDepts.map((deptId: number | string) => ({
      deptId,
      roleIds: deptRolesMap[String(deptId)] || [],
    }))

    const submitData = {
      deptId: form.deptId,
      deptRoles,
    }

    await updateUserRole(submitData, dataId.value)
    Message.success('分配成功')
    emit('save-success')
    return true
  }
  catch (error) {
    return false
  }
}

// 初始化
const onOpen = async (id: string) => {
  reset()
  dataId.value = id

  // 加载部门和角色列表
  await Promise.all([
    getDeptList(),
    getRoleList(),
  ])

  // 获取用户详情
  const { data } = await getUser(id)

  // 设置主部门
  if (data.deptId) {
    form.deptId = data.deptId
  }

  // 设置部门角色配置
  if (data.deptRoles && data.deptRoles.length > 0) {
    form.selectedDepts = data.deptRoles.map(item => item.deptId)
    data.deptRoles.forEach((item: { deptId: number, roleIds: number[] }) => {
      deptRolesMap[String(item.deptId)] = item.roleIds
    })

    // 将主部门排在第一位（仅在加载数据时排序）
    if (form.deptId && form.selectedDepts.length > 1) {
      const mainDeptId = form.deptId
      const otherDepts = form.selectedDepts.filter(id => String(id) !== String(mainDeptId))
      form.selectedDepts = [mainDeptId, ...otherDepts]
    }
  }

  visible.value = true
}

defineExpose({ onOpen })
</script>

<style scoped lang="scss">
.dept-roles-section {
  margin-top: 16px;
}

.dept-card {
  :deep(.arco-card-header) {
    background-color: var(--color-fill-2);
    border-bottom: 1px solid var(--color-border-2);
  }

  :deep(.arco-card-body) {
    padding: 12px 16px;
  }
}
</style>
