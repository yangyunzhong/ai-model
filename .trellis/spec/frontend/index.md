# 前端开发规范

> 适用于本仓库参考前端 `ruoyi-plus-vben5/`。正式页面开发优先遵守 Vben5 + Vue 3 + antdv-next + Alova + VxeGrid 的既有体系。

## 技术底座

- 主开发目录：`ruoyi-plus-vben5/apps/web-antd`。
- 框架：Vben Admin 5，Vue 3.5，TypeScript。
- UI：antdv-next，不引入 Element Plus、Naive UI、Arco 等替代体系。
- 表格：优先 `#/adapter/vxe-table` 的 `useVbenVxeGrid`。
- 表单：优先 `#/adapter/form` 的 `useVbenForm`。
- 弹窗：优先 `@vben/common-ui` 的 `useVbenModal` / `useVbenDrawer`。
- 请求：`#/utils/http` 中的 `alovaInstance`。
- 包管理：只使用 pnpm，Node 版本需满足项目 README 的 `>=22.16.0`。

## 规范索引

| 规范 | 说明 | 使用时机 |
| --- | --- | --- |
| [Directory Structure](./directory-structure.md) | monorepo、apps/web-antd、api/views/store 组织 | 新增页面、接口、组件 |
| [Component Guidelines](./component-guidelines.md) | Vue SFC、Vben 组件、表格/表单/弹窗组合 | 写页面和组件 |
| [Platform Governance](./platform-governance.md) | 平台组件治理、Vben 布局保护、前端 Mock 开发、交付预览 | 修改平台组件、布局、Mock、典型页、正式前端交接 |
| [Hook Guidelines](./hook-guidelines.md) | Composition API、Vben composables、数据加载 | 抽取逻辑、使用 use* API |
| [State Management](./state-management.md) | Pinia、本地状态、服务端状态、字典/租户/权限 | 判断状态放哪里 |
| [Quality Guidelines](./quality-guidelines.md) | 前端禁用模式、验证、UI 边界 | 代码评审和交付 |
| [Type Safety](./type-safety.md) | model.d.ts、API 泛型、表单校验类型 | 写接口和类型 |

## 开发前检查

1. 先找同类页面：`views/system/*`、`views/demo/*`、`views/workflow/*`。
2. 新增 CRUD 页面优先参考 `views/demo/demo/index.vue` 和 `demo-modal.vue`。
3. API 文件优先参考 `api/system/user/index.ts` 的 `enum Api + function` 风格。
4. 页面权限按钮使用 `v-access:code`，权限码需与后端 `@SaCheckPermission` 对齐。
5. 原型阶段可以用前端 mock/本地数据，但交接包必须说明真实接口意图和字段映射。
6. PM/UI 原型阶段先读 [PM/UI 原型规范](../prototype/index.md)，产物必须是可运行前端 Mock MVP，并同步起草 draft API 协议和 draft 数据库/数据模型设计；不提前做正式后端/API/数据库设计。
7. 涉及平台组件、Vben 布局、antdv-next 组件封装、Mock 登录权限、典型页或交付预览时，先读 [Platform Governance](./platform-governance.md)。

## 禁止

- 不修改 `node_modules`。
- 不引入新的 UI 组件库。
- 不绕过 Vben 布局手写后台导航、菜单、面包屑。
- 不在页面中散落重复请求封装。
- 不把接口路径字符串直接写在组件里。
- 不把 `ruoyi-plus-vben5/AGENTS.md` 当作独立规则源；前端长期规则以 `.trellis/spec/frontend/` 为准。
