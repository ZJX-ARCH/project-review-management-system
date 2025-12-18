# 前端任务微规格

**任务ID**: T001-Frontend
**模块**: [模块名称]
**功能**: [功能描述]
**优先级**: P0 / P1 / P2
**预估时间**: XX分钟

---

## 任务目标

[用一句话描述这个前端任务要实现什么]

---

## 上下文

### 相关设计
- 设计文档: `01-design-final.md`
- UI设计稿: [Figma/设计文件链接或描述]
- 交互原型: [原型链接或描述]

### 依赖的后端API
| API端点 | 方法 | 用途 | 状态 |
|---------|------|------|------|
| `/api/users` | GET | 获取用户列表 | ✅ 已实现 / ⏳ 待实现 |
| `/api/users/{id}` | GET | 获取用户详情 | ✅ 已实现 / ⏳ 待实现 |
| `/api/users` | POST | 创建用户 | ✅ 已实现 / ⏳ 待实现 |

### 技术栈
- 框架: React 18 / Vue 3 / Angular / [其他]
- 状态管理: Redux / Zustand / Pinia / [其他]
- 样式方案: Tailwind CSS / CSS Modules / Styled Components / [其他]
- UI组件库: Ant Design / Material-UI / [其他] / 无

---

## 需要实现的内容

### 1. 页面/组件

#### 组件1: [组件名称]
**文件路径**: `src/components/[ComponentName].tsx`
**组件类型**: 页面组件 / 业务组件 / 通用组件
**组件职责**: [一句话说明组件的职责]

**Props定义**:
```typescript
interface [ComponentName]Props {
  userId: string;
  onSave: (data: UserData) => void;
  onCancel: () => void;
}
```

**State定义**:
```typescript
interface [ComponentName]State {
  loading: boolean;
  error: string | null;
  formData: UserFormData;
}
```

**主要功能**:
- [ ] 功能1: [描述]
- [ ] 功能2: [描述]
- [ ] 功能3: [描述]

**UI要求**:
- 布局: [描述布局结构]
- 样式: [描述关键样式要求]
- 响应式: [描述响应式要求]

#### 组件2: [组件名称]
[同上格式]

### 2. 路由配置

**新增路由**:
```typescript
{
  path: '/users',
  component: UserListPage,
  meta: {
    title: '用户列表',
    requiresAuth: true
  }
}
```

**路由守卫**:
- [ ] 需要认证
- [ ] 需要权限检查
- [ ] 其他守卫逻辑

### 3. 状态管理

#### Store/State模块
**模块名称**: `userStore` / `userSlice`
**文件路径**: `src/store/modules/user.ts`

**State结构**:
```typescript
interface UserState {
  users: User[];
  currentUser: User | null;
  loading: boolean;
  error: string | null;
}
```

**Actions/Mutations**:
- `fetchUsers()` - 获取用户列表
- `fetchUserById(id)` - 获取用户详情
- `createUser(data)` - 创建用户
- `updateUser(id, data)` - 更新用户
- `deleteUser(id)` - 删除用户

### 4. API集成

**API Service文件**: `src/services/userService.ts`

**API函数**:
```typescript
export const userService = {
  async getUsers(): Promise<User[]> {
    const response = await axios.get('/api/users');
    return response.data;
  },

  async getUserById(id: string): Promise<User> {
    const response = await axios.get(`/api/users/${id}`);
    return response.data;
  },

  async createUser(data: CreateUserDto): Promise<User> {
    const response = await axios.post('/api/users', data);
    return response.data;
  }
};
```

### 5. 表单处理

**表单验证规则**:
```typescript
const validationSchema = {
  username: {
    required: true,
    minLength: 3,
    maxLength: 20,
    pattern: /^[a-zA-Z0-9_]+$/
  },
  email: {
    required: true,
    pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  },
  password: {
    required: true,
    minLength: 8
  }
};
```

**错误提示**:
- 用户名不能为空
- 用户名长度必须在3-20个字符之间
- 邮箱格式不正确
- 密码长度不能少于8位

### 6. 交互细节

#### 加载状态
- [ ] API调用时显示loading spinner
- [ ] 禁用提交按钮防止重复提交
- [ ] 加载骨架屏（如适用）

#### 错误处理
- [ ] 网络错误提示
- [ ] API错误提示（根据错误码显示不同消息）
- [ ] 表单验证错误提示

#### 成功反馈
- [ ] 操作成功toast提示
- [ ] 自动关闭弹窗
- [ ] 刷新列表数据

