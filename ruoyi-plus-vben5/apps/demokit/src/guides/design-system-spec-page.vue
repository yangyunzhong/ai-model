<script setup lang="ts">
import type { PlatformTableColumns } from '@st/platform-ui';

import {
  PlatformSection,
  PlatformTable,
  PlatformViewStack,
} from '@st/platform-ui';

const tokenColumns: PlatformTableColumns = [
  {
    dataIndex: 'token',
    key: 'token',
    title: 'Token',
    width: 360,
  },
  {
    dataIndex: 'usage',
    key: 'usage',
    title: '用途',
    width: 260,
  },
  {
    dataIndex: 'antiPattern',
    key: 'antiPattern',
    title: '禁止做法',
  },
];

const tokenRows = [
  {
    antiPattern: '页面内自行写品牌绿或复制其他项目色值',
    key: 'brand',
    token: '--st-color-brand',
    usage: '主按钮、主强调、选中态',
  },
  {
    antiPattern: '随手挑选浅绿、黄绿或其他近似色',
    key: 'accent',
    token: '--st-color-accent',
    usage: '次要纯色按钮、辅助强调、第二强调层',
  },
  {
    antiPattern: '随手调整透明度来凑层级',
    key: 'text',
    token: '--st-color-text-primary / secondary / tertiary',
    usage: '主文案、说明文案、弱化文案',
  },
  {
    antiPattern: '页面里局部重写一套灰边颜色',
    key: 'border',
    token: '--st-color-border-control / subtle',
    usage: '输入框、卡片、表格边框',
  },
  {
    antiPattern: '用品牌色或 warning 色表达危险动作',
    key: 'danger',
    token: '--st-color-danger',
    usage: '删除、驳回、严重告警',
  },
  {
    antiPattern: '每个页面单独决定 section 间距',
    key: 'spacing',
    token: '--st-layout-section-gap / --st-module-content-padding',
    usage: '区块间距、模块内边距',
  },
  {
    antiPattern: '在表格页面里直接覆盖局部颜色',
    key: 'table',
    token: '--st-table-*',
    usage: '表格工具栏、行 hover、选中态',
  },
] as const;
</script>

<template>
  <PlatformViewStack class="guide-page">
    <div class="guide-callout">
      视觉规则优先落在 <code>@st/platform-styles</code>，结构规则优先落在
      <code>@st/platform-ui</code>，业务页只负责消费，不重新定义设计系统。
    </div>

    <PlatformSection title="主题与视觉结果">
      <ul class="guide-bullet-list">
        <li>页面底色统一来自 <code>--st-color-page-bg</code>，卡片底色统一来自 <code>--st-color-card-bg</code>。</li>
        <li>主品牌色、悬停色、选中底色统一走 <code>--st-color-brand</code>、<code>--st-color-brand-hover</code>、<code>--st-color-fill-selected</code>。</li>
        <li>卡片圆角、控件圆角、阴影统一走 <code>--st-radius-card</code>、<code>--st-radius-control</code>、<code>--st-shadow-card</code>。</li>
        <li>页面装饰、背景效果和组件边框优先在平台样式层控制，不在业务页面再写一套局部视觉体系。</li>
      </ul>
    </PlatformSection>

    <PlatformSection title="语义 Token">
      <PlatformTable
        :columns="tokenColumns"
        :column-setting-enabled="false"
        :data-source="tokenRows"
        :pagination="false"
        :show-index="false"
      />
    </PlatformSection>

    <PlatformSection title="布局骨架">
      <p>
        页面结构先稳定，视觉再覆盖。默认以“头部 + 概览区 + 主数据区 + 次要区块”为主，不直接在空白页面里自由堆卡片。
      </p>
      <pre class="guide-code"><code>&lt;template&gt;
  &lt;div class="page-shell"&gt;
    &lt;PlatformViewToolbar
      title="项目总览"
      description="聚合进度、风险、合同与执行状态"
    /&gt;

    &lt;section class="kpi-grid"&gt;
      &lt;PlatformStatCard title="活跃项目" value="38" /&gt;
      &lt;PlatformStatCard title="待审批" value="12" /&gt;
      &lt;PlatformStatCard title="本周风险" value="4" /&gt;
    &lt;/section&gt;

    &lt;PlatformSection title="项目列表"&gt;
      &lt;PlatformTable /&gt;
    &lt;/PlatformSection&gt;
  &lt;/div&gt;
&lt;/template&gt;</code></pre>
      <ul class="guide-bullet-list">
        <li>标题区只做标题、描述和主操作，不把筛选条件塞进同一行。</li>
        <li>数据区优先放进 <code>PlatformSection</code>，保证标题、说明、工具条和主体有清晰边界。</li>
        <li>图表、列表、详情区块之间使用稳定的 section 间距，不做忽大忽小的局部微调。</li>
      </ul>
    </PlatformSection>

    <PlatformSection title="状态与反馈">
      <div class="guide-card-grid guide-card-grid--two">
        <article class="guide-card">
          <h3>空 / 加载 / 错误</h3>
          <p>必须在数据区内表达，不要只在控制台报错或只显示一个空白容器。</p>
        </article>
        <article class="guide-card">
          <h3>成功 / 警告 / 危险</h3>
          <p>使用 <code>PlatformStatusTag</code>、<code>PlatformProgress</code> 和语义 token，不自定义“红绿灯”体系。</p>
        </article>
      </div>
      <ul class="guide-bullet-list">
        <li>删除、驳回、超时等风险动作统一使用 danger 语义，不使用品牌主色。</li>
        <li>说明性反馈优先放在区块标题下或操作区旁，不在内容卡片里到处插提示文字。</li>
        <li>表格工具、分页、批量操作说明保持在数据区容器内部，不漂浮到页面其他位置。</li>
      </ul>
    </PlatformSection>

    <PlatformSection title="代码约束">
      <ul class="guide-bullet-list">
        <li>新增页面默认先看 DemoKit 规则和业务范例，再写业务代码。</li>
        <li>不在页面里直接复制平台组件内部样式；需要统一改动时回到平台源组件或平台样式。</li>
        <li>不在业务页引入新的视觉体系来绕开现有 token 与状态表达。</li>
        <li>当某个业务模式准备被复用时，先整理为平台组件或规则页，再大面积铺开。</li>
      </ul>
    </PlatformSection>
  </PlatformViewStack>
</template>
