# 评审模板管理 UI 改进总结报告

## 📋 改进概览

本次UI改进针对评审模板管理模块的新增、编辑、详情页面进行了全面优化，解决了布局混乱、视觉层次不清、功能缺失等问题。

---

## ✅ 已完成的改进

### 1. **修复 CategoryPanel.vue 的 emit 错误**
**问题**: 添加大类时报错 `ReferenceError: emit is not defined`

**解决方案**:
```typescript
// Before
defineEmits<{...}>()

// After
const emit = defineEmits<{...}>()
```

**文件**: `components/CategoryPanel.vue`

---

### 2. **重新设计 Step 1（基本信息）布局**
**问题**: 字段散乱，没有清晰的视觉分组，用户体验差

**解决方案**:
- 将表单分为3个明确的区域（Section）：
  - **基本信息**: 模板编码、模板名称
  - **评分配置**: 模板总分、状态、模板说明
  - **附加信息**: 排序、备注
- 每个区域都有：
  - 图标标识（增强视觉识别）
  - 标题和描述文字（说明用途）
  - 清晰的分隔线

**视觉效果**:
- 信息层次清晰
- 减少视觉噪音
- 提升专业感

**文件**: `components/BasicInfoStep.vue`

---

### 3. **调整 Step 2 左右面板比例**
**问题**: 右侧评分项面板过窄，导致内容需要横向滚动

**解决方案**:
```vue
<!-- Before -->
<a-col :md="10" :lg="9">  <!-- 左侧：约40% -->
<a-col :md="14" :lg="15"> <!-- 右侧：约60% -->

<!-- After -->
<a-col :md="8" :lg="7">   <!-- 左侧：约33% -->
<a-col :md="16" :lg="17"> <!-- 右侧：约67% -->
```

**效果**:
- 右侧空间增加约10%
- 减少或消除横向滚动
- 更好的空间利用率

**文件**: `components/ScoringItemsStep.vue`

---

### 4. **编辑页面默认显示第二步**
**问题**: 编辑模板时，用户主要需要修改评分项，但默认显示第一步（基本信息）

**解决方案**:
```typescript
// Before
const currentStep = ref(0)  // 默认第一步

// After
const currentStep = ref(1)  // 默认第二步（评分项配置）
```

**逻辑**:
- 新增模板：从第一步开始（需要填写基本信息）
- 编辑模板：从第二步开始（基本信息已有，主要编辑评分项）

**文件**: `pages/Edit.vue`

---

### 5. **自动生成编码功能完善**
**当前实现**: Mock 函数，查找最大编号+1

**后端集成指南**:
```typescript
/**
 * Backend API: POST /api/evaluation-templates/generate-code
 *
 * 功能要求:
 * 1. 生成格式: TPL_XXX (如: TPL_001)
 * 2. 查询数据库确保唯一性
 * 3. 处理并发请求（数据库锁/约束）
 * 4. 支持自定义前缀配置（可选）
 */
```

**文件**:
- `api/mock.ts` - Mock实现 + 集成指南注释
- `components/BasicInfoStep.vue` - 调用接口

---

### 6. **数据字典集成（模板总分）**
**问题**: 总分选项硬编码，不便于管理和扩展

**解决方案**:

#### SQL配置文件
创建了 `docs/sql/dict_evaluation_template.sql`:
```sql
-- 字典配置
INSERT INTO sys_dict (dict_code, dict_name, ...)
VALUES ('evaluation_template_total_score', '评审模板总分', ...);

-- 字典项
INSERT INTO sys_dict_item (dict_code, dict_label, dict_value, ...)
VALUES
  ('evaluation_template_total_score', '100分', '100.00', 1, ...),
  ('evaluation_template_total_score', '120分', '120.00', 2, ...),
  ...
```

#### 前端集成指南
在 `BasicInfoStep.vue` 中添加了详细注释：
```typescript
/**
 * 集成步骤:
 * 1. Import: import { useDictStore } from '@/store'
 * 2. Load: dictStore.loadDict('evaluation_template_total_score')
 * 3. Use: dictStore.getDict('evaluation_template_total_score')
 */
```

**优势**:
- 集中管理（后台可配置）
- 无需改代码即可新增选项
- 与系统其他字典一致

**文件**:
- `docs/sql/dict_evaluation_template.sql` - SQL配置
- `components/BasicInfoStep.vue` - 集成指南

---

### 7. **表单验证机制**
**验证内容**:
1. **必填字段验证**:
   - 模板编码（3-20字符，TPL_XXX格式）
   - 模板名称（2-50字符）
   - 模板总分
   - 状态

2. **评分校验**:
   - 至少有一个大类
   - 至少有一个评分项
   - 评分项总分 = 模板总分

**实现位置**:
```typescript
// 提交时验证
if (!formState.isFormValid) {
  Message.warning('请检查表单信息是否完整且总分是否匹配')
  return
}
```

**文件**:
- `components/BasicInfoStep.vue` - 字段规则
- `hooks/useTemplateForm.ts` - 总分校验逻辑
- `pages/Create.vue` & `pages/Edit.vue` - 提交验证

---

