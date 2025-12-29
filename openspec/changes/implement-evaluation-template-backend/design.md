# Design Document: 评审模板管理后端实现

## 架构设计 (Architecture)

### 整体架构

```
┌─────────────────────────────────────────────────────┐
│                    Frontend (Vue 3)                  │
│  ┌─────────┐  ┌──────────┐  ┌──────────┐           │
│  │ List.vue│  │Create.vue│  │ Edit.vue │           │
│  └────┬────┘  └────┬─────┘  └────┬─────┘           │
└───────┼───────────┼─────────────┼──────────────────┘
        │           │              │
        │   HTTP Requests (JSON)   │
        ▼           ▼              ▼
┌─────────────────────────────────────────────────────┐
│              Controller Layer (API)                  │
│  ┌────────────────────────────────────────────────┐ │
│  │   EvaluationTemplateController                 │ │
│  │  - @CrudRequestMapping                        │ │
│  │  - @PostMapping (custom create)               │ │
│  │  - @PutMapping (custom update)                │ │
│  │  - @PostMapping ("/export")                   │ │
│  └──────────────────┬─────────────────────────────┘ │
└─────────────────────┼───────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────────┐
│              Service Layer (业务逻辑)                 │
│  ┌────────────────────────────────────────────────┐ │
│  │   EvaluationTemplateService                    │ │
│  │  - create(req) → validate → save template     │ │
│  │                  → batch save items            │ │
│  │  - update(req, id) → validate → update        │ │
│  │                      → delete old items        │ │
│  │                      → batch save new items    │ │
│  │  - delete(ids) → delete items → delete tmpl   │ │
│  │  - getDetail(id) → get template + items       │ │
│  │  - export(...) → query + format               │ │
│  └──────────────────┬─────────────────────────────┘ │
└─────────────────────┼───────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────────┐
│              Mapper Layer (数据访问)                  │
│  ┌──────────────────────┐  ┌────────────────────┐  │
│  │EvaluationTemplateMapper│  │...ItemMapper      │  │
│  │ - baseMapper methods  │  │- selectByTemplateId│  │
│  │ - custom queries      │  │- deleteByTemplateId│  │
│  └──────────┬────────────┘  └────────┬───────────┘  │
└─────────────┼────────────────────────┼──────────────┘
              ▼                        ▼
┌─────────────────────────────────────────────────────┐
│                    Database (MySQL)                  │
│  ┌────────────────────┐  ┌────────────────────────┐ │
│  │prj_evaluation_     │  │prj_evaluation_template_│ │
│  │template            │  │item                    │ │
│  └────────────────────┘  └────────────────────────┘ │
└─────────────────────────────────────────────────────┘
```

## 数据模型设计 (Data Model)

### 实体关系图 (ER Diagram)

```
prj_evaluation_template (1) ←──── (N) prj_evaluation_template_item
├── id (PK)                         ├── id (PK)
├── code (UK)                       ├── template_id (FK)
├── name                            ├── category_name
├── description                     ├── category_sort
├── total_score                     ├── item_name
├── item_count (统计字段)            ├── item_sort
├── category_count (统计字段)        ├── max_score
├── status                          ├── description
├── sort                            ├── create_time
├── remark                          └── update_time
├── create_time
└── update_time
```

### 数据组织方式

**后端存储**: 扁平化结构（item 表中同时存储 category 和 item 信息）

**优势**:
- ✅ 简化数据库设计（无需单独的 category 表）
- ✅ 查询高效（一次 JOIN 即可获取完整数据）
- ✅ 支持灵活排序（category_sort + item_sort）

**前端展示**: 分组展示（按 category_name + category_sort 分组）

```typescript
// 前端数据结构
interface Category {
  id: string
  name: string
  sort: number
  items: ScoringItem[]
  subtotal: number
}
```

## 核心业务逻辑 (Core Business Logic)

### 1. 创建模板流程

```
创建模板(EvaluationTemplateReq)
  ├── 1. 前置校验
  │   ├── 编码唯一性检查 (code not exists)
  │   ├── 名称唯一性检查 (name not exists)
  │   └── 总分一致性检查 (sum(items.maxScore) == totalScore)
  │
  ├── 2. 保存模板主表
  │   ├── 生成主键 ID
  │   ├── 设置 create_user, create_time
  │   ├── 初始 item_count = 0, category_count = 0
  │   └── INSERT INTO prj_evaluation_template
  │
  ├── 3. 批量保存评分项
  │   ├── 为每个 item 生成 ID
  │   ├── 设置 template_id = 模板ID
  │   ├── 设置 create_user, create_time
  │   └── BATCH INSERT INTO prj_evaluation_template_item
  │
  └── 4. 更新统计字段
      ├── 计算 item_count = items.size()
      ├── 计算 category_count = distinct(category_name).size()
      └── UPDATE prj_evaluation_template SET item_count, category_count
```

### 2. 修改模板流程

