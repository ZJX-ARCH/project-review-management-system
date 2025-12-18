# 前端实现交接单（Frontend Implementation Handover）

> **From**: Designer (Gemini) / Architect (Opus) / Critic (Codex)
> **To**: Frontend Builder (Codex/Sonnet)
> **Module**: [填写模块名称]
> **Version**: V4.0

---

## ⚠️ CRITICAL WARNINGS（V4.0强制）

### 🚨 中文显示要求（TOP PRIORITY）

**HTML meta 标签（强制）**：
```html
<meta charset="UTF-8">
```

**Axios 配置（强制）**：
```javascript
const api = axios.create({
    responseEncoding: 'utf8',
    headers: {
        'Content-Type': 'application/json;charset=UTF-8'
    }
});
```

### 🚨 V4.0特殊要求

**立即Git提交（强制）**：
- 每个组件测试通过后立即提交

**中文测试（强制）**：
```javascript
test('用户可以输入中文用户名', () => {
    const input = screen.getByPlaceholder('请输入用户名');
    fireEvent.change(input, { target: { value: '张三' } });
    expect(input.value).toBe('张三');
});
```

---

## 📋 交接文档清单

### 必读文档（V4.0）
- [ ] `01-design-draft.md` (Designer起草 - 前端部分)
- [ ] `01-critique.md` (Critic发现的问题)
- [ ] `01-architect-verdict.md` (Architect批准)
- [ ] `02-tasks.md` (前端任务)

---

## 🔧 技术栈

- **框架**：React 18
- **UI 库**：Ant Design 5.x
- **HTTP**：Axios (配置 responseEncoding: 'utf8')
- **测试**：Jest + React Testing Library

---

## 📊 核心设计要点

[从三方对抗设计文档中提取前端组件]

---

## 🧪 测试要求（V4.0强制）

### 中文测试示例
```javascript
test('用户可以输入中文用户名', () => {
    render(<RegisterForm />);

    const input = screen.getByPlaceholderText('请输入用户名');
    fireEvent.change(input, { target: { value: '张三' } });

    expect(input.value).toBe('张三');
});
```

---

## ✅ 验收标准（V4.0）

- [ ] 所有组件实现完整
- [ ] 所有测试通过（Exit Code 0）
- [ ] 中文测试通过
- [ ] HTML 包含 meta charset
- [ ] Axios 配置 responseEncoding
- [ ] **立即Git提交（每组件一次）**

---

## 🔄 完成后操作（V4.0流程）

### 1. 运行测试
```bash
npm test
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

---

**V4.0特性：三方对抗设计完成 → 立即实施 → 测试通过 → 立即提交 → 更新记忆**
