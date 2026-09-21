# 路由配置规范

本文件用于解决 AI 新增页面时常见的路由问题：语法报错、页面不显示、菜单不出现、路径能访问但标题/图标/权限异常。

## 先判断页面类型

| 场景 | 推荐做法 |
| --- | --- |
| 新增一个独立一级菜单页面 | 新增或修改 `apps/web-antd/src/router/routes/modules/<module>.ts` 中一个顶层 route |
| 新增某个模块下的多个页面 | 使用一个父 route + `children`，父 route 只负责菜单分组和重定向 |
| 新增隐藏详情页 | 子 route 设置 `meta.hideInMenu: true`，并保留明确 `name` 和 `title` |
| 原型/Mock 页面 | 仍然按正式路由写法配置，数据可以先用本地 Mock |

## 路由文件固定位置

```txt
apps/web-antd/src/router/routes/modules/<module>.ts
```

路由文件会被 `apps/web-antd/src/router/routes/index.ts` 通过 `import.meta.glob('./modules/**/*.ts')` 自动读取。一般不需要手动修改 `routes/index.ts`。

## 单页面模板

适用于只有一个菜单入口的页面。

```ts
import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    component: () => import('#/views/battery/construction/index.vue'),
    meta: {
      icon: 'icon-shezhi',
      order: 50,
      title: '施工管理',
    },
    name: 'BatteryConstruction',
    path: '/battery/construction',
  },
];

export default routes;
```

## 父子页面模板

适用于一个模块下有多个页面，例如“智能考勤管理”下面挂“施工管理”和“文档列表”。

```ts
import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'icon-shezhi',
      order: 50,
      title: '智能考勤管理',
    },
    name: 'Battery',
    path: '/battery',
    redirect: '/battery/construction',
    children: [
      {
        component: () => import('#/views/battery/construction/index.vue'),
        meta: {
          title: '施工管理',
        },
        name: 'BatteryConstruction',
        path: 'construction',
      },
      {
        component: () =>
          import('#/views/battery/archive/document-list/index.vue'),
        meta: {
          title: '文档列表',
        },
        name: 'BatteryDocumentList',
        path: 'archive/document-list',
      },
    ],
  },
];

export default routes;
```

## 必填字段

| 字段 | 要求 |
| --- | --- |
| `name` | 全项目唯一，使用 PascalCase，例如 `BatteryConstruction` |
| `path` | 同级唯一；父级用绝对路径，子级用相对路径 |
| `component` | 必须指向真实存在的 `.vue` 文件 |
| `meta.title` | 必填，作为菜单和标签页标题 |
| `meta.icon` | 一级菜单建议填写 |
| `meta.order` | 一级菜单建议填写，控制排序 |

## 常见错误

| 错误 | 结果 | 正确做法 |
| --- | --- | --- |
| `meta` 对象少逗号、少引号、少括号 | Vite OXC parse error | 改完立即运行 `pnpm run check:routes` |
| 子路由 `path` 写成 `/xxx` | 菜单层级或跳转异常 | 子路由用相对路径，例如 `construction` |
| `component` 路径不存在 | 页面白屏或动态导入失败 | 先确认 `apps/web-antd/src/views/**/index.vue` 存在 |
| `name` 重复 | keep-alive、tab、跳转异常 | 每个 route name 全局唯一 |
| 只新增页面文件，没加 route | 页面永远不显示 | 同步新增 `routes/modules/*.ts` |
| 改了 `routes/index.ts` | 破坏自动加载 | 新业务页面只改 `modules/*.ts` |

## AI 创建页面前输出

```md
路由方案：
| 项 | 内容 |
| --- | --- |
| 路由文件 | apps/web-antd/src/router/routes/modules/<module>.ts |
| 页面路径 | apps/web-antd/src/views/.../index.vue |
| 路由 path | /... |
| 路由 name | ... |
| 菜单标题 | ... |
| 是否一级菜单 | 是/否 |
| 是否隐藏菜单 | 是/否 |
```

## 完成后检查

```bash
pnpm run check:routes
pnpm run check:ai-kit
```

如果 `pnpm` 因本机环境 fetch 失败，可以直接运行：

```bash
node ./scripts/check-web-routes.mjs
node ./scripts/check-ai-kit.mjs
```
