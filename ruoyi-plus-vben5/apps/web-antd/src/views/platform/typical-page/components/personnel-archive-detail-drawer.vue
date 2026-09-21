<script setup lang="ts">
import type { PersonnelArchiveRecord } from '#/mock/modules/platform/typical-page';

import { computed } from 'vue';

import { VbenIcon } from '@vben/icons';

import { PlatformDrawer, PlatformStatusTag } from '@st/platform-ui';

import {
  getPersonnelArchiveBlacklistMeta,
  getPersonnelArchiveEmploymentMeta,
  getPersonnelArchiveHealthMeta,
  getPersonnelArchiveQualificationMeta,
  getPersonnelArchiveSectionValue,
  maskIdCard,
} from '#/mock/modules/platform/typical-page';

const props = defineProps<{
  record: null | PersonnelArchiveRecord;
}>();
const open = defineModel<boolean>('open', {
  default: false,
});

const employmentMeta = computed(() =>
  props.record ? getPersonnelArchiveEmploymentMeta(props.record.status) : null,
);
const healthMeta = computed(() =>
  props.record ? getPersonnelArchiveHealthMeta(props.record.healthStatus) : null,
);
const blacklistMeta = computed(() =>
  props.record
    ? getPersonnelArchiveBlacklistMeta(props.record.blacklistStatus)
    : null,
);
const qualificationMeta = computed(() =>
  props.record
    ? getPersonnelArchiveQualificationMeta(props.record.qualificationStatus)
    : null,
);
const isHoursOverLimit = computed(
  () =>
    Boolean(props.record) &&
    props.record!.monthlyHours > props.record!.monthlyHoursLimit,
);

function formatHours(value: number, limit: number) {
  return value > limit ? `${value}h（超时!）` : `${value}h`;
}
</script>

<template>
  <PlatformDrawer
    v-model:open="open"
    :destroy-on-close="true"
    :width="760"
    class="personnel-archive-detail-drawer"
    placement="right"
    title="详情"
  >
    <template v-if="record">
      <div class="personnel-archive-detail">
        <header class="personnel-archive-detail__header">
          <div class="personnel-archive-detail__identity">
            <span
              class="personnel-archive-detail__avatar"
              :style="{ backgroundColor: record.theme }"
            >
              {{ record.name.slice(0, 1) }}
            </span>

            <div class="personnel-archive-detail__headline">
              <h2>{{ record.name }}</h2>
              <p>{{ record.archiveNo }} · {{ record.contractor }}</p>
            </div>
          </div>

          <PlatformStatusTag
            :label="employmentMeta?.label"
            :status="employmentMeta?.status"
          />
        </header>

        <section class="personnel-archive-detail__section">
          <h3>
            <VbenIcon icon="lucide:user" />
            <span>基础信息</span>
          </h3>
          <div class="personnel-archive-detail__grid">
            <div class="personnel-archive-detail__item">
              <span>性别</span>
              <strong>{{ record.gender }}</strong>
            </div>
            <div class="personnel-archive-detail__item">
              <span>年龄</span>
              <strong>{{ record.age }}岁</strong>
            </div>
            <div class="personnel-archive-detail__item">
              <span>联系电话</span>
              <strong>{{ record.phone }}</strong>
            </div>
            <div class="personnel-archive-detail__item">
              <span>身份证号</span>
              <strong>{{ maskIdCard(record.idCard) }}</strong>
            </div>
            <div class="personnel-archive-detail__item">
              <span>身体状况</span>
              <strong>
                <PlatformStatusTag
                  :label="healthMeta?.label"
                  :status="healthMeta?.tone === 'danger' ? 'error' : healthMeta?.tone === 'warning' ? 'warning' : 'success'"
                />
              </strong>
            </div>
            <div class="personnel-archive-detail__item">
              <span>黑名单</span>
              <strong>
                <PlatformStatusTag
                  :label="blacklistMeta?.label"
                  :status="blacklistMeta?.status"
                />
              </strong>
            </div>
          </div>
        </section>

        <section class="personnel-archive-detail__section">
          <h3>
            <VbenIcon icon="lucide:building-2" />
            <span>合同与项目</span>
          </h3>
          <div class="personnel-archive-detail__grid">
            <div class="personnel-archive-detail__item">
              <span>所属承包商</span>
              <strong>{{ record.contractor }}</strong>
            </div>
            <div class="personnel-archive-detail__item">
              <span>岗位</span>
              <strong>{{ record.position }}</strong>
            </div>
            <div class="personnel-archive-detail__item">
              <span>所属项目</span>
              <strong>{{ record.project }}</strong>
            </div>
            <div class="personnel-archive-detail__item">
              <span>入职日期</span>
              <strong>{{ record.startDate }}</strong>
            </div>
            <div class="personnel-archive-detail__item">
              <span>离职日期</span>
              <strong>{{ getPersonnelArchiveSectionValue(record.endDate) }}</strong>
            </div>
          </div>
        </section>

        <section class="personnel-archive-detail__section">
          <h3>
            <VbenIcon icon="lucide:badge-check" />
            <span>资质信息</span>
          </h3>

          <article class="personnel-archive-detail__qualification">
            <div class="personnel-archive-detail__qualification-icon">
              <VbenIcon icon="lucide:award" />
            </div>

            <div class="personnel-archive-detail__qualification-content">
              <strong>{{ record.qualificationName }}</strong>
              <p>到期日：{{ record.qualificationExpireDate || '未填写' }}</p>
            </div>

            <PlatformStatusTag
              :label="qualificationMeta?.label"
              :status="
                qualificationMeta?.tone === 'danger'
                  ? 'error'
                  : qualificationMeta?.tone === 'warning'
                    ? 'warning'
                    : qualificationMeta?.tone === 'info'
                      ? 'default'
                      : 'success'
              "
            />
          </article>
        </section>

        <section class="personnel-archive-detail__section">
          <h3>
            <VbenIcon icon="lucide:clock-3" />
            <span>工时信息</span>
          </h3>
          <div class="personnel-archive-detail__grid">
            <div class="personnel-archive-detail__item">
              <span>本月工时</span>
              <strong class="personnel-archive-detail__hours" :class="{ 'personnel-archive-detail__hours--danger': isHoursOverLimit }">
                {{
                  formatHours(
                    record.monthlyHours,
                    record.monthlyHoursLimit,
                  )
                }}
              </strong>
            </div>
            <div class="personnel-archive-detail__item">
              <span>工时上限</span>
              <strong>{{ record.monthlyHoursLimit }}h/月</strong>
            </div>
          </div>
        </section>
      </div>
    </template>
  </PlatformDrawer>
