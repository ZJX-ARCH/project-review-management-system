# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

---

## 模块概述

`continew-plugin-review` 是项目评审管理系统的核心后端模块，实现了从项目申请到归档的完整生命周期管理。

**已完成：** 流程模板管理（process/management）、表单模板管理（form）
**待实现：** 项目类型配置（type）、项目管理（project）、任务管理（task）等

---

## 子模块结构

```
continew-plugin-review/
├── continew-plugin-review-common/   # 共享枚举、引擎、工具
│   ├── enums/                       # ProjectStatus, ApprovalRule, TaskType, ModificationStrategy
│   ├── engine/                      # StateFlowEngine（状态流转）, TimeoutCheckEngine（超时检查）
│   └── constant/                    # FileStorageConstants
├── continew-plugin-review-template/ # 流程模板管理（process + management）
│   ├── controller/                  # ProcessTemplateController, ManagementTemplateController
│   ├── model/entity/                # ProcessTemplateDO, ManagementTemplateDO, ManagementStageDO
│   └── service/impl/                # ProcessTemplateServiceImpl, ManagementTemplateServiceImpl
└── continew-plugin-review-form/     # 表单模板管理
    ├── controller/                  # FormTemplateController
    ├── model/entity/                # FormTemplateDO, FormFieldDO, FormTemplateFileDO
    └── service/impl/                # FormTemplateServiceImpl
```

---

## 数据权限设计（重要）

**核心机制：** 用户可属于多个部门，每个部门有不同角色，同一时刻只操作当前部门的数据。

- 所有模板表都有 `dept_id` 字段（后续 alter table 添加，见 `review_template_add_dept_id.sql`）
- 查询时根据当前用户所在部门的 `dept_id` 过滤数据
- `ProcessTemplateDO`、`ManagementTemplateDO`、`FormTemplateDO` 均有 `Long deptId` 字段
- **目前 page() 方法中数据权限过滤尚未实现**（标注了 TODO），需在实现 type/project 模块时补充

---

## 项目状态机（28个状态）

`ProjectStatus` 枚举定义了完整的状态流转，`StateFlowEngine` 负责规则验证：

```
草稿(1) → 已提交(2) → 审核中(10) → 审核通过(11) → 评审中(20) → 评审通过(21) → 决策中(30) → 决策通过(31) → 项目执行中(50) → 项目验收中(53) → 验收通过(54) → 已归档_已完成(90)
```

关键规则：
- 驳回 → 已终止(40) → 已归档_已取消(92)
- 需修改 → 回到对应 `*中` 状态
- 等待中 → 回到对应 `*中` 状态
- 0-49 = 评审阶段，50-89 = 执行阶段，90+ = 归档阶段

---

## 评审流程模板（Process Template）

**数据表：** `review_process_template` + `review_process_template_round_name`

**设计要点：**
- 流程模板定义审核/评审/决策各多少轮（auditRounds/reviewRounds/decisionRounds）
- 每轮次可配置名称（RoundName），存于子表 `review_process_template_round_name`
- 轮次名称数量必须与轮次配置总数严格匹配，序号必须从1开始连续
- 模板编码格式：`PROC_` 前缀 + 大写字母/数字/下划线，自动生成时使用时间戳
- 新建时默认**启用**状态

---

## 管理流程模板（Management Template）

**数据表：** `review_management_template` + `review_management_stage`

**设计要点：**
- 管理模板定义项目执行的阶段（Stage），阶段类型为 KICKOFF/EXECUTION/ACCEPTANCE
- 模板层只定义阶段结构（名称、类型、顺序、是否必须），**计划完成天数由类型层配置**
- 子表使用 DELETE + INSERT 策略更新（无软删除，直接物理删除后重建）
- 模板编码格式：`MGMT_` 前缀

---

## 表单模板（Form Template）

**数据表：** `review_form_template` + `review_form_field` + `review_form_template_file`

**设计要点：**
- 模板类型（TemplateTypeEnum）：申请(1)/审核(2)/评审(3)/决策(4)/立项阶段(5)/执行阶段(6)/验收阶段(7)
- 字段类型（FieldTypeEnum）：TEXT/TEXTAREA/NUMBER/DATE/SELECT/RADIO/CHECKBOX/FILE/FILE_TEMPLATE/TABLE/SCORE
- `layoutConfig` 和 `fieldConfig` 存储为 JSON 字符串，读取时手动解析（`ObjectMapper.readTree()`）
- 字段更新使用 DELETE + INSERT 策略
- 新建时默认**禁用**状态（与流程模板相反，需手动启用）
- 文件上传路径由 `FileStorageConstants.REVIEW_FORM_TEMPLATE_PATH` 定义

---

## Service 层规范

参照 `ProcessTemplateServiceImpl` 和 `ManagementTemplateServiceImpl`：

1. **继承** `ServiceImpl<Mapper, DO>` (来自 `continew-starter-data`)
2. **事务**：写操作加 `@Transactional(rollbackFor = Exception.class)`
3. **唯一性校验**：编码和名称都需检查唯一性（排除自身）
4. **自动填充**：`createUser/createTime/updateUser/updateTime` 由框架自动填充，不要手动设置
5. **逻辑删除**：`deleted` 字段由 MyBatis Plus 自动处理，但部分 QueryWrapper 查询需显式加 `.eq("deleted", 0)`
6. **分页**：使用 `Page<DO>` + `PageQuery` 返回 `PageResp<Resp>`
7. **TODO 预留**：被其他模块引用检查、数据权限过滤均有 TODO 注释标记

---

## Controller 层规范

参照 `ProcessTemplateController`：

- `@SaCheckPermission` 权限标识格式：`review:模块:资源:操作`（如 `review:template:process:query`）
- 删除接口用 `@RequestBody List<Long> ids`（支持批量）
- 响应统一使用 `R<T>` 包装，成功返回 `R.ok(data)`

---

## 数据库变更规范

- 使用 Liquibase 管理，文件位于 `continew-server/src/main/resources/db/changelog/mysql/plugin/`
- review 模块目录：`plugin/review/` 和 `plugin/form/`
- 文件命名：`review_[表名]_[描述].sql`
- changeset ID 格式：`zjx:[描述]-[序号]`

---

## 待实现模块（TODO）

按优先级排列：
1. **项目类型（type）** - 组合 process_template + management_template + form_template 的配置层
2. **项目管理（project）** - 使用 StateFlowEngine 驱动状态流转的核心模块
3. **任务管理（task）** - 对应 TaskType 枚举的各类审批任务
4. **数据权限过滤** - ProcessTemplateServiceImpl.page() 中的 TODO 需要完成
5. **引用检查** - 删除/修改模板前检查是否被 project_type 引用
