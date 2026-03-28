<script setup lang="ts">
import { ref, computed } from 'vue'
import type { NodeHistoryResp } from '@/apis/review/type'
import FormRenderer from './FormRenderer.vue'
import {
  IconCheck,
  IconClose,
  IconFile,
  IconDown,
} from '@arco-design/web-vue/es/icon'

defineOptions({ name: 'ReviewHistoryTimeline' })

interface Props {
  nodeHistory: NodeHistoryResp[]
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
})

const expandedNodes = ref<Set<string>>(new Set())

// 分离评审阶段和管理阶段（只显示已完成的节点）
const reviewPhaseNodes = computed(() =>
  props.nodeHistory.filter(n =>
    ['APPLICATION', 'AUDIT', 'REVIEW', 'DECISION'].includes(n.nodeType) &&
    (n.nodeType === 'APPLICATION' || n.nodeResult != null)
  )
)

const managementPhaseNodes = computed(() =>
  props.nodeHistory.filter(n =>
    ['MANAGEMENT', 'ACCEPTANCE', 'STAGE_SUBMISSION'].includes(n.nodeType) &&
    n.nodeResult != null
  )
)

function toggleNode(key: string) {
  const newSet = new Set(expandedNodes.value)
  if (newSet.has(key)) {
    newSet.delete(key)
  }
  else {
    newSet.add(key)
  }
  expandedNodes.value = newSet
}

function nodeDotClass(node: NodeHistoryResp) {
  if (node.nodeType === 'APPLICATION')
    return 'dot-application'
  if (node.nodeResult === 'PASS')
    return 'dot-pass'
  if (node.nodeResult === 'REJECT')
    return 'dot-reject'
  return 'dot-pending'
}

function formatTime(t?: string) {
  if (!t)
    return ''
  return t.replace('T', ' ').substring(0, 16)
}
</script>

