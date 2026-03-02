<template>
  <div class="tab-container">
    <a-alert v-if="disabled" type="warning" style="margin-bottom: 16px">
      类型已启用，如需修改请先禁用类型。
    </a-alert>
    <a-alert v-else type="info" style="margin-bottom: 16px">
      为5类角色配置人员范围。全部配置完成后方可启用类型。
    </a-alert>

    <a-spin :loading="saveLoading">
      <a-row :gutter="16">
        <a-col v-for="role in roleConfigs" :key="role.roleType" :span="24" style="margin-bottom: 16px">
          <a-card :bordered="false">
            <template #title>
              <a-space>
                <span>{{ roleLabel(role.roleType) }}</span>
                <a-tag v-if="isRoleConfigured(role)" color="green" size="small">已配置</a-tag>
                <a-tag v-else color="gray" size="small">未配置</a-tag>
              </a-space>
            </template>

            <a-form layout="vertical">
              <a-row :gutter="16">
                <a-col :span="8">
                  <a-form-item label="范围类型">
                    <a-select
                      v-model="role.scopeType"
                      :disabled="disabled"
                      placeholder="请选择范围类型"
                      style="width: 100%"
                      @change="onScopeTypeChange(role)"
                    >
                      <a-option value="USER">指定用户</a-option>
                      <a-option value="ROLE">按系统角色</a-option>
                      <a-option value="DEPT">按部门</a-option>
                      <a-option value="COMBINED">组合规则</a-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :span="16">
                  <a-form-item label="备注">
                    <a-input
                      v-model="role.remark"
                      :disabled="disabled"
                      placeholder="可选备注信息"
                      :max-length="200"
                    />
                  </a-form-item>
                </a-col>
              </a-row>

              <!-- USER 范围 -->
              <template v-if="role.scopeType === 'USER'">
                <a-form-item label="用户ID列表">
                  <a-input-tag
                    v-model="role.parsed.userIds"
                    :disabled="disabled"
                    placeholder="输入用户ID后按回车添加"
                    unique-value
                    style="width: 100%"
                  />
                  <div class="hint">输入系统用户ID，多个ID逐个添加</div>
                </a-form-item>
              </template>

              <!-- DEPT 范围 -->
              <template v-else-if="role.scopeType === 'DEPT'">
                <a-row :gutter="16">
                  <a-col :span="16">
                    <a-form-item label="部门ID列表">
                      <a-input-tag
                        v-model="role.parsed.deptIds"
                        :disabled="disabled"
                        placeholder="输入部门ID后按回车添加"
                        unique-value
                        style="width: 100%"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :span="8">
                    <a-form-item label="包含子部门">
                      <a-switch v-model="role.parsed.includeSub" :disabled="disabled" />
                    </a-form-item>
                  </a-col>
                </a-row>
              </template>

              <!-- ROLE 范围 -->
              <template v-else-if="role.scopeType === 'ROLE'">
                <a-form-item label="业务角色">
                  <a-input-tag
                    v-model="role.parsed.businessRoles"
                    :disabled="disabled"
                    placeholder="输入业务角色名后按回车添加，如：REVIEWER"
                    unique-value
                    style="width: 100%"
                  />
                  <div class="hint">填写系统中定义的业务角色标识</div>
                </a-form-item>
              </template>

              <!-- COMBINED 范围 -->
              <template v-else-if="role.scopeType === 'COMBINED'">
                <a-form-item label="规则配置（JSON）">
                  <a-textarea
                    v-model="role.parsed.combinedJson"
                    :disabled="disabled"
                    placeholder='{"rule":"UNION","conditions":[...]} 或 {"rule":"PROJECT_DEPT_LEADER"}'
                    :auto-size="{ minRows: 3, maxRows: 6 }"
                    style="width: 100%; font-family: monospace"
                  />
                  <div class="hint">UNION: 并集规则；PROJECT_DEPT_LEADER: 动态规则（项目所在部门负责人）</div>
                </a-form-item>
              </template>
            </a-form>
          </a-card>
        </a-col>
      </a-row>

      <div style="text-align: right">
        <a-button type="primary" :loading="saveLoading" :disabled="disabled" @click="handleSave">
          保存人员范围
        </a-button>
      </div>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { Message } from '@arco-design/web-vue'
import {
  savePersonnel,
  type TypePersonnelConfigResp,
  RoleType,
  ScopeType,
} from '@/apis/review'

interface ParsedScope {
  userIds: string[]
  deptIds: string[]
  includeSub: boolean
  businessRoles: string[]
  combinedJson: string
}

interface RoleConfig {
  roleType: RoleType
  scopeType: ScopeType | undefined
  remark: string
  parsed: ParsedScope
}

const props = defineProps<{
  typeId: number
  personnelConfigs: TypePersonnelConfigResp[]
  disabled?: boolean
}>()

