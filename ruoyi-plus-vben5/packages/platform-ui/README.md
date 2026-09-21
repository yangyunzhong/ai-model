# @st/platform-ui

`@st/platform-ui` 是 ST-design 的平台 Vue 组件包，承载业务应用可复用的 `Platform*` 组件。

## 使用规则

新增页面默认从本包引用平台组件：

```ts
import {
  PlatformModal,
  PlatformQueryPanel,
  PlatformTable,
} from '@st/platform-ui';
```

迁移过渡期内，`apps/web-antd/src/components/platform/index.ts` 仍保留兼容出口：

```ts
export * from '@st/platform-ui';
```

所以存量页面继续使用 `#/components/platform` 不会立刻失效。新增页面、重构页面和平台组件改造优先使用 `@st/platform-ui`。

## 职责边界

本包只放平台 UI 组件，不放业务逻辑。

可以放入：

1. 平台表格、查询面板、表单字段、弹窗、抽屉、树、状态标签、标题、文件列表等通用组件。
2. 与组件强相关的类型。
3. 与组件内部渲染相关的局部样式。

不要放入：

1. 业务页面、业务路由、业务 Mock、业务接口。
2. 只服务单个页面的临时布局。
3. Vben 核心布局、Ant Design Vue 源码或 `node_modules` 补丁。
4. 跨平台的 token、主题变量和全局样式，这些后续应进入 `@st/platform-styles`。
5. Vxe、ECharts、Upload 等适配层，这些后续应进入 `@st/platform-adapter`。

## 修改组件时的原则

如果一个问题需要所有页面统一生效，修改本包中的平台组件源头。

如果只是单个页面的特殊布局，保留在业务页面，不上升到平台包。

修改平台组件后至少检查：

1. `git diff --check`
2. 平台组件目录 ESLint
3. 至少一个平台验证页
4. 至少一个真实业务页

浏览器验证未完成时，交付报告必须明确说明。
