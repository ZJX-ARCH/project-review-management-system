# 项目评审管理系统修复与优化实现计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 修复项目状态与节点不匹配、流程未自动推进、前端UI权限控制、详情页表单展示、任务详情页进度条等问题。

**Architecture:** 后端修复状态机逻辑和管理阶段任务分配；前端重构项目详情页（进度条+历史节点抽屉）、修复权限控制（PROJECT_ADMIN 才能强制终止）、修复任务详情页转办（用户选择器替代手输ID）。

**Tech Stack:** Spring Boot 3.x + MyBatis Plus / Vue 3.5 + Arco Design 2.57

---

## 问题清单（已分析根因）

### Bug 1：项目列表"当前节点"与"项目状态"不匹配
- **现象**：显示"决策 第1轮"但状态是"评审中"
- **根因**：`formatCurrentNode()` 用 `currentNodeType/currentNodeSequence` 显示节点，但 `PROJECT_STATUS_MAP` 的 status=30 写的是"评审中"，而 DECISION 节点时 status=40（决策中）。实际是**列表列宽不够导致截断**，或者后端 `currentNodeType` 与 `status` 确实不同步。
- **真正根因**：`WorkflowEngine.statusForReviewNode()` 已正确映射，但 `formatCurrentNode()` 显示的是 `currentNodeType`（DECISION），而 status 标签显示的是 status=30（REVIEWING）。说明后端 `project.status` 没有随节点推进更新，或者前端列宽截断了"决策"只显示了"评审"。
- **修复**：前端 `formatCurrentNode` 改为同时展示节点类型中文名，不依赖 status 字段；后端确认 `statusForReviewNode` 在每次节点推进时都被调用。

### Bug 2：决策结束后未自动进入管理阶段
- **根因**：`WorkflowEngine.startExecutionPhase()` 在决策通过后调用，但 `MANAGEMENT` 任务分配后，申请人需要提交阶段成果才能触发 `StageFormSubmittedEvent`，进而分配 `MANAGEMENT` 审核任务给管理人员。**当前设计是激活首阶段后立即分配 MANAGEMENT 任务**（WorkflowEngine:379），这是正确的。
- **实际问题**：管理人员收到 MANAGEMENT 任务后，在"我的任务"里看到的是 MANAGEMENT 类型任务，但**申请人在"项目管理"里看不到"提交阶段成果"按钮**，因为 `submitStageForm` 要求 status 是 EXECUTING/OVERTIME/ACCEPTING，而 `getProjectAndCheckOwner` 只允许申请人操作。
- **修复**：确认流程正确性，重点修复前端展示。

### Bug 3：强制终止按钮权限控制
- **现象**：任何人都能看到强制终止按钮
- **根因**：`canTerminate` 只判断状态，没有判断角色。应只有 `PROJECT_ADMIN` 角色才能终止。
- **修复**：前端加 `v-permission="['review:project:terminate']"` 已有，但需要确保只有 PROJECT_ADMIN 角色拥有该权限。后端 `terminate()` 方法去掉 `getProjectAndCheckOwner`（只检查申请人），改为只检查项目存在。

### Bug 4：项目详情页申请表单没有内容
- **根因**：`ProjectDetailResp` 中 `applicationFormTemplate` 字段后端没有填充（`getDetail()` 方法只填充了 `applicationFormData`，没有查表单模板）。
- **修复**：后端 `ProjectServiceImpl.getDetail()` 补充查询 `applicationFormTemplate`。

### Bug 5：任务详情页进度条体验差
- **需求**：用进度条展示所有轮次/阶段，当前轮高亮，点击历史轮次可查看该轮详情（抽屉或弹窗）。
- **修复**：前端任务详情页左栏顶部加 Steps 进度条，点击已完成节点弹出历史详情抽屉。

### Bug 6：转办弹窗用手输用户ID体验差
- **修复**：改为用户搜索选择器（调用系统用户搜索接口）。

---

## Task 1：后端 - 修复 ProjectServiceImpl.getDetail() 补充表单模板

**Files:**
- Modify: `continew-admin/continew-plugin-review/continew-plugin-review-project/src/main/java/top/continew/admin/review/project/service/impl/ProjectServiceImpl.java`

**Step 1: 在 getDetail() 中补充查询 applicationFormTemplate**

在 `getDetail()` 方法的"填充 applicationFormData"之后，添加：

