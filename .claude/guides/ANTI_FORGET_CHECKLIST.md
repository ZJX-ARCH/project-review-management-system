# 防遗漏检查清单

> **V4.0质量保证** - 确保不遗漏任何功能点

---

## 使用场景

### 何时使用此清单？

1. **阶段1 - Critic红队攻击时**：检查Designer的设计是否遗漏功能
2. **阶段1 - Architect终审时**：最终批准前的完整性检查
3. **阶段7 - 测试验证时**：确保所有功能都已测试
4. **阶段9 - 模块交付时**：确保交付物完整

---

## 功能遗漏检查

### 1. CRUD完整性检查

对于任何数据实体，确保包含：

```
[ ] Create（创建）- 添加新记录
[ ] Read（读取）- 查询/列表/详情
[ ] Update（更新）- 编辑现有记录
[ ] Delete（删除）- 删除记录
[ ] Validation（验证）- 输入验证
[ ] Error Handling（错误处理）- 异常情况处理
```

**示例**：用户管理
- ✅ 创建用户
- ✅ 查看用户列表
- ✅ 查看用户详情
- ✅ 编辑用户信息
- ✅ 删除用户
- ❌ **遗漏**：批量删除用户

---

### 2. 认证/授权完整性检查

```
[ ] 注册（Register）
[ ] 登录（Login）
[ ] 登出（Logout）
[ ] 忘记密码（Forgot Password）
[ ] 重置密码（Reset Password）
[ ] 修改密码（Change Password）
[ ] "记住我"（Remember Me）
[ ] 邮箱验证（Email Verification）
[ ] 多设备登录管理
[ ] 会话管理
[ ] 权限检查
```

**常见遗漏**：
- ❌ 忘记密码功能
- ❌ "记住我"功能
- ❌ 邮箱验证功能
- ❌ 多设备登录管理

---

### 3. 列表/分页完整性检查

```
[ ] 分页（Pagination）
[ ] 排序（Sorting）
[ ] 筛选（Filtering）
[ ] 搜索（Search）
[ ] 导出（Export）
[ ] 批量操作（Bulk Actions）
[ ] 空状态提示（Empty State）
[ ] 加载状态提示（Loading State）
[ ] 错误状态提示（Error State）
```

**常见遗漏**：
- ❌ 排序功能
- ❌ 高级筛选
- ❌ 导出Excel
- ❌ 空状态提示

---

### 4. 表单完整性检查

```
[ ] 表单验证（前端）
[ ] 表单验证（后端）
[ ] 错误提示（Error Messages）
[ ] 成功提示（Success Messages）
[ ] 加载状态（Loading State）
[ ] 禁用重复提交（Prevent Duplicate Submission）
[ ] 必填字段标识（Required Field Indicator）
[ ] 默认值填充（Default Values）
[ ] 表单重置（Reset）
[ ] 表单取消（Cancel）
```

**常见遗漏**：
- ❌ 前端验证（只做了后端）
- ❌ 禁止重复提交
- ❌ 表单重置按钮

---

### 5. 文件上传完整性检查

```
[ ] 文件选择
[ ] 文件类型验证
[ ] 文件大小验证
[ ] 上传进度显示
[ ] 上传失败处理
[ ] 文件预览
[ ] 文件删除
[ ] 多文件上传（如需要）
[ ] 拖拽上传（如需要）
```

**常见遗漏**：
- ❌ 上传进度显示
- ❌ 文件预览
- ❌ 大文件分片上传

---

## 前后端对应检查

### 检查方法

对于每个前端页面/组件，检查是否有对应的后端API：

```
前端：登录页面
  └─► 后端：POST /api/auth/login ✅

前端：用户列表页面
  ├─► 后端：GET /api/users（列表）✅
  ├─► 后端：GET /api/users/{id}（详情）✅
  ├─► 后端：DELETE /api/users/{id}（删除）✅
  └─► 后端：PUT /api/users/{id}（编辑）❌ 遗漏！

前端：用户注册页面
  ├─► 后端：POST /api/users/register ✅
  └─► 后端：POST /api/users/verify-email ❌ 遗漏！
```

### 常见遗漏模式

1. **前端有，后端无**：
   - 前端有"导出"按钮 → 后端缺少导出API
   - 前端有"批量删除" → 后端缺少批量删除API

2. **后端有，前端无**：
   - 后端有刷新Token API → 前端没调用
   - 后端有统计API → 前端没展示

---

## 安全遗漏检查

### 常见安全遗漏

```
[ ] SQL注入防护（使用参数化查询）
[ ] XSS防护（输入验证+输出转义）
[ ] CSRF防护（Token验证）
[ ] 密码加密（BCrypt/Argon2）
[ ] API认证（JWT/Session）
[ ] API授权（权限检查）
[ ] 敏感数据加密（如手机号、身份证）
[ ] API限流（防暴力破解）
[ ] HTTPS强制
[ ] 安全响应头（CSP, X-Frame-Options等）
```

**检查示例**：登录API

```
✅ 密码BCrypt加密
✅ 失败3次锁定10分钟
❌ 遗漏：没有验证码（容易被暴力破解）
❌ 遗漏：没有设备指纹（无法检测异常登录）
```

---

## 性能遗漏检查

### 常见性能遗漏

```
[ ] 数据库索引（常用查询字段）
[ ] N+1查询优化
[ ] 分页加载（不要一次查所有数据）
[ ] 缓存热点数据（Redis）
[ ] 静态资源CDN
[ ] 图片懒加载
[ ] 代码分割（Code Splitting）
[ ] API响应压缩（Gzip）
```

