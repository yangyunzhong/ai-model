<script setup lang="ts">
import type { SelectProps } from 'antdv-next';

import { computed, ref, useSlots } from 'vue';

import { VbenIcon } from '@vben/icons';

import { PlatformButton } from '../button';
import { PlatformInput, PlatformSelect } from '../field';
import { PlatformIcon } from '../icon';

type TableTool =
  | 'add'
  | 'export'
  | 'fullscreen'
  | 'refresh'
  | 'search'
  | 'setting';

const props = withDefaults(
  defineProps<{
    bleed?: boolean;
    bleedOffset?: number | string;
    description?: string;
    searchPlaceholder?: string;
    searchValue?: string;
    searchWidth?: number | string;
    statusOptions?: SelectProps['options'];
    statusValue?: string;
    statusWidth?: number | string;
    title?: string;
    tools?: TableTool[];
    typeOptions?: SelectProps['options'];
    typeValue?: string;
    typeWidth?: number | string;
  }>(),
  {
    bleed: true,
    bleedOffset: 'var(--st-module-content-padding)',
    description: '',
    searchPlaceholder: '搜索关键词',
    searchValue: undefined,
    searchWidth: undefined,
    statusOptions: () => [],
    statusValue: '',
    statusWidth: undefined,
    title: '',
    tools: () => ['search', 'refresh', 'setting', 'fullscreen'],
    typeOptions: () => [],
    typeValue: '',
    typeWidth: undefined,
  },
);

const emit = defineEmits<{
  add: [];
  export: [];
  fullscreen: [];
  refresh: [];
  search: [];
  setting: [event: MouseEvent];
  'update:searchValue': [value: string];
  'update:statusValue': [value: string];
  'update:typeValue': [value: string];
}>();

const slots = useSlots();
const toolbarRef = ref<HTMLElement>();
const searchExpanded = ref(false);

const toolMeta = {
  add: {
    icon: 'icon-xinzeng',
    label: '新增',
  },
  export: {
    icon: 'icon-xiazai1',
    label: '导出',
  },
  fullscreen: {
    icon: 'lucide:maximize-2',
    label: '全屏表格',
  },
  refresh: {
    icon: 'lucide:refresh-cw',
    label: '刷新表格',
  },
  search: {
    icon: 'lucide:search',
    label: '搜索表格',
  },
  setting: {
    icon: 'lucide:settings',
    label: '表格设置',
  },
} as const;

const effectiveTools = computed<TableTool[]>(() => {
  if (props.searchValue === undefined || props.tools.includes('search')) {
    return props.tools;
  }

  return ['search', ...props.tools];
});
const toolbarTools = computed(() =>
  effectiveTools.value.map((tool) => ({
    key: tool,
    ...toolMeta[tool],
  })),
);
const hasMeta = computed(
  () =>
    Boolean(props.title || props.description || slots.title || slots.description),
);
const hasFilters = computed(() =>
  Boolean(
    slots.filters ||
      props.statusOptions?.length ||
      props.typeOptions?.length,
  ),
);
const hasMain = computed(() => hasMeta.value || hasFilters.value);
const showSearchInput = computed(
  () => props.searchValue !== undefined && searchExpanded.value,
);
const searchModel = computed({
  get: () => props.searchValue ?? '',
  set: (value) => emit('update:searchValue', value),
});
const statusModel = computed({
  get: () => props.statusValue ?? '',
  set: (value) => emit('update:statusValue', String(value ?? '')),
});
const typeModel = computed({
  get: () => props.typeValue ?? '',
  set: (value) => emit('update:typeValue', String(value ?? '')),
});
const toolbarStyle = computed(() => ({
  '--platform-table-toolbar-bleed': normalizeWidth(
    props.bleedOffset,
    'var(--st-module-content-padding)',
  ),
  '--platform-table-toolbar-search-width': normalizeWidth(
    props.searchWidth,
    '280px',
  ),
}));

function handleToolClick(tool: TableTool, event: MouseEvent) {
  switch (tool) {
    case 'add': {
      emit('add');
      break;
    }
    case 'export': {
      emit('export');
      break;
    }
    case 'fullscreen': {
      emit('fullscreen');
      break;
    }
    case 'refresh': {
      const refreshHandled = dispatchRefreshRequest();

      if (refreshHandled) {
        break;
      }

      emit('refresh');
      break;
    }
    case 'search': {
      if (props.searchValue === undefined) {
        emit('search');
        break;
      }
      searchExpanded.value = !searchExpanded.value;
      break;
    }
    case 'setting': {
      const settingHandled = dispatchColumnSettingRequest(event);

      if (settingHandled) {
        break;
      }

      emit('setting', event);
      break;
    }
  }
}

function normalizeWidth(width: number | string | undefined, fallback: string) {
  if (typeof width === 'number') {
    return `${width}px`;
  }
  return width || fallback;
}

function dispatchRefreshRequest() {
  if (typeof window === 'undefined') {
    return false;
  }

  const container = toolbarRef.value?.parentElement;

  if (!container) {
    return false;
  }

  const refreshEvent = new CustomEvent('platform-table:refresh-request', {
    bubbles: false,
    cancelable: true,
  });

  container.dispatchEvent(refreshEvent);

  return refreshEvent.defaultPrevented;
}

function dispatchColumnSettingRequest(anchor: MouseEvent) {
  if (typeof window === 'undefined') {
    return false;
  }

  const container = toolbarRef.value?.parentElement;

  if (!container) {
    return false;
  }

  const settingEvent = new CustomEvent('platform-table:column-setting-request', {
    bubbles: false,
    cancelable: true,
    detail: {
      anchor,
    },
  });

  container.dispatchEvent(settingEvent);

  return settingEvent.defaultPrevented;
}
</script>

