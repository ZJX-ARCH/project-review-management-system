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
        <a-button v-permission="['review:project:add']" type="primary" @click="onAdd">
          <template #icon><icon-plus /></template>
          <template #default>提交新项目</template>
        </a-button>
      </template>

      <!-- 当前节点 -->
      <template #currentNode="{ record }">
        <span>{{ formatCurrentNode(record) }}</span>
      </template>

      <!-- 项目状态 -->
      <template #status="{ record }">
        <a-tag :color="PROJECT_STATUS_MAP[record.status]?.color ?? 'gray'">
          {{ PROJECT_STATUS_MAP[record.status]?.label ?? record.status }}
        </a-tag>
      </template>

      <!-- 操作列 -->
      <template #action="{ record }">
        <a-space>
          <a-link
            v-permission="['review:project:query']"
            title="详情"
            @click="onDetail(record)"
          >详情</a-link>

          <!-- 草稿：继续编辑 -->
          <a-link
            v-if="record.status === ProjectStatus.DRAFT"
            v-permission="['review:project:add']"
            title="继续编辑"
            @click="onContinueEdit(record)"
          >继续编辑</a-link>

          <!-- 评审阶段：撤销 -->
          <a-link
            v-if="canRevoke(record.status)"
            v-permission="['review:project:revoke']"
            status="warning"
            title="撤销申请"
            @click="onRevoke(record)"
          >撤销</a-link>

          <!-- 草稿：删除（尚无接口，暂隐藏） -->
        </a-space>
      </template>
    </GiTable>
  </GiPageLayout>
</template>

<script setup lang="ts">
import { watch } from 'vue'
import type { TableInstance } from '@arco-design/web-vue'
import { Message, Modal } from '@arco-design/web-vue'
import { useRoute, useRouter } from 'vue-router'
import { getProjectPage, revokeProject, listEnabledTypes } from '@/apis/review'
import { PROJECT_STATUS_MAP, ProjectStatus } from '@/apis/review/type'
import type { ProjectListResp, ProjectTypeResp } from '@/apis/review/type'
import { useResetReactive, useTable } from '@/hooks'
import { isMobile } from '@/utils'
import has from '@/utils/has'
import type { ColumnItem } from '@/components/GiForm'

defineOptions({ name: 'ReviewProjectList' })

const route = useRoute()
const router = useRouter()

// ——— 查询表单 ———
const [queryForm, resetForm] = useResetReactive({
  sort: ['id,desc'],
})

// 项目类型选项（异步加载）
const typeOptions = ref<{ label: string; value: number }[]>([])
onMounted(async () => {
  const res = await listEnabledTypes()
  typeOptions.value = (res.data ?? []).map((t: ProjectTypeResp) => ({
    label: t.typeName,
    value: t.id,
  }))
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
    label: '项目类型',
    field: 'typeId',
    span: { xs: 24, sm: 8, xxl: 6 },
    props: {
      placeholder: '请选择项目类型',
      options: typeOptions,
      allowClear: true,
    },
  },
  {
    type: 'select',
    label: '项目状态',
    field: 'status',
    span: { xs: 24, sm: 8, xxl: 6 },
    props: {
      placeholder: '请选择项目状态',
      allowClear: true,
      options: Object.entries(PROJECT_STATUS_MAP).map(([v, info]) => ({
        label: info.label,
        value: Number(v),
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
} = useTable((page) => getProjectPage({ ...queryForm, ...page }), { immediate: true })

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
    title: '项目类型',
    dataIndex: 'typeName',
    width: 140,
  },
  {
    title: '当前节点',
    dataIndex: 'currentNode',
    slotName: 'currentNode',
    width: 130,
  },
  {
    title: '项目状态',
    dataIndex: 'status',
    slotName: 'status',
    width: 110,
    align: 'center',
  },
  {
    title: '提交时间',
    dataIndex: 'submittedTime',
    width: 170,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 170,
    show: false,
  },
  {
    title: '操作',
    dataIndex: 'action',
    slotName: 'action',
    width: 160,
    align: 'center',
    fixed: !isMobile() ? 'right' : undefined,
    show: has.hasPermOr(['review:project:query', 'review:project:add', 'review:project:revoke']),
  },
]

// ——— 工具函数 ———
const TASK_TYPE_LABEL: Record<string, string> = {
  AUDIT: '审核', REVIEW: '评审', DECISION: '决策',
  MANAGEMENT: '管理', ACCEPTANCE: '验收',
}

function formatCurrentNode(record: ProjectListResp): string {
  if (!record.currentNodeType) return '—'
  const label = TASK_TYPE_LABEL[record.currentNodeType] ?? record.currentNodeType
  if (!record.currentNodeSequence) return label
  const suffix = ['MANAGEMENT', 'ACCEPTANCE'].includes(record.currentNodeType)
    ? `第${record.currentNodeSequence}阶段`
    : `第${record.currentNodeSequence}轮`
  return `${label} ${suffix}`
}

/** 评审阶段（已提交~决策中）可撤销 */
function canRevoke(status: number): boolean {
  return [10, 20, 30, 40].includes(status)
}

// ——— 操作 ———
const reset = () => {
  resetForm()
  search()
}

const onAdd = () => {
  router.push('/review/project/create')
}

const onDetail = (record: ProjectListResp) => {
  router.push(`/review/project/detail/${record.id}`)
}

const onContinueEdit = (record: ProjectListResp) => {
  router.push(`/review/project/create?draftId=${record.id}`)
}

const onRevoke = (record: ProjectListResp) => {
  Modal.confirm({
    title: '确认撤销申请',
    content: `确定要撤销项目"${record.projectName}"的申请吗？撤销后项目将回到草稿状态。`,
    okButtonProps: { status: 'warning' },
    onOk: async () => {
      try {
        await revokeProject(record.id)
        Message.success('撤销成功')
        search()
      }
      catch (error) {
        console.error('撤销失败:', error)
      }
    },
  })
}

// 从子页面返回时刷新
watch(() => route.query.t, (newVal) => {
  if (newVal) search()
})
</script>
