<template>
  <GiPageLayout>
    <GiTable
      row-key="id"
      :data="dataList"
      :columns="columns"
      :loading="loading"
      :scroll="{ x: '100%', y: '100%', minWidth: 1200 }"
      :pagination="pagination"
      :disabled-tools="['size']"
      @refresh="search"
    >
      <template #top>
        <GiForm
          v-model="queryForm"
          search
          :columns="queryFormColumns"
          size="medium"
          @search="search"
          @reset="reset"
        />
      </template>
      <template #toolbar-left>
        <a-button v-permission="['review:template:form:create']" type="primary" @click="onAdd">
          <template #icon><icon-plus /></template>
          <template #default>新建模板</template>
        </a-button>
      </template>
      <template #templateType="{ record }">
        <a-tag :color="getTemplateTypeColor(record.templateType)">
          {{ getTemplateTypeLabel(record.templateType) }}
        </a-tag>
      </template>
      <template #fieldCount="{ record }">
        <a-tag>{{ record.fieldCount || 0 }} 个字段</a-tag>
      </template>
      <template #status="{ record }">
        <GiCellStatus :status="record.status" />
      </template>
      <template #action="{ record }">
        <a-space>
          <a-link v-permission="['review:template:form:query']" title="详情" @click="onDetail(record)">详情</a-link>
          <a-link v-permission="['review:template:form:update']" title="编辑" @click="onUpdate(record)">编辑</a-link>
          <a-dropdown>
            <a-button
              v-if="has.hasPermOr(['review:template:form:status', 'review:template:form:delete'])"
              type="text"
              size="mini"
              title="更多"
            >
              <template #icon>
                <icon-more :size="16" />
              </template>
            </a-button>
            <template #content>
              <a-doption v-permission="['review:template:form:status']" @click="onToggleStatus(record)">
                {{ record.status === 1 ? '禁用' : '启用' }}
              </a-doption>
              <a-doption v-permission="['review:template:form:delete']">
                <a-link status="danger" title="删除" @click="onDelete(record)">删除</a-link>
              </a-doption>
            </template>
          </a-dropdown>
        </a-space>
      </template>
    </GiTable>
    <!-- 详情抽屉 -->
    <FormTemplateDetail
      :visible="detailDrawerVisible"
      :id="currentId"
      @update:visible="detailDrawerVisible = $event"
      @edit="handleEditFromDetail"
    />
  </GiPageLayout>
</template>

<script setup lang="ts">
import { watch } from 'vue'
import type { TableInstance } from '@arco-design/web-vue'
import { Message, Modal } from '@arco-design/web-vue'
import { useRoute, useRouter } from 'vue-router'
import FormTemplateDetail from './components/FormTemplateDetail.vue'
import {
  deleteFormTemplate,
  listFormTemplate,
  updateFormTemplateStatus,
  type FormTemplateResp,
} from '@/apis/review'
import { DisEnableStatusList } from '@/constant/common'
import { useResetReactive, useTable } from '@/hooks'
import { isMobile } from '@/utils'
import has from '@/utils/has'
import type { ColumnItem } from '@/components/GiForm'

defineOptions({ name: 'FormTemplate' })

const route = useRoute()
const router = useRouter()

// 模板类型选项
const templateTypeOptions = [
  { label: '申请表单', value: 1 },
  { label: '审核表单', value: 2 },
  { label: '评审表单', value: 3 },
  { label: '决策表单', value: 4 },
  { label: '立项阶段', value: 5 },
  { label: '执行阶段', value: 6 },
  { label: '验收阶段', value: 7 },
]

// 获取模板类型标签
const getTemplateTypeLabel = (type: number) => {
  return templateTypeOptions.find(item => item.value === type)?.label || '未知'
}

// 获取模板类型颜色
const getTemplateTypeColor = (type: number) => {
  const colorMap: Record<number, string> = {
    1: 'blue', // 申请表单
    2: 'green', // 审核表单
    3: 'orange', // 评审表单
    4: 'purple', // 决策表单
    5: 'cyan', // 立项阶段
    6: 'magenta', // 执行阶段
    7: 'red', // 验收阶段
  }
  return colorMap[type] || 'gray'
}

// 查询表单
const [queryForm, resetForm] = useResetReactive({
  sort: ['id,desc'],
})

