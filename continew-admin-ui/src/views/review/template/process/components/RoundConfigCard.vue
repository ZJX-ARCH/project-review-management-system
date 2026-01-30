<template>
  <a-card class="round-config-card" :bordered="true">
    <template #title>
      <a-space>
        <span>{{ round.roundName || '未命名轮次' }}</span>
        <a-tag :color="getRoundColor(round.roundType)" size="small">
          {{ getRoundTypeName(round.roundType) }}
        </a-tag>
      </a-space>
    </template>
    <template #extra>
      <a-space>
        <a-button v-if="!isFirst" size="small" @click="$emit('move-up')">
          <template #icon><icon-arrow-up /></template>
        </a-button>
        <a-button v-if="!isLast" size="small" @click="$emit('move-down')">
          <template #icon><icon-arrow-down /></template>
        </a-button>
        <a-button size="small" status="danger" @click="handleDelete">
          <template #icon><icon-delete /></template>
        </a-button>
      </a-space>
    </template>

    <a-form :model="localRound" layout="vertical">
      <a-form-item label="轮次名称" required>
        <a-input
          v-model="localRound.roundName"
          placeholder="请输入轮次名称"
          @change="handleUpdate"
        />
      </a-form-item>
    </a-form>
  </a-card>
</template>

<script setup lang="ts">
import { IconArrowUp, IconArrowDown, IconDelete } from '@arco-design/web-vue/es/icon'
import { Modal } from '@arco-design/web-vue'
import type { RoundNameReq } from '@/apis/review'
import { RoundType } from '@/apis/review'

defineOptions({ name: 'RoundConfigCard' })

interface Props {
  round: RoundNameReq
  isFirst?: boolean
  isLast?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  isFirst: false,
  isLast: false,
})

const emit = defineEmits<{
  (e: 'update', value: RoundNameReq): void
  (e: 'move-up'): void
  (e: 'move-down'): void
  (e: 'delete'): void
}>()

const localRound = ref<RoundNameReq>({ ...props.round })

watch(() => props.round, (newVal) => {
  localRound.value = { ...newVal }
}, { deep: true })

/** 获取轮次类型名称 */
const getRoundTypeName = (type: RoundType): string => {
  const typeMap = {
    [RoundType.AUDIT]: '审核',
    [RoundType.REVIEW]: '评审',
    [RoundType.DECISION]: '决策',
  }
  return typeMap[type] || type
}

/** 获取轮次颜色 */
const getRoundColor = (type: RoundType): string => {
  const colorMap = {
    [RoundType.AUDIT]: 'green',
    [RoundType.REVIEW]: 'blue',
    [RoundType.DECISION]: 'orange',
  }
  return colorMap[type] || 'gray'
}

const handleUpdate = () => {
  emit('update', { ...localRound.value })
}

const handleDelete = () => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除轮次"${localRound.value.roundName || '未命名轮次'}"吗？`,
    onOk: () => {
      emit('delete')
    },
  })
}
</script>

<style scoped>
.round-config-card {
  margin-bottom: 16px;
  border: 1px solid var(--color-border-2);
  transition: all 0.3s;
}

.round-config-card:hover {
  border-color: var(--color-primary-light-3);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}
</style>
