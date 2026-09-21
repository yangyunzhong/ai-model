export type PersonnelArchiveEmploymentStatus = 'active' | 'pending' | 'resigned';
export type PersonnelArchiveHealthStatus = 'abnormal' | 'normal' | 'review';
export type PersonnelArchiveQualificationStatus =
  | 'expired'
  | 'none'
  | 'valid'
  | 'warning';
export type PersonnelArchiveBlacklistStatus = 'blocked' | 'normal' | 'restricted';

export interface PersonnelArchiveQuery {
  keyword: string;
}

export interface PersonnelArchiveFormModel {
  age?: string;
  contractor?: string;
  gender?: string;
  healthStatus?: string;
  idCard?: string;
  name?: string;
  phone?: string;
  position?: string;
  project?: string;
  qualificationCert?: string;
  qualificationExpireDate?: string;
  startDate?: string;
}

export interface PersonnelArchiveRecord {
  archiveNo: string;
  age: string;
  blacklistStatus: PersonnelArchiveBlacklistStatus;
  contractor: string;
  endDate?: string;
  gender: string;
  healthStatus: PersonnelArchiveHealthStatus;
  id: string;
  idCard: string;
  monthlyHours: number;
  monthlyHoursLimit: number;
  name: string;
  phone: string;
  position: string;
  project: string;
  qualificationCert: string;
  qualificationExpireDate: string;
  qualificationName: string;
  qualificationStatus: PersonnelArchiveQualificationStatus;
  status: PersonnelArchiveEmploymentStatus;
  startDate: string;
  theme: string;
}

export const genderOptions = [
  { label: '男', value: '男' },
  { label: '女', value: '女' },
];

export const healthStatusOptions = [
  { label: '正常', value: '正常' },
  { label: '需复查', value: '需复查' },
  { label: '异常', value: '异常' },
];

export const projectOptions = [
  { label: '城市中央商务区A栋综合体', value: '城市中央商务区A栋综合体' },
  { label: '滨河路市政道路改造工程', value: '滨河路市政道路改造工程' },
  { label: '科技产业园精装修项目', value: '科技产业园精装修项目' },
  { label: '新区综合管廊工程', value: '新区综合管廊工程' },
  { label: '湿地公园景观绿化工程', value: '湿地公园景观绿化工程' },
  { label: '老旧小区改造提升项目', value: '老旧小区改造提升项目' },
  { label: '跨河大桥新建工程', value: '跨河大桥新建工程' },
  { label: '智慧园区弱电系统工程', value: '智慧园区弱电系统工程' },
];

export const positionOptions = [
  { label: '项目经理', value: '项目经理' },
  { label: '技术负责人', value: '技术负责人' },
  { label: '安全员', value: '安全员' },
  { label: '质量员', value: '质量员' },
  { label: '施工员', value: '施工员' },
  { label: '电工', value: '电工' },
  { label: '焊工', value: '焊工' },
  { label: '架子工', value: '架子工' },
  { label: '起重工', value: '起重工' },
  { label: '保洁员', value: '保洁员' },
];

export const personnelArchiveEmploymentStatusMap: Record<
  PersonnelArchiveEmploymentStatus,
  {
    label: string;
    status: 'default' | 'processing' | 'success' | 'warning';
  }
> = {
  active: { label: '在岗', status: 'success' },
  pending: { label: '待审核', status: 'warning' },
  resigned: { label: '已离职', status: 'default' },
};

export const personnelArchiveHealthStatusMap: Record<
  PersonnelArchiveHealthStatus,
  {
    label: string;
    tone: 'danger' | 'success' | 'warning';
  }
> = {
  abnormal: { label: '体检异常', tone: 'danger' },
  normal: { label: '体检正常', tone: 'success' },
  review: { label: '需复查', tone: 'warning' },
};

export const personnelArchiveQualificationStatusMap: Record<
  PersonnelArchiveQualificationStatus,
  {
    label: string;
    tone: 'danger' | 'info' | 'success' | 'warning';
  }
> = {
  expired: { label: '已过期', tone: 'danger' },
  none: { label: '无资质', tone: 'info' },
  valid: { label: '正常', tone: 'success' },
  warning: { label: '即将到期', tone: 'warning' },
};

