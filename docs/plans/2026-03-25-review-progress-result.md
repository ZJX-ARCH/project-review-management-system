# 评审进度结果展示 Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 在项目详情页的评审进度条中展示每个节点的实际结果（通过/驳回、人数、均分），并修复终止/归档后进度条消失的问题。

**Architecture:**
后端 `buildReviewProgress()` 补充查询已完成任务的汇总结果，填充到 `NodeProgressItem` 新增字段；前端进度条节点支持点击弹出 Popover 显示详情，终止/归档状态下也显示进度条。

**Tech Stack:** Spring Boot 3.x + MyBatis Plus / Vue 3.5 + Arco Design 2.57

---

## Task 1：后端 - NodeProgressItem 新增结果字段

**Files:**
- Modify: `continew-admin/continew-plugin-review/continew-plugin-review-project/src/main/java/top/continew/admin/review/project/model/resp/ProjectDetailResp.java:128-138`

在 `NodeProgressItem` 内部类新增 4 个字段：

```java
@Data
@Schema(description = "评审节点进度项")
public static class NodeProgressItem {
    @Schema(description = "节点类型（AUDIT/REVIEW/DECISION）")
    private String nodeType;
    @Schema(description = "节点序号")
    private Integer nodeSequence;
    @Schema(description = "节点名称")
    private String nodeName;
    /** PENDING / ACTIVE / COMPLETED / REJECTED */
    @Schema(description = "节点状态")
    private String nodeStatus;
    // ↓ 新增字段
    @Schema(description = "通过人数（已完成节点才有）")
    private Integer passCount;
    @Schema(description = "总人数（已完成节点才有）")
    private Integer totalCount;
    @Schema(description = "平均分（SCORE_PASS 模式才有）")
    private java.math.BigDecimal averageScore;
    @Schema(description = "节点结果（PASS/REJECT，已完成节点才有）")
    private String nodeResult;
}
```

注意：`nodeStatus` 新增 `REJECTED` 值，用于区分"已完成且通过"和"已完成但驳回"。

---

## Task 2：后端 - buildReviewProgress() 补充查询任务结果

**Files:**
- Modify: `continew-admin/continew-plugin-review/continew-plugin-review-project/src/main/java/top/continew/admin/review/project/service/impl/ProjectServiceImpl.java`

**Step 1：方法签名加入 projectId 参数**

```java
private List<ProjectDetailResp.NodeProgressItem> buildReviewProgress(
        ProjectTypeSnapshot snapshot, ReviewProjectDO project) {
```

**Step 2：查询该项目所有已完成任务，按节点分组汇总**

在方法开头，查询 `review_task` 表中该项目所有 `status=COMPLETED` 的评审类任务（AUDIT/REVIEW/DECISION），按 `taskType + nodeSequence` 分组：

```java
// 查已完成的评审任务，构建节点结果 Map
// key: "AUDIT_1" / "REVIEW_1" / "DECISION_1"
Map<String, NodeResult> nodeResultMap = buildNodeResultMap(project.getId());
```

新增私有方法 `buildNodeResultMap`：

```java
private Map<String, NodeResult> buildNodeResultMap(Long projectId) {
    List<ReviewTaskDO> completedTasks = taskMapper.selectList(
        new LambdaQueryWrapper<ReviewTaskDO>()
            .eq(ReviewTaskDO::getProjectId, projectId)
            .eq(ReviewTaskDO::getStatus, TaskStatusEnum.COMPLETED)
            .in(ReviewTaskDO::getTaskType, List.of(TaskType.AUDIT, TaskType.REVIEW, TaskType.DECISION))
            .eq(ReviewTaskDO::getDeleted, 0));

    // 按 taskType_nodeSequence 分组
    Map<String, List<ReviewTaskDO>> grouped = completedTasks.stream()
        .collect(Collectors.groupingBy(t -> t.getTaskType().getValue() + "_" + t.getNodeSequence()));

    Map<String, NodeResult> result = new HashMap<>();
    for (Map.Entry<String, List<ReviewTaskDO>> entry : grouped.entrySet()) {
        List<ReviewTaskDO> tasks = entry.getValue();
        long passCount = tasks.stream().filter(t -> t.getDecision() == TaskDecisionEnum.PASS).count();
        OptionalDouble avg = tasks.stream()
            .filter(t -> t.getScore() != null)
            .mapToDouble(t -> t.getScore().doubleValue())
            .average();
        NodeResult nr = new NodeResult();
        nr.passCount = (int) passCount;
        nr.totalCount = tasks.size();
        nr.averageScore = avg.isPresent()
            ? BigDecimal.valueOf(avg.getAsDouble()).setScale(2, RoundingMode.HALF_UP) : null;
        nr.result = passCount == tasks.size() ? "PASS" : "REJECT";
        result.put(entry.getKey(), nr);
    }
    return result;
}

// 内部数据类
private static class NodeResult {
    int passCount;
    int totalCount;
    BigDecimal averageScore;
    String result; // PASS / REJECT
}
```

