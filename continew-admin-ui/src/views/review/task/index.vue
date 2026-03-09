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

      <!-- 快捷状态 Tab -->
      <template #toolbar-left>
        <a-radio-group
          v-model="statusTab"
          type="button"
          size="medium"
          @change="onTabChange"
        >
          <a-radio value="">全部</a-radio>
          <a-radio value="PENDING">待处理</a-radio>
          <a-radio value="SAVED">暂存中</a-radio>
          <a-radio value="COMPLETED">已完成</a-radio>
        </a-radio-group>
      </template>

      <!-- 任务类型 -->
      <template #taskType="{ record }">
        <a-tag>{{ PROJECT_TASK_TYPE_MAP[record.taskType] ?? record.taskType }}</a-tag>
      </template>

      <!-- 任务状态 -->
      <template #status="{ record }">
        <a-tag :color="PROJECT_TASK_STATUS_MAP[record.status]?.color ?? 'gray'">
          {{ PROJECT_TASK_STATUS_MAP[record.status]?.label ?? record.status }}
        </a-tag>
      </template>

      <!-- 操作列 -->
      <template #action="{ record }">
        <a-link
          v-permission="['review:task:query']"
          @click="onProcess(record)"
        >
          {{ record.status === 'PENDING' || record.status === 'SAVED' ? '处理' : '查看' }}
        </a-link>
      </template>
    </GiTable>
  </GiPageLayout>
</template>

<script setup lang="ts">
import { watch, ref } from 'vue'
import type { TableInstance } from '@arco-design/web-vue'
import { useRoute, useRouter } from 'vue-router'
import { getMyTasks } from '@/apis/review'
import { PROJECT_TASK_TYPE_MAP, PROJECT_TASK_STATUS_MAP } from '@/apis/review/type'
import type { TaskListResp } from '@/apis/review/type'
import { useResetReactive, useTable } from '@/hooks'
import { isMobile } from '@/utils'
import has from '@/utils/has'
import type { ColumnItem } from '@/components/GiForm'

defineOptions({ name: 'ReviewTaskList' })

const route = useRoute()
const router = useRouter()

// ——— 快捷 Tab ———
const statusTab = ref('')

const onTabChange = (val: string) => {
  queryForm.status = val || undefined
  search()
}

// ——— 查询表单 ———
const [queryForm, resetForm] = useResetReactive({
  sort: ['id,desc'],
  status: undefined as string | undefined,
})

const queryFormColumns: ColumnItem[] = reactive([
  {
    type: 'input',
    label: '项目名称',
    field: 'projectName',
    span: { xs: 24, sm: 8, xxl: 6 },
    props: { placeholder: '请输入项目名称' },
  },
  {
    type: 'select',
    label: '任务类型',
    field: 'taskType',
    span: { xs: 24, sm: 8, xxl: 6 },
    props: {
      placeholder: '请选择任务类型',
      allowClear: true,
      options: Object.entries(PROJECT_TASK_TYPE_MAP).map(([v, l]) => ({ label: l, value: v })),
    },
  },
  {
    type: 'select',
    label: '任务状态',
    field: 'status',
    span: { xs: 24, sm: 8, xxl: 6 },
    props: {
      placeholder: '请选择任务状态',
      allowClear: true,
      options: Object.entries(PROJECT_TASK_STATUS_MAP).map(([v, info]) => ({
        label: info.label,
        value: v,
      })),
    },
  },
])

// ——— 表格数据 ———
const {
  tableData: dataList,
  loading,
  pagination,
  search,
} = useTable((page) => getMyTasks({ ...queryForm, ...page }), { immediate: true })

// ——— 列配置 ———
const columns: TableInstance['columns'] = [
  {
    title: '序号',
    width: 60,
    align: 'center',
    render: ({ rowIndex }) => h('span', {}, rowIndex + 1 + (pagination.current - 1) * pagination.pageSize),
    fixed: !isMobile() ? 'left' : undefined,
  },
  {
    title: '项目名称',
    dataIndex: 'projectName',
    minWidth: 200,
    ellipsis: true,
    tooltip: true,
    fixed: !isMobile() ? 'left' : undefined,
  },
  {
    title: '申请人',
    dataIndex: 'applicantName',
    width: 110,
  },
  {
    title: '任务类型',
    dataIndex: 'taskType',
    slotName: 'taskType',
    width: 100,
    align: 'center',
  },
  {
    title: '节点名称',
    dataIndex: 'nodeName',
    width: 140,
  },
  {
    title: '任务状态',
    dataIndex: 'status',
    slotName: 'status',
    width: 100,
    align: 'center',
  },
  {
    title: '分配时间',
    dataIndex: 'assignTime',
    width: 170,
  },
  {
    title: '完成时间',
    dataIndex: 'completeTime',
    width: 170,
    show: false,
  },
  {
    title: '操作',
    dataIndex: 'action',
    slotName: 'action',
    width: 80,
    align: 'center',
    fixed: !isMobile() ? 'right' : undefined,
    show: has.hasPermOr(['review:task:query']),
  },
]

// ——— 操作 ———
const reset = () => {
  resetForm()
  statusTab.value = ''
  search()
}

const onProcess = (record: TaskListResp) => {
  router.push(`/review/task/detail/${record.id}`)
}

// 子页面返回时刷新
watch(() => route.query.t, (newVal) => {
  if (newVal) search()
})
</script>
