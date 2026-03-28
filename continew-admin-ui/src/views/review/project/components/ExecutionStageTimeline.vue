<script setup lang="ts">
import { ref } from 'vue'
import type { ProjectStageResp } from '@/apis/review/type'
import { PROJECT_STAGE_STATUS_MAP } from '@/apis/review/type'
import FormRenderer from './FormRenderer.vue'
import {
  IconCheck,
  IconClose,
  IconDown,
  IconClockCircle,
} from '@arco-design/web-vue/es/icon'

defineOptions({ name: 'ExecutionStageTimeline' })

interface Props {
  stages: ProjectStageResp[]
}

const props = defineProps<Props>()
const expandedStages = ref<Set<number>>(new Set())

// 默认展开第一个非 PENDING 且非 REJECTED 的阶段，或最后一个 REJECTED 阶段
const firstActive = props.stages.find(s => s.status !== 'PENDING' && s.status !== 'REJECTED')
  ?? [...props.stages].reverse().find(s => s.status === 'REJECTED')
if (firstActive) {
  expandedStages.value.add(firstActive.stageOrder)
}

function toggleStage(order: number) {
  const s = new Set(expandedStages.value)
  s.has(order) ? s.delete(order) : s.add(order)
  expandedStages.value = s
}

function stageDotClass(stage: ProjectStageResp) {
  if (stage.status === 'COMPLETED') return 'dot-pass'
  if (stage.status === 'REJECTED') return 'dot-reject'
  if (stage.status === 'IN_PROGRESS' || stage.status === 'SUBMITTED') return 'dot-active'
  return 'dot-pending'
}

function canExpand(stage: ProjectStageResp) {
  return stage.status !== 'PENDING' || !!stage.stageFormData
}

function formatTime(t?: string) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 16)
}
</script>

<template>
  <div class="execution-stage-timeline">
    <div
      v-for="(stage, idx) in stages"
      :key="stage.id"
      class="stage-timeline-item"
    >
      <div
        class="stage-node-header"
        :class="{ expanded: expandedStages.has(stage.stageOrder), clickable: canExpand(stage) }"
        @click="canExpand(stage) && toggleStage(stage.stageOrder)"
      >
        <div class="node-step-indicator">
          <div class="node-dot" :class="stageDotClass(stage)">
            <IconCheck v-if="stage.status === 'COMPLETED'" />
            <IconClose v-else-if="stage.status === 'REJECTED'" />
            <IconClockCircle v-else-if="stage.status === 'IN_PROGRESS' || stage.status === 'SUBMITTED'" />
            <span v-else>{{ idx + 1 }}</span>
          </div>
        </div>
        <div class="node-header-content">
          <div class="node-header-top">
            <span class="node-name">{{ stage.stageName }}</span>
            <a-tag
              :color="PROJECT_STAGE_STATUS_MAP[stage.status]?.color ?? 'gray'"
              size="small"
            >
              {{ PROJECT_STAGE_STATUS_MAP[stage.status]?.label ?? stage.status }}
            </a-tag>
            <a-tag v-if="stage.isOverdue" color="red" size="small">已超时</a-tag>
          </div>
          <div class="node-meta">
            <span v-if="stage.startDate">开始：{{ stage.startDate }}</span>
            <span v-if="stage.deadline">截止：{{ stage.deadline }}</span>
            <span v-if="stage.plannedDays">计划 {{ stage.plannedDays }} 天</span>
          </div>
        </div>
        <IconDown
          v-if="canExpand(stage)"
          class="node-expand-icon"
          :class="{ rotated: expandedStages.has(stage.stageOrder) }"
        />
      </div>
      <div
        v-if="expandedStages.has(stage.stageOrder)"
        class="stage-node-content"
      >
        <div v-if="stage.stageFormData && stage.stageFormTemplate" class="stage-form-card">
          <!-- 提交人信息头 -->
          <div v-if="stage.submitterName" class="person-entry-header">
            <a-avatar :size="28" style="background: rgb(var(--primary-6)); font-size: 12px; color: #fff;">
              {{ stage.submitterName?.charAt(0) }}
            </a-avatar>
            <span class="person-name">{{ stage.submitterName }}</span>
            <span class="person-time">{{ formatTime(stage.submitTime) }}</span>
          </div>

          <FormRenderer
            :model-value="stage.stageFormData"
            :template="stage.stageFormTemplate"
            readonly
          />

          <!-- 审核人信息 -->
          <div v-if="stage.reviewerName" class="reviewer-footer">
            <div class="person-entry-header">
              <a-avatar :size="28" style="background: #ff7d00; font-size: 12px; color: #fff;">
                {{ stage.reviewerName?.charAt(0) }}
              </a-avatar>
              <span class="person-name">{{ stage.reviewerName }}</span>
              <a-tag
                v-if="stage.reviewDecision"
                :color="stage.reviewDecision === 'PASS' ? 'green' : 'red'"
                size="small"
              >
                {{ stage.reviewDecision === 'PASS' ? '通过' : stage.reviewDecision === 'REJECT' ? '驳回' : stage.reviewDecision }}
              </a-tag>
              <span class="person-time">{{ formatTime(stage.reviewTime) }}</span>
            </div>
          </div>
        </div>
        <div v-else-if="stage.stageFormData" class="no-template-tip">
          阶段成果已提交（无表单模板）
        </div>
        <div v-else class="no-template-tip">暂无提交成果</div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.execution-stage-timeline {
  padding: 8px 0;
}

