<template>
  <GiPageLayout>
    <a-page-header
      :title="isCreate ? '新建表单模板' : '编辑表单模板'"
      @back="handleBack"
    >
      <template #extra>
        <a-space>
          <a-button :loading="previewLoading" @click="handlePreview">
            <template #icon><icon-eye /></template>
            预览
          </a-button>
          <a-button type="primary" :loading="saveLoading" @click="handleSave">
            <template #icon><icon-save /></template>
            保存
          </a-button>
          <a-button @click="handleBack">取消</a-button>
        </a-space>
      </template>
    </a-page-header>

    <a-spin :loading="loading" class="spin-container">
      <div class="designer-container">
        <!-- 基本信息卡片 -->
        <a-card :bordered="false" class="form-card" style="margin-bottom: 16px;">
          <a-form
            ref="formRef"
            :model="formData"
            :rules="formRules"
            layout="inline"
            :label-col-props="{ span: 6 }"
          >
            <a-row :gutter="16" style="width: 100%;">
              <a-col :span="6">
                <a-form-item label="模板名称" field="templateName" required>
                  <a-input
                    v-model="formData.templateName"
                    placeholder="请输入模板名称"
                    :max-length="100"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="6">
                <a-form-item label="模板编码" field="templateCode">
                  <a-input-group v-if="isCreate">
                    <a-input
                      v-model="formData.templateCode"
                      placeholder="请输入模板编码或点击自动生成"
                      :max-length="20"
                      show-word-limit
                    />
                    <a-button
                      :disabled="generateCodeDisabled"
                      @click="handleGenerateCode"
                    >
                      {{ generateCodeText }}
                    </a-button>
                  </a-input-group>
                  <a-input
                    v-else
                    v-model="formData.templateCode"
                    disabled
                    placeholder="模板编码（自动生成）"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="6">
                <a-form-item label="模板类型" field="templateType" required>
                  <a-select
                    v-model="formData.templateType"
                    placeholder="请选择模板类型"
                    :options="templateTypeOptions"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="6">
                <a-form-item label="排序" field="sort">
                  <a-input-number
                    v-model="formData.sort"
                    :min="0"
                    :max="9999"
                    placeholder="排序值"
                    :style="{ width: '100%' }"
                  />
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="16" style="width: 100%; margin-top: 8px;">
              <a-col :span="24">
                <a-form-item label="模板描述" field="description" :label-col-props="{ span: 2 }">
                  <a-textarea
                    v-model="formData.description"
                    placeholder="请输入模板描述"
                    :max-length="500"
                    :auto-size="{ minRows: 2, maxRows: 4 }"
                    show-word-limit
                  />
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </a-card>

        <!-- 三列布局：字段库 + 画布 + 配置面板 -->
        <div class="design-workspace">
          <!-- 左侧：字段库 -->
          <div class="field-library-panel">
            <FieldLibrary @add-field="handleAddField" />
          </div>

          <!-- 中间：表单画布 -->
          <div class="canvas-panel">
            <FormCanvas
              v-model:fields="formData.fields"
              v-model:layout-config="formData.layoutConfig"
              v-model:selected-field-index="selectedFieldIndex"
              @field-select="handleFieldSelect"
              @field-remove="handleFieldRemove"
            />
          </div>

          <!-- 右侧：字段配置面板 -->
          <div class="config-panel">
            <FieldConfigPanel
              :selected-field="selectedField"
              :field-index="selectedFieldIndex"
              @update="handleFieldUpdate"
            />
          </div>
        </div>
      </div>
    </a-spin>

    <!-- 预览弹窗 -->
    <a-modal
      v-model:visible="previewVisible"
      title="表单预览"
      width="90%"
      :footer="false"
      unmount-on-close
      fullscreen
    >
      <FormPreview
        :template-data="formData"
        :template-type-options="templateTypeOptions"
      />
    </a-modal>
  </GiPageLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Message } from '@arco-design/web-vue'
