# 平台组件包化治理方案

本文档用于约束 `apps/web-antd` 平台组件从应用内目录迁移到 workspace 包的过程。目标是避免维护多份组件拷贝，同时保证一处修改、处处生效，并尽量不影响已经开发完成的 `web-antd` 功能。

## 目标

1. 平台组件只保留一份源码。
2. 业务页面统一引用平台组件，不再自行复制 `Button / Table / Form / Modal / Drawer / Tree` 等通用实现。
3. 通过 `pnpm workspace` 管理本地包引用，由包管理器自动生成软连接，不手工维护 symlink。
4. 迁移过程保持可回滚、可验证、可分阶段交付。
5. 前端源码联调交付包保留 monorepo 结构，保证平台包可以被正常解析。

## 包职责

| 包路径 | 包名 | 职责 | 是否承载业务逻辑 |
| --- | --- | --- | --- |
| `packages/platform-ui` | `@st/platform-ui` | Vue 平台组件唯一源码，例如表格、查询面板、弹窗、抽屉、标题、状态、文件列表等 | 否 |
| `packages/platform-styles` | `@st/platform-styles` | 平台 token、主题变量、Antdv 覆盖样式和全局样式入口 | 否 |
| `packages/platform-adapter` | `@st/platform-adapter` | Vben、Vxe、ECharts、Upload 等适配层 | 否 |
| `packages/platform-types` | `@st/platform-types` | 平台公共类型 | 否 |
| `packages/platform-utils` | `@st/platform-utils` | 与业务无关的纯工具函数 | 否 |
| `apps/web-antd` | `@vben/web-antd` | 业务页面、路由、Mock、权限、页面状态和业务数据 | 是 |

## AI Kit 入口

为了让 Codex、Trae 等 AI 工具按同一套工程结构生成页面，本项目新增 `docs/ai-kit/` 作为 AI 可读入口：

| 文件 | 作用 |
| --- | --- |
| `docs/ai-kit/AGENTS.md` | AI 读取顺序和输出要求 |
| `docs/ai-kit/AI-USAGE.md` | 页面模板、组件优先级、禁止模式 |
| `docs/ai-kit/DESIGN-SYSTEM.md` | token、间距、样式落点 |
| `docs/ai-kit/DEMO-KIT.md` | DemoKit 维护边界 |
| `docs/ai-kit/components-manifest.json` | 组件注册表，供 AI 做选型 |

新增或重构页面前，AI 必须先读 `ruoyi-plus-vben5/AGENTS.md` 和 `docs/ai-kit/AI-USAGE.md`，并输出组件映射。完成后至少运行：

```bash
pnpm run check:ai-kit
```

## 硬性规则

1. 不手工创建跨项目 symlink 作为正式复用方式。
2. 不复制一份平台组件到另一个 app 或仓库后各自维护。
3. 不在业务页面内长期维护通用组件样式补丁。
4. 不把业务接口、业务 Mock、业务路由和页面状态放进平台包。
5. 不在未验证前一次性改全量业务页面导入路径。
6. 不为了包化迁移修改 `node_modules`、Ant Design Vue 源码或 Vben 核心布局。

## 引用规则

迁移完成后的新页面默认从平台包引用：

```ts
import { PlatformQueryPanel, PlatformTable, PlatformModal } from '@st/platform-ui';
```

迁移过渡期允许保留 `apps/web-antd/src/components/platform/index.ts` 作为兼容出口，但它只能转发平台包，不再承载第二份组件源码。

```ts
export * from '@st/platform-ui';
```

过渡期内，已有页面可以继续使用：

```ts
import { PlatformTable } from '#/components/platform';
```

但新增页面、重构页面和平台组件改造应优先使用 `@st/platform-ui`。

## 修改平台组件时怎么提需求

如果目标是所有页面统一生效，需求应明确说：

> 不要只改当前页面，请从平台组件源头处理，所有引用该组件的页面统一生效。

如果只允许当前页面特殊处理，需求应明确说：

> 这次只改当前页面布局，不上升到平台组件。

如果用户没有明确说“只改当前页面”，Button、Table、Form、Input、Select、DatePicker、Modal、Drawer、Tabs、Tree、工具栏、状态标签、文件列表等通用能力默认按平台组件源头处理。

## 迁移阶段

### 阶段 0：规范和包骨架

1. 新增 `@st/platform-ui` 包骨架。
2. 写清楚平台组件包化规则、迁移边界、交付影响和验证方式。
3. 不移动现有组件源码，不改业务页面导入。

验收标准：

1. 文档规则明确。
2. 当前页面功能不受影响。
3. 后续迁移步骤和回滚边界清楚。

### 阶段 1：无行为变化迁移

1. 将 `apps/web-antd/src/components/platform/**` 迁移到 `packages/platform-ui/src/**`。
2. 保持目录结构和导出名不变。
3. 将 `apps/web-antd/src/components/platform/index.ts` 改成只转发 `@st/platform-ui`。
4. 只新增 `apps/web-antd` 对 `@st/platform-ui` 的 workspace 依赖，不批量修改业务页面导入。

验收标准：

1. 已开发页面仍可运行。
2. `#/components/platform` 兼容出口仍可用。
3. 修改 `packages/platform-ui` 后，`web-antd` 页面实时生效。

