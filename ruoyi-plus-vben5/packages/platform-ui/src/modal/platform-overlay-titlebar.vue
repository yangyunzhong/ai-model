<script setup lang="ts">
withDefaults(
  defineProps<{
    closeIconClass: string;
    collapseFullscreenIconClass?: string;
    fullscreen?: boolean;
    fullscreenIconClass?: string;
    showFullscreen?: boolean;
    title?: string;
  }>(),
  {
    collapseFullscreenIconClass: '',
    fullscreen: false,
    fullscreenIconClass: '',
    showFullscreen: false,
    title: '',
  },
);

defineEmits<{
  cancel: [MouseEvent];
  toggleFullscreen: [];
}>();
</script>

<template>
  <div class="platform-overlay-titlebar">
    <div class="platform-overlay-titlebar__title-content">
      <div class="platform-overlay-titlebar__title-text">
        <slot name="title">
          {{ title }}
        </slot>
      </div>
      <slot name="title-extra"></slot>
    </div>

    <div class="platform-overlay-titlebar__actions">
      <button
        v-if="showFullscreen"
        class="platform-overlay-titlebar__icon-button platform-overlay-titlebar__icon-button--fullscreen"
        :class="{
          'platform-overlay-titlebar__icon-button--active': fullscreen,
        }"
        type="button"
        @click.stop="$emit('toggleFullscreen')"
      >
        <span class="platform-overlay-titlebar__icon-bg" aria-hidden="true"></span>
        <span class="sr-only">
          {{ fullscreen ? '退出全屏' : '全屏' }}
        </span>
        <i
          v-if="fullscreenIconClass"
          class="platform-overlay-titlebar__icon iconfont platform-overlay-titlebar__icon--expand"
          :class="fullscreenIconClass"
          aria-hidden="true"
        ></i>
        <i
          v-if="collapseFullscreenIconClass"
          class="platform-overlay-titlebar__icon iconfont platform-overlay-titlebar__icon--shrink"
          :class="collapseFullscreenIconClass"
          aria-hidden="true"
        ></i>
      </button>

      <button
        class="platform-overlay-titlebar__icon-button platform-overlay-titlebar__icon-button--close"
        type="button"
        @click.stop="$emit('cancel', $event)"
      >
        <span class="platform-overlay-titlebar__icon-bg" aria-hidden="true"></span>
        <span class="sr-only">关闭</span>
        <i
          class="platform-overlay-titlebar__icon iconfont"
          :class="closeIconClass"
          aria-hidden="true"
        ></i>
      </button>
    </div>

    <div class="platform-overlay-titlebar__line" aria-hidden="true"></div>
  </div>
</template>

<style scoped>
.platform-overlay-titlebar {
  position: relative;
  width: 100%;
  height: var(--platform-overlay-titlebar-height, 80px);
}

.platform-overlay-titlebar__line {
  position: absolute;
  right: var(--platform-overlay-titlebar-line-inset-inline, 40px);
  bottom: var(--platform-overlay-titlebar-line-offset-bottom, 0);
  left: var(--platform-overlay-titlebar-line-inset-inline, 40px);
  height: 3px;
  background: var(--st-color-text-strong, #0b0c0d);
  pointer-events: none;
}

.platform-overlay-titlebar__title-content {
  position: absolute;
  top: var(--platform-overlay-titlebar-title-offset-top, 40px);
  left: var(--platform-overlay-titlebar-title-inset-inline-start, 40px);
  right: var(--platform-overlay-titlebar-title-inset-inline-end, 120px);
  min-width: 0;
  z-index: 1;
}

.platform-overlay-titlebar__title-text {
  min-width: 0;
  color: var(--st-modal-icon-color, var(--st-color-text-strong, #0b0c0d));
  font-size: 18px;
  font-weight: 600;
  line-height: 24px;
}

.platform-overlay-titlebar__title-text :deep(*) {
  margin: 0;
}

.platform-overlay-titlebar__actions {
  position: absolute;
  top: var(--platform-overlay-titlebar-actions-offset-top, 22px);
  right: var(--platform-overlay-titlebar-actions-offset-inline-end, 22px);
  display: flex;
  align-items: center;
  gap: 20px;
  z-index: 1;
}

.platform-overlay-titlebar__icon-button {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  padding: 0;
  color: var(--st-modal-icon-color, var(--st-color-text-strong, #0b0c0d));
  background: transparent;
  border: none;
  border-radius: 0;
  cursor: pointer;
  isolation: isolate;
}

.platform-overlay-titlebar__icon-button:focus-visible {
  outline: none;
}

.platform-overlay-titlebar__icon-button::before {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 20px;
  height: 20px;
  content: '';
  background: var(--st-modal-icon-hover-bg, #e7ebf4);
  border-radius: 4px;
  transform: translate(-50%, -50%);
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.16s ease;
  z-index: 0;
}

.platform-overlay-titlebar__icon-bg {
  display: none;
}

.platform-overlay-titlebar__icon-button--fullscreen:hover::before,
.platform-overlay-titlebar__icon-button--fullscreen:focus-visible::before,
.platform-overlay-titlebar__icon-button--close:hover::before,
.platform-overlay-titlebar__icon-button--close:focus-visible::before {
  opacity: 1;
}

.platform-overlay-titlebar__icon {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 24px;
  height: 24px;
  flex-shrink: 0;
  color: inherit;
  font-size: 24px;
  line-height: 1;
  z-index: 1;
  transform: translate(-50%, -50%);
  transition: opacity 0.16s ease;
}

.platform-overlay-titlebar__icon-button--fullscreen .platform-overlay-titlebar__icon--expand {
  opacity: 1;
}

.platform-overlay-titlebar__icon-button--fullscreen .platform-overlay-titlebar__icon--shrink {
  opacity: 0;
}

.platform-overlay-titlebar__icon-button--fullscreen.platform-overlay-titlebar__icon-button--active
  .platform-overlay-titlebar__icon--expand {
  opacity: 0;
}

.platform-overlay-titlebar__icon-button--fullscreen.platform-overlay-titlebar__icon-button--active
  .platform-overlay-titlebar__icon--shrink {
  opacity: 1;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  white-space: nowrap;
  clip: rect(0, 0, 0, 0);
  border: 0;
}
</style>
