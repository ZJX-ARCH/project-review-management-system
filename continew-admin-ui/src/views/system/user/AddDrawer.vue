<template>
  <a-drawer
    v-model:visible="visible"
    :title="title"
    :mask-closable="false"
    :esc-to-close="false"
    :width="width >= 600 ? 600 : '100%'"
    @before-ok="save"
    @close="reset"
  >
    <GiForm ref="formRef" v-model="form" :columns="columns" />

    <!-- 部门角色配置区 -->
    <div v-if="selectedDepts.length > 0" class="dept-roles-section">
      <a-divider>部门角色配置</a-divider>
      <a-space direction="vertical" :size="16" fill>
        <a-card
          v-for="deptId in selectedDepts"
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
  </a-drawer>
</template>

<script setup lang="ts">
import { Message, type TreeNodeData } from '@arco-design/web-vue'
import { useWindowSize } from '@vueuse/core'
import { computed, reactive, ref } from 'vue'
import { addUser, getUser, updateUser } from '@/apis/system/user'
import { type ColumnItem, GiForm } from '@/components/GiForm'
import type { Gender, Status } from '@/types/global'
import { GenderList } from '@/constant/common'
import { useResetReactive } from '@/hooks'
import { useDept, useRole } from '@/hooks/app'
import { encryptByRsa } from '@/utils/encrypt'

const emit = defineEmits<{
  (e: 'save-success'): void
}>()

const { width } = useWindowSize()

interface DeptRoleItem {
  deptId: number | string
  roleIds: number[]
}

const dataId = ref('')
const visible = ref(false)
const isUpdate = computed(() => !!dataId.value)
const title = computed(() => (isUpdate.value ? '修改用户' : '新增用户'))
const formRef = ref<InstanceType<typeof GiForm>>()
const { roleList, getRoleList } = useRole()
const { deptList, getDeptList } = useDept()

// 选中的部门ID列表(从form中获取)
const selectedDepts = computed(() => form.selectedDepts || [])

// 部门角色映射 { deptId: [roleId1, roleId2] }
// 使用字符串作为 key 以避免大整数精度问题
const deptRolesMap = reactive<Record<string, number[]>>({})

// 获取部门名称
const getDeptName = (deptId: number | string) => {
  // 直接从 deptList 中递归查找
  const findDept = (nodes: TreeNodeData[], targetId: number | string): TreeNodeData | null => {
    for (const node of nodes) {
      // TreeNodeData 使用 key 字段存储 ID
      const nodeId = node.key || node.value
      // 使用字符串比较避免精度问题
      if (String(nodeId) === String(targetId)) {
        return node
      }
      if (node.children && node.children.length > 0) {
        const found = findDept(node.children, targetId)
        if (found)
          return found
      }
    }
    return null
  }

  const dept = findDept(deptList.value, deptId)
  return dept ? (dept.title as string) : `部门 ${deptId}`
}

// 处理部门变更
const handleDeptChange = (value: (number | string)[]) => {
  // 不进行类型转换，保持原样以避免大整数精度丢失
  // 移除未选中部门的角色配置
  Object.keys(deptRolesMap).forEach((key) => {
    if (!value.some(v => String(v) === key)) {
      delete deptRolesMap[key]
    }
  })

  // 如果当前主部门被移除，选择第一个部门作为主部门
  if (form.deptId && !value.some(v => String(v) === String(form.deptId))) {
    form.deptId = value.length > 0 ? value[0] : undefined
  }

  // 如果还没有主部门且有部门被选中，默认第一个为主部门
  if (!form.deptId && value.length > 0) {
    form.deptId = value[0]
  }

  // 更新 selectedDepts，保持原始类型
  form.selectedDepts = value
}

const [form, resetForm] = useResetReactive({
  gender: 1 as Gender,
  status: 1 as Status,
  deptId: undefined as number | string | undefined,
  selectedDepts: [] as (number | string)[],
})

