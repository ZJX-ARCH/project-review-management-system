<template>
  <div class="field-config-panel">
    <div class="panel-header">
      <h3>字段配置</h3>
    </div>

    <div class="panel-body">
      <!-- 未选中字段 -->
      <a-empty v-if="!selectedField" description="请选择一个字段进行配置">
        <template #image>
          <icon-settings style="font-size: 48px; color: var(--color-text-4);" />
        </template>
      </a-empty>

      <!-- 已选中字段 -->
      <a-form v-else :model="localField" layout="vertical" size="small">
        <!-- 基本配置 -->
        <div class="config-section">
          <div class="section-title">基本配置</div>

          <a-form-item label="字段名称" required>
            <a-input
              v-model="localField.fieldName"
              placeholder="请输入字段名称"
              @change="handleUpdate"
            />
          </a-form-item>

          <a-form-item label="字段编码" required>
            <a-input
              v-model="localField.fieldCode"
              placeholder="请输入字段编码"
              @change="handleUpdate"
            />
          </a-form-item>

          <a-form-item label="字段类型">
            <a-select v-model="localField.fieldType" disabled>
              <a-option
                v-for="option in fieldTypeOptions"
                :key="option.value"
                :value="option.value"
                :label="option.label"
              />
            </a-select>
          </a-form-item>

          <a-form-item label="栅格占位">
            <a-slider
              v-model="localField.span"
              :min="1"
              :max="24"
              :marks="{ 1: '1', 6: '6', 12: '12', 18: '18', 24: '24' }"
              show-tooltip
              @change="handleUpdate"
            />
          </a-form-item>

          <a-form-item label="是否必填">
            <a-switch
              v-model="localField.isRequired"
              @change="handleUpdate"
            />
          </a-form-item>

          <a-form-item label="排序">
            <a-input-number
              v-model="localField.sort"
              :min="1"
              :style="{ width: '100%' }"
              @change="handleUpdate"
            />
          </a-form-item>
        </div>

        <!-- 字段配置 -->
        <div class="config-section">
          <div class="section-title">字段配置</div>

          <!-- 单行文本 -->
          <template v-if="localField.fieldType === 'TEXT'">
            <a-form-item label="占位提示">
              <a-input
                v-model="localField.fieldConfig.placeholder"
                placeholder="请输入占位提示"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="最大长度">
              <a-input-number
                v-model="localField.fieldConfig.maxLength"
                :min="1"
                :max="5000"
                :style="{ width: '100%' }"
                @change="handleUpdate"
              />
            </a-form-item>
          </template>

          <!-- 多行文本 -->
          <template v-else-if="localField.fieldType === 'TEXTAREA'">
            <a-form-item label="占位提示">
              <a-input
                v-model="localField.fieldConfig.placeholder"
                placeholder="请输入占位提示"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="行数">
              <a-input-number
                v-model="localField.fieldConfig.rows"
                :min="2"
                :max="20"
                :style="{ width: '100%' }"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="最大长度">
              <a-input-number
                v-model="localField.fieldConfig.maxLength"
                :min="1"
                :max="10000"
                :style="{ width: '100%' }"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="显示字数统计">
              <a-switch
                v-model="localField.fieldConfig.showWordCount"
                @change="handleUpdate"
              />
            </a-form-item>
          </template>

          <!-- 数字 -->
          <template v-else-if="localField.fieldType === 'NUMBER'">
            <a-form-item label="占位提示">
              <a-input
                v-model="localField.fieldConfig.placeholder"
                placeholder="请输入占位提示"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="最小值">
              <a-input-number
                v-model="localField.fieldConfig.min"
                :style="{ width: '100%' }"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="最大值">
              <a-input-number
                v-model="localField.fieldConfig.max"
                :style="{ width: '100%' }"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="精度">
              <a-input-number
                v-model="localField.fieldConfig.precision"
                :min="0"
                :max="10"
                :style="{ width: '100%' }"
                @change="handleUpdate"
              />
            </a-form-item>
          </template>

          <!-- 日期 -->
          <template v-else-if="localField.fieldType === 'DATE'">
            <a-form-item label="占位提示">
              <a-input
                v-model="localField.fieldConfig.placeholder"
                placeholder="请输入占位提示"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="日期格式">
              <a-select
                v-model="localField.fieldConfig.format"
                @change="handleUpdate"
              >
                <a-option value="YYYY-MM-DD">年-月-日</a-option>
                <a-option value="YYYY-MM-DD HH:mm:ss">年-月-日 时:分:秒</a-option>
                <a-option value="YYYY-MM">年-月</a-option>
              </a-select>
            </a-form-item>
          </template>

          <!-- 下拉选择/单选/多选 -->
          <template v-else-if="['SELECT', 'RADIO', 'CHECKBOX'].includes(localField.fieldType)">
            <a-form-item v-if="localField.fieldType === 'SELECT'" label="占位提示">
              <a-input
                v-model="localField.fieldConfig.placeholder"
                placeholder="请输入占位提示"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="选项配置">
              <template v-if="localField.fieldConfig.options && localField.fieldConfig.options.length > 0">
                <div
                  v-for="(option, index) in localField.fieldConfig.options"
                  :key="index"
                  class="option-item"
                >
                  <a-input
                    v-model="option.label"
                    placeholder="选项标签"
                    size="small"
                    @input="handleUpdate"
                  />
                  <a-input
                    v-model="option.value"
                    placeholder="选项值"
                    size="small"
                    @input="handleUpdate"
                  />
                  <a-button
                    size="small"
                    status="danger"
                    @click="removeOption(index)"
                  >
                    <icon-delete />
                  </a-button>
                </div>
              </template>
              <a-empty v-else description="暂无选项" :style="{ marginBottom: '12px' }" />
              <a-button size="small" type="dashed" long @click="addOption">
                <icon-plus /> 添加选项
              </a-button>
            </a-form-item>
            <a-form-item v-if="localField.fieldType === 'SELECT'" label="允许清除">
              <a-switch
                v-model="localField.fieldConfig.allowClear"
                @change="handleUpdate"
              />
            </a-form-item>
          </template>

          <!-- 评分 -->
          <template v-else-if="localField.fieldType === 'SCORE'">
            <a-form-item label="星星数量">
              <a-input-number
                v-model="localField.fieldConfig.count"
                :min="1"
                :max="10"
                :style="{ width: '100%' }"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="允许半星">
              <a-switch
                v-model="localField.fieldConfig.allowHalf"
                @change="handleUpdate"
              />
            </a-form-item>
          </template>

          <!-- 文件 -->
          <template v-else-if="localField.fieldType === 'FILE'">
            <a-form-item label="最大文件数">
              <a-input-number
                v-model="localField.fieldConfig.maxCount"
                :min="1"
                :max="50"
                :style="{ width: '100%' }"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="最大文件大小(MB)">
              <a-input-number
                v-model="localField.fieldConfig.maxSize"
                :min="1"
                :max="200"
                :style="{ width: '100%' }"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="提示信息">
              <a-textarea
                v-model="localField.fieldConfig.tips"
                placeholder="请输入提示信息"
                :auto-size="{ minRows: 2, maxRows: 4 }"
                @change="handleUpdate"
              />
            </a-form-item>
          </template>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import type { FormFieldReq } from '@/apis/review'

