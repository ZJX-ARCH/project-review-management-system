<template>
  <a-drawer v-model:visible="visible" title="用户详情" :width="width >= 600 ? 600 : '100%'" :footer="false">
    <a-descriptions :column="2" size="large" class="general-description">
      <a-descriptions-item label="ID" :span="2">
        <a-typography-paragraph copyable>{{ dataDetail?.id }}</a-typography-paragraph>
      </a-descriptions-item>
      <a-descriptions-item label="用户名">{{ dataDetail?.username }}</a-descriptions-item>
      <a-descriptions-item label="昵称">{{ dataDetail?.nickname }}</a-descriptions-item>
      <a-descriptions-item label="性别">
        <span v-if="dataDetail?.gender === 1">男</span>
        <span v-else-if="dataDetail?.gender === 2">女</span>
        <span v-else>未知</span>
      </a-descriptions-item>
      <a-descriptions-item label="状态">
        <a-tag v-if="dataDetail?.status === 1" color="green">启用</a-tag>
        <a-tag v-else color="red">禁用</a-tag>
      </a-descriptions-item>
      <a-descriptions-item label="手机号">{{ dataDetail?.phone || '暂无' }}</a-descriptions-item>
      <a-descriptions-item label="邮箱">{{ dataDetail?.email || '暂无' }}</a-descriptions-item>
      <a-descriptions-item label="创建人">{{ dataDetail?.createUserString }}</a-descriptions-item>
      <a-descriptions-item label="创建时间">{{ dataDetail?.createTime }}</a-descriptions-item>
      <a-descriptions-item label="修改人">{{ dataDetail?.updateUserString }}</a-descriptions-item>
      <a-descriptions-item label="修改时间">{{ dataDetail?.updateTime }}</a-descriptions-item>
      <a-descriptions-item label="描述" :span="2">{{ dataDetail?.description }}</a-descriptions-item>
    </a-descriptions>

    <!-- 部门角色配置区 -->
    <a-divider orientation="left">部门角色配置</a-divider>
    <div v-if="deptRolesDisplay && deptRolesDisplay.length > 0" class="dept-roles-section">
      <a-space direction="vertical" :size="12" fill>
        <a-card
          v-for="item in deptRolesDisplay"
          :key="item.deptId"
          :title="item.deptName"
          :bordered="true"
          size="small"
          class="dept-card"
        >
          <template #extra>
            <a-tag v-if="String(item.deptId) === String(dataDetail?.deptId)" color="arcoblue">主部门</a-tag>
          </template>
          <GiCellTags :data="item.roleNames" />
        </a-card>
      </a-space>
    </div>
    <a-empty v-else description="暂无部门角色配置" />
  </a-drawer>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useWindowSize } from '@vueuse/core'
import { type UserDetailResp, getUser as getDetail } from '@/apis/system/user'
import { useDept, useRole } from '@/hooks/app'

const { width } = useWindowSize()

const dataId = ref('')
const dataDetail = ref<UserDetailResp>()
const visible = ref(false)

const { deptList, getDeptList } = useDept()
const { roleList, getRoleList } = useRole()

// 部门角色显示数据
const deptRolesDisplay = computed(() => {
  if (!dataDetail.value?.deptRoles || !dataDetail.value.deptRoles.length) {
    return []
  }

  // 创建部门ID到名称的映射
  const deptMap = new Map<string, string>()
  const buildDeptMap = (nodes: any[]) => {
    nodes.forEach((node) => {
      const nodeId = String(node.key || node.value)
      deptMap.set(nodeId, node.title)
      if (node.children && node.children.length > 0) {
        buildDeptMap(node.children)
      }
    })
  }
  buildDeptMap(deptList.value)

  // 创建角色ID到名称的映射
  const roleMap = new Map<number, string>()
  roleList.value.forEach((role: any) => {
    roleMap.set(Number(role.value), role.label)
  })

  // 映射部门角色数据
  const mappedData = dataDetail.value.deptRoles.map((item) => {
    const deptId = item.deptId
    const deptName = deptMap.get(String(deptId)) || `部门${deptId}`
    const roleNames = item.roleIds
      .map(roleId => roleMap.get(Number(roleId)))
      .filter(Boolean) as string[]

    return {
      deptId,
      deptName,
      roleNames,
    }
  })

  // 将主部门排在第一位
  const mainDeptId = dataDetail.value.deptId
  if (mainDeptId && mappedData.length > 1) {
    const mainDeptIndex = mappedData.findIndex(item => String(item.deptId) === String(mainDeptId))
    if (mainDeptIndex > 0) {
      const mainDept = mappedData.splice(mainDeptIndex, 1)[0]
      mappedData.unshift(mainDept)
    }
  }

  return mappedData
})

// 查询详情
const getDataDetail = async () => {
  // 并行加载部门和角色列表
  await Promise.all([
    getDeptList(),
    getRoleList(),
  ])

  const { data } = await getDetail(dataId.value)
  dataDetail.value = data
}

// 打开
const onOpen = async (id: string) => {
  dataId.value = id
  await getDataDetail()
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
