import { defineComponent, h } from 'vue';

import { Button } from 'antdv-next';
import { omit } from 'lodash-es';

/**
 * 表格操作列按钮专用
 */
export const ActionButton = defineComponent({
  name: 'ActionButton',
  props: omit({}, ['type', 'ghost', 'size']),
  setup(props, { attrs, slots }) {
    return () =>
      h(
        Button,
        {
          ...props,
          ...attrs,
          class: ['platform-action-button', attrs.class],
          type: 'link',
          // ghost: true,
          size: 'small',
        },
        slots,
      );
  },
});
