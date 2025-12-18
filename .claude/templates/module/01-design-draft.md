# 三方对抗设计 - Designer起草

> **V4.0阶段1步骤1** - Gemini 3.0 Pro起草完整设计

---

## 模块信息

**模块名称**: [模块名]

**Designer**: Gemini 3.0 Pro

**设计时间**: [YYYY-MM-DD HH:MM]

---

## ⚠️ Designer职责

作为Designer，你需要：
1. 起草完整的前后端设计
2. 覆盖所有功能点（不要遗漏）
3. 包含前端页面/组件 + 后端API + 数据库设计
4. 考虑用户体验和性能
5. **重要**：这是初稿，Critic会进行红队攻击找漏洞

---

## 前端设计

### 页面/组件列表

#### 1. [页面/组件1名称]

**路由**: `/[path]`

**功能描述**: [描述此页面/组件的功能]

**UI设计**:

```
┌─────────────────────────────────────────┐
│  [页面标题]                              │
│                                         │
│  [UI元素1]                               │
│  [UI元素2]                               │
│  [UI元素3]                               │
│                                         │
│  [按钮] [按钮]                           │
└─────────────────────────────────────────┘
```

**组件树**:

```jsx
<PageName>
  <Header />
  <FormSection>
    <InputField name="field1" />
    <InputField name="field2" />
    <Button>提交</Button>
  </FormSection>
</PageName>
```

**状态管理**:

```javascript
{
  field1: '',
  field2: '',
  loading: false,
  error: null
}
```

**交互流程**:

1. 用户输入field1、field2
2. 点击"提交"按钮
3. 显示loading状态
4. 调用后端API：POST /api/[path]
5. 成功：显示成功消息，跳转到[下一个页面]
6. 失败：显示错误消息

**数据验证**（前端）:

- field1：必填，长度3-20字符
- field2：必填，数字类型，范围1-100

---

#### 2. [页面/组件2名称]

[同上结构]

---

## 后端设计

### API接口列表

#### 1. [API1名称]

**端点**: `POST /api/[path]`

**描述**: [功能描述]

**请求参数**:

```json
{
  "param1": "string (必填，描述)",
  "param2": 123 (必填，数字，范围1-100)
}
```

**请求头**:

```
Authorization: Bearer <token>
Content-Type: application/json
```

**响应成功** (200):

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "field1": "value",
    "field2": 123,
    "createdAt": "2025-11-30T14:23:15Z"
  }
}
```

**响应失败**:

- `400 Bad Request`:

```json
{
  "code": 400,
  "message": "参数错误",
  "errors": [
    {"field": "param1", "message": "长度必须在3-20之间"}
  ]
}
```

- `401 Unauthorized`:

```json
{
  "code": 401,
  "message": "未认证，请先登录"
}
```

- `403 Forbidden`:

```json
{
  "code": 403,
  "message": "无权限访问此资源"
}
```

**业务逻辑**:

1. 验证请求参数
2. 检查用户认证（JWT token）
3. 检查用户权限
4. 执行业务逻辑（如创建记录）
5. 返回结果

**数据验证**（后端）:

- param1：非空，长度3-20，只能包含字母数字
- param2：非空，整数，范围1-100

**异常处理**:

- 参数验证失败 → 400
- 数据库错误 → 500（记录日志）
- 业务规则冲突 → 400（如用户名已存在）

---

#### 2. [API2名称]

[同上结构]

---

## 数据库设计

### 表1: [表名]

```sql
CREATE TABLE [table_name] (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    field1 VARCHAR(255) NOT NULL COMMENT '字段1描述',
    field2 INT NOT NULL DEFAULT 0 COMMENT '字段2描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME NULL COMMENT '删除时间（软删除）',

    INDEX idx_field1 (field1),
    INDEX idx_status_created (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表描述';
```

**字段说明**:

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | BIGINT | 主键 | 自增 |
| field1 | VARCHAR(255) | 字段1描述 | 非空 |
| field2 | INT | 字段2描述 | 非空，默认0 |
| status | TINYINT | 状态 | 1-正常，0-禁用 |
| created_at | DATETIME | 创建时间 | 自动填充 |
| updated_at | DATETIME | 更新时间 | 自动更新 |
| deleted_at | DATETIME | 删除时间 | 软删除 |

**索引设计**:

- `idx_field1`: 单列索引，用于按field1查询
- `idx_status_created`: 复合索引，用于按状态和时间排序查询

---

### 表2: [表名]

[同上结构]

---

## 前后端对应关系

### 功能1: [功能名称]

```
前端：[页面/组件名称]
  ├─► POST /api/[path1] - 创建
  ├─► GET /api/[path2] - 查询列表
  ├─► GET /api/[path3]/{id} - 查询详情
  ├─► PUT /api/[path4]/{id} - 更新
  └─► DELETE /api/[path5]/{id} - 删除
```

### 功能2: [功能名称]

[同上结构]

---

## 安全设计

### 认证机制

- **方式**: JWT
- **Token存储**: localStorage（或httpOnly cookie）
- **Token有效期**: 2小时
- **刷新机制**: refresh token（有效期14天）

### 授权机制

- **模型**: RBAC（基于角色）
- **角色**: admin, user, guest
- **权限检查**: 每个API入口检查

### 安全措施

- ✅ SQL注入防护（MyBatis参数化查询）
- ✅ XSS防护（前端输入验证 + 后端输出转义）
- ✅ CSRF防护（SameSite Cookie + CSRF Token）
- ✅ 密码加密（BCrypt）
- ✅ API限流（每IP每分钟100次）
- ✅ HTTPS强制

---

## 性能设计

### 性能目标

- API响应时间：P95 < 100ms
- 页面加载时间：P95 < 2s
- 并发支持：1000个并发用户

### 优化策略

#### 前端优化

- 代码分割（Code Splitting）
- 懒加载（Lazy Loading）
- 图片优化（WebP格式）
- 缓存策略（浏览器缓存）

#### 后端优化

- 数据库索引（所有查询字段）
- 查询优化（避免N+1）
- 缓存热点数据（Redis）
- 连接池（HikariCP）

---

## 边缘案例考虑

### 空值处理

- 空字符串：返回默认列表
- null值：返回错误提示
- 空数组：显示"暂无数据"

### 超长输入

- 前端限制：最大长度
- 后端验证：超长拒绝

### 并发处理

- 乐观锁（version字段）
- 唯一性约束（数据库级）

---

## 测试计划

### 单元测试

- Service层所有方法
- 工具类所有方法
- 覆盖率 ≥ 80%

### 集成测试

- 所有API接口
- 数据库访问

### E2E测试

- 核心业务流程
- 用户旅程

---

## 下一步（等待Critic）

此设计将交给**Critic（Codex 5.1）**进行红队攻击，找出：
- 功能遗漏
- 安全漏洞
- 性能问题
- 边缘案例
- 过度设计

---

**📝 此文档由Designer（Gemini）在阶段1步骤1生成**
