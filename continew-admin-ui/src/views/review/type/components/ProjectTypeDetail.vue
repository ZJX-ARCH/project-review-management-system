<template>
  <a-drawer
    :visible="visible"
    :title="detail?.typeName || '项目类型详情'"
    :width="680"
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
          <a-descriptions-item label="描述" :span="2">{{ detail.description || '—' }}</a-descriptions-item>
        </a-descriptions>

        <!-- 流程配置 -->
        <a-divider orientation="left">流程模板配置</a-divider>
        <template v-if="detail.processConfigs?.length">
          <a-descriptions :column="1" bordered size="small">
            <a-descriptions-item
              v-for="pc in detail.processConfigs"
              :key="pc.processType"
              :label="pc.processType === 'REVIEW' ? '评审流程模板' : '管理流程模板'"
            >
              {{ pc.templateName || `ID: ${pc.templateId}` }}
            </a-descriptions-item>
          </a-descriptions>
        </template>
        <a-empty v-else description="暂未配置流程模板" />

        <!-- 表单映射摘要 -->
        <a-divider orientation="left">表单映射（{{ detail.formMappings?.length || 0 }} 个节点）</a-divider>
        <template v-if="detail.formMappings?.length">
          <div class="tag-list">
            <a-tag
              v-for="fm in detail.formMappings"
              :key="`${fm.mappingType}-${fm.nodeType}-${fm.nodeSequence}`"
              :color="fm.mappingType === 'REVIEW' ? 'blue' : 'purple'"
            >
              {{ formMappingLabel(fm) }}
            </a-tag>
          </div>
        </template>
        <a-empty v-else description="暂未配置表单映射" />

        <!-- 人员范围 -->
        <a-divider orientation="left">人员范围配置（{{ detail.personnelConfigs?.length || 0 }}/5 角色）</a-divider>
        <template v-if="detail.personnelConfigs?.length">
          <a-descriptions :column="1" bordered size="small">
            <a-descriptions-item
              v-for="pc in detail.personnelConfigs"
              :key="pc.roleType"
              :label="roleLabel(pc.roleType)"
            >
              <a-space>
                <a-tag>{{ scopeTypeLabel(pc.scopeType) }}</a-tag>
                <span style="color: var(--color-text-3); font-size: 12px">{{ pc.scopeConfig }}</span>
              </a-space>
            </a-descriptions-item>
          </a-descriptions>
        </template>
        <a-empty v-else description="暂未配置人员范围" />

        <!-- 审批规则 -->
        <a-divider orientation="left">审批规则（{{ detail.approvalConfigs?.length || 0 }} 个节点）</a-divider>
        <template v-if="detail.approvalConfigs?.length">
          <a-descriptions :column="1" bordered size="small">
            <a-descriptions-item
              v-for="ac in detail.approvalConfigs"
              :key="ac.nodeScope"
              :label="ac.nodeScope"
            >
              <a-space>
                <a-tag :color="approvalModeColor(ac.approvalMode)">{{ approvalModeLabel(ac.approvalMode) }}</a-tag>
                <span v-if="ac.approvalMode === 'VOTE_MAJORITY_PASS'" style="font-size: 12px">
                  比例={{ (ac.majorityRatio ?? 0.67).toFixed(2) }}
                </span>
                <span v-if="ac.approvalMode === 'SCORE_PASS'" style="font-size: 12px">
                  通过阈值={{ ac.passThreshold }}
                </span>
              </a-space>
            </a-descriptions-item>
          </a-descriptions>
        </template>
        <a-empty v-else description="暂未配置审批规则" />

        <!-- 可见性 -->
        <a-divider orientation="left">可见性配置（{{ detail.visibilityConfigs?.length || 0 }} 条）</a-divider>
        <template v-if="detail.visibilityConfigs?.length">
          <div class="tag-list">
            <a-tag
              v-for="vc in detail.visibilityConfigs"
              :key="`${vc.visibilityType}-${vc.targetId}`"
              :color="vc.visibilityType === 'ALL' ? 'green' : vc.visibilityType === 'DEPT' ? 'blue' : 'orange'"
            >
              {{ visibilityLabel(vc) }}
            </a-tag>
          </div>
        </template>
        <a-empty v-else description="暂未配置可见性" />
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
import { Message } from '@arco-design/web-vue'
import { getProjectType, type ProjectTypeDetailResp } from '@/apis/review'

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
  }
  catch (error) {
    console.error('加载详情失败:', error)
    Message.error('加载详情失败')
  }
  finally {
    loading.value = false
  }
}

watch(
  () => [props.visible, props.id] as const,
  ([visible, id]) => {
    if (visible && id) {
      loadDetail(id)
    }
  },
  { immediate: true }
)

// 辅助函数
const roleLabel = (type: string) => ({
  AUDITOR: '审核人', REVIEWER: '评审人', DECISION_MAKER: '决策人',
  MANAGER: '管理员', ACCEPTANCE_INSPECTOR: '验收人',
}[type] ?? type)

const scopeTypeLabel = (type: string) => ({
  USER: '指定用户', ROLE: '按角色', DEPT: '按部门', COMBINED: '组合规则',
}[type] ?? type)

const approvalModeLabel = (mode: string) => ({
  VOTE_ALL_PASS: '全部通过', VOTE_MAJORITY_PASS: '多数通过',
  VOTE_ONE_PASS: '一票通过', SCORE_PASS: '评分通过',
}[mode] ?? mode)

const approvalModeColor = (mode: string) => ({
  VOTE_ALL_PASS: 'orange', VOTE_MAJORITY_PASS: 'blue',
  VOTE_ONE_PASS: 'green', SCORE_PASS: 'red',
}[mode] ?? 'gray')

const formMappingLabel = (fm: { mappingType: string; nodeType: string; nodeSequence?: number; formTemplateName?: string }) => {
  const nodeLabel = { APPLICATION: '申请', AUDIT: '审核', REVIEW: '评审', DECISION: '决策', STAGE: '阶段' }[fm.nodeType] ?? fm.nodeType
  const seq = fm.nodeSequence ? `_${fm.nodeSequence}` : ''
  return `${nodeLabel}${seq}`
}

const visibilityLabel = (vc: { visibilityType: string; targetId?: number }) => {
  if (vc.visibilityType === 'ALL') return '全部可见'
  if (vc.visibilityType === 'DEPT') return `部门(${vc.targetId})`
  return `用户(${vc.targetId})`
}
</script>

<style scoped>
.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
</style>
