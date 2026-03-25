# 进度条完成人数 + 前后端状态值对齐 Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 修复进度条"已完成N/M人"展示、前后端 ProjectStatus 状态值不一致、以及流程检查发现的其他问题。

**Architecture:**
后端 NodeProgressItem 新增 completedCount/requiredCount 字段，buildNodeResultMap 同时统计进行中节点的完成人数；前端 PROJECT_STATUS_MAP 状态值与后端枚举对齐；修复流程中发现的其他不一致。

**Tech Stack:** Spring Boot 3.x + MyBatis Plus / Vue 3.5 + Arco Design 2.57

---

## 流程一致性检查结论

### ✅ 完全对齐（无需修改）
- 两大阶段架构、事件驱动模型、4种审批规则、验收必须性、并发安全
- 管理阶段 REJECT 后不自动分配任务（等申请人重新提交成果触发 StageFormSubmittedEvent）✅ 正确
- 验收驳回回退到指定阶段 ✅ 正确
- 转办范围校验 ✅ 正确

### ❌ 需要修复的问题

**P0 - 严重：前端 PROJECT_STATUS_MAP 状态值与后端枚举不一致**

后端枚举实际值：
```
DRAFT=1, SUBMITTED=2
AUDITING=10, AUDIT_PASSED=11, NEEDS_REVISION_AUDIT=12, AUDIT_REJECTED=13, WAITING_AUDIT=14
REVIEWING=20, REVIEW_PASSED=21, NEEDS_REVISION_REVIEW=22, REVIEW_REJECTED=23, WAITING_REVIEW=24
DECIDING=30, DECISION_PASSED=31, NEEDS_REVISION_DECISION=32, DECISION_REJECTED=33, WAITING_DECISION=34
TERMINATED=40
EXECUTING=50, OVERTIME=51, SUSPENDED=52, ACCEPTING=53, ACCEPTANCE_PASSED=54, ACCEPTANCE_FAILED=55
ARCHIVED_COMPLETED=90, ARCHIVED_UNQUALIFIED=91, ARCHIVED_CANCELLED=92, VOIDED=99
```

前端当前 PROJECT_STATUS_MAP（错误）：
```
1=草稿, 10=已提交, 20=审核中, 30=评审中, 40=决策中, 49=已终止
50=执行中, 55=执行超时, 60=验收中, 90=已完成, 91=不合格, 92=已取消, 99=已作废
```

前端 REVIEW_PHASE_STATUSES、EXECUTION_PHASE_STATUSES、canRevoke 等判断也全部用了错误的值。

**P1 - 进度条缺少"已完成N/M人"展示**
NodeProgressItem 缺少 completedCount/requiredCount 字段，ACTIVE 节点看不到进度。

---

## Task 1：前端 - 修复 PROJECT_STATUS_MAP 和相关状态判断

**Files:**
- Modify: `continew-admin-ui/src/apis/review/type.ts`
- Modify: `continew-admin-ui/src/views/review/project/index.vue`
- Modify: `continew-admin-ui/src/views/review/project/detail/[id].vue`

**Step 1: 修复 type.ts 中的 ProjectStatus 枚举和 PROJECT_STATUS_MAP**