<template>
  <a-spin v-if="loading" style="width:100%; padding: 40px 0; text-align:center;" />

  <div v-else class="history-timeline">
    <!-- 评审阶段 -->
    <template v-if="reviewPhaseNodes.length">
      <div class="phase-section-title">评审阶段</div>
      <div
        v-for="(node, idx) in reviewPhaseNodes"
        :key="`review_${node.nodeType}_${node.nodeSequence}`"
        class="history-node-item"
      >
        <!-- 节点头部 -->
        <div
          class="history-node-header"
          :class="{ expanded: expandedNodes.has(`${node.nodeType}_${node.nodeSequence}`) }"
          @click="toggleNode(`${node.nodeType}_${node.nodeSequence}`)"
        >
          <div class="node-step-indicator">
            <div class="node-dot" :class="nodeDotClass(node)">
              <IconCheck v-if="node.nodeResult === 'PASS'" />
              <IconClose v-else-if="node.nodeResult === 'REJECT'" />
              <IconFile v-else-if="node.nodeType === 'APPLICATION'" />
              <span v-else>{{ idx }}</span>
            </div>
          </div>
          <div class="node-header-content">
            <div class="node-header-top">
              <span class="node-name">{{ node.nodeName }}</span>
              <a-tag
                v-if="node.nodeResult"
                :color="node.nodeResult === 'PASS' ? 'green' : 'red'"
                size="small"
              >
                {{ node.nodeResult === 'PASS' ? '通过' : '驳回' }}
              </a-tag>
              <span v-if="node.passCount != null" class="node-count-text">
                {{ node.passCount }}/{{ node.totalCount }} 人通过
              </span>
            </div>
            <div v-if="node.averageScore != null" class="node-avg-score">
              平均分：{{ node.averageScore }}
            </div>
          </div>
          <IconDown
            class="node-expand-icon"
            :class="{ rotated: expandedNodes.has(`${node.nodeType}_${node.nodeSequence}`) }"
          />
        </div>

        <!-- 展开内容 -->
        <div
          v-if="expandedNodes.has(`${node.nodeType}_${node.nodeSequence}`)"
          class="history-node-entries"
        >
          <a-tabs v-if="node.entries.length > 1" type="card" size="small">
            <a-tab-pane
              v-for="(entry, ei) in node.entries"
              :key="ei"
              :title="`${entry.assigneeName}${entry.decision ? ` - ${entry.decision === 'PASS' ? '通过' : '驳回'}` : ''}`"
            >
              <div class="person-entry-content">
                <div class="person-entry-meta">
                  <span v-if="entry.score != null" class="person-score">{{ entry.score }} 分</span>
                  <span class="person-time">{{ formatTime(entry.completeTime) }}</span>
                </div>
                <div v-if="entry.formTemplate && entry.formData" class="person-form">
                  <FormRenderer :model-value="entry.formData" :template="entry.formTemplate" readonly />
                </div>
              </div>
            </a-tab-pane>
          </a-tabs>
          <div v-else-if="node.entries.length === 1" class="person-entry">
            <div class="person-entry-header">
              <a-avatar :size="28" style="background: rgb(var(--primary-6)); font-size: 12px; color: #fff;">
                {{ node.entries[0].assigneeName?.charAt(0) }}
              </a-avatar>
              <span class="person-name">{{ node.entries[0].assigneeName }}</span>
              <a-tag
                v-if="node.entries[0].decision"
                :color="node.entries[0].decision === 'PASS' ? 'green' : 'red'"
                size="small"
              >
                {{ node.entries[0].decision === 'PASS' ? '通过' : '驳回' }}
              </a-tag>
              <span v-if="node.entries[0].score != null" class="person-score">{{ node.entries[0].score }} 分</span>
              <span class="person-time">{{ formatTime(node.entries[0].completeTime) }}</span>
            </div>
            <div v-if="node.entries[0].formTemplate && node.entries[0].formData" class="person-form">
              <FormRenderer :model-value="node.entries[0].formData" :template="node.entries[0].formTemplate" readonly />
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- 管理阶段 -->
    <template v-if="managementPhaseNodes.length">
      <div class="phase-section-title" :class="{ 'has-top-border': reviewPhaseNodes.length > 0 }">管理阶段</div>
      <div
        v-for="(node, idx) in managementPhaseNodes"
        :key="`mgmt_${node.nodeType}_${node.nodeSequence}`"
        class="history-node-item"
      >
        <div
          class="history-node-header"
          :class="{ expanded: expandedNodes.has(`${node.nodeType}_${node.nodeSequence}`) }"
          @click="toggleNode(`${node.nodeType}_${node.nodeSequence}`)"
        >
          <div class="node-step-indicator">
            <div class="node-dot" :class="nodeDotClass(node)">
              <IconCheck v-if="node.nodeResult === 'PASS'" />
              <IconClose v-else-if="node.nodeResult === 'REJECT'" />
              <span v-else>{{ idx + 1 }}</span>
            </div>
          </div>
          <div class="node-header-content">
            <div class="node-header-top">
              <span class="node-name">{{ node.nodeName }}</span>
              <a-tag
                v-if="node.nodeResult"
                :color="node.nodeResult === 'PASS' ? 'green' : 'red'"
                size="small"
              >
                {{ node.nodeResult === 'PASS' ? '通过' : '驳回' }}
              </a-tag>
              <span v-if="node.passCount != null" class="node-count-text">
                {{ node.passCount }}/{{ node.totalCount }} 人通过
              </span>
            </div>
            <div v-if="node.averageScore != null" class="node-avg-score">
              平均分：{{ node.averageScore }}
            </div>
          </div>
          <IconDown
            class="node-expand-icon"
            :class="{ rotated: expandedNodes.has(`${node.nodeType}_${node.nodeSequence}`) }"
          />
        </div>

        <div
          v-if="expandedNodes.has(`${node.nodeType}_${node.nodeSequence}`)"
          class="history-node-entries"
        >
          <a-tabs v-if="node.entries.length > 1" type="card" size="small">
            <a-tab-pane
              v-for="(entry, ei) in node.entries"
              :key="ei"
              :title="`${entry.assigneeName}${entry.decision ? ` - ${entry.decision === 'PASS' ? '通过' : '驳回'}` : ''}`"
            >
              <div class="person-entry-content">
                <div class="person-entry-meta">
                  <span v-if="entry.score != null" class="person-score">{{ entry.score }} 分</span>
                  <span class="person-time">{{ formatTime(entry.completeTime) }}</span>
                </div>
                <div v-if="entry.formTemplate && entry.formData" class="person-form">
                  <FormRenderer :model-value="entry.formData" :template="entry.formTemplate" readonly />
                </div>
              </div>
            </a-tab-pane>
          </a-tabs>
          <div v-else-if="node.entries.length === 1" class="person-entry">
            <div class="person-entry-header">
              <a-avatar :size="28" style="background: rgb(var(--primary-6)); font-size: 12px; color: #fff;">
                {{ node.entries[0].assigneeName?.charAt(0) }}
              </a-avatar>
              <span class="person-name">{{ node.entries[0].assigneeName }}</span>
              <a-tag
                v-if="node.entries[0].decision"
                :color="node.entries[0].decision === 'PASS' ? 'green' : 'red'"
                size="small"
              >
                {{ node.entries[0].decision === 'PASS' ? '通过' : '驳回' }}
              </a-tag>
              <span v-if="node.entries[0].score != null" class="person-score">{{ node.entries[0].score }} 分</span>
              <span class="person-time">{{ formatTime(node.entries[0].completeTime) }}</span>
            </div>
            <div v-if="node.entries[0].formTemplate && node.entries[0].formData" class="person-form">
              <FormRenderer :model-value="node.entries[0].formData" :template="node.entries[0].formTemplate" readonly />
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- 无数据 -->
    <a-empty v-if="!reviewPhaseNodes.length && !managementPhaseNodes.length" description="暂无历史记录" />
  </div>
