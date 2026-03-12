<template>
  <GiPageLayout :body-style="{ overflowY: 'auto' }">
    <a-page-header
      :title="detail?.projectName ?? '任务详情'"
      @back="router.push({ path: '/review/task', query: { t: Date.now() } })"
    >
      <template #subtitle>
        <a-tag v-if="detail" style="margin-left: 4px;">
          {{ PROJECT_TASK_TYPE_MAP[detail.taskType] ?? detail.taskType }}
        </a-tag>
      </template>
      <template #extra>
        <a-button @click="router.push({ path: '/review/task', query: { t: Date.now() } })">返回列表</a-button>
      </template>
    </a-page-header>

    <a-spin :loading="loading" style="width: 100%;">
      <div v-if="detail">
        <a-row :gutter="16">
          <!-- 左栏：任务处理区 -->
          <a-col :span="16">

            <!-- 任务信息卡片 -->
            <a-card title="任务信息" style="margin-bottom: 16px;">
              <a-descriptions :column="3" size="medium">
                <a-descriptions-item label="任务类型">
                  <a-tag>{{ PROJECT_TASK_TYPE_MAP[detail.taskType] ?? detail.taskType }}</a-tag>
                </a-descriptions-item>
                <a-descriptions-item label="节点名称">{{ detail.nodeName }}</a-descriptions-item>
                <a-descriptions-item label="任务状态">
                  <a-tag :color="PROJECT_TASK_STATUS_MAP[detail.taskStatus]?.color ?? 'gray'">
                    {{ PROJECT_TASK_STATUS_MAP[detail.taskStatus]?.label ?? detail.taskStatus }}
                  </a-tag>
                </a-descriptions-item>
                <a-descriptions-item label="分配时间">{{ detail.assignTime }}</a-descriptions-item>
              </a-descriptions>
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

            <!-- 决策区（可操作时展示） -->
            <a-card v-if="isEditable" title="提交决策" style="margin-bottom: 16px;">
              <a-form :model="submitForm" layout="vertical">
                <a-form-item v-if="!hasScoreTable" label="决策结果" required>
                  <a-radio-group v-model="submitForm.decision">
                    <a-radio value="PASS">
                      <a-tag color="green">通过</a-tag>
                    </a-radio>
                    <a-radio value="REJECT">
                      <a-tag color="red">驳回</a-tag>
                    </a-radio>
                    <template v-if="detail.taskType === 'MANAGEMENT'">
                      <a-radio value="UNQUALIFIED">
                        <a-tag color="orange">不合格</a-tag>
                      </a-radio>
                      <a-radio value="WITHDRAW">
                        <a-tag color="gray">撤回</a-tag>
                      </a-radio>
                    </template>
                  </a-radio-group>
                </a-form-item>

                <a-alert
                  v-if="hasScoreTable"
                  type="info"
                  style="margin-bottom: 16px;"
                >
                  表单中包含评分表，提交后系统将自动汇总评分并生成决策结果。
                </a-alert>

                <a-form-item
                  v-if="detail.taskType === 'ACCEPTANCE' && submitForm.decision === 'REJECT'"
                  label="驳回回退到阶段"
                  required
                >
                  <a-select
                    v-model="submitForm.rejectBackToStageOrder"
                    placeholder="请选择回退到的阶段"
                    style="width: 280px;"
                  >
                    <a-option
                      v-for="stage in detail.allStages"
                      :key="stage.stageOrder"
                      :value="stage.stageOrder"
                    >{{ stage.stageName }}</a-option>
                  </a-select>
                </a-form-item>
              </a-form>

              <div class="decision-actions">
                <a-space>
                  <a-button :loading="saving" @click="onSave">暂存</a-button>
                  <a-button @click="transferVisible = true">转办</a-button>
                  <a-button type="primary" :loading="submitting" @click="onSubmit">提交决策</a-button>
                </a-space>
              </div>
            </a-card>

          </a-col>

          <!-- 右栏：项目信息 + 历史节点 -->
          <a-col :span="8">

            <a-card title="项目信息" style="margin-bottom: 16px;">
              <a-descriptions :column="1" size="small">
                <a-descriptions-item label="项目名称">{{ detail.projectName }}</a-descriptions-item>
                <a-descriptions-item label="申请人">{{ detail.applicantName }}</a-descriptions-item>
                <a-descriptions-item label="提交时间">{{ detail.submittedTime ?? '—' }}</a-descriptions-item>
                <a-descriptions-item v-if="detail.description" label="项目描述">{{ detail.description }}</a-descriptions-item>
              </a-descriptions>
            </a-card>

            <a-card
              v-if="detail.applicationFormTemplate"
              title="申请表单"
              :body-style="{ padding: '12px' }"
              style="margin-bottom: 16px;"
            >
              <FormRenderer
                :model-value="detail.applicationFormData ?? {}"
                :template="detail.applicationFormTemplate"
                readonly
              />
            </a-card>

            <a-card v-if="detail.previousNodes?.length" title="历史节点" style="margin-bottom: 16px;">
              <a-timeline>
                <a-timeline-item
                  v-for="node in detail.previousNodes"
                  :key="`${node.nodeType}_${node.nodeSequence}`"
                  :color="node.result === 'PASS' ? 'green' : 'red'"
                >
                  <template #dot>
                    <icon-check-circle v-if="node.result === 'PASS'" style="color: green;" />
                    <icon-close-circle v-else style="color: red;" />
                  </template>
                  <div class="history-node">
                    <div class="node-title">{{ node.nodeName }}</div>
                    <div class="node-stats">
                      通过 {{ node.passCount }} / 共 {{ node.totalCount }} 人
                      <span v-if="node.averageScore != null">均分：{{ node.averageScore }}</span>
                    </div>
                  </div>
                </a-timeline-item>
              </a-timeline>
            </a-card>

            <a-card
              v-if="detail.currentStage && (detail.taskType === 'MANAGEMENT' || detail.taskType === 'ACCEPTANCE')"
              title="当前阶段"
              style="margin-bottom: 16px;"
            >
              <a-descriptions :column="1" size="small">
                <a-descriptions-item label="阶段名称">{{ detail.currentStage.stageName }}</a-descriptions-item>
                <a-descriptions-item label="阶段状态">
                  <a-tag :color="PROJECT_STAGE_STATUS_MAP[detail.currentStage.status]?.color ?? 'gray'">
                    {{ PROJECT_STAGE_STATUS_MAP[detail.currentStage.status]?.label ?? detail.currentStage.status }}
                  </a-tag>
                </a-descriptions-item>
                <a-descriptions-item v-if="detail.currentStage.deadline" label="截止日期">
                  {{ detail.currentStage.deadline }}
                  <a-tag v-if="detail.currentStage.isOverdue" color="red" size="small">已超时</a-tag>
                </a-descriptions-item>
              </a-descriptions>
            </a-card>

          </a-col>
        </a-row>
      </div>
    </a-spin>

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
        <a-form-item label="转办给（用户ID）" required>
          <a-input-number
            v-model="transferForm.targetUserId"
            placeholder="请输入目标用户ID"
            style="width: 100%;"
          />
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
import { getTaskDetail, saveTask, submitTask, transferTask } from '@/apis/review'
import {
  PROJECT_TASK_TYPE_MAP,
  PROJECT_TASK_STATUS_MAP,
  PROJECT_STAGE_STATUS_MAP,
} from '@/apis/review/type'
import type { TaskDetailResp } from '@/apis/review/type'
import FormRenderer from '../../project/components/FormRenderer.vue'

defineOptions({ name: 'ReviewTaskDetail' })

const router = useRouter()
const route = useRoute()
// 保持字符串传输，避免雪花 ID 超出 JS 安全整数范围导致精度丢失
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
  // 校验表单必填字段（含评分表）
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
  targetUserId: undefined as number | undefined,
  transferRemark: '',
})

const onTransfer = async () => {
  if (!transferForm.targetUserId) {
    Message.warning('请输入转办目标用户ID')
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
.decision-actions {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.history-node .node-title {
  font-weight: 500;
}

.history-node .node-stats {
  font-size: 12px;
  color: var(--color-text-3);
  margin-top: 2px;
}
</style>