```typescript
export enum ProjectStatus {
  DRAFT = 1,
  SUBMITTED = 2,
  // 审核流程
  AUDITING = 10,
  AUDIT_PASSED = 11,
  NEEDS_REVISION_AUDIT = 12,
  AUDIT_REJECTED = 13,
  WAITING_AUDIT = 14,
  // 评审流程
  REVIEWING = 20,
  REVIEW_PASSED = 21,
  NEEDS_REVISION_REVIEW = 22,
  REVIEW_REJECTED = 23,
  WAITING_REVIEW = 24,
  // 决策流程
  DECIDING = 30,
  DECISION_PASSED = 31,
  NEEDS_REVISION_DECISION = 32,
  DECISION_REJECTED = 33,
  WAITING_DECISION = 34,
  // 终止
  TERMINATED = 40,
  // 执行阶段
  EXECUTING = 50,
  OVERTIME = 51,
  SUSPENDED = 52,
  ACCEPTING = 53,
  ACCEPTANCE_PASSED = 54,
  ACCEPTANCE_FAILED = 55,
  // 归档
  ARCHIVED_COMPLETED = 90,
  ARCHIVED_UNQUALIFIED = 91,
  ARCHIVED_CANCELLED = 92,
  VOIDED = 99,
}

export const PROJECT_STATUS_MAP: Record<number, { label: string; color: string }> = {
  1:  { label: '草稿',       color: 'gray'   },
  2:  { label: '已提交',     color: 'blue'   },
  10: { label: '审核中',     color: 'orange' },
  11: { label: '审核通过',   color: 'green'  },
  12: { label: '需修改',     color: 'gray'   },
  13: { label: '审核驳回',   color: 'red'    },
  14: { label: '等待中',     color: 'orange' },
  20: { label: '评审中',     color: 'orange' },
  21: { label: '评审通过',   color: 'green'  },
  22: { label: '需修改',     color: 'gray'   },
  23: { label: '评审驳回',   color: 'red'    },
  24: { label: '等待中',     color: 'orange' },
  30: { label: '决策中',     color: 'orange' },
  31: { label: '决策通过',   color: 'green'  },
  32: { label: '需修改',     color: 'gray'   },
  33: { label: '决策驳回',   color: 'red'    },
  34: { label: '等待中',     color: 'orange' },
  40: { label: '已终止',     color: 'red'    },
  50: { label: '执行中',     color: 'blue'   },
  51: { label: '执行超时',   color: 'red'    },
  52: { label: '项目暂停',   color: 'orange' },
  53: { label: '验收中',     color: 'purple' },
  54: { label: '验收通过',   color: 'green'  },
  55: { label: '验收不通过', color: 'red'    },
  90: { label: '已完成',     color: 'green'  },
  91: { label: '不合格',     color: 'red'    },
  92: { label: '已取消',     color: 'gray'   },
  99: { label: '已作废',     color: 'gray'   },
}
```

**Step 2: 修复 project/index.vue 中的状态判断**

```typescript
// canRevoke：评审阶段（已提交~决策中，含等待/需修改中间状态）
function canRevoke(status: number): boolean {
  return status >= 2 && status < 40
}
```

**Step 3: 修复 project/detail/[id].vue 中的状态判断**

```typescript
// 评审阶段：2~39
const REVIEW_PHASE_STATUSES_RANGE = [2, 39] // status >= 2 && status < 40
const isReviewPhase = computed(() => {
  const s = detail.value?.status ?? 0
  return s >= 2 && s < 40
})

// 执行阶段：50~89
const isExecutionPhase = computed(() => {
  const s = detail.value?.status ?? 0
  return s >= 50 && s < 90
})

// 可撤销：评审阶段
const canRevoke = computed(() => {
  const s = detail.value?.status ?? 0
  return s >= 2 && s < 40
})

// 可有痕修改：评审阶段
const canUpdateForm = computed(() => {
  const s = detail.value?.status ?? 0
  return s >= 2 && s < 40
})

// 终止状态
const isTerminated = computed(() => detail.value?.status === 40)
```

同时修复 `buildReviewProgress` 后端中 `isTerminated` 的判断：
```java
// 原来
boolean isTerminated = project.getStatus() == ProjectStatus.TERMINATED;
// 确认 ProjectStatus.TERMINATED.getValue() == 40 ✅ 正确，无需修改
```

---

## Task 2：后端 - NodeProgressItem 新增 completedCount/requiredCount

