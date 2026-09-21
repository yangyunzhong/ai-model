export type PersonnelEmploymentStatus = 'active' | 'pending' | 'resigned';
export type PersonnelQualificationStatus =
  | 'expired'
  | 'none'
  | 'valid'
  | 'warning';

export interface PersonnelOverviewQuery {
  keyword: string;
  qualificationStatus: '' | PersonnelQualificationStatus;
  status: '' | PersonnelEmploymentStatus;
}

export interface PersonnelOverviewFormModel {
  age?: number | string;
  contractor?: string;
  gender?: string;
  health?: string;
  id?: number;
  idCard?: string;
  name?: string;
  phone?: string;
  position?: string;
  project?: string;
  qualificationExpireDate?: string;
  qualificationName?: string;
  qualificationStatus?: PersonnelQualificationStatus;
  startDate?: string;
  status?: PersonnelEmploymentStatus;
}

export interface PersonnelOverviewRecord {
  age: number;
  code: string;
  contractor: string;
  gender: string;
  health: string;
  id: number;
  idCard: string;
  name: string;
  phone: string;
  position: string;
  project: string;
  qualificationExpireDate: string;
  qualificationName: string;
  qualificationStatus: PersonnelQualificationStatus;
  startDate: string;
  status: PersonnelEmploymentStatus;
}

export interface PersonnelOverviewStatCard {
  icon: string;
  title: string;
  trendText: string;
  trendType: 'down' | 'neutral' | 'up';
  type: 'danger' | 'info' | 'primary' | 'success' | 'warning';
  value: number;
}

export const personnelStatusOptions = [
  { label: '全部状态', value: '' },
  { label: '在岗', value: 'active' },
  { label: '待审核', value: 'pending' },
  { label: '已离职', value: 'resigned' },
];

export const qualificationStatusOptions = [
  { label: '全部资质', value: '' },
  { label: '有效', value: 'valid' },
  { label: '即将到期', value: 'warning' },
  { label: '已过期', value: 'expired' },
  { label: '无资质', value: 'none' },
];

export const personnelGenderOptions = [
  { label: '男', value: '男' },
  { label: '女', value: '女' },
];

export const personnelHealthOptions = [
  { label: '健康', value: '健康' },
  { label: '复查中', value: '复查中' },
  { label: '异常', value: '异常' },
];

export const personnelContractorOptions = [
  { label: '新疆华能建设有限公司', value: '新疆华能建设有限公司' },
  { label: '中电建新疆工程公司', value: '中电建新疆工程公司' },
  { label: '天山能源服务有限公司', value: '天山能源服务有限公司' },
  { label: '西域运维科技有限公司', value: '西域运维科技有限公司' },
];

export const personnelProjectOptions = [
  { label: '新疆新能源基地运维项目', value: '新疆新能源基地运维项目' },
  { label: '昌吉储能站检修项目', value: '昌吉储能站检修项目' },
  { label: '哈密风电场扩容项目', value: '哈密风电场扩容项目' },
  { label: '吐鲁番光伏巡检项目', value: '吐鲁番光伏巡检项目' },
];

export const personnelPositionOptions = [
  { label: '电气检修工', value: '电气检修工' },
  { label: '安全员', value: '安全员' },
  { label: '项目经理', value: '项目经理' },
  { label: '资料员', value: '资料员' },
  { label: '高处作业人员', value: '高处作业人员' },
];

export const personnelStatusMap: Record<
  PersonnelEmploymentStatus,
  { label: string; status: 'default' | 'success' | 'warning' }
> = {
  active: { label: '在岗', status: 'success' },
  pending: { label: '待审核', status: 'warning' },
  resigned: { label: '已离职', status: 'default' },
};

export const qualificationStatusMap: Record<
  PersonnelQualificationStatus,
  { label: string; status: 'default' | 'error' | 'success' | 'warning' }
> = {
  expired: { label: '已过期', status: 'error' },
  none: { label: '无资质', status: 'default' },
  valid: { label: '有效', status: 'success' },
  warning: { label: '即将到期', status: 'warning' },
};

