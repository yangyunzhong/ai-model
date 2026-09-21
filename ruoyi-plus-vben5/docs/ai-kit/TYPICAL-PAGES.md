# 典型页面 Recipe

本文件把后台系统中重复率最高的页面固化成可复用 Recipe。AI 读取到新需求后，应先匹配下面 3 类页面，再基于典型页面替换业务字段、接口、按钮和文案。

## 使用原则

1. 先匹配页面类型，不直接从空白页面生成。
2. 复制结构思路，不复制业务数据和 Mock 内容。
3. 保留平台组件组合，替换字段、列、权限、接口、状态枚举。
4. 如果新需求只改变内容，不新增布局模式，不新增平台组件。
5. 如果新需求要求的结构超出 Recipe，先说明差异，再决定是否沉淀新 Recipe。

## 页面壳结构锁

典型页面不是视觉参考图，而是结构契约。AI 可以替换业务字段、表格列、接口和按钮文案，但不能重写筛选区、表格工具栏、表格容器和分页间距。

| 区域 | 必须保留 | 禁止替代 |
| --- | --- | --- |
| 筛选区 | `PlatformQueryPanel` 或平台查询插槽 | 自写 `.search-row`、`.search-panel`、`.filter-form` 后手调栅格和按钮间距 |
| 表格工具栏 | `PlatformTableToolbar` | 自写 `.toolbar`、`.table-actions`、`.operation-bar` 并分散按钮 |
| 表格容器 | 单一 `platform-surface` / `PlatformSection` 包住工具栏和表格 | 查询区、工具栏、表格各套一层随意 card 或无间距 div |
| 表格和分页 | `PlatformTable` 或已确认的 Vben/Vxe 表格体系 | 裸写 `a-table` 后自补 pagination 样式 |
| 模块节奏 | `--st-layout-section-gap`、`--st-module-content-padding` | 页面 scoped CSS 里硬编码 `padding: 24px`、`margin-bottom: 24px` 等标准节奏 |

识别到典型页需求时，先输出“Recipe 壳映射”，再写代码：

```md
Recipe 壳映射：
| 页面壳区域 | 本次使用 | 是否允许重写 |
| --- | --- | --- |
| 筛选区 | PlatformQueryPanel | 否 |
| 表格面板 | platform-surface / PlatformSection | 否 |
| 工具栏 | PlatformTableToolbar | 否 |
| 表格和分页 | PlatformTable | 否 |
```

## Recipe 1：筛选区 + 表格工具栏 + 表格 + 页码

| 项 | 内容 |
| --- | --- |
| 页面类型 | 标准查询列表页 |
| 典型页面 | 项目全景管理 - 项目总览 |
| 参考源码 | `apps/web-antd/src/views/project/overview/index.vue` |
| 数据源示例 | `apps/web-antd/src/views/project/overview/project-overview-source.ts` |
| 适用需求 | 项目列表、人员列表、资源台账、审批列表、合同列表、可分页管理清单 |

推荐结构：

```txt
Page
PlatformViewToolbar
PlatformQueryPanel
platform-surface
PlatformTableToolbar
PlatformTable
PlatformModal / PlatformDrawer
```

必须保留：

- 筛选区整块使用 `PlatformQueryPanel`；只替换字段，不改它的 grid、actions、折叠和响应式规则。
- `platform-surface` / `PlatformSection` 作为表格面板外壳，里面紧跟 `PlatformTableToolbar + PlatformTable`。
- 查询条件进入 query model。
- 表格列通过 columns/computed 管理。
- 工具栏主按钮放 `PlatformTableToolbar` 的 `actions`。
- `PlatformTableToolbar` 不额外包一层自定义宽度容器，不在页面 CSS 里改左右 padding。
- 表格操作列使用 `PlatformButton scene="action"`。
- 状态列使用 `PlatformStatusTag`。
- 分页底部空间由 `PlatformTable` 和表格面板负责，不在页面内写 `.pagination`、`.ant-pagination` 的 margin/padding 覆盖。
- 新增/编辑使用 `PlatformModal + PlatformEditForm`。

可替换：

- 页面标题、描述、header actions。
- 查询字段、表格列、操作按钮、状态枚举。
- 数据加载函数和 Mock source。
- 弹窗字段和详情抽屉内容。

禁止：

- 把典型页面当截图参考，只复刻字段和视觉，不复用 Recipe 壳结构。
- 自写 `.search-row`、`.search-actions`、`.toolbar`、`.table-wrapper`、`.pagination` 来替代平台组件。
- 裸写 `a-table` 后自行补分页。
- 将查询区、工具栏、分页拆成多个无规则 div。
- 页面内自写按钮、状态标签和弹窗 footer 间距。

常见错误和正确写法：

```vue
<!-- 错：字段对了，但筛选区/工具栏/分页壳被重写，后续最容易出现间距和自适应 bug -->
<div class="search-panel">
  <div class="search-row">...</div>
</div>
<div class="table-toolbar">...</div>
<a-table />
<div class="pagination">...</div>

<!-- 对：只替换字段、列、接口，壳结构保留平台组件 -->
<PlatformQueryPanel v-model="queryModel">...</PlatformQueryPanel>
<section class="platform-surface">
  <PlatformTableToolbar>...</PlatformTableToolbar>
  <PlatformTable :columns="columns" :data-source="rows" />
</section>
```

