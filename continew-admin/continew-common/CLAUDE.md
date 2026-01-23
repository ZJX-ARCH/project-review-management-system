# continew-common - 公共模块

[根目录](../../CLAUDE.md) > [continew-admin](../CLAUDE.md) > **continew-common**

---

## 模块职责

`continew-common` 是 ContiNew Admin 的公共基础模块，提供全局配置、工具类、基础类、异常处理等跨模块共享的能力。

### 核心职责

- **全局配置**：异常处理、WebSocket、接口文档、验证码配置
- **基础类**：Controller 基类、枚举基类
- **工具类**：文件工具、加密工具等
- **异常处理**：全局异常处理器
- **常量定义**：缓存 Key、系统常量等

---

## 关键组件

### 1. 全局异常处理

**类**：
- `GlobalExceptionHandler` - 全局异常处理器
- `GlobalSaTokenExceptionHandler` - Sa-Token 异常处理器

**功能**：
- 统一处理业务异常、参数校验异常、系统异常
- 返回统一的 JSON 响应格式
- 记录异常日志

---

### 2. 接口文档配置

**类**：
- `NextDoc4jCustomPermissionDisplay` - 自定义权限展示
- `NextDoc4jCustomPathFiltering` - 自定义路径过滤
- `GlobalSpringDocResponseOperationCustomizer` - 全局响应自定义

**功能**：
- 自动生成 Swagger/Knife4j 接口文档
- 自定义接口权限展示
- 统一响应格式

---

### 3. WebSocket 配置

**类**：
- `WebSocketClientServiceImpl` - WebSocket 客户端服务实现

**功能**：
- 支持服务端推送消息到客户端
- 管理 WebSocket 会话

---

### 4. 验证码配置

**类**：
- `CaptchaProperties` - 验证码配置属性

**功能**：
- 图形验证码配置
- 行为验证码配置

---

### 5. 基础 Controller

**类**：
- `BaseController` - 控制器基类

**功能**：
- 提供通用的响应方法
- 提供分页参数处理

---

## 目录结构

```
continew-common/
├── src/main/java/top/continew/admin/common/
│   ├── base/                # 基础类
│   │   └── controller/      # 控制器基类
│   ├── config/              # 全局配置
│   │   ├── exception/       # 异常处理
│   │   ├── doc/             # 接口文档配置
│   │   ├── websocket/       # WebSocket 配置
│   │   └── CaptchaProperties.java
│   ├── constant/            # 常量定义
│   ├── enums/               # 枚举定义
│   └── util/                # 工具类
└── src/main/resources/
    └── application-common.yml  # 公共配置
```

---

## 关键依赖

```xml
<dependencies>
    <!-- ContiNew Starter -->
    <dependency>
        <groupId>top.continew.starter</groupId>
        <artifactId>continew-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>top.continew.starter</groupId>
        <artifactId>continew-starter-api-doc</artifactId>
    </dependency>
    <dependency>
        <groupId>top.continew.starter</groupId>
        <artifactId>continew-starter-captcha-graphic</artifactId>
    </dependency>
    <dependency>
        <groupId>top.continew.starter</groupId>
        <artifactId>continew-starter-captcha-behavior</artifactId>
    </dependency>

    <!-- Hutool -->
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
    </dependency>
</dependencies>
```

---

## 异常处理

### 异常类型

| 异常类 | HTTP 状态码 | 说明 |
|--------|-------------|------|
| `BusinessException` | 200 | 业务异常 |
| `BadRequestException` | 400 | 请求参数错误 |
| `UnauthorizedException` | 401 | 未登录 |
| `ForbiddenException` | 403 | 无权限 |
| `NotFoundException` | 404 | 资源不存在 |

### 响应格式

```json
{
  "code": 1,
  "msg": "操作失败",
  "data": null,
  "timestamp": 1642345678901
}
```

---

## 常见问题

### Q1：如何添加全局异常处理？

在 `GlobalExceptionHandler` 中添加新的 `@ExceptionHandler` 方法。

### Q2：如何自定义响应格式？

修改 `continew-starter-web` 的响应配置或实现自定义的 `ResponseAdvice`。

---

*此文档由 AI 自动生成，最后更新于 2026-01-16 11:48:58*