**Step 3：在 buildReviewProgress() 中使用 nodeResultMap 填充结果**

在 `.map(round -> { ... })` 内，设置完 `nodeStatus` 后，补充填充结果字段：

```java
// 填充已完成节点的结果
String key = round.getRoundType() + "_" + round.getRoundSequence();
NodeResult nr = nodeResultMap.get(key);
if (nr != null) {
    item.setPassCount(nr.passCount);
    item.setTotalCount(nr.totalCount);
    item.setAverageScore(nr.averageScore);
    item.setNodeResult(nr.result);
    // 用实际结果覆盖推断的 nodeStatus
    if ("COMPLETED".equals(item.getNodeStatus())) {
        item.setNodeStatus("PASS".equals(nr.result) ? "COMPLETED" : "REJECTED");
    }
}
```

**Step 4：修复终止状态下的进度条**

当前 `allCompleted` 判断：
```java
boolean allCompleted = project.getStatus() != null && project.getStatus().getValue() >= 50;
```

需要额外处理 `TERMINATED`（status=49）：终止时不是"全部完成"，而是某轮被驳回。此时 `currentNodeType` 已被清空，需要从 `nodeResultMap` 反推哪轮是最后一轮。

修改逻辑：
```java
boolean allCompleted = project.getStatus() != null && project.getStatus().getValue() >= 50;
boolean isTerminated = project.getStatus() == ProjectStatus.TERMINATED;

// 终止时：有结果的节点按结果显示，无结果的节点显示 PENDING
// 执行阶段/归档时：所有评审节点均已完成
```

在 `.map(round -> { ... })` 中，当 `isTerminated` 且 `currentNodeType == null` 时：
- 有 `nodeResultMap` 记录的节点 → 按实际结果显示
- 无记录的节点 → PENDING

---

## Task 3：前端 - NodeProgressItem 类型更新

**Files:**
- Modify: `continew-admin-ui/src/apis/review/type.ts`

在 `NodeProgressItem` 接口新增字段：

```typescript
/** 评审节点进度项 */
export interface NodeProgressItem {
  nodeType: string
  nodeSequence: number
  nodeName: string
  /** PENDING / ACTIVE / COMPLETED / REJECTED */
  nodeStatus: string
  // 新增
  passCount?: number
  totalCount?: number
  averageScore?: number
  nodeResult?: string
}
```

---

## Task 4：前端 - 进度条展示结果 + 修复终止状态显示

**Files:**
- Modify: `continew-admin-ui/src/views/review/project/detail/[id].vue`

**Step 1：修复 isReviewPhase 判断，终止/归档后也显示进度条**

```typescript
// 原来：只有评审中才显示
const REVIEW_PHASE_STATUSES = [10, 20, 30, 40]
const isReviewPhase = computed(() => REVIEW_PHASE_STATUSES.includes(detail.value?.status ?? 0))

// 改为：有快照（曾经提交过）就显示进度条
const hasReviewProgress = computed(() =>
  (detail.value?.reviewProgress?.length ?? 0) > 0
)
```

**Step 2：nodeStatusToStep 新增 REJECTED 映射**

```typescript
function nodeStatusToStep(status: string): 'wait' | 'process' | 'finish' | 'error' {
  if (status === 'COMPLETED') return 'finish'
  if (status === 'ACTIVE') return 'process'
  if (status === 'REJECTED') return 'error'   // 新增
  return 'wait'
}
```

**Step 3：进度条节点加 Popover 显示结果详情**

```vue
<!-- 评审阶段进度（有快照就显示） -->
<a-card v-if="hasReviewProgress" title="评审流程进度" style="margin-bottom: 16px;">
  <a-steps size="small">
    <a-step
      v-for="(node, idx) in detail.reviewProgress"
      :key="idx"
      :title="node.nodeName || `${TASK_TYPE_LABEL[node.nodeType] ?? node.nodeType} 第${node.nodeSequence}轮`"
      :status="nodeStatusToStep(node.nodeStatus)"
    >
      <template v-if="node.nodeResult" #description>
        <a-popover position="bottom" trigger="hover">
          <span class="node-result-link">
            <a-tag
              :color="node.nodeResult === 'PASS' ? 'green' : 'red'"
              size="small"
            >{{ node.nodeResult === 'PASS' ? '通过' : '驳回' }}</a-tag>
          </span>
          <template #content>
            <div class="node-result-popover">
              <div>通过：{{ node.passCount }} / {{ node.totalCount }} 人</div>
              <div v-if="node.averageScore != null">平均分：{{ node.averageScore }}</div>
            </div>
          </template>
        </a-popover>
      </template>
    </a-step>
  </a-steps>
</a-card>
```

**Step 4：加样式**

```css
.node-result-link {
  cursor: default;
}

.node-result-popover {
  min-width: 130px;
  line-height: 1.8;
  font-size: 13px;
}
```

---

## 执行顺序

1. Task 1（后端 DTO 加字段）
2. Task 2（后端 buildReviewProgress 补充查询）
3. Task 3（前端类型更新）
4. Task 4（前端进度条展示）
