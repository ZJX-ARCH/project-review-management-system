<template>
  <a-card class="phase-config-card" :bordered="true">
    <template #title>
      <a-space>
        <a-tag color="arcoblue" size="small">阶段{{ phase.stageOrder }}</a-tag>
        <span>{{ phase.stageName || '未命名阶段' }}</span>
        <a-tag :color="getPhaseColor(phase.stageType)" size="small">
          {{ getPhaseTypeName(phase.stageType) }}
        </a-tag>
      </a-space>
    </template>
    <template #extra>
      <a-space>
        <a-button v-if="canMoveUp" size="small" @click="$emit('move-up')">
          <template #icon><icon-arrow-up /></template>
        </a-button>
        <a-button v-if="canMoveDown" size="small" @click="$emit('move-down')">
          <template #icon><icon-arrow-down /></template>
        </a-button>
        <a-button v-if="canDelete" size="small" status="danger" @click="handleDelete">
          <template #icon><icon-delete /></template>
        </a-button>
      </a-space>
    </template>

    <a-form :model="localPhase" layout="vertical">
      <a-form-item label="阶段名称" required>
        <a-input
          v-model="localPhase.stageName"
          placeholder="请输入阶段名称"
          @change="handleUpdate"
        />
      </a-form-item>
    </a-form>
  </a-card>
</template>

<script setup lang="ts">
import { IconArrowUp, IconArrowDown, IconDelete } from '@arco-design/web-vue/es/icon'
import { Modal } from '@arco-design/web-vue'
import type { StageReq } from '@/apis/review'
import { StageType } from '@/apis/review'

defineOptions({ name: 'PhaseConfigCard' })

interface Props {
  phase: StageReq
  isFirst?: boolean
  isLast?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  isFirst: false,
  isLast: false,
})

const emit = defineEmits<{
  (e: 'update', value: StageReq): void
  (e: 'move-up'): void
  (e: 'move-down'): void
  (e: 'delete'): void
}>()

const localPhase = ref<StageReq>({ ...props.phase })

watch(() => props.phase, (newVal) => {
  localPhase.value = { ...newVal }
}, { deep: true })

/** 是否可以上移 */
const canMoveUp = computed(() => {
  // 立项阶段不能上移（第一个）
  if (props.phase.stageType === StageType.KICKOFF)
    return false
  // 执行阶段如果在第二个位置（立项后面）不能上移
  if (props.isFirst)
    return false
  return true
})

/** 是否可以下移 */
const canMoveDown = computed(() => {
  // 验收阶段不能下移（最后一个）
  if (props.phase.stageType === StageType.ACCEPTANCE)
    return false
  // 执行阶段如果在倒数第二个位置（验收前面）不能下移
  if (props.isLast)
    return false
  return true
})

/** 是否可以删除 */
const canDelete = computed(() => {
  // 立项和验收阶段不能删除
  return props.phase.stageType === StageType.EXECUTION
})

/** 获取阶段类型名称 */
const getPhaseTypeName = (type: StageType): string => {
  const typeMap = {
    [StageType.KICKOFF]: '立项',
    [StageType.EXECUTION]: '执行',
    [StageType.ACCEPTANCE]: '验收',
  }
  return typeMap[type] || type
}

/** 获取阶段颜色 */
const getPhaseColor = (type: StageType): string => {
  const colorMap = {
    [StageType.KICKOFF]: 'green',
    [StageType.EXECUTION]: 'blue',
    [StageType.ACCEPTANCE]: 'orange',
  }
  return colorMap[type] || 'gray'
}

const handleUpdate = () => {
  emit('update', { ...localPhase.value })
}

const handleDelete = () => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除阶段"${localPhase.value.stageName || '未命名阶段'}"吗？`,
    onOk: () => {
      emit('delete')
    },
  })
}
</script>

<style scoped>
.phase-config-card {
  margin-bottom: 16px;
  border: 1px solid var(--color-border-2);
  transition: all 0.3s;
}

.phase-config-card:hover {
  border-color: var(--color-primary-light-3);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.form-tip {
  font-size: 12px;
  color: var(--color-text-3);
}
</style>
