#  系统功能

- 仪表盘：提供工作台、分析页，工作台提供功能快捷导航入口、最新公告、动态；分析页提供全面数据可视化能力
- 个人中心：支持基础信息修改、密码修改、邮箱绑定、手机号绑定（并提供行为验证码、短信限流等安全处理）、第三方账号绑定/解绑（微信登录）、头像裁剪上传
- 消息中心：提供站内信消息统一查看、标记已读、全部已读、删除等功能（目前仅支持系统通知消息）、提供个人公告查看
- 用户管理：管理系统用户，包含新增、修改、删除、导入、导出、重置密码、分配角色等功能
- 角色管理：管理系统用户的功能权限及数据权限，包含新增、修改、删除、分配角色等功能
- 菜单管理：管理系统菜单及按钮权限，支持多级菜单，动态路由，包含新增、修改、删除等功能
- 部门管理：管理系统组织架构，包含新增、修改、删除、导出等功能，以树形列表进行展示
- 通知公告：管理系统公告，支持通知范围（所有人、指定用户）、通知方式（系统消息、登录弹窗）、定时发送、置顶设置
- 文件管理：管理系统文件及文件夹，支持回收站、上传/分片上传、下载、预览（目前支持图片、音视频、PDF、Word、Excel、PPT）、重命名、切换视图（列表、网格）等功能
- 字典管理：管理系统公用数据字典，例如：消息类型。支持字典标签背景色和排序等配置
- 系统配置：
  - 网站配置：提供修改系统标题、Logo、favicon、版权信息等基础配置功能，以方便用户系统与其自身品牌形象保持一致
  - 安全配置：提供密码策略修改，支持丰富的密码策略设定，包括但不限于 `密码有效期`、`密码重复次数`、`密码错误锁定账号次数、时间` 等
  - 登录配置：提供验证码开关等登录相关配置
  - 邮件配置：提供系统发件箱配置，也支持通过配置文件指定
  - 短信配置：提供系统短信服务配置，也支持通过配置文件指定
  - 存储配置：管理文件存储配置，支持本地存储、兼容 S3 协议对象存储
  - 客户端配置：多端（PC端、小程序端等）认证管理，可设置不同的 token 有效期
- 在线用户：管理当前登录用户，可一键踢除下线
- 日志管理：管理系统登录日志、操作日志，支持查看日志详情，包含请求头、响应头等报文信息
- 短信日志：管理系统短信发送日志，支持删除、导出
- 应用管理：管理第三方系统应用 AK、SK，包含新增、修改、删除、查看密钥、重置密钥等功能，支持设置密钥有效期
- 租户管理：管理租户信息，包含新增、修改、删除、分配角色等功能
- 租户套餐：管理租户套餐信息，包含新增、修改、删除、查看等功能
- 任务管理：管理系统定时任务，包含新增、修改、删除、执行功能，支持 Cron（可配置式生成 Cron 表达式） 和固定频率
- 任务日志：管理定时任务执行日志，包含停止、重试指定批次等功能
- 代码生成：提供根据数据库表自动生成相应的前后端 CRUD 代码的功能，支持同步最新表结构及代码生成预览

# 项目结构