</template>

<style scoped lang="scss">
.history-timeline {
  padding: 8px 0;
}

.phase-section-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-1);
  padding: 12px 8px 8px;
  margin-bottom: 8px;
  border-bottom: 2px solid var(--color-border-2);

  &.has-top-border {
    margin-top: 24px;
    padding-top: 24px;
    border-top: 3px solid var(--color-border-3);
  }
}

.history-node-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 2px solid var(--color-border-3);

  &:last-child {
    border-bottom: none;
    margin-bottom: 0;
    padding-bottom: 0;
  }
}

.history-node-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 10px 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.15s;

  &:hover {
    background: var(--color-fill-2);
  }
}

.node-step-indicator {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex-shrink: 0;
  width: 28px;
}

.node-dot {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  color: #fff;

  &.dot-application { background: #00b42a; }
  &.dot-pass        { background: #00b42a; }
  &.dot-reject      { background: #f53f3f; }
  &.dot-pending     { background: var(--color-fill-4); color: var(--color-text-3); }
}

.node-line {
  width: 2px;
  flex: 1;
  min-height: 16px;
  background: var(--color-border-2);
  margin: 4px 0;
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

.node-count-text {
  font-size: 12px;
  color: var(--color-text-3);
}

.node-avg-score {
  font-size: 12px;
  color: var(--color-text-3);
  margin-top: 2px;
}

.node-expand-icon {
  flex-shrink: 0;
  color: var(--color-text-3);
  transition: transform 0.2s;
  margin-top: 6px;

  &.rotated { transform: rotate(180deg); }
}

.history-node-entries {
  margin-left: 40px;
  border-left: 2px solid var(--color-border-2);
  padding-left: 16px;
  margin-bottom: 8px;
}

.person-entry-content {
  padding: 12px 0;
}

.person-entry-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--color-border-2);
}

.person-entry {
  margin-bottom: 16px;
  padding: 12px;
  border: 1px solid var(--color-border-3);
  border-radius: 6px;
  background: var(--color-bg-2);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.person-entry-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.person-name {
  font-weight: 500;
  font-size: 13px;
}

.person-score {
  font-size: 12px;
  color: var(--color-primary-6);
  font-weight: 500;
}

.person-time {
  font-size: 12px;
  color: var(--color-text-3);
  margin-left: auto;
}

.person-form {
  border-top: 1px solid var(--color-border-2);
  padding-top: 10px;
}
</style>
