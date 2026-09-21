# 前端 Hook 与组合式逻辑规范

## 基础原则

- 新逻辑使用 Vue Composition API。
- 优先复用 Vben 提供的 `useVbenForm`、`useVbenVxeGrid`、`useVbenModal`、`useVbenDrawer`。
- 页面内简单状态可直接使用 `ref`、`computed`、`watch`。
- 多页面复用状态再考虑抽 composable 或 Pinia store。

## useVbenVxeGrid

- 查询逻辑放在 `proxyConfig.ajax.query`。
- 查询参数应合并表单值和分页参数。
- 表格刷新统一使用 `tableApi.query()`。
- 需要读取选中行时使用 grid API，不维护重复 selected state。

## useVbenForm

- schema 优先由函数返回，方便注入字典、权限或上下文。
- 表单校验使用 `formApi.validate()`。
- 表单值只读时，修改前 `cloneDeep`。
- 表单关闭时 `resetForm()`。

## useVbenModal / useVbenDrawer

- 打开前通过 `setData` 传上下文。
- `onOpenChange` 中只处理打开后的初始化；关闭清理放 cancel/close 逻辑。
- 提交时必须设置 loading，finally 中关闭 loading。

## 自定义 composable

只有满足以下条件才新增：

- 同一逻辑被 2 个以上页面复用。
- 逻辑包含状态、请求、权限或事件编排，不只是普通工具函数。
- 能保持业务语义清晰，例如 `useDeptTreeOptions`。

## 禁止

- 禁止把一次性页面变量抽成全局 composable。
- 禁止同时维护表格内部选中状态和外部重复数组，除非有明确跨组件需求。
- 禁止在 watch 中做无边界请求，必须有触发条件和清理策略。
