<script setup lang="ts">
import PlatformViewToolbar from './platform-view-toolbar.vue';

defineProps<{
  description?: string;
  tabs?: ReadonlyArray<{
    label: string;
    value: string;
  }>;
  title: string;
  value: string;
}>();

const emit = defineEmits<{
  'update:value': [value: string];
}>();
</script>

<template>
  <section class="platform-page-shell">
    <div class="platform-page-shell__header">
      <PlatformViewToolbar
        :description="description"
        :title="title"
      />
      <div
        v-if="tabs && tabs.length > 0"
        class="platform-page-shell__tabs"
        role="tablist"
        aria-label="页面内容分段"
      >
        <button
          v-for="tab in tabs"
          :key="tab.value"
          class="platform-page-shell__tab"
          :class="{ 'is-active': value === tab.value }"
          type="button"
          @click="emit('update:value', tab.value)"
        >
          {{ tab.label }}
        </button>
      </div>
    </div>

    <article class="platform-page-shell__panel">
      <slot></slot>
    </article>
  </section>
</template>

<style scoped>
.platform-page-shell {
  display: grid;
  gap: var(--st-layout-section-gap);
}

.platform-page-shell__header {
  display: grid;
  gap: 20px;
}

.platform-page-shell__tabs {
  display: flex;
  gap: 28px;
  padding-bottom: 10px;
  border-bottom: 1px solid hsl(var(--border));
}

.platform-page-shell__tab {
  position: relative;
  padding: 0 0 12px;
  color: hsl(var(--muted-foreground));
  font-size: 16px;
  font-weight: 600;
  background: transparent;
  border: 0;
  cursor: pointer;
}

.platform-page-shell__tab.is-active {
  color: hsl(var(--primary));
}

.platform-page-shell__tab.is-active::after {
  position: absolute;
  right: 0;
  bottom: -11px;
  left: 0;
  height: 3px;
  content: '';
  background: hsl(var(--primary));
  border-radius: 999px;
}

.platform-page-shell__panel {
  min-height: 420px;
  padding: var(--st-module-content-padding);
  background: hsl(var(--st-color-card-bg));
  border: 1px solid hsl(var(--border));
  border-radius: var(--st-radius-card);
  box-shadow: var(--st-shadow-card);
}

@media (max-width: 960px) {
  .platform-page-shell__tabs {
    gap: 20px;
    overflow-x: auto;
  }

  .platform-page-shell__tab {
    white-space: nowrap;
  }

  .platform-page-shell__panel {
    padding: 20px;
  }
}
</style>
