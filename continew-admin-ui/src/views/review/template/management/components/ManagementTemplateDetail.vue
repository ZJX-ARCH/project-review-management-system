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
            <a-descriptions-item label="阶段数量">
              {{ templateData.stages?.length || 0 }}
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
          <ManagementFlowVisualization :phases="templateData.stages || []" />
        </a-card>

        <!-- 详细配置 -->
        <a-card title="阶段详细配置" :bordered="false" class="detail-card">
          <a-table
            :columns="detailColumns"
            :data="sortedPhases"
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #stageType="{ record }">
              <a-tag :color="getPhaseColor(record.stageType)" size="small">
                {{ getPhaseTypeName(record.stageType) }}
              </a-tag>
            </template>
            <template #isRequired="{ record }">
              <a-tag :color="record.isRequired ? 'blue' : 'gray'" size="small">
                {{ record.isRequired ? '必须' : '可选' }}
              </a-tag>
            </template>
          </a-table>
        </a-card>
      </div>
    </a-spin>

    <template #footer>
      <a-space>
        <a-button v-permission="['review:template:management:update']" type="primary" @click="handleEdit">
          编辑
        </a-button>
        <a-button @click="handleCancel">关闭</a-button>
      </a-space>
    </template>
  </a-drawer>
</template>

<script setup lang="ts">
import type { TableColumnData } from '@arco-design/web-vue'
import ManagementFlowVisualization from './ManagementFlowVisualization.vue'
import { getManagementTemplate, type ManagementTemplateResp, StageType } from '@/apis/review'

defineOptions({ name: 'ManagementTemplateDetail' })

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
const templateData = ref<ManagementTemplateResp | null>(null)

// 排序后的阶段列表
const sortedPhases = computed(() => {
  if (!templateData.value?.stages)
    return []
  return [...templateData.value.stages].sort((a, b) => a.stageOrder - b.stageOrder)
})

// 详细配置表格列
const detailColumns: TableColumnData[] = [
  { title: '序号', width: 80, align: 'center', render: ({ rowIndex }) => h('span', {}, rowIndex + 1) },
  { title: '阶段名称', dataIndex: 'stageName', minWidth: 150 },
  { title: '阶段类型', dataIndex: 'stageType', slotName: 'stageType', width: 100, align: 'center' },
  { title: '阶段顺序', dataIndex: 'stageOrder', width: 100, align: 'center' },
  { title: '是否必须', dataIndex: 'isRequired', slotName: 'isRequired', width: 100, align: 'center' },
]

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

// 加载详情
const loadDetail = async () => {
  if (!props.id)
    return

  loading.value = true
  try {
    const res = await getManagementTemplate(props.id)
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
