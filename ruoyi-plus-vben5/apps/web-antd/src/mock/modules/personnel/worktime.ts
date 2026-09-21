export type WorktimeAlertLevel = 'high' | 'medium';

export interface WorktimeAlert {
  contractor: string;
  id: number;
  level: WorktimeAlertLevel;
  monthlyHours: number;
  name: string;
  overHours: number;
  position: string;
  project: string;
  notifiedAt?: string;
}

export interface WorktimeStatCard {
  color?: string;
  description?: string;
  icon?: string;
  title: string;
  type: 'danger' | 'info' | 'primary' | 'success' | 'warning';
  unit?: string;
  value: number | string;
}

export const worktimeThresholdText = '180小时/月';
export const worktimeThreshold = 180;

const worktimeAlertSeed: WorktimeAlert[] = [
  {
    contractor: '金螳螂装饰',
    id: 1,
    level: 'medium',
    monthlyHours: 185,
    name: '赵丽华',
    overHours: 5,
    position: '安全员',
    project: '科技产业园精装修项目',
  },
  {
    contractor: '保安公司',
    id: 2,
    level: 'medium',
    monthlyHours: 189,
    name: '杨柳',
    overHours: 9,
    position: '保洁员',
    project: '市民广场地下停车场',
  },
  {
    contractor: '华为技术',
    id: 3,
    level: 'medium',
    monthlyHours: 186,
    name: '徐达',
    overHours: 6,
    position: '电工',
    project: '滨河路市政道路改造工程',
  },
  {
    contractor: '北控水务',
    id: 4,
    level: 'high',
    monthlyHours: 195,
    name: '常遇春',
    overHours: 15,
    position: '巡检员',
    project: '科技产业园精装修项目',
  },
  {
    contractor: '北控水务',
    id: 5,
    level: 'high',
    monthlyHours: 192,
    name: '杜甫',
    overHours: 12,
    position: '保洁员',
    project: '园区保安保洁服务',
  },
];

let worktimeAlerts = [...worktimeAlertSeed];

export const worktimeAlertLevelMap: Record<
  WorktimeAlertLevel,
  { label: string; status: 'error' | 'warning' }
> = {
  high: { label: '重点预警', status: 'error' },
  medium: { label: '超时预警', status: 'warning' },
};

export async function getWorktimeStats(): Promise<WorktimeStatCard[]> {
  return [
    {
      color: 'hsl(var(--st-color-brand))',
      description: '当前在岗人员月均统计',
      icon: 'lucide:clock-3',
      title: '人均月工时',
      type: 'success',
      unit: 'h',
      value: 169,
    },
    {
      description: '超过阈值需通知整改',
      icon: 'lucide:triangle-alert',
      title: '超工时预警',
      type: 'danger',
      value: worktimeAlerts.length,
    },
    {
      description: '当前管控阈值',
      icon: 'lucide:gauge',
      title: '工时上限',
      type: 'info',
      value: '180h/月',
    },
    {
      description: '本轮暂不开发核查模块',
      icon: 'lucide:briefcase-business',
      title: '兼职核查异常',
      type: 'primary',
      value: 0,
    },
  ];
}

export async function getWorktimeAlerts(): Promise<WorktimeAlert[]> {
  return [...worktimeAlerts];
}

export async function notifyWorktimeRectification(id: number) {
  const now = new Date();
  const notifiedAt = `${String(now.getMonth() + 1).padStart(2, '0')}-${String(
    now.getDate(),
  ).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(
    now.getMinutes(),
  ).padStart(2, '0')}`;

  worktimeAlerts = worktimeAlerts.map((item) =>
    item.id === id ? { ...item, notifiedAt } : item,
  );
}
