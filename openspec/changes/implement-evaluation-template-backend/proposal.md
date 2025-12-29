# Proposal: 评审模板管理后端实现 (Backend Implementation for Evaluation Template Management)

## 概述 (Summary)

实现评审模板管理系统的后端 API 接口，对接已完成的前端界面，提供完整的 CRUD、导出、编码生成等功能。

## 背景 (Context)

前端 Sprint 1 已完成（见 `implement-evaluation-template-management`），包含:
- ✅ 完整的 UI 组件（列表页、创建页、编辑页、详情页）
- ✅ 分步向导表单（基本信息 + 评分项配置）
- ✅ TypeScript 接口定义
- ✅ Mock 数据

现需要实现后端 API 以支持真实数据操作。

## 目标 (Goals)

### 主要目标
1. **实现完整的评审模板 CRUD API**，支持模板的增删改查
2. **实现嵌套评分项管理**，一个模板包含多个大类，每个大类包含多个评分项
3. **实现数据验证**，确保编码唯一、名称唯一、总分校验
4. **实现导出功能**，支持分组导出和小计行
5. **实现编码自动生成**，格式为 `TPL_XXX`

### 非目标
- ❌ 前端界面修改（前端已完成）
- ❌ 权限管理（使用现有权限体系）
- ❌ 模板版本管理（后续扩展）
- ❌ 模板导入功能（后续扩展）

## 需求 (Requirements)

### 功能需求

1. **分页查询** - 支持按编码、名称、状态筛选，支持排序
2. **详情查询** - 返回模板基本信息和完整评分项列表
3. **创建模板** - 提交基本信息和评分项，校验唯一性和总分
4. **修改模板** - 全量更新（删除旧评分项，插入新评分项）
5. **删除模板** - 批量删除模板及其关联评分项
6. **导出模板** - 支持按 IDs 或按查询条件导出，Excel 格式，包含分组和小计
7. **生成编码** - 自动生成唯一的模板编码

### 数据库需求

已有表结构（见 `prj_evaluation_template.sql`）:
- ✅ `prj_evaluation_template` - 模板主表
- ✅ `prj_evaluation_template_item` - 评分项表

**数据库修改需求**:
- ⚠️ 需要添加字典 `eval_template_score`（100/120/150 分）

### API 接口需求

基于前端 `api/index.ts` 和 `api/type.ts` 定义：

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 分页查询 | GET | `/prj/template/evaluation-template` | 支持 code/name/status 筛选 |
| 详情查询 | GET | `/prj/template/evaluation-template/{id}` | 返回模板和评分项 |
| 创建模板 | POST | `/prj/template/evaluation-template` | 提交基本信息和评分项 |
| 修改模板 | PUT | `/prj/template/evaluation-template/{id}` | 全量更新 |
| 批量删除 | DELETE | `/prj/template/evaluation-template` | 传入 IDs |
| 导出模板 | POST | `/prj/template/evaluation-template/export` | 按 IDs 或查询条件 |
| 生成编码 | GET | `/prj/template/evaluation-template/generate-code` | 返回唯一编码 |

## 技术设计 (Technical Design)

### 架构分层

```
Controller (API 层)
    ↓
Service (业务层)
    ↓
Mapper (数据层)
```

### 数据模型

**实体类 (Entity/DO)**:
- `EvaluationTemplateDO` - 模板主表实体
- `EvaluationTemplateItemDO` - 评分项实体

**请求参数 (Req)**:
- `EvaluationTemplateReq` - 创建/修改模板
- `EvaluationTemplateItemReq` - 嵌套在模板请求中
- `EvaluationTemplateQuery` - 查询条件

**响应参数 (Resp)**:
- `EvaluationTemplateResp` - 列表响应
- `EvaluationTemplateDetailResp` - 详情响应
- `EvaluationTemplateItemResp` - 评分项响应

### 关键技术点

1. **事务管理** - 创建/修改/删除时确保模板和评分项的原子性
2. **批量插入** - 评分项使用 MyBatis Plus 的 `saveBatch`
3. **BigDecimal 处理** - 分数字段使用 `BigDecimal` 类型
4. **数据校验** - 使用 Jakarta Validation 和自定义校验逻辑
5. **导出格式** - 使用 EasyExcel + 自定义 CellWriteHandler

### 核心验证逻辑

```java
// 1. 编码唯一性校验（排除自身）
// 2. 名称唯一性校验（排除自身）
// 3. 总分范围校验（50-500）
// 4. 总分一致性校验：
//    sum(items.maxScore) == template.totalScore
```

## 开发阶段 (Phases)

### Phase 1: 基础 CRUD（2-3 天）
- 创建实体类、请求参数、响应参数
- 实现 Mapper 和基础 SQL
- 实现 Service 的增删改查逻辑
- 实现 Controller 接口
- 单元测试

### Phase 2: 数据验证（0.5-1 天）
- 实现编码/名称唯一性校验
- 实现总分一致性校验
- 添加自定义校验注解（如需要）

### Phase 3: 导出功能（1-1.5 天）
- 实现数据组装逻辑
- 实现自定义导出样式
- 处理分组小计和总计行

### Phase 4: 集成测试（0.5-1 天）
- 前后端联调
- 边界case测试
- 性能测试

## 风险与依赖 (Risks & Dependencies)

### 依赖
- ✅ 前端界面已完成
- ✅ 数据库表已创建
- ⚠️ 需要添加字典 `eval_template_score`

### 风险
- **总分校验复杂度** - 需要处理浮点数精度问题 → 使用 `BigDecimal`
- **批量操作性能** - 评分项可能较多 → 使用批量插入
- **导出格式要求** - 需要分组和小计 → 使用自定义 CellWriteHandler

## 验收标准 (Acceptance Criteria)

- [ ] 所有 API 接口按照前端定义实现
- [ ] 通过前端集成测试（创建、编辑、删除、导出、详情查看）
- [ ] 编码和名称唯一性校验生效
- [ ] 总分校验生效（误差 < 0.01）
- [ ] 导出文件格式正确（包含分组、小计、总计）
- [ ] 批量删除支持多个模板
- [ ] 自动生成编码格式正确且唯一
- [ ] 遵循 `开发标准规范.md` 和 Alibaba Java 规范
- [ ] 所有代码有中文注释

## 相关文档 (Related Documents)

- `implement-evaluation-template-management/tasks.md` - 前端任务清单
- `docs/project/开发标准规范.md` - 开发规范
- `docs/sql/prj_evaluation_template.sql` - 数据库表结构
- `continew-admin-ui/src/views/.../evaluationTemplate/README.md` - 前端模块说明
- `continew-admin-ui/src/views/.../evaluationTemplate/api/type.ts` - 前端接口定义
