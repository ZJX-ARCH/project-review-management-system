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
        <a-button v-permission="['review:template:management:create']" type="primary" @click="onAdd">
          <template #icon><icon-plus /></template>
          <template #default>新建模板</template>
        </a-button>
      </template>
      <template #phasesSummary="{ record }">
        <PhasesSummary :phases="record.stages || []" />
      </template>
      <template #status="{ record }">
        <GiCellStatus :status="record.status" />
      </template>
      <template #action="{ record }">
        <a-space>
          <a-link v-permission="['review:template:management:query']" title="详情" @click="onDetail(record)">详情</a-link>
          <a-link v-permission="['review:template:management:update']" title="编辑" @click="onUpdate(record)">编辑</a-link>
          <a-dropdown>
            <a-button
              v-if="has.hasPermOr(['review:template:management:status', 'review:template:management:delete'])"
              type="text"
              size="mini"
              title="更多"
            >
              <template #icon>
                <icon-more :size="16" />
              </template>
            </a-button>
            <template #content>
              <a-doption v-permission="['review:template:management:status']" @click="onToggleStatus(record)">
                {{ record.status === 1 ? '禁用' : '启用' }}
              </a-doption>
              <a-doption v-permission="['review:template:management:delete']">
                <a-link status="danger" title="删除" @click="onDelete(record)">删除</a-link>
              </a-doption>
            </template>
          </a-dropdown>
        </a-space>
      </template>
    </GiTable>
    <!-- 详情抽屉 -->
    <ManagementTemplateDetail
      :visible="detailDrawerVisible"
      :id="currentId"
      @update:visible="detailDrawerVisible = $event"
      @edit="handleEditFromDetail"
    />
  </GiPageLayout>
</template>

<script setup lang="ts">
import type { TableInstance } from '@arco-design/web-vue'
import { Message, Modal } from '@arco-design/web-vue'
import { useRouter } from 'vue-router'
import PhasesSummary from './components/PhasesSummary.vue'
import ManagementTemplateDetail from './components/ManagementTemplateDetail.vue'
import {
  deleteManagementTemplate,
  listManagementTemplate,
  updateManagementTemplateStatus,
  type ManagementTemplateResp,
} from '@/apis/review'
import { DisEnableStatusList } from '@/constant/common'
import { useResetReactive, useTable } from '@/hooks'
import { isMobile } from '@/utils'
import has from '@/utils/has'
import type { ColumnItem } from '@/components/GiForm'

defineOptions({ name: 'ManagementTemplate' })

const router = useRouter()

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
    span: { xs: 24, sm: 8, xxl: 8 },
    props: {
      placeholder: '请输入模板名称',
    },
  },
  {
    type: 'input',
    label: '模板编码',
    field: 'templateCode',
    span: { xs: 24, sm: 8, xxl: 8 },
    props: {
      placeholder: '请输入模板编码',
    },
  },
  {
    type: 'select',
    label: '启用状态',
    field: 'status',
    span: { xs: 24, sm: 8, xxl: 8 },
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
} = useTable((page) => listManagementTemplate({ ...queryForm, ...page }), { immediate: true })

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
    title: '阶段配置',
    dataIndex: 'phasesSummary',
    slotName: 'phasesSummary',
    minWidth: 250,
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
    width: 180,
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
      'review:template:management:query',
      'review:template:management:update',
      'review:template:management:status',
      'review:template:management:delete',
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
  router.push('/review/template/management/add')
}

// 查看详情
const onDetail = (record: ManagementTemplateResp) => {
  currentId.value = record.id
  detailDrawerVisible.value = true
}

// 修改
const onUpdate = (record: ManagementTemplateResp) => {
  router.push(`/review/template/management/edit/${record.id}`)
}

// 从详情页跳转到编辑页
const handleEditFromDetail = (id: number) => {
  router.push(`/review/template/management/edit/${id}`)
}

// 切换状态
const onToggleStatus = (record: ManagementTemplateResp) => {
  const targetStatus = record.status === 1 ? 2 : 1
  const actionText = targetStatus === 1 ? '启用' : '禁用'

  Modal.confirm({
    title: `确认${actionText}`,
    content: `确定要${actionText}模板"${record.templateName}"吗？`,
    onOk: async () => {
      try {
        await updateManagementTemplateStatus(record.id, targetStatus)
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
const onDelete = (record: ManagementTemplateResp) => {
  handleDelete(
    () => deleteManagementTemplate([record.id]),
    {
      content: `确定要删除模板"${record.templateName}"吗？删除后将无法恢复！`,
      showModal: true,
    },
  )
}
</script>
