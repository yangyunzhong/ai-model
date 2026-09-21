export type SafetyStudyArchiveStatus = 'active' | 'inactive' | 'pending';

export interface SafetyStudyArchiveQuery {
  department: '' | SafetyStudyArchiveRecord['department'];
  keyword: string;
  projectCategory: '' | SafetyStudyArchiveRecord['projectCategory'];
  status: '' | SafetyStudyArchiveStatus;
  workType: '' | SafetyStudyArchiveRecord['workType'];
}

export interface SafetyStudyArchiveRecord {
  code: string;
  department: '安全部' | '工程部' | '技术部' | '运维部';
  gender: '女' | '男';
  id: number;
  idCardMasked: string;
  name: string;
  phone: string;
  projectCategory: '安全保障类' | '施工管理类' | '检修维护类' | '运维支持类';
  status: SafetyStudyArchiveStatus;
  workType:
    | '安全员'
    | '工程协调员'
    | '电气检修工'
    | '资料员'
    | '项目经理'
    | '运行巡检员';
}

type SafetyStudyArchiveStatusMeta = {
  label: string;
  status: 'default' | 'processing' | 'success' | 'warning';
};

export const safetyStudyDepartmentOptions = [
  { label: '全部部门', value: '' },
  { label: '工程部', value: '工程部' },
  { label: '安全部', value: '安全部' },
  { label: '运维部', value: '运维部' },
  { label: '技术部', value: '技术部' },
];

export const safetyStudyWorkTypeOptions = [
  { label: '全部工种', value: '' },
  { label: '电气检修工', value: '电气检修工' },
  { label: '安全员', value: '安全员' },
  { label: '资料员', value: '资料员' },
  { label: '项目经理', value: '项目经理' },
  { label: '工程协调员', value: '工程协调员' },
  { label: '运行巡检员', value: '运行巡检员' },
];

export const safetyStudyStatusOptions = [
  { label: '全部状态', value: '' },
  { label: '在岗', value: 'active' },
  { label: '待审核', value: 'pending' },
  { label: '离岗', value: 'inactive' },
];

export const safetyStudyProjectCategoryOptions = [
  { label: '全部类别', value: '' },
  { label: '检修维护类', value: '检修维护类' },
  { label: '安全保障类', value: '安全保障类' },
  { label: '运维支持类', value: '运维支持类' },
  { label: '施工管理类', value: '施工管理类' },
];

export const safetyStudyStatusMap: Record<
  SafetyStudyArchiveStatus,
  SafetyStudyArchiveStatusMeta
> = {
  active: {
    label: '在岗',
    status: 'success',
  },
  inactive: {
    label: '离岗',
    status: 'default',
  },
  pending: {
    label: '待审核',
    status: 'warning',
  },
};

const safetyStudyArchiveSeed: SafetyStudyArchiveRecord[] = [
  {
    code: 'PRSN2024001',
    department: '工程部',
    gender: '男',
    id: 1,
    idCardMasked: '440300********1234',
    name: '李建国',
    phone: '13800138001',
    projectCategory: '检修维护类',
    status: 'active',
    workType: '电气检修工',
  },
  {
    code: 'PRSN2024002',
    department: '安全部',
    gender: '男',
    id: 2,
    idCardMasked: '440300********5678',
    name: '王志强',
    phone: '13800138002',
    projectCategory: '安全保障类',
    status: 'active',
    workType: '安全员',
  },
  {
    code: 'PRSN2025001',
    department: '运维部',
    gender: '女',
    id: 3,
    idCardMasked: '440300********9012',
    name: '赵丽华',
    phone: '13800138003',
    projectCategory: '运维支持类',
    status: 'pending',
    workType: '资料员',
  },
  {
    code: 'PRSN2023001',
    department: '技术部',
    gender: '男',
    id: 4,
    idCardMasked: '440300********3456',
    name: '孙伟',
    phone: '13800138004',
    projectCategory: '施工管理类',
    status: 'active',
    workType: '项目经理',
  },
  {
    code: 'PRSN2024015',
    department: '工程部',
    gender: '男',
    id: 5,
    idCardMasked: '440300********7788',
    name: '陈海涛',
    phone: '13800138005',
    projectCategory: '检修维护类',
    status: 'inactive',
    workType: '工程协调员',
  },
  {
    code: 'PRSN2024026',
    department: '运维部',
    gender: '女',
    id: 6,
    idCardMasked: '440300********2468',
    name: '林雨晴',
    phone: '13800138006',
    projectCategory: '运维支持类',
    status: 'active',
    workType: '运行巡检员',
  },
];

let safetyStudyArchiveRows = [...safetyStudyArchiveSeed];

export async function getSafetyStudyArchiveList(
  query: SafetyStudyArchiveQuery,
): Promise<SafetyStudyArchiveRecord[]> {
  const keyword = query.keyword.trim().toLowerCase();

  return safetyStudyArchiveRows.filter((item) => {
    const matchesKeyword =
      !keyword ||
      [
        item.code,
        item.name,
        item.idCardMasked,
        item.phone,
        item.department,
        item.workType,
      ].some((field) => field.toLowerCase().includes(keyword));

    const matchesDepartment =
      !query.department || item.department === query.department;
    const matchesWorkType = !query.workType || item.workType === query.workType;
    const matchesStatus = !query.status || item.status === query.status;
    const matchesProjectCategory =
      !query.projectCategory || item.projectCategory === query.projectCategory;

    return (
      matchesKeyword &&
      matchesDepartment &&
      matchesProjectCategory &&
      matchesStatus &&
      matchesWorkType
    );
  });
}

export async function removeSafetyStudyArchiveRecord(id: number) {
  safetyStudyArchiveRows = safetyStudyArchiveRows.filter((item) => item.id !== id);
}

export function getSafetyStudyStatusMeta(status: SafetyStudyArchiveStatus) {
  return safetyStudyStatusMap[status];
}
