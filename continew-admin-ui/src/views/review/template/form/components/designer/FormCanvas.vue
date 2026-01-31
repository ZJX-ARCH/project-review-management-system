<template>
  <div class="form-canvas">
    <!-- 画布头部 -->
    <div class="canvas-header">
      <div class="header-left">
        <h3>表单画布</h3>
        <span class="field-count">{{ fields?.length || 0 }} 个字段</span>
      </div>
      <div class="header-right">
        <a-space>
          <a-tooltip content="栅格列数">
            <span class="config-item">
              <icon-layout /> {{ layoutConfig?.gridCols || 24 }}
            </span>
          </a-tooltip>
          <a-tooltip content="标签宽度">
            <span class="config-item">
              <icon-font-colors /> {{ layoutConfig?.labelWidth || 120 }}px
            </span>
          </a-tooltip>
          <a-tooltip content="标签对齐">
            <span class="config-item">
              <icon-align-left /> {{ layoutConfig?.labelAlign || 'right' }}
            </span>
          </a-tooltip>
        </a-space>
      </div>
    </div>

    <!-- 画布主体 -->
    <div class="canvas-body">
      <!-- 空状态 -->
      <a-empty v-if="!fields || fields.length === 0" description="暂无字段，请从左侧字段库添加">
        <template #image>
          <icon-file style="font-size: 64px; color: var(--color-text-4);" />
        </template>
      </a-empty>

      <!-- 字段列表 -->
      <div v-else class="field-list">
        <a-row :gutter="16">
          <a-col
            v-for="(field, index) in fields"
            :key="index"
            :span="field.span"
            class="field-col"
          >
            <div
              class="field-wrapper"
              :class="{
                selected: selectedFieldIndex === index,
              }"
              @click="handleFieldSelect(index)"
            >
              <div class="field-header">
                <div class="field-info">
                  <icon-drag-arrow class="drag-icon" />
                  <span class="field-name">{{ field.fieldName }}</span>
                  <a-tag size="small" :color="getFieldTypeColor(field.fieldType)">
                    {{ getFieldTypeLabel(field.fieldType) }}
                  </a-tag>
                  <a-tag v-if="field.isRequired" size="small" color="red">必填</a-tag>
                </div>
                <div class="field-actions">
                  <a-button-group size="mini" type="text">
                    <a-button
                      v-if="index > 0"
                      title="上移"
                      @click.stop="handleFieldMove(index, 'up')"
                    >
                      <icon-arrow-up :size="14" />
                    </a-button>
                    <a-button
                      v-if="index < fields.length - 1"
                      title="下移"
                      @click.stop="handleFieldMove(index, 'down')"
                    >
                      <icon-arrow-down :size="14" />
                    </a-button>
                    <a-button
                      status="danger"
                      title="删除"
                      @click.stop="handleFieldRemove(index)"
                    >
                      <icon-delete :size="14" />
                    </a-button>
                  </a-button-group>
                </div>
              </div>

              <div class="field-content">
                <div class="field-preview">
                  <span class="field-label">{{ field.fieldName }}:</span>
                  <span class="field-placeholder">
                    {{ getFieldPlaceholder(field) }}
                  </span>
                </div>
                <div class="field-meta">
                  <span class="meta-item">编码: {{ field.fieldCode }}</span>
                  <span class="meta-item">占位: {{ field.span }}/24</span>
                  <span class="meta-item">排序: {{ field.sort }}</span>
                </div>
              </div>
            </div>
          </a-col>
        </a-row>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Modal } from '@arco-design/web-vue'
import type { FormFieldReq } from '@/apis/review'

defineOptions({ name: 'FormCanvas' })

interface Props {
  fields?: FormFieldReq[]
  layoutConfig?: {
    gridCols: number
    labelWidth: number
    labelAlign: string
  }
  selectedFieldIndex?: number | null
}

const props = withDefaults(defineProps<Props>(), {
  fields: () => [],
  layoutConfig: () => ({
    gridCols: 24,
    labelWidth: 120,
    labelAlign: 'right',
  }),
  selectedFieldIndex: null,
})

const emit = defineEmits<{
  (e: 'update:fields', value: FormFieldReq[]): void
  (e: 'update:layoutConfig', value: any): void
  (e: 'update:selectedFieldIndex', value: number | null): void
  (e: 'field-select', index: number): void
  (e: 'field-remove', index: number): void
}>()

