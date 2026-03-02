<template>
  <GiPageLayout>
    <a-page-header
      :title="isCreate ? '新建项目类型' : '项目类型配置'"
      @back="handleBack"
    >
      <template #subtitle>
        <template v-if="!isCreate && detail">
          <span class="type-code">{{ detail.typeCode }}</span>
          <a-tag :color="statusTagColor" style="margin-left: 8px">{{ statusTagText }}</a-tag>
        </template>
      </template>
      <template #extra>
        <a-space v-if="!isCreate && detail">
          <a-button
            v-if="detail.status !== 1"
            v-permission="['review:type:status']"
            type="primary"
            status="success"
            :loading="enableLoading"
            @click="handleEnable"
          >
            启用类型
          </a-button>
          <a-button
            v-if="detail.status === 1"
            v-permission="['review:type:status']"
            status="warning"
            :loading="disableLoading"
            @click="handleDisable"
          >
            禁用类型
          </a-button>
          <a-button @click="handleBack">返回列表</a-button>
        </a-space>
        <a-button v-else @click="handleBack">取消</a-button>
      </template>
    </a-page-header>

    <a-spin :loading="loading" class="spin-container">
      <div class="config-body">
        <!-- 新建模式：只显示基本信息表单 -->
        <template v-if="isCreate">
          <a-card title="基本信息" :bordered="false">
            <a-form ref="basicFormRef" :model="basicForm" :rules="basicFormRules" layout="vertical">
              <a-row :gutter="16">
                <a-col :span="12">
                  <a-form-item label="类型名称" field="typeName" required>
                    <a-input v-model="basicForm.typeName" placeholder="请输入类型名称" :max-length="100" />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item label="类型编码" field="typeCode">
                    <a-input-group>
                      <a-input
                        v-model="basicForm.typeCode"
                        placeholder="留空自动生成，格式：TYPE_XXX"
                        :max-length="50"
                      />
                      <a-button :disabled="generateCodeDisabled" @click="handleGenerateCode">
                        {{ generateCodeCooldown > 0 ? `${generateCodeCooldown}s` : '自动生成' }}
                      </a-button>
                    </a-input-group>
                  </a-form-item>
                </a-col>
              </a-row>
              <a-row :gutter="16">
                <a-col :span="12">
                  <a-form-item label="排序" field="sortOrder">
                    <a-input-number v-model="basicForm.sortOrder" :min="0" placeholder="数字越小越靠前" style="width: 100%" />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item label="描述" field="description">
                    <a-input v-model="basicForm.description" placeholder="请输入类型描述" :max-length="500" />
                  </a-form-item>
                </a-col>
              </a-row>
            </a-form>
            <div style="margin-top: 16px; text-align: right">
              <a-button type="primary" :loading="saveBasicLoading" @click="handleCreateAndContinue">
                创建并继续配置
              </a-button>
            </div>
          </a-card>
        </template>

        <!-- 编辑模式：多 Tab 配置 -->
        <template v-else>
          <a-tabs v-model:active-key="activeTab" class="config-tabs">
            <!-- Tab 1：基本信息 -->
            <a-tab-pane key="basic" title="基本信息">
              <a-card :bordered="false" style="margin-top: 8px">
                <a-form ref="basicFormRef" :model="basicForm" :rules="basicFormRules" layout="vertical">
                  <a-row :gutter="16">
                    <a-col :span="12">
                      <a-form-item label="类型名称" field="typeName" required>
                        <a-input v-model="basicForm.typeName" placeholder="请输入类型名称" :max-length="100" />
                      </a-form-item>
                    </a-col>
                    <a-col :span="12">
                      <a-form-item label="类型编码">
                        <a-input v-model="basicForm.typeCode" disabled placeholder="类型编码（创建后不可修改）" />
                      </a-form-item>
                    </a-col>
                  </a-row>
                  <a-row :gutter="16">
                    <a-col :span="12">
                      <a-form-item label="排序" field="sortOrder">
                        <a-input-number v-model="basicForm.sortOrder" :min="0" placeholder="数字越小越靠前" style="width: 100%" />
                      </a-form-item>
                    </a-col>
                    <a-col :span="12">
                      <a-form-item label="描述" field="description">
                        <a-input v-model="basicForm.description" placeholder="请输入类型描述" :max-length="500" />
                      </a-form-item>
                    </a-col>
                  </a-row>
                </a-form>
                <div style="margin-top: 16px; text-align: right">
                  <a-button type="primary" :loading="saveBasicLoading" @click="handleSaveBasic">保存基本信息</a-button>
                </div>
              </a-card>
            </a-tab-pane>

            <!-- Tab 2：流程模板 -->
            <a-tab-pane key="process" title="流程模板">
              <ProcessTab
                v-if="typeId"
                :type-id="typeId"
                :process-configs="detail?.processConfigs || []"
                :disabled="detail?.status === 1"
                @saved="onSubConfigSaved"
              />
            </a-tab-pane>

            <!-- Tab 3：表单映射 -->
            <a-tab-pane key="form" title="表单映射">
              <FormMappingTab
                v-if="typeId"
                :type-id="typeId"
                :form-mappings="detail?.formMappings || []"
                :review-template="reviewTemplate"
                :manage-template="manageTemplate"
                :disabled="detail?.status === 1"
                @saved="onSubConfigSaved"
              />
            </a-tab-pane>

            <!-- Tab 4：人员范围 -->
            <a-tab-pane key="personnel" title="人员范围">
              <PersonnelTab
                v-if="typeId"
                :type-id="typeId"
                :personnel-configs="detail?.personnelConfigs || []"
                :disabled="detail?.status === 1"
                @saved="onSubConfigSaved"
              />
            </a-tab-pane>

            <!-- Tab 5：审批规则 -->
            <a-tab-pane key="approval" title="审批规则">
              <ApprovalTab
                v-if="typeId"
                :type-id="typeId"
                :approval-configs="detail?.approvalConfigs || []"
                :review-template="reviewTemplate"
                :manage-stages="manageTemplate?.stages || []"
                :disabled="detail?.status === 1"
                @saved="onSubConfigSaved"
              />
            </a-tab-pane>

            <!-- Tab 6：可见性 -->
            <a-tab-pane key="visibility" title="可见性">
              <VisibilityTab
                v-if="typeId"
                :type-id="typeId"
                :visibility-configs="detail?.visibilityConfigs || []"
                :disabled="detail?.status === 1"
                @saved="onSubConfigSaved"
              />
            </a-tab-pane>
          </a-tabs>
        </template>
      </div>
    </a-spin>
  </GiPageLayout>
