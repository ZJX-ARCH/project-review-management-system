<template>
  <a-space v-if="roundSummary.length > 0" :size="4" wrap>
    <template v-for="(item, index) in roundSummary">
      <a-tag :key="`tag-${item.type}`" :color="item.color" size="small">
        {{ item.name }} x{{ item.count }}
      </a-tag>
      <icon-arrow-right
        v-if="index < roundSummary.length - 1"
        :key="`arrow-${item.type}`"
        :size="12"
        :style="{ color: 'var(--color-text-3)' }"
      />
    </template>
  </a-space>
  <span v-else style="color: var(--color-text-3)">暂无配置</span>
</template>

<script setup lang="ts">
import { computed } from 'vue'
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

/** 轮次摘要 - 按类型合并显示 */
const roundSummary = computed(() => {
  const summary: Array<{ type: RoundType, name: string, count: number, color: string, order: number }> = []

  // 审核轮次
  if (props.auditRounds > 0) {
    summary.push({
      type: RoundType.AUDIT,
      name: '审核',
      count: props.auditRounds,
      color: 'green',
      order: 1,
    })
  }

  // 评审轮次
  if (props.reviewRounds > 0) {
    summary.push({
      type: RoundType.REVIEW,
      name: '评审',
      count: props.reviewRounds,
      color: 'blue',
      order: 2,
    })
  }

  // 决策轮次
  if (props.decisionRounds > 0) {
    summary.push({
      type: RoundType.DECISION,
      name: '决策',
      count: props.decisionRounds,
      color: 'orange',
      order: 3,
    })
  }

  return summary.sort((a, b) => a.order - b.order)
})
</script>
