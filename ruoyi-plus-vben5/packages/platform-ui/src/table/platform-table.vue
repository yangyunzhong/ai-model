<!-- eslint-disable vue/one-component-per-file -- Keep render-only filter dropdowns colocated during package migration. -->
<script setup lang="ts">
import type {
  TableEmits,
  TablePaginationConfig,
  TableProps,
} from 'antdv-next';

import type { PlatformTableColumn, PlatformTableColumns } from './types';

import {
  computed,
  defineComponent,
  h,
  nextTick,
  onBeforeUnmount,
  onMounted,
  ref,
  useSlots,
  type VNodeChild,
  watch,
} from 'vue';

import { Checkbox, CheckboxGroup, Table } from 'antdv-next';

import { PlatformRangePicker } from '../field';
type PlatformTableIndexColumn = Partial<PlatformTableColumn>;
type PlatformFilterKey = boolean | number | string;
type PlatformFilterValue = PlatformFilterKey[];
type PlatformFilterItem = {
  children?: PlatformFilterItem[];
  text?: VNodeChild;
  value?: PlatformFilterKey;
};
type PlatformFilterDropdownProps = {
  clearFilters?: (param?: { closeDropdown?: boolean; confirm?: boolean }) => void;
  close?: () => void;
  confirm: (param?: { closeDropdown: boolean }) => void;
  filterMultiple?: boolean;
  filters?: PlatformFilterItem[];
  selectedKeys: PlatformFilterKey[];
  setSelectedKeys: (selectedKeys: PlatformFilterKey[]) => void;
};
type PlatformDateRangeFilterDropdownProps = {
  clearFilters?: (param?: { closeDropdown?: boolean; confirm?: boolean }) => void;
  close?: () => void;
  confirm: (param?: { closeDropdown: boolean }) => void;
  selectedKeys: PlatformFilterKey[];
  setSelectedKeys: (selectedKeys: PlatformFilterKey[]) => void;
};
type PlatformFlattenedFilterItem = {
  depth: number;
  label: VNodeChild;
  value: PlatformFilterKey;
};
type PlatformColumnSettingItem = {
  key: string;
  label: string;
  required: boolean;
};
type PlatformColumnSettingRequestEvent = CustomEvent<{
  anchor?: HTMLElement | MouseEvent;
}>;
type PlatformTableCellProps = Record<string, any>;

defineOptions({
  inheritAttrs: false,
});

const props = withDefaults(
  defineProps<{
    actionColumnWidth?: number | string;
    adaptiveHeight?: boolean;
    adaptiveHeightBottomOffset?: number;
    adaptiveHeightMin?: number;
    columns?: PlatformTableColumns;
    columnSettingEnabled?: boolean;
    columnSettingKey?: string;
    indexColumn?: PlatformTableIndexColumn;
    pagination?: TableProps['pagination'];
    scroll?: TableProps['scroll'];
    showIndex?: boolean;
  }>(),
  {
    adaptiveHeight: false,
    adaptiveHeightBottomOffset: 24,
    adaptiveHeightMin: 240,
    actionColumnWidth: 184,
    columnSettingEnabled: true,
    columnSettingKey: '',
    columns: () => [],
    indexColumn: () => ({}),
    pagination: undefined,
    scroll: undefined,
    showIndex: true,
  },
);
const emit = defineEmits<{
  change: Parameters<TableEmits['change']>;
}>();

const PLATFORM_ALL_FILTER_KEY = '__platform_table_filter_all__';
const PLATFORM_ALL_FILTER_LABEL = '全部';

const slots = useSlots();
const tableWrapperRef = ref<HTMLElement>();
const columnSettingPanelRef = ref<HTMLElement>();
const adaptiveScrollY = ref<number>();
const adaptiveWrapperMinHeight = ref<number>();
const innerPaginationCurrent = ref(1);
const innerPaginationPageSize = ref(10);
const columnSettingOpen = ref(false);
const columnSettingAnchor = ref<HTMLElement>();
const columnSettingPosition = ref({
  left: 0,
  top: 0,
});
const visibleColumnKeys = ref<string[]>([]);
let resizeObserver: ResizeObserver | undefined;
let updateFrame = 0;
let platformRequestContainer: HTMLElement | null = null;
let previousPaginationSnapshot = '';

