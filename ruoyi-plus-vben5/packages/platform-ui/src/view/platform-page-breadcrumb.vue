<script setup lang="ts">
import type { PlatformBreadcrumbItem } from './types';

import { useRouter } from 'vue-router';

import { Breadcrumb, BreadcrumbItem } from 'antdv-next';

import { PlatformIcon } from '../icon';

withDefaults(
  defineProps<{
    items?: PlatformBreadcrumbItem[];
  }>(),
  {
    items: () => [],
  },
);

const router = useRouter();

function handleNavigate(path?: string) {
  if (!path) {
    return;
  }
  router.push(path);
}
</script>

<template>
  <Breadcrumb class="platform-page-breadcrumb" separator="/">
    <BreadcrumbItem
      v-for="(item, index) in items"
      :key="`${item.title}-${index}`"
    >
      <button
        v-if="item.path && index !== items.length - 1"
        class="platform-page-breadcrumb__link"
        type="button"
        @click="handleNavigate(item.path)"
      >
        <PlatformIcon
          v-if="item.icon"
          class="platform-page-breadcrumb__icon"
          :icon="item.icon"
        />
        <span>{{ item.title }}</span>
      </button>
      <span
        v-else
        class="platform-page-breadcrumb__current"
      >
        <PlatformIcon
          v-if="item.icon"
          class="platform-page-breadcrumb__icon"
          :icon="item.icon"
        />
        <span>{{ item.title }}</span>
      </span>
    </BreadcrumbItem>
  </Breadcrumb>
</template>

<style scoped>
.platform-page-breadcrumb {
  display: flex;
  align-items: center;
  min-height: 24px;
}

.platform-page-breadcrumb__link,
.platform-page-breadcrumb__current {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  line-height: 22px;
}

.platform-page-breadcrumb__link {
  padding: 0;
  border: none;
  background: transparent;
  color: hsl(var(--muted-foreground));
  cursor: pointer;
  transition: color 0.2s ease;
}

.platform-page-breadcrumb__link:hover {
  color: hsl(var(--primary));
}

.platform-page-breadcrumb__current {
  color: hsl(var(--foreground));
  font-weight: 700;
}

.platform-page-breadcrumb__icon {
  font-size: 18px;
  line-height: 1;
}

.platform-page-breadcrumb :deep(.ant-breadcrumb-link) {
  display: inline-flex;
  align-items: center;
}

.platform-page-breadcrumb :deep(.ant-breadcrumb-separator) {
  margin-inline: 12px;
  color: hsl(var(--muted-foreground) / 0.72);
  font-size: 14px;
}
</style>