// 查询表单配置
const queryFormColumns: ColumnItem[] = reactive([
  {
    type: 'input',
    label: '模板名称',
    field: 'templateName',
    span: { xs: 24, sm: 8, xxl: 6 },
    props: {
      placeholder: '请输入模板名称',
    },
  },
  {
    type: 'input',
    label: '模板编码',
    field: 'templateCode',
    span: { xs: 24, sm: 8, xxl: 6 },
    props: {
      placeholder: '请输入模板编码',
    },
  },
  {
    type: 'select',
    label: '模板类型',
    field: 'templateType',
    span: { xs: 24, sm: 8, xxl: 6 },
    props: {
      options: templateTypeOptions,
      placeholder: '请选择模板类型',
    },
  },
  {
    type: 'select',
    label: '启用状态',
    field: 'status',
    span: { xs: 24, sm: 8, xxl: 6 },
    props: {
      options: DisEnableStatusList,
      placeholder: '请选择状态',
    },
  },
])

// 表格数据
const {
  tableData: dataList,
  loading,
  pagination,
  search,
  handleDelete,
} = useTable((page) => listFormTemplate({ ...queryForm, ...page }), { immediate: true })

// 表格列配置
const columns: TableInstance['columns'] = [
  {
    title: '序号',
    width: 66,
    align: 'center',
    render: ({ rowIndex }) => h('span', {}, rowIndex + 1 + (pagination.current - 1) * pagination.pageSize),
    fixed: !isMobile() ? 'left' : undefined,
  },
  {
    title: '模板名称',
    dataIndex: 'templateName',
    minWidth: 180,
    ellipsis: true,
    tooltip: true,
    fixed: !isMobile() ? 'left' : undefined,
  },
  {
    title: '模板编码',
    dataIndex: 'templateCode',
    minWidth: 200,
    ellipsis: true,
    tooltip: true,
  },
  {
    title: '模板类型',
    dataIndex: 'templateType',
    slotName: 'templateType',
    width: 120,
    align: 'center',
  },
  {
    title: '字段数量',
    dataIndex: 'fieldCount',
    slotName: 'fieldCount',
    width: 110,
    align: 'center',
  },
  {
    title: '启用状态',
    dataIndex: 'status',
    slotName: 'status',
    width: 100,
    align: 'center',
  },
  {
    title: '模板描述',
    dataIndex: 'description',
    minWidth: 200,
    ellipsis: true,
    tooltip: true,
    show: false,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 165,
    ellipsis: true,
    tooltip: true,
  },
  {
    title: '创建人',
    dataIndex: 'createUserString',
    width: 120,
    ellipsis: true,
    tooltip: true,
    show: false,
  },
  {
    title: '修改时间',
    dataIndex: 'updateTime',
    width: 180,
    show: false,
  },
  {
    title: '操作',
    dataIndex: 'action',
    slotName: 'action',
    width: 180,
    align: 'center',
    fixed: !isMobile() ? 'right' : undefined,
    show: has.hasPermOr([
      'review:template:form:query',
      'review:template:form:update',
      'review:template:form:status',
      'review:template:form:delete',
    ]),
  },
]

// 详情抽屉状态
const detailDrawerVisible = ref(false)
const currentId = ref<number | undefined>()

// 重置查询
const reset = () => {
  resetForm()
  search()
}

// 新增
const onAdd = () => {
  router.push('/review/template/form/designer')
}

// 查看详情
const onDetail = (record: FormTemplateResp) => {
  currentId.value = record.id
  detailDrawerVisible.value = true
}

// 修改
const onUpdate = (record: FormTemplateResp) => {
  router.push(`/review/template/form/designer/${record.id}`)
}

// 从详情页跳转到编辑页
const handleEditFromDetail = (id: number) => {
  router.push(`/review/template/form/designer/${id}`)
}

// 切换状态
const onToggleStatus = (record: FormTemplateResp) => {
  const targetStatus = record.status === 1 ? 2 : 1
  const actionText = targetStatus === 1 ? '启用' : '禁用'

  Modal.confirm({
    title: `确认${actionText}`,
    content: `确定要${actionText}模板"${record.templateName}"吗？`,
    onOk: async () => {
      try {
        await updateFormTemplateStatus(record.id, targetStatus)
        Message.success(`${actionText}成功`)
        search()
      }
      catch (error) {
        console.error(`${actionText}失败:`, error)
      }
    },
  })
}

// 删除
const onDelete = (record: FormTemplateResp) => {
  handleDelete(
    () => deleteFormTemplate([record.id]),
    {
      content: `确定要删除模板"${record.templateName}"吗？删除后将无法恢复！`,
      showModal: true,
    },
  )
}

// 监听路由参数变化，从编辑页返回时触发刷新
watch(() => route.query.t, (newVal) => {
  if (newVal) {
    search()
  }
})
</script>
