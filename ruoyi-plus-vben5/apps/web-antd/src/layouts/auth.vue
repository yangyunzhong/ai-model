<script lang="ts" setup>
import { computed } from 'vue';

import { AuthPageLayout } from '@vben/layouts';
import { preferences } from '@vben/preferences';

import { $t } from '#/locales';

const appName = computed(() => preferences.app.name);
const logo = computed(() => 'iconfont:icon-LOGO-green');
const logoDark = computed(() => 'iconfont:icon-LOGO-green');
</script>

<template>
  <AuthPageLayout
    :app-name="appName"
    :copyright="false"
    :logo="logo"
    :logo-dark="logoDark"
    :page-description="$t('authentication.pageDesc')"
    :page-title="$t('authentication.pageTitle')"
    :toolbar="false"
  >
    <!-- 自定义工具栏 -->
    <!-- <template #toolbar></template> -->
  </AuthPageLayout>
</template>