**检查示例**：用户列表查询

```sql
-- 查询语句
SELECT * FROM users WHERE status = 1 ORDER BY created_at DESC LIMIT 20;

-- 索引检查
✅ 有主键索引 (id)
❌ 遗漏：缺少status字段索引
❌ 遗漏：缺少created_at字段索引

-- 优化后
CREATE INDEX idx_status_created ON users(status, created_at);
```

---

## 边缘案例检查

### 1. 空值处理

```
[ ] 空字符串（""）
[ ] null值
[ ] undefined
[ ] 空数组（[]）
[ ] 空对象（{}）
```

**示例**：用户名搜索

```javascript
// ❌ 没有处理空字符串
function searchUsers(keyword) {
  return db.query("SELECT * FROM users WHERE username LIKE ?", [`%${keyword}%`]);
}

// ✅ 处理边缘案例
function searchUsers(keyword) {
  if (!keyword || keyword.trim() === '') {
    return db.query("SELECT * FROM users LIMIT 20"); // 返回默认列表
  }
  return db.query("SELECT * FROM users WHERE username LIKE ?", [`%${keyword.trim()}%`]);
}
```

---

### 2. 超长输入处理

```
[ ] 超长文本（如1万字的评论）
[ ] 超长列表（如1万个选项）
[ ] 超大文件（如100MB的图片）
```

**示例**：用户名长度

```
✅ 前端验证：最多20字符
❌ 遗漏：后端没有验证（可以绕过前端直接发请求）
```

---

### 3. 并发处理

```
[ ] 重复提交（点击按钮多次）
[ ] 并发编辑（两人同时编辑同一数据）
[ ] 库存扣减（高并发下）
[ ] 唯一性冲突（同时注册同一用户名）
```

**示例**：商品库存扣减

```sql
-- ❌ 没有并发处理
UPDATE products SET stock = stock - 1 WHERE id = 1;

-- ✅ 使用乐观锁
UPDATE products SET stock = stock - 1, version = version + 1
WHERE id = 1 AND version = ? AND stock > 0;
```

---

### 4. 特殊字符处理

```
[ ] SQL特殊字符（'、"、;）
[ ] HTML特殊字符（<、>、&）
[ ] JavaScript特殊字符（<script>）
[ ] 路径特殊字符（../、./）
[ ] emoji表情符号
```

---

## 用户体验遗漏检查

### 常见UX遗漏

```
[ ] 加载提示（Loading Indicator）
[ ] 错误提示（Error Message）
[ ] 成功提示（Success Message）
[ ] 确认对话框（Confirmation Dialog）- 删除等危险操作
[ ] 空状态提示（Empty State）- 无数据时
[ ] 禁用状态（Disabled State）- 按钮禁用时的视觉反馈
[ ] 键盘导航（Tab键支持）
[ ] 响应式设计（移动端适配）
```

**检查示例**：删除用户

```
✅ 有确认对话框
❌ 遗漏：删除成功后没有提示消息
❌ 遗漏：删除按钮没有Loading状态
❌ 遗漏：删除失败没有错误提示
```

---

## 测试遗漏检查

### 测试覆盖检查

```
[ ] 单元测试（Service层方法）
[ ] 集成测试（API接口）
[ ] E2E测试（用户流程）
[ ] 边缘案例测试
[ ] 错误路径测试
[ ] 性能测试
[ ] 安全测试
```

**检查示例**：登录功能

```
✅ 测试：正确的用户名和密码
✅ 测试：错误的密码
❌ 遗漏：用户名不存在的情况
❌ 遗漏：空用户名的情况
❌ 遗漏：空密码的情况
❌ 遗漏：失败3次后锁定的情况
❌ 遗漏：SQL注入尝试
```

---

## 文档遗漏检查

### 需要的文档

```
[ ] API文档（Swagger/OpenAPI）
[ ] 数据库表设计文档
[ ] 部署文档
[ ] 环境配置文档
[ ] 故障排查文档
[ ] 用户使用手册（如需要）
```

---

## V4.0三方对抗中的应用

### Critic角色使用此清单

在阶段1步骤2，Critic（Codex）进行红队攻击时，应使用此清单全面检查Designer的设计：

```
1. 读取Designer的设计文档（01-design-draft.md）

2. 使用本清单逐项检查：
   - CRUD完整性 ✓
   - 认证/授权完整性 ✗（缺少"记住我"）
   - 前后端对应 ✗（前端有导出，后端无API）
   - 安全检查 ✗（缺少API限流）
   - ...

3. 生成critique报告（01-critique.md）：
   - Critical问题：[X]个
   - High问题：[Y]个
   - Medium问题：[Z]个
```

---

## 检查清单模板

### 复制此模板用于每个模块

```markdown
## [模块名称]功能遗漏检查

### CRUD完整性
- [ ] Create
- [ ] Read
- [ ] Update
- [ ] Delete

### 前后端对应
- [ ] [前端页面1] ↔ [后端API1]
- [ ] [前端页面2] ↔ [后端API2]

### 安全检查
- [ ] SQL注入防护
- [ ] XSS防护
- [ ] 认证授权

### 性能检查
- [ ] 数据库索引
- [ ] 分页加载
- [ ] 缓存策略

### 边缘案例
- [ ] 空值处理
- [ ] 超长输入
- [ ] 并发处理

### 用户体验
- [ ] 加载提示
- [ ] 错误提示
- [ ] 空状态

### 测试覆盖
- [ ] 单元测试
- [ ] 集成测试
- [ ] E2E测试
```

---

**✅ 使用此清单，确保V4.0三方对抗设计不遗漏任何功能！**