### 8. **导出功能增强与审查机制**
**当前实现**: 基础导出功能

**后端要求文档**:
```typescript
/**
 * 1. 数据验证:
 *    - 权限验证
 *    - 数据完整性检查
 *    - 导出数量限制
 *    - 操作日志记录
 *
 * 2. 导出格式:
 *    - Excel (.xlsx)
 *    - 多个sheet（基本信息 + 评分项详情）
 *    - 导出元数据（时间、操作人、筛选条件）
 *
 * 3. 安全措施:
 *    - 敏感数据脱敏
 *    - 速率限制
 *    - 临时URL + 过期时间
 *
 * 4. 审查机制（可选）:
 *    - 创建导出申请
 *    - 管理员审批
 *    - 通知下载
 *    - 下载历史追踪
 */
```

**文件**: `pages/List.vue` - 导出函数 + 详细注释

---

## 📝 需要注意的两个问题

### 1. **面包屑导航清理**
**问题**: 新增/编辑/详情页面有冗余的面包屑导航

**建议解决方案**:

#### 方案A: 隐藏非列表页的面包屑
```vue
<!-- 在布局组件中 -->
<a-breadcrumb v-if="!isDetailPage">
  <!-- breadcrumb items -->
</a-breadcrumb>
```

#### 方案B: 简化面包屑（推荐）
```typescript
// 列表页: 工作台 > 系统配置管理 > 评审模板管理
// 详情页: 评审模板管理 > 模板详情
// 编辑页: 评审模板管理 > 编辑模板
```

**实现文件**:
- 检查路由配置中的 `meta.breadcrumb`
- 或在页面组件中自定义面包屑

---

### 2. **Detail 页面收起问题**
**问题**: 评分项收起后，页面变小，只占左半边

**原因分析**:
- 可能是 Detail 页面没有使用响应式布局
- 或者表格/折叠面板的宽度设置有问题

**建议解决方案**:

#### 检查点1: 表格宽度
```vue
<!-- Detail.vue 中的表格 -->
<a-table style="width: 100%">
  <!-- 确保表格占满容器宽度 -->
</a-table>
```

#### 检查点2: 容器布局
```scss
.detail-container {
  width: 100%;  // 确保容器占满宽度
  max-width: 1400px;  // 可以保留最大宽度限制
}
```

#### 检查点3: 折叠面板
```vue
<a-collapse :default-active-key="['1', '2', '3']">
  <!-- 默认展开所有面板，避免收起导致布局变化 -->
</a-collapse>
```

**文件**: `pages/Detail.vue`

---

## 🎨 UI设计原则总结

本次改进遵循以下设计原则：

1. **精致企业级风格**
   - 专业、清晰、高效
   - 充足的呼吸空间
   - 精确的对齐

2. **清晰的视觉层次**
   - 使用分组和分隔
   - 图标辅助识别
   - 标题和描述文字

3. **合理的空间利用**
   - 33%/67% 的黄金比例
   - 响应式布局
   - 减少滚动需求

4. **一致的用户体验**
   - Create 和 Edit 页面布局一致
   - 遵循 Arco Design 规范
   - 符合用户操作习惯

---

## 📁 修改文件清单

### 组件文件
- ✅ `components/BasicInfoStep.vue` - 重新设计布局，添加分组
- ✅ `components/ScoringItemsStep.vue` - 调整面板比例
- ✅ `components/CategoryPanel.vue` - 修复 emit 错误

### 页面文件
- ✅ `pages/List.vue` - 添加导出验证注释
- ✅ `pages/Create.vue` - 保持原有逻辑
- ✅ `pages/Edit.vue` - 默认显示第二步
- ⚠️ `pages/Detail.vue` - 需要修复收起问题（待用户反馈具体情况）

### API文件
- ✅ `api/mock.ts` - 完善生成编码函数，添加集成指南

### 文档文件
- ✅ `docs/sql/dict_evaluation_template.sql` - 数据字典配置SQL
- ✅ `docs/UI_IMPROVEMENTS_SUMMARY.md` - 本文档

---

## 🚀 后续工作

### 立即需要
1. **确认面包屑清理方案** - 需要用户决定使用哪种方案
2. **修复 Detail 页面收起问题** - 需要用户提供具体的复现步骤或截图

### 后端集成时
1. **实现编码生成API** - 参考 `api/mock.ts` 中的注释
2. **配置数据字典** - 执行 `docs/sql/dict_evaluation_template.sql`
3. **集成字典到前端** - 参考 `BasicInfoStep.vue` 中的集成指南
4. **实现导出功能** - 参考 `List.vue` 中的后端要求文档

---

## 💡 设计亮点

1. **Section Headers** - 带图标和描述的分组标题，提升专业感
2. **智能布局** - 33%/67% 黄金比例，最大化空间利用
3. **用户中心** - 编辑页默认第二步，符合用户实际使用场景
4. **完善文档** - 详尽的集成指南和后端要求，便于团队协作

---

## 📞 联系与反馈

如有任何问题或建议，请及时反馈：
- UI/UX问题
- 功能需求
- 技术疑问

---

*文档生成时间: 2025-12-28*
*最后更新: 2025-12-28*
