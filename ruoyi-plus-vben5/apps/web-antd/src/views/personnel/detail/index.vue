<script setup lang="ts">
import { Page } from '@vben/common-ui';

import { PlatformPageBreadcrumb, PlatformSubpageTitle } from '@st/platform-ui';

const breadcrumbItems = [
  {
    icon: 'icon-renyuanzonglan',
    path: '/personnel/overview',
    title: '人员总览',
  },
  {
    title: '人员详情',
  },
];
</script>

<template>
  <Page :auto-content-height="true">
    <div class="personnel-detail-page">
      <PlatformPageBreadcrumb :items="breadcrumbItems" />

      <section class="platform-surface personnel-detail-page__content">
        <PlatformSubpageTitle
          divider
          padding="0 0 14px"
          title="基本信息"
        />
      </section>
    </div>
  </Page>
</template>

<style scoped>
.personnel-detail-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 100%;
}

.personnel-detail-page__content {
  flex: 1;
  min-height: 360px;
}
</style>
