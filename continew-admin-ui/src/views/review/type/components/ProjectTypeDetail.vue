<template>
  <a-drawer
    :visible="visible"
    :title="detail?.typeName || '项目类型详情'"
    :width="700"
    :footer="false"
    @cancel="handleClose"
    @update:visible="emit('update:visible', $event)"
  >
    <a-spin :loading="loading">
      <template v-if="detail">
        <!-- 基本信息 -->
        <a-descriptions :column="2" bordered size="medium">
          <a-descriptions-item label="类型名称">{{ detail.typeName }}</a-descriptions-item>
          <a-descriptions-item label="类型编码">
            <a-tag color="arcoblue">{{ detail.typeCode }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="状态">
            <a-tag v-if="detail.status === 0" color="gray">草稿</a-tag>
            <a-tag v-else-if="detail.status === 1" color="green">已启用</a-tag>
            <a-tag v-else color="orange">已禁用</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="排序">{{ detail.sortOrder ?? '—' }}</a-descriptions-item>
          <a-descriptions-item label="创建时间">{{ detail.createTime || '—' }}</a-descriptions-item>
          <a-descriptions-item label="创建人">{{ detail.createUserString || '—' }}</a-descriptions-item>
          <a-descriptions-item v-if="detail.description" label="描述" :span="2">
            {{ detail.description }}
          </a-descriptions-item>
        </a-descriptions>

        <!-- 流程模板 -->
        <div class="section-title">流程模板</div>
        <template v-if="detail.processConfigs?.length">
          <div class="config-grid">
            <div v-for="pc in detail.processConfigs" :key="pc.processType" class="config-item">
              <span class="config-label">{{ pc.processType === 'REVIEW' ? '评审流程' : '管理流程' }}</span>
              <span class="config-value">{{ pc.templateName || `ID: ${pc.templateId}` }}</span>
            </div>
          </div>
        </template>
        <a-empty v-else :image-size="40" description="暂未配置" />

        <!-- 表单映射 -->
        <div class="section-title">表单映射</div>
        <template v-if="detail.formMappings?.length">
          <!-- 评审流程 -->
          <template v-if="reviewFormMappings.length">
            <div class="sub-title">评审流程</div>
            <div class="form-mapping-list">
              <div v-for="fm in reviewFormMappings" :key="`${fm.nodeType}-${fm.nodeSequence}`" class="form-mapping-row">
                <span class="node-name">{{ formNodeLabel(fm.nodeType, fm.nodeSequence) }}</span>
                <a-tag size="small" color="arcoblue">{{ fm.formTemplateName || `模板 ${fm.formTemplateId}` }}</a-tag>
              </div>
            </div>
          </template>
          <!-- 管理流程 -->
          <template v-if="manageFormMappings.length">
            <div class="sub-title">管理流程</div>
            <div class="form-mapping-list">
              <div v-for="fm in manageFormMappings" :key="`${fm.nodeType}-${fm.nodeSequence}`" class="form-mapping-row">
                <span class="node-name">{{ formNodeLabel(fm.nodeType, fm.nodeSequence) }}</span>
                <a-tag size="small" color="purple">{{ fm.formTemplateName || `模板 ${fm.formTemplateId}` }}</a-tag>
              </div>
            </div>
          </template>
        </template>
        <a-empty v-else :image-size="40" description="暂未配置" />

        <!-- 人员范围 -->
        <div class="section-title">人员范围</div>
        <template v-if="detail.personnelConfigs?.length">
          <div
            v-for="[nodeKey, rules] in groupedPersonnel"
            :key="nodeKey"
            class="node-group"
          >
            <div class="node-group-title">{{ nodeKey }}</div>
            <div class="scope-rule-list">
              <div v-for="(rule, i) in rules" :key="i" class="scope-rule-row">
                <a-tag size="small" :color="rule.scopeType === 'USER' ? 'orange' : 'cyan'">
                  {{ scopeTypeLabel(rule.scopeType) }}
                </a-tag>
                <span class="scope-summary">{{ parseScopeConfig(rule.scopeType, rule.scopeConfig) }}</span>
                <span v-if="rule.remark" class="scope-remark">{{ rule.remark }}</span>
              </div>
            </div>
          </div>
        </template>
        <a-empty v-else :image-size="40" description="暂未配置" />

        <!-- 审批规则 -->
        <div class="section-title">审批规则</div>
        <template v-if="detail.approvalConfigs?.length">
          <div class="approval-list">
            <div v-for="ac in detail.approvalConfigs" :key="ac.nodeScope" class="approval-row">
              <span class="node-name">{{ nodeScopeToLabel(ac.nodeScope) }}</span>
              <div class="approval-detail">
                <a-tag size="small" :color="approvalModeColor(ac.approvalMode)">
                  {{ approvalModeLabel(ac.approvalMode) }}
                </a-tag>
                <span v-if="ac.requiredReviewerCount" class="detail-text">审批人数 {{ ac.requiredReviewerCount }}</span>
                <span v-if="ac.approvalMode === 'VOTE_MAJORITY_PASS'" class="detail-text">
                  通过比例 {{ ((ac.majorityRatio ?? 0.67) * 100).toFixed(0) }}%
                </span>
                <span v-if="ac.approvalMode === 'SCORE_PASS' && ac.passThreshold != null" class="detail-text">
                  通过阈值 {{ ac.passThreshold }}
                </span>
              </div>
            </div>
          </div>
        </template>
        <a-empty v-else :image-size="40" description="暂未配置" />
      </template>
    </a-spin>

    <template #footer>
      <a-space style="float: right">
        <a-button v-permission="['review:type:update']" type="primary" @click="handleConfig">
          进入配置
        </a-button>
        <a-button @click="handleClose">关闭</a-button>
      </a-space>
    </template>
  </a-drawer>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { Message } from '@arco-design/web-vue'
import { type ProjectTypeDetailResp, type TypePersonnelConfigResp, getProjectType } from '@/apis/review'

const props = defineProps<{
  visible: boolean
  id?: string | number
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'config', id: string | number): void
}>()

