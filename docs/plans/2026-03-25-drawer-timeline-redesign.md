# 抽屉步骤条重设计 Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 将任务详情页抽屉的 Tabs 改为步骤条，每步对应一个节点（申请表单 + 各轮审核/评审/决策），展示每个人的填写内容，并严格控制同轮互不可见。

**Architecture:**
后端新增两个接口：① 任务详情页获取节点历史详情（处理人视角，只返回前序节点每人内容）；② 项目详情页获取完整历史（申请人视角，返回所有已完成节点每人内容）。前端抽屉改为垂直步骤条，点击步骤展开对应内容。

**Tech Stack:** Spring Boot 3.x + MyBatis Plus / Vue 3.5 + Arco Design 2.57（a-steps + a-collapse）

---

## 可见性规则

| 查看者 | 申请表单 | 前序节点（已完成） | 当前节点 | 后续节点 |
|--------|---------|-----------------|---------|---------|
| 申请人（项目详情页） | ✅ 自己填的 | ✅ 每人内容+姓名 | ✅ 每人内容+姓名 | ✅ 全部 |
| 处理人（任务详情页） | ✅ 只读 | ✅ 每人内容+姓名 | ❌ 只看自己 | ❌ 不展示 |
| 同轮其他处理人 | ✅ 只读 | ✅ 前序 | ❌ 互不可见 | ❌ |

---

## 数据结构设计

### 新增 DTO：NodeHistoryResp（节点历史，含每人填写）

```java
// 位置：continew-plugin-review-project/.../model/resp/NodeHistoryResp.java
@Data
public class NodeHistoryResp implements Serializable {
    /** 节点类型 AUDIT/REVIEW/DECISION */
    private String nodeType;
    /** 节点序号 */
    private Integer nodeSequence;
    /** 节点名称 */
    private String nodeName;
    /** 节点汇总结果 PASS/REJECT（已完成节点才有） */
    private String nodeResult;
    /** 通过人数 */
    private Integer passCount;
    /** 总人数 */
    private Integer totalCount;
    /** 平均分（SCORE_PASS 模式） */
    private BigDecimal averageScore;
    /** 该节点每个人的填写记录 */
    private List<PersonEntryResp> entries;

    @Data
    public static class PersonEntryResp implements Serializable {
        /** 处理人姓名 */
        private String assigneeName;
        /** 决策结果 PASS/REJECT */
        private String decision;
        /** 评分（SCORE_PASS 模式） */
        private BigDecimal score;
        /** 表单填写数据 */
        private Map<String, Object> formData;
        /** 表单模板（用于渲染） */
        private FormTemplateResp formTemplate;
        /** 完成时间 */
        private LocalDateTime completeTime;
    }
}
```

---

## Task 1：后端 - 创建 NodeHistoryResp DTO

**Files:**
- Create: `continew-admin/continew-plugin-review/continew-plugin-review-project/src/main/java/top/continew/admin/review/project/model/resp/NodeHistoryResp.java`

完整代码见上方数据结构设计。

---

## Task 2：后端 - TaskService 新增 getNodeHistory 方法（处理人视角）

**Files:**
- Modify: `continew-plugin-review-project/.../service/TaskService.java`
- Modify: `continew-plugin-review-project/.../service/impl/TaskServiceImpl.java`

**接口声明：**
```java
/**
 * 获取任务的节点历史（处理人视角）
 * 返回：申请表单节点 + 前序已完成节点每人内容（不含当前节点同轮其他人）
 */
List<NodeHistoryResp> getNodeHistory(Long taskId);
```

**实现逻辑：**

```java
@Override
public List<NodeHistoryResp> getNodeHistory(Long taskId) {
    ReviewTaskDO task = getTaskAndCheckAssignee(taskId);
    ReviewProjectDO project = projectMapper.selectById(task.getProjectId());
    if (project == null) throw new BusinessException("关联项目不存在");

    List<NodeHistoryResp> result = new ArrayList<>();

    // 1. 申请表单节点（固定第一个）
    NodeHistoryResp appNode = buildApplicationNode(project);
    result.add(appNode);

    // 2. 前序已完成节点（不含当前节点的同轮任务）
    // 查所有 COMPLETED 任务，排除当前节点（同 taskType + nodeSequence）
    List<ReviewTaskDO> completedTasks = taskMapper.selectList(
        new LambdaQueryWrapper<ReviewTaskDO>()
            .eq(ReviewTaskDO::getProjectId, project.getId())
            .eq(ReviewTaskDO::getStatus, TaskStatusEnum.COMPLETED)
            .eq(ReviewTaskDO::getDeleted, 0)
            // 排除当前节点的所有任务（同轮互不可见）
            .not(w -> w.eq(ReviewTaskDO::getTaskType, task.getTaskType())
                       .eq(ReviewTaskDO::getNodeSequence, task.getNodeSequence())));

    result.addAll(buildNodeHistoryList(project, completedTasks));
    return result;
}
```

