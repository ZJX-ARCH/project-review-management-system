# ContiNew Starter - 企业级 Spring Boot Starter 集合

[根目录](../CLAUDE.md) > **continew-starter**

---

## 变更记录 (Changelog)

### 2026-01-16 11:48:58
- 初始化 ContiNew Starter 基础文档
- 扫描并记录 Starter 模块结构

---

## 模块职责

`ContiNew Starter`（Continue New Starter）是一套企业级 Spring Boot Starter 增强工具集，基于"约定优于配置"的理念，进一步精简常规配置，提供完整的配置解决方案，帮助开发人员更快速地将常用第三方库或工具集成到 Spring Boot Web 应用程序中。

### 核心理念

- **开箱即用**：引入依赖后即可使用，无需复杂配置
- **约定优于配置**：提供合理的默认配置，减少样板代码
- **企业实践验证**：所有 Starter 均来自 ContiNew Admin 项目的实践沉淀
- **灵活可配置**：虽然提供默认配置，但支持灵活自定义

### 起源

ContiNew Starter 源自 ContiNew Admin 后台管理框架项目。在 ContiNew Admin 2.x 版本时，作者将项目中的通用基础能力进行了抽离并深度优化，使其可以独立使用，也可以在 ContiNew Admin 中使用。

---

## 入口与启动

ContiNew Starter 本身是一个库项目，不提供可执行入口。它通过 Maven 依赖的方式被其他 Spring Boot 项目引用。

### 使用方式

#### 1. 添加父 POM（推荐）

```xml
<parent>
    <groupId>top.continew.starter</groupId>
    <artifactId>continew-starter</artifactId>
    <version>2.15.0</version>
</parent>
```

#### 2. 添加 BOM 依赖管理

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>top.continew.starter</groupId>
            <artifactId>continew-starter-bom</artifactId>
            <version>2.15.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

#### 3. 引入所需 Starter

```xml
<dependency>
    <groupId>top.continew.starter</groupId>
    <artifactId>continew-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>top.continew.starter</groupId>
    <artifactId>continew-starter-data-mp</artifactId>
</dependency>

<!-- 更多 Starter... -->
```

---

## 对外接口

### Starter 模块列表

| Starter 模块 | 功能描述 | 关键依赖 |
|-------------|---------|---------|
| **continew-starter-core** | 核心工具类、自动配置、应用信息 | Hutool、Spring Boot |
| **continew-starter-web** | Web 增强（全局响应、异常处理、跨域） | Spring Web |
| **continew-starter-json-jackson** | Jackson 序列化增强 | Jackson |
| **continew-starter-api-doc** | 接口文档（Swagger/Knife4j） | SpringDoc、Knife4j |
| **continew-starter-validation** | 参数校验增强 | Hibernate Validator |
| **continew-starter-data-mp** | MyBatis Plus 增强 | MyBatis Plus、CosId |
| **continew-starter-data-mf** | MyBatis Flex 增强 | MyBatis Flex |
| **continew-starter-cache-redisson** | Redisson 缓存与分布式锁 | Redisson |
| **continew-starter-cache-jetcache** | JetCache 多级缓存 | JetCache |
| **continew-starter-cache-springcache** | Spring Cache 增强 | Spring Cache |
| **continew-starter-auth-satoken** | Sa-Token 认证授权增强 | Sa-Token |
| **continew-starter-auth-justauth** | 第三方登录（JustAuth） | JustAuth |
| **continew-starter-log-aop** | 操作日志（AOP 方式） | AOP |
| **continew-starter-log-interceptor** | 操作日志（拦截器方式） | Web Interceptor |
| **continew-starter-storage** | 文件存储抽象（本地/S3/OSS） | X-File-Storage |
| **continew-starter-messaging-mail** | 邮件发送 | Spring Mail |
| **continew-starter-messaging-websocket** | WebSocket 支持 | Spring WebSocket |
| **continew-starter-messaging-mqtt** | MQTT 消息 | Eclipse Paho |
| **continew-starter-captcha-graphic** | 图形验证码 | EasyCaptcha |
| **continew-starter-captcha-behavior** | 行为验证码 | AJ-Captcha |
| **continew-starter-ratelimiter** | 限流器 | Redisson |
| **continew-starter-idempotent** | 幂等性 | Redisson |
| **continew-starter-trace** | 链路追踪（TLog） | TLog |
| **continew-starter-excel-poi** | Excel 导入导出（POI） | Apache POI |
| **continew-starter-excel-fastexcel** | Excel 导入导出（FastExcel） | FastExcel |
| **continew-starter-encrypt-*** | 加密（API/字段/密码） | Hutool Crypto |
| **continew-starter-security-*** | 安全（XSS/脱敏/敏感词） | - |
| **continew-starter-license-*** | 许可证管理 | TrueLicense |
| **continew-starter-extension-crud** | CRUD 增强（代码生成友好） | - |
| **continew-starter-extension-tenant** | 多租户支持 | MyBatis Plus/Flex |
| **continew-starter-extension-datapermission** | 数据权限 | MyBatis Plus/Flex |

