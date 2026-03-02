<template>
  <div class="tab-container">
    <a-alert v-if="disabled" type="warning" style="margin-bottom: 16px">
      类型已启用，修改流程模板将导致现有表单映射失效，请先禁用类型后再操作。
    </a-alert>
    <a-alert v-else type="info" style="margin-bottom: 16px">
      选择评审流程模板和管理流程模板。更换模板后，已配置的表单映射将被清空，需重新配置。
    </a-alert>

    <a-spin :loading="loading">
      <a-row :gutter="24">
        <!-- 评审流程模板 -->
        <a-col :span="12">
          <a-card title="评审流程模板" :bordered="false" class="process-card">
            <a-select
              v-model="form.reviewTemplateId"
              placeholder="请选择评审流程模板"
              :options="reviewOptions"
              :loading="optionsLoading"
              allow-clear
              style="width: 100%; margin-bottom: 16px"
              :disabled="disabled"
            />
            <!-- 选中模板预览 -->
            <template v-if="selectedReviewTemplate">
              <a-descriptions :column="1" size="small">
                <a-descriptions-item label="模板名称">{{ selectedReviewTemplate.templateName }}</a-descriptions-item>
                <a-descriptions-item label="模板编码">{{ selectedReviewTemplate.templateCode }}</a-descriptions-item>
                <a-descriptions-item label="轮次配置">
                  <span>审核×{{ selectedReviewTemplate.auditRounds }}</span>
                  <a-divider direction="vertical" />
                  <span>评审×{{ selectedReviewTemplate.reviewRounds }}</span>
                  <a-divider direction="vertical" />
                  <span>决策×{{ selectedReviewTemplate.decisionRounds }}</span>
                </a-descriptions-item>
              </a-descriptions>
              <div v-if="selectedReviewTemplate.roundNames?.length" style="margin-top: 8px">
                <a-tag v-for="rn in selectedReviewTemplate.roundNames" :key="`${rn.roundType}-${rn.roundSequence}`" style="margin: 2px">
                  {{ roundTypeLabel(rn.roundType) }}{{ rn.roundSequence }}：{{ rn.roundName }}
                </a-tag>
              </div>
            </template>
            <a-empty v-else-if="!optionsLoading" description="暂未选择评审流程模板" />
          </a-card>
        </a-col>

        <!-- 管理流程模板 -->
        <a-col :span="12">
          <a-card title="管理流程模板" :bordered="false" class="process-card">
            <a-select
              v-model="form.manageTemplateId"
              placeholder="请选择管理流程模板"
              :options="manageOptions"
              :loading="optionsLoading"
              allow-clear
              style="width: 100%; margin-bottom: 16px"
              :disabled="disabled"
            />
            <!-- 选中模板预览 -->
            <template v-if="selectedManageTemplate">
              <a-descriptions :column="1" size="small">
                <a-descriptions-item label="模板名称">{{ selectedManageTemplate.templateName }}</a-descriptions-item>
                <a-descriptions-item label="模板编码">{{ selectedManageTemplate.templateCode }}</a-descriptions-item>
                <a-descriptions-item label="阶段数">{{ selectedManageTemplate.stages?.length ?? 0 }} 个阶段</a-descriptions-item>
              </a-descriptions>
              <div style="margin-top: 8px">
                <a-tag
                  v-for="stage in selectedManageTemplate.stages"
                  :key="stage.stageOrder"
                  :color="stageTypeColor(stage.stageType)"
                  style="margin: 2px"
                >
                  {{ stage.stageOrder }}. {{ stage.stageName }}
                </a-tag>
              </div>
            </template>
            <a-empty v-else-if="!optionsLoading" description="暂未选择管理流程模板" />
          </a-card>
        </a-col>
      </a-row>

      <div style="margin-top: 16px; text-align: right">
        <a-button
          type="primary"
          :loading="saveLoading"
          :disabled="disabled || (!form.reviewTemplateId && !form.manageTemplateId)"
          @click="handleSave"
        >
          保存流程配置
        </a-button>
      </div>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { Message } from '@arco-design/web-vue'
import {
  listEnabledManagementTemplate,
  listEnabledProcessTemplate,
  saveProcess,
  type ManagementTemplateResp,
  type ProcessTemplateResp,
  type TypeProcessConfigResp,
  ProcessType,
} from '@/apis/review'

const props = defineProps<{
  typeId: number
  processConfigs: TypeProcessConfigResp[]
  disabled?: boolean
}>()

const emit = defineEmits<{
  (e: 'saved'): void
}>()

const loading = ref(false)
const optionsLoading = ref(false)
const saveLoading = ref(false)

// 下拉选项数据
const reviewTemplates = ref<ProcessTemplateResp[]>([])
const manageTemplates = ref<ManagementTemplateResp[]>([])

const reviewOptions = computed(() =>
  reviewTemplates.value.map(t => ({ label: t.templateName, value: t.id }))
)
const manageOptions = computed(() =>
  manageTemplates.value.map(t => ({ label: t.templateName, value: t.id }))
)

// 表单
const form = reactive({
  reviewTemplateId: undefined as number | undefined,
  manageTemplateId: undefined as number | undefined,
})

// 选中模板详情
const selectedReviewTemplate = computed(() =>
  reviewTemplates.value.find(t => t.id === form.reviewTemplateId) ?? null
)
const selectedManageTemplate = computed(() =>
  manageTemplates.value.find(t => t.id === form.manageTemplateId) ?? null
)

const roundTypeLabel = (type: string) => ({ AUDIT: '审核', REVIEW: '评审', DECISION: '决策' }[type] ?? type)
const stageTypeColor = (type: string) => ({ KICKOFF: 'blue', EXECUTION: 'green', ACCEPTANCE: 'orange' }[type] ?? 'gray')

/** 加载下拉选项 */
const loadOptions = async () => {
  optionsLoading.value = true
  try {
    const [ptRes, mtRes] = await Promise.all([
      listEnabledProcessTemplate(),
      listEnabledManagementTemplate(),
    ])
    reviewTemplates.value = ptRes.data || []
    manageTemplates.value = mtRes.data || []
  }
  catch (error) {
    console.error('加载模板选项失败:', error)
  }
  finally {
    optionsLoading.value = false
  }
}

/** 从 props 初始化已有选中值 */
const initFromProps = () => {
  const reviewConfig = props.processConfigs.find(c => c.processType === ProcessType.REVIEW)
  const manageConfig = props.processConfigs.find(c => c.processType === ProcessType.MANAGE)
  form.reviewTemplateId = reviewConfig?.templateId
  form.manageTemplateId = manageConfig?.templateId
}

/** 保存 */
const handleSave = async () => {
  if (!form.reviewTemplateId || !form.manageTemplateId) {
    Message.warning('请同时选择评审流程模板和管理流程模板')
    return
  }
  saveLoading.value = true
  try {
    await saveProcess(props.typeId, [
      { processType: ProcessType.REVIEW, templateId: form.reviewTemplateId },
      { processType: ProcessType.MANAGE, templateId: form.manageTemplateId },
    ])
    Message.success('流程配置保存成功')
    emit('saved')
  }
  catch (error) {
    console.error('保存失败:', error)
  }
  finally {
    saveLoading.value = false
  }
}

onMounted(async () => {
  await loadOptions()
  initFromProps()
})

// 当父组件更新 processConfigs 时重新初始化
watch(() => props.processConfigs, initFromProps, { deep: true })
</script>

<style scoped>
.tab-container {
  padding: 16px 0;
}

.process-card {
  min-height: 240px;
}
</style>