**buildApplicationNode 方法：**
```java
private NodeHistoryResp buildApplicationNode(ReviewProjectDO project) {
    NodeHistoryResp node = new NodeHistoryResp();
    node.setNodeType("APPLICATION");
    node.setNodeSequence(0);
    node.setNodeName("申请表单");
    // 申请人信息
    NodeHistoryResp.PersonEntryResp entry = new NodeHistoryResp.PersonEntryResp();
    UserDO applicant = userMapper.selectById(project.getApplicantId());
    entry.setAssigneeName(applicant != null ? applicant.getNickname() : "申请人");
    entry.setFormData(project.getApplicationFormData() instanceof Map
        ? (Map<String, Object>) project.getApplicationFormData() : null);
    // 申请表单模板
    try {
        ProjectTypeSnapshot snapshot = objectMapper.convertValue(
            project.getSnapshotConfig(), ProjectTypeSnapshot.class);
        if (snapshot != null && snapshot.getFormMappings() != null) {
            Long tplId = snapshot.getFormMappings().get("APPLICATION");
            if (tplId != null) entry.setFormTemplate(formTemplateService.getDetail(tplId));
        }
    } catch (Exception ignored) {}
    node.setEntries(List.of(entry));
    return node;
}
```

**buildNodeHistoryList 方法（按节点分组，填充每人内容）：**
```java
private List<NodeHistoryResp> buildNodeHistoryList(
        ReviewProjectDO project, List<ReviewTaskDO> tasks) {
    // 按 taskType_nodeSequence 分组
    Map<String, List<ReviewTaskDO>> grouped = tasks.stream()
        .collect(Collectors.groupingBy(
            t -> t.getTaskType().getValue() + "_" + t.getNodeSequence()));

    // 查所有处理人姓名
    Set<Long> assigneeIds = tasks.stream()
        .map(ReviewTaskDO::getAssigneeId).collect(Collectors.toSet());
    Map<Long, String> nameMap = userMapper.selectBatchIds(assigneeIds).stream()
        .collect(Collectors.toMap(UserDO::getId, UserDO::getNickname));

    List<NodeHistoryResp> nodes = new ArrayList<>();
    for (Map.Entry<String, List<ReviewTaskDO>> entry : grouped.entrySet()) {
        List<ReviewTaskDO> nodeTasks = entry.getValue();
        ReviewTaskDO sample = nodeTasks.get(0);

        NodeHistoryResp node = new NodeHistoryResp();
        node.setNodeType(sample.getTaskType().getValue());
        node.setNodeSequence(sample.getNodeSequence());
        node.setNodeName(resolveNodeName(project, sample.getTaskType(), sample.getNodeSequence()));

        // 汇总
        long passCount = nodeTasks.stream()
            .filter(t -> t.getDecision() == TaskDecisionEnum.PASS).count();
        node.setPassCount((int) passCount);
        node.setTotalCount(nodeTasks.size());
        node.setNodeResult(passCount == nodeTasks.size() ? "PASS" : "REJECT");
        OptionalDouble avg = nodeTasks.stream()
            .filter(t -> t.getScore() != null)
            .mapToDouble(t -> t.getScore().doubleValue()).average();
        if (avg.isPresent()) node.setAverageScore(
            BigDecimal.valueOf(avg.getAsDouble()).setScale(2, RoundingMode.HALF_UP));

        // 每人填写
        List<NodeHistoryResp.PersonEntryResp> entries = nodeTasks.stream().map(t -> {
            NodeHistoryResp.PersonEntryResp pe = new NodeHistoryResp.PersonEntryResp();
            pe.setAssigneeName(nameMap.getOrDefault(t.getAssigneeId(), "处理人"));
            pe.setDecision(t.getDecision() != null ? t.getDecision().getValue() : null);
            pe.setScore(t.getScore());
            pe.setCompleteTime(t.getCompleteTime());
            if (t.getFormData() instanceof Map) {
                pe.setFormData((Map<String, Object>) t.getFormData());
            }
            // 加载表单模板
            pe.setFormTemplate(resolveTaskFormTemplate(project,
                sample.getTaskType(), sample.getNodeSequence()));
            return pe;
        }).collect(Collectors.toList());
        node.setEntries(entries);
        nodes.add(node);
    }

    // 按 AUDIT→REVIEW→DECISION + sequence 排序
    List<String> typeOrder = List.of("AUDIT", "REVIEW", "DECISION");
    nodes.sort(Comparator
        .comparingInt((NodeHistoryResp n) -> typeOrder.indexOf(n.getNodeType()))
        .thenComparingInt(NodeHistoryResp::getNodeSequence));
    return nodes;
}
```