const loading = ref(false)
const detail = ref<ProjectTypeDetailResp | null>(null)

const handleClose = () => emit('update:visible', false)
const handleConfig = () => {
  if (detail.value) {
    emit('config', detail.value.id)
    handleClose()
  }
}

const loadDetail = async (id: string | number) => {
  loading.value = true
  detail.value = null
  try {
    const res = await getProjectType(id)
    detail.value = res.data
  } catch {
    Message.error('加载详情失败')
  } finally {
    loading.value = false
  }
}

watch(
  () => [props.visible, props.id] as const,
  ([visible, id]) => {
    if (visible && id) loadDetail(id)
  },
  { immediate: true },
)

// ── 计算属性 ──────────────────────────────────────────────

const reviewFormMappings = computed(() =>
  (detail.value?.formMappings ?? []).filter(fm => fm.mappingType === 'REVIEW'),
)
const manageFormMappings = computed(() =>
  (detail.value?.formMappings ?? []).filter(fm => fm.mappingType === 'MANAGE'),
)

/** 将人员范围按节点分组，key 为中文节点名 */
const groupedPersonnel = computed(() => {
  const map = new Map<string, TypePersonnelConfigResp[]>()
  for (const pc of detail.value?.personnelConfigs ?? []) {
    const key = personnelNodeLabel(pc.nodeType, pc.nodeSequence)
    if (!map.has(key)) map.set(key, [])
    map.get(key)!.push(pc)
  }
  return map
})

// ── 辅助函数 ──────────────────────────────────────────────

/** 表单映射节点标签 */
function formNodeLabel(nodeType: string, seq?: number): string {
  const labels: Record<string, string> = {
    APPLICATION: '申请节点', AUDIT: `第${seq}轮审核节点`,
    REVIEW: `第${seq}轮评审节点`, DECISION: `第${seq}轮决策节点`,
    STAGE: `第${seq}阶段`,
  }
  return labels[nodeType] ?? nodeType
}

