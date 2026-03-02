<template>
  <div class="node-form">
    <a-row :gutter="16">
      <!-- 审批模式 -->
      <a-col :span="8">
        <a-form-item label="审批模式">
          <a-select
            v-model="form.approvalMode"
            :disabled="disabled"
            placeholder="请选择审批模式"
            style="width: 100%"
            @change="onModeChange"
          >
            <a-option value="VOTE_ALL_PASS">全部通过</a-option>
            <a-option value="VOTE_MAJORITY_PASS">多数通过</a-option>
            <a-option value="VOTE_ONE_PASS">一票通过</a-option>
            <a-option value="SCORE_PASS">评分通过</a-option>
          </a-select>
        </a-form-item>
      </a-col>

      <!-- 多数通过比例 -->
      <a-col v-if="form.approvalMode === 'VOTE_MAJORITY_PASS'" :span="8">
        <a-form-item label="多数通过比例">
          <a-input-number
            v-model="form.majorityRatio"
            :disabled="disabled"
            :min="0.01"
            :max="1.00"
            :step="0.01"
            :precision="2"
            placeholder="默认0.67（2/3）"
            style="width: 100%"
          />
        </a-form-item>
      </a-col>
    </a-row>

    <!-- 评分模式配置 -->
    <template v-if="form.approvalMode === 'SCORE_PASS'">
      <a-divider orientation="left" style="margin: 8px 0">评分配置</a-divider>
      <a-row :gutter="16">
        <a-col :span="6">
          <a-form-item label="通过阈值 *">
            <a-input-number
              v-model="form.passThreshold"
              :disabled="disabled"
              :min="0"
              :max="100"
              :precision="2"
              placeholder="如：60"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="良好阈值">
            <a-input-number
              v-model="form.goodThreshold"
              :disabled="disabled"
              :min="0"
              :max="100"
              :precision="2"
              placeholder="如：75"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="优秀阈值">
            <a-input-number
              v-model="form.excellentThreshold"
              :disabled="disabled"
              :min="0"
              :max="100"
              :precision="2"
              placeholder="如：85"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="计算方式">
            <a-select v-model="form.scoreCalcMethod" :disabled="disabled" placeholder="请选择" style="width: 100%">
              <a-option value="WEIGHTED_AVG">加权平均</a-option>
              <a-option value="AVG">算术平均</a-option>
              <a-option value="MIN">取最低分</a-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16">
        <a-col :span="8">
          <a-form-item label="评审人权重模式">
            <a-select v-model="form.weightMode" :disabled="disabled" placeholder="请选择" style="width: 100%">
              <a-option value="EQUAL">等权重（各评审人平等）</a-option>
              <a-option value="PRESET">预设权重（按用户ID指定）</a-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>

      <!-- PRESET 权重配置 -->
      <template v-if="form.weightMode === 'PRESET'">
        <a-divider orientation="left" style="margin: 8px 0">评审人预设权重</a-divider>
        <div v-for="(w, idx) in form.reviewerWeights" :key="idx" style="display: flex; gap: 8px; margin-bottom: 8px; align-items: center">
          <a-input-number
            v-model="w.userId"
            :disabled="disabled"
            placeholder="用户ID"
            :min="1"
            style="width: 150px"
          />
          <a-input-number
            v-model="w.weight"
            :disabled="disabled"
            placeholder="权重"
            :min="0"
            :max="1"
            :step="0.01"
            :precision="4"
            style="width: 140px"
          />
          <a-button v-if="!disabled" type="text" status="danger" size="small" @click="removeWeight(idx)">删除</a-button>
        </div>
        <a-button v-if="!disabled" type="dashed" size="small" @click="addWeight">
          <template #icon><icon-plus /></template>
          添加评审人权重
        </a-button>
        <div class="hint">所有评审人权重之和必须等于 1.0</div>
      </template>

      <!-- 多 SCORE_TABLE 字段权重 -->
      <a-divider orientation="left" style="margin: 8px 0">评分表字段权重（可选）</a-divider>
      <div class="hint" style="margin-bottom: 8px">若表单中有多个评分表字段，请在此配置各字段的权重（之和=1.0）。只有一个评分表字段时可忽略。</div>
      <div v-for="(entry, idx) in scoreTableWeightEntries" :key="idx" style="display: flex; gap: 8px; margin-bottom: 8px; align-items: center">
        <a-input
          v-model="entry.fieldCode"
          :disabled="disabled"
          placeholder="字段编码（fieldCode）"
          style="width: 200px"
        />
        <a-input-number
          v-model="entry.weight"
          :disabled="disabled"
          placeholder="权重"
          :min="0"
          :max="1"
          :step="0.01"
          :precision="4"
          style="width: 140px"
        />
        <a-button v-if="!disabled" type="text" status="danger" size="small" @click="removeScoreWeight(idx)">删除</a-button>
      </div>
      <a-button v-if="!disabled" type="dashed" size="small" @click="addScoreWeight">
        <template #icon><icon-plus /></template>
        添加评分表字段权重
      </a-button>
    </template>

    <!-- ACCEPTANCE 专属：验收不通过回退目标 -->
    <template v-if="isAcceptance">
      <a-divider orientation="left" style="margin: 8px 0">验收不通过回退配置</a-divider>
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item label="回退到阶段">
            <a-select
              v-model="form.rejectBackTo"
              :disabled="disabled"
              placeholder="选择验收不通过时的回退目标"
              style="width: 100%"
            >
              <a-option value="FIRST_EXECUTION">回到第一个执行阶段</a-option>
              <a-option
                v-for="stage in executionStages"
                :key="stage.stageOrder"
                :value="String(stage.stageOrder)"
              >
                回到：{{ stage.stageName }}（第{{ stage.stageOrder }}阶段）
              </a-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>
    </template>

    <!-- 当前节点配置预览 -->
    <div v-if="form.approvalMode" style="margin-top: 8px; text-align: right">
      <a-button size="small" type="outline" @click="emitChange">应用此配置</a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  type StageResp,
  type TypeApprovalConfigReq,
  type TypeApprovalConfigResp,
  type TypeReviewerWeightReq,
  StageType,
  ApprovalMode,
  WeightMode,
  ScoreCalcMethod,
} from '@/apis/review'

