<template>
  <GiPageLayout :body-style="{ overflowY: 'auto' }">
    <a-page-header
      :title="detail?.projectName ?? '项目详情'"
      @back="router.push({ path: '/review/project', query: { t: Date.now() } })"
    >
      <template #extra>
        <a-space>
          <a-button
            v-if="canRevoke"
            v-permission="['review:project:revoke']"
            status="warning"
            :loading="revoking"
            @click="onRevoke"
          >撤销申请</a-button>
          <a-button
            v-if="canUpdateForm"
            v-permission="['review:project:update']"
            @click="openUpdateForm"
          >有痕修改</a-button>
          <a-button
            v-if="canTerminate"
            v-permission="['review:project:terminate']"
            status="danger"
            :loading="terminating"
            @click="onTerminate"
          >强制终止</a-button>
          <a-button @click="router.push({ path: '/review/project', query: { t: Date.now() } })">返回列表</a-button>
        </a-space>
      </template>
    </a-page-header>

    <a-spin :loading="loading" style="width: 100%;">
      <div v-if="detail">

        <!-- 状态卡片 -->
        <a-card style="margin-bottom: 16px;">
          <a-descriptions :column="3" size="medium">
            <a-descriptions-item label="项目状态">
              <a-tag :color="PROJECT_STATUS_MAP[detail.status]?.color ?? 'gray'">
                {{ PROJECT_STATUS_MAP[detail.status]?.label ?? detail.status }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="项目类型">{{ detail.typeName }}</a-descriptions-item>
            <a-descriptions-item label="提交时间">{{ detail.submittedTime ?? '—' }}</a-descriptions-item>
          </a-descriptions>
        </a-card>

        <!-- 评审阶段进度（有快照就显示） -->
        <a-card v-if="hasReviewProgress" title="评审流程进度" style="margin-bottom: 16px;">
          <a-steps size="small">
            <a-step
              v-for="(node, idx) in detail.reviewProgress"
              :key="idx"
              :title="node.nodeName || `${TASK_TYPE_LABEL[node.nodeType] ?? node.nodeType} 第${node.nodeSequence}轮`"
              :status="nodeStatusToStep(node.nodeStatus)"
            >
              <template #description>
                <!-- 已完成节点：显示通过/驳回结果，hover 弹详情 -->
                <template v-if="node.nodeResult">
                  <a-popover position="bottom" trigger="hover">
                    <span class="node-result-link">
                      <a-tag :color="node.nodeResult === 'PASS' ? 'green' : 'red'" size="small">
                        {{ node.nodeResult === 'PASS' ? '通过' : '驳回' }}
                      </a-tag>
                    </span>
                    <template #content>
                      <div class="node-result-popover">
                        <div>通过：{{ node.passCount }} / {{ node.totalCount }} 人</div>
                        <div v-if="node.averageScore != null">平均分：{{ node.averageScore }}</div>
                      </div>
                    </template>
                  </a-popover>
                </template>
                <!-- 进行中节点：显示已完成人数进度 -->
                <template v-else-if="node.nodeStatus === 'ACTIVE' && node.requiredCount">
                  <span class="node-progress-text">
                    已完成 {{ node.completedCount ?? 0 }}/{{ node.requiredCount }} 人
                  </span>
                </template>
              </template>
            </a-step>
          </a-steps>
        </a-card>

        <!-- 基本信息 -->
        <a-card title="基本信息" style="margin-bottom: 16px;">
          <a-descriptions :column="2">
            <a-descriptions-item label="项目名称">{{ detail.projectName }}</a-descriptions-item>
            <a-descriptions-item label="项目类型">{{ detail.typeName }}</a-descriptions-item>
            <a-descriptions-item label="项目描述" :span="2">{{ detail.description ?? '—' }}</a-descriptions-item>
          </a-descriptions>
        </a-card>

        <!-- 评审历史（申请人视角） -->
        <a-card v-if="reviewHistory.length" title="评审历史" style="margin-bottom: 16px;">
          <ReviewHistoryTimeline :node-history="reviewHistory" :loading="reviewHistoryLoading" />
        </a-card>

        <!-- 执行阶段进度（执行阶段或已归档时显示） -->
        <a-card
          v-if="(isExecutionPhase || isArchived) && detail.stages?.length"
          title="执行阶段进度"
          style="margin-bottom: 16px;"
        >
          <ExecutionStageTimeline :stages="detail.stages" />
        </a-card>
      </div>
    </a-spin>

    <!-- 有痕修改弹窗 -->
    <a-modal
      v-model:visible="updateFormVisible"
      title="有痕修改申请表单"
      width="700px"
      :ok-loading="updateFormLoading"
      @ok="onSubmitUpdateForm"
      @cancel="updateFormVisible = false"
    >
      <a-form :model="updateFormData" layout="vertical">
        <a-form-item label="修改原因" required>
          <a-textarea v-model="updateFormData.modifyReason" placeholder="请说明本次修改的原因" :max-length="200" />
        </a-form-item>
        <a-form-item label="申请表单">
          <FormRenderer
            v-if="detail?.applicationFormTemplate"
            v-model="updateFormData.formData"
            :template="detail.applicationFormTemplate"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </GiPageLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Message, Modal } from '@arco-design/web-vue'
import { getProjectDetail, revokeProject, terminateProject, updateProjectForm, getProjectReviewHistory } from '@/apis/review'
import { PROJECT_STATUS_MAP, PROJECT_STAGE_STATUS_MAP } from '@/apis/review/type'
import type { ProjectDetailResp, ProjectStageResp, NodeHistoryResp } from '@/apis/review/type'
import has from '@/utils/has'
import FormRenderer from '../components/FormRenderer.vue'
import ReviewHistoryTimeline from '../components/ReviewHistoryTimeline.vue'
import ExecutionStageTimeline from '../components/ExecutionStageTimeline.vue'

