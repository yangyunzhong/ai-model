<script setup lang="ts">
import type { ToolbarType } from './types';

import { computed } from 'vue';

import { preferences, usePreferences } from '@vben/preferences';

import { Copyright } from '../basic/copyright';
import AuthenticationFormView from './form.vue';
import SloganIcon from './icons/slogan.vue';
import Toolbar from './toolbar.vue';

interface Props {
  appName?: string;
  logo?: string;
  logoDark?: string;
  pageTitle?: string;
  pageDescription?: string;
  sloganImage?: string;
  toolbar?: boolean;
  copyright?: boolean;
  toolbarList?: ToolbarType[];
  clickLogo?: () => void;
}

const props = withDefaults(defineProps<Props>(), {
  appName: '',
  copyright: true,
  logo: '',
  logoDark: '',
  pageDescription: '',
  pageTitle: '',
  sloganImage: '',
  toolbar: true,
  toolbarList: () => ['color', 'language', 'layout', 'theme'],
  clickLogo: () => {},
});

const { authPanelCenter, authPanelLeft, authPanelRight, isDark } =
  usePreferences();

/**
 * @zh_CN 根据主题选择合适的 logo 图标
 */
const logoSrc = computed(() => {
  // 如果是暗色主题且提供了 logoDark，则使用暗色主题的 logo
  if (isDark.value && props.logoDark) {
    return props.logoDark;
  }
  // 否则使用默认的 logo
  return props.logo;
});

const logoIconfontClassName = computed(() => {
  if (!logoSrc.value?.startsWith('iconfont:')) {
    return '';
  }
  return logoSrc.value.replace('iconfont:', '').trim();
});
</script>

<template>
  <div
    :class="[isDark ? 'dark' : '']"
    class="flex min-h-full flex-1 overflow-x-hidden select-none"
  >
    <template v-if="toolbar">
      <slot name="toolbar">
        <Toolbar :toolbar-list="toolbarList" />
      </slot>
    </template>
    <!-- 左侧认证面板 -->
    <AuthenticationFormView
      v-if="authPanelLeft"
      class="min-h-full w-2/5 flex-1"
      data-side="left"
    >
      <template v-if="copyright" #copyright>
        <slot name="copyright">
          <Copyright
            v-if="preferences.copyright.enable"
            v-bind="preferences.copyright"
          />
        </slot>
      </template>
    </AuthenticationFormView>

    <!-- 系统介绍 -->
    <div v-if="!authPanelCenter" class="relative hidden w-0 flex-1 lg:block">
      <div
        class="absolute inset-0 size-full bg-background-deep dark:bg-[#070709]"
      >
        <div class="login-background absolute top-0 left-0 size-full"></div>
        <div
          :key="authPanelLeft ? 'left' : authPanelRight ? 'right' : 'center'"
          class="mr-20 flex-col-center h-full"
          :class="{
            'enter-x': authPanelLeft,
            '-enter-x': authPanelRight,
          }"
        >
          <template v-if="sloganImage">
            <img
              :alt="appName"
              :src="sloganImage"
              class="h-64 w-2/5 animate-float"
            />
          </template>
          <SloganIcon v-else :alt="appName" class="h-64 w-2/5 animate-float" />
          <div class="text-1xl mt-6 font-sans text-foreground lg:text-2xl">
            {{ pageTitle }}
          </div>
          <div class="mt-2 dark:text-muted-foreground">
            {{ pageDescription }}
          </div>
        </div>
      </div>
    </div>

    <!-- 中心认证面板 -->
    <div
      v-if="authPanelCenter"
      class="relative flex min-h-full w-full items-center justify-center px-6"
    >
      <div class="login-background absolute top-0 left-0 size-full"></div>
      <div class="relative z-10 flex w-full flex-col items-center">
        <slot name="logo">
          <div
            v-if="logoSrc || appName"
            class="mb-6 flex w-full max-w-[480px] justify-center text-foreground"
            @click="clickLogo"
          >
            <div class="flex items-center justify-center text-foreground">
              <i
                v-if="logoIconfontClassName"
                :aria-label="appName"
                class="mr-4 size-14 auth-page-logo__iconfont iconfont"
                :class="logoIconfontClassName"
              ></i>
              <img
                v-else-if="logoSrc"
                :key="logoSrc"
                :alt="appName"
                :src="logoSrc"
                class="mr-4 size-14 object-contain"
                width="42"
              />
              <p
                v-if="appName"
                class="m-0 text-[28px] font-semibold tracking-tight"
              >
                {{ appName }}
              </p>
            </div>
          </div>
        </slot>
        <AuthenticationFormView
          class="mx-auto w-full max-w-[480px] rounded-3xl pb-10 shadow-float shadow-primary/5 md:bg-background"
          data-side="bottom"
        >
          <template v-if="copyright" #copyright>
            <slot name="copyright">
              <Copyright
                v-if="preferences.copyright.enable"
                v-bind="preferences.copyright"
              />
            </slot>
          </template>
        </AuthenticationFormView>
      </div>
    </div>

    <!-- 右侧认证面板 -->
    <AuthenticationFormView
      v-if="authPanelRight"
      class="min-h-full w-2/5 flex-1"
      data-side="right"
    >
      <template v-if="copyright" #copyright>
        <slot name="copyright">
          <Copyright
            v-if="preferences.copyright.enable"
            v-bind="preferences.copyright"
          />
        </slot>
      </template>
    </AuthenticationFormView>
  </div>
</template>

<style scoped>
.login-background {
  background: linear-gradient(
    154deg,
    #07070915 30%,
    hsl(var(--primary) / 30%) 48%,
    #07070915 64%
  );
  filter: blur(100px);
}

.dark {
  .login-background {
    background: linear-gradient(
      154deg,
      #07070915 30%,
      hsl(var(--primary) / 20%) 48%,
      #07070915 64%
    );
    filter: blur(100px);
  }
}

:deep(.side-content) {
  margin-top: 24px;
  width: 100%;
  max-width: 480px;
}

.auth-page-logo__iconfont {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 56px;
  line-height: 1;
}
</style>
