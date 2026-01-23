# 系统功能

-   仪表盘：提供工作台、分析页，工作台提供功能快捷导航入口、最新公告、动态；分析页提供全面数据可视化能力
-   个人中心：支持基础信息修改、密码修改、邮箱绑定、手机号绑定（并提供行为验证码、短信限流等安全处理）、第三方账号绑定/解绑（微信登录）、头像裁剪上传
-   消息中心：提供站内信消息统一查看、标记已读、全部已读、删除等功能（目前仅支持系统通知消息）、提供个人公告查看
-   用户管理：管理系统用户，包含新增、修改、删除、导入、导出、重置密码、分配角色等功能
-   角色管理：管理系统用户的功能权限及数据权限，包含新增、修改、删除、分配角色等功能
-   菜单管理：管理系统菜单及按钮权限，支持多级菜单，动态路由，包含新增、修改、删除等功能
-   部门管理：管理系统组织架构，包含新增、修改、删除、导出等功能，以树形列表进行展示
-   通知公告：管理系统公告，支持通知范围（所有人、指定用户）、通知方式（系统消息、登录弹窗）、定时发送、置顶设置
-   文件管理：管理系统文件及文件夹，支持回收站、上传/分片上传、下载、预览（目前支持图片、音视频、PDF、Word、Excel、PPT）、重命名、切换视图（列表、网格）等功能
-   字典管理：管理系统公用数据字典，例如：消息类型。支持字典标签背景色和排序等配置
-   系统配置：
    -   网站配置：提供修改系统标题、Logo、favicon、版权信息等基础配置功能，以方便用户系统与其自身品牌形象保持一致
    -   安全配置：提供密码策略修改，支持丰富的密码策略设定，包括但不限于 `密码有效期`、`密码重复次数`、`密码错误锁定账号次数、时间` 等
    -   登录配置：提供验证码开关等登录相关配置
    -   邮件配置：提供系统发件箱配置，也支持通过配置文件指定
    -   短信配置：提供系统短信服务配置，也支持通过配置文件指定
    -   存储配置：管理文件存储配置，支持本地存储、兼容 S3 协议对象存储
    -   客户端配置：多端（PC端、小程序端等）认证管理，可设置不同的 token 有效期
-   在线用户：管理当前登录用户，可一键踢除下线
-   日志管理：管理系统登录日志、操作日志，支持查看日志详情，包含请求头、响应头等报文信息
-   短信日志：管理系统短信发送日志，支持删除、导出
-   应用管理：管理第三方系统应用 AK、SK，包含新增、修改、删除、查看密钥、重置密钥等功能，支持设置密钥有效期
-   租户管理：管理租户信息，包含新增、修改、删除、分配角色等功能
-   租户套餐：管理租户套餐信息，包含新增、修改、删除、查看等功能
-   任务管理：管理系统定时任务，包含新增、修改、删除、执行功能，支持 Cron（可配置式生成 Cron 表达式） 和固定频率
-   任务日志：管理定时任务执行日志，包含停止、重试指定批次等功能
-   代码生成：提供根据数据库表自动生成相应的前后端 CRUD 代码的功能，支持同步最新表结构及代码生成预览

# 核心技术栈

名称

版本

简介