const passthroughSlotNames = computed(() =>
  Object.keys(slots).filter((name) => name !== 'bodyCell'),
);
const baseColumns = computed(() => {
  const columns = props.columns ?? [];
  let nextColumns = columns;

  if (
    props.showIndex &&
    !columns.some((column) => column.key === '__platform_index')
  ) {
    const indexColumn: PlatformTableColumn = {
      align: 'center',
      customCell: createColumnCellClassExtender(
        props.indexColumn.customCell,
        'platform-table__index-column-cell',
      ),
      customHeaderCell: createColumnCellClassExtender(
        props.indexColumn.customHeaderCell,
        'platform-table__index-column-header',
      ),
      dataIndex: '__platform_index',
      key: '__platform_index',
      title: '序号',
      width: 88,
      ...props.indexColumn,
    };
    nextColumns = [indexColumn, ...columns];
  }

  return nextColumns;
});
const columnSettingItems = computed<PlatformColumnSettingItem[]>(() =>
  flattenColumns(baseColumns.value).map((column: PlatformTableColumn, index: number) => ({
    key: getColumnIdentity(column, index),
    label: getColumnSettingLabel(column, index),
    required: isColumnRequired(column),
  })),
);
const mergedColumns = computed(() => {
  const enabledVisibleKeys =
    visibleColumnKeys.value.length > 0
      ? new Set(visibleColumnKeys.value)
      : new Set(columnSettingItems.value.map((item) => item.key));

  return enhancePlatformColumns(
    filterColumnsByVisibility(baseColumns.value, enabledVisibleKeys),
  );
});
const mergedScroll = computed<TableProps['scroll']>(() => {
  const baseScroll = props.scroll ? { ...props.scroll } : {};
  const nextScroll = { ...baseScroll };
  const defaultScrollX = getDefaultScrollX(mergedColumns.value);

  if (nextScroll.x === undefined && defaultScrollX !== undefined) {
    nextScroll.x = defaultScrollX;
  }

  if (
    props.adaptiveHeight &&
    nextScroll.y === undefined &&
    adaptiveScrollY.value !== undefined
  ) {
    nextScroll.y = adaptiveScrollY.value;
  }

  return Object.keys(nextScroll).length > 0 ? nextScroll : undefined;
});
const mergedPagination = computed<TableProps['pagination']>(() => {
  const pagination = props.pagination;

  if (pagination === false) {
    return false;
  }

  const basePagination =
    pagination && typeof pagination !== 'boolean' ? pagination : {};

  return {
    current: innerPaginationCurrent.value,
    pageSize: innerPaginationPageSize.value,
    pageSizeOptions: ['10', '20', '50', '100'],
    showSizeChanger: true,
    ...basePagination,
  };
});
const tableWrapperStyle = computed(() => ({
  '--platform-table-body-min-height':
    adaptiveScrollY.value === undefined ? undefined : `${adaptiveScrollY.value}px`,
  '--platform-table-wrapper-min-height':
    adaptiveWrapperMinHeight.value === undefined
      ? undefined
      : `${adaptiveWrapperMinHeight.value}px`,
}));
const checkedColumnKeys = computed({
  get: () => visibleColumnKeys.value,
  set: (value) => {
    const allowedKeys = new Set(
      columnSettingItems.value
        .filter((item) => !item.required)
        .map((item) => item.key),
    );
    const requiredKeys = columnSettingItems.value
      .filter((item) => item.required)
      .map((item) => item.key);
    visibleColumnKeys.value = [
      ...requiredKeys,
      ...value.filter((key) => allowedKeys.has(key)),
    ];
    persistVisibleColumns();
  },
});

function getIndexCellValue(index: number) {
  return getPaginationOffset() + index + 1;
}

function createColumnCellClassExtender(
  originalHandler: ((...args: any[]) => PlatformTableCellProps) | undefined,
  className: string,
) {
  return (...args: any[]) => {
    const originalProps = originalHandler?.(...args) ?? {};
    const mergedClassName = mergeClassNames(
      originalProps.className,
      originalProps.class,
      className,
    );

    return {
      ...originalProps,
      class: mergedClassName,
      className: mergedClassName,
    };
  };
}

function mergeClassNames(...classNames: Array<null | string | undefined>) {
  return classNames.filter(Boolean).join(' ');
}

function getPaginationOffset() {
  const pagination = mergedPagination.value;
  if (!pagination || typeof pagination === 'boolean') {
    return 0;
  }

  const current = Number(
    pagination.current ?? 1,
  );
  const pageSize = Number(
    pagination.pageSize ?? 10,
  );

  return (Math.max(current, 1) - 1) * Math.max(pageSize, 1);
}

function handleChange(
  ...args: Parameters<TableEmits['change']>
) {
  const [pagination] = args;

  innerPaginationCurrent.value = Number(
    pagination?.current ?? innerPaginationCurrent.value,
  );
  innerPaginationPageSize.value = Number(
    pagination?.pageSize ?? innerPaginationPageSize.value,
  );
  emit('change', ...args);
}

function getElementHeight(selector: string) {
  const element = tableWrapperRef.value?.querySelector(selector);

  if (!(element instanceof HTMLElement)) {
    return 0;
  }

  const rect = element.getBoundingClientRect();
  const style = window.getComputedStyle(element);

  return (
    rect.height +
    Number.parseFloat(style.marginTop || '0') +
    Number.parseFloat(style.marginBottom || '0')
  );
}

function getTableBodyTop() {
  const body = tableWrapperRef.value?.querySelector('.ant-table-tbody');

  if (body instanceof HTMLElement) {
    return body.getBoundingClientRect().top;
  }

  const table = tableWrapperRef.value?.querySelector('.ant-table');

  if (table instanceof HTMLElement) {
    return table.getBoundingClientRect().top;
  }

  return tableWrapperRef.value?.getBoundingClientRect().top ?? 0;
}

function updateAdaptiveHeight() {
  if (!props.adaptiveHeight || props.scroll?.y !== undefined) {
    adaptiveScrollY.value = undefined;
    adaptiveWrapperMinHeight.value = undefined;
    return;
  }

  const wrapperTop =
    tableWrapperRef.value?.getBoundingClientRect().top ?? getTableBodyTop();
  const tableBodyTop = getTableBodyTop();
  const paginationHeight = getElementHeight('.ant-pagination');
  const availableHeight =
    window.innerHeight -
    tableBodyTop -
    paginationHeight -
    props.adaptiveHeightBottomOffset;

  adaptiveScrollY.value = Math.max(
    props.adaptiveHeightMin,
    Math.floor(availableHeight),
  );
  adaptiveWrapperMinHeight.value = Math.max(
    props.adaptiveHeightMin,
    Math.floor(window.innerHeight - wrapperTop - props.adaptiveHeightBottomOffset),
  );
}

