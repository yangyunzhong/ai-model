<script setup lang="ts">
import { ref } from 'vue';

import { PlatformSteps } from '@st/platform-ui';

const basicCurrent = ref(1);

const basicItems = [
  { title: '选择项目' },
  { title: '填写申请表' },
  { title: '提交审核' },
  { title: '完成' },
];

const errorCurrent = ref(1);

const errorItems = [
  { title: '填写信息' },
  { title: '提交审核', status: 'error' as const },
  { title: '完成' },
];

const verticalItems = [
  { title: '需求确认', description: '与客户确认需求范围' },
  { title: '方案设计', description: '输出技术方案和排期' },
  { title: '开发实施', description: '按方案完成开发与自测' },
  { title: '验收交付', description: '客户验收并上线' },
];

const dotCurrent = ref(1);

const dotItems = [
  { title: '步骤一', description: '描述信息一' },
  { title: '步骤二', description: '描述信息二' },
  { title: '步骤三', description: '描述信息三' },
];

const iconItems = [
  { title: '登录', icon: 'lucide:log-in' },
  { title: '验证', icon: 'lucide:shield-check' },
  { title: '支付', icon: 'lucide:credit-card' },
  { title: '完成', icon: 'lucide:check-circle-2' },
];

const navCurrent = ref(0);

const navItems = [
  { title: '项目立项', description: '立项审批流程' },
  { title: '方案评审', description: '技术方案评审' },
  { title: '开发测试', description: '编码与测试验证' },
];

const smallItems = [
  { title: '第一步' },
  { title: '第二步' },
  { title: '第三步' },
];

const panelItems = [
  { title: '登录' },
  { title: '验证' },
  { title: '支付' },
  { title: '完成' },
];

const clickableCurrent = ref(0);

const clickableItems = [
  { title: '步骤一', description: '点击可切换' },
  { title: '步骤二', description: '点击可切换' },
  { title: '步骤三', description: '点击可切换' },
];

function handleClickableChange(current: number) {
  clickableCurrent.value = current;
}
</script>

<template>
  <div class="demo-flow">
    <div class="demo-mode">
      <h1>基础步骤条</h1>
      <h2>默认水平步骤条，通过 current 控制当前步骤</h2>
      <div class="demo-surface">
        <PlatformSteps :current="basicCurrent" :items="basicItems" />
        <div style="margin-top: 16px; display: flex; gap: 8px">
          <button
            :disabled="basicCurrent <= 0"
            @click="basicCurrent--"
          >
            上一步
          </button>
          <button
            :disabled="basicCurrent >= basicItems.length - 1"
            @click="basicCurrent++"
          >
            下一步
          </button>
        </div>
      </div>
    </div>

    <div class="demo-mode">
      <h1>错误状态</h1>
      <h2>步骤项设置 status="error" 标记异常步骤</h2>
      <div class="demo-surface">
        <PlatformSteps :current="errorCurrent" :items="errorItems" />
      </div>
    </div>

    <div class="demo-mode">
      <h1>垂直步骤条</h1>
      <h2>orientation="vertical" 适合详情页侧边流程展示</h2>
      <div class="demo-surface" style="max-width: 400px">
        <PlatformSteps
          :current="1"
          :items="verticalItems"
          orientation="vertical"
        />
      </div>
    </div>

    <div class="demo-mode">
      <h1>点状步骤条</h1>
      <h2>type="dot" 适合简洁进度展示</h2>
      <div class="demo-surface">
        <PlatformSteps :current="dotCurrent" :items="dotItems" type="dot" />
        <div style="margin-top: 16px; display: flex; gap: 8px">
          <button
            :disabled="dotCurrent <= 0"
            @click="dotCurrent--"
          >
            上一步
          </button>
          <button
            :disabled="dotCurrent >= dotItems.length - 1"
            @click="dotCurrent++"
          >
            下一步
          </button>
        </div>
      </div>
    </div>

    <div class="demo-mode">
      <h1>自定义图标</h1>
      <h2>步骤项通过 icon 指定 PlatformIcon 图标</h2>
      <div class="demo-surface">
        <PlatformSteps :current="1" :items="iconItems" />
      </div>
    </div>

    <div class="demo-mode">
      <h1>导航步骤条</h1>
      <h2>type="navigation" 适合多流程切换场景</h2>
      <div class="demo-surface">
        <PlatformSteps :current="navCurrent" :items="navItems" type="navigation" />
      </div>
    </div>

    <div class="demo-mode">
      <h1>面板步骤条</h1>
      <h2>type="panel" 适合向导式分步操作</h2>
      <div class="demo-surface">
        <PlatformSteps :current="1" :items="panelItems" type="panel" />
      </div>
    </div>

    <div class="demo-mode">
      <h1>小尺寸</h1>
      <h2>size="small" 适合弹窗、抽屉等紧凑空间</h2>
      <div class="demo-surface">
        <PlatformSteps :current="1" :items="smallItems" size="small" />
      </div>
    </div>

    <div class="demo-mode">
      <h1>可点击切换</h1>
      <h2>监听 change 事件实现步骤可点击切换</h2>
      <div class="demo-surface">
        <PlatformSteps
          :current="clickableCurrent"
          :items="clickableItems"
          @change="handleClickableChange"
        />
        <p style="margin-top: 12px; color: hsl(var(--muted-foreground)); font-size: 13px">
          当前步骤：第 {{ clickableCurrent + 1 }} 步
        </p>
      </div>
    </div>

    <div class="demo-mode">
      <h1>带描述信息</h1>
      <h2>步骤项通过 description 添加补充说明</h2>
      <div class="demo-surface">
        <PlatformSteps
          :current="1"
          :items="[
            { title: '选择项目', description: '选择需要操作的项目' },
            { title: '填写申请表', description: '完善申请信息' },
            { title: '提交审核', description: '等待管理员审核' },
          ]"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.demo-mode {
  display: grid;
  gap: 12px;
}

.demo-mode h1 {
  margin: 0;
  color: hsl(var(--foreground));
  font-size: 16px;
  font-weight: 700;
}

.demo-mode h2 {
  margin: 0;
  color: hsl(var(--muted-foreground));
  font-size: 13px;
  font-weight: 400;
  line-height: 1.6;
}

.demo-surface button {
  padding: 4px 12px;
  border: 1px solid hsl(var(--border));
  border-radius: 6px;
  background: hsl(var(--background));
  color: hsl(var(--foreground));
  font-size: 13px;
  cursor: pointer;
}

.demo-surface button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.demo-surface button:hover:not(:disabled) {
  border-color: hsl(var(--primary));
  color: hsl(var(--primary));
}
</style>
