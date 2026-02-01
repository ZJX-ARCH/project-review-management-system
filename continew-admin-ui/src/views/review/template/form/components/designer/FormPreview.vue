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
            v-show="field.isVisible !== false"
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
                :disabled="field.isReadonly === true"
              />

              <!-- 多行文本 -->
              <a-textarea
                v-else-if="field.fieldType === 'TEXTAREA'"
                v-model="previewData[field.fieldCode]"
                :placeholder="field.fieldConfig?.placeholder || '请输入'"
                :max-length="field.fieldConfig?.maxLength"
                :auto-size="{ minRows: field.fieldConfig?.rows || 4, maxRows: 10 }"
                :show-word-limit="field.fieldConfig?.showWordCount"
                :disabled="field.isReadonly === true"
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
                :disabled="field.isReadonly === true"
              />

              <!-- 日期 -->
              <a-date-picker
                v-else-if="field.fieldType === 'DATE'"
                v-model="previewData[field.fieldCode]"
                :placeholder="field.fieldConfig?.placeholder || '请选择日期'"
                :format="field.fieldConfig?.format || 'YYYY-MM-DD'"
                :show-time="isDateWithTime(field.fieldConfig?.format)"
                :mode="getDatePickerMode(field.fieldConfig?.format)"
                :style="{ width: '100%' }"
                :disabled="field.isReadonly === true"
              />

              <!-- 下拉选择 -->
              <a-select
                v-else-if="field.fieldType === 'SELECT'"
                v-model="previewData[field.fieldCode]"
                :placeholder="field.fieldConfig?.placeholder || '请选择'"
                :options="field.fieldConfig?.options || []"
                :allow-clear="field.fieldConfig?.allowClear"
                :disabled="field.isReadonly === true"
              />

              <!-- 单选 -->
              <a-radio-group
                v-else-if="field.fieldType === 'RADIO'"
                v-model="previewData[field.fieldCode]"
                :options="field.fieldConfig?.options || []"
                :disabled="field.isReadonly === true"
              />

              <!-- 多选 -->
              <a-checkbox-group
                v-else-if="field.fieldType === 'CHECKBOX'"
                v-model="previewData[field.fieldCode]"
                :options="field.fieldConfig?.options || []"
                :disabled="field.isReadonly === true"
              />

              <!-- 评分 -->
              <a-rate
                v-else-if="field.fieldType === 'SCORE'"
                v-model="previewData[field.fieldCode]"
                :count="field.fieldConfig?.count || 5"
                :allow-half="field.fieldConfig?.allowHalf"
                :grade-desc="field.fieldConfig?.gradeDesc"
                :disabled="field.isReadonly === true"
              />

              <!-- 文件上传 -->
              <a-upload
                v-else-if="field.fieldType === 'FILE'"
                :file-list="previewData[field.fieldCode]"
                :limit="field.fieldConfig?.maxCount || 5"
                action=""
                :custom-request="handleFileUpload"
                :disabled="field.isReadonly === true"
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

              <!-- 文件模板 -->
              <div v-else-if="field.fieldType === 'FILE_TEMPLATE'" class="file-template-field">
                <a-space direction="vertical" fill>
                  <div v-if="field.fieldConfig?.templateFiles && field.fieldConfig.templateFiles.length > 0" class="template-files-list">
                    <div
                      v-for="(file, fileIndex) in field.fieldConfig.templateFiles"
                      :key="fileIndex"
                      class="template-file-item"
                    >
                      <div class="file-info">
                        <icon-file style="color: var(--color-primary-6);" />
                        <span class="file-name">{{ file.name || '未命名文件' }}</span>
                      </div>
                      <a-space :size="8" class="file-actions">
                        <!-- 只有图片才显示预览按钮 -->
                        <a-button
                          v-if="field.fieldConfig?.allowPreview && isImageFile(file.name)"
                          type="primary"
                          size="small"
                          @click="handlePreviewTemplateFile(file)"
                        >
                          <icon-eye />
                          预览
                        </a-button>
                        <a-button
                          v-if="field.fieldConfig?.allowDownload"
                          type="outline"
                          size="small"
                          @click="handleDownloadTemplateFile(file)"
                        >
                          <icon-download />
                          下载
                        </a-button>
                      </a-space>
                    </div>
                  </div>
                  <a-empty v-else description="暂无模板文件" :style="{ margin: '8px 0' }" />
                  <div v-if="field.fieldConfig?.tips" class="template-tips">
                    <icon-info-circle />
                    {{ field.fieldConfig.tips }}
                  </div>
                </a-space>
              </div>

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
                        <!-- 下拉选择 -->
                        <a-select
                          v-else-if="col.type === 'SELECT'"
                          v-model="row[col.code]"
                          :options="col.options || []"
                          :placeholder="`请选择${col.name}`"
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

              <!-- 评分表 -->
              <div v-else-if="field.fieldType === 'SCORE_TABLE'" class="score-table-field">
                <div class="score-table">
                  <!-- 表头 -->
                  <div class="score-table-header">
                    <div class="score-table-cell score-item-name">评分项</div>
                    <div class="score-table-cell score-item-max">满分</div>
                    <div
                      v-if="field.fieldConfig?.scoreMode === 'WEIGHTED' && field.fieldConfig?.displayConfig?.showWeight"
                      class="score-table-cell score-item-weight"
                    >
                      权重
                    </div>
                    <div class="score-table-cell score-item-score">得分</div>
                  </div>

                  <!-- 评分项 -->
                  <div class="score-table-body">
                    <div
                      v-for="(item, index) in field.fieldConfig?.scoreItems || []"
                      :key="item.id"
                      class="score-table-row"
                    >
                      <div class="score-table-cell score-item-name">
                        <div class="item-name-content">
                          <span class="item-index">{{ index + 1 }}.</span>
                          <span class="item-name">{{ item.itemName }}</span>
                        </div>
                        <div
                          v-if="field.fieldConfig?.displayConfig?.showDescription && item.description"
                          class="item-description"
                        >
                          {{ item.description }}
                        </div>
                      </div>
                      <div class="score-table-cell score-item-max">
                        {{ item.maxScore }}
                      </div>
                      <div
                        v-if="field.fieldConfig?.scoreMode === 'WEIGHTED' && field.fieldConfig?.displayConfig?.showWeight"
                        class="score-table-cell score-item-weight"
                      >
                        {{ (item.weight * 100).toFixed(0) }}%
                      </div>
                      <div class="score-table-cell score-item-score">
                        <a-input-number
                          v-model="getScoreTableData(field.fieldCode)[item.itemCode]"
                          :min="0"
                          :max="item.maxScore"
                          :precision="item.allowDecimal ? item.decimalPlaces : 0"
                          :placeholder="`请输入分数(0-${item.maxScore})`"
                          size="small"
                          :style="{ width: '120px' }"
                        />
                      </div>
                    </div>
                  </div>

                  <!-- 汇总信息 -->
                  <div v-if="field.fieldConfig?.displayConfig?.showSummary" class="score-table-summary">
                    <div class="summary-row">
                      <span class="summary-label">总分:</span>
                      <span class="summary-value">{{ calculateScoreTableTotal(field) }} / {{ calculateScoreTableMaxTotal(field) }}</span>
                    </div>
                    <div v-if="field.fieldConfig?.scoreMode === 'WEIGHTED'" class="summary-row">
                      <span class="summary-label">加权分:</span>
                      <span class="summary-value">{{ calculateScoreTableWeighted(field).toFixed(1) }}</span>
                    </div>
                    <div class="summary-row">
                      <span class="summary-label">得分率:</span>
                      <span class="summary-value">{{ calculateScoreTablePercentage(field).toFixed(1) }}%</span>
                    </div>
                    <div class="summary-row">
                      <span class="summary-label">评价:</span>
                      <span :class="['summary-value', 'level-' + getScoreTableLevel(field)]">
                        {{ getScoreTableLevelText(field) }}
                      </span>
                    </div>
                  </div>
                </div>
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
import { Message } from '@arco-design/web-vue'
import type { RequestOption } from '@arco-design/web-vue'
import { IconDownload, IconEye, IconFile, IconInfoCircle, IconPlus, IconUpload } from '@arco-design/web-vue/es/icon'
import type { FormFieldReq, FormTemplateReq } from '@/apis/review'
import { uploadFile } from '@/apis/system/file'

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

