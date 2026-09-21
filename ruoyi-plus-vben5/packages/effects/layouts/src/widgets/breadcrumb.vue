<script lang="ts" setup>
import type { BreadcrumbStyleType } from '@vben/types';
import type { IBreadcrumb } from '@vben-core/shadcn-ui';

import { VbenBreadcrumbView } from '@vben-core/shadcn-ui';
import { computed } from 'vue';
import { useRouter } from 'vue-router';

import { usePlatformBreadcrumbItems } from './use-platform-breadcrumb-items';

interface Props {
  hideWhenOnlyOne?: boolean;
  showHome?: boolean;
  showIcon?: boolean;
  type?: BreadcrumbStyleType;
}

const props = withDefaults(defineProps<Props>(), {
  showHome: false,
  showIcon: false,
  type: 'normal',
});

const router = useRouter();
const { platformBreadcrumbItems } = usePlatformBreadcrumbItems();

const breadcrumbs = computed((): IBreadcrumb[] => {
  const resultBreadcrumb: IBreadcrumb[] = [];

  for (const item of platformBreadcrumbItems.value) {
    resultBreadcrumb.push({
      icon: item.icon,
      path: item.path || '',
      title: item.title,
    });
  }
  if (props.showHome) {
    resultBreadcrumb.unshift({
      icon: 'mdi:home-outline',
      isHome: true,
      path: '/',
    });
  }
  if (props.hideWhenOnlyOne && resultBreadcrumb.length === 1) {
    return [];
  }

  return resultBreadcrumb;
});

function handleSelect(path: string) {
  router.push(path);
}
</script>
<template>
  <VbenBreadcrumbView
    :breadcrumbs="breadcrumbs"
    :show-icon="showIcon"
    :style-type="type"
    class="ml-2"
    @select="handleSelect"
  />
</template>
