# 前端目录结构

## 顶层结构

```text
ruoyi-plus-vben5/
├── apps/
│   └── web-antd/        # 当前业务前端主应用
├── packages/            # Vben 公共包、主题、工具、通用 UI
├── internal/            # 工程化内部配置
├── scripts/             # 构建和辅助脚本
└── pnpm-workspace.yaml
```

## 主应用结构

```text
apps/web-antd/src/
├── adapter/             # Vben Form、VxeTable、组件适配层
├── api/                 # 跨页面共享接口
├── components/          # 项目级组件
├── layouts/             # 应用布局接入
├── locales/             # 国际化
├── router/              # 路由、权限、菜单接入
├── store/               # Pinia store
├── utils/               # 项目工具
└── views/               # 页面
```

## 页面模块组织

常规后台页面优先使用：

```text
views/<domain>/<feature>/
├── index.vue            # 列表/主页面
├── data.ts 或 data.tsx  # 表格列、查询表单、弹窗表单 schema
├── <feature>-modal.vue  # 新增/编辑弹窗
├── <feature>-drawer.vue # 详情/复杂编辑抽屉
└── api/                 # 仅该页面私有接口，跨页面复用则放 src/api
    ├── index.ts
    └── model.d.ts
```

跨页面复用接口放：

```text
api/<domain>/<feature>/
├── index.ts
└── model.d.ts
```

## 命名约定

- Vue 文件使用 kebab-case：`demo-modal.vue`、`dept-drawer.vue`。
- 页面配置使用 `data.ts` / `data.tsx`。
- API 函数使用业务动词：`userList`、`userAdd`、`userUpdate`、`userRemove`。
- API 路径集中在 `enum Api`。
- 类型放在同目录 `model.d.ts`，组件内只保留局部临时类型。

## 组件落点

- 页面私有组件放在页面目录或 `components/` 子目录。
- 多页面复用组件放 `apps/web-antd/src/components`。
- Vben/Vxe/Form 的全局行为优先改 `adapter/`，不要在单页面绕开。
- 布局、菜单、路由权限不要随意修改 `packages` 核心包；确需修改必须先说明影响范围。