// 判断日期是否需要显示时间选择器
const isDateWithTime = (format?: string) => {
  if (!format)
    return false
  // 如果格式包含时分秒（HH、mm、ss），则显示时间选择器
  return /HH|mm|ss|hh|H|h|m|s/.test(format)
}

// 获取日期选择器的模式
const getDatePickerMode = (format?: string) => {
  if (!format)
    return 'date'
  // 如果格式只包含年月（YYYY-MM），使用月份模式
  if (/^YYYY-MM$/.test(format)) {
    return 'month'
  }
  // 如果格式只包含年（YYYY），使用年份模式
  if (/^YYYY$/.test(format)) {
    return 'year'
  }
  return 'date'
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
    options: col.options || [],
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

// 下载单个模板文件
const handleDownloadTemplateFile = (file: any) => {
  // 从文件对象中提取 URL（支持多种格式）
  const url = file.url || file.response?.data?.url || file.response?.url || file.response?.path
  if (!url) {
    Message.warning('文件地址不存在，无法下载')
    return
  }

  // 使用 fetch 下载并保持原始文件名
  fetch(url)
    .then(response => response.blob())
    .then((blob) => {
      const link = document.createElement('a')
      const objectUrl = URL.createObjectURL(blob)
      link.href = objectUrl
      link.download = file.name || '模板文件' // 使用原始文件名
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      // 释放对象 URL
      URL.revokeObjectURL(objectUrl)
    })
    .catch((error) => {
      console.error('下载失败:', error)
      Message.error('下载失败，请稍后重试')
    })
}

// 预览单个模板文件
const handlePreviewTemplateFile = (file: any) => {
  // 从文件对象中提取 URL（支持多种格式）
  const url = file.url || file.response?.data?.url || file.response?.url || file.response?.path
  if (!url) {
    Message.warning('文件地址不存在，无法预览')
    return
  }
  // 在新窗口打开预览
  window.open(url, '_blank')
}

// 判断是否为图片文件
const isImageFile = (fileName: string) => {
  if (!fileName)
    return false
  const ext = fileName.toLowerCase().split('.').pop()
  const imageExts = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'svg', 'ico']
  return imageExts.includes(ext || '')
}

