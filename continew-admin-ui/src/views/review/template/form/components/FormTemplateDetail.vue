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
            <a-descriptions-item label="模板类型">
              <a-tag :color="getTemplateTypeColor(templateData.templateType)">
                {{ getTemplateTypeLabel(templateData.templateType) }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="启用状态">
              <GiCellStatus :status="templateData.status" />
            </a-descriptions-item>
            <a-descriptions-item label="字段数量">
              {{ templateData.fieldCount || 0 }} 个
            </a-descriptions-item>
            <a-descriptions-item label="创建时间">
              {{ templateData.createTime }}
            </a-descriptions-item>
            <a-descriptions-item label="排序">
              {{ templateData.sort }}
            </a-descriptions-item>
            <a-descriptions-item label="修改时间">
              {{ templateData.updateTime || '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="模板描述" :span="2">
              {{ templateData.description || '-' }}
            </a-descriptions-item>
          </a-descriptions>
        </a-card>

        <!-- 布局配置 -->
        <a-card v-if="templateData.layoutConfig" title="布局配置" :bordered="false" class="config-card">
          <a-descriptions :column="3" bordered>
            <a-descriptions-item label="栅格列数">
              {{ templateData.layoutConfig.gridCols || 24 }}
            </a-descriptions-item>
            <a-descriptions-item label="标签宽度">
              {{ templateData.layoutConfig.labelWidth || 120 }}px
            </a-descriptions-item>
            <a-descriptions-item label="标签对齐">
              {{ templateData.layoutConfig.labelAlign || 'right' }}
            </a-descriptions-item>
          </a-descriptions>
        </a-card>

        <!-- 字段列表 -->
        <a-card title="字段配置" :bordered="false" class="field-card">
          <a-table
            :columns="fieldColumns"
            :data="templateData.fields || []"
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #fieldType="{ record }">
              <a-tag :color="getFieldTypeColor(record.fieldType)" size="small">
                {{ getFieldTypeLabel(record.fieldType) }}
              </a-tag>
            </template>
            <template #isRequired="{ record }">
              <a-tag :color="record.isRequired ? 'red' : 'gray'" size="small">
                {{ record.isRequired ? '必填' : '选填' }}
              </a-tag>
            </template>
            <template #span="{ record }">
              {{ record.span }} / 24
            </template>
          </a-table>
        </a-card>

        <!-- 附件文件 -->
        <a-card v-if="templateData.files && templateData.files.length > 0" title="附件文件" :bordered="false" class="file-card">
          <a-table
            :columns="fileColumns"
            :data="templateData.files || []"
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #fileType="{ record }">
              <a-tag size="small">
                {{ record.fileType === 'TEMPLATE' ? '模板文件' : '示例文件' }}
              </a-tag>
            </template>
          </a-table>
        </a-card>
      </div>
    </a-spin>

    <template #footer>
      <a-space>
        <a-button v-permission="['review:template:form:update']" type="primary" @click="handleEdit">
          编辑
        </a-button>
        <a-button @click="handleCancel">关闭</a-button>
      </a-space>
    </template>
  </a-drawer>
</template>

<script setup lang="ts">
import type { TableColumnData } from '@arco-design/web-vue'
import { getFormTemplate, type FormTemplateResp } from '@/apis/review'

defineOptions({ name: 'FormTemplateDetail' })

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
const templateData = ref<FormTemplateResp | null>(null)

// 模板类型选项
const templateTypeOptions = [
  { label: '申请表单', value: 1, color: 'blue' },
  { label: '审核表单', value: 2, color: 'green' },
  { label: '评审表单', value: 3, color: 'orange' },
  { label: '决策表单', value: 4, color: 'purple' },
  { label: '立项阶段', value: 5, color: 'cyan' },
  { label: '执行阶段', value: 6, color: 'magenta' },
  { label: '验收阶段', value: 7, color: 'red' },
]

// 字段类型选项
const fieldTypeOptions = [
  { label: '单行文本', value: 'TEXT', color: 'blue' },
  { label: '多行文本', value: 'TEXTAREA', color: 'cyan' },
  { label: '数字', value: 'NUMBER', color: 'green' },
  { label: '日期', value: 'DATE', color: 'orange' },
  { label: '下拉选择', value: 'SELECT', color: 'purple' },
  { label: '单选', value: 'RADIO', color: 'magenta' },
  { label: '多选', value: 'CHECKBOX', color: 'geekblue' },
  { label: '评分', value: 'SCORE', color: 'gold' },
  { label: '文件', value: 'FILE', color: 'red' },
  { label: '表格', value: 'TABLE', color: 'volcano' },
]

// 获取模板类型标签
const getTemplateTypeLabel = (type: number) => {
  return templateTypeOptions.find(item => item.value === type)?.label || '未知'
}

// 获取模板类型颜色
const getTemplateTypeColor = (type: number) => {
  return templateTypeOptions.find(item => item.value === type)?.color || 'gray'
}

// 获取字段类型标签
const getFieldTypeLabel = (type: string) => {
  return fieldTypeOptions.find(item => item.value === type)?.label || type
}

// 获取字段类型颜色
const getFieldTypeColor = (type: string) => {
  return fieldTypeOptions.find(item => item.value === type)?.color || 'gray'
}

// 字段配置表格列
const fieldColumns: TableColumnData[] = [
  { title: '序号', width: 70, align: 'center', render: ({ rowIndex }) => h('span', {}, rowIndex + 1) },
  { title: '字段名称', dataIndex: 'fieldName', minWidth: 120 },
  { title: '字段编码', dataIndex: 'fieldCode', minWidth: 120 },
  { title: '字段类型', dataIndex: 'fieldType', slotName: 'fieldType', width: 110, align: 'center' },
  { title: '栅格占位', dataIndex: 'span', slotName: 'span', width: 100, align: 'center' },
  { title: '是否必填', dataIndex: 'isRequired', slotName: 'isRequired', width: 90, align: 'center' },
  { title: '排序', dataIndex: 'sort', width: 80, align: 'center' },
]

// 附件文件表格列
const fileColumns: TableColumnData[] = [
  { title: '序号', width: 70, align: 'center', render: ({ rowIndex }) => h('span', {}, rowIndex + 1) },
  { title: '文件类型', dataIndex: 'fileType', slotName: 'fileType', width: 110, align: 'center' },
  { title: '文件名称', dataIndex: 'fileName', minWidth: 200 },
  { title: '文件说明', dataIndex: 'description', minWidth: 150 },
  { title: '排序', dataIndex: 'sort', width: 80, align: 'center' },
]

// 加载详情
const loadDetail = async () => {
  if (!props.id)
    return

  loading.value = true
  try {
    templateData.value = await getFormTemplate(props.id)
  }
  catch (error) {
    console.error('加载模板详情失败:', error)
  }
  finally {
    loading.value = false
  }
}

// 关闭抽屉
const handleCancel = () => {
  emit('update:visible', false)
}

// 编辑
const handleEdit = () => {
  if (templateData.value) {
    emit('edit', templateData.value.id)
    handleCancel()
  }
}

// 监听可见性变化
watch(() => props.visible, (newVal) => {
  if (newVal) {
    loadDetail()
  }
  else {
    templateData.value = null
  }
})

// 监听ID变化
watch(() => props.id, () => {
  if (props.visible) {
    loadDetail()
  }
})
</script>

<style lang="scss" scoped>
.detail-container {
  .info-card,
  .config-card,
  .field-card,
  .file-card {
    margin-bottom: 16px;

    &:last-child {
      margin-bottom: 0;
    }
  }
}
</style>
