<script lang="ts" setup>
import type { TabDefinition } from '@vben/types';

import { computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { PlatformPageTabs } from '@st/platform-ui';
import { useTabs } from '@vben/hooks';
import { $t, useI18n } from '@vben/locales';
import { getTabKey, useTabbarStore } from '@vben/stores';

defineOptions({ name: 'ContentPageTabs' });

const route = useRoute();
const router = useRouter();
const tabbarStore = useTabbarStore();
const { locale } = useI18n();
const { closeTabByKey, unpinTab } = useTabs();

const active = computed(() => getTabKey(route));

function withLocale(tab: TabDefinition): TabDefinition {
  return {
    ...tab,
    meta: {
      ...tab.meta,
      title: tab.meta?.title ? $t(tab.meta.title as string) : tab.meta?.title,
    },
  };
}

const tabs = computed(() => {
  return tabbarStore.getTabs.map((tab: TabDefinition) => withLocale(tab));
});

watch(
  [() => route.fullPath, () => locale.value],
  () => {
    const meta = route.matched?.[route.matched.length - 1]?.meta;
    tabbarStore.addTab({
      ...route,
      meta: meta || route.meta,
    });
  },
  { immediate: true },
);

async function handleClick(key: string) {
  const tab = tabbarStore.getTabByKey(key);
  if (!tab) {
    return;
  }
  await router.push(tab.fullPath || tab.path);
}

async function handleClose(key: string) {
  await closeTabByKey(key);
}

async function handleUnpin(tab: TabDefinition) {
  await unpinTab(tab);
}
</script>

<template>
  <div class="content-page-tabs">
    <PlatformPageTabs
      :active="active"
      :tabs="tabs"
      @close="handleClose"
      @unpin="handleUnpin"
      @update:active="handleClick"
    />
  </div>
</template>

<style scoped>
.content-page-tabs {
  position: relative;
  display: flex;
  flex: 1 1 auto;
  min-width: 0;
  padding: 4px 24px;
  background: hsl(var(--card));
}

.content-page-tabs::after {
  content: '';
  position: absolute;
  right: 0;
  bottom: -1px;
  left: 0;
  height: 1px;
  background: hsl(var(--border) / 72%);
  pointer-events: none;
  z-index: 1;
}
</style>
