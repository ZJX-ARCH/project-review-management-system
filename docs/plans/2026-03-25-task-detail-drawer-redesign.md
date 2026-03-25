# 任务详情页重设计 - 抽屉式参考面板

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 重构任务详情页，将"申请表单/历史节点/阶段成果"等参考内容移入右侧抽屉，主区域专注任务处理，同时支持未来扩展更多参考面板。

**Architecture:**
主区域全宽展示任务信息+表单+决策区，右侧固定一列"参考面板"快捷按钮（图标+文字），点击任意按钮从右侧滑出对应抽屉。抽屉内用 Tabs 组织多个参考内容（申请表单、历史节点、阶段成果），未来新增内容只需加 Tab 即可。

**Tech Stack:** Vue 3.5 + Arco Design 2.57（a-drawer + a-tabs）

---

## 设计说明

### 整体布局

```
┌─────────────────────────────────────────┬──────┐
│  页头（项目名 + 任务类型标签 + 操作按钮）         │      │
├─────────────────────────────────────────┤  参  │
│  流程进度条（水平 Steps，顶部通栏）              │  考  │
├─────────────────────────────────────────┤  面  │
│  任务信息（Descriptions，紧凑）               │  板  │
├─────────────────────────────────────────┤  按  │
│  任务表单（FormRenderer）                   │  钮  │
├─────────────────────────────────────────┤  列  │
│  决策区（决策结果 + 操作按钮）                  │      │
└─────────────────────────────────────────┴──────┘
```

主区域：`calc(100% - 48px)` 宽，右侧固定 48px 按钮列。

### 参考面板按钮列

竖排图标按钮，每个按钮：
- 图标 + 竖排文字
- 激活时高亮（主色背景）
- 点击同一个再次点击关闭抽屉

按钮列表（可扩展）：
| 图标 | 文字 | 抽屉内容 |
|------|------|---------|
| `icon-file-text` | 申请表单 | 申请表单只读渲染 |
| `icon-history` | 历史节点 | 各轮次结果 Timeline |
| `icon-layers` | 阶段成果 | 当前/全部阶段表单（MANAGEMENT/ACCEPTANCE 时显示） |

### 抽屉设计

- 宽度：`560px`（足够展示表单，不遮挡主区域太多）
- 无遮罩（`mask: false`），用户可同时操作主区域
- 标题：项目名称
- 内部用 `a-tabs` 切换多个参考内容
- 关闭按钮在右上角

### 流程进度条改为水平通栏

当前进度条在右栏竖排，改为主区域顶部水平 Steps：
- 已完成节点：绿色 finish，点击弹出该节点详情（小 Popover 显示通过人数/均分）
- 当前节点：蓝色 process
- 待处理节点：灰色 wait

---

## Task 1：重构主区域布局（去掉左右两栏，改为全宽+右侧按钮列）

**Files:**
- Modify: `continew-admin-ui/src/views/review/task/detail/[id].vue`

**改动：**

1. 删除 `<a-row>` / `<a-col :span="16">` / `<a-col :span="8">` 结构
2. 主内容区改为全宽，右侧加固定 48px 按钮列
3. 整体用 flex 布局：

```vue
<div class="task-detail-layout">
  <!-- 主内容区 -->
  <div class="task-main">
    <!-- 水平流程进度条 -->
    <!-- 任务信息 -->
    <!-- 任务表单 -->
    <!-- 决策区 -->
  </div>
  <!-- 右侧参考面板按钮列 -->
  <div class="task-side-panel">
    <div
      v-for="panel in sidePanels"
      :key="panel.key"
      class="side-panel-btn"
      :class="{ active: activePanel === panel.key }"
      @click="togglePanel(panel.key)"
    >
      <component :is="panel.icon" class="panel-btn-icon" />
      <span class="panel-btn-label">{{ panel.label }}</span>
    </div>
  </div>
</div>
```

**样式：**
```scss
.task-detail-layout {
  display: flex;
  gap: 0;
  align-items: flex-start;
}

.task-main {
  flex: 1;
  min-width: 0;
  padding-right: 8px;
}

.task-side-panel {
  width: 48px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  position: sticky;
  top: 16px;
}

.side-panel-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 10px 4px;
  border-radius: 6px;
  cursor: pointer;
  color: var(--color-text-2);
  background: var(--color-fill-1);
  border: 1px solid var(--color-border-2);
  transition: all 0.2s;
  user-select: none;

  &:hover {
    color: var(--color-primary-6);
    border-color: var(--color-primary-4);
    background: var(--color-primary-1);
  }

  &.active {
    color: #fff;
    background: var(--color-primary-6);
    border-color: var(--color-primary-6);
  }

  .panel-btn-icon {
    font-size: 16px;
  }

  .panel-btn-label {
    font-size: 11px;
    writing-mode: vertical-rl;
    letter-spacing: 2px;
  }
}
```

