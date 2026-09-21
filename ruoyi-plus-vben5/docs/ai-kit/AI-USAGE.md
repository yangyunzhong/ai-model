# AI 使用手册

本手册用于约束 AI 生成页面、重构页面和补平台组件时的默认决策。目标是让不同 AI 工具都沿着已有工程结构选择和组合，而不是凭通用经验手写一套后台页面。

## 总原则

1. 先选页面模板，再选组件。
2. 先用 `@st/platform-ui` 复合组件，再考虑基础组件。
3. 先用 `@st/platform-styles` token，再写页面 scoped CSS。
4. 先复用 `apps/web-antd` 真实案例，再新增 DemoKit 示例。
5. 同一结构在两个以上页面出现时，优先沉淀到 `packages/platform-ui`。

## 平台组件精准引用规则

只要需求命中平台组件已覆盖的能力，就必须精准引用平台组件，而不是只模仿平台组件的样式。典型页面只是高频组合示例；真正的底层约束是“平台组件能力优先”。

| 已覆盖能力 | 必须引用 | 不允许写法 |
| --- | --- | --- |
| 查询/筛选 | `PlatformQueryPanel`、`PlatformSearchForm`、平台字段组件 | 页面内自写 search/filter 布局、label、按钮区间距 |
| 表格工具栏 | `PlatformTableToolbar` | 自写 toolbar/action-bar/operation-bar |
| 表格/分页 | `PlatformTable` 或已确认的 `useVbenVxeGrid` 体系 | 裸写 `a-table` 后页面级补分页、筛选、列设置 |
| 树面板 | `PlatformTreePanel` | 自写树搜索框、树容器和左右间距 |
| 弹窗/抽屉 | `PlatformModal`、`PlatformDrawer` | 自写 `a-modal`/`a-drawer` 并调 footer 间距 |
| 表单字段 | `PlatformFormItem`、`PlatformInput`、`PlatformSelect`、`PlatformDatePicker` 等 | 原生控件缺 placeholder、label 宽度和密度不统一 |
| 状态/按钮 | `PlatformStatusTag`、`PlatformButton` | 页面内自配颜色、大小、操作按钮间距 |

新增正式业务页面时，平台组件统一从 `@st/platform-ui` 引用；平台样式和标准间距统一依赖 `@st/platform-styles` token。除非组件能力不存在且已在交付说明里写明原因，否则不能在页面 scoped CSS 中复刻平台组件。

## 平台公共布局三大块

`web-antd` 业务页面默认都运行在同一套平台布局下，顶部导航栏、左侧导航栏、内容区页签不是单页能力，不允许在业务页内临时重写。

| 公共区域 | 当前契约 | 源码落点 | DemoKit 入口 |
| --- | --- | --- | --- |
| 顶部导航栏 | 单独占一整行；固定展示 logo + 系统名称；不放一级菜单 | `packages/effects/layouts/src/basic/layout.vue` | `平台布局三大块` |
| 左侧导航栏 | `platform-rail` 样式；默认收起；一级菜单显示图标 + 最多两行文字；品牌绿背景 | `packages/@core/ui-kit/menu-ui/src/components/menu.vue` | `平台布局三大块` |
| 内容区页签 | 记录已打开页面；支持点击跳转和关闭；替代内容区传统路径面包屑 | `packages/effects/layouts/src/widgets/content-page-tabs.vue` + `packages/platform-ui/src/view/platform-page-tabs.vue` | `PlatformPageTabs`、`平台布局三大块` |

开发规则：

1. 新增页面默认只实现右侧业务内容，不在页面内补顶部栏、左侧栏或页签条。
2. 缺少系统标题、菜单 hover、浮窗选中态、页签关闭态时，优先检查平台布局配置、菜单组件和 `PlatformPageTabs`，不要写页面 scoped CSS 临时覆盖。
3. 顶部导航栏与左侧导航栏属于 Vben layout/menu 源组件能力；内容区页签的展示层是 `PlatformPageTabs`，路由接入层是 `ContentPageTabs`。
4. DemoKit 中的 `平台布局三大块` 用于说明整体契约，`PlatformPageTabs` 用于查看页签展示组件本身。

