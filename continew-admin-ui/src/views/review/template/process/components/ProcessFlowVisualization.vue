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
          <div class="step-icon">
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
  background: linear-gradient(135deg, #3370ff 0%, #3491fa 100%);
  color: white;
  border-radius: 50%;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(51, 112, 255, 0.3);
}

.empty-flow {
  padding: 40px;
  background: var(--color-fill-1);
  border-radius: 8px;
  text-align: center;
}
</style>
