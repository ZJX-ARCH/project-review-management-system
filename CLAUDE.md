# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

---

# ContiNew Admin - 项目评审管理系统

本项目基于 ContiNew Admin 框架，开发了一套完整的项目评审管理系统。核心自研代码位于 `continew-plugin-review` 和 `continew-admin-ui/src/views/review`。

---

## 常用命令

### 后端构建与运行

```bash
# 启动后端（在 continew-admin/ 目录）
cd continew-admin/continew-server
mvn spring-boot:run

# 格式化代码（提交前必须执行）
mvn spotless:apply

# 接口文档地址
# http://localhost:8000/doc.html
```

### 前端开发

```bash
# 在 continew-admin-ui/ 目录
pnpm install       # 安装依赖
pnpm dev           # 启动开发服务器（http://localhost:5173）
pnpm typecheck     # TypeScript 类型检查
pnpm lint          # ESLint 检查
pnpm lint:fix      # 自动修复代码风格
```

---

## 技术栈

| 层 | 技术 |
|----|------|
| 后端框架 | Spring Boot 3.x + ContiNew Starter 2.15.0 |
| ORM | MyBatis Plus（逻辑删除字段 `deleted`，0=未删除，id=已删除） |
| 认证授权 | Sa-Token（权限标识 `@SaCheckPermission`） |
| 数据库变更 | Liquibase（文件位于 `continew-server/src/main/resources/db/changelog/mysql/`） |
| 前端框架 | Vue 3.5 + TypeScript 5 + Arco Design 2.57 |
| 状态管理 | Pinia |
| 代码风格 | 后端 Spotless + P3C，前端 `@antfu/eslint-config` |

---

## 项目结构（评审模块重点）

### 后端：`continew-admin/continew-plugin-review/`

```
continew-plugin-review/
├── continew-plugin-review-common/   # 共享基础层
│   ├── enums/ProjectStatus.java     # 28个项目状态枚举
│   ├── enums/ApprovalRule.java      # 审批规则（全部通过/多数通过等）
│   ├── enums/TaskType.java          # 任务类型（AUDIT/REVIEW/DECISION等）
│   ├── enums/ModificationStrategy.java  # 修改回退策略
│   ├── engine/StateFlowEngine.java  # 状态机（验证流转规则）
│   └── engine/TimeoutCheckEngine.java   # 超时计算
├── continew-plugin-review-template/ # 流程模板（已完成）
└── continew-plugin-review-form/     # 表单模板（已完成）
```

详细设计见 [continew-admin/continew-plugin-review/CLAUDE.md](continew-admin/continew-plugin-review/CLAUDE.md)

### 前端：`continew-admin-ui/src/views/review/`

```
review/template/
├── process/       # 评审流程模板（轮次配置：审核/评审/决策）
├── management/    # 管理流程模板（阶段配置：立项/执行/验收）
└── form/          # 表单模板（可视化拖拽设计器）
    └── components/designer/   # 字段库、画布、配置面板、预览
```

API 类型定义：`continew-admin-ui/src/apis/review/type.ts`
API 函数：`continew-admin-ui/src/apis/review/[process|management|form]-template.ts`

---

## 数据权限设计

**核心约定**：用户可属于多个部门，每个部门有不同角色，同一时刻只操作当前部门的数据范围。

- 所有业务表都持有 `dept_id` 字段
- 查询列表时按当前用户所在部门的 `dept_id` 过滤
- **目前 Service 层的 `page()` 方法中数据权限过滤尚未实现（标有 TODO）**，需在后续 type/project 模块一起落地

---

## 项目状态机（ProjectStatus）

28 个状态，按阶段划分：
- **评审阶段（1-49）**：草稿 → 已提交 → 审核 → 评审 → 决策 → 已终止
- **执行阶段（50-89）**：项目执行中 → 超时/暂停/验收
- **归档阶段（90+）**：已归档_已完成 / 已归档_不合格 / 已归档_已取消 / 已作废

`StateFlowEngine` 维护完整的流转规则表，调用 `transition()` 前先调用 `isValidTransition()` 校验。

---

## 编码规范

### 后端

- 实体类继承 `BaseDO`（包含 createUser/createTime/updateUser/updateTime/deleted，框架自动填充）
- Service 继承 `ServiceImpl<Mapper, DO>`
- 写操作加 `@Transactional(rollbackFor = Exception.class)`
- 逻辑删除：MyBatis Plus 自动处理，但手写 `QueryWrapper` 查询须显式加 `.eq("deleted", 0)`
- 异常：用 `BusinessException`（业务错误）或 `BadRequestException`（参数错误）
- 权限标识格式：`review:模块:资源:操作`（如 `review:template:process:query`）

### 前端

- 页面列表用 `GiTable` + `GiForm` + `useTable()` hook 组合
- 权限控制用 `v-permission` 指令或 `has.hasPermOr()`
- 从编辑页返回列表时通过路由 query 参数 `t` 触发刷新（`watch(() => route.query.t, ...)`）
- 表单设计器页面用路由跳转（`/review/template/form/designer/:id?`），非弹窗

### 数据库变更

- Liquibase changeSet ID 格式：`zjx:[描述]-[序号]`
- review 模板变更文件位于 `db/changelog/mysql/plugin/review/` 和 `plugin/form/`

---

## 待实现模块（TODO）

1. **项目类型（type）**：组合 process_template + management_template + form_template 的配置
2. **项目管理（project）**：使用 `StateFlowEngine` 驱动，实现完整申请审批流程
3. **任务管理（task）**：对应各 `TaskType` 的审批任务
4. **数据权限**：各 Service `page()` 中的 TODO 标注处需补充 dept_id 过滤逻辑
5. **引用检查**：删除/修改模板前检查是否被 project_type 引用

---

## 环境要求

- JDK 17+，Maven 3.8+，MySQL 8.0+，Redis 6.0+
- Node.js 18+，pnpm 8+
