<script setup lang="ts">
import type { PlatformNoticeListItem } from '@st/platform-ui';

import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue';
import type { ComponentPublicInstance } from 'vue';
import { useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';
import { useUserStore } from '@vben/stores';

import { Avatar, Popover } from 'antdv-next';

import {
  PlatformButton,
  PlatformEntryCard,
  PlatformIcon,
  PlatformNoticeList,
  PlatformSection,
  PlatformSectionAction,
  PlatformSegmented,
} from '@st/platform-ui';
import { preferences } from '@vben/preferences';

type WeekDayItem = {
  date: string;
  isWeekend?: boolean;
  key: string;
  label: string;
};

type ScheduleItem = {
  title: string;
  time: string;
};

type AccessEntry = {
  icon: string;
  color: string;
  key: string;
  label: string;
  permissions?: string[];
  roles?: string[];
  url?: string;
};

type AppEntry = {
  category: string;
  color: string;
  icon: string;
  key: string;
  label: string;
  permissions?: string[];
  roles?: string[];
  url?: string;
};

type TodoGroup = {
  count: number;
  key: string;
  label: string;
};

type TodoItem = PlatformNoticeListItem & {
  description: string;
  group: string;
  id: number;
  title: string;
};

const router = useRouter();
const userStore = useUserStore();

const activeWeekDay = ref('tue');
const activeTodoGroup = ref('all');
const activeAppCategory = ref('production');
const todoFilterViewportRef = ref<HTMLElement>();

const todoFilterButtonRefs = new Map<string, HTMLButtonElement>();
const overflowTodoGroupKeys = ref<string[]>([]);

let todoFilterResizeObserver: ResizeObserver | null = null;

const weekDays: WeekDayItem[] = [
  { key: 'mon', label: '一', date: '6' },
  { key: 'tue', label: '二', date: '7' },
  { key: 'wed', label: '三', date: '8' },
  { key: 'thu', label: '四', date: '9' },
  { key: 'fri', label: '五', date: '10' },
  { key: 'sat', label: '六', date: '11', isWeekend: true },
  { key: 'sun', label: '日', date: '12', isWeekend: true },
];

const scheduleMap: Record<string, ScheduleItem[]> = {
  mon: [
    { time: '09:00 - 10:00', title: '周例会' },
    { time: '15:30 - 17:00', title: '项目对齐' },
  ],
  tue: [
    { time: '14:00 - 16:00', title: 'XXXXXX项目组例会' },
    { time: '14:00 - 16:00', title: 'XXXXXX项目组第一次研讨会' },
  ],
  wed: [
    { time: '10:00 - 11:30', title: '里程碑复盘' },
    { time: '16:00 - 17:30', title: '需求评审' },
  ],
  thu: [
    { time: '09:30 - 10:30', title: '待办同步' },
    { time: '15:00 - 16:30', title: '方案评审' },
  ],
  fri: [
    { time: '11:00 - 12:00', title: '发布确认' },
    { time: '14:30 - 16:00', title: '客户演示' },
  ],
  sat: [
    { time: '10:00 - 11:00', title: '值班安排' },
    { time: '15:00 - 16:00', title: '巡检复核' },
  ],
  sun: [
    { time: '09:00 - 10:00', title: '周报整理' },
    { time: '16:30 - 17:30', title: '下周计划' },
  ],
};

const recentVisits = [
  { key: 'duty', label: '值班管理', url: '/project/progress' },
  { key: 'log', label: '运行日志', url: '/monitor/operlog' },
  { key: 'user', label: '用户管理', url: '/system/user' },
];

const accessEntries: AccessEntry[] = [
  { color: '#10b981', icon: 'lucide:corner-down-left', key: '3d', label: '三维数字化', url: '/project/progress' },
  { color: '#6366f1', icon: 'lucide:users', key: 'meeting', label: '会议管理', permissions: ['*:*:*'], url: '/workflow/task/allTaskWaiting' },
  { color: '#f59e0b', icon: 'lucide:settings-2', key: 'log', label: '运行日志', permissions: ['*:*:*'], url: '/monitor/operlog' },
  { color: '#ef4444', icon: 'lucide:flag', key: 'command', label: '应急指挥', url: '/project/evaluation' },
  { color: '#3b82f6', icon: 'lucide:link-2', key: 'defect', label: '缺陷管理', url: '/project/document' },
  { color: '#8b5cf6', icon: 'lucide:sliders-horizontal', key: 'setting', label: '设置', permissions: ['*:*:*'], url: '/system/config' },
];

const appCategories = [
  { label: '生产', value: 'production' },
  { label: '安全', value: 'safety' },
  { label: '经营', value: 'operation' },
  { label: '办公', value: 'office' },
  { label: '党建', value: 'party' },
  { label: '三维', value: 'three-d' },
];

const appEntries: AppEntry[] = [
  { category: 'production', color: '#16a34a', icon: 'lucide:activity', key: 'run', label: '运行管理', url: '/project/progress' },
  { category: 'production', color: '#84cc16', icon: 'lucide:ticket', key: 'two-ticket', label: '两票管理', url: '/workflow/task/allTaskWaiting' },
  { category: 'production', color: '#f59e0b', icon: 'lucide:users', key: 'team', label: '班组管理', url: '/personnel/overview' },
  { category: 'production', color: '#ea580c', icon: 'lucide:cpu', key: 'device', label: '设备管理', url: '/system/dept' },
  { category: 'production', color: '#3b82f6', icon: 'lucide:boxes', key: 'material', label: '物资管理', url: '/project/document' },
  { category: 'production', color: '#ef4444', icon: 'lucide:bolt', key: 'tech', label: '生产技术管理', url: '/project/information' },
  { category: 'production', color: '#6366f1', icon: 'lucide:layout-grid', key: 'project', label: '生产项目管理系统', roles: ['admin'], url: '/project/overview' },
  { category: 'production', color: '#8b5cf6', icon: 'lucide:database', key: 'library', label: '生产项目正式库', roles: ['admin'], url: '/project/document' },
  { category: 'production', color: '#10b981', icon: 'lucide:cloud-upload', key: 'upload-report', label: '广东省重点污染源自行监测数据上报平台', permissions: ['*:*:*'], url: '/system/notice' },
  { category: 'production', color: '#64748b', icon: 'lucide:cloud', key: 'public-report', label: '广东省重点污染源自行监测数据公开平台', permissions: ['*:*:*'], url: '/system/notice' },
  { category: 'production', color: '#d97706', icon: 'lucide:flask-conical', key: 'test-library', label: '生产项目测试库', roles: ['admin'], url: '/demo/demo' },
  { category: 'safety', color: '#14b8a6', icon: 'lucide:shield-check', key: 'safety-1', label: '安全巡检', url: '/battery/construction' },
  { category: 'safety', color: '#f97316', icon: 'lucide:badge-alert', key: 'safety-2', label: '风险辨识', url: '/personnel/qualification' },
  { category: 'operation', color: '#0ea5e9', icon: 'lucide:bar-chart-3', key: 'ops-1', label: '经营分析', url: '/project/evaluation' },
  { category: 'office', color: '#06b6d4', icon: 'lucide:calendar-days', key: 'office-1', label: '会议纪要', url: '/workflow/leave' },
  { category: 'party', color: '#db2777', icon: 'lucide:landmark', key: 'party-1', label: '党建学习', url: '/workflow/processInstance' },
  { category: 'three-d', color: '#7c3aed', icon: 'lucide:scan-line', key: 'three-1', label: '三维场景', url: '/project/progress' },
];

const todoGroups: TodoGroup[] = [
  { count: 20, key: 'all', label: '全部待办' },
  { count: 0, key: 'evam', label: '集团EVAM系统' },
  { count: 0, key: 'capital', label: '股权增值管理系统' },
  { count: 0, key: 'site', label: '智慧工地管控系统' },
  { count: 0, key: 'oa', label: 'OA办公系统' },
  { count: 0, key: 'business', label: '离电商务网' },
];

const todoItems: TodoItem[] = [
  {
    description: '待办详情内容在这里显示待办详情内容在这里显示……',
    group: 'all',
    id: 1,
    meta: '1分钟前',
    status: 'info',
    tag: '考试任务',
    title: '你有一场考试需要参加!',
  },
  {
    description: '待办详情内容在这里显示待办详情内容在这里显示……',
    group: 'all',
    id: 2,
    meta: '1分钟前',
    status: 'info',
    tag: '考试任务',
    title: '你有一场考试需要参加!',
  },
  {
    description: '待办详情内容在这里显示待办详情内容在这里显示……',
    group: 'all',
    id: 3,
    meta: '1分钟前',
    status: 'info',
    tag: '考试任务',
    title: '你有一场考试需要参加!',
  },
  {
    description: '待办详情内容在这里显示待办详情内容在这里显示……',
    group: 'all',
    id: 4,
    meta: '1分钟前',
    status: 'info',
    tag: '考试任务',
    title: '你有一场考试需要参加!',
  },
  {
    description: '待办详情内容在这里显示待办详情内容在这里显示……',
    group: 'all',
    id: 5,
    meta: '1分钟前',
    status: 'info',
    tag: '考试任务',
    title: '你有一场考试需要参加!',
  },
];

const noticeItems: PlatformNoticeListItem[] = [
  {
    description: '东莞市启动市级人大代表换届选举工作……',
    id: 1,
    meta: '1分钟前',
    status: 'info',
    tag: '消息来源',
    title: '消息的标题在这里显示消息的标题',
  },
  {
    description: '东莞市启动市级人大代表换届选举工作……',
    id: 2,
    meta: '1分钟前',
    status: 'info',
    tag: '消息来源',
    title: '消息的标题在这里显示消息的标题',
  },
  {
    description: '东莞市启动市级人大代表换届选举工作……',
    id: 3,
    meta: '1分钟前',
    status: 'info',
    tag: '消息来源',
    title: '消息的标题在这里显示消息的标题',
  },
  {
    description: '东莞市启动市级人大代表换届选举工作……',
    id: 4,
    meta: '1分钟前',
    status: 'info',
    tag: '消息来源',
    title: '消息的标题在这里显示消息的标题',
  },
  {
    description: '东莞市启动市级人大代表换届选举工作……',
    id: 5,
    meta: '1分钟前',
    status: 'info',
    tag: '消息来源',
    title: '消息的标题在这里显示消息的标题',
  },
  {
    description: '东莞市启动市级人大代表换届选举工作……',
    id: 6,
    meta: '1分钟前',
    status: 'info',
    tag: '消息来源',
    title: '消息的标题在这里显示消息的标题',
  },
  {
    description: '东莞市启动市级人大代表换届选举工作……',
    id: 7,
    meta: '1分钟前',
    status: 'info',
    tag: '消息来源',
    title: '消息的标题在这里显示消息的标题',
  },
  {
    description: '东莞市启动市级人大代表换届选举工作……',
    id: 8,
    meta: '1分钟前',
    status: 'info',
    tag: '消息来源',
    title: '消息的标题在这里显示消息的标题',
  },
  {
    description: '东莞市启动市级人大代表换届选举工作……',
    id: 9,
    meta: '1分钟前',
    status: 'info',
    tag: '消息来源',
    title: '消息的标题在这里显示消息的标题',
  },
  {
    description: '东莞市启动市级人大代表换届选举工作……',
    id: 10,
    meta: '1分钟前',
    status: 'info',
    tag: '消息来源',
    title: '消息的标题在这里显示消息的标题',
  },
];

const visibleQuickEntries = computed(() =>
  accessEntries.filter((item) => isVisible(item.roles, item.permissions)).slice(0, 6),
);
const visibleAppEntries = computed(() =>
  appEntries
    .filter((item) => item.category === activeAppCategory.value && isVisible(item.roles, item.permissions))
    .slice(0, 8),
);
const filteredTodoItems = computed(() =>
  activeTodoGroup.value === 'all'
    ? todoItems
    : todoItems.filter((item) => item.group === activeTodoGroup.value),
);
const overflowTodoGroups = computed(() =>
  todoGroups.filter((group) => overflowTodoGroupKeys.value.includes(group.key)),
);
const selectedScheduleItems = computed(
  () => scheduleMap[activeWeekDay.value] ?? [],
);

const currentUserName = '系统管理员';
const avatarSrc = computed(() => userStore.userInfo?.avatar || preferences.app.defaultAvatar);
const currentTimeLeft = 90;

function isVisible(roles?: string[], permissions?: string[]) {
  const userRoles = new Set<string>();
  const rawRoles = userStore.userInfo?.roles;

  if (Array.isArray(rawRoles)) {
    for (const role of rawRoles as Array<string | { roleKey?: string }>) {
      if (typeof role === 'string') {
        userRoles.add(role);
        continue;
      }
      if (role && typeof role.roleKey === 'string') {
        userRoles.add(role.roleKey);
      }
    }
  }
  const userPermissions = new Set(userStore.userInfo?.permissions ?? []);

  if ((permissions?.length ?? 0) > 0) {
    return permissions!.some(
      (permission) => userPermissions.has(permission) || userPermissions.has('*:*:*'),
    );
  }

  if ((roles?.length ?? 0) > 0) {
    return roles!.some((role) => userRoles.has(role) || userRoles.has('admin'));
  }

  return true;
}

async function handleOpen(url?: string, label?: string) {
  if (!url) {
    window.message.info(label ? `已打开 ${label}` : '入口已打开');
    return;
  }

  const resolved = router.resolve(url);
  if (resolved.matched.length > 0) {
    await router.push(url);
    return;
  }

  window.message.info(label ? `已保留 ${label} 入口` : '入口已保留');
}

function setTodoFilterButtonRef(key: string) {
  return (element: Element | ComponentPublicInstance | null) => {
    if (element instanceof HTMLButtonElement) {
      todoFilterButtonRefs.set(key, element);
      return;
    }

    if (element && '$el' in element && element.$el instanceof HTMLButtonElement) {
      todoFilterButtonRefs.set(key, element.$el);
      return;
    }

    todoFilterButtonRefs.delete(key);
  };
}

function updateTodoOverflowGroups() {
  const viewport = todoFilterViewportRef.value;

  if (!viewport) {
    return;
  }

  overflowTodoGroupKeys.value = todoGroups
    .filter((group) => {
      const button = todoFilterButtonRefs.get(group.key);
      if (!button) {
        return false;
      }
      return button.offsetTop + button.offsetHeight > viewport.clientHeight;
    })
    .map((group) => group.key);
}

function handleTodoFilterWheel(event: WheelEvent) {
  const viewport = todoFilterViewportRef.value;
  if (!viewport) {
    return;
  }

  const horizontalDelta = event.deltaX === 0 ? event.deltaY : event.deltaX;
  if (horizontalDelta === 0) {
    return;
  }

  event.preventDefault();
  viewport.scrollLeft += horizontalDelta;
}

function handleTodoGroupSelect(key: string) {
  activeTodoGroup.value = key;

  nextTick(() => {
    const viewport = todoFilterViewportRef.value;
    const button = todoFilterButtonRefs.get(key);

    if (!viewport || !button) {
      return;
    }

    viewport.scrollTo({
      left: Math.max(button.offsetLeft - 16, 0),
      behavior: 'smooth',
    });
  });
}

onMounted(() => {
  nextTick(() => {
    updateTodoOverflowGroups();

    if (!todoFilterViewportRef.value) {
      return;
    }

    todoFilterResizeObserver = new ResizeObserver(() => {
      updateTodoOverflowGroups();
    });

    todoFilterResizeObserver.observe(todoFilterViewportRef.value);
  });
});

onBeforeUnmount(() => {
  todoFilterResizeObserver?.disconnect();
});
</script>

<template>
  <Page :auto-content-height="true">
    <div class="workbench-home">
      <section class="workbench-home__hero">
        <Avatar :size="68" :src="avatarSrc">
          系
        </Avatar>

        <div class="workbench-home__hero-copy">
          <div class="workbench-home__hero-row">
            <div class="workbench-home__hero-text">
              <div class="workbench-home__greeting-row">
                <p class="workbench-home__greeting">下午好，{{ currentUserName }}</p>
              </div>
              <p class="workbench-home__subtitle">
                欢迎使用统一工作平台，距离修改密码还有
                <span>{{ currentTimeLeft }}</span>
                天
              </p>
            </div>
          </div>
        </div>
      </section>

      <div class="workbench-home__grid">
        <div class="workbench-home__grid-row workbench-home__grid-row--top">
          <PlatformSection
            class="workbench-home__surface workbench-home__surface--schedule"
            title="日程"
          >
            <template #title-extra>
              <PlatformButton
                class="workbench-home__primary-action"
                size="small"
                type="primary"
              >
                <template #icon>
                  <PlatformIcon icon="lucide:plus" />
                </template>
                新增日程
              </PlatformButton>
            </template>
            <template #extra>
              <PlatformSectionAction kind="more" label="更多日程" />
            </template>

            <div class="workbench-home__calendar">
              <button
                v-for="day in weekDays"
                :key="day.key"
                class="workbench-home__calendar-day"
                :class="{
                  'is-active': activeWeekDay === day.key,
                  'is-weekend': day.isWeekend,
                }"
                type="button"
                @click="activeWeekDay = day.key"
              >
                <span>{{ day.label }}</span>
                <strong>{{ day.date }}</strong>
              </button>
            </div>

            <ul class="workbench-home__schedule-list">
              <li
                v-for="item in selectedScheduleItems"
                :key="`${item.time}-${item.title}`"
                class="workbench-home__schedule-item"
              >
                <span class="workbench-home__schedule-time">{{ item.time }}</span>
                <span class="workbench-home__schedule-title">{{ item.title }}</span>
              </li>
            </ul>
          </PlatformSection>

          <PlatformSection
            class="workbench-home__surface workbench-home__surface--shortcut"
            title="快捷入口"
          >
            <template #extra>
              <PlatformSectionAction icon="lucide:chevron-right" label="设置" />
            </template>

            <div class="workbench-home__shortcut-grid">
              <PlatformEntryCard
                v-for="item in visibleQuickEntries"
                :key="item.key"
                :icon="item.icon"
                :icon-background="item.color"
                :label="item.label"
                variant="shortcut"
                @click="handleOpen(item.url, item.label)"
              />
            </div>

            <div class="workbench-home__access-strip">
              <span class="workbench-home__access-title">最近访问：</span>
              <div class="workbench-home__access-links">
                <button
                  v-for="item in recentVisits"
                  :key="item.key"
                  class="workbench-home__access-link"
                  type="button"
                  @click="handleOpen(item.url, item.label)"
                >
                  {{ item.label }}
                </button>
              </div>
            </div>
          </PlatformSection>
        </div>

        <div class="workbench-home__grid-row workbench-home__grid-row--bottom">
          <PlatformSection
            class="workbench-home__surface workbench-home__surface--todo"
            title="待办"
          >
            <template #extra>
              <PlatformSectionAction kind="refresh" />
              <PlatformSectionAction kind="more" />
            </template>

            <div class="workbench-home__todo-filter-bar">
              <div
                ref="todoFilterViewportRef"
                class="workbench-home__todo-filters"
                :class="{ 'has-overflow': overflowTodoGroups.length > 0 }"
                @wheel="handleTodoFilterWheel"
              >
                <div class="workbench-home__todo-filters-track">
                  <button
                    v-for="group in todoGroups"
                    :ref="setTodoFilterButtonRef(group.key)"
                    :key="group.key"
                    class="workbench-home__todo-filter"
                    :class="{ 'is-active': activeTodoGroup === group.key }"
                    type="button"
                    @click="handleTodoGroupSelect(group.key)"
                  >
                    <span>{{ group.label }}</span>
                    <em>({{ group.count }})</em>
                  </button>
                </div>
              </div>

              <Popover
                v-if="overflowTodoGroups.length > 0"
                overlay-class-name="workbench-home__todo-more-popover"
                placement="bottomRight"
                trigger="hover"
              >
                <template #content>
                  <div class="workbench-home__todo-more-menu">
                    <button
                      v-for="group in overflowTodoGroups"
                      :key="group.key"
                      class="workbench-home__todo-more-item"
                      :class="{ 'is-active': activeTodoGroup === group.key }"
                      type="button"
                      @click="handleTodoGroupSelect(group.key)"
                    >
                      {{ group.label }}
                    </button>
                  </div>
                </template>

                <button class="workbench-home__todo-more-trigger" type="button">
                  <PlatformIcon icon="lucide:ellipsis" />
                </button>
              </Popover>
            </div>

            <PlatformNoticeList
              class="workbench-home__todo-list"
              :items="filteredTodoItems"
              variant="workbench"
            />
          </PlatformSection>

          <div class="workbench-home__right-stack">
            <PlatformSection
              class="workbench-home__surface workbench-home__surface--apps"
              title="应用入口"
            >
              <template #center>
                <PlatformSegmented
                  class="workbench-home__app-segmented"
                  v-model:value="activeAppCategory"
                  :options="appCategories"
                />
              </template>
              <template #extra>
                <PlatformSectionAction icon="lucide:key-round" label="密码设置" />
              </template>

              <div class="workbench-home__app-grid">
                <PlatformEntryCard
                  v-for="item in visibleAppEntries"
                  :key="item.key"
                  :icon="item.icon"
                  :icon-color="item.color"
                  :label="item.label"
                  variant="app"
                  @click="handleOpen(item.url, item.label)"
                />
              </div>
            </PlatformSection>

            <PlatformSection
              class="workbench-home__surface workbench-home__surface--notice"
              title="通知消息"
            >
              <template #extra>
                <PlatformSectionAction kind="refresh" />
                <PlatformSectionAction kind="more" />
              </template>

              <PlatformNoticeList
                class="workbench-home__notice-list"
                :items="noticeItems"
                variant="workbench"
              />
            </PlatformSection>
          </div>
        </div>
      </div>
    </div>
  </Page>
