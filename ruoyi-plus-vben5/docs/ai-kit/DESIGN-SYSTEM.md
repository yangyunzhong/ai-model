# 设计系统规范

本项目的设计系统由 `@st/platform-ui`、`@st/platform-styles`、DemoKit 和真实业务页面共同约束。AI 调整视觉时，必须先判断修改应该落在 token、平台组件、适配层还是页面局部。

## 技术栈

| 层 | 技术 |
| --- | --- |
| 框架 | Vue 3 + Vben Admin 5 |
| UI 基础 | antdv-next |
| 平台组件 | `@st/platform-ui` |
| 样式 token | `@st/platform-styles` |
| 表格适配 | VxeGrid + `@st/platform-adapter` |
| Demo 入口 | `apps/demokit` |

## Token 来源

主 token 文件：

```txt
packages/platform-styles/src/tokens/index.css
```

页面和平台组件优先使用语义变量，不直接写固定视觉值。

| 类型 | Token |
| --- | --- |
| 主色 | `--st-color-brand`、`--st-color-brand-hover`、`--st-color-brand-active` |
| 辅助色 | `--st-color-accent`、`--st-color-accent-hover`、`--st-color-accent-active` |
| 页面背景 | `--st-color-page-bg` |
| 卡片背景 | `--st-color-card-bg` |
| 文本 | `--st-color-text-primary`、`--st-color-text-secondary`、`--st-color-text-tertiary` |
| 边框 | `--st-color-border-subtle`、`--st-color-border-control` |
| 圆角 | `--st-radius-control`、`--st-radius-card` |
| 控件高度 | `--st-control-height` |
| 表格尺寸 | `--st-table-header-height`、`--st-table-row-height` |
| 模块间距 | `--st-layout-section-gap` |
| 内容内边距 | `--st-module-content-padding` |
| 阴影 | `--st-shadow-card`、`--st-shadow-stat-card` |

## 色彩引用规范

本项目当前统一采用 2 个核心品牌色：

| 角色 | 色值 | Token | 默认用途 |
| --- | --- | --- | --- |
| 主色 | `#009943` | `--st-color-brand` | 主按钮、一级强调、主导航、主进度、重点数据主趋势 |
| 辅助色 | `#b7d342` | `--st-color-accent` | 次要纯色按钮、辅助强调卡片、辅助数据高亮、主色之外的第二强调层 |

使用规则：

1. 纯色主按钮统一使用主色 `#009943`。
2. 纯色次要按钮统一使用辅助色 `#b7d342`，不再自由挑选黄绿、浅绿或其他邻近色。
3. 页面出现多色强调时，必须同时出现主色和辅助色，不允许只出现一种品牌绿就结束。
4. 卡片、按钮、统计块、页签、状态条等需要第二强调层时，优先从辅助色体系取值，而不是额外引入新的主题色。
5. 非品牌语义色仍按原语义保留：危险操作使用 `--st-color-danger`，告警类使用 warning 体系，不拿辅助色替代错误色。
6. 页面和组件优先引用 token，不直接在业务文件写 `#009943` 或 `#b7d342`；只有文档说明或 token 定义本身允许写原始色值。

推荐映射：

| 场景 | 推荐颜色 |
| --- | --- |
| 主操作按钮 / 当前主入口 | 主色 |
| 次操作纯色按钮 / 辅助切换按钮 | 辅助色 |
| 主卡片顶部描边 / 主趋势值 | 主色 |
| 辅助卡片标签 / 第二趋势值 / 辅助强调块 | 辅助色 |
| 多色统计区 | 至少一处主色 + 一处辅助色 |

## 间距规则

| 场景 | 默认值 |
| --- | --- |
| 页面模块纵向间距 | `var(--st-layout-section-gap)` |
| 内容区 padding | `var(--st-module-content-padding)` |
| 查询表单操作间距 | `var(--st-search-form-action-gap)` |
| 表格边缘单元格 padding | `var(--st-table-edge-cell-padding)` |

页面 scoped CSS 只处理页面私有布局，例如分栏比例、业务网格、高度、滚动区域。模块节奏、卡片 padding、表单密度、弹窗 footer 间距应优先由平台组件或 token 控制。

## 样式落点决策

| 问题 | 应修改哪里 |
| --- | --- |
| 所有页面按钮密度不一致 | `packages/platform-ui/src/button` 或 `packages/platform-styles/src/antd/button.css` |
| 主色/辅助色使用不一致 | `packages/platform-styles/src/tokens/index.css` + `DESIGN-SYSTEM.md` |
| 查询面板间距不一致 | `PlatformQueryPanel` 或 token |
| 表格行高、表头、分页不一致 | `packages/platform-styles/src/vxe-table` 或 `packages/platform-styles/src/antd/table.css` |
| 弹窗内容和 footer 间距不一致 | `PlatformModal` / `PlatformDrawer` |
| 单个业务页面左右栏比例 | 页面 scoped CSS |
| 新业务卡片只出现一次 | 页面私有组件 |
| 新业务卡片出现两次以上 | 评估沉淀为平台组件 |

## AI 视觉改动检查

修改视觉后，AI 必须回答：

1. 改动落点是 token、平台组件、适配层还是页面局部？
2. 是否会影响多个页面？
3. 是否同步 DemoKit 示例？
4. 是否存在页面级样式覆盖平台组件？
5. 是否运行 `pnpm run check:ai-kit`？
