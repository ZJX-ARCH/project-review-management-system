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
            v-model="deptRolesMap[deptId]"
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
  deptId: number
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
const deptRolesMap = reactive<Record<number, number[]>>({})

// 扁平化的部门列表
const flatDeptList = computed(() => {
  const flatten = (nodes: TreeNodeData[]): any[] => {
    const result: any[] = []
    nodes.forEach((node) => {
      result.push({
        label: node.title,
        value: node.value,
      })
      if (node.children && node.children.length > 0) {
        result.push(...flatten(node.children))
      }
    })
    return result
  }
  return flatten(deptList.value)
})

// 获取部门名称
const getDeptName = (deptId: number) => {
  const dept = flatDeptList.value.find(d => d.value === deptId)
  return dept ? dept.label : `部门 ${deptId}`
}

// 处理部门变更
const handleDeptChange = (value: number[]) => {
  // 移除未选中部门的角色配置
  Object.keys(deptRolesMap).forEach((key) => {
    if (!value.includes(Number(key))) {
      delete deptRolesMap[Number(key)]
    }
  })

  // 如果当前主部门被移除，选择第一个部门作为主部门
  if (form.deptId && !value.includes(form.deptId)) {
    form.deptId = value.length > 0 ? value[0] : undefined
  }

  // 如果还没有主部门且有部门被选中，默认第一个为主部门
  if (!form.deptId && value.length > 0) {
    form.deptId = value[0]
  }
}

const [form, resetForm] = useResetReactive({
  gender: 1 as Gender,
  status: 1 as Status,
  deptId: undefined as number | undefined,
  selectedDepts: [] as number[],
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
    type: 'select',
    span: 24,
    required: true,
    props: {
      options: flatDeptList,
      multiple: true,
      allowClear: true,
      allowSearch: true,
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
  Object.keys(deptRolesMap).forEach(key => delete deptRolesMap[Number(key)])
}

// 保存
const save = async () => {
  const rawPassword = form.password
  try {
    const isInvalid = await formRef.value?.formRef?.validate()
    if (isInvalid)
      return false

    // 检查是否所有部门都配置了角色
    const hasEmptyRoles = selectedDepts.value.some(deptId => !deptRolesMap[deptId] || deptRolesMap[deptId].length === 0)
    if (hasEmptyRoles) {
      Message.error('请为所有部门配置角色')
      return false
    }

    // 构建deptRoles数组
    const deptRoles: DeptRoleItem[] = selectedDepts.value.map(deptId => ({
      deptId,
      roleIds: deptRolesMap[deptId] || [],
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
      deptRolesMap[dr.deptId] = dr.roleIds
    })
  }
  else if (data.deptId && data.roleIds) {
    // 兼容旧数据格式
    form.selectedDepts = [data.deptId]
    deptRolesMap[data.deptId] = data.roleIds
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