// 处理文件上传
const handleFileUpload = (options: RequestOption) => {
  ;(async function requestWrap() {
    const { onProgress, onError, onSuccess, fileItem, name = 'file' } = options
    onProgress(20)
    const formData = new FormData()
    formData.append('parentPath', '/form/')
    formData.append(name as string, fileItem.file as Blob)
    try {
      const res = await uploadFile(formData)
      Message.success('上传成功')
      onSuccess(res)
    }
    catch (error) {
      onError(error)
    }
  })()
  return {
    abort() {
      Message.error('上传已取消')
    },
  }
}

// 获取评分表数据
const getScoreTableData = (fieldCode: string) => {
  if (!previewData.value[fieldCode]) {
    previewData.value[fieldCode] = { scores: {} }
  }
  if (!previewData.value[fieldCode].scores) {
    previewData.value[fieldCode].scores = {}
  }
  return previewData.value[fieldCode].scores
}

// 计算评分表总分
const calculateScoreTableTotal = (field: any) => {
  const scores = getScoreTableData(field.fieldCode)
  const items = field.fieldConfig?.scoreItems || []
  return items.reduce((sum: number, item: any) => {
    const score = scores[item.itemCode] || 0
    return sum + score
  }, 0)
}

// 计算评分表满分
const calculateScoreTableMaxTotal = (field: any) => {
  const items = field.fieldConfig?.scoreItems || []
  return items.reduce((sum: number, item: any) => sum + (item.maxScore || 0), 0)
}

// 计算加权分
const calculateScoreTableWeighted = (field: any) => {
  const scores = getScoreTableData(field.fieldCode)
  const items = field.fieldConfig?.scoreItems || []
  return items.reduce((sum: number, item: any) => {
    const score = scores[item.itemCode] || 0
    return sum + (score * (item.weight || 0))
  }, 0)
}

// 计算得分率
const calculateScoreTablePercentage = (field: any) => {
  const maxTotal = calculateScoreTableMaxTotal(field)
  if (maxTotal === 0)
    return 0

  const scoreMode = field.fieldConfig?.scoreMode
  const currentScore = scoreMode === 'WEIGHTED'
    ? calculateScoreTableWeighted(field)
    : calculateScoreTableTotal(field)

  return (currentScore / maxTotal) * 100
}

// 获取评价等级
const getScoreTableLevel = (field: any) => {
  const percentage = calculateScoreTablePercentage(field)
  const passConfig = field.fieldConfig?.passConfig || {}

  let passValue = passConfig.passValue || 60
  let excellentValue = passConfig.excellentValue || 85
  let goodValue = passConfig.goodValue || 75

  // 如果是固定分数模式，需要转换为百分比
  if (passConfig.passType === 'FIXED') {
    const maxTotal = calculateScoreTableMaxTotal(field)
    if (maxTotal > 0) {
      passValue = (passValue / maxTotal) * 100
      if (excellentValue)
        excellentValue = (excellentValue / maxTotal) * 100
      if (goodValue)
        goodValue = (goodValue / maxTotal) * 100
    }
  }

  if (percentage >= excellentValue)
    return 'EXCELLENT'
  if (percentage >= goodValue)
    return 'GOOD'
  if (percentage >= passValue)
    return 'PASS'
  return 'FAIL'
}

