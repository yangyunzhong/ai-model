<script setup lang="ts">
import type { PropType, VNode } from 'vue';

import { Comment, computed, defineComponent, Fragment, ref, Text, useSlots, watch } from 'vue';

import { PlatformButton } from '../button';

defineOptions({
  inheritAttrs: false,
});

const props = withDefaults(
  defineProps<{
    collapsed?: boolean;
    collapsedRows?: number;
    collapsible?: boolean;
    columns?: number;
    defaultCollapsed?: boolean;
    labelWidth?: number;
    queryLoading?: boolean;
    queryText?: string;
    resetText?: string;
    showQuery?: boolean;
    showReset?: boolean;
  }>(),
  {
    collapsed: undefined,
    collapsedRows: 1,
    collapsible: false,
    columns: 4,
    defaultCollapsed: true,
    labelWidth: 96,
    queryLoading: false,
    queryText: '查询',
    resetText: '重置',
    showQuery: true,
    showReset: true,
  },
);

const emit = defineEmits<{
  query: [];
  reset: [];
  search: [];
  'update:collapsed': [value: boolean];
}>();
const RenderVNode = defineComponent({
  name: 'PlatformQueryPanelRenderVNode',
  props: {
    node: {
      required: true,
      type: Object as PropType<VNode>,
    },
  },
  render() {
    return this.node;
  },
});

const slots = useSlots();
const innerCollapsed = ref(props.defaultCollapsed);

const fieldNodes = computed(() => flattenNodes(slots.default?.() ?? []));
const columnCount = computed(() => Math.max(1, props.columns));
const collapsedFieldCount = computed(() =>
  Math.max(1, columnCount.value * Math.max(1, props.collapsedRows) - 1),
);
const canCollapse = computed(
  () =>
    props.collapsible && fieldNodes.value.length > collapsedFieldCount.value,
);
const collapsedState = computed({
  get: () => props.collapsed ?? innerCollapsed.value,
  set: (value: boolean) => {
    if (props.collapsed === undefined) {
      innerCollapsed.value = value;
    }
    emit('update:collapsed', value);
  },
});
const visibleFieldNodes = computed(() => {
  if (!canCollapse.value || !collapsedState.value) {
    return fieldNodes.value;
  }

  return fieldNodes.value.slice(0, collapsedFieldCount.value);
});
const panelStyle = computed(() => ({
  '--platform-query-panel-label-width': `${props.labelWidth}px`,
  '--platform-query-panel-columns': `repeat(${columnCount.value}, minmax(0, 1fr))`,
}));
const hasActions = computed(
  () =>
    props.showQuery ||
    props.showReset ||
    Boolean(slots.actions) ||
    canCollapse.value,
);
const collapseLabel = computed(() =>
  collapsedState.value ? '展开' : '收起',
);
const collapseIconClass = computed(() =>
  collapsedState.value
    ? 'platform-query-panel__collapse-icon iconfont icon-jiantouxia'
    : 'platform-query-panel__collapse-icon iconfont icon-jiantouxia platform-query-panel__collapse-icon--expanded',
);
const actionsStyle = computed(() => {
  const columns = columnCount.value;

  if (collapsedState.value) {
    return {
      gridColumn: `${columns} / span 1`,
    };
  }

  const remainder = visibleFieldNodes.value.length % columns;

  if (remainder === 0) {
    return {
      gridColumn: `${columns} / span 1`,
    };
  }

  return {
    gridColumn: `${remainder + 1} / ${columns + 1}`,
  };
});

watch(
  () => props.defaultCollapsed,
  (value) => {
    if (props.collapsed === undefined) {
      innerCollapsed.value = value;
    }
  },
);

function handleQuery() {
  emit('query');
  emit('search');
}

function handleReset() {
  emit('reset');
}

function toggleCollapsed() {
  collapsedState.value = !collapsedState.value;
}

function flattenNodes(nodes: VNode[]): VNode[] {
  const flattened: VNode[] = [];

  for (const node of nodes) {
    if (node.type === Comment) {
      continue;
    }

    if (node.type === Text) {
      if (typeof node.children === 'string' && !node.children.trim()) {
        continue;
      }
      flattened.push(node);
      continue;
    }

    if (node.type === Fragment) {
      const children = Array.isArray(node.children)
        ? (node.children as VNode[])
        : [];
      flattened.push(...flattenNodes(children));
      continue;
    }

    flattened.push(node);
  }

  return flattened;
}
</script>

