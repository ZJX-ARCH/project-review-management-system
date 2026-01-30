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

    <a-spin :loading="loading" style="width: 100%;">
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
                  <a-input-group>
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
              添加阶段
            </a-button>
          </template>

          <a-alert type="info" style="margin-bottom: 16px;">
            <ul style="margin: 0; padding-left: 20px;">
              <li>阶段顺序必须从1开始连续，不能跳号</li>
              <li>建议至少包含立项阶段和验收阶段</li>
              <li>可以添加多个执行阶段</li>
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
          <a-empty v-else description="暂无阶段配置，请点击"添加阶段"按钮开始配置" />
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
}

// 阶段列表
const phases = ref<StageReq[]>([])

/** 添加阶段 */
const handleAddPhase = () => {
  const newOrder = phases.value.length + 1
  phases.value.push({
    stageName: `阶段${newOrder}`,
    stageType: StageType.EXECUTION,
    stageOrder: newOrder,
    isRequired: true,
  })
}

/** 更新阶段 */
const handlePhaseUpdate = (index: number, phase: StageReq) => {
  phases.value[index] = phase
}

/** 上移阶段 */
const handleMoveUp = (index: number) => {
  if (index === 0)
    return

  ;[phases.value[index], phases.value[index - 1]] = [phases.value[index - 1], phases.value[index]]

  // 更新顺序
  reorderPhases()
}

/** 下移阶段 */
const handleMoveDown = (index: number) => {
  if (index === phases.value.length - 1)
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
  if (phases.value.length === 0) {
    Message.warning('请至少添加一个阶段')
    return false
  }

  // 检查顺序是否连续
  const orders = phases.value.map(p => p.stageOrder).sort((a, b) => a - b)
  for (let i = 0; i < orders.length; i++) {
    if (orders[i] !== i + 1) {
      Message.warning('阶段顺序必须从1开始连续')
      return false
    }
  }

  // 检查是否有重复顺序
  const orderSet = new Set(orders)
  if (orderSet.size !== orders.length) {
    Message.warning('阶段顺序不能重复')
    return false
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
  const valid = await formRef.value?.validate()
  if (!valid) {
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

    handleBack()
  }
  catch (error) {
    console.error('保存失败:', error)
  }
  finally {
    saveLoading.value = false
  }
}

/** 返回 */
const handleBack = () => {
  router.back()
}

// 初始化
onMounted(async () => {
  if (isCreate.value) {
    await generateCode()
  }
  else {
    await loadDetail()
  }
})
</script>

<style scoped>
.edit-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
  height: 100%;
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
