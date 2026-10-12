<script setup lang="ts">
import { ref } from 'vue';

import { PlatformModal } from '@st/platform-ui';

import { getExecLogLines } from '../../data';

const logOpen = ref(false);
const execNo = ref('');
const logLines = ref<string[]>([]);

/** 打开执行日志弹窗 */
function open(no: string) {
  execNo.value = no;
  logLines.value = getExecLogLines(no);
  logOpen.value = true;
}

defineExpose({ open });
</script>

<template>
  <PlatformModal
    v-model:open="logOpen"
    :footer="null"
    destroy-on-close
    :title="`执行日志 - ${execNo}`"
    width="720px"
  >
    <pre class="task-exec-log">{{ logLines.join('\n') }}</pre>
  </PlatformModal>
</template>

<style scoped>
.task-exec-log {
  max-height: 50vh;
  margin: 0;
  padding: 12px;
  overflow: auto;
  font-size: 12px;
  line-height: 1.8;
  color: hsl(var(--foreground));
  background: hsl(var(--accent) / 40%);
  border-radius: var(--st-radius-control);
}
</style>
