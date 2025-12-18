# 故障排查指南（Troubleshooting Guide）

> **版本**：V4.0
> **更新时间**：2025-11-30
> **适用场景**：遇到问题时的快速排查指南

---

## 📋 概述

本指南涵盖 V4.0 系统中最常见的问题和解决方案，特别是：
1. **中文乱码问题**（最常见）
2. **功能遗漏问题**
3. **测试失败问题**
4. **记忆系统问题**（V4.0特有）
5. **Git提交问题**（V4.0特有）
6. **超时问题**（V4.0已解决）

---

## 🚨 问题1：中文乱码（TOP PRIORITY）

### 症状
- 数据库中中文显示为 `???` 或乱码
- API 响应中中文显示为 `??????` 或 `\uXXXX`
- 前端页面中文显示为方框或乱码
- 测试中中文断言失败

### 根本原因
**编码配置不统一**，可能的原因：
1. 源文件不是 UTF-8 编码
2. 数据库字符集不是 utf8mb4
3. 连接字符串缺少 characterEncoding=utf8mb4
4. API 响应头缺少 charset=UTF-8
5. 前端缺少 meta charset 或 Axios 配置错误

### 排查步骤

#### Step 1: 运行编码验证脚本
```bash
C:\Users\lenovo\.claude\workflows\v4.0\scripts\validate-encoding.bat
```

**预期结果**：Exit Code 0

**如果失败**，查看输出，定位问题文件。

#### Step 2: 检查源文件编码

##### 后端（Java）
**检查方法**：
1. 打开 IDEA
2. 查看右下角编码显示
3. 应该显示 "UTF-8"

**如果不是 UTF-8**：
1. 打开 Settings → Editor → File Encodings
2. 设置：
   - Global Encoding: UTF-8
   - Project Encoding: UTF-8
   - Default encoding for properties files: UTF-8
3. 重新打开文件，选择 "Reload" (UTF-8)

##### 前端（React）
**检查方法**：
1. 打开 VSCode
2. 查看右下角编码显示
3. 应该显示 "UTF-8"

**如果不是 UTF-8**：
1. 点击右下角编码
2. 选择 "Save with Encoding"
3. 选择 "UTF-8"

#### Step 3: 检查数据库编码

##### 检查表字符集
```sql
SHOW CREATE TABLE users;
```

**预期结果**：
```sql
CREATE TABLE `users` (
  ...
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**如果不是 utf8mb4**：
```sql
ALTER TABLE users CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

### 快速修复清单

- [ ] ✅ 源文件：UTF-8 编码
- [ ] ✅ 数据库表：utf8mb4 字符集
- [ ] ✅ 连接字符串：characterEncoding=utf8mb4&useUnicode=true
- [ ] ✅ API 响应头：charset=UTF-8
- [ ] ✅ HTML meta：charset="UTF-8"
- [ ] ✅ Axios：responseEncoding: 'utf8'
- [ ] ✅ 中文测试：测试通过

---

## 🚨 问题2：功能遗漏

### 症状
- 模块只实现了部分子功能
- 遗漏了设计文档中的某些子功能
- 只实现了后端，遗漏了前端

### V4.0 的防遗漏机制
1. **三方对抗设计**：Designer → Critic → Architect
2. **Critic 检查清单**：guides/ANTI_FORGET_CHECKLIST.md
3. **双检查点**：submodule + module

### 排查步骤

#### Step 1: 检查三方对抗设计文档

**检查清单**：
- [ ] 01-design-draft.md 存在且完整？
- [ ] 01-critique.md 存在且发现问题全部修复？
- [ ] 01-architect-verdict.md 批准通过？

**如果遗漏**：
1. 回到阶段1（三方对抗设计）
2. 补充遗漏的设计
3. 重新走完三方流程

#### Step 2: 检查Critic的问题列表

打开 `01-critique.md`

**检查**：
- Critical 问题：[X]个，全部修复？
- High 问题：[Y]个，全部修复？

**如果未修复**：
1. 逐个修复问题
2. 更新设计文档
3. Architect 重新审核

#### Step 3: 执行模块检查点
运行 `module-checkpoint.md`

**检查**：
- 子模块完成度：[X / Y]
- 前端页面完整性
- 后端 API 完整性

---

## 🚨 问题3：记忆系统失效（V4.0特有）

### 症状
- 重启后 Claude 不记得之前的工作
- QUICK_RESUME.md 内容过时或为空
- 无法快速恢复会话（超过30秒）

### V4.0 的4层记忆系统
- **L0: QUICK_RESUME.md** - 30秒恢复
- **L1: PROJECT_CONTEXT.md** - 项目概览
- **L2: MODULE_CONTEXT.md** - 模块详情
- **L3: HISTORY.md** - 完整历史

### 排查步骤

#### Step 1: 运行恢复脚本
```bash
C:\Users\lenovo\.claude\workflows\v4.0\scripts\resume.bat
```

**预期输出**：
```
## V4.0 Quick Resume
- Current Module: [模块名]
- Current Stage: [阶段]
- Last 3 Operations: ...
- Next Step: ...
```

#### Step 2: 检查QUICK_RESUME.md

打开 `docs/progress/QUICK_RESUME.md`

**必须包含**：
- 当前状态（模块、阶段、进度）
- 最近3次操作（时间倒序）
- 下一步计划
- 重要提醒

**格式要求**：
- 总长度 ≤ 500字
- 一句话恢复指令

