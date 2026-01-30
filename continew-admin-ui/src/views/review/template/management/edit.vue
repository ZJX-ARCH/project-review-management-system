<template>
  <GiPageLayout>
    <a-page-header
      :title="isCreate ? '新建管理流程模板' : '编辑管理流程模板'"
      @back="handleBack"
    >
      <template #extra>
        <a-space>
          <a-button type="primary" :loading="saveLoading" @click="handleSave">
            保存
          </a-button>
          <a-button @click="handleBack">取消</a-button>
        </a-space>
      </template>
    </a-page-header>

    <a-spin :loading="loading" class="spin-container">
      <div class="edit-container">
        <!-- 基本信息 -->
        <a-card title="基本信息" :bordered="false" class="form-card">
          <a-form
            ref="formRef"
            :model="formData"
            :rules="formRules"
            layout="vertical"
          >
            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="模板名称" field="templateName" required>
                  <a-input
                    v-model="formData.templateName"
                    placeholder="请输入模板名称"
                    :max-length="100"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
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
            </a-row>

            <a-form-item label="模板描述" field="description">
              <a-textarea
                v-model="formData.description"
                placeholder="请输入模板描述"
                :max-length="500"
                :auto-size="{ minRows: 3, maxRows: 6 }"
                show-word-limit
              />
            </a-form-item>
          </a-form>
        </a-card>

        <!-- 阶段配置 -->
        <a-card title="阶段配置" :bordered="false" class="config-card">
          <template #extra>
            <a-button type="primary" @click="handleAddPhase">
              <template #icon><icon-plus /></template>
              添加执行阶段
            </a-button>
          </template>

          <a-alert type="info" style="margin-bottom: 16px;">
            <ul style="margin: 0; padding-left: 20px;">
              <li>立项和验收阶段为固定阶段，不可删除</li>
              <li>可在立项和验收之间添加多个执行阶段</li>
              <li>阶段顺序可通过上移/下移按钮调整</li>
            </ul>
          </a-alert>

          <div v-if="phases.length > 0" class="phase-section">
            <PhaseConfigCard
              v-for="(phase, index) in phases"
              :key="`phase-${index}`"
              :phase="phase"
              :is-first="index === 0"
              :is-last="index === phases.length - 1"
              @update="handlePhaseUpdate(index, $event)"
              @move-up="handleMoveUp(index)"
              @move-down="handleMoveDown(index)"
              @delete="handlePhaseDelete(index)"
            />
          </div>
          <a-empty v-else description="暂无阶段配置，请点击【添加阶段】按钮开始配置" />
        </a-card>

        <!-- 流程预览 -->
        <a-card title="流程预览" :bordered="false" class="preview-card">
          <ManagementFlowVisualization :phases="phases" />
        </a-card>
      </div>
    </a-spin>
  </GiPageLayout>
</template>

<script setup lang="ts">
import { Message } from '@arco-design/web-vue'
import type { FormInstance } from '@arco-design/web-vue'
import { IconPlus } from '@arco-design/web-vue/es/icon'
import PhaseConfigCard from './components/PhaseConfigCard.vue'
import ManagementFlowVisualization from './components/ManagementFlowVisualization.vue'
import {
  createManagementTemplate,
  generateManagementTemplateCode,
  getManagementTemplate,
  updateManagementTemplate,
  type ManagementTemplateReq,
  type StageReq,
  StageType,
} from '@/apis/review'

defineOptions({ name: 'ManagementTemplateEdit' })

const route = useRoute()
const router = useRouter()

// 是否为创建模式
const isCreate = computed(() => !route.params.id)

// 加载状态
const loading = ref(false)
const saveLoading = ref(false)

// 自动生成编码按钮状态
const generateCodeDisabled = ref(false)
const generateCodeCooldown = ref(0)
const generateCodeText = computed(() => {
  return generateCodeCooldown.value > 0 ? `${generateCodeCooldown.value}s` : '自动生成'
})

// 表单引用
const formRef = ref<FormInstance>()

// 表单数据
const formData = reactive<ManagementTemplateReq>({
  templateName: '',
  templateCode: '',
  description: '',
  stages: [],
})

// 表单验证规则
const formRules = {
  templateName: [
    { required: true, message: '请输入模板名称' },
    { minLength: 2, maxLength: 100, message: '模板名称长度为2-100个字符' },
  ],
  templateCode: [
    {
      validator: (value: string, callback: (error?: string) => void) => {
        if (!value) {
          callback()
          return
        }
        const pattern = /^MGMT_[A-Z0-9_]+$/
        if (!pattern.test(value)) {
          callback('模板编码必须以MGMT_开头，后续只能包含大写字母、数字和下划线')
          return
        }
        callback()
      },
    },
  ],
}

// 阶段列表
const phases = ref<StageReq[]>([])

/** 初始化默认阶段（立项和验收） */
const initDefaultPhases = () => {
  phases.value = [
    {
      stageName: '立项阶段',
      stageType: StageType.KICKOFF,
      stageOrder: 1,
      isRequired: true,
    },
    {
      stageName: '验收阶段',
      stageType: StageType.ACCEPTANCE,
      stageOrder: 2,
      isRequired: true,
    },
  ]
}

