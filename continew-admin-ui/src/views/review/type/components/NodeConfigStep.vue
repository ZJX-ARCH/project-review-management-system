<template>
  <div class="node-config-step">
    <!-- 无模板提示 -->
    <a-alert v-if="!props.reviewTemplate && !props.manageTemplate" type="warning" style="margin-bottom: 16px">
      请先在【流程模板】步骤中选择并保存流程模板，才能配置节点。
    </a-alert>
    <a-alert v-else-if="disabled" type="warning" style="margin-bottom: 16px">
      类型已启用，如需修改请先禁用类型。
    </a-alert>

    <a-spin :loading="saveLoading">
      <!-- 评审阶段 -->
      <template v-if="reviewNodes.length > 0">
        <div class="phase-header">
          <span class="phase-title">评审阶段</span>
          <a-space size="mini">
            <a-link @click="expandedReviewKeys = reviewNodes.map(n => n.key)">全部展开</a-link>
            <a-divider direction="vertical" />
            <a-link @click="expandedReviewKeys = []">全部收起</a-link>
          </a-space>
        </div>
        <a-collapse :active-key="expandedReviewKeys" :bordered="false" class="node-collapse" @change="(keys) => expandedReviewKeys = keys">
          <a-collapse-item v-for="node in reviewNodes" :key="node.key">
            <template #header>
              <a-space>
                <a-tag :color="nodeTagColor(node)" size="small">{{ nodeTagLabel(node) }}</a-tag>
                <span>{{ node.label }}</span>
                <a-tag v-if="isNodeConfigured(node)" color="green" size="small">已配置</a-tag>
                <a-tag v-else color="arcoblue" size="small">待配置</a-tag>
              </a-space>
            </template>
            <!-- 表单模板：独占一行 -->
            <a-form layout="vertical" style="margin-bottom: 4px">
              <a-form-item label="表单模板">
                <a-select
                  v-model="formSelections[node.key]"
                  :options="formOptions[node.templateType] || []"
                  :disabled="disabled"
                  allow-clear
                  placeholder="请选择表单模板"
                  style="width: 100%"
                />
              </a-form-item>
            </a-form>
            <!-- 人员范围 -->
            <a-divider orientation="left">{{ node.personnelLabel }}</a-divider>
            <ScopeRuleList
              v-model="nodePersonnel[node.key]"
              :disabled="disabled"
              :role-id="node.roleId"
            />
            <!-- 审批规则 -->
            <template v-if="node.hasApproval">
              <a-divider orientation="left">审批规则</a-divider>
              <ApprovalNodeForm
                :node-scope="node.approvalNodeScope"
                :node-label="node.label"
                :is-acceptance="node.stageType === 'ACCEPTANCE'"
                :manage-stages="props.manageTemplate?.stages || []"
                :initial-config="props.approvalConfigs.find(a => a.nodeScope === node.approvalNodeScope)"
                :disabled="disabled"
                @change="(cfg) => { nodeApproval[node.approvalNodeScope] = cfg }"
              />
            </template>
          </a-collapse-item>
        </a-collapse>
      </template>

      <!-- 管理阶段 -->
      <template v-if="manageNodes.length > 0">
        <div class="phase-header" :style="reviewNodes.length > 0 ? 'margin-top: 16px' : ''">
          <span class="phase-title">管理阶段</span>
          <a-space size="mini">
            <a-link @click="expandedManageKeys = manageNodes.map(n => n.key)">全部展开</a-link>
            <a-divider direction="vertical" />
            <a-link @click="expandedManageKeys = []">全部收起</a-link>
          </a-space>
        </div>
        <a-collapse :active-key="expandedManageKeys" :bordered="false" class="node-collapse" @change="(keys) => expandedManageKeys = keys">
          <a-collapse-item v-for="node in manageNodes" :key="node.key">
            <template #header>
              <a-space>
                <a-tag :color="nodeTagColor(node)" size="small">{{ nodeTagLabel(node) }}</a-tag>
                <span>{{ node.label }}</span>
                <a-tag v-if="isNodeConfigured(node)" color="green" size="small">已配置</a-tag>
                <a-tag v-else color="arcoblue" size="small">待配置</a-tag>
              </a-space>
            </template>
            <!-- 表单模板：独占一行 -->
            <a-form layout="vertical" style="margin-bottom: 4px">
              <a-form-item label="表单模板">
                <a-select
                  v-model="formSelections[node.key]"
                  :options="formOptions[node.templateType] || []"
                  :disabled="disabled"
                  allow-clear
                  placeholder="请选择表单模板"
                  style="width: 100%"
                />
              </a-form-item>
            </a-form>
            <!-- 人员范围 -->
            <a-divider orientation="left">{{ node.personnelLabel }}</a-divider>
            <ScopeRuleList
              v-model="nodePersonnel[node.key]"
              :disabled="disabled"
              :role-id="node.roleId"
            />
            <!-- 审批规则 -->
            <template v-if="node.hasApproval">
              <a-divider orientation="left">审批规则</a-divider>
              <ApprovalNodeForm
                :node-scope="node.approvalNodeScope"
                :node-label="node.label"
                :is-acceptance="node.stageType === 'ACCEPTANCE'"
                :manage-stages="props.manageTemplate?.stages || []"
                :initial-config="props.approvalConfigs.find(a => a.nodeScope === node.approvalNodeScope)"
                :disabled="disabled"
                @change="(cfg) => { nodeApproval[node.approvalNodeScope] = cfg }"
              />
            </template>
          </a-collapse-item>
        </a-collapse>
      </template>

      <!-- 保存按钮 -->
      <div v-if="allNodes.length > 0" style="margin-top: 16px; text-align: right">
        <a-button type="primary" :loading="saveLoading" :disabled="disabled" @click="handleSave">
          保存节点配置
        </a-button>
      </div>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { Message } from '@arco-design/web-vue'
