# 评审模板管理后端实现 - OpenSpec Proposal

## 📄 文档索引

- **[proposal.md](./proposal.md)** - 提案概述、目标、需求、技术设计、阶段划分
- **[design.md](./design.md)** - 详细架构设计、数据模型、业务逻辑、技术选型
- **[tasks.md](./tasks.md)** - 分阶段任务清单（7个阶段，共160+任务）

## 🎯 提案目标

实现评审模板管理系统的**完整后端 API**，对接已完成的前端界面（Sprint 1），提供：

1. ✅ **完整的 CRUD API** - 模板的增删改查
2. ✅ **嵌套数据管理** - 一个模板包含多个大类和评分项
3. ✅ **数据验证** - 编码唯一、名称唯一、总分校验
4. ✅ **导出功能** - 支持分组导出和小计行
5. ✅ **编码自动生成** - 格式为 `TPL_XXX`

## 📊 当前状态

| 模块 | 状态 | 说明 |
|------|------|------|
| 前端 Sprint 1 | ✅ 已完成 | UI组件、表单、Mock数据 |
| 数据库表 | ✅ 已完成 | `prj_evaluation_template` 和 `prj_evaluation_template_item` |
| 数据字典 | ⚠️ 待添加 | `eval_template_score`（100/120/150分） |
| 后端实现 | ❌ 待开始 | 本提案内容 |

## 🏗️ 技术架构

```
前端 (Vue 3 + TypeScript)
         ↓
    RESTful API
         ↓
Controller Layer (API接口)
         ↓
Service Layer (业务逻辑)
         ↓
Mapper Layer (数据访问)
         ↓
Database (MySQL)
```

## 📦 交付物

### Phase 1: 数据字典（0.5天）
- SQL 脚本：添加 `eval_template_score` 字典

### Phase 2: 数据模型层（1天）
- `EvaluationTemplateDO` / `EvaluationTemplateItemDO` - 实体类
- `EvaluationTemplateReq` / `EvaluationTemplateItemReq` - 请求参数
- `EvaluationTemplateResp` / `EvaluationTemplateDetailResp` - 响应参数
- `EvaluationTemplateQuery` - 查询条件

### Phase 3: 数据访问层（1天）
- `EvaluationTemplateMapper` / `EvaluationTemplateItemMapper` - Mapper接口
- `EvaluationTemplateMapper.xml` / `EvaluationTemplateItemMapper.xml` - SQL映射

### Phase 4: 业务逻辑层（2-3天）
- `EvaluationTemplateService` - Service接口
- `EvaluationTemplateServiceImpl` - Service实现
  - `create()` - 创建模板
  - `update()` - 修改模板
  - `delete()` - 批量删除
  - `getDetail()` - 查询详情
  - `generateCode()` - 生成编码
  - `validateTemplate()` - 数据校验

### Phase 5: API接口层（0.5-1天）
- `EvaluationTemplateController` - Controller
  - `POST /prj/template/evaluation-template` - 创建
  - `PUT /prj/template/evaluation-template/{id}` - 修改
  - `GET /prj/template/evaluation-template` - 分页查询
  - `GET /prj/template/evaluation-template/{id}` - 详情
  - `DELETE /prj/template/evaluation-template` - 批量删除
  - `POST /prj/template/evaluation-template/export` - 导出
  - `GET /prj/template/evaluation-template/generate-code` - 生成编码

### Phase 6: 导出功能（1-1.5天）
- `EvaluationTemplateExportResp` - 导出数据模型
- `EvaluationTemplateExportStyleStrategy` - 自定义导出样式

### Phase 7: 集成测试（1天）
- 单元测试
- 前后端联调
- 边界case测试
- 性能测试

## 🔑 核心业务逻辑

### 1. 创建模板流程

```
1. 前置校验
   ├── 编码唯一性 (code not exists)
   ├── 名称唯一性 (name not exists)
   └── 总分一致性 (sum(items.maxScore) == totalScore)

2. 保存模板主表
   └── INSERT INTO prj_evaluation_template

3. 批量保存评分项
   └── BATCH INSERT INTO prj_evaluation_template_item

4. 更新统计字段
   └── UPDATE item_count, category_count
```

### 2. 修改模板流程

```
1. 前置校验 (同创建)
2. 删除旧评分项
   └── DELETE FROM prj_evaluation_template_item WHERE template_id = ?
3. 更新模板主表
4. 批量保存新评分项
5. 更新统计字段
```

### 3. 总分校验逻辑

```java
BigDecimal itemsSum = items.stream()
    .map(EvaluationTemplateItemReq::getMaxScore)
    .reduce(BigDecimal.ZERO, BigDecimal::add);

boolean isMatch = itemsSum.compareTo(totalScore) == 0;
```

## 📋 API 接口清单

| 接口 | 方法 | 路径 | 权限 |
|------|------|------|------|
| 分页查询 | GET | `/prj/template/evaluation-template` | `prj:template:evaluation-template:list` |
| 详情查询 | GET | `/prj/template/evaluation-template/{id}` | `prj:template:evaluation-template:get` |
| 创建模板 | POST | `/prj/template/evaluation-template` | `prj:template:evaluation-template:create` |
| 修改模板 | PUT | `/prj/template/evaluation-template/{id}` | `prj:template:evaluation-template:update` |
| 批量删除 | DELETE | `/prj/template/evaluation-template` | `prj:template:evaluation-template:delete` |
| 导出模板 | POST | `/prj/template/evaluation-template/export` | `prj:template:evaluation-template:export` |
| 生成编码 | GET | `/prj/template/evaluation-template/generate-code` | `prj:template:evaluation-template:create` |