import type { FormInstance } from '@arco-design/web-vue'
import FieldLibrary from './components/designer/FieldLibrary.vue'
import FormCanvas from './components/designer/FormCanvas.vue'
import FieldConfigPanel from './components/designer/FieldConfigPanel.vue'
import FormPreview from './components/designer/FormPreview.vue'
import {
  createFormTemplate,
  generateFormTemplateCode,
  getFormTemplate,
  updateFormTemplate,
  type FormFieldReq,
  type FormTemplateReq,
} from '@/apis/review'

defineOptions({ name: 'FormDesigner' })

const route = useRoute()
const router = useRouter()

// 是否为创建模式
const isCreate = computed(() => !route.params.id)

// 加载状态
const loading = ref(false)
const saveLoading = ref(false)
const previewLoading = ref(false)
const previewVisible = ref(false)

// 表单引用
const formRef = ref<FormInstance>()

// 模板类型选项
const templateTypeOptions = [
  { label: '申请表单', value: 1 },
  { label: '审核表单', value: 2 },
  { label: '评审表单', value: 3 },
  { label: '决策表单', value: 4 },
  { label: '立项阶段管理表单', value: 5 },
  { label: '执行阶段管理表单', value: 6 },
  { label: '验收阶段管理表单', value: 7 },
]

// 表单数据
const formData = ref<FormTemplateReq>({
  templateName: '',
  templateCode: '',
  templateType: 1,
  description: '',
  sort: 1,
  layoutConfig: {
    gridCols: 24,
    labelWidth: 120,
    labelAlign: 'right',
  },
  fields: [],
})

// 表单验证规则
const formRules = {
  templateName: [{ required: true, message: '请输入模板名称' }],
  templateType: [{ required: true, message: '请选择模板类型' }],
}

// 生成编码按钮状态
const generateCodeDisabled = ref(false)
const generateCodeCooldown = ref(0)
const generateCodeText = computed(() => {
  return generateCodeCooldown.value > 0 ? `${generateCodeCooldown.value}s` : '自动生成'
})

// 当前选中的字段索引
const selectedFieldIndex = ref<number | null>(null)

// 当前选中的字段
const selectedField = computed<FormFieldReq | null>(() => {
  if (selectedFieldIndex.value !== null && formData.value.fields) {
    return formData.value.fields[selectedFieldIndex.value] || null
  }
  return null
})

// 加载模板详情
const loadDetail = async () => {
  if (!route.params.id)
    return

  loading.value = true
  try {
    const response = await getFormTemplate(route.params.id as string)
    // 解包响应数据：如果response有data属性则使用data，否则使用response本身
    const data = (response as any).data || response

    formData.value = {
      templateName: data.templateName,
      templateCode: data.templateCode,
      templateType: data.templateType,
      description: data.description,
      sort: data.sort,
      layoutConfig: data.layoutConfig || {
        gridCols: 24,
        labelWidth: 120,
        labelAlign: 'right',
      },
      fields: data.fields || [],
    }
  }
  catch (error) {
    console.error('加载模板详情失败:', error)
    Message.error('加载模板详情失败')
  }
  finally {
    loading.value = false
  }
}

// 生成模板编码
const handleGenerateCode = async () => {
  if (generateCodeDisabled.value)
    return

  try {
    const response = await generateFormTemplateCode()
    // 解包响应数据：如果response有data属性则使用data，否则使用response本身
    const code = (response as any).data || response
    formData.value.templateCode = code
    Message.success('编码生成成功')
  }
  catch (error) {
    console.error('生成编码失败:', error)
    Message.error('编码生成失败')
  }

  // 启动5秒冷却时间
  generateCodeDisabled.value = true
  generateCodeCooldown.value = 5

  const timer = setInterval(() => {
    generateCodeCooldown.value--
    if (generateCodeCooldown.value <= 0) {
      clearInterval(timer)
      generateCodeDisabled.value = false
    }
  }, 1000)
}

