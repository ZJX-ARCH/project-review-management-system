<template>
  <GiPageLayout>
    <GiTable
      row-key="id"
      :data="dataList"
      :columns="columns"
      :loading="loading"
      :scroll="{ x: '100%', y: '100%', minWidth: 1100 }"
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
        <a-button v-permission="['review:type:create']" type="primary" @click="onAdd">
          <template #icon><icon-plus /></template>
          <template #default>新建类型</template>
        </a-button>
      </template>
      <!-- 状态列 -->
      <template #status="{ record }">
        <a-tag v-if="record.status === 0" color="gray">草稿</a-tag>
        <a-tag v-else-if="record.status === 1" color="green">已启用</a-tag>
        <a-tag v-else color="orange">已禁用</a-tag>
      </template>
      <!-- 操作列 -->
      <template #action="{ record }">
        <a-space>
          <a-link v-permission="['review:type:query']" title="配置详情" @click="onConfig(record)">配置</a-link>
          <a-link v-permission="['review:type:query']" title="详情" @click="onDetail(record)">详情</a-link>
          <a-dropdown>
            <a-button
              v-if="has.hasPermOr(['review:type:status', 'review:type:delete'])"
              type="text"
              size="mini"
              title="更多"
            >
              <template #icon><icon-more :size="16" /></template>
            </a-button>
            <template #content>
              <a-doption
                v-permission="['review:type:status']"
                :disabled="record.status === 0"
                @click="onToggleStatus(record)"
              >
                {{ record.status === 1 ? '禁用' : '启用' }}
              </a-doption>
              <a-doption v-permission="['review:type:delete']">
                <a-link status="danger" title="删除" @click="onDelete(record)">删除</a-link>
              </a-doption>
            </template>
          </a-dropdown>
        </a-space>
      </template>
    </GiTable>

    <!-- 详情抽屉 -->
    <ProjectTypeDetail
      :visible="detailDrawerVisible"
      :id="currentId"
      @update:visible="detailDrawerVisible = $event"
      @config="handleConfigFromDetail"
    />
  </GiPageLayout>
</template>

<script setup lang="ts">
import { watch } from 'vue'
import type { TableInstance } from '@arco-design/web-vue'
import { Message, Modal } from '@arco-design/web-vue'
import { useRoute, useRouter } from 'vue-router'
import ProjectTypeDetail from './components/ProjectTypeDetail.vue'
import {
  deleteProjectType,
  disableProjectType,
  enableProjectType,
  listProjectType,
  type ProjectTypeResp,
} from '@/apis/review'
import { useResetReactive, useTable } from '@/hooks'
import { isMobile } from '@/utils'
import has from '@/utils/has'
import type { ColumnItem } from '@/components/GiForm'

defineOptions({ name: 'ProjectType' })

const route = useRoute()
const router = useRouter()

const statusOptions = [
  { label: '草稿', value: 0 },
  { label: '已启用', value: 1 },
  { label: '已禁用', value: 2 },
]

// 查询表单
const [queryForm, resetForm] = useResetReactive({
  sort: ['sort_order,asc', 'id,desc'],
})

// 查询表单配置
const queryFormColumns: ColumnItem[] = reactive([
  {
    type: 'input',
    label: '类型名称',
    field: 'typeName',
    span: { xs: 24, sm: 8, xxl: 8 },
    props: { placeholder: '请输入类型名称' },
  },
  {
    type: 'input',
    label: '类型编码',
    field: 'typeCode',
    span: { xs: 24, sm: 8, xxl: 8 },
    props: { placeholder: '请输入类型编码' },
  },
  {
    type: 'select',
    label: '状态',
    field: 'status',
    span: { xs: 24, sm: 8, xxl: 8 },
    props: { options: statusOptions, placeholder: '请选择状态' },
  },
])

// 表格数据
const {
  tableData: dataList,
  loading,
  pagination,
  search,
  handleDelete,
} = useTable((page) => listProjectType({ ...queryForm, ...page }), { immediate: true })

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
    title: '类型名称',
    dataIndex: 'typeName',
    minWidth: 180,
    ellipsis: true,
    tooltip: true,
    fixed: !isMobile() ? 'left' : undefined,
  },
  {
    title: '类型编码',
    dataIndex: 'typeCode',
    minWidth: 180,
    ellipsis: true,
    tooltip: true,
  },
  {
    title: '状态',
    dataIndex: 'status',
    slotName: 'status',
    width: 100,
    align: 'center',
  },
  {
    title: '排序',
    dataIndex: 'sortOrder',
    width: 80,
    align: 'center',
  },
  {
    title: '描述',
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
    title: '操作',
    dataIndex: 'action',
    slotName: 'action',
    width: 180,
    align: 'center',
    fixed: !isMobile() ? 'right' : undefined,
    show: has.hasPermOr([
      'review:type:query',
      'review:type:status',
      'review:type:delete',
    ]),
  },
]

// 详情抽屉状态
const detailDrawerVisible = ref(false)
const currentId = ref<number | undefined>()

const reset = () => {
  resetForm()
  search()
}

// 新建：创建基本信息后跳转配置页
const onAdd = () => {
  router.push('/review/type/config')
}

// 查看详情
const onDetail = (record: ProjectTypeResp) => {
  currentId.value = record.id
  detailDrawerVisible.value = true
}

// 进入配置页
const onConfig = (record: ProjectTypeResp) => {
  router.push(`/review/type/config/${record.id}`)
}

// 从详情页跳转到配置页
const handleConfigFromDetail = (id: number) => {
  router.push(`/review/type/config/${id}`)
}

// 切换启用/禁用状态
const onToggleStatus = (record: ProjectTypeResp) => {
  const isEnable = record.status !== 1
  const actionText = isEnable ? '启用' : '禁用'

  Modal.confirm({
    title: `确认${actionText}`,
    content: `确定要${actionText}类型"${record.typeName}"吗？${isEnable ? '启用前将对配置完整性进行全量验证。' : ''}`,
    onOk: async () => {
      try {
        if (isEnable) {
          await enableProjectType(record.id)
        }
        else {
          await disableProjectType(record.id)
        }
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
const onDelete = (record: ProjectTypeResp) => {
  handleDelete(
    () => deleteProjectType([record.id]),
    {
      content: `确定要删除类型"${record.typeName}"吗？删除后将无法恢复！`,
      showModal: true,
    },
  )
}

// 监听路由参数变化（从配置页返回时刷新）
watch(() => route.query.t, (newVal) => {
  if (newVal) {
    search()
  }
})
</script>