</template>

<script setup lang="ts">
import { Message, Modal } from '@arco-design/web-vue'
import type { FormInstance } from '@arco-design/web-vue'
import ProcessTab from './components/ProcessTab.vue'
import FormMappingTab from './components/FormMappingTab.vue'
import PersonnelTab from './components/PersonnelTab.vue'
import ApprovalTab from './components/ApprovalTab.vue'
import VisibilityTab from './components/VisibilityTab.vue'
import {
  createProjectType,
  disableProjectType,
  enableProjectType,
  generateTypeCode,
  getProjectType,
  updateProjectType,
  type ProjectTypeDetailResp,
  type ProjectTypeReq,
} from '@/apis/review'
import {
  getProcessTemplate,
  type ProcessTemplateResp,
} from '@/apis/review'
import {
  getManagementTemplate,
  type ManagementTemplateResp,
} from '@/apis/review'

defineOptions({ name: 'ReviewTypeConfigure' })

const route = useRoute()
const router = useRouter()

// 是否为创建模式
const isCreate = computed(() => !route.params.id)
// 保持字符串避免 JS 大整数精度丢失（Snowflake ID 超出 Number.MAX_SAFE_INTEGER）
const typeId = computed(() => route.params.id ? String(route.params.id) : undefined)

// 页面状态
const loading = ref(false)
const saveBasicLoading = ref(false)
const enableLoading = ref(false)
const disableLoading = ref(false)

// 当前激活 Tab
const activeTab = ref('basic')

// 类型详情
const detail = ref<ProjectTypeDetailResp | null>(null)

// 关联的流程模板（用于 FormMappingTab 和 ApprovalTab）
const reviewTemplate = ref<ProcessTemplateResp | null>(null)
const manageTemplate = ref<ManagementTemplateResp | null>(null)

// 状态标签
const statusTagText = computed(() => {
  if (!detail.value) return ''
  const map: Record<number, string> = { 0: '草稿', 1: '已启用', 2: '已禁用' }
  return map[detail.value.status] ?? ''
})
const statusTagColor = computed(() => {
  if (!detail.value) return 'gray'
  const map: Record<number, string> = { 0: 'gray', 1: 'green', 2: 'orange' }
  return map[detail.value.status] ?? 'gray'
})

// 基本信息表单
const basicFormRef = ref<FormInstance>()
const basicForm = reactive<ProjectTypeReq>({
  typeName: '',
  typeCode: '',
  description: '',
  sortOrder: 0,
})
const basicFormRules = {
  typeName: [
    { required: true, message: '请输入类型名称' },
    { maxLength: 100, message: '类型名称最多100个字符' },
  ],
  typeCode: [
    {
      validator: (value: string, callback: (error?: string) => void) => {
        if (!value) { callback(); return }
        const pattern = /^TYPE_[A-Z0-9_]*$/
        if (!pattern.test(value)) {
          callback('类型编码必须以TYPE_开头，后续只能包含大写字母、数字和下划线')
          return
        }
        callback()
      },
    },
  ],
}

