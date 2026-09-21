# PlatformSteps 步骤条

引导用户按照步骤完成任务的导航条，基于 antdv-next Steps 封装。

## 什么时候使用

- 任务需要按固定顺序分步完成时，如申请流程、审批流程、向导操作。
- 需要展示当前进度和剩余步骤时。
- 需要在弹窗或抽屉中引导用户分步填写表单时。

## 基础用法

通过 `current` 控制当前步骤，`items` 定义步骤列表。

```vue
<script setup lang="ts">
import { ref } from 'vue';
import { PlatformSteps } from '@st/platform-ui';

const current = ref(0);
const items = [
  { title: '选择项目' },
  { title: '填写申请表' },
  { title: '提交审核' },
  { title: '完成' },
];
</script>

<template>
  <PlatformSteps :current="current" :items="items" />
</template>
```

## 步骤类型

通过 `type` 切换步骤条样式，支持 5 种类型：

| type 值 | 说明 | 适用场景 |
| --- | --- | --- |
| `default` | 默认带数字圆圈的步骤条 | 通用分步流程 |
| `dot` | 点状步骤条 | 简洁进度展示 |
| `inline` | 内联步骤条 | 列表内容中的流程状态 |
| `navigation` | 导航式步骤条 | 多流程切换 |
| `panel` | 面板式步骤条 | 向导式分步操作 |

```vue
<PlatformSteps :current="1" :items="items" type="dot" />
<PlatformSteps :current="1" :items="items" type="navigation" />
<PlatformSteps :current="1" :items="items" type="panel" />
```

## 错误状态

步骤项通过 `status="error"` 标记异常步骤。

```vue
<script setup lang="ts">
import { PlatformSteps } from '@st/platform-ui';

const items = [
  { title: '填写信息' },
  { title: '提交审核', status: 'error' },
  { title: '完成' },
];
</script>

<template>
  <PlatformSteps :current="1" :items="items" />
</template>
```

## 垂直方向

`orientation="vertical"` 适合详情页侧边流程展示。

```vue
<PlatformSteps
  :current="1"
  :items="items"
  orientation="vertical"
/>
```

## 自定义图标

步骤项通过 `icon` 指定 PlatformIcon 图标名称。

```vue
<script setup lang="ts">
import { PlatformSteps } from '@st/platform-ui';

const items = [
  { title: '登录', icon: 'lucide:log-in' },
  { title: '验证', icon: 'lucide:shield-check' },
  { title: '支付', icon: 'lucide:credit-card' },
  { title: '完成', icon: 'lucide:check-circle-2' },
];
</script>

<template>
  <PlatformSteps :current="1" :items="items" />
</template>
```

## 可点击切换

监听 `change` 事件实现步骤可点击切换。

```vue
<script setup lang="ts">
import { ref } from 'vue';
import { PlatformSteps } from '@st/platform-ui';

const current = ref(0);
const items = [
  { title: '步骤一', description: '点击可切换' },
  { title: '步骤二', description: '点击可切换' },
  { title: '步骤三', description: '点击可切换' },
];
</script>

<template>
  <PlatformSteps :current="current" :items="items" @change="current = $event" />
</template>
```

## Props

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| current | 当前步骤，从 0 开始计数 | `number` | `0` |
| items | 步骤项列表 | `StepItem[]` | `[]` |
| orientation | 方向 | `'horizontal' \| 'vertical'` | `'horizontal'` |
| size | 尺寸 | `'default' \| 'small'` | `'default'` |
| status | 当前步骤状态 | `'wait' \| 'process' \| 'finish' \| 'error'` | `'process'` |
| type | 步骤条类型 | `'default' \| 'dot' \| 'inline' \| 'navigation' \| 'panel'` | `'default'` |
| variant | 样式变体 | `'filled' \| 'outlined'` | `'filled'` |

### StepItem

| 属性 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| title | 步骤标题 | `string` | — |
| content | 步骤描述（映射为 antdv description） | `string` | — |
| description | 步骤描述 | `string` | — |
| icon | 图标名称，支持 PlatformIcon 格式 | `string` | — |
| status | 步骤状态 | `'wait' \| 'process' \| 'finish' \| 'error'` | `'wait'` |
| subTitle | 副标题 | `string` | — |
| disabled | 是否禁用点击 | `boolean` | `false` |

## Emits

| 事件 | 说明 | 回调参数 |
| --- | --- | --- |
| change | 步骤切换时触发 | `(current: number)` |

## 设计约束

- 组件透传 antdv-next Steps 所有原生属性（`$attrs`），未列出的属性直接传递到底层 Steps。
- `items.icon` 传入字符串图标名时，组件内部自动转换为 PlatformIcon 渲染；也可使用 antdv 原生 `iconRender` 插槽完全自定义。
- `items.content` 是旧组件兼容字段，映射为 antdv 的 `description`；新代码建议直接使用 `description`。
- 弹窗/抽屉等紧凑空间建议使用 `size="small"`。
