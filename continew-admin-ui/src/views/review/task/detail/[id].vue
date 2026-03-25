<template>
  <GiPageLayout :body-style="{ overflowY: 'auto' }">
    <!-- Task 4: 页头精简，任务类型/节点名/状态内联到 subtitle -->
    <a-page-header
      :title="detail?.projectName ?? '任务详情'"
      @back="router.push({ path: '/review/task', query: { t: Date.now() } })"
    >
      <template #subtitle>
        <a-space v-if="detail" size="small" style="margin-left: 8px;">
          <a-tag>{{ PROJECT_TASK_TYPE_MAP[detail.taskType] ?? detail.taskType }}</a-tag>
          <span style="color: var(--color-text-3); font-size: 13px;">{{ detail.nodeName }}</span>
          <a-tag :color="PROJECT_TASK_STATUS_MAP[detail.taskStatus]?.color ?? 'gray'">
            {{ PROJECT_TASK_STATUS_MAP[detail.taskStatus]?.label ?? detail.taskStatus }}
          </a-tag>
        </a-space>
      </template>
      <template #extra>
        <a-button @click="router.push({ path: '/review/task', query: { t: Date.now() } })">返回列表</a-button>
      </template>
    </a-page-header>

    <a-spin :loading="loading" style="width: 100%;">
      <div v-if="detail">
        <!-- Task 1: 全宽主区域 + 右侧 48px 按钮列 -->
        <div class="task-detail-layout">
          <!-- 主内容区 -->
          <div class="task-main">

            <!-- Task 2: 水平流程进度条 -->
            <a-card v-if="allProgressSteps.length" :body-style="{ padding: '16px 20px' }" style="margin-bottom: 16px;">
              <a-steps :current="currentProgressStep" size="small">
                <a-step
                  v-for="(step, idx) in allProgressSteps"
                  :key="idx"
                  :title="step.title"
                  :status="step.status"
                >
                  <template v-if="step.description" #description>
                    <a-popover v-if="step.clickable" position="bottom">
                      <span class="step-desc-link">{{ step.description }}</span>
                      <template #content>
                        <div class="step-popover">
                          <div>通过：{{ step.passCount }} / {{ step.totalCount }} 人</div>
                          <div v-if="step.averageScore != null">平均分：{{ step.averageScore }}</div>
                          <div>结果：
                            <a-tag :color="step.result === 'PASS' ? 'green' : 'red'" size="small">
                              {{ step.result === 'PASS' ? '通过' : '驳回' }}
                            </a-tag>
                          </div>
                        </div>
                      </template>
                    </a-popover>
                    <span v-else>{{ step.description }}</span>
                  </template>
                </a-step>
              </a-steps>
            </a-card>

            <!-- 任务表单（可填写） -->
            <a-card
              v-if="detail.taskFormTemplate && isEditable"
              title="任务表单"
              style="margin-bottom: 16px;"
            >
              <FormRenderer ref="formRendererRef" v-model="taskFormData" :template="detail.taskFormTemplate" :score-table-required="true" />
            </a-card>

            <!-- 任务表单（已完成，只读） -->
            <a-card
              v-else-if="detail.taskFormTemplate && !isEditable"
              title="任务表单（已提交）"
              style="margin-bottom: 16px;"
            >
              <FormRenderer
                :model-value="detail.savedFormData ?? {}"
                :template="detail.taskFormTemplate"
                readonly
              />
            </a-card>

            <!-- Task 5: 决策区 UI 优化 -->
            <a-card v-if="isEditable" style="margin-bottom: 16px;">
              <template #title>
                <span>提交决策</span>
                <span style="font-size: 12px; color: var(--color-text-3); margin-left: 8px;">
                  分配时间：{{ detail.assignTime }}
                </span>
              </template>

              <!-- 卡片式决策选项（非评分表模式） -->
              <div v-if="!hasScoreTable" class="decision-options">
                <div
                  v-for="opt in decisionOptions"
                  :key="opt.value"
                  class="decision-option"
                  :class="{ selected: submitForm.decision === opt.value }"
                  @click="submitForm.decision = opt.value"
                >
                  <component :is="opt.icon" class="option-icon" />
                  <span>{{ opt.label }}</span>
                </div>
              </div>

              <a-alert v-if="hasScoreTable" type="info" style="margin-bottom: 16px;">
                表单中包含评分表，提交后系统将自动汇总评分并生成决策结果。
              </a-alert>

              <!-- 验收驳回回退阶段 -->
              <a-form-item
                v-if="detail.taskType === 'ACCEPTANCE' && submitForm.decision === 'REJECT'"
                label="驳回回退到阶段"
                required
                style="margin-top: 16px;"
              >
                <a-select v-model="submitForm.rejectBackToStageOrder" placeholder="请选择回退到的阶段" style="width: 280px;">
                  <a-option v-for="stage in detail.allStages" :key="stage.stageOrder" :value="stage.stageOrder">
                    {{ stage.stageName }}
                  </a-option>
                </a-select>
              </a-form-item>

              <div class="decision-actions">
                <a-space>
                  <a-button :loading="saving" @click="onSave">
                    <template #icon><icon-save /></template>
                    暂存
                  </a-button>
                  <a-button @click="openTransferModal">
                    <template #icon><icon-swap /></template>
                    转办
                  </a-button>
                  <a-button type="primary" :loading="submitting" @click="onSubmit">
                    <template #icon><icon-send /></template>
                    提交决策
                  </a-button>
                </a-space>
              </div>
            </a-card>

          </div>

          <!-- Task 1: 右侧参考面板按钮列 -->
          <div class="task-side-panel">
            <div
              v-for="panel in sidePanels"
              :key="panel.key"
              class="side-panel-btn"
              :class="{ active: activePanel === panel.key }"
              @click="togglePanel(panel.key)"
            >
              <component :is="panel.icon" class="panel-btn-icon" />
              <span class="panel-btn-label">{{ panel.label }}</span>
            </div>
          </div>
        </div>
      </div>
    </a-spin>

    <!-- Task 3: 参考面板抽屉 -->
    <a-drawer
      v-model:visible="drawerVisible"
      :title="detail?.projectName"
      :width="drawerWidth"
      :mask="false"
      placement="right"
      @cancel="closeDrawer"
    >
      <!-- 拖拽调宽手柄 -->
      <div class="drawer-resize-handle" @mousedown="onResizeStart" />

      <!-- 使用 ReviewHistoryTimeline 组件 -->
      <ReviewHistoryTimeline :node-history="nodeHistory" :loading="historyLoading" />
    </a-drawer>

    <!-- 转办弹窗 -->
    <a-modal
      v-model:visible="transferVisible"
      title="转办任务"
      width="480px"
      :ok-loading="transferLoading"
      @ok="onTransfer"
      @cancel="transferVisible = false"
    >
      <a-form :model="transferForm" layout="vertical">
        <a-form-item label="转办给" required>
          <a-select
            v-model="transferForm.targetUserId"
            placeholder="请选择转办对象"
            :loading="candidatesLoading"
            style="width: 100%;"
          >
            <a-option
              v-for="u in candidates"
              :key="u.userId"
              :value="u.userId"
            >
              {{ u.nickname }}（{{ u.username }}）
            </a-option>
          </a-select>
        </a-form-item>
        <a-form-item label="转办备注">
          <a-textarea v-model="transferForm.transferRemark" placeholder="请输入转办原因（选填）" :max-length="200" />
        </a-form-item>
      </a-form>
    </a-modal>
  </GiPageLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Message, Modal } from '@arco-design/web-vue'
