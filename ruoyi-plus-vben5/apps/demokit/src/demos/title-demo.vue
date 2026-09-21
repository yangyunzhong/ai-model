<script setup lang="ts">
import {
  PlatformButton,
  PlatformModalTitle,
  PlatformPageTitle,
  PlatformSectionAction,
  PlatformSegmented,
  PlatformSectionTitle,
  PlatformSubpageTitle,
} from '@st/platform-ui';

defineProps<{
  demoId?: string;
}>();

const titleComponents = [
  'PlatformPageTitle',
  'PlatformSubpageTitle',
  'PlatformSectionTitle',
  'PlatformModalTitle',
] as const;

const segmentOptions = [
  { label: '生产', value: 'production' },
  { label: '安全', value: 'safety' },
] as const;
</script>

<template>
  <div class="demo-flow">
    <div class="demo-example-list">
      <div
        v-for="item in titleComponents"
        :key="item"
        class="demo-surface demo-flow"
      >
        <PlatformPageTitle
          v-if="item === 'PlatformPageTitle'"
          title="项目全景总览"
          description="用于页面顶部，承接页面定位、摘要说明和信息总览。"
        />

        <PlatformSubpageTitle
          v-else-if="item === 'PlatformSubpageTitle'"
          divider
          title="基本信息"
          description="用于二级页面标题，适合详情页、分步页和内容主区入口。"
        >
          <template #extra>
            <span class="demo-muted">右侧附加说明</span>
          </template>
        </PlatformSubpageTitle>

        <PlatformSectionTitle
          v-else-if="item === 'PlatformSectionTitle'"
          divider
          icon="lucide:badge-check"
          title="项目甘特图"
          description="用于白卡片内容区的模块标题和附加操作。"
        >
          <template #title-extra>
            <PlatformButton size="small" type="primary">新增日程</PlatformButton>
          </template>
          <template #center>
            <PlatformSegmented :options="segmentOptions" value="production" />
          </template>
          <template #extra>
            <PlatformSectionAction kind="more" label="更多" />
          </template>
        </PlatformSectionTitle>

        <PlatformModalTitle
          v-else
          title="编辑项目计划"
        />
      </div>
    </div>
  </div>
</template>
