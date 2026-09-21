# 前端平台治理规范

> 适用于正式开发阶段维护 `ruoyi-plus-vben5/`、Vben Admin 5、antdv-next、平台组件、前端 Mock 页面和开发交接预览。PM/UI 原型阶段仍优先遵守 `../prototype/` 规范，先保证可运行前端 Mock MVP。

## 规则来源与落点

- `ruoyi-plus-vben5/AGENTS.md` 中的长期前端规则已整理到本文件。
- 子目录 `ruoyi-plus-vben5/AGENTS.md` 只保留入口指针，不再维护独立规则副本。
- `ruoyi-plus-vben5/docs/ai-kit/` 是面向 Codex、Trae 等 AI 工具的前端工程化入口，用于放置组件注册表、组件选型、设计 token、DemoKit 维护和检查命令。
- 新增长期有效的前端组件、布局、Mock、交付预览或平台治理规则时，优先更新 `.trellis/spec/frontend/`；只有跨全仓库协作边界的规则才更新根目录 `AGENTS.md`。

## 前端开发协作规则

1. 先理解需求：确认目标、业务场景、页面范围、数据来源、权限要求、验收标准和禁止修改范围。
2. 小范围、低风险修复可直接执行；涉及架构、全局样式、平台组件、Vben 核心布局、批量修改或不可逆影响时，先给方案并等待确认。
3. 方案至少说明可行性、模块划分、组件落点、样式落点、验证方式、风险和影响范围。
4. 大任务拆成可验证、可回滚的小阶段；每阶段完成后说明完成内容、修改文件、验证结果、遗留问题和下一步。
5. 重要上下文必须沉淀到仓库文档或 `.trellis/spec/`，不能只留在聊天记录里。
6. 用户说“开工继续”时，先读根 `AGENTS.md`、本前端规范索引和当前任务直接相关文件，输出接续摘要和建议执行顺序，再推进修改。
7. 用户说“今天结束”时，总结完成和未完成内容；如产生长期规则，更新对应 `.trellis/spec/frontend/` 文件。

接续摘要控制在 800 字以内，只保留下一轮必需信息，并覆盖当前目标、本轮完成内容、本轮改动文件、当前未完成问题、当前阻断点、关键规则、禁止修改范围、下一轮第一优先级、下一轮不要做什么、需要验证的页面和需要执行的检查命令。

## 技术参考入口

本项目基于 Vben Admin 5.0 + ant-design-vue。涉及 Vben、Ant Design Vue 或 ruoyi-plus-vben5 时，优先参考：

1. Vben Admin 5.0 文档：<https://doc.vben.pro/guide/introduction/vben.html>
2. Ant Design Vue 组件：<https://www.antdv.com/components/overview-cn>
3. ruoyi-plus-vben5：<https://github.com/imdap/ruoyi-plus-vben5>

文档、API、组件行为可能变化时，先查证再下结论。

## 平台组件治理

涉及 Button、Table、Tree、Form、Input、Select、DatePicker、Pagination、Modal、Drawer、Tabs、SearchForm、工具栏、操作列、状态标签、导航、面包屑等通用能力时，默认按平台级问题处理，而不是只改当前页面。

平台组件长期目标是 workspace 包化复用：

1. `packages/platform-ui`：平台 Vue 组件唯一源码。
2. `packages/platform-styles`：token、主题变量、全局样式。
3. `packages/platform-adapter`：Vben、Vxe、ECharts、Upload 等适配层。

要求：

1. 通过 `pnpm workspace` 和 `workspace:*` 依赖复用，不使用手工 `ln -s` 或多份组件拷贝。
2. 包化迁移期间，`apps/web-antd/src/components/platform` 只作为兼容出口或临时源头。
3. 新页面和新平台能力默认从 `@st/platform-ui` 引用；已有页面允许分批迁移，不做纯为导入路径整洁的大批量改动。
4. 平台组件源码不得反向依赖 `apps/web-antd` 的业务路由、业务 Mock、业务接口或页面状态。
5. 只要平台组件已覆盖某个能力，新页面必须精准引用对应平台组件和 token；不能只复制视觉效果，重新手写查询区、工具栏、分页、弹窗 footer、状态标签或字段间距。

## 默认落点

通用组件样式和交互优先落到：

1. 平台组件：`packages/platform-ui`
2. 兼容出口：`apps/web-antd/src/components/platform/index.ts`
3. 适配层：`packages/platform-adapter`
4. 全局样式与 token：`packages/platform-styles`
5. 组件验证页：`apps/web-antd/src/views/platform/typical-page/index.vue`

