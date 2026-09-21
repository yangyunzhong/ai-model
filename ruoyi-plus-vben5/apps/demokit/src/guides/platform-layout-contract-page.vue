<script setup lang="ts">
import type { PlatformTableColumns } from '@st/platform-ui';

import {
  PlatformSection,
  PlatformTable,
  PlatformViewStack,
  PlatformViewToolbar,
} from '@st/platform-ui';

const layoutColumns: PlatformTableColumns = [
  {
    dataIndex: 'region',
    key: 'region',
    title: '公共区域',
    width: 180,
  },
  {
    dataIndex: 'source',
    key: 'source',
    title: '源码落点',
    width: 360,
  },
  {
    dataIndex: 'contract',
    key: 'contract',
    title: '契约',
  },
];

const layoutRows = [
  {
    contract: '单独占一整行，左侧固定展示 logo 和系统名称，不承载一级菜单。',
    key: 'header',
    region: '顶部导航栏',
    source: 'packages/effects/layouts/src/basic/layout.vue',
  },
  {
    contract: '默认收起，展示一级菜单图标和最多两行文字；品牌绿背景，hover/选中使用 #0B0C0D 26% 透明度。',
    key: 'sidebar',
    region: '左侧导航栏',
    source: 'packages/@core/ui-kit/menu-ui/src/components/menu.vue',
  },
  {
    contract: '位于内容区顶部，记录已打开页面；支持点击跳转和关闭，不再用传统路径面包屑替代。',
    key: 'tabs',
    region: '内容区页签',
    source: 'packages/platform-ui/src/view/platform-page-tabs.vue',
  },
];
</script>

<template>
  <PlatformViewStack class="guide-page">
    <PlatformViewToolbar
      description="顶部导航栏、左侧导航栏、内容区页签是每个业务页面都会出现的平台布局能力。后续页面开发只消费这套布局，不在单页里重写。"
      title="平台布局三大块"
    />

    <div class="guide-callout">
      当前固定为：顶部栏单独一整行；左侧 rail 和右侧内容区从顶部栏下方开始；内容区顶部使用可关闭页签记录已打开页面。
    </div>

    <PlatformSection title="布局预览">
      <div class="layout-contract-preview">
        <header class="layout-contract-preview__header">
          <span class="layout-contract-preview__logo">G</span>
          <strong>项目管理系统</strong>
        </header>
        <div class="layout-contract-preview__body">
          <aside class="layout-contract-preview__rail">
            <span class="is-active">项目全景<br />管理</span>
            <span>人员全生<br />命周期</span>
            <span>智能考勤<br />管理</span>
          </aside>
          <main class="layout-contract-preview__content">
            <div class="layout-contract-preview__tabs">
              <span class="is-active">项目信息管理 x</span>
              <span>进度可视化跟踪</span>
            </div>
            <div class="layout-contract-preview__page">
              <strong>右侧业务内容区</strong>
              <p>业务页面只负责自己的内容，不重写顶部栏、左侧栏和页签条。</p>
            </div>
          </main>
        </div>
      </div>
    </PlatformSection>

    <PlatformSection title="源码落点与契约">
      <PlatformTable
        :columns="layoutColumns"
        :column-setting-enabled="false"
        :data-source="layoutRows"
        :pagination="false"
        :show-index="false"
      />
    </PlatformSection>

    <PlatformSection title="开发规则">
      <ul class="guide-bullet-list">
        <li>顶部导航栏只放 logo、系统名称和右侧工具，不再放一级菜单。</li>
        <li>左侧导航栏使用 <code>navigation.styleType = 'platform-rail'</code>，不要在业务页面覆盖菜单颜色、圆角、hover 或浮窗样式。</li>
        <li>内容区页签由布局层 <code>ContentPageTabs</code> 接入路由和 tabbar store，展示组件 <code>PlatformPageTabs</code> 只负责 UI 和事件。</li>
        <li>新增页面默认出现在这三块布局之下；缺少页签、左侧菜单或系统标题时，优先检查布局配置和平台组件，不在页面里临时补。</li>
      </ul>
    </PlatformSection>
  </PlatformViewStack>
</template>

<style scoped>
.layout-contract-preview {
  overflow: hidden;
  border: 1px solid hsl(var(--st-color-border-subtle));
  border-radius: 6px;
  background: hsl(var(--st-color-page-bg));
}

.layout-contract-preview__header {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 48px;
  padding: 0 18px;
  color: hsl(var(--st-color-text-primary));
  background: hsl(var(--card));
  border-bottom: 1px solid hsl(var(--st-color-border-subtle));
}

.layout-contract-preview__logo {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  color: hsl(0 0% 100%);
  font-weight: 700;
  background: hsl(var(--st-color-brand));
  border-radius: 4px;
}

.layout-contract-preview__body {
  display: grid;
  grid-template-columns: 88px minmax(0, 1fr);
  min-height: 240px;
}

.layout-contract-preview__rail {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: center;
  padding: 12px 8px;
  color: hsl(0 0% 100%);
  background: hsl(var(--st-color-brand));
}

.layout-contract-preview__rail span {
  width: 72px;
  padding: 8px 4px;
  line-height: 1.25;
  text-align: center;
  border-radius: 4px;
}

.layout-contract-preview__rail .is-active {
  background: hsl(0 0% 5% / 26%);
}

.layout-contract-preview__content {
  min-width: 0;
}

.layout-contract-preview__tabs {
  display: flex;
  align-items: center;
  gap: 14px;
  height: 44px;
  padding: 4px 24px;
  background: hsl(var(--card));
}

.layout-contract-preview__tabs span {
  color: hsl(var(--st-color-text-secondary));
}

.layout-contract-preview__tabs .is-active {
  padding: 8px 12px;
  color: hsl(var(--st-color-brand));
  font-weight: 700;
  background: hsl(var(--st-color-fill-selected));
  border-radius: 3px 3px 0 0;
}

.layout-contract-preview__page {
  padding: 28px;
}

.layout-contract-preview__page strong {
  font-size: 20px;
}

.layout-contract-preview__page p {
  margin-top: 8px;
  color: hsl(var(--st-color-text-secondary));
}
</style>