interface ScoreWeightEntry {
  fieldCode: string
  weight: number
}

const props = defineProps<{
  nodeScope: string
  nodeLabel: string
  isAcceptance: boolean
  manageStages: StageResp[]
  initialConfig?: TypeApprovalConfigResp
  disabled?: boolean
}>()

const emit = defineEmits<{
  (e: 'change', config: TypeApprovalConfigReq | null): void
}>()

interface FormState {
  approvalMode: ApprovalMode | undefined
  majorityRatio: number | undefined
  passThreshold: number | undefined
  goodThreshold: number | undefined
  excellentThreshold: number | undefined
  scoreCalcMethod: ScoreCalcMethod | undefined
  weightMode: WeightMode | undefined
  rejectBackTo: string | undefined
  reviewerWeights: TypeReviewerWeightReq[]
}

const form = reactive<FormState>({
  approvalMode: undefined,
  majorityRatio: 0.67,
  passThreshold: undefined,
  goodThreshold: undefined,
  excellentThreshold: undefined,
  scoreCalcMethod: undefined,
  weightMode: undefined,
  rejectBackTo: undefined,
  reviewerWeights: [],
})

const scoreTableWeightEntries = ref<ScoreWeightEntry[]>([])

/** 仅 EXECUTION 类型的阶段，用于 ACCEPTANCE 回退目标选项 */
const executionStages = computed(() =>
  props.manageStages.filter(s => s.stageType === StageType.EXECUTION)
)

