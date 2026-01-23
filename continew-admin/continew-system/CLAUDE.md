# continew-system - 系统管理模块

[根目录](../../CLAUDE.md) > [continew-admin](../CLAUDE.md) > **continew-system**

---

## 模块职责

`continew-system` 是 ContiNew Admin 的核心业务模块，提供系统管理相关的所有功能，包括用户管理、角色管理、菜单管理、部门管理、字典管理、日志管理、文件管理、系统配置等。

### 核心职责

- **认证与授权**：用户登录、登出、权限验证、在线用户管理
- **组织架构管理**：用户、角色、部门、菜单的增删改查与关联
- **系统配置**：字典、参数、客户端、短信、邮件、存储等配置
- **日志监控**：操作日志、登录日志、短信日志记录与查询
- **文件管理**：文件上传、下载、预览、回收站
- **公告管理**：系统公告发布与推送
- **消息中心**：用户消息管理

---

## 对外接口

### Controller 列表（18 个）

| Controller | 路径前缀 | 主要功能 |
|-----------|---------|---------|
| `AuthController` | `/auth` | 登录、登出、获取路由、用户信息 |
| `OnlineUserController` | `/auth/online` | 在线用户列表、强制下线 |
| `UserController` | `/system/user` | 用户增删改查、重置密码、角色分配 |
| `UserProfileController` | `/system/user/profile` | 个人中心、修改密码、头像上传 |
| `UserMessageController` | `/system/user/message` | 用户消息管理 |
| `RoleController` | `/system/role` | 角色管理、权限分配 |
| `MenuController` | `/system/menu` | 菜单管理、按钮权限 |
| `DeptController` | `/system/dept` | 部门管理 |
| `DictController` | `/system/dict` | 字典管理 |
| `DictItemController` | `/system/dict/item` | 字典项管理 |
| `FileController` | `/system/file` | 文件管理 |
| `FileRecycleController` | `/system/file/recycle` | 文件回收站 |
| `MultipartUploadController` | `/system/file/multipart` | 分片上传 |
| `NoticeController` | `/system/notice` | 公告管理 |
| `OptionController` | `/system/option` | 参数配置 |
| `ClientController` | `/system/client` | 客户端配置 |
| `StorageController` | `/system/storage` | 存储配置 |
| `SmsConfigController` | `/system/sms/config` | 短信配置 |
| `SmsLogController` | `/system/sms/log` | 短信日志 |
| `LogController` | `/system/log` | 操作日志、登录日志 |
| `CommonController` | `/system/common` | 公共接口（图片上传、地区数据等） |

---

## 关键依赖

```xml
<dependencies>
    <dependency>
        <groupId>top.continew.admin</groupId>
        <artifactId>continew-common</artifactId>
    </dependency>

    <!-- ContiNew Starter -->
    <dependency>
        <groupId>top.continew.starter</groupId>
        <artifactId>continew-starter-data-mp</artifactId>
    </dependency>
    <dependency>
        <groupId>top.continew.starter</groupId>
        <artifactId>continew-starter-cache-redisson</artifactId>
    </dependency>
    <dependency>
        <groupId>top.continew.starter</groupId>
        <artifactId>continew-starter-storage</artifactId>
    </dependency>
    <dependency>
        <groupId>top.continew.starter</groupId>
        <artifactId>continew-starter-auth-satoken</artifactId>
    </dependency>
    <dependency>
        <groupId>top.continew.starter</groupId>
        <artifactId>continew-starter-auth-justauth</artifactId>
    </dependency>
    <dependency>
        <groupId>top.continew.starter</groupId>
        <artifactId>continew-starter-messaging-mail</artifactId>
    </dependency>
    <dependency>
        <groupId>top.continew.starter</groupId>
        <artifactId>continew-starter-messaging-websocket</artifactId>
    </dependency>
</dependencies>
```

---

## 数据模型

### 主要实体表

