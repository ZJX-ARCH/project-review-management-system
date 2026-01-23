# ContiNew Admin UI - 前端项目

[根目录](../CLAUDE.md) > **continew-admin-ui**

---

## 变更记录 (Changelog)

### 2026-01-16 11:48:58
- 初始化前端项目文档
- 扫描并记录前端目录结构与核心配置

---

## 模块职责

`continew-admin-ui` 是 ContiNew Admin 的前端 UI 项目，采用 Vue 3 + TypeScript + Arco Design 技术栈构建。它提供了一个现代化、响应式的中后台管理界面，与后端 API 通过 RESTful 接口通信。

### 核心职责

- **用户界面展示**：提供仪表盘、系统管理、监控、租户管理等功能页面
- **用户交互**：处理用户输入、表单验证、数据展示与操作
- **路由管理**：基于 Vue Router 实现前端路由与权限控制
- **状态管理**：使用 Pinia 管理全局状态（用户信息、路由、主题等）
- **API 调用**：通过 Axios 封装的 HTTP 客户端与后端交互
- **主题定制**：支持多种布局（默认、顶部、混合、分栏）与主题切换

---

## 入口与启动

### 开发环境启动

```bash
# 安装依赖
pnpm install

# 启动开发服务器
pnpm dev
```

访问地址：http://localhost:5173（Vite 默认端口，会自动打开浏览器）

### 生产构建

```bash
# 类型检查 + 构建生产版本
pnpm build

# 构建测试环境版本
pnpm build:test

# 预览生产构建
pnpm preview
```

构建产物：`dist/` 目录

---

## 对外接口

### 页面路由

| 路由路径 | 页面名称 | 功能描述 |
|---------|---------|---------|
| `/` | 首页重定向 | 根据权限重定向到工作台或其他首页 |
| `/login` | 登录页 | 账号/邮箱/手机登录，第三方登录 |
| `/dashboard/workplace` | 工作台 | 个人工作台，快捷入口 |
| `/dashboard/analysis` | 数据分析 | 访问统计、地域分布、浏览器统计等 |
| `/system/user` | 用户管理 | 用户增删改查、角色分配、重置密码 |
| `/system/role` | 角色管理 | 角色配置、权限分配 |
| `/system/menu` | 菜单管理 | 菜单树管理、按钮权限配置 |
| `/system/dept` | 部门管理 | 部门树管理 |
| `/system/dict` | 字典管理 | 字典与字典项管理 |
| `/system/file` | 文件管理 | 文件上传、预览、下载、回收站 |
| `/system/notice` | 公告管理 | 公告发布与查看 |
| `/system/config` | 系统配置 | 站点、邮件、短信、存储、安全等配置 |
| `/monitor/online` | 在线用户 | 在线用户监控与强制下线 |
| `/monitor/log/operation` | 操作日志 | 用户操作日志查询 |
| `/monitor/log/login` | 登录日志 | 登录记录查询 |
| `/monitor/sms/log` | 短信日志 | 短信发送记录 |
| `/tenant/management` | 租户管理 | 租户信息管理（多租户模式） |
| `/tenant/package` | 租户套餐 | 套餐配置与菜单分配 |
| `/open/app` | 应用管理 | 开放平台应用管理 |
| `/schedule/job` | 任务管理 | 定时任务配置 |
| `/schedule/log` | 任务日志 | 任务执行日志 |
| `/code/generator` | 代码生成 | 根据数据库表生成代码 |
| `/user/profile` | 个人中心 | 个人信息、修改密码 |
| `/user/message` | 消息中心 | 系统消息查看 |

### API 代理配置

开发环境下，前端通过 Vite 代理转发 API 请求到后端：

```typescript
// vite.config.ts
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8000',  // 后端服务地址
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')
    }
  }
}
```

---

## 关键依赖与配置

### 核心依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| `vue` | ^3.5.4 | 核心框架 |
| `typescript` | ~5.0.4 | 类型系统 |
| `vite` | ^5.1.5 | 构建工具 |
| `@arco-design/web-vue` | ^2.57.0 | UI 组件库 |
| `pinia` | ^2.0.16 | 状态管理 |
| `vue-router` | ^4.3.3 | 路由管理 |
| `axios` | ^0.27.2 | HTTP 客户端 |
| `echarts` | ^5.4.2 | 图表库 |
| `dayjs` | ^1.11.4 | 日期处理 |
| `lodash-es` | ^4.17.21 | 工具函数库 |
| `crypto-js` | ^4.2.0 | 加密库（密码加密） |
| `jsencrypt` | ^3.3.2 | RSA 加密 |
| `nprogress` | ^0.2.0 | 页面加载进度条 |

### 开发依赖