---

## Task 3：后端 - ProjectService 新增 getReviewHistory 方法（申请人视角）

**Files:**
- Modify: `continew-plugin-review-project/.../service/ProjectService.java`
- Modify: `continew-plugin-review-project/.../service/impl/ProjectServiceImpl.java`

**接口声明：**
```java
/**
 * 获取项目完整评审历史（申请人视角）
 * 返回：申请表单节点 + 所有已完成节点每人内容
 */
List<NodeHistoryResp> getReviewHistory(Long projectId);
```

**实现逻辑：**
```java
@Override
public List<NodeHistoryResp> getReviewHistory(Long projectId) {
    ReviewProjectDO project = getProjectAndCheckOwner(projectId);

    List<NodeHistoryResp> result = new ArrayList<>();

    // 1. 申请表单节点
    result.add(buildApplicationNode(project));

    // 2. 所有已完成的评审任务（申请人可看全部）
    List<ReviewTaskDO> completedTasks = taskMapper.selectList(
        new LambdaQueryWrapper<ReviewTaskDO>()
            .eq(ReviewTaskDO::getProjectId, projectId)
            .eq(ReviewTaskDO::getStatus, TaskStatusEnum.COMPLETED)
            .in(ReviewTaskDO::getTaskType,
                List.of(TaskType.AUDIT, TaskType.REVIEW, TaskType.DECISION))
            .eq(ReviewTaskDO::getDeleted, 0));

    result.addAll(buildNodeHistoryList(project, completedTasks));
    return result;
}
```

注意：`buildApplicationNode` 和 `buildNodeHistoryList` 两个方法在 TaskServiceImpl 和 ProjectServiceImpl 中都需要，提取到一个共享的 `NodeHistoryBuilder` 组件（Spring Bean）避免重复。

---

## Task 4：后端 - 提取 NodeHistoryBuilder 组件（DRY）

**Files:**
- Create: `continew-plugin-review-project/.../engine/NodeHistoryBuilder.java`

将 `buildApplicationNode` 和 `buildNodeHistoryList` 提取为独立 Spring `@Component`，TaskServiceImpl 和 ProjectServiceImpl 都注入使用。

---

## Task 5：后端 - Controller 新增接口

**Files:**
- Modify: `continew-plugin-review-project/.../controller/TaskController.java`
- Modify: `continew-plugin-review-project/.../controller/ProjectController.java`

**TaskController 新增：**
```java
@GetMapping("/{taskId}/history")
@Operation(summary = "获取节点历史（处理人视角）")
@SaCheckPermission("review:task:query")
public R<List<NodeHistoryResp>> getNodeHistory(@PathVariable Long taskId) {
    return R.ok(taskService.getNodeHistory(taskId));
}
```

**ProjectController 新增：**
```java
@GetMapping("/{projectId}/review-history")
@Operation(summary = "获取评审历史（申请人视角）")
@SaCheckPermission("review:project:query")
public R<List<NodeHistoryResp>> getReviewHistory(@PathVariable Long projectId) {
    return R.ok(projectService.getReviewHistory(projectId));
}
```

---

## Task 6：前端 - 新增 API 函数和类型

**Files:**
- Modify: `continew-admin-ui/src/apis/review/type.ts`
- Modify: `continew-admin-ui/src/apis/review/task.ts`
- Modify: `continew-admin-ui/src/apis/review/project.ts`

**type.ts 新增：**
```typescript
/** 节点历史中每人的填写记录 */
export interface PersonEntryResp {
  assigneeName: string
  decision?: string
  score?: number
  formData?: Record<string, unknown>
  formTemplate?: ProjectFormTemplateResp
  completeTime?: string
}

/** 节点历史（含每人填写） */
export interface NodeHistoryResp {
  nodeType: string        // APPLICATION / AUDIT / REVIEW / DECISION
  nodeSequence: number
  nodeName: string
  nodeResult?: string     // PASS / REJECT
  passCount?: number
  totalCount?: number
  averageScore?: number
  entries: PersonEntryResp[]
}
```