function scheduleAdaptiveHeightUpdate() {
  if (updateFrame) {
    cancelAnimationFrame(updateFrame);
  }

  updateFrame = requestAnimationFrame(() => {
    updateFrame = 0;
    updateAdaptiveHeight();
  });
}

onMounted(() => {
  syncPaginationState(true);
  syncVisibleColumns();
  nextTick(scheduleAdaptiveHeightUpdate);
  window.addEventListener('resize', scheduleAdaptiveHeightUpdate);

  platformRequestContainer = tableWrapperRef.value?.parentElement ?? null;
  platformRequestContainer?.addEventListener(
    'platform-table:refresh-request',
    handleRefreshRequest as EventListener,
  );
  platformRequestContainer?.addEventListener(
    'platform-table:column-setting-request',
    handleColumnSettingRequest as EventListener,
  );

  if (tableWrapperRef.value && typeof ResizeObserver !== 'undefined') {
    resizeObserver = new ResizeObserver(scheduleAdaptiveHeightUpdate);
    resizeObserver.observe(tableWrapperRef.value);

    if (tableWrapperRef.value.parentElement) {
      resizeObserver.observe(tableWrapperRef.value.parentElement);
    }
  }
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', scheduleAdaptiveHeightUpdate);
  window.removeEventListener('mousedown', handleColumnSettingPointerDown);
  window.removeEventListener('keydown', handleColumnSettingKeydown);
  window.removeEventListener('resize', handleColumnSettingViewportChange);
  window.removeEventListener('scroll', handleColumnSettingViewportChange, true);
  resizeObserver?.disconnect();

  if (updateFrame) {
    cancelAnimationFrame(updateFrame);
  }

  platformRequestContainer?.removeEventListener(
    'platform-table:refresh-request',
    handleRefreshRequest as EventListener,
  );
  platformRequestContainer?.removeEventListener(
    'platform-table:column-setting-request',
    handleColumnSettingRequest as EventListener,
  );
  platformRequestContainer = null;
});

watch(
  () => props.columns,
  () => {
    syncVisibleColumns();
  },
  { deep: true },
);

watch(
  () => props.pagination,
  () => {
    syncPaginationState();
  },
  { deep: true },
);

function createPlatformFilterDropdown(column: PlatformTableColumn) {
  return (dropdownProps: PlatformFilterDropdownProps) =>
    h(PlatformTableFilterDropdown, {
      ...dropdownProps,
      filterMultiple: column.filterMultiple,
    });
}

function handleRefreshRequest(event: Event) {
  if (!(event instanceof CustomEvent)) {
    return;
  }

  if (!hasActiveColumnFilters()) {
    return;
  }

  event.preventDefault();
  resetColumnFilters();
}

function handleColumnSettingRequest(event: Event) {
  if (!(event instanceof CustomEvent) || !props.columnSettingEnabled) {
    return;
  }

  event.preventDefault();
  openColumnSetting(
    (event as PlatformColumnSettingRequestEvent).detail?.anchor,
  );
}

function hasActiveColumnFilters() {
  return flattenColumns(baseColumns.value).some((column) => {
    if (!isFilterableColumn(column)) {
      return false;
    }

    const filteredValue = column.filteredValue;

    return Array.isArray(filteredValue) && filteredValue.length > 0;
  });
}

function isFilterableColumn(column: PlatformTableColumn) {
  return Boolean(column.filters?.length || column.platformFilter);
}

function resetColumnFilters() {
  const clearedFilters: Record<string, null | PlatformFilterValue> = {};

  flattenColumns(baseColumns.value).forEach((column, index) => {
    if (!isFilterableColumn(column)) {
      return;
    }

    clearedFilters[getColumnIdentity(column, index)] = null;
  });

  const pagination = resolveRefreshPagination();

  emit('change', pagination, clearedFilters, {}, {
    action: 'filter',
    currentDataSource: [],
  });
}

function resolveRefreshPagination(): TablePaginationConfig {
  const pagination = mergedPagination.value;

  if (!pagination || typeof pagination === 'boolean') {
    return {};
  }

  return {
    current: pagination.current,
    pageSize: pagination.pageSize,
    total: pagination.total,
  };
}

function syncPaginationState(force = false) {
  const pagination = props.pagination;

  if (!pagination || typeof pagination === 'boolean') {
    if (force) {
      innerPaginationCurrent.value = 1;
      innerPaginationPageSize.value = 10;
      previousPaginationSnapshot = '';
    }
    return;
  }

  const snapshot = JSON.stringify({
    current: pagination.current,
    defaultCurrent: pagination.defaultCurrent,
    pageSize: pagination.pageSize,
    defaultPageSize: pagination.defaultPageSize,
  });

  if (!force && snapshot === previousPaginationSnapshot) {
    return;
  }

  previousPaginationSnapshot = snapshot;
  innerPaginationCurrent.value = Number(
    pagination.current ?? pagination.defaultCurrent ?? 1,
  );
  innerPaginationPageSize.value = Number(
    pagination.pageSize ?? pagination.defaultPageSize ?? 10,
  );
}

