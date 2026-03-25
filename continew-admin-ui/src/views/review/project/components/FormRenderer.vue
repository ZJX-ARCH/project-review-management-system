<!--
  FormRenderer：根据 ProjectFormTemplateResp 动态渲染可填写表单。
  支持字段类型：TEXT / TEXTAREA / NUMBER / DATE / SELECT / RADIO / CHECKBOX / SCORE / FILE / TABLE / SCORE_TABLE
-->
<template>
  <a-form ref="formRef" :model="formDataProxy" :rules="formRules" layout="vertical">
    <a-row :gutter="16">
      <a-col
        v-for="field in template.fields"
        :key="field.fieldCode"
        :span="field.span ?? 24"
      >
        <a-form-item
          :label="field.fieldName"
          :field="field.fieldCode"
          :required="field.fieldType === 'SCORE_TABLE' ? (scoreTableRequired || field.isRequired) : field.isRequired"
        >
          <!-- 单行文本 -->
          <a-input
            v-if="field.fieldType === 'TEXT'"
            v-model="formDataProxy[field.fieldCode]"
            :placeholder="`请输入${field.fieldName}`"
            :disabled="readonly"
          />

          <!-- 多行文本 -->
          <a-textarea
            v-else-if="field.fieldType === 'TEXTAREA'"
            v-model="formDataProxy[field.fieldCode]"
            :placeholder="`请输入${field.fieldName}`"
            :auto-size="{ minRows: 3, maxRows: 6 }"
            :disabled="readonly"
          />

          <!-- 数字 -->
          <a-input-number
            v-else-if="field.fieldType === 'NUMBER'"
            v-model="formDataProxy[field.fieldCode]"
            :placeholder="`请输入${field.fieldName}`"
            :disabled="readonly"
            style="width: 100%;"
          />

          <!-- 日期 -->
          <a-date-picker
            v-else-if="field.fieldType === 'DATE'"
            v-model="formDataProxy[field.fieldCode]"
            :placeholder="`请选择${field.fieldName}`"
            :disabled="readonly"
            style="width: 100%;"
          />

          <!-- 下拉选择 -->
          <a-select
            v-else-if="field.fieldType === 'SELECT'"
            v-model="formDataProxy[field.fieldCode]"
            :placeholder="`请选择${field.fieldName}`"
            :disabled="readonly"
            :options="parseOptions(field.fieldConfig)"
          />

          <!-- 单选 -->
          <a-radio-group
            v-else-if="field.fieldType === 'RADIO'"
            v-model="formDataProxy[field.fieldCode]"
            :disabled="readonly"
          >
            <a-radio
              v-for="opt in parseOptions(field.fieldConfig)"
              :key="String(opt.value)"
              :value="opt.value"
            >{{ opt.label }}</a-radio>
          </a-radio-group>

          <!-- 多选 -->
          <a-checkbox-group
            v-else-if="field.fieldType === 'CHECKBOX'"
            v-model="formDataProxy[field.fieldCode]"
            :disabled="readonly"
            :options="parseOptions(field.fieldConfig)"
          />

          <!-- 星级评分 -->
          <a-rate
            v-else-if="field.fieldType === 'SCORE'"
            v-model="formDataProxy[field.fieldCode]"
            :disabled="readonly"
            allow-half
          />

          <!-- 文件上传 -->
          <template v-else-if="field.fieldType === 'FILE' || field.fieldType === 'FILE_TEMPLATE'">
            <a-upload
              v-if="!readonly"
              action=""
              multiple
              :file-list="getFileList(field.fieldCode)"
              :limit="field.fieldConfig?.maxCount || 5"
              :custom-request="(opts) => handleFileUpload(field.fieldCode, opts)"
            >
              <template #upload-button>
                <a-button>
                  <template #icon><icon-upload /></template>
                  上传文件
                </a-button>
              </template>
            </a-upload>
            <!-- 只读：文件卡片列表 -->
            <div v-else class="readonly-file-list">
              <template v-if="getFileList(field.fieldCode).length">
                <a
                  v-for="(file, fi) in getFileList(field.fieldCode)"
                  :key="fi"
                  class="readonly-file-card"
                  :href="file.url || undefined"
                  :target="file.url ? '_blank' : undefined"
                  rel="noopener noreferrer"
                  :style="{ cursor: file.url ? 'pointer' : 'default', textDecoration: 'none' }"
                >
                  <span class="file-card-icon">
                    <icon-file />
                  </span>
                  <span class="file-card-name">{{ file.name || file.url || '未知文件' }}</span>
                  <icon-link v-if="file.url" class="file-card-link-icon" />
                </a>
              </template>
              <span v-else class="readonly-value">—</span>
            </div>
          </template>

          <!-- 自定义表格 -->
          <div v-else-if="field.fieldType === 'TABLE'" class="table-field">
            <div class="custom-table">
              <!-- 表头 -->
              <div class="table-header">
                <div
                  v-for="col in getTableColumns(field)"
                  :key="col.code"
                  class="table-cell header-cell"
                >
                  {{ col.name }}
                </div>
                <div v-if="!readonly" class="table-cell header-cell actions-header">操作</div>
              </div>

              <!-- 数据行 -->
              <div v-if="getTableRows(field.fieldCode).length > 0" class="table-body">
                <div
                  v-for="(row, rowIndex) in getTableRows(field.fieldCode)"
                  :key="rowIndex"
                  class="table-row"
                >
                  <div
                    v-for="col in getTableColumns(field)"
                    :key="col.code"
                    class="table-cell"
                  >
                    <a-input-number
                      v-if="col.type === 'NUMBER'"
                      v-model="row[col.code]"
                      :precision="col.precision || 0"
                      :placeholder="`请输入${col.name}`"
                      :disabled="readonly"
                      size="small"
                      style="width: 100%;"
                    />
                    <a-select
                      v-else-if="col.type === 'SELECT'"
                      v-model="row[col.code]"
                      :options="col.options || []"
                      :placeholder="`请选择${col.name}`"
                      :disabled="readonly"
                      size="small"
                      style="width: 100%;"
                    />
                    <a-input
                      v-else
                      v-model="row[col.code]"
                      :placeholder="`请输入${col.name}`"
                      :disabled="readonly"
                      size="small"
                    />
                  </div>
                  <div v-if="!readonly" class="table-cell actions-cell">
                    <a-space :size="4">
                      <a-button
                        type="text"
                        size="mini"
                        :disabled="rowIndex === 0"
                        @click="handleMoveTableRowUp(field.fieldCode, rowIndex)"
                      >上移</a-button>
                      <a-button
                        type="text"
                        size="mini"
                        :disabled="rowIndex === getTableRows(field.fieldCode).length - 1"
                        @click="handleMoveTableRowDown(field.fieldCode, rowIndex)"
                      >下移</a-button>
                      <a-button
                        type="text"
                        status="danger"
                        size="mini"
                        @click="handleDeleteTableRow(field.fieldCode, rowIndex)"
                      >删除</a-button>
                    </a-space>
                  </div>
                </div>
              </div>

              <!-- 空状态 -->
              <div v-else class="table-empty">
                {{ readonly ? '暂无数据' : '暂无数据，点击下方"添加行"按钮添加数据' }}
              </div>
            </div>

            <a-button
              v-if="!readonly"
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
                    {{ field.fieldConfig?.scoreMode === 'WEIGHTED' ? field.fieldConfig?.totalScore : item.maxScore }}
                  </div>
                  <div
                    v-if="field.fieldConfig?.scoreMode === 'WEIGHTED' && field.fieldConfig?.displayConfig?.showWeight"
                    class="score-table-cell score-item-weight"
                  >
                    {{ (item.weight * 100).toFixed(0) }}%
                  </div>
                  <div class="score-table-cell score-item-score">
                    <a-input-number
                      :model-value="getScoreTableScores(field.fieldCode)[item.itemCode]"
                      :min="0"
                      :max="field.fieldConfig?.scoreMode === 'WEIGHTED' ? field.fieldConfig?.totalScore : item.maxScore"
                      :precision="item.allowDecimal ? item.decimalPlaces : 0"
                      :placeholder="`0 ~ ${field.fieldConfig?.scoreMode === 'WEIGHTED' ? field.fieldConfig?.totalScore : item.maxScore}`"
                      :disabled="readonly"
                      size="small"
                      :style="{ width: '120px' }"
                      @change="(val) => onScoreChange(field.fieldCode, item.itemCode, val)"
                    />
                  </div>
                </div>
              </div>

              <!-- 汇总信息 -->
              <div v-if="field.fieldConfig?.displayConfig?.showSummary" class="score-table-summary">
                <div v-if="field.fieldConfig?.scoreMode === 'WEIGHTED'" class="summary-row">
                  <span class="summary-label">总分:</span>
                  <span class="summary-value">{{ calcWeighted(field).toFixed(1) }}</span>
                </div>
                <div v-else class="summary-row">
                  <span class="summary-label">总分:</span>
                  <span class="summary-value">{{ calcTotal(field) }} / {{ calcMaxTotal(field) }}</span>
                </div>
                <div class="summary-row">
                  <span class="summary-label">得分率:</span>
                  <span class="summary-value">{{ calcPercentage(field).toFixed(1) }}%</span>
                </div>
                <div class="summary-row">
                  <span class="summary-label">评价:</span>
                  <span :class="['summary-value', `level-${calcLevel(field)}`]">
                    {{ calcLevelText(field) }}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- 未知类型 fallback -->
          <template v-else>
            <a-input
              v-if="!readonly"
              v-model="formDataProxy[field.fieldCode]"
              :placeholder="`请输入${field.fieldName}`"
            />
            <span v-else class="readonly-value">{{ formatValue(formDataProxy[field.fieldCode]) }}</span>
          </template>
        </a-form-item>
      </a-col>
    </a-row>
  </a-form>
