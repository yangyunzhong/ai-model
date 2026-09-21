<script setup lang="ts">
import type { ModalProps } from 'antdv-next';

import { computed, useAttrs, useSlots, watch } from 'vue';

import { Modal } from 'antdv-next';

import PlatformModalTitle from './platform-modal-title.vue';

defineOptions({
  inheritAttrs: false,
});

const attrs = useAttrs();
const slots = useSlots();

const open = defineModel<boolean>('open', {
  default: false,
});

const isFullscreen = defineModel<boolean>('fullscreen', {
  default: false,
});

const modalTitle = computed(() => {
  if (typeof attrs.title === 'string' || typeof attrs.title === 'number') {
    return String(attrs.title);
  }
  return '';
});

const showTitleHeader = computed(() => {
  return !!modalTitle.value || !!slots.title;
});

const forwardedSlotNames = computed(() => {
  return Object.keys(slots).filter(
    (name) => !['default', 'title', 'title-extra'].includes(name),
  );
});

function mergeClassNames(...values: unknown[]) {
  return values
    .filter((value) => typeof value === 'string' && value.trim())
    .join(' ');
}

function normalizeStyle(value: unknown) {
  if (value && typeof value === 'object' && !Array.isArray(value)) {
    return { ...value };
  }
  return {};
}

function normalizeSemanticStyles(value: unknown) {
  if (value && typeof value === 'object' && !Array.isArray(value)) {
    return { ...(value as Record<string, unknown>) };
  }
  return {};
}

function normalizeModalWidth(value: unknown): ModalProps['width'] {
  if (typeof value === 'number' || typeof value === 'string') {
    return value;
  }

  if (value && typeof value === 'object' && !Array.isArray(value)) {
    return value as ModalProps['width'];
  }

  return undefined;
}

function normalizeBoolean(value: unknown) {
  if (typeof value === 'boolean') {
    return value;
  }
  if (value === '') {
    return true;
  }
  if (value === 'true') {
    return true;
  }
  if (value === 'false') {
    return false;
  }
  return undefined;
}

const modalAttrs = computed(() => {
  const {
    bodyStyle,
    'body-style': bodyStyleKebab,
    class: modalClass,
    closable: _closable,
    destroyOnClose,
    'destroy-on-close': destroyOnCloseKebab,
    destroyOnHidden,
    'destroy-on-hidden': destroyOnHiddenKebab,
    style,
    styles,
    title: _title,
    wrapClassName,
    width,
    ...rest
  } = attrs;

  const mergedWrapClassName = mergeClassNames(
    'platform-modal__wrap',
    isFullscreen.value ? 'platform-modal__wrap--fullscreen' : '',
    wrapClassName,
  );

  const mergedClassName = mergeClassNames(
    'platform-modal',
    isFullscreen.value ? 'platform-modal--fullscreen' : '',
    modalClass,
  );

  const mergedStyle = normalizeStyle(style);
  const mergedBodyStyle = {
    ...normalizeStyle(bodyStyle),
    ...normalizeStyle(bodyStyleKebab),
  };
  const mergedSemanticStyles = normalizeSemanticStyles(styles);
  const mergedDestroyOnHidden = normalizeBoolean(
    destroyOnHidden ??
      destroyOnHiddenKebab ??
      destroyOnClose ??
      destroyOnCloseKebab,
  );

  mergedSemanticStyles.body = {
    ...mergedBodyStyle,
    ...normalizeStyle(mergedSemanticStyles.body),
    padding: '24px 40px 0',
  };
  mergedSemanticStyles.footer = {
    ...normalizeStyle(mergedSemanticStyles.footer),
    background: 'transparent',
    borderTop: '1px solid hsl(var(--st-color-border-subtle))',
    marginTop: '0',
    padding: '16px 40px 24px',
  };
  mergedSemanticStyles.header = {
    ...normalizeStyle(mergedSemanticStyles.header),
    background: 'transparent',
    borderBottom: 'none',
    marginBottom: 0,
    padding: 0,
  };

  if (isFullscreen.value) {
    Object.assign(mergedStyle, {
      height: '100vh',
      inset: '0',
      maxWidth: 'none',
      paddingBottom: '0',
      top: '0',
      width: '100vw',
    });

    Object.assign(mergedBodyStyle, {
      overflowY: 'auto',
      paddingBottom: '24px',
    });

    mergedSemanticStyles.body = {
      ...normalizeStyle(mergedSemanticStyles.body),
      minHeight: 0,
      overflowY: 'auto',
      paddingBottom: '24px',
    };
  }

  return {
    ...rest,
    class: mergedClassName,
    closable: false,
    destroyOnHidden: mergedDestroyOnHidden,
    style: mergedStyle,
    styles: mergedSemanticStyles,
    width: isFullscreen.value ? '100vw' : normalizeModalWidth(width),
    wrapClassName: mergedWrapClassName,
  };
});

