# 代码评审问题清单

> 评审时间：2026-03-25
> 评审范围：`docs/plans/2026-03-25-project-review-fixes.md` 全部 Task 的实现
> 涉及文件：10 个文件，571 行新增，198 行删除
>
> **修复进度：C1 ✅ C2 ✅ C3 ✅ I2 ✅ I3 ✅ M1 ✅ | I1 ❌ 待修复**

---

## Critical（必须修复，否则不可上线）

### C1：`applyNodeRoleFilter` 与 DEPT/USER 范围配置语义冲突 ✅ 已修复

**文件：** `continew-plugin-review-project/.../engine/TaskAssignmentEngine.java:328-352`

**问题：**
当人员范围配置为 `DEPT` 或 `USER` 类型时，`applyNodeRoleFilter` 会强制要求这些人还必须持有对应角色（如 AUDITOR）。管理员明确配置"某部门所有人"，但部门内没有 AUDITOR 角色的人会被过滤掉，导致候选人池为空，任务分配抛出异常。

**修复方案：**
移除 `applyNodeRoleFilter` 的强制叠加逻辑，让角色约束只通过 `COMBINED` 类型的人员范围配置来表达。或者仅在 `personnelConfigs` 为空时作为兜底回退，不在已有明确范围配置时叠加过滤。

**实际修复：**
新增三处兜底逻辑：角色不存在、角色无成员、交集为空时均回退到原始候选人池，不再强制过滤。

---

### C2：`userStore.roleList` 不存在，`canTerminate` 永远为 false ✅ 已修复

**文件：** `continew-admin-ui/src/views/review/project/detail/[id].vue:210`

**问题：**
`useUserStore` 暴露的是 `roles`（`ref<string[]>`，存角色 code 字符串），没有 `roleList` 属性。`userStore.roleList?.some(...)` 永远返回 `undefined`，导致所有用户（包括 PROJECT_ADMIN）都看不到强制终止按钮。

**修复方案（推荐）：**
```typescript
import has from '@/utils/has'

const canTerminate = computed(() => {
  const s = detail.value?.status ?? 0
  const notArchived = s !== 0 && ![49, 90, 91, 92, 99].includes(s)
  return notArchived && has.hasPerm('review:project:terminate')
})
```

**实际修复：**
采用推荐方案，改用 `has.hasPerm('review:project:terminate')`，与后端权限对齐。

---

### C3：`TaskType` 枚举缺少 `KICKOFF`，执行阶段初始化会抛异常 ✅ 已修复

**文件：** `continew-plugin-review-project/.../engine/WorkflowEngine.java:348`

**问题：**
`TaskType` 枚举只有 `AUDIT/REVIEW/DECISION/MANAGEMENT/ACCEPTANCE`，没有 `KICKOFF`。`startExecutionPhase()` 中执行 `TaskType.valueOf(info.getStageType())` 时，若阶段类型为 `KICKOFF` 会抛 `IllegalArgumentException`，导致决策通过后进入执行阶段时整个流程崩溃。

**修复方案：**
```java
// 原来（有问题）
s.setStageType(TaskType.valueOf(info.getStageType()));

// 修复后
TaskType stageTaskType = "ACCEPTANCE".equals(info.getStageType())
    ? TaskType.ACCEPTANCE : TaskType.MANAGEMENT;
s.setStageType(stageTaskType);
```

**实际修复：**
采用三元表达式，`ACCEPTANCE` 映射到 `TaskType.ACCEPTANCE`，其余（含 `KICKOFF`/`EXECUTION`）全部映射到 `TaskType.MANAGEMENT`，不再调用 `valueOf`。

---

## Important（上线前修复）

### I1：转办搜索使用 `description` 字段，搜索无结果 ❌ 待修复

**文件：** `continew-admin-ui/src/views/review/task/detail/[id].vue:403`

**问题：**
`UserQuery.description` 是用户备注字段，不是姓名或账号。用户在转办弹窗输入姓名关键词时，搜索结果为空。

**待定修复方案（二选一）：**
- 方案 A：后端 `UserQuery` 新增 `keyword` 字段，支持模糊匹配 `nickname`/`username`，前端改用 `keyword` 参数
- 方案 B：前端改为加载当前节点候选人列表（调用后端 `findCandidates` 对应接口），用户从候选人中选择，同时保证转办目标合法

---

### I2：`page()` 权限判断语义耦合 ✅ 已修复

**文件：** `continew-plugin-review-project/.../service/impl/ProjectServiceImpl.java:172`

**问题：**
用 `review:project:terminate` 权限来判断"是否可查看所有项目"，语义不准确。未来若有人只有 terminate 权限但不应看到所有项目（或反之），逻辑会出错。

**修复方案：**
在 DB changeset 中给 PROJECT_ADMIN 额外分配一个 `review:project:list:all` 权限，代码中改用该权限判断列表可见范围，与终止操作权限解耦。

**实际修复：**
新增 changeset-5，创建 `review:project:list:all` 权限菜单并分配给 PROJECT_ADMIN，`page()` 改用该权限判断。

---

### I3：`buildReviewProgress` 依赖 `indexOf` 返回 -1 的副作用 ✅ 已修复

**文件：** `continew-plugin-review-project/.../service/impl/ProjectServiceImpl.java:561`

**问题：**
当 `currentNodeType` 为 null 时，`typeOrder.indexOf("")` 返回 -1，所有节点都被标记为 PENDING，行为偶然正确但意图不清晰，后续维护容易引入 bug。

**实际修复：**
新增 `if (!allCompleted && project.getCurrentNodeType() == null)` 早返回分支，明确将所有节点标记为 PENDING，不再依赖 `indexOf` 返回 -1 的副作用。

---

## Minor（可后续处理）

### M1：DB changeset `role_id` 硬编码，跨环境可能静默失败 ✅ 已修复

**文件：** `continew-server/.../plugin/project/review_project_data.sql:64`

**问题：**
`role_id = 806225197471424650` 硬编码，若不同环境 PROJECT_ADMIN 角色 ID 不同，INSERT 会静默执行 0 行而不报错。

**修复方案：**
改为子查询，按角色 code 查找：
```sql
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.id, 1737201293016
FROM sys_role r
WHERE r.code = 'PROJECT_ADMIN' AND r.deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm
    WHERE rm.role_id = r.id AND rm.menu_id = 1737201293016
  );
```

**实际修复：**
changeset-4 和 changeset-5 均已改为按 `code='PROJECT_ADMIN'` 子查询。

---

## 汇总

| ID | 优先级 | 问题 | 影响 | 状态 |
|----|--------|------|------|------|
| C1 | Critical | `applyNodeRoleFilter` 过度过滤候选人 | 任务分配失败，流程卡死 | ✅ 已修复 |
| C2 | Critical | `userStore.roleList` 不存在 | 终止按钮对所有人不可见 | ✅ 已修复 |
| C3 | Critical | `TaskType.valueOf("KICKOFF")` 抛异常 | 决策通过后执行阶段无法启动 | ✅ 已修复 |
| I1 | Important | 转办搜索字段错误（`description`） | 搜索无结果，无法转办 | ❌ 待修复 |
| I2 | Important | 权限语义耦合 | 维护风险 | ✅ 已修复 |
| I3 | Important | `buildReviewProgress` 意图不清晰 | 代码可读性，潜在维护 bug | ✅ 已修复 |
| M1 | Minor | DB role_id 硬编码 | 跨环境静默失败 | ✅ 已修复 |