| 依赖 | 用途 |
|------|------|
| `@vitejs/plugin-vue` | Vue 3 支持 |
| `@vitejs/plugin-vue-jsx` | JSX 支持 |
| `unplugin-auto-import` | 自动导入 API |
| `unplugin-vue-components` | 自动导入组件 |
| `vite-plugin-svg-icons` | SVG 图标支持 |
| `vite-plugin-mock` | Mock 数据 |
| `eslint` + `@antfu/eslint-config` | 代码检查 |
| `less` + `sass` | CSS 预处理器 |

### 项目配置文件

| 文件 | 用途 |
|------|------|
| `vite.config.ts` | Vite 构建配置 |
| `tsconfig.json` | TypeScript 编译配置 |
| `package.json` | 依赖与脚本管理 |
| `.env` | 环境变量配置 |
| `eslint.config.js` | ESLint 规则 |

---

## 数据模型

### TypeScript 类型定义

前端通过 TypeScript 接口定义数据模型，主要分为：

1. **请求参数类型**：Query、Form、Body 参数
2. **响应数据类型**：API 返回的实体与列表
3. **状态类型**：Pinia Store 中的状态
4. **组件 Props 类型**：组件属性定义

示例（用户实体）：

```typescript
export interface UserModel {
  id: string
  username: string
  nickname?: string
  email?: string
  phone?: string
  gender?: number
  avatar?: string
  status?: number
  createTime?: string
  // ...
}
```

### 状态管理（Pinia Stores）

| Store 模块 | 文件路径 | 管理内容 |
|-----------|---------|---------|
| `useUserStore` | `src/stores/user.ts` | 用户信息、登录状态、权限 |
| `useRouteStore` | `src/stores/route.ts` | 动态路由、菜单数据 |
| `useAppStore` | `src/stores/app.ts` | 应用配置、主题、布局 |
| `useTabBarStore` | `src/stores/tabBar.ts` | 标签页管理 |

---

## 测试与质量

### 代码检查

```bash
# 运行 ESLint 检查
pnpm lint

# 自动修复代码风格问题
pnpm lint:fix
```

### 类型检查

```bash
# 运行 TypeScript 类型检查
pnpm typecheck
```

### 构建检查

```bash
# 构建时会自动执行类型检查
pnpm build
```

### Git Hooks

项目配置了 `lint-staged`，在 Git 提交前自动检查代码：

```json
"lint-staged": {
  "*": "eslint --fix"
}
```

---

## 常见问题 (FAQ)

### Q1：如何修改后端 API 地址？

编辑 `.env` 文件（或 `.env.development`）：

```env
VITE_API_BASE_URL=http://your-backend-url:8000
```

### Q2：如何添加新的页面路由？

1. 在 `src/views/` 下创建页面组件
2. 在后端菜单管理中配置对应的路由
3. 前端会自动从后端获取路由配置并动态注册

### Q3：如何自定义主题颜色？

编辑 `src/styles/var.scss`：

```scss
$primary-color: #1890ff;  // 修改主题色
```

### Q4：构建后文件过大怎么办？

检查 `vite.config.ts` 中的 `chunkSizeWarningLimit` 配置，或优化依赖导入方式（按需导入）。

### Q5：如何使用 Mock 数据？

在 `src/mock/` 目录下定义 Mock 接口，Vite 会自动拦截对应的请求。

---

## 相关文件清单

### 核心配置

- `package.json` - 依赖与脚本配置
- `vite.config.ts` - Vite 构建配置
- `tsconfig.json` - TypeScript 配置
- `.env` - 环境变量

### 源码目录

```
src/
├── App.vue                 # 根组件
├── main.ts                 # 入口文件
├── api/                    # API 接口定义
├── assets/                 # 静态资源
├── components/             # 公共组件
│   ├── GiTable/           # 表格组件
│   ├── GiForm/            # 表单组件
│   └── ...
├── layout/                 # 布局组件
│   ├── LayoutDefault.vue  # 默认布局
│   ├── LayoutTop.vue      # 顶部布局
│   └── ...
├── router/                 # 路由配置
│   ├── index.ts
│   ├── route.ts
│   └── guard.ts           # 路由守卫
├── stores/                 # Pinia 状态管理
│   ├── user.ts
│   ├── route.ts
│   └── app.ts
├── styles/                 # 全局样式
│   ├── var.scss           # 变量定义
│   └── global.scss        # 全局样式
├── utils/                  # 工具函数
│   ├── request.ts         # Axios 封装
│   └── ...
└── views/                  # 页面视图
    ├── dashboard/         # 仪表盘
    ├── system/            # 系统管理
    ├── monitor/           # 系统监控
    ├── tenant/            # 租户管理
    ├── open/              # 能力开放
    ├── schedule/          # 任务调度
    ├── code/              # 代码生成
    ├── login/             # 登录页
    └── user/              # 个人中心
```

---

## 变更记录 (Changelog)

*前端模块级别的变更记录将在后续更新时补充*

---

*此文档由 AI 自动生成，最后更新于 2026-01-16 11:48:58*
