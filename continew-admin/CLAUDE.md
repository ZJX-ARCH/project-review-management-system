# ContiNew Admin - 后端主项目

[根目录](../CLAUDE.md) > **continew-admin**

---

## 变更记录 (Changelog)

### 2026-01-16 11:48:58
- 初始化模块文档
- 扫描并记录子模块结构

---

## 模块职责

`continew-admin` 是项目的后端主模块，采用 Maven 多模块结构组织代码。它是一个聚合项目（`packaging: pom`），本身不包含业务代码，而是将各个子模块组合在一起统一管理依赖版本、构建配置和发布流程。

### 核心职责

- **统一依赖管理**：通过父 POM 继承 `continew-starter` 并管理子模块依赖版本
- **模块聚合**：将启动模块、业务模块、插件模块、公共模块等组合成一个完整的后端系统
- **版本控制**：使用 `${revision}` 统一管理项目版本号（当前为 `4.2.0-SNAPSHOT`）
- **构建配置**：配置 Maven 编译插件、单元测试插件、版本号扁平化插件等

---

## 入口与启动

### 主启动类

- **路径**：`continew-server/src/main/java/top/continew/admin/ContiNewAdminApplication.java`
- **端口**：默认使用 Spring Boot 默认端口（可在 `application.yml` 中配置）
- **上下文路径**：`/`（根路径）

### 启动流程

1. `ContiNewAdminApplication.main()` 方法启动 Spring Boot 应用
2. 设置 `continew-starter.version` 系统属性
3. 加载配置文件：
   - `application.yml`（主配置）
   - `application-dev.yml`（开发环境）
   - `application-generator.yml`（代码生成器配置）
4. 初始化核心组件：
   - Sa-Token 认证授权
   - JetCache 缓存
   - MyBatis Plus 数据访问
   - X-File-Storage 文件存储
   - OpenFeign 服务调用
5. 启动成功后打印访问地址与文档地址

### 启动输出示例

```
--------------------------------------------------------
ContiNew Admin server started successfully.
ContiNew Starter: v2.15.0 (Spring Boot: v3.x.x)
当前版本: v4.2.0-SNAPSHOT (Profile: dev)
服务地址: localhost:8000
接口文档: localhost:8000/doc.html
--------------------------------------------------------
```

---

## 对外接口

### API 文档

- **Swagger UI**：`http://localhost:8000/swagger-ui`
- **Knife4j**：`http://localhost:8000/doc.html`（推荐）
- **OpenAPI JSON**：`http://localhost:8000/v3/api-docs`

### 主要 API 模块

| API 模块 | 路径前缀 | 职责 | Controller 数量 |
|---------|---------|------|----------------|
| 认证授权 | `/auth` | 用户登录、登出、路由、在线用户管理 | 2 |
| 系统管理 | `/system` | 用户、角色、菜单、部门、字典、配置等 | 18 |
| 系统监控 | `/monitor` | 日志查询、在线用户监控 | - |
| 租户管理 | `/tenant` | 租户、租户套餐管理 | 3 |
| 能力开放 | `/open` | 应用管理、API 接口开放 | 1 |
| 任务调度 | `/schedule` | 定时任务、任务日志管理 | 3 |
| 代码生成 | `/generator` | 代码生成器配置与生成 | 1 |
| 仪表盘 | `/dashboard` | 数据统计与图表 | 1 |
| 验证码 | `/captcha` | 图形/行为验证码生成 | 1 |

---

## 关键依赖与配置

### 父 POM 依赖

```xml
<parent>
    <groupId>top.continew.starter</groupId>
    <artifactId>continew-starter</artifactId>
    <version>2.15.0</version>
</parent>
```

### 全局依赖

- **Hutool**：Java 工具类库（`cn.hutool:hutool-all`）
- **Lombok**：简化 JavaBean 编写（`org.projectlombok:lombok`）

### 子模块依赖关系

```
continew-server (可执行模块)
  ├── continew-system (系统管理)
  ├── continew-plugin-* (各插件模块)
  └── continew-common (公共模块)

continew-system (业务核心)
  └── continew-common

continew-plugin-* (插件)
  └── continew-common

continew-common (基础)
  └── continew-starter-*
```