---

## 关键依赖与配置

### 技术栈要求

- **Java**：17+
- **Spring Boot**：3.x
- **Maven**：3.8+

### 依赖管理

ContiNew Starter 使用 `continew-starter-dependencies` 统一管理第三方依赖版本，确保版本兼容性。

### 配置前缀

所有 Starter 的配置都以 `continew-starter` 开头，例如：

```yaml
# Web 响应配置
continew-starter.web.response:
  default-success-code: 0
  default-error-code: 1

# MyBatis Plus 扩展配置
continew-starter.data-mp:
  mapper-package: top.continew.admin.**.mapper
  id-generator:
    type: COSID

# 租户配置
continew-starter.tenant:
  enabled: true
  isolation-level: LINE
```

---

## 数据模型

ContiNew Starter 本身不定义业务数据模型，但提供了一些基础模型与注解：

### 基础枚举

- **BaseEnum**：枚举基类，支持代码与描述
- **DatabaseType**：数据库类型枚举（MySQL、PostgreSQL 等）
- **QueryType**：查询类型枚举（等于、模糊、范围等）

### 常用注解

- **@Query**：查询条件注解（用于动态查询）
- **@DataPermission**：数据权限注解
- **@RateLimiter**：限流注解
- **@Idempotent**：幂等性注解
- **@Log**：操作日志注解

---

## 测试与质量

### 代码格式化

使用 Spotless + P3C 代码风格：

```xml
<plugin>
    <groupId>com.diffplug.spotless</groupId>
    <artifactId>spotless-maven-plugin</artifactId>
    <configuration>
        <java>
            <removeUnusedImports/>
            <eclipse>
                <file>.style/p3c-codestyle.xml</file>
            </eclipse>
            <licenseHeader>
                <file>.style/license-header</file>
            </licenseHeader>
        </java>
    </configuration>
</plugin>
```

### License 管理

所有代码文件头部自动添加 LGPL 许可证声明。

---

## 常见问题 (FAQ)

### Q1：ContiNew Starter 与 Spring Boot Starter 有什么区别？

ContiNew Starter 是在 Spring Boot Starter 的基础上进行了进一步的封装和增强，提供了更符合企业开发需求的默认配置和工具类。

### Q2：如何查看某个 Starter 的详细使用文档？

访问 [ContiNew Starter 官方文档](https://continew.top/docs/starter/) 查看详细的使用指南。

### Q3：可以单独使用某个 Starter 吗？

可以。每个 Starter 都是独立的，可以单独引入使用，不需要引入整个 ContiNew Starter 父 POM。

### Q4：如何贡献代码或反馈问题？

访问 GitHub 仓库提交 Issue 或 Pull Request：https://github.com/continew-org/continew-starter

---

## 相关文件清单

### 核心配置

- `pom.xml` - Maven 聚合项目配置
- `continew-starter-dependencies/pom.xml` - 依赖版本管理
- `continew-starter-bom/pom.xml` - BOM 依赖清单
- `.style/p3c-codestyle.xml` - 代码格式规范
- `.style/license-header` - 许可证头模板

### 主要模块目录

```
continew-starter/
├── continew-starter-dependencies/    # 依赖版本管理
├── continew-starter-bom/             # BOM 依赖清单
├── continew-starter-core/            # 核心工具
├── continew-starter-web/             # Web 增强
├── continew-starter-json/            # JSON 处理
├── continew-starter-api-doc/         # 接口文档
├── continew-starter-validation/      # 参数校验
├── continew-starter-data/            # 数据访问
│   ├── continew-starter-data-core/
│   ├── continew-starter-data-mp/
│   └── continew-starter-data-mf/
├── continew-starter-cache/           # 缓存
│   ├── continew-starter-cache-redisson/
│   ├── continew-starter-cache-jetcache/
│   └── continew-starter-cache-springcache/
├── continew-starter-auth/            # 认证授权
│   ├── continew-starter-auth-satoken/
│   └── continew-starter-auth-justauth/
├── continew-starter-log/             # 日志
├── continew-starter-storage/         # 文件存储
├── continew-starter-messaging/       # 消息
├── continew-starter-captcha/         # 验证码
├── continew-starter-ratelimiter/     # 限流
├── continew-starter-idempotent/      # 幂等性
├── continew-starter-trace/           # 链路追踪
├── continew-starter-excel/           # Excel
├── continew-starter-encrypt/         # 加密
├── continew-starter-security/        # 安全
├── continew-starter-license/         # 许可证
└── continew-starter-extension/       # 扩展能力
    ├── continew-starter-extension-crud/
    ├── continew-starter-extension-tenant/
    └── continew-starter-extension-datapermission/
```

---

## 许可证

ContiNew Starter 使用 **GNU LGPL** 许可证，允许商业使用，但修改后需开源贡献。

---

## 相关链接

- **项目地址**：https://github.com/continew-org/continew-starter
- **在线文档**：https://continew.top/docs/starter/
- **作者**：Charles7c (charles7c@126.com)

---

*此文档由 AI 自动生成，最后更新于 2026-01-16 11:48:58*
