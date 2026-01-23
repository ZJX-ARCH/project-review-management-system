# continew-server - 启动入口模块

[根目录](../../CLAUDE.md) > [continew-admin](../CLAUDE.md) > **continew-server**

---

## 模块职责

`continew-server` 是 ContiNew Admin 的 Spring Boot 启动入口模块，负责整合所有业务模块并提供可执行的应用程序。

### 核心职责

- **应用启动**：提供 `ContiNewAdminApplication` 主启动类
- **Controller 层**：包含全局 Controller（如验证码、仪表盘）
- **配置管理**：管理应用配置文件（application.yml、logback.xml）
- **打包部署**：通过 `spring-boot-maven-plugin` 打包为可执行 jar

---

## 入口与启动

### 主启动类

```java
@SpringBootApplication
@EnableCrudApi
@EnableGlobalResponse
@EnableFileStorage
@EnableMethodCache
@EnableFeignClients
public class ContiNewAdminApplication implements ApplicationRunner {
    public static void main(String[] args) {
        SpringApplication.run(ContiNewAdminApplication.class, args);
    }
}
```

**路径**：`src/main/java/top/continew/admin/ContiNewAdminApplication.java`

### 启动命令

```bash
# 开发环境启动
mvn spring-boot:run

# 生产环境启动（打包后）
java -jar continew-server-4.2.0-SNAPSHOT.jar
```

---

## 对外接口

### Controller 列表

| Controller | 路径 | 功能 |
|-----------|------|------|
| `CaptchaController` | `/captcha` | 验证码生成与校验 |
| `DashboardController` | `/dashboard` | 仪表盘数据统计 |

---

## 关键依赖

```xml
<dependencies>
    <dependency>
        <groupId>top.continew.admin</groupId>
        <artifactId>continew-system</artifactId>
    </dependency>
    <dependency>
        <groupId>top.continew.admin</groupId>
        <artifactId>continew-plugin-generator</artifactId>
    </dependency>
    <dependency>
        <groupId>top.continew.admin</groupId>
        <artifactId>continew-plugin-open</artifactId>
    </dependency>
    <dependency>
        <groupId>top.continew.admin</groupId>
        <artifactId>continew-plugin-schedule</artifactId>
    </dependency>
    <dependency>
        <groupId>top.continew.admin</groupId>
        <artifactId>continew-plugin-tenant</artifactId>
    </dependency>
</dependencies>
```

---

## 关键配置

### 配置文件

- `src/main/resources/config/application.yml` - 主配置
- `src/main/resources/config/application-dev.yml` - 开发环境
- `src/main/resources/config/application-prod.yml` - 生产环境
- `src/main/resources/logback-spring.xml` - 日志配置

### 核心配置项

```yaml
server:
  port: 8000
  servlet:
    context-path: /

spring:
  profiles:
    active: dev
  application:
    name: continew-admin

application:
  name: ContiNew Admin
  version: 4.2.0-SNAPSHOT
  description: 项目评审管理系统
```

---

## 定时任务

### 任务类

- `NoticePublishJob` - 公告发布定时任务
- `DemoEnvironmentJob` - 演示环境定时清理任务

---

## 测试

### 测试类

- `ContiNewAdminApplicationTests` - 应用启动测试

```java
@SpringBootTest
class ContiNewAdminApplicationTests {
    @Test
    void contextLoads() {
        // 测试应用上下文加载
    }
}
```

---

*此文档由 AI 自动生成，最后更新于 2026-01-16 11:48:58*