<template>
  <div
    ref="toolbarRef"
    class="platform-table-toolbar"
    :class="{ 'platform-table-toolbar--bleed': bleed }"
    :style="toolbarStyle"
  >
    <div v-if="$slots.actions" class="platform-table-toolbar__actions">
      <slot name="actions"></slot>
    </div>

    <div v-if="hasMain" class="platform-table-toolbar__main">
      <div v-if="hasMeta" class="platform-table-toolbar__meta">
        <slot name="title">
          <h2 v-if="title" class="platform-table-toolbar__title">
            {{ title }}
          </h2>
        </slot>
        <slot name="description">
          <p v-if="description" class="platform-table-toolbar__description">
            {{ description }}
          </p>
        </slot>
      </div>

      <div v-if="hasFilters" class="platform-table-toolbar__filters">
        <slot name="filters">
          <PlatformSelect
            v-if="statusOptions?.length"
            v-model:value="statusModel"
            :options="statusOptions"
            :width="statusWidth || 96"
            @change="emit('search')"
          />
          <PlatformSelect
            v-if="typeOptions?.length"
            v-model:value="typeModel"
            :options="typeOptions"
            :width="typeWidth || 132"
            @change="emit('search')"
          />
        </slot>
      </div>
    </div>

    <div class="platform-table-toolbar__tools">
      <slot name="tools-before"></slot>
      <div v-if="showSearchInput" class="platform-table-toolbar__search">
        <PlatformInput
          v-model:value="searchModel"
          allow-clear
          :placeholder="searchPlaceholder"
          @change="emit('search')"
          @press-enter="emit('search')"
        >
          <template #prefix>
            <VbenIcon icon="lucide:search" />
          </template>
        </PlatformInput>
      </div>
      <PlatformButton
        v-for="tool in toolbarTools"
        :key="tool.key"
        :aria-label="tool.label"
        :class="{
          'platform-table-toolbar__tool--active':
            tool.key === 'search' && showSearchInput,
        }"
        :data-tool="tool.key"
        scene="toolbar"
        shape="circle"
        @click="handleToolClick(tool.key, $event)"
      >
        <template #icon>
          <PlatformIcon :icon="tool.icon" />
        </template>
      </PlatformButton>
      <slot name="tools-after"></slot>
    </div>
  </div>
</template>

<style scoped>
.platform-table-toolbar {
  display: flex;
  width: 100%;
  box-sizing: border-box;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  min-height: 56px;
  padding: 12px var(--st-module-content-padding);
  margin-bottom: var(--st-layout-section-gap);
  background: hsl(var(--st-color-table-toolbar-bg));
  border-radius: var(--st-radius-card);
}

.platform-table-toolbar--bleed {
  width: calc(
    100% +
      (var(--platform-table-toolbar-bleed, var(--st-module-content-padding)) * 2)
  );
  margin: calc(
      var(--platform-table-toolbar-bleed, var(--st-module-content-padding)) *
        -1
    )
    calc(
      var(--platform-table-toolbar-bleed, var(--st-module-content-padding)) *
        -1
    )
    var(--st-layout-section-gap);
  border-radius: var(--st-radius-card) var(--st-radius-card) 0 0;
}

.platform-table-toolbar__main {
  display: flex;
  flex: 1;
  align-items: center;
  gap: 16px;
  min-width: 0;
}

.platform-table-toolbar__meta {
  flex: none;
  min-width: 0;
}

.platform-table-toolbar__title {
  margin: 0;
  color: hsl(var(--foreground));
  font-size: var(--st-font-size-lg);
  font-weight: 700;
  line-height: var(--st-control-height);
}

.platform-table-toolbar__description {
  margin: 2px 0 0;
  color: hsl(var(--muted-foreground));
  font-size: var(--st-font-size-sm);
  line-height: var(--st-line-height-base);
}

.platform-table-toolbar__filters {
  display: inline-flex;
  flex: 1;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
  min-width: 0;
}

.platform-table-toolbar__search {
  width: var(--platform-table-toolbar-search-width, 280px);
}

.platform-table-toolbar__search :deep(.platform-input) {
  height: var(--st-control-height);
}

.platform-table-toolbar__actions,
.platform-table-toolbar__tools {
  display: inline-flex;
  align-items: center;
  gap: 12px;
}

.platform-table-toolbar__actions {
  min-width: 0;
  flex-wrap: wrap;
}

.platform-table-toolbar__actions :slotted(.ant-btn-primary:not(.ant-btn-dangerous)) {
  order: -20;
}

.platform-table-toolbar__actions :slotted(.ant-btn-dangerous) {
  order: -10;
}

.platform-table-toolbar__tools {
  flex-shrink: 0;
}

.platform-table-toolbar__tool--active {
  color: hsl(var(--st-color-table-tool-hover-icon));
  background: hsl(var(--st-color-table-tool-hover-bg));
  border-color: hsl(var(--st-color-table-tool-hover-border));
}

@media (max-width: 960px) {
  .platform-table-toolbar,
  .platform-table-toolbar__main,
  .platform-table-toolbar__filters {
    align-items: stretch;
    flex-direction: column;
  }

  .platform-table-toolbar__actions,
  .platform-table-toolbar__tools {
    justify-content: flex-start;
  }

  .platform-table-toolbar__search {
    width: 100%;
  }
}
</style>
