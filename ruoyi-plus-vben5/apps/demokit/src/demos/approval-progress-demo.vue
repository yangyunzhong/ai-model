<script setup lang="ts">
import type { PlatformApprovalProgressItem } from '@st/platform-ui';

import { PlatformApprovalProgress } from '@st/platform-ui';

const items: PlatformApprovalProgressItem[] = [
  {
    assignee: '王敏',
    department: '项目管理部',
    id: 'submit',
    status: 'finished',
    time: '05-28 09:20',
    title: '发起立项申请',
  },
  {
    assignee: '陈涛',
    department: '质量中心',
    id: 'review',
    status: 'current',
    time: '处理中',
    title: '质量复核',
  },
  {
    assignee: '李蕾',
    department: '分管领导',
    id: 'approve',
    status: 'pending',
    title: '领导审批',
  },
];
</script>

<template>
  <div class="demo-grid-2">
    <div class="demo-surface demo-surface--bare">
      <PlatformApprovalProgress :items="items" />
    </div>

    <div class="demo-surface demo-flow">
      <h3 class="demo-title">审批流展示建议</h3>
      <p class="demo-muted">
        适合放在详情页右侧、抽屉内或流程面板中，统一展示当前节点、责任人和处理时点。
      </p>
      <ul class="demo-list">
        <li>适用于审批、验收、整改、合同签署等线性流程。</li>
        <li>当前节点用 `current`，已完成节点用 `finished`，待处理节点用 `pending`。</li>
        <li>如果业务页已有右侧流程条，优先回收到平台组件，而不是页面里重复写时间线。</li>
      </ul>
    </div>
  </div>
</template>
