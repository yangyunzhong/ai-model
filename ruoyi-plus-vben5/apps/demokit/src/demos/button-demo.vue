<script setup lang="ts">
import { PlatformButton } from '@st/platform-ui';
</script>

<template>
  <div class="demo-flow">
    <div class="demo-row">
      <PlatformButton type="primary">主按钮</PlatformButton>
      <PlatformButton>默认按钮</PlatformButton>
      <PlatformButton danger>危险操作</PlatformButton>
      <PlatformButton disabled>禁用按钮</PlatformButton>
    </div>
    <div class="demo-row">
      <PlatformButton scene="toolbar" type="primary">工具栏主操作</PlatformButton>
      <PlatformButton scene="toolbar">工具栏次操作</PlatformButton>
      <PlatformButton scene="collapse">展开更多</PlatformButton>
    </div>
    <div class="demo-row">
      <PlatformButton scene="action" type="link">编辑</PlatformButton>
      <PlatformButton scene="action" danger type="link">删除</PlatformButton>
    </div>
  </div>
</template>
