export interface PlatformNoticeListItem {
  actionText?: string;
  description?: string;
  disabled?: boolean;
  id: number | string;
  meta?: string;
  status?: 'danger' | 'default' | 'info' | 'success' | 'warning';
  tag?: string;
  title: string;
}
