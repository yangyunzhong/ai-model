<script setup lang="ts">
import type { PlatformTableColumns } from '@st/platform-ui';

import {
  PlatformSection,
  PlatformTable,
  PlatformViewStack,
} from '@st/platform-ui';

const deliverableColumns: PlatformTableColumns = [
  {
    dataIndex: 'layer',
    key: 'layer',
    title: '层级',
    width: 160,
  },
  {
    dataIndex: 'artifact',
    key: 'artifact',
    title: '主要产物',
    width: 360,
  },
  {
    dataIndex: 'requirement',
    key: 'requirement',
    title: '交付要求',
  },
];

const deliverableRows = [
  {
    artifact: '本页、组件优先、设计系统规范',
    key: 'rule',
    layer: '规则层',
    requirement: '能告诉后续协作者先看哪里、先改哪里、什么不能做。',
  },
  {
    artifact: 'DemoKit 组件示例与业务范例',
    key: 'demo',
    layer: 'Demo 层',
    requirement: '能被快速预览，用来确认 UI 组合是否成立。',
  },
  {
    artifact: 'packages/platform-ui、packages/platform-styles',
    key: 'source',
    layer: '源码层',
    requirement: '平台复用能力统一沉淀，避免相同结构在业务页重复制造。',
  },
  {
    artifact: 'apps/web-antd 页面与接口',
    key: 'business',
    layer: '业务层',
    requirement: '承接真实权限、路由、接口、业务规则与最终联调。',
  },
];
</script>

<template>
  <PlatformViewStack class="guide-page">
    <div class="guide-callout">
      DemoKit 负责“看见什么可用”，<code>@st/platform-ui</code> 负责“真正修改哪里”，
      <code>apps/web-antd</code> 负责“业务怎么消费与验证”。
    </div>

    <PlatformSection title="定位">
      <div class="guide-stack">
        <p>
          这套系统不是单纯的组件画廊，而是“规范 + Demo + 源组件 + 业务验证”的组合体。我们在
          DemoKit 中找入口，在平台组件包中做复用沉淀，在业务项目里确认真实可用。
        </p>
        <p>
          当前技术栈以 Vue 3、Vite、Ant Design Vue、<code>@st/platform-ui</code> 和
          <code>@st/platform-styles</code> 为准。页面搭建优先复用平台组件，不重新发明基础结构。
        </p>
      </div>
    </PlatformSection>

    <PlatformSection title="分层边界">
      <div class="guide-card-grid guide-card-grid--three">
        <article class="guide-card">
          <h3>DemoKit</h3>
          <code>apps/demokit</code>
          <p>承载规则页、组件 Demo、业务页面范例，是 AI 和开发先定位参考的入口。</p>
        </article>
        <article class="guide-card">
          <h3>平台组件包</h3>
          <code>packages/platform-ui</code>
          <p>承载可复用的 Platform 组件。只放平台 UI 能力，不放业务接口、路由和页面状态。</p>
        </article>
        <article class="guide-card">
          <h3>业务消费层</h3>
          <code>apps/web-antd</code>
          <p>承载真实业务页面、接口联动与权限逻辑。页面特殊规则留在业务层，不污染平台包。</p>
        </article>
      </div>
    </PlatformSection>

    <PlatformSection title="默认契约">
      <ul class="guide-bullet-list">
        <li>平台组件优先从 <code>packages/platform-ui/src/index.ts</code> 的 barrel 出口导入，不在业务页绕开统一入口。</li>
        <li>颜色、圆角、阴影、间距优先使用 <code>packages/platform-styles/src/tokens/index.css</code> 中的语义变量，不在页面里硬编码视觉值。</li>
        <li>新增业务页时，先在 DemoKit 找到最接近的页面模板和组件组合，再决定是否需要补平台能力。</li>
        <li>如果一个改动应该让多个页面一起受益，就改平台组件源头；如果只服务单页特殊布局，就留在业务页。</li>
        <li>平台组件 contract 变化后，DemoKit 对应 Demo 和规则页要同步更新，避免“源码已变、文档未跟”的断层。</li>
      </ul>
    </PlatformSection>

    <PlatformSection title="标准构建流程">
      <ol class="guide-ordered-list">
        <li>先从“系统组件优先级”页选页面骨架，确定标题区、筛选区、数据区、状态区分别由谁承载。</li>
        <li>如果页面需要新的共性组合，先在 <code>packages/platform-ui</code> 补组件，再回业务页接入。</li>
        <li>在 <code>apps/demokit</code> 补示例或规则说明，保证后续 AI / 开发能重新找到这套模式。</li>
        <li>在 <code>apps/web-antd</code> 做真实业务接线，补接口、权限、状态与空加载异常处理。</li>
        <li>至少验证一个 Demo 页面和一个真实业务页，确认平台改动没有只在样板里成立。</li>
      </ol>
    </PlatformSection>

    <PlatformSection title="协作交付物">
      <PlatformTable
        :columns="deliverableColumns"
        :column-setting-enabled="false"
        :data-source="deliverableRows"
        :pagination="false"
        :show-index="false"
      />
    </PlatformSection>

    <PlatformSection title="给 AI 的最短指令">
      <pre class="guide-code"><code>先在 DemoKit 找到最接近的页面骨架；
如果需要全局复用，修改 packages/platform-ui；
样式变量走 packages/platform-styles/src/tokens/index.css；
最后补一个 demokit 示例，并验证 apps/web-antd 的真实页面。</code></pre>
    </PlatformSection>
  </PlatformViewStack>
</template>
