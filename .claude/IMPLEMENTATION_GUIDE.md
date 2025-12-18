# 自主协作工作流系统 V4.0 - 实施指南

> 🚀 V4.0强化版 - 零中断、零遗忘、零失误的7x24自主工作团队

---

## 📋 目录

1. [V4.0新特性概览](#v40新特性概览)
2. [系统概述](#系统概述)
3. [准备工作](#准备工作)
4. [快速开始](#快速开始)
5. [详细配置](#详细配置)
6. [使用方法](#使用方法)
7. [进度监控与恢复](#进度监控与恢复)
8. [V4核心特性详解](#v4核心特性详解)
9. [SuperClaude增强系统](#superclaude增强系统)
10. [故障排查](#故障排查)
11. [最佳实践](#最佳实践)
12. [常见问题](#常见问题)

---

## 🆕 V4.0新特性概览

### 与V2.0的主要区别

| 方面 | V2.0 | V4.0 |
|------|------|------|
| **Agent超时** | ❌ 容易中途停止 | ✅ 渐进式休眠机制 |
| **Git提交** | ⚠️ 手动提醒 | ✅ 测试后立即自动提交 |
| **执行方式** | ⚠️ 可能并行混乱 | ✅ 强制串行执行 |
| **上下文恢复** | ⚠️ 重启后遗忘 | ✅ 30秒快速恢复 |
| **设计质量** | ⚠️ 可能遗漏功能 | ✅ 三方对抗设计 |
| **用户控制** | ⚠️ 架构无审查 | ✅ P0用户审查 |
| **Agent交接** | ⚠️ 可能缺上下文 | ✅ 强制交接文档 |

### V4.0核心改进（7大特性）

```
1️⃣ 渐进式休眠机制
   └─ 第1次: 工作5分钟 → 休眠10秒
   └─ 第2次: 工作5分钟 → 休眠30秒
   └─ 第3次+: 工作5分钟 → 休眠1分钟

2️⃣ 三方对抗设计（阶段1）
   └─ Designer (Gemini) → 起草设计
   └─ Critic (Codex) → 红队攻击
   └─ Architect (Opus) → 终审批准

3️⃣ 4层记忆系统
   └─ L0: QUICK_RESUME.md（30秒恢复）
   └─ L1: PROJECT_CONTEXT.md（项目级）
   └─ L2: MODULE_CONTEXT.md（模块级）
   └─ L3: HISTORY.md（完整历史）

4️⃣ 强制串行执行
   └─ 一个任务完成验证后才能开始下一个
   └─ 主控必须验证每个Agent的输出

5️⃣ 立即Git提交
   └─ 测试通过 → 立即进入阶段8
   └─ 每个功能单独提交，禁止堆积

6️⃣ P0用户审查
   └─ 架构设计完成后等待用户确认
   └─ 用户输入"继续"后才进入实施

7️⃣ 强制交接文档
   └─ 每次调用Agent必须准备完整交接文档
   └─ 包含任务背景、要求、约束、验收标准
```

---

## 📖 系统概述

### V4.0是什么？

V4.0是**企业级自主协作工作流系统**，让Claude Code、Codex和Gemini组成一个：

- ✅ **零中断团队**：渐进式休眠防止超时，长任务也能完成
- ✅ **零遗忘团队**：4层记忆系统，30秒恢复所有上下文
- ✅ **零失误团队**：三方对抗设计，设计阶段就消除缺陷
- ✅ **全自动团队**：从需求到交付，自动完成9个阶段
- ✅ **可追溯团队**：所有决策和代码都有文档记录
- ✅ **高质量团队**：林纳斯哲学+三方对抗+强制测试

### V4.0工作流程

```
用户输入需求
    ↓
P0. 架构设计 (Opus) → 等待用户审查确认 ← 【V4新增】
    ↓
0. 需求解析 (Sonnet)
    ↓
1. 三方对抗设计 ← 【V4新增】
   ├─ Designer (Gemini) → 起草设计
   ├─ Critic (Codex) → 红队攻击
   ├─ Designer (Gemini) → 修正（如需要）
   └─ Architect (Opus) → 终审批准
    ↓
2. 任务拆解 (Sonnet)
    ↓
3. 上下文准备 (Sonnet) ← 【V4强制交接文档】
    ↓
4. 代码实现 ← 【V4渐进式休眠】
   ├─ 前端 → Gemini 3.0 Pro
   └─ 后端 → Codex 5.1
    ↓
5. 代码审查 (Sonnet)
    ↓
6. 代码修正 (继承阶段4的模型)
    ↓
7. 测试验证 (Sonnet)
    ↓ 【V4强制：测试通过立即进入阶段8】
8. Git提交 (Haiku) ← 【V4立即提交】
    ↓
9. 模块交付 (Sonnet)
    ↓
【循环直到项目完成】
    ↓
P10. 项目完成度检查 (Opus)
    ↓
P99. 最终交付 (Opus)
```

### V4.0核心特点

| 特点 | V2.0 | V4.0 |
|------|------|------|
| **自主性** | 高 | 极高（P0后完全自主） |
| **可靠性** | 中（容易超时） | 极高（渐进式休眠） |
| **可恢复性** | 低 | 极高（30秒恢复） |
| **设计质量** | 中 | 极高（三方对抗） |
| **可追溯性** | 高 | 极高（4层记忆） |
| **用户控制** | 低 | 高（P0审查） |

---

## 🛠️ 准备工作

### Tier 1: 必需准备

#### 1. Claude Code环境

- ✅ 已安装最新版Claude Code
- ✅ 已登录Anthropic账号
- ✅ 可以使用Sonnet 4.5和Opus 4.5模型

**验证**：
```bash
# 在Claude Code中输入
@claude 你当前使用的是什么模型？
```

#### 2. 多模型MCP配置（V4.0必需）

**V4.0需要配置3个MCP服务器**：

**配置文件位置**: `C:\Users\lenovo\.claude\config.json`

**完整配置**:

```json
{
  "mcpServers": {
    "codex": {
      "command": "npx",
      "args": ["-y", "@anthropics/codex-mcp-server"],
      "env": {
        "OPENAI_API_KEY": "您的OpenAI API Key"
      },
      "description": "V4.0后端代码实现 + Critic角色"
    },
    "gemini": {
      "command": "npx",
      "args": ["-y", "@anthropics/gemini-mcp-server"],
      "env": {
        "GOOGLE_API_KEY": "您的Google API Key"
      },
      "description": "V4.0前端代码实现 + Designer角色"
    }
  }
}
```

**获取API Key**:
- **OpenAI API Key**: https://platform.openai.com/api-keys
  - 需要GPT-5.1-Codex访问权限
- **Google API Key**: https://makersuite.google.com/app/apikey
  - 需要Gemini 3.0 Pro访问权限

**测试MCP连接**:
```bash
# 在Claude Code中
/mcp list

# 应该看到：
# ✅ codex (connected)
# ✅ gemini (connected)
```

#### 3. Git配置

```bash
cd 您的项目目录
git init
git config user.name "您的名字"
git config user.email "您的邮箱"
```

#### 4. V4.0配置文件

**创建项目结构**:

```bash
# 进入项目目录
cd 您的项目目录

# 创建V4.0目录结构
mkdir -p .claude
mkdir -p docs/project
mkdir -p docs/progress
mkdir -p docs/dev/handovers

# 复制V4.0配置
cp C:\Users\lenovo\.claude\workflows\v4.0\workflow.json .claude/workflow.json
cp C:\Users\lenovo\.claude\workflows\v4.0\persona-mapping.json .claude/persona-mapping.json
cp C:\Users\lenovo\.claude\workflows\v4.0\superclaude-integration.json .claude/superclaude-integration.json
cp C:\Users\lenovo\.claude\workflows\v4.0\settings.local.json .claude/settings.local.json

# 复制模板
cp -r C:\Users\lenovo\.claude\workflows\v4.0\templates .claude/templates

# 创建V4.0记忆文件
echo "# 快速恢复\n\n暂无进行中的任务" > docs/progress/QUICK_RESUME.md
echo "# 项目上下文\n\n项目刚开始" > docs/progress/PROJECT_CONTEXT.md
echo "# 模块上下文\n\n" > docs/progress/MODULE_CONTEXT.md
echo "# 历史记录\n\n" > docs/progress/HISTORY.md
```

#### 5. 项目规范文件（CLAUDE.md）

在项目根目录创建 `CLAUDE.md`：

```markdown
# 项目开发规范

## 技术栈

- 后端: Spring Boot 2.7.18 + MyBatis-Plus
- 前端: React 18 + Ant Design
- 数据库: MySQL 8.0

## 代码规范

- 编码: UTF-8（无BOM）
- 缩进: 2空格（前端）/ 4空格（后端）
- 命名: 驼峰命名法
- 注释: 公共方法必须有JSDoc/Javadoc

## V4.0特殊要求

- ✅ 前端代码由Gemini实现
- ✅ 后端代码由Codex实现
- ✅ 所有设计必须经过三方对抗
- ✅ 每个功能测试通过后立即Git提交

## 测试要求

- 核心业务必须有单元测试
- 测试覆盖率 ≥80%
- 使用TDD方式开发

## 禁止事项

- 不修改公共API签名（除非明确允许）
- 不引入未经批准的第三方依赖
- 不跳过测试阶段
```

---

### Tier 2: 推荐配置

#### 6. 额外的MCP服务器

**Context7 MCP** - 文档查找（SuperClaude的-c7标志）:

```json
{
  "mcpServers": {
    "context7": {
      "command": "npx",
      "args": ["-y", "@context7/mcp-server"]
    }
  }
}
```

**Sequential Thinking MCP** - 深度思维（SuperClaude的-seq标志）:

```json
{
  "mcpServers": {
    "sequential-thinking": {
      "command": "npx",
      "args": ["-y", "@anthropics/sequential-thinking-mcp-server"]
    }
  }
}
```

**Magic UI MCP** - UI构建器（SuperClaude的-magic标志）:

```json
{
  "mcpServers": {
    "magic": {
      "command": "npx",
      "args": ["-y", "@21st/magic-ui-mcp-server"]
    }
  }
}
```

#### 7. 云盘同步（强烈推荐）

**V4.0的4层记忆系统依赖实时同步**：

1. 将 `docs/progress/` 目录添加到OneDrive/Dropbox
2. 在手机上安装对应App
3. 随时查看以下文件：
   - `QUICK_RESUME.md` - 30秒了解当前状态
   - `PROJECT_CONTEXT.md` - 了解项目整体进度
   - `HISTORY.md` - 查看所有已完成任务

---

## ⚡ 快速开始

### 方案A: 使用初始化脚本（推荐）

**V4.0提供自动化初始化脚本**：

```bash
# 进入您的项目目录
cd D:\MyProject

# 运行V4.0初始化脚本
C:\Users\lenovo\.claude\workflows\v4.0\scripts\init-project.bat
```

脚本会自动：
- ✅ 创建所有必需的目录
- ✅ 复制V4.0配置文件
- ✅ 初始化4层记忆文件
- ✅ 初始化Git仓库
- ✅ 创建CLAUDE.md模板

### 方案B: 手动配置

参考上面的 **Tier 1: 必需准备** 部分。

### 第一个V4.0任务

在Claude Code中：

```
@claude

请使用 V4.0 工作流系统完成以下任务：

【项目】：用户管理系统

【核心功能】：
1. 用户认证模块（Auth）
   - 注册、登录、记住我、忘记密码、邮箱验证

2. 用户资料模块（Profile）
   - 查看资料、编辑资料、头像上传

【技术栈】：
- 前端：React + Ant Design
- 后端：Spring Boot + MyBatis-Plus
- 数据库：MySQL 8.0

【V4.0要求】：
- 使用三方对抗设计
- 前端由Gemini实现
- 后端由Codex实现
- 测试通过立即Git提交

请自主完成，我会在 QUICK_RESUME.md 查看进度。
```

**工作流会自动**：

1. **阶段P0（Opus）**：设计整体架构 → 生成 `docs/project/master-plan.md`
2. **等待您审查**：Claude输出"请查看架构设计，确认后输入'继续'"
3. **您输入"继续"**后，工作流继续
4. **阶段1（三方对抗）**：
   - Designer(Gemini)起草设计
   - Critic(Codex)红队攻击
   - Designer(Gemini)修正
   - Architect(Opus)终审
5. **阶段2-9**：自动完成所有实施阶段
6. **实时更新记忆**：每5-10分钟更新 `QUICK_RESUME.md`

---

## ⚙️ 详细配置

### 配置1: 验证环境

```bash
# 验证Claude Code
claude --version

# 验证Git
git --version

# 验证Node.js（MCP需要）
node --version  # 需要 >= 18.0.0
npm --version
```

### 配置2: 测试MCP服务器

**测试Codex**：
```
@claude 使用Codex MCP写一个Hello World函数
```

**测试Gemini**：
```
@claude 使用Gemini MCP创建一个React Hello组件
```

### 配置3: 验证V4.0配置

```
@claude 请读取 .claude/workflow.json 并验证V4.0配置是否正确

需要检查：
1. 版本号是否为4.0
2. 是否有渐进式休眠配置
3. 是否有三方对抗设计配置
4. 是否有4层记忆系统配置
```

### 配置4: 初始化Git

```bash
cd 您的项目目录

# 初始化Git
git init
git add .
git commit -m "chore: initialize V4.0 autonomous workflow system"
```

---

## 🚀 使用方法

### V4.0任务启动模板

```
@claude

【使用V4.0工作流系统】

项目：[项目名称]

核心功能：
1. [功能1描述]
   - [子功能1-1]
   - [子功能1-2]

2. [功能2描述]
   - [子功能2-1]
   - [子功能2-2]

技术栈：
- 前端：[技术栈]
- 后端：[技术栈]
- 数据库：[数据库]

V4.0要求：
- 使用三方对抗设计
- 前端由Gemini实现
- 后端由Codex实现
- 所有长任务使用渐进式休眠
- 测试通过立即Git提交
- 实时更新QUICK_RESUME.md

约束条件：
- [性能要求]
- [安全要求]
- [其他约束]

请自主完成，我会在 docs/progress/QUICK_RESUME.md 查看进度。
```

### V4.0执行过程

#### 阶段P0: 架构设计（用户审查）

**执行者**: Opus 4.5
**输出**: `docs/project/master-plan.md`

```
Claude会输出：
"📋 项目架构设计已完成，请查看 docs/project/master-plan.md

⚠️ 请确认架构设计是否符合预期。
确认后输入'继续'，如需修改请说明。"

【此时工作流会暂停，等待您的输入】
```

**您需要做什么**：
1. 打开 `docs/project/master-plan.md` 查看架构设计
2. 如果满意，输入 `继续`
3. 如果需要修改，输入修改意见，如 `Auth模块需要支持OAuth登录`

#### 阶段1: 三方对抗设计

**步骤1 - Designer起草（Gemini 3.0 Pro）**:
- 生成 `01-design-draft.md`
- 覆盖前后端完整设计
- 包含所有功能点

**步骤2 - Critic红队攻击（Codex 5.1）**:
- 生成 `01-critique.md`
- 找出设计漏洞、遗漏、边缘案例
- 标记Critical/High/Medium/Low问题

**步骤3 - Designer修正（如有Critical/High问题）**:
- 生成 `01-design-v2.md`
- 修复所有Critical和High问题

**步骤4 - Architect终审（Opus 4.5）**:
- 生成 `01-architect-verdict.md`
- 决策：✅ 批准 或 ❌ 驳回
- 如果驳回，返回步骤1

#### 阶段2-3: 任务拆解与上下文准备

**自动进行**，生成：
- `02-tasks.md`
- `03-handover-card.md` ← V4.0强制交接文档

#### 阶段4: 代码实现（渐进式休眠）

**前端任务 → Gemini 3.0 Pro**:
```
交接文档会包含：

⚠️ 重要：请在执行过程中渐进式休眠防止超时：
- 第1次工作5分钟后休眠10秒
- 第2次工作5分钟后休眠30秒
- 第3次及以后每工作5分钟休眠1分钟

[任务背景、要求、约束、验收标准...]
```

**后端任务 → Codex 5.1**:
- 同样包含渐进式休眠指令
- 实现API、数据库、业务逻辑

#### 阶段5-7: 审查、修正、测试

**自动进行**，串行执行

#### 阶段8: Git提交（立即执行）

**触发条件**: 阶段7测试通过（Exit Code 0）

**执行者**: Haiku 4.5

**操作**:
```bash
git status
git add [相关文件]
git commit -m "[Haiku生成的commit message]

🤖 Generated with Claude Code

Co-Authored-By: Claude <noreply@anthropic.com>"
```

**同时更新**: `docs/progress/HISTORY.md`
```markdown
## 2025-11-30 14:23:15
- 任务：实现用户注册功能
- Commit: abc123f
- 状态：✅ 完成
```

#### 阶段9: 模块交付

**生成交付文档**: `09-delivery.md`

**循环**: 如果还有待完成的模块，返回阶段2

---

## 📱 进度监控与恢复

### V4.0的4层记忆系统

```
┌─────────────────────────────────────────┐
│ L0: QUICK_RESUME.md                     │
│  - 最新状态：Auth模块实现中              │
│  - 当前步骤：阶段4 后端实现              │
│  - 最近3次操作                          │
│  - 恢复指令："继续完成Auth模块后端"      │
│  📱 手机查看这个文件即可了解一切          │
└─────────────────────────────────────────┘
         ▼
┌─────────────────────────────────────────┐
│ L1: PROJECT_CONTEXT.md                  │
│  - 项目：用户管理系统                   │
│  - 已完成：1/2 模块                     │
│  - 待完成：Profile模块                  │
└─────────────────────────────────────────┘
         ▼
┌─────────────────────────────────────────┐
│ L2: MODULE_CONTEXT.md                   │
│  - 当前模块：Auth                       │
│  - 前端状态：✅ 完成                     │
│  - 后端状态：🔄 进行中                   │
│  - 遗留问题：需要添加邮箱验证测试        │
└─────────────────────────────────────────┘
         ▼
┌─────────────────────────────────────────┐
│ L3: HISTORY.md                          │
│  - 完整时间线                           │
│  - 所有Git提交                          │
│  - 所有决策记录                         │
└─────────────────────────────────────────┘
```

### 手机实时监控

**方案1: 云盘同步（推荐）**

1. 将 `docs/progress/` 添加到OneDrive
2. 手机打开OneDrive App
3. 查看 `QUICK_RESUME.md`

**示例内容**:
```markdown
# 快速恢复

## 最新状态
- 时间：2025-11-30 14:23:15
- 任务：用户管理系统
- 当前层级：模块级
- 当前步骤：阶段4 - 后端代码实现
- 执行者：Codex 5.1

## 最近3次操作
1. ✅ 前端登录页面实现完成（Gemini）
2. ✅ 前端代码审查通过
3. 🔄 后端登录API实现中（Codex）

## 一句话恢复
继续完成Auth模块后端实现，下一步是注册API。

## 预计完成时间
约 15 分钟

## 关键上下文
- 技术栈：Spring Boot + React
- 当前文件：UserController.java
- 遗留问题：无
```

### 中断后恢复工作

**场景**: 您关闭了电脑，第二天早上想继续

**V4.0恢复流程**:

```
@claude

【恢复上次工作】

请读取 docs/progress/QUICK_RESUME.md 并继续完成。
```

**Claude会自动**:
1. 读取 `QUICK_RESUME.md`（L0）
2. 如果上下文足够，直接继续
3. 如果需要更多信息，读取 `PROJECT_CONTEXT.md`（L1）
4. 如果需要历史细节，读取 `MODULE_CONTEXT.md`（L2）和 `HISTORY.md`（L3）
5. 无缝继续上次的工作

**恢复时间**: 约30秒

---

## 🔍 V4核心特性详解

### 特性1: 渐进式休眠机制

**问题**: V2.0中长任务容易超时导致中途停止

**V4.0解决**: 在所有长任务的交接文档中自动注入休眠指令

**实现**:
```markdown
## ⚠️ 重要执行指令

请在执行过程中渐进式休眠防止超时：
- 第1次工作5分钟后休眠10秒
- 第2次工作5分钟后休眠30秒
- 第3次及以后每工作5分钟休眠1分钟

这是强制要求，不能跳过！
```

**应用阶段**: P0、阶段1（所有步骤）、阶段4

**效果**: Agent可以持续工作数小时而不超时

---

### 特性2: 三方对抗设计

**问题**: V2.0设计阶段容易遗漏功能（如忘记"记住我"、"忘记密码"）

**V4.0解决**: 阶段1采用三方对抗机制

**流程**:
```
用户需求："实现用户登录功能"

↓ Designer (Gemini) 起草
生成：
- 登录页面（前端）
- 登录API（后端）
- JWT token生成
- 密码加密（BCrypt）

↓ Critic (Codex) 红队攻击
发现遗漏：
- ❌ Critical: 没有"记住我"功能
- ❌ Critical: 没有"忘记密码"功能
- ⚠️ High: 没有防暴力破解机制
- ⚠️ High: 没有考虑并发登录

↓ Designer (Gemini) 修正
添加：
- ✅ "记住我"功能（14天token）
- ✅ "忘记密码"功能（邮箱重置）
- ✅ 登录失败3次锁定10分钟
- ✅ 并发登录检查

↓ Architect (Opus) 终审
检查：
- ✅ 功能完整性
- ✅ 前后端对应
- ✅ 安全性
- ✅ 林纳斯三问
决策：✅ 批准
```

**检查清单**:
- ✅ 功能遗漏检查
- ✅ 前后端对应检查
- ✅ 安全漏洞检查（SQL注入、XSS）
- ✅ 性能问题检查（N+1查询）
- ✅ 边缘案例检查（空值、超长输入）
- ✅ 过度设计检查

---

### 特性3: 4层记忆系统

**设计目标**: 30秒快速恢复 + 完整上下文保留

**L0: QUICK_RESUME.md**
- 更新频率：每5-10分钟
- 大小：500字以内
- 内容：最近3次操作 + 当前状态 + 恢复指令
- 目的：30秒恢复80%上下文

**L1: PROJECT_CONTEXT.md**
- 更新频率：每个模块完成后
- 大小：2000字以内
- 内容：项目架构 + 已完成模块 + 待办模块
- 目的：理解项目全貌

**L2: MODULE_CONTEXT.md**
- 更新频率：每个子模块完成后
- 大小：1000字以内
- 内容：模块设计 + 前后端状态 + 遗留问题
- 目的：理解当前模块

**L3: HISTORY.md**
- 更新频率：每个任务完成后追加
- 大小：无限制
- 内容：时间线 + Git提交ID + 决策记录
- 目的：完整历史追溯

**使用场景**:
```python
def 恢复工作():
    # 步骤1：读取L0（30秒）
    quick_resume = read("QUICK_RESUME.md")
    if 上下文足够:
        return 继续工作()

    # 步骤2：读取L1（如需更多上下文）
    project_context = read("PROJECT_CONTEXT.md")
    if 上下文足够:
        return 继续工作()

    # 步骤3：读取L2和L3（如需历史细节）
    module_context = read("MODULE_CONTEXT.md")
    history = read("HISTORY.md")
    return 继续工作()
```

---

### 特性4: 强制串行执行

**问题**: V2.0并行执行导致状态混乱、遗漏检查

**V4.0解决**: 主控强制串行执行

**主控逻辑**:
```python
class 主控Claude:
    def 执行工作流(self):
        while 还有任务:
            # 1. 选择当前任务（只能1个）
            当前任务 = self.选择下一个任务()

            # 2. 准备交接文档
            交接文档 = self.准备交接文档(
                任务=当前任务,
                包含渐进式休眠指令=True
            )

            # 3. 调用Agent（串行，等待完成）
            结果 = self.调用Agent(
                任务=当前任务,
                交接文档=交接文档,
                等待完成=True  # ⚠️ 强制等待
            )

            # 4. 验证Agent输出
            验证通过 = self.验证输出(
                检查文件是否生成=True,
                检查内容是否正确=True,
                检查测试是否通过=True
            )

            if not 验证通过:
                self.记录错误()
                self.进入修正流程()
                continue

            # 5. 更新记忆文件
            self.更新记忆(
                QUICK_RESUME=True,
                HISTORY=True
            )

            # 6. 如果测试通过，立即Git提交
            if 测试通过:
                self.立即Git提交()

            # 7. 选择下一个任务
```

**禁止行为**:
```python
# ❌ 禁止
def 错误的做法():
    # 同时调用多个Agent
    task1 = async_call_agent(agent1)
    task2 = async_call_agent(agent2)
    wait_all([task1, task2])

# ✅ 正确
def 正确的做法():
    # 串行调用
    result1 = call_agent(agent1)
    验证(result1)
    result2 = call_agent(agent2)
    验证(result2)
```

---

### 特性5: 立即Git提交

**问题**: V2.0容易忘记Git提交，或堆积多个功能后一起提交

**V4.0解决**: 阶段8强制规则

**流程**:
```
阶段7（测试验证）通过
         │
         ▼
    ┌─────────┐
    │ V4检查： │
    │ 测试通过？│
    └─────────┘
         │ YES
         ▼
┌─────────────────────────┐
│ 阶段8（Git提交）        │
│  1. 检查git status       │
│  2. 暂存文件            │
│  3. Haiku生成commit msg │
│  4. 执行git commit       │
│  5. 更新HISTORY.md      │
└─────────────────────────┘
         │
         ▼
   ✅ 立即选择下一个任务
   （不能停顿）
```

**强制规则**:
- ✅ 测试通过后立即Git提交
- ❌ 不允许堆积多个功能再一起提交
- ✅ 每个任务单独提交一次
- ✅ 自动更新 HISTORY.md（任务ID + Commit Hash）

**Commit Message格式**:
```
feat(auth): implement user login with JWT

- Add login API endpoint
- Add BCrypt password encryption
- Add JWT token generation
- Add "Remember Me" functionality
- Add rate limiting (3 attempts / 10min)

🤖 Generated with Claude Code

Co-Authored-By: Claude <noreply@anthropic.com>
```

---

### 特性6: P0用户审查

**问题**: V2.0架构设计后直接执行，用户无法审查

**V4.0解决**: 阶段P0增加强制用户审查步骤

**流程**:
```
1. Opus完成架构设计 → 生成 docs/project/master-plan.md

2. 主控输出消息：
   "📋 项目架构设计已完成，请查看 docs/project/master-plan.md

   ⚠️ 请确认架构设计是否符合预期。
   确认后输入'继续'，如需修改请说明。"

3. 等待用户输入（必须等待，不能自动继续）

4. 用户确认后，进入阶段0
```

**好处**:
- ✅ 用户可以在实施前审查架构
- ✅ 避免方向性错误
- ✅ 可以及时调整技术选型
- ✅ 增强用户控制感

---

### 特性7: 强制交接文档

**问题**: V2.0 Agent执行时缺少完整上下文

**V4.0解决**: 每次调用Agent必须准备交接文档

**交接文档模板**:
```markdown
# 交接文档

## ⚠️ 重要执行指令
[渐进式休眠指令...]

## 任务背景
项目：用户管理系统
模块：Auth
当前阶段：阶段4 - 后端代码实现
上游输出：前端登录页面已完成

## 任务要求
实现用户登录API

功能点：
- POST /api/users/login
- 输入：用户名/邮箱、密码、是否记住我
- 输出：JWT token、用户信息
- 加密：BCrypt
- 记住我：14天token
- 失败处理：3次锁定10分钟

## 约束条件
- 必须使用Spring Boot + MyBatis-Plus
- 必须遵守CLAUDE.md中的规范
- 不能修改User实体类
- 必须包含单元测试

## 验收标准
- [ ] API可以正常调用
- [ ] 密码正确时返回token
- [ ] 密码错误时返回401
- [ ] "记住我"功能正常
- [ ] 失败3次后锁定
- [ ] 单元测试覆盖率 ≥80%
- [ ] 代码通过编译
- [ ] 所有测试通过

## 相关文件
- 设计文档：docs/sessions/xxx/01-design-v2.md
- 前端实现：src/pages/Login.tsx
- 实体类：src/main/java/entity/User.java
- 需要创建：src/main/java/controller/AuthController.java
```

**主控准备交接文档的时机**:
- 阶段P0调用Opus
- 阶段1的每个步骤（Designer、Critic、Architect）
- 阶段4调用Gemini/Codex

---

## 🎭 SuperClaude增强系统

### SuperClaude在V4.0中的作用

V4.0完全集成了SuperClaude系统，提供：

| 组件 | V4.0中的应用 |
|------|-------------|
| **Persona角色** | 每个阶段自动激活对应Persona |
| **斜杠命令** | 简化复杂操作，如 `/build --react -magic -tdd` |
| **标志系统** | 控制MCP服务器、思维模式等 |

### V4.0阶段与Persona映射

```
P0  → architect (Opus)      - 系统架构设计
0   → analyzer (Sonnet)     - 需求分析
1   → 三方对抗
      ├─ designer (Gemini)  - 起草设计
      ├─ critic (Codex)     - 红队攻击
      └─ architect (Opus)   - 终审批准
2   → architect + qa        - 任务拆解
3   → dynamic               - 根据任务类型选择
4   → dynamic
      ├─ frontend (Gemini)  - 前端实现
      └─ backend (Codex)    - 后端实现
5   → refactorer + security + qa - 代码审查
6   → 继承阶段4              - 代码修正
7   → qa + performance      - 测试验证
8   → none (Haiku)          - Git提交
9   → dynamic               - 模块交付
```

### V4.0推荐命令

**阶段P0 - 架构设计**:
```bash
/sc:design --architecture --plan -think-hard -c7
```

**阶段1.步骤1 - Designer起草**:
```bash
/sc:design --api --ddd -plan -c7
```

**阶段1.步骤2 - Critic攻击**:
```bash
/sc:improve --quality --evidence
# 或
/sc:reflect --evidence
```

**阶段4 - 前端实现**:
```bash
/sc:build --react --magic --tdd -c7
```

**阶段4 - 后端实现**:
```bash
/sc:build --api --tdd --coverage -c7
```

**阶段5 - 代码审查**:
```bash
/sc:review --quality --evidence -think-hard
/sc:scan --security --owasp
```

**阶段7 - 测试验证**:
```bash
/sc:test --unit --integration --e2e --coverage
# 如果是Web项目
/sc:test --unit --integration --e2e --coverage -pup
```

### V4.0常用标志

| 标志 | 用途 | V4.0推荐使用场景 |
|------|------|-----------------|
| `-plan` | 查看执行计划 | P0、阶段1、关键操作 |
| `-tdd` | 测试驱动开发 | **阶段4必用** |
| `-c7` | 查找库文档 | 阶段1、3、4 |
| `-seq` | 深度思维 | 阶段0、1、复杂分析 |
| `-magic` | UI构建器 | 阶段4前端开发 |
| `-pup` | 浏览器测试 | 阶段7 Web项目测试 |

---

## 🔧 故障排查

### 问题1: Agent超时中途停止

**症状**: 长任务执行到一半突然停止

**V4.0解决**: 渐进式休眠机制

**检查**:
```
1. 打开交接文档（如 03-handover-card.md）
2. 检查是否包含渐进式休眠指令
3. 如果缺失，手动添加到prompt中
```

**预防**: V4.0已自动注入，不应出现此问题

---

### 问题2: 功能遗漏

**症状**: 实现后发现缺少某些子功能

**V4.0解决**: 三方对抗设计

**检查**:
```
1. 查看 01-critique.md
2. 检查Critic是否标记了该遗漏
3. 如果标记了但Designer没修正，查看 01-design-v2.md
4. 如果Architect批准了有遗漏的设计，反馈给主控
```

**补救**:
```
@claude

发现设计遗漏：[描述遗漏的功能]

请：
1. 重新进入阶段1三方对抗设计
2. 确保Critic检查该遗漏
3. Designer修正设计
4. Architect重新审批
```

---

### 问题3: 恢复工作时遗忘上下文

**症状**: 中断后恢复，Claude不知道之前做了什么

**V4.0解决**: 4层记忆系统

**恢复步骤**:
```
@claude

【恢复上次工作】

请按以下顺序恢复上下文：

1. 读取 docs/progress/QUICK_RESUME.md（L0）
2. 如果上下文不够，读取 docs/progress/PROJECT_CONTEXT.md（L1）
3. 如果需要模块详情，读取 docs/progress/MODULE_CONTEXT.md（L2）
4. 如果需要历史追溯，读取 docs/progress/HISTORY.md（L3）

然后继续完成任务。
```

**预防**: 确保工作流实时更新这4个文件

---

### 问题4: Git提交堆积

**症状**: 多个功能完成后才一起提交

**V4.0解决**: 立即Git提交机制

**检查**:
```
1. 查看 .claude/workflow.json
2. 确认阶段7和阶段8之间是否有立即执行规则
3. 确认测试通过后是否自动进入阶段8
```

**修正**:
```
@claude

检测到Git提交堆积。

V4.0要求：
- 阶段7测试通过后，立即进入阶段8
- 每个功能单独提交一次
- 禁止堆积多个功能

请遵守此规则。
```

---

### 问题5: MCP服务器连接失败

**症状**: 无法调用Codex或Gemini

**检查**:
```bash
# 在Claude Code中
/mcp list

# 应该看到：
# ✅ codex (connected)
# ✅ gemini (connected)

# 如果看到：
# ❌ codex (disconnected)
```

**解决**:
```bash
# 1. 检查API Key
cat C:\Users\lenovo\.claude\config.json

# 2. 测试API Key
# Codex
curl https://api.openai.com/v1/models \
  -H "Authorization: Bearer YOUR_KEY"

# Gemini
curl "https://generativelanguage.googleapis.com/v1/models?key=YOUR_KEY"

# 3. 重启Claude Code

# 4. 重新测试
/mcp list
```

---

## 💡 最佳实践

### 实践1: 任务描述要全面

❌ **不好的描述**:
```
@claude 做一个用户登录
```

✅ **好的描述**:
```
@claude

【使用V4.0工作流系统】

项目：用户管理系统
模块：Auth - 用户登录

功能点：
1. 登录页面（前端）
   - 用户名/邮箱输入
   - 密码输入
   - "记住我"复选框
   - "忘记密码"链接
   - 表单验证

2. 登录API（后端）
   - POST /api/users/login
   - BCrypt密码验证
   - JWT token生成
   - "记住我"功能（14天token）
   - 失败3次锁定10分钟

技术栈：
- 前端：React + Ant Design （Gemini实现）
- 后端：Spring Boot + MyBatis-Plus （Codex实现）

约束：
- 遵守CLAUDE.md规范
- 单元测试覆盖率 ≥80%
- 响应时间 <100ms

请使用V4.0工作流自主完成：
- P0架构设计等待我审查
- 阶段1使用三方对抗设计
- 渐进式休眠防止超时
- 测试通过立即Git提交
- 实时更新QUICK_RESUME.md
```

---

### 实践2: P0架构审查要仔细

**审查清单**:
```
[ ] 技术栈选择是否合理？
[ ] 模块划分是否清晰？
[ ] 是否有性能瓶颈？
[ ] 是否有安全风险？
[ ] 是否过度设计？
[ ] 是否缺少必要模块？
[ ] 是否符合项目规范（CLAUDE.md）？
```

**如果不满意**:
```
@claude

架构设计需要调整：

1. 技术栈：不要用XXX，改用YYY
   理由：[...]

2. 模块划分：Auth模块太大，拆分成：
   - Authentication（认证）
   - Authorization（授权）
   - Profile（资料）

3. 性能优化：添加Redis缓存层

请重新设计架构。
```

---

### 实践3: 定期查看QUICK_RESUME.md

**推荐频率**: 每30分钟

**查看内容**:
- 当前在做什么？
- 进度如何？
- 是否遇到问题？
- 预计何时完成？

**如果发现问题，及时介入**:
```
@claude

看到QUICK_RESUME.md中提到：[某个问题]

我的建议：[...]

请调整后继续。
```

---

### 实践4: 维护CLAUDE.md

**定期更新**（每个模块完成后）:

```markdown
# 项目开发规范

## 经验教训（新增）

### Auth模块
- ✅ JWT token应设置合理过期时间（不要太长）
- ✅ "记住我"token和普通token分开
- ⚠️ 密码重置链接有效期不要超过1小时
- ❌ 不要在前端存储明文密码

### Profile模块
- ...
```

---

### 实践5: 保留所有历史记录

**不要删除**:
```
docs/
├── project/
│   └── master-plan.md          ← 保留
├── progress/
│   ├── QUICK_RESUME.md         ← 保留
│   ├── PROJECT_CONTEXT.md      ← 保留
│   ├── MODULE_CONTEXT.md       ← 保留
│   └── HISTORY.md              ← 保留
└── sessions/
    ├── 20251130-auth/          ← 保留所有会话
    └── 20251201-profile/       ← 保留所有会话
```

**好处**:
- ✅ 可以回溯决策过程
- ✅ 可以学习改进
- ✅ 可以作为项目文档
- ✅ 可以快速恢复到任意时间点

---

## ❓ 常见问题

### Q1: V4.0与V2.0能否共存？

**A**: 可以

```
您的项目/
├── .claude/
│   ├── workflow.json           ← V4.0配置
│   ├── workflow-v2.json        ← V2.0配置（备份）
│   └── ...
```

使用时指定版本：
```
@claude 使用 .claude/workflow.json（V4.0）完成任务
```

---

### Q2: 如何跳过P0用户审查？

**A**: 不推荐，但可以修改配置

编辑 `.claude/workflow.json`:
```json
"阶段P0": {
  "用户审查": {
    "启用": false  ← 改为false
  }
}
```

**警告**: 跳过审查可能导致方向性错误

---

### Q3: 如何调整渐进式休眠时间？

**A**: 编辑 `.claude/workflow.json`

```json
"渐进式休眠策略": {
  "第1次": "工作10分钟 → 休眠15秒",  ← 自定义
  "第2次": "工作10分钟 → 休眠45秒",  ← 自定义
  "第3次及以后": "工作10分钟 → 休眠2分钟"  ← 自定义
}
```

---

### Q4: 如何禁用三方对抗设计？

**A**: 不推荐，但可以修改配置

编辑 `.claude/workflow.json`:
```json
"阶段1": {
  "三方对抗": {
    "启用": false,  ← 改为false
    "回退到V2模式": true
  }
}
```

**警告**: 禁用后可能遗漏功能

---

### Q5: 前端也想用Codex，怎么办？

**A**: 修改模型选择策略

编辑 `.claude/workflow.json`:
```json
"模型选择策略": {
  "阶段4_前端": "gpt-5.1-codex",  ← 改为codex
  "阶段4_后端": "gpt-5.1-codex"
}
```

或在任务描述中指定：
```
@claude

【前端也用Codex实现】

[任务描述...]
```

---

### Q6: 如何查看完整的Git提交历史？

**A**: 查看 `docs/progress/HISTORY.md`

或使用Git命令：
```bash
git log --oneline --decorate --graph
```

---

### Q7: QUICK_RESUME.md没有实时更新？

**A**: 检查工作流配置

编辑 `.claude/workflow.json`:
```json
"V4_强制记录更新": {
  "QUICK_RESUME更新频率": "每5-10分钟",  ← 检查此项
  "强制执行": true
}
```

手动触发更新：
```
@claude

请立即更新 docs/progress/QUICK_RESUME.md，包含：
- 当前状态
- 最近3次操作
- 一句话恢复指令
```

---

### Q8: 如何查看Critic的完整攻击报告？

**A**: 查看会话目录

```
docs/sessions/<session-id>/
└── 01-critique.md  ← Critic的完整报告
```

包含：
- Critical问题列表
- High问题列表
- Medium问题列表
- Low问题列表
- 改进建议

---

## 📞 获取帮助

### 遇到问题？

1. **查看V4.0文档**:
   - 📖 [README.md](./README.md) - 快速了解V4.0
   - 📐 [ARCHITECTURE.md](./ARCHITECTURE.md) - 理解系统设计
   - 📖 本文档 - 详细使用指南

2. **查看进度文件**:
   - `docs/progress/QUICK_RESUME.md` - 当前状态
   - `docs/progress/HISTORY.md` - 历史记录

3. **查看错误日志**:
   - `docs/sessions/<session-id>/errors.md`

### 需要定制？

V4.0完全可定制：
- 修改 `.claude/workflow.json` - 核心工作流
- 修改 `.claude/persona-mapping.json` - Persona映射
- 修改 `.claude/superclaude-integration.json` - SuperClaude配置
- 修改 `CLAUDE.md` - 项目规范

---

## ✅ 检查清单

### 首次使用前

```
[ ] Claude Code已安装（最新版）
[ ] Codex MCP已配置并测试通过
[ ] Gemini MCP已配置并测试通过
[ ] Git已初始化
[ ] V4.0目录结构已创建（docs/progress等）
[ ] workflow.json已复制到.claude/
[ ] persona-mapping.json已复制到.claude/
[ ] superclaude-integration.json已复制到.claude/
[ ] CLAUDE.md已创建
[ ] 4层记忆文件已初始化
      [ ] QUICK_RESUME.md
      [ ] PROJECT_CONTEXT.md
      [ ] MODULE_CONTEXT.md
      [ ] HISTORY.md
[ ] 云盘同步已配置（推荐）
[ ] 已测试简单任务
[ ] 已验证P0用户审查功能
[ ] 已验证三方对抗设计功能
[ ] 已验证渐进式休眠功能
```

---

## 🎉 开始使用

一切就绪后，开始您的第一个V4.0自主任务：

```
@claude

【使用V4.0工作流系统】

项目：[您的项目名称]

核心功能：
1. [功能1]
   - [子功能1-1]
   - [子功能1-2]

2. [功能2]
   - [子功能2-1]
   - [子功能2-2]

技术栈：
- 前端：[技术栈]
- 后端：[技术栈]
- 数据库：[数据库]

V4.0要求：
- P0架构设计完成后等待我审查
- 阶段1使用三方对抗设计
- 前端由Gemini实现
- 后端由Codex实现
- 所有长任务使用渐进式休眠
- 测试通过立即Git提交
- 实时更新QUICK_RESUME.md

请自主完成，我会在 docs/progress/QUICK_RESUME.md 查看进度。
```

然后，坐下来，喝杯咖啡，看着V4.0团队为您工作吧！☕️

---

**版本**: 4.0
**最后更新**: 2025-11-30
**作者**: Claude Code团队
**适用于**: 企业级、零中断、零遗忘、零失误的自主开发工作流

**🎉 V4.0已就绪，开始您的零失误开发之旅！**