function createPlatformDateRangeFilterDropdown(column: PlatformTableColumn) {
  return (dropdownProps: PlatformDateRangeFilterDropdownProps) =>
    h(PlatformTableDateRangeFilterDropdown, {
      ...dropdownProps,
      placeholder:
        column.platformFilter?.type === 'dateRange'
          ? column.platformFilter.placeholder
          : undefined,
      valueFormat:
        column.platformFilter?.type === 'dateRange'
          ? column.platformFilter.valueFormat
          : undefined,
    });
}

function enhancePlatformColumns(
  columns: PlatformTableColumns | undefined,
): TableProps['columns'] {
  return (columns ?? []).map((column) => {
    const { platformFilter, ...plainColumn } = column as PlatformTableColumn & {
      platformFilter?: PlatformTableColumn['platformFilter'];
    };

    if ('children' in column && column.children?.length) {
      return {
        ...plainColumn,
        children: enhancePlatformColumns(column.children as PlatformTableColumns),
      };
    }

    const nextColumn = isActionColumn(plainColumn)
      ? appendColumnClass(
          {
            ...plainColumn,
            width: props.actionColumnWidth,
          },
          'platform-table__action-column',
        )
      : plainColumn;

    if (nextColumn.filterDropdown) {
      return nextColumn;
    }

    if (platformFilter?.type === 'dateRange') {
      return {
        ...nextColumn,
        filterDropdown: createPlatformDateRangeFilterDropdown({
          ...nextColumn,
          platformFilter,
        }),
        filterIcon: createPlatformFilterIcon(nextColumn.filterIcon),
      };
    }

    if (!nextColumn.filters?.length) {
      return nextColumn;
    }

    return {
      ...nextColumn,
      filterDropdown: createPlatformFilterDropdown(nextColumn),
      filterIcon: createPlatformFilterIcon(nextColumn.filterIcon),
    };
  }) as NonNullable<TableProps['columns']>;
}

function createPlatformFilterIcon(
  customIcon?: PlatformTableColumn['filterIcon'],
) {
  return (filtered: boolean) => {
    if (typeof customIcon === 'function') {
      return customIcon(filtered);
    }

    if (customIcon) {
      return customIcon;
    }

    return h('i', {
      class: 'platform-table__filter-icon iconfont icon-shaixuan',
      'aria-hidden': 'true',
    });
  };
}

function isActionColumn(column: PlatformTableColumn) {
  return column.key === 'action' || column.title === '操作';
}

function appendColumnClass(column: PlatformTableColumn, className: string) {
  return {
    ...column,
    className: [column.className, className].filter(Boolean).join(' '),
  };
}

function getDefaultScrollX(columns: TableProps['columns']) {
  const leafColumns = flattenColumns(columns);

  if (leafColumns.length === 0) {
    return undefined;
  }

  const hasExplicitWidth = leafColumns.some(
    (column: PlatformTableColumn) => getColumnWidth(column) > 0,
  );
  const totalWidth = leafColumns.reduce(
    (total: number, column: PlatformTableColumn) => total + getColumnWidth(column),
    0,
  );

  if (hasExplicitWidth && totalWidth > 0) {
    return totalWidth;
  }

  return 'max-content';
}

function flattenColumns(
  columns: PlatformTableColumn[] | undefined,
): PlatformTableColumn[] {
  return (columns ?? []).flatMap((column) =>
    'children' in column && column.children?.length
      ? flattenColumns(column.children)
      : [column],
  );
}

function getColumnDataIndex(column: PlatformTableColumn) {
  if ('dataIndex' in column) {
    return column.dataIndex;
  }
  return undefined;
}

function getColumnWidth(column: PlatformTableColumn) {
  const width = column.width;

  if (typeof width === 'number') {
    return width;
  }

  if (typeof width === 'string') {
    const numericWidth = Number.parseFloat(width);
    return Number.isFinite(numericWidth) ? numericWidth : 0;
  }

  return 0;
}

function getColumnIdentity(column: PlatformTableColumn, index: number) {
  if (typeof column.key === 'string' || typeof column.key === 'number') {
    return String(column.key);
  }
  const dataIndex = getColumnDataIndex(column);
  if (typeof dataIndex === 'string' || typeof dataIndex === 'number') {
    return String(dataIndex);
  }
  if (Array.isArray(dataIndex)) {
    return dataIndex.join('.');
  }
  return `column-${index}`;
}

function getColumnSettingLabel(column: PlatformTableColumn, index: number) {
  if (typeof column.title === 'string' && column.title.trim()) {
    return column.title;
  }
  const dataIndex = getColumnDataIndex(column);
  if (typeof dataIndex === 'string' && dataIndex) {
    return dataIndex;
  }
  if (Array.isArray(dataIndex) && dataIndex.length > 0) {
    return dataIndex.join('.');
  }
  return `列${index + 1}`;
}

function isColumnRequired(column: PlatformTableColumn) {
  return (
    column.key === '__platform_index' ||
    column.key === 'action' ||
    column.title === '操作'
  );
}

function filterColumnsByVisibility(columns: PlatformTableColumn[], visibleKeys: Set<string>) {
  return (columns ?? []).filter((column, index) =>
    visibleKeys.has(getColumnIdentity(column, index)),
  );
}

function getColumnSettingStorageKey() {
  if (!props.columnSettingKey) {
    return '';
  }
  return `platform-table-columns:${props.columnSettingKey}`;
}

