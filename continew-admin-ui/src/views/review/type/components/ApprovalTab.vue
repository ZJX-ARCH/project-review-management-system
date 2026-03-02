<template>
  <div class="tab-container">
    <a-alert v-if="!reviewTemplate" type="warning" style="margin-bottom: 16px">
      请先在【流程模板】标签页中选择并保存流程模板，才能配置审批规则。
    </a-alert>
    <a-alert v-else-if="disabled" type="warning" style="margin-bottom: 16px">
      类型已启用，如需修改请先禁用类型。
    </a-alert>
    <a-alert v-else type="info" style="margin-bottom: 16px">
      为每个需要判定的节点配置审批规则。支持投票模式（全部通过/多数通过/一票通过）和评分模式。
    </a-alert>

    <template v-if="reviewTemplate">
      <a-collapse v-model:active-key="activeKeys" :bordered="false">
        <a-collapse-item
          v-for="node in allNodes"
          :key="node.nodeScope"
          :header="node.label"
        >
          <template #extra>
            <a-tag v-if="getConfig(node.nodeScope)" color="green" size="small">已配置</a-tag>
            <a-tag v-else color="gray" size="small">未配置</a-tag>
          </template>

          <ApprovalNodeForm
            :node-scope="node.nodeScope"
            :node-label="node.label"
            :is-acceptance="node.isAcceptance"
            :manage-stages="manageStages"
            :initial-config="getConfig(node.nodeScope)"
            :disabled="disabled"
            @change="onNodeConfigChange(node.nodeScope, $event)"
          />
        </a-collapse-item>
      </a-collapse>

      <div style="margin-top: 16px; text-align: right">
        <a-button type="primary" :loading="saveLoading" :disabled="disabled" @click="handleSave">
          保存审批规则
        </a-button>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { Message } from '@arco-design/web-vue'
import ApprovalNodeForm from './ApprovalNodeForm.vue'
import {
  saveApproval,
  type ProcessTemplateResp,
  type StageResp,
  type TypeApprovalConfigReq,
  type TypeApprovalConfigResp,
} from '@/apis/review'

interface NodeDef {
  nodeScope: string
  label: string
  isAcceptance: boolean
}

const props = defineProps<{
  typeId: number
  approvalConfigs: TypeApprovalConfigResp[]
  reviewTemplate: ProcessTemplateResp | null
  manageStages: StageResp[]
  disabled?: boolean
}>()

const emit = defineEmits<{
  (e: 'saved'): void
}>()

const saveLoading = ref(false)
const activeKeys = ref<string[]>([])

/** 所有需要审批判定的节点（根据评审流程模板推导） */
const allNodes = computed<NodeDef[]>(() => {
  if (!props.reviewTemplate) return []
  const t = props.reviewTemplate
  const nodes: NodeDef[] = []

  // AUDIT 审核轮
  for (let i = 1; i <= (t.auditRounds || 0); i++) {
    const rn = t.roundNames?.find(r => r.roundType === 'AUDIT' && r.roundSequence === i)
    nodes.push({
      nodeScope: `AUDIT_${i}`,
      label: rn ? `审核第${i}轮：${rn.roundName}` : `审核第${i}轮`,
      isAcceptance: false,
    })
  }

  // REVIEW 评审轮
  for (let i = 1; i <= (t.reviewRounds || 0); i++) {
    const rn = t.roundNames?.find(r => r.roundType === 'REVIEW' && r.roundSequence === i)
    nodes.push({
      nodeScope: `REVIEW_${i}`,
      label: rn ? `评审第${i}轮：${rn.roundName}` : `评审第${i}轮`,
      isAcceptance: false,
    })
  }

  // DECISION 决策轮
  for (let i = 1; i <= (t.decisionRounds || 0); i++) {
    const rn = t.roundNames?.find(r => r.roundType === 'DECISION' && r.roundSequence === i)
    nodes.push({
      nodeScope: `DECISION_${i}`,
      label: rn ? `决策第${i}轮：${rn.roundName}` : `决策第${i}轮`,
      isAcceptance: false,
    })
  }

  // ACCEPTANCE 验收（总是最后一个）
  nodes.push({ nodeScope: 'ACCEPTANCE', label: '验收', isAcceptance: true })

  return nodes
})

/** 存储各节点的配置（key = nodeScope） */
const nodeConfigs = ref<Record<string, TypeApprovalConfigReq>>({})

const getConfig = (nodeScope: string) => props.approvalConfigs.find(c => c.nodeScope === nodeScope)

const onNodeConfigChange = (nodeScope: string, config: TypeApprovalConfigReq | null) => {
  if (config) {
    nodeConfigs.value[nodeScope] = config
  }
  else {
    delete nodeConfigs.value[nodeScope]
  }
}

/** 初始化各节点配置 */
const initFromProps = () => {
  const configs: Record<string, TypeApprovalConfigReq> = {}
  for (const cfg of props.approvalConfigs) {
    configs[cfg.nodeScope] = { ...cfg }
  }
  nodeConfigs.value = configs
  // 展开已配置的节点
  activeKeys.value = props.approvalConfigs.map(c => c.nodeScope)
}

/** 保存所有审批规则 */
const handleSave = async () => {
  const reqs = Object.values(nodeConfigs.value)
  if (reqs.length === 0) {
    Message.warning('请至少配置一个节点的审批规则')
    return
  }

  saveLoading.value = true
  try {
    await saveApproval(props.typeId, reqs)
    Message.success('审批规则保存成功')
    emit('saved')
  }
  catch (error) {
    console.error('保存失败:', error)
  }
  finally {
    saveLoading.value = false
  }
}

onMounted(initFromProps)
watch(() => props.approvalConfigs, initFromProps, { deep: true })
</script>

<style scoped>
.tab-container {
  padding: 16px 0;
}
</style>
