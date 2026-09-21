import type { Component } from 'vue';

import ApprovalProgressDemo from './demos/approval-progress-demo.vue';
import ButtonDemo from './demos/button-demo.vue';
import ChartDemo from './demos/chart-demo.vue';
import EntryCardDemo from './demos/entry-card-demo.vue';
import FieldDemo from './demos/field-demo.vue';
import FileDemo from './demos/file-demo.vue';
import FormDemo from './demos/form-demo.vue';
import IconDemo from './demos/icon-demo.vue';
import NoticeListDemo from './demos/notice-list-demo.vue';
import OverlayDemo from './demos/overlay-demo.vue';
import ProgressDemo from './demos/progress-demo.vue';
import SegmentedDemo from './demos/segmented-demo.vue';
import StatCardDemo from './demos/stat-card-demo.vue';
import StatusTagDemo from './demos/status-tag-demo.vue';
import TableDemo from './demos/table-demo.vue';
import TaskCardDemo from './demos/task-card-demo.vue';
import TitleDemo from './demos/title-demo.vue';
import TreeDemo from './demos/tree-demo.vue';
import ViewLayoutDemo from './demos/view-layout-demo.vue';
import ViewToolbarDemo from './demos/view-toolbar-demo.vue';
import DesignSystemSpecPage from './guides/design-system-spec-page.vue';
import PlatformLayoutContractPage from './guides/platform-layout-contract-page.vue';
import SystemBuildGuidePage from './guides/system-build-guide-page.vue';
import SystemComponentPriorityPage from './guides/system-component-priority-page.vue';

export type DemoCategoryEntry = {
  description: string;
  title: string;
};

export type DemoComponentEntry = {
  api: string[];
  category: string;
  component: Component;
  demoPath: string;
  description: string;
  group: string;
  id: string;
  name: string;
  scenarios: string[];
  sourcePath: string;
  title: string;
  usageSnippet: string;
};

export type DemoProjectEntry = {
  coverImage: string;
  description: string;
  name: string;
  openHref: string;
  routePath: string;
  sourcePath: string;
  title: string;
};

export type TypicalBusinessPageEntry = {
  components: string[];
  description: string;
  id: string;
  recipe: string;
  replacementPoints: string[];
  routeHint: string;
  sourcePath: string;
  title: string;
};

export type GuideEntry = {
  component: Component;
  description: string;
  id: string;
  title: string;
};

export const componentCategories: DemoCategoryEntry[] = [
  {
    description: '按钮、图标和标题等可跨页面复用的基础能力。',
    title: '通用',
  },
  {
    description: '用于组织页面结构、模块容器和内容堆叠。',
    title: '布局',
  },
  {
    description: '用于页面切换、路径导航和视图模式切换。',
    title: '导航',
  },
  {
    description: '用于表单字段、筛选区、编辑区等数据录入场景。',
    title: '数据录入',
  },
  {
    description: '用于列表、树、表格、看板、文件与图表等数据呈现。',
    title: '数据展示',
  },
  {
    description: '用于状态反馈、流程进度与覆盖层交互。',
    title: '反馈',
  },
];

export const demoProjects: DemoProjectEntry[] = [
  {
    coverImage: '/images/web-antd-home.png',
    description: '当前主业务项目，承载项目管理、合同、资料、评估和进度等页面。',
    name: 'web-antd',
    openHref: 'http://127.0.0.1:3000',
    routePath: 'ruoyi-plus-vben5/apps/web-antd',
    sourcePath: 'apps/web-antd/src/views/project',
    title: '项目管理平台',
  },
];

