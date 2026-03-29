<template>
  <a-card
    class="general-card"
    title="我的消息"
    :header-style="{ paddingBottom: '0' }"
    :body-style="{ padding: '15px 20px 13px 20px' }"
  >
    <template #extra>
      <a-link @click="openMessageCenter">更多</a-link>
    </template>
    <a-skeleton v-if="loading" :loading="loading" :animation="true">
      <a-skeleton-line :rows="5" />
    </a-skeleton>
    <div v-else>
      <a-empty v-if="dataList.length === 0" description="暂无消息" />
      <div v-else>
        <div v-for="item in dataList" :key="item.id" class="message-item">
          <div class="message-header">
            <a-tag :color="item.type === 1 ? 'blue' : 'orange'" size="small">
              {{ item.type === 1 ? '系统' : '安全' }}
            </a-tag>
            <span class="message-time">{{ formatTime(item.createTime) }}</span>
          </div>
          <a-link class="message-title" @click="onDetail(item.id)">
            {{ item.title }}
          </a-link>
          <div class="message-content">{{ item.content }}</div>
        </div>
      </div>
    </div>
  </a-card>
</template>

<script setup lang="ts">
import { listMessage, type MessageResp } from '@/apis'

const dataList = ref<MessageResp[]>([])
const loading = ref(false)

const getDataList = async () => {
  try {
    loading.value = true
    const res = await listMessage({ page: 1, size: 5, sort: ['createTime,desc'] })
    dataList.value = res.data.list || []
  }
  finally {
    loading.value = false
  }
}

const router = useRouter()

const onDetail = (id: number) => {
  router.push({ path: '/user/message', query: { id } })
}

const openMessageCenter = () => {
  router.push('/user/message')
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

.message-item {
  margin-bottom: 14px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--color-border-2);

  &:last-child {
    margin-bottom: 0;
    padding-bottom: 0;
    border-bottom: none;
  }
}

.message-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6px;
}

.message-time {
  font-size: 12px;
  color: var(--color-text-3);
}

.message-title {
  display: block;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 6px;
  color: var(--color-text-1);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.message-content {
  font-size: 13px;
  line-height: 1.6;
  color: var(--color-text-3);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>