```java
// 填充申请表单模板（供前端只读渲染）
if (project.getSnapshotConfig() != null) {
    try {
        ProjectTypeSnapshot snapshot = objectMapper.convertValue(
            project.getSnapshotConfig(), ProjectTypeSnapshot.class);
        if (snapshot != null && snapshot.getFormMappings() != null) {
            Long formTemplateId = snapshot.getFormMappings().get("APPLICATION");
            if (formTemplateId != null) {
                resp.setApplicationFormTemplate(formTemplateService.getDetail(formTemplateId));
            }
        }
    } catch (Exception e) {
        log.warn("[Project] 解析申请表单模板失败：{}", e.getMessage());
    }
}
```

需要注入 `ObjectMapper`（已有）。

**Step 2: 确认 ProjectDetailResp 有 applicationFormTemplate 字段**

检查 `model/resp/ProjectDetailResp.java`，确认有该字段，类型为 `FormTemplateResp`。

**Step 3: 修复 terminate() 方法权限检查**

当前 `terminate()` 已经不调用 `getProjectAndCheckOwner`（只检查申请人），是正确的。确认后端 `@SaCheckPermission("review:project:terminate")` 注解存在于 Controller。

---

## Task 2：后端 - 确认管理阶段任务分配逻辑

**Files:**
- Read: `WorkflowEngine.java`（已读，逻辑正确）

**分析结论：**
- 决策通过 → `startExecutionPhase()` → 创建阶段实例 → 激活首阶段 → `assignTasks(MANAGEMENT, stageOrder=1)`
- 管理人员收到 MANAGEMENT 任务，在"我的任务"处理（PASS/REJECT/UNQUALIFIED/WITHDRAW）
- 管理人员 PASS → `handleManagementNodeResult` → 激活下一阶段 → 分配下一阶段 MANAGEMENT 任务
- **申请人提交阶段成果**：`submitStageForm()` → `StageFormSubmittedEvent` → 分配 MANAGEMENT 任务

**问题发现**：`startExecutionPhase()` 激活首阶段后立即分配 MANAGEMENT 任务，但此时申请人还没提交阶段成果。这意味着管理人员会收到一个任务，但阶段成果还是空的。

**设计澄清**：根据业务流程文档，管理阶段的流程是：
1. 申请人填写阶段成果（在"项目管理"详情页提交）
2. 提交后触发 `StageFormSubmittedEvent` → 分配 MANAGEMENT 任务给管理人员
3. 管理人员审核

**当前代码问题**：`startExecutionPhase()` 在激活首阶段后就立即分配了 MANAGEMENT 任务（第379行），这是**提前分配**，应该等申请人提交成果后再分配。

**修复方案**：`startExecutionPhase()` 中不立即分配任务，只激活阶段。等申请人提交 `submitStageForm()` 后再分配。

**Files:**
- Modify: `WorkflowEngine.java:379` 删除提前分配的 `assignmentEngine.assignTasks()` 调用

---

## Task 3：后端 - 修复 WorkflowEngine 管理阶段任务分配时机

**Files:**
- Modify: `continew-admin/continew-plugin-review/continew-plugin-review-project/src/main/java/top/continew/admin/review/project/engine/WorkflowEngine.java`

**Step 1: 修改 startExecutionPhase() 删除提前分配**

将第 377-380 行：
```java
// 激活首阶段后立即分配任务（设计文档：激活 KICKOFF → 分配 MANAGEMENT 任务）
// 管理人员需知晓项目已进入执行阶段；申请人提交成果后，dedup 保护避免重复分配
assignmentEngine.assignTasks(projectId, firstTaskType, firstStage.getStageOrder());
log.info("[Workflow] 项目{} 进入执行阶段，首阶段{}（{}）已激活，任务已分配", ...);
```

改为：
```java
log.info("[Workflow] 项目{} 进入执行阶段，首阶段{}（{}）已激活，等待申请人提交成果",
        projectId, firstStage.getStageOrder(), firstStage.getStageType());
```

**Step 2: 同样修改 handleManagementNodeResult() 中 PASS 分支**

当前 PASS 分支激活下一阶段后没有立即分配任务（正确），只是更新项目状态。确认无需修改。

---

## Task 4：前端 - 修复项目列表"当前节点"显示

**Files:**
- Modify: `continew-admin-ui/src/views/review/project/index.vue`

**Step 1: 修改 formatCurrentNode 函数**

当前：
```typescript
function formatCurrentNode(record: ProjectListResp): string {
  if (!record.currentNodeType) return '—'
  const label = TASK_TYPE_LABEL[record.currentNodeType] ?? record.currentNodeType
  return record.currentNodeSequence ? `${label} 第${record.currentNodeSequence}轮` : label
}
```

