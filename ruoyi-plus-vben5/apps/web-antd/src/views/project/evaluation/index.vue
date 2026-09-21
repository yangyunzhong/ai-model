<script setup lang="ts">
import type { TableEmits, TableProps } from 'antdv-next';

import type {
  EvaluationProject,
  EvaluationRecord,
  EvaluationStatCard,
} from '#/mock/modules/project/evaluation';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  PlatformEditForm,
  PlatformFormItem,
  PlatformInput,
  PlatformModal,
  PlatformSectionTitle,
  PlatformSelect,
  PlatformStatCard,
  PlatformStatusTag,
  PlatformTable,
  PlatformTaskCard,
  PlatformViewToolbar,
} from '@st/platform-ui';

import {
  evaluationProjectStageMap,
  evaluationProjectStatusMap,
  evaluationRecordResultMap,
  getEvaluationProjects,
  getEvaluationRecords,
  getEvaluationStats,
  submitEvaluation,
} from '#/mock/modules/project/evaluation';

interface EvaluationFormModel {
  evaluator: string;
  projectId?: number;
  remark: string;
  score: number;
}

const formModel = reactive<EvaluationFormModel>({
  evaluator: '项目管理部',
  projectId: undefined,
  remark: '',
  score: 90,
});
const evaluationProjects = ref<EvaluationProject[]>([]);
const evaluationRecords = ref<EvaluationRecord[]>([]);
const statCards = ref<EvaluationStatCard[]>([]);
const loading = ref(false);
const saving = ref(false);
const formOpen = ref(false);
const recordQuery = reactive({
  result: '',
});

const projectOptions = computed(() =>
  evaluationProjects.value.map((item) => ({
    label: item.name,
    value: item.id,
  })),
);
const recordResultOptions = computed(() => [
  { label: '全部结果', value: '' },
  ...Object.entries(evaluationRecordResultMap).map(([value, meta]) => ({
    label: meta.label,
    value,
  })),
]);
const currentProject = computed(() =>
  evaluationProjects.value.find((item) => item.id === formModel.projectId),
);
const filteredEvaluationRecords = computed(() =>
  evaluationRecords.value.filter((record) => {
    const matchResult = !recordQuery.result || record.result === recordQuery.result;

    return matchResult;
  }),
);
const recordColumns = computed<TableProps['columns']>(() => [
  {
    dataIndex: 'name',
    key: 'name',
    title: '项目名称',
    width: 260,
  },
  {
    dataIndex: 'result',
    filterMultiple: false,
    filteredValue: recordQuery.result ? [recordQuery.result] : null,
    filters: recordResultOptions.value
      .filter((item) => item.value)
      .map((item) => ({ text: item.label, value: item.value })),
    key: 'result',
    title: '评估结果',
    width: 110,
  },
  {
    dataIndex: 'date',
    key: 'date',
    title: '评估日期',
    width: 130,
  },
  {
    dataIndex: 'evaluator',
    key: 'evaluator',
    title: '评估组',
    width: 140,
  },
  {
    dataIndex: 'score',
    key: 'score',
    title: '得分',
    width: 96,
  },
]);
const recordPagination = computed(() => ({
  pageSize: 6,
  showTotal: (total: number) => `共 ${total} 条`,
  total: filteredEvaluationRecords.value.length,
}));

onMounted(loadEvaluationPage);

async function loadEvaluationPage() {
  loading.value = true;
  try {
    const [projects, records, stats] = await Promise.all([
      getEvaluationProjects(),
      getEvaluationRecords(),
      getEvaluationStats(),
    ]);
    evaluationProjects.value = projects;
    evaluationRecords.value = records;
    statCards.value = stats;
  } finally {
    loading.value = false;
  }
}

function handleRecordTableChange(...args: Parameters<TableEmits['change']>) {
  const [, filters] = args;
  recordQuery.result = getFirstFilterValue(
    filters.result,
  ) as '' | EvaluationRecord['result'];
}

function handleCreateEvaluation(project?: EvaluationProject) {
  Object.assign(formModel, {
    evaluator: '项目管理部',
    projectId: project?.id ?? evaluationProjects.value[0]?.id,
    remark: '',
    score: 90,
  });
  formOpen.value = true;
}