```
修改模板(id, EvaluationTemplateReq)
  ├── 1. 前置校验
  │   ├── 模板是否存在 (id exists)
  │   ├── 编码唯一性检查 (code not exists AND id != current)
  │   ├── 名称唯一性检查 (name not exists AND id != current)
  │   └── 总分一致性检查
  │
  ├── 2. 删除旧评分项
  │   └── DELETE FROM prj_evaluation_template_item WHERE template_id = id
  │
  ├── 3. 更新模板主表
  │   ├── 设置 update_user, update_time
  │   └── UPDATE prj_evaluation_template
  │
  ├── 4. 批量保存新评分项
  │   └── （同创建流程）
  │
  └── 5. 更新统计字段
      └── （同创建流程）
```

**设计决策**: 为什么选择"删除旧项 + 插入新项"而不是"差异更新"？

- ✅ 简化逻辑（避免复杂的增删改判断）
- ✅ 保证一致性（前端传什么，后端就存什么）
- ✅ 性能可接受（评分项数量通常 < 100）
- ✅ 与 ContiNew Admin 现有模式一致

### 3. 删除模板流程

```
删除模板(List<Long> ids)
  ├── 1. 删除关联评分项
  │   └── DELETE FROM prj_evaluation_template_item
  │       WHERE template_id IN (ids)
  │
  └── 2. 软删除模板主表
      └── UPDATE prj_evaluation_template
          SET deleted = id
          WHERE id IN (ids)
```

**注意**: 使用逻辑删除（deleted = id），而非物理删除。

### 4. 详情查询流程

```
查询详情(id)
  ├── 1. 查询模板主表
  │   └── SELECT * FROM prj_evaluation_template WHERE id = ?
  │
  ├── 2. 查询评分项
  │   └── SELECT * FROM prj_evaluation_template_item
  │       WHERE template_id = ?
  │       ORDER BY category_sort, item_sort
  │
  └── 3. 组装返回
      └── EvaluationTemplateDetailResp {
            ...basicInfo,
            items: List<EvaluationTemplateItemResp>
          }
```

### 5. 导出流程

```
导出模板(query, ids)
  ├── 1. 查询模板列表
  │   ├── IF ids provided: WHERE id IN (ids)
  │   └── ELSE: WHERE query conditions
  │
  ├── 2. 为每个模板查询评分项
  │   └── SELECT * WHERE template_id IN (templateIds)
  │
  ├── 3. 按大类分组并计算小计
  │   └── Group by category_name, category_sort
  │       Calculate subtotal = sum(max_score)
  │
  ├── 4. 构建 Excel 行数据
  │   ├── 普通行: 模板编码 | 模板名称 | 大类 | 评分项 | 满分
  │   ├── 小计行: 模板编码 | 模板名称 | 大类小计 | - | 小计值
  │   └── 总计行: 模板编码 | 模板名称 | 总计 | - | 总分
  │
  └── 5. 应用自定义样式
      ├── 小计行: 灰色背景 + 加粗
      └── 总计行: 蓝色背景 + 白色加粗文字
```

**导出格式示例**:

| 模板编码 | 模板名称 | 评分大类 | 评分项 | 满分 |
|---------|---------|---------|--------|------|
| TPL_001 | 技术评审 | 技术创新 | 技术先进性 | 15.00 |
| TPL_001 | 技术评审 | 技术创新 | 创新突破性 | 10.00 |
| TPL_001 | 技术评审 | **技术创新小计** | - | **25.00** |
| TPL_001 | 技术评审 | 应用价值 | 应用前景 | 15.00 |
| ... | ... | ... | ... | ... |
| TPL_001 | 技术评审 | **总计** | - | **100.00** |

## 数据校验设计 (Validation)

### 1. 参数校验（Jakarta Validation）

```java
@NotBlank(message = "模板编码不能为空")
@Pattern(regexp = "^TPL_[A-Z0-9]+$", message = "编码格式不正确")
private String code;

@NotBlank(message = "模板名称不能为空")
@Length(max = 50, message = "名称长度不能超过50个字符")
private String name;

@NotNull(message = "总分不能为空")
@DecimalMin(value = "50.00", message = "总分不能低于50分")
@DecimalMax(value = "500.00", message = "总分不能超过500分")
private BigDecimal totalScore;

@NotEmpty(message = "评分项不能为空")
@Valid
private List<EvaluationTemplateItemReq> items;
```

### 2. 业务校验（Service 层）