```
continew-admin-ui
├─ config              # Vite 插件配置
├─ public              # 公共静态资源（favicon.ico、logo.svg）
├─ src
│  ├─ apis             # 请求接口
│  │  ├─ auth            # 认证模块
│  │  ├─ code            # 代码生成模块
│  │  ├─ common          # 公共模块
│  │  ├─ monitor         # 系统监控模块
│  │  ├─ open            # 能力开放模块
│  │  ├─ tenant          # 租户模块
│  │  ├─ schedule        # 任务调度模块
│  │  └─ system          # 系统管理模块
│  ├─ assets           # 静态资源
│  │  ├─ icons           # 图标资源
│  │  ├─ images          # 图片资源
│  │  └─ fonts           # 字体资源
│  ├─ components       # 通用业务组件
│  ├─ config           # 全局配置（包含 echarts 主题）
│  │  └─ settings.json   # 配置文件
│  ├─ directives       # 指令集（如需，可自行补充）
│  ├─ hooks            # 全局 hooks
│  ├─ layout           # 布局
│  ├─ mock             # 模拟数据
│  ├─ router           # 路由配置
│  ├─ stores           # 状态管理中心
│  ├─ types            # TypeScript 类型
│  ├─ utils            # 工具库（mock 全局开启/关闭）
│  ├─ views            # 页面
│  │  ├─ code            # 代码生成
│  │  │  └─ generator      # 代码生成
│  │  ├─ dashboard       # 仪表盘
│  │  │  ├─ analysis       # 分析页
│  │  │  └─ workplace      # 工作台
│  │  ├─ default         # 默认页面
│  │  ├─ login           # 登录模块
│  │  ├─ setting         # 设置
│  │  │  ├─ profile        # 个人中心
│  │  │  └─ message        # 消息中心
│  │  ├─ monitor         # 系统监控
│  │  │  ├─ log            # 系统日志
│  │  │  │  ├─ login         # 登录日志
│  │  │  │  └─ operation     # 操作日志
│  │  │  └─ online           # 在线用户
│  │  ├─ open            # 能力开放
│  │  │ └─ user            # 应用管理
│  │  ├─ tenant          # 租户管理
│  │  │ ├─ management      # 租户管理
│  │  │ └─ package         # 套餐管理
│  │  ├─ schedule        # 任务调度
│  │  │ ├─ job             # 任务管理
│  │  │ └─ log             # 任务日志
│  │  └─ system          # 系统管理
│  │    ├─ config          # 系统配置
│  │    ├─ dept            # 部门管理
│  │    ├─ dict            # 字典管理
│  │    ├─ file            # 文件管理
│  │    ├─ menu            # 菜单管理
│  │    ├─ notice          # 通知公告
│  │    ├─ role            # 角色管理
│  │    ├─ storage         # 存储管理
│  │    └─ user            # 用户管理
│  ├─ App.vue
│  └─ main.ts
├─ .env.development    # 开发环境配置
├─ .env.production     # 生产环境配置
├─ .env.test           # 测试环境配置
├─ eslint.config.js    # ESLint 配置
├─ index.html
├─ package.json
├─ package-lock.json
├─ pnpm-lock.yaml
├─ tsconfig.json
├─ vite.config.ts
├─ .gitignore（Git 忽略文件相关配置文件）
├─ .github（GitHub 相关配置目录，实际开发时直接删除）
├─ .image（截图目录，实际开发时直接删除）
├─ .vscode（VSCode 配置目录）
├─ LICENSE（开源协议文件）
├─ CHANGELOG.md（更新日志文件，实际开发时直接删除）
└─ README.md（项目 README 文件，实际开发时替换为真实内容）
```



# 核心技术栈