.stage-timeline-item {
  position: relative;
  padding-left: 32px;
  margin-bottom: 4px;
}

.stage-node-header {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px 12px 10px 0;
  border-radius: 6px;
  transition: background 0.15s;

  &.clickable {
    cursor: pointer;
    &:hover { background: var(--color-fill-1); }
  }
}

.node-step-indicator {
  position: absolute;
  left: 0;
  top: 10px;
  width: 24px;
  display: flex;
  justify-content: center;
}

.node-dot {
  width: 24px;
  height: 24px;
  min-width: 24px;
  min-height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  border: 2px solid;
  flex-shrink: 0;
  box-sizing: border-box;
  overflow: hidden;

  &.dot-pass {
    background: #e8f7e8;
    border-color: #00b42a;
    color: #00b42a;
  }
  &.dot-reject {
    background: #fff0f0;
    border-color: #f53f3f;
    color: #f53f3f;
  }
  &.dot-active {
    background: rgb(var(--primary-1));
    border-color: rgb(var(--primary-6));
    color: rgb(var(--primary-6));
  }
  &.dot-pending {
    background: var(--color-fill-2);
    border-color: var(--color-border-3);
    color: var(--color-text-3);
  }
}

.node-header-content {
  flex: 1;
  min-width: 0;
}

.node-header-top {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.node-name {
  font-weight: 500;
  font-size: 14px;
}

.node-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: var(--color-text-3);
  margin-top: 3px;
}

.node-expand-icon {
  transition: transform 0.2s;
  color: var(--color-text-3);
  font-size: 14px;
  flex-shrink: 0;
  margin-top: 4px;

  &.rotated { transform: rotate(180deg); }
}

.stage-node-content {
  padding: 12px 0 12px 0;
  border-left: 2px solid var(--color-border-2);
  margin-left: 11px;
  padding-left: 16px;
  margin-bottom: 8px;
}

.stage-form-card {
  padding: 12px;
  border: 1px solid var(--color-border-3);
  border-radius: 6px;
  background: var(--color-bg-2);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.no-template-tip {
  color: var(--color-text-3);
  font-size: 13px;
  padding: 8px 0;
}

.person-entry-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--color-border-2);
  flex-wrap: wrap;
}

.person-name {
  font-weight: 500;
  font-size: 13px;
}

.person-time {
  font-size: 12px;
  color: var(--color-text-3);
  margin-left: auto;
}

.reviewer-footer {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--color-border-2);

  .person-entry-header {
    margin-bottom: 0;
    padding-bottom: 0;
    border-bottom: none;
  }
}
</style>