const columns: ColumnItem[] = reactive([
  {
    label: '昵称',
    field: 'nickname',
    type: 'input',
    span: 24,
    required: true,
    props: {
      maxLength: 30,
    },
  },
  {
    label: '用户名',
    field: 'username',
    type: 'input',
    span: 24,
    required: true,
    props: {
      maxLength: 64,
    },
  },
  {
    label: '密码',
    field: 'password',
    type: 'input-password',
    span: 24,
    required: true,
    props: {
      maxLength: 32,
      showWordLimit: true,
    },
    hide: () => isUpdate.value,
  },
  {
    label: '手机号码',
    field: 'phone',
    type: 'input',
    span: 24,
    props: {
      maxLength: 11,
    },
  },
  {
    label: '邮箱',
    field: 'email',
    type: 'input',
    span: 24,
    props: {
      maxLength: 255,
    },
  },
  {
    label: '性别',
    field: 'gender',
    type: 'radio-group',
    span: 24,
    props: {
      options: GenderList,
    },
  },
  {
    label: '选择部门（可多选）',
    field: 'selectedDepts',
    type: 'tree-select',
    span: 24,
    required: true,
    props: {
      data: deptList,
      multiple: true,
      allowClear: true,
      allowSearch: true,
      maxTagCount: 0, // 0 表示响应式折叠，不限制数量
      fieldNames: {
        key: 'key',
        title: 'title',
        children: 'children',
      },
      onChange: handleDeptChange,
    },
  },
  {
    label: '描述',
    field: 'description',
    type: 'textarea',
    span: 24,
  },
  {
    label: '状态',
    field: 'status',
    type: 'switch',
    span: 24,
    props: {
      type: 'round',
      checkedValue: 1,
      uncheckedValue: 2,
      checkedText: '启用',
      uncheckedText: '禁用',
    },
  },
])

// 重置
const reset = () => {
  formRef.value?.formRef?.resetFields()
  resetForm()
  Object.keys(deptRolesMap).forEach(key => delete deptRolesMap[key])
}

// 保存
const save = async () => {
  const rawPassword = form.password
  try {
    const isInvalid = await formRef.value?.formRef?.validate()
    if (isInvalid)
      return false

    // 检查是否所有部门都配置了角色
    const hasEmptyRoles = selectedDepts.value.some((deptId: number | string) => {
      const key = String(deptId)
      return !deptRolesMap[key] || deptRolesMap[key].length === 0
    })
    if (hasEmptyRoles) {
      Message.error('请为所有部门配置角色')
      return false
    }

    // 构建deptRoles数组
    const deptRoles: DeptRoleItem[] = selectedDepts.value.map((deptId: number | string) => ({
      deptId,
      roleIds: deptRolesMap[String(deptId)] || [],
    }))

    const submitData = {
      ...form,
      deptRoles,
    }

    if (isUpdate.value) {
      await updateUser(submitData, dataId.value)
      Message.success('修改成功')
    }
    else {
      if (rawPassword) {
        submitData.password = encryptByRsa(rawPassword) || ''
      }
      await addUser(submitData)
      Message.success('新增成功')
    }
    emit('save-success')
    return true
  }
  catch (error) {
    form.password = rawPassword
    return false
  }
}

// 新增
const onAdd = async () => {
  reset()
  if (!deptList.value.length) {
    await getDeptList()
  }
  if (!roleList.value.length) {
    await getRoleList()
  }
  dataId.value = ''
  visible.value = true
}

// 修改
const onUpdate = async (id: string) => {
  reset()
  dataId.value = id
  if (!deptList.value.length) {
    await getDeptList()
  }
  if (!roleList.value.length) {
    await getRoleList()
  }

  const { data } = await getUser(id)
  Object.assign(form, data)

  // 解析deptRoles数据
  if (data.deptRoles && Array.isArray(data.deptRoles)) {
    form.selectedDepts = data.deptRoles.map((dr: DeptRoleItem) => dr.deptId)
    data.deptRoles.forEach((dr: DeptRoleItem) => {
      deptRolesMap[String(dr.deptId)] = dr.roleIds
    })
  }
  else if (data.deptId && data.roleIds) {
    // 兼容旧数据格式
    form.selectedDepts = [data.deptId]
    deptRolesMap[String(data.deptId)] = data.roleIds
  }

  // 将主部门排在第一位（仅在加载数据时排序）
  if (form.deptId && form.selectedDepts && form.selectedDepts.length > 1) {
    const mainDeptId = form.deptId
    const otherDepts = form.selectedDepts.filter(id => String(id) !== String(mainDeptId))
    form.selectedDepts = [mainDeptId, ...otherDepts]
  }

  visible.value = true
}

defineExpose({ onAdd, onUpdate })
</script>

<style scoped lang="scss">
.dept-roles-section {
  margin-top: 20px;
}

:deep(.dept-card) {
  .arco-card-header {
    padding: 12px 16px;
    background-color: var(--color-fill-2);
  }

  .arco-card-body {
    padding: 16px;
  }
}
</style>
