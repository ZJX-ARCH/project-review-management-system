<template>
  <GiPageLayout>
    <a-page-header
      :title="isCreate ? '新建评审流程模板' : '编辑评审流程模板'"
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

            <a-row :gutter="16">
              <a-col :span="8">
                <a-form-item label="审核轮次数量" field="auditRounds" required>
                  <a-input-number
                    v-model="formData.auditRounds"
                    :min="0"
                    :max="10"
                    :style="{ width: '100%' }"
                    @change="handleRoundsChange"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="评审轮次数量" field="reviewRounds" required>
                  <a-input-number
                    v-model="formData.reviewRounds"
                    :min="0"
                    :max="10"
                    :style="{ width: '100%' }"
                    @change="handleRoundsChange"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="决策轮次数量" field="decisionRounds" required>
                  <a-input-number
                    v-model="formData.decisionRounds"
                    :min="1"
                    :max="10"
                    :style="{ width: '100%' }"
                    @change="handleRoundsChange"
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

        <!-- 轮次配置 -->
        <a-card title="轮次配置" :bordered="false" class="config-card">
          <a-alert type="info" style="margin-bottom: 16px;">
            <ul style="margin: 0; padding-left: 20px;">
              <li>审核和评审轮次数量范围：0-10，决策轮次数量范围：1-10</li>
              <li>轮次名称将根据轮次数量自动生成，您可以手动修改</li>
              <li>轮次序号必须从1开始连续，同类型内不能重复</li>
            </ul>
          </a-alert>

          <!-- 审核轮次 -->
          <div v-if="auditRounds.length > 0" class="round-section">
            <h4>审核轮次 ({{ auditRounds.length }})</h4>
            <RoundConfigCard
              v-for="(round, index) in auditRounds"
              :key="`audit-${index}`"
              :round="round"
              :is-first="index === 0"
              :is-last="index === auditRounds.length - 1"
              @update="handleRoundUpdate(RoundType.AUDIT, index, $event)"
              @move-up="handleMoveUp(RoundType.AUDIT, index)"
              @move-down="handleMoveDown(RoundType.AUDIT, index)"
              @delete="handleRoundDelete(RoundType.AUDIT, index)"
            />
          </div>

          <!-- 评审轮次 -->
          <div v-if="reviewRounds.length > 0" class="round-section">
            <h4>评审轮次 ({{ reviewRounds.length }})</h4>
            <RoundConfigCard
              v-for="(round, index) in reviewRounds"
              :key="`review-${index}`"
              :round="round"
              :is-first="index === 0"
              :is-last="index === reviewRounds.length - 1"
              @update="handleRoundUpdate(RoundType.REVIEW, index, $event)"
              @move-up="handleMoveUp(RoundType.REVIEW, index)"
              @move-down="handleMoveDown(RoundType.REVIEW, index)"
              @delete="handleRoundDelete(RoundType.REVIEW, index)"
            />
          </div>

          <!-- 决策轮次 -->
          <div v-if="decisionRounds.length > 0" class="round-section">
            <h4>决策轮次 ({{ decisionRounds.length }})</h4>
            <RoundConfigCard
              v-for="(round, index) in decisionRounds"
              :key="`decision-${index}`"
              :round="round"
              :is-first="index === 0"
              :is-last="index === decisionRounds.length - 1"
              @update="handleRoundUpdate(RoundType.DECISION, index, $event)"
              @move-up="handleMoveUp(RoundType.DECISION, index)"
              @move-down="handleMoveDown(RoundType.DECISION, index)"
              @delete="handleRoundDelete(RoundType.DECISION, index)"
            />
          </div>
        </a-card>

        <!-- 流程预览 -->
        <a-card title="流程预览" :bordered="false" class="preview-card">
          <ProcessFlowVisualization :rounds="allRounds" />
        </a-card>
      </div>
    </a-spin>
  </GiPageLayout>
</template>

<script setup lang="ts">
import { Message } from '@arco-design/web-vue'
import type { FormInstance } from '@arco-design/web-vue'
import RoundConfigCard from './components/RoundConfigCard.vue'
import ProcessFlowVisualization from './components/ProcessFlowVisualization.vue'
import {
  createProcessTemplate,
  generateProcessTemplateCode,
  getProcessTemplate,
  updateProcessTemplate,
  type ProcessTemplateReq,
  type RoundNameReq,
  RoundType,
} from '@/apis/review'

defineOptions({ name: 'ProcessTemplateEdit' })

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
const formData = reactive<ProcessTemplateReq>({
  templateName: '',
  templateCode: '',
  description: '',
  auditRounds: 0,
  reviewRounds: 0,
  decisionRounds: 1,
  roundNames: [],
})

// 表单验证规则
const formRules = {
  templateName: [
    { required: true, message: '请输入模板名称' },
    { minLength: 2, maxLength: 100, message: '模板名称长度为2-100个字符' },
  ],
  auditRounds: [
    { required: true, message: '请输入审核轮次数量' },
    { type: 'number', min: 0, max: 10, message: '审核轮次数量范围为0-10' },
  ],
  reviewRounds: [
    { required: true, message: '请输入评审轮次数量' },
    { type: 'number', min: 0, max: 10, message: '评审轮次数量范围为0-10' },
  ],
  decisionRounds: [
    { required: true, message: '请输入决策轮次数量' },
    { type: 'number', min: 1, max: 10, message: '决策轮次数量范围为1-10' },
  ],
}

// 按类型分组的轮次
const auditRounds = ref<RoundNameReq[]>([])
const reviewRounds = ref<RoundNameReq[]>([])
const decisionRounds = ref<RoundNameReq[]>([])