**task.ts 新增：**
```typescript
export function getTaskNodeHistory(id: number | string) {
  return http.get<NodeHistoryResp[]>(`/review/task/${id}/history`)
}
```

**project.ts 新增：**
```typescript
export function getProjectReviewHistory(id: number | string) {
  return http.get<NodeHistoryResp[]>(`/review/project/${id}/review-history`)
}
```

---

## Task 7：前端 - 任务详情页抽屉改为步骤条

**Files:**
- Modify: `continew-admin-ui/src/views/review/task/detail/[id].vue`

**Step 1: 抽屉内容替换**

删除原来的 `a-tabs`，改为步骤条 + 折叠面板：

```vue
<a-drawer
  v-model:visible="drawerVisible"
  :title="detail?.projectName"
  :width="drawerWidth"
  :mask="false"
  placement="right"
  @cancel="closeDrawer"
>
  <div class="drawer-resize-handle" @mousedown="onResizeStart" />

  <!-- 加载中 -->
  <a-spin v-if="historyLoading" style="width:100%; padding: 40px 0; text-align:center;" />

  <!-- 步骤条 -->
  <div v-else class="history-timeline">
    <div
      v-for="(node, idx) in nodeHistory"
      :key="`${node.nodeType}_${node.nodeSequence}`"
      class="history-node-item"
    >
      <!-- 节点头部（可点击展开/收起） -->
      <div
        class="history-node-header"
        :class="{ expanded: expandedNodes.has(`${node.nodeType}_${node.nodeSequence}`) }"
        @click="toggleNode(`${node.nodeType}_${node.nodeSequence}`)"
      >
        <!-- 左侧：步骤圆点 + 连接线 -->
        <div class="node-step-indicator">
          <div class="node-dot" :class="nodeDotClass(node)">
            <icon-check v-if="node.nodeResult === 'PASS'" />
            <icon-close v-else-if="node.nodeResult === 'REJECT'" />
            <icon-file v-else-if="node.nodeType === 'APPLICATION'" />
            <span v-else>{{ idx }}</span>
          </div>
          <div v-if="idx < nodeHistory.length - 1" class="node-line" />
        </div>
        <!-- 右侧：节点信息 -->
        <div class="node-header-content">
          <div class="node-header-top">
            <span class="node-name">{{ node.nodeName }}</span>
            <a-tag
              v-if="node.nodeResult"
              :color="node.nodeResult === 'PASS' ? 'green' : 'red'"
              size="small"
            >{{ node.nodeResult === 'PASS' ? '通过' : '驳回' }}</a-tag>
            <span v-if="node.passCount != null" class="node-count-text">
              {{ node.passCount }}/{{ node.totalCount }} 人通过
            </span>
          </div>
          <div v-if="node.averageScore != null" class="node-avg-score">
            平均分：{{ node.averageScore }}
          </div>
        </div>
        <icon-down
          class="node-expand-icon"
          :class="{ rotated: expandedNodes.has(`${node.nodeType}_${node.nodeSequence}`) }"
        />
      </div>

      <!-- 展开内容：每人填写 -->
      <div
        v-if="expandedNodes.has(`${node.nodeType}_${node.nodeSequence}`)"
        class="history-node-entries"
      >
        <div
          v-for="(entry, ei) in node.entries"
          :key="ei"
          class="person-entry"
        >
          <!-- 人员头部 -->
          <div class="person-entry-header">
            <a-avatar :size="28" style="background: var(--color-primary-6); font-size: 12px;">
              {{ entry.assigneeName?.charAt(0) }}
            </a-avatar>
            <span class="person-name">{{ entry.assigneeName }}</span>
            <a-tag
              v-if="entry.decision"
              :color="entry.decision === 'PASS' ? 'green' : 'red'"
              size="small"
            >{{ entry.decision === 'PASS' ? '通过' : '驳回' }}</a-tag>
            <span v-if="entry.score != null" class="person-score">{{ entry.score }} 分</span>
            <span class="person-time">{{ formatTime(entry.completeTime) }}</span>
          </div>
          <!-- 表单内容 -->
          <div v-if="entry.formTemplate && entry.formData" class="person-form">
            <FormRenderer
              :model-value="entry.formData"
              :template="entry.formTemplate"
              readonly
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</a-drawer>
```

**Step 2: 数据加载逻辑**