<template>
  <section
    class="platform-query-panel platform-surface"
    :style="panelStyle"
  >
    <div class="platform-query-panel__grid">
      <div
        v-for="(fieldNode, index) in visibleFieldNodes"
        :key="fieldNode.key ?? `platform-query-panel-field-${index}`"
        class="platform-query-panel__field"
      >
        <RenderVNode :node="fieldNode" />
      </div>

      <div
        v-if="hasActions"
        class="platform-query-panel__actions"
        :style="actionsStyle"
      >
        <slot name="actions"></slot>
        <PlatformButton
          v-if="showReset"
          scene="toolbar"
          @click="handleReset"
        >
          {{ resetText }}
        </PlatformButton>
        <PlatformButton
          v-if="showQuery"
          :loading="queryLoading"
          scene="toolbar"
          type="primary"
          @click="handleQuery"
        >
          {{ queryText }}
        </PlatformButton>
        <PlatformButton
          v-if="canCollapse"
          scene="collapse"
          @click="toggleCollapsed"
        >
          {{ collapseLabel }}
          <template #icon>
            <i :class="collapseIconClass" aria-hidden="true"></i>
          </template>
        </PlatformButton>
      </div>
    </div>
  </section>
</template>

<style scoped>
.platform-query-panel {
  padding: var(--st-module-content-padding);
}

.platform-query-panel__grid {
  display: grid;
  grid-template-columns: var(--platform-query-panel-columns);
  gap: 16px 24px;
}

.platform-query-panel__field {
  min-width: 0;
}

.platform-query-panel__field :deep(.ant-form-item) {
  margin-bottom: 0;
}

.platform-query-panel__field :deep(.ant-form-item-row) {
  display: flex;
  align-items: center;
}

.platform-query-panel__field :deep(.ant-form-item-label) {
  flex: 0 0 var(--platform-query-panel-label-width);
  max-width: var(--platform-query-panel-label-width);
  padding: 0 12px 0 0;
  text-align: right;
  white-space: nowrap;
}

.platform-query-panel__field :deep(.ant-form-item-label > label) {
  display: inline-flex;
  align-items: center;
  justify-content: flex-end;
  width: 100%;
  height: var(--st-control-height);
  color: hsl(var(--foreground));
}

.platform-query-panel__field :deep(.ant-form-item-label > label::after) {
  display: none;
  content: none;
}

.platform-query-panel__field :deep(.ant-form-item-control) {
  flex: 1;
  min-width: 0;
}

.platform-query-panel__field :deep(.ant-form-item-control-input) {
  min-height: var(--st-control-height);
}

.platform-query-panel__field :deep(.ant-form-item-control-input-content) {
  width: 100%;
}

.platform-query-panel__field :deep(.ant-input),
.platform-query-panel__field :deep(.ant-input-affix-wrapper),
.platform-query-panel__field :deep(.ant-picker),
.platform-query-panel__field
  :deep(.ant-select:not(.ant-select-customize-input) .ant-select-selector) {
  width: 100%;
}

.platform-query-panel__actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: var(--st-search-form-action-gap);
  min-width: 0;
}

.platform-query-panel__collapse-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 12px;
  height: 12px;
  color: currentColor;
  font-size: 12px;
  line-height: 1;
  transition: transform 0.16s ease;
}

.platform-query-panel__collapse-icon--expanded {
  transform: rotate(180deg);
}

@media (max-width: 768px) {
  .platform-query-panel__grid {
    grid-template-columns: 1fr;
  }

  .platform-query-panel__field :deep(.ant-form-item-row) {
    align-items: stretch;
    flex-direction: column;
  }

  .platform-query-panel__field :deep(.ant-form-item-label) {
    flex: none;
    max-width: none;
    padding: 0 0 8px;
    text-align: left;
  }

  .platform-query-panel__field :deep(.ant-form-item-label > label) {
    justify-content: flex-start;
    height: auto;
  }

  .platform-query-panel__actions {
    grid-column: auto !important;
    flex-wrap: wrap;
    justify-content: flex-start;
  }
}

@media (max-width: 1440px) {
  .platform-query-panel__grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
