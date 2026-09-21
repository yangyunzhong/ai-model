import type { PlatformBreadcrumbItem } from '@st/platform-ui';

import { computed } from 'vue';
import { useRoute } from 'vue-router';

import { $t } from '@vben/locales';

function usePlatformBreadcrumbItems() {
  const route = useRoute();

  const items = computed((): PlatformBreadcrumbItem[] => {
    const result: PlatformBreadcrumbItem[] = [];

    for (const match of route.matched) {
      const { meta, path } = match;
      const { hideChildrenInMenu, hideInBreadcrumb, icon, name, title } =
        meta || {};

      if (hideInBreadcrumb || hideChildrenInMenu || !path) {
        continue;
      }

      const itemTitle = title ? $t((title || name) as string) : '';
      if (!itemTitle) {
        continue;
      }

      result.push({
        icon: icon as string | undefined,
        path,
        title: itemTitle,
      });
    }

    return result.map((item, index) => ({
      ...item,
      path: index === result.length - 1 ? undefined : item.path,
    }));
  });

  return {
    platformBreadcrumbItems: items,
  };
}

export { usePlatformBreadcrumbItems };
