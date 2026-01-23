# ContiNew Starter

## 简介

ContiNew Starter（Continue New Starter）基于“约定优于配置”理念，进一步精简常规配置，提供完整的配置解决方案，帮助开发人员更快速地将常用第三方库或工具集成到 Spring Boot Web 应用程序中。

ContiNew Starter 封装了一系列经过企业实践验证的依赖包（如 MyBatis-Plus、SaToken），可轻松集成到应用中，减少开发人员手动引入依赖及配置的工作量，为 Spring Boot Web 项目的灵活快速构建提供支持。

> ContiNew Starter 源自 [ContiNew Admin](https://github.com/continew-org/continew-admin) 后台管理框架项目。随着 ContiNew Admin 的发展，作者发现初学者需要关注过多的脚手架通用基础能力，且在新项目中复用这些能力时存在迁移困难的问题。于是，ContiNew Starter 应运而生，作者在 ContiNew Admin 2.x 版本时，将项目中的通用基础能力进行了抽离并深度优化。这样，无论是在 ContiNew Admin 中使用，还是单独使用这些基础能力，都可以更加轻松。

## 解决痛点

在开发一个 Java Web 项目之前，我们可能需要做如下准备工作：

1. 引入 Spring Boot 父项目进行版本锁定（无 Spring 不 Java）。
2. 引入 Spring Boot Web 依赖。
3. 根据需求引入不同组件的 Starter。
4. 针对引入的 Starter 进行配置（查阅文档或通过搜索引擎查找常用配置）。 
   - 编写 Java 配置。 
   - 编写 application.yml 配置。
5. 编写各类全局处理器。
6. 开始业务开发。

在 Spring Boot “约定优于配置” 理念的帮助下，我们开发一个 Spring Java Web 程序已经简化到了不可思议的程度，而且很多设计良好的组件 Starter 提供了极大的扩展性，提供了非常多的配置，给使用者最大的可行性，当你需要处理一些自定义场景时，这些配置简直是太过方便。

然而，高度扩展性也带来了配置复杂性，新手用户在初次使用组件时往往需要花费大量精力在配置上。因此，各种脚手架项目应运而生，你可能会想，这基础配置关脚手架项目什么事？**脚手架项目的作用不仅仅是提供一系列通用基础功能，更多的是提供了一种通用的解决方案，无论是针对所使用组件的配置，还是实现的某个功能的设计，亦或是开发规范** 。即使是初学者，把脚手架项目拿过来，只需要删减不需要的功能，修改品牌元素，就可以继续在其基础上进行开发一个成熟的项目。

ContiNew Starter 将脚手架项目中的通用基础配置进行封装与深度优化，从企业实践角度精简配置，使新项目或已有项目在使用这些组件时更加便捷。

## 像数1，2，3一样容易

1.在项目 pom.xml 中锁定版本（**以下两种方式任选其一**）

方式一：如您使用的是 Spring Boot Parent 的方式，则替换 Spring Boot Parent 为 ContiNew Starter。

```xml
<parent>
    <groupId>top.continew.starter</groupId>
    <artifactId>continew-starter</artifactId>
    <version>{latest-version}</version>
</parent>
```

方式二：如您使用的是引入 Spring Boot Dependencies 的方式，则替换 Spring Boot Dependencies 为 ContiNew Starter Dependencies

```xml
<properties>
    <java.version>17</java.version>
    <maven.compiler.source>${java.version}</maven.compiler.source>
    <maven.compiler.target>${java.version}</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>

<dependencyManagement>
    <dependencies>
        <!-- ContiNew Starter Dependencies -->
        <dependency>
            <groupId>top.continew.starter</groupId>
            <artifactId>continew-starter-dependencies</artifactId>
            <version>{latest-version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

2.在项目 pom.xml 中引入所需模块依赖

```xml
<dependencies>
    <!-- Web 模块 -->
    <dependency>
        <groupId>top.continew.starter</groupId>
        <artifactId>continew-starter-web</artifactId>
    </dependency>
</dependencies>
```

3.在 application.yml 中根据引入模块，添加所需配置

示例：跨域配置

```yaml
--- ### 跨域配置
continew-starter.web:
  cors:
    enabled: true
    # 配置允许跨域的域名
    allowed-origins: '*'
    # 配置允许跨域的请求方式
    allowed-methods: '*'
    # 配置允许跨域的请求头
    allowed-headers: '*'
    # 配置允许跨域的响应头
    exposed-headers: '*'
```

## 模块结构

```
continew-starter
├─ continew-starter-core（核心模块：包含线程池等自动配置）
├─ continew-starter-json（JSON 模块）
│  └─ continew-starter-json-jackson
├─ continew-starter-api-doc（接口文档模块：Spring Doc + Knife4j）
├─ continew-starter-validation（校验模块：Hibernate Validator）
├─ continew-starter-web（Web 开发模块：包含跨域、全局异常+响应、链路追踪等自动配置）
├─ continew-starter-cache（缓存模块）
│  ├─ continew-starter-cache-redisson（Redisson）
│  ├─ continew-starter-cache-jetcache（JetCache 多级缓存）
│  └─ continew-starter-cache-springcache（Spring 缓存）
├─ continew-starter-auth（认证模块）
│  ├─ continew-starter-auth-satoken（国产轻量认证鉴权）
│  └─ continew-starter-auth-justauth（第三方登录）
├─ continew-starter-data（数据访问模块）
│  ├─ continew-starter-data-core（核心模块）
│  ├─ continew-starter-data-mp（MyBatis Plus）
│  └─ continew-starter-data-mf（MyBatis Flex）
├─ continew-starter-encrypt（加密模块）
│  ├─ continew-starter-encrypt-core（核心模块）
│  ├─ continew-starter-encrypt-field（字段加密）
│  └─ continew-starter-encrypt-api（API 加密）
│  └─ continew-starter-encrypt-password-encoder（密码编码器）
├─ continew-starter-security（安全模块）
│  ├─ continew-starter-security-mask（脱敏：JSON 数据脱敏）
│  ├─ continew-starter-security-xss（XSS 过滤）
│  └─ continew-starter-security-sensitivewords（敏感词）
├─ continew-starter-ratelimiter（限流模块）
├─ continew-starter-idempotent（幂等模块）
├─ continew-starter-trace（链路追踪模块）
├─ continew-starter-captcha（验证码模块）
│  ├─ continew-starter-captcha-graphic（静态验证码）
│  └─ continew-starter-captcha-behavior（动态验证码）
├─ continew-starter-messaging（消息模块）
│  ├─ continew-starter-messaging-mail（邮件）
│  └─ continew-starter-messaging-websocket（WebSocket）
├─ continew-starter-log（日志模块）
│  ├─ continew-starter-log-core（核心模块）
│  ├─ continew-starter-log-aop（基于 AOP 实现）
│  └─ continew-starter-log-interceptor（基于拦截器实现（Spring Boot Actuator HttpTrace 增强版））
├─ continew-starter-excel（Excel 文件处理模块）
│  ├─ continew-starter-excel-core（核心模块）
│  ├─ continew-starter-excel-fastexcel（FastExcel）
│  └─ continew-starter-excel-poi（POI）
├─ continew-starter-storage（存储模块）
│  └─ continew-starter-storage-local（本地存储）
├─ continew-starter-license（License 模块）
│  ├─ continew-starter-license-core（核心模块）
│  ├─ continew-starter-license-generator（License 生成器）
│  └─ continew-starter-license-verifier（License 校验器）
└─ continew-starter-extension（扩展模块）
   ├─ continew-starter-extension-datapermission（数据权限模块）
   │  ├─ continew-starter-extension-datapermission-core（核心模块）
   │  └─ continew-starter-extension-datapermission-mp（MyBatis Plus）
   ├─ continew-starter-extension-tenant（租户模块）
   │  ├─ continew-starter-extension-tenant-core（核心模块）
   │  └─ continew-starter-extension-tenant-mp（MyBatis Plus）
   └─ continew-starter-extension-crud（CRUD 模块）
      ├─ continew-starter-extension-crud-core（核心模块）
      ├─ continew-starter-extension-crud-mp（MyBatis Plus）
      └─ continew-starter-extension-crud-mf（MyBatis Flex）
```

