export interface PlatformStatusBoardItem {
  description?: string;
  id: number | string;
  meta?: string;
  progress?: number;
  title: string;
}

export interface PlatformStatusBoardColumn {
  color?: string;
  count?: number;
  items: PlatformStatusBoardItem[];
  key: string;
  label: string;
  progressColor?: string;
}

export interface PlatformViewOption {
  icon?: string;
  label: string;
  value: string;
}

export interface PlatformViewAction {
  danger?: boolean;
  disabled?: boolean;
  hidden?: boolean;
  icon?: string;
  key: string;
  label: string;
  loading?: boolean;
  type?: 'dashed' | 'default' | 'link' | 'primary' | 'text';
}

export interface PlatformBreadcrumbItem {
  icon?: string;
  path?: string;
  title: string;
}

export type PlatformViewTool = 'export' | 'fullscreen' | 'refresh' | 'setting';
