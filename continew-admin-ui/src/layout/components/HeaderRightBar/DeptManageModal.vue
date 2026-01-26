<template>
  <a-modal
    v-model:visible="visible"
    title="部门管理"
    :width="width >= 700 ? 700 : '100%'"
    :footer="false"
    :mask-closable="true"
    draggable
  >
    <a-spin :loading="loading" style="width: 100%;">
      <div v-if="deptRoles && deptRoles.length > 0" class="dept-list">
        <a-space direction="vertical" :size="16" fill>
          <a-card
            v-for="item in deptRoles"
            :key="item.deptId"
            :title="item.deptName"
            :bordered="true"
            size="small"
            class="dept-card"
            :class="{ 'active-dept': String(item.deptId) === String(currentDeptId) }"
          >
            <template #extra>
              <a-space :size="12">
                <!-- 当前部门标识 -->
                <a-tag v-if="String(item.deptId) === String(currentDeptId)" color="arcoblue">
                  当前部门
                </a-tag>
                <!-- 主部门标识 -->
                <a-tag v-if="String(item.deptId) === String(mainDeptId)" color="green">
                  默认部门
                </a-tag>
                <!-- 操作按钮 -->
                <a-space :size="8">
                  <a-button
                    v-if="String(item.deptId) !== String(currentDeptId)"
                    type="primary"
                    size="mini"
                    @click="handleSwitch(item.deptId)"
                  >
                    切换
                  </a-button>
                  <a-button
                    v-if="String(item.deptId) !== String(mainDeptId)"
                    size="mini"
                    @click="handleSetDefault(item.deptId)"
                  >
                    设为默认
                  </a-button>
                </a-space>
              </a-space>
            </template>
            <!-- 角色列表 -->
            <div class="roles-content">
              <div class="roles-label">角色：</div>
              <a-space :size="8" wrap>
                <a-tag
                  v-for="(roleName, index) in item.roleNames.slice(0, 4)"
                  :key="index"
                  color="blue"
                >
                  {{ roleName }}
                </a-tag>
                <a-tag
                  v-if="item.roleNames.length > 4"
                  color="orange"
                >
                  +{{ item.roleNames.length - 4 }}
                </a-tag>
              </a-space>
            </div>
          </a-card>
        </a-space>
      </div>
      <a-empty v-else description="暂无部门信息" />
    </a-spin>
  </a-modal>
</template>

<script setup lang="ts">
import { Message, Modal } from '@arco-design/web-vue'
import { ref } from 'vue'
import { useWindowSize } from '@vueuse/core'
import { getUserDeptRoles, setDefaultDept, switchDept } from '@/apis'
import { useUserStore } from '@/stores'

defineOptions({ name: 'DeptManageModal' })

const { width } = useWindowSize()
const userStore = useUserStore()

const visible = ref(false)
const loading = ref(false)

// 当前部门ID
const currentDeptId = ref<number>()
// 主部门ID
const mainDeptId = ref<number>()
// 用户部门角色列表
const deptRoles = ref<Array<{
  deptId: number
  deptName: string
  roleNames: string[]
}>>([])

// 打开弹框
const open = async () => {
  visible.value = true
  await loadDeptRoles()
}

// 加载用户部门角色信息
const loadDeptRoles = async () => {
  loading.value = true
  try {
    const { data } = await getUserDeptRoles()
    deptRoles.value = data.deptRoles || []
    currentDeptId.value = data.currentDeptId
    mainDeptId.value = data.mainDeptId
  }
  catch (error) {
    Message.error('加载部门信息失败')
  }
  finally {
    loading.value = false
  }
}

// 切换部门
const handleSwitch = (deptId: number) => {
  Modal.confirm({
    title: '确认切换部门？',
    content: '切换部门后，页面将刷新以加载新部门的数据和权限',
    onOk: async () => {
      try {
        await switchDept({ deptId })
        // 刷新用户信息和路由
        await userStore.getUserInfo()
        await userStore.refreshRoutes()
        Message.success('部门切换成功')
        visible.value = false
        // 刷新页面
        window.location.reload()
      }
      catch (error) {
        Message.error('部门切换失败')
      }
    },
  })
}

// 设置默认部门
const handleSetDefault = (deptId: number) => {
  Modal.confirm({
    title: '确认设置默认部门？',
    content: '设置后，下次登录将自动进入该部门',
    onOk: async () => {
      try {
        await setDefaultDept({ deptId })
        Message.success('设置成功')
        // 重新加载部门信息
        await loadDeptRoles()
        // 更新用户信息
        await userStore.getUserInfo()
      }
      catch (error) {
        Message.error('设置失败')
      }
    },
  })
}

defineExpose({ open })
</script>

<style scoped lang="scss">
.dept-list {
  max-height: 60vh;
  overflow-y: auto;
}

.dept-card {
  transition: all 0.3s;

  :deep(.arco-card-header) {
    background-color: var(--color-fill-2);
    border-bottom: 1px solid var(--color-border-2);
  }

  :deep(.arco-card-body) {
    padding: 12px 16px;
  }

  &.active-dept {
    border-color: rgb(var(--primary-6));
    box-shadow: 0 0 0 1px rgb(var(--primary-6));
  }
}

.roles-content {
  display: flex;
  align-items: flex-start;
  gap: 8px;

  .roles-label {
    flex-shrink: 0;
    color: var(--color-text-2);
    font-weight: 500;
  }
}
</style>