defineOptions({ name: 'ReviewProjectDetail' })

const router = useRouter()
const route = useRoute()
// 保持字符串传输，避免雪花 ID 超出 JS 安全整数范围导致精度丢失
const projectId = computed(() => route.params.id as string)

// ——— 数据 ———
const detail = ref<ProjectDetailResp | null>(null)
const loading = ref(false)
const revoking = ref(false)
const terminating = ref(false)
const reviewHistory = ref<NodeHistoryResp[]>([])
const reviewHistoryLoading = ref(false)

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getProjectDetail(projectId.value)
    detail.value = res.data
  }
  finally {
    loading.value = false
  }
}

const loadReviewHistory = async () => {
  reviewHistoryLoading.value = true
  try {
    const res = await getProjectReviewHistory(projectId.value)
    reviewHistory.value = res.data ?? []
  }
  finally {
    reviewHistoryLoading.value = false
  }
}

onMounted(() => {
  loadDetail()
  loadReviewHistory()
})

// ——— 状态判断 ———
const isReviewPhase = computed(() => {
  const s = detail.value?.status ?? 0
  return s >= 2 && s < 40
})
const hasReviewProgress = computed(() => (detail.value?.reviewProgress?.length ?? 0) > 0)
const isExecutionPhase = computed(() => {
  const s = detail.value?.status ?? 0
  return s >= 50 && s < 90
})
const isArchived = computed(() => {
  const s = detail.value?.status ?? 0
  return [90, 91, 92, 99].includes(s)
})
const canRevoke = computed(() => {
  const s = detail.value?.status ?? 0
  return s >= 2 && s < 40
})
const canUpdateForm = computed(() => {
  const s = detail.value?.status ?? 0
  return s >= 2 && s < 40
})
const canTerminate = computed(() => {
  const s = detail.value?.status ?? 0
  const notArchived = s !== 0 && ![40, 90, 91, 92, 99].includes(s)
  return notArchived && has.hasPerm('review:project:terminate')
})

// ——— 进度条辅助 ———
const TASK_TYPE_LABEL: Record<string, string> = {
  AUDIT: '审核', REVIEW: '评审', DECISION: '决策',
  MANAGEMENT: '管理', ACCEPTANCE: '验收',
}

function nodeStatusToStep(status: string): 'wait' | 'process' | 'finish' | 'error' {
  if (status === 'COMPLETED') return 'finish'
  if (status === 'ACTIVE') return 'process'
  if (status === 'REJECTED') return 'error'
  return 'wait'
}

function stageStatusToStep(status: string): 'wait' | 'process' | 'finish' | 'error' {
  if (status === 'COMPLETED') return 'finish'
  if (status === 'REJECTED') return 'error'
  if (status === 'IN_PROGRESS' || status === 'SUBMITTED') return 'process'
  return 'wait'
}

// ——— 操作 ———
const onRevoke = () => {
  Modal.confirm({
    title: '确认撤销申请',
    content: '确定要撤销本项目的申请吗？撤销后项目将回到草稿状态。',
    okButtonProps: { status: 'warning' },
    onOk: async () => {
      revoking.value = true
      try {
        await revokeProject(projectId.value)
        Message.success('撤销成功')
        await loadDetail()
      }
      finally {
        revoking.value = false
      }
    },
  })
}

const onTerminate = () => {
  Modal.confirm({
    title: '确认强制终止',
    content: '确定要强制终止本项目吗？终止后无法恢复！',
    okButtonProps: { status: 'danger' },
    onOk: async () => {
      terminating.value = true
      try {
        await terminateProject(projectId.value)
        Message.success('项目已终止')
        await loadDetail()
      }
      finally {
        terminating.value = false
      }
    },
  })
}

// ——— 有痕修改 ———
const updateFormVisible = ref(false)
const updateFormLoading = ref(false)
const updateFormData = reactive({
  modifyReason: '',
  formData: {} as Record<string, unknown>,
})

const openUpdateForm = () => {
  updateFormData.modifyReason = ''
  updateFormData.formData = { ...(detail.value?.applicationFormData ?? {}) }
  updateFormVisible.value = true
}

const onSubmitUpdateForm = async () => {
  if (!updateFormData.modifyReason.trim()) {
    Message.warning('请填写修改原因')
    return
  }
  updateFormLoading.value = true
  try {
    await updateProjectForm(projectId.value, {
      formData: updateFormData.formData,
      modifyReason: updateFormData.modifyReason,
    })
    Message.success('修改成功')
    updateFormVisible.value = false
    await loadDetail()
  }
  finally {
    updateFormLoading.value = false
  }
}

</script>

<style scoped>
.stage-item {
  border: 1px solid var(--color-border-2);
  border-radius: 4px;
  padding: 12px 16px;
  margin-bottom: 12px;
}

.stage-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.stage-name {
  font-weight: 500;
  font-size: 14px;
}

.stage-meta {
  display: flex;
  gap: 16px;
  color: var(--color-text-3);
  font-size: 12px;
}

.no-template-tip {
  color: var(--color-text-3);
  text-align: center;
  padding: 20px 0;
}

.node-result-link {
  cursor: default;
}

.node-result-popover {
  min-width: 130px;
  line-height: 1.8;
  font-size: 13px;
}

.node-progress-text {
  font-size: 12px;
  color: var(--color-text-3);
}
</style>
