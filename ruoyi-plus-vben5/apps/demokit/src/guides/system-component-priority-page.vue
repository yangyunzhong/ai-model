<script setup lang="ts">
import type { PlatformTableColumns } from '@st/platform-ui';

import {
  PlatformSection,
  PlatformTable,
  PlatformViewStack,
} from '@st/platform-ui';

const selectionColumns: PlatformTableColumns = [
  {
    dataIndex: 'scene',
    key: 'scene',
    title: '场景',
    width: 280,
  },
  {
    dataIndex: 'component',
    key: 'component',
    title: '优先组件',
    width: 520,
  },
  {
    dataIndex: 'description',
    key: 'description',
    title: '说明',
  },
];

const selectionRows = [
  {
    component: 'PlatformViewToolbar',
    description: '统一页面头部结构，避免每页自己拼标题栏。',
    key: 'view-toolbar',
    scene: '页面标题、说明、右侧主操作',
  },
  {
    component: 'ContentPageTabs + PlatformPageTabs',
    description: '内容区顶部记录已打开页面，支持点击切换和关闭，不在业务页手写 tab。',
    key: 'page-tabs',
    scene: '已打开页面记录',
  },
  {
    component: 'PlatformPageBreadcrumb',
    description: '仅用于静态层级定位，不替代内容区已打开页面页签。',
    key: 'breadcrumb',
    scene: '静态面包屑 / 层级导航',
  },
  {
    component: 'PlatformStatCard',
    description: '用于单个关键指标，不再额外手写卡片壳。',
    key: 'stat-card',
    scene: 'KPI、摘要指标、趋势',
  },
  {
    component: 'PlatformStatusBoard',
    description: '适合详情页或看板页顶部的关键状态面板。',
    key: 'status-board',
    scene: '状态总览区',
  },
  {
    component: 'PlatformSearchForm / PlatformQueryPanel',
    description: '简单查询用表单，复杂筛选用面板，操作按钮保持在统一尾部。',
    key: 'query',
    scene: '查询条件区',
  },
  {
    component: 'PlatformSection + PlatformTable + PlatformTableToolbar',
    description: '数据区要有标题、说明和工具栏，不裸放表格。',
    key: 'table',
    scene: '列表区与表格工具条',
  },
  {
    component: 'PlatformDescriptions',
    description: '对象基础信息统一用描述型布局，不手写多列 label/value。',
    key: 'descriptions',
    scene: '详情基础字段',
  },
  {
    component: 'PlatformEditForm / PlatformForm',
    description: '先用平台表单骨架，再按字段类型补 field 组件。',
    key: 'form',
    scene: '表单录入',
  },
  {
    component: 'PlatformInput / PlatformSelect / PlatformDatePicker',
    description: '字段密度、圆角与禁用态保持一致。',
    key: 'field',
    scene: '字段级输入',
  },
  {
    component: 'PlatformFileList / PlatformFileItem',
    description: '附件类内容统一展示，不让业务页重复做图标和元信息排版。',
    key: 'file',
    scene: '文件与附件清单',
  },
  {
    component: 'PlatformTaskCard / PlatformNoticeList',
    description: '适合工作台和消息中心的短内容块。',
    key: 'task',
    scene: '任务、待办、通知',
  },
  {
    component: 'PlatformEchartsPanel',
    description: '图表统一承载标题、说明和高度，避免图表实例直接裸露在页面中。',
    key: 'chart',
    scene: '图表容器',
  },
  {
    component: 'PlatformStatusTag / PlatformProgress',
    description: '语义状态和进度密度统一，不自己配色。',
    key: 'status',
    scene: '状态标签与进度',
  },
  {
    component: 'PlatformSegmented / PlatformViewSwitch',
    description: '列表、看板、时间轴等同级视图切换优先走统一入口。',
    key: 'switch',
    scene: '视图切换',
  },
];

const pagePatterns = [
  {
    description: '适合查询、筛选、批量操作、导出与分页说明并存的页面。',
    flow: 'PlatformViewToolbar → PlatformSearchForm / PlatformQueryPanel → PlatformSection + PlatformTable',
    title: '列表页',
  },
  {
    description: '顶部先给概览，再做 2/1 或 1/1/1 的内容分栏，图表标题和工具统一留在容器内。',
    flow: 'PlatformViewToolbar → PlatformStatCard → 图表区 / 任务区 / 通知区',
    title: '仪表盘',
  },
  {
    description: '基础信息、关联记录、审批进度分区展示，避免把所有字段塞进一张大卡片。',
    flow: 'PlatformViewToolbar → PlatformStatusBoard → PlatformDescriptions → PlatformSection',
    title: '详情页',
  },
  {
    description: '分组字段、校验提示和提交动作保持在固定位置，不用业务页临时排版。',
    flow: 'PlatformViewToolbar → PlatformEditForm / PlatformForm → 底部提交区',
    title: '表单页',
  },
];
</script>

<template>
  <PlatformViewStack class="guide-page">
    <div class="guide-callout">
      默认遵循“先平台复合组件，后基础组件；先补 kit，再写业务一次性结构”。
    </div>

    <PlatformSection title="默认规则">
      <ul class="guide-bullet-list">
        <li>优先从 <code>@st/platform-ui</code> 导入平台组件，不复制 DemoKit 内部示例代码。</li>
        <li>先选页面骨架，再选字段和状态组件；不要从按钮、栅格开始反推整页结构。</li>
        <li>同一模式在两个以上页面重复出现时，优先补平台组件，而不是让业务层长期复制。</li>
        <li>图表、列表、详情、表单都应有稳定容器，不把说明文字和操作按钮散落到页面各处。</li>
        <li>如果页面只需要轻量差异，使用 props 扩展；如果需要结构性差异，再申请新平台组件。</li>
      </ul>
    </PlatformSection>

    <PlatformSection title="组件选型表">
      <PlatformTable
        :columns="selectionColumns"
        :column-setting-enabled="false"
        :data-source="selectionRows"
        :pagination="false"
        :show-index="false"
      />
    </PlatformSection>

    <PlatformSection title="常见页面模板">
      <div class="guide-card-grid guide-card-grid--two">
        <article v-for="pattern in pagePatterns" :key="pattern.title" class="guide-card">
          <h3>{{ pattern.title }}</h3>
          <p><code>{{ pattern.flow }}</code></p>
          <p>{{ pattern.description }}</p>
        </article>
      </div>
    </PlatformSection>

    <PlatformSection title="缺少模式时怎么做">
      <ol class="guide-ordered-list">
        <li>先搜索 DemoKit 和 <code>packages/platform-ui/src</code>，确认不是已有能力未被发现。</li>
        <li>确认是共性模式后，在平台包中补组件或补 props，不在业务页落一份临时版本。</li>
        <li>同步补 DemoKit 示例或规则说明，让下一次协作能直接复用这次沉淀。</li>
      </ol>
    </PlatformSection>
  </PlatformViewStack>
</template>