export const personnelArchiveBlacklistStatusMap: Record<
  PersonnelArchiveBlacklistStatus,
  {
    label: string;
    status: 'default' | 'error' | 'success' | 'warning';
  }
> = {
  blocked: { label: '黑名单', status: 'error' },
  normal: { label: '正常', status: 'success' },
  restricted: { label: '受限', status: 'warning' },
};

export const personnelArchiveMonthlyHoursLimit = 180;

const archivePalette = [
  '#10b981',
  '#3b82f6',
  '#8b5cf6',
  '#ec4899',
  '#f59e0b',
  '#14b8a6',
  '#ef4444',
  '#6366f1',
];

const personnelSeed = [
  createArchiveRecord({
    age: '38',
    archiveNo: 'PW2026001',
    blacklistStatus: 'normal',
    contractor: '中建三局',
    endDate: '2026-01-15',
    gender: '男',
    healthStatus: 'abnormal',
    id: 'PW2026001',
    idCard: '4201191976****9278',
    monthlyHours: 196,
    monthlyHoursLimit: 180,
    name: '李建国',
    phone: '13880673896',
    position: '项目经理',
    project: '城市中央商务区A栋综合体',
    qualificationCert: '特种作业操作证',
    qualificationExpireDate: '2026-01-01',
    qualificationName: '特种作业操作证',
    qualificationStatus: 'warning',
    status: 'resigned',
    startDate: '2025-01-01',
    theme: '#10b981',
  }),
  createArchiveRecord({
    age: '43',
    archiveNo: 'PW2026002',
    blacklistStatus: 'normal',
    contractor: '中铁十二局',
    gender: '男',
    healthStatus: 'normal',
    id: 'PW2026002',
    idCard: '4201191982****5148',
    monthlyHours: 171,
    monthlyHoursLimit: 180,
    name: '王志强',
    phone: '13928147209',
    position: '技术负责人',
    project: '滨河路市政道路改造工程',
    qualificationCert: '安全生产证',
    qualificationExpireDate: '2026-08-16',
    qualificationName: '安全生产证',
    qualificationStatus: 'valid',
    status: 'active',
    startDate: '2025-02-02',
    theme: '#3b82f6',
  }),
  createArchiveRecord({
    age: '44',
    archiveNo: 'PW2026003',
    blacklistStatus: 'normal',
    contractor: '金螳螂装饰',
    gender: '女',
    healthStatus: 'normal',
    id: 'PW2026003',
    idCard: '4201191981****6188',
    monthlyHours: 158,
    monthlyHoursLimit: 180,
    name: '赵丽华',
    phone: '13702471682',
    position: '安全员',
    project: '科技产业园精装修项目',
    qualificationCert: '职业资格证',
    qualificationExpireDate: '2027-03-03',
    qualificationName: '职业资格证',
    qualificationStatus: 'valid',
    status: 'active',
    startDate: '2025-03-03',
    theme: '#8b5cf6',
  }),
  createArchiveRecord({
    age: '40',
    archiveNo: 'PW2026004',
    blacklistStatus: 'normal',
    contractor: '中交一公局',
    gender: '男',
    healthStatus: 'normal',
    id: 'PW2026004',
    idCard: '4201191984****2268',
    monthlyHours: 165,
    monthlyHoursLimit: 180,
    name: '孙伟',
    phone: '13612488752',
    position: '质量员',
    project: '新区综合管廊工程',
    qualificationCert: '上岗证',
    qualificationExpireDate: '2027-04-04',
    qualificationName: '上岗证',
    qualificationStatus: 'valid',
    status: 'active',
    startDate: '2025-04-04',
    theme: '#ec4899',
  }),
  createArchiveRecord({
    age: '33',
    archiveNo: 'PW2026005',
    blacklistStatus: 'normal',
    contractor: '东方园林',
    gender: '男',
    healthStatus: 'normal',
    id: 'PW2026005',
    idCard: '4201191992****4488',
    monthlyHours: 153,
    monthlyHoursLimit: 180,
    name: '马超',
    phone: '13568871245',
    position: '施工员',
    project: '湿地公园景观绿化工程',
    qualificationCert: '职业资格证',
    qualificationExpireDate: '2026-12-20',
    qualificationName: '职业资格证',
    qualificationStatus: 'warning',
    status: 'active',
    startDate: '2025-05-05',
    theme: '#f59e0b',
  }),
  createArchiveRecord({
    age: '31',
    archiveNo: 'PW2026006',
    blacklistStatus: 'normal',
    contractor: '城建集团',
    gender: '女',
    healthStatus: 'abnormal',
    id: 'PW2026006',
    idCard: '4201191994****7728',
    monthlyHours: 149,
    monthlyHoursLimit: 180,
    name: '黄磊',
    phone: '13811224773',
    position: '焊工',
    project: '老旧小区改造提升项目',
    qualificationCert: '特种作业操作证',
    qualificationExpireDate: '2026-05-06',
    qualificationName: '特种作业操作证',
    qualificationStatus: 'warning',
    status: 'pending',
    startDate: '2025-06-06',
    theme: '#14b8a6',
  }),
  createArchiveRecord({
    age: '40',
    archiveNo: 'PW2026007',
    blacklistStatus: 'normal',
    contractor: '华为技术',
    gender: '女',
    healthStatus: 'normal',
    id: 'PW2026007',
    idCard: '4201191991****6638',
    monthlyHours: 174,
    monthlyHoursLimit: 180,
    name: '林小芳',
    phone: '13956273380',
    position: '电工',
    project: '跨河大桥新建工程',
    qualificationCert: '上岗证',
    qualificationExpireDate: '2026-10-12',
    qualificationName: '上岗证',
    qualificationStatus: 'valid',
    status: 'active',
    startDate: '2025-07-07',
    theme: '#ef4444',
  }),
  createArchiveRecord({
    age: '32',
    archiveNo: 'PW2026008',
    blacklistStatus: 'normal',
    contractor: '北控水务',
    gender: '男',
    healthStatus: 'normal',
    id: 'PW2026008',
    idCard: '4201191986****1208',
    monthlyHours: 162,
    monthlyHoursLimit: 180,
    name: '郑浩',
    phone: '13822611790',
    position: '架子工',
    project: '智慧园区弱电系统工程',
    qualificationCert: '职业资格证',
    qualificationExpireDate: '2027-08-08',
    qualificationName: '职业资格证',
    qualificationStatus: 'valid',
    status: 'resigned',
    startDate: '2025-08-08',
    theme: '#6366f1',
  }),
  createArchiveRecord({
    age: '31',
    archiveNo: 'PW2026009',
    blacklistStatus: 'normal',
    contractor: '中建八局',
    gender: '男',
    healthStatus: 'normal',
    id: 'PW2026009',
    idCard: '4201191993****4338',
    monthlyHours: 168,
    monthlyHoursLimit: 180,
    name: '钱进',
    phone: '13777581109',
    position: '起重工',
    project: '城南污水处理厂扩容',
    qualificationCert: '特种作业操作证',
    qualificationExpireDate: '2026-11-09',
    qualificationName: '特种作业操作证',
    qualificationStatus: 'valid',
    status: 'active',
    startDate: '2025-09-09',
    theme: '#0ea5e9',
  }),
  createArchiveRecord({
    age: '44',
    archiveNo: 'PW2026010',
    blacklistStatus: 'normal',
    contractor: '保安公司',
    gender: '女',
    healthStatus: 'normal',
    id: 'PW2026010',
    idCard: '4201191980****3328',
    monthlyHours: 139,
    monthlyHoursLimit: 180,
    name: '杨柳',
    phone: '13633457221',
    position: '保洁员',
    project: '市民广场地下停车场',
    qualificationCert: '上岗证',
    qualificationExpireDate: '2026-09-30',
    qualificationName: '上岗证',
    qualificationStatus: 'valid',
    status: 'active',
    startDate: '2025-10-10',
    theme: '#22c55e',
  }),
  createArchiveRecord({
    age: '43',
    archiveNo: 'PW2026011',
    blacklistStatus: 'restricted',
    contractor: '中建三局',
    gender: '男',
    healthStatus: 'review',
    id: 'PW2026011',
    idCard: '4201191979****2948',
    monthlyHours: 188,
    monthlyHoursLimit: 180,
    name: '何青',
    phone: '13788992214',
    position: '保安',
    project: '地铁3号线轨道维保',
    qualificationCert: '职业资格证',
    qualificationExpireDate: '2026-04-14',
    qualificationName: '职业资格证',
    qualificationStatus: 'expired',
    status: 'pending',
    startDate: '2025-11-11',
    theme: '#60a5fa',
  }),
  createArchiveRecord({
    age: '35',
    archiveNo: 'PW2026012',
    blacklistStatus: 'normal',
    contractor: '中铁十二局',
    gender: '男',
    healthStatus: 'normal',
    id: 'PW2026012',
    idCard: '4201191988****5578',
    monthlyHours: 176,
    monthlyHoursLimit: 180,
    name: '丁一',
    phone: '13891027733',
    position: '巡检员',
    project: '滨海新区道路照明工程',
    qualificationCert: '安全生产证',
    qualificationExpireDate: '2026-12-12',
    qualificationName: '安全生产证',
    qualificationStatus: 'valid',
    status: 'active',
    startDate: '2025-12-12',
    theme: '#a855f7',
  }),
  createArchiveRecord({
    age: '26',
    archiveNo: 'PW2026013',
    blacklistStatus: 'normal',
    contractor: '城建集团',
    gender: '男',
    healthStatus: 'normal',
    id: 'PW2026013',
    idCard: '4201191998****3668',
    monthlyHours: 163,
    monthlyHoursLimit: 180,
    name: '柳宗元',
    phone: '13918226477',
    position: '质量员',
    project: '城市中央商务区A栋综合体',
    qualificationCert: '上岗证',
    qualificationExpireDate: '2027-02-20',
    qualificationName: '上岗证',
    qualificationStatus: 'valid',
    status: 'pending',
    startDate: '2026-01-11',
    theme: '#6366f1',
  }),
  createArchiveRecord({
    age: '30',
    archiveNo: 'PW2026014',
    blacklistStatus: 'normal',
    contractor: '华为技术',
    gender: '男',
    healthStatus: 'normal',
    id: 'PW2026014',
    idCard: '4201191996****1128',
    monthlyHours: 170,
    monthlyHoursLimit: 180,
    name: '白居易',
    phone: '13744791882',
    position: '施工员',
    project: '科技产业园精装修项目',
    qualificationCert: '特种作业操作证',
    qualificationExpireDate: '2027-04-30',
    qualificationName: '特种作业操作证',
    qualificationStatus: 'valid',
    status: 'active',
    startDate: '2026-02-12',
    theme: '#14b8a6',
  }),
  createArchiveRecord({
    age: '48',
    archiveNo: 'PW2026015',
    blacklistStatus: 'normal',
    contractor: '北控水务',
    gender: '男',
    healthStatus: 'normal',
    id: 'PW2026015',
    idCard: '4201191978****7748',
    monthlyHours: 182,
    monthlyHoursLimit: 180,
    name: '杜甫',
    phone: '13652298844',
    position: '焊工',
    project: '新区综合管廊工程',
    qualificationCert: '职业资格证',
    qualificationExpireDate: '2026-11-22',
    qualificationName: '职业资格证',
    qualificationStatus: 'valid',
    status: 'active',
    startDate: '2026-03-03',
    theme: '#22c55e',
  }),
  createArchiveRecord({
    age: '30',
    archiveNo: 'PW2026016',
    blacklistStatus: 'normal',
    contractor: '保安公司',
    gender: '女',
    healthStatus: 'normal',
    id: 'PW2026016',
    idCard: '4201191997****9828',
    monthlyHours: 145,
    monthlyHoursLimit: 180,
    name: '李白',
    phone: '13926517783',
    position: '电工',
    project: '市民广场地下停车场',
    qualificationCert: '上岗证',
    qualificationExpireDate: '2027-06-08',
    qualificationName: '上岗证',
    qualificationStatus: 'valid',
    status: 'resigned',
    startDate: '2026-04-18',
    theme: '#3b82f6',
  }),
] as PersonnelArchiveRecord[];