// 字段类型选项
const fieldTypeOptions: Record<string, { label: string, color: string }> = {
  TEXT: { label: '单行文本', color: 'blue' },
  TEXTAREA: { label: '多行文本', color: 'cyan' },
  NUMBER: { label: '数字', color: 'green' },
  DATE: { label: '日期', color: 'orange' },
  SELECT: { label: '下拉选择', color: 'purple' },
  RADIO: { label: '单选', color: 'magenta' },
  CHECKBOX: { label: '多选', color: 'geekblue' },
  SCORE: { label: '评分', color: 'gold' },
  FILE: { label: '文件上传', color: 'red' },
  FILE_TEMPLATE: { label: '文件模板', color: 'pinkpurple' },
  TABLE: { label: '表格', color: 'volcano' },
}

// 获取字段类型标签
const getFieldTypeLabel = (type: string) => {
  return fieldTypeOptions[type]?.label || type
}

// 获取字段类型颜色
const getFieldTypeColor = (type: string) => {
  return fieldTypeOptions[type]?.color || 'gray'
}

// 获取字段占位符
const getFieldPlaceholder = (field: FormFieldReq) => {
  const config = field.fieldConfig || {}
  return config.placeholder || `请输入${field.fieldName}`
}

// 选中字段
const handleFieldSelect = (index: number) => {
  emit('update:selectedFieldIndex', index)
  emit('field-select', index)
}

// 移动字段
const handleFieldMove = (index: number, direction: 'up' | 'down') => {
  if (!props.fields)
    return

  const newFields = [...props.fields]
  const targetIndex = direction === 'up' ? index - 1 : index + 1

  if (targetIndex < 0 || targetIndex >= newFields.length)
    return

  // 交换位置
  [newFields[index], newFields[targetIndex]] = [newFields[targetIndex], newFields[index]]

  // 更新排序值
  newFields.forEach((field, idx) => {
    field.sort = idx + 1
  })

  emit('update:fields', newFields)

  // 更新选中索引
  emit('update:selectedFieldIndex', targetIndex)
}

// 删除字段
const handleFieldRemove = (index: number) => {
  Modal.confirm({
    title: '确认删除',
    content: '确定要删除该字段吗？',
    onOk: () => {
      emit('field-remove', index)
    },
  })
}
</script>

<style lang="scss" scoped>
.form-canvas {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 16px;

  .canvas-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--color-border-2);

    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;

      h3 {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: var(--color-text-1);
      }

      .field-count {
        font-size: 12px;
        color: var(--color-text-3);
      }
    }

    .header-right {
      .config-item {
        display: inline-flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        color: var(--color-text-2);
      }
    }
  }

  .canvas-body {
    flex: 1;
    min-height: 0;
    overflow-y: auto;

    .field-list {
      .field-col {
        margin-bottom: 16px;

        .field-wrapper {
          position: relative;
          padding: 12px;
          background: var(--color-bg-1);
          border: 2px solid var(--color-border-2);
          border-radius: 4px;
          cursor: pointer;
          transition: all 0.2s;

          &:hover {
            border-color: var(--color-border-3);
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
          }

          &.selected {
            border-color: rgb(var(--primary-6));
            background: var(--color-primary-light-1);
            box-shadow: 0 4px 12px rgba(var(--primary-6), 0.2);
          }

          .field-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 8px;

            .field-info {
              display: flex;
              align-items: center;
              gap: 8px;
              flex: 1;
              min-width: 0;

              .drag-icon {
                flex-shrink: 0;
                color: var(--color-text-4);
                cursor: move;
              }

              .field-name {
                font-weight: 500;
                color: var(--color-text-1);
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
              }
            }

            .field-actions {
              flex-shrink: 0;
            }
          }

          .field-content {
            .field-preview {
              margin-bottom: 8px;
              padding: 8px;
              background: #fff;
              border: 1px solid var(--color-border-2);
              border-radius: 2px;
              font-size: 13px;

              .field-label {
                font-weight: 500;
                color: var(--color-text-2);
                margin-right: 8px;
              }

              .field-placeholder {
                color: var(--color-text-4);
              }
            }

            .field-meta {
              display: flex;
              gap: 12px;
              font-size: 12px;
              color: var(--color-text-3);

              .meta-item {
                &::before {
                  content: '•';
                  margin-right: 4px;
                }
              }
            }
          }
        }
      }
    }
  }
}
</style>
