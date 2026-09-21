import type { TableProps } from 'antdv-next';

export type PlatformTableDateRangeFilterConfig = {
  placeholder?: [string, string];
  type: 'dateRange';
  valueFormat?: string;
};

export type PlatformTableFilterConfig = PlatformTableDateRangeFilterConfig;

export type PlatformTableColumn = NonNullable<TableProps['columns']>[number] & {
  customCell?: (...args: any[]) => Record<string, any>;
  customHeaderCell?: (...args: any[]) => Record<string, any>;
  platformFilter?: PlatformTableFilterConfig;
};

export type PlatformTableColumns = PlatformTableColumn[];
