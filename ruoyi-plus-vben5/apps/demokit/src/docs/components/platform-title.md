## PlatformTitleFamily

`PlatformTitleFamily` 是平台标题体系的统称，包含 4 个层级递减的标题组件：`PlatformPageTitle`、`PlatformSubpageTitle`、`PlatformSectionTitle` 和 `PlatformModalTitle`。页面层优先使用它们承接页面标题、二级页面标题、区块标题和弹窗标题，避免手写不一致的标题样式。

### 什么时候使用

- 页面顶部标题：使用 `PlatformPageTitle`，承接页面定位和摘要说明。
- 二级页面/详情页标题：使用 `PlatformSubpageTitle`，带地铁图标装饰和可选分割线。
- 卡片区块标题：使用 `PlatformSectionTitle`，支持图标、描述、标题右侧小动作、标题中部切换区和右侧附加操作。
- 弹窗/抽屉标题：使用 `PlatformModalTitle`，自带全屏切换和关闭按钮。

### 基础用法

```vue
<PlatformPageTitle title="项目管理" description="管理所有项目的基本信息、合同和进度。" />
<PlatformSubpageTitle divider title="项目详情" />
<PlatformSectionTitle icon="lucide:badge-check" title="资质审核" />
<PlatformModalTitle title="新增施工记录" />
```

### PlatformPageTitle

页面级标题，22px 粗体 + 可选描述文字。放在页面最顶部，用于定位当前页面。

`title` 和 `description` 均为可选：只传 `title` 渲染纯标题，只传 `description` 渲染纯描述（少见），两者都传渲染标题 + 描述。

```vue
<PlatformPageTitle
  title="项目全景总览"
  description="用于页面顶部，承接页面定位、摘要说明和信息总览。"
/>
```

### PlatformSubpageTitle

二级页面标题，基于 `PlatformSectionTitle` 封装，自带地铁图标装饰。常用于详情页、分步页和内容主区入口。

`divider` 控制底部是否显示分割线，`extra` 插槽用于右侧附加内容（如编号、状态标签）。

```vue
<PlatformSubpageTitle
  divider
  title="基本信息"
  description="项目核心字段和配置。"
>
  <template #extra>
    <span>PRJ-2026-042</span>
  </template>
</PlatformSubpageTitle>
```

### PlatformSectionTitle

区块标题，用于白卡片内容区的模块标题。支持 `icon`（PlatformIcon 图标名）或 `iconSrc`（图片 URL）作为标题前缀图标；`title-extra` 用于紧贴标题右侧的小动作，`center` 用于标题中部区域，`extra` 用于最右侧附加操作或说明。

`divider` 控制底部是否显示分割线。`titleSize` 和 `titleWeight` 可自定义标题字号和字重（SubpageTitle 内部通过这两个 prop 覆盖默认值）。

```vue
<PlatformSectionTitle
  divider
  icon="lucide:badge-check"
  title="项目甘特图"
  description="按时间线展示项目进度。"
>
  <template #title-extra>
    <PlatformButton size="small" type="primary">新增日程</PlatformButton>
  </template>
  <template #center>
    <PlatformSegmented :options="[{ label: '生产', value: 'production' }]" value="production" />
  </template>
  <template #extra>
    <PlatformSectionAction kind="more" label="更多" />
  </template>
</PlatformSectionTitle>
```

### PlatformModalTitle

弹窗/抽屉标题，基于 `PlatformOverlayTitlebar` 封装，自带全屏切换按钮和关闭按钮。通常不需要单独使用，`PlatformModal` 内部会自动渲染。

`showFullscreen` 控制是否显示全屏切换按钮，`fullscreen` 控制当前是否全屏状态。

```vue
<PlatformModalTitle
  title="编辑项目计划"
  :fullscreen="isFullscreen"
  @toggle-fullscreen="isFullscreen = !isFullscreen"
  @cancel="handleClose"
/>
```

### Props — PlatformPageTitle

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `title` | `string` | 页面标题 | `''` |
| `description` | `string` | 标题下方描述文字 | `''` |

### Props — PlatformSubpageTitle

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `title` | `string` | 二级页面标题 | 必填 |
| `description` | `string` | 标题下方描述文字 | `''` |
| `divider` | `boolean` | 是否显示底部分割线 | `false` |
| `padding` | `string` | 容器内边距 | `'0'` |

### Props — PlatformSectionTitle

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `title` | `string` | 区块标题 | 必填 |
| `description` | `string` | 标题下方描述文字 | `''` |
| `divider` | `boolean` | 是否显示底部分割线 | `false` |
| `icon` | `string` | PlatformIcon 图标名 | `''` |
| `iconSrc` | `string` | 图片图标 URL | `''` |
| `iconWidth` | `number \| string` | 图标宽度 | `''` |
| `iconHeight` | `number \| string` | 图标高度 | `''` |
| `iconGap` | `number \| string` | 图标与标题间距 | `8` |
| `imageAlt` | `string` | 图片图标 alt 文本 | `''` |
| `padding` | `string` | 容器内边距 | `'0'` |
| `titleSize` | `number \| string` | 标题字号 | `''` |
| `titleWeight` | `number \| string` | 标题字重 | `''` |

### Slots — PlatformSectionTitle

| 插槽 | 说明 |
|------|------|
| `title-extra` | 紧贴标题右侧的小动作，适合“新增日程”这类标题侧主按钮 |
| `center` | 标题中部区域，适合分段控件、视图切换等居中内容 |
| `extra` | 标题最右侧的附加操作或说明 |

### Props — PlatformModalTitle

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `title` | `string` | 弹窗标题 | `''` |
| `fullscreen` | `boolean` | 是否全屏状态 | `false` |
| `showFullscreen` | `boolean` | 是否显示全屏切换按钮 | `true` |
| `closeIconClass` | `string` | 关闭按钮图标 class | `'icon-danchuangguanli'` |
| `fullscreenIconClass` | `string` | 全屏按钮图标 class | `'icon-danchuangquanping'` |
| `collapseFullscreenIconClass` | `string` | 退出全屏按钮图标 class | `'icon-danchuangshouqi'` |

### Emits — PlatformModalTitle

| 事件 | 参数 | 说明 |
|------|------|------|
| `cancel` | `(event: MouseEvent)` | 点击关闭按钮 |
| `toggleFullscreen` | — | 点击全屏/退出全屏按钮 |

### 设计约束

- 页面标题层级必须按 PageTitle → SubpageTitle → SectionTitle → ModalTitle 递减，不要跳级使用。
- PlatformPageTitle 不支持 `extra` 插槽，页面顶部附加操作应放在 `PlatformViewToolbar` 中。
- PlatformSectionTitle 的 `icon` 和 `iconSrc` 二选一，不要同时传入。
- PlatformModalTitle 通常不需要单独使用，`PlatformModal` 内部会自动渲染；仅在自定义弹窗结构时才单独引入。
- `PlatformTitleFamily` 是平台能力入口，页面里不要重复手写一套标题的 h1/h2 + 描述 + 分割线结构。
