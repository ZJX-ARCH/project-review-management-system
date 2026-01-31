<template>
  <div class="form-preview">
    <!-- 模板信息 -->
    <a-card title="模板信息" :bordered="false" class="info-card">
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="模板名称">
          {{ templateData.templateName || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="模板编码">
          {{ templateData.templateCode || '（自动生成）' }}
        </a-descriptions-item>
        <a-descriptions-item label="模板类型">
          <a-tag>{{ getTemplateTypeLabel(templateData.templateType) }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="字段数量">
          {{ templateData.fields?.length || 0 }} 个
        </a-descriptions-item>
        <a-descriptions-item label="模板描述" :span="2">
          {{ templateData.description || '-' }}
        </a-descriptions-item>
      </a-descriptions>
    </a-card>

    <!-- 表单预览 -->
    <a-card title="表单预览" :bordered="false" class="form-card">
      <a-alert type="info" style="margin-bottom: 16px;">
        <div>以下为表单实际填写效果预览，所有字段均为示例展示</div>
      </a-alert>

      <a-form
        :model="previewData"
        :label-col-props="{ style: { width: `${templateData.layoutConfig?.labelWidth || 120}px` } }"
        :label-align="templateData.layoutConfig?.labelAlign || 'right'"
      >
        <a-row :gutter="16">
          <a-col
            v-for="(field, index) in templateData.fields"
            :key="index"
            :span="field.span"
          >
            <a-form-item
              :label="field.fieldName"
              :required="field.isRequired"
              :field="field.fieldCode"
            >
              <!-- 单行文本 -->
              <a-input
                v-if="field.fieldType === 'TEXT'"
                v-model="previewData[field.fieldCode]"
                :placeholder="field.fieldConfig?.placeholder || '请输入'"
                :max-length="field.fieldConfig?.maxLength"
              />

              <!-- 多行文本 -->
              <a-textarea
                v-else-if="field.fieldType === 'TEXTAREA'"
                v-model="previewData[field.fieldCode]"
                :placeholder="field.fieldConfig?.placeholder || '请输入'"
                :max-length="field.fieldConfig?.maxLength"
                :auto-size="{ minRows: field.fieldConfig?.rows || 4, maxRows: 10 }"
                :show-word-limit="field.fieldConfig?.showWordCount"
              />

              <!-- 数字 -->
              <a-input-number
                v-else-if="field.fieldType === 'NUMBER'"
                v-model="previewData[field.fieldCode]"
                :placeholder="field.fieldConfig?.placeholder || '请输入数字'"
                :min="field.fieldConfig?.min"
                :max="field.fieldConfig?.max"
                :precision="field.fieldConfig?.precision || 0"
                :style="{ width: '100%' }"
              />

              <!-- 日期 -->
              <a-date-picker
                v-else-if="field.fieldType === 'DATE'"
                v-model="previewData[field.fieldCode]"
                :placeholder="field.fieldConfig?.placeholder || '请选择日期'"
                :format="field.fieldConfig?.format || 'YYYY-MM-DD'"
                :style="{ width: '100%' }"
              />

              <!-- 下拉选择 -->
              <a-select
                v-else-if="field.fieldType === 'SELECT'"
                v-model="previewData[field.fieldCode]"
                :placeholder="field.fieldConfig?.placeholder || '请选择'"
                :options="field.fieldConfig?.options || []"
                :allow-clear="field.fieldConfig?.allowClear"
              />

              <!-- 单选 -->
              <a-radio-group
                v-else-if="field.fieldType === 'RADIO'"
                v-model="previewData[field.fieldCode]"
                :options="field.fieldConfig?.options || []"
              />

              <!-- 多选 -->
              <a-checkbox-group
                v-else-if="field.fieldType === 'CHECKBOX'"
                v-model="previewData[field.fieldCode]"
                :options="field.fieldConfig?.options || []"
              />

              <!-- 评分 -->
              <a-rate
                v-else-if="field.fieldType === 'SCORE'"
                v-model="previewData[field.fieldCode]"
                :count="field.fieldConfig?.count || 5"
                :allow-half="field.fieldConfig?.allowHalf"
                :grade-desc="field.fieldConfig?.gradeDesc"
              />

              <!-- 文件 -->
              <a-upload
                v-else-if="field.fieldType === 'FILE'"
                :file-list="previewData[field.fieldCode]"
                :limit="field.fieldConfig?.maxCount || 5"
              >
                <template #upload-button>
                  <a-button type="outline">
                    <icon-upload />
                    上传文件
                  </a-button>
                </template>
                <template v-if="field.fieldConfig?.tips" #tip>
                  {{ field.fieldConfig.tips }}
                </template>
              </a-upload>

              <!-- 表格 -->
              <div v-else-if="field.fieldType === 'TABLE'" class="table-field">
                <a-table
                  :columns="getTableColumns(field)"
                  :data="previewData[field.fieldCode] || []"
                  :pagination="false"
                  size="small"
                />
                <a-button
                  type="dashed"
                  size="small"
                  long
                  style="margin-top: 8px;"
                >
                  <icon-plus /> 添加行
                </a-button>
              </div>

              <!-- 未知类型 -->
              <a-input
                v-else
                disabled
                :placeholder="`不支持的字段类型: ${field.fieldType}`"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import type { FormFieldReq, FormTemplateReq } from '@/apis/review'

defineOptions({ name: 'FormPreview' })

interface Props {
  templateData: FormTemplateReq
  templateTypeOptions: Array<{ label: string, value: number }>
}

const props = defineProps<Props>()

// 预览数据
const previewData = ref<Record<string, any>>({})

// 获取模板类型标签
const getTemplateTypeLabel = (type: number) => {
  return props.templateTypeOptions.find(item => item.value === type)?.label || '未知'
}

// 获取表格列配置
const getTableColumns = (field: FormFieldReq) => {
  const columns = field.fieldConfig?.columns || []
  return columns.map((col: any) => ({
    title: col.name,
    dataIndex: col.code,
    width: col.width,
  }))
}

// 初始化预览数据
watch(
  () => props.templateData.fields,
  (fields) => {
    if (!fields)
      return

    const data: Record<string, any> = {}
    fields.forEach((field) => {
      // 根据字段类型初始化默认值
      if (field.fieldType === 'CHECKBOX') {
        data[field.fieldCode] = []
      }
      else if (field.fieldType === 'FILE') {
        data[field.fieldCode] = []
      }
      else if (field.fieldType === 'TABLE') {
        data[field.fieldCode] = []
      }
      else {
        data[field.fieldCode] = undefined
      }
    })
    previewData.value = data
  },
  { immediate: true },
)
</script>

<style lang="scss" scoped>
.form-preview {
  .info-card,
  .form-card {
    margin-bottom: 16px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .table-field {
    width: 100%;
  }
}
</style>
