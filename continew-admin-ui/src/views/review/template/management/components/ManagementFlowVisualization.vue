<template>
  <div v-if="phases && phases.length > 0" class="flow-visualization">
    <!-- 阶段过多时显示提示 -->
    <a-alert v-if="sortedPhases.length > 6" type="info" :closable="false" style="margin-bottom: 16px;">
      阶段较多，可左右滚动查看完整流程
    </a-alert>

    <div class="steps-wrapper" :class="{ 'many-phases': sortedPhases.length > 6 }">
      <a-steps :current="sortedPhases.length" direction="horizontal" class="flow-steps">
        <a-step
          v-for="(phase, index) in sortedPhases"
          :key="phase.id || index"
          :title="phase.stageName"
          :description="getPhaseDescription(phase)"
          :status="getPhaseStatus(phase)"
        >
          <template #icon>
            <div class="step-icon" :class="getPhaseIconClass(phase)">
              <icon-check-circle v-if="phase.isRequired" />
              <icon-minus-circle v-else />
            </div>
          </template>
        </a-step>
      </a-steps>
    </div>
  </div>
  <div v-else class="empty-flow">
    <a-empty description="暂无阶段配置" />
  </div>
</template>

<script setup lang="ts">
import { IconCheckCircle, IconMinusCircle } from '@arco-design/web-vue/es/icon'
import type { StageResp } from '@/apis/review'
import { StageType } from '@/apis/review'

defineOptions({ name: 'ManagementFlowVisualization' })

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

/** 获取阶段描述 */
const getPhaseDescription = (phase: StageResp): string => {
  const typeMap = {
    [StageType.KICKOFF]: '立项',
    [StageType.EXECUTION]: '执行',
    [StageType.ACCEPTANCE]: '验收',
  }
  const typeName = typeMap[phase.stageType] || phase.stageType
  const requiredText = phase.isRequired ? '必须' : '可选'
  return `${typeName} - ${requiredText}`
}

/** 获取阶段状态（控制步骤条颜色） */
const getPhaseStatus = (phase: StageResp): string => {
  // 根据阶段类型返回不同状态
  if (phase.stageType === StageType.ACCEPTANCE) {
    return 'finish' // 橙色
  }
  return 'process' // 蓝色
}

/** 获取图标样式类 */
const getPhaseIconClass = (phase: StageResp): string => {
  const classes = []

  // 根据阶段类型添加颜色类
  if (phase.stageType === StageType.KICKOFF) {
    classes.push('icon-green')
  }
  else if (phase.stageType === StageType.EXECUTION) {
    classes.push('icon-blue')
  }
  else if (phase.stageType === StageType.ACCEPTANCE) {
    classes.push('icon-orange')
  }

  // 可选阶段使用灰色
  if (!phase.isRequired) {
    classes.push('icon-gray')
  }

  return classes.join(' ')
}
</script>

<style scoped>
.flow-visualization {
  padding: 20px;
}

.steps-wrapper {
  padding: 40px 20px;
  background: linear-gradient(90deg, #f0f7ff 0%, #e8f4ff 100%);
  border-radius: 8px;
  overflow-x: auto;
  overflow-y: hidden;
}

/* 阶段过多时使用横向滚动 */
.steps-wrapper.many-phases {
  overflow-x: auto;
}

.steps-wrapper.many-phases .flow-steps {
  min-width: max-content;
}

.flow-steps {
  width: 100%;
}

/* 隐藏 Arco Steps 默认图标样式 */
:deep(.arco-steps-item-icon) {
  background: transparent !important;
  border: none !important;
}

:deep(.arco-steps-item-icon .arco-icon) {
  display: none;
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

/* 立项阶段 - 绿色 */
.step-icon.icon-green {
  background: linear-gradient(135deg, #00b42a 0%, #23c343 100%);
  box-shadow: 0 2px 8px rgba(0, 180, 42, 0.3);
}

/* 执行阶段 - 蓝色 */
.step-icon.icon-blue {
  background: linear-gradient(135deg, #3370ff 0%, #3491fa 100%);
  box-shadow: 0 2px 8px rgba(51, 112, 255, 0.3);
}

/* 验收阶段 - 橙色 */
.step-icon.icon-orange {
  background: linear-gradient(135deg, #ff7d00 0%, #ff9a2e 100%);
  box-shadow: 0 2px 8px rgba(255, 125, 0, 0.3);
}

/* 可选阶段 - 灰色 */
.step-icon.icon-gray {
  background: linear-gradient(135deg, #86909c 0%, #a9aeb8 100%);
  box-shadow: 0 2px 8px rgba(134, 144, 156, 0.3);
}

.empty-flow {
  padding: 40px;
  background: var(--color-fill-1);
  border-radius: 8px;
  text-align: center;
}

/* 滚动条美化 */
.steps-wrapper::-webkit-scrollbar {
  height: 8px;
}

.steps-wrapper::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.06);
  border-radius: 4px;
}

.steps-wrapper::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 4px;
}

.steps-wrapper::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.3);
}
</style>