补充规则：

- `/platform/typical-page` 只作为组合验证场，不作为通用视觉样式最终落点。
- 页面 scoped CSS 只处理页面布局，如分栏、间距、高度、滚动区域。
- 平台默认间距通过 token 维护，模块间距默认 `--st-layout-section-gap: 24px`，内容区默认 `--st-module-content-padding: 24px`。
- 品牌色长期固定为主色 `#009943` 和辅助色 `#b7d342`；按钮、卡片、统计块、页签等视觉组件优先引用 `packages/platform-styles/src/tokens/index.css` 中对应 token，不在页面级自定义新的主题绿系。

## 平台默认能力

1. 查询列表页默认优先使用 `PlatformQueryPanel + PlatformTableToolbar`；`PlatformQueryPanel` 默认收起，除非页面被明确要求默认展开。
2. `PlatformTable` 表头筛选默认由平台接管，必须提供“全部”选项；空筛选等于默认选中“全部”，点击即生效，不显示默认“重置 / 确定”按钮。
3. `PlatformTable` 默认不做内部自适应滚动，除非页面明确要求或显式传入 `scroll.y`。
4. `PlatformTable` 和 `#/adapter/vxe-table` 默认提供序号列；特殊页面需显式关闭并说明原因。
5. 真实业务页当前大量使用 `useVbenVxeGrid`，不能未经确认直接用 `PlatformTable` 批量替换。
6. 所有输入控件必须提供符合业务语义的 placeholder。
7. 新增、编辑、维护类表单优先复用 `PlatformEditForm`、`PlatformFormItem` 及平台字段组件。
8. 表单布局只允许 `layout="vertical"` 或 `layout="horizontal" + label-preset="inline-compact"`；如果平台预设不满足需求，先扩展平台表单源组件。
9. 模块标题优先使用 `PlatformSectionTitle`、`PlatformSection` 或 `PlatformEchartsPanel`。
10. 二级页面顶部优先使用 `PlatformViewToolbar`；同级分段切换优先使用 `PlatformSegmented`。
11. 统计卡视觉值优先在平台组件和 token 维护；文件行场景优先使用 `PlatformFileList` / `PlatformFileItem`。
12. 弹窗内容区与 footer 间距统一由 `PlatformModal` 控制。
13. 平台封装组件遇到 Ant Design Vue 新旧语义属性变更时，优先在平台层做兼容转换。
14. 卡片类 hover 默认只做上移反馈；如需 hover 描边，必须说明业务原因并优先沉淀到平台组件或 token。
15. 纯色主按钮统一使用主色，纯色次要按钮统一使用辅助色；页面存在多色强调时，主色和辅助色必须同时出现。
16. 所有可点击交互在默认态或 hover 态都必须明确显示手型指针 `cursor: pointer`，包括按钮、分段项、标签切换、图标入口、卡片入口、日历日期、最近访问标签和列表操作；不能依赖浏览器或三方组件默认行为碰运气。
17. 模块卡片默认不使用描边；只通过背景、圆角、阴影和间距区分层级。除非页面被明确要求展示描边，否则 `platform-surface`、页面模块卡片和工作台类模块面板都不得默认加 `border`。

## 平台公共布局三大块

`apps/web-antd` 的页面默认共享顶部导航栏、左侧导航栏和内容区页签三块公共布局。新增页面只实现右侧业务内容，不在页面内重写这三块。

| 公共区域 | 当前规则 | 源码落点 |
| --- | --- | --- |
| 顶部导航栏 | 单独占一整行，左侧始终显示 logo + 系统名称，不放一级菜单。 | `packages/effects/layouts/src/basic/layout.vue` |
| 左侧导航栏 | 使用 `navigation.styleType = 'platform-rail'`；默认收起；一级菜单显示图标 + 文字；品牌绿背景；hover/选中底色为 `#0B0C0D` 26% 透明度。 | `packages/@core/ui-kit/menu-ui/src/components/menu.vue`、`packages/@core/ui-kit/layout-ui/src/components/layout-sidebar.vue` |
| 内容区页签 | 位于右侧内容区顶部，记录已打开页面，支持点击跳转和关闭；替代内容区传统路径面包屑。 | `packages/effects/layouts/src/widgets/content-page-tabs.vue`、`packages/platform-ui/src/view/platform-page-tabs.vue` |

要求：