/** 人员范围节点标签 */
function personnelNodeLabel(nodeType: string, seq?: number): string {
  const labels: Record<string, string> = {
    APPLICATION: '申请节点', AUDIT: `第${seq}轮审核节点`,
    REVIEW: `第${seq}轮评审节点`, DECISION: `第${seq}轮决策节点`,
    STAGE: `第${seq}阶段`,
  }
  return labels[nodeType] ?? nodeType
}

/** 审批规则节点标识转中文 */
function nodeScopeToLabel(nodeScope: string): string {
  if (nodeScope === 'ACCEPTANCE') return '验收节点'
  const match = nodeScope.match(/^(AUDIT|REVIEW|DECISION)_(\d+)$/)
  if (match) {
    const typeMap: Record<string, string> = { AUDIT: '审核', REVIEW: '评审', DECISION: '决策' }
    return `第${match[2]}轮${typeMap[match[1]]}节点`
  }
  return nodeScope
}

/** 范围类型标签 */
function scopeTypeLabel(type: string): string {
  return ({ USER: '指定用户', DEPT: '按部门' } as Record<string, string>)[type] ?? type
}

/** 将 scopeConfig JSON 转为可读摘要 */
function parseScopeConfig(scopeType: string, config: string): string {
  try {
    const obj = typeof config === 'string' ? JSON.parse(config) : config
    if (scopeType === 'USER') {
      const count = obj.userIds?.length ?? 0
      return `共 ${count} 人`
    }
    if (scopeType === 'DEPT') {
      const count = obj.deptIds?.length ?? 0
      return `共 ${count} 个部门${obj.includeSub ? '（含子部门）' : ''}`
    }
  } catch {
    // ignore
  }
  return ''
}

const approvalModeLabel = (mode: string) =>
  ({ VOTE_ALL_PASS: '全部通过', VOTE_MAJORITY_PASS: '多数通过', VOTE_ONE_PASS: '一票通过', SCORE_PASS: '评分通过' } as Record<string, string>)[mode] ?? mode

const approvalModeColor = (mode: string) =>
  ({ VOTE_ALL_PASS: 'orange', VOTE_MAJORITY_PASS: 'blue', VOTE_ONE_PASS: 'green', SCORE_PASS: 'red' } as Record<string, string>)[mode] ?? 'gray'
</script>

<style scoped>
.section-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-2);
  margin: 16px 0 8px;
  padding-left: 8px;
  border-left: 3px solid rgb(var(--arcoblue-5));
}

.sub-title {
  font-size: 12px;
  font-weight: 600;
  color: var(--color-text-3);
  margin: 8px 0 4px;
}

/* 流程模板网格 */
.config-grid {
  display: flex;
  gap: 12px;
}

.config-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 10px 12px;
  background: var(--color-fill-1);
  border-radius: 6px;
  border: 1px solid var(--color-border-1);
}

.config-label {
  font-size: 12px;
  color: var(--color-text-3);
}

.config-value {
  font-size: 13px;
  color: var(--color-text-1);
  font-weight: 500;
}

/* 表单映射列表 */
.form-mapping-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 8px;
}

.form-mapping-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 5px 8px;
  background: var(--color-fill-1);
  border-radius: 4px;
}

/* 节点分组（人员范围） */
.node-group {
  margin-bottom: 8px;
}

.node-group-title {
  font-size: 12px;
  font-weight: 600;
  color: var(--color-text-2);
  margin-bottom: 4px;
}

.scope-rule-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding-left: 10px;
  border-left: 2px solid var(--color-border-2);
}

.scope-rule-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  background: var(--color-fill-1);
  border-radius: 4px;
}

.scope-summary {
  font-size: 13px;
  color: var(--color-text-1);
}

.scope-remark {
  font-size: 12px;
  color: var(--color-text-3);
  margin-left: auto;
}

/* 审批规则列表 */
.approval-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.approval-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  background: var(--color-fill-1);
  border-radius: 6px;
}

.node-name {
  font-size: 13px;
  color: var(--color-text-1);
  font-weight: 500;
  min-width: 120px;
}

.approval-detail {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-text {
  font-size: 12px;
  color: var(--color-text-2);
  background: var(--color-fill-2);
  padding: 1px 6px;
  border-radius: 3px;
}
</style>