**sidePanels 数据（computed，按任务类型过滤）：**
```typescript
const sidePanels = computed(() => {
  const panels = [
    { key: 'application', label: '申请表单', icon: 'icon-file-text' },
    { key: 'history', label: '历史节点', icon: 'icon-history' },
  ]
  if (detail.value?.taskType === 'MANAGEMENT' || detail.value?.taskType === 'ACCEPTANCE') {
    panels.push({ key: 'stage', label: '阶段成果', icon: 'icon-layers' })
  }
  return panels
})
```

---

## Task 2：水平流程进度条

**Files:**
- Modify: `continew-admin-ui/src/views/review/task/detail/[id].vue`

将右栏的竖向 Steps 移到主区域顶部，改为水平方向，节点可点击弹出 Popover 显示详情：

```vue
<!-- 水平流程进度条 -->
<a-card v-if="allProgressSteps.length" :body-style="{ padding: '16px 20px' }" style="margin-bottom: 16px;">
  <a-steps :current="currentProgressStep" size="small">
    <a-step
      v-for="(step, idx) in allProgressSteps"
      :key="idx"
      :title="step.title"
      :status="step.status"
    >
      <template v-if="step.description" #description>
        <a-popover v-if="step.clickable" position="bottom">
          <span class="step-desc-link">{{ step.description }}</span>
          <template #content>
            <div class="step-popover">
              <div>通过：{{ step.passCount }} / {{ step.totalCount }} 人</div>
              <div v-if="step.averageScore != null">平均分：{{ step.averageScore }}</div>
              <div>结果：
                <a-tag :color="step.result === 'PASS' ? 'green' : 'red'" size="small">
                  {{ step.result === 'PASS' ? '通过' : '驳回' }}
                </a-tag>
              </div>
            </div>
          </template>
        </a-popover>
        <span v-else>{{ step.description }}</span>
      </template>
    </a-step>
  </a-steps>
</a-card>
```

**ProgressStep 类型扩展：**
```typescript
interface ProgressStep {
  title: string
  status: 'wait' | 'process' | 'finish' | 'error'
  description?: string
  clickable?: boolean
  passCount?: number
  totalCount?: number
  averageScore?: number | null
  result?: string
}
```

**allProgressSteps 更新：**
```typescript
const allProgressSteps = computed<ProgressStep[]>(() => {
  if (!detail.value) return []
  const steps: ProgressStep[] = []
  for (const node of (detail.value.previousNodes ?? [])) {
    steps.push({
      title: node.nodeName || `${TASK_TYPE_LABEL[node.nodeType] ?? node.nodeType} 第${node.nodeSequence}轮`,
      status: node.result === 'PASS' ? 'finish' : 'error',
      description: `${node.passCount}/${node.totalCount} 人通过`,
      clickable: true,
      passCount: node.passCount,
      totalCount: node.totalCount,
      averageScore: node.averageScore ?? null,
      result: node.result,
    })
  }
  steps.push({
    title: detail.value.nodeName || `${TASK_TYPE_LABEL[detail.value.taskType] ?? detail.value.taskType}`,
    status: 'process',
    description: '处理中',
  })
  return steps
})
```

---

## Task 3：抽屉组件（含 Tabs）

**Files:**
- Modify: `continew-admin-ui/src/views/review/task/detail/[id].vue`