</template>

<script setup lang="ts">
defineOptions({ name: 'FormRenderer' })
import { computed, ref, watch } from 'vue'
import { Message } from '@arco-design/web-vue'
import type { FormInstance, RequestOption } from '@arco-design/web-vue'
import type { ProjectFormTemplateResp } from '@/apis/review/type'
import { uploadReviewFile } from '@/apis/review/project'

interface Props {
  modelValue: Record<string, unknown>
  template: ProjectFormTemplateResp
  readonly?: boolean
  /** 是否强制要求评分表每项都填（任务表单传 true，申请表单不传则按 isRequired 判断） */
  scoreTableRequired?: boolean
}

interface Emits {
  (e: 'update:modelValue', value: Record<string, unknown>): void
}

const props = withDefaults(defineProps<Props>(), {
  readonly: false,
  scoreTableRequired: false,
})
const emit = defineEmits<Emits>()

const formRef = ref<FormInstance>()

// 根据字段类型和 isRequired 动态生成校验规则
const formRules = computed(() => {
  const rules: Record<string, any[]> = {}
  for (const field of props.template.fields ?? []) {
    if (field.fieldType === 'SCORE_TABLE') {
      // scoreTableRequired=true（任务表单）：强制每项必填；否则（申请表单）按 isRequired 决定
      const needValidate = props.scoreTableRequired || field.isRequired
      const items = (field.fieldConfig?.scoreItems ?? []) as any[]
      if (needValidate && items.length > 0) {
        rules[field.fieldCode] = [{
          validator: (value: any, cb: (error?: string) => void) => {
            const scores = value?.scores ?? {}
            const hasMissing = items.some(
              item => scores[item.itemCode] === null || scores[item.itemCode] === undefined,
            )
            hasMissing ? cb(`请完整填写《${field.fieldName}》的所有评分项`) : cb()
          },
        }]
      }
    }
    else if (field.isRequired) {
      // 非评分表字段：按 isRequired 配置决定是否必填
      rules[field.fieldCode] = [{ required: true, message: `请填写${field.fieldName}` }]
    }
  }
  return rules
})

