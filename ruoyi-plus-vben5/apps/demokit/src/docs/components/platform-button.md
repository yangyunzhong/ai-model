## PlatformButton

`PlatformButton` 是平台按钮的统一入口，基于 `antdv-next` 的 `Button`、`Dropdown`、`Popover` 和 `SpaceCompact` 组合封装。页面层优先使用它承接普通按钮、工具栏按钮、行操作按钮、收起按钮、二次确认和菜单组合按钮。

### 组件来源

- 主要底层组件：`antdv-next` Button
- [组件来源文档](https://www.antdv-next.com/components/button-cn)

### 什么时候使用

- 页面主操作：新增、保存、提交、导出。
- 查询工具栏：查询、重置、展开更多筛选项。
- 表格行操作：编辑、删除、查看详情。
- 需要二次确认的危险操作：删除、作废、撤回、批量处理。
- 一个主操作附带更多入口时：左侧主按钮执行默认动作，右侧省略号按钮打开菜单。

### 基础用法

```vue
<PlatformButton type="primary">主按钮</PlatformButton>
<PlatformButton>默认按钮</PlatformButton>
<PlatformButton danger>危险操作</PlatformButton>
<PlatformButton disabled>禁用按钮</PlatformButton>
```

### 场景样式

- `scene="default"`：默认按钮，适合表单底部或普通区域操作。
- `scene="toolbar"`：工具栏按钮，带平台工具栏的默认尺寸和浅色背景。
- `scene="collapse"`：收起/展开筛选项按钮，视觉上弱于主操作。
- `scene="action"`：表格行内文字操作，默认适配 `type="link"`。

```vue
<PlatformButton scene="toolbar" type="primary">查询</PlatformButton>
<PlatformButton scene="toolbar">重置</PlatformButton>
<PlatformButton scene="collapse">展开更多</PlatformButton>
<PlatformButton scene="action" type="link">编辑</PlatformButton>
```

### 二次确认

传入 `secondConfirmText` 后，点击主按钮不会立即触发业务 `click`，会先打开 `a-popover`。用户点击确认后才会继续触发原始 `click` 事件；点击取消只关闭浮层。

```vue
<PlatformButton
  scene="action"
  danger
  second-confirm-text="确认删除这条数据吗？"
  type="link"
  @click="handleDelete"
>
  删除
</PlatformButton>
```

### 菜单组合按钮

传入 `menuItems` 后，组件会渲染为 `a-space-compact` 组合按钮：左侧按钮保留默认点击行为，右侧省略号按钮打开 `a-dropdown` 菜单。省略号图标使用 `@vben/icons`，不直接依赖 `@antdv-next/icons`。

```vue
<script setup lang="ts">
import type { MenuEmits, MenuItemType } from 'antdv-next';

const menuItems: MenuItemType[] = [
  { key: 'export', label: '导出' },
  { key: 'archive', label: '归档' },
  { key: 'notify', label: '通知' },
];

const handleMenuClick: MenuEmits['click'] = ({ key }) => {
  console.log('menu click', key);
};
</script>

<template>
  <PlatformButton
    :menu-items="menuItems"
    scene="toolbar"
    type="primary"
    @click="handleDefaultAction"
    @menu-click="handleMenuClick"
  >
    操作
  </PlatformButton>
</template>
```

### 二次确认与菜单组合

`secondConfirmText` 和 `menuItems` 可以一起使用。此时左侧主按钮负责二次确认，右侧省略号只负责菜单。

```vue
<PlatformButton
  :menu-items="menuItems"
  second-confirm-text="确认执行主操作吗？"
  scene="toolbar"
  type="primary"
  @click="handlePrimaryAction"
  @menu-click="handleMenuClick"
>
  处理
</PlatformButton>
```

### Props

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `scene` | `'default' \| 'toolbar' \| 'action' \| 'collapse'` | 按钮场景样式 | `'default'` |
| `secondConfirmText` | `string` | 二次确认文案。有值时启用 `a-popover` 确认 | — |
| `menuItems` | `MenuProps['items']` | 下拉菜单项。有值时启用组合按钮 | `[]` |
| `menuPlacement` | `DropdownProps['placement']` | 菜单弹出位置 | `'bottomLeft'` |
| `menuTrigger` | `DropdownProps['trigger']` | 菜单触发方式 | `['click']` |

组件会继续透传 `type`、`danger`、`disabled`、`loading`、`size`、`shape` 等 `antdv-next` Button 属性。

### Emits

| 事件 | 参数 | 说明 |
|------|------|------|
| `click` | `(event: MouseEvent)` | 普通点击或二次确认通过后的点击 |
| `menuClick` | `(info: MenuInfo)` | 下拉菜单点击事件，类型对齐 `MenuEmits['click']` |

### 设计约束

- 删除、作废、撤回等危险操作必须使用二次确认，不要直接执行。
- 带更多操作的按钮使用组合按钮，不要把主按钮替换成纯下拉按钮。
- 平台组件内部图标统一使用 `@vben/icons`，不要在 `packages/platform-ui` 中直接新增 `@antdv-next/icons` 依赖。
- `PlatformButton` 是平台能力入口，页面里不要重复手写一套 `a-space-compact + a-dropdown + a-button` 结构。
