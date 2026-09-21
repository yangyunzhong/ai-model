export type ProjectInformationStatus =
  | 'archived'
  | 'completed'
  | 'pending'
  | 'running';

export interface ProjectInformationQuery {
  department?: string;
  entryDateRange?: [string, string];
  keyword?: string;
  status?: '' | ProjectInformationStatus;
  type?: string;
}

export interface ProjectInformationRecord {
  amount: number;
  bidOpenDate: string;
  biddingResult: string;
  businessSector: string;
  code: string;
  contractor: string;
  department: string;
  departmentHandler: string;
  description: string;
  durationDays: number;
  entryDate: string;
  equipmentList: string;
  filingCode: string;
  hasSubProject: boolean;
  id: number;
  manager: string;
  name: string;
  plannedExitDate: string;
  procurementAgency: string;
  procurementMethod: string;
  singleSourceFilingCode: string;
  staffing: string;
  status: ProjectInformationStatus;
  type: string;
}

export const projectInformationTypeOptions = [
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

export const projectInformationStatusOptions = [
  { label: '全部状态', value: '' },
  { label: '待启动', value: 'pending' },
  { label: '进行中', value: 'running' },
  { label: '已完成', value: 'completed' },
  { label: '已归档', value: 'archived' },
];

export const projectInformationDepartmentOptions = [
  { label: '工程一部', value: '工程一部' },
  { label: '工程二部', value: '工程二部' },
  { label: '工程三部', value: '工程三部' },
  { label: '设计院', value: '设计院' },
  { label: '设备管理部', value: '设备管理部' },
];

export const projectInformationProcurementMethodOptions = [
  { label: '公开招标', value: '公开招标' },
  { label: '邀请招标', value: '邀请招标' },
  { label: '竞争性谈判', value: '竞争性谈判' },
  { label: '单一来源采购', value: '单一来源采购' },
  { label: '询价采购', value: '询价采购' },
];

export const projectInformationBiddingResultOptions = [
  { label: '中标', value: '中标' },
  { label: '流标', value: '流标' },
  { label: '废标', value: '废标' },
  { label: '待定', value: '待定' },
];

export const businessSectorOptions = [
  { label: '全部板块', value: '' },
  { label: '土建施工', value: '土建施工' },
  { label: '机电安装', value: '机电安装' },
  { label: '装饰装修', value: '装饰装修' },
  { label: '市政路桥', value: '市政路桥' },
  { label: '园林绿化', value: '园林绿化' },
  { label: '设备维保', value: '设备维保' },
  { label: '安保服务', value: '安保服务' },
  { label: '弱电智能化', value: '弱电智能化' },
];

export const hasSubProjectOptions = [
  { label: '是', value: true },
  { label: '否', value: false },
];

export const projectInformationStatusMap: Record<
  ProjectInformationStatus,
  {
    label: string;
    status: 'default' | 'error' | 'processing' | 'success' | 'warning';
  }
> = {
  archived: { label: '已归档', status: 'default' },
  completed: { label: '已完成', status: 'success' },
  pending: { label: '待启动', status: 'warning' },
  running: { label: '进行中', status: 'processing' },
};

let projectInformationRows: ProjectInformationRecord[] = [
  {
    amount: 11_578,
    bidOpenDate: '2025-12-12',
    biddingResult: '中建二局中标',
    businessSector: '土建施工',
    code: 'PJ2026001',
    contractor: '中建二局',
    department: '工程一部',
    departmentHandler: '李建国',
    description: '城市核心区综合体委外建设项目。',
    durationDays: 626,
    entryDate: '2026-01-05',
    equipmentList: '塔吊2台、升降机4台、混凝土泵车2台',
    filingCode: 'BA-2026-001',
    hasSubProject: true,
    id: 1,
    manager: '张明远',
    name: '城市中央商务区A栋综合体',
    plannedExitDate: '2027-09-18',
    procurementAgency: '华诚招标代理',
    procurementMethod: '公开招标',
    singleSourceFilingCode: '',
    staffing: '项目经理1人、安全员3人、施工人员86人',
    status: 'running',
    type: '房建工程',
  },
  {
    amount: 17_608,
    bidOpenDate: '2025-12-18',
    biddingResult: '中铁十二局中标',
    businessSector: '市政路桥',
    code: 'PJ2026002',
    contractor: '中铁十二局',
    department: '工程二部',
    departmentHandler: '王芳',
    description: '滨河路主路、辅路及配套管网改造。',
    durationDays: 803,
    entryDate: '2026-02-10',
    equipmentList: '摊铺机2台、压路机6台、运输车18台',
    filingCode: 'BA-2026-002',
    hasSubProject: false,
    id: 2,
    manager: '刘海涛',
    name: '滨河路市政道路改造工程',
    plannedExitDate: '2028-03-15',
    procurementAgency: '城建招采中心',
    procurementMethod: '邀请招标',
    singleSourceFilingCode: '',
    staffing: '项目经理1人、技术员5人、施工人员72人',
    status: 'pending',
    type: '市政工程',
  },
  {
    amount: 28_947,
    bidOpenDate: '2025-11-29',
    biddingResult: '金螳螂装饰中标',
    businessSector: '装饰装修',
    code: 'PJ2026003',
    contractor: '金螳螂装饰',
    department: '工程三部',
    departmentHandler: '赵敏',
    description: '科技产业园办公区和公共区域精装修。',
    durationDays: 812,
    entryDate: '2026-01-12',
    equipmentList: '脚手架12套、喷涂设备8套、运输车6台',
    filingCode: 'BA-2026-003',
    hasSubProject: true,
    id: 3,
    manager: '陈思雅',
    name: '科技产业园精装修项目',
    plannedExitDate: '2028-03-24',
    procurementAgency: '启明工程咨询',
    procurementMethod: '竞争性谈判',
    singleSourceFilingCode: '',
    staffing: '项目经理1人、设计协调3人、施工人员64人',
    status: 'completed',
    type: '装饰装修',
  },
  {
    amount: 4578,
    bidOpenDate: '2025-12-05',
    biddingResult: '中交一公局中标',
    businessSector: '机电安装',
    code: 'PJ2026004',
    contractor: '中交一公局',
    department: '设计院',
    departmentHandler: '孙伟',
    description: '新区地下综合管廊施工与机电安装。',
    durationDays: 940,
    entryDate: '2026-01-20',
    equipmentList: '盾构配套设备1批、吊装设备3台',
    filingCode: 'BA-2026-004',
    hasSubProject: false,
    id: 4,
    manager: '赵国强',
    name: '新区综合管廊工程',
    plannedExitDate: '2028-07-30',
    procurementAgency: '中咨招标',
    procurementMethod: '单一来源采购',
    singleSourceFilingCode: 'DY-2026-004',
    staffing: '项目经理1人、机电人员12人、施工人员45人',
    status: 'archived',
    type: '基础设施',
  },
  {
    amount: 15_169,
    bidOpenDate: '2025-12-21',
    biddingResult: '东方园林中标',
    businessSector: '园林绿化',
    code: 'PJ2026005',
    contractor: '东方园林',
    department: '设备管理部',
    departmentHandler: '周丽娟',
    description: '湿地公园园林景观、绿化养护和栈道修缮。',
    durationDays: 716,
    entryDate: '2026-02-01',
    equipmentList: '洒水车4台、修剪设备20套、运输车8台',
    filingCode: 'BA-2026-005',
    hasSubProject: true,
    id: 5,
    manager: '林雨桐',
    name: '湿地公园景观绿化工程',
    plannedExitDate: '2027-12-20',
    procurementAgency: '绿城咨询',
    procurementMethod: '询价采购',
    singleSourceFilingCode: '',
    staffing: '项目经理1人、养护人员38人、巡检人员6人',
    status: 'running',
    type: '园林景观',
  },
  {
    amount: 38_097,
    bidOpenDate: '2025-12-25',
    biddingResult: '城建集团中标',
    businessSector: '设备维保',
    code: 'PJ2026006',
    contractor: '城建集团',
    department: '工程一部',
    departmentHandler: '吴强',
    description: '老旧小区公共区域、道路和附属设施改造。',
    durationDays: 534,
    entryDate: '2026-02-15',
    equipmentList: '起重设备2台、运输车12台、检测设备1批',
    filingCode: 'BA-2026-006',
    hasSubProject: false,
    id: 6,
    manager: '王建华',
    name: '老旧小区改造提升项目',
    plannedExitDate: '2027-06-21',
    procurementAgency: '城投招标代理',
    procurementMethod: '公开招标',
    singleSourceFilingCode: '',
    staffing: '项目经理1人、安全员2人、施工人员96人',
    status: 'pending',
    type: '特种设备维保',
  },
  {
    amount: 40_343,
    bidOpenDate: '2025-11-17',
    biddingResult: '中交二航局中标',
    businessSector: '市政路桥',
    code: 'PJ2026007',
    contractor: '中交二航局',
    department: '工程二部',
    departmentHandler: '郑海涛',
    description: '跨河大桥主体施工和附属交通设施建设。',
    durationDays: 270,
    entryDate: '2026-01-08',
    equipmentList: '架桥机1台、吊车6台、运输车20台',
    filingCode: 'BA-2026-007',
    hasSubProject: true,
    id: 7,
    manager: '孙立民',
    name: '跨河大桥新建工程',
    plannedExitDate: '2026-09-29',
    procurementAgency: '交通工程咨询',
    procurementMethod: '邀请招标',
    singleSourceFilingCode: '',
    staffing: '项目经理1人、技术员8人、施工人员110人',
    status: 'completed',
    type: '车辆整改',
  },
  {
    amount: 9559,
    bidOpenDate: '2025-12-09',
    biddingResult: '华为技术中标',
    businessSector: '弱电智能化',
    code: 'PJ2026008',
    contractor: '华为技术',
    department: '工程三部',
    departmentHandler: '钱晓明',
    description: '智慧园区弱电、网络和安防系统集成。',
    durationDays: 385,
    entryDate: '2026-01-16',
    equipmentList: '交换机42台、摄像头260套、布线设备1批',
    filingCode: 'BA-2026-008',
    hasSubProject: false,
    id: 8,
    manager: '何晓峰',
    name: '智慧园区弱电系统工程',
    plannedExitDate: '2027-01-23',
    procurementAgency: '数智招采中心',
    procurementMethod: '竞争性谈判',
    singleSourceFilingCode: '',
    staffing: '项目经理1人、实施工程师14人、施工人员36人',
    status: 'archived',
    type: '轨道桥隧维保',
  },
  {
    amount: 3694,
    bidOpenDate: '2025-12-28',
    biddingResult: '北控水务中标',
    businessSector: '土建施工',
    code: 'PJ2026009',
    contractor: '北控水务',
    department: '设计院',
    departmentHandler: '冯志刚',
    description: '城南污水处理厂扩容和配套管线改造。',
    durationDays: 912,
    entryDate: '2026-02-20',
    equipmentList: '泵站设备1批、检测设备12套、运输车5台',
    filingCode: 'BA-2026-009',
    hasSubProject: true,
    id: 9,
    manager: '杨志刚',
    name: '城南污水处理厂扩容',
    plannedExitDate: '2028-07-02',
    procurementAgency: '水务工程咨询',
    procurementMethod: '单一来源采购',
    singleSourceFilingCode: 'DY-2026-009',
    staffing: '项目经理1人、工艺工程师4人、施工人员52人',
    status: 'running',
    type: '保安保洁',
  },
  {
    amount: 2753,
    bidOpenDate: '2025-12-30',
    biddingResult: '中建八局中标',
    businessSector: '安保服务',
    code: 'PJ2026010',
    contractor: '中建八局',
    department: '设备管理部',
    departmentHandler: '韩梅',
    description: '市民广场地下停车场主体结构和机电工程。',
    durationDays: 792,
    entryDate: '2026-03-01',
    equipmentList: '挖掘机4台、吊车3台、通风设备1批',
    filingCode: 'BA-2026-010',
    hasSubProject: false,
    id: 10,
    manager: '周敏',
    name: '市民广场地下停车场',
    plannedExitDate: '2028-03-03',
    procurementAgency: '广场项目招采组',
    procurementMethod: '询价采购',
    singleSourceFilingCode: '',
    staffing: '项目经理1人、机电人员10人、施工人员68人',
    status: 'pending',
    type: '房建工程',
  },
];

export async function getProjectInformationList(
  query: ProjectInformationQuery,
) {
  const keyword = query.keyword?.trim().toLowerCase();

  return projectInformationRows.filter((item) => {
    const matchedKeyword = keyword
      ? [item.name, item.code, item.manager].some((field) =>
          field.toLowerCase().includes(keyword),
        )
      : true;
    const matchedStatus = query.status ? item.status === query.status : true;
    const matchedType = query.type ? item.type === query.type : true;
    const matchedDepartment = query.department
      ? item.department === query.department
      : true;

    let matchedDateRange = true;
    if (query.entryDateRange?.[0] && query.entryDateRange?.[1]) {
      const entry = item.entryDate;
      matchedDateRange =
        entry >= query.entryDateRange[0] && entry <= query.entryDateRange[1];
    }

    return (
      matchedKeyword &&
      matchedStatus &&
      matchedType &&
      matchedDepartment &&
      matchedDateRange
    );
    });
}

export async function saveProjectInformationProject(
  project: Partial<ProjectInformationRecord>,
) {
  if (project.id) {
    projectInformationRows = projectInformationRows.map((item) =>
      item.id === project.id
        ? ({ ...item, ...project } as ProjectInformationRecord)
        : item,
    );
    return;
  }

  const nextId = Math.max(...projectInformationRows.map((item) => item.id)) + 1;
  projectInformationRows = [
    {
      amount: project.amount ?? 0,
      bidOpenDate: project.bidOpenDate || '',
      biddingResult: project.biddingResult || '',
      businessSector: project.businessSector || '',
      code: project.code || `PJ${2_026_000 + nextId}`,
      contractor: project.contractor || '',
      department: project.department || '',
      departmentHandler: project.departmentHandler || '',
      description: project.description || '',
      durationDays: project.durationDays ?? 0,
      entryDate: project.entryDate || '',
      equipmentList: project.equipmentList || '',
      filingCode: project.filingCode || '',
      hasSubProject: project.hasSubProject ?? false,
      id: nextId,
      manager: project.manager || '',
      name: project.name || '未命名项目',
      plannedExitDate: project.plannedExitDate || '',
      procurementAgency: project.procurementAgency || '',
      procurementMethod: project.procurementMethod || '',
      singleSourceFilingCode: project.singleSourceFilingCode || '',
      staffing: project.staffing || '',
      status: project.status || 'pending',
      type: project.type || '房建工程',
    },
    ...projectInformationRows,
  ];
}

export async function archiveProjectInformationProject(id: number) {
  projectInformationRows = projectInformationRows.map((item) =>
    item.id === id ? { ...item, status: 'archived' } : item,
  );
}

export async function deleteProjectInformationProject(id: number) {
  projectInformationRows = projectInformationRows.filter(
    (item) => item.id !== id,
  );
}

// ========== 导出功能 ==========

/** 表格导出列定义 */
const exportColumns: { key: keyof ProjectInformationRecord; title: string }[] =
  [
    { key: 'code', title: '项目编码' },
    { key: 'name', title: '项目名称' },
    { key: 'type', title: '项目类型' },
    { key: 'department', title: '主管部门' },
    { key: 'departmentHandler', title: '主管部门经办人' },
    { key: 'businessSector', title: '业务板块' },
    { key: 'hasSubProject', title: '是否含子项目' },
    { key: 'contractor', title: '承包商' },
    { key: 'amount', title: '合同金额(万元)' },
    { key: 'procurementMethod', title: '招采方式' },
    { key: 'biddingResult', title: '招标结果' },
    { key: 'entryDate', title: '进场时间' },
    { key: 'manager', title: '项目负责人' },
    { key: 'status', title: '状态' },
  ];

function formatExportRow(
  row: ProjectInformationRecord,
): Record<string, string | number | boolean> {
  return {
    项目编码: row.code,
    项目名称: row.name,
    项目类型: row.type,
    主管部门: row.department,
    主管部门经办人: row.departmentHandler,
    业务板块: row.businessSector,
    是否含子项目: row.hasSubProject ? '是' : '否',
    承包商: row.contractor,
    '合同金额(万元)': row.amount,
    招采方式: row.procurementMethod,
    招标结果: row.biddingResult,
    进场时间: row.entryDate,
    项目负责人: row.manager,
    状态:
      projectInformationStatusMap[row.status]?.label ?? (row.status as string),
  };
}

/**
 * 导出为 CSV 文件
 * @param rows 要导出的数据行
 * @param filename 文件名（不含扩展名）
 */
export function exportToCsv(
  rows: ProjectInformationRecord[],
  filename = '项目信息列表',
) {
  if (rows.length === 0) {
    return;
  }

  const headers = exportColumns.map((col) => col.title);
  const csvRows = rows.map(formatExportRow);
  const csvContent = [
    headers.join(','),
    ...csvRows.map((row) =>
      headers.map((h) => {
        const val = String(row[h] ?? '');
        // CSV 转义：包含逗号/双引号/换行的字段需要用双引号包裹
        if (val.includes(',') || val.includes('"') || val.includes('\n')) {
          return `"${val.replace(/"/g, '""')}"`;
        }
        return val;
      }).join(','),
    ),
  ].join('\n');

  // 添加 BOM 以支持 Excel 正确识别 UTF-8
  const bom = '\uFEFF';
  const blob = new Blob([bom + csvContent], {
    type: 'text/csv;charset=utf-8;',
  });
  downloadBlob(blob, `${filename}.csv`);
}

/**
 * 导出为类 Excel 格式（HTML Table，可直接用 Excel 打开）
 * 前端 Mock 阶段使用 HTML Table 方式生成 .xls，无需引入 SheetJS 等重型库
 * @param rows 要导出的数据行
 * @param filename 文件名（不含扩展名）
 */
export function exportToExcel(
  rows: ProjectInformationRecord[],
  filename = '项目信息列表',
) {
  if (rows.length === 0) {
    return;
  }

  const headers = exportColumns.map((col) => `<th>${col.title}</th>`);
  const bodyRows = rows.map((row) => {
    const formatted = formatExportRow(row);
    return (
      `<tr>${headers.map(
        (_, i) =>
          `<td>${String(formatted[exportColumns[i].title] ?? '')}</td>`,
      )}</tr>`
    );
  });

  const html = [
    '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40">',
    '<head><meta charset="UTF-8"><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>项目信息</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head>',
    '<body><table border="1"><thead>',
    `<tr>${headers.join('')}</tr>`,
    '</thead><tbody>',
    ...bodyRows,
    '</tbody></table></body></html>',
  ].join('\n');

  const blob = new Blob([html], {
    type: 'application/vnd.ms-excel;charset=utf-8;',
  });
  downloadBlob(blob, `${filename}.xls`);
}

/** 通用 Blob 下载工具函数 */
function downloadBlob(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}
