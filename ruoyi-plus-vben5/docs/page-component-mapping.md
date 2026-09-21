# 页面组件映射

本文件记录已开发页面到平台组件的映射关系、CSS 边界和后续回收方向。长期前端治理规则以 `.trellis/spec/frontend/` 为准，AI 选型入口见 `docs/ai-kit/`。

## 总体边界

| 类型 | 当前原则 | 样式落点 |
| --- | --- | --- |
| 页面标题区 | 二级页面默认使用 `PlatformViewToolbar` | `components/platform/view` |
| 标准查询区 | 查询列表页优先使用 `PlatformQueryPanel`，表格工具栏不再混写筛选动作 | `components/platform/form` |
| 表格工具栏 | 业务主按钮最左、次要按钮随后；搜索、刷新、设置、全屏在右侧 | `components/platform/table` |
| 表格 | `PlatformTable` 默认提供序号列、表头筛选、列设置、分页与横向滚动能力 | `components/platform/table` |
| 统计卡 | 业务页只传标题、数值、趋势、图标和类型 | `components/platform/stat`、设计 token |
| 模块标题 | 优先使用 `PlatformSectionTitle` / `PlatformSection` / `PlatformEchartsPanel` | `components/platform/view`、`components/platform/chart` |
| 页面 scoped CSS | 只保留页面布局、业务单元格和暂未平台化的业务结构 | 页面文件 |

## 已开发页面映射

| 页面 / 模块 | 页面类型 | 当前使用组件 | 是否复用现有组件 | 暂留页面私有结构 | 建议处理方式 |
| --- | --- | --- | --- | --- | --- |
| `/workbench/index` 工作台首页 | 个性化工作台页 | `Page`、`PlatformSection`、`PlatformSectionTitle`、`PlatformSectionAction`、`PlatformSegmented`、`PlatformNoticeList(workbench)`、`PlatformEntryCard`、`PlatformButton`、`PlatformIcon` | 是 | 顶部问候区、最近访问标签条、待办筛选溢出浮层 | 模块卡片壳、标题编排、入口卡和消息列表已回收平台；保留少量页面级布局与交互壳 |
| `/project/information` 项目信息管理 | 查询列表页 + 分段表单弹窗 | `PlatformViewToolbar`、`PlatformQueryPanel`、`PlatformTableToolbar`、`PlatformTable`、`PlatformModal`、`PlatformSegmented`、平台表单控件 | 是 | 动态人员配置 / 设备清单行、弹窗表单分区稳高 | 查询区已接入 `PlatformQueryPanel`；后续评估 `PlatformDynamicRows` |
| `/project/progress` 进度可视化跟踪 | 看板 / 甘特图页 | `PlatformViewToolbar`、`PlatformSectionTitle`、ECharts 插件 | 部分 | `ProjectProgressBoard`、`ProjectProgressGanttChart`、预警列表 | 先核对现有 `PlatformStatusBoard` 是否可承接看板，不要继续复制私有看板 |
| `/project/contract` 合同与付款管理 | 多区块业务卡片页 | `PlatformViewToolbar`、`PlatformStatCard`、`PlatformStatusTag`、ECharts 插件 | 部分 | 合同卡片、`ContractPaymentMiniBar` | 付款节点暂留业务组件；出现第二个场景后再抽平台付款节点 / 步骤进度 |
| `/project/document` 文档与台账管理 | 文档台账页 | `PlatformViewToolbar`、`PlatformStatCard`、`PlatformSectionTitle`、`PlatformFileList` | 是 | 隐藏上传 input 与上传触发逻辑 | 保留；后续如多页上传，抽统一上传入口 |
| `/project/evaluation` 中期评估与验收 | 双区块业务页 + 表格 | `PlatformViewToolbar`、`PlatformStatCard`、`PlatformTableToolbar`、`PlatformTaskCard`、`PlatformTable`、`PlatformModal` | 是 | 评分样式、左右区块业务布局 | 待评估项目卡已接入 `PlatformTaskCard`；后续如更多任务卡复用，再扩展配置能力 |
| `/personnel/overview` 人员总览 | 查询列表页 | `PlatformViewToolbar`、`PlatformQueryPanel`、`PlatformStatCard`、`PlatformTableToolbar`、`PlatformTable`、`PlatformModal` | 是 | 人员姓名编号单元格、新增保存 Mock | 查询区已接入 `PlatformQueryPanel`；人员单元格后续并入 `PlatformEntityCell` |
| `/platform/typical-page` 人员档案管理 | 卡片档案页 + 详情抽屉 | `PlatformViewToolbar`、`PlatformButton`、`PlatformInput`、`PlatformModal`、`PlatformDrawer`、`PlatformStatusTag` | 部分 | 人员档案卡、卡片骨架屏、详情抽屉字段块 | 保留业务卡片；若与工时/评估卡共性增强，再抽实体卡平台能力 |
| `/personnel/qualification` 资质与准入管控 | 预警列表 + 规则配置页 | `PlatformViewToolbar`、`PlatformStatCard`、`PlatformSection`、`PlatformNoticeList` | 部分 | 准入规则卡片 | `PlatformNoticeList` 可保留；规则卡多页复用后抽配置卡 |
| `/personnel/turnover` 变动与流失率统计 | 统计图表页 | `PlatformViewToolbar`、`PlatformStatCard`、`PlatformEchartsPanel` | 是 | 流失率排行图 option、tooltip 避让、双侧标签 | 后续沉淀排行条形图 option 工厂 |
| `/personnel/worktime` 工时与兼职管控 | 预警卡片页 | `PlatformViewToolbar`、`PlatformStatCard`、`PlatformSectionTitle`、`PlatformStatusTag`、`PlatformButton` | 部分 | 超工时预警卡、指标块、通知状态 | 优先评估 `PlatformAlertCard` 与 `PlatformMetricGrid` |
| `/battery/construction` 施工管理 | 标准查询列表页 | `PlatformViewToolbar`、`PlatformQueryPanel`、`PlatformTableToolbar`、`PlatformTable` | 是 | 项目名称单元格、业务按钮提示文案 | 作为 `PlatformQueryPanel` 首个真实样板继续视觉复核 |