export const businessPages: TypicalBusinessPageEntry[] = [
  {
    components: [
      'PlatformViewToolbar',
      'PlatformTableToolbar',
      'PlatformTable',
      'PlatformModal',
      'PlatformDrawer',
    ],
    description: '筛选区、表格工具栏、表格、页码组成的标准查询列表页。后续普通管理清单优先套这个结构。',
    id: 'recipe-filter-table-page',
    recipe: 'Recipe 1：筛选区 + 表格工具栏 + 表格 + 页码',
    replacementPoints: ['标题与描述', '查询字段', '表格列', '操作按钮', '状态枚举', '新增/编辑/详情弹窗'],
    routeHint: '项目全景管理 / 项目总览',
    sourcePath: 'apps/web-antd/src/views/project/overview/index.vue',
    title: '标准查询列表页',
  },
  {
    components: [
      'PlatformViewToolbar',
      'PlatformStatCard',
      'PlatformQueryPanel',
      'PlatformTableToolbar',
      'PlatformTable',
      'PlatformApprovalProgress',
    ],
    description: '顶部指标卡片辅助表格管理的列表页。适合施工、考勤、审批状态、质量统计等带摘要指标的页面。',
    id: 'recipe-metric-table-page',
    recipe: 'Recipe 2：顶部数据指标卡片 + 表格工具栏 + 表格 + 页码',
    replacementPoints: ['指标卡字段', '查询条件', '表格列', '审批/详情弹窗', '顶部操作按钮'],
    routeHint: '智能考勤管理 / 施工管理',
    sourcePath: 'apps/web-antd/src/views/battery/construction/index.vue',
    title: '指标辅助型列表页',
  },
  {
    components: [
      'PlatformTreePanel',
      'PlatformSectionTitle',
      'PlatformTable',
      'PlatformEditForm',
      'PlatformButton',
    ],
    description: '左侧树结构驱动右侧表格与维护信息。适合组织树、分类树、目录树、区域树等后台管理页面。',
    id: 'recipe-tree-table-page',
    recipe: 'Recipe 3：树结构 + 右侧筛选区/表格工具栏 + 表格 + 页码',
    replacementPoints: ['树节点模型', '右侧标题与操作', '表格列', '行选择逻辑', '维护信息表单'],
    routeHint: '智能考勤管理 / 文档列表',
    sourcePath: 'apps/web-antd/src/views/battery/archive/document-list/index.vue',
    title: '左树右表管理页',
  },
  {
    components: ['Vben Authentication', 'LoginForm', 'Mock token flow'],
    description: '系统登录入口。原型和 Mock 模式必须保留登录页，不通过删除登录页绕过权限流程。',
    id: 'recipe-login-page',
    recipe: '登录页：Vben 认证入口 + Mock 登录状态',
    replacementPoints: ['登录标题', '认证方式', 'Mock 用户信息', '菜单和权限数据', '真实接口切换方式'],
    routeHint: '系统登录',
    sourcePath: 'apps/web-antd/src/views/_core/authentication/login.vue',
    title: '登录页',
  },
];