1. 缺系统标题、菜单 hover、浮窗选中态、页签关闭态等问题，优先回到上述平台落点处理。
2. `PlatformPageTabs` 只负责页签展示和事件；路由记录、跳转、关闭等 Vben tabbar 行为由 `ContentPageTabs` 接入，避免 `packages/platform-ui` 反向依赖业务布局状态。
3. DemoKit 必须保留 `平台布局三大块` 规则页和 `PlatformPageTabs` 组件示例，作为后续页面开发的可视化入口。
4. 不允许在业务页面 scoped CSS 中单独覆盖顶部栏、左侧 rail 菜单、内容区页签的颜色、圆角、hover、选中态或间距。

## 典型页面壳约束

典型页面是页面壳契约，不是截图参考。AI 或开发者可以替换业务字段、接口、表格列、权限码和按钮文案，但不能重写平台已封装的结构壳。

强制约束：

1. 标准列表页的筛选区必须使用 `PlatformQueryPanel` 或已确认的平台查询组件，不自写 `.search-row`、`.search-panel`、`.filter-form`。
2. 表格面板必须保持 `platform-surface` / `PlatformSection` 内部包含 `PlatformTableToolbar + PlatformTable` 或项目确认的 Vben/Vxe 表格组合。
3. 工具栏左右留白、按钮间距和宽度由 `PlatformTableToolbar` 负责，不在页面级 CSS 覆盖。
4. 分页底部空间由 `PlatformTable`、Vben/Vxe 表格壳或表格面板负责，不在页面级写 `.pagination`、`.ant-pagination` 的 margin/padding。
5. 如果需求确实超出平台组件能力，先说明缺失能力、影响页面和建议沉淀位置，确认后再扩展平台组件；不先写临时页面壳绕过。

## 组件映射与确认

开发或修改前，按任务复杂度输出组件映射：

| 页面区域 | 原生组件 | 平台组件/适配层 | 是否改造 | 样式落点 |
| --- | --- | --- | --- | --- |
| 查询区 | `a-form` / `a-input` / `a-select` | `PlatformSearchForm` 等 | 是/否 | 平台组件/token |
| 表格 | `a-table` / Vxe Grid | `PlatformTable` / Vxe 适配层 | 是/否 | 平台组件/适配层 |
| 树结构 | `a-tree` | `PlatformTree` / `PlatformTreePanel` | 是/否 | 平台组件 |
| 弹窗/抽屉 | `a-modal` / `a-drawer` | `PlatformModal` / `PlatformDrawer` | 是/否 | 平台组件 |

规则：

- 简单样式反馈可输出简版映射。
- 复杂页面、截图复刻、平台级改造必须输出完整映射和文件归属。
- AI 新增或重构前端页面前，必须先查 `ruoyi-plus-vben5/docs/ai-kit/components-manifest.json`，选择已有平台组件；若不使用推荐组件，必须说明原因。
- AI 新增或修改页面路由前，必须先读 `ruoyi-plus-vben5/docs/ai-kit/ROUTING.md`，输出路由方案，并在完成后运行 `pnpm run check:routes` 或 `node ./scripts/check-web-routes.mjs`。
- 新增平台组件或扩展已有平台组件能力前，必须先说明为什么不能复用现有组件、影响哪些页面、是否有页面局部替代方案、是否需要 token 或全局样式支持，并等待用户确认。
- 即使是小反馈，只要修改点落在平台组件、适配层、token 或全局样式入口，也要先说明这是平台能力扩展，并给出简版映射和影响范围。

## 典型页面 Demo

`/platform/typical-page` 的目标是把真实业务页面中的业务内容抽取成平台组件验证场，用于暴露 ant-design-vue 组件平台化问题；它不是虚构页面，也不是业务源码复刻页。

规则：

1. 数据、字段、菜单、树节点、表格列、操作入口必须来自真实业务页面、路由、接口模型或已存在 Mock。
2. 后端不可用时，可以使用当前项目已有模型、字段、路由、页面配置和已存在 Mock 作为受控数据来源，但必须说明来源。
3. 典型页专用数据源放在页面专用适配文件中，避免污染真实业务页和全局 mock server。
4. 主体只依赖列表、树、保存、删除、状态切换等抽象方法，不依赖 Mock 实现细节。
5. 顶部导航、左侧导航、面包屑优先在 Vben 布局、菜单源逻辑、布局样式或对应导航组件中处理。
6. 当前预览阶段只保留“平台组件”模块作为唯一可见一级模块，默认首页指向 `/platform/typical-page`。

## Vben 布局保护

Vben Layout、Menu、Breadcrumb、Tabs、Route-to-Menu 属于后台框架地基层，不为单个页面随意修改。

高风险目录：

1. `packages/effects/layouts/**`
2. `packages/@core/ui-kit/menu-ui/**`