### 工作台首页组件映射

| 页面区域 | 组件 | 来源 | 处理方式 | 说明 |
| --- | --- | --- | --- | --- |
| 顶部问候区头像 | `Avatar` | `antdv-next` | 暂留页面层 | 目前仅首页使用，后续如多页复用再封装 `PlatformAvatar`。 |
| 顶部问候文案 | `PlatformButton`、普通文本 | `@st/platform-ui` | 平台组件 + 页面文本 | `个人中心` 采用平台按钮；当前首页直接使用 `PlatformButton`，底层再落到 `antdv-next/Button`。 |
| 日程 / 待办 / 快捷入口 / 应用入口 / 通知消息标题 | `PlatformSection`、`PlatformSectionTitle` | `@st/platform-ui` | 平台组件 | 统一模块卡片壳、标题、`title-extra / center / extra` 槽位，支持标题右侧贴按钮和标题中部放分段控件。 |
| 标题动作按钮 | `PlatformSectionAction` | `@st/platform-ui` | 平台组件 | `刷新`、`更多`、`查看更多日程` 等文本 + icon 常规动作统一使用平台组件，避免页面内各自拼 icon 和 hover 规则。 |
| 最近访问 | 普通按钮组 | 页面层 | 页面私有布局 | 位于快捷入口底部，左对齐标签按钮，点击直接进入对应系统。 |
| 快捷入口图标按钮 | `PlatformEntryCard` | `@st/platform-ui` | 平台组件 | 纵向快捷入口卡已收口为平台组件，页面只负责网格列数与间距。 |
| 应用入口网格 | `PlatformEntryCard`、`PlatformSegmented` | `@st/platform-ui` | 平台组件 + 页面私有布局 | 标题栏内居中分段；横向应用卡已收口为 `PlatformEntryCard app` 变体。 |
| 通知消息列表 / 待办列表 | `PlatformNoticeList(workbench)`、`Popover` | `@st/platform-ui` + `antdv-next` | 平台组件 + 页面私有交互 | 两行工作台列表布局已收口到 `PlatformNoticeList workbench`；待办筛选溢出浮层仍由页面层承接。 |
| 页面卡片壳 | `Page`、`PlatformSection` | `@vben/common-ui` + `@st/platform-ui` | 平台组件 + 页面壳 | 模块卡片统一走 `PlatformSection + platform-surface`；页面只负责左右两列、高度与底部对齐。 |

