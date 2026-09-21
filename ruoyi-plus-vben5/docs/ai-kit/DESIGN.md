# DESIGN.md

本文件是 AI 设计规则短入口。详细设计系统规则见 `DESIGN-SYSTEM.md`。

AI 在修改页面视觉、间距、组件样式、主题、卡片、表格、表单、弹窗前，必须先读取：

1. `DESIGN-SYSTEM.md`
2. `components-manifest.json`
3. `AI-USAGE.md`

长期设计 token 以 `packages/platform-styles/src/tokens/index.css` 为准。平台组件样式优先落到 `packages/platform-ui` 或 `packages/platform-styles`。页面 scoped CSS 只处理页面私有布局，例如分栏比例、高度、滚动区域和少量业务单元格。

禁止把标准模块间距、内容 padding、圆角、阴影和状态色散落在页面文件里。能用 token 表达的样式，优先使用 token。

当前品牌色硬规则：

1. 主色：`#009943`
2. 辅助色：`#b7d342`
3. 纯色主按钮用主色，纯色次要按钮用辅助色。
4. 页面出现多色强调时，主色与辅助色必须同时出现。
