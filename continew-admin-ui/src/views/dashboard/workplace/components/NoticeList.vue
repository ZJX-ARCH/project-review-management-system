<template>
  <a-card
    class="general-card"
    title="系统公告"
    :header-style="{ paddingBottom: '0' }"
    :body-style="{ padding: '15px 20px 13px 20px' }"
  >
    <template #extra>
      <a-link @click="openNoticeCenter">更多</a-link>
    </template>
    <a-skeleton v-if="loading" :loading="loading" :animation="true">
      <a-skeleton-line :rows="5" />
    </a-skeleton>
    <div v-else>
      <a-empty v-if="dataList.length === 0" description="暂无公告" />
      <div v-else>
        <div v-for="item in dataList" :key="item.id" class="notice-item">
          <div class="notice-header">
            <a-tag v-if="item.isTop" color="red" size="small">置顶</a-tag>
            <span class="notice-time">{{ formatTime(item.createTime) }}</span>
          </div>
          <a-link class="notice-title" @click="onDetail(item.id)">
            {{ item.title }}
          </a-link>
        </div>
      </div>
    </div>
  </a-card>
</template>

<script setup lang="ts">
import { type DashboardNoticeResp, listDashboardNotice } from '@/apis'

const dataList = ref<DashboardNoticeResp[]>([])
const loading = ref(false)

const getDataList = async () => {
  try {
    loading.value = true
    const res = await listDashboardNotice()
    dataList.value = res.data
  }
  finally {
    loading.value = false
  }
}

const router = useRouter()

const onDetail = (id: number) => {
  router.push({ path: '/user/notice', query: { id } })
}

const openNoticeCenter = () => {
  router.push({ path: '/user/message', query: { tab: 'notice' } })
}

const formatTime = (time: string) => {
  if (!time)
    return ''
  return time.replace('T', ' ').substring(0, 16)
}

onMounted(() => {
  getDataList()
})
</script>

<style scoped lang="scss">
.general-card {
  height: 600px;

  :deep(.arco-card-body) {
    height: calc(100% - 57px);
    overflow-y: auto;
  }
}

.notice-item {
  margin-bottom: 14px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--color-border-2);

  &:last-child {
    margin-bottom: 0;
    padding-bottom: 0;
    border-bottom: none;
  }
}

.notice-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.notice-time {
  font-size: 12px;
  color: var(--color-text-3);
}

.notice-title {
  display: block;
  font-size: 14px;
  color: var(--color-text-1);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