## Recipe 2：顶部数据指标卡片 + 表格工具栏 + 表格 + 页码

| 项 | 内容 |
| --- | --- |
| 页面类型 | 指标辅助型列表页 |
| 典型页面 | 智能考勤管理 - 施工管理 |
| 参考源码 | `apps/web-antd/src/views/battery/construction/index.vue` |
| 数据源示例 | `apps/web-antd/src/views/battery/construction/construction-source.ts` |
| 适用需求 | 有统计摘要的管理列表、审批状态清单、施工/考勤/质量类流程列表 |

推荐结构：

```txt
Page
PlatformViewToolbar
PlatformStatCard grid 或 PlatformQueryPanel
platform-surface
PlatformTableToolbar
PlatformTable
PlatformModal / PlatformDrawer / PlatformApprovalProgress
```

说明：如果需求明确有“顶部数据指标卡片”，在 `PlatformViewToolbar` 后插入 `PlatformStatCard` grid；如果页面更偏审批查询，可保留 `PlatformQueryPanel` 并把指标作为可选区。

必须保留：

- 指标卡使用 `PlatformStatCard`。
- 表格区继续使用 `PlatformTableToolbar + PlatformTable`。
- 如果页面同时有查询条件，查询区继续使用 `PlatformQueryPanel`，不能为了放指标卡改成自写筛选行。
- 指标区、查询区、表格面板之间只使用 `var(--st-layout-section-gap)`。
- 详情或审批进度使用 `PlatformModal`、`PlatformDrawer`、`PlatformApprovalProgress`。
- 标准模块间距使用 `var(--st-layout-section-gap)`。

可替换：

- 指标卡数量和指标字段。
- 查询条件是否出现、是否折叠。
- 表格列、操作列和详情弹窗内容。

禁止：

- 自写指标卡壳。
- 图省事把指标、查询、表格揉在同一个 card 里。
- 单页硬编码卡片 padding、gap、状态色。

## Recipe 3：树结构 + 右侧筛选区/表格工具栏 + 表格 + 页码

| 项 | 内容 |
| --- | --- |
| 页面类型 | 左树右表页 |
| 典型页面 | 智能考勤管理 - 文档列表 |
| 参考源码 | `apps/web-antd/src/views/battery/archive/document-list/index.vue` |
| 数据源示例 | `apps/web-antd/src/views/battery/archive/document-list/document-list-source.ts` |
| 适用需求 | 组织树 + 用户列表、分类树 + 文档列表、区域树 + 设备列表、目录树 + 台账 |

推荐结构：

```txt
Page
layout shell
PlatformTreePanel
right platform-surface
PlatformSectionTitle / PlatformTableToolbar
PlatformTable
PlatformEditForm / footer actions
```

必须保留：

- 左侧树使用 `PlatformTreePanel`。
- 右侧内容区使用平台 surface 和 token 间距。
- 右侧如果有筛选区，继续使用 `PlatformQueryPanel` 或 Recipe 已定义的平台查询插槽。
- 右侧工具栏、表格和分页仍然是一组 `PlatformTableToolbar + PlatformTable`，不能因左右布局重新写工具栏宽度和分页底部间距。
- 表格使用 `PlatformTable`，行选择通过 row-selection 管理。
- 右侧维护区使用 `PlatformEditForm` 和平台字段组件。

可替换：

- 树节点字段、搜索 placeholder、默认选中节点。
- 右侧标题、上传/新增按钮、表格列。
- 选中行后的维护信息或详情区。

禁止：

- 自写树面板、搜索框和树容器。
- 左右区块用随意 card 嵌套。
- 右侧表格和维护表单使用两套不一致的间距。

## AI 匹配规则

| 需求关键词 | 优先 Recipe |
| --- | --- |
| 查询、筛选、列表、分页、导出、新增、编辑、删除 | Recipe 1 |
| 指标、统计、总数、趋势、审批状态、顶部卡片 | Recipe 2 |
| 树、组织、分类、目录、区域、左侧导航、右侧列表 | Recipe 3 |

如果同时命中多个关键词，按页面主体判断：

- 主体是树驱动筛选，选 Recipe 3。
- 主体是指标辅助列表，选 Recipe 2。
- 主体是普通列表管理，选 Recipe 1。

## AI 输出格式

```md
页面 Recipe：Recipe 1 / 2 / 3
参考页面：apps/web-antd/src/views/...

替换清单：
| 类型 | 原页面 | 新需求 |
| --- | --- | --- |
| 标题 | | |
| 查询字段 | | |
| 表格列 | | |
| 操作按钮 | | |
| 数据接口/Mock | | |
| 权限码 | | |

不改动：
- 平台组件组合
- 标准间距 token
- 表格工具栏和分页结构
```