/** 切换审批模式时重置相关字段 */
const onModeChange = () => {
  form.majorityRatio = 0.67
  form.passThreshold = undefined
  form.goodThreshold = undefined
  form.excellentThreshold = undefined
  form.scoreCalcMethod = undefined
  form.weightMode = undefined
  form.reviewerWeights = []
  scoreTableWeightEntries.value = []
}

const addWeight = () => form.reviewerWeights.push({ userId: 0, weight: 0 })
const removeWeight = (idx: number) => form.reviewerWeights.splice(idx, 1)

const addScoreWeight = () => scoreTableWeightEntries.value.push({ fieldCode: '', weight: 0 })
const removeScoreWeight = (idx: number) => scoreTableWeightEntries.value.splice(idx, 1)

/** 构建 scoreTableWeights 对象 */
const buildScoreTableWeights = () => {
  if (scoreTableWeightEntries.value.length === 0) return undefined
  const result: Record<string, number> = {}
  for (const entry of scoreTableWeightEntries.value) {
    if (entry.fieldCode) result[entry.fieldCode] = entry.weight
  }
  return Object.keys(result).length > 0 ? result : undefined
}

/** 将当前表单配置 emit 出去 */
const emitChange = () => {
  if (!form.approvalMode) {
    emit('change', null)
    return
  }

  const config: TypeApprovalConfigReq = {
    nodeScope: props.nodeScope,
    approvalMode: form.approvalMode,
  }

  if (form.approvalMode === ApprovalMode.VOTE_MAJORITY_PASS) {
    config.majorityRatio = form.majorityRatio
  }

  if (form.approvalMode === ApprovalMode.SCORE_PASS) {
    config.passThreshold = form.passThreshold
    config.goodThreshold = form.goodThreshold
    config.excellentThreshold = form.excellentThreshold
    config.scoreCalcMethod = form.scoreCalcMethod
    config.weightMode = form.weightMode
    config.scoreTableWeights = buildScoreTableWeights()
    if (form.weightMode === WeightMode.PRESET) {
      config.reviewerWeights = form.reviewerWeights.filter(w => w.userId > 0)
    }
  }

  if (props.isAcceptance) {
    config.rejectBackTo = form.rejectBackTo
  }

  emit('change', config)
}

/** 从 initialConfig 初始化表单 */
const initFromConfig = () => {
  if (!props.initialConfig) return

  const cfg = props.initialConfig
  form.approvalMode = cfg.approvalMode
  form.majorityRatio = cfg.majorityRatio ?? 0.67
  form.passThreshold = cfg.passThreshold
  form.goodThreshold = cfg.goodThreshold
  form.excellentThreshold = cfg.excellentThreshold
  form.scoreCalcMethod = cfg.scoreCalcMethod
  form.weightMode = cfg.weightMode
  form.rejectBackTo = cfg.rejectBackTo
  form.reviewerWeights = cfg.reviewerWeights?.map(w => ({ userId: w.userId, weight: w.weight })) || []

  // 恢复 scoreTableWeights
  if (cfg.scoreTableWeights) {
    scoreTableWeightEntries.value = Object.entries(cfg.scoreTableWeights).map(([fieldCode, weight]) => ({
      fieldCode,
      weight,
    }))
  }
  else {
    scoreTableWeightEntries.value = []
  }

  // 自动触发一次 emit 以同步父组件
  emitChange()
}

// 监听 approvalMode 变化，自动 emit（让父组件保持同步）
watch(() => form.approvalMode, emitChange)
watch(() => form.majorityRatio, emitChange)
watch(() => form.passThreshold, emitChange)
watch(() => form.weightMode, emitChange)
watch(() => form.rejectBackTo, emitChange)

onMounted(initFromConfig)
watch(() => props.initialConfig, initFromConfig, { deep: true })
</script>

<style scoped>
.node-form {
  padding: 8px 0;
}

.hint {
  color: var(--color-text-3);
  font-size: 12px;
  margin-top: 4px;
}
</style>