/** 添加执行阶段 */
const handleAddPhase = () => {
  // 在验收阶段之前插入执行阶段
  const newOrder = phases.value.length
  phases.value.splice(phases.value.length - 1, 0, {
    stageName: `执行阶段${newOrder - 1}`,
    stageType: StageType.EXECUTION,
    stageOrder: newOrder,
    isRequired: true,
  })
  // 重新分配顺序
  reorderPhases()
}

/** 更新阶段 */
const handlePhaseUpdate = (index: number, phase: StageReq) => {
  phases.value[index] = phase
}

/** 上移阶段 */
const handleMoveUp = (index: number) => {
  // 不能移动立项阶段（index=0）
  // 执行阶段不能移动到立项之前（index=1时不能上移）
  if (index <= 1)
    return

  ;[phases.value[index], phases.value[index - 1]] = [phases.value[index - 1], phases.value[index]]

  // 更新顺序
  reorderPhases()
}

/** 下移阶段 */
const handleMoveDown = (index: number) => {
  // 不能移动验收阶段（最后一个）
  // 执行阶段不能移动到验收之后（倒数第二个时不能下移）
  if (index >= phases.value.length - 2)
    return

  ;[phases.value[index], phases.value[index + 1]] = [phases.value[index + 1], phases.value[index]]

  // 更新顺序
  reorderPhases()
}

/** 删除阶段 */
const handlePhaseDelete = (index: number) => {
  phases.value.splice(index, 1)

  // 重新分配顺序
  reorderPhases()
}

/** 重新分配阶段顺序 */
const reorderPhases = () => {
  phases.value.forEach((phase, index) => {
    phase.stageOrder = index + 1
  })
}

/** 验证阶段配置 */
const validatePhases = (): boolean => {
  if (phases.value.length < 2) {
    Message.warning('至少需要立项和验收两个阶段')
    return false
  }

  // 检查第一个必须是立项
  if (phases.value[0].stageType !== StageType.KICKOFF) {
    Message.warning('第一个阶段必须是立项阶段')
    return false
  }

  // 检查最后一个必须是验收
  if (phases.value[phases.value.length - 1].stageType !== StageType.ACCEPTANCE) {
    Message.warning('最后一个阶段必须是验收阶段')
    return false
  }

  // 检查阶段名称不能为空
  for (const phase of phases.value) {
    if (!phase.stageName || phase.stageName.trim() === '') {
      Message.warning('阶段名称不能为空')
      return false
    }
  }

  return true
}

/** 加载详情 */
const loadDetail = async () => {
  if (!route.params.id)
    return

  loading.value = true
  try {
    const res = await getManagementTemplate(route.params.id as string)
    const data = res.data

    Object.assign(formData, {
      templateName: data.templateName,
      templateCode: data.templateCode,
      description: data.description,
    })

    phases.value = data.stages || []
  }
  catch (error) {
    console.error('加载详情失败:', error)
    Message.error('加载详情失败')
  }
  finally {
    loading.value = false
  }
}

/** 生成模板编码 */
const generateCode = async () => {
  try {
    const res = await generateManagementTemplateCode()
    formData.templateCode = res.data
  }
  catch (error) {
    console.error('生成模板编码失败:', error)
  }
}

/** 处理自动生成编码 */
const handleGenerateCode = async () => {
  if (generateCodeDisabled.value)
    return

  await generateCode()

  // 开始5秒冷却
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

/** 保存 */
const handleSave = async () => {
  try {
    // validate() 验证通过时返回 undefined，验证失败时会抛出异常
    await formRef.value?.validate()
  }
  catch {
    Message.warning('请检查表单填写是否完整')
    return
  }

  if (!validatePhases()) {
    return
  }

  saveLoading.value = true
  try {
    // 设置阶段列表
    formData.stages = phases.value

    if (isCreate.value) {
      await createManagementTemplate(formData)
      Message.success('创建成功')
    }
    else {
      await updateManagementTemplate(route.params.id as string, formData)
      Message.success('更新成功')
    }

    handleBack(true)
  }
  catch (error) {
    console.error('保存失败:', error)
  }
  finally {
    saveLoading.value = false
  }
}

/** 返回 */
const handleBack = (needRefresh = false) => {
  if (needRefresh) {
    // 保存成功后，使用 replace 返回并添加时间戳参数触发列表刷新
    router.replace({
      path: '/review/template/management',
      query: { t: Date.now().toString() },
    })
  }
  else {
    router.back()
  }
}

// 初始化
onMounted(async () => {
  if (isCreate.value) {
    await generateCode()
    // 初始化默认阶段（立项和验收）
    initDefaultPhases()
  }
  else {
    await loadDetail()
  }
})
</script>

<style scoped>
.spin-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.spin-container :deep(.arco-spin) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.edit-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

.form-card,
.config-card,
.preview-card {
  margin-bottom: 0;
}

.phase-section {
  margin-top: 16px;
}
</style>
