<script setup lang="ts">
import type {
  DropdownProps,
  MenuEmits,
  MenuProps,
} from 'antdv-next';

import { VbenIcon } from '@vben/icons';
import { Button, Dropdown, Popover, SpaceCompact } from 'antdv-next';
import { computed, shallowRef } from 'vue';

defineOptions({
  inheritAttrs: false,
});

const props = withDefaults(
  defineProps<{
    menuItems?: MenuProps['items'];
    menuPlacement?: DropdownProps['placement'];
    menuTrigger?: DropdownProps['trigger'];
    scene?: 'action' | 'collapse' | 'default' | 'toolbar';
    secondConfirmText?: string;
  }>(),
  {
    menuItems: () => [],
    menuPlacement: 'bottomLeft',
    menuTrigger: () => ['click'],
    scene: 'default',
    secondConfirmText: '',
  },
);

const emit = defineEmits<{
  click: [event: MouseEvent];
  menuClick: Parameters<MenuEmits['click']>;
}>();

const secondConfirmOpen = shallowRef(false);
const pendingClickEvent = shallowRef<MouseEvent>();
const hasMenu = computed(() => (props.menuItems?.length ?? 0) > 0);
const menuConfig = computed<DropdownProps['menu']>(() => ({
  items: props.menuItems,
  onClick: handleMenuClick,
}));

function handleButtonClick(event: MouseEvent) {
  if (!props.secondConfirmText) {
    emit('click', event);
    return;
  }

  pendingClickEvent.value = event;
  secondConfirmOpen.value = true;
}

function handleConfirmClick(event: MouseEvent) {
  secondConfirmOpen.value = false;
  emit('click', pendingClickEvent.value ?? event);
  pendingClickEvent.value = undefined;
}

function handleCancelClick() {
  secondConfirmOpen.value = false;
  pendingClickEvent.value = undefined;
}

const handleMenuClick: MenuEmits['click'] = (...args) => {
  emit('menuClick', ...args);
};
</script>

<template>
  <SpaceCompact v-if="hasMenu" class="platform-button-menu">
    <Popover
      v-if="secondConfirmText"
      v-model:open="secondConfirmOpen"
      placement="top"
      trigger="click"
    >
      <template #content>
        <div class="platform-button-confirm">
          <div class="platform-button-confirm__text">
            {{ secondConfirmText }}
          </div>
          <div class="platform-button-confirm__actions">
            <Button size="small" @click="handleCancelClick">取消</Button>
            <Button size="small" type="primary" @click="handleConfirmClick">
              确认
            </Button>
          </div>
        </div>
      </template>

      <Button
        v-bind="$attrs"
        class="platform-button"
        :class="`platform-button--${scene}`"
        @click="handleButtonClick"
      >
        <template v-for="(_, name) in $slots" #[name]="slotProps">
          <slot :name="name" v-bind="slotProps || {}"></slot>
        </template>
      </Button>
    </Popover>

    <Button
      v-else
      v-bind="$attrs"
      class="platform-button"
      :class="`platform-button--${scene}`"
      @click="handleButtonClick"
    >
      <template v-for="(_, name) in $slots" #[name]="slotProps">
        <slot :name="name" v-bind="slotProps || {}"></slot>
      </template>
    </Button>

    <Dropdown
      :menu="menuConfig"
      :placement="menuPlacement"
      :trigger="menuTrigger"
    >
      <Button
        aria-label="更多操作"
        class="platform-button platform-button__menu-trigger"
        :class="`platform-button--${scene}`"
        :disabled="Boolean($attrs.disabled)"
      >
        <template #icon>
          <VbenIcon icon="lucide:ellipsis" />
        </template>
      </Button>
    </Dropdown>
  </SpaceCompact>

  <Button
    v-else-if="!secondConfirmText"
    v-bind="$attrs"
    class="platform-button"
    :class="`platform-button--${scene}`"
    @click="handleButtonClick"
  >
    <template v-for="(_, name) in $slots" #[name]="slotProps">
      <slot :name="name" v-bind="slotProps || {}"></slot>
    </template>
  </Button>

  <Popover
    v-else
    v-model:open="secondConfirmOpen"
    placement="top"
    trigger="click"
  >
    <template #content>
      <div class="platform-button-confirm">
        <div class="platform-button-confirm__text">{{ secondConfirmText }}</div>
        <div class="platform-button-confirm__actions">
          <Button size="small" @click="handleCancelClick">取消</Button>
          <Button size="small" type="primary" @click="handleConfirmClick">
            确认
          </Button>
        </div>
      </div>
    </template>

    <Button
      v-bind="$attrs"
      class="platform-button"
      :class="`platform-button--${scene}`"
      @click="handleButtonClick"
    >
      <template v-for="(_, name) in $slots" #[name]="slotProps">
        <slot :name="name" v-bind="slotProps || {}"></slot>
      </template>
    </Button>
  </Popover>