export const componentRegistry: DemoComponentEntry[] = [
  {
    api: ['scene: default/toolbar/action/collapse', 'type: 按钮类型', 'disabled: 禁用', 'loading: 加载态'],
    category: '通用',
    component: ButtonDemo,
    demoPath: 'apps/demokit/src/demos/button-demo.vue',
    description: '项目内按钮统一入口，覆盖工具栏、行操作、普通按钮和收起按钮。',
    group: 'button',
    id: 'platform-button',
    name: 'PlatformButton',
    scenarios: ['页面主操作', '表格操作', '工具栏'],
    sourcePath: 'packages/platform-ui/src/button/platform-button.vue',
    title: '按钮',
    usageSnippet: `<PlatformButton scene="toolbar" type="primary">
  新增
</PlatformButton>`,
  },
  {
    api: ['icon: 图标名，兼容 iconfont 与 lucide'],
    category: '通用',
    component: IconDemo,
    demoPath: 'apps/demokit/src/demos/icon-demo.vue',
    description: '统一兼容 iconfont 与 @vben/icons，页面层不需要判断图标来源。',
    group: 'icon',
    id: 'platform-icon',
    name: 'PlatformIcon',
    scenarios: ['按钮图标', '状态图标', '菜单图标'],
    sourcePath: 'packages/platform-ui/src/icon/platform-icon.vue',
    title: '图标',
    usageSnippet: `<PlatformIcon icon="lucide:folder-tree" />`,
  },
  {
    api: ['title: 标题文案', 'description: 标题说明', 'icon/iconSrc: 图标来源', 'title-extra: 紧贴标题右侧的小动作', 'center: 中间区域', 'extra: 右侧附加操作'],
    category: '通用',
    component: TitleDemo,
    demoPath: 'apps/demokit/src/demos/title-demo.vue',
    description: '统一二级模块标题、说明和右侧操作，适合详情页、卡片头和区块标题。',
    group: 'view',
    id: 'platform-section-title',
    name: 'PlatformSectionTitle',
    scenarios: ['详情区块标题', '卡片头部', '模块标题'],
    sourcePath: 'packages/platform-ui/src/view/platform-section-title.vue',
    title: '模块标题',
    usageSnippet: `<PlatformSectionTitle
  icon="lucide:badge-check"
  title="运行概览"
  description="查看当前执行状态和负责人"
>
  <template #title-extra>
    <PlatformButton size="small" type="primary">新增日程</PlatformButton>
  </template>
  <template #center>
    <PlatformSegmented :options="options" value="production" />
  </template>
</PlatformSectionTitle>`,
  },
  {
    api: ['icon: 图标名', 'label: 标题', 'variant: shortcut/app', 'iconColor/iconBackground: 图标配色'],
    category: '数据展示',
    component: EntryCardDemo,
    demoPath: 'apps/demokit/src/demos/entry-card-demo.vue',
    description: '统一快捷入口与应用入口卡片，覆盖纵向图标入口和横向灰底入口两种常见工作台模式。',
    group: 'entry',
    id: 'platform-entry-card',
    name: 'PlatformEntryCard',
    scenarios: ['快捷入口', '应用入口', '系统功能导航'],
    sourcePath: 'packages/platform-ui/src/entry/platform-entry-card.vue',
    title: '入口卡片',
    usageSnippet: `<PlatformEntryCard
  icon="lucide:activity"
  label="运行管理"
  variant="app"
/>`,
  },
  {
    api: ['title: 区块标题', 'description: 标题说明', 'extra: 头部附加操作'],
    category: '布局',
    component: ViewLayoutDemo,
    demoPath: 'apps/demokit/src/demos/view-layout-demo.vue',
    description: '统一模块外框和标题头部，适合作为详情页或配置页的内容容器。',
    group: 'view',
    id: 'platform-section',
    name: 'PlatformSection',
    scenarios: ['详情区块', '配置区块', '数据卡片容器'],
    sourcePath: 'packages/platform-ui/src/view/platform-section.vue',
    title: '内容分区',
    usageSnippet: `<PlatformSection title="基础信息" description="展示项目核心字段">
  ...
</PlatformSection>`,
  },
  {
    api: ['default slot: 页面模块内容堆叠'],
    category: '布局',
    component: ViewLayoutDemo,
    demoPath: 'apps/demokit/src/demos/view-layout-demo.vue',
    description: '统一页面纵向间距，适合把多个平台区块按标准节奏向下堆叠。',
    group: 'view',
    id: 'platform-view-stack',
    name: 'PlatformViewStack',
    scenarios: ['详情页主体', '看板页面', '组合模块页面'],
    sourcePath: 'packages/platform-ui/src/view/platform-view-stack.vue',
    title: '页面堆叠容器',
    usageSnippet: `<PlatformViewStack>
  <PlatformSection title="概览" />
  <PlatformSection title="附件" />
</PlatformViewStack>`,
  },
  {
    api: ['value: 当前值', 'options: 分段选项'],
    category: '导航',
    component: SegmentedDemo,
    demoPath: 'apps/demokit/src/demos/segmented-demo.vue',
    description: '用于列表、看板、日历等同级模式切换。',
    group: 'segmented',
    id: 'platform-segmented',
    name: 'PlatformSegmented',
    scenarios: ['视图切换', '模式切换', '状态切换'],
    sourcePath: 'packages/platform-ui/src/segmented/platform-segmented.vue',
    title: '分段切换',
    usageSnippet: `<PlatformSegmented
  v-model:value="view"
  :options="[{ label: '列表', value: 'list' }, { label: '看板', value: 'board' }]"
/>`,
  },
  {
    api: ['modelValue: 当前视图值', 'options: 带 icon 的切换选项'],
    category: '导航',
    component: ViewLayoutDemo,
    demoPath: 'apps/demokit/src/demos/view-layout-demo.vue',
    description: '适合页面右上角视图切换，统一按钮样式和激活态。',
    group: 'view',
    id: 'platform-view-switch',
    name: 'PlatformViewSwitch',
    scenarios: ['列表/看板切换', '概览/明细切换', '图文视图切换'],
    sourcePath: 'packages/platform-ui/src/view/platform-view-switch.vue',
    title: '视图切换按钮组',
    usageSnippet: `<PlatformViewSwitch
  v-model="currentView"
  :options="[{ label: '看板', value: 'board' }, { label: '列表', value: 'list' }]"
/>`,
  },
  {
    api: ['items: 面包屑节点列表'],
    category: '导航',
    component: ViewLayoutDemo,
    demoPath: 'apps/demokit/src/demos/view-layout-demo.vue',
    description: '统一页面路径层级和图标风格，避免业务页自行拼接面包屑。',
    group: 'view',
    id: 'platform-page-breadcrumb',
    name: 'PlatformPageBreadcrumb',
    scenarios: ['详情页面包屑', '模块入口页', '流程层级导航'],
    sourcePath: 'packages/platform-ui/src/view/platform-page-breadcrumb.vue',
    title: '面包屑',
    usageSnippet: `<PlatformPageBreadcrumb
  :items="[
    { title: '平台组件', path: '/' },
    { title: '布局与标题' },
  ]"
/>`,
  },
  {
    api: ['tabs: 页面页签列表', 'active: 当前激活页签 key', 'close: 关闭页签', 'update:active: 点击跳转页签'],
    category: '导航',
    component: ViewLayoutDemo,
    demoPath: 'apps/demokit/src/demos/view-layout-demo.vue',
    description: '记录已打开页面，支持点击切换和关闭，适合替代传统路径面包屑放在内容区顶部。',
    group: 'view',
    id: 'platform-page-tabs',
    name: 'PlatformPageTabs',
    scenarios: ['页面访问轨迹', '内容区顶部页签导航', '可关闭页面记录'],
    sourcePath: 'packages/platform-ui/src/view/platform-page-tabs.vue',
    title: '页面页签条',
    usageSnippet: `<PlatformPageTabs
  active="/system/user"
  :tabs="tabs"
/>`,
  },
  {
    api: ['title: 页面标题', 'description: 标题说明', 'actions: 右侧主操作', 'tools: 导出、刷新、设置工具'],
    category: '导航',
    component: ViewToolbarDemo,
    demoPath: 'apps/demokit/src/demos/view-toolbar-demo.vue',
    description: '统一列表页、详情页和配置页顶部的标题、说明和操作区。',
    group: 'view',
    id: 'platform-view-toolbar',
    name: 'PlatformViewToolbar',
    scenarios: ['列表页顶部', '详情页顶部', '配置页顶部'],
    sourcePath: 'packages/platform-ui/src/view/platform-view-toolbar.vue',
    title: '页面头部',
    usageSnippet: `<PlatformViewToolbar
  title="项目总览"
  description="集中查看项目进度、风险和关键里程碑。"
  :actions="[{ key: 'create', label: '新建项目', type: 'primary' }]"
/>`,
  },
  {
    api: ['value: 输入值', 'allowClear: 清空按钮', 'placeholder: 提示文案'],
    category: '数据录入',
    component: FieldDemo,
    demoPath: 'apps/demokit/src/demos/field-demo.vue',
    description: '统一输入框宽度和基础样式，适合查询区、编辑表单和抽屉输入场景。',
    group: 'field',
    id: 'platform-input',
    name: 'PlatformInput',
    scenarios: ['关键词输入', '编辑表单', '抽屉内输入'],
    sourcePath: 'packages/platform-ui/src/field/platform-input.vue',
    title: '输入框',
    usageSnippet: `<PlatformInput allow-clear placeholder="请输入项目名称" />`,
  },
  {
    api: ['options: 选项列表', 'width: 宽度', 'minChars: 自动宽度最小字符数'],
    category: '数据录入',
    component: FieldDemo,
    demoPath: 'apps/demokit/src/demos/field-demo.vue',
    description: '统一选择器宽度与下拉图标，适合筛选区和表单下拉项。',
    group: 'field',
    id: 'platform-select',
    name: 'PlatformSelect',
    scenarios: ['状态筛选', '负责人选择', '分类选择'],
    sourcePath: 'packages/platform-ui/src/field/platform-select.vue',
    title: '选择器',
    usageSnippet: `<PlatformSelect
  :options="statusOptions"
  placeholder="请选择状态"
/>`,
  },
  {
    api: ['placeholder: 占位文案', 'value: 日期值'],
    category: '数据录入',
    component: FieldDemo,
    demoPath: 'apps/demokit/src/demos/field-demo.vue',
    description: '统一日期选择器宽度和图标风格，适合计划时间与节点日期输入。',
    group: 'field',
    id: 'platform-date-picker',
    name: 'PlatformDatePicker',
    scenarios: ['计划日期', '节点日期', '生效时间'],
    sourcePath: 'packages/platform-ui/src/field/platform-date-picker.vue',
    title: '日期选择器',
    usageSnippet: `<PlatformDatePicker placeholder="请选择日期" />`,
  },
  {
    api: ['placeholder: 区间占位文案', 'value: 日期区间'],
    category: '数据录入',
    component: FieldDemo,
    demoPath: 'apps/demokit/src/demos/field-demo.vue',
    description: '统一起止日期范围选择器，适合查询区和计划周期录入。',
    group: 'field',
    id: 'platform-range-picker',
    name: 'PlatformRangePicker',
    scenarios: ['时间筛选', '周期设置', '计划排期'],
    sourcePath: 'packages/platform-ui/src/field/platform-range-picker.vue',
    title: '日期范围选择器',
    usageSnippet: `<PlatformRangePicker />`,
  },
  {
    api: ['label: 标签', 'name: 字段名', 'rules: 校验规则'],
    category: '数据录入',
    component: FieldDemo,
    demoPath: 'apps/demokit/src/demos/field-demo.vue',
    description: '统一表单项间距和与平台表单容器的配合方式。',
    group: 'field',
    id: 'platform-form-item',
    name: 'PlatformFormItem',
    scenarios: ['编辑表单字段', '筛选字段', '弹窗字段'],
    sourcePath: 'packages/platform-ui/src/field/platform-form-item.vue',
    title: '表单项',
    usageSnippet: `<PlatformFormItem label="项目名称" name="projectName">
  <PlatformInput placeholder="请输入项目名称" />
</PlatformFormItem>`,
  },
  {
    api: ['layout: 布局模式', 'labelPreset: 标签预设', 'model: 表单对象'],
    category: '数据录入',
    component: FieldDemo,
    demoPath: 'apps/demokit/src/demos/field-demo.vue',
    description: '平台级表单容器，统一字段间距、标签宽度和校验暴露接口。',
    group: 'form',
    id: 'platform-form',
    name: 'PlatformForm',
    scenarios: ['编辑表单', '查看表单', '表单布局统一'],
    sourcePath: 'packages/platform-ui/src/form/platform-form.vue',
    title: '表单容器',
    usageSnippet: `<PlatformForm :model="formState" layout="horizontal" label-preset="inline-compact">
  ...
</PlatformForm>`,
  },
  {
    api: ['继承 PlatformForm，默认 variant=edit'],
    category: '数据录入',
    component: FormDemo,
    demoPath: 'apps/demokit/src/demos/form-demo.vue',
    description: '编辑场景的表单预设，适合新增、编辑和维护类弹窗内容区。',
    group: 'form',
    id: 'platform-edit-form',
    name: 'PlatformEditForm',
    scenarios: ['编辑弹窗', '抽屉表单', '维护表单'],
    sourcePath: 'packages/platform-ui/src/form/platform-edit-form.vue',
    title: '编辑表单',
    usageSnippet: `<PlatformEditForm :model="editState" layout="horizontal" label-preset="inline-compact">
  ...
</PlatformEditForm>`,
  },
  {
    api: ['columns: 列数或 grid 模板', 'actions slot: 操作区'],
    category: '数据录入',
    component: FormDemo,
    demoPath: 'apps/demokit/src/demos/form-demo.vue',
    description: '轻量筛选表单容器，适合顶部搜索条和紧凑型筛选区。',
    group: 'form',
    id: 'platform-search-form',
    name: 'PlatformSearchForm',
    scenarios: ['顶部搜索条', '轻量筛选区', '组合查询栏'],
    sourcePath: 'packages/platform-ui/src/form/platform-search-form.vue',
    title: '搜索表单',
    usageSnippet: `<PlatformSearchForm :model="queryState">
  ...
  <template #actions>
    <PlatformButton scene="toolbar" type="primary">查询</PlatformButton>
  </template>
</PlatformSearchForm>`,
  },
  {
    api: ['columns: 字段列数', 'collapsed/collapsible: 收起控制', 'query/reset 事件'],
    category: '数据录入',
    component: FormDemo,
    demoPath: 'apps/demokit/src/demos/form-demo.vue',
    description: '统一筛选区外框、字段网格和查询/重置/展开收起行为。',
    group: 'form',
    id: 'platform-query-panel',
    name: 'PlatformQueryPanel',
    scenarios: ['列表页筛选区', '报表筛选区', '复杂查询区'],
    sourcePath: 'packages/platform-ui/src/form/platform-query-panel.vue',
    title: '筛选面板',
    usageSnippet: `<PlatformQueryPanel :columns="3" collapsible @query="handleQuery" @reset="handleReset">
  ...
</PlatformQueryPanel>`,
  },
  {
    api: ['title: 标题', 'description: 说明', 'search/status/type: 工具栏筛选项', 'tools: 工具按钮'],
    category: '数据展示',
    component: TableDemo,
    demoPath: 'apps/demokit/src/demos/table-demo.vue',
    description: '统一表格头部的标题、筛选、工具按钮和表格设置入口。',
    group: 'table',
    id: 'platform-table-toolbar',
    name: 'PlatformTableToolbar',
    scenarios: ['列表页工具栏', '表格筛选头部', '数据看板表格头'],
    sourcePath: 'packages/platform-ui/src/table/platform-table-toolbar.vue',
    title: '表格工具栏',
    usageSnippet: `<PlatformTableToolbar
  title="项目清单"
  v-model:search-value="searchValue"
  :status-options="statusOptions"
/>`,
  },
  {
    api: ['columns: 列定义', 'showIndex: 是否显示序号', 'pagination: 分页配置', 'adaptiveHeight: 自适应高度'],
    category: '数据展示',
    component: TableDemo,
    demoPath: 'apps/demokit/src/demos/table-demo.vue',
    description: '统一平台表格序号列、列设置、筛选行为和自适应高度能力。',
    group: 'table',
    id: 'platform-table',
    name: 'PlatformTable',
    scenarios: ['业务列表页', '统计清单', '详情子表格'],
    sourcePath: 'packages/platform-ui/src/table/platform-table.vue',
    title: '表格',
    usageSnippet: `<PlatformTable :columns="columns" :data-source="tableData" row-key="id" />`,
  },
  {
    api: ['treeData: 树数据', 'selectedKeys: 选中节点', 'slots: 自定义节点渲染'],
    category: '数据展示',
    component: TreeDemo,
    demoPath: 'apps/demokit/src/demos/tree-demo.vue',
    description: '统一树结构展开箭头、悬浮态和选中态，适合目录树与组织树。',
    group: 'tree',
    id: 'platform-tree',
    name: 'PlatformTree',
    scenarios: ['目录树', '组织树', '模块树'],
    sourcePath: 'packages/platform-ui/src/tree/platform-tree.vue',
    title: '树结构',
    usageSnippet: `<PlatformTree
  :tree-data="treeData"
  v-model:selected-keys="selectedKeys"
/>`,
  },
  {
    api: ['title: 面板标题', 'showSearch/showRefresh: 头部工具', 'treeData: 树数据'],
    category: '数据展示',
    component: TreeDemo,
    demoPath: 'apps/demokit/src/demos/tree-demo.vue',
    description: '把树搜索、刷新和内容区域收敛到统一容器，适合左侧导航树。',
    group: 'tree',
    id: 'platform-tree-panel',
    name: 'PlatformTreePanel',
    scenarios: ['左侧树导航', '资源目录', '页面模块树'],
    sourcePath: 'packages/platform-ui/src/tree/platform-tree-panel.vue',
    title: '树结构面板',
    usageSnippet: `<PlatformTreePanel
  title="页面导航树"
  show-search
  show-refresh
  :tree-data="treeData"
/>`,
  },
  {
    api: ['title: 指标名称', 'value: 指标值', 'trendText: 趋势说明', 'type: 语义类型'],
    category: '数据展示',
    component: StatCardDemo,
    demoPath: 'apps/demokit/src/demos/stat-card-demo.vue',
    description: '统一 KPI 视觉、趋势信息和图标区域，适合仪表盘和详情页概览。',
    group: 'stat',
    id: 'platform-stat-card',
    name: 'PlatformStatCard',
    scenarios: ['仪表盘 KPI', '列表页顶部概览', '详情页关键状态'],
    sourcePath: 'packages/platform-ui/src/stat/platform-stat-card.vue',
    title: '指标卡片',
    usageSnippet: `<PlatformStatCard
  title="活跃项目"
  value="38"
  trend-text="+6 本周新增"
  trend-type="up"
/>`,
  },
  {
    api: ['title: 任务标题', 'description: 描述', 'progress: 进度', 'tags: 状态标签'],
    category: '数据展示',
    component: TaskCardDemo,
    demoPath: 'apps/demokit/src/demos/task-card-demo.vue',
    description: '用于任务、审批、项目短卡片，统一标题、状态、进度和主操作。',
    group: 'task-card',
    id: 'platform-task-card',
    name: 'PlatformTaskCard',
    scenarios: ['待办任务', '项目卡片', '审批卡片'],
    sourcePath: 'packages/platform-ui/src/task-card/platform-task-card.vue',
    title: '任务卡片',
    usageSnippet: `<PlatformTaskCard
  title="质量缺陷复核"
  description="华东数据治理专项"
  :progress="72"
  progress-label="72%"
/>`,
  },
  {
    api: ['item: 单条消息数据'],
    category: '数据展示',
    component: NoticeListDemo,
    demoPath: 'apps/demokit/src/demos/notice-list-demo.vue',
    description: '单条消息项，适合嵌入抽屉、消息中心和侧边提醒栏。',
    group: 'notice',
    id: 'platform-notice-item',
    name: 'PlatformNoticeItem',
    scenarios: ['单条提醒', '抽屉消息项', '待办卡片里的通知'],
    sourcePath: 'packages/platform-ui/src/notice/platform-notice-item.vue',
    title: '消息项',
    usageSnippet: `<PlatformNoticeItem :item="noticeItem" />`,
  },
  {
    api: ['items: 消息列表数据', 'loading: 加载态', 'emptyText: 空状态文案', 'variant: default/workbench', 'action: 列表项操作'],
    category: '数据展示',
    component: NoticeListDemo,
    demoPath: 'apps/demokit/src/demos/notice-list-demo.vue',
    description: '适合消息中心、待办提醒和通知列表的统一列表容器。',
    group: 'notice',
    id: 'platform-notice-list',
    name: 'PlatformNoticeList',
    scenarios: ['消息中心', '待办提醒', '系统通知'],
    sourcePath: 'packages/platform-ui/src/notice/platform-notice-list.vue',
    title: '消息列表',
    usageSnippet: `<PlatformNoticeList
  :items="noticeItems"
  variant="workbench"
  @action="handleOpen"
/>`,
  },
  {
    api: ['item: 文件数据', 'downloading: 下载态', 'download: 下载事件'],
    category: '数据展示',
    component: FileDemo,
    demoPath: 'apps/demokit/src/demos/file-demo.vue',
    description: '单文件卡片，用于附件区、资料中心和项目成果卡。',
    group: 'file',
    id: 'platform-file-item',
    name: 'PlatformFileItem',
    scenarios: ['详情页附件', '成果文件', '文档卡片'],
    sourcePath: 'packages/platform-ui/src/file/platform-file-item.vue',
    title: '文件项',
    usageSnippet: `<PlatformFileItem :item="fileItem" @download="handleDownload" />`,
  },
  {
    api: ['items: 文件列表', 'columns: 列数', 'downloadingId: 当前下载项'],
    category: '数据展示',
    component: FileDemo,
    demoPath: 'apps/demokit/src/demos/file-demo.vue',
    description: '统一附件和资料卡片列表编排，适合详情页或资源中心。',
    group: 'file',
    id: 'platform-file-list',
    name: 'PlatformFileList',
    scenarios: ['附件列表', '资料中心', '成果归档'],
    sourcePath: 'packages/platform-ui/src/file/platform-file-list.vue',
    title: '文件列表',
    usageSnippet: `<PlatformFileList :items="files" @download="handleDownload" />`,
  },
  {
    api: ['items: 描述项', 'column: 列数', 'bordered: 是否显示边框'],
    category: '数据展示',
    component: ViewLayoutDemo,
    demoPath: 'apps/demokit/src/demos/view-layout-demo.vue',
    description: '统一详情信息展示的标签列和边框样式，适合基础信息区。',
    group: 'view',
    id: 'platform-descriptions',
    name: 'PlatformDescriptions',
    scenarios: ['详情信息', '基础资料', '只读信息块'],
    sourcePath: 'packages/platform-ui/src/view/platform-descriptions.vue',
    title: '描述列表',
    usageSnippet: `<PlatformDescriptions :column="2" :items="descriptionItems" />`,
  },
  {
    api: ['columns: 看板列配置'],
    category: '数据展示',
    component: ViewLayoutDemo,
    demoPath: 'apps/demokit/src/demos/view-layout-demo.vue',
    description: '统一状态分栏看板，用于任务流转、项目阶段和问题池管理。',
    group: 'view',
    id: 'platform-status-board',
    name: 'PlatformStatusBoard',
    scenarios: ['任务看板', '项目阶段看板', '问题状态看板'],
    sourcePath: 'packages/platform-ui/src/view/platform-status-board.vue',
    title: '状态看板',
    usageSnippet: `<PlatformStatusBoard :columns="statusColumns" />`,
  },
  {
    api: ['items: 审批节点数据', 'title: 面板标题'],
    category: '数据展示',
    component: ApprovalProgressDemo,
    demoPath: 'apps/demokit/src/demos/approval-progress-demo.vue',
    description: '统一审批流和流程进度展示，适合详情页右侧或抽屉内流程区。',
    group: 'approval',
    id: 'platform-approval-progress',
    name: 'PlatformApprovalProgress',
    scenarios: ['审批流', '整改流程', '验收流程'],
    sourcePath: 'packages/platform-ui/src/approval/platform-approval-progress.vue',
    title: '审批进度',
    usageSnippet: `<PlatformApprovalProgress :items="approvalItems" />`,
  },
  {
    api: ['option: echarts 配置项', 'title: 标题', 'description: 说明', 'height: 图表高度'],
    category: '数据展示',
    component: ChartDemo,
    demoPath: 'apps/demokit/src/demos/chart-demo.vue',
    description: '当前已封装图表容器，后续柱状图、折线图、饼图、玉环图、玫瑰图等图表类型统一从这里接入。',
    group: 'chart',
    id: 'platform-echarts-panel',
    name: 'PlatformEchartsPanel',
    scenarios: ['柱状图', '折线图', '饼图', '玉环图', '玫瑰图'],
    sourcePath: 'packages/platform-ui/src/chart/platform-echarts-panel.vue',
    title: '图表',
    usageSnippet: `<PlatformEchartsPanel
  title="年度趋势"
  :option="chartOption"
/>`,
  },
  {
    api: ['label: 状态文案', 'status: 语义状态', 'variant: tag/dot'],
    category: '反馈',
    component: StatusTagDemo,
    demoPath: 'apps/demokit/src/demos/status-tag-demo.vue',
    description: '统一业务状态标签，避免页面自行定义颜色和密度。',
    group: 'status',
    id: 'platform-status-tag',
    name: 'PlatformStatusTag',
    scenarios: ['审批状态', '项目状态', '风险等级'],
    sourcePath: 'packages/platform-ui/src/status/platform-status-tag.vue',
    title: '状态标签',
    usageSnippet: `<PlatformStatusTag label="进行中" status="processing" />
<PlatformStatusTag label="已通过" status="success" variant="dot" />`,
  },
  {
    api: ['value: 0-100 进度值', 'showValue: 是否显示百分比', 'color: 自定义颜色'],
    category: '反馈',
    component: ProgressDemo,
    demoPath: 'apps/demokit/src/demos/progress-demo.vue',
    description: '适用于项目完成率、流程进度和容量使用率展示。',
    group: 'progress',
    id: 'platform-progress',
    name: 'PlatformProgress',
    scenarios: ['项目进度', '整改进度', '容量占用'],
    sourcePath: 'packages/platform-ui/src/progress/platform-progress.vue',
    title: '进度条',
    usageSnippet: `<PlatformProgress label="项目进度" :value="72" show-value />`,
  },
  {
    api: ['open: 显示状态', 'title: 标题', 'width: 弹窗宽度'],
    category: '反馈',
    component: OverlayDemo,
    demoPath: 'apps/demokit/src/demos/overlay-demo.vue',
    description: '统一弹窗标题栏、全屏行为和内容间距，适合新增编辑与审批确认。',
    group: 'overlay',
    id: 'platform-modal',
    name: 'PlatformModal',
    scenarios: ['编辑弹窗', '确认弹窗', '审批弹窗'],
    sourcePath: 'packages/platform-ui/src/modal/platform-modal.vue',
    title: '弹窗',
    usageSnippet: `<PlatformModal v-model:open="modalOpen" title="编辑项目计划">
  ...
</PlatformModal>`,
  },
  {
    api: ['open: 显示状态', 'title: 标题', 'width: 抽屉宽度'],
    category: '反馈',
    component: OverlayDemo,
    demoPath: 'apps/demokit/src/demos/overlay-demo.vue',
    description: '统一抽屉标题栏和内容间距，适合右侧详情与配置维护场景。',
    group: 'overlay',
    id: 'platform-drawer',
    name: 'PlatformDrawer',
    scenarios: ['详情抽屉', '配置抽屉', '侧边编辑'],
    sourcePath: 'packages/platform-ui/src/drawer/platform-drawer.vue',
    title: '抽屉',
    usageSnippet: `<PlatformDrawer v-model:open="drawerOpen" title="项目详情抽屉">
  ...
</PlatformDrawer>`,
  },
];

export const componentCategoryOrder = componentCategories.map((item) => item.title);

export const knownPlatformComponentCount = componentRegistry.length;

export const knownPlatformGroups = Array.from(
  new Set(componentRegistry.map((item) => item.group)),
);

export const guideRegistry: GuideEntry[] = [
  {
    component: PlatformLayoutContractPage,
    description: '顶部导航栏、左侧导航栏和内容区页签的公共布局契约。',
    id: 'platform-layout-contract',
    title: '平台布局三大块',
  },
  {
    component: SystemComponentPriorityPage,
    description: '先选页面骨架，再选组件的系统组件优先说明。',
    id: 'system-component-priority',
    title: '系统组件优先',
  },
  {
    component: DesignSystemSpecPage,
    description: '平台视觉 token、结构规则与设计系统约束。',
    id: 'design-system-spec',
    title: '设计系统规范',
  },
  {
    component: SystemBuildGuidePage,
    description: 'DemoKit、平台组件包与业务项目的协作构建路径。',
    id: 'system-build-guide',
    title: '系统构建指南',
  },
];