let personnelRows = [...personnelSeed];

export async function getPersonnelArchiveList(query: PersonnelArchiveQuery) {
  const keyword = query.keyword.trim().toLowerCase();

  if (!keyword) {
    return personnelRows;
  }

  return personnelRows.filter((item) =>
    [
      item.archiveNo,
      item.contractor,
      item.name,
      item.position,
      item.project,
      item.qualificationCert,
    ].some((value) => value.toLowerCase().includes(keyword)),
  );
}

export async function createPersonnelArchiveRecord(
  form: PersonnelArchiveFormModel,
) {
  const nextIndex = personnelRows.length + 1;
  const archiveNo = `PW2026${String(nextIndex).padStart(3, '0')}`;
  const status = getQualificationStatus(form.qualificationExpireDate);
  const theme = archivePalette[(nextIndex - 1) % archivePalette.length] ?? '#10b981';

  personnelRows = [
    createArchiveRecord({
      age: form.age || '30',
      archiveNo,
      blacklistStatus: 'normal',
      contractor: form.contractor || '待分配承包商',
      gender: form.gender || '男',
      healthStatus: mapHealthStatus(form.healthStatus),
      id: archiveNo,
      idCard: form.idCard || '待补充',
      monthlyHours: 0,
      monthlyHoursLimit: personnelArchiveMonthlyHoursLimit,
      name: form.name || '新增人员',
      phone: form.phone || '待补充',
      position: form.position || '待分配岗位',
      project: form.project || '待分配项目',
      qualificationCert: form.qualificationCert || '待补充',
      qualificationExpireDate: form.qualificationExpireDate || '',
      qualificationName: form.qualificationCert || '待补充',
      qualificationStatus: status,
      status: 'pending',
      startDate: form.startDate || '2026-01-01',
      theme,
    }),
    ...personnelRows,
  ];
}