### 阶段 2：新增页面切换包引用

1. 新增页面统一从 `@st/platform-ui` 引用。
2. 存量页面只在被实际修改时顺手切换导入路径。
3. 不为了导入路径整洁做无业务收益的大批量改动。

验收标准：

1. 新增页面不再从 app 内平台目录引用。
2. 存量页面功能不因导入路径迁移产生回归。

### 阶段 3：样式与适配层拆分

1. 稳定 token、全局样式和 Antdv 覆盖进入 `packages/platform-styles`。
2. Vxe、ECharts、Vben 适配进入 `packages/platform-adapter`。
3. 平台组件只依赖平台样式和适配包，不反向依赖业务 app。

验收标准：

1. 平台 UI、样式和适配职责清晰。
2. `apps/web-antd` 只消费平台能力，不维护第二份平台源码。

当前状态：

1. `@st/platform-styles`、`@st/platform-adapter`、`@st/platform-types`、`@st/platform-utils` 的 workspace 包骨架已创建。
2. `@st/platform-styles/tokens` 已承载 ST-design 平台 CSS 变量，包括品牌色、文字色、边框、控件高度、表格尺寸、模块间距、字号、圆角和阴影；Vben core design token 文件只保留通用主题变量，并通过 `var(--st-*)` 消费平台 token。
3. `apps/web-antd` 运行时样式入口已切到 `@st/platform-styles/antd`；该入口当前已承载 Ant Design Vue 全局覆盖和平台 CSS，并先导入 `@st/platform-styles/tokens`，`packages/styles/src/antd/index.css` 仅保留兼容桥接。Antdv 覆盖内部已按覆盖对象拆分为 `base / button / field / form / tree / table / pagination / overlay / tag / platform-common / feedback / validation / utilities` 等分文件，`index.css` 只负责组织导入顺序。
4. `@st/platform-styles/vxe-table` 已开始承载稳定 Vxe 表格变量和全局覆盖，并先导入 `@st/platform-styles/tokens`；`packages/effects/plugins/src/vxe-table/style.css` 仅保留对平台样式入口的桥接导入。
5. `apps/web-antd/src/adapter/vxe-table.ts` 已切到 `@st/platform-adapter/vxe-table`；该平台入口已开始承载稳定 Vxe 规则，包括默认序号列、工具栏默认项、行高、刷新方式、复选状态判断和排序参数转换。
6. `@st/platform-adapter/echarts` 已作为无行为变化入口接入，当前转发现有 Vben ECharts 插件，平台组件和应用图表消费者已开始通过该入口消费 ECharts 能力。
7. `@st/platform-adapter/upload` 已承载上传纯类型、默认图片/文件 accept 列表、默认文件预览函数、`UploadInfoApi` 查询抽象、`UploadFeedbackAdapter` 反馈抽象，以及裁剪上传参数 / 返回 / API 契约类型；应用侧上传组件通过该入口复用这些稳定定义，并继续在 `apps/web-antd` 注入默认 `uploadApi`、`ossInfo`、全局消息/确认框反馈和头像裁剪 UI。
8. `apps/web-antd/src/adapter/vxe-table.ts` 仍保留应用侧初始化职责，包括 `useVbenForm` 注入和 `CellImage` / `CellLink` 等当前应用渲染器注册。
9. Upload 的 `FileUpload` / `ImageUpload`、`uploadApi`、`ossInfo`、头像裁剪 UI 和业务弹窗仍绑定业务 API 与应用状态，未拆清前不进入 `@st/platform-adapter`。

## 交付包影响

### 预览包

Vite 构建 `preview/` 时会把 `@st/platform-ui` 中被引用的组件一起打进产物，客户或项目经理运行预览包时不需要单独安装平台组件包。

### 源码联调包

给前端和后端联调的源码包不能只包含 `apps/web-antd`。必须保留 monorepo 必需文件和平台包：

```txt
package.json
pnpm-workspace.yaml
pnpm-lock.yaml
apps/web-antd
packages/platform-ui
packages/platform-styles
packages/platform-adapter
packages/platform-types
packages/platform-utils
packages/@core/**
packages/effects/**
internal/**
```

如果只交付 `apps/web-antd`，workspace 依赖会缺失，`@st/platform-ui` 和现有 `@vben/*` 包都可能无法解析。

## 验证要求

每个迁移阶段至少做以下检查：

1. `git diff --check`
2. 目标文件 ESLint 或包级类型检查
3. 本地预览 HTTP 检查
4. 至少验证 `/platform/typical-page`
5. 至少验证一个真实业务页，例如 `/workbench/index`、`/project/information`、`/battery/construction`

如果浏览器验证未完成，报告中必须明确说明，不得直接说“视觉已经生效”。

## 回滚策略

1. 阶段 0 只新增文档和包骨架，可直接回滚文档与包定义。
2. 阶段 1 保留 app 内兼容出口，若平台包解析异常，可临时把出口恢复为原目录导出。
3. 阶段 2 只逐步切导入路径，单个页面可单独回退。
4. 阶段 3 涉及样式和适配层，必须按组件或样式入口分批提交，禁止混在一次大改里。