**如果格式错误**：
```markdown
## 当前状态
- 模块：Auth 模块
- 阶段：Stage 5（后端实现）
- 进度：3/7 完成

## 最近3次操作
1. ✅ 完成"用户登录"后端 (2025-11-30 10:30)
2. ✅ 通过子模块检查点 (2025-11-30 10:25)
3. ⏳ 开始"用户登出"后端 (进行中)

## 下一步计划
- [ ] 完成"用户登出"后端实现

## 一句话恢复
继续实现Auth模块的"用户登出"后端功能，已完成3/7子模块
```

#### Step 3: 验证30秒恢复

**测试方法**：
1. 关闭当前会话
2. 打开新会话
3. 运行 `resume.bat`
4. 让 Claude 读取输出
5. 询问："我现在在做什么？"

**预期**：Claude 能在30秒内准确回答当前状态

---

## 🚨 问题4：Git提交问题（V4.0特有）

### 症状
- 测试通过但忘记提交
- 多个功能累积后一次性提交
- 提交消息不规范

### V4.0 的Git提交机制
- **立即提交**：测试通过后立即提交
- **单功能提交**：每个功能单独提交
- **规范消息**：包含功能描述、测试结果、Claude标记

### 排查步骤

#### Step 1: 检查Git状态
```bash
git status
```

**症状识别**：
- 大量未提交的文件 → 违反立即提交原则
- 多个功能混在一起 → 违反单功能提交

#### Step 2: 立即补救提交

如果测试已通过但未提交：
```bash
# 立即提交当前功能
git add [当前功能相关文件]
git commit -m "feat(模块): [功能描述]

- 实现了[具体内容]

测试：
- 单元测试通过 ✅
- 中文测试通过 ✅

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>"
```

#### Step 3: 更新HISTORY.md

立即记录到 `docs/progress/HISTORY.md`：
```markdown
### [时间] - feat: [功能名]
- **Commit**: [hash]
- **测试**: 通过 ✅
- **说明**: [简短说明]
```

---

## 🚨 问题5：超时问题（V4.0已解决）

### V4.0 的渐进式休眠机制

**三级休眠**：
1. **5min → 10s**：常规任务
2. **5min → 30s**：中等任务
3. **5min → 1min**：长任务

### 如果仍然超时

#### 症状
- Claude 停止响应超过5分钟
- 任务执行中断

#### 处理
1. **检查休眠信号**：工作流是否正确发送休眠信号
2. **检查任务大小**：是否需要拆分任务
3. **使用恢复机制**：
   ```bash
   C:\Users\lenovo\.claude\workflows\v4.0\scripts\resume.bat
   ```
4. **30秒恢复**：基于QUICK_RESUME.md恢复上下文

---

## 🛠️ 常用诊断命令

### 后端（Spring Boot）
```bash
# 运行测试
mvn test

# 运行特定测试
mvn test -Dtest=UserServiceTest

# 查看测试覆盖率
mvn test jacoco:report

# 构建项目
mvn clean package

# 运行应用
mvn spring-boot:run
```

### 前端（React）
```bash
# 运行测试
npm test

# 查看测试覆盖率
npm test -- --coverage

# 构建生产版本
npm run build

# 启动开发服务器
npm start
```

### 编码验证
```bash
# 运行编码验证脚本
C:\Users\lenovo\.claude\workflows\v4.0\scripts\validate-encoding.bat
```

### 恢复会话
```bash
# V4.0 快速恢复
C:\Users\lenovo\.claude\workflows\v4.0\scripts\resume.bat
```

---

## 📊 问题严重程度分级

### 🔴 Critical（必须立即修复）
- 中文乱码（影响用户体验）
- 测试失败（阻塞部署）
- 记忆系统失效（V4.0）
- Git未提交（V4.0）
- 三方对抗设计Critical问题未修复（V4.0）

### 🟠 High（强烈建议修复）
- 功能遗漏
- 性能问题（响应时间 > 1秒）
- 前端或后端遗漏

### 🟡 Medium（建议修复）
- 测试覆盖率不足
- 代码规范问题
- 文档缺失

### 🟢 Low（可选修复）
- 小的 UI 问题
- 优化建议

---

## 📝 故障记录模板

当遇到问题时，记录到 `docs/progress/issues.md`：

```markdown
## Issue #[ID]：[问题标题]

**发现时间**：[时间]
**严重程度**：🔴 Critical / 🟠 High / 🟡 Medium / 🟢 Low
**V4.0阶段**：[阶段编号]

**症状**：
[详细描述问题现象]

**根本原因**：
[分析根本原因]

**解决方案**：
[详细描述如何修复]

**验证**：
[如何验证问题已解决]

**预防措施**：
[如何防止问题再次发生]

**V4.0机制**：
[相关的V4.0机制，如：三方对抗、4层记忆、渐进式休眠等]

**解决时间**：[时间]
**解决者**：[Sonnet/Gemini/Codex/Opus]
```

---

## 🎯 总结

### 最常见的5个问题（V4.0）
1. **中文乱码**（60%）→ 编码配置不统一
2. **功能遗漏**（15%）→ 未使用三方对抗设计
3. **记忆系统失效**（10%）→ 未及时更新4层记忆
4. **Git未提交**（10%）→ 违反立即提交原则
5. **测试失败**（5%）→ 编码或逻辑错误

### 快速排查流程
1. **编码问题**：运行 `validate-encoding.bat`
2. **功能遗漏**：检查三方对抗设计文档
3. **记忆失效**：运行 `resume.bat`
4. **Git问题**：`git status` + 立即提交
5. **测试失败**：查看测试日志，逐个修复

### V4.0 的优势
- ✅ 三方对抗设计 → 减少遗漏
- ✅ 4层记忆系统 → 30秒恢复
- ✅ 渐进式休眠 → 不会超时
- ✅ 立即Git提交 → 不会遗忘

---

**遇到问题，先查本指南！V4.0让问题更少、恢复更快！**
