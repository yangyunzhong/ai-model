export type PlatformApprovalProgressItemStatus =
  | 'current'
  | 'finished'
  | 'pending';

export interface PlatformApprovalProgressItem {
  assignee: string;
  avatarIcon?: string;
  department?: string;
  dotIcon?: string;
  id: number | string;
  status: PlatformApprovalProgressItemStatus;
  time?: string;
  title: string;
}
