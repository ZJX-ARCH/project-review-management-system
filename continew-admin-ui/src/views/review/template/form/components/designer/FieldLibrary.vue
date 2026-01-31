<template>
  <div class="field-library">
    <div class="library-header">
      <h3>字段库</h3>
      <p class="tip">点击字段添加到画布</p>
    </div>

    <div class="field-groups">
      <!-- 基础字段 -->
      <div class="field-group">
        <div class="group-title">
          <icon-align-left style="margin-right: 4px;" />
          基础字段
        </div>
        <div class="field-list">
          <div
            v-for="field in basicFields"
            :key="field.type"
            class="field-item"
            @click="handleAddField(field)"
          >
            <component :is="field.icon" :size="18" />
            <span>{{ field.label }}</span>
          </div>
        </div>
      </div>

      <!-- 选择字段 -->
      <div class="field-group">
        <div class="group-title">
          <icon-check-circle style="margin-right: 4px;" />
          选择字段
        </div>
        <div class="field-list">
          <div
            v-for="field in selectionFields"
            :key="field.type"
            class="field-item"
            @click="handleAddField(field)"
          >
            <component :is="field.icon" :size="18" />
            <span>{{ field.label }}</span>
          </div>
        </div>
      </div>

      <!-- 高级字段 -->
      <div class="field-group">
        <div class="group-title">
          <icon-settings style="margin-right: 4px;" />
          高级字段
        </div>
        <div class="field-list">
          <div
            v-for="field in advancedFields"
            :key="field.type"
            class="field-item"
            @click="handleAddField(field)"
          >
            <component :is="field.icon" :size="18" />
            <span>{{ field.label }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  IconAlignLeft,
  IconCheckCircle,
  IconSettings,
} from '@arco-design/web-vue/es/icon'
import type { FormFieldReq } from '@/apis/review'

defineOptions({ name: 'FieldLibrary' })

const emit = defineEmits<{
  (e: 'add-field', field: Partial<FormFieldReq>): void
}>()

// 基础字段
const basicFields = [
  {
    type: 'TEXT',
    label: '单行文本',
    icon: 'icon-edit',
    defaultConfig: {
      placeholder: '请输入',
      maxLength: 100,
    },
  },
  {
    type: 'TEXTAREA',
    label: '多行文本',
    icon: 'icon-file-text',
    defaultConfig: {
      placeholder: '请输入',
      rows: 4,
      maxLength: 500,
      showWordCount: true,
    },
  },
  {
    type: 'NUMBER',
    label: '数字',
    icon: 'icon-number',
    defaultConfig: {
      placeholder: '请输入数字',
      min: 0,
      precision: 0,
    },
  },
  {
    type: 'DATE',
    label: '日期',
    icon: 'icon-calendar',
    defaultConfig: {
      placeholder: '请选择日期',
      format: 'YYYY-MM-DD',
    },
  },
]

// 选择字段
const selectionFields = [
  {
    type: 'SELECT',
    label: '下拉选择',
    icon: 'icon-select-all',
    defaultConfig: {
      placeholder: '请选择',
      options: [
        { label: '选项1', value: 'option1' },
        { label: '选项2', value: 'option2' },
      ],
      allowClear: true,
    },
  },
  {
    type: 'RADIO',
    label: '单选',
    icon: 'icon-check-circle',
    defaultConfig: {
      options: [
        { label: '选项1', value: 'option1' },
        { label: '选项2', value: 'option2' },
      ],
    },
  },
  {
    type: 'CHECKBOX',
    label: '多选',
    icon: 'icon-check-square',
    defaultConfig: {
      options: [
        { label: '选项1', value: 'option1' },
        { label: '选项2', value: 'option2' },
      ],
    },
  },
  {
    type: 'SCORE',
    label: '评分',
    icon: 'icon-star',
    defaultConfig: {
      count: 5,
      allowHalf: false,
      gradeDesc: ['非常差', '差', '一般', '好', '非常好'],
    },
  },
]

// 高级字段
const advancedFields = [
  {
    type: 'FILE',
    label: '文件上传',
    icon: 'icon-upload',
    defaultConfig: {
      maxCount: 5,
      maxSize: 20,
      allowedTypes: ['.pdf', '.docx', '.xlsx', '.jpg', '.png'],
      tips: '支持PDF、Word、Excel、图片格式，单文件最大20MB',
    },
  },
  {
    type: 'FILE_TEMPLATE',
    label: '文件模板',
    icon: 'icon-file',
    defaultConfig: {
      templateFiles: [],
      allowDownload: true,
      allowPreview: false,
      tips: '点击下载模板文件',
    },
  },
  {
    type: 'TABLE',
    label: '动态表格',
    icon: 'icon-table',
    defaultConfig: {
      columns: [
        { name: '列1', code: 'col1', type: 'TEXT', width: 150, required: true },
        { name: '列2', code: 'col2', type: 'TEXT', width: 150, required: false },
      ],
      minRows: 1,
      maxRows: 10,
      allowAdd: true,
      allowDelete: true,
    },
  },
]

// 添加字段
const handleAddField = (fieldTemplate: any) => {
  const newField: Partial<FormFieldReq> = {
    fieldName: fieldTemplate.label,
    fieldCode: `field_${Date.now()}`,
    fieldType: fieldTemplate.type,
    span: 12,
    isRequired: false,
    sort: 0,
    fieldConfig: fieldTemplate.defaultConfig || {},
  }

  emit('add-field', newField)
}
</script>

<style lang="scss" scoped>
.field-library {
  padding: 16px;

  .library-header {
    margin-bottom: 16px;

    h3 {
      margin: 0 0 8px 0;
      font-size: 16px;
      font-weight: 600;
      color: var(--color-text-1);
    }

    .tip {
      margin: 0;
      font-size: 12px;
      color: var(--color-text-3);
    }
  }

  .field-groups {
    .field-group {
      margin-bottom: 20px;

      &:last-child {
        margin-bottom: 0;
      }

      .group-title {
        display: flex;
        align-items: center;
        margin-bottom: 12px;
        font-size: 14px;
        font-weight: 600;
        color: var(--color-text-2);
      }

      .field-list {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 8px;

        .field-item {
          display: flex;
          align-items: center;
          justify-content: center;
          flex-direction: column;
          gap: 6px;
          padding: 12px 8px;
          background: #fff;
          border: 1px solid var(--color-border-2);
          border-radius: 4px;
          cursor: pointer;
          transition: all 0.2s;

          &:hover {
            border-color: rgb(var(--primary-6));
            background: var(--color-primary-light-1);
            transform: translateY(-2px);
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          }

          &:active {
            transform: translateY(0);
          }

          span {
            font-size: 12px;
            color: var(--color-text-2);
            text-align: center;
          }
        }
      }
    }
  }
}
</style>
