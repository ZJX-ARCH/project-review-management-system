<template>
  <div v-if="phaseSummary.length > 0" style="display: flex; align-items: center; gap: 4px; flex-wrap: wrap;">
    <span
      v-for="(item, index) in phaseSummary"
      :key="`phase-${item.type}`"
      style="display: contents;"
    >
      <a-tag :color="item.color" size="small">
        {{ item.name }} x{{ item.count }}
      </a-tag>
      <icon-arrow-right
        v-if="index < phaseSummary.length - 1"
        :size="12"
        :style="{ color: 'var(--color-text-3)' }"
      />
    </span>
  </div>
  <span v-else style="color: var(--color-text-3)">暂无配置</span>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { IconArrowRight } from '@arco-design/web-vue/es/icon'
import type { StageResp } from '@/apis/review'
import { StageType } from '@/apis/review'

defineOptions({ name: 'PhasesSummary' })

interface Props {
  phases: StageResp[]
}

const props = withDefaults(defineProps<Props>(), {
  phases: () => [],
})

/** 阶段摘要 - 按类型合并显示 */
const phaseSummary = computed(() => {
  const summary: Array<{ type: StageType, name: string, count: number, color: string, order: number }> = []

  // 统计各类型阶段数量
  const typeCounts = new Map<StageType, number>()
  props.phases.forEach((phase) => {
    typeCounts.set(phase.stageType, (typeCounts.get(phase.stageType) || 0) + 1)
  })

  // 立项阶段
  if (typeCounts.has(StageType.KICKOFF)) {
    summary.push({
      type: StageType.KICKOFF,
      name: '立项',
      count: typeCounts.get(StageType.KICKOFF)!,
      color: 'green',
      order: 1,
    })
  }

  // 执行阶段
  if (typeCounts.has(StageType.EXECUTION)) {
    summary.push({
      type: StageType.EXECUTION,
      name: '执行',
      count: typeCounts.get(StageType.EXECUTION)!,
      color: 'blue',
      order: 2,
    })
  }

  // 验收阶段
  if (typeCounts.has(StageType.ACCEPTANCE)) {
    summary.push({
      type: StageType.ACCEPTANCE,
      name: '验收',
      count: typeCounts.get(StageType.ACCEPTANCE)!,
      color: 'orange',
      order: 3,
    })
  }

  return summary.sort((a, b) => a.order - b.order)
})
</script>
