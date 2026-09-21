import type { PlatformNoticeListItem } from '@st/platform-ui';

export interface AccessRuleItem {
  description: string;
  icon: string;
  id: number | string;
  statusText: string;
  title: string;
  type: 'danger' | 'info' | 'primary' | 'success' | 'warning';
}

export interface QualificationAccessStatCard {
  description?: string;
  icon?: string;
  title: string;
  type: 'danger' | 'info' | 'primary' | 'success' | 'warning';
  value: number | string;
}

interface QualificationWarningSeed {
  certificate: string;
  expireDate: string;
  id: number;
  name: string;
  remindedAt?: string;
}

const qualificationWarningSeed: QualificationWarningSeed[] = [
  {
    certificate: '特种作业操作证',
    expireDate: '2026-01-01',
    id: 1,
    name: '李建国',
  },
  {
    certificate: '上岗证',
    expireDate: '2026-05-05',
    id: 2,
    name: '马超',
  },
  {
    certificate: '职业资格证',
    expireDate: '2026-01-13',
    id: 3,
    name: '蔡明',
  },
  {
    certificate: '安全生产考核合格证',
    expireDate: '2026-05-17',
    id: 4,
    name: '徐达',
  },
  {
    certificate: '上岗证',
    expireDate: '2026-01-25',
    id: 5,
    name: '韩宽',
  },
  {
    certificate: '技能鉴定证书',
    expireDate: '2026-05-01',
    id: 6,
    name: '李白',
  },
];

let qualificationWarnings = [...qualificationWarningSeed];

export async function getQualificationAccessStats(): Promise<
  QualificationAccessStatCard[]
> {
  return [
    {
      description: '30天内到期需复审',
      icon: 'lucide:calendar-clock',
      title: '资质即将到期',
      type: 'warning',
      value: qualificationWarnings.length,
    },
    {
      description: '需要补录资质信息',
      icon: 'lucide:file-x-2',
      title: '无资质在岗',
      type: 'danger',
      value: 7,
    },
    {
      description: '自动拦截中',
      icon: 'lucide:shield-x',
      title: '准入不合规',
      type: 'info',
      value: 3,
    },
  ];
}

export async function getQualificationWarningList(): Promise<
  PlatformNoticeListItem[]
> {
  return qualificationWarnings.map((item) => ({
    actionText: item.remindedAt ? '再次提醒' : '发送提醒',
    description: item.remindedAt ? `已提醒 ${item.remindedAt}` : undefined,
    id: item.id,
    meta: `${item.certificate} - 到期 ${item.expireDate}`,
    status: 'warning',
    tag: '即将到期',
    title: item.name,
  }));
}

export async function getAccessRuleList(): Promise<AccessRuleItem[]> {
  return [
    {
      description: '男不超过60周岁，女不超过55周岁',
      icon: 'lucide:calendar-days',
      id: 'age',
      statusText: '已启用',
      title: '年龄限制',
      type: 'success',
    },
    {
      description: '特种作业必须持有有效操作证',
      icon: 'lucide:id-card',
      id: 'qualification',
      statusText: '已启用',
      title: '资质要求',
      type: 'info',
    },
    {
      description: '身体状况必须为“正常”',
      icon: 'lucide:heart-pulse',
      id: 'health',
      statusText: '已启用',
      title: '体检要求',
      type: 'danger',
    },
    {
      description: '黑名单人员自动禁止入场',
      icon: 'lucide:ban',
      id: 'blacklist',
      statusText: '已启用',
      title: '黑名单拦截',
      type: 'primary',
    },
  ];
}

export async function sendQualificationReminder(id: number | string) {
  const now = new Date();
  const remindedAt = `${String(now.getMonth() + 1).padStart(2, '0')}-${String(
    now.getDate(),
  ).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(
    now.getMinutes(),
  ).padStart(2, '0')}`;

  qualificationWarnings = qualificationWarnings.map((item) =>
    item.id === Number(id) ? { ...item, remindedAt } : item,
  );
}