涉及顶部导航、左侧菜单、breadcrumb、tabs、keep-alive、mixed menu、route-to-menu、权限菜单时，按以下顺序排查：

1. 页面使用方式
2. 路由配置或 menu meta
3. mock 菜单源
4. layout、menu、theme 配置
5. token 或 CSS 变量
6. 平台组件或项目适配层
7. Vben 核心源码

如果必须修改 Vben 核心布局或菜单逻辑，先输出问题归因、影响范围、回滚方案和验证方案，确认后再开发。

## 前端 Mock 开发与接口未联通模式

当前阶段主要用于维护平台组件库、产出前端 mock 交互页面源码，并在后续交付给前端开发与后端联调；不得因为真实后端接口未联通而阻塞页面开发。

### 本地预览端口

1. `apps/web-antd` 固定使用 `http://127.0.0.1:5175/`，`apps/demokit` 固定使用 `http://127.0.0.1:5174/`。
2. 启动 `web-antd` 前必须检查 `5173/5175` 是否已有旧的 `web-antd` Vite 进程；如存在，先停止旧进程，再启动新的 `5175`。
3. `web-antd` dev 脚本必须使用 `--strictPort --force`，端口占用或缓存异常要直接暴露，不能让 Vite 自动漂移到其他端口。
4. 同一时刻不允许同时保留多个 `web-antd` dev server；否则浏览器可能打开旧端口，导致看到旧版页面。

### 登录与权限

1. 必须保留登录页，不允许删除登录页来绕过问题。
2. 优先复用 Vben 既有登录、token、用户信息、权限、菜单、动态路由和路由守卫机制。
3. 当 `VITE_USE_MOCK=true` 或等价 Mock 配置开启时，登录流程必须使用前端假数据完成，并提供假 token、假用户信息、假角色权限、假菜单和假路由数据。
4. 页面刷新后，如本地已有 Mock token，应保持登录状态，不因真实后端接口失败退回登录页。

### 开发边界

1. 当前阶段不对接真实后端登录、用户信息、权限或菜单接口，不要求用户先提供后端接口地址。
2. 不因为接口 404、500、跨域、token 失效或菜单接口失败阻塞前端 mock 页面开发。
3. 页面数据优先使用 Mock 数据和前端本地状态模拟，表格、新增、编辑、删除、详情、筛选等交互当前优先做前端模拟。
4. 除登录状态恢复所需 token 和基础状态外，不额外引入持久化复杂度。

### 切换真实接口

1. 切换真实后端时，优先通过环境变量关闭 Mock，例如 `VITE_USE_MOCK=false`。
2. 不为 Mock 模式大规模重构框架底层，不引入新的 UI 组件库或独立权限体系来解决登录问题。
3. Mock 数据结构尽量贴近真实接口返回，降低后续联调迁移成本。
4. 如果 Mock 逻辑只是开发期模拟，必须在交付报告中说明位置、影响范围和切换方式。

### 交付预览构建

1. 联调交付包的 `preview/` 必须使用专门的 handoff 环境构建，不直接拿 `production` 产物改路径。
2. 构建时必须提供 `apps/web-antd/.env.handoff`，并通过 `vite build --mode handoff` 的等价流程触发 handoff 配置。
3. 给项目经理和客户演示的交付包，主入口优先考虑 Windows 电脑：提供无需开发环境、无需手动打开终端的双击启动器。
4. 不再把 `file://` 直接打开 Vue/Vite SPA 当作主预览方案；`index.html` 只做备用说明入口，真实预览必须通过本地 HTTP 服务进入登录页。

## 验证与收尾

修改平台组件后，至少说明：

1. 当前典型页面是否生效。
2. 真实业务页是否已接入，哪些还未接入。
3. 是否有页面绕过平台组件直接使用 ant-design-vue 原生组件。
4. 是否存在页面级样式覆盖导致平台样式不生效。
5. 如果未做浏览器验证、构建或类型检查，说明原因。
6. 本轮最终命中的生效层级是源码 token、主题注入、适配层、平台组件样式还是页面局部补丁。

典型页面、截图页面、Figma 页面或业务右侧内容页收尾时，收尾报告需补“平台治理影响”，至少覆盖：

1. 本轮发现的 ant-design-vue 原生组件问题。
2. 已通过平台层解决的问题。
3. 仍是页面临时实现的问题。
4. 未来应回收为平台组件的页面子组件。
5. 后续新页面禁止继续复制的实现。
6. 应进入主题变量或统一样式入口的样式。
7. 仍存在的页面级样式债务。
