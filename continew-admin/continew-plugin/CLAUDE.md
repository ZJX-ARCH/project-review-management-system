# continew-plugin - 插件模块集合

[根目录](../../CLAUDE.md) > [continew-admin](../CLAUDE.md) > **continew-plugin**

---

## 模块职责

`continew-plugin` 是插件模块的聚合项目，包含代码生成器、租户管理、任务调度、能力开放等扩展功能模块。这些模块设计为可插拔的插件，未来将改造为独立插件。

---

## 子模块列表

### 1. continew-plugin-generator - 代码生成器

**路径**：`continew-plugin-generator`

**功能**：
- 根据数据库表自动生成前后端代码（Controller、Service、Mapper、Entity、Vue 页面）
- 支持自定义生成模板
- 支持字段配置（显示/隐藏、查询条件、表单类型等）

**Controller**：
- `GeneratorController` - 代码生成配置与生成接口

---

### 2. continew-plugin-tenant - 租户管理

**路径**：`continew-plugin-tenant`

**功能**：
- 租户信息管理（增删改查）
- 租户套餐管理（套餐与菜单关联）
- 租户隔离配置（行级隔离）

**Controller**：
- `TenantController` - 租户管理
- `PackageController` - 租户套餐管理
- `CommonController` - 公共接口（套餐列表等）

**数据表**：
- `tenant` - 租户表
- `tenant_package` - 租户套餐表
- `tenant_package_menu` - 租户套餐与菜单关联表

**核心实现**：
- `TenantApiImpl` - 租户 API 实现（供 ContiNew Starter Tenant 调用）
- `PackageMenuApiImpl` - 套餐菜单 API 实现

---

### 3. continew-plugin-schedule - 任务调度

**路径**：`continew-plugin-schedule`

**功能**：
- 定时任务管理（创建、编辑、启用/禁用、手动执行）
- 任务执行日志查询
- 集成 PowerJob 任务调度引擎

**Controller**：
- `JobController` - 任务管理
- `JobLogController` - 任务日志查询
- `DefaultController` - 默认任务执行入口

**依赖**：
- PowerJob Server（需单独部署）或使用内置的 `continew-extension-schedule-server`

**配置**：
```yaml
powerjob:
  worker:
    enabled: true
    app-name: continew-admin
    server-address: 127.0.0.1:7700
```

---

### 4. continew-plugin-open - 能力开放

**路径**：`continew-plugin-open`

**功能**：
- 应用管理（注册、编辑、启用/禁用）
- API 接口开放与权限控制
- 请求签名验证

**Controller**：
- `AppController` - 应用管理

**核心实现**：
- `OpenApiSignTemplate` - 开放平台签名模板
- `SaCheckPermissionHandler` - 权限验证处理器

**数据表**：
- `sys_app` - 应用表

---

## 插件依赖关系

```
continew-plugin-generator
  └── continew-common

continew-plugin-tenant
  └── continew-common

continew-plugin-schedule
  ├── continew-common
  └── PowerJob Worker

continew-plugin-open
  └── continew-common
```

---

## 插件启用与禁用

### 禁用插件

在 `continew-admin/pom.xml` 中注释掉对应的 `<module>` 标签：

```xml
<modules>
    <!-- <module>continew-plugin/continew-plugin-generator</module> -->
    <!-- <module>continew-plugin/continew-plugin-tenant</module> -->
</modules>
```

同时在 `continew-server/pom.xml` 中移除对应的依赖。

---

## 常见问题

### Q1：如何自定义代码生成模板？

修改 `continew-plugin-generator` 模块中的模板文件（位于 `resources/templates/`）。

### Q2：租户隔离如何实现？

通过 MyBatis Plus 拦截器自动在 SQL 中添加 `tenant_id` 条件，无需手动处理。

### Q3：任务调度如何配置？

需要先启动 PowerJob Server 或使用内置的 `continew-extension-schedule-server`，然后在系统中配置任务。

---

*此文档由 AI 自动生成，最后更新于 2026-01-16 11:48:58*
