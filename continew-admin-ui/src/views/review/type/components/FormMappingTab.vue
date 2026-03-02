<template>
  <div class="tab-container">
    <a-alert v-if="!reviewTemplate && !manageTemplate" type="warning" style="margin-bottom: 16px">
      请先在【流程模板】标签页中选择并保存流程模板，才能配置表单映射。
    </a-alert>
    <a-alert v-else-if="disabled" type="warning" style="margin-bottom: 16px">
      类型已启用，如需修改请先禁用类型。
    </a-alert>
    <a-alert v-else type="info" style="margin-bottom: 16px">
      为每个流程节点选择对应的表单模板。表单类型需与节点类型匹配。
    </a-alert>

    <a-spin :loading="loading">
      <!-- 评审流程节点映射 -->
      <a-card
        v-if="reviewTemplate"
        title="评审流程节点"
        :bordered="false"
        style="margin-bottom: 16px"
      >
        <a-table :data="reviewNodes" :pagination="false" :bordered="false">
          <template #columns>
            <a-table-column title="节点" data-index="label" :width="200" />
            <a-table-column title="节点类型" data-index="nodeType" :width="120">
              <template #cell="{ record }">
                <a-tag :color="nodeTypeColor(record.nodeType)">{{ nodeTypeLabel(record.nodeType) }}</a-tag>
              </template>
            </a-table-column>
            <a-table-column title="表单模板" data-index="formTemplateId">
              <template #cell="{ record }">
                <a-select
                  v-model="formSelections[record.key]"
                  :placeholder="`请选择${templateTypeLabel(record.templateType)}表单模板`"
                  :options="formOptions[record.templateType] || []"
                  :loading="optionsLoading"
                  :disabled="disabled"
                  allow-clear
                  style="width: 100%"
                />
              </template>
            </a-table-column>
          </template>
        </a-table>
      </a-card>

      <!-- 管理流程节点映射 -->
      <a-card
        v-if="manageTemplate"
        title="管理流程节点"
        :bordered="false"
        style="margin-bottom: 16px"
      >
        <a-table :data="manageNodes" :pagination="false" :bordered="false">
          <template #columns>
            <a-table-column title="节点" data-index="label" :width="200" />
            <a-table-column title="节点类型" data-index="stageType" :width="120">
              <template #cell="{ record }">
                <a-tag :color="stageTypeColor(record.stageType)">{{ stageTypeLabel(record.stageType) }}</a-tag>
              </template>
            </a-table-column>
            <a-table-column title="表单模板" data-index="formTemplateId">
              <template #cell="{ record }">
                <a-select
                  v-model="formSelections[record.key]"
                  :placeholder="`请选择${templateTypeLabel(record.templateType)}表单模板`"
                  :options="formOptions[record.templateType] || []"
                  :loading="optionsLoading"
                  :disabled="disabled"
                  allow-clear
                  style="width: 100%"
                />
              </template>
            </a-table-column>
          </template>
        </a-table>
      </a-card>

      <div v-if="reviewTemplate || manageTemplate" style="text-align: right">
        <a-button type="primary" :loading="saveLoading" :disabled="disabled" @click="handleSave">
          保存表单映射
        </a-button>
      </div>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { Message } from '@arco-design/web-vue'
import {
  listEnabledFormTemplate,
  saveFormMapping,
  type FormTemplateResp,
  type ManagementTemplateResp,
  type ProcessTemplateResp,
  type TypeFormMappingResp,
  TemplateType,
} from '@/apis/review'

interface NodeRow {
  key: string
  label: string
  mappingType: string
  nodeType: string
  nodeSequence?: number
  templateType: number
  stageType?: string
  formTemplateId?: number
}

const props = defineProps<{
  typeId: number
  formMappings: TypeFormMappingResp[]
  reviewTemplate: ProcessTemplateResp | null
  manageTemplate: ManagementTemplateResp | null
  disabled?: boolean
}>()

const emit = defineEmits<{
  (e: 'saved'): void
}>()

const loading = ref(false)
const optionsLoading = ref(false)
const saveLoading = ref(false)

// 各类型表单下拉选项
const formOptions = ref<Record<number, { label: string; value: number }[]>>({})

// 各节点的表单模板选择（key -> formTemplateId），独立 reactive 保证视图响应
const formSelections = reactive<Record<string, number | undefined>>({})

/** 生成评审流程节点列表 */
const reviewNodes = computed<NodeRow[]>(() => {
  if (!props.reviewTemplate) return []
  const nodes: NodeRow[] = []
  const t = props.reviewTemplate

  // APPLICATION 申请节点（类型1）
  nodes.push({
    key: 'REVIEW_APPLICATION',
    label: '申请节点',
    mappingType: 'REVIEW',
    nodeType: 'APPLICATION',
    nodeSequence: undefined,
    templateType: TemplateType.APPLICATION,
    formTemplateId: undefined,
  })

  // AUDIT 审核轮（类型2）
  for (let i = 1; i <= (t.auditRounds || 0); i++) {
    const rn = t.roundNames?.find(r => r.roundType === 'AUDIT' && r.roundSequence === i)
    nodes.push({
      key: `AUDIT_${i}`,
      label: rn ? `审核第${i}轮：${rn.roundName}` : `审核第${i}轮`,
      mappingType: 'REVIEW',
      nodeType: 'AUDIT',
      nodeSequence: i,
      templateType: TemplateType.AUDIT,
      formTemplateId: undefined,
    })
  }

  // REVIEW 评审轮（类型3）
  for (let i = 1; i <= (t.reviewRounds || 0); i++) {
    const rn = t.roundNames?.find(r => r.roundType === 'REVIEW' && r.roundSequence === i)
    nodes.push({
      key: `REVIEW_${i}`,
      label: rn ? `评审第${i}轮：${rn.roundName}` : `评审第${i}轮`,
      mappingType: 'REVIEW',
      nodeType: 'REVIEW',
      nodeSequence: i,
      templateType: TemplateType.REVIEW,
      formTemplateId: undefined,
    })
  }

  // DECISION 决策轮（类型4）
  for (let i = 1; i <= (t.decisionRounds || 0); i++) {
    const rn = t.roundNames?.find(r => r.roundType === 'DECISION' && r.roundSequence === i)
    nodes.push({
      key: `DECISION_${i}`,
      label: rn ? `决策第${i}轮：${rn.roundName}` : `决策第${i}轮`,
      mappingType: 'REVIEW',
      nodeType: 'DECISION',
      nodeSequence: i,
      templateType: TemplateType.DECISION,
      formTemplateId: undefined,
    })
  }

  return nodes
})