// 添加字段
const handleAddField = (fieldTemplate: Partial<FormFieldReq>) => {
  if (!formData.value.fields) {
    formData.value.fields = []
  }

  // 生成唯一的字段编码
  const fieldCount = formData.value.fields.length + 1
  const newField: FormFieldReq = {
    fieldName: fieldTemplate.fieldName || '新字段',
    fieldCode: fieldTemplate.fieldCode || `field_${Date.now()}`,
    fieldType: fieldTemplate.fieldType || 'TEXT',
    span: fieldTemplate.span || 12,
    isRequired: fieldTemplate.isRequired ?? false,
    isVisible: fieldTemplate.isVisible ?? true,
    isReadonly: fieldTemplate.isReadonly ?? false,
    sort: fieldCount,
    fieldConfig: fieldTemplate.fieldConfig || {},
  }

  formData.value.fields.push(newField)

  // 自动选中新添加的字段
  selectedFieldIndex.value = formData.value.fields.length - 1

  Message.success('字段添加成功')
}

// 选中字段
const handleFieldSelect = (index: number) => {
  selectedFieldIndex.value = index
}

// 移除字段
const handleFieldRemove = (index: number) => {
  if (!formData.value.fields)
    return

  formData.value.fields.splice(index, 1)

  // 重新计算排序
  formData.value.fields.forEach((field, idx) => {
    field.sort = idx + 1
  })

  // 清除选中状态
  if (selectedFieldIndex.value === index) {
    selectedFieldIndex.value = null
  }
  else if (selectedFieldIndex.value !== null && selectedFieldIndex.value > index) {
    selectedFieldIndex.value -= 1
  }

  Message.success('字段删除成功')
}

// 更新字段配置
const handleFieldUpdate = (updatedField: FormFieldReq) => {
  if (selectedFieldIndex.value !== null && formData.value.fields) {
    formData.value.fields[selectedFieldIndex.value] = updatedField
  }
}

// 预览
const handlePreview = () => {
  previewVisible.value = true
}

// 保存
const handleSave = async () => {
  try {
    await formRef.value?.validate()
  }
  catch (error) {
    Message.error('请检查必填项')
    return
  }

  if (!formData.value.fields || formData.value.fields.length === 0) {
    Message.warning('请至少添加一个字段')
    return
  }

  saveLoading.value = true
  try {
    if (isCreate.value) {
      await createFormTemplate(formData.value)
      Message.success('创建成功')
    }
    else {
      await updateFormTemplate(route.params.id as string, formData.value)
      Message.success('保存成功')
    }
    handleBack()
  }
  catch (error) {
    console.error('保存失败:', error)
    Message.error('保存失败')
  }
  finally {
    saveLoading.value = false
  }
}

// 返回
const handleBack = () => {
  router.push({
    path: '/review/template/form',
    query: { t: Date.now().toString() },
  })
}

// 初始化
onMounted(() => {
  if (!isCreate.value) {
    loadDetail()
  }
})
</script>

<style lang="scss" scoped>
.designer-container {
  height: 100%;
  display: flex;
  flex-direction: column;

  .form-card {
    flex-shrink: 0;
  }

  .design-workspace {
    flex: 1;
    display: flex;
    gap: 16px;
    min-height: 0;
    overflow: hidden;

    .field-library-panel {
      width: 240px;
      flex-shrink: 0;
      overflow-y: auto;
      background: var(--color-bg-2);
      border-radius: 4px;
    }

    .canvas-panel {
      flex: 1;
      min-width: 0;
      overflow-y: auto;
      background: #fff;
      border-radius: 4px;
    }

    .config-panel {
      width: 360px;
      flex-shrink: 0;
      overflow-y: auto;
      background: var(--color-bg-2);
      border-radius: 4px;
    }
  }
}

.spin-container {
  height: calc(100vh - 120px);
  overflow: hidden;
}
</style>
