<template>
  <div class="node-form">
    <!-- 第一行：审批模式 + 相关参数 -->
    <a-row :gutter="16" align="start">
      <!-- 审批模式 -->
      <a-col :span="8">
        <a-form-item>
          <template #label>
            <span>审批模式<span class="required-star"> *</span></span>
          </template>
          <!-- 有评分表：固定评分通过，只读 -->
          <a-select
            v-if="hasScoreTable"
            :model-value="ApprovalMode.SCORE_PASS"
            disabled
            style="width: 100%"
          >
            <a-option :value="ApprovalMode.SCORE_PASS">评分通过</a-option>
          </a-select>
          <!-- 无评分表：投票模式可选 -->
          <a-select
            v-else
            v-model="form.approvalMode"
            :disabled="disabled"
            placeholder="请选择审批模式"
            style="width: 100%"
            @change="onModeChange"
          >
            <a-option :value="ApprovalMode.VOTE_ALL_PASS">全部通过</a-option>
            <a-option :value="ApprovalMode.VOTE_MAJORITY_PASS">多数通过</a-option>
            <a-option :value="ApprovalMode.VOTE_ONE_PASS">一票通过</a-option>
          </a-select>
        </a-form-item>
      </a-col>

      <!-- 多数通过比例（投票-多数时） -->
      <a-col v-if="!hasScoreTable && form.approvalMode === ApprovalMode.VOTE_MAJORITY_PASS" :span="8">
        <a-form-item>
          <template #label>
            <span>多数通过比例<span class="required-star"> *</span></span>
          </template>
          <a-input-number
            v-model="form.majorityRatio"
            :disabled="disabled"
            :min="0.01"
            :max="1.00"
            :step="0.01"
            :precision="2"
            placeholder="默认 0.67（2/3）"
            style="width: 100%"
          />
        </a-form-item>
      </a-col>

      <!-- 通过阈值（评分通过时） -->
      <a-col v-if="hasScoreTable" :span="8">
        <a-form-item>
          <template #label>
            <span>通过阈值（0-100）<span class="required-star"> *</span></span>
          </template>
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
    </a-row>

    <!-- 评分表列表（有评分表时显示） -->
    <template v-if="hasScoreTable">
      <div class="section-label">评分表列表</div>

      <!-- 多个评分表：显示计分方式 + 表格 -->
      <template v-if="scoreFields.length > 1">
        <a-row :gutter="16" style="margin-bottom: 8px">
          <a-col :span="8">
            <a-form-item label="计分方式">
              <a-select
                v-model="form.scoreWeightMode"
                :disabled="disabled"
                style="width: 100%"
                @change="emitChange"
              >
                <a-option value="EQUAL">平均（各评分表等权）</a-option>
                <a-option value="WEIGHTED">加权（自定义各评分表权重）</a-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <!-- 加权模式下才显示权重配置表格 -->
        <div v-if="form.scoreWeightMode === 'WEIGHTED'" class="weight-table-wrap">
          <table class="weight-table">
            <thead>
              <tr>
                <th class="col-index">序号</th>
                <th>评分表名称</th>
                <th class="col-weight">权重（0~1，总和须为1.0）</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(f, idx) in scoreFields" :key="f.fieldCode">
                <td class="col-index">{{ idx + 1 }}</td>
                <td>{{ f.fieldName }}</td>
                <td class="col-weight">
                  <a-input-number
                    v-model="form.scoreFieldWeights[f.fieldCode]"
                    :disabled="disabled"
                    :min="0"
                    :max="1"
                    :step="0.05"
                    :precision="2"
                    style="width: 120px"
                  />
                </td>
              </tr>
            </tbody>
          </table>

          <!-- 权重总和提示 -->
          <div class="weight-summary">
            <span>权重总和：{{ totalWeight.toFixed(2) }}</span>
            <a-tag
              :color="Math.abs(totalWeight - 1) < 0.001 ? 'green' : 'orange'"
              size="small"
              style="margin-left: 8px"
            >
              {{ Math.abs(totalWeight - 1) < 0.001 ? '✓ 满足要求' : '请使总和等于 1.0' }}
            </a-tag>
          </div>
        </div>
      </template>

      <!-- 单个评分表：仅显示名称 -->
      <a-table
        v-else-if="scoreFields.length === 1"
        :data="scoreFields"
        :pagination="false"
        size="small"
        :bordered="{ wrapper: true, cell: true }"
        style="margin-bottom: 8px"
      >
        <a-table-column title="序号" :width="60" align="center">
          <template #cell>1</template>
        </a-table-column>
        <a-table-column title="评分表名称" data-index="fieldName" />
      </a-table>
    </template>

    <!-- 审批人数（人员范围已配置后才显示） -->
    <template v-if="hasPersonnel">
      <a-divider style="margin: 8px 0" />
      <a-row :gutter="16">
        <a-col :span="8">
          <a-form-item
            :help="props.maxReviewerCount !== undefined
              ? `范围内共 ${props.maxReviewerCount} 人，请填写所需参与审批的人数`
              : '所需参与审批的人员数量，用于检验人员范围是否满足要求'"
          >
            <template #label>
              <span>审批人数<span class="required-star"> *</span></span>
            </template>
            <a-input-number
              v-model="form.requiredReviewerCount"
              :disabled="disabled"
              :min="1"
              :max="props.maxReviewerCount"
              :precision="0"
              :placeholder="props.maxReviewerCount !== undefined ? `最多 ${props.maxReviewerCount} 人` : '如：3'"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>
      </a-row>
    </template>

  </div>
</template>

<script setup lang="ts">
import { computed, reactive, watch, onMounted } from 'vue'
import {
  type TypeApprovalConfigReq,
  type TypeApprovalConfigResp,
  ApprovalMode,
} from '@/apis/review'

