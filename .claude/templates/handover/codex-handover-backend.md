# 后端实现交接单（Backend Implementation Handover）

> **From**: Designer (Gemini) / Architect (Opus) / Critic (Codex)
> **To**: Backend Builder (Codex/Sonnet)
> **Module**: [填写模块名称]
> **Version**: V4.0

---

## ⚠️ CRITICAL WARNINGS（V4.0强制）

### 🚨 编码要求（TOP PRIORITY）

**必须使用 UTF-8 编码（无 BOM）**：
- 所有 .java 文件必须 UTF-8
- 数据库字符集必须 utf8mb4
- 连接字符串包含 characterEncoding=utf8mb4
- API 响应头包含 charset=UTF-8

### 🚨 V4.0特殊要求

**立即Git提交（强制）**：
- 每个功能测试通过后立即提交
- 不累积多个功能一起提交
- 提交消息包含Claude标记

**编写中文测试（强制）**：
- 至少3个中文测试用例
- 测试中文存储和检索

---

## 📋 交接文档清单

### 必读文档（V4.0）
- [ ] `01-design-draft.md` (Designer起草)
- [ ] `01-critique.md` (Critic攻击)
- [ ] `01-architect-verdict.md` (Architect终审)
- [ ] `02-tasks.md` (任务清单)
- [ ] `guides/ENCODING_GUIDE.md`

---

## 🎯 实施任务清单

[从 02-tasks.md 中提取后端任务]

---

## 🔧 技术栈

- **框架**：Spring Boot 3.x
- **ORM**：MyBatis-Plus
- **数据库**：MySQL 8.0+ (utf8mb4)
- **安全**：Spring Security + JWT
- **测试**：JUnit 5 + Mockito

---

## 📊 核心设计要点

[从三方对抗设计文档中提取]

---

## 🧪 测试要求（V4.0强制）

### 中文测试示例
```java
@Test
public void testRegisterWithChineseName() {
    RegisterDTO dto = new RegisterDTO();
    dto.setUsername("张三");

    User result = userService.register(dto);

    assertEquals("张三", result.getUsername());
}
```

---

## ✅ 验收标准（V4.0）

- [ ] 所有功能实现完整
- [ ] 所有测试通过（Exit Code 0）
- [ ] 中文测试通过（无乱码）
- [ ] 编码验证脚本通过
- [ ] **立即Git提交（每功能一次）**

---

## 🔄 完成后操作（V4.0流程）

### 1. 运行测试
```bash
mvn test
```

### 2. 立即Git提交
```bash
git add .
git commit -m "feat(模块): [功能描述]

测试：通过 ✅
中文测试：通过 ✅

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>"
```

### 3. 更新4层记忆
- QUICK_RESUME.md
- MODULE_CONTEXT.md
- HISTORY.md

---

**V4.0特性：三方对抗设计完成 → 立即实施 → 测试通过 → 立即提交 → 更新记忆**