import ScopeRuleList from './ScopeRuleList.vue'
import { type ScopeConfig, defaultScopeConfig, deserializeScopeConfig, serializeScopeConfig } from './scope-config'
import ApprovalNodeForm from './ApprovalNodeForm.vue'
import { listRole } from '@/apis/system/role'
import type { RoleResp } from '@/apis/system/role'
import {
  type FormTemplateResp,
  type ManagementTemplateResp,
  type ProcessTemplateResp,
  TemplateType,
  type TypeApprovalConfigReq,
  type TypeApprovalConfigResp,
  type TypeFormMappingResp,
  type TypePersonnelConfigResp,
  listEnabledFormTemplate,
  saveApproval,
  saveFormMapping,
  savePersonnel,
} from '@/apis/review'

interface NodeDef {
  key: string
  label: string
  nodeType: string
  nodeSequence?: number
  stageType?: string
  templateType: number
  mappingType: string
  personnelLabel: string
  hasApproval: boolean
  approvalNodeScope: string
  roleCode: string
  roleId: string
}

const props = defineProps<{
  typeId: string | number
  formMappings: TypeFormMappingResp[]
  personnelConfigs: TypePersonnelConfigResp[]
  approvalConfigs: TypeApprovalConfigResp[]
  reviewTemplate: ProcessTemplateResp | null
  manageTemplate: ManagementTemplateResp | null
  disabled?: boolean
}>()

const emit = defineEmits<{
  (e: 'saved'): void
}>()

// 表单模板选项（按 templateType 分组）
const formOptions = ref<Record<number, { label: string, value: number }[]>>({})

// 表单选择（key -> formTemplateId）
const formSelections = reactive<Record<string, number | undefined>>({})

// 人员范围（key -> ScopeConfig[]，一个节点可配置多条规则）
const nodePersonnel = reactive<Record<string, ScopeConfig[]>>({})

// 审批规则（nodeScope -> TypeApprovalConfigReq | null）
const nodeApproval = reactive<Record<string, TypeApprovalConfigReq | null>>({})

const saveLoading = ref(false)

// 角色编码 → 角色ID 映射（从后端加载）
const roleCodeToId = ref<Record<string, string>>({})

