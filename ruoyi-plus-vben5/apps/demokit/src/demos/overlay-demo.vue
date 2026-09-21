<script setup lang="ts">
import { ref } from 'vue';

import {
  PlatformButton,
  PlatformDrawer,
  PlatformFormItem,
  PlatformInput,
  PlatformModal,
  PlatformModalTitle,
} from '@st/platform-ui';

const drawerOpen = ref(false);
const modalOpen = ref(false);

const props = defineProps<{
  demoId?: string;
}>();
</script>

<template>
  <div class="demo-flow">
    <div class="demo-row">
      <PlatformButton
        v-if="props.demoId !== 'platform-drawer'"
        type="primary"
        @click="modalOpen = true"
      >
        打开弹窗
      </PlatformButton>
      <PlatformButton
        v-if="props.demoId !== 'platform-modal'"
        :type="props.demoId === 'platform-drawer' ? 'primary' : 'default'"
        @click="drawerOpen = true"
      >
        打开抽屉
      </PlatformButton>
    </div>

    <div class="demo-grid-2" :class="{ 'demo-grid-2--single': !!props.demoId }">
      <div v-if="props.demoId === 'platform-modal-title'" class="demo-surface demo-flow">
        <PlatformModalTitle title="编辑项目计划" />
      </div>

      <div v-if="props.demoId !== 'platform-drawer'" class="demo-surface demo-flow">
        <h3 class="demo-title">PlatformModal</h3>
        <p class="demo-muted">
          适合新增、编辑、审批确认等需要强聚焦的流程弹层，统一标题栏、全屏和 footer 间距。
        </p>
      </div>

      <div v-if="props.demoId !== 'platform-modal'" class="demo-surface demo-flow">
        <h3 class="demo-title">PlatformDrawer</h3>
        <p class="demo-muted">
          适合右侧详情、批量配置、附加信息维护等不希望打断当前上下文的场景。
        </p>
      </div>
    </div>

    <PlatformModal
      v-if="props.demoId !== 'platform-drawer' && props.demoId !== 'platform-modal-title'"
      v-model:open="modalOpen"
      title="编辑项目计划"
      width="860px"
    >
      <div class="demo-flow">
        <PlatformFormItem label="项目名称">
          <PlatformInput placeholder="请输入项目名称" />
        </PlatformFormItem>
        <PlatformFormItem label="负责人">
          <PlatformInput placeholder="请输入负责人" />
        </PlatformFormItem>
      </div>
    </PlatformModal>

    <PlatformDrawer
      v-if="props.demoId !== 'platform-modal' && props.demoId !== 'platform-modal-title'"
      v-model:open="drawerOpen"
      title="项目详情抽屉"
      width="520px"
    >
      <div class="demo-flow">
        <p class="demo-muted">
          这里可以继续承载审批流、文件列表、附加信息等内容，不需要页面再重复造抽屉头部。
        </p>
        <PlatformInput placeholder="抽屉中的输入框" />
      </div>
    </PlatformDrawer>
  </div>
</template>