const personnelSeed: PersonnelOverviewRecord[] = [
  createRecord(1, '李建国', '男', 42, '13899110001', '新疆华能建设有限公司', '新疆新能源基地运维项目', '电气检修工', '2024-03-12', 'active', '高压电工作业证', '2027-04-18', 'valid'),
  createRecord(2, '王志强', '男', 36, '13899110002', '中电建新疆工程公司', '昌吉储能站检修项目', '安全员', '2024-05-20', 'active', '安全员C证', '2026-06-12', 'warning'),
  createRecord(3, '赵丽华', '女', 31, '13899110003', '天山能源服务有限公司', '哈密风电场扩容项目', '资料员', '2025-01-08', 'pending', '资料员证', '2028-01-08', 'valid'),
  createRecord(4, '孙伟', '男', 45, '13899110004', '西域运维科技有限公司', '吐鲁番光伏巡检项目', '项目经理', '2023-11-18', 'active', '一级建造师', '2026-12-20', 'valid'),
  createRecord(5, '马超', '男', 29, '13899110005', '新疆华能建设有限公司', '新疆新能源基地运维项目', '高处作业人员', '2025-03-01', 'active', '高处作业证', '2026-05-30', 'warning'),
  createRecord(6, '黄磊', '男', 39, '13899110006', '中电建新疆工程公司', '昌吉储能站检修项目', '电气检修工', '2024-09-15', 'resigned', '低压电工作业证', '2025-12-01', 'expired'),
  createRecord(7, '林小芳', '女', 28, '13899110007', '天山能源服务有限公司', '哈密风电场扩容项目', '资料员', '2025-04-16', 'active', '', '', 'none'),
  createRecord(8, '郑浩', '男', 33, '13899110008', '西域运维科技有限公司', '吐鲁番光伏巡检项目', '安全员', '2024-07-09', 'pending', '安全员C证', '2027-02-28', 'valid'),
  createRecord(9, '钱进', '男', 41, '13899110009', '新疆华能建设有限公司', '新疆新能源基地运维项目', '项目经理', '2023-06-02', 'active', '注册安全工程师', '2028-09-30', 'valid'),
  createRecord(10, '杨柳', '女', 30, '13899110010', '中电建新疆工程公司', '昌吉储能站检修项目', '资料员', '2025-02-14', 'active', '资料员证', '2027-08-22', 'valid'),
  createRecord(11, '何青', '女', 35, '13899110011', '天山能源服务有限公司', '哈密风电场扩容项目', '安全员', '2024-12-10', 'active', '安全员C证', '2026-05-18', 'warning'),
  createRecord(12, '丁一', '男', 27, '13899110012', '西域运维科技有限公司', '吐鲁番光伏巡检项目', '高处作业人员', '2025-05-01', 'pending', '', '', 'none'),
];

let personnelRows = [...personnelSeed];

export async function getPersonnelOverviewList(query: PersonnelOverviewQuery) {
  const keyword = query.keyword.trim().toLowerCase();

  return personnelRows.filter((item) => {
    const matchKeyword =
      !keyword ||
      [item.code, item.name, item.contractor, item.project, item.position].some(
        (value) => value.toLowerCase().includes(keyword),
      );
    const matchStatus = !query.status || item.status === query.status;
    const matchQualification =
      !query.qualificationStatus ||
      item.qualificationStatus === query.qualificationStatus;

    return matchKeyword && matchStatus && matchQualification;
  });
}

export async function getPersonnelOverviewStats(): Promise<
  PersonnelOverviewStatCard[]