export async function removePersonnelArchiveRecord(id: string) {
  personnelRows = personnelRows.filter((item) => item.id !== id);
}

export function getPersonnelArchiveEmploymentMeta(
  status: PersonnelArchiveEmploymentStatus,
) {
  return personnelArchiveEmploymentStatusMap[status];
}

export function getPersonnelArchiveHealthMeta(
  status: PersonnelArchiveHealthStatus,
) {
  return personnelArchiveHealthStatusMap[status];
}

export function getPersonnelArchiveQualificationMeta(
  status: PersonnelArchiveQualificationStatus,
) {
  return personnelArchiveQualificationStatusMap[status];
}

export function getPersonnelArchiveBlacklistMeta(
  status: PersonnelArchiveBlacklistStatus,
) {
  return personnelArchiveBlacklistStatusMap[status];
}

export function getPersonnelArchiveInitial(name: string) {
  return name.slice(0, 1);
}

export function getPersonnelArchiveTags(record: PersonnelArchiveRecord) {
  return [
    getPersonnelArchiveHealthMeta(record.healthStatus),
    getPersonnelArchiveQualificationMeta(record.qualificationStatus),
  ];
}

export function getPersonnelArchiveSectionValue(value?: string) {
  return value || '未填写';
}

export function maskIdCard(value: string) {
  if (!value || value === '待补充') {
    return value || '未填写';
  }

  return value.length > 8 ? `${value.slice(0, 6)}****${value.slice(-4)}` : value;
}

function createArchiveRecord(options: PersonnelArchiveRecord): PersonnelArchiveRecord {
  return options;
}

function getQualificationStatus(
  expireDate?: string,
): PersonnelArchiveQualificationStatus {
  if (!expireDate) {
    return 'none';
  }

  const expire = new Date(expireDate).getTime();
  const now = Date.now();
  const warningLine = now + 1000 * 60 * 60 * 24 * 30;

  if (expire < now) {
    return 'expired';
  }

  return expire <= warningLine ? 'warning' : 'valid';
}

function mapHealthStatus(value?: string): PersonnelArchiveHealthStatus {
  if (value === '异常') {
    return 'abnormal';
  }
  if (value === '需复查') {
    return 'review';
  }
  return 'normal';
}
