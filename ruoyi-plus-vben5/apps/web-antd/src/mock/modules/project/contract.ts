export type ContractPaymentStatus = 'completed' | 'running';
export type PaymentNodeStatus = 'paid' | 'pending' | 'upcoming';

export interface ContractPaymentNode {
  amount: number;
  label: string;
  percent: number;
  status: PaymentNodeStatus;
}

export interface ContractPaymentRecord {
  contractAmount: number;
  id: number;
  name: string;
  nodes: ContractPaymentNode[];
  status: ContractPaymentStatus;
}

export interface ContractPaymentStatCard {
  color?: string;
  title: string;
  type?: 'danger' | 'info' | 'primary' | 'success' | 'warning';
  unit: string;
  value: string;
}

export const contractPaymentStatusMap: Record<
  ContractPaymentStatus,
  { label: string; status: 'processing' | 'success' }
> = {
  completed: { label: '已完成', status: 'success' },
  running: { label: '进行中', status: 'processing' },
};

export const paymentNodeStatusMap: Record<
  PaymentNodeStatus,
  { color: string; label: string }
> = {
  paid: { color: '#10b981', label: '已支付' },
  pending: { color: '#f59e0b', label: '待审批' },
  upcoming: { color: '#cbd5e1', label: '未到期' },
};

const contractPaymentRows: ContractPaymentRecord[] = [
  createContractPaymentRecord({
    contractAmount: 28_485,
    id: 1,
    name: '城市中央商务区A栋综合体',
    nodeStatuses: ['paid', 'pending', 'upcoming'],
    status: 'running',
  }),
  createContractPaymentRecord({
    contractAmount: 45_744,
    id: 2,
    name: '科技产业园精装修项目',
    nodeStatuses: ['paid', 'paid', 'upcoming'],
    status: 'completed',
  }),
  createContractPaymentRecord({
    contractAmount: 39_461,
    id: 3,
    name: '湿地公园景观绿化工程',
    nodeStatuses: ['paid', 'pending', 'upcoming'],
    status: 'running',
  }),
  createContractPaymentRecord({
    contractAmount: 21_613,
    id: 4,
    name: '跨河大桥新建工程',
    nodeStatuses: ['paid', 'paid', 'upcoming'],
    status: 'completed',
  }),
  createContractPaymentRecord({
    contractAmount: 24_690,
    id: 5,
    name: '城南污水处理厂扩容',
    nodeStatuses: ['paid', 'pending', 'upcoming'],
    status: 'running',
  }),
  createContractPaymentRecord({
    contractAmount: 3409,
    id: 6,
    name: '地铁3号线轨道维保',
    nodeStatuses: ['paid', 'paid', 'upcoming'],
    status: 'completed',
  }),
  createContractPaymentRecord({
    contractAmount: 22_754,
    id: 7,
    name: '老旧小区改造提升项目',
    nodeStatuses: ['paid', 'pending', 'upcoming'],
    status: 'running',
  }),
  createContractPaymentRecord({
    contractAmount: 17_844,
    id: 8,
    name: '智慧园区弱电系统工程',
    nodeStatuses: ['paid', 'paid', 'upcoming'],
    status: 'completed',
  }),
];

export async function getContractPaymentList() {
  return contractPaymentRows;
}

export async function getContractPaymentStats(): Promise<
  ContractPaymentStatCard[]
> {
  return [
    {
      title: '合同总额',
      type: 'success',
      unit: '亿',
      value: '20.4',
    },
    {
      color: '#3b82f6',
      title: '已付金额',
      type: 'info',
      unit: '亿',
      value: '12.4',
    },
    {
      title: '待审批付款',
      type: 'warning',
      unit: '笔',
      value: '4',
    },
  ];
}

function createContractPaymentRecord(options: {
  contractAmount: number;
  id: number;
  name: string;
  nodeStatuses: [PaymentNodeStatus, PaymentNodeStatus, PaymentNodeStatus];
  status: ContractPaymentStatus;
}): ContractPaymentRecord {
  const nodeTemplates = [
    { label: '预付款', percent: 30 },
    { label: '中期款', percent: 40 },
    { label: '尾款', percent: 30 },
  ];

  return {
    contractAmount: options.contractAmount,
    id: options.id,
    name: options.name,
    nodes: nodeTemplates.map((node, index) => ({
      amount: Math.round((options.contractAmount * node.percent) / 100),
      label: node.label,
      percent: node.percent,
      status: options.nodeStatuses[index]!,
    })),
    status: options.status,
  };
}