</template>

<style scoped>
.workbench-home {
  display: flex;
  flex-direction: column;
  gap: 24px;
  min-width: 0;
}

.workbench-home__hero {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 0;
}

.workbench-home__hero-copy {
  flex: 1;
  min-width: 0;
}

.workbench-home__hero-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  min-width: 0;
}

.workbench-home__hero-text {
  min-width: 0;
}

.workbench-home__greeting-row {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.workbench-home__greeting {
  margin: 0;
  color: hsl(var(--foreground));
  font-size: 24px;
  font-weight: 700;
  line-height: 32px;
}

.workbench-home__subtitle {
  margin: 4px 0 0;
  color: hsl(var(--muted-foreground));
  font-size: 14px;
  line-height: 22px;
}

.workbench-home__subtitle span {
  color: hsl(var(--primary));
  font-weight: 700;
}

.workbench-home__grid {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.workbench-home__grid-row {
  display: grid;
  grid-template-columns: minmax(320px, 0.92fr) minmax(0, 1.4fr);
  gap: 24px;
  align-items: stretch;
  min-width: 0;
}

.workbench-home__grid-row > * {
  min-width: 0;
}

.workbench-home__surface {
  display: flex;
  flex-direction: column;
  min-width: 0;
  overflow: hidden;
}

.workbench-home__surface :deep(.platform-section__header) {
  margin-bottom: 0;
}

.workbench-home__surface--schedule,
.workbench-home__surface--shortcut {
  min-height: 270px;
  height: 100%;
}

.workbench-home__surface--todo,
.workbench-home__right-stack {
  min-height: 0;
}

.workbench-home__surface--todo {
  flex: 1;
  min-height: 0;
}

.workbench-home__right-stack {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 24px;
}

.workbench-home__calendar {
  display: grid;
  grid-template-columns: repeat(7, minmax(0, 1fr));
  gap: 8px;
  margin-top: 16px;
}

.workbench-home__calendar-day {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  gap: 6px;
  min-height: 72px;
  padding: 10px 6px 8px;
  color: hsl(var(--foreground));
  background: transparent;
  border: 0;
  border-radius: 8px;
  transition:
    color 0.16s ease,
    background 0.16s ease;
}

.workbench-home__calendar-day span {
  font-size: 14px;
  color: hsl(var(--foreground));
  line-height: 20px;
}

.workbench-home__calendar-day strong {
  font-size: 16px;
  font-weight: 500;
  line-height: 20px;
  color: hsl(var(--foreground));
}

.workbench-home__calendar-day.is-weekend span,
.workbench-home__calendar-day.is-weekend strong {
  color: hsl(var(--muted-foreground));
}

.workbench-home__calendar-day:hover:not(.is-active) {
  color: hsl(var(--primary));
  background: hsl(var(--primary) / 10%);
}

.workbench-home__calendar-day:hover:not(.is-active) span,
.workbench-home__calendar-day:hover:not(.is-active) strong {
  color: hsl(var(--primary));
}

.workbench-home__calendar-day.is-active {
  color: #fff;
  background: hsl(var(--primary));
}

.workbench-home__calendar-day.is-active span,
.workbench-home__calendar-day.is-active strong {
  color: #fff;
}

.workbench-home__schedule-list {
  padding: 0;
  margin: 16px 0 0;
  list-style: none;
}

.workbench-home__schedule-item {
  display: grid;
  grid-template-columns: 116px minmax(0, 1fr);
  gap: 12px;
  align-items: center;
  min-height: 48px;
  padding: 0 12px;
  color: hsl(var(--foreground));
  background: transparent;
}

.workbench-home__schedule-item + .workbench-home__schedule-item {
  margin-top: 8px;
  padding-top: 10px;
  border-top: 1px solid hsl(var(--st-color-border-subtle));
}

.workbench-home__schedule-time {
  color: hsl(var(--primary));
  font-size: 15px;
  font-weight: 700;
  line-height: 22px;
  white-space: nowrap;
}

.workbench-home__schedule-title {
  overflow: hidden;
  color: hsl(var(--foreground));
  font-size: 14px;
  line-height: 22px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.workbench-home__todo-filter-bar {
  position: relative;
  display: flex;
  align-items: flex-end;
  gap: 8px;
  margin-top: 16px;
  min-width: 0;
}

.workbench-home__todo-filters {
  flex: 1;
  min-width: 0;
  max-height: 84px;
  overflow: hidden;
  scrollbar-width: none;
}

.workbench-home__todo-filters.has-overflow {
  padding-right: 48px;
}

.workbench-home__todo-filters::-webkit-scrollbar {
  display: none;
}

.workbench-home__todo-filters-track {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px 20px;
  min-width: 0;
  padding-inline-end: 8px;
}

.workbench-home__todo-more-trigger {
  display: inline-flex;
  position: absolute;
  right: 0;
  bottom: 0;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  width: 32px;
  height: 32px;
  color: hsl(var(--foreground));
  background: hsl(var(--background));
  border: 0;
  border-radius: 999px;
  transition: background 0.16s ease;
}

.workbench-home__todo-more-trigger:hover {
  background: hsl(var(--st-color-fill-selected));
}

.workbench-home__todo-more-trigger :deep(svg) {
  width: 16px;
  height: 16px;
}

.workbench-home__todo-filter {
  display: inline-flex;
  flex: 0 0 auto;
  align-items: center;
  cursor: pointer;
  gap: 4px;
  min-height: 32px;
  padding: 0 4px;
  color: hsl(var(--foreground));
  font-size: 14px;
  line-height: 20px;
  white-space: nowrap;
  background: transparent;
  border: 0;
}

.workbench-home__todo-filter em {
  font-style: normal;
}

.workbench-home__todo-filter.is-active {
  padding-inline: 18px;
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  background: hsl(var(--primary));
  border-radius: 999px;
}

.workbench-home__todo-filter:not(.is-active):hover,
.workbench-home__todo-filter:not(.is-active):focus-visible {
  color: hsl(var(--primary));
}

.workbench-home__todo-more-menu {
  display: flex;
  flex-direction: column;
  min-width: 180px;
  padding: 6px 0;
}

.workbench-home__todo-more-item {
  padding: 12px 18px;
  cursor: pointer;
  color: hsl(var(--foreground));
  font-size: 14px;
  line-height: 22px;
  text-align: left;
  white-space: nowrap;
  background: transparent;
  border: 0;
}

.workbench-home__todo-more-item:hover,
.workbench-home__todo-more-item.is-active {
  color: hsl(var(--primary));
  background: hsl(var(--primary) / 8%);
}

.workbench-home__access-strip {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 12px;
  min-height: 52px;
  padding: 10px 18px;
  margin-top: 8px;
  background: hsl(var(--primary) / 10%);
  border-radius: 8px;
}

.workbench-home__access-title {
  flex: 0 0 auto;
  color: hsl(var(--foreground));
  font-size: 15px;
  font-weight: 700;
  line-height: 22px;
}

.workbench-home__access-links {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.workbench-home__access-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  width: 88px;
  min-height: 32px;
  padding: 0 12px;
  color: hsl(var(--foreground));
  font-size: 14px;
  font-weight: 500;
  line-height: 20px;
  white-space: nowrap;
  background: hsl(var(--background));
  border: 0;
  border-radius: 8px;
  transition:
    color 0.16s ease,
    background 0.16s ease,
    box-shadow 0.16s ease;
}

.workbench-home__access-link:hover {
  color: hsl(var(--primary));
  background: hsl(var(--background));
  box-shadow: inset 0 0 0 1px hsl(var(--primary) / 20%);
}

.workbench-home__shortcut-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 8px;
  margin-top: 20px;
  align-items: start;
  flex: 1;
}

.workbench-home__app-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  grid-auto-rows: 72px;
  gap: 12px 16px;
  margin-top: 20px;
  align-content: start;
  flex: none;
  min-height: 156px;
}

.workbench-home__calendar-day:hover {
  transform: translateY(-1px);
}

.workbench-home__surface--apps,
.workbench-home__surface--notice {
  flex: 1;
}

.workbench-home__surface--apps {
  flex: none;
  min-height: 0;
}

.workbench-home__app-segmented {
  max-width: 100%;
  background: hsl(var(--st-color-border-subtle));
  border-radius: 8px;
}

.workbench-home__app-segmented :deep(.ant-segmented-item-label) {
  min-height: 32px;
  padding: 0 16px;
  line-height: 32px;
  font-size: 14px;
}

.workbench-home__app-segmented :deep(.ant-segmented-group) {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 4px;
  max-height: 68px;
  overflow: hidden;
}

.workbench-home__app-segmented :deep(.ant-segmented-item) {
  margin: 0 !important;
  flex: 0 0 auto;
}

.workbench-home__notice-list {
  flex: 1;
  margin-top: 20px;
  max-height: 360px;
  overflow-y: auto;
  padding-right: 4px;
}

.workbench-home__todo-list {
  flex: 1;
  margin-top: 16px;
}

.workbench-home__primary-action {
  min-width: auto;
  margin-block: 2px;
}

.workbench-home__surface--schedule
  :deep(.platform-section-title__title-extra .platform-button) {
  min-height: 32px;
  height: 32px;
  padding-block: 0;
}

.workbench-home__primary-action :deep(.platform-icon),
.workbench-home__primary-action :deep(svg) {
  color: #fff;
}

@media (max-width: 1280px) {
  .workbench-home__grid {
    gap: 20px;
  }

  .workbench-home__grid-row {
    grid-template-columns: 1fr;
  }

  .workbench-home__shortcut-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .workbench-home__app-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .workbench-home__hero,
  .workbench-home__hero-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .workbench-home__access-strip {
    align-items: flex-start;
    flex-direction: column;
  }

  .workbench-home__access-links {
    width: 100%;
  }

  .workbench-home__calendar {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }

  .workbench-home__schedule-item {
    grid-template-columns: 1fr;
  }

  .workbench-home__shortcut-grid,
  .workbench-home__app-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .workbench-home__todo-filter-bar {
    align-items: flex-start;
  }
}
</style>