// 所有轮次（用于预览）
const allRounds = computed(() => {
  return [
    ...auditRounds.value,
    ...reviewRounds.value,
    ...decisionRounds.value,
  ]
})

/** 生成默认轮次名称 */
const generateDefaultRoundName = (type: RoundType, sequence: number): string => {
  const typeMap = {
    [RoundType.AUDIT]: '审核',
    [RoundType.REVIEW]: '评审',
    [RoundType.DECISION]: '决策',
  }
  return `第${sequence}轮${typeMap[type]}`
}

/** 处理轮次数量变化 */
const handleRoundsChange = () => {
  // 审核轮次
  const auditCount = formData.auditRounds
  if (auditRounds.value.length < auditCount) {
    for (let i = auditRounds.value.length + 1; i <= auditCount; i++) {
      auditRounds.value.push({
        roundType: RoundType.AUDIT,
        roundSequence: i,
        roundName: generateDefaultRoundName(RoundType.AUDIT, i),
      })
    }
  }
  else if (auditRounds.value.length > auditCount) {
    auditRounds.value = auditRounds.value.slice(0, auditCount)
  }

  // 评审轮次
  const reviewCount = formData.reviewRounds
  if (reviewRounds.value.length < reviewCount) {
    for (let i = reviewRounds.value.length + 1; i <= reviewCount; i++) {
      reviewRounds.value.push({
        roundType: RoundType.REVIEW,
        roundSequence: i,
        roundName: generateDefaultRoundName(RoundType.REVIEW, i),
      })
    }
  }
  else if (reviewRounds.value.length > reviewCount) {
    reviewRounds.value = reviewRounds.value.slice(0, reviewCount)
  }

  // 决策轮次
  const decisionCount = formData.decisionRounds
  if (decisionRounds.value.length < decisionCount) {
    for (let i = decisionRounds.value.length + 1; i <= decisionCount; i++) {
      decisionRounds.value.push({
        roundType: RoundType.DECISION,
        roundSequence: i,
        roundName: generateDefaultRoundName(RoundType.DECISION, i),
      })
    }
  }
  else if (decisionRounds.value.length > decisionCount) {
    decisionRounds.value = decisionRounds.value.slice(0, decisionCount)
  }
}

/** 更新轮次 */
const handleRoundUpdate = (type: RoundType, index: number, round: RoundNameReq) => {
  if (type === RoundType.AUDIT) {
    auditRounds.value[index] = round
  }
  else if (type === RoundType.REVIEW) {
    reviewRounds.value[index] = round
  }
  else if (type === RoundType.DECISION) {
    decisionRounds.value[index] = round
  }
}

/** 上移轮次 */
const handleMoveUp = (type: RoundType, index: number) => {
  if (index === 0)
    return

  const rounds = type === RoundType.AUDIT ? auditRounds.value : type === RoundType.REVIEW ? reviewRounds.value : decisionRounds.value
  ;[rounds[index], rounds[index - 1]] = [rounds[index - 1], rounds[index]]

  // 更新序号
  rounds[index - 1].roundSequence = index
  rounds[index].roundSequence = index + 1
}

/** 下移轮次 */
const handleMoveDown = (type: RoundType, index: number) => {
  const rounds = type === RoundType.AUDIT ? auditRounds.value : type === RoundType.REVIEW ? reviewRounds.value : decisionRounds.value
  if (index === rounds.length - 1)
    return

  ;[rounds[index], rounds[index + 1]] = [rounds[index + 1], rounds[index]]

  // 更新序号
  rounds[index].roundSequence = index + 1
  rounds[index + 1].roundSequence = index + 2
}

/** 删除轮次 */
const handleRoundDelete = (type: RoundType, index: number) => {
  const rounds = type === RoundType.AUDIT ? auditRounds.value : type === RoundType.REVIEW ? reviewRounds.value : decisionRounds.value
  rounds.splice(index, 1)

  // 更新数量和序号
  if (type === RoundType.AUDIT) {
    formData.auditRounds = rounds.length
  }
  else if (type === RoundType.REVIEW) {
    formData.reviewRounds = rounds.length
  }
  else if (type === RoundType.DECISION) {
    formData.decisionRounds = rounds.length
  }

  // 重新分配序号
  rounds.forEach((round, i) => {
    round.roundSequence = i + 1
  })
}

/** 加载详情 */
const loadDetail = async () => {
  if (!route.params.id)
    return

  loading.value = true
  try {
    const res = await getProcessTemplate(route.params.id as string)
    const data = res.data

    Object.assign(formData, {
      templateName: data.templateName,
      templateCode: data.templateCode,
      description: data.description,
      auditRounds: data.auditRounds,
      reviewRounds: data.reviewRounds,
      decisionRounds: data.decisionRounds,
    })

    // 按类型分组轮次
    auditRounds.value = data.roundNames.filter(r => r.roundType === RoundType.AUDIT)
    reviewRounds.value = data.roundNames.filter(r => r.roundType === RoundType.REVIEW)
    decisionRounds.value = data.roundNames.filter(r => r.roundType === RoundType.DECISION)
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
    const res = await generateProcessTemplateCode()
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

  saveLoading.value = true
  try {
    // 合并所有轮次
    formData.roundNames = allRounds.value

    if (isCreate.value) {
      await createProcessTemplate(formData)
      Message.success('创建成功')
    }
    else {
      await updateProcessTemplate(route.params.id as string, formData)
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
    handleRoundsChange() // 生成默认决策轮次
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

.round-section {
  margin-bottom: 24px;
}

.round-section h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-1);
}
</style>