defineOptions({ name: 'FieldConfigPanel' })

interface Props {
  selectedField?: FormFieldReq | null
  fieldIndex?: number | null
}

const props = withDefaults(defineProps<Props>(), {
  selectedField: null,
  fieldIndex: null,
})

const emit = defineEmits<{
  (e: 'update', field: FormFieldReq): void
}>()

// 字段类型选项
const fieldTypeOptions = [
  { label: '单行文本', value: 'TEXT' },
  { label: '多行文本', value: 'TEXTAREA' },
  { label: '数字', value: 'NUMBER' },
  { label: '日期', value: 'DATE' },
  { label: '下拉选择', value: 'SELECT' },
  { label: '单选', value: 'RADIO' },
  { label: '多选', value: 'CHECKBOX' },
  { label: '评分', value: 'SCORE' },
  { label: '文件上传', value: 'FILE' },
  { label: '动态表格', value: 'TABLE' },
]

// 本地字段数据
const localField = ref<FormFieldReq | null>(null)

// 监听选中字段变化
watch(
  () => props.selectedField,
  (newField) => {
    if (newField) {
      // 深拷贝字段数据
      localField.value = JSON.parse(JSON.stringify(newField))
      // 确保 fieldConfig 存在
      if (!localField.value.fieldConfig) {
        localField.value.fieldConfig = {}
      }
      // 确保选择类字段的options数组存在且格式正确
      if (['SELECT', 'RADIO', 'CHECKBOX'].includes(localField.value.fieldType)) {
        if (!localField.value.fieldConfig.options || !Array.isArray(localField.value.fieldConfig.options)) {
          localField.value.fieldConfig.options = []
        }
        // 确保每个选项都有 label 和 value 属性，过滤掉无效选项
        localField.value.fieldConfig.options = localField.value.fieldConfig.options
          .map((opt: any) => {
            if (typeof opt === 'object' && opt !== null) {
              return {
                label: opt.label || '',
                value: opt.value || '',
              }
            }
            return null
          })
          .filter((opt: any) => opt !== null)
      }
    }
    else {
      localField.value = null
    }
  },
  { immediate: true, deep: true },
)

// 更新字段配置
const handleUpdate = () => {
  if (localField.value) {
    emit('update', localField.value)
  }
}

// 添加选项
const addOption = () => {
  if (!localField.value)
    return

  if (!localField.value.fieldConfig.options) {
    localField.value.fieldConfig.options = []
  }

  localField.value.fieldConfig.options.push({
    label: `选项${localField.value.fieldConfig.options.length + 1}`,
    value: `option${localField.value.fieldConfig.options.length + 1}`,
  })

  handleUpdate()
}

// 删除选项
const removeOption = (index: number) => {
  if (!localField.value || !localField.value.fieldConfig.options)
    return

  localField.value.fieldConfig.options.splice(index, 1)
  handleUpdate()
}
</script>

<style lang="scss" scoped>
.field-config-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 16px;

  .panel-header {
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--color-border-2);

    h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
      color: var(--color-text-1);
    }
  }

  .panel-body {
    flex: 1;
    min-height: 0;
    overflow-y: auto;

    .config-section {
      margin-bottom: 20px;

      .section-title {
        margin-bottom: 12px;
        padding-left: 8px;
        font-size: 14px;
        font-weight: 600;
        color: var(--color-text-2);
        border-left: 3px solid rgb(var(--primary-6));
      }

      .option-item {
        display: flex;
        gap: 8px;
        margin-bottom: 8px;

        :deep(.arco-input) {
          flex: 1;
        }
      }
    }
  }
}
</style>