watch(open, (value) => {
  if (!value && isFullscreen.value) {
    isFullscreen.value = false;
  }
});

function callAttrHandler(name: string, event: MouseEvent) {
  const handler = attrs[name];

  if (Array.isArray(handler)) {
    for (const item of handler) {
      if (typeof item === 'function') {
        item(event);
      }
    }
    return;
  }

  if (typeof handler === 'function') {
    handler(event);
  }
}

function handleCancel(event: MouseEvent) {
  open.value = false;
  callAttrHandler('onCancel', event);
}

function toggleFullscreen() {
  isFullscreen.value = !isFullscreen.value;
}
</script>

<template>
  <Modal v-model:open="open" v-bind="modalAttrs">
    <template v-if="showTitleHeader" #title>
      <PlatformModalTitle
        :fullscreen="isFullscreen"
        :title="modalTitle"
        @cancel="handleCancel"
        @toggle-fullscreen="toggleFullscreen"
      >
        <template #title>
          <slot name="title">
            {{ modalTitle }}
          </slot>
        </template>
        <template #title-extra>
          <slot name="title-extra"></slot>
        </template>
      </PlatformModalTitle>
    </template>

    <slot></slot>

    <template v-for="name in forwardedSlotNames" #[name]="slotProps">
      <slot :name="name" v-bind="slotProps || {}"></slot>
    </template>
  </Modal>
</template>

<style scoped>
.platform-modal {
  --st-modal-header-padding-inline: 40px;
  --st-modal-header-padding-top: 20px;
  --st-modal-header-padding-bottom: 13px;
  --st-modal-icon-hover-bg: #e7ebf4;
  --st-modal-icon-color: var(--st-color-text-strong, #0b0c0d);
}

.platform-modal :deep(.ant-modal-content) {
  overflow: hidden;
  padding: 0;
  border-radius: var(--st-radius-card);
}

.platform-modal :deep(.ant-modal-header) {
  height: 80px;
  padding: 0;
  margin-bottom: 0;
  background: transparent;
  border-bottom: none;
}

.platform-modal :deep(.ant-modal-title) {
  display: block;
  width: 100%;
}

.platform-modal :deep(.ant-modal-body) {
  padding: 24px 40px 0;
}

.platform-modal :deep(.ant-modal-footer) {
  margin-top: 0;
  padding: 16px 40px 24px;
  background: transparent;
  border-top: 1px solid hsl(var(--st-color-border-subtle));
}

:deep(.platform-modal__wrap--fullscreen) {
  position: fixed;
  inset: 0;
  overflow: hidden;
}

:deep(.platform-modal__wrap--fullscreen .ant-modal) {
  display: flex;
  flex-direction: column;
  top: 0 !important;
  left: 0;
  right: 0;
  max-width: none;
  margin: 0;
  padding: 0;
  height: 100vh !important;
  min-height: 100vh;
}

:deep(.platform-modal__wrap--fullscreen .ant-modal-container) {
  display: flex;
  height: 100vh;
  min-height: 100vh;
  padding: 0 40px 24px 40px !important;
}

:deep(.platform-modal__wrap--fullscreen .ant-modal-content) {
  display: flex;
  flex: 1;
  flex-direction: column;
  height: 100vh !important;
  min-height: 100vh;
  max-height: 100vh;
  border-radius: 0 !important;
}

:deep(.platform-modal__wrap--fullscreen .ant-modal-body) {
  flex: 1;
  min-height: 0;
  height: 100%;
  overflow: auto;
}

</style>