function syncVisibleColumns() {
  const nextItems = columnSettingItems.value;
  const defaultKeys = nextItems.map((item) => item.key);
  const storageKey = getColumnSettingStorageKey();

  if (!storageKey || typeof window === 'undefined') {
    visibleColumnKeys.value = defaultKeys;
    return;
  }

  try {
    const rawValue = window.localStorage.getItem(storageKey);
    if (!rawValue) {
      visibleColumnKeys.value = defaultKeys;
      return;
    }

    const savedKeys = JSON.parse(rawValue);
    if (!Array.isArray(savedKeys)) {
      visibleColumnKeys.value = defaultKeys;
      return;
    }

    const allowedKeys = new Set(defaultKeys);
    const requiredKeys = nextItems
      .filter((item) => item.required)
      .map((item) => item.key);
    visibleColumnKeys.value = [
      ...requiredKeys,
      ...savedKeys
        .map(String)
        .filter((key) => allowedKeys.has(key) && !requiredKeys.includes(key)),
    ];
  } catch {
    visibleColumnKeys.value = defaultKeys;
  }
}

function persistVisibleColumns() {
  const storageKey = getColumnSettingStorageKey();
  if (!storageKey || typeof window === 'undefined') {
    return;
  }

  window.localStorage.setItem(storageKey, JSON.stringify(visibleColumnKeys.value));
}

function resolveColumnSettingAnchor(anchor?: HTMLElement | MouseEvent) {
  if (anchor instanceof MouseEvent) {
    const currentTarget = anchor.currentTarget;

    if (currentTarget instanceof HTMLElement) {
      return currentTarget;
    }
  }

  if (anchor instanceof HTMLElement) {
    return anchor;
  }

  return tableWrapperRef.value ?? undefined;
}

function updateColumnSettingPosition() {
  if (typeof window === 'undefined') {
    return;
  }

  const anchor = columnSettingAnchor.value ?? tableWrapperRef.value;

  if (!anchor) {
    return;
  }

  const anchorRect = anchor.getBoundingClientRect();
  const panelWidth = columnSettingPanelRef.value?.offsetWidth ?? 360;
  const panelHeight = columnSettingPanelRef.value?.offsetHeight ?? 320;
  const gutter = 12;
  const maxLeft = Math.max(gutter, window.innerWidth - panelWidth - gutter);
  const maxTop = Math.max(gutter, window.innerHeight - panelHeight - gutter);
  const preferredLeft = anchorRect.right - panelWidth;
  const preferredTop = anchorRect.bottom + 8;

  columnSettingPosition.value = {
    left: Math.min(Math.max(preferredLeft, gutter), maxLeft),
    top: Math.min(Math.max(preferredTop, gutter), maxTop),
  };
}

function openColumnSetting(anchor?: HTMLElement | MouseEvent) {
  if (!props.columnSettingEnabled) {
    return;
  }

  const nextAnchor = resolveColumnSettingAnchor(anchor);

  if (columnSettingOpen.value && nextAnchor === columnSettingAnchor.value) {
    closeColumnSetting();
    return;
  }

  columnSettingAnchor.value = nextAnchor;
  columnSettingOpen.value = true;
  nextTick(updateColumnSettingPosition);
}

function closeColumnSetting() {
  columnSettingOpen.value = false;
  columnSettingAnchor.value = undefined;
}

function handleColumnSettingPointerDown(event: MouseEvent) {
  const target = event.target;

  if (!(target instanceof Node)) {
    return;
  }

  if (columnSettingPanelRef.value?.contains(target)) {
    return;
  }

  if (columnSettingAnchor.value?.contains(target)) {
    return;
  }

  closeColumnSetting();
}

function handleColumnSettingKeydown(event: KeyboardEvent) {
  if (event.key === 'Escape') {
    closeColumnSetting();
  }
}

function handleColumnSettingViewportChange() {
  if (!columnSettingOpen.value) {
    return;
  }

  nextTick(updateColumnSettingPosition);
}

defineExpose({
  closeColumnSetting,
  openColumnSetting,
});

watch(columnSettingOpen, (open) => {
  if (typeof window === 'undefined') {
    return;
  }

  if (open) {
    window.addEventListener('mousedown', handleColumnSettingPointerDown);
    window.addEventListener('keydown', handleColumnSettingKeydown);
    window.addEventListener('resize', handleColumnSettingViewportChange);
    window.addEventListener('scroll', handleColumnSettingViewportChange, true);
    return;
  }

  window.removeEventListener('mousedown', handleColumnSettingPointerDown);
  window.removeEventListener('keydown', handleColumnSettingKeydown);
  window.removeEventListener('resize', handleColumnSettingViewportChange);
  window.removeEventListener('scroll', handleColumnSettingViewportChange, true);
});

function flattenFilterItems(
  filters: PlatformFilterItem[] | undefined,
  depth = 0,
): PlatformFlattenedFilterItem[] {
  return (filters ?? []).flatMap((item, index) => {
    const value = item.value ?? String(index);
    const current = {
      depth,
      label: item.text ?? value,
      value,
    };
    return [
      current,
      ...flattenFilterItems(item.children, depth + 1),
    ];
  });
}

function isSameFilterKey(a: PlatformFilterKey, b: PlatformFilterKey) {
  return String(a) === String(b);
}