const loadRoles = async () => {
  try {
    const res = await listRole({ sort: [] })
    const map: Record<string, string> = {}
    for (const role of (res.data || []) as RoleResp[]) {
      map[role.code] = role.id
    }
    roleCodeToId.value = map
  }
  catch (error) {
    console.error('加载角色列表失败:', error)
  }
}

// 折叠面板展开状态（key 为 string | number，与 Arco Design 类型一致）
const expandedReviewKeys = ref<(string | number)[]>([])
const expandedManageKeys = ref<(string | number)[]>([])

/** 按照流程节点顺序构建节点列表 */
const allNodes = computed<NodeDef[]>(() => {
  const nodes: NodeDef[] = []
  const t = props.reviewTemplate

  if (t) {
    nodes.push({
      key: 'APPLICATION',
      label: '申请节点',
      nodeType: 'APPLICATION',
      nodeSequence: undefined,
      templateType: TemplateType.APPLICATION,
      mappingType: 'REVIEW',
      personnelLabel: '申请人范围',
      hasApproval: false,
      approvalNodeScope: '',
      roleCode: 'APPLICANT',
      roleId: roleCodeToId.value['APPLICANT'] || '',
    })

    for (let i = 1; i <= (t.auditRounds || 0); i++) {
      const rn = t.roundNames?.find((r) => r.roundType === 'AUDIT' && r.roundSequence === i)
      nodes.push({
        key: `AUDIT_${i}`,
        label: rn ? `审核第${i}轮：${rn.roundName}` : `审核第${i}轮`,
        nodeType: 'AUDIT',
        nodeSequence: i,
        templateType: TemplateType.AUDIT,
        mappingType: 'REVIEW',
        personnelLabel: '审核人范围',
        hasApproval: true,
        approvalNodeScope: `AUDIT_${i}`,
        roleCode: 'AUDITOR',
        roleId: roleCodeToId.value['AUDITOR'] || '',
      })
    }

    for (let i = 1; i <= (t.reviewRounds || 0); i++) {
      const rn = t.roundNames?.find((r) => r.roundType === 'REVIEW' && r.roundSequence === i)
      nodes.push({
        key: `REVIEW_${i}`,
        label: rn ? `评审第${i}轮：${rn.roundName}` : `评审第${i}轮`,
        nodeType: 'REVIEW',
        nodeSequence: i,
        templateType: TemplateType.REVIEW,
        mappingType: 'REVIEW',
        personnelLabel: '评审人范围',
        hasApproval: true,
        approvalNodeScope: `REVIEW_${i}`,
        roleCode: 'REVIEWER',
        roleId: roleCodeToId.value['REVIEWER'] || '',
      })
    }

    for (let i = 1; i <= (t.decisionRounds || 0); i++) {
      const rn = t.roundNames?.find((r) => r.roundType === 'DECISION' && r.roundSequence === i)
      nodes.push({
        key: `DECISION_${i}`,
        label: rn ? `决策第${i}轮：${rn.roundName}` : `决策第${i}轮`,
        nodeType: 'DECISION',
        nodeSequence: i,
        templateType: TemplateType.DECISION,
        mappingType: 'REVIEW',
        personnelLabel: '决策人范围',
        hasApproval: true,
        approvalNodeScope: `DECISION_${i}`,
        roleCode: 'DECISION_MAKER',
        roleId: roleCodeToId.value['DECISION_MAKER'] || '',
      })
    }
  }

  for (const stage of props.manageTemplate?.stages || []) {
    const stageTemplateMap: Record<string, number> = {
      KICKOFF: TemplateType.KICKOFF,
      EXECUTION: TemplateType.EXECUTION,
      ACCEPTANCE: TemplateType.ACCEPTANCE,
    }
    const personnelLabelMap: Record<string, string> = {
      KICKOFF: '管理人范围',
      EXECUTION: '管理人范围',
      ACCEPTANCE: '验收人范围',
    }
    const stageRoleCodeMap: Record<string, string> = {
      KICKOFF: 'MANAGER',
      EXECUTION: 'MANAGER',
      ACCEPTANCE: 'ACCEPTANCE_INSPECTOR',
    }
    const stageRoleCode = stageRoleCodeMap[stage.stageType] ?? 'MANAGER'
    nodes.push({
      key: `STAGE_${stage.stageOrder}`,
      label: stage.stageName,
      nodeType: 'STAGE',
      nodeSequence: stage.stageOrder,
      stageType: stage.stageType,
      templateType: stageTemplateMap[stage.stageType] ?? TemplateType.EXECUTION,
      mappingType: 'MANAGE',
      personnelLabel: personnelLabelMap[stage.stageType] ?? '管理人范围',
      hasApproval: stage.stageType === 'ACCEPTANCE',
      approvalNodeScope: stage.stageType === 'ACCEPTANCE' ? 'ACCEPTANCE' : '',
      roleCode: stageRoleCode,
      roleId: roleCodeToId.value[stageRoleCode] || '',
    })
  }

  return nodes
})

