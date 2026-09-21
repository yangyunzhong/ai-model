import type {
  PlatformFileListItem,
  PlatformFileType,
} from '@st/platform-ui';

export type ProjectDocumentCategory =
  | 'contract'
  | 'ledger'
  | 'report'
  | 'technical';

export interface ProjectDocumentRecord extends PlatformFileListItem {
  category: ProjectDocumentCategory;
  projectCode: string;
  type: PlatformFileType;
}

export interface ProjectDocumentStatCard {
  color?: string;
  title: string;
  type?: 'danger' | 'info' | 'primary' | 'success' | 'warning';
  value: number | string;
}

const projectNames = [
  '城市中央商务区A栋综合体',
  '滨河路市政道路改造工程',
  '科技产业园精装修项目',
  '新区综合管廊工程',
  '湿地公园景观绿化工程',
];

let documentRows: ProjectDocumentRecord[] = [
  createDocument(
    1,
    '施工合同.pdf',
    'contract',
    'pdf',
    projectNames[0],
    '2.4MB',
    '2026-01-15',
  ),
  createDocument(
    2,
    '技术方案.docx',
    'technical',
    'doc',
    projectNames[0],
    '5.1MB',
    '2026-02-01',
  ),
  createDocument(
    3,
    '预算清单.xlsx',
    'ledger',
    'xls',
    projectNames[0],
    '1.2MB',
    '2026-01-20',
  ),
  createDocument(
    4,
    '施工合同.pdf',
    'contract',
    'pdf',
    projectNames[1],
    '2.4MB',
    '2026-01-15',
  ),
  createDocument(
    5,
    '技术方案.docx',
    'technical',
    'doc',
    projectNames[1],
    '5.1MB',
    '2026-02-01',
  ),
  createDocument(
    6,
    '预算清单.xlsx',
    'ledger',
    'xls',
    projectNames[1],
    '1.2MB',
    '2026-01-20',
  ),
  createDocument(
    7,
    '施工合同.pdf',
    'contract',
    'pdf',
    projectNames[2],
    '2.4MB',
    '2026-01-15',
  ),
  createDocument(
    8,
    '技术方案.docx',
    'technical',
    'doc',
    projectNames[2],
    '5.1MB',
    '2026-02-01',
  ),
  createDocument(
    9,
    '预算清单.xlsx',
    'ledger',
    'xls',
    projectNames[2],
    '1.2MB',
    '2026-01-20',
  ),
  createDocument(
    10,
    '施工合同.pdf',
    'contract',
    'pdf',
    projectNames[3],
    '2.4MB',
    '2026-01-15',
  ),
  createDocument(
    11,
    '技术方案.docx',
    'technical',
    'doc',
    projectNames[3],
    '5.1MB',
    '2026-02-01',
  ),
  createDocument(
    12,
    '验收报告.pdf',
    'report',
    'report',
    projectNames[4],
    '3.6MB',
    '2026-03-08',
  ),
];

export async function downloadProjectDocument(id: number | string) {
  await wait(260);
  return documentRows.find((item) => item.id === id);
}

export async function exportProjectDocumentLedger() {
  await wait(220);
  return {
    fileName: '文档与台账清单.xlsx',
    total: documentRows.length,
  };
}

export async function getProjectDocumentList() {
  await wait(120);
  return documentRows;
}

export async function getProjectDocumentStats(): Promise<
  ProjectDocumentStatCard[]
> {
  await wait(120);
  return [
    {
      title: '文档总数',
      type: 'success',
      value: 72,
    },
    {
      title: '合同文件',
      type: 'info',
      value: 16,
    },
    {
      title: '技术文档',
      type: 'warning',
      value: 16,
    },
    {
      color: '#8b5cf6',
      title: '数据报表',
      value: 16,
    },
  ];
}

export async function uploadProjectDocuments(files: File[]) {
  await wait(260);

  const nextId =
    Math.max(...documentRows.map((item) => Number(item.id)).filter(Boolean)) +
    1;
  const uploadedRows = files.map((file, index) => {
    const type = getFileType(file.name);
    return createDocument(
      nextId + index,
      file.name,
      getCategoryByFileType(type),
      type,
      projectNames[index % projectNames.length] || projectNames[0],
      formatFileSize(file.size),
      '2026-05-07',
    );
  });

  documentRows = [...uploadedRows, ...documentRows];
  return uploadedRows;
}

function createDocument(
  id: number,
  name: string,
  category: ProjectDocumentCategory,
  type: PlatformFileType,
  projectName = '',
  size = '',
  date = '',
): ProjectDocumentRecord {
  return {
    category,
    date,
    id,
    name,
    projectCode: `PJ${2_026_000 + id}`,
    projectName,
    size,
    type,
  };
}

function formatFileSize(size: number) {
  if (size < 1024 * 1024) {
    return `${Math.max(1, Math.round(size / 1024))}KB`;
  }
  return `${(size / 1024 / 1024).toFixed(1)}MB`;
}

function getFileType(fileName: string): PlatformFileType {
  const extension = fileName.split('.').pop()?.toLowerCase();

  if (extension === 'pdf') {
    return 'pdf';
  }
  if (extension === 'doc' || extension === 'docx') {
    return 'doc';
  }
  if (extension === 'xls' || extension === 'xlsx') {
    return 'xls';
  }
  return 'other';
}

function getCategoryByFileType(
  type: PlatformFileType,
): ProjectDocumentCategory {
  if (type === 'xls') {
    return 'ledger';
  }
  if (type === 'pdf') {
    return 'contract';
  }
  return 'technical';
}

function wait(duration: number) {
  return new Promise((resolve) => window.setTimeout(resolve, duration));
}