interface ScoreFieldInfo {
  fieldCode: string
  fieldName: string
}

const props = defineProps<{
  nodeScope: string
  nodeLabel: string
  scoreFields: ScoreFieldInfo[]
  hasPersonnel: boolean
  maxReviewerCount?: number
  initialConfig?: TypeApprovalConfigResp
  disabled?: boolean
}>()

const emit = defineEmits<{
  (e: 'change', config: TypeApprovalConfigReq | null): void
}>()

interface FormState {
  requiredReviewerCount: number | undefined
  approvalMode: ApprovalMode | undefined
  majorityRatio: number | undefined
  passThreshold: number | undefined
  scoreWeightMode: 'EQUAL' | 'WEIGHTED'
  scoreFieldWeights: Record<string, number>
}

const form = reactive<FormState>({
  requiredReviewerCount: undefined,
  approvalMode: undefined,
  majorityRatio: 0.67,
  passThreshold: undefined,
  scoreWeightMode: 'EQUAL',
  scoreFieldWeights: {},
})

/** 是否存在评分表 */
const hasScoreTable = computed(() => props.scoreFields.length > 0)

/** 加权模式下权重总和 */
const totalWeight = computed(() => {
  const weights = Object.values(form.scoreFieldWeights) as number[]
  const sum = weights.reduce((acc: number, w: number) => acc + (w || 0), 0)
  return Math.round(sum * 100) / 100
})

/** 切换投票模式时重置多数通过比例 */
const onModeChange = () => {
  form.majorityRatio = 0.67
}

/** 将当前表单配置 emit 出去 */
const emitChange = () => {
  const effectiveMode = hasScoreTable.value ? ApprovalMode.SCORE_PASS : form.approvalMode
  if (!effectiveMode) {
    emit('change', null)
    return
  }

  const config: TypeApprovalConfigReq = {
    nodeScope: props.nodeScope,
    approvalMode: effectiveMode,
    requiredReviewerCount: form.requiredReviewerCount,
  }

  if (effectiveMode === ApprovalMode.VOTE_MAJORITY_PASS) {
    config.majorityRatio = form.majorityRatio
  }

  if (effectiveMode === ApprovalMode.SCORE_PASS) {
    config.passThreshold = form.passThreshold
    if (props.scoreFields.length > 1 && form.scoreWeightMode === 'WEIGHTED') {
      config.scoreTableWeights = { ...form.scoreFieldWeights }
    }
  }

  emit('change', config)
}

/** 从 initialConfig 初始化表单 */
const initFromConfig = () => {
  if (!props.initialConfig) return

  const cfg = props.initialConfig
  form.requiredReviewerCount = cfg.requiredReviewerCount
  form.majorityRatio = cfg.majorityRatio ?? 0.67
  form.passThreshold = cfg.passThreshold

  if (!hasScoreTable.value) {
    form.approvalMode = (cfg.approvalMode !== ApprovalMode.SCORE_PASS)
      ? cfg.approvalMode
      : ApprovalMode.VOTE_ALL_PASS
  }

  if (cfg.scoreTableWeights && Object.keys(cfg.scoreTableWeights).length > 0) {
    form.scoreWeightMode = 'WEIGHTED'
    form.scoreFieldWeights = { ...(cfg.scoreTableWeights as Record<string, number>) }
  }

  emitChange()
}

/** 当评分表列表变化时，自动初始化权重并同步审批模式 */
watch(
  () => props.scoreFields,
  (fields: ScoreFieldInfo[]) => {
    if (fields.length > 0) {
      const equalWeight = Math.round((1 / fields.length) * 100) / 100
      const weights: Record<string, number> = {}
      for (const f of fields) {
        weights[f.fieldCode] = form.scoreFieldWeights[f.fieldCode] ?? equalWeight
      }
      form.scoreFieldWeights = weights
    } else {
      if (!form.approvalMode || form.approvalMode === ApprovalMode.SCORE_PASS) {
        form.approvalMode = ApprovalMode.VOTE_ALL_PASS
      }
      form.scoreFieldWeights = {}
    }
    emitChange()
  },
  { immediate: true, deep: true },
)

// 监听表单变化，自动同步给父组件
watch(() => form.requiredReviewerCount, emitChange)
watch(() => form.approvalMode, emitChange)
watch(() => form.majorityRatio, emitChange)
watch(() => form.passThreshold, emitChange)
watch(() => form.scoreWeightMode, emitChange)
watch(() => form.scoreFieldWeights, emitChange, { deep: true })

onMounted(initFromConfig)
watch(() => props.initialConfig, initFromConfig, { deep: true })
</script>

<style scoped>
.required-star {
  color: rgb(var(--red-6));
}

.node-form {
  padding: 4px 0 8px;
}

.section-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-2);
  margin: 0 0 8px;
  padding-left: 8px;
  border-left: 3px solid rgb(var(--arcoblue-5));
}

.weight-summary {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: var(--color-text-2);
  margin-bottom: 8px;
  padding: 4px 8px;
  background: var(--color-fill-1);
  border-radius: 4px;
}

.weight-table-wrap {
  margin-bottom: 8px;
}

.weight-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
  border: 1px solid var(--color-border-2);
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 8px;
}

.weight-table th,
.weight-table td {
  padding: 8px 12px;
  border: 1px solid var(--color-border-2);
  text-align: left;
}

.weight-table thead tr {
  background: var(--color-fill-2);
  color: var(--color-text-2);
  font-weight: 600;
}

.weight-table .col-index {
  width: 60px;
  text-align: center;
}

.weight-table .col-weight {
  width: 220px;
  text-align: center;
}
</style>