async function handleSubmitEvaluation() {
  if (!formModel.projectId) {
    window.message.warning('请选择待评估项目。');
    return;
  }

  saving.value = true;
  try {
    await submitEvaluation({
      evaluator: formModel.evaluator,
      projectId: formModel.projectId,
      remark: formModel.remark,
      score: Number(formModel.score) || 0,
    });
    window.message.success('评估记录已生成');
    formOpen.value = false;
    await loadEvaluationPage();
  } finally {
    saving.value = false;
  }
}

function getScoreClass(score: number) {
  return score >= 90
    ? 'project-evaluation-score--success'
    : 'project-evaluation-score--warning';
}

function getFirstFilterValue(value: unknown) {
  if (Array.isArray(value)) {
    return String(value[0] ?? '');
  }
  return '';
}

function getEvaluationRecord(record: unknown) {
  return record as EvaluationRecord;
}

function getRecordResultMeta(record: unknown) {
  return evaluationRecordResultMap[getEvaluationRecord(record).result];
}

function getRecordScore(record: unknown) {
  return getEvaluationRecord(record).score;
}

function getProjectTags(project: EvaluationProject) {
  return [
    evaluationProjectStageMap[project.stage],
    evaluationProjectStatusMap[project.status],
  ];
}
</script>

<template>
  <Page :auto-content-height="true">
    <div class="project-evaluation-page">
      <PlatformViewToolbar
        description="评估模板、验收流程数字化留痕"
        title="中期评估与验收管理"
      />

      <section class="project-evaluation-stat-grid">
        <PlatformStatCard
          v-for="card in statCards"
          :key="card.title"
          :loading="loading"
          v-bind="card"
        />
      </section>

      <section class="project-evaluation-workspace">
        <article class="platform-surface project-evaluation-panel">
          <PlatformSectionTitle
            padding="0 0 24px"
            title="待评估项目"
          />

          <div class="project-evaluation-panel__body project-evaluation-panel__body--cards">
            <PlatformTaskCard
              v-for="project in evaluationProjects"
              :key="project.id"
              action-label="发起评估"
              :description="`${project.department} · ${project.manager}`"
              :meta="`截止 ${project.dueDate}`"
              :progress="project.progress"
              :progress-label="`进度 ${project.progress}%`"
              :tags="getProjectTags(project)"
              :title="project.name"
              @action="handleCreateEvaluation(project)"
            />
            <div
              v-if="evaluationProjects.length === 0"
              class="project-evaluation-empty"
            >
              暂无符合条件的待评估项目
            </div>
          </div>
        </article>

        <article class="platform-surface project-evaluation-panel project-evaluation-panel--records">
          <PlatformSectionTitle
            padding="0 0 24px"
            title="评估记录"
          />

          <PlatformTable
            :adaptive-height-bottom-offset="48"
            column-setting-key="project-evaluation-records"
            :columns="recordColumns"
            :data-source="filteredEvaluationRecords"
            :loading="loading"
            :pagination="recordPagination"
            row-key="id"
            show-index
            size="middle"
            @change="handleRecordTableChange"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'name'">
                <div class="project-record-name">
                  <strong>{{ record.name }}</strong>
                  <span>{{ record.evaluator }}</span>
                </div>
              </template>
              <template v-else-if="column.key === 'result'">
                <PlatformStatusTag
                  :label="getRecordResultMeta(record).label"
                  :status="getRecordResultMeta(record).status"
                />
              </template>
              <template v-else-if="column.key === 'score'">
                <strong
                  class="project-evaluation-score"
                  :class="getScoreClass(getRecordScore(record))"
                >
                  {{ getRecordScore(record) }}分
                </strong>
              </template>
            </template>
          </PlatformTable>
        </article>
      </section>
    </div>

    <PlatformModal
      v-model:open="formOpen"
      :confirm-loading="saving"
      destroy-on-hidden
      title="发起评估"
      width="720px"
      @cancel="formOpen = false"
      @ok="handleSubmitEvaluation"
    >
      <PlatformEditForm :model="formModel" layout="vertical">
        <div class="project-evaluation-form-grid">
          <PlatformFormItem label="待评估项目">
            <PlatformSelect
              v-model:value="formModel.projectId"
              :options="projectOptions"
              placeholder="请选择待评估项目"
            />
          </PlatformFormItem>
          <PlatformFormItem label="评估人 / 评估组">
            <PlatformInput
              v-model:value="formModel.evaluator"
              placeholder="请输入评估人或评估组"
            />
          </PlatformFormItem>
          <PlatformFormItem label="评估得分">
            <PlatformInput
              v-model:value="formModel.score"
              placeholder="请输入评估得分"
              type="number"
            />
          </PlatformFormItem>
          <PlatformFormItem label="当前进度">
            <PlatformInput
              :value="
                currentProject ? `${currentProject.progress}%` : '未选择项目'
              "
              disabled
            />
          </PlatformFormItem>
        </div>
        <PlatformFormItem label="评估说明">
          <PlatformInput
            v-model:value="formModel.remark"
            placeholder="记录本次中期评估或验收意见"
          />
        </PlatformFormItem>
      </PlatformEditForm>
    </PlatformModal>
  </Page>