defineExpose({
  validate: () => formRef.value?.validate(),
})

const formDataProxy = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v),
})

// TABLE / SCORE_TABLE 字段在数据中不存在时提前初始化，避免渲染时产生副作用
watch(
  () => props.template.fields,
  (fields) => {
    if (!fields?.length) return
    const current = { ...props.modelValue }
    let changed = false
    for (const field of fields) {
      if ((field.fieldType === 'TABLE' || field.fieldType === 'FILE' || field.fieldType === 'FILE_TEMPLATE')
        && !Array.isArray(current[field.fieldCode])) {
        current[field.fieldCode] = []
        changed = true
      }
      if (field.fieldType === 'SCORE_TABLE') {
        const val = current[field.fieldCode] as any
        if (!val || typeof val !== 'object' || !val.scores) {
          current[field.fieldCode] = { scores: val?.scores ?? {} }
          changed = true
        }
      }
    }
    if (changed) emit('update:modelValue', current)
  },
  { immediate: true },
)

// ——— 通用工具 ———

function parseOptions(fieldConfig: unknown): { label: string; value: unknown }[] {
  if (!fieldConfig) return []
  const cfg = typeof fieldConfig === 'string' ? JSON.parse(fieldConfig) : fieldConfig
  const opts = (cfg as any)?.options
  if (Array.isArray(opts)) {
    return opts.map((o: any) => ({ label: o.label ?? o, value: o.value ?? o }))
  }
  return []
}

