<!-- OPENSPEC:START -->
# OpenSpec Instructions

These instructions are for AI assistants working in this project.

Always open `@/openspec/AGENTS.md` when the request:
- Mentions planning or proposals (words like proposal, spec, change, plan)
- Introduces new capabilities, breaking changes, architecture shifts, or big performance/security work
- Sounds ambiguous and you need the authoritative spec before coding

Use `@/openspec/AGENTS.md` to learn:
- How to create and apply change proposals
- Spec format and conventions
- Project structure and guidelines

Keep this managed block so 'openspec update' can refresh the instructions.

<!-- OPENSPEC:END -->

---

## 前端编码规范 (Frontend Coding Standards)

### 1. 导入顺序 (Import Order)
- **Vue API 导入必须在组件导入之前**
- **第三方库导入在最前面**
- **本地工具/类型导入在最后**

```typescript
// ✅ 正确示例
import { Message } from '@arco-design/web-vue'
import { computed, defineOptions, ref } from 'vue'
import BasicInfoStep from '../components/BasicInfoStep.vue'
import { useTemplateForm } from '../hooks/useTemplateForm'

// ❌ 错误示例
import BasicInfoStep from '../components/BasicInfoStep.vue'  // 组件导入在前
import { computed, ref } from 'vue'  // Vue API 在后
```

### 2. 箭头函数参数规范 (Arrow Function Parameters)
- **回调函数参数统一使用括号包裹**，即使只有单个参数
- 保持代码风格一致性，避免 ESLint 警告

```typescript
// ✅ 正确示例
items.filter((item) => item.categoryId === categoryId)
categories.forEach((category) => console.log(category))

// ❌ 错误示例
items.filter(item => item.categoryId === categoryId)  // 缺少括号
categories.forEach(category => console.log(category))  // 缺少括号
```

### 3. Try-Catch-Finally 格式 (Error Handling Format)
- **catch 和 finally 关键字必须与前面的大括号保持在同一行**
- 避免不必要的换行

```typescript
// ✅ 正确示例
try {
  await someOperation()
} catch (error) {
  console.error(error)
} finally {
  loading.value = false
}

// ❌ 错误示例
try {
  await someOperation()
}
catch (error) {  // catch 另起一行
  console.error(error)
}
finally {  // finally 另起一行
  loading.value = false
}
```

### 4. If-Else 格式 (Conditional Statement Format)
- **else 关键字必须与前面的大括号保持在同一行**

```typescript
// ✅ 正确示例
if (condition) {
  doSomething()
} else {
  doOtherwiseT()
}

// ❌ 错误示例
if (condition) {
  doSomething()
}
else {  // else 另起一行
  doOtherwise()
}
```

### 5. 清理未使用的导入 (Remove Unused Imports)
- **删除所有未使用的导入、变量、函数**
- 定期检查并清理冗余代码

```typescript
// ✅ 正确示例
import { ref } from 'vue'
const count = ref(0)

// ❌ 错误示例
import { ref, computed } from 'vue'  // computed 未使用
import { useTabsStore } from '@/stores'  // store 未使用
const count = ref(0)
```

### 6. 代码简化原则 (Code Simplification)
- **避免不必要的 if-return-else 模式**
- **直接返回布尔表达式**

```typescript
// ✅ 正确示例
const isValid = computed(() => {
  if (!basicInfo.value.name)
    return false
  return isScoreValid.value
})

// ❌ 错误示例
const isValid = computed(() => {
  if (!basicInfo.value.name)
    return false
  if (!isScoreValid.value)
    return false
  return true
})
```

### 7. 异步操作规范 (Async Operations)
- **router.push 等异步操作需要 await**
- **删除操作后的刷新需要 await**

```typescript
// ✅ 正确示例
await router.push(LIST_PATH)
await search()

// ❌ 错误示例
router.push(LIST_PATH)  // 缺少 await
search()  // 缺少 await
```

### 8. Vue Composition API 规范 (Vue Composition API Standards)
- **始终使用 defineOptions 定义组件名称**
- **确保正确导入所有使用的 Vue API**

```typescript
// ✅ 正确示例
import { computed, defineOptions, ref } from 'vue'
defineOptions({ name: 'ComponentName' })

// ❌ 错误示例
// 缺少 defineOptions 导入或未定义组件名称
```

### 9. 数据模型同步 (Data Model Synchronization)
- **前后端数据结构变更需要同步更新**
- **移除废弃字段时，需要同时更新类型定义、组件使用、API 调用**

```typescript
// 示例：移除 isBreadcrumb 字段时需要同步更新
// 1. 类型定义 (src/apis/system/type.ts)
// 2. 路由处理 (src/stores/modules/route.ts)
// 3. 表单组件 (src/views/system/menu/AddModal.vue)
```

---

## 编码规范检查清单 (Coding Standards Checklist)

提交代码前，请确认以下事项：

- [ ] 导入顺序正确（第三方库 → Vue API → 组件 → 本地工具）
- [ ] 箭头函数参数使用括号包裹
- [ ] try-catch-finally 和 if-else 格式正确
- [ ] 无未使用的导入、变量、函数
- [ ] 异步操作正确使用 await
- [ ] 组件使用 defineOptions 定义名称
- [ ] 无 ESLint 警告或错误
- [ ] 数据模型变更已同步更新所有相关文件