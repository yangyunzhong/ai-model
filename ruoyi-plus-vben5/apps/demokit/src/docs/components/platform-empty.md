## PlatformEmpty

`PlatformEmpty` 是平台空状态的统一入口，基于 `antdv-next` 的 `Empty` 组合封装。页面层优先使用它承接暂无数据、无搜索结果、无权限、网络断开等空状态占位，提供 5 种预设场景和自定义图标/图片/描述能力。

### 什么时候使用

- 数据列表为空：使用 `scene="default"` 展示通用暂无数据占位。
- 搜索/筛选无结果：使用 `scene="no-result"` 展示未找到匹配结果。
- 用户无访问权限：使用 `scene="no-permission"` 展示无权限提示。
- 网络连接断开：使用 `scene="offline"` 展示网络断开提示。
- 表格/列表内嵌小空态：使用 `scene="simple"` 使用 antdv 内置小图。
- 需要引导用户操作：通过默认插槽添加操作按钮。

### 基础用法

不传任何 prop 时，默认展示通用暂无数据空状态。

```vue
<PlatformEmpty />
```

### 预设场景

通过 `scene` 选择预设场景，自动匹配图标和描述文案。支持 5 种场景：

| scene | 图标 | 默认描述 |
|-------|------|----------|
| `default` | `lucide:inbox` | 暂无数据 |
| `no-result` | `lucide:search` | 未找到匹配结果 |
| `no-permission` | `lucide:lock-keyhole` | 暂无访问权限 |
| `offline` | `lucide:circle-alert` | 网络连接已断开 |
| `simple` | antdv 内置小图 | 暂无数据 |

```vue
<PlatformEmpty scene="no-result" />
<PlatformEmpty scene="no-permission" />
<PlatformEmpty scene="offline" />
<PlatformEmpty scene="simple" />
```

### 自定义描述

通过 `description` 覆盖场景默认描述文案。

```vue
<PlatformEmpty
  icon="lucide:folder-tree"
  description="当前目录下还没有文件，点击上传添加"
/>
```

### 自定义图标

通过 `icon` 指定 PlatformIcon 图标名，覆盖场景默认图标。传入 `icon` 后，不再使用 antdv 的默认图片，改为渲染圆形图标背景。

```vue
<PlatformEmpty
  icon="lucide:mail-check"
  description="暂无未读消息"
/>
```

### 自定义图片

通过 `image` 传入图片 URL，替换默认图标。传入 `image` 后，`icon` 不再生效，直接使用 antdv Empty 的图片展示。

```vue
<PlatformEmpty
  image="https://gw.alipayobjects.com/zos/antfincdn/ZHrcdLPrvN/empty.svg"
  description="自定义空状态图片"
/>
```

### 带操作按钮

通过默认插槽添加操作按钮，引导用户下一步操作。

```vue
<script setup lang="ts">
import { PlatformButton, PlatformEmpty } from '@st/platform-ui';
</script>

<template>
  <PlatformEmpty description="还没有任何项目">
    <PlatformButton type="primary" scene="toolbar">新建项目</PlatformButton>
  </PlatformEmpty>
</template>
```

### 自定义描述插槽

通过 `description` 插槽自定义描述内容，支持富文本和交互。

```vue
<PlatformEmpty icon="lucide:circle-alert">
  <template #description>
    <span>数据加载失败，请</span>
    <a href="javascript:void(0)" @click="handleRetry">重试</a>
  </template>
</PlatformEmpty>
```

### Props

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `scene` | `'default' \| 'no-result' \| 'no-permission' \| 'offline' \| 'simple'` | 预设场景，自动匹配图标和描述 | `'default'` |
| `description` | `string` | 描述文案，覆盖场景默认描述 | `''` |
| `icon` | `string` | PlatformIcon 图标名，覆盖场景默认图标 | `''` |
| `image` | `string` | 自定义图片 URL，传入后 icon 不再生效 | `''` |

组件会继续透传 `class`、`style` 等 `antdv-next` Empty 属性。

### Emits

无自定义事件。

### 设计约束

- 优先使用 `scene` 预设场景，不要为常见空状态手写图标和描述组合。
- `icon` 和 `image` 二选一，不要同时传入；`image` 优先级高于 `icon`。
- 表格/列表内嵌空状态使用 `scene="simple"`，不要用默认大图模式。
- 空状态需要引导操作时，通过默认插槽添加按钮，不要让用户停留在无出口的空页面。
- `PlatformEmpty` 是平台能力入口，页面里不要重复手写一套空状态图标 + 描述 + 按钮结构。
