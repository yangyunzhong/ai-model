<script setup lang="ts">
import type { CSSProperties } from 'vue';

import { computed, useSlots } from 'vue';

interface Props {
  /**
   * 横屏
   */
  fullWidth: boolean;
  /**
   * 高度
   */
  height: number;
  /**
   * 是否移动端
   */
  isMobile: boolean;
  /**
   * 是否显示
   */
  show: boolean;
  /**
   * 侧边菜单宽度
   */
  sidebarWidth: number;
  /**
   * 主题
   */
  theme: string | undefined;
  /**
   * 宽度
   */
  width: string;
  /**
   * zIndex
   */
  zIndex: number;
}

const props = withDefaults(defineProps<Props>(), {});

const slots = useSlots();

const style = computed((): CSSProperties => {
  const { fullWidth, height, show } = props;
  const right = !show || !fullWidth ? undefined : 0;

  return {
    height: `${height}px`,
    marginTop: show ? 0 : `-${height}px`,
    right,
  };
});

const logoStyle = computed((): CSSProperties => {
  const baseWidth = props.isMobile
    ? 40
    : `max(${props.sidebarWidth}px, var(--st-header-logo-width, 360px))`;

  return {
    flex: '0 0 auto',
    minWidth: typeof baseWidth === 'number' ? `${baseWidth}px` : baseWidth,
  };
});
</script>

<template>
  <header
    :class="theme"
    :style="style"
    class="vben-layout-header top-0 flex w-full flex-[0_0_auto] items-center border-b bg-header transition-[margin-top] duration-200"
  >
    <div v-if="slots.logo" class="vben-layout-header__logo" :style="logoStyle">
      <slot name="logo"></slot>
    </div>

    <slot name="toggle-button"> </slot>

    <slot></slot>
  </header>
</template>

<style scoped>
.vben-layout-header {
  color: hsl(var(--header-foreground));
  border-color: hsl(var(--header-border));
  box-shadow: 0 1px 0 hsl(var(--st-color-nav-divider));
}

.vben-layout-header__logo {
  height: 100%;
  overflow: hidden;
}

.vben-layout-header :deep(.text-foreground),
.vben-layout-header :deep(.text-muted-foreground),
.vben-layout-header :deep(.vben-icon-button) {
  color: hsl(var(--header-foreground) / 90%);
}

.vben-layout-header :deep(.text-foreground:hover),
.vben-layout-header :deep(.vben-icon-button:hover) {
  color: hsl(var(--header-foreground));
}
</style>