function formatValue(v: unknown): string {
  if (v === null || v === undefined) return '—'
  if (Array.isArray(v)) return v.join(', ')
  return String(v)
}

function getFileList(fieldCode: string): any[] {
  const val = formDataProxy.value[fieldCode]
  return Array.isArray(val) ? val : []
}

function handleFileUpload(fieldCode: string, options: RequestOption) {
  const { onProgress, onError, onSuccess, fileItem, name = 'file' } = options
  ;(async () => {
    onProgress(20)
    const formData = new FormData()
    formData.append(name as string, fileItem.file as Blob)
    try {
      const res = await uploadReviewFile(formData, '/review/project/')
      onSuccess(res)
      // 把成功上传的文件记录追加到 formData
      const current = getFileList(fieldCode)
      const uploaded = (res as any)?.data
      formDataProxy.value = {
        ...formDataProxy.value,
        [fieldCode]: [...current, { name: fileItem.name, url: uploaded?.url ?? uploaded }],
      }
    }
    catch (error) {
      Message.error('文件上传失败')
      onError(error)
    }
  })()
  return { abort() {} }
}

// ——— TABLE ———

function getTableColumns(field: any): { name: string; code: string; type: string; precision?: number; options?: any[] }[] {
  const config = field.fieldConfig
  if (!config?.columns) return []
  return config.columns.map((col: any) => ({
    name: col.name || col.title || '未命名列',
    code: col.code || col.dataIndex,
    type: col.type || 'TEXT',
    precision: col.precision,
    options: col.options || [],
  }))
}

function getTableRows(fieldCode: string): Record<string, any>[] {
  const val = formDataProxy.value[fieldCode]
  if (!Array.isArray(val)) return []
  return val as Record<string, any>[]
}

function handleAddTableRow(field: any) {
  const config = field.fieldConfig
  if (!config?.columns) return
  const newRow: Record<string, any> = {}
  config.columns.forEach((col: any) => {
    newRow[col.code || col.dataIndex] = col.type === 'NUMBER' ? undefined : ''
  })
  const current = formDataProxy.value
  const rows = Array.isArray(current[field.fieldCode]) ? [...(current[field.fieldCode] as any[])] : []
  formDataProxy.value = { ...current, [field.fieldCode]: [...rows, newRow] }
}

function handleDeleteTableRow(fieldCode: string, rowIndex: number) {
  const rows = [...getTableRows(fieldCode)]
  rows.splice(rowIndex, 1)
  formDataProxy.value = { ...formDataProxy.value, [fieldCode]: rows }
}

function handleMoveTableRowUp(fieldCode: string, rowIndex: number) {
  if (rowIndex <= 0) return
  const rows = [...getTableRows(fieldCode)]
  ;[rows[rowIndex - 1], rows[rowIndex]] = [rows[rowIndex], rows[rowIndex - 1]]
  formDataProxy.value = { ...formDataProxy.value, [fieldCode]: rows }
}

function handleMoveTableRowDown(fieldCode: string, rowIndex: number) {
  const rows = [...getTableRows(fieldCode)]
  if (rowIndex >= rows.length - 1) return
  ;[rows[rowIndex], rows[rowIndex + 1]] = [rows[rowIndex + 1], rows[rowIndex]]
  formDataProxy.value = { ...formDataProxy.value, [fieldCode]: rows }
}

// ——— SCORE_TABLE ———

// 返回评分表的 scores 对象（只读访问，已由 watch 保证初始化）
function getScoreTableScores(fieldCode: string): Record<string, number> {
  const val = formDataProxy.value[fieldCode] as any
  return val?.scores ?? {}
}

// 评分变更时通过 setter 更新父组件
function onScoreChange(fieldCode: string, itemCode: string, val: number | undefined) {
  const current = formDataProxy.value[fieldCode] as any
  formDataProxy.value = {
    ...formDataProxy.value,
    [fieldCode]: { ...current, scores: { ...(current?.scores ?? {}), [itemCode]: val } },
  }
}

