<template>
  <a-space v-if="rounds && rounds.length > 0" :size="4">
    <template v-for="(round, index) in rounds" :key="round.id || index">
      <a-tag :color="getRoundColor(round.roundType)" size="small">
        {{ getRoundTypeName(round.roundType) }}(×{{ getRoundCount(round.roundType) }})
      </a-tag>
      <icon-arrow-right v-if="index < rounds.length - 1" :size="12" :style="{ color: 'var(--color-text-3)' }" />
    </template>
  </a-space>
  <span v-else style="color: var(--color-text-3)">暂无配置</span>
</template>

<script setup lang="ts">
import { IconArrowRight } from '@arco-design/web-vue/es/icon'
import type { RoundNameResp } from '@/apis/review'
import { RoundType } from '@/apis/review'

defineOptions({ name: 'RoundsSummary' })

interface Props {
  rounds: RoundNameResp[]
  auditRounds?: number
  reviewRounds?: number
  decisionRounds?: number
}

const props = withDefaults(defineProps<Props>(), {
  rounds: () => [],
  auditRounds: 0,
  reviewRounds: 0,
  decisionRounds: 0,
})

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

/** 获取轮次数量 */
const getRoundCount = (type: RoundType): number => {
  switch (type) {
    case RoundType.AUDIT:
      return props.auditRounds
    case RoundType.REVIEW:
      return props.reviewRounds
    case RoundType.DECISION:
      return props.decisionRounds
    default:
      return 0
  }
}
</script>