| 名称                                              | 版本   | 简介                                                         |
| :------------------------------------------------ | :----- | :----------------------------------------------------------- |
| [Vue](https://cn.vuejs.org/)                      | 3.4.21 | 渐进式 JavaScript 框架，易学易用，性能出色，适用场景丰富的 Web 前端框架。 |
| [Arco Design](https://arco.design/vue/docs/start) | 2.55.0 | 字节跳动推出的前端 UI 框架，年轻化的色彩和组件设计。         |
| [TypeScript](https://www.typescriptlang.org/zh/)  | 5.0.4  | TypeScript 是微软开发的一个开源的编程语言，通过在 JavaScript 的基础上添加静态类型定义构建而成。 |
| [Vite](https://cn.vitejs.dev/)                    | 5.1.5  | 下一代的前端工具链，为开发提供极速响应。                     |



# 权限管理

为了实现按钮级权限控制，本项目提供了权限指令 + 权限 API 两种鉴权方式。

## 鉴权指令

通过在对应按钮上使用 `v-permission` 指令，即可控制该按钮仅在有相应权限的用户访问时显示。



```
<a-button v-permission="['system:user:add']">新增</a-button>
```

## 鉴权函数

在一些更复杂的场景，我们可能需要更灵活的权限 API 来实现权限处理。



```
import has from '@/utils/has'

const columns: TableInstanceColumns[] = [
  {
    title: '操作',
    slotName: 'action',
    width: 200,
    align: 'center',
    fixed: !isMobile() ? 'right' : undefined,
    show: has.hasPermOr(['system:user:update', 'system:user:delete', 'system:user:resetPwd'])
  }
]
```

| 鉴权函数                          | 描述                                       |
| :-------------------------------- | :----------------------------------------- |
| hasPerm(permission: string)       | 验证用户是否具备某权限                     |
| hasPermOr(permissions: string[])  | 验证用户是否含有指定权限，只需包含其中一个 |
| hasPermAnd(permissions: string[]) | 验证用户是否含有指定权限，必须全部拥有     |
| hasRole(role: string)             | 验证用户是否具备某角色                     |
| hasRoleOr(roles: string[])        | 验证用户是否含有指定角色，只需包含其中一个 |
| hasRoleAnd(roles: string[])       | 验证用户是否含有指定角色，必须全部拥有     |



# 路由设计

在本项目中，路由基于 Vue Router 实现，区分有两大类：常驻路由（静态路由）和异步路由（动态路由）。

## 常驻路由（静态路由）

静态路由是在 `src/router/route.ts` 文件中定义的，例如：登录、仪表盘、个人中心、消息中心等路由。



```
import type { RouteRecordRaw } from 'vue-router'

/** 默认布局 */
const Layout = () => import('@/layout/index.vue')

/** 系统路由 */
export const systemRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { hidden: true },
  },
  {
    path: '/',
    name: 'Dashboard',
    component: Layout,
    redirect: '/dashboard/workplace',
    meta: { title: '仪表盘', icon: 'dashboard', hidden: false },
    children: [
      {
        path: '/dashboard/workplace',
        name: 'Workplace',
        component: () => import('@/views/dashboard/workplace/index.vue'),
        meta: { title: '工作台', icon: 'desktop', hidden: false, affix: true },
      },
      {
        path: '/dashboard/analysis',
        name: 'Analysis',
        component: () => import('@/views/dashboard/analysis/index.vue'),
        meta: { title: '分析页', icon: 'insert-chart', hidden: false },
      },
    ],
  },
  // 其他略...
]
```

## 异步路由（动态路由）

动态路由则是通过 系统管理/菜单管理 进行维护，在系统访问时通过调用后端接口 `/auth/user/route` 查询当前用户的菜单及权限，动态添加到系统中。

关键代码在 `src/stores/modules/route.ts` 文件的 `generateRoutes` 方法中，首先调用接口获取所有动态路由，处理后将常驻路由和动态路由进行合并生成路由树。

## 路由守卫

路由守卫在 `src/router/guard.ts` 文件中定义，包括全局前置守卫、全局后置守卫等。



```
/** 初始化路由守卫 */
export const setupRouterGuard = (router: Router) => {
  router.beforeEach(async (to, from, next) => {
    NProgress.start()
    const userStore = useUserStore()
    const routeStore = useRouteStore()
    // 判断该用户是否登录
    if (getToken()) {
      if (to.path === '/login') {
        // 如果已经登录，并准备进入 Login 页面，则重定向到主页
        next()
      } else {
        if (!hasRouteFlag) {
          try {
            await userStore.getInfo()
            if (userStore.userInfo.pwdExpired && to.path !== '/pwdExpired') {
              Message.warning('密码已过期，请修改密码')
              next('/pwdExpired')
            }
            const accessRoutes = await routeStore.generateRoutes()
            accessRoutes.forEach((route) => {
              if (!isHttp(route.path)) {
                router.addRoute(route) // 动态添加可访问路由表
              }
            })
            hasRouteFlag = true
            // 确保添加路由已完成
            // 设置 replace: true, 因此导航将不会留下历史记录
            next({ ...to, replace: true })
          } catch (error: any) {
            // 过程中发生任何错误，都直接重置 Token，并重定向到登录页面
            await userStore.logoutCallBack()
            next(`/login?redirect=${to.path}`)
          }
        } else {
          next()
        }
      }
    } else {
      // 如果没有 Token
      if (whiteList.includes(to.path)) {
        // 如果在免登录的白名单中，则直接进入
        next()
      } else {
        // 其他没有访问权限的页面将被重定向到登录页面
        next('/login')
      }
    }

    // 生产环境开启检测版本更新
    const isProd = import.meta.env.PROD
    if (isProd) {
      await compareTag()
    }
  })

  router.onError(() => {
    NProgress.done()
  })

  router.afterEach(() => {
    NProgress.done()
  })
}
```

## 更换系统首页

默认情况下，系统首页为仪表盘/工作台页面，如果你需要调整系统首页，只需要几个步骤即可完成。

接下来演示一下，将默认首页调整为 仪表盘/分析页 的步骤：

1. 在 `src/router/route.ts` 中，将仪表盘路由的 `redirect` 地址调整为其子级路由分析页 `path`

   

   ```
     {
       path: '/',
       name: 'Dashboard',
       component: Layout,
       redirect: '/dashboard/analysis',
       meta: { title: '仪表盘', icon: 'dashboard', hidden: false },
       children: [
         {
           path: '/dashboard/workplace',
           name: 'Workplace',
           component: () => import('@/views/dashboard/workplace/index.vue'),
           meta: { title: '工作台', icon: 'desktop', hidden: false, affix: true },
         },
         {
           path: '/dashboard/analysis',
           name: 'Analysis',
           component: () => import('@/views/dashboard/analysis/index.vue'),
           meta: { title: '分析页', icon: 'insert-chart', hidden: false },
         },
       ],
     }
   ```

2. 全局搜索 `workplace`，将相关代码进行调整

目前在 `src/components/Breadcrumb/index.vue` 中定义有一个 `getHome` 方法，修改下方高亮的路径为分析页路径。



```
const getHome = () => {
  if (!home) {
    const cloneRoutes = JSON.parse(JSON.stringify(routes)) as RouteLocationMatched[]
    const obj = findTree(cloneRoutes, (i) => i.path === '/dashboard/analysis')
    home = obj.item
  }
}
```

如果你有更多的地方使用到了首页的代码，请同步进行调整

1. 重启前端项目，正常访问页面

温馨提示

`/` 是系统根路径，也就是系统首页 path，如果你不需要仪表盘下拆分多个菜单，可以只配一个子级路由即可，例如：可以把工作台路由直接删除，这样页面默认就只会显示一个一级菜单：分析页。

可参考常见问题：[在目录下只添加了一个菜单，目录不显示](https://continew.top/admin/faq.html#only-one-menu)

## 参考资料

1. Vue Router：https://router.vuejs.org/zh/
2. GiDemo：https://gitee.com/lin0716/gi-demo#项目规范



# 图标

本框架前端基于 Gi-Demo 前端模板开发，提供了 SVG 图标处理方案，并提供了图标选择器。

## SVG 图标

| 名称      | 位置                     |
| :-------- | :----------------------- |
| GiSvgIcon | src/components/GiSvgIcon |

基于 `vite-plugin-svg-icons` 实现的 SVG 图标处理方案，配置文件位置：`config/plugins/svg-icon.ts`。

使用 SVG 图标前，只需要将对应图标 SVG 文件放到 `src/assets/icons` 目录下即可。（需要重启项目）

使用方式：



```
<GiSvgIcon name="SVG 文件名称" :size="图标大小（默认20）" />
```

## 图标选择器

| 名称           | 位置                          |
| :------------- | :---------------------------- |
| GiIconSelector | src/components/GiIconSelector |

使用方式：



```
<GiIconSelector v-model="xxx" />
```

![1](https://continew.top/images/admin/frontend/component/icon/1.png)

![2](https://continew.top/images/admin/frontend/component/icon/2.png)



# 移除无用代码

## 移除百度统计代码

在 `index.html` 文件中，找到如下代码，删除即可：

index.html



```
<script>
  var _hmt = _hmt || [];
  (function() {
    var hm = document.createElement("script");
    hm.src = "https://hm.baidu.com/hm.js?246a935992138d6770cabe711402315c";
    var s = document.getElementsByTagName("script")[0];
    s.parentNode.insertBefore(hm, s);
  })();
</script>
```

## 移除万维广告代码

请注意：为支持项目持续发展，v3.6.0版本中我们引入了万维广告联盟，相关广告会在官网和演示站点展示。由于演示站点采用自动化部署，所以在您实际开发时，需手动移除广告代码：

1. 删除 `src/layout/components/WwAds.vue` 文件
2. 移除 `src/layout/LayoutMix.vue` 文件中的 WwAds 使用
3. 移除 `src/layout/components/Asider/index.vue` 文件中的 WwAds 使用
