<!--
  FormRenderer：根据 ProjectFormTemplateResp 动态渲染可填写表单。
  支持字段类型：TEXT / TEXTAREA / NUMBER / DATE / SELECT / RADIO / CHECKBOX / SCORE / FILE
-->
<template>
  <a-form :model="formDataProxy" layout="vertical">
    <a-row :gutter="16">
      <a-col
        v-for="field in template.fields"
        :key="field.fieldCode"
        :span="field.span ?? 24"
      >
        <a-form-item
          :label="field.fieldName"
          :field="field.fieldCode"
          :required="field.isRequired"
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
              :key="opt.value"
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

          <!-- 评分 -->
          <a-rate
            v-else-if="field.fieldType === 'SCORE'"
            v-model="formDataProxy[field.fieldCode]"
            :disabled="readonly"
            allow-half
          />

          <!-- 文件上传（简化：只展示提示） -->
          <template v-else-if="field.fieldType === 'FILE' || field.fieldType === 'FILE_TEMPLATE'">
            <a-upload
              v-if="!readonly"
              multiple
              @change="(fileList: any) => onFileChange(field.fieldCode, fileList)"
            >
              <template #upload-button>
                <a-button>
                  <template #icon><icon-upload /></template>
                  上传文件
                </a-button>
              </template>
            </a-upload>
            <span v-else class="readonly-value">{{ formatValue(formDataProxy[field.fieldCode]) }}</span>
          </template>

          <!-- 其他类型：文本展示 -->
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
import { computed } from 'vue'
import type { ProjectFormTemplateResp } from '@/apis/review/type'

interface Props {
  modelValue: Record<string, unknown>
  template: ProjectFormTemplateResp
  readonly?: boolean
}

interface Emits {
  (e: 'update:modelValue', value: Record<string, unknown>): void
}

const props = withDefaults(defineProps<Props>(), {
  readonly: false,
})
const emit = defineEmits<Emits>()

const formDataProxy = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v),
})

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

function onFileChange(fieldCode: string, fileList: any[]) {
  formDataProxy.value = {
    ...formDataProxy.value,
    [fieldCode]: fileList.map(f => f.url ?? f.name),
  }
}
</script>

<style scoped>
.readonly-value {
  color: var(--color-text-2);
}
</style>
