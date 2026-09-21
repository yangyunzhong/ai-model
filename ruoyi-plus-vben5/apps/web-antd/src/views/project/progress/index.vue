<script setup lang="ts">
import type {
  ProjectProgressRecord,
  ProjectProgressWarning,
} from '#/mock/modules/project/progress';

import { onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  PlatformSectionTitle,
  type PlatformViewOption,
  PlatformViewToolbar,
} from '@st/platform-ui';
import { Empty } from 'antdv-next';

import ProjectProgressBoard from './components/project-progress-board.vue';
import ProjectProgressGanttChart from './components/project-progress-gantt-chart.vue';
import {
  getProjectProgressBoardList,
  getProjectProgressGanttList,
  getProjectProgressWarnings,
} from '#/mock/modules/project/progress';

type ProjectProgressView = 'board' | 'gantt';

const activeView = ref<ProjectProgressView>('gantt');
const boardRows = ref<ProjectProgressRecord[]>([]);
const ganttRows = ref<ProjectProgressRecord[]>([]);
const warnings = ref<ProjectProgressWarning[]>([]);
const loading = ref(false);
const viewOptions: PlatformViewOption[] = [
  {
    icon: 'icon-gantetu',
    label: '甘特图',
    value: 'gantt',
  },
  {
    icon: 'icon-kanban',
    label: '看板',
    value: 'board',
  },
];

onMounted(async () => {
  await loadProgress();
});

async function loadProgress() {
  loading.value = true;
  try {
    const [boardList, ganttList, warningList] = await Promise.all([
      getProjectProgressBoardList(),
      getProjectProgressGanttList(),
      getProjectProgressWarnings(),
    ]);
    boardRows.value = boardList;
    ganttRows.value = ganttList;
    warnings.value = warningList;
  } finally {
    loading.value = false;
  }
}

function setActiveView(view: ProjectProgressView) {
  activeView.value = view;
}

function handleViewChange(value: string) {
  setActiveView(value as ProjectProgressView);
}
</script>

<template>
  <Page auto-content-height>
    <div class="project-progress-page" :aria-busy="loading">
      <PlatformViewToolbar
        description="甘特图 / 看板 / 进度条"
        title="进度可视化跟踪"
        :view-options="viewOptions"
        :view-value="activeView"
        @update:view-value="handleViewChange"
      />

      <section v-if="activeView === 'gantt'" class="platform-surface">
        <ProjectProgressGanttChart :rows="ganttRows" :year="2026" />
      </section>

      <section v-else class="project-progress-board-section">
        <ProjectProgressBoard :rows="boardRows" />
      </section>

      <section v-if="activeView === 'gantt'" class="platform-surface">
        <PlatformSectionTitle
          class="project-progress-section-title"
          title="进度预警"
        />
        <ul v-if="warnings.length > 0" class="project-progress-warning-list">
          <li v-for="warning in warnings" :key="warning.id">
            <strong>{{ warning.name }}</strong>
            <span>{{ warning.reason }}</span>
          </li>
        </ul>
        <Empty
          v-else
          class="project-progress-empty"
          description="暂无进度滞后项目"
          :image="Empty.PRESENTED_IMAGE_SIMPLE"
        />
      </section>
    </div>
  </Page>
</template>

<style scoped>
.project-progress-page {
  display: flex;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
  min-height: 100%;
}

.project-progress-board-section {
  min-width: 0;
}

.project-progress-section-title {
  margin: 0 0 18px;
}

.project-progress-warning-list {
  padding: 0;
  margin: 0;
  list-style: none;
}

.project-progress-warning-list li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 0;
  border-top: 1px solid hsl(var(--st-color-border-subtle));
}

.project-progress-warning-list strong {
  color: hsl(var(--foreground));
}

.project-progress-warning-list span {
  color: hsl(var(--muted-foreground));
}

.project-progress-empty {
  padding: 10px 0;
  text-align: left;
}

</style>