问题：MANAGEMENT/ACCEPTANCE 类型不应显示"第N轮"，应显示"第N阶段"。

修改为：
```typescript
function formatCurrentNode(record: ProjectListResp): string {
  if (!record.currentNodeType) return '—'
  const label = TASK_TYPE_LABEL[record.currentNodeType] ?? record.currentNodeType
  if (!record.currentNodeSequence) return label
  const suffix = ['MANAGEMENT', 'ACCEPTANCE'].includes(record.currentNodeType)
    ? `第${record.currentNodeSequence}阶段`
    : `第${record.currentNodeSequence}轮`
  return `${label} ${suffix}`
}
```

---

## Task 5：前端 - 项目详情页重构（进度条 + 历史节点抽屉）

**Files:**
- Modify: `continew-admin-ui/src/views/review/project/detail/[id].vue`

**Step 1: 重构评审流程进度条**

当前进度条只显示"当前节点"一个步骤，没有意义。

新设计：
- 从 `detail.snapshotConfig.rounds` 读取所有轮次（需后端在 `ProjectDetailResp` 中暴露 snapshot 的 rounds/stages）
- 或者：后端新增 `reviewProgress` 字段，返回所有节点列表 + 当前节点索引

**后端方案（最小改动）**：在 `ProjectDetailResp` 中新增：
```java
private List<NodeProgressItem> reviewProgress; // 评审阶段进度
private List<StageProgressItem> stageProgress; // 管理阶段进度
```

在 `getDetail()` 中从 snapshot 构建这两个列表。

**前端方案**：
- 评审阶段：用 `a-steps` 展示所有轮次，当前轮高亮，已完成轮可点击查看历史
- 点击历史轮次：弹出抽屉，展示该轮所有任务的决策结果（需后端接口）
- 管理阶段：用 `a-steps` 展示所有阶段，当前阶段高亮

**Step 2: 修复强制终止按钮权限**

当前 `canTerminate` 计算属性只判断状态，没有判断角色。

修改：
```typescript
import { useUserStore } from '@/stores'
const userStore = useUserStore()

const canTerminate = computed(() => {
  const s = detail.value?.status ?? 0
  const notArchived = s !== 0 && ![49, 90, 91, 92, 99].includes(s)
  // 只有 PROJECT_ADMIN 或 super_admin 才能强制终止
  const hasAdminRole = userStore.roleList?.some(r =>
    ['PROJECT_ADMIN', 'super_admin'].includes(r.code)
  )
  return notArchived && hasAdminRole
})
```

**Step 3: 修复有痕修改弹窗 - 预填当前表单数据**

当前打开弹窗时 `updateFormData.formData` 是空对象，应预填当前 `detail.applicationFormData`：

```typescript
const openUpdateForm = () => {
  updateFormData.modifyReason = ''
  updateFormData.formData = { ...(detail.value?.applicationFormData ?? {}) }
  updateFormVisible.value = true
}
```

---

## Task 6：前端 - 后端新增 ProjectDetailResp 进度字段

**Files:**
- Modify: `continew-admin/continew-plugin-review/continew-plugin-review-project/src/main/java/top/continew/admin/review/project/model/resp/ProjectDetailResp.java`
- Modify: `ProjectServiceImpl.java` - getDetail() 方法

**Step 1: 在 ProjectDetailResp 中新增进度字段**

```java
/** 评审阶段进度（从快照构建，含所有轮次及当前状态） */
private List<NodeProgressItem> reviewProgress;

/** 管理阶段进度（从快照构建，含所有阶段及当前状态） */
private List<StageProgressItem> stageProgress;

@Data
public static class NodeProgressItem {
    private String nodeType;      // AUDIT/REVIEW/DECISION
    private Integer nodeSequence;
    private String nodeName;
    private String nodeStatus;    // PENDING/ACTIVE/COMPLETED/REJECTED
}

@Data
public static class StageProgressItem {
    private Integer stageOrder;
    private String stageName;
    private String stageType;     // KICKOFF/EXECUTION/ACCEPTANCE
    private String stageStatus;   // PENDING/IN_PROGRESS/SUBMITTED/COMPLETED/REJECTED
    private Boolean isOverdue;
}
```

**Step 2: 在 getDetail() 中构建进度列表**

从 snapshot.rounds 构建 reviewProgress，结合 project.currentNodeType/currentNodeSequence 判断每个节点状态：
- 序号 < 当前节点 → COMPLETED（已通过）
- 序号 == 当前节点 → ACTIVE
- 序号 > 当前节点 → PENDING

