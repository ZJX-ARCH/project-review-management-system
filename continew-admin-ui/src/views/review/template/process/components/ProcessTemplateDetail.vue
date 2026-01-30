<template>
  <a-drawer
    :visible="visible"
    :width="900"
    unmount-on-close
    render-to-body
    @cancel="handleCancel"
  >
    <template #title>
      <span>模板详情：{{ templateData?.templateName }}</span>
    </template>

    <a-spin :loading="loading" style="width: 100%;">
      <div v-if="templateData" class="detail-container">
        <!-- 基本信息 -->
        <a-card title="基本信息" :bordered="false" class="info-card">
          <a-descriptions :column="2" bordered>
            <a-descriptions-item label="模板名称">
              {{ templateData.templateName }}
            </a-descriptions-item>
            <a-descriptions-item label="模板编码">
              {{ templateData.templateCode }}
            </a-descriptions-item>
            <a-descriptions-item label="启用状态">
              <GiCellStatus :status="templateData.status" />
            </a-descriptions-item>
            <a-descriptions-item label="创建时间">
              {{ templateData.createTime }}
            </a-descriptions-item>
            <a-descriptions-item label="轮次配置">
              审核:{{ templateData.auditRounds }} | 评审:{{ templateData.reviewRounds}} | 决策:{{ templateData.decisionRounds }}
            </a-descriptions-item>
            <a-descriptions-item label="修改时间">
              {{ templateData.updateTime || '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="模板描述" :span="2">
              {{ templateData.description || '-' }}
            </a-descriptions-item>
          </a-descriptions>
        </a-card>

        <!-- 流程可视化 -->
        <a-card title="流程可视化" :bordered="false" class="flow-card">
          <ProcessFlowVisualization :rounds="templateData.roundNames || []" />
        </a-card>

        <!-- 详细配置 -->
        <a-card title="轮次详细配置" :bordered="false" class="detail-card">
          <a-table
            :columns="detailColumns"
            :data="sortedRounds"
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #roundType="{ record }">
              <a-tag :color="getRoundColor(record.roundType)" size="small">
                {{ getRoundTypeName(record.roundType) }}
              </a-tag>
            </template>
          </a-table>
        </a-card>
      </div>
    </a-spin>

    <template #footer>
      <a-space>
        <a-button v-permission="['review:template:process:update']" type="primary" @click="handleEdit">
          编辑
        </a-button>
        <a-button @click="handleCancel">关闭</a-button>
      </a-space>
    </template>
  </a-drawer>
</template>

<script setup lang="ts">
import type { TableColumnData } from '@arco-design/web-vue'
import ProcessFlowVisualization from './ProcessFlowVisualization.vue'
import { getProcessTemplate, type ProcessTemplateResp, RoundType } from '@/apis/review'

defineOptions({ name: 'ProcessTemplateDetail' })

interface Props {
  visible: boolean
  id?: number | string
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  id: undefined,
})

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'edit', id: number): void
}>()

// 加载状态
const loading = ref(false)

// 模板数据
const templateData = ref<ProcessTemplateResp | null>(null)

// 排序后的轮次列表
const sortedRounds = computed(() => {
  if (!templateData.value?.roundNames)
    return []

  return [...templateData.value.roundNames].sort((a, b) => {
    const typeOrder = { [RoundType.AUDIT]: 1, [RoundType.REVIEW]: 2, [RoundType.DECISION]: 3 }
    const typeCompare = typeOrder[a.roundType] - typeOrder[b.roundType]
    if (typeCompare !== 0)
      return typeCompare
    return a.roundSequence - b.roundSequence
  })
})

// 详细配置表格列
const detailColumns: TableColumnData[] = [
  { title: '序号', width: 80, align: 'center', render: ({ rowIndex }) => h('span', {}, rowIndex + 1) },
  { title: '轮次名称', dataIndex: 'roundName', minWidth: 150 },
  { title: '轮次类型', dataIndex: 'roundType', slotName: 'roundType', width: 100, align: 'center' },
  { title: '轮次序号', dataIndex: 'roundSequence', width: 100, align: 'center' },
]

/** 获取轮次类型名称 */
const getRoundTypeName = (type: RoundType): string => {
  const typeMap = {
    [RoundType.AUDIT]: '审核',
    [RoundType.REVIEW]: '评审',
    [RoundType.DECISION]: '决策',
  }
  return typeMap[type] || type
}

/** 获取轮次颜色 */
const getRoundColor = (type: RoundType): string => {
  const colorMap = {
    [RoundType.AUDIT]: 'green',
    [RoundType.REVIEW]: 'blue',
    [RoundType.DECISION]: 'orange',
  }
  return colorMap[type] || 'gray'
}

// 加载详情
const loadDetail = async () => {
  if (!props.id)
    return

  loading.value = true
  try {
    const res = await getProcessTemplate(props.id)
    templateData.value = res.data
  }
  catch (error) {
    console.error('加载详情失败:', error)
  }
  finally {
    loading.value = false
  }
}

// 关闭
const handleCancel = () => {
  emit('update:visible', false)
  templateData.value = null
}

// 编辑
const handleEdit = () => {
  if (templateData.value) {
    emit('edit', templateData.value.id)
    handleCancel()
  }
}

// 监听visible变化，加载详情
watch(() => props.visible, (newVal) => {
  if (newVal)
    loadDetail()
})
</script>

<style scoped>
.detail-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-card,
.flow-card,
.detail-card {
  margin-bottom: 0;
}

:deep(.arco-card-header) {
  font-weight: 600;
  font-size: 16px;
}
</style>
