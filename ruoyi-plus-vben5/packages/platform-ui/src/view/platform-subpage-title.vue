<script setup lang="ts">
import ditieIcon from './assets/ditie.png';

import PlatformSectionTitle from './platform-section-title.vue';

withDefaults(
  defineProps<{
    description?: string;
    divider?: boolean;
    padding?: string;
    title: string;
  }>(),
  {
    description: '',
    divider: false,
    padding: '0',
  },
);
</script>

<template>
  <PlatformSectionTitle
    class="platform-subpage-title"
    :description="description"
    :divider="divider"
    :icon-gap="4"
    :icon-height="16"
    :icon-src="ditieIcon"
    :icon-width="28"
    image-alt="二级页面标题"
    :padding="padding"
    :title="title"
    :title-size="16"
    :title-weight="600"
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
</template>

<style scoped>
.platform-subpage-title {
  min-width: 0;
}
</style>