AI 在动手前必须给出简短判断：

```md
平台组件命中：
| 需求区域 | 已有平台能力 | 引用方式 | 是否需要新增能力 |
| --- | --- | --- | --- |
| 筛选区 | PlatformQueryPanel | @st/platform-ui | 否 |
| 表格工具栏 | PlatformTableToolbar | @st/platform-ui | 否 |
```

## 组件选型表

| 场景 | 优先组件 | 避免方式 |
| --- | --- | --- |
| 页面标题、说明、操作 | `PlatformViewToolbar` | 手写标题栏、按钮散落在页面顶部 |
| 模块标题、标题右侧小动作、标题中部切换 | `PlatformSectionTitle`、`PlatformSection` | 在页面里手写标题布局或用绝对定位硬摆分段控件 |
| 页面已打开记录 / 内容区页签 | `PlatformPageTabs` | 在页面内自写一排可关闭 tab |
| 面包屑 / 静态层级定位 | `PlatformPageBreadcrumb` | 在页面标题中拼路径文案 |
| 工作台入口卡 / 功能导航卡 | `PlatformEntryCard` | 页面里重复手写图标 + 文案入口卡 |
| KPI / 摘要指标 | `PlatformStatCard` | 每页自写指标卡壳 |
| 查询筛选 | `PlatformQueryPanel`、`PlatformSearchForm` | 裸写 `a-form` 并手调间距 |
| 表格工具条 | `PlatformTableToolbar` | 查询按钮、刷新、设置混在多个 div |
| 数据表格 | `PlatformTable` 或 `useVbenVxeGrid` | 裸写 `a-table` 后再补分页和筛选 |
| 详情字段 | `PlatformDescriptions` | 手写多列 label/value 栅格 |
| 编辑表单 | `PlatformEditForm`、`PlatformForm` | 页面内维护大段表单布局 CSS |
| 字段输入 | `PlatformInput`、`PlatformSelect`、`PlatformDatePicker`、`PlatformRangePicker` | 原生输入控件缺 placeholder、密度不统一 |
| 弹窗 / 抽屉 | `PlatformModal`、`PlatformDrawer` | 裸写 `a-modal`、`a-drawer` 并单独调 footer 间距 |
| 状态 | `PlatformStatusTag`、`PlatformProgress` | 页面内自配颜色 |
| 文件 | `PlatformFileList`、`PlatformFileItem` | 每页自写附件行 |
| 通知 / 待办 | `PlatformNoticeList`、`PlatformTaskCard` | 自写卡片壳和 footer 操作 |
| 分段 / 视图切换 | `PlatformSegmented`、`PlatformViewSwitch` | 多个按钮临时切 tab |
| 图表容器 | `PlatformEchartsPanel` | 裸放 ECharts 容器 |

## 页面模板

后台管理页面优先匹配 `TYPICAL-PAGES.md` 中的 3 类 Recipe：标准查询列表页、指标辅助型列表页、左树右表页。只有新需求无法套入这 3 类时，才按下面通用模板重新组合。

### 后台列表页壳检查

生成或重构后台列表页时，先确认“壳”再替换业务内容。字段、列、接口、按钮文案可以变，下面 4 个壳区域默认不允许重写：

| 检查项 | 合格标准 | 出错表现 |
| --- | --- | --- |
| 筛选区壳 | 使用 `PlatformQueryPanel` / `PlatformSearchForm`，字段进入 query model | 自写 `.search-row`、`.filter-panel`，移动端换行错乱 |
| 工具栏壳 | 使用 `PlatformTableToolbar`，按钮通过插槽或 props 进入工具栏 | 工具栏宽度撑满/不留左右间距，按钮分散在多个 div |
| 表格面板壳 | `platform-surface` / `PlatformSection` 包住工具栏和表格 | 工具栏、表格、分页分裂成多个无规则 card |
| 分页壳 | 使用 `PlatformTable` 或已确认的 Vben/Vxe 表格分页机制 | 页面 CSS 覆盖 `.ant-pagination`，底部没有留白或出现粘底 bug |