function calcTotal(field: any): number {
  const scores = getScoreTableScores(field.fieldCode)
  return (field.fieldConfig?.scoreItems || []).reduce((sum: number, item: any) => sum + (scores[item.itemCode] || 0), 0)
}

function calcMaxTotal(field: any): number {
  if (field.fieldConfig?.scoreMode === 'WEIGHTED') return field.fieldConfig?.totalScore || 100
  return (field.fieldConfig?.scoreItems || []).reduce((sum: number, item: any) => sum + (item.maxScore || 0), 0)
}

function calcWeighted(field: any): number {
  const scores = getScoreTableScores(field.fieldCode)
  return (field.fieldConfig?.scoreItems || []).reduce((sum: number, item: any) => {
    return sum + (scores[item.itemCode] || 0) * (item.weight || 0)
  }, 0)
}

function calcPercentage(field: any): number {
  const maxTotal = calcMaxTotal(field)
  if (maxTotal === 0) return 0
  if (field.fieldConfig?.scoreMode === 'WEIGHTED') return (calcWeighted(field) / maxTotal) * 100
  return (calcTotal(field) / maxTotal) * 100
}

function calcLevel(field: any): string {
  const percentage = calcPercentage(field)
  const passConfig = field.fieldConfig?.passConfig || {}
  let passValue = passConfig.passValue || 60
  let goodValue = passConfig.goodValue || 75
  let excellentValue = passConfig.excellentValue || 85

  if (passConfig.passType === 'FIXED') {
    const maxTotal = calcMaxTotal(field)
    if (maxTotal > 0) {
      passValue = (passValue / maxTotal) * 100
      goodValue = (goodValue / maxTotal) * 100
      excellentValue = (excellentValue / maxTotal) * 100
    }
  }
  if (percentage >= excellentValue) return 'EXCELLENT'
  if (percentage >= goodValue) return 'GOOD'
  if (percentage >= passValue) return 'PASS'
  return 'FAIL'
}

function calcLevelText(field: any): string {
  const map: Record<string, string> = { EXCELLENT: '优秀', GOOD: '良好', PASS: '及格', FAIL: '不及格' }
  return map[calcLevel(field)] || '未评分'
}
</script>

<style lang="scss" scoped>
.readonly-value {
  color: var(--color-text-2);
}

.readonly-file-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.readonly-file-card {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  border: 1px solid var(--color-border-2);
  border-radius: 4px;
  background: var(--color-fill-1);
  color: var(--color-text-1);
  font-size: 13px;
  transition: background 0.15s, border-color 0.15s;
  max-width: 100%;

  &[href]:hover {
    background: var(--color-primary-1);
    border-color: var(--color-primary-4);
    color: var(--color-primary-6);
  }

  .file-card-icon {
    font-size: 15px;
    color: var(--color-primary-6);
    flex-shrink: 0;
  }

  .file-card-name {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .file-card-link-icon {
    font-size: 12px;
    color: var(--color-text-3);
    flex-shrink: 0;
  }
}

// 自定义表格
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

        &:last-child { border-bottom: none; }
        &:hover { background-color: var(--color-fill-1); }
      }
    }

    .table-cell {
      flex: 1;
      padding: 8px 12px;
      border-right: 1px solid var(--color-border-2);
      display: flex;
      align-items: center;

      &:last-child { border-right: none; }

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

// 评分表
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

        &:last-child { border-right: none; }
        &.score-item-name { text-align: left; padding-left: 16px; }
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

        &:last-child { border-bottom: none; }
        &:hover { background-color: #fafbfc; }

        .score-table-cell {
          padding: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          border-right: 1px solid #f0f1f3;

          &:last-child { border-right: none; }

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

              .item-index { color: var(--color-text-3); font-size: 13px; }
              .item-name { font-size: 14px; }
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

        &:last-child { margin-bottom: 0; }

        .summary-label { font-size: 14px; color: #606266; font-weight: 500; }

        .summary-value {
          font-size: 16px;
          font-weight: 600;
          color: #303133;

          &.level-EXCELLENT { color: rgb(var(--success-6)); }
          &.level-GOOD { color: rgb(var(--primary-6)); }
          &.level-PASS { color: rgb(var(--warning-6)); }
          &.level-FAIL { color: rgb(var(--danger-6)); }
        }
      }
    }
  }
}
</style>
