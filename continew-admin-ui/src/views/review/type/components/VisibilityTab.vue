<template>
  <div class="tab-container">
    <a-alert v-if="disabled" type="warning" style="margin-bottom: 16px">
      类型已启用，如需修改请先禁用类型。
    </a-alert>
    <a-alert v-else type="info" style="margin-bottom: 16px">
      配置哪些部门或用户可以看到并申请该类型项目。至少需配置一条可见性规则。
    </a-alert>

    <!-- 当前配置列表 -->
    <a-card :bordered="false" style="margin-bottom: 16px">
      <template #title>
        <a-space>
          <span>可见性规则</span>
          <a-tag color="blue">{{ items.length }} 条</a-tag>
        </a-space>
      </template>
      <template #extra>
        <a-button
          v-if="!disabled"
          type="primary"
          size="small"
          @click="showAddForm = true"
        >
          <template #icon><icon-plus /></template>
          添加规则
        </a-button>
      </template>

      <a-table :data="items" :pagination="false" :bordered="false">
        <template #columns>
          <a-table-column title="可见类型" data-index="visibilityType" :width="140">
            <template #cell="{ record }">
              <a-tag :color="visibilityTypeColor(record.visibilityType)">
                {{ visibilityTypeLabel(record.visibilityType) }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column title="目标ID" data-index="targetId" :width="160">
            <template #cell="{ record }">
              <span v-if="record.visibilityType === 'ALL'">—（全部可见）</span>
              <span v-else>{{ record.targetId }}</span>
            </template>
          </a-table-column>
          <a-table-column title="操作" :width="80" align="center">
            <template #cell="{ rowIndex }">
              <a-button
                v-if="!disabled"
                type="text"
                status="danger"
                size="small"
                @click="removeItem(rowIndex)"
              >
                删除
              </a-button>
              <span v-else>—</span>
            </template>
          </a-table-column>
        </template>
      </a-table>

      <a-empty v-if="items.length === 0" description="暂无可见性配置，请点击添加规则" />
    </a-card>

    <!-- 添加规则弹窗 -->
    <a-modal
      v-model:visible="showAddForm"
      title="添加可见性规则"
      :width="400"
      @ok="handleAdd"
      @cancel="resetAddForm"
    >
      <a-form :model="addForm" layout="vertical">
        <a-form-item label="可见类型" required>
          <a-select v-model="addForm.visibilityType" placeholder="请选择" style="width: 100%" @change="onAddTypeChange">
            <a-option value="ALL">全部可见（所有人均可申请）</a-option>
            <a-option value="DEPT">指定部门</a-option>
            <a-option value="USER">指定用户</a-option>
          </a-select>
        </a-form-item>

        <a-form-item v-if="addForm.visibilityType === 'DEPT'" label="部门ID" required>
          <a-input-number
            v-model="addForm.targetId"
            placeholder="请输入部门ID"
            :min="1"
            style="width: 100%"
          />
        </a-form-item>

        <a-form-item v-if="addForm.visibilityType === 'USER'" label="用户ID" required>
          <a-input-number
            v-model="addForm.targetId"
            placeholder="请输入用户ID"
            :min="1"
            style="width: 100%"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <div style="text-align: right">
      <a-button
        type="primary"
        :loading="saveLoading"
        :disabled="disabled || items.length === 0"
        @click="handleSave"
      >
        保存可见性配置
      </a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Message } from '@arco-design/web-vue'
import {
  saveVisibility,
  type TypeVisibilityConfigResp,
  VisibilityType,
} from '@/apis/review'

interface VisibilityItem {
  visibilityType: VisibilityType
  targetId?: number
}

const props = defineProps<{
  typeId: number
  visibilityConfigs: TypeVisibilityConfigResp[]
  disabled?: boolean
}>()

const emit = defineEmits<{
  (e: 'saved'): void
}>()

const saveLoading = ref(false)
const showAddForm = ref(false)

// 当前配置列表（可编辑的本地副本）
const items = ref<VisibilityItem[]>([])

// 添加表单
const addForm = reactive<VisibilityItem>({
  visibilityType: VisibilityType.DEPT,
  targetId: undefined,
})

const visibilityTypeLabel = (type: string) => ({
  ALL: '全部可见', DEPT: '指定部门', USER: '指定用户',
}[type] ?? type)

const visibilityTypeColor = (type: string) => ({
  ALL: 'green', DEPT: 'blue', USER: 'orange',
}[type] ?? 'gray')

const onAddTypeChange = () => {
  addForm.targetId = undefined
}

const resetAddForm = () => {
  addForm.visibilityType = VisibilityType.DEPT
  addForm.targetId = undefined
}

/** 添加一条规则 */
const handleAdd = () => {
  if (addForm.visibilityType === VisibilityType.ALL) {
    if (items.value.some(i => i.visibilityType === VisibilityType.ALL)) {
      Message.warning('全部可见规则只能添加一条')
      return
    }
    items.value.push({ visibilityType: VisibilityType.ALL })
  }
  else {
    if (!addForm.targetId) {
      Message.warning('请输入目标ID')
      return
    }
    // 检查重复
    const exists = items.value.some(
      i => i.visibilityType === addForm.visibilityType && i.targetId === addForm.targetId
    )
    if (exists) {
      Message.warning('该规则已存在')
      return
    }
    items.value.push({ visibilityType: addForm.visibilityType, targetId: addForm.targetId })
  }

  showAddForm.value = false
  resetAddForm()
}

/** 删除一条规则 */
const removeItem = (idx: number) => {
  items.value.splice(idx, 1)
}

/** 保存 */
const handleSave = async () => {
  if (items.value.length === 0) {
    Message.warning('请至少添加一条可见性规则')
    return
  }

  saveLoading.value = true
  try {
    await saveVisibility(props.typeId, items.value.map(i => ({
      visibilityType: i.visibilityType,
      targetId: i.targetId,
    })))
    Message.success('可见性配置保存成功')
    emit('saved')
  }
  catch (error) {
    console.error('保存失败:', error)
  }
  finally {
    saveLoading.value = false
  }
}

/** 从 props 初始化 */
const initFromProps = () => {
  items.value = props.visibilityConfigs.map(c => ({
    visibilityType: c.visibilityType,
    targetId: c.targetId,
  }))
}

onMounted(initFromProps)
watch(() => props.visibilityConfigs, initFromProps, { deep: true })
</script>

<style scoped>
.tab-container {
  padding: 16px 0;
}
</style>