如果 AI 发现自己准备写 `search-panel`、`table-toolbar`、`table-wrapper`、`pagination` 等页面级 class，应先停下，改为查 `components-manifest.json` 和 `TYPICAL-PAGES.md`。

### 列表页

推荐顺序：

```txt
PlatformViewToolbar
PlatformQueryPanel 或 PlatformSearchForm
PlatformSection
PlatformTableToolbar
PlatformTable 或 useVbenVxeGrid
PlatformModal / PlatformDrawer
```

适用：管理列表、资源台账、人员清单、施工管理、资料管理。

优先参考：`apps/web-antd/src/views/project/overview/index.vue`。

### 详情页

推荐顺序：

```txt
PlatformViewToolbar
PlatformStatusBoard 或 PlatformStatCard
PlatformDescriptions
PlatformSection
PlatformTable / PlatformFileList / PlatformApprovalProgress
```

适用：项目详情、设备详情、人员档案、合同详情。

### 表单页

推荐顺序：

```txt
PlatformViewToolbar
PlatformEditForm 或 PlatformForm
PlatformFormItem
PlatformInput / PlatformSelect / PlatformDatePicker
PlatformButton
```

适用：新增、编辑、配置、规则维护。

### 仪表盘

推荐顺序：

```txt
PlatformViewToolbar
PlatformStatCard
PlatformEchartsPanel
PlatformSection
PlatformEntryCard
PlatformNoticeList / PlatformTaskCard / PlatformTable
```

适用：工作台、经营概览、质量分析、进度看板。

如果仪表盘主体仍是表格列表，优先按 `TYPICAL-PAGES.md` 的 Recipe 2 处理。

## 缺少组件时

1. 搜索 `apps/demokit/src/registry.ts`、`packages/platform-ui/src`、`docs/page-component-mapping.md`。
2. 如果已有组件只缺 props，优先扩展现有组件。
3. 如果确实是新共性模式，先说明它影响哪些页面，再新增平台组件。
4. 如果只是单页业务结构，留在页面私有组件，并在组件映射里标注“暂留页面层”。
5. 平台组件新增后，同步 DemoKit 示例和 `components-manifest.json`。
6. 所有可点击交互必须显式提供 `cursor: pointer`，不能依赖浏览器或第三方组件默认光标；包括按钮、卡片入口、分段项、标签切换、图标入口和日期选择。
7. 模块卡片默认不使用描边；优先通过背景、圆角、阴影和间距区分层级。除非需求明确要求描边，否则不要给 `platform-surface` 或页面模块卡片增加 `border`。

## AI 交付格式

每次新增或明显重构页面，交付说明必须包含：

```md
组件映射：
| 页面区域 | 使用组件 | 样式落点 | 说明 |
| --- | --- | --- | --- |

平台治理影响：
- 已复用的平台能力：
- 暂留页面层的结构：
- 后续应回收为平台组件的能力：
- 已运行的检查：
```

## 禁止模式

- 禁止为了赶效果复制 DemoKit 示例作为业务源码。
- 禁止在业务页面里重新实现平台已有组件。
- 禁止新增 UI 组件库。
- 禁止页面级 CSS 覆盖平台组件默认样式来制造“局部一致”。
- 禁止在典型列表页中自写筛选区、表格工具栏、分页间距；这些属于 Recipe 壳结构，不属于业务字段内容。
- 禁止把 `margin: 24px`、`gap: 24px`、`padding: 24px` 这类标准节奏散落在页面中；应使用 `var(--st-layout-section-gap)` 或 `var(--st-module-content-padding)`。
