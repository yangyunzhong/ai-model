<script setup lang="ts">
import PlatformOverlayTitlebar from './platform-overlay-titlebar.vue';

withDefaults(
  defineProps<{
    closeIconClass?: string;
    collapseFullscreenIconClass?: string;
    fullscreen?: boolean;
    fullscreenIconClass?: string;
    showFullscreen?: boolean;
    title?: string;
  }>(),
  {
    closeIconClass: 'icon-danchuangguanli',
    collapseFullscreenIconClass: 'icon-danchuangshouqi',
    fullscreen: false,
    fullscreenIconClass: 'icon-danchuangquanping',
    showFullscreen: true,
    title: '',
  },
);

defineEmits<{
  cancel: [MouseEvent];
  toggleFullscreen: [];
}>();
</script>

<template>
  <PlatformOverlayTitlebar
    :close-icon-class="closeIconClass"
    :collapse-fullscreen-icon-class="collapseFullscreenIconClass"
    :fullscreen="fullscreen"
    :fullscreen-icon-class="fullscreenIconClass"
    :show-fullscreen="showFullscreen"
    :title="title"
    @cancel="$emit('cancel', $event)"
    @toggle-fullscreen="$emit('toggleFullscreen')"
  >
    <template v-if="$slots.title" #title>
      <slot name="title"></slot>
    </template>
    <template v-if="$slots['title-extra']" #title-extra>
      <slot name="title-extra"></slot>
    </template>
  </PlatformOverlayTitlebar>
</template>