**抽屉模板：**
```vue
<a-drawer
  v-model:visible="drawerVisible"
  :title="detail?.projectName"
  :width="560"
  :mask="false"
  placement="right"
  @cancel="closeDrawer"
>
  <a-tabs v-model:active-key="drawerTab" type="line" size="small">

    <!-- 申请表单 Tab -->
    <a-tab-pane key="application" title="申请表单">
      <div v-if="detail?.applicationFormTemplate" style="padding: 4px 0;">
        <FormRenderer
          :model-value="detail.applicationFormData ?? {}"
          :template="detail.applicationFormTemplate"
          readonly
        />
      </div>
      <a-empty v-else description="暂无申请表单" />
    </a-tab-pane>

    <!-- 历史节点 Tab -->
    <a-tab-pane key="history" title="历史节点">
      <div v-if="detail?.previousNodes?.length">
        <a-timeline>
          <a-timeline-item
            v-for="node in detail.previousNodes"
            :key="`${node.nodeType}_${node.nodeSequence}`"
            :color="node.result === 'PASS' ? 'green' : 'red'"
          >
            <template #dot>
              <icon-check-circle v-if="node.result === 'PASS'" style="color: #00b42a;" />
              <icon-close-circle v-else style="color: #f53f3f;" />
            </template>
            <div class="history-node">
              <div class="node-title">{{ node.nodeName }}</div>
              <div class="node-stats">
                通过 {{ node.passCount }} / 共 {{ node.totalCount }} 人
                <span v-if="node.averageScore != null"> · 均分 {{ node.averageScore }}</span>
              </div>
            </div>
          </a-timeline-item>
        </a-timeline>
      </div>
      <a-empty v-else description="暂无历史节点" />
    </a-tab-pane>

    <!-- 阶段成果 Tab（仅 MANAGEMENT/ACCEPTANCE 显示） -->
    <a-tab-pane
      v-if="detail?.taskType === 'MANAGEMENT' || detail?.taskType === 'ACCEPTANCE'"
      key="stage"
      title="阶段成果"
    >
      <!-- MANAGEMENT：当前阶段 -->
      <template v-if="detail?.taskType === 'MANAGEMENT' && detail?.currentStage">
        <a-descriptions :column="1" size="small" style="margin-bottom: 12px;">
          <a-descriptions-item label="阶段名称">{{ detail.currentStage.stageName }}</a-descriptions-item>
          <a-descriptions-item label="阶段状态">
            <a-tag :color="PROJECT_STAGE_STATUS_MAP[detail.currentStage.status]?.color ?? 'gray'" size="small">
              {{ PROJECT_STAGE_STATUS_MAP[detail.currentStage.status]?.label ?? detail.currentStage.status }}
            </a-tag>
            <a-tag v-if="detail.currentStage.isOverdue" color="red" size="small" style="margin-left: 4px;">已超时</a-tag>
          </a-descriptions-item>
          <a-descriptions-item v-if="detail.currentStage.deadline" label="截止日期">
            {{ detail.currentStage.deadline }}
          </a-descriptions-item>
        </a-descriptions>
        <a-empty v-if="!detail.currentStage.stageFormData" description="申请人尚未提交阶段成果" />
      </template>
      <!-- ACCEPTANCE：全部阶段 -->
      <template v-else-if="detail?.taskType === 'ACCEPTANCE' && detail?.allStages?.length">
        <a-collapse :default-active-key="[detail.allStages[detail.allStages.length - 1]?.stageOrder]" :bordered="false">
          <a-collapse-item
            v-for="stage in detail.allStages"
            :key="stage.stageOrder"
            :header="stage.stageName"
          >
            <template #extra>
              <a-tag :color="PROJECT_STAGE_STATUS_MAP[stage.status]?.color ?? 'gray'" size="small">
                {{ PROJECT_STAGE_STATUS_MAP[stage.status]?.label ?? stage.status }}
              </a-tag>
            </template>
            <a-empty v-if="!stage.stageFormData" description="暂无成果数据" />
          </a-collapse-item>
        </a-collapse>
      </template>
      <a-empty v-else description="暂无阶段数据" />
    </a-tab-pane>

  </a-tabs>
</a-drawer>
```

**抽屉状态逻辑：**
```typescript
const drawerVisible = ref(false)
const drawerTab = ref('application')
const activePanel = ref<string | null>(null)

// 点击按钮：同一个再点关闭，不同的切换
function togglePanel(key: string) {
  if (activePanel.value === key && drawerVisible.value) {
    drawerVisible.value = false
    activePanel.value = null
  } else {
    activePanel.value = key
    drawerTab.value = key
    drawerVisible.value = true
  }
}

function closeDrawer() {
  drawerVisible.value = false
  activePanel.value = null
}
```

---

## Task 4：任务信息卡片精简

**Files:**
- Modify: `continew-admin-ui/src/views/review/task/detail/[id].vue`

当前任务信息卡片占用太多空间，精简为页头副标题区域内联展示：