// 获取评价等级文本
const getScoreTableLevelText = (field: any) => {
  const level = getScoreTableLevel(field)
  const levelMap: Record<string, string> = {
    EXCELLENT: '优秀',
    GOOD: '良好',
    PASS: '及格',
    FAIL: '不及格',
  }
  return levelMap[level] || '未评分'
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
        data[field.fieldCode] = field.fieldConfig?.defaultValue || []
      }
      else if (field.fieldType === 'FILE') {
        data[field.fieldCode] = []
      }
      else if (field.fieldType === 'TABLE') {
        data[field.fieldCode] = []
      }
      else {
        data[field.fieldCode] = field.fieldConfig?.defaultValue
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

  .file-template-field {
    width: 100%;

    .template-files-list {
      border: 1px solid var(--color-border-2);
      border-radius: 4px;
      overflow: hidden;

      .template-file-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 12px;
        border-bottom: 1px solid var(--color-border-2);
        transition: all 0.2s;

        &:last-child {
          border-bottom: none;
        }

        &:hover {
          background-color: var(--color-fill-1);

          // 悬浮时显示操作按钮
          .file-actions {
            opacity: 1;
            visibility: visible;
          }
        }

        .file-info {
          display: flex;
          align-items: center;
          gap: 8px;
          flex: 1;

          .file-name {
            color: var(--color-text-1);
            font-size: 14px;
          }
        }

        // 默认隐藏操作按钮
        .file-actions {
          opacity: 0;
          visibility: hidden;
          transition: all 0.2s;
        }
      }
    }

    .template-tips {
      display: flex;
      align-items: center;
      gap: 6px;
      margin-top: 8px;
      padding: 8px 12px;
      background-color: var(--color-fill-1);
      border-radius: 4px;
      color: var(--color-text-3);
      font-size: 13px;
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

  .score-table-field {
    width: 100%;

    .score-table {
      border: 2px solid #d9dde3;
      border-radius: 8px;
      overflow: hidden;
      background-color: #ffffff;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

      .score-table-header {
        display: grid;
        grid-template-columns: 2fr 100px 160px;
        background: linear-gradient(to bottom, #f5f7fa, #ebeef5);
        border-bottom: 2px solid #d0d7de;

        &:has(.score-item-weight) {
          grid-template-columns: 2fr 100px 100px 160px;
        }

        .score-table-cell {
          font-weight: 600;
          color: var(--color-text-1);
          text-align: center;
          padding: 14px 12px;
          font-size: 14px;
          border-right: 1px solid #e4e7ed;

          &:last-child {
            border-right: none;
          }

          &.score-item-name {
            text-align: left;
            padding-left: 16px;
          }
        }
      }

      .score-table-body {
        .score-table-row {
          display: grid;
          grid-template-columns: 2fr 100px 160px;
          border-bottom: 1px solid var(--color-border-2);
          transition: background-color 0.2s;

          &:has(.score-item-weight) {
            grid-template-columns: 2fr 100px 100px 160px;
          }

          &:last-child {
            border-bottom: none;
          }

          &:hover {
            background-color: #fafbfc;
          }

          .score-table-cell {
            padding: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-right: 1px solid #f0f1f3;

            &:last-child {
              border-right: none;
            }

            &.score-item-name {
              flex-direction: column;
              align-items: flex-start;
              justify-content: flex-start;

              .item-name-content {
                display: flex;
                align-items: center;
                gap: 6px;
                font-weight: 500;
                color: var(--color-text-1);

                .item-index {
                  color: var(--color-text-3);
                  font-size: 13px;
                }

                .item-name {
                  font-size: 14px;
                }
              }

              .item-description {
                margin-top: 4px;
                padding-left: 20px;
                font-size: 12px;
                color: var(--color-text-3);
                line-height: 1.5;
              }
            }

            &.score-item-max,
            &.score-item-weight {
              font-size: 14px;
              color: var(--color-text-2);
              font-weight: 500;
            }
          }
        }
      }

      .score-table-summary {
        padding: 16px 20px;
        background: linear-gradient(to bottom, #f8f9fb, #f0f2f5);
        border-top: 2px solid #d9dde3;

        .summary-row {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 10px 14px;
          margin-bottom: 8px;
          background-color: #ffffff;
          border-radius: 6px;
          border: 1px solid #e4e7ed;
          box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);

          &:last-child {
            margin-bottom: 0;
          }

          .summary-label {
            font-size: 14px;
            color: #606266;
            font-weight: 500;
          }

          .summary-value {
            font-size: 16px;
            font-weight: 600;
            color: #303133;

            &.level-EXCELLENT {
              color: rgb(var(--success-6));
            }

            &.level-GOOD {
              color: rgb(var(--primary-6));
            }

            &.level-PASS {
              color: rgb(var(--warning-6));
            }

            &.level-FAIL {
              color: rgb(var(--danger-6));
            }
          }
        }
      }
    }
  }
}
</style>
