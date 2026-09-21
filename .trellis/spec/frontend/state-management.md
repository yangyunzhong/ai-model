# 前端状态管理规范

## 状态分类

- 页面局部状态：`ref` / `computed`，例如弹窗是否编辑、当前选中行、loading。
- 组件组合状态：由 `useVbenForm`、`useVbenVxeGrid`、`useVbenModal` 管理。
- 全局业务状态：Pinia store，例如 `auth`、`tenant`、`dict`、`notify`。
- 服务端列表数据：优先由 VxeGrid proxy 查询管理，不额外全局缓存。

## 什么时候用 Pinia

只有满足以下情况才新增或扩展 store：

- 登录用户、token、权限、菜单等跨应用状态。
- 租户切换、字典缓存、通知等多页面复用状态。
- 页面刷新后仍需恢复的关键状态。

普通列表页的查询条件、分页、弹窗状态不要放入全局 store。

## 字典

- 字典选项优先复用现有 `dict` store 和 `getDictOptions`。
- 字典常量优先使用 `DictEnum`。
- 不在页面中硬编码后端字典 label/value，除非是原型 mock 且已标注。

## 租户与权限

- 租户状态由现有 tenant store 管理。
- 权限展示使用 `v-access:code`，不要自写角色判断替代全局权限体系。
- 超级管理员、租户管理员等特殊规则要先核对后端实际权限模型。

## 服务端状态

- CRUD 成功后通过 `tableApi.query()` 刷新。
- 不为普通列表引入复杂缓存层。
- Mock 阶段数据可放页面 source 文件或 mock 服务，但交接包要说明切换真实接口方式。

## 禁止

- 禁止把表格数据复制到多个 ref 中来回同步。
- 禁止绕过 auth store 手写 token 存取。
- 禁止把仅一个弹窗使用的状态放进全局 store。