说明：当前首页直接使用的 `antdv-next` 原子件仅有 `Avatar`；`Button`、`Segmented`、消息列表等能力均已通过 `@st/platform-ui` 封装后再使用，页面只保留少量布局 CSS。

## 当前已沉淀的平台能力

| 能力 | 当前落点 | 已接入页面 |
| --- | --- | --- |
| 页面标题 + 描述 + 动作区 | `PlatformViewToolbar` | 项目、人员、施工等已开发页面 |
| 表格工具栏排序、搜索展开、刷新、设置、全屏 | `PlatformTableToolbar` | 项目总览、项目信息、人员总览、评估页、施工管理 |
| 表格序号列、表头筛选、列设置、分页 | `PlatformTable` | 项目总览、项目信息、人员总览、评估记录、施工管理 |
| 查询面板折叠、查询、重置 | `PlatformQueryPanel` | 施工管理、项目信息管理、人员总览 |
| 任务卡片标题、标签、元信息、进度和操作 | `PlatformTaskCard` | 中期评估与验收 |
| 统计卡视觉与图标尺寸 | `PlatformStatCard` | 项目、人员、合同、文档、评估、工时等页面 |
| 状态标签 | `PlatformStatusTag` | 表格状态列、卡片状态、详情抽屉 |
| 文件列表 | `PlatformFileList` / `PlatformFileItem` | 文档与台账管理 |
| 预警列表 | `PlatformNoticeList` / `PlatformNoticeItem` | 资质与准入管控 |
| 分段控件 | `PlatformSegmented` | 项目信息弹窗 |

## 暂留页面层的内容

| 内容 | 暂留原因 | 后续触发条件 |
| --- | --- | --- |
| 实体单元格 / 姓名编号 / 项目编号组合 | 当前字段与点击入口仍有业务差异 | 两个以上页面继续出现同类结构时抽 `PlatformEntityCell` |
| 超工时预警卡、人员档案卡 | 信息层级和操作按钮差异仍较大 | 卡片壳、头像、指标块、footer 操作出现稳定共性时抽平台卡片 |
| 动态明细行 | 目前只在项目信息进场信息中出现 | 后续弹窗继续出现“多行输入 + 增删”时抽 `PlatformDynamicRows` |
| 付款节点 mini bar | 当前属于合同付款业务语义 | 第二个付款 / 节点进度场景出现后再抽 |
| 甘特图 option 和排行图 option | 与具体业务数据强相关 | 同类图表出现第二个页面后沉淀 option 工厂 |

## 后续优先整理项

| 优先级 | 事项 | 建议 |
| --- | --- | --- |
| 高 | `PlatformQueryPanel` 存量接入复核 | `/project/information` 和 `/personnel/overview` 已迁移；下一步复核更多查询列表页是否符合迁移条件 |
| 高 | 卡片壳与指标块样式治理 | `/project/evaluation` 已沉淀 `PlatformTaskCard`；下一步从 `/personnel/worktime`、`/platform/typical-page` 继续观察共性 |
| 中 | `PlatformStatusBoard` 与 `ProjectProgressBoard` 边界 | 决定复用、升级或删除未成熟平台组件 |
| 中 | 演示目录清理 | `apps/web-antd/src/views/演示使用自行删除/` 已删除；后续仅保留对仍被路由引用的 `/views/demo` 做单独评估 |
| 中 | 文档持续更新 | 新增页面或平台组件后同步维护本映射表 |
