## PlatformIcon

`PlatformIcon` 是平台图标的统一入口，基于 `@vben/icons` 的 `VbenIcon` 和项目 iconfont 字体图标双源组合封装。页面层优先使用它承接所有图标渲染，无需手动判断图标来源。

### 什么时候使用

- 页面中需要渲染任何图标：直接传入 icon 值，组件自动路由到正确来源。
- 使用 iconfont 业务图标：icon 值以 `icon-` 开头时自动走字体图标。
- 使用 Lucide 等通用图标：icon 值以 `lucide:` 前缀传入，走 Iconify 在线加载。
- 组件内部需要嵌入图标：如按钮图标、表格操作图标、状态图标等。
- 菜单图标配置：路由菜单的 icon 字段值直接传入即可。

### 基础用法

传入 `icon` 字符串，组件根据前缀自动判断来源。`icon-` 开头走 iconfont，其余走 VbenIcon。

```vue
<template>
  <PlatformIcon icon="icon-xinzeng" />
  <PlatformIcon icon="lucide:search" />
  <PlatformIcon icon="lucide:settings" />
</template>
```

### iconfont 业务图标

icon 值以 `icon-` 开头时，PlatformIcon 渲染为 `<i class="iconfont icon-xxx">` 字体图标。项目 iconfont 共包含 30 个业务图标，分为通用操作和业务模块两类。

**通用操作图标（11 个）**：

| icon 值 | 中文 |
|---------|------|
| `icon-xinzeng` | 新增 |
| `icon-bianji` | 编辑 |
| `icon-shanchu` | 删除 |
| `icon-sousuo` | 搜索 |
| `icon-shaixuan` | 筛选 |
| `icon-xiazai1` | 下载 |
| `icon-fanhui1` | 返回 |
| `icon-jiantouxia` | 箭头下 |
| `icon-chakangengduo` | 查看更多 |
| `icon-zhangjie` | 章节 |
| `icon-qiehuan` | 切换 |

**弹窗控制图标（3 个）**：

| icon 值 | 中文 |
|---------|------|
| `icon-danchuangguanli` | 弹窗管理 |
| `icon-danchuangquanping` | 弹窗全屏 |
| `icon-danchuangshouqi` | 弹窗收起 |

**业务模块图标（16 个）**：

| icon 值 | 中文 |
|---------|------|
| `icon-gongsi` | 公司 |
| `icon-gongshiyujianzhiguankong` | 公示与建筑管控 |
| `icon-gantetu` | 甘特图 |
| `icon-kanban` | 看板 |
| `icon-jindukeshihuagenzong` | 进度可视化跟踪 |
| `icon-biandongyuliushishuaitongji` | 变动率流失率统计 |
| `icon-xiangmuxinxiguanli` | 项目信息管理 |
| `icon-xiangmuzonglan` | 项目总览 |
| `icon-wendangyutaizhangguanli` | 文档与台账管理 |
| `icon-renyuanzonglan` | 人员总览 |
| `icon-renyuandanganguanli` | 人员档案管理 |
| `icon-zizhiyuzhunruguankong` | 资质与准入管控 |
| `icon-zhongqipingguyuyanshouguanli` | 中期评估与验收管理 |
| `icon-shigongguanli` | 施工管理 |
| `icon-hetongyufukuanguanli` | 合同与付款管理 |
| `icon-shuiliduixiangshuju` | 水利对象数据 |

```vue
<script setup lang="ts">
import { PlatformIcon } from '@st/platform-ui';
</script>

<template>
  <PlatformIcon icon="icon-xinzeng" />
  <PlatformIcon icon="icon-shanchu" />
  <PlatformIcon icon="icon-gantetu" />
</template>
```

### Lucide 图标

icon 值不以 `icon-` 开头时，PlatformIcon 委托给 `VbenIcon` 渲染。最常用的前缀是 `lucide:`，基于 Iconify 在线加载，支持 1000+ 图标。项目已在 `@vben/icons` 中离线导出了 67 个常用 Lucide 图标，离线导出的图标无需网络请求即可渲染。

**常用 Lucide 图标速查**：

| 分类 | icon 值示例 |
|------|------------|
| 操作 | `lucide:plus` `lucide:check` `lucide:x` `lucide:copy` `lucide:edit` `lucide:trash-2` |
| 导航 | `lucide:chevron-down` `lucide:chevron-left` `lucide:chevron-right` `lucide:arrow-up` `lucide:arrow-down` |
| 状态 | `lucide:circle-alert` `lucide:circle-check-big` `lucide:circle-x` `lucide:circle-help` `lucide:info` |
| 视图 | `lucide:layout-grid` `lucide:grid` `lucide:folder-tree` `lucide:table-properties` `lucide:fullscreen` |
| 用户 | `lucide:user-round-pen` `lucide:bell` `lucide:mail-check` `lucide:lock-keyhole` `lucide:log-out` |
| 主题 | `lucide:sun` `lucide:moon-star` `lucide:palette` `lucide:swatch-book` `lucide:languages` |

```vue
<script setup lang="ts">
import { PlatformIcon } from '@st/platform-ui';
</script>

<template>
  <PlatformIcon icon="lucide:plus" />
  <PlatformIcon icon="lucide:search" />
  <PlatformIcon icon="lucide:circle-alert" />
</template>
```

### 图标尺寸

PlatformIcon 的 iconfont 分支使用 `font-size: 1em`，Lucide 分支（VbenIcon）的 SVG 同样继承父元素字号。因此通过父元素的 `font-size` 或 CSS 类即可统一控制两种来源的图标大小。

```vue
<script setup lang="ts">
import { PlatformIcon } from '@st/platform-ui';
</script>

<template>
  <span style="font-size: 16px"><PlatformIcon icon="lucide:check" /></span>
  <span style="font-size: 24px"><PlatformIcon icon="lucide:check" /></span>
  <span style="font-size: 32px"><PlatformIcon icon="lucide:check" /></span>
  <span style="font-size: 16px"><PlatformIcon icon="icon-sousuo" /></span>
  <span style="font-size: 24px"><PlatformIcon icon="icon-sousuo" /></span>
  <span style="font-size: 32px"><PlatformIcon icon="icon-sousuo" /></span>
</template>
```

### Props

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `icon` | `string` | 图标标识。以 `icon-` 开头走 iconfont 字体图标，否则走 VbenIcon（Iconify） | 必填 |

组件会继续透传 `class`、`style` 等 HTML 原生属性。VbenIcon 分支还透传 `iconify` 相关属性（如 `width`、`height`、`color`）。

### Emits

无自定义事件。

### 设计约束

- iconfont 图标依赖项目字体文件，新增 iconfont 图标需更新 `apps/web-antd/src/assets/iconfont/` 目录下的字体文件和 CSS。
- Lucide 图标使用 `lucide:` 前缀，不要省略前缀直接写图标名（如 `plus`），否则 Iconify 无法正确解析。
- 不要在页面中直接使用 `<i class="iconfont icon-xxx">` 或 `<VbenIcon>` 渲染图标，统一通过 PlatformIcon 接入。
- 图标大小通过 `font-size` 控制，不要对 PlatformIcon 设置固定的 `width`/`height`，以免两种来源的尺寸行为不一致。
- `PlatformIcon` 是平台能力入口，页面里不要重复手写一套 iconfont 或 VbenIcon 的判断逻辑。