</template>

<style scoped>
.platform-button {
  cursor: pointer;
  font-weight: 500;
  border-radius: var(--st-radius-control);
}

.platform-button--toolbar {
  min-width: calc(var(--st-control-height) * 2.1);
}

.platform-button__menu-trigger {
  min-width: var(--st-control-height);
  padding-inline: 8px;
}

.platform-button--toolbar:not(.ant-btn-circle):not(.ant-btn-primary):not(
    .ant-btn-dangerous
  ) {
  color: hsl(var(--primary));
  background: hsl(var(--st-color-fill-selected));
  border-color: hsl(var(--st-color-brand-outline));
}

.platform-button--toolbar:not(.ant-btn-circle):not(.ant-btn-primary):not(
    .ant-btn-dangerous
  ):not(:disabled):not(.ant-btn-disabled):hover,
.platform-button--toolbar:not(.ant-btn-circle):not(.ant-btn-primary):not(
    .ant-btn-dangerous
  ):not(:disabled):not(.ant-btn-disabled):focus-visible {
  color: hsl(var(--primary));
  background: hsl(var(--primary) / 14%);
  border-color: hsl(var(--primary) / 45%);
}

.platform-button--toolbar.ant-btn-circle {
  min-width: var(--st-control-height);
  color: hsl(var(--st-color-table-tool-icon));
  background: hsl(var(--background));
  border-color: hsl(0 0% 0% / 0);
}

.platform-button--toolbar.ant-btn-circle:not(:disabled):not(
    .ant-btn-disabled
  ):hover,
.platform-button--toolbar.ant-btn-circle:not(:disabled):not(
    .ant-btn-disabled
  ):focus-visible {
  color: hsl(var(--st-color-table-tool-hover-icon));
  background: hsl(var(--st-color-table-tool-hover-bg));
  border-color: hsl(var(--st-color-table-tool-hover-border));
}

.platform-button--collapse {
  min-width: calc(var(--st-control-height) * 2.1);
  color: hsl(var(--primary));
  background: transparent;
  border-color: transparent;
}

.platform-button--collapse:not(:disabled):not(.ant-btn-disabled):hover,
.platform-button--collapse:not(:disabled):not(.ant-btn-disabled):focus-visible {
  color: hsl(var(--primary));
  background: hsl(var(--st-color-fill-selected));
  border-color: transparent;
}

.platform-button--action {
  padding-inline: 0;
  font-weight: 600;
  text-decoration: none;
  text-underline-offset: 3px;
}

.platform-button--action + .platform-button--action {
  margin-inline-start: 16px;
}

.platform-button--action:not(:disabled):not(.ant-btn-disabled):hover,
.platform-button--action:not(:disabled):not(.ant-btn-disabled):focus-visible {
  text-decoration: underline;
  background: transparent;
}

.platform-button--action.ant-btn-dangerous,
.platform-button--action.ant-btn-dangerous:not(:disabled):not(
    .ant-btn-disabled
  ):hover,
.platform-button--action.ant-btn-dangerous:not(:disabled):not(
    .ant-btn-disabled
  ):focus-visible {
  color: hsl(var(--st-color-danger)) !important;
}

.platform-button-confirm {
  max-width: 220px;
}

.platform-button-confirm__text {
  margin-bottom: 12px;
  color: hsl(var(--foreground));
  line-height: var(--st-line-height-base);
}

.platform-button-confirm__actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
