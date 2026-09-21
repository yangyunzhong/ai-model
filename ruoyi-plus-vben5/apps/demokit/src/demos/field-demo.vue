<script setup lang="ts">
import { reactive } from 'vue';

import {
  PlatformDatePicker,
  PlatformForm,
  PlatformFormItem,
  PlatformInput,
  PlatformRangePicker,
  PlatformSelect,
} from '@st/platform-ui';

const formState = reactive({
  owner: 'east',
  projectName: '华东治理专项',
});

const ownerOptions = [
  { label: '华东区域', value: 'east' },
  { label: '华北区域', value: 'north' },
  { label: '西南区域', value: 'west' },
];

const layoutState = reactive({
  horizontalDate: '',
  horizontalKeyword: '',
  horizontalOwner: 'east',
  horizontalPeriod: [],
  verticalDate: '',
  verticalKeyword: '',
  verticalOwner: 'north',
  verticalPeriod: [],
});

const props = defineProps<{
  demoId?: string;
}>();
</script>

<template>
  <div class="demo-flow">
    <div
      v-if="props.demoId === 'platform-form-item'"
      class="demo-surface demo-flow"
    >
      <h3 class="demo-title">FormItem 负责单个字段包装</h3>
      <p class="demo-muted">
        它解决的是标签、字段名、校验和字段间距，不负责整张表单的布局策略。
      </p>
      <PlatformForm
        :model="formState"
        label-preset="inline-compact"
        layout="horizontal"
      >
        <PlatformFormItem label="项目名称" name="projectName">
          <PlatformInput
            v-model:value="formState.projectName"
            allow-clear
            placeholder="请输入项目名称"
          />
        </PlatformFormItem>
      </PlatformForm>
    </div>

    <div
      v-if="props.demoId === 'platform-form'"
      class="demo-surface demo-flow"
    >
      <h3 class="demo-title">Form 负责整张表单容器</h3>
      <p class="demo-muted">
        它决定布局、label 预设、model 和整体验证暴露；具体字段仍然通过 FormItem 组织。
      </p>
      <PlatformForm
        :model="formState"
        label-preset="inline-compact"
        layout="horizontal"
      >
        <PlatformFormItem label="项目名称" name="projectName">
          <PlatformInput
            v-model:value="formState.projectName"
            allow-clear
            placeholder="请输入项目名称"
          />
        </PlatformFormItem>
        <PlatformFormItem label="所属区域" name="owner">
          <PlatformSelect
            v-model:value="formState.owner"
            :options="ownerOptions"
            placeholder="请选择区域"
          />
        </PlatformFormItem>

        <PlatformFormItem label="计划日期">
          <PlatformDatePicker placeholder="请选择日期" />
        </PlatformFormItem>

        <PlatformFormItem label="执行周期">
          <PlatformRangePicker :placeholder="['开始日期', '结束日期']" />
        </PlatformFormItem>
      </PlatformForm>
    </div>

    <div
      v-if="props.demoId !== 'platform-form' && props.demoId !== 'platform-form-item'"
      class="demo-grid-2"
    >
      <div
        v-if="props.demoId === 'platform-input'"
        class="demo-surface demo-flow"
      >
        <h3 class="demo-title">Input 只演示基础输入能力</h3>
        <p class="demo-muted">适合关键词、名称、备注等单值输入，不承担表单布局职责。</p>
        <div class="demo-example-list">
          <section class="demo-example-card">
            <header class="demo-example-card__header">
              <strong>案例 1：关键词搜索</strong>
              <span>单值输入 / 带清空</span>
            </header>
            <PlatformInput allow-clear placeholder="输入关键词" />
          </section>

          <section class="demo-example-card">
            <header class="demo-example-card__header">
              <strong>案例 2：项目名称</strong>
              <span>单值输入 / 业务命名</span>
            </header>
            <PlatformInput placeholder="请输入项目名称" />
          </section>
        </div>

        <div class="demo-flow">
          <h4 class="demo-subtitle">系统平台里的两种布局方案</h4>
          <div class="demo-grid-2">
            <section class="demo-example-card">
              <header class="demo-example-card__header">
                <strong>方案 A：上下布局</strong>
                <span>适合详情维护、字段说明较长的表单</span>
              </header>
              <PlatformForm :model="layoutState" layout="vertical">
                <PlatformFormItem label="项目关键词" name="verticalKeyword">
                  <PlatformInput
                    v-model:value="layoutState.verticalKeyword"
                    allow-clear
                    placeholder="请输入项目关键词"
                  />
                </PlatformFormItem>
              </PlatformForm>
            </section>

            <section class="demo-example-card">
              <header class="demo-example-card__header">
                <strong>方案 B：左右布局</strong>
                <span>适合后台查询区、紧凑编辑区</span>
              </header>
              <PlatformForm
                :model="layoutState"
                label-preset="inline-compact"
                layout="horizontal"
              >
                <PlatformFormItem label="项目关键词" name="horizontalKeyword">
                  <PlatformInput
                    v-model:value="layoutState.horizontalKeyword"
                    allow-clear
                    placeholder="请输入项目关键词"
                  />
                </PlatformFormItem>
              </PlatformForm>
            </section>
          </div>
        </div>
      </div>

      <div
        v-if="props.demoId === 'platform-select'"
        class="demo-surface demo-flow"
      >
        <h3 class="demo-title">Select 只演示基础选择能力</h3>
        <p class="demo-muted">适合状态、区域、分类等有限选项选择，不负责搜索表单动作区。</p>
        <div class="demo-example-list">
          <section class="demo-example-card">
            <header class="demo-example-card__header">
              <strong>案例 1：快速选择</strong>
              <span>短文案选项 / 紧凑宽度</span>
            </header>
            <PlatformSelect
              :options="ownerOptions"
              placeholder="快速选择"
              width="180px"
            />
          </section>

          <section class="demo-example-card">
            <header class="demo-example-card__header">
              <strong>案例 2：所属区域</strong>
              <span>业务字段 / 常规宽度</span>
            </header>
            <PlatformSelect
              :options="ownerOptions"
              placeholder="请选择所属区域"
              width="220px"
            />
          </section>
        </div>

        <div class="demo-flow">
          <h4 class="demo-subtitle">系统平台里的两种布局方案</h4>
          <div class="demo-grid-2">
            <section class="demo-example-card">
              <header class="demo-example-card__header">
                <strong>方案 A：上下布局</strong>
                <span>适合资料录入、配置页表单</span>
              </header>
              <PlatformForm :model="layoutState" layout="vertical">
                <PlatformFormItem label="所属区域" name="verticalOwner">
                  <PlatformSelect
                    v-model:value="layoutState.verticalOwner"
                    :options="ownerOptions"
                    placeholder="请选择所属区域"
                  />
                </PlatformFormItem>
              </PlatformForm>
            </section>

            <section class="demo-example-card">
              <header class="demo-example-card__header">
                <strong>方案 B：左右布局</strong>
                <span>适合查询区、行内编辑表单</span>
              </header>
              <PlatformForm
                :model="layoutState"
                label-preset="inline-compact"
                layout="horizontal"
              >
                <PlatformFormItem label="所属区域" name="horizontalOwner">
                  <PlatformSelect
                    v-model:value="layoutState.horizontalOwner"
                    :options="ownerOptions"
                    placeholder="请选择所属区域"
                  />
                </PlatformFormItem>
              </PlatformForm>
            </section>
          </div>
        </div>
      </div>

      <div
        v-if="props.demoId === 'platform-date-picker'"
        class="demo-surface demo-flow"
      >
        <h3 class="demo-title">DatePicker 只演示单日期输入</h3>
        <p class="demo-muted">适合节点日期、生效日期、计划时间等单日期场景。</p>
        <div class="demo-example-list">
          <section class="demo-example-card">
            <header class="demo-example-card__header">
              <strong>案例 1：单日期</strong>
              <span>基础日期输入</span>
            </header>
            <PlatformDatePicker placeholder="单日期" />
          </section>

          <section class="demo-example-card">
            <header class="demo-example-card__header">
              <strong>案例 2：计划日期</strong>
              <span>业务字段 / 日期选择</span>
            </header>
            <PlatformDatePicker placeholder="请选择计划日期" />
          </section>
        </div>

        <div class="demo-flow">
          <h4 class="demo-subtitle">系统平台里的两种布局方案</h4>
          <div class="demo-grid-2">
            <section class="demo-example-card">
              <header class="demo-example-card__header">
                <strong>方案 A：上下布局</strong>
                <span>适合节点配置、排期维护</span>
              </header>
              <PlatformForm :model="layoutState" layout="vertical">
                <PlatformFormItem label="计划日期" name="verticalDate">
                  <PlatformDatePicker
                    v-model:value="layoutState.verticalDate"
                    placeholder="请选择计划日期"
                  />
                </PlatformFormItem>
              </PlatformForm>
            </section>

            <section class="demo-example-card">
              <header class="demo-example-card__header">
                <strong>方案 B：左右布局</strong>
                <span>适合后台查询区、紧凑编辑表单</span>
              </header>
              <PlatformForm
                :model="layoutState"
                label-preset="inline-compact"
                layout="horizontal"
              >
                <PlatformFormItem label="计划日期" name="horizontalDate">
                  <PlatformDatePicker
                    v-model:value="layoutState.horizontalDate"
                    placeholder="请选择计划日期"
                  />
                </PlatformFormItem>
              </PlatformForm>
            </section>
          </div>
        </div>
      </div>

      <div
        v-if="props.demoId === 'platform-range-picker'"
        class="demo-surface demo-flow"
      >
        <h3 class="demo-title">RangePicker 只演示日期区间</h3>
        <p class="demo-muted">适合统计区间、执行周期和起止时间筛选。</p>
        <div class="demo-example-list">
          <section class="demo-example-card">
            <header class="demo-example-card__header">
              <strong>案例 1：默认区间</strong>
              <span>统计时间 / 范围筛选</span>
            </header>
            <PlatformRangePicker />
          </section>

          <section class="demo-example-card">
            <header class="demo-example-card__header">
              <strong>案例 2：执行周期</strong>
              <span>业务字段 / 起止时间</span>
            </header>
            <PlatformRangePicker :placeholder="['开始日期', '结束日期']" />
          </section>
        </div>

        <div class="demo-flow">
          <h4 class="demo-subtitle">系统平台里的两种布局方案</h4>
          <div class="demo-grid-2">
            <section class="demo-example-card">
              <header class="demo-example-card__header">
                <strong>方案 A：上下布局</strong>
                <span>适合周期配置、完整信息录入</span>
              </header>
              <PlatformForm :model="layoutState" layout="vertical">
                <PlatformFormItem label="执行周期" name="verticalPeriod">
                  <PlatformRangePicker
                    v-model:value="layoutState.verticalPeriod"
                    :placeholder="['开始日期', '结束日期']"
                  />
                </PlatformFormItem>
              </PlatformForm>
            </section>

            <section class="demo-example-card">
              <header class="demo-example-card__header">
                <strong>方案 B：左右布局</strong>
                <span>适合查询区、批量筛选区</span>
              </header>
              <PlatformForm
                :model="layoutState"
                label-preset="inline-compact"
                layout="horizontal"
              >
                <PlatformFormItem label="执行周期" name="horizontalPeriod">
                  <PlatformRangePicker
                    v-model:value="layoutState.horizontalPeriod"
                    :placeholder="['开始日期', '结束日期']"
                  />
                </PlatformFormItem>
              </PlatformForm>
            </section>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
