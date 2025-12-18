# 设计交接单（Design Handover）

> **From**: User / Architect (Opus)
> **To**: Designer (Gemini)
> **Module**: [填写模块名称]
> **Version**: V4.0

---

## 📋 设计任务

### 你的角色
你是 **Designer**，负责创建模块的初始设计草案。

### V4.0三方对抗设计流程
```
1. Designer (你) → 创建初始设计草案 (01-design-draft.md)
2. Critic (Codex) → 攻击设计，找出所有问题 (01-critique.md)
3. Architect (Opus) → 终审决策 (01-architect-verdict.md)
```

---

## 🎯 设计要求

### 必须包含的内容

#### 1. 前端设计
- 页面列表
- 组件列表
- 路由设计
- 状态管理
- UI/UX 设计

#### 2. 后端设计
- 数据模型（Entity）
- API 接口（RESTful）
- 业务逻辑（Service）
- 数据访问（Repository）

#### 3. 数据库设计
- 表结构
- 字段类型
- 索引设计
- 字符集（utf8mb4）

---

## ⚠️ 防遗漏检查清单

**在提交设计前，必须检查**：
- [ ] CRUD功能是否完整（Create, Read, Update, Delete）
- [ ] 前后端是否一一对应
- [ ] 是否考虑了边界情况
- [ ] 是否考虑了安全性
- [ ] 是否考虑了性能
- [ ] 是否支持中文
- [ ] 是否有足够的错误处理

---

## ✅ 输出文档

### 创建：`01-design-draft.md`

包含：
1. 模块概述
2. 前端设计（详细）
3. 后端设计（详细）
4. 数据库设计（详细）
5. API接口列表
6. 数据流图
7. 安全设计
8. 性能考虑

---

## 🔄 下一步

完成设计后：
1. 提交 `01-design-draft.md`
2. 交给 Critic 审查
3. 根据 Critic 的反馈修改
4. 等待 Architect 终审

---

**V4.0特性：三方对抗确保设计零遗漏！**