</template>

<style scoped>
.personnel-archive-detail {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.personnel-archive-detail__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.personnel-archive-detail__identity {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 0;
}

.personnel-archive-detail__avatar {
  display: inline-flex;
  width: 88px;
  height: 88px;
  flex: 0 0 88px;
  align-items: center;
  justify-content: center;
  color: hsl(var(--primary-foreground));
  border-radius: 18px;
  font-size: 42px;
  font-weight: 700;
}

.personnel-archive-detail__headline {
  min-width: 0;
}

.personnel-archive-detail__headline h2 {
  margin: 0;
  color: hsl(var(--foreground));
  font-size: 30px;
  font-weight: 800;
  line-height: 38px;
}

.personnel-archive-detail__headline p {
  margin: 6px 0 0;
  color: hsl(var(--muted-foreground));
  font-size: 15px;
  line-height: 24px;
}

.personnel-archive-detail__section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.personnel-archive-detail__section h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  padding-bottom: 12px;
  color: hsl(var(--primary));
  border-bottom: 1px solid hsl(var(--border));
  font-size: 18px;
  font-weight: 700;
}

.personnel-archive-detail__section h3 :deep(svg) {
  width: 1em;
  height: 1em;
}

.personnel-archive-detail__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 24px 48px;
}

.personnel-archive-detail__item {
  min-width: 0;
}

.personnel-archive-detail__item span {
  display: block;
  margin-bottom: 6px;
  color: hsl(var(--muted-foreground));
  font-size: 15px;
  line-height: 22px;
}

.personnel-archive-detail__item strong {
  color: hsl(var(--foreground));
  font-size: 17px;
  font-weight: 700;
  line-height: 24px;
}

.personnel-archive-detail__item strong :deep(.ant-tag) {
  margin-right: 0;
}

.personnel-archive-detail__qualification {
  display: grid;
  grid-template-columns: 46px minmax(0, 1fr) auto;
  gap: 14px;
  align-items: center;
  padding: 16px 18px;
  background: hsl(var(--background));
  border: 1px solid hsl(var(--border));
  border-radius: var(--st-radius-card);
  box-shadow: 0 6px 20px rgb(15 23 42 / 4%);
}

.personnel-archive-detail__qualification-icon {
  display: inline-flex;
  width: 46px;
  height: 46px;
  align-items: center;
  justify-content: center;
  color: hsl(var(--warning));
  background: color-mix(in srgb, hsl(var(--warning)) 12%, transparent);
  border-radius: 12px;
  font-size: 22px;
}

.personnel-archive-detail__qualification-icon :deep(svg) {
  width: 1em;
  height: 1em;
}

.personnel-archive-detail__qualification-content {
  min-width: 0;
}

.personnel-archive-detail__qualification-content strong {
  display: block;
  color: hsl(var(--foreground));
  font-size: 17px;
  font-weight: 700;
  line-height: 24px;
}

.personnel-archive-detail__qualification-content p {
  margin: 4px 0 0;
  color: hsl(var(--muted-foreground));
  font-size: 14px;
  line-height: 22px;
}

.personnel-archive-detail__hours {
  color: hsl(var(--foreground));
}

.personnel-archive-detail__hours--danger {
  color: hsl(var(--destructive));
}

@media (max-width: 900px) {
  .personnel-archive-detail__header {
    flex-direction: column;
    align-items: flex-start;
  }

  .personnel-archive-detail__grid,
  .personnel-archive-detail__qualification {
    grid-template-columns: 1fr;
  }
}
</style>