const PlatformTableFilterDropdown = defineComponent({
  name: 'PlatformTableFilterDropdown',
  props: {
    confirm: {
      required: true,
      type: Function,
    },
    filterMultiple: {
      default: false,
      type: Boolean,
    },
    filters: {
      default: () => [],
      type: Array,
    },
    selectedKeys: {
      default: () => [],
      type: Array,
    },
    setSelectedKeys: {
      required: true,
      type: Function,
    },
  },
  setup(componentProps) {
    const flatItems = computed(() =>
      flattenFilterItems(componentProps.filters as PlatformFilterItem[]),
    );
    const selectedKeys = computed(() =>
      (componentProps.selectedKeys as PlatformFilterKey[]).map((key) => key),
    );
    const isAllSelected = computed(() => selectedKeys.value.length === 0);

    function applySelectedKeys(keys: PlatformFilterKey[]) {
      componentProps.setSelectedKeys(keys);
      componentProps.confirm({
        closeDropdown: !componentProps.filterMultiple,
      });
    }

    function handleItemClick(value: PlatformFilterKey) {
      if (isSameFilterKey(value, PLATFORM_ALL_FILTER_KEY)) {
        applySelectedKeys([]);
        return;
      }

      if (!componentProps.filterMultiple) {
        applySelectedKeys([value]);
        return;
      }

      const hasValue = selectedKeys.value.some((key) =>
        isSameFilterKey(key, value),
      );
      applySelectedKeys(
        hasValue
          ? selectedKeys.value.filter((key) => !isSameFilterKey(key, value))
          : [...selectedKeys.value, value],
      );
    }

    function renderItem(
      item: PlatformFlattenedFilterItem,
      active: boolean,
    ) {
      return h(
        'button',
        {
          'aria-checked': active,
          class: [
            'platform-table-filter-dropdown__item',
            active && 'platform-table-filter-dropdown__item--active',
          ],
          role: componentProps.filterMultiple
            ? 'menuitemcheckbox'
            : 'menuitemradio',
          style: {
            paddingInlineStart: `${12 + item.depth * 16}px`,
          },
          type: 'button',
          onClick: (event: MouseEvent) => {
            event.stopPropagation();
            handleItemClick(item.value);
          },
        },
        [
          h('span', {
            class: [
              'platform-table-filter-dropdown__indicator',
              active &&
                'platform-table-filter-dropdown__indicator--active',
            ],
          }),
          h('span', { class: 'platform-table-filter-dropdown__label' }, [
            item.label,
          ]),
        ],
      );
    }

    return () => {
      const items = [
        {
          depth: 0,
          label: PLATFORM_ALL_FILTER_LABEL,
          value: PLATFORM_ALL_FILTER_KEY,
        },
        ...flatItems.value,
      ];

      return h(
        'div',
        {
          class: 'platform-table-filter-dropdown',
          role: 'menu',
        },
        items.map((item) =>
          renderItem(
            item,
            isSameFilterKey(item.value, PLATFORM_ALL_FILTER_KEY)
              ? isAllSelected.value
              : selectedKeys.value.some((key) =>
                  isSameFilterKey(key, item.value),
                ),
          ),
        ),
      );
    };
  },
});

const PlatformTableDateRangeFilterDropdown = defineComponent({
  name: 'PlatformTableDateRangeFilterDropdown',
  props: {
    clearFilters: {
      default: undefined,
      type: Function,
    },
    confirm: {
      required: true,
      type: Function,
    },
    placeholder: {
      default: () => ['开始日期', '结束日期'],
      type: Array,
    },
    selectedKeys: {
      default: () => [],
      type: Array,
    },
    setSelectedKeys: {
      required: true,
      type: Function,
    },
    valueFormat: {
      default: 'YYYY-MM-DD',
      type: String,
    },
  },
  setup(componentProps) {
    const pickerOpen = ref(true);
    const rangeValue = computed(() => {
      const firstKey = componentProps.selectedKeys?.[0];

      if (typeof firstKey !== 'string' || !firstKey) {
        return [];
      }

      const [startDate = '', endDate = ''] = firstKey.split('|');

      return startDate && endDate ? [startDate, endDate] : [];
    });

    function handleChange(value: unknown) {
      const nextValue = Array.isArray(value)
        ? value.filter(
            (item): item is string => typeof item === 'string' && item.length > 0,
          )
        : [];

      if (nextValue.length === 2) {
        pickerOpen.value = false;
        componentProps.setSelectedKeys([nextValue.join('|')]);
        componentProps.confirm({
          closeDropdown: true,
        });
        return;
      }

      pickerOpen.value = false;
      componentProps.setSelectedKeys([]);
      componentProps.clearFilters?.({
        closeDropdown: true,
        confirm: true,
      });
    }

    function handleClear() {
      pickerOpen.value = false;
      componentProps.setSelectedKeys([]);
      componentProps.clearFilters?.({
        closeDropdown: true,
        confirm: true,
      });
    }

    return () =>
      h('div', { class: 'platform-table-filter-dropdown platform-table-filter-dropdown--date-range' }, [
        h(PlatformRangePicker, {
          allowClear: true,
          class: 'platform-table-filter-dropdown__range-trigger',
          format: componentProps.valueFormat,
          getPopupContainer: (triggerNode: HTMLElement) =>
            triggerNode.parentElement ?? triggerNode,
          open: pickerOpen.value,
          placeholder: componentProps.placeholder,
          value: rangeValue.value,
          valueFormat: componentProps.valueFormat,
          onChange: handleChange,
          onOpenChange: (open: boolean) => {
            pickerOpen.value = open;
          },
        }),
        h(
          'div',
          { class: 'platform-table-filter-dropdown__footer' },
          [
            h(
              'button',
              {
                class: 'platform-table-filter-dropdown__footer-button',
                type: 'button',
                onClick: handleClear,
              },
              '清空',
            ),
          ],
        ),
      ]);
  },
});
</script>