[Vue](https://vuejs.org/)

3.5.4

渐进式 JavaScript 框架，易学易用，性能出色，适用场景丰富的 Web 前端框架。

[Arco Design](https://arco.design/vue/docs/start)

2.57.0

字节跳动推出的前端 UI 框架，年轻化的色彩和组件设计。

[TypeScript](https://www.typescriptlang.org/zh/)

5.0.4

TypeScript 是微软开发的一个开源的编程语言，通过在 JavaScript 的基础上添加静态类型定义构建而成。

[Vite](https://vite.dev/)

5.1.5

下一代的前端工具链，为开发提供极速响应。

[ContiNew Starter](https://github.com/continew-org/continew-starter)

2.14.0

ContiNew Starter 包含了一系列经过企业实践优化的依赖包（如 MyBatis-Plus、SaToken），可轻松集成到应用中，为开发人员减少手动引入依赖及配置的麻烦，为 Spring Boot Web 项目的灵活快速构建提供支持。

[Spring Boot](https://spring.io/projects/spring-boot)

3.3.12

简化 Spring 应用的初始搭建和开发过程，基于“约定优于配置”的理念，使开发人员不再需要定义样板化的配置。（Spring Boot 3.0 开始，要求 Java 17 作为最低版本）

[Undertow](https://undertow.io/)

2.3.18.Final

采用 Java 开发的灵活的高性能 Web 服务器，提供包括阻塞和基于 NIO 的非堵塞机制。

[Sa-Token + JWT](https://sa-token.dev33.cn/)

1.44.0

轻量级 Java 权限认证框架，让鉴权变得简单、优雅。

[MyBatis Plus](https://baomidou.com/)

3.5.12

MyBatis 的增强工具，在 MyBatis 的基础上只做增强不做改变，简化开发、提高效率。

[dynamic-datasource-spring-boot-starter](https://www.kancloud.cn/tracy5546/dynamic-datasource/2264611)

4.3.1

基于 Spring Boot 的快速集成多数据源的启动器。

Hikari

5.1.0

JDBC 连接池，号称 “史上最快连接池”，SpringBoot 在 2.0 之后，采用的默认数据库连接池就是 Hikari。

[MySQL](https://dev.mysql.com/downloads/mysql/)

8.0.42

体积小、速度快、总体拥有成本低，是最流行的关系型数据库管理系统之一。

[mysql-connector-j](https://dev.mysql.com/doc/connector-j/8.0/en/)

8.3.0

MySQL Java 驱动。

[P6Spy](https://github.com/p6spy/p6spy)

3.9.1

SQL 性能分析组件。

[LiquibaLiquibasese](https://github.com/liquibase/liquibase)

4.27.0

用于管理数据库版本，跟踪、管理和应用数据库变化。

[JetCache](https://github.com/alibaba/jetcache/blob/master/docs/CN/Readme.md)

2.7.8

一个基于 Java 的缓存系统封装，提供统一的 API 和注解来简化缓存的使用。提供了比 SpringCache 更加强大的注解，可以原生的支持 TTL、两级缓存、分布式自动刷新，还提供了 Cache 接口用于手工缓存操作。

[Redisson](https://github.com/redisson/redisson/wiki/Redisson%E9%A1%B9%E7%9B%AE%E4%BB%8B%E7%BB%8D)

3.49.0

不仅仅是一个 Redis Java 客户端，Redisson 充分的利用了 Redis 键值数据库提供的一系列优势，为使用者提供了一系列具有分布式特性的常用工具：分布式锁、限流器等。

[Redis](https://redis.io/)

7.2.8

高性能的 key-value 数据库。

[Snail Job](https://snailjob.opensnail.com/)

1.5.0

灵活，可靠和快速的分布式任务重试和分布式任务调度平台。

[X File Storage](https://x-file-storage.xuyanwu.cn/#/)

2.2.1

一行代码将文件存储到本地、FTP、SFTP、WebDAV、阿里云 OSS、华为云 OBS...等其它兼容 S3 协议的存储平台。

[SMS4J](https://sms4j.com/)

3.3.4

短信聚合框架，轻松集成多家短信服务，解决接入多个短信 SDK 的繁琐流程。

[Just Auth](https://justauth.cn/)

1.16.7

开箱即用的整合第三方登录的开源组件，脱离繁琐的第三方登录 SDK，让登录变得 So easy！

[Fast Excel](https://github.com/fast-excel/fastexcel)

1.2.0

（由原 EasyExcel 作者创建的新项目）一个基于 Java 的、快速、简洁、解决大文件内存溢出的 Excel 处理工具。

[AJ-Captcha](https://ajcaptcha.beliefteam.cn/captcha-doc/)

1.3.0

Java 行为验证码，包含滑动拼图、文字点选两种方式，UI支持弹出和嵌入两种方式。

Easy Captcha

1.6.2

Java 图形验证码，支持 gif、中文、算术等类型，可用于 Java Web、JavaSE 等项目。

[Crane4j](https://createsequence.gitee.io/crane4j-doc/#/)

2.9.0

一个基于注解的，用于完成一切 “根据 A 的 key 值拿到 B，再把 B 的属性映射到 A” 这类需求的字段填充框架。

[SpEL Validator](https://spel-validator.sticki.cn/)

0.5.2-beta

基于 SpEL 的 jakarta.validation-api 扩展增强包。

[CosID](https://cosid.ahoo.me/guide/getting-started.html)

2.13.0

旨在提供通用、灵活、高性能的分布式 ID 生成器。

[Graceful Response](https://doc.feiniaojin.com/graceful-response/home.html)

5.0.4-boot3

一个Spring Boot技术栈下的优雅响应处理组件，可以帮助开发者完成响应数据封装、异常处理、错误码填充等过程，提高开发效率，提高代码质量。

[Knife4j](https://doc.xiaominfo.com/)

4.5.0

前身是 swagger-bootstrap-ui，集 Swagger2 和 OpenAPI3 为一体的增强解决方案。

[OpenFeign](https://springdoc.cn/spring-cloud-openfeign/)

13.5

Spring Cloud OpenFeign 是一种基于 Spring Cloud 的声明式 REST 客户端，它简化了与 HTTP 服务交互的过程。

[Hutool](https://www.hutool.cn/)

5.8.38

小而全的 Java 工具类库，通过静态方法封装，降低相关 API 的学习成本，提高工作效率，使 Java 拥有函数式语言般的优雅，让 Java 语言也可以“甜甜的”。

[Lombok](https://projectlombok.org/)

1.18.36

在 Java 开发过程中用注解的方式，简化了 JavaBean 的编写，避免了冗余和样板式代码，让编写的类更加简洁。

# 项目结构

> [!TIP]后端采用按功能拆分模块的开发方式，下方项目目录结构是按照模块的层次顺序进行介绍的，实际 IDE 中 `continew-common` 模块会因为字母排序原因排在上方。

```
continew-admin├─ continew-server（打包部署模块）│  ├─ src│  │  ├─ main│  │  │  ├─ java/top/continew/admin│  │  │  │  ├─ config （配置）│  │  │  │  │  ├─ log（操作日志配置）│  │  │  │  │  └─ satoken（SaToken 认证配置）│  │  │  │  ├─ controller（通用 API）│  │  │  │  ├─ job （定时任务）│  │  │  │  └─ ContiNewAdminApplication.java（ContiNew Admin 启动程序）│  │  │  └─ resources│  │  │     ├─ config（核心配置目录）│  │  │     │  ├─ application-dev.yml（开发环境配置文件）│  │  │     │  ├─ application-prod.yml（生产环境配置文件）│  │  │     │  └─ application.yml（通用配置文件）│  │  │     ├─ db/changelog（Liquibase 数据脚本配置目录）│  │  │     │  ├─ mysql（MySQL 数据库初始 SQL 脚本目录）│  │  │     │  ├─ postgresql（PostgreSQL 数据库初始 SQL 脚本目录）│  │  │     │  └─ db.changelog-master.yaml（Liquibase 变更记录文件）│  │  │     ├─ templates（模板配置目录，例如：邮件模板）│  │  │     ├─ banner.txt（Banner 配置文件）│  │  │     └─ logback-spring.xml（日志配置文件）│  │  └─ test（测试相关代码目录）│  └─ pom.xml（包含打包相关配置）├─ continew-system（系统管理模块，存放系统管理相关业务功能，例如：部门管理、角色管理、用户管理等）│  ├─ src│  │  ├─ main│  │  │  ├─ java/top/continew/admin│  │  │  │  ├─ auth（系统认证相关业务）│  │  │  │  │  ├─ controller（系统认证相关 API）│  │  │  │  │  ├─ service（系统认证相关业务接口及实现类）│  │  │  │  │  ├─ model（系统认证相关模型）│  │  │  │  │  │  ├─ query（系统认证相关查询条件）│  │  │  │  │  │  ├─ req（系统认证相关请求参数（Request））│  │  │  │  │  │  └─ resp（系统认证相关响应参数（Response））│  │  │  │  │  ├─ enums（系统认证相关枚举）│  │  │  │  │  ├─ constant（系统认证相关常量）│  │  │  │  │  ├─ handler（系统认证相关处理器）│  │  │  │  │  └─ config（系统认证相关配置）│  │  │  │  └─ system（系统管理相关业务）│  │  │  │     ├─ api（系统管理相关公共业务 API 实现）│  │  │  │     ├─ controller（系统管理相关 API）│  │  │  │     ├─ service（系统管理相关业务接口及实现类）│  │  │  │     ├─ mapper（系统管理相关 Mapper）│  │  │  │     ├─ model（系统管理相关模型）│  │  │  │     │  ├─ entity（系统管理相关实体）│  │  │  │     │  ├─ query（系统管理相关查询条件）│  │  │  │     │  ├─ req（系统管理相关请求参数（Request））│  │  │  │     │  └─ resp（系统管理相关响应参数（Response））│  │  │  │     ├─ enums（系统管理相关枚举）│  │  │  │     ├─ constant（系统管理相关常量）│  │  │  │     ├─ util（系统管理相关工具类）│  │  │  │     ├─ validation（系统管理相关参数校验工具类）│  │  │  │     ├─ container（系统管理相关 Crane4j 数据填充容器配置）│  │  │  │     └─ config（系统管理相关配置）│  │  │  └─ resources│  │  │     └─ mapper（系统管理相关 Mapper XML 文件目录）│  │  └─ test（测试相关代码目录）│  └─ pom.xml├─ continew-plugin（插件模块，存放能力开放、租户等扩展模块，后续会进行插件化改造）│  ├─ continew-plugin-open（能力开放插件模块）│  │  ├─ src│  │  │  ├─ main/java/top/continew/admin/open│  │  │  │  ├─ controller（能力开放相关 API）│  │  │  │  ├─ service（能力开放相关业务接口及实现类）│  │  │  │  ├─ mapper（能力开放相关 Mapper）│  │  │  │  ├─ model（能力开放相关模型）│  │  │  │  │  ├─ entity（能力开放相关实体）│  │  │  │  │  ├─ query（能力开放相关查询条件）│  │  │  │  │  ├─ req（能力开放相关请求参数（Request））│  │  │  │  │  └─ resp（能力开放相关响应参数（Response））│  │  │  │  ├─ util（能力开放相关工具类）│  │  │  │  ├─ handler（能力开放相关处理器）│  │  │  │  ├─ sign（能力开放相关 API 参数签名算法）│  │  │  │  └─ config（能力开放相关配置）│  │  │  └─ test（测试相关代码目录）│  │  └─ pom.xml│  ├─ continew-plugin-tenant（租户插件模块）│  │  ├─ src│  │  │  ├─ main/java/top/continew/admin/tenant│  │  │  │  ├─ api（租户相关公共业务 API 实现）│  │  │  │  ├─ controller（租户相关 API）│  │  │  │  ├─ service（租户相关业务接口及实现类）│  │  │  │  ├─ mapper（租户相关 Mapper）│  │  │  │  ├─ model（租户相关模型）│  │  │  │  │  ├─ entity（租户相关实体）│  │  │  │  │  ├─ query（租户相关查询条件）│  │  │  │  │  ├─ req（租户相关请求参数（Request））│  │  │  │  │  └─ resp（租户相关响应参数（Response））│  │  │  │  ├─ enums（租户相关枚举）│  │  │  │  ├─ constant（租户相关常量类）│  │  │  │  ├─ util（租户相关工具类）│  │  │  │  └─ config（租户相关配置）│  │  │  └─ test（测试相关代码目录）│  │  └─ pom.xml│  ├─ continew-plugin-schedule（任务调度插件模块）│  │  ├─ src│  │  │  ├─ main/java/top/continew/admin/schedule│  │  │  │  ├─ controller（任务调度相关 API）│  │  │  │  ├─ service（代码生成器相关业务接口及实现类）│  │  │  │  ├─ api（任务调度中心相关 Feign API）│  │  │  │  ├─ model（任务调度相关模型）│  │  │  │  │  ├─ query（任务调度相关查询条件）│  │  │  │  │  ├─ req（任务调度相关请求参数（Request））│  │  │  │  │  └─ resp（任务调度相关响应参数（Response））│  │  │  │  ├─ enums（任务调度相关枚举）│  │  │  │  ├─ constant（任务调度相关常量类）│  │  │  │  ├─ exception（任务调度相关异常）│  │  │  │  ├─ annotation（任务调度相关注解）│  │  │  │  └─ config（任务调度相关配置）│  │  │  └─ test（测试相关代码目录）│  │  └─ pom.xml│  ├─ continew-plugin-generator（代码生成器插件模块）│  │  ├─ src│  │  │  ├─ main│  │  │  │  ├─ java/top/continew/admin/generator│  │  │  │  │  ├─ controller（代码生成器相关 API）│  │  │  │  │  ├─ service（代码生成器相关业务接口及实现类）│  │  │  │  │  ├─ mapper（代码生成器相关 Mapper）│  │  │  │  │  ├─ model（代码生成器相关模型）│  │  │  │  │  │  ├─ entity（代码生成器相关实体）│  │  │  │  │  │  ├─ query（代码生成器相关查询条件）│  │  │  │  │  │  ├─ req（代码生成器相关请求参数（Request））│  │  │  │  │  │  └─ resp（代码生成器相关响应参数（Response））│  │  │  │  │  ├─ enums（代码生成器相关枚举）│  │  │  │  │  └─ config（代码生成器相关配置）│  │  │  │  └─ resources│  │  │  │     └─ templates（代码生成相关模板目录）│  │  │  │       ├─ backend（后端模板目录）│  │  │  │       └─ frontend（前端模板目录）│  │  │  └─ test（测试相关代码目录）│  │  └─ pom.xml│  └─ pom.xml├─ continew-common（公共模块，存放公共工具类，公共配置等）│  ├─ src│  │  ├─ main/java/top/continew/admin/common│  │  │  ├─ api（公共业务 API）│  │  │  ├─ base（公共基类）│  │  │  │  ├─ controller（控制器基类）│  │  │  │  ├─ mapper（Mapper 接口基类）│  │  │  │  ├─ model（公共模型）│  │  │  │  │  ├─ entity（实体基类）│  │  │  │  │  └─ resp（列表、详情响应基类）│  │  │  │  └─ service（业务接口及实现基类）│  │  │  ├─ model（公共模型）│  │  │  │  ├─ dto（公共数据传输对象（DTO））│  │  │  │  └─ req（公共请求参数（Request））│  │  │  ├─ context（公共上下文）│  │  │  ├─ enums（公共枚举）│  │  │  ├─ constant（公共常量类）│  │  │  ├─ util（公共工具类）│  │  │  └─ config（公共配置）│  │  │    ├─ crud（CRUD 配置）│  │  │    ├─ mybatis（MyBatis Plus 配置）│  │  │    ├─ websocket（Websocket 配置）│  │  │    ├─ doc（接口文档配置）│  │  │    ├─ excel（Excel 配置）│  │  │    └─ exception（全局异常处理）│  │  └─ test（测试相关代码目录）│  └─ pom.xml├─ continew-extension（扩展模块）│  ├─ continew-extension-schedule-server（任务调度服务端模块，实际开发时如果是公司统一提供环境，可直接删除本模块）│  │  ├─ src│  │  │  ├─ main│  │  │  │  ├─ java/top/continew/admin/extension/schedule│  │  │  │  │  └─ ScheduleServerApplication.java（任务调度服务端启动程序）│  │  │  │  └─ resources│  │  │  │     ├─ config（核心配置目录）│  │  │  │     │  ├─ application-dev.yml（开发环境配置文件）│  │  │  │     │  ├─ application-prod.yml（生产环境配置文件）│  │  │  │     │  └─ application.yml（通用配置文件）│  │  │  │     ├─ db/changelog（Liquibase 数据脚本配置目录）│  │  │  │     │  ├─ mysql（MySQL 数据库初始 SQL 脚本目录）│  │  │  │     │  ├─ postgresql（PostgreSQL 数据库初始 SQL 脚本目录）│  │  │  │     │  └─ db.changelog-master.yaml（Liquibase 变更记录文件）│  │  │  │     └─ logback-spring.xml（日志配置文件）│  │  │  └─ test（测试相关代码目录）│  │  └─ pom.xml│  └─ pom.xml├─ .github（GitHub 相关配置目录，实际开发时直接删除）├─ .idea│  └─ icon.png（IDEA 项目图标，实际开发时直接删除）├─ .image（截图目录，实际开发时直接删除）├─ .style（代码格式、License文件头相关配置目录，实际开发时根据需要取舍，删除时注意删除 /pom.xml 中的 spotless 插件配置）├─ .gitignore（Git 忽略文件相关配置文件）├─ docker（项目部署相关配置目录，实际开发时可备份后直接删除）├─ LICENSE（开源协议文件）├─ CHANGELOG.md（更新日志文件，实际开发时直接删除）├─ README.md（项目 README 文件，实际开发时替换为真实内容）├─ lombok.config（Lombok 全局配置文件）└─ pom.xml（包含版本锁定及全局插件相关配置）
```

# 认证鉴权

本项目采用轻量级 Java 权限认证框架 `Sa-Token` 实现完整的权限认证方案。Sa-Token 提供了简洁易用的 API，支持多种权限认证方式，满足项目的安全需求。

Sa-Token

Sa-Token 是一个轻量级 Java 权限认证框架，主要解决：登录认证、权限认证、单点登录、OAuth2.0、分布式Session会话、微服务网关鉴权 等一系列权限相关问题。

## 配置说明

本项目依赖：ContiNew Starter `continew-starter-auth-satoken`，其提供了 SaToken 框架的轻量扩展优化，详情请查阅 starter 对应文档。

-   预设 SaToken 部分样板化配置
-   自定义 SaToken 持久层 Redisson 实现
-   提供 SaToken 默认放行路径配置

本项目的 SaToken 核心配置主要集中在 `continew-server/.../config/satoken` 包下。

## 认证

### 忽略认证

如果需要忽略认证，只需要在对应 Controller 类或 Controller 方法上添加 `@SaIgnore` 注解即可。将此注解放在类上，将忽略该 Controller 内所有 API 的认证；放在方法上，则仅忽略对应 API 的认证。

```
@SaIgnore@Operation(summary = "登录", description = "用户登录")@PostMapping("/login")public LoginResp login(@RequestBody @Valid LoginReq req, HttpServletRequest request) {    return authService.login(req, request);}
```

## 鉴权

项目中主要使用两种权限校验方式：注解鉴权和 CRUD 默认鉴权。

### 注解鉴权

在 Controller 方法上添加 `@SaCheckPermission` 注解进行权限校验，该注解表示用户必须具有指定权限才能访问该方法。更多用法请查阅 [SaToken 官方文档](https://sa-token.cc/doc.html#/use/jur-auth)。

```
@SaCheckPermission("system:role:updatePermission")@Operation(summary = "修改权限", description = "修改角色的功能权限")@PutMapping("/{id}/permission")public void updatePermission(@PathVariable("id") Long id, @RequestBody @Valid RoleUpdatePermissionReq req) {    baseService.updatePermission(id, req);}
```

### CRUD 默认鉴权

对于继承了 `top.continew.admin.common.base.controller.BaseController` 的 Controller，项目为 CRUD API 统一添加了权限校验，无需在每个方法上单独添加校验注解。

DeptController.java

```
/** * 部门管理 API * * @author Charles7c * @since 2023/1/22 17:50 */@Tag(name = "部门管理 API")@RestController@CrudRequestMapping(value = "/system/dept", api = {Api.TREE, Api.GET, Api.CREATE, Api.UPDATE, Api.BATCH_DELETE,    Api.EXPORT, Api.DICT_TREE})public class DeptController extends BaseController<DeptService, DeptResp, DeptResp, DeptQuery, DeptReq> {}
```

在 `BaseController` 的 `preHandle` 方法中，系统会根据 Controller 的 API URL 和 API 类型自动生成权限码，例如：

-   Controller API URL 为 `/system/dept`
-   API 类型为 `Api.CREATE`（对应新增操作）
-   生成的权限码则为 `system:dept:create`

所以，当你创建新的 Controller 并继承 `BaseController` 时，请确保菜单权限配置遵循相同的规则，否则将校验失败。

### 忽略鉴权

和忽略认证一样，如果需要忽略鉴权，只需要在对应 Controller 类或 Controller 方法上添加 `@SaIgnore` 注解即可。将此注解放在类上，将忽略该 Controller 内所有 API 的鉴权；放在方法上，则仅忽略对应 API 的鉴权。

```
@SaIgnore@Operation(summary = "登录", description = "用户登录")@PostMapping("/login")public LoginResp login(@RequestBody @Valid LoginReq req, HttpServletRequest request) {    return authService.login(req, request);}
```

## 常见问题

1.  全局修改：在 `continew-common` 中修改 `BaseController` 的 `preHandle` 方法
2.  局部修改：在 Controller 中重写 `preHandle` 方法

## 参考资料

1.Sa-Token 权限认证：[https://sa-token.cc/doc.html#/use/jur-auth](https://sa-token.cc/doc.html#/use/jur-auth)

# 数据权限

在现代企业应用系统中，数据权限管理是确保数据安全与合规访问的核心功能之一。它允许系统管理员根据用户的角色、部门、职位等因素灵活配置数据访问权限，从而实现对敏感信息的有效保护和精细化管理。

场景：销售经理仅能查看和编辑自己及其下属团队的销售记录。实现：为“销售经理”角色配置“查看/编辑”权限，并设定数据范围为“本部门及以下数据权限”。当销售经理登录系统查询销售数据时，系统自动过滤，只展示符合权限的数据。

数据权限，简而言之，就是限制用户能够查看或操作的数据范围。这包括但不限于查看特定记录、编辑某些字段或执行特定操作的能力。在本项目中，目前已实现了行级数据权限，通过为每个角色选择数据权限范围，实现细粒度的数据权限控制。

## 角色与权限分配

管理员登录系统后，导航至“系统管理”->“角色管理”，点击“新增角色”，输入角色名称（如“销售经理”）及描述，选择好数据权限范围（如“本部门及以下数据权限”），保存即可创建新角色。

创建好角色之后，可以通过用户管理将角色分配给用户，如果开放了注册功能，也可以将指定角色设为新用户默认值。

## 数据权限控制

数据权限控制通过拦截 SQL 语句实现，在查询数据时，系统会自动根据当前登录用户的数据权限过滤结果集，确保用户只能看到其有权访问的信息。

### 配置数据权限插件

实现 `top.continew.starter.data.mybatis.plus.datapermission.DataPermissionFilter` 接口，重写它的两个方法，并注入到 Spring 容器。

本项目中已经处理好了本部分内容。

```
public interface DataPermissionFilter {    /**     * 是否过滤     *     * @return true：过滤；false：不过滤     */    boolean isFilter();    /**     * 获取当前用户信息     *     * @return 当前用户信息     */    DataPermissionCurrentUser getCurrentUser();}
```

### 使用注解

通过在 Mapper 接口方法上添加 `@DataPermission` 注解，指定数据过滤的关键信息。

本项目中已经提供了 `DataPermissionMapper` 来方便快速实现通用数据权限控制。

```
public interface DataPermissionMapper<T> extends BaseMapper<T> {    /**     * 根据 entity 条件，查询全部记录     *     * @param queryWrapper 实体对象封装操作类（可以为 null）     * @return 全部记录     */    @Override    @DataPermission    List<T> selectList(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);    /**     * 根据 entity 条件，查询全部记录（并翻页）     *     * @param page         分页查询条件     * @param queryWrapper 实体对象封装操作类（可以为 null）     * @return 全部记录（并翻页）     */    @Override    @DataPermission    List<T> selectList(IPage<T> page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);}
```

如果它不能满足你的需要，可以根据你的实际情况，在对应 Mapper 接口方法上配置 `@DataPermission` 注解信息。

字段

描述

默认值

value

表别名（等价于 tableAlias）

tableAlias

表别名

id

ID

id

deptId

部门 ID

dept_id

userId

用户 ID（仅本人数据权限，必须确认配置此信息）

create_user

roleId

角色 ID（角色和部门关联表）

role_id

deptTableAlias

部门表别名

sys_dept

roleDeptTableAlias

角色和部门关联表别名

sys_role_dept

# 响应处理

cnadmin 对响应处理进行了统一封装，包括：统一响应数据结构、统一异常处理等，旨在简化开发流程并提升API的一致性和可维护性。

## 配置说明

本项目依赖 ContiNew Starter `continew-starter-web`，其提供了 Spring Web 轻量扩展优化，详情请查阅 starter 对应文档。该模块主要提供以下功能：

-   **跨域支持**：通过配置文件快速启用和定制跨域过滤器（CorsFilter）
-   **统一响应处理**：基于 Graceful Response 实现响应数据封装、异常处理和错误码管理
-   **Query 参数转换器**：Date、LocalDate、LocalTime、LocalDateTime、BaseEnum 类型参数自动转换
-   **服务器优化**：默认使用 Undertow 替代 Tomcat，提供更优性能；支持全局禁用不安全 HTTP 请求方法

## 统一响应数据结构

我们采用 Graceful Response 进行统一的响应数据封装，接口只需直接返回数据即可被自动封装为标准响应结构。这有助于保持 API 响应格式的一致性，并简化前端数据处理逻辑。

### 使用示例

以下是一个简单的控制器示例，展示了如何返回数据：

TestController.java

```
@GetMapping("/{id}")public UserDetailResp get(@PathVariable Long id) {    // 直接返回业务数据，框架会自动封装为统一响应结构    return userService.get(id);}
```

返回的 JSON 结构如下：

```
{    "code": "0",      // 响应码：0表示成功    "msg": "ok",      // 响应消息    "success": true,  // 是否成功    "timestamp": 1755240981618,  // 时间戳    "data": {         // 业务数据        ...    }}
```

### 排除响应封装

如果你不需要封装响应结构（例如：下载数据接口），可以添加 `@ExcludeFromGracefulResponse` 注解来排除：

DownloadController.java

```
@ExcludeFromGracefulResponse@GetMapping("/download")public ResponseEntity<Resource> downloadFile() {    // 直接返回文件资源，不进行响应封装    Resource resource = new ClassPathResource("example.pdf");    return ResponseEntity.ok()            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=example.pdf")            .body(resource);}
```

### 自定义响应配置

Graceful Response 提供了灵活的自定义响应配置。下方为 cnadmin 默认的全局响应配置，你可根据需要自行调整：

application.yml

```
--- ### 全局响应配置continew-starter.web.response:  # 自定义失败 HTTP 状态码（默认：200，建议业务和通信状态码区分）  default-http-status-code-on-error: 200  # 自定义成功响应码（默认：0）  default-success-code: 0  # 自定义成功提示（默认：ok）  default-success-msg: ok  # 自定义失败响应码（默认：1）  default-error-code: 1  # 自定义失败提示（默认：error）  default-error-msg: error  # 是否将原生异常错误信息填充到状态信息中  origin-exception-using-detail-message: false  # 响应类全名（配置后 response-style 将不再生效）  response-class-full-name: top.continew.starter.web.model.R
```

> 提示：我们已在 `continew-starter-web` 中默认提供了 `top.continew.starter.web.model.R` 响应结构并封装部分常用方法。如果无法满足需求或需要自定义结构，请参考该类进行定制。

## 统一异常处理

cnadmin 采用 Graceful Response 结合自定义异常处理器的方式进行统一异常拦截处理，确保 API 错误响应的一致性和友好性。异常处理代码位于 `continew-common` 模块的 `config/exception` 包下，你可以根据需要自行增加或删减异常处理。

### 全局异常处理器

**GlobalExceptionHandler（全局异常处理器）** 处理以下异常类型：

异常类型

描述

BaseException

自定义异常

BusinessException

业务异常

BadRequestException

自定义验证异常-错误请求

MissingServletRequestParameterException

方法参数缺失异常，例如：`@RequestParam` 参数缺失

BindException、MethodArgumentNotValidException

参数校验不通过异常，例如：`@NotBlank` 等

MethodArgumentTypeMismatchException

方法参数类型不匹配异常，例如：`@RequestParam` 参数类型不匹配

HttpMessageNotReadableException

HTTP 消息不可读异常 例如： 1.@RequestBody 缺失请求体 2.@RequestBody 实体内参数类型不匹配 3.请求体解析格式异常

MultipartException

文件上传异常-超过上传大小限制

NoHandlerFoundException

请求 URL 不存在异常

HttpRequestMethodNotSupportedException

不支持的 HTTP 请求方法异常

### SaToken 异常处理器

**GlobalSaTokenExceptionHandler（全局 SaToken 异常处理器）** 处理以下认证和授权异常：

异常类型

描述

NotLoginException

未登录或登录过期异常

NotPermissionException

权限不足异常

NotRoleException

角色不匹配异常

## 参考资料

1.  Graceful Response 项目主页：[https://doc.feiniaojin.com/graceful-response/home.html](https://doc.feiniaojin.com/graceful-response/home.html)
2.  ContiNew Starter Web 文档：[https://continew.top/starter/module/web.html](https://continew.top/starter/module/web.html)

# 数据字典

在本项目中，字典分为两种：数据库字典和枚举字典，接下来介绍下在本项目后端中如何使用数据字典。

## 数据库字典

数据库字典，即对应 `sys_dict`（字典） 和 `sys_dict_item`（字典项） 表中的字典数据，可通过菜单 系统管理/字典管理 进行数据维护。

在本项目中，数据库字典建议和业务隔离，仅做数据维护使用，例如：`message_type` 字典，系统消息根据不同业务的项目，维护不同的类型，避免业务逻辑中硬编码。

## 枚举字典

枚举字典，即实现了 `top.continew.starter.core.enums.BaseEnum` 枚举接口的枚举类。

在本项目中，枚举字典则和业务关联较强，例如：`auth_type_enum`（`AuthTypeEnum` 枚举） 字典，根据不同的认证类型有不同的认证处理逻辑。

在系统启动时，会自动初始化枚举字典到内存中。

## 接口访问

如果需要获取指定字典的数据项，可以通过下方接口进行查询，枚举字典 `code` 为对应枚举的类名，也可以写作下划线连接风格。

```
GET /common/dict/:code
```

例如：`/common/dict/auth_type_enum` 的响应结果。

```
{    "code": "0",    "msg": "ok",    "success": true,    "timestamp": 1743430116643,    "data": [        {            "label": "账号",            "value": "ACCOUNT",            "disabled": null,            "extra": "success"        },        {            "label": "邮箱",            "value": "EMAIL",            "disabled": null,            "extra": "primary"        },        {            "label": "手机号",            "value": "PHONE",            "disabled": null,            "extra": "primary"        },        {            "label": "第三方账号",            "value": "SOCIAL",            "disabled": null,            "extra": "error"        }    ]}
```

## 前端使用

在前端使用，可以使用 `useDict` 函数进行字典数据项的获取，在同一个页面只要不刷新浏览器，只会获取一次。

```
import { useDict } from '@/hooks/app'const { auth_type_enum } = useDict('auth_type_enum')
```

获取到字典数据项后，可以根据需要在页面中展示。

例如：在下拉选项中使用：

```
<a-select  v-model="queryForm.authType"  :options="auth_type_enum"  placeholder="请选择终端类型"  allow-clear  style="width: 160px"  @change="search"/>
```

例如：在表格单元格中使用：

```
<GiCellTag :value="record.authType" :dict="auth_type_enum" />
```

更多用法可搜索 `useDict` 查看已用功能。

# 数据填充（翻译）

Crane4j 简介

Crane4j 一个简单易用的关联字段填充框架，能够通过简单的注解配置快速根据外键/编码值填充相关字段，支持字典，枚举，方法等多种数据源。

## 场景一：填充用户昵称

每一张业务表基本都会涉及 `createUser`、`updateUser` 等审计字段，每次为了查询创建人信息，就不得不写一套联表 SQL。现在 ContiNew Admin 有了 Crane4j 的加成，你只需要参考如下使用即可。 如果继承了 `BaseResp`、`BaseDetailResp`，那下方这注解也不用了。

1、定义数据源容器

由于用户昵称属于高频使用到的数据源容器，所以在 `continew-starter-extension-crud` 依赖中已经预定义了一个容器 `ContainerPool.USER_NICKNAME`。

使用前只需要实现此接口，然后实现此方法即可。ContiNew Admin `UserServiceImpl` 中已经处理好了，所以你不需要做任何处理了。

```
public interface CommonUserService {    /**     * 根据 ID 查询昵称     *     * @param id ID     * @return 昵称     */    @ContainerMethod(namespace = ContainerPool.USER_NICKNAME, type = MappingType.ORDER_OF_KEYS)    String getNicknameById(Long id);}
```

2、定义填充规则

针对需要填充的实体字段添加填充规则，下方示例表示如果 `createUser` 字段值不为空，则通过数据源容器 `ContainerConstants.USER_NICKNAME` 中根据 createUser 字段值查询数据，并将数据赋值给 `createUserString` 字段。

```
// continew-admin-system/top.continew.admin.system.model.resp.log@Data@Schema(description = "日志详情信息")public class LogDetailResp implements Serializable {    // 其他字段略    /**     * 创建人     */    @JsonIgnore    @ConditionOnPropertyNotNull    @Assemble(prop = ":createUserString", container = ContainerConstants.USER_NICKNAME)    private Long createUser;    /**     * 创建人     */    @Schema(description = "创建人", example = "张三")    private String createUserString;}
```

3、定义填充触发器

到此环节，Crane4j 已经知道如何填充数据了，最后我们只需要在对应方法上触发填充即可。

如果你继承了 `BaseServiceImpl`，那么它提供的那些查询方法都已经配置好了填充触发器，你直接使用或重写内容即可。

```
// continew-admin-system/top.continew.admin.system.service.impl@Service@RequiredArgsConstructorpublic class LogServiceImpl implements LogService {    // 其他方法略    @Override    @AutoOperate(type = LogDetailResp.class)    public LogDetailResp get(Long id) {        LogDO logDO = baseMapper.selectById(id);        CheckUtils.throwIfNotExists(logDO, "LogDO", "ID", id);        return BeanUtil.copyProperties(logDO, LogDetailResp.class);    }}
```

## 场景二：一次性填充

除了高频使用的用户名填充，本项目中还有只需要填充一次的场景，这种只需要一次的场景，个人建议省去第一步。

示例如下：

```
// continew-admin-system/top.continew.admin.system.service.impl@Data@Schema(description = "部门信息")public class DeptResp extends BaseDetailResp {    // 其他字段略    /**     * 上级部门 ID     */    @Schema(description = "上级部门 ID", example = "2")    @ConditionOnExpression(value = "#target.parentId != 0")    @AssembleMethod(props = @Mapping(src = "name", ref = "parentName"), targetType = DeptService.class, method = @ContainerMethod(bindMethod = "get", resultType = DeptResp.class))    private Long parentId;    /**     * 上级部门     */    @Schema(description = "上级部门", example = "天津总部")    private String parentName;}
```

然后根据你的需要去配置填充触发即可，ContiNew Admin 项目中，由于 `DeptServiceImpl` 继承了 `BaseServiceImpl`，且所需填充场景就是其提供的 `tree` 方法，所以不需要定义了。

## 参考资料

1.Crane4j 强大又好用的关联字段填充框架：[https://opengoofy.github.io/crane4j](https://opengoofy.github.io/crane4j)

# Liquibase（数据库版本控制工具）

在《快速开始》篇已经跑通 ContiNew Admin 项目的同学一定知道，ContiNew Admin 项目首次启动前，无需手动执行 SQL 脚本来创建数据库表及导入初始数据，仅需配置好数据库名后启动程序就行。

实际上，这得益于 ContiNew Admin 集成的 Liquibase 组件，项目启动后，Liquibase 会在指定数据库中自动建表并初始化数据。

当然了，Liquibase 的功能远不止于此，本篇将对它进行简要介绍。

## 简介

> Liquibase 是一个开源的数据库版本控制工具，主要用于跟踪、管理和应用数据库变化。它支持多种数据库系统，包括 MySQL、PostgreSQL、Oracle、SQL Server 等，并且能够以声明性的方式定义数据库模式的变化，确保这些变化可以以可重复的方式应用‌。

我们在项目实际开发时，至少拥有三套环境，一般为 dev 开发环境、test/uat 测试环境、prod 生产环境，每套环境的数据库往往都需要随着项目的迭代进行同步更新。在没有使用数据库版本控制工具之前，这些环境的数据库变更通常依赖于开发人员手动执行 SQL 脚本，或者通过数据库迁移工具来管理。然而，手动执行 SQL 脚本容易出错，难以追踪变更历史，且难以保证不同环境间的一致性。

Liquibase 通过其独特的变更日志（changelog）机制，很好地解决了这些问题。变更日志以 XML、JSON、SQL 或 YAML 格式的文件存储，详细记录了每一次数据库变更的内容，包括新增表、修改表结构、添加索引、添加外键约束等操作。这些变更日志可以被 Liquibase 自动解析和执行，确保数据库结构在不同环境间能够保持一致。

## 集成组件

### 引入依赖

在项目中集成 Liquibase 非常简单，首先，我们在 `pom.xml` 中引入依赖。（不需要指定版本，Spring Boot 已经默认指定了其版本）

```
<dependencies>  <!-- Liquibase（用于管理数据库版本，跟踪、管理和应用数据库变化） -->  <dependency>    <groupId>org.liquibase</groupId>    <artifactId>liquibase-core</artifactId>  </dependency></dependencies>
```

### 配置

Liquibase 配置前还需要配置好数据源，这个就不介绍了。

在 `application.yml` 中添加 Liquibase 配置，指定 changelog 文件路径，启用 Liquibase。

```
## Liquibase 配置spring.liquibase:  # 是否启用  enabled: true  # 配置文件路径  change-log: classpath:/db/changelog/db.changelog-master.yaml
```

### 编写 CHANGELOG

在 `resources` 目录下创建 `db/changelog` 目录，然后在这个目录下创建 `db.changelog-master.yaml` 文件。这个文件主要是为了集中引入分散的 CHANGELOG 文件。

```
databaseChangeLog:  - include:      file: db/changelog/table.sql
```

然后根据需要，创建对应的 CHANGELOG 文件，上方简介也提到过了，CHANGELOG 文件可以是 XML、JSON、YAML、SQL 格式文件，本篇以 ContiNew Admin 项目里的使用格式 SQL 举例。如果你需要使用其他格式，可以翻阅官方资料，学习相应语法。SQL 格式的优势可复制性和可阅读性强，但是如果要存在多套不同的数据库，那用 XML 这类格式更好一些，因为它们用声明式语法来指定 changelog，这样方便迁移和兼容不同数据库。而 SQL 就比较限定了某一种数据库语法格式，有利有弊，根据你自己需要选择。

创建 `table.sql` 文件，添加如下注释。

```
-- liquibase formatted sql
```

然后还是根据需要添加 SQL 内容，本篇还是以 MySQL 语法为举例添加一个菜单表结构，用来创建菜单表。

核心就是 `-- changeset` 这个语法，每次你要添加数据库表变更，都需要用它来开头，至于后面的内容就是作者等信息。

`-- comment` 可以为这次 changelog 记录增加注释。

```
-- liquibase formatted sql-- changeset charles7c:1-- comment 初始化表结构CREATE TABLE IF NOT EXISTS `sys_menu` (    `id`          bigint(20)   NOT NULL AUTO_INCREMENT     COMMENT 'ID',    `title`       varchar(30)  NOT NULL                    COMMENT '标题',    `parent_id`   bigint(20)   NOT NULL DEFAULT 0          COMMENT '上级菜单ID',    `type`        tinyint(1)   UNSIGNED NOT NULL DEFAULT 1 COMMENT '类型（1：目录；2：菜单；3：按钮）',    `path`        varchar(255) DEFAULT NULL                COMMENT '路由地址',    `name`        varchar(50)  DEFAULT NULL                COMMENT '组件名称',    `component`   varchar(255) DEFAULT NULL                COMMENT '组件路径',    `redirect`    varchar(255) DEFAULT NULL                COMMENT '重定向地址',    `icon`        varchar(50)  DEFAULT NULL                COMMENT '图标',    `is_external` bit(1)       DEFAULT b'0'                COMMENT '是否外链',    `is_cache`    bit(1)       DEFAULT b'0'                COMMENT '是否缓存',    `is_hidden`   bit(1)       DEFAULT b'0'                COMMENT '是否隐藏',    `permission`  varchar(100) DEFAULT NULL                COMMENT '权限标识',    `sort`        int          NOT NULL DEFAULT 999        COMMENT '排序',    `status`      tinyint(1)   UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（1：启用；2：禁用）',    `create_user` bigint(20)   NOT NULL                    COMMENT '创建人',    `create_time` datetime     NOT NULL                    COMMENT '创建时间',    `update_user` bigint(20)   DEFAULT NULL                COMMENT '修改人',    `update_time` datetime     DEFAULT NULL                COMMENT '修改时间',    PRIMARY KEY (`id`),    UNIQUE INDEX `uk_title_parent_id`(`title`, `parent_id`),    INDEX `idx_parent_id`(`parent_id`),    INDEX `idx_create_user`(`create_user`),    INDEX `idx_update_user`(`update_user`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';
```

### 启动程序

这么添加完成后，第一次启动程序，Liquibase 会在对应数据库首先自动创建好两张表。

-   databasechangelog（记录表，很关键）
-   databasechangeloglock（锁表，无需关心）

创建好这两张表后，就会检测 CHANGELOG 文件，根据文件内容自动执行相应表创建等变更操作，操作完后，`databasechangelog` 文件就

ID

AUTHOR

FILENAME

DATEEXECUTED

...

MD5SUM

COMMENTS

1

charles7c

db/changelog/table.sql

2024-12-30 20:35:21

9:8e4080d1f7a635035839b7df0c02ef9c

初始化表结构

相信聪明的你，看到这张表内容后，一下子就明白了吧。原理很简单的，按照 Liquibase 语法来写变更日志，写好后，每次启动程序 Liquibase 检测有没有需要执行的 changelog，有就执行，然后插入对应的执行记录，并且为文件生成一个 MD5 值，这个 MD5 值也是检测文件有没有修改之前内容的关键。

因为，它要防止老六在写着写着后面的 changeset，还会去把前面已经执行过的 changeset 内容改掉。这是破坏性操作，是不被允许的，正常你只能继续往下追加 `-- changeset`。

## 常见问题

像上面提到的，如果有老 6 不老老实实追加 changeset，而是改动之前执行过的内容，那么在启动程序后就会报类似如下错误。

```
Unsatisfied dependency expressed through bean property 'sqlSessionTemplate': Error creating bean with name 'liquibase' defined in class path resource [org/springframework/boot/autoconfigure/liquibase/LiquibaseAutoConfiguration$LiquibaseConfiguration.class]: Validation Failed:1 changesets check sumdb/changelog/xxx/continew-admin_xxx.sql::1::Charles7c was:8:1181b1e4347607c6084736f1a9c27327 but is now: 8:382915a29ba9c391a43b7b346e5fb6b3...
```

简单来说就是对比 MD5 发现和以前的不一样了，那 Liquibase 就会罢工。

如果要解决呢，解决方法有 3 个：

1.  如果你没什么重要资料，清库，把所有内容删掉重新启动，重新执行最简单
2.  将提示的最新 MD5 值替换到对应记录里，这样启动就不报错了，但是你改动的之前的内容，它肯定不会去执行的，需要你自己手动去更新最近的 SQL 变更
3.  **推荐：** 这种方法和第 2 个方法一样，只不过更高效一点。新建一个空白数据库，更改项目所使用的数据库，运行起来后将最新生成的 `databasechangelog` 表替换到原来报错的数据库，这样再启动就不会报错了，但是仍需要你手动去更新最近的 SQL 变更

温馨提示

ContiNew 项目团队没有老 6，我们也希望能尽可能使用 changeset 追加变更，以方便你们升级 SQL。

但有时候强迫症，或者我们也会定期几个版本进行一次 SQL 压缩合并来保持代码简洁。这样难免会调整之前执行过的内容，所以如果这时候你 pull 下了最新代码，启动程序执行，就会抛出如上错误。

但我相信你能理解，毕竟这是一个开源模板项目，而且代码简洁干净是我们的追求，但我们尽力压迫强迫症，延长压缩 SQL 的版本周期。

# Open API（开放能力）

为了满足跨系统接口调用需求，自 3.4.0 版本起，我们新增了 [能力开放] 模块。在该模块中包含了 [应用管理] 功能，专门用于管理第三方系统应用的 AK/SK 信息。

此外，我们还借助 `SaToken` 的 `api-sign` 接口签名插件模块，实现了验签鉴权的能力。

下面将对 Open API 能力的使用进行简要介绍与说明。

## 新增应用

打开 [能力开放/应用管理] 菜单页面，点击 [新增]，在弹出的对话框中依次填写应用名称、描述、过期时间等参数，确认无误后，点击 [确定] 即可创建一个新应用。

![20241216204032](https://continew.top/images/admin/backend/open-api/20241216204032.png)

新增应用成功后，系统会自动生成 Access Key（AK）和 Secret Key（SK）信息。请务必复制并妥善保存这些信息，切勿泄露。若不慎泄露SK，可通过 [重置密钥] 功能进行重置，以确保应用安全。

![20241216204125](https://continew.top/images/admin/backend/open-api/20241216204125.png)

## 重置密钥

若 Secret Key（SK）不慎泄露，可随时通过 [重置密钥] 功能来重新生成新的密钥，以确保应用安全。

![20241216204153](https://continew.top/images/admin/backend/open-api/20241216204153.png)

## 认证鉴权

新增应用并配置完成后，您就可以凭借该应用的 AK/SK 来调用相应的开放能力了。下方将以调用查询角色列表接口为例，演示 Open API 认证鉴权参数构建过程。

前置：准备好如下信息。

```
// 应用 AK/SKString accessKey = "应用AK";String secretKey = "应用SK";// API 地址String apiUrl = "http://127.0.0.1:8000/system/role";// 基础请求参数Map<String, Object> paramMap = new HashMap<>();paramMap.put("description", "管理员");
```

1.应用的请求中必须加上 `nonce`（随机字符串）参数，防止请求被无限重放。

```
// 随机字符串（唯一）paramMap.put("nonce", RandomUtil.randomString(32));
```

2.应用的请求中必须加上 `timestamp`（时间戳）参数，将请求的有效性限定在一个有限时间范围内（默认 15 分钟）。

```
// 时间戳paramMap.put("timestamp", System.currentTimeMillis());
```

3.应用的请求中必须加上 `accessKey`（AK）参数，用于标识应用身份。

```
// AKparamMap.put("accessKey", accessKey);
```

4.应用的请求中必须加上 `sign`（签名）参数，用于应用的身份验证和请求的防篡改验证。

```
// sign（签名）paramMap.put("sign", sign);
```

**`sign` 参数算法如下：**

a.根据系统参数名称（除 sign）将所有请求系统参数按照自然顺序进行排序。

```
TreeMap<String, Object> buildSignParamMap = new TreeMap<>(paramMap);
```

b.拼接 `key`（SK）参数。

注意：`key`（SK）不在URL中传递，双方配置在自己系统中，用于计算 sign 值，增强安全性。

```
buildSignParamMap.put("key", secretKey);
```

c.将所有参数连接成一个字符串，例如：a=1&b=2&c=3。

```
String buildSignParamStr = HttpUtil.toParams(buildSignParamMap, null, false);
```

d.采用 MD5（32位小写）加密方式加密整个参数字符串。

```
String sign = SecureUtil.md5(buildSignParamStr);
```

5.拼接好如上鉴权参数后，即可顺利调用 API，完整代码如下：

```
// 应用 AK/SKString accessKey = "应用AK";String secretKey = "应用SK";// API 地址String apiUrl = "http://127.0.0.1:8000/system/role";// 基础请求参数Map<String, Object> paramMap = new HashMap<>();paramMap.put("description", "管理员");// 构建鉴权参数// 随机字符串paramMap.put("nonce", RandomUtil.randomString(32));// 时间戳paramMap.put("timestamp", System.currentTimeMillis());// AKparamMap.put("accessKey", accessKey);// 签名// 将所有请求系统参数按照自然顺序进行排序TreeMap<String, Object> buildSignParamMap = new TreeMap<>(paramMap);// 拼接 key（SK）参数buildSignParamMap.put("key", secretKey);// 将所有参数连接成一个字符串，例如：a=18b=28c=3String buildSignParamStr = HttpUtil.toParams(buildSignParamMap, null, false);// 采用 MD5（32位小写）加密方式加密整个参数字符串String sign = SecureUtil.md5(buildSignParamStr);paramMap.put("sign", sign);// 拼接完整的请求 URLString fullUrl = apiUrl + "?" + HttpUtil.toParams(paramMap, null, false);// 发送请求String resp = HttpUtil.get(fullUrl);log.info(resp);
```

## 参考资料

1.  SaToken API 接口参数签名：[https://sa-token.cc/doc.html#/plugin/api-sign](https://sa-token.cc/doc.html#/plugin/api-sign)

# 定时任务

本项目中的定时任务采用的是灵活，可靠和快速的分布式任务重试和分布式任务调度平台 `Snail Job`。

## 部署服务端（任务调度中心）

首先，单独部署一套任务调度平台的服务端（任务调度中心）环境，开发期间也可以直接使用本项目提供的 `continew-extension/continew-extension-schedule-server` 服务端项目，方便快速开发使用，接下来就介绍下如何使用该服务端项目。

1.  创建数据库 `continew_admin_job`，用于任务调度中心数据存储使用。
2.  修改 `application-dev.yml` 配置文件中的数据源配置。
3.  启动 `continew-extension-schedule-server` 中的 `ScheduleServerApplication` 入口程序。
4.  启动完成后，`continew_admin_job` 数据库会自动初始化相关数据表。
5.  访问 `http://localhost:8001/snail-job`，默认用户及密码：admin/admin

## 修改客户端

1.  根据任务调度中心的实际使用情况，修改 `continew-webapi` 下 `application-dev.yml` 中的任务调度配置。
2.  默认情况下，dev 环境的任务调度配置是被关闭的，以防止控制台一直打印连接错误，所以当你有需要使用时，需要启用任务调度相关配置。

```
snail-job:  enabled: true
```

## 编写定时任务

根据 Snail Job API 编写定时任务程序执行代码，更多种方式请查看其官方文档。

```
@Componentpublic class Test {    // name 属性就是创建任务时使用到的执行器名称    @JobExecutor(name = "test")    public void test() {        SnailJobLog.REMOTE.info("测试日志输出：{}", LocalDateTime.now());    }}
```

启动或重新启动 Admin 项目。

## 创建定时任务

任务程序方面准备好之后，可访问 ContiNew Admin 项目创建及管理定时任务（目前尚不支持自动注册定时任务）。

打开 [任务调度/任务管理] 菜单，点击 [新增]，在弹出的新增对话框中根据需要，依次配置好定时任务，如图所示：

![20240804152612](https://continew.top/images/admin/backend/job/20240804152612.png)

创建好任务之后，可以启用定时任务或在对应任务记录后点击 [执行]，此操作会立即下发一次执行请求。然后，可点击 [日志] 跳转到 [任务日志] 菜单页面，查看对应定时任务执行记录。

友情提示

你还可以直接在 Snail Job 的服务端界面创建定时任务，本项目目前是直接对接的 Snail Job API，所以可以说仅仅是页面不同，数据来源则是一致的。

## 参考资料

1.Snail Job 定时任务：[https://snailjob.opensnail.com/docs/quickstart/abilityuse/schedule_task.html](https://snailjob.opensnail.com/docs/quickstart/abilityuse/schedule_task.html)

# 关闭/移除多租户

ContiNew Admin 从 v4.0.0 版本开始正式集成了多租户能力，这是社区从 1.x 版本起就持续期待的功能。考虑到维护成本和开发精力，我们最终决定放弃多租户与无租户分支并行的方案，改为默认提供多租户能力。

为了满足部分不需要多租户功能的用户需求，本文档将详细介绍如何关闭或移除多租户能力，确保所有用户都能顺利升级和使用最新版本。

注意事项

如果你体验了多租户功能，即初始化过租户模块的 SQL 脚本（默认项目运行时会执行 `plugin_tenant.sql` 脚本），请务必在关闭或移除租户功能之前，**将数据库进行重置**。否则在没有租户隔离的情况下，操作数据会出现错误。

**问题一：** 租户模块的 SQL 脚本初始化会为需要租户隔离的业务表增加 `tenant_id` 字段，并添加唯一校验。如果不重置数据库表，唯一校验将失效。**问题二：** 假设你创建过一个租户管理员 admin，在没有租户隔离的情况下，查询会返回两条admin数据，导致查询结果出错。

## 快速关闭租户功能（推荐）

如果您只是暂时不需要使用多租户功能，推荐通过简单配置来关闭它，而不是完全移除相关代码。

### 步骤 1：修改配置文件

在 `application.yml` 中，将 `continew-starter.tenant.enabled` 设置为 `false`：

continew-server/application.yml

```
# 租户配置continew-starter.tenant:  enabled: false
```

### 步骤 2：修改数据库变更日志

从 `db.changelog` 目录下的 `db.changelog-master.yaml` 文件中，注释掉与 `plugin_tenant.sql` 相关的脚本引用。

### 步骤 3：前端自动适配

前端会通过后端接口 `/system/common/dict/option/tenant` 自动获取租户启用状态，无需手动修改前端代码。

通过以上三步简单配置，即可完全关闭多租户功能，系统将以无租户模式运行。

## 完全移除租户相关代码

如果您确定永远不会使用多租户功能，可以选择完全移除相关代码。

### 步骤 1：移除租户依赖

从 `continew-common/pom.xml` 文件中移除租户扩展模块依赖：

continew-common/pom.xml

```
<!-- 移除 ContiNew Starter 扩展模块 - 租户 --><!-- <dependency>    <groupId>top.continew.starter</groupId>    <artifactId>continew-starter-extension-tenant-mp</artifactId></dependency> -->
```

### 步骤 2：移除租户配置

从 `application.yml` 中移除所有租户相关配置：

continew-server/application.yml

```
# 移除租户配置# continew-starter.tenant:#   ...
```

### 步骤 3：移除租户数据库脚本

删除 `db.changelog` 目录下的 `plugin_tenant.sql` 脚本文件，并从 `db.changelog-master.yaml` 中移除对该脚本的引用。

### 步骤 4：移除租户模块

1.  从 `continew-plugin` 中移除 `continew-plugin-tenant` 模块
2.  从 `continew-server` 的依赖配置中移除 `continew-plugin-tenant`
3.  从 `continew-common/api` 包中删除所有与租户相关的 API 定义
4.  编译项目，检查并修复可能出现的其他依赖错误
5.  移除前端项目中与租户相关的代码（如 `useTenant` 等）

## 注意事项

1.  我们已尽可能将租户相关代码封装在 `continew-plugin-tenant` 模块中，并通过 `TenantContextHolder.isTenantEnabled()` 方法对所有相关功能进行了条件判断（如有遗漏，欢迎提交 PR）。
2.  如需暂时禁用多租户功能，推荐使用「快速关闭」方案，这样可以保留未来启用多租户的可能性。
3.  完全移除租户代码前，请确保您已充分了解系统架构，避免误删关键代码导致系统异常。
4.  移除或修改代码后，请务必进行全面测试，确保系统功能正常。

# 切换数据库

ContiNew Admin 默认使用 MySQL 数据库，但从 v2.5.0 版本开始，框架逐步扩展适配更多数据库系统，目前已初步支持 PostgreSQL 数据库。本文将详细介绍如何切换数据库环境，以下操作以切换到 PostgreSQL 为例。

## 切换步骤

### 步骤 1：添加数据库驱动依赖

首先，在 `continew-common/pom.xml` 文件中引入 PostgreSQL 数据库驱动，并根据实际需要移除 MySQL 数据库驱动依赖：

continew-common/pom.xml

```
<!-- PostgreSQL Java 驱动 --><dependency>    <groupId>org.postgresql</groupId>    <artifactId>postgresql</artifactId></dependency>
```

### 步骤 2：修改数据源配置

修改 `application-dev.yml` 中的数据源配置。**注意**：开发环境（dev）默认使用 P6SPY 组件打印 SQL 执行日志，生产环境（prod）则默认不使用 P6SPY，请根据实际环境进行调整：

continew-server/application-dev.yml

```
--- ### 数据源配置spring.datasource:  type: com.zaxxer.hikari.HikariDataSource  url: jdbc:p6spy:postgresql://${DB_HOST:127.0.0.1}:${DB_PORT:5432}/${DB_NAME:continew_admin}?options=-c%20TimeZone=Asia/Shanghai&sslmode=prefer&channelBinding=require&stringtype=unspecified  username: ${DB_USER:postgres}  password: ${DB_PWD:123456}  driver-class-name: com.p6spy.engine.spy.P6SpyDriver
```

### 步骤 3：配置 MyBatis Plus

修改 `application.yml` 中的 MyBatis Plus 配置，设置正确的数据库类型：

continew-server/application.yml

```
--- ### MyBatis Plus 配置mybatis-plus:  ## MyBatis 配置  configuration:    # 数据库类型，用于在 Mapper.xml 中进行数据库区分，以适应不同数据库 SQL 语句    database-id: pgsql  ## 扩展配置  extension:    enabled: true    # 分页插件配置    pagination:      enabled: true      db-type: POSTGRE_SQL
```

### 步骤 4：更新数据库变更日志

修改 `db.changelog-master.yaml` 文件，指定 PostgreSQL 相关的初始脚本：

continew-server/db.changelog-master.yaml

```
databaseChangeLog:  - include:      file: db/changelog/postgresql/main_table.sql  - include:      file: db/changelog/postgresql/main_column.sql  - include:      file: db/changelog/postgresql/main_data.sql  - include:      file: db/changelog/postgresql/plugin/plugin_open.sql  - include:      file: db/changelog/postgresql/plugin/plugin_tenant.sql  - include:      file: db/changelog/postgresql/plugin/plugin_schedule.sql  - include:      file: db/changelog/postgresql/plugin/plugin_generator.sql
```

### 步骤 5：调整代码生成模板

如果你需要使用代码生成功能，还需修改 `continew-plugin-generator/src/main/resources/templates/backend/Menu.ftl` 文件，将 PostgreSQL 脚本模板的注释放开，并注释掉其他数据库的脚本模板。

## 完成切换

完成上述所有步骤后，数据库切换配置即告完成。你可以启动项目，验证是否能正常连接到 PostgreSQL 数据库。

注意事项

1.  切换数据库前，请确保已备份原有数据库数据，以免数据丢失。
2.  如已基于 ContiNew Admin 开发了业务代码，请检查 SQL 代码是否使用了数据库特有函数或语法，并及时调整以确保兼容性。
3.  PostgreSQL 与 MySQL 在数据类型、函数等方面存在差异，例如字符串处理、日期函数等，需要特别注意。

# 切换为数据库自增 ID

ContiNew Admin 在 v2.4.0 版本前仅支持 MySQL 数据库，从该版本开始启动了多数据库适配计划（目前已额外支持 PostgreSQL）。为确保跨数据库兼容性，框架默认采用分布式 ID 生成方案 [CosId](https://gitee.com/AhooWang/CosId) 作为主键生成策略。

如果您的项目：

1.  仅使用 MySQL 数据库
2.  业务场景简单，不需要分布式 ID 的特性
3.  希望保持数据库原生自增 ID 的简洁性

那么可以按照本文档所述步骤，将 ID 生成策略切换为 MySQL 原生自增 ID。

## 步骤 1：调整 ID 生成策略

修改 `application.yml` 配置文件中的 MyBatis Plus 配置，调整全局 ID 生成策略，移除 ID 生成器配置项。

continew-server/application.yml

```
--- ### MyBatis Plus 配置mybatis-plus:  ## 全局配置  global-config:    db-config:      # auto 代表使用数据库自增策略（需要在表中设置好自增约束）      id-type: AUTO  ## 扩展配置  extension:    enabled: true    # ID 生成器配置    #id-generator:    #  type: COSID
```

## 步骤 2：移除 CosId 组件

由于切换为数据库自增 ID 后不再需要分布式 ID 生成器，因此需要移除 CosId 相关配置和依赖。

### 2.1 移除配置

删除 `application.yml` 配置文件中与 CosId 相关的所有配置项。

continew-server/application.yml

```
--- ### CosId 配置#cosid:  #...
```

### 2.2 移除依赖

删除 `continew-common/pom.xml` 文件中的 CosId 依赖：

continew-common/pom.xml

```
<!-- CosId（通用、灵活、高性能的分布式 ID 生成器） --><dependency>    <groupId>me.ahoo.cosid</groupId>    <artifactId>cosid-spring-boot-starter</artifactId></dependency><dependency>    <groupId>me.ahoo.cosid</groupId>    <artifactId>cosid-spring-redis</artifactId></dependency>
```

## 步骤三：调整 Liquibase SQL 脚本

框架默认提供的 MySQL 建表脚本（位于 `continew-server/.../resources/db/changelog/mysql` 目录下）中已保留了 `AUTO_INCREMENT` 约束配置，因此在切换为自增 ID 策略时，通常不需要对建表脚本进行额外修改。

### 3.1 确认自增配置

建议检查脚本中的表定义，确保主键列已正确配置 `AUTO_INCREMENT` 约束，例如：

```
CREATE TABLE `sys_user` (  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',  -- 其他字段...  PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 3.2 清理示例数据（可选）

项目中包含部分示例数据，建议删除或调整这些数据，确保它们符合自增 ID 的生成规则。

## 完成验证

完成上述配置后，启动项目并进行如下验证：

1.  新增数据时，检查数据库表是否正确生成自增 ID
2.  确认系统功能正常，没有出现 ID 生成相关的错误
3.  监控日志，确保没有与 CosId 相关的错误或警告信息

# 切换为多数据源

ContiNew Admin 在 v3.4.0 版本（对应 ContiNew Starter v2.7.1）中移除了默认集成的多数据源组件，旨在降低新项目的上手成本和简化配置复杂度。

若你的业务场景需要使用多数据源功能，可以通过以下两种方式实现：

1.  参考[提交记录](https://gitee.com/continew/continew-admin/commit/67589a73566ec556bc3e5f9498e9793e9495519b)逆向还原多数据源相关代码
2.  按照本文档所述步骤重新引入多数据源功能

本文将介绍如何基于 MyBatis Plus ORM 框架重新集成多数据源功能。

## 步骤 1：引入依赖

首先，在 `continew-common/pom.xml` 引入多数据源依赖。（不需要声明版本，在 ContiNew Starter 中已经锁定过了）

continew-common/pom.xml

```
<!-- Dynamic Datasource（基于 Spring Boot 的快速集成多数据源的启动器） --><dependency>    <groupId>com.baomidou</groupId>    <artifactId>dynamic-datasource-spring-boot3-starter</artifactId></dependency>
```

## 步骤 2：配置多数据源

修改 `application-dev.yml` 文件中的数据源配置，添加动态数据源相关配置。生产环境部署前，需对应修改 `application-prod.yml` 等环境配置文件。

continew-server/application-dev.yml

```
--- ### 数据源配置spring.datasource:  type: com.zaxxer.hikari.HikariDataSource  ## 动态数据源配置（可配多主多从：m1、s1...；纯粹多库：mysql、oracle...；混合配置：m1、s1、oracle...）  dynamic:    # 是否启用 P6Spy（SQL 性能分析组件，该插件有性能损耗，不建议生产环境使用）    p6spy: true    # 设置默认的数据源或者数据源组（默认：master）    primary: master    # 严格匹配数据源（true：未匹配到指定数据源时抛异常；false：使用默认数据源；默认 false）    strict: false    datasource:      # 主库配置（可配多个，构成多主）      master:        url: jdbc:mysql://${DB_HOST:127.0.0.1}:${DB_PORT:3306}/${DB_NAME:continew_admin}?serverTimezone=Asia/Shanghai&useSSL=true&useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true&autoReconnect=true&allowPublicKeyRetrieval=true&nullCatalogMeansCurrent=true        username: ${DB_USER:root}        password: ${DB_PWD:123456}        driver-class-name: com.mysql.cj.jdbc.Driver        type: ${spring.datasource.type}      # 从库配置（可配多个，构成多从）      slave_1:        url: jdbc:postgresql://${DB_HOST_SLAVE:127.0.0.1}:${DB_PORT_SLAVE:5432}/${DB_NAME_SLAVE:continew_admin}?options=-c%20TimeZone=Asia/Shanghai&sslmode=prefer&channelBinding=require&stringtype=unspecified        username: ${DB_USER_SLAVE:postgres}        password: ${DB_PWD_SLAVE:123456}        # 延迟初始化，首次使用时才创建连接池        lazy: true        driver-class-name: org.postgresql.Driver        type: ${spring.datasource.type}    # Hikari 连接池配置（完整配置请参阅：https://github.com/brettwooldridge/HikariCP）    hikari:      # 最大连接数量（默认 10，根据实际环境调整）      # 注意：当连接达到上限，并且没有空闲连接可用时，获取连接将在超时前阻塞最多 connectionTimeout 毫秒      maximum-pool-size: 20      # 获取连接超时时间（默认 30000 毫秒，30 秒）      connection-timeout: 30000      # 空闲连接最大存活时间（默认 600000 毫秒，10 分钟）      idle-timeout: 600000      # 保持连接活动的频率，以防止它被数据库或网络基础设施超时。该值必须小于 maxLifetime（默认 0，禁用）      keepaliveTime: 30000      # 连接最大生存时间（默认 1800000 毫秒，30 分钟）      max-lifetime: 1800000
```

温馨提示

1.  生产环境建议关闭 p6spy，以避免性能损耗
2.  上方配置以 PostgreSQL 为从库示例，请根据你的实际业务需要调整
3.  可根据实际业务需求添加更多数据源配置（如 slave_2、oracle_db 等）

## 步骤 3：调整 MyBatis Plus 配置

修改 `application.yml` 文件中的 MyBatis Plus 配置，移除数据库类型指定，以支持多数据源。

continew-server/application.yml

```
--- ### MyBatis Plus 配置mybatis-plus:  ## 扩展配置  extension:    enabled: true    # 分页插件配置    pagination:      enabled: true      db-type:  
```

## 步骤 4：使用多数据源

完成以上配置后，即可在代码中使用 `@DS` 注解切换数据源。

### 4.1 类级别切换

在 Service 类上添加 `@DS` 注解，指定该类的所有方法使用的数据源：

```
@Service@DS("slave_1")  // 类级别指定默认使用 slave_1 数据源public class UserServiceImpl implements UserService {  // 方法实现...}
```

### 4.2 方法级别切换

在具体方法上添加 `@DS` 注解，可以覆盖类级别指定的数据源：

```
@Service@DS("master")  // 类级别默认使用 master 数据源public class UserServiceImpl implements UserService {  @Autowired  private JdbcTemplate jdbcTemplate;  @Override  @DS("slave_1")  // 方法级别覆盖，使用 slave_1 数据源  public List<Map<String, Object>> selectByCondition() {    return jdbcTemplate.queryForList("select * from sys_user where age > 10");  }}
```

## 参考资料

1.多数据源支持 | MyBatis-Plus：[https://baomidou.com/guides/dynamic-datasource/](https://baomidou.com/guides/dynamic-datasource/)

# 调整 Starter 中依赖版本

有一些同学在项目开发时，遇到了一些依赖版本的限制，想要升级或降级版本。但在框架里找了半天，也没有找到在哪里进行更改。

## 依赖版本管理机制

ContiNew Admin 框架的外部依赖版本主要由 ContiNew Starter 的 `continew-starter-dependencies` 模块统一锁定管理。（若你刚接触框架，对 ContiNew Admin 与 ContiNew Starter 的关系不太了解，可[点击查看相关说明](https://continew.top/about/faq.html#why-does-continew-starter-use-lgpl3-license)）

以下是 ContiNew Starter v2.13.4 版本中锁定的依赖版本列表，不同版本的 Starter 可能会有差异，你可以直接查看对应版本的 `continew-starter-dependencies` 模块获取最新信息：

continew-starter-dependencies/pom.xml

```
<!-- Core Framework Versions --><spring-boot.version>3.3.12</spring-boot.version><spring-cloud.version>2023.0.5</spring-cloud.version><!-- Cache and Storage Versions --><redisson.version>3.49.0</redisson.version><jetcache.version>2.7.8</jetcache.version><!-- Security and Authentication Versions --><sa-token.version>1.44.0</sa-token.version><just-auth.version>1.16.7</just-auth.version><!-- Database and ORM Versions --><mybatis-plus.version>3.5.12</mybatis-plus.version><mybatis-flex.version>1.10.9</mybatis-flex.version><dynamic-datasource.version>4.3.1</dynamic-datasource.version><p6spy.version>3.9.1</p6spy.version><!-- ID Generator and Job Scheduler Versions --><cosid.version>2.13.0</cosid.version><snail-job.version>1.5.0</snail-job.version><!-- Messaging and Notification Versions --><sms4j.version>3.3.5</sms4j.version><!-- Captcha Versions --><aj-captcha.version>1.4.0</aj-captcha.version><easy-captcha.version>1.6.2</easy-captcha.version><!-- Excel Processing Versions --><fastexcel.version>1.2.0</fastexcel.version><poi.version>5.4.1</poi.version><!-- File Storage Versions --><x-file-storage.version>2.2.1</x-file-storage.version><aws-s3-v1.version>1.12.783</aws-s3-v1.version><aws-sdk.version>2.31.63</aws-sdk.version><aws-crt.version>0.38.5</aws-crt.version><thumbnails.version>0.4.20</thumbnails.version><!-- Validation and Response Processing Versions --><graceful-response.version>5.0.5-boot3</graceful-response.version><spel-validator.version>0.5.2-beta</spel-validator.version><crane4j.version>2.9.0</crane4j.version><!-- API Documentation Versions --><knife4j.version>4.5.0</knife4j.version><!-- Tracing and Logging Versions --><tlog.version>1.5.2</tlog.version><!-- License and Compression Versions --><truelicense.version>1.33</truelicense.version><zip4j.version>2.11.5</zip4j.version><!-- HTTP Client and Utilities Versions --><okhttp.version>4.12.0</okhttp.version><ttl.version>2.14.5</ttl.version><ip2region.version>3.3.6</ip2region.version><hutool.version>5.8.38</hutool.version><snakeyaml.version>2.4</snakeyaml.version><nashorn.version>15.6</nashorn.version><!-- Security Vulnerability Fix Versions --><commons-beanutils.version>1.11.0</commons-beanutils.version><commons-io.version>2.17.0</commons-io.version><commons-compress.version>1.26.0</commons-compress.version><!-- Maven Plugin Versions --><flatten.version>1.7.0</flatten.version><spotless.version>2.44.3</spotless.version><sonar.version>3.11.0.3922</sonar.version>
```

## 关于修改依赖版本的建议

原则上，我们不建议直接修改 ContiNew Starter 中锁定的依赖版本。这是因为 Starter 的核心作用就是集中管理依赖版本，确保各组件之间的兼容性，避免版本冲突。自行调整版本可能会导致不可预见的兼容性问题。

但如果你有特殊需求或无法等待 Starter 版本更新，可以通过以下方法调整依赖版本：

## 方式一：通过 Maven 继承特性修改（推荐）

如果你是通过 `parent` 方式引入的 ContiNew Starter（ContiNew Admin 默认采用此方式）：

continew-admin/pom.xml

```
<!--    下方 parent 为 ContiNew Starter（Continue New Starter）。    ContiNew Starter 基于"约定优于配置"的理念，    再次精简常规配置，提供一个更为完整的配置解决方案，帮助开发人员更加快速的集成常用第三方库或工具到 Spring Boot Web 应用程序中。    ContiNew Starter 包含了一系列经过企业实践优化的依赖包（如 MyBatis-Plus、SaToken），    可轻松集成到应用中，为开发人员减少手动引入依赖及配置的麻烦，为 Spring Boot Web 项目的灵活快速构建提供支持。--><parent>    <groupId>top.continew.starter</groupId>    <artifactId>continew-starter</artifactId>    <version>最新版本</version></parent>
```

你可以利用 Maven 的继承特性，在项目的 `pom.xml` 文件中通过覆盖 `properties` 中的依赖版本变量来调整版本。

例如：要将 Spring Boot 版本调整为 `3.4.7`，只需在 `pom.xml` 中添加：

continew-admin/pom.xml

```
<properties>    <spring-boot.version>3.4.7</spring-boot.version></properties>
```

**注意**：你需要确保覆盖的变量名称与 ContiNew Starter `continew-starter-dependencies` 模块中定义的完全一致。

## 方式二：通过 dependencyManagement 管理

基于 Maven 的依赖解析原则（路径最近者优先、第一声明者优先），你可以在项目的 `pom.xml` 文件中通过 `dependencyManagement` 标签直接管理依赖版本。这种方式可以更精细地控制特定依赖的版本。

# 集成 Thymeleaf

ContiNew Admin 默认为前后端分离项目，后端项目仅提供数据接口服务。但如果你仅仅需要使用 Thymeleaf 模板引擎来开发一些简单的页面，那么按下方步骤操作即可。

1.首先，引入模板引擎的依赖。

```
<dependency>    <groupId>org.springframework.boot</groupId>    <artifactId>spring-boot-starter-thymeleaf</artifactId></dependency>
```

2.在 resources/templates 目录下新增模板文件。

例如：`test.html`

```
<!DOCTYPE html><html><head></head><body>    <h1>呵呵</h1></body></html>
```

注意：如果你想放在其他位置，可以在 `application.yml` 中指定模板文件的位置（Spring Boot 默认配置如下）。

```
### Thymeleaf 配置spring:  thymeleaf:    prefix: classpath:/templates/    suffix: .html    mode: HTML
```

3.编写 Controller，访问模板页面。

注意：是 `@Controller` 而不是 `@RestController`，并根据需要做好认证处理。

```
@SaIgnore // 忽略认证@Controllerpublic class TestController {    @GetMapping("/test")    public String test() {        // 返回模板名称        return "test";    }}
```

4.启动/重启后端项目，访问 URL `http://localhost:8000/test`。

操作完上方步骤后即可访问到模板页面。