import type {
  UseVbenVxeGrid,
  VxeGridPropTypes,
} from '@vben/plugins/vxe-table';

export * from '@vben/plugins/vxe-table';
export type * from '@vben/plugins/vxe-table';

type VbenVxeGridOptions = Parameters<UseVbenVxeGrid>[0];
type VbenVxeGridGridOptions = NonNullable<VbenVxeGridOptions['gridOptions']>;
type VbenVxeGridColumns = VbenVxeGridGridOptions['columns'];

export type PlatformVxeGridOptions = VbenVxeGridOptions & {
  platformIndex?: boolean;
};

function withPlatformIndexColumn(columns: VbenVxeGridColumns) {
  const indexColumn = {
    align: 'center' as const,
    field: '__platform_index',
    title: '序号',
    type: 'seq' as const,
    width: 88,
  };

  if (columns?.[0]?.type !== 'checkbox') {
    return [indexColumn, ...(columns ?? [])];
  }

  const [firstColumn, ...restColumns] = columns;
  return [firstColumn, indexColumn, ...restColumns];
}

export function withPlatformVxeGridOptions(
  options: PlatformVxeGridOptions,
): VbenVxeGridOptions {
  const toolbarConfig = options.gridOptions?.toolbarConfig ?? {};
  const headerCellConfig = options.gridOptions?.headerCellConfig ?? {};
  const cellConfig = options.gridOptions?.cellConfig ?? {};
  const columns = options.gridOptions?.columns ?? [];
  const shouldShowIndex = options.platformIndex !== false;
  const hasIndexColumn = columns.some(
    (column) => column.type === 'seq' || column.field === '__platform_index',
  );
  const mergedColumns =
    shouldShowIndex && columns.length > 0 && !hasIndexColumn
      ? withPlatformIndexColumn(columns)
      : columns;

  return {
    ...options,
    gridOptions: {
      ...options.gridOptions,
      columns: mergedColumns,
      headerCellConfig: {
        height: 44,
        ...headerCellConfig,
      },
      cellConfig: {
        height: 44,
        ...cellConfig,
      },
      toolbarConfig: {
        custom: true,
        refresh: true,
        search: true,
        zoom: true,
        ...toolbarConfig,
        customOptions: {
          icon: 'vxe-icon-setting',
          ...toolbarConfig.customOptions,
        },
        refreshOptions: {
          code: 'query',
          ...toolbarConfig.refreshOptions,
        },
      },
    },
  };
}

export function createPlatformVxeGrid(
  useBaseVbenVxeGrid: UseVbenVxeGrid,
): UseVbenVxeGrid {
  return ((options: PlatformVxeGridOptions) =>
    useBaseVbenVxeGrid(withPlatformVxeGridOptions(options))) as UseVbenVxeGrid;
}

/**
 * 判断 vxe-table 的复选框是否选中。
 */
export function vxeCheckboxChecked(
  tableApi: ReturnType<UseVbenVxeGrid>[1],
) {
  return tableApi?.grid?.getCheckboxRecords?.()?.length > 0;
}

/**
 * 将 vxe-table 排序参数转换为后端常用的 orderByColumn / isAsc 参数。
 */
export function addSortParams(
  params: Record<string, any>,
  sortList: VxeGridPropTypes.ProxyAjaxQuerySortCheckedParams[],
) {
  if (sortList.length === 0) {
    return;
  }
  params.orderByColumn = sortList.map((item) => item.field).join(',');
  params.isAsc = sortList.map((item) => item.order).join(',');
}
