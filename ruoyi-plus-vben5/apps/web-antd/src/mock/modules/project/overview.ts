export type ProjectStatus = 'archived' | 'completed' | 'pending' | 'running';

export interface ProjectOverviewQuery {
  keyword?: string;
  status?: '' | ProjectStatus;
  type?: string;
}

export interface ProjectRecord {
  amount: number;
  code: string;
  color: string;
  department: string;
  durationDays: number;
  endDate: string;
  id: number;
  manager: string;
  name: string;
  progress: number;
  startDate: string;
  status: ProjectStatus;
  type: string;
}

export interface ProjectStatCard {
  description?: string;
  icon?: string;
  title: string;
  trendText?: string;
  trendType?: 'down' | 'neutral' | 'up';
  type?: 'danger' | 'info' | 'primary' | 'success' | 'warning';
  unit?: string;
  value: string;
}

let projectRows: ProjectRecord[] = [
  {
    amount: 11_578,
    code: 'PJ2026001',
    color: '#ec4899',
    department: '工程一部',
    durationDays: 626,
    endDate: '2027-09-18',
    id: 1,
    manager: '张明远',
    name: '城市中央商务区A栋综合体',
    progress: 41,
    startDate: '2026-01-01',
    status: 'running',
    type: '房建工程',
  },
  {
    amount: 17_608,
    code: 'PJ2026002',
    color: '#f59e0b',
    department: '工程二部',
    durationDays: 803,
    endDate: '2028-03-15',
    id: 2,
    manager: '刘海涛',
    name: '滨河路市政道路改造工程',
    progress: 0,
    startDate: '2026-01-02',
    status: 'pending',
    type: '市政工程',
  },
  {
    amount: 28_947,
    code: 'PJ2026003',
    color: '#10b981',
    department: '工程三部',
    durationDays: 812,
    endDate: '2028-03-24',
    id: 3,
    manager: '陈思雅',
    name: '科技产业园精装修项目',
    progress: 100,
    startDate: '2026-01-02',
    status: 'completed',
    type: '装饰装修',
  },
  {
    amount: 4_578,
    code: 'PJ2026004',
    color: '#ef4444',
    department: '设计院',
    durationDays: 940,
    endDate: '2028-07-30',
    id: 4,
    manager: '赵国强',
    name: '新区综合管廊工程',
    progress: 100,
    startDate: '2026-01-02',
    status: 'archived',
    type: '基础设施',
  },
  {
    amount: 15_169,
    code: 'PJ2026005',
    color: '#8b5cf6',
    department: '设备管理部',
    durationDays: 716,
    endDate: '2027-12-20',
    id: 5,
    manager: '林雨桐',
    name: '湿地公园景观绿化工程',
    progress: 24,
    startDate: '2026-01-03',
    status: 'running',
    type: '园林景观',
  },
  {
    amount: 38_097,
    code: 'PJ2026006',
    color: '#14b8a6',
    department: '工程一部',
    durationDays: 534,
    endDate: '2027-06-21',
    id: 6,
    manager: '王建华',
    name: '老旧小区改造提升项目',
    progress: 0,
    startDate: '2026-01-03',
    status: 'pending',
    type: '特种设备维保',
  },
  {
    amount: 40_343,
    code: 'PJ2026007',
    color: '#22c55e',
    department: '工程二部',
    durationDays: 270,
    endDate: '2026-09-29',
    id: 7,
    manager: '孙立民',
    name: '跨河大桥新建工程',
    progress: 100,
    startDate: '2026-01-03',
    status: 'completed',
    type: '车辆整改',
  },
  {
    amount: 9_559,
    code: 'PJ2026008',
    color: '#3b82f6',
    department: '工程三部',
    durationDays: 385,
    endDate: '2027-01-23',
    id: 8,
    manager: '何晓峰',
    name: '智慧园区弱电系统工程',
    progress: 100,
    startDate: '2026-01-03',
    status: 'archived',
    type: '轨道桥隧维保',
  },
  {
    amount: 3_694,
    code: 'PJ2026009',
    color: '#8b5cf6',
    department: '设计院',
    durationDays: 912,
    endDate: '2028-07-02',
    id: 9,
    manager: '杨志刚',
    name: '城南污水处理厂扩容',
    progress: 76,
    startDate: '2026-01-03',
    status: 'running',
    type: '保安保洁',
  },
  {
    amount: 2_753,
    code: 'PJ2026010',
    color: '#ef4444',
    department: '设备管理部',
    durationDays: 792,
    endDate: '2028-03-03',
    id: 10,
    manager: '周敏',
    name: '市民广场地下停车场',
    progress: 0,
    startDate: '2026-01-03',
    status: 'pending',
    type: '房建工程',
  },
  {
    amount: 23_308,
    code: 'PJ2026011',
    color: '#6366f1',
    department: '工程一部',
    durationDays: 549,
    endDate: '2027-07-06',
    id: 11,
    manager: '张明远',
    name: '地铁3号线轨道维保',
    progress: 100,
    startDate: '2026-01-03',
    status: 'completed',
    type: '市政工程',
  },
];

