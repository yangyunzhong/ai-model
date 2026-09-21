<script setup lang="ts">
import PlatformSectionTitle from './platform-section-title.vue';

withDefaults(
  defineProps<{
    description?: string;
    title?: string;
  }>(),
  {
    description: '',
    title: '',
  },
);
</script>

<template>
  <section class="platform-section platform-surface">
    <PlatformSectionTitle
      v-if="title || description || $slots['title-extra'] || $slots.center || $slots.extra"
      :description="description"
      :title="title"
      class="platform-section__header"
    >
      <template v-if="$slots['title-extra']" #title-extra>
        <slot name="title-extra"></slot>
      </template>
      <template v-if="$slots.center" #center>
        <slot name="center"></slot>
      </template>
      <template v-if="$slots.extra" #extra>
        <slot name="extra"></slot>
      </template>
    </PlatformSectionTitle>
    <slot></slot>
  </section>
</template>

<style scoped>
.platform-section {
  min-width: 0;
}

.platform-section__header {
  margin-bottom: 18px;
}
</style>