/** 生成管理流程节点列表 */
const manageNodes = computed<NodeRow[]>(() => {
  if (!props.manageTemplate) return []
  const nodes: NodeRow[] = []

  for (const stage of props.manageTemplate.stages || []) {
    const templateTypeMap: Record<string, number> = {
      KICKOFF: TemplateType.KICKOFF,
      EXECUTION: TemplateType.EXECUTION,
      ACCEPTANCE: TemplateType.ACCEPTANCE,
    }
    nodes.push({
      key: `MANAGE_STAGE_${stage.stageOrder}`,
      label: `${stage.stageName}（第${stage.stageOrder}阶段）`,
      mappingType: 'MANAGE',
      nodeType: 'STAGE',
      nodeSequence: stage.stageOrder,
      templateType: templateTypeMap[stage.stageType] ?? TemplateType.EXECUTION,
      stageType: stage.stageType,
      formTemplateId: undefined,
    })
  }

  return nodes
})

/** 从 props.formMappings 填充现有映射 */
const fillFromMappings = () => {
  const allNodes = [...reviewNodes.value, ...manageNodes.value]
  for (const node of allNodes) {
    const existing = props.formMappings.find(m =>
      m.mappingType === node.mappingType
      && m.nodeType === node.nodeType
      && (m.nodeSequence ?? null) === (node.nodeSequence ?? null)
    )
    formSelections[node.key] = existing?.formTemplateId
  }
}

/** 加载各类型的已启用表单模板 */
const loadFormOptions = async () => {
  optionsLoading.value = true
  try {
    const types = [
      TemplateType.APPLICATION,
      TemplateType.AUDIT,
      TemplateType.REVIEW,
      TemplateType.DECISION,
      TemplateType.KICKOFF,
      TemplateType.EXECUTION,
      TemplateType.ACCEPTANCE,
    ]
    const results = await Promise.all(types.map(t => listEnabledFormTemplate(t)))
    const options: Record<number, { label: string; value: number }[]> = {}
    types.forEach((t, idx) => {
      const data: FormTemplateResp[] = results[idx].data || []
      options[t] = data.map(f => ({ label: f.templateName, value: f.id }))
    })
    formOptions.value = options
  }
  catch (error) {
    console.error('加载表单模板选项失败:', error)
  }
  finally {
    optionsLoading.value = false
  }
}

/** 保存表单映射 */
const handleSave = async () => {
  const allNodes = [...reviewNodes.value, ...manageNodes.value]
  const reqs = allNodes
    .filter(n => formSelections[n.key])
    .map(n => ({
      mappingType: n.mappingType,
      nodeType: n.nodeType,
      nodeSequence: n.nodeSequence,
      formTemplateId: formSelections[n.key]!,
    }))

  if (reqs.length === 0) {
    Message.warning('请至少为一个节点选择表单模板')
    return
  }

  saveLoading.value = true
  try {
    await saveFormMapping(props.typeId, reqs)
    Message.success('表单映射保存成功')
    emit('saved')
  }
  catch (error) {
    console.error('保存失败:', error)
  }
  finally {
    saveLoading.value = false
  }
}

const nodeTypeLabel = (type: string) => ({
  APPLICATION: '申请', AUDIT: '审核', REVIEW: '评审',
  DECISION: '决策', STAGE: '阶段',
}[type] ?? type)

const nodeTypeColor = (type: string) => ({
  APPLICATION: 'blue', AUDIT: 'orange', REVIEW: 'green',
  DECISION: 'red', STAGE: 'purple',
}[type] ?? 'gray')

const stageTypeLabel = (type: string) => ({ KICKOFF: '立项', EXECUTION: '执行', ACCEPTANCE: '验收' }[type] ?? type)
const stageTypeColor = (type: string) => ({ KICKOFF: 'blue', EXECUTION: 'green', ACCEPTANCE: 'orange' }[type] ?? 'gray')

const templateTypeLabel = (type: number) => ({
  1: '申请', 2: '审核', 3: '评审', 4: '决策', 5: '立项', 6: '执行', 7: '验收',
}[type] ?? '')

onMounted(async () => {
  await loadFormOptions()
  fillFromMappings()
})

watch(() => props.formMappings, fillFromMappings, { deep: true })
watch([() => props.reviewTemplate, () => props.manageTemplate], () => {
  nextTick(() => fillFromMappings())
})
</script>

<style scoped>
.tab-container {
  padding: 16px 0;
}
</style>