</template>

<style scoped>
.project-evaluation-page {
  --project-evaluation-pending-panel-width: 440px;
  --project-evaluation-panel-bottom-space: 24px;

  display: flex;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
  height: 100%;
  min-height: 0;
}

.project-evaluation-stat-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: var(--st-layout-section-gap);
}

.project-evaluation-workspace {
  display: grid;
  align-items: stretch;
  flex: 1;
  grid-template-columns: var(--project-evaluation-pending-panel-width) minmax(0, 1fr);
  gap: var(--st-layout-section-gap);
  min-height: 0;
}

.project-evaluation-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-width: 0;
  min-height: 0;
  overflow: hidden;
}

.project-evaluation-panel__body {
  display: flex;
  flex-direction: column;
  flex: 1;
  gap: 12px;
  min-height: 0;
  overflow: auto;
}

.project-evaluation-panel__body--cards {
  gap: 16px;
  padding-bottom: var(--project-evaluation-panel-bottom-space);
}

.project-evaluation-panel__body--cards :deep(.platform-task-card) {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  column-gap: 24px;
  row-gap: 8px;
}

.project-evaluation-panel__body--cards :deep(.platform-task-card__hero),
.project-evaluation-panel__body--cards :deep(.platform-task-card__headline) {
  display: contents;
}

.project-evaluation-panel__body--cards :deep(.platform-task-card__headline h3) {
  grid-column: 1 / -1;
}

.project-evaluation-panel__body--cards :deep(.platform-task-card__headline p) {
  grid-column: 1;
  margin-top: 0;
}

.project-evaluation-panel__body--cards :deep(.platform-task-card__hero > .platform-button) {
  grid-column: 2;
  grid-row: 2 / span 2;
  align-self: center;
  margin-top: 0;
}

.project-evaluation-panel__body--cards :deep(.platform-task-card__tags) {
  grid-column: 1;
  margin-bottom: 0;
}

.project-evaluation-panel__body--cards :deep(.platform-task-card__meta),
.project-evaluation-panel__body--cards :deep(.platform-task-card__progress-row) {
  grid-column: 1 / -1;
}

.project-evaluation-panel--records {
  align-self: start;
}

.project-record-name {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.project-record-name span {
  color: hsl(var(--muted-foreground));
  font-size: var(--st-font-size-sm);
}

.project-evaluation-score {
  flex: 0 0 auto;
  font-size: var(--st-font-size-base);
  line-height: 24px;
}

.project-evaluation-score--success {
  color: hsl(var(--st-color-brand));
}

.project-evaluation-score--warning {
  color: hsl(var(--st-color-warning-action));
}

.project-evaluation-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 120px;
  color: hsl(var(--muted-foreground));
  border: 1px dashed hsl(var(--border));
  border-radius: var(--st-radius-card);
}

.project-evaluation-form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 16px;
}

@media (max-width: 1200px) {
  .project-evaluation-workspace {
    grid-template-columns: var(--project-evaluation-pending-panel-width) minmax(0, 1fr);
  }
}

@media (max-width: 960px) {
  .project-evaluation-page {
    height: auto;
    min-height: 100%;
  }

  .project-evaluation-stat-grid,
  .project-evaluation-workspace,
  .project-evaluation-form-grid {
    grid-template-columns: 1fr;
  }

  .project-evaluation-workspace {
    flex: none;
  }

  .project-evaluation-panel {
    max-height: none;
  }
}
</style>