// 自动生成编码
const generateCodeDisabled = ref(false)
const generateCodeCooldown = ref(0)

const handleGenerateCode = async () => {
  if (generateCodeDisabled.value) return
  try {
    const res = await generateTypeCode()
    basicForm.typeCode = res.data
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
  catch (error) {
    console.error('生成编码失败:', error)
  }
}

/** 加载类型完整详情及关联的流程模板 */
const loadDetail = async () => {
  if (!typeId.value) return
  loading.value = true
  try {
    const res = await getProjectType(typeId.value)
    detail.value = res.data

    // 同步基本信息表单
    Object.assign(basicForm, {
      typeName: res.data.typeName,
      typeCode: res.data.typeCode,
      description: res.data.description || '',
      sortOrder: res.data.sortOrder ?? 0,
    })

    // 加载关联的流程模板（供 FormMappingTab 和 ApprovalTab 使用）
    const reviewConfig = res.data.processConfigs?.find(c => c.processType === 'REVIEW')
    const manageConfig = res.data.processConfigs?.find(c => c.processType === 'MANAGE')

    const [ptRes, mtRes] = await Promise.allSettled([
      reviewConfig ? getProcessTemplate(reviewConfig.templateId) : Promise.resolve(null),
      manageConfig ? getManagementTemplate(manageConfig.templateId) : Promise.resolve(null),
    ])

    reviewTemplate.value = ptRes.status === 'fulfilled' && ptRes.value ? ptRes.value.data : null
    manageTemplate.value = mtRes.status === 'fulfilled' && mtRes.value ? mtRes.value.data : null
  }
  catch (error) {
    console.error('加载详情失败:', error)
    Message.error('加载详情失败')
  }
  finally {
    loading.value = false
  }
}

/** 创建并继续配置 */
const handleCreateAndContinue = async () => {
  try {
    await basicFormRef.value?.validate()
  }
  catch {
    Message.warning('请检查表单填写是否完整')
    return
  }
  saveBasicLoading.value = true
  try {
    const res = await createProjectType(basicForm)
    const newId = res.data
    Message.success('创建成功，请继续配置各项参数')
    router.replace(`/review/type/configure/${newId}`)
  }
  catch (error) {
    console.error('创建失败:', error)
  }
  finally {
    saveBasicLoading.value = false
  }
}

/** 保存基本信息（编辑模式） */
const handleSaveBasic = async () => {
  try {
    await basicFormRef.value?.validate()
  }
  catch {
    Message.warning('请检查表单填写是否完整')
    return
  }
  saveBasicLoading.value = true
  try {
    await updateProjectType(typeId.value!, {
      typeName: basicForm.typeName,
      description: basicForm.description,
      sortOrder: basicForm.sortOrder,
    })
    Message.success('保存成功')
    await loadDetail()
  }
  catch (error) {
    console.error('保存失败:', error)
  }
  finally {
    saveBasicLoading.value = false
  }
}

/** 子配置保存后刷新详情（包括重新加载关联模板） */
const onSubConfigSaved = async () => {
  await loadDetail()
}

/** 启用类型 */
const handleEnable = async () => {
  Modal.confirm({
    title: '确认启用',
    content: '启用前将对类型配置进行全量验证（流程模板、表单映射、人员范围、审批规则、可见性），配置不完整将无法启用。确定继续？',
    onOk: async () => {
      enableLoading.value = true
      try {
        await enableProjectType(typeId.value!)
        Message.success('启用成功')
        await loadDetail()
      }
      catch (error) {
        console.error('启用失败:', error)
      }
      finally {
        enableLoading.value = false
      }
    },
  })
}

/** 禁用类型 */
const handleDisable = async () => {
  Modal.confirm({
    title: '确认禁用',
    content: '禁用后新申请将无法选择该类型，进行中的项目不受影响。确定禁用？',
    onOk: async () => {
      disableLoading.value = true
      try {
        await disableProjectType(typeId.value!)
        Message.success('禁用成功')
        await loadDetail()
      }
      catch (error) {
        console.error('禁用失败:', error)
      }
      finally {
        disableLoading.value = false
      }
    },
  })
}

/** 返回列表 */
const handleBack = () => {
  router.replace({
    path: '/review/type',
    query: { t: Date.now().toString() },
  })
}

onMounted(async () => {
  if (isCreate.value) {
    // 新建模式：自动生成编码
    await handleGenerateCode()
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

.config-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.type-code {
  color: var(--color-text-3);
  font-size: 13px;
  font-family: monospace;
}

.config-tabs :deep(.arco-tabs-content) {
  padding: 0;
}
</style>