import {
  IconFile,
  IconHistory,
  IconLayers,
  IconCheckCircle,
  IconCloseCircle,
  IconExclamationCircle,
  IconUndo,
} from '@arco-design/web-vue/es/icon'
import { getTaskDetail, saveTask, submitTask, transferTask, getTaskCandidates, getTaskNodeHistory } from '@/apis/review'
import {
  PROJECT_TASK_TYPE_MAP,
  PROJECT_TASK_STATUS_MAP,
  PROJECT_STAGE_STATUS_MAP,
} from '@/apis/review/type'
import type { TaskDetailResp, TaskCandidateResp, NodeHistoryResp } from '@/apis/review/type'
import FormRenderer from '../../project/components/FormRenderer.vue'
import ReviewHistoryTimeline from '../../project/components/ReviewHistoryTimeline.vue'

defineOptions({ name: 'ReviewTaskDetail' })

const router = useRouter()
const route = useRoute()
const taskId = computed(() => route.params.id as string)

// ——— 数据 ———
const detail = ref<TaskDetailResp | null>(null)
const loading = ref(false)

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getTaskDetail(taskId.value)
    detail.value = res.data
    if (res.data?.savedFormData) {
      taskFormData.value = { ...res.data.savedFormData }
    }
  }
  finally {
    loading.value = false
  }
}

onMounted(loadDetail)

// ——— 状态判断 ———
const isEditable = computed(() =>
  detail.value?.taskStatus === 'PENDING' || detail.value?.taskStatus === 'SAVED',
)

const hasScoreTable = computed(() =>
  detail.value?.taskFormTemplate?.fields?.some(f => f.fieldType === 'SCORE_TABLE'),
)

