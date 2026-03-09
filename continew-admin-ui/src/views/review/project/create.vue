<template>
  <GiPageLayout :body-style="{ overflowY: 'auto' }">
    <a-page-header title="提交新项目申请" @back="router.back()">
      <template #extra>
        <a-button @click="router.back()">取消</a-button>
      </template>
    </a-page-header>

    <!-- 步骤条 -->
    <div class="steps-wrap">
      <a-steps :current="step">
        <a-step title="选择项目类型" />
        <a-step title="填写申请信息" />
      </a-steps>
    </div>

    <!-- 步骤一：基本信息 + 选择类型 -->
    <a-card v-if="step === 1" title="项目基本信息" :bordered="false">
      <a-form ref="basicFormRef" :model="projectForm" layout="vertical" :rules="basicRules">
        <a-form-item label="项目名称" field="projectName" required>
          <a-input v-model="projectForm.projectName" placeholder="请输入项目名称" :max-length="100" show-word-limit />
        </a-form-item>
        <a-form-item label="项目描述" field="description">
          <a-textarea
            v-model="projectForm.description"
            placeholder="请输入项目描述（选填）"
            :max-length="500"
            :auto-size="{ minRows: 3, maxRows: 6 }"
            show-word-limit
          />
        </a-form-item>
        <a-form-item label="项目类型" field="typeId" required>
          <a-select
            v-model="projectForm.typeId"
            placeholder="请选择项目类型"
            :options="typeOptions"
            :loading="typeLoading"
            allow-clear
            style="width: 320px;"
            @change="onTypeChange"
          />
        </a-form-item>
      </a-form>
      <div class="step-actions">
        <a-button type="primary" :loading="nextLoading" @click="onNextStep">
          下一步：填写申请信息
          <template #icon><icon-arrow-right /></template>
        </a-button>
      </div>
    </a-card>

    <!-- 步骤二：填写申请表单 -->
    <a-card v-if="step === 2" title="申请表单" :bordered="false">
      <div v-if="formTemplate && formTemplate.fields?.length">
        <FormRenderer ref="formRendererRef" v-model="formData" :template="formTemplate" />
      </div>
      <a-empty v-else-if="!templateLoading" description="该类型暂无申请表单配置" />
      <div v-else class="template-loading">
        <a-spin tip="加载表单模板中..." />
      </div>
      <div class="step-actions">
        <a-space>
          <a-button @click="step = 1">
            <template #icon><icon-arrow-left /></template>
            上一步
          </a-button>
          <a-button :loading="savingDraft" @click="onSaveDraft">暂存草稿</a-button>
          <a-button type="primary" :loading="submitting" @click="onSubmit">提交申请</a-button>
        </a-space>
      </div>
    </a-card>
  </GiPageLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Message } from '@arco-design/web-vue'
import type { FormInstance } from '@arco-design/web-vue'
import {
  listEnabledTypes,
  getApplicationForm,
  createProject,
  submitProject,
  getProjectDetail,
} from '@/apis/review'
import type { ProjectFormTemplateResp, ProjectTypeResp } from '@/apis/review/type'
import FormRenderer from './components/FormRenderer.vue'

defineOptions({ name: 'ReviewProjectCreate' })

const router = useRouter()
const route = useRoute()

// ——— 状态 ———
const step = ref(1)
const basicFormRef = ref<FormInstance>()
const formRendererRef = ref<{ validate: () => Promise<Record<string, string[]> | undefined> }>()

const projectForm = reactive({
  typeId: undefined as number | undefined,
  projectName: '',
  description: '',
})

const basicRules = {
  projectName: [{ required: true, message: '请输入项目名称' }],
  typeId: [{ required: true, message: '请选择项目类型' }],
}

const formData = ref<Record<string, unknown>>({})
const formTemplate = ref<ProjectFormTemplateResp | null>(null)
// 保持字符串类型，避免雪花 ID 精度丢失
const draftProjectId = ref<string | null>(null)
const typeOptions = ref<{ label: string; value: number }[]>([])
const typeLoading = ref(false)
const templateLoading = ref(false)
const nextLoading = ref(false)
const savingDraft = ref(false)
const submitting = ref(false)

// ——— 初始化：加载类型列表 & 草稿回显 ———
onMounted(async () => {
  typeLoading.value = true
  try {
    const res = await listEnabledTypes()
    typeOptions.value = (res.data ?? []).map((t: ProjectTypeResp) => ({
      label: t.typeName,
      value: t.id,
    }))
  }
  finally {
    typeLoading.value = false
  }

  // 若从"继续编辑"入口携带 draftId，则回显草稿
  const draftId = route.query.draftId
  if (draftId) {
    draftProjectId.value = String(draftId)
    try {
      const res = await getProjectDetail(draftProjectId.value)
      const draft = res.data
      projectForm.projectName = draft.projectName
      projectForm.description = draft.description ?? ''
      projectForm.typeId = draft.typeId
      formData.value = draft.applicationFormData ?? {}
    }
    catch {
      Message.error('加载草稿失败')
    }
  }
})

// 选类型时加载表单模板
const onTypeChange = async (typeId: number | undefined) => {
  if (!typeId) {
    formTemplate.value = null
    return
  }
  templateLoading.value = true
  try {
    const res = await getApplicationForm(typeId)
    formTemplate.value = res.data ?? null
  }
  catch {
    formTemplate.value = null
    Message.warning('获取申请表单失败，请检查类型配置')
  }
  finally {
    templateLoading.value = false
  }
}

// 草稿回显时 typeId 恢复后加载模板
watch(() => projectForm.typeId, (v) => {
  if (v && draftProjectId.value) onTypeChange(v)
}, { once: true })

// ——— 下一步 ———
const onNextStep = async () => {
  const err = await basicFormRef.value?.validate()
  if (err) return
  if (!formTemplate.value) {
    await onTypeChange(projectForm.typeId)
  }
  step.value = 2
}

// ——— 暂存草稿 ———
const onSaveDraft = async () => {
  savingDraft.value = true
  try {
    if (!draftProjectId.value) {
      const res = await createProject({
        typeId: projectForm.typeId!,
        projectName: projectForm.projectName,
        description: projectForm.description,
      })
      draftProjectId.value = res.data
    }
    Message.success('草稿已保存')
    router.replace({ path: '/review/project', query: { t: Date.now() } })
  }
  catch (e) {
    console.error('暂存失败:', e)
  }
  finally {
    savingDraft.value = false
  }
}

// ——— 提交申请 ———
const onSubmit = async () => {
  // 校验必填字段
  const errors = await formRendererRef.value?.validate()
  if (errors) return

  submitting.value = true
  try {
    if (!draftProjectId.value) {
      const res = await createProject({
        typeId: projectForm.typeId!,
        projectName: projectForm.projectName,
        description: projectForm.description,
      })
      draftProjectId.value = res.data
    }

    await submitProject(draftProjectId.value!, { formData: formData.value })
    Message.success('申请提交成功')
    router.replace(`/review/project/detail/${draftProjectId.value}`)
  }
  catch (e) {
    console.error('提交失败:', e)
  }
  finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.steps-wrap {
  padding: 16px 0 24px;
}

.step-actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

.template-loading {
  display: flex;
  justify-content: center;
  padding: 40px 0;
}
</style>