| 表名 | 实体类 | 说明 |
|------|--------|------|
| `sys_user` | `UserDO` | 用户表 |
| `sys_role` | `RoleDO` | 角色表 |
| `sys_menu` | `MenuDO` | 菜单表 |
| `sys_dept` | `DeptDO` | 部门表 |
| `sys_user_role` | `UserRoleDO` | 用户角色关联表 |
| `sys_role_menu` | `RoleMenuDO` | 角色菜单关联表 |
| `sys_role_dept` | `RoleDeptDO` | 角色部门关联表 |
| `sys_dict` | `DictDO` | 字典表 |
| `sys_dict_item` | `DictItemDO` | 字典项表 |
| `sys_file` | `FileDO` | 文件表 |
| `sys_notice` | `NoticeDO` | 公告表 |
| `sys_option` | `OptionDO` | 参数表 |
| `sys_client` | `ClientDO` | 客户端表 |
| `sys_storage` | `StorageDO` | 存储配置表 |
| `sys_sms_config` | `SmsConfigDO` | 短信配置表 |
| `sys_sms_log` | `SmsLogDO` | 短信日志表 |
| `sys_log` | `LogDO` | 操作日志表 |
| `sys_login_log` | `LoginLogDO` | 登录日志表 |

### 包结构

```
top.continew.admin.system/
├── controller/          # 控制器层
├── service/             # 业务逻辑层
│   └── impl/
├── mapper/              # 数据访问层
├── model/               # 数据模型
│   ├── entity/          # 实体类（DO）
│   ├── query/           # 查询参数（Query）
│   ├── req/             # 请求参数（Request）
│   └── resp/            # 响应结果（Response）
├── enums/               # 枚举定义
├── config/              # 配置类
├── handler/             # 处理器（文件存储、短信等）
└── api/                 # 内部 API 接口（供其他模块调用）
```

---

## 认证授权

### 登录方式

1. **账号密码登录**：`AccountLoginHandler`
2. **邮箱登录**：`EmailLoginHandler`
3. **手机号登录**：`PhoneLoginHandler`
4. **第三方登录**：`SocialLoginHandler`（支持 Gitee、GitHub 等）

### 登录处理器工厂

```java
@Component
public class LoginHandlerFactory {
    private final Map<LoginType, AbstractLoginHandler> handlerMap;

    public AbstractLoginHandler getHandler(LoginType loginType) {
        return handlerMap.get(loginType);
    }
}
```

### 权限验证

- 使用 Sa-Token 进行权限验证
- 支持角色级权限（`@SaCheckRole`）
- 支持接口级权限（`@SaCheckPermission`）
- 支持数据权限（基于部门范围）

---

## 文件存储

### 存储策略

- **本地存储**：`LocalStorageHandler`
- **S3 存储**：`S3StorageHandler`（兼容 MinIO、阿里云 OSS、腾讯云 COS 等）

### 文件配置加载器

```java
@Component
public class FileStorageConfigLoader implements FileStoragePropertiesExtend {
    // 从数据库动态加载文件存储配置
}
```

### 分片上传

支持大文件分片上传，通过 `MultipartUploadController` 实现断点续传。

---

## 短信与邮件

### 短信配置

通过 `SmsConfigLoader` 从数据库动态加载短信配置，支持多种短信服务商。

### 邮件配置

通过 `MailConfigurerImpl` 从数据库动态加载邮件配置。

---

## 系统配置

### 配置管理

- **字典管理**：系统字典与字典项
- **参数管理**：系统参数配置
- **客户端配置**：登录超时、并发控制等
- **存储配置**：文件存储策略
- **短信配置**：短信服务商配置
- **邮件配置**：SMTP 配置

---

## 测试

当前模块未包含独立的测试用例，测试通过 `continew-server` 模块的集成测试覆盖。

---

## 常见问题

### Q1：如何添加新的登录方式？

1. 继承 `AbstractLoginHandler`
2. 实现 `doLogin()` 方法
3. 注册到 `LoginHandlerFactory`

### Q2：如何自定义文件存储策略？

实现 `StorageHandler` 接口并注册到 `StorageHandlerFactory`。

---

*此文档由 AI 自动生成，最后更新于 2026-01-16 11:48:58*
