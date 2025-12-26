# M109 评审模板管理 - 模块文档

> 基于原型图的评审模板管理模块完整设计
> 创建时间：2025-12-20

---

## 📂 文档目录

```
M109-评审模板管理/
├── README.md              # 本文件（模块概览）
├── 01-需求文档.md          # 详细需求文档
└── 02-数据库脚本.sql       # 建表和初始数据脚本
```

---

## 📋 模块概述

**模块名称**：M109 评审模板管理
**优先级**：P0（核心基础模块）
**依赖关系**：无（独立模块）

### 核心功能
1. 评审模板列表查询（搜索、分页）
2. 新建评审模板（基本信息 + 评分项配置）
3. 编辑评审模板
4. 复制评审模板
5. 启用/停用模板
6. 查看评分项详情

---

## 🎯 业务价值

评审模板是专家评审的核心依据：
- 每个项目类型关联一个评审模板
- 专家评审时按模板中的评分项进行打分
- 模板定义了评分标准和评分项
- 支持灵活配置不同类型项目的评审标准

---

## 🗂 数据库设计

### 表结构

| 表名 | 说明 | 关键字段 |
|-----|------|---------|
| `prj_evaluation_template` | 评审模板主表 | code, name, total_score, item_count |
| `prj_evaluation_template_item` | 评审模板评分项表 | template_id, category_name, item_name, max_score |

### 数据关系
```
prj_evaluation_template (1) ←→ (N) prj_evaluation_template_item
```

### 索引设计
- 主表：code 唯一索引、status 普通索引
- 明细表：template_id 索引、(template_id, category_sort, item_sort) 联合索引

---

## 📊 初始数据

系统预置 **5 个评审模板**：

| 编码 | 模板名称 | 评分项数量 | 大类数量 | 状态 |
|-----|---------|----------|---------|------|
| TPL_001 | 产品研发评审模板 | 8 项 | 5 大类 | 启用 |
| TPL_002 | 技术创新评审模板 | 10 项 | 5 大类 | 启用 |
| TPL_003 | 流程优化评审模板 | 6 项 | 4 大类 | 启用 |
| TPL_004 | 基础设施评审模板 | 7 项 | 5 大类 | 启用 |
| TPL_005 | 市场拓展评审模板 | 9 项 | 5 大类 | 停用 |

**示例**：技术创新评审模板（TPL_002）
```
一、技术创新性（25分）
  1. 技术先进性（15分）
  2. 创新突破性（10分）

二、技术可行性（25分）
  3. 技术方案合理性（15分）
  4. 实施团队能力（10分）

三、应用价值（30分）
  5. 应用前景（15分）
  6. 预期效益（15分）

四、预算合理性（10分）
  7. 预算编制合理性（10分）

五、风险控制（10分）
  8. 风险识别与应对（5分）
  9. 知识产权保护（3分）
  10. 技术保密措施（2分）

总分：100分
```

---

## 🔑 核心业务规则

### 1. 总分固定规则
- 所有评审模板总分必须等于 **100.00 分**
- 保存时强制校验总分
- 前端实时计算并显示总分（颜色提示）

### 2. 评分项结构
```
评审模板
  └── 评分大类（Category）
        └── 评分项（Item）
```
- 支持多级结构：大类 → 评分项
- 大类分数 = 所有评分项分数之和
- 评分项分值支持小数（精度 0.5）

### 3. 模板编码规则
- 格式：`TPL_XXX`（XXX 为 3 位数字）
- 系统内唯一
- 创建后不可修改

### 4. 状态管理
- 启用（status=1）：可被项目类型关联使用
- 停用（status=2）：不可被新项目类型关联

### 5. 删除限制
- 如果模板已被项目类型关联，不允许删除
- 建议操作：停用模板（软删除）

---

## 🔗 API 接口清单