<template>
  <div
    ref="tableWrapperRef"
    class="platform-table-wrapper"
    :style="tableWrapperStyle"
  >
    <Table
      v-bind="$attrs"
      class="platform-table"
      :columns="mergedColumns"
      :pagination="mergedPagination"
      :scroll="mergedScroll"
      @change="handleChange"
    >
      <template #bodyCell="slotProps">
        <template v-if="slotProps.column?.key === '__platform_index'">
          {{ getIndexCellValue(slotProps.index) }}
        </template>
        <slot v-else name="bodyCell" v-bind="slotProps || {}"></slot>
      </template>
      <template v-for="name in passthroughSlotNames" #[name]="slotProps">
        <slot :name="name" v-bind="slotProps || {}"></slot>
      </template>
    </Table>

    <Teleport to="body">
      <div
        v-if="columnSettingOpen"
        ref="columnSettingPanelRef"
        class="platform-table-column-setting-panel"
        :style="{
          left: `${columnSettingPosition.left}px`,
          top: `${columnSettingPosition.top}px`,
        }"
      >
        <div class="platform-table-column-setting">
          <CheckboxGroup v-model:value="checkedColumnKeys">
            <Checkbox
              v-for="item in columnSettingItems"
              :key="item.key"
              :disabled="item.required"
              :value="item.key"
            >
              {{ item.label }}
            </Checkbox>
          </CheckboxGroup>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<style scoped>
.platform-table-wrapper {
  display: flex;
  flex-direction: column;
  min-height: var(--platform-table-wrapper-min-height, 0);
}

.platform-table-column-setting-panel {
  position: fixed;
  z-index: 1080;
  width: max-content;
  max-width: calc(100vw - 24px);
  max-height: min(420px, calc(100vh - 24px));
  min-width: 0;
  padding: 16px;
  overflow: auto;
  background: hsl(var(--background));
  border: 1px solid hsl(var(--st-color-border-control));
  border-radius: var(--st-radius-card);
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.12);
}

.platform-table-column-setting {
  display: grid;
  gap: 12px;
}

.platform-table-column-setting :deep(.ant-checkbox-group) {
  display: grid;
  gap: 12px;
}

.platform-table-column-setting :deep(.ant-checkbox-wrapper) {
  margin-inline-start: 0;
}

.platform-table {
  flex: 1;
  width: 100%;
}

.platform-table :deep(.ant-table-body) {
  min-height: var(--platform-table-body-min-height, 0);
}

.platform-table :deep(.ant-table) {
  border-radius: var(--st-radius-card);
}

.platform-table :deep(.ant-table-container) {
  overflow: hidden;
  border: 1px solid hsl(var(--st-color-table-outline));
  border-radius: var(--st-radius-card);
}

.platform-table :deep(.ant-table-content),
.platform-table :deep(.ant-table-body) {
  scrollbar-color: hsl(var(--st-color-border-control)) transparent;
  scrollbar-width: thin;
}

.platform-table :deep(.ant-table-content::-webkit-scrollbar),
.platform-table :deep(.ant-table-body::-webkit-scrollbar) {
  height: 10px;
}

.platform-table :deep(.ant-table-content::-webkit-scrollbar-thumb),
.platform-table :deep(.ant-table-body::-webkit-scrollbar-thumb) {
  background: hsl(var(--st-color-border-control));
  border-radius: 999px;
}

.platform-table :deep(.ant-table-content::-webkit-scrollbar-track),
.platform-table :deep(.ant-table-body::-webkit-scrollbar-track) {
  background: transparent;
}

.platform-table :deep(.ant-table-thead > tr > th) {
  font-weight: 500;
}

.platform-table :deep(.platform-table__index-column-header),
.platform-table :deep(.platform-table__index-column-header .ant-table-column-title),
.platform-table :deep(.platform-table__index-column-cell) {
  white-space: nowrap;
}

.platform-table :deep(.ant-table-thead > tr > th:first-child),
.platform-table :deep(.ant-table-tbody > tr > td:first-child),
.platform-table :deep(.platform-table__action-column) {
  padding-inline: var(--st-table-edge-cell-padding);
}

.platform-table :deep(.ant-table-cell-fix),
.platform-table :deep(.ant-table-cell-fix-left),
.platform-table :deep(.ant-table-cell-fix-right),
.platform-table :deep(.ant-table-cell-fix-left-last),
.platform-table :deep(.ant-table-cell-fix-right-first) {
  isolation: isolate;
  z-index: 30 !important;
  overflow: hidden;
  background: hsl(var(--st-color-table-cell-bg-solid)) !important;
  background-color: hsl(var(--st-color-table-cell-bg-solid)) !important;
  background-clip: padding-box;
  background-image: none !important;
}

.platform-table :deep(.ant-table-thead > tr > th.ant-table-cell-fix),
.platform-table :deep(.ant-table-thead > tr > th.ant-table-cell-fix-left),
.platform-table :deep(.ant-table-thead > tr > th.ant-table-cell-fix-right),
.platform-table :deep(.ant-table-thead > tr > th.ant-table-cell-fix-left-last),
.platform-table :deep(.ant-table-thead > tr > th.ant-table-cell-fix-right-first) {
  z-index: 31 !important;
  background: hsl(var(--st-color-table-header-bg)) !important;
  background-color: hsl(var(--st-color-table-header-bg)) !important;
  background-image: none !important;
}

.platform-table
  :deep(.ant-table-tbody > tr.ant-table-row:hover > td.ant-table-cell-fix),