export const projectStatusOptions = [
  { label: '全部', value: '' },
  { label: '进行中', value: 'running' },
  { label: '待启动', value: 'pending' },
  { label: '已完成', value: 'completed' },
  { label: '已归档', value: 'archived' },
];

export const projectTypeOptions = [
  { label: '全部类型', value: '' },
  { label: '房建工程', value: '房建工程' },
  { label: '市政工程', value: '市政工程' },
  { label: '装饰装修', value: '装饰装修' },
  { label: '基础设施', value: '基础设施' },
  { label: '园林景观', value: '园林景观' },
  { label: '特种设备维保', value: '特种设备维保' },
  { label: '车辆整改', value: '车辆整改' },
  { label: '轨道桥隧维保', value: '轨道桥隧维保' },
  { label: '保安保洁', value: '保安保洁' },
];

export const projectStatusMap: Record<
  ProjectStatus,
  { label: string; status: 'default' | 'error' | 'processing' | 'success' | 'warning' }
> = {
  archived: { label: '已归档', status: 'default' },
  completed: { label: '已完成', status: 'success' },
  pending: { label: '待启动', status: 'warning' },
  running: { label: '进行中', status: 'processing' },
};

export async function getProjectOverviewList(query: ProjectOverviewQuery) {
  const keyword = query.keyword?.trim().toLowerCase();

  return projectRows.filter((item) => {
    const matchedKeyword = keyword
      ? [item.name, item.code, item.manager].some((field) =>
          field.toLowerCase().includes(keyword),
        )
      : true;
    const matchedStatus = query.status ? item.status === query.status : true;
    const matchedType = query.type ? item.type === query.type : true;

    return matchedKeyword && matchedStatus && matchedType;
  });
}

export async function getProjectOverviewStats(): Promise<ProjectStatCard[]> {
  const totalAmount = projectRows.reduce((sum, item) => sum + item.amount, 0);

  return [
    {
      icon: 'lucide:folder-kanban',
      title: '项目总数',
      trendText: '较上月增长12%',
      trendType: 'up',
      type: 'success',
      value: String(projectRows.length),
    },
    {
      icon: 'lucide:loader-circle',
      title: '进行中',
      trendText: '较上月增长8%',
      trendType: 'up',
      type: 'info',
      value: String(countByStatus('running')),
    },
    {
      icon: 'lucide:clock-3',
      title: '待启动',
      trendText: '较上月减少3%',
      trendType: 'down',
      type: 'warning',
      value: String(countByStatus('pending')),
    },
    {
      icon: 'lucide:circle-check',
      title: '已完成',
      trendText: '较上月增长5%',
      trendType: 'up',
      type: 'primary',
      value: String(countByStatus('completed')),
    },
    {
      description: '涵盖全部委外项目',
      icon: 'lucide:badge-dollar-sign',
      title: '合同总额',
      type: 'danger',
      unit: '亿',
      value: (totalAmount / 10_000).toFixed(2),
    },
  ];
}

export async function saveProjectOverviewProject(
  project: Partial<ProjectRecord>,
) {
  if (project.id) {
    projectRows = projectRows.map((item) =>
      item.id === project.id ? ({ ...item, ...project } as ProjectRecord) : item,
    );
    return;
  }

  const nextId = Math.max(...projectRows.map((item) => item.id)) + 1;
  projectRows = [
    {
      amount: project.amount ?? 0,
      code: project.code || `PJ${2026000 + nextId}`,
      color: project.color || '#10b981',
      department: project.department || '工程一部',
      durationDays: project.durationDays ?? 0,
      endDate: project.endDate || '2026-12-31',
      id: nextId,
      manager: project.manager || '待确认',
      name: project.name || '未命名项目',
      progress: project.progress ?? 0,
      startDate: project.startDate || '2026-01-01',
      status: project.status || 'pending',
      type: project.type || '房建工程',
    },
    ...projectRows,
  ];
}

export async function archiveProjectOverviewProject(id: number) {
  projectRows = projectRows.map((item) =>
    item.id === id ? { ...item, status: 'archived' } : item,
  );
}

export async function deleteProjectOverviewProject(id: number) {
  projectRows = projectRows.filter((item) => item.id !== id);
}

function countByStatus(status: ProjectStatus) {
  return projectRows.filter((item) => item.status === status).length;
}