const emit = defineEmits<{
  (e: 'saved'): void
}>()

const saveLoading = ref(false)

const defaultParsed = (): ParsedScope => ({
  userIds: [],
  deptIds: [],
  includeSub: false,
  businessRoles: [],
  combinedJson: '',
})

/** 5 种角色顺序固定 */
const roleConfigs = reactive<RoleConfig[]>([
  { roleType: RoleType.AUDITOR, scopeType: undefined, remark: '', parsed: defaultParsed() },
  { roleType: RoleType.REVIEWER, scopeType: undefined, remark: '', parsed: defaultParsed() },
  { roleType: RoleType.DECISION_MAKER, scopeType: undefined, remark: '', parsed: defaultParsed() },
  { roleType: RoleType.MANAGER, scopeType: undefined, remark: '', parsed: defaultParsed() },
  { roleType: RoleType.ACCEPTANCE_INSPECTOR, scopeType: undefined, remark: '', parsed: defaultParsed() },
])

const roleLabel = (type: RoleType) => ({
  [RoleType.AUDITOR]: '审核人（AUDITOR）',
  [RoleType.REVIEWER]: '评审人（REVIEWER）',
  [RoleType.DECISION_MAKER]: '决策人（DECISION_MAKER）',
  [RoleType.MANAGER]: '管理员（MANAGER）',
  [RoleType.ACCEPTANCE_INSPECTOR]: '验收人（ACCEPTANCE_INSPECTOR）',
}[type] ?? type)

const isRoleConfigured = (role: RoleConfig) => !!role.scopeType

/** 切换 scopeType 时重置 parsed */
const onScopeTypeChange = (role: RoleConfig) => {
  role.parsed = defaultParsed()
}

/** 将 parsed 结构序列化为 scopeConfig JSON 字符串 */
const serializeScopeConfig = (role: RoleConfig): string => {
  if (!role.scopeType) return '{}'
  switch (role.scopeType) {
    case ScopeType.USER:
      return JSON.stringify({ userIds: role.parsed.userIds.map(Number).filter(n => !Number.isNaN(n)) })
    case ScopeType.DEPT:
      return JSON.stringify({ deptIds: role.parsed.deptIds.map(Number).filter(n => !Number.isNaN(n)), includeSub: role.parsed.includeSub })
    case ScopeType.ROLE:
      return JSON.stringify({ businessRoles: role.parsed.businessRoles })
    case ScopeType.COMBINED:
      return role.parsed.combinedJson || '{}'
    default:
      return '{}'
  }
}

/** 将 scopeConfig JSON 字符串反序列化到 parsed */
const deserializeScopeConfig = (role: RoleConfig, scopeConfig: string, scopeType: ScopeType) => {
  try {
    const obj = JSON.parse(scopeConfig)
    switch (scopeType) {
      case ScopeType.USER:
        role.parsed.userIds = (obj.userIds || []).map(String)
        break
      case ScopeType.DEPT:
        role.parsed.deptIds = (obj.deptIds || []).map(String)
        role.parsed.includeSub = obj.includeSub ?? false
        break
      case ScopeType.ROLE:
        role.parsed.businessRoles = obj.businessRoles || []
        break
      case ScopeType.COMBINED:
        role.parsed.combinedJson = scopeConfig
        break
    }
  }
  catch {
    if (scopeType === ScopeType.COMBINED) {
      role.parsed.combinedJson = scopeConfig
    }
  }
}

/** 从 props 初始化 */
const initFromProps = () => {
  for (const rc of roleConfigs) {
    const existing = props.personnelConfigs.find(p => p.roleType === rc.roleType)
    if (existing) {
      rc.scopeType = existing.scopeType
      rc.remark = existing.remark || ''
      rc.parsed = defaultParsed()
      deserializeScopeConfig(rc, existing.scopeConfig, existing.scopeType)
    }
    else {
      rc.scopeType = undefined
      rc.remark = ''
      rc.parsed = defaultParsed()
    }
  }
}

/** 保存 */
const handleSave = async () => {
  const unconfigured = roleConfigs.filter(r => !r.scopeType)
  if (unconfigured.length > 0) {
    Message.warning(`以下角色未配置范围：${unconfigured.map(r => roleLabel(r.roleType)).join('、')}`)
    return
  }

  const reqs = roleConfigs.map(r => ({
    roleType: r.roleType,
    scopeType: r.scopeType!,
    scopeConfig: serializeScopeConfig(r),
    remark: r.remark,
  }))

  saveLoading.value = true
  try {
    await savePersonnel(props.typeId, reqs)
    Message.success('人员范围保存成功')
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
watch(() => props.personnelConfigs, initFromProps, { deep: true })
</script>

<style scoped>
.tab-container {
  padding: 16px 0;
}

.hint {
  color: var(--color-text-3);
  font-size: 12px;
  margin-top: 4px;
}
</style>
