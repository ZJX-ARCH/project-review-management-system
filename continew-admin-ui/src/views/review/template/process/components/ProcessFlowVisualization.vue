<template>
  <div v-if="rounds && rounds.length > 0" class="flow-visualization">
    <a-steps :current="rounds.length" direction="horizontal" class="flow-steps">
      <a-step
        v-for="(round, index) in sortedRounds"
        :key="round.id || index"
        :title="round.roundName"
        :description="getRoundDescription(round)"
        :status="getRoundStatus(round.roundType)"
      >
        <template #icon>
          <div class="step-icon" :class="getRoundIconClass(round.roundType)">
            {{ index + 1 }}
          </div>
        </template>
      </a-step>
    </a-steps>
  </div>
  <div v-else class="empty-flow">
    <a-empty description="暂无轮次配置" />
  </div>
</template>

<script setup lang="ts">
import type { RoundNameResp } from '@/apis/review'
import { RoundType } from '@/apis/review'

defineOptions({ name: 'ProcessFlowVisualization' })

interface Props {
  rounds: RoundNameResp[]
}

const props = withDefaults(defineProps<Props>(), {
  rounds: () => [],
})

/** 排序后的轮次（按roundType和roundSequence排序） */
const sortedRounds = computed(() => {
  if (!props.rounds || props.rounds.length === 0)
    return []

  return [...props.rounds].sort((a, b) => {
    // 先按类型排序：AUDIT < REVIEW < DECISION
    const typeOrder = { [RoundType.AUDIT]: 1, [RoundType.REVIEW]: 2, [RoundType.DECISION]: 3 }
    const typeCompare = typeOrder[a.roundType] - typeOrder[b.roundType]
    if (typeCompare !== 0)
      return typeCompare

    // 同类型内按序号排序
    return a.roundSequence - b.roundSequence
  })
})

/** 获取轮次描述 */
const getRoundDescription = (round: RoundNameResp): string => {
  const typeMap = {
    [RoundType.AUDIT]: '审核',
    [RoundType.REVIEW]: '评审',
    [RoundType.DECISION]: '决策',
  }
  return `${typeMap[round.roundType]} - 第${round.roundSequence}轮`
}

/** 获取轮次状态（用于控制颜色） */
const getRoundStatus = (type: RoundType): string => {
  const statusMap = {
    [RoundType.AUDIT]: 'process',
    [RoundType.REVIEW]: 'process',
    [RoundType.DECISION]: 'finish',
  }
  return statusMap[type] || 'process'
}

/** 获取图标样式类 */
const getRoundIconClass = (type: RoundType): string => {
  const classMap = {
    [RoundType.AUDIT]: 'icon-green',
    [RoundType.REVIEW]: 'icon-blue',
    [RoundType.DECISION]: 'icon-orange',
  }
  return classMap[type] || 'icon-blue'
}
</script>

<style scoped>
.flow-visualization {
  padding: 20px;
}

.flow-steps {
  padding: 40px 20px;
  background: linear-gradient(90deg, #f0f7ff 0%, #e8f4ff 100%);
  border-radius: 8px;
}

/* 完全清除 Arco Steps 默认图标样式 - 使用更激进的策略 */
:deep(.arco-steps-item-icon),
:deep(.arco-steps-icon) {
  all: unset !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  width: auto !important;
  height: auto !important;
  padding: 0 !important;
  margin: 0 !important;
  background: none !important;
  border: none !important;
  box-shadow: none !important;
  overflow: visible !important;
}

/* 清除所有伪元素 */
:deep(.arco-steps-item-icon::before),
:deep(.arco-steps-item-icon::after),
:deep(.arco-steps-icon::before),
:deep(.arco-steps-icon::after) {
  content: none !important;
  display: none !important;
}

/* 隐藏所有默认的内部图标元素 */
:deep(.arco-steps-item-icon .arco-icon),
:deep(.arco-steps-icon .arco-icon),
:deep(.arco-steps-item-icon > *:not(.step-icon)),
:deep(.arco-steps-icon > *:not(.step-icon)) {
  display: none !important;
  visibility: hidden !important;
}

/* 覆盖所有状态下的默认样式 */
:deep(.arco-steps-item-process .arco-steps-item-icon),
:deep(.arco-steps-item-process .arco-steps-icon),
:deep(.arco-steps-item-finish .arco-steps-item-icon),
:deep(.arco-steps-item-finish .arco-steps-icon),
:deep(.arco-steps-item-wait .arco-steps-item-icon),
:deep(.arco-steps-item-wait .arco-steps-icon),
:deep(.arco-steps-item-error .arco-steps-item-icon),
:deep(.arco-steps-item-error .arco-steps-icon) {
  all: unset !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  background: none !important;
  border: none !important;
  color: inherit !important;
}

:deep(.arco-steps-item-title) {
  font-weight: 600;
  font-size: 14px;
}

:deep(.arco-steps-item-description) {
  font-size: 12px;
  color: var(--color-text-3);
}

.step-icon {
  width: 32px;
  height: 32px;
  line-height: 32px;
  text-align: center;
  color: white;
  border-radius: 50%;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(51, 112, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 1;
}

/* 审核阶段 - 绿色 */
.step-icon.icon-green {
  background: linear-gradient(135deg, #00b42a 0%, #23c343 100%);
  box-shadow: 0 2px 8px rgba(0, 180, 42, 0.3);
}

/* 评审阶段 - 蓝色 */
.step-icon.icon-blue {
  background: linear-gradient(135deg, #3370ff 0%, #3491fa 100%);
  box-shadow: 0 2px 8px rgba(51, 112, 255, 0.3);
}

/* 决策阶段 - 橙色 */
.step-icon.icon-orange {
  background: linear-gradient(135deg, #ff7d00 0%, #ff9a2e 100%);
  box-shadow: 0 2px 8px rgba(255, 125, 0, 0.3);
}

.empty-flow {
  padding: 40px;
  background: var(--color-fill-1);
  border-radius: 8px;
  text-align: center;
}
</style>
