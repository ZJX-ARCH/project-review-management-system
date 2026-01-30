<template>
  <a-space v-if="phases && phases.length > 0" :size="4">
    <template v-for="(phase, index) in sortedPhases" :key="phase.id || index">
      <a-tag :color="getPhaseColor(phase.stageType)" size="small">
        {{ getPhaseTypeName(phase.stageType) }}
      </a-tag>
      <icon-arrow-right v-if="index < sortedPhases.length - 1" :size="12" :style="{ color: 'var(--color-text-3)' }" />
    </template>
  </a-space>
  <span v-else style="color: var(--color-text-3)">暂无配置</span>
</template>

<script setup lang="ts">
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

/** 排序后的阶段 */
const sortedPhases = computed(() => {
  return [...props.phases].sort((a, b) => a.stageOrder - b.stageOrder)
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
</script>