**Files:**
- Modify: `continew-admin/continew-plugin-review/continew-plugin-review-project/src/main/java/top/continew/admin/review/project/model/resp/ProjectDetailResp.java:128-138`

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
    @Schema(description = "通过人数（已完成节点才有）")
    private Integer passCount;
    @Schema(description = "总人数（已完成节点才有）")
    private Integer totalCount;
    @Schema(description = "平均分（SCORE_PASS 模式才有）")
    private java.math.BigDecimal averageScore;
    @Schema(description = "节点结果（PASS/REJECT，已完成节点才有）")
    private String nodeResult;
    // ↓ 新增
    @Schema(description = "已完成人数（进行中节点用于展示进度）")
    private Integer completedCount;
    @Schema(description = "需要人数（从审批规则读取）")
    private Integer requiredCount;
}
```

---

## Task 3：后端 - buildNodeResultMap 同时统计进行中节点完成人数

**Files:**
- Modify: `continew-admin/continew-plugin-review/continew-plugin-review-project/src/main/java/top/continew/admin/review/project/service/impl/ProjectServiceImpl.java`

**Step 1: NodeResult 新增 completedCount 字段**

```java
private static class NodeResult {
    int passCount;
    int totalCount;
    BigDecimal averageScore;
    String result; // PASS / REJECT（节点已汇总时才有）
    int completedCount; // 已完成任务数（含进行中节点）
    boolean isFinished; // 节点是否已汇总完成
}
```

**Step 2: buildNodeResultMap 改为查所有非 CANCELLED 任务**

```java
private Map<String, NodeResult> buildNodeResultMap(Long projectId) {
    // 查所有非 CANCELLED 任务（含 PENDING/SAVED/COMPLETED/TRANSFERRED）
    List<ReviewTaskDO> allTasks = taskMapper.selectList(
            new LambdaQueryWrapper<ReviewTaskDO>()
                    .eq(ReviewTaskDO::getProjectId, projectId)
                    .in(ReviewTaskDO::getTaskType, List.of(TaskType.AUDIT, TaskType.REVIEW, TaskType.DECISION))
                    .ne(ReviewTaskDO::getStatus, TaskStatusEnum.CANCELLED)
                    .eq(ReviewTaskDO::getDeleted, 0));

    Map<String, List<ReviewTaskDO>> grouped = allTasks.stream()
            .collect(Collectors.groupingBy(t -> t.getTaskType().getValue() + "_" + t.getNodeSequence()));

    Map<String, NodeResult> result = new HashMap<>();
    for (Map.Entry<String, List<ReviewTaskDO>> entry : grouped.entrySet()) {
        List<ReviewTaskDO> tasks = entry.getValue();

        // 已完成任务（COMPLETED 或 TRANSFERRED）
        List<ReviewTaskDO> completedTasks = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatusEnum.COMPLETED
                        || t.getStatus() == TaskStatusEnum.TRANSFERRED)
                .toList();

        // 节点是否已全部完成（可以汇总）
        boolean isFinished = tasks.stream()
                .allMatch(t -> t.getStatus() == TaskStatusEnum.COMPLETED
                        || t.getStatus() == TaskStatusEnum.TRANSFERRED);

        long passCount = completedTasks.stream()
                .filter(t -> t.getDecision() == TaskDecisionEnum.PASS).count();
        OptionalDouble avg = completedTasks.stream()
                .filter(t -> t.getScore() != null)
                .mapToDouble(t -> t.getScore().doubleValue())
                .average();

        NodeResult nr = new NodeResult();
        nr.completedCount = completedTasks.size();
        nr.totalCount = tasks.size();
        nr.isFinished = isFinished;
        if (isFinished && !completedTasks.isEmpty()) {
            nr.passCount = (int) passCount;
            nr.averageScore = avg.isPresent()
                    ? BigDecimal.valueOf(avg.getAsDouble()).setScale(2, RoundingMode.HALF_UP) : null;
            nr.result = passCount == completedTasks.size() ? "PASS" : "REJECT";
        }
        result.put(entry.getKey(), nr);
    }
    return result;
}
```

**Step 3: buildReviewProgress 填充 completedCount/requiredCount**

在 `.map(round -> { ... })` 中，填充完 nodeStatus 和结果后，补充：

```java
String key = round.getRoundType() + "_" + round.getRoundSequence();
NodeResult nr = nodeResultMap.get(key);
if (nr != null) {
    item.setCompletedCount(nr.completedCount);
    item.setTotalCount(nr.totalCount); // 总任务数（已分配人数）
    if (nr.isFinished) {
        item.setPassCount(nr.passCount);
        item.setAverageScore(nr.averageScore);
        item.setNodeResult(nr.result);
        if ("COMPLETED".equals(item.getNodeStatus())) {
            item.setNodeStatus("PASS".equals(nr.result) ? "COMPLETED" : "REJECTED");
        }
    }
    // 从快照读取 requiredCount
    String nodeScope = round.getRoundType() + "_" + round.getRoundSequence();
    if (snapshot.getApprovalRules() != null) {
        ProjectTypeSnapshot.ApprovalRuleInfo rule = snapshot.getApprovalRules().get(nodeScope);
        if (rule != null && rule.getRequiredReviewerCount() != null) {
            item.setRequiredCount(rule.getRequiredReviewerCount());
        }
    }
    // requiredCount 兜底用 totalCount
    if (item.getRequiredCount() == null) {
        item.setRequiredCount(nr.totalCount);
    }
}
```

---

## Task 4：前端 - 进度条展示"已完成N/M人"

**Files:**
- Modify: `continew-admin-ui/src/apis/review/type.ts`（NodeProgressItem 新增字段）
- Modify: `continew-admin-ui/src/views/review/project/detail/[id].vue`

**Step 1: type.ts NodeProgressItem 新增字段**

```typescript
export interface NodeProgressItem {
  nodeType: string
  nodeSequence: number
  nodeName: string
  nodeStatus: string
  passCount?: number
  totalCount?: number
  averageScore?: number
  nodeResult?: string
  // 新增
  completedCount?: number
  requiredCount?: number
}
```

**Step 2: 进度条节点 description 展示逻辑**

```vue
<a-step
  v-for="(node, idx) in detail.reviewProgress"
  :key="idx"
  :title="node.nodeName || `${TASK_TYPE_LABEL[node.nodeType] ?? node.nodeType} 第${node.nodeSequence}轮`"
  :status="nodeStatusToStep(node.nodeStatus)"
