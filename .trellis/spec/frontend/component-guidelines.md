# 前端组件规范

## Vue SFC 基础

- 使用 Vue 3 Composition API。
- SFC 默认使用 `<script setup lang="ts">`。
- Props/Emits 使用 TypeScript 类型声明。
- 不使用 Options API 编写新组件。

```vue
<script setup lang="ts">
const emit = defineEmits<{ reload: [] }>();
</script>
```

## 页面骨架

- 后台页面默认使用 Vben `Page` 包裹。
- 列表页优先是“查询表单 + VxeGrid + 工具栏 + 操作列 + 弹窗/抽屉”。
- 不在业务页面手写全局布局、菜单、顶部栏、面包屑。

## 表格

- 优先使用 `useVbenVxeGrid`。
- `gridOptions.proxyConfig.ajax.query` 中把 Vxe 分页参数转成后端 `pageNum` / `pageSize`。
- `rowConfig.keyField` 必须使用稳定主键。
- 批量操作用 `tableApi.grid.getCheckboxRecords()`。
- 工具栏 slot 使用 `toolbar-actions` / `toolbar-tools`。

```ts
const [BasicTable, tableApi] = useVbenVxeGrid({
  formOptions,
  gridOptions,
});
```

## 表单

- 优先使用 `useVbenForm`，schema 放到 `data.ts` / `data.tsx`。
- 弹窗表单默认 `showDefaultActions: false`，提交由 Modal confirm 驱动。
- 表单值需要修改时，先 `cloneDeep(await formApi.getValues())`。
- 新增/编辑复用一个 modal 时，用 `isUpdate` 和 modal data 判断。

## 弹窗与抽屉

- 优先使用 `useVbenModal` / `useVbenDrawer`。
- 打开弹窗时通过 `modalApi.setData(...)` 传递上下文。
- `onOpenChange` 中加载详情数据，并使用 loading 状态。
- 关闭后重置表单，避免下次打开残留。

## 平台详情组件

- `PlatformDescriptions` 的 `items` 条目内容字段使用 `content`，不要写成 `children`，否则表格标签会渲染但内容为空。

```ts
const detailItems = [
  {
    content: record.name,
    label: '姓名',
  },
];
```

- 移动端或窄屏抽屉内使用 `PlatformDescriptions` 时，优先传响应式 `column`，例如 `{ xs: 1, sm: 1, md: 2 }`，避免两列描述表挤压导致内容竖排。

## 权限与操作

- 按钮权限使用 `v-access:code`。
- 权限码必须与后端 `@SaCheckPermission` 对齐。
- 删除等危险操作使用确认框，不直接执行。
- 删除成功后刷新 `tableApi.query()`。
- 按钮主动触发接口调用时，必须绑定 `loading` 状态，防止重复提交并给用户即时反馈。
- 所有异步接口调用必须声明 `loading` 状态，在请求前置为 `true`，在 `finally` 中置为 `false`，确保无论成功或失败都能恢复。

```vue
<script setup lang="ts">
const submitLoading = ref(false);

// async/await 写法：使用 try/finally
async function handleSubmit() {
  submitLoading.value = true;
  try {
    await api.submit(formData);
  } finally {
    submitLoading.value = false;
  }
}

// Promise 链式写法：使用 .finally()
function handleDelete(id: string) {
  deleteLoading.value = true;
  api
    .remove(id)
    .then(() => message.success('删除成功'))
    .catch((err) => message.error(err.message))
    .finally(() => {
      deleteLoading.value = false;
    });
}
</script>

<template>
  <a-button :loading="submitLoading" type="primary" @click="handleSubmit">
    提交
  </a-button>
  <a-button :loading="deleteLoading" danger @click="handleDelete(id)">
    删除
  </a-button>
</template>
```

## 样式

- 优先使用项目已有 Vben/antdv-next/Tailwind 工具类。
- 页面 scoped 样式只处理页面局部布局，不承载通用组件视觉规则。
- 通用视觉能力应沉淀到组件或 adapter。

## 禁止

- 禁止引入其他 UI 组件库替代 antdv-next。
- 禁止在组件里直接拼接口 URL。
- 禁止在多个页面复制同一套表格/表单 schema 逻辑而不抽取。
- 禁止为了一个页面修改 Vben 核心布局。
