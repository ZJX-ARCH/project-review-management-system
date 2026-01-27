<template>
  <a-modal
    v-model:visible="visible"
    title="分配角色"
    :mask-closable="false"
    :esc-to-close="false"
    :width="width >= 1100 ? 1100 : '100%'"
    draggable
    @before-ok="save"
    @close="reset"
  >
    <UserSelect v-if="visible" ref="UserSelectRef" v-model:value="selectedUsers" :role-id="dataId" @select-user="onSelectUser" />

    <!-- 部门选择区域 -->
    <template v-if="usersNeedDeptSelect.length > 0">
      <a-divider>为以下用户选择部门</a-divider>
      <a-form :model="userDeptMap" layout="vertical">
        <a-form-item
          v-for="user in usersNeedDeptSelect"
          :key="user.userId"
          :label="`${user.nickname || user.username} 的部门`"
          :field="user.userId"
          :rules="[{ required: true, message: '请选择部门' }]"
        >
          <a-select
            v-model="userDeptMap[user.userId]"
            placeholder="请选择部门(可多选)"
            allow-search
            multiple
          >
            <a-option
              v-for="dept in userDeptsMap[user.userId]"
              :key="dept.value"
              :value="dept.value"
              :label="dept.label"
            >
              {{ dept.label }}
            </a-option>
          </a-select>
        </a-form-item>
      </a-form>
    </template>
  </a-modal>
</template>

<script setup lang="ts">
import { Message } from '@arco-design/web-vue'
import { useWindowSize } from '@vueuse/core'
import { assignToUsers } from '@/apis/system/role'
import { getUser, listUserDepts, listAssignedDepts } from '@/apis/system/user'
import type { LabelValueState } from '@/types/global'

const emit = defineEmits<{
  (e: 'save-success'): void
}>()

const { width } = useWindowSize()

const dataId = ref('')
const visible = ref(false)
const selectedUsers = ref<string[]>([])

// 用户部门数据
const userDeptsMap = ref<Record<string, LabelValueState[]>>({}) // 用户ID -> 部门列表
const userDeptMap = ref<Record<string, string[]>>({}) // 用户ID -> 选中的部门ID数组(支持多选)

// 需要选择部门的用户列表
interface UserInfo {
  userId: string
  username: string
  nickname?: string
}
const usersNeedDeptSelect = ref<UserInfo[]>([])

// 用户选择回调
const onSelectUser = async (value: string[]) => {
  selectedUsers.value = value

  // 清空之前的部门选择数据
  userDeptsMap.value = {}
  userDeptMap.value = {}
  usersNeedDeptSelect.value = []

  // 查询每个用户的部门列表
  for (const userId of value) {
    try {
      // 并行查询用户部门列表、用户详情、已分配的部门
      const [deptsRes, userRes, assignedDeptsRes] = await Promise.all([
        listUserDepts(userId),
        getUser(userId),
        listAssignedDepts(userId, dataId.value),
      ])

      const allDepts = deptsRes.data || []
      const userInfo = userRes.data
      const assignedDeptIds = assignedDeptsRes.data || []

      // 过滤掉已分配该角色的部门
      const availableDepts = allDepts.filter(dept => !assignedDeptIds.includes(String(dept.value)))

      // 如果没有可用部门，跳过该用户
      if (availableDepts.length === 0) {
        Message.warning(`用户 ${userInfo.nickname || userInfo.username} 在所有部门都已拥有该角色`)
        continue
      }

      userDeptsMap.value[userId] = availableDepts

      // 所有用户都需要手动选择部门，让用户明确看到正在为哪个部门分配角色
      usersNeedDeptSelect.value.push({
        userId,
        username: userInfo.username,
        nickname: userInfo.nickname,
      })
    } catch (error) {
      console.error(`查询用户 ${userId} 的部门列表失败:`, error)
    }
  }
}

const UserSelectRef = ref()
// 重置
const reset = () => {
  dataId.value = ''
  selectedUsers.value = []
  userDeptsMap.value = {}
  userDeptMap.value = {}
  usersNeedDeptSelect.value = []
  UserSelectRef.value?.onClearSelected()
}

// 保存
const save = async () => {
  try {
    const isInvalid = selectedUsers.value.length === 0
    if (isInvalid) {
      Message.warning('请选择用户')
      return false
    }

    // 检查是否所有需要选择部门的用户都已选择
    for (const user of usersNeedDeptSelect.value) {
      if (!userDeptMap.value[user.userId] || userDeptMap.value[user.userId].length === 0) {
        Message.warning(`请为 ${user.nickname || user.username} 选择部门`)
        return false
      }
    }

    // 构建请求数据 - 每个用户的每个部门都创建一条记录
    const userDepts: Array<{ userId: string, deptId?: string }> = []

    for (const userId of selectedUsers.value) {
      const deptIds = userDeptMap.value[userId] || []

      if (deptIds.length > 0) {
        // 用户有部门选择，为每个部门创建一条记录
        deptIds.forEach(deptId => {
          userDepts.push({
            userId,  // 保持字符串格式，避免精度丢失
            deptId,
          })
        })
      } else {
        // 用户没有部门（全局角色）
        userDepts.push({
          userId,  // 保持字符串格式，避免精度丢失
          deptId: undefined,
        })
      }
    }

    await assignToUsers(dataId.value, { userDepts })
    Message.success(`分配成功，共为 ${selectedUsers.value.length} 个用户分配了角色`)
    reset()
    emit('save-success')
    return true
  } catch (error) {
    console.error('保存失败:', error)
    return false
  }
}

// 打开
const onOpen = async (id: string) => {
  dataId.value = id
  selectedUsers.value = []
  visible.value = true
}

defineExpose({ onOpen })
</script>

<style scoped lang="scss">
:deep(.arco-divider-text) {
  font-weight: 500;
  color: var(--color-text-1);
}
</style>
