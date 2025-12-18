# SuperClaude 集成使用指南

> **版本**：V4.0
> **更新时间**：2025-11-30
> **集成状态**：✅ 已集成

---

## 📋 概述

SuperClaude 是一个增强型 AI 协作系统，通过 **斜杠命令** 来增强 V4.0 工作流。

### 核心特性

1. **斜杠命令**：简化复杂操作的快捷命令
2. **MCP 集成**：Context7、Sequential Thinking、Magic UI、Puppeteer、Chrome DevTools、IDE
3. **智能标志**：增强功能的控制标志

### V4.0 特性

- ✅ 三方对抗设计
- ✅ 4层记忆系统
- ✅ 渐进式休眠
- ✅ 强制串行执行
- ✅ 立即Git提交

---

## 💡 斜杠命令使用

### 设计类命令

```bash
/sc:design          # 设计系统架构
/sc:brainstorm      # 需求分析和头脑风暴
```

### 实现类命令

```bash
/sc:implement       # 功能实现
/sc:build           # 构建和编译
```

### 分析类命令

```bash
/sc:analyze         # 代码分析
/sc:explain         # 解释代码
```

### 测试类命令

```bash
/sc:test            # 运行测试
```

### Git类命令

```bash
/sc:git             # Git操作
```

---

## 🔧 在 V4.0 中使用 SuperClaude

### 自动激活（推荐）

V4.0 的各个阶段会自动使用合适的SuperClaude命令：

**示例**：
```
Stage 1: 三方对抗设计
→ Designer: /sc:design
→ Critic: /sc:analyze
→ Architect: /sc:design --architecture
```

### 手动使用命令

在任何阶段，你都可以使用斜杠命令来简化操作：

**示例**：
```
用户：/sc:implement --tdd
Sonnet：[启用TDD模式]
        [实现功能]
        [立即运行测试]
        [测试通过后Git提交]
```

---

## 🧠 SuperClaude 与 V4.0 特性集成

### 与三方对抗设计的配合

- Designer: 使用 `/sc:design` 创建初始设计
- Critic: 使用 `/sc:analyze` 发现问题
- Architect: 使用 `/sc:design --architecture` 终审

### 与4层记忆系统的配合

- 所有SuperClaude命令执行后自动更新记忆
- QUICK_RESUME.md 包含最后使用的命令
- 恢复时自动提示推荐命令

### 与立即Git提交的配合

- `/sc:implement` 自动在测试通过后提交
- `/sc:git` 处理所有Git操作
- 符合V4.0的提交规范

---

## 🚨 常见问题

### Q1: 我必须使用斜杠命令吗？
**A**: 不是必须的。斜杠命令是简化操作的快捷方式，你可以用自然语言描述相同的意图。

### Q2: V4.0的SuperClaude和V3.1有什么区别？
**A**: V4.0集成了：
- 三方对抗设计支持
- 4层记忆系统集成
- 渐进式休眠机制
- 立即Git提交

---

## 📚 相关文档

- **V4.0 工作流**：`workflow.json`
- **V4.0 架构文档**：`ARCHITECTURE.md`
- **记忆系统指南**：`guides/MEMORY_GUIDE.md`

---

**SuperClaude 让 V4.0 更智能、更高效！**