/** 评审阶段节点（APPLICATION / AUDIT / REVIEW / DECISION） */
const reviewNodes = computed(() => allNodes.value.filter((n) => n.mappingType === 'REVIEW'))

/** 管理阶段节点（STAGE） */
const manageNodes = computed(() => allNodes.value.filter((n) => n.mappingType === 'MANAGE'))

/** 加载表单模板选项 */
const loadFormOptions = async () => {
  const types = [
    TemplateType.APPLICATION,
    TemplateType.AUDIT,
    TemplateType.REVIEW,
    TemplateType.DECISION,
    TemplateType.KICKOFF,
    TemplateType.EXECUTION,
    TemplateType.ACCEPTANCE,
  ]
  try {
    const results = await Promise.all(types.map((t) => listEnabledFormTemplate(t)))
    const options: Record<number, { label: string, value: number }[]> = {}
    types.forEach((t, idx) => {
      const data: FormTemplateResp[] = results[idx].data || []
      options[t] = data.map((f) => ({ label: f.templateName, value: f.id }))
    })
    formOptions.value = options
  } catch (error) {
    console.error('加载表单模板选项失败:', error)
  }
}

/** 从 props 初始化各节点的状态 */
const fillFromProps = () => {
  for (const node of allNodes.value) {
    // 表单映射
    const fm = props.formMappings.find((m) =>
      m.mappingType === node.mappingType
      && m.nodeType === node.nodeType
      && (m.nodeSequence ?? null) === (node.nodeSequence ?? null),
    )
    formSelections[node.key] = fm?.formTemplateId

    // 人员范围：按 nodeKey 分组，一个节点可对应多条规则
    const pcs = props.personnelConfigs.filter((p) =>
      p.nodeType === node.nodeType
      && (p.nodeSequence ?? null) === (node.nodeSequence ?? null),
    )
    if (pcs.length > 0) {
      const newRules: ScopeConfig[] = pcs.map((pc) => ({
        scopeType: pc.scopeType,
        remark: pc.remark || '',
        parsed: deserializeScopeConfig(pc.scopeConfig, pc.scopeType),
      }))
      if (nodePersonnel[node.key]) {
        nodePersonnel[node.key].splice(0, nodePersonnel[node.key].length, ...newRules)
      } else {
        nodePersonnel[node.key] = newRules
      }
    } else {
      if (!nodePersonnel[node.key]) {
        nodePersonnel[node.key] = []
      }
      // 已存在时不重置（保留用户的未保存编辑）
    }

    // 审批规则
    if (node.hasApproval) {
      const ac = props.approvalConfigs.find((a) => a.nodeScope === node.approvalNodeScope)
      nodeApproval[node.approvalNodeScope] = ac ?? null
    }
  }
}

