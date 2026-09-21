export type EvaluationProjectStage = 'acceptance' | 'midterm';
export type EvaluationProjectStatus = 'overdue' | 'waiting';
export type EvaluationRecordResult = 'excellent' | 'qualified';

export interface EvaluationProject {
  department: string;
  dueDate: string;
  id: number;
  manager: string;
  name: string;
  progress: number;
  stage: EvaluationProjectStage;
  status: EvaluationProjectStatus;
}

export interface EvaluationRecord {
  date: string;
  evaluator: string;
  id: number;
  name: string;
  result: EvaluationRecordResult;
  score: number;
}

export interface EvaluationStatCard {
  color?: string;
  title: string;
  type?: 'danger' | 'info' | 'primary' | 'success' | 'warning';
  unit: string;
  value: string;
}

export interface EvaluationSubmitPayload {
  evaluator: string;
  projectId: number;
  remark: string;
  score: number;
}

export const evaluationProjectStageMap: Record<
  EvaluationProjectStage,
  { label: string; status: 'processing' | 'warning' }
> = {
  acceptance: { label: '验收评估', status: 'processing' },
  midterm: { label: '中期评估', status: 'warning' },
};

export const evaluationProjectStatusMap: Record<
  EvaluationProjectStatus,
  { label: string; status: 'error' | 'warning' }
> = {
  overdue: { label: '逾期预警', status: 'error' },
  waiting: { label: '待评估', status: 'warning' },
};

export const evaluationRecordResultMap: Record<
  EvaluationRecordResult,
  { label: string; status: 'success' | 'warning' }
> = {
  excellent: { label: '优秀', status: 'success' },
  qualified: { label: '合格', status: 'warning' },
};

let pendingEvaluationProjects: EvaluationProject[] = [
  {
    department: '工程管理部',
    dueDate: '2026-05-20',
    id: 1,
    manager: '陈晓峰',
    name: '城市中央商务区A栋综合体',
    progress: 55,
    stage: 'midterm',
    status: 'waiting',
  },
  {
    department: '生态建设部',
    dueDate: '2026-05-24',
    id: 2,
    manager: '宋雨晴',
    name: '湿地公园景观绿化工程',
    progress: 73,
    stage: 'acceptance',
    status: 'waiting',
  },
  {
    department: '水务事业部',
    dueDate: '2026-05-18',
    id: 3,
    manager: '周立言',
    name: '城南污水处理厂扩容',
    progress: 41,
    stage: 'midterm',
    status: 'overdue',
  },
  {
    department: '园区运维部',
    dueDate: '2026-05-26',
    id: 4,
    manager: '许佳宁',
    name: '园区保安保洁服务',
    progress: 69,
    stage: 'acceptance',
    status: 'waiting',
  },
];

let evaluationRecords: EvaluationRecord[] = [
  {
    date: '2026-04-10',
    evaluator: '评估一组',
    id: 1,
    name: '科技产业园精装修项目',
    result: 'qualified',
    score: 82,
  },
  {
    date: '2026-04-13',
    evaluator: '验收专家组',
    id: 2,
    name: '新区综合管廊工程',
    result: 'excellent',
    score: 99,
  },
  {
    date: '2026-04-15',
    evaluator: '评估二组',
    id: 3,
    name: '跨河大桥新建工程',
    result: 'qualified',
    score: 88,
  },
  {
    date: '2026-04-28',
    evaluator: '信息化验收组',
    id: 4,
    name: '智慧园区弱电系统工程',
    result: 'excellent',
    score: 97,
  },
  {
    date: '2026-04-19',
    evaluator: '轨道交通评估组',
    id: 5,
    name: '地铁3号线轨道维保',
    result: 'excellent',
    score: 94,
  },
  {
    date: '2026-04-23',
    evaluator: '设备验收组',
    id: 6,
    name: '特种设备年检整改',
    result: 'qualified',
    score: 80,
  },
];

export async function getEvaluationProjects() {
  return pendingEvaluationProjects;
}

export async function getEvaluationRecords() {
  return evaluationRecords;
}

export async function getEvaluationStats(): Promise<EvaluationStatCard[]> {
  const totalScore = evaluationRecords.reduce(
    (sum, record) => sum + record.score,
    0,
  );
  const averageScore = Math.round(totalScore / evaluationRecords.length);

  return [
    {
      title: '已评估项目',
      type: 'success',
      unit: '',
      value: String(evaluationRecords.length + 1),
    },
    {
      title: '待评估项目',
      type: 'warning',
      unit: '',
      value: String(pendingEvaluationProjects.length),
    },
    {
      color: '#3b82f6',
      title: '平均评估得分',
      type: 'info',
      unit: '',
      value: String(averageScore),
    },
  ];
}

export async function submitEvaluation(payload: EvaluationSubmitPayload) {
  const project = pendingEvaluationProjects.find(
    (item) => item.id === payload.projectId,
  );

  if (!project) {
    return;
  }

  const score = Math.max(0, Math.min(100, Math.round(payload.score)));
  const nextId = Math.max(...evaluationRecords.map((item) => item.id)) + 1;

  evaluationRecords = [
    {
      date: new Date().toISOString().slice(0, 10),
      evaluator: payload.evaluator || '项目管理部',
      id: nextId,
      name: project.name,
      result: score >= 90 ? 'excellent' : 'qualified',
      score,
    },
    ...evaluationRecords,
  ];

  pendingEvaluationProjects = pendingEvaluationProjects.filter(
    (item) => item.id !== payload.projectId,
  );
}
