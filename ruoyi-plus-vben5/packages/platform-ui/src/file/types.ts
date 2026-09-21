export type PlatformFileType = 'doc' | 'other' | 'pdf' | 'report' | 'xls';

export interface PlatformFileListItem {
  date?: string;
  description?: string;
  id: number | string;
  name: string;
  projectName?: string;
  size?: string;
  type?: PlatformFileType;
}