/** 节点标签颜色（STAGE 按阶段类型细分，与管理模板配色一致） */
const nodeTagColor = (node: NodeDef): string => {
  if (node.nodeType === 'STAGE') {
    return { KICKOFF: 'green', EXECUTION: 'blue', ACCEPTANCE: 'orange' }[node.stageType ?? ''] ?? 'gray'
  }
  return { APPLICATION: 'arcoblue', AUDIT: 'orange', REVIEW: 'green', DECISION: 'red' }[node.nodeType] ?? 'gray'
}

/** 节点标签文字（STAGE 按阶段类型细分，与管理模板一致） */
const nodeTagLabel = (node: NodeDef): string => {
  if (node.nodeType === 'STAGE') {
    return { KICKOFF: '立项', EXECUTION: '执行', ACCEPTANCE: '验收' }[node.stageType ?? ''] ?? '阶段'
  }
  return { APPLICATION: '申请', AUDIT: '审核', REVIEW: '评审', DECISION: '决策' }[node.nodeType] ?? node.nodeType
}

/** 判断节点是否已完成基本配置 */
const isNodeConfigured = (node: NodeDef) => {
  const hasForm = !!formSelections[node.key]
  const rules: ScopeConfig[] = nodePersonnel[node.key] || []
  const hasPersonnel = rules.length > 0 && rules.some((r) => !!r.scopeType)
  return hasForm && hasPersonnel
}

/** 保存所有节点配置 */
const handleSave = async () => {
  // 构建表单映射请求
  const formReqs = allNodes.value
    .filter((n) => formSelections[n.key])
    .map((n) => ({
      mappingType: n.mappingType,
      nodeType: n.nodeType,
      nodeSequence: n.nodeSequence,
      formTemplateId: formSelections[n.key]!,
    }))

  // 构建人员范围请求（展开每个节点的多条规则，过滤未设置 scopeType 的条目）
  const personnelReqs = allNodes.value.flatMap((n: NodeDef) => {
    const rules: ScopeConfig[] = nodePersonnel[n.key] || []
    return rules
      .filter((r: ScopeConfig) => r.scopeType)
      .map((r: ScopeConfig) => ({
        nodeType: n.nodeType,
        nodeSequence: n.nodeSequence,
        scopeType: r.scopeType!,
        scopeConfig: serializeScopeConfig(r),
        remark: r.remark,
      }))
  })

  // 构建审批规则请求（过滤掉未设置的）
  const approvalReqs = Object.entries(nodeApproval)
    .filter(([, v]) => v?.approvalMode)
    .map(([nodeScope, v]) => ({ nodeScope, ...v! }))

  saveLoading.value = true
  try {
    await Promise.all([
      formReqs.length > 0 ? saveFormMapping(props.typeId, formReqs) : Promise.resolve(),
      personnelReqs.length > 0 ? savePersonnel(props.typeId, personnelReqs) : Promise.resolve(),
      approvalReqs.length > 0 ? saveApproval(props.typeId, approvalReqs) : Promise.resolve(),
    ])
    Message.success('节点配置保存成功')
    emit('saved')
  } catch (error) {
    console.error('保存失败:', error)
    Message.error('保存失败，请检查网络或联系管理员')
  } finally {
    saveLoading.value = false
  }
}

onMounted(async () => {
  await loadRoles()
  await loadFormOptions()
  fillFromProps()
})

watch(
  [() => props.formMappings, () => props.personnelConfigs, () => props.approvalConfigs],
  () => { fillFromProps() },
  { deep: true },
)

watch(
  [() => props.reviewTemplate, () => props.manageTemplate],
  () => { nextTick(() => fillFromProps()) },
)
</script>

<style scoped>
.node-config-step {
  padding: 16px 0;
}

.phase-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.phase-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-1);
  padding-left: 10px;
  border-left: 3px solid rgb(var(--arcoblue-6));
}

.node-collapse :deep(.arco-collapse-item) {
  margin-bottom: 8px;
  border: 1px solid var(--color-border-2);
  border-radius: 4px;
}

.node-collapse :deep(.arco-collapse-item-header) {
  background-color: var(--color-fill-2);
  border-radius: 4px;
}

.node-collapse :deep(.arco-collapse-item-content) {
  padding: 16px;
}
</style>