## ⚠️ 重要注意事项

### 1. 数据库字段类型

```java
// ✅ 正确：使用 BigDecimal
private BigDecimal totalScore;
private BigDecimal maxScore;

// ❌ 错误：不要使用 Double
private Double totalScore; // 浮点数精度问题
```

### 2. 前后端数据格式

```typescript
// 前端 (TypeScript)
totalScore: string = "100.00"  // 字符串格式

// 后端 (Java)
BigDecimal totalScore = new BigDecimal("100.00")  // BigDecimal

// JSON 传输
{"totalScore": "100.00"}  // 字符串
```

### 3. 事务管理

```java
@Override
@Transactional(rollbackFor = Exception.class)
public void create(EvaluationTemplateReq req) {
    // 确保模板和评分项的原子性
}
```

### 4. 编码规范

- ✅ 所有类、方法、字段必须有中文注释
- ✅ 遵循 `docs/project/开发标准规范.md`
- ✅ 遵循 Alibaba Java 编码规范
- ✅ Controller 不写业务逻辑
- ✅ Service 必须定义接口
- ✅ 使用 Lombok 减少样板代码

## 🧪 测试策略

### 单元测试

- 使用 JUnit 5 + Mockito
- 覆盖所有 Service 方法
- 测试正常场景和异常场景

### 集成测试

- 使用 Spring Boot Test
- 使用 H2 内存数据库
- 测试完整的请求-响应流程

### 前后端联调

- 使用 Postman 或 Knife4j 测试 API
- 使用浏览器开发者工具查看网络请求
- 测试所有前端页面功能

## 📊 预估工作量

| 阶段 | 预估时间 |
|------|---------|
| Phase 1: 数据字典 | 0.5 天 |
| Phase 2: 数据模型层 | 1 天 |
| Phase 3: 数据访问层 | 1 天 |
| Phase 4: 业务逻辑层 | 2-3 天 |
| Phase 5: API接口层 | 0.5-1 天 |
| Phase 6: 导出功能 | 1-1.5 天 |
| Phase 7: 集成测试 | 1 天 |
| **总计** | **7-9 个工作日** |

## ✅ 验收标准

### 功能验收

- [ ] 所有 API 接口可正常调用
- [ ] 创建模板成功，数据正确保存
- [ ] 修改模板成功，数据正确更新
- [ ] 删除模板成功，关联项同步删除
- [ ] 详情查询返回完整数据
- [ ] 导出 Excel 格式正确（包含分组、小计、总计）
- [ ] 编码自动生成格式正确且唯一
- [ ] 编码/名称唯一性校验生效
- [ ] 总分一致性校验生效（误差 < 0.01）

### 性能验收

- [ ] 创建/修改响应时间 < 500ms
- [ ] 列表查询响应时间 < 200ms
- [ ] 详情查询响应时间 < 200ms
- [ ] 导出响应时间 < 2s（10个模板）

### 代码质量验收

- [ ] 所有代码有中文注释
- [ ] 遵循开发规范
- [ ] 无 SonarLint 警告
- [ ] 单元测试覆盖率 > 80%

## 📚 相关文档

- `implement-evaluation-template-management/tasks.md` - 前端任务清单（Sprint 1）
- `docs/project/开发标准规范.md` - 开发规范
- `docs/sql/prj_evaluation_template.sql` - 数据库表结构
- `continew-admin-ui/.../evaluationTemplate/README.md` - 前端模块说明
- `continew-admin-ui/.../evaluationTemplate/api/type.ts` - 前端接口定义

## 🚀 开始开发

### 1. 准备工作

```bash
# 1. 切换到开发分支
git checkout -b feature/evaluation-template-backend

# 2. 检查数据库表是否创建
# 运行 docs/sql/prj_evaluation_template.sql

# 3. 添加数据字典
# 执行 Phase 1 中的 SQL 脚本
```

### 2. 开发流程

```bash
# 1. 按照 tasks.md 中的顺序逐个完成任务
# 2. 每完成一个小任务提交一次代码
git add .
git commit -m "feat: 添加评审模板实体类"

# 3. 定期推送到远程仓库
git push origin feature/evaluation-template-backend
```

### 3. 测试流程

```bash
# 1. 运行单元测试
mvn test

# 2. 启动后端服务
mvn spring-boot:run

# 3. 访问 Swagger 文档
http://localhost:8080/doc.html

# 4. 配置前端，切换为真实 API
# 修改 continew-admin-ui/src/views/.../api/index.ts
# 将 USE_MOCK 改为 false
```

## 💡 开发建议

1. **严格遵循开发规范** - 参考 `开发标准规范.md` 和现有模块（如 `UserService`）
2. **先测试后提交** - 每个功能完成后先本地测试，确认无误后再提交
3. **及时沟通** - 遇到问题及时沟通，避免方向错误
4. **代码审查** - 提交前使用 SonarLint 检查代码质量
5. **文档同步** - 及时更新 API 文档和注释

## 📞 联系方式

如有疑问，请联系：
- 项目负责人：[待填写]
- 前端负责人：[待填写]
- 后端负责人：[待填写]

---

**创建日期**: 2025-12-29
**最后更新**: 2025-12-29
**状态**: 📝 待审核
