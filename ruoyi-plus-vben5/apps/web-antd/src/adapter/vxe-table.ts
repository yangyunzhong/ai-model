import { h } from 'vue';

import {
  createPlatformVxeGrid,
  setupVbenVxeTable,
  useVbenVxeGrid as useBaseVbenVxeGrid,
} from '@st/platform-adapter/vxe-table';
import { Button, Image } from 'antdv-next';

import { useVbenForm } from './form';

setupVbenVxeTable({
  configVxeTable: (vxeUI) => {
    vxeUI.setConfig({
      grid: {
        align: 'center',
        // https://vxetable.cn/#/component/table/base/border
        border: 'inner',
        minHeight: 180,
        formConfig: {
          // 全局禁用vxe-table的表单配置，使用formOptions
          enabled: false,
        },
        proxyConfig: {
          autoLoad: true,
          response: {
            result: 'rows',
            total: 'total',
            list: 'rows',
          },
          showActiveMsg: true,
          showResponseMsg: false,
        },
        // 溢出展示形式
        showOverflow: true,
        pagerConfig: {
          // 默认条数
          pageSize: 10,
          // 分页可选条数
          pageSizes: [10, 20, 30, 40, 50],
        },
        rowConfig: {
          // 鼠标移入行显示 hover 样式
          isHover: true,
          // 点击行高亮
          isCurrent: false,
        },
        columnConfig: {
          // 可拖拽列宽
          resizable: true,
        },
        headerCellConfig: {
          height: 44,
        },
        cellConfig: {
          height: 44,
        },
        // 右上角工具栏
        toolbarConfig: {
          // 自定义列
          custom: true,
          customOptions: {
            icon: 'vxe-icon-setting',
          },
          // 最大化
          zoom: true,
          // 刷新
          refresh: true,
          refreshOptions: {
            // 默认为reload 修改为在当前页刷新
            code: 'query',
          },
        },
        // 圆角按钮
        round: true,
        // 表格尺寸
        size: 'medium',
        customConfig: {
          // 表格右上角自定义列配置 是否保存到localStorage
          // 必须存在id参数才能使用
          storage: false,
        },
      },
    });

    // 表格配置项可以用 cellRender: { name: 'CellImage' },
    vxeUI.renderer.add('CellImage', {
      renderTableDefault(renderOpts, params) {
        const { props } = renderOpts;
        const { column, row } = params;
        return h(Image, { src: row[column.field], ...props });
      },
    });

    // 表格配置项可以用 cellRender: { name: 'CellLink' },
    vxeUI.renderer.add('CellLink', {
      renderTableDefault(renderOpts) {
        const { props } = renderOpts;
        return h(
          Button,
          { size: 'small', type: 'link' },
          { default: () => props?.text },
        );
      },
    });

    // 这里可以自行扩展 vxe-table 的全局配置，比如自定义格式化
    // vxeUI.formats.add
  },
  useVbenForm,
});

export const useVbenVxeGrid = createPlatformVxeGrid(useBaseVbenVxeGrid);

export { addSortParams, vxeCheckboxChecked } from '@st/platform-adapter/vxe-table';
export type * from '@st/platform-adapter/vxe-table';