```java
private void validateTemplate(EvaluationTemplateReq req, Long excludeId) {
    // 1. 编码唯一性
    Long codeCount = lambdaQuery()
        .eq(EvaluationTemplateDO::getCode, req.getCode())
        .ne(excludeId != null, EvaluationTemplateDO::getId, excludeId)
        .count();
    ValidationUtils.throwIf(codeCount > 0, "模板编码 [{}] 已存在", req.getCode());

    // 2. 名称唯一性
    Long nameCount = lambdaQuery()
        .eq(EvaluationTemplateDO::getName, req.getName())
        .ne(excludeId != null, EvaluationTemplateDO::getId, excludeId)
        .count();
    ValidationUtils.throwIf(nameCount > 0, "模板名称 [{}] 已存在", req.getName());

    // 3. 总分一致性
    BigDecimal itemsSum = req.getItems().stream()
        .map(EvaluationTemplateItemReq::getMaxScore)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalScore = req.getTotalScore();
    boolean isMatch = itemsSum.compareTo(totalScore) == 0;

    ValidationUtils.throwIf(!isMatch,
        "评分项总分 [{}] 与模板总分 [{}] 不一致", itemsSum, totalScore);
}
```

## 性能优化设计 (Performance)

### 1. 批量操作

```java
// 使用 MyBatis Plus 批量插入
evaluationTemplateItemService.saveBatch(items, 1000); // 每批次 1000 条
```

### 2. 查询优化

```sql
-- 添加索引（已在表结构中定义）
CREATE INDEX idx_template_id ON prj_evaluation_template_item(template_id);
CREATE INDEX idx_category_sort ON prj_evaluation_template_item(template_id, category_sort, item_sort);
```

### 3. 统计字段

使用冗余字段 `item_count` 和 `category_count` 避免列表查询时的 COUNT 子查询。

## 技术选型说明 (Technical Choices)

### 1. BigDecimal vs Double

**选择 BigDecimal** 用于分数字段

**原因**:
- ✅ 避免浮点数精度问题（0.1 + 0.2 ≠ 0.3）
- ✅ 金融/评分系统的标准做法
- ✅ 支持精确的比较和运算

**前后端约定**:
- 前端：使用 `string` 类型存储 decimal 值（如 `"100.00"`）
- 后端：使用 `BigDecimal` 类型
- 传输：JSON 中以字符串形式传递

### 2. 事务管理

**所有写操作方法都必须添加 `@Transactional(rollbackFor = Exception.class)`**

```java
@Override
@Transactional(rollbackFor = Exception.class)
public void create(EvaluationTemplateReq req) {
    // 保证模板和评分项的原子性
}
```

### 3. 导出实现

使用 **EasyExcel + 自定义 CellWriteHandler**

```java
public class EvaluationTemplateExportStyleStrategy implements CellWriteHandler {
    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        // 判断是否为小计行或总计行，应用特殊样式
    }
}
```

## 安全性考虑 (Security)

1. **权限控制** - 使用 `@SaCheckPermission` 注解
2. **SQL 注入防护** - 使用 MyBatis Plus 的参数化查询
3. **XSS 防护** - 前端已做转义，后端存储原始数据
4. **CSRF 防护** - 框架默认支持

## 可扩展性设计 (Extensibility)

### 未来可能的扩展点

1. **模板复制功能**
   - 新增 API: `POST /prj/template/evaluation-template/{id}/copy`
   - 复制时生成新编码

2. **模板版本管理**
   - 新增版本号字段 `version`
   - 修改时创建新版本记录

3. **评分项公式计算**
   - 新增公式字段 `formula`
   - 支持加权平均等计算

4. **导入功能**
   - 新增 API: `POST /prj/template/evaluation-template/import`
   - 解析 Excel 并批量创建

## 开发注意事项 (Development Notes)

1. **严格遵循 `开发标准规范.md`**
   - Controller 不写业务逻辑
   - Service 必须定义接口
   - 所有方法必须有中文注释
   - 使用 Lombok 减少样板代码

2. **参考现有模块实现**
   - `UserService` - 复杂的 CRUD 逻辑
   - `DeptService` - 树形数据处理
   - `MenuService` - 嵌套数据处理

3. **测试策略**
   - 单元测试：使用 JUnit 5 + Mockito
   - 集成测试：使用 Spring Boot Test + H2
   - 前后端联调：使用 Postman/Knife4j

4. **代码审查要点**
   - 事务注解是否添加
   - 异常处理是否完善
   - 日志记录是否充分
   - 性能是否满足要求（<200ms）

## 数据字典 SQL (Dictionary SQL)

```sql
-- 插入评审模板总分字典
INSERT INTO `sys_dict` (`id`, `name`, `code`, `description`, `is_system`, `create_user`, `create_time`)
VALUES (101, '评审模板总分', 'eval_template_score', '评审模板的满分值选项', b'1', 1, NOW());

-- 插入字典项
INSERT INTO `sys_dict_item` (`id`, `label`, `value`, `color`, `sort`, `description`, `status`, `dict_id`, `create_user`, `create_time`)
VALUES
(1001, '100分', '100.00', 'primary', 1, '标准模板满分100分', 1, 101, 1, NOW()),
(1002, '120分', '120.00', 'success', 2, '扩展模板满分120分', 1, 101, 1, NOW()),
(1003, '150分', '150.00', 'warning', 3, '高级模板满分150分', 1, 101, 1, NOW());
```

**注意**: 请先检查 `sys_dict` 表中最大的 `id`，避免主键冲突。