>
  <template #description>
    <!-- 已完成节点：显示通过/驳回结果，hover 弹详情 -->
    <template v-if="node.nodeResult">
      <a-popover position="bottom" trigger="hover">
        <span class="node-result-link">
          <a-tag :color="node.nodeResult === 'PASS' ? 'green' : 'red'" size="small">
            {{ node.nodeResult === 'PASS' ? '通过' : '驳回' }}
          </a-tag>
        </span>
        <template #content>
          <div class="node-result-popover">
            <div>通过：{{ node.passCount }} / {{ node.totalCount }} 人</div>
            <div v-if="node.averageScore != null">平均分：{{ node.averageScore }}</div>
          </div>
        </template>
      </a-popover>
    </template>
    <!-- 进行中节点：显示已完成人数进度 -->
    <template v-else-if="node.nodeStatus === 'ACTIVE' && node.requiredCount">
      <span class="node-progress-text">
        已完成 {{ node.completedCount ?? 0 }}/{{ node.requiredCount }} 人
      </span>
    </template>
  </template>
</a-step>
```

**Step 3: 加样式**

```css
.node-progress-text {
  font-size: 12px;
  color: var(--color-text-3);
}
```

---

## 执行顺序

1. Task 1（前端状态值修复，最高优先级，影响所有状态展示）
2. Task 2（后端 DTO 新增字段）
3. Task 3（后端 buildNodeResultMap 重构）
4. Task 4（前端进度条展示）
