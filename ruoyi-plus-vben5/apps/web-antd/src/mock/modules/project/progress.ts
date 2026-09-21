export type ProjectProgressStatus =
  | 'archived'
  | 'completed'
  | 'pending'
  | 'running';

export interface ProjectProgressMilestone {
  label: string;
  month: number;
}

export interface ProjectProgressRecord {
  color: string;
  department: string;
  endMonth: number;
  id: number;
  manager: string;
  milestones: ProjectProgressMilestone[];
  name: string;
  progress: number;
  startMonth: number;
  status: ProjectProgressStatus;
}

export interface ProjectProgressWarning {
  id: number;
  name: string;
  reason: string;
}

export interface ProjectProgressQuery {
  department?: string;
  keyword?: string;
  manager?: string;
  status?: '' | ProjectProgressStatus;
  year?: number;
}

export const projectProgressYearOptions = [
  { label: '2026年度', value: 2026 },
  { label: '2025年度', value: 2025 },
];

export const projectProgressStatusOptions = [
  { label: '全部状态', value: '' },
  { label: '待启动', value: 'pending' },
  { label: '进行中', value: 'running' },
  { label: '已完成', value: 'completed' },
  { label: '已归档', value: 'archived' },
];

export const progressStatusOrder: ProjectProgressStatus[] = [
  'pending',
  'running',
  'completed',
  'archived',
];

export const progressStatusMap: Record<
  ProjectProgressStatus,
  { barColor: string; label: string; tokenColor: string }
> = {
  archived: {
    barColor: '#94a3b8',
    label: '已归档',
    tokenColor: '#94a3b8',
  },
  completed: {
    barColor: '#3b82f6',
    label: '已完成',
    tokenColor: '#3b82f6',
  },
  pending: {
    barColor: '#e2e8f0',
    label: '待启动',
    tokenColor: '#f59e0b',
  },
  running: {
    barColor: '#00c853',
    label: '进行中',
    tokenColor: '#10b981',
  },
};