| 接口 | 方法 | 说明 | 权限 |
|-----|------|------|------|
| `/api/config/evaluation-template` | GET | 列表查询 | `config:template:list` |
| `/api/config/evaluation-template/{id}` | GET | 详情查询 | `config:template:get` |
| `/api/config/evaluation-template` | POST | 新增 | `config:template:create` |
| `/api/config/evaluation-template/{id}` | PUT | 修改 | `config:template:update` |
| `/api/config/evaluation-template/{id}` | DELETE | 删除 | `config:template:delete` |
| `/api/config/evaluation-template/{id}/copy` | POST | 复制 | `config:template:create` |

---

## 🎨 前端页面

### 列表页
- **路由**：`/config/evaluation-template`
- **组件**：`views/config/evaluation-template/index.vue`
- **功能**：搜索、列表展示、新建、编辑、复制、查看详情

### 编辑页
- **路由**：`/config/evaluation-template/edit`
- **组件**：`views/config/evaluation-template/edit/index.vue`
- **功能**：
  - 基本信息编辑
  - 动态评分项配置（支持添加/删除大类和评分项）
  - 实时总分计算和校验
  - 总分颜色提示（绿色=100、红色>100、黄色<100）

---

## 📦 后端模块结构

```
continew-server/
└── src/main/java/top/continew/admin/
    └── config/                    # 配置管理模块
        └── template/              # 评审模板子模块
            ├── controller/
            │   └── EvaluationTemplateController.java
            ├── service/
            │   ├── EvaluationTemplateService.java
            │   └── impl/
            │       └── EvaluationTemplateServiceImpl.java
            ├── mapper/
            │   ├── EvaluationTemplateMapper.java
            │   └── EvaluationTemplateItemMapper.java
            └── model/
                ├── entity/
                │   ├── EvaluationTemplate.java
                │   └── EvaluationTemplateItem.java
                ├── req/
                │   ├── EvaluationTemplateQuery.java
                │   ├── EvaluationTemplateReq.java
                │   └── EvaluationTemplateCopyReq.java
                └── resp/
                    ├── EvaluationTemplateResp.java
                    └── EvaluationTemplateDetailResp.java
```

---

## ⚠️ 注意事项

### 1. 模板修改影响范围
- ✅ 不影响：已完成的评审记录
- ⚠️ 影响：进行中的评审任务
- 💡 建议：重大修改应创建新模板，旧模板停用

### 2. 数据一致性
- 删除模板时检查是否被项目类型关联
- 修改模板时事务处理（主表 + 明细表）
- 评分项删除需级联检查

### 3. 性能优化
- 评审模板数据量不大，可全量缓存
- 评分项查询使用索引优化
- 列表查询支持分页

### 4. 前端交互体验
- 评分项编辑采用动态表单
- 实时计算总分并校验
- 保存前二次确认
- 复制功能预填充数据

---

## 🚀 实施步骤

### 阶段一：数据库准备
1. 执行 `02-数据库脚本.sql` 创建表结构
2. 初始化 5 个评审模板数据
3. 验证数据完整性

### 阶段二：后端开发
1. 创建实体类（Entity）
2. 创建 Mapper 接口
3. 创建 Service 层（接口 + 实现类）
4. 创建 Controller 层
5. 编写单元测试

### 阶段三：前端开发
1. 创建列表页组件
2. 创建编辑页组件
3. 实现评分项动态表单
4. 实现总分实时计算
5. 联调后端接口

### 阶段四：测试验证
1. 功能测试（增删改查）
2. 业务规则测试（总分校验、关联检查）
3. 权限测试
4. 性能测试

---

## 📖 相关文档

- [原型图](../../prototypes/evaluation-template-list.html)
- [01-需求文档.md](./01-需求文档.md) - 详细需求说明
- [02-数据库脚本.sql](./02-数据库脚本.sql) - 建表和初始数据

---

## 📝 变更记录

| 版本 | 日期 | 变更内容 | 作者 |
|-----|------|---------|------|
| V1.0 | 2025-12-20 | 初始版本，完成需求文档和数据库设计 | System |

---

*文档版本：V1.0*
*最后更新：2025-12-20*
