<template>
  <a-dropdown trigger="hover" @select="handleDeptSwitch">
    <a-button size="mini" class="gi_hover_btn dept-switcher">
      <template #icon>
        <icon-apps :size="18" />
      </template>
      <span v-if="!['xs', 'sm'].includes(breakpoint)" class="dept-name">
        {{ currentDeptName }}
      </span>
    </a-button>
    <template #content>
      <a-doption v-for="dept in optionalDepts" :key="dept.value" :value="dept.value">
        <icon-check v-if="isCurrentDept(dept.value)" />
        <span :class="{ 'current-dept': isCurrentDept(dept.value) }">{{ dept.label }}</span>
      </a-doption>
    </template>
  </a-dropdown>
</template>

<script setup lang="ts">
import { Message } from '@arco-design/web-vue'
import { computed } from 'vue'
import type { DeptOption } from '@/apis'
import { switchDept } from '@/apis'
import { useBreakpoint } from '@/hooks'
import { useUserStore } from '@/stores'

defineOptions({ name: 'DeptSwitcher' })

const { breakpoint } = useBreakpoint()
const userStore = useUserStore()

// 当前部门名称
const currentDeptName = computed(() => userStore.deptName || '暂无部门')

// 可选部门列表
const optionalDepts = computed(() => userStore.optionalDepts || [])

// 判断是否为当前部门
const isCurrentDept = (deptId: number) => {
  return optionalDepts.value.find(d => d.value === deptId)?.label === currentDeptName.value
}

// 切换部门
const handleDeptSwitch = async (deptId: string | number | Record<string, any> | undefined) => {
  if (typeof deptId !== 'number')
    return

  // 如果选择的是当前部门，不做处理
  if (isCurrentDept(deptId))
    return

  try {
    await switchDept({ deptId })
    // 刷新用户信息和路由
    await userStore.getUserInfo()
    await userStore.refreshRoutes()
    Message.success('部门切换成功')
    // 刷新页面以加载新部门的数据
    window.location.reload()
  }
  catch (error) {
    Message.error('部门切换失败')
  }
}
</script>

<style scoped lang="scss">
.dept-switcher {
  .dept-name {
    margin-left: 6px;
    max-width: 100px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.current-dept {
  font-weight: 600;
  color: rgb(var(--primary-6));
}

.arco-icon-check {
  margin-right: 4px;
  color: rgb(var(--primary-6));
}
</style>
