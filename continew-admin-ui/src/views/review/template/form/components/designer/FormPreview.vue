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
        :label-align="templateData.layoutConfig?.labelAlign || 'right'"
        :rules="formRules"
        auto-label-width
      >
        <a-row :gutter="16">
          <a-col
            v-for="(field, index) in templateData.fields"
            :key="index"
            :span="field.span"
          >
            <a-form-item
              :label="field.fieldName"
              :field="field.fieldCode"
              :label-col-props="{ span: 6 }"
              :wrapper-col-props="{ span: 18 }"
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
                <div class="custom-table">
                  <!-- 表头 -->
                  <div class="table-header">
                    <div
                      v-for="col in getTableColumnsSimple(field)"
                      :key="col.code"
                      class="table-cell header-cell"
                    >
                      {{ col.name }}
                    </div>
                    <div class="table-cell header-cell actions-header">
                      操作
                    </div>
                  </div>

                  <!-- 数据行 -->
                  <div v-if="previewData[field.fieldCode]?.length > 0" class="table-body">
                    <div
                      v-for="(row, rowIndex) in previewData[field.fieldCode]"
                      :key="rowIndex"
                      class="table-row"
                    >
                      <div
                        v-for="col in getTableColumnsSimple(field)"
                        :key="col.code"
                        class="table-cell"
                      >
                        <!-- 数字输入 -->
                        <a-input-number
                          v-if="col.type === 'NUMBER'"
                          v-model="row[col.code]"
                          :precision="col.precision || 0"
                          :placeholder="`请输入${col.name}`"
                          size="small"
                          style="width: 100%;"
                        />
                        <!-- 文本输入 -->
                        <a-input
                          v-else
                          v-model="row[col.code]"
                          :placeholder="`请输入${col.name}`"
                          size="small"
                        />
                      </div>
                      <div class="table-cell actions-cell">
                        <a-space :size="4">
                          <a-button
                            type="text"
                            size="mini"
                            :disabled="rowIndex === 0"
                            @click="handleMoveTableRowUp(field.fieldCode, rowIndex)"
                          >
                            上移
                          </a-button>
                          <a-button
                            type="text"
                            size="mini"
                            :disabled="rowIndex === previewData[field.fieldCode].length - 1"
                            @click="handleMoveTableRowDown(field.fieldCode, rowIndex)"
                          >
                            下移
                          </a-button>
                          <a-button
                            type="text"
                            status="danger"
                            size="mini"
                            @click="handleDeleteTableRow(field.fieldCode, rowIndex)"
                          >
                            删除
                          </a-button>
                        </a-space>
                      </div>
                    </div>
                  </div>

                  <!-- 空状态 -->
                  <div v-else class="table-empty">
                    暂无数据，点击下方"添加行"按钮添加数据
                  </div>
                </div>

                <a-button
                  type="dashed"
                  size="small"
                  long
                  style="margin-top: 8px;"
                  @click="handleAddTableRow(field)"
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
import { computed, ref, watch } from 'vue'
import { IconPlus, IconUpload } from '@arco-design/web-vue/es/icon'
import type { FormFieldReq, FormTemplateReq } from '@/apis/review'

defineOptions({ name: 'FormPreview' })

interface Props {
  templateData: FormTemplateReq
  templateTypeOptions: Array<{ label: string, value: number }>
}

const props = defineProps<Props>()

// 预览数据
const previewData = ref<Record<string, any>>({})

// 表单验证规则
const formRules = computed(() => {
  const rules: Record<string, any> = {}
  if (!props.templateData.fields)
    return rules

  props.templateData.fields.forEach((field) => {
    if (field.isRequired) {
      rules[field.fieldCode] = [
        {
          required: true,
          message: `${field.fieldName}不能为空`,
        },
      ]
    }
  })
  return rules
})

// 获取模板类型标签
const getTemplateTypeLabel = (type: number) => {
  return props.templateTypeOptions.find(item => item.value === type)?.label || '未知'
}

// 获取表格列配置（简化版，用于自定义表格渲染）
const getTableColumnsSimple = (field: FormFieldReq) => {
  const config = field.fieldConfig
  if (!config || !config.columns)
    return []

  return config.columns.map((col: any) => ({
    name: col.name || col.title || '未命名列',
    code: col.code || col.dataIndex,
    type: col.type || 'TEXT',
    width: col.width,
    precision: col.precision,
  }))
}

// 添加表格行
const handleAddTableRow = (field: FormFieldReq) => {
  const config = field.fieldConfig
  if (!config || !config.columns)
    return

  // 创建新行数据
  const newRow: Record<string, any> = {}
  config.columns.forEach((col: any) => {
    const code = col.code || col.dataIndex
    newRow[code] = undefined
  })

  // 添加到表格数据中
  if (!previewData.value[field.fieldCode]) {
    previewData.value[field.fieldCode] = []
  }
  previewData.value[field.fieldCode].push(newRow)
}

// 删除表格行
const handleDeleteTableRow = (fieldCode: string, rowIndex: number) => {
  if (previewData.value[fieldCode]) {
    previewData.value[fieldCode].splice(rowIndex, 1)
  }
}

// 上移表格行
const handleMoveTableRowUp = (fieldCode: string, rowIndex: number) => {
  if (rowIndex > 0 && previewData.value[fieldCode]) {
    const rows = previewData.value[fieldCode]
    const temp = rows[rowIndex]
    rows[rowIndex] = rows[rowIndex - 1]
    rows[rowIndex - 1] = temp
  }
}

// 下移表格行
const handleMoveTableRowDown = (fieldCode: string, rowIndex: number) => {
  if (previewData.value[fieldCode]) {
    const rows = previewData.value[fieldCode]
    if (rowIndex < rows.length - 1) {
      const temp = rows[rowIndex]
      rows[rowIndex] = rows[rowIndex + 1]
      rows[rowIndex + 1] = temp
    }
  }
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
  width: 100%;
  height: 100%;
  overflow-y: auto;
  padding: 16px;

  .info-card,
  .form-card {
    margin-bottom: 16px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .table-field {
    width: 100%;

    .custom-table {
      border: 1px solid var(--color-border-2);
      border-radius: 4px;
      overflow: hidden;

      .table-header {
        display: flex;
        background-color: var(--color-fill-2);
        border-bottom: 1px solid var(--color-border-2);

        .header-cell {
          font-weight: 600;
          color: var(--color-text-1);
        }
      }

      .table-body {
        .table-row {
          display: flex;
          border-bottom: 1px solid var(--color-border-2);

          &:last-child {
            border-bottom: none;
          }

          &:hover {
            background-color: var(--color-fill-1);
          }
        }
      }

      .table-cell {
        flex: 1;
        padding: 8px 12px;
        border-right: 1px solid var(--color-border-2);
        display: flex;
        align-items: center;

        &:last-child {
          border-right: none;
        }

        &.actions-cell,
        &.actions-header {
          flex: 0 0 180px;
          justify-content: center;
          padding: 4px 8px;
        }
      }

      .table-empty {
        padding: 40px 20px;
        text-align: center;
        color: var(--color-text-3);
        background-color: var(--color-fill-1);
      }
    }
  }
}
</style>