const progressRows: ProjectProgressRecord[] = [
  {
    color: '#00c853',
    department: '中建三局',
    endMonth: 12,
    id: 1,
    manager: '张明远',
    milestones: [
      { label: '开工', month: 4 },
      { label: '主体完成', month: 10 },
    ],
    name: '城市中央商务区A栋综合体',
    progress: 55,
    startMonth: 4,
    status: 'running',
  },
  {
    color: '#3b82f6',
    department: '中铁十一局',
    endMonth: 12,
    id: 2,
    manager: '刘海沧',
    milestones: [{ label: '设计交底', month: 4 }],
    name: '滨河路市政道路改造工程',
    progress: 0,
    startMonth: 4,
    status: 'pending',
  },
  {
    color: '#8b5cf6',
    department: '东方园林',
    endMonth: 12,
    id: 3,
    manager: '林雨桐',
    milestones: [
      { label: '苗木进场', month: 2 },
      { label: '景观收口', month: 10 },
    ],
    name: '湿地公园景观绿化工程',
    progress: 73,
    startMonth: 2,
    status: 'running',
  },
  {
    color: '#f59e0b',
    department: '城建集团',
    endMonth: 12,
    id: 4,
    manager: '王建华',
    milestones: [{ label: '立项', month: 1 }],
    name: '老旧小区改造提升项目',
    progress: 0,
    startMonth: 1,
    status: 'pending',
  },
  {
    color: '#ec4899',
    department: '北控水务',
    endMonth: 12,
    id: 5,
    manager: '杨志刚',
    milestones: [{ label: '设备进场', month: 6 }],
    name: '城南污水处理厂扩容',
    progress: 42,
    startMonth: 6,
    status: 'running',
  },
  {
    color: '#10b981',
    department: '中建八局',
    endMonth: 12,
    id: 6,
    manager: '周敏',
    milestones: [{ label: '基坑完成', month: 5 }],
    name: '市民广场地下停车场',
    progress: 38,
    startMonth: 5,
    status: 'running',
  },
  {
    color: '#ef4444',
    department: '金蝶邮装饰',
    endMonth: 9,
    id: 7,
    manager: '陈思晴',
    milestones: [
      { label: '服务进场', month: 1 },
      { label: '季度验收', month: 5 },
    ],
    name: '园区保安保洁服务',
    progress: 65,
    startMonth: 1,
    status: 'completed',
  },
  {
    color: '#6366f1',
    department: '中交一公局',
    endMonth: 12,
    id: 8,
    manager: '赵国强',
    milestones: [{ label: '整改启动', month: 2 }],
    name: '车辆安全整改工程',
    progress: 0,
    startMonth: 2,
    status: 'pending',
  },
  {
    color: '#3b82f6',
    department: '金蝶邮装饰',
    endMonth: 12,
    id: 9,
    manager: '陈思涵',
    milestones: [{ label: '竣工', month: 12 }],
    name: '科技产业园精装修项目',
    progress: 100,
    startMonth: 1,
    status: 'completed',
  },
  {
    color: '#3b82f6',
    department: '中交二航局',
    endMonth: 12,
    id: 10,
    manager: '孙立良',
    milestones: [{ label: '验收', month: 12 }],
    name: '跨河大桥新建工程',
    progress: 100,
    startMonth: 1,
    status: 'completed',
  },
  {
    color: '#3b82f6',
    department: '中建三局',
    endMonth: 12,
    id: 11,
    manager: '张明远',
    milestones: [{ label: '验收', month: 12 }],
    name: '地铁3号线轨道维保',
    progress: 100,
    startMonth: 1,
    status: 'completed',
  },
  {
    color: '#3b82f6',
    department: '东方园林',
    endMonth: 12,
    id: 12,
    manager: '林雨桐',
    milestones: [{ label: '验收', month: 12 }],
    name: '隧道照明升级工程',
    progress: 100,
    startMonth: 1,
    status: 'completed',
  },
  {
    color: '#94a3b8',
    department: '中交二公司',
    endMonth: 12,
    id: 13,
    manager: '赵国强',
    milestones: [{ label: '归档', month: 12 }],
    name: '新区综合管廊工程',
    progress: 100,
    startMonth: 1,
    status: 'archived',
  },
  {
    color: '#94a3b8',
    department: '华为技术',
    endMonth: 12,
    id: 14,
    manager: '何晓峰',
    milestones: [{ label: '归档', month: 12 }],
    name: '智慧园区弱电系统工程',
    progress: 100,
    startMonth: 1,
    status: 'archived',
  },
  {
    color: '#94a3b8',
    department: '中铁十二局',
    endMonth: 12,
    id: 15,
    manager: '刘海涛',
    milestones: [{ label: '归档', month: 12 }],
    name: '特种设备年检整改',
    progress: 100,
    startMonth: 1,
    status: 'archived',
  },
];

export const projectProgressDepartmentOptions = [
  { label: '全部单位', value: '' },
  ...Array.from(new Set(progressRows.map((row) => row.department))).map(
    (department) => ({
      label: department,
      value: department,
    }),
  ),
];

export const projectProgressManagerOptions = [
  { label: '全部负责人', value: '' },
  ...Array.from(new Set(progressRows.map((row) => row.manager))).map(
    (manager) => ({
      label: manager,
      value: manager,
    }),
  ),
];

export async function getProjectProgressBoardList(
  query: ProjectProgressQuery = {},
) {
  return filterProjectProgressRows(query);
}

export async function getProjectProgressGanttList(
  query: ProjectProgressQuery = {},
) {
  return filterProjectProgressRows(query).slice(0, 8);
}

export async function getProjectProgressWarnings(
  query: ProjectProgressQuery = {},
): Promise<ProjectProgressWarning[]> {
  return filterProjectProgressRows(query)
    .filter((row) => row.status === 'running' && row.progress < 45)
    .map((row) => ({
      id: row.id,
      name: row.name,
      reason: `当前进度 ${row.progress}%，需关注节点推进。`,
    }));
}

function filterProjectProgressRows(query: ProjectProgressQuery) {
  const keyword = query.keyword?.trim();

  return progressRows.filter((row) => {
    const matchesKeyword = keyword
      ? row.name.includes(keyword) ||
        row.department.includes(keyword) ||
        row.manager.includes(keyword)
      : true;
    const matchesStatus = query.status ? row.status === query.status : true;
    const matchesDepartment = query.department
      ? row.department === query.department
      : true;
    const matchesManager = query.manager ? row.manager === query.manager : true;

    return (
      matchesKeyword && matchesStatus && matchesDepartment && matchesManager
    );
  });
}
