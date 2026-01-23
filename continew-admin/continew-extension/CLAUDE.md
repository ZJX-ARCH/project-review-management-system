# continew-extension - 扩展模块

[根目录](../../CLAUDE.md) > [continew-admin](../CLAUDE.md) > **continew-extension**

---

## 模块职责

`continew-extension` 是扩展模块的聚合项目，包含任务调度服务器等扩展功能。

---

## 子模块列表

### continew-extension-schedule-server - 任务调度服务器

**路径**：`continew-extension-schedule-server`

**功能**：
- 提供内置的 PowerJob Server 能力
- 支持分布式任务调度
- 支持任务执行日志查询

**启动类**：
```java
@SpringBootApplication
public class ScheduleServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScheduleServerApplication.class, args);
    }
}
```

**配置**：
```yaml
server:
  port: 7700

spring:
  application:
    name: continew-admin-schedule-server
```

**启动命令**：
```bash
cd continew-extension/continew-extension-schedule-server
mvn spring-boot:run
```

---

## 使用场景

当不想单独部署 PowerJob Server 时，可以使用内置的调度服务器，简化部署架构。

---

*此文档由 AI 自动生成，最后更新于 2026-01-16 11:48:58*
