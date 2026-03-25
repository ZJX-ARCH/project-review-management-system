# 转办候选人接口实现计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 修复转办弹窗搜索无结果的问题（I1），将手动输入用户 ID / 按 description 搜索改为直接展示当前节点候选人列表。

**Architecture:**
后端新增 `GET /review/task/{taskId}/candidates` 接口，复用 `TaskAssignmentEngine.findCandidates()` 查询当前节点候选人，排除自己和已有任务的人，返回用户姓名+账号列表。前端转办弹窗改为下拉选择，打开时加载候选人列表。

**Tech Stack:** Spring Boot 3.x + MyBatis Plus / Vue 3.5 + Arco Design 2.57

---

## 选择方案 B 的理由

| 对比项 | 方案 A（后端加 keyword 搜索） | 方案 B（展示候选人列表）✅ |
|--------|-------------------------------|---------------------------|
| 后端改动 | 修改 `UserQuery` + Service + Controller | 新增一个接口，复用已有逻辑 |
| 合法性保证 | 仍需后端二次校验 | 天然合法，前端只展示可选的人 |
| 用户体验 | 需要输入关键词才能看到结果 | 打开即可看到所有可选人员 |
| 候选人数量 | 通常较少（节点配置的人员范围） | 同上，适合直接展示 |

---

## Task 1：后端 - 新增候选人响应 DTO

**Files:**
- Create: `continew-plugin-review-project/src/main/java/top/continew/admin/review/project/model/resp/TaskCandidateResp.java`

```java
package top.continew.admin.review.project.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "转办候选人")
public class TaskCandidateResp {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户账号")
    private String username;
}
```

---

## Task 2：后端 - TaskService 新增 listCandidates 方法

**Files:**
- Modify: `continew-plugin-review-project/src/main/java/top/continew/admin/review/project/service/TaskService.java`
- Modify: `continew-plugin-review-project/src/main/java/top/continew/admin/review/project/service/impl/TaskServiceImpl.java`

**Step 1: 接口声明**

在 `TaskService.java` 新增：
```java
/**
 * 查询当前任务的转办候选人列表（排除自己和已有任务的人）
 */
List<TaskCandidateResp> listCandidates(Long taskId);
```

**Step 2: 实现**

在 `TaskServiceImpl.java` 新增：
```java
@Override
public List<TaskCandidateResp> listCandidates(Long taskId) {
    ReviewTaskDO task = getTaskAndCheckAssignee(taskId);
    ReviewProjectDO project = projectMapper.selectById(task.getProjectId());
    if (project == null) {
        throw new BusinessException("关联项目不存在");
    }

    // 查当前节点候选人池
    Set<Long> candidatePool = assignmentEngine.findCandidates(
            project.getTypeId(), task.getTaskType(), task.getNodeSequence());

    // 排除自己
    candidatePool.remove(task.getAssigneeId());

    if (candidatePool.isEmpty()) {
        return Collections.emptyList();
    }

    // 查用户信息（selectBatchIds 绕过 @DataPermission）
    return userMapper.selectBatchIds(candidatePool).stream()
            .map(u -> {
                TaskCandidateResp resp = new TaskCandidateResp();
                resp.setUserId(u.getId());
                resp.setNickname(u.getNickname());
                resp.setUsername(u.getUsername());
                return resp;
            })
            .sorted(Comparator.comparing(TaskCandidateResp::getNickname,
                    Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList());
}
```

---

## Task 3：后端 - TaskController 新增接口

**Files:**
- Modify: `continew-plugin-review-project/src/main/java/top/continew/admin/review/project/controller/TaskController.java`

新增：
```java
/**
 * 查询转办候选人列表
 */
@GetMapping("/{taskId}/candidates")
@Operation(summary = "查询转办候选人列表")
@SaCheckPermission("review:task:transfer")
public R<List<TaskCandidateResp>> listCandidates(@PathVariable Long taskId) {
    return R.ok(taskService.listCandidates(taskId));
}
```

---

## Task 4：前端 - 新增 API 函数和类型

**Files:**
- Modify: `continew-admin-ui/src/apis/review/index.ts`（新增 `getTaskCandidates`）
- Modify: `continew-admin-ui/src/apis/review/type.ts`（新增 `TaskCandidateResp`）

**type.ts 新增：**
```typescript
/** 转办候选人 */
export interface TaskCandidateResp {
  userId: number
  nickname: string
  username: string
}
```

**index.ts 新增：**
```typescript
/** 查询转办候选人列表 */
export function getTaskCandidates(taskId: string | number) {
  return request.get<TaskCandidateResp[]>(`/review/task/${taskId}/candidates`)
}
```

---

## Task 5：前端 - 重构转办弹窗

**Files:**
- Modify: `continew-admin-ui/src/views/review/task/detail/[id].vue`

**Step 1: 模板改为 a-select 展示候选人**

```vue
<a-form-item label="转办给" required>
  <a-select
    v-model="transferForm.targetUserId"
    placeholder="请选择转办对象"
    :loading="candidatesLoading"
    style="width: 100%;"
  >
    <a-option
      v-for="u in candidates"
      :key="u.userId"
      :value="u.userId"
    >
      {{ u.nickname }}（{{ u.username }}）
    </a-option>
  </a-select>
</a-form-item>
```

**Step 2: 打开弹窗时加载候选人**

```typescript
import { getTaskCandidates } from '@/apis/review'
import type { TaskCandidateResp } from '@/apis/review/type'

const candidates = ref<TaskCandidateResp[]>([])
const candidatesLoading = ref(false)

// 打开转办弹窗时加载候选人
const openTransferModal = async () => {
  transferForm.targetUserId = undefined
  transferForm.transferRemark = ''
  transferVisible.value = true
  candidatesLoading.value = true
  try {
    const res = await getTaskCandidates(taskId.value)
    candidates.value = res.data ?? []
  } finally {
    candidatesLoading.value = false
  }
}
```

**Step 3: 按钮改为调用 openTransferModal**

```vue
<!-- 原来 -->
<a-button @click="transferVisible = true">转办</a-button>

<!-- 改为 -->
<a-button @click="openTransferModal">转办</a-button>
```

**Step 4: 删除不再需要的 onSearchUser、userOptions、listAllUser 引用**

---

## 执行顺序

1. Task 1（创建 DTO）
2. Task 2（Service 实现）
3. Task 3（Controller 接口）
4. Task 4（前端 API）
5. Task 5（前端弹窗重构）