```typescript
import { getTaskNodeHistory } from '@/apis/review'
import type { NodeHistoryResp } from '@/apis/review/type'

const nodeHistory = ref<NodeHistoryResp[]>([])
const historyLoading = ref(false)
const expandedNodes = ref<Set<string>>(new Set())

// 打开抽屉时加载历史
async function openDrawer() {
  drawerVisible.value = true
  historyLoading.value = true
  try {
    const res = await getTaskNodeHistory(taskId.value)
    nodeHistory.value = res.data ?? []
    // 默认展开第一个节点（申请表单）
    if (nodeHistory.value.length > 0) {
      const first = nodeHistory.value[0]
      expandedNodes.value.add(`${first.nodeType}_${first.nodeSequence}`)
    }
  } finally {
    historyLoading.value = false
  }
}

function toggleNode(key: string) {
  if (expandedNodes.value.has(key)) {
    expandedNodes.value.delete(key)
  } else {
    expandedNodes.value.add(key)
  }
}

function nodeDotClass(node: NodeHistoryResp) {
  if (node.nodeType === 'APPLICATION') return 'dot-application'
  if (node.nodeResult === 'PASS') return 'dot-pass'
  if (node.nodeResult === 'REJECT') return 'dot-reject'
  return 'dot-pending'
}

function formatTime(t?: string) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 16)
}
```

**Step 3: 右侧按钮列改为单一"查看历史"按钮**

原来的 `sidePanels`（申请表单/历史节点/阶段成果）合并为一个按钮，点击打开步骤条抽屉：

```typescript
// 简化：只有一个按钮
function openHistoryDrawer() {
  openDrawer()
  drawerVisible.value = true
}
```

**Step 4: 样式**

```scss
.history-timeline {
  padding: 8px 0;
}

.history-node-item {
  display: flex;
  flex-direction: column;
}

.history-node-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 10px 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.15s;

  &:hover {
    background: var(--color-fill-2);
  }
}

.node-step-indicator {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex-shrink: 0;
  width: 28px;
}

.node-dot {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  color: #fff;

  &.dot-application { background: var(--color-primary-6); }
  &.dot-pass        { background: #00b42a; }
  &.dot-reject      { background: #f53f3f; }
  &.dot-pending     { background: var(--color-fill-4); color: var(--color-text-3); }
}

.node-line {
  width: 2px;
  flex: 1;
  min-height: 16px;
  background: var(--color-border-2);
  margin: 4px 0;
}

.node-header-content {
  flex: 1;
  min-width: 0;
}

.node-header-top {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.node-name {
  font-weight: 500;
  font-size: 14px;
}

.node-count-text {
  font-size: 12px;
  color: var(--color-text-3);
}

.node-avg-score {
  font-size: 12px;
  color: var(--color-text-3);
  margin-top: 2px;
}

.node-expand-icon {
  flex-shrink: 0;
  color: var(--color-text-3);
  transition: transform 0.2s;
  margin-top: 6px;

  &.rotated { transform: rotate(180deg); }
}

.history-node-entries {
  margin-left: 40px;
  border-left: 2px solid var(--color-border-2);
  padding-left: 16px;
  margin-bottom: 8px;
}

.person-entry {
  margin-bottom: 16px;
  padding: 12px;
  border: 1px solid var(--color-border-2);
  border-radius: 6px;
  background: var(--color-bg-2);
}

.person-entry-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.person-name {
  font-weight: 500;
  font-size: 13px;
}

.person-score {
  font-size: 12px;
  color: var(--color-primary-6);
  font-weight: 500;
}

.person-time {
  font-size: 12px;
  color: var(--color-text-3);
  margin-left: auto;
}

.person-form {
  border-top: 1px solid var(--color-border-2);
  padding-top: 10px;
}
```

---

## Task 8：前端 - 项目详情页同样改造

**Files:**
- Modify: `continew-admin-ui/src/views/review/project/detail/[id].vue`

申请人视角的项目详情页，在"申请信息"卡片下方新增"评审历史"卡片，使用相同的步骤条组件展示完整历史（调用 `getProjectReviewHistory`）。

提取步骤条为独立组件复用：

**Files:**
- Create: `continew-admin-ui/src/views/review/project/components/ReviewHistoryTimeline.vue`

该组件接收 `nodeHistory: NodeHistoryResp[]` prop，渲染步骤条，任务详情页抽屉和项目详情页都使用它。

---

## 执行顺序

1. Task 1（创建 NodeHistoryResp DTO）
2. Task 4（创建 NodeHistoryBuilder 组件）
3. Task 2（TaskService 实现）
4. Task 3（ProjectService 实现）
5. Task 5（Controller 接口）
6. Task 6（前端 API 和类型）
7. Task 8 前半（提取 ReviewHistoryTimeline 组件）
8. Task 7（任务详情页抽屉改造）
9. Task 8 后半（项目详情页接入）