从 stages 表构建 stageProgress（直接用 DB 中的 stage 状态）。

---

## Task 7：前端 - 任务详情页进度条

**Files:**
- Modify: `continew-admin-ui/src/views/review/task/detail/[id].vue`

**Step 1: 在左栏顶部加进度条卡片**

利用 `detail.previousNodes` + 当前节点构建进度步骤：

```vue
<a-card title="流程进度" style="margin-bottom: 16px;">
  <a-steps :current="currentStepIndex" size="small" direction="horizontal">
    <a-step
      v-for="(step, idx) in allSteps"
      :key="idx"
      :title="step.nodeName"
      :status="step.stepStatus"
      @click="step.clickable && openHistoryDrawer(step)"
      :style="step.clickable ? 'cursor:pointer' : ''"
    />
  </a-steps>
</a-card>
```

`allSteps` 由 `previousNodes`（已完成）+ 当前节点 构建。

**Step 2: 历史节点抽屉**

点击已完成节点，弹出抽屉展示该节点的汇总信息（passCount/totalCount/averageScore）。

---

## Task 8：前端 - 转办弹窗改为用户搜索选择器

**Files:**
- Modify: `continew-admin-ui/src/views/review/task/detail/[id].vue`

**Step 1: 调用系统用户搜索接口**

使用 `getUserPage` 或 `getUserList` 接口，加 `a-select` 远程搜索：

```vue
<a-select
  v-model="transferForm.targetUserId"
  placeholder="搜索用户姓名"
  allow-search
  :filter-option="false"
  @search="onSearchUser"
>
  <a-option v-for="u in userOptions" :key="u.id" :value="u.id">
    {{ u.nickname }}（{{ u.username }}）
  </a-option>
</a-select>
```

---

## Task 9：前端 - 项目管理列表增加 PROJECT_ADMIN 视角

**Files:**
- Modify: `continew-admin-ui/src/views/review/project/index.vue`

**需求**：PROJECT_ADMIN 应能看到**所有项目**（不只是自己申请的），并有强制终止操作。

**后端问题**：`ProjectServiceImpl.page()` 强制过滤 `applicantId = currentUserId`，PROJECT_ADMIN 看不到别人的项目。

**修复方案**：
- 后端：判断当前用户是否有 `review:project:terminate` 权限，有则不加 applicantId 过滤
- 前端：PROJECT_ADMIN 在列表中显示"终止"按钮

**后端修改**：
```java
// 非管理员只查自己的项目
if (!StpUtil.hasPermission("review:project:terminate")) {
    wrapper.eq(ReviewProjectDO::getApplicantId, currentUserId);
}
```

---

## Task 10：数据库 - 无需新增表，只需确认菜单权限分配

**确认事项**：
- `review:project:terminate` 权限（ID=1737201293016）需分配给 PROJECT_ADMIN 角色
- 当前 `review_project_data.sql` 已创建该权限菜单，但未分配给 PROJECT_ADMIN 角色
- 需要在 Liquibase 中新增 changeset，将该权限分配给 PROJECT_ADMIN

**Files:**
- Modify: `continew-admin/continew-server/src/main/resources/db/changelog/mysql/plugin/project/review_project_data.sql`

新增 changeset：
```sql
-- changeset zjx:review-project-data-4
-- comment 将终止项目权限分配给项目管理员角色

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 806225197471424650, 1737201293016
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_role_menu`
    WHERE `role_id` = 806225197471424650 AND `menu_id` = 1737201293016
);

-- rollback DELETE FROM `sys_role_menu` WHERE `role_id` = 806225197471424650 AND `menu_id` = 1737201293016;
```

---

## 执行顺序

1. Task 1（后端 getDetail 补充表单模板）
2. Task 2+3（后端 WorkflowEngine 修复任务分配时机）
3. Task 6（后端 ProjectDetailResp 新增进度字段）
4. Task 9 后端部分（page() 权限判断）
5. Task 10（DB 权限分配）
6. Task 4（前端列表节点显示修复）
7. Task 5（前端项目详情页重构）
8. Task 7（前端任务详情页进度条）
9. Task 8（前端转办用户选择器）

---

**Plan complete and saved to `docs/plans/2026-03-25-project-review-fixes.md`.**

**Two execution options:**

**1. Subagent-Driven (this session)** - 逐 Task 派发子 Agent，每个 Task 完成后 review，快速迭代

**2. Parallel Session (separate)** - 新开 session 用 executing-plans skill 批量执行

**Which approach?**