### 核心配置文件

| 文件路径 | 用途 |
|---------|------|
| `continew-server/src/main/resources/config/application.yml` | 主配置文件 |
| `continew-server/src/main/resources/config/application-dev.yml` | 开发环境配置 |
| `continew-server/src/main/resources/config/application-prod.yml` | 生产环境配置 |
| `continew-server/src/main/resources/config/application-generator.yml` | 代码生成器配置 |
| `continew-server/src/main/resources/logback-spring.xml` | 日志配置 |

---

## 数据模型

### 数据库支持

- **MySQL 8.0+**（推荐，默认配置）
- **PostgreSQL 13+**（通过修改 `database-id` 切换）

### 表结构组织

| 表前缀 | 所属模块 | 示例表名 |
|--------|---------|---------|
| `sys_` | 系统管理 | `sys_user`、`sys_role`、`sys_menu`、`sys_dept` |
| `tenant_` | 租户管理 | `tenant`、`tenant_package` |
| `gen_` | 代码生成 | `gen_config`、`gen_field_config` |
| `schedule_` | 任务调度 | `schedule_job`、`schedule_job_log` |

### ORM 框架

- **MyBatis Plus**：增强的 MyBatis 框架
- **主键策略**：`ASSIGN_ID`（CosId 雪花算法）
- **逻辑删除**：
  - 字段名：`deleted`
  - 未删除值：`0`
  - 已删除值：`id`（解决唯一索引冲突）

### 租户隔离

- **隔离级别**：行级（`LINE`）
- **租户 ID 键名**：`X-Tenant-Id`（请求头）
- **默认租户 ID**：`0`（超级管理员租户）
- **忽略表**：租户表、菜单表、字典表、系统配置表等

---

## 测试与质量

### 测试配置

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <skip>true</skip> <!-- 当前跳过单元测试 -->
    </configuration>
</plugin>
```

### 测试文件

- **集成测试**：`continew-server/src/test/java/top/continew/admin/ContiNewAdminApplicationTests.java`

### 代码质量工具

- **Spotless**：自动格式化代码（P3C 阿里巴巴代码规范）
- **License Header**：自动添加许可证头

---

## 常见问题 (FAQ)

### Q1：如何切换数据库类型？

编辑 `continew-server/src/main/resources/config/application.yml`：

```yaml
mybatis-plus:
  configuration:
    database-id: pgsql  # 从 mysql 改为 pgsql
  extension:
    pagination:
      db-type: POSTGRESQL  # 从 MYSQL 改为 POSTGRESQL
```

### Q2：如何禁用某个插件模块？

在 `pom.xml` 中注释掉对应的 `<module>` 标签，并在 `continew-server/pom.xml` 中移除该模块的依赖。

### Q3：如何修改端口号？

编辑 `application-dev.yml`：

```yaml
server:
  port: 8080  # 修改为你需要的端口
```

### Q4：如何配置多环境？

创建新的配置文件 `application-{env}.yml`，然后在启动时指定：

```bash
java -jar continew-server.jar --spring.profiles.active={env}
```

---

## 相关文件清单

### 核心配置

- `pom.xml` - Maven 聚合项目配置
- `continew-server/pom.xml` - 启动模块配置
- `continew-server/src/main/java/top/continew/admin/ContiNewAdminApplication.java` - 主启动类
- `continew-server/src/main/resources/config/application*.yml` - 应用配置文件

### 子模块目录

- `continew-server/` - 启动入口模块 [查看文档](./continew-server/CLAUDE.md)
- `continew-system/` - 系统管理模块 [查看文档](./continew-system/CLAUDE.md)
- `continew-plugin/` - 插件模块集合 [查看文档](./continew-plugin/CLAUDE.md)
- `continew-common/` - 公共模块 [查看文档](./continew-common/CLAUDE.md)
- `continew-extension/` - 扩展模块 [查看文档](./continew-extension/CLAUDE.md)

---

## 变更记录 (Changelog)

*模块级别的变更记录将在后续更新时补充*

---

*此文档由 AI 自动生成，最后更新于 2026-01-16 11:48:58*