> {
  const contractorCount = new Set(personnelRows.map((item) => item.contractor))
    .size;

  return [
    {
      icon: 'lucide:users',
      title: '在岗人数',
      trendText: '当前项目现场人员',
      trendType: 'up',
      type: 'success',
      value: countByStatus('active'),
    },
    {
      icon: 'lucide:user-check',
      title: '待审核',
      trendText: '待完成入场审核',
      trendType: 'neutral',
      type: 'warning',
      value: countByStatus('pending'),
    },
    {
      icon: 'lucide:user-x',
      title: '已离职',
      trendText: '历史退场人员',
      trendType: 'neutral',
      type: 'info',
      value: countByStatus('resigned'),
    },
    {
      icon: 'lucide:badge-alert',
      title: '资质到期预警',
      trendText: '证书即将到期或已过期',
      trendType: 'down',
      type: 'danger',
      value: personnelRows.filter((item) =>
        ['expired', 'warning'].includes(item.qualificationStatus),
      ).length,
    },
    {
      icon: 'lucide:building-2',
      title: '承包商数量',
      trendText: '当前人员所属承包商',
      trendType: 'neutral',
      type: 'primary',
      value: contractorCount,
    },
  ];
}

export async function savePersonnelOverviewRecord(
  form: PersonnelOverviewFormModel,
) {
  const id = Number(form.id);
  const nextRecord: PersonnelOverviewRecord = {
    age: Number(form.age) || 0,
    code:
      id > 0
        ? (personnelRows.find((item) => item.id === id)?.code ?? createCode(id))
        : createCode(personnelRows.length + 1),
    contractor: form.contractor ?? '',
    gender: form.gender ?? '',
    health: form.health ?? '健康',
    id: id > 0 ? id : Date.now(),
    idCard: form.idCard ?? '',
    name: form.name ?? '',
    phone: form.phone ?? '',
    position: form.position ?? '',
    project: form.project ?? '',
    qualificationExpireDate: form.qualificationExpireDate ?? '',
    qualificationName: form.qualificationName ?? '',
    qualificationStatus:
      form.qualificationStatus ??
      getQualificationStatus(
        form.qualificationName,
        form.qualificationExpireDate,
      ),
    startDate: form.startDate ?? '',
    status: form.status ?? 'pending',
  };

  if (id > 0) {
    personnelRows = personnelRows.map((item) =>
      item.id === id ? nextRecord : item,
    );
    return nextRecord;
  }

  personnelRows = [nextRecord, ...personnelRows];
  return nextRecord;
}

export async function removePersonnelOverviewRecord(id: number) {
  personnelRows = personnelRows.filter((item) => item.id !== id);
}

export function getPersonnelStatusMeta(status: PersonnelEmploymentStatus) {
  return personnelStatusMap[status];
}

export function getQualificationStatusMeta(
  status: PersonnelQualificationStatus,
) {
  return qualificationStatusMap[status];
}

function createRecord(
  id: number,
  name: string,
  gender: string,
  age: number,
  phone: string,
  contractor: string,
  project: string,
  position: string,
  startDate: string,
  status: PersonnelEmploymentStatus,
  qualificationName: string,
  qualificationExpireDate: string,
  qualificationStatus: PersonnelQualificationStatus,
): PersonnelOverviewRecord {
  return {
    age,
    code: createCode(id),
    contractor,
    gender,
    health: '健康',
    id,
    idCard: `650100198${id.toString().padStart(2, '0')}01001${id}`,
    name,
    phone,
    position,
    project,
    qualificationExpireDate,
    qualificationName,
    qualificationStatus,
    startDate,
    status,
  };
}

function createCode(index: number) {
  return `PW2026${String(index).padStart(3, '0')}`;
}

function countByStatus(status: PersonnelEmploymentStatus) {
  return personnelRows.filter((item) => item.status === status).length;
}

function getQualificationStatus(
  qualificationName?: string,
  qualificationExpireDate?: string,
): PersonnelQualificationStatus {
  if (!qualificationName) {
    return 'none';
  }

  if (!qualificationExpireDate) {
    return 'valid';
  }

  const expireTime = new Date(qualificationExpireDate).getTime();
  const now = Date.now();
  const warningDays = 1000 * 60 * 60 * 24 * 60;

  if (Number.isNaN(expireTime)) {
    return 'valid';
  }

  if (expireTime < now) {
    return 'expired';
  }

  return expireTime - now <= warningDays ? 'warning' : 'valid';
}