---

## 约束条件

### 必须遵守
- [x] 使用TypeScript编写，类型定义完整
- [x] 遵守项目的ESLint和Prettier配置
- [x] 组件文件不超过300行，超过需拆分
- [x] 使用函数式组件和Hooks（React）
- [x] CSS类名使用BEM命名规范（或项目约定）
- [x] 可访问性（a11y）基本要求（语义化HTML、ARIA属性）

### 禁止操作
- [ ] 不使用any类型（除非绝对必要）
- [ ] 不直接修改props
- [ ] 不在组件内部直接调用localStorage（使用统一的storage service）
- [ ] 不引入未经批准的第三方库

### 性能要求
- [ ] 列表使用虚拟滚动（如果列表很长）
- [ ] 图片使用懒加载
- [ ] 避免不必要的re-render（使用memo、useMemo、useCallback）
- [ ] 大型计算使用useMemo缓存

---

## 验收标准

### 功能验收
- [ ] 页面能够正常访问（路由正确）
- [ ] 所有UI元素按设计稿正确显示
- [ ] 表单提交功能正常
- [ ] 数据能够正确显示和更新
- [ ] 所有交互功能正常（点击、hover、focus等）

### UI验收
- [ ] 在Chrome/Firefox/Safari中显示一致
- [ ] 响应式布局在移动端正常
- [ ] 字体、颜色、间距符合设计规范
- [ ] 交互动画流畅

### 代码验收
- [ ] TypeScript编译无错误
- [ ] ESLint检查无错误
- [ ] 单元测试通过（覆盖率>80%）
- [ ] 组件测试通过（使用React Testing Library或Vue Test Utils）

### 浏览器控制台检查
```bash
# 使用Puppeteer自动检查
npm run test:console

# 预期结果
✅ No console errors
✅ No network errors
✅ All resources loaded
```

### 自动化验证命令
```bash
# 编译检查
npm run build

# 类型检查
npm run type-check

# 代码规范检查
npm run lint

# 单元测试
npm run test:unit

# 组件测试
npm run test:component

# E2E测试（如有）
npm run test:e2e
```

---

## 测试要求

### 单元测试
**测试文件**: `src/components/__tests__/[ComponentName].test.tsx`

**测试用例**:
- [ ] 组件正常渲染
- [ ] Props正确传递和显示
- [ ] 事件处理函数正确调用
- [ ] 状态更新正常
- [ ] 边界条件处理（空数据、错误状态等）

### 组件测试
- [ ] 用户交互测试（点击按钮、输入表单）
- [ ] API调用mock测试
- [ ] 错误场景测试
- [ ] 加载状态测试

---

## 参考文件

### 需要阅读的文件
- `CLAUDE.md` - 项目开发规范
- `src/types/user.ts` - 用户相关类型定义
- `src/components/common/Button.tsx` - 通用按钮组件示例
- `src/services/api.ts` - API调用封装

### 可参考的类似实现
- `src/pages/ProductList.tsx` - 类似的列表页面实现
- `src/components/UserForm.tsx` - 类似的表单组件实现

---

## 实现检查清单

### 开始前
- [ ] 阅读相关设计文档
- [ ] 确认后端API已实现（或有mock）
- [ ] 确认UI设计稿可访问
- [ ] 拉取最新代码

### 实现中
- [ ] 创建组件文件和测试文件
- [ ] 定义TypeScript类型
- [ ] 实现组件逻辑
- [ ] 添加样式
- [ ] 编写测试
- [ ] 本地验证功能

### 完成后
- [ ] 代码自检（遵守规范）
- [ ] 运行所有验收命令
- [ ] 浏览器手工测试
- [ ] 更新相关文档
- [ ] 生成实现报告（04-implementation.md）

---

## 预期输出文件

### 新增文件
- `src/pages/[PageName].tsx` - 页面组件
- `src/components/[ComponentName].tsx` - 业务组件
- `src/store/modules/[module].ts` - 状态管理
- `src/services/[service].ts` - API服务
- `src/__tests__/[ComponentName].test.tsx` - 测试文件

### 修改文件
- `src/router/index.ts` - 添加路由配置
- `src/store/index.ts` - 注册新的store模块

---

## 备注

[任何需要特别说明的内容]

---

**创建时间**: YYYY-MM-DD HH:mm:ss
**创建者**: Claude Code
**任务拆解来源**: 02-tasks.md