```vue
<a-page-header :title="detail?.projectName ?? '任务详情'" @back="...">
  <template #subtitle>
    <a-space size="small" style="margin-left: 8px;">
      <a-tag>{{ PROJECT_TASK_TYPE_MAP[detail.taskType] ?? detail.taskType }}</a-tag>
      <span style="color: var(--color-text-3); font-size: 13px;">{{ detail.nodeName }}</span>
      <a-tag :color="PROJECT_TASK_STATUS_MAP[detail.taskStatus]?.color ?? 'gray'">
        {{ PROJECT_TASK_STATUS_MAP[detail.taskStatus]?.label ?? detail.taskStatus }}
      </a-tag>
    </a-space>
  </template>
  <template #extra>
    <a-space>
      <a-button @click="router.push({ path: '/review/task', query: { t: Date.now() } })">返回列表</a-button>
    </a-space>
  </template>
</a-page-header>
```

删除原来的"任务信息"卡片（`<a-card title="任务信息">`），分配时间移到决策区底部小字显示。

---

## Task 5：决策区 UI 优化

**Files:**
- Modify: `continew-admin-ui/src/views/review/task/detail/[id].vue`

决策区改为更清晰的视觉层次：

```vue
<a-card v-if="isEditable" style="margin-bottom: 16px;">
  <template #title>
    <span>提交决策</span>
    <span style="font-size: 12px; color: var(--color-text-3); margin-left: 8px;">
      分配时间：{{ detail.assignTime }}
    </span>
  </template>

  <!-- 决策结果选择（非评分表模式） -->
  <div v-if="!hasScoreTable" class="decision-options">
    <div
      v-for="opt in decisionOptions"
      :key="opt.value"
      class="decision-option"
      :class="{ selected: submitForm.decision === opt.value }"
      @click="submitForm.decision = opt.value"
    >
      <component :is="opt.icon" class="option-icon" />
      <span>{{ opt.label }}</span>
    </div>
  </div>

  <a-alert v-if="hasScoreTable" type="info" style="margin-bottom: 16px;">
    表单中包含评分表，提交后系统将自动汇总评分并生成决策结果。
  </a-alert>

  <!-- 验收驳回回退阶段 -->
  <a-form-item
    v-if="detail.taskType === 'ACCEPTANCE' && submitForm.decision === 'REJECT'"
    label="驳回回退到阶段"
    required
    style="margin-top: 16px;"
  >
    <a-select v-model="submitForm.rejectBackToStageOrder" placeholder="请选择回退到的阶段" style="width: 280px;">
      <a-option v-for="stage in detail.allStages" :key="stage.stageOrder" :value="stage.stageOrder">
        {{ stage.stageName }}
      </a-option>
    </a-select>
  </a-form-item>

  <!-- 操作按钮 -->
  <div class="decision-actions">
    <a-space>
      <a-button :loading="saving" @click="onSave">
        <template #icon><icon-save /></template>
        暂存
      </a-button>
      <a-button @click="openTransferModal">
        <template #icon><icon-swap /></template>
        转办
      </a-button>
      <a-button type="primary" :loading="submitting" @click="onSubmit">
        <template #icon><icon-send /></template>
        提交决策
      </a-button>
    </a-space>
  </div>
</a-card>
```

**决策选项数据（computed，按任务类型过滤）：**
```typescript
const decisionOptions = computed(() => {
  const base = [
    { value: 'PASS', label: '通过', icon: 'icon-check-circle', color: 'green' },
    { value: 'REJECT', label: '驳回', icon: 'icon-close-circle', color: 'red' },
  ]
  if (detail.value?.taskType === 'MANAGEMENT') {
    base.push(
      { value: 'UNQUALIFIED', label: '不合格', icon: 'icon-exclamation-circle', color: 'orange' },
      { value: 'WITHDRAW', label: '撤回', icon: 'icon-undo', color: 'gray' },
    )
  }
  return base
})
```

**决策选项样式：**
```scss
.decision-options {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.decision-option {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  border: 2px solid var(--color-border-2);
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  color: var(--color-text-1);
  transition: all 0.2s;
  background: var(--color-bg-1);

  &:hover {
    border-color: var(--color-primary-4);
    color: var(--color-primary-6);
  }

  &.selected {
    border-color: var(--color-primary-6);
    background: var(--color-primary-1);
    color: var(--color-primary-6);
    font-weight: 500;
  }

  .option-icon {
    font-size: 16px;
  }
}
```

---

## 执行顺序

1. Task 4（页头精简，最小改动，先建立基础）
2. Task 1（主区域布局重构 + 右侧按钮列）
3. Task 2（水平进度条 + Popover）
4. Task 3（抽屉 + Tabs）
5. Task 5（决策区 UI 优化）