// ——— 表单数据 ———
const formRendererRef = ref<{ validate: () => Promise<Record<string, string[]> | undefined> }>()
const taskFormData = ref<Record<string, unknown>>({})

const submitForm = reactive({
  decision: '' as string,
  score: undefined as number | undefined,
  rejectBackToStageOrder: undefined as number | undefined,
})

// ——— Task 2: 水平流程进度条 ———
const TASK_TYPE_LABEL: Record<string, string> = {
  AUDIT: '审核', REVIEW: '评审', DECISION: '决策',
  MANAGEMENT: '管理', ACCEPTANCE: '验收',
}

interface ProgressStep {
  title: string
  status: 'wait' | 'process' | 'finish' | 'error'
  description?: string
  clickable?: boolean
  passCount?: number
  totalCount?: number
  averageScore?: number | null
  result?: string
}

const allProgressSteps = computed<ProgressStep[]>(() => {
  if (!detail.value) return []
  const steps: ProgressStep[] = []
  for (const node of (detail.value.previousNodes ?? [])) {
    steps.push({
      title: node.nodeName || `${TASK_TYPE_LABEL[node.nodeType] ?? node.nodeType} 第${node.nodeSequence}轮`,
      status: node.result === 'PASS' ? 'finish' : 'error',
      description: `${node.passCount}/${node.totalCount} 人通过`,
      clickable: true,
      passCount: node.passCount,
      totalCount: node.totalCount,
      averageScore: node.averageScore ?? null,
      result: node.result,
    })
  }
  steps.push({
    title: detail.value.nodeName || `${TASK_TYPE_LABEL[detail.value.taskType] ?? detail.value.taskType}`,
    status: 'process',
    description: '处理中',
  })
  return steps
})

const currentProgressStep = computed(() => allProgressSteps.value.length - 1)

// ——— Task 1: 右侧参考面板 ———
const sidePanels = computed(() => {
  return [
    { key: 'history', label: '查看历史', icon: IconHistory },
  ]
})

// ——— Task 3: 抽屉状态 ———
const drawerVisible = ref(false)
const drawerTab = ref('application')
const activePanel = ref<string | null>(null)
const drawerWidth = ref(560)

// 节点历史数据
const nodeHistory = ref<NodeHistoryResp[]>([])
const historyLoading = ref(false)

async function loadNodeHistory() {
  historyLoading.value = true
  try {
    const res = await getTaskNodeHistory(taskId.value)
    nodeHistory.value = res.data ?? []
  }
  finally {
    historyLoading.value = false
  }
}

function togglePanel(key: string) {
  if (activePanel.value === key && drawerVisible.value) {
    drawerVisible.value = false
    activePanel.value = null
  }
  else {
    activePanel.value = key
    drawerTab.value = key
    drawerVisible.value = true
    // 打开抽屉时加载节点历史
    if (nodeHistory.value.length === 0) {
      loadNodeHistory()
    }
  }
}

function closeDrawer() {
  drawerVisible.value = false
  activePanel.value = null
}

// 拖拽调整抽屉宽度
function onResizeStart(e: MouseEvent) {
  e.preventDefault()
  const startX = e.clientX
  const startWidth = drawerWidth.value

  const onMouseMove = (ev: MouseEvent) => {
    const delta = startX - ev.clientX
    const newWidth = Math.min(Math.max(startWidth + delta, 320), window.innerWidth * 0.8)
    drawerWidth.value = Math.round(newWidth)
  }

  const onMouseUp = () => {
    document.removeEventListener('mousemove', onMouseMove)
    document.removeEventListener('mouseup', onMouseUp)
    document.body.style.cursor = ''
    document.body.style.userSelect = ''
  }

  document.body.style.cursor = 'ew-resize'
  document.body.style.userSelect = 'none'
  document.addEventListener('mousemove', onMouseMove)
  document.addEventListener('mouseup', onMouseUp)
}

// ——— Task 5: 决策选项 ———
const decisionOptions = computed(() => {
  const base = [
    { value: 'PASS', label: '通过', icon: IconCheckCircle },
    { value: 'REJECT', label: '驳回', icon: IconCloseCircle },
  ]
  if (detail.value?.taskType === 'MANAGEMENT') {
    base.push(
      { value: 'UNQUALIFIED', label: '不合格', icon: IconExclamationCircle },
      { value: 'WITHDRAW', label: '撤回', icon: IconUndo },
    )
  }
  return base
})

// ——— 暂存 ———
const saving = ref(false)
const onSave = async () => {
  saving.value = true
  try {
    await saveTask(taskId.value, { formData: taskFormData.value })
    Message.success('已暂存')
  }
  finally {
    saving.value = false
  }
}