.platform-table
  :deep(.ant-table-tbody > tr.ant-table-row:hover > td.ant-table-cell-fix-left),
.platform-table
  :deep(.ant-table-tbody > tr.ant-table-row:hover > td.ant-table-cell-fix-right),
.platform-table
  :deep(.ant-table-tbody > tr.ant-table-row:hover > td.ant-table-cell-fix-left-last),
.platform-table
  :deep(.ant-table-tbody > tr.ant-table-row:hover > td.ant-table-cell-fix-right-first),
.platform-table
  :deep(.ant-table-tbody > tr > td.ant-table-cell-fix.ant-table-cell-row-hover),
.platform-table
  :deep(.ant-table-tbody > tr > td.ant-table-cell-fix-left.ant-table-cell-row-hover),
.platform-table
  :deep(.ant-table-tbody > tr > td.ant-table-cell-fix-right.ant-table-cell-row-hover),
.platform-table
  :deep(.ant-table-tbody > tr > td.ant-table-cell-fix-left-last.ant-table-cell-row-hover),
.platform-table :deep(.ant-table-tbody > tr > td.ant-table-cell-fix-right-first.ant-table-cell-row-hover) {
  z-index: 32 !important;
  background: var(--st-color-table-row-hover-bg-solid) !important;
  background-color: var(--st-color-table-row-hover-bg-solid) !important;
  background-image: none !important;
}

.platform-table
  :deep(
    .ant-table-tbody
      > tr.ant-table-row-selected
      > td.ant-table-cell-fix
  ),
.platform-table
  :deep(
    .ant-table-tbody
      > tr.ant-table-row-selected
      > td.ant-table-cell-fix-left
  ),
.platform-table
  :deep(
    .ant-table-tbody
      > tr.ant-table-row-selected
      > td.ant-table-cell-fix-right
  ) {
  z-index: 32 !important;
  background: hsl(var(--st-color-table-row-selected-bg)) !important;
  background-color: hsl(var(--st-color-table-row-selected-bg)) !important;
  background-image: none !important;
}

.platform-table :deep(.ant-table-filter-column) {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  max-width: 100%;
}

.platform-table :deep(.ant-table-filter-column-title) {
  flex: none;
}

.platform-table :deep(.ant-table-filter-trigger) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  margin-inline-start: 0;
  color: hsl(var(--st-color-text-tertiary));
  border-radius: var(--st-radius-control);
  transition:
    color 0.16s ease,
    background-color 0.16s ease;
}

.platform-table :deep(.ant-table-filter-trigger:hover),
.platform-table :deep(.ant-table-filter-trigger.active) {
  color: hsl(var(--primary));
  background: hsl(var(--st-color-fill-selected));
}

.platform-table :deep(.platform-table__filter-icon) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 14px;
  height: 14px;
  font-size: 16px;
  line-height: 1;
}

:global(.platform-table-filter-dropdown) {
  min-width: 168px;
  max-height: 320px;
  padding: 6px;
  overflow-y: auto;
}

:global(.platform-table-filter-dropdown__item) {
  display: flex;
  width: 100%;
  height: 36px;
  align-items: center;
  gap: 10px;
  padding: 0 12px;
  color: hsl(var(--foreground));
  font-size: var(--st-font-size-base);
  line-height: var(--st-line-height-base);
  text-align: left;
  cursor: pointer;
  background: transparent;
  border: 0;
  border-radius: var(--st-radius-control);
  transition:
    color 0.16s ease,
    background-color 0.16s ease;
}

:global(.platform-table-filter-dropdown__item:hover),
:global(.platform-table-filter-dropdown__item--active) {
  background: hsl(var(--st-color-fill-selected));
}

:global(.platform-table-filter-dropdown__indicator) {
  position: relative;
  display: inline-flex;
  flex: none;
  width: 16px;
  height: 16px;
  background: hsl(var(--background));
  border: 1px solid hsl(var(--st-color-border-control));
  border-radius: 999px;
  transition:
    background-color 0.16s ease,
    border-color 0.16s ease;
}

:global(.platform-table-filter-dropdown__indicator--active) {
  border-color: hsl(var(--primary));
}

:global(.platform-table-filter-dropdown__indicator--active::after) {
  position: absolute;
  inset: 4px;
  content: '';
  background: hsl(var(--primary));
  border-radius: inherit;
}

:global(.platform-table-filter-dropdown__label) {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

:global(.platform-table-filter-dropdown--date-range) {
  min-width: 0;
  max-height: none;
  padding: 0;
}

:global(.platform-table-filter-dropdown--date-range .platform-table-filter-dropdown__range-trigger) {
  position: absolute;
  width: 1px;
  height: 1px;
  opacity: 0;
  pointer-events: none;
  overflow: hidden;
}

:global(.platform-table-filter-dropdown--date-range .ant-picker-dropdown) {
  position: static !important;
  inset: auto !important;
  display: block !important;
  min-width: 0 !important;
  box-shadow: none !important;
}

:global(.platform-table-filter-dropdown__footer) {
  display: flex;
  justify-content: flex-end;
  padding: 0 12px 12px;
}

:global(.platform-table-filter-dropdown__footer-button) {
  color: hsl(var(--primary));
  font-size: var(--st-font-size-sm);
  background: transparent;
  border: 0;
  cursor: pointer;
}

:global(.platform-table-filter-dropdown__footer-button:hover) {
  color: hsl(var(--st-color-brand-hover));
}
</style>