// ——— 提交决策 ———
const submitting = ref(false)
const onSubmit = async () => {
  const formErrors = await formRendererRef.value?.validate()
  if (formErrors) return

  if (!hasScoreTable.value && !submitForm.decision) {
    Message.warning('请选择决策结果')
    return
  }
  if (detail.value?.taskType === 'ACCEPTANCE'
    && submitForm.decision === 'REJECT'
    && !submitForm.rejectBackToStageOrder) {
    Message.warning('请选择驳回回退到的阶段')
    return
  }

  if (hasScoreTable.value) {
    submitForm.decision = 'PASS'
  }

  Modal.confirm({
    title: '确认提交决策',
    content: `确定提交决策"${submitForm.decision}"吗？提交后不可修改。`,
    onOk: async () => {
      submitting.value = true
      try {
        await submitTask(taskId.value, {
          decision: submitForm.decision,
          score: submitForm.score,
          rejectBackToStageOrder: submitForm.rejectBackToStageOrder,
          formData: taskFormData.value,
        })
        Message.success('决策提交成功')
        router.replace({ path: '/review/task', query: { t: Date.now() } })
      }
      finally {
        submitting.value = false
      }
    },
  })
}

// ——— 转办 ———
const transferVisible = ref(false)
const transferLoading = ref(false)
const transferForm = reactive({
  targetUserId: undefined as string | undefined,
  transferRemark: '',
})
const candidates = ref<TaskCandidateResp[]>([])
const candidatesLoading = ref(false)

const openTransferModal = async () => {
  transferForm.targetUserId = undefined
  transferForm.transferRemark = ''
  transferVisible.value = true
  candidatesLoading.value = true
  try {
    const res = await getTaskCandidates(taskId.value)
    candidates.value = res.data ?? []
  }
  finally {
    candidatesLoading.value = false
  }
}

const onTransfer = async () => {
  if (!transferForm.targetUserId) {
    Message.warning('请选择转办目标用户')
    return
  }
  transferLoading.value = true
  try {
    await transferTask(taskId.value, {
      targetUserId: transferForm.targetUserId,
      transferRemark: transferForm.transferRemark,
    })
    Message.success('转办成功')
    transferVisible.value = false
    router.replace({ path: '/review/task', query: { t: Date.now() } })
  }
  finally {
    transferLoading.value = false
  }
}
</script>

<style scoped>
/* Task 1: 布局 */
.task-detail-layout {
  display: flex;
  gap: 0;
  align-items: flex-start;
}

.task-main {
  flex: 1;
  min-width: 0;
  padding-right: 8px;
}

.task-side-panel {
  width: 48px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  position: sticky;
  top: 16px;
}

.side-panel-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 10px 4px;
  border-radius: 6px;
  cursor: pointer;
  color: var(--color-text-2);
  background: var(--color-fill-1);
  border: 1px solid var(--color-border-2);
  transition: all 0.2s;
  user-select: none;
}

.side-panel-btn:hover {
  color: rgb(var(--primary-6));
  border-color: rgb(var(--primary-4));
  background: rgb(var(--primary-1));
}

.side-panel-btn.active {
  color: #fff;
  background: rgb(var(--primary-6));
  border-color: rgb(var(--primary-6));
}

.panel-btn-icon {
  font-size: 16px;
}

.panel-btn-label {
  font-size: 11px;
  writing-mode: vertical-rl;
  letter-spacing: 2px;
}

/* Task 2: 进度条 */
.step-desc-link {
  cursor: pointer;
  color: rgb(var(--primary-6));
  text-decoration: underline dotted;
}

.step-popover {
  min-width: 140px;
  line-height: 1.8;
  font-size: 13px;
}

/* Task 3: 抽屉内容 */
.drawer-resize-handle {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 5px;
  cursor: ew-resize;
  z-index: 10;
  transition: background 0.15s;
}

.drawer-resize-handle:hover {
  background: rgb(var(--primary-4));
}

.history-node .node-title {
  font-weight: 500;
}

.history-node .node-stats {
  font-size: 12px;
  color: var(--color-text-3);
  margin-top: 2px;
}

/* Task 5: 决策区 */
.decision-options {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.decision-option {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  border: 2px solid var(--color-border-2);
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  color: var(--color-text-1);
  transition: all 0.2s;
  background: var(--color-bg-1);
}

.decision-option:hover {
  border-color: rgb(var(--primary-4));
  color: rgb(var(--primary-6));
}

.decision-option.selected {
  border-color: rgb(var(--primary-6));
  background: rgb(var(--primary-1));
  color: rgb(var(--primary-6));
  font-weight: 500;
}

.option-icon {
  font-size: 16px;
}

.decision-actions {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>