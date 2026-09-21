import { fileURLToPath, URL } from 'node:url';

import { defineConfig } from '@vben/vite-config';

// 自行取消注释来启用按需导入功能
// import { AntdvNextResolver } from '@antdv-next/auto-import-resolver'
// import Components from 'unplugin-vue-components/vite';

export default defineConfig(async () => {
  return {
    application: {},
    vite: {
      optimizeDeps: {
        exclude: ['@vueuse/motion', 'antdv-next', 'vue-i18n', 'vue-json-pretty'],
        include: [
          'dayjs',
          'dayjs/plugin/advancedFormat',
          'dayjs/plugin/customParseFormat',
          'dayjs/plugin/localeData',
          'dayjs/plugin/weekday',
          'dayjs/plugin/weekOfYear',
          'dayjs/plugin/weekYear',
        ],
      },
      plugins: [
        // Components({
        //   dirs: [], // 默认会导入src/components目录下所有组件 不需要
        //   dts: './types/components.d.ts', // 输出类型文件
        //   resolvers: [
        //     AntdvNextResolver({
        //       // 需要排除Button组件 全局已经默认导入了
        //       exclude: ['Button'],
        //     }),
        //   ],
        // }),
      ],
      resolve: {
        alias: [
          {
            find: 'antdv-next/locale',
            replacement: fileURLToPath(
              new URL('node_modules/antdv-next/dist/locale', import.meta.url),
            ),
          },
          {
            find: 'antdv-next',
            replacement: fileURLToPath(
              new URL('node_modules/antdv-next', import.meta.url),
            ),
          },
          {
            find: 'vue-router',
            replacement: fileURLToPath(
              new URL('node_modules/vue-router', import.meta.url),
            ),
          },
          {
            find: '@st/platform-styles/antd',
            replacement: fileURLToPath(
              new URL('../../packages/platform-styles/src/antd/index.css', import.meta.url),
            ),
          },
          {
            find: '@st/platform-styles/vxe-table',
            replacement: fileURLToPath(
              new URL(
                '../../packages/platform-styles/src/vxe-table/index.css',
                import.meta.url,
              ),
            ),
          },
          {
            find: '@st/platform-adapter',
            replacement: fileURLToPath(
              new URL('../../packages/platform-adapter/src', import.meta.url),
            ),
          },
          {
            find: '@st/platform-styles',
            replacement: fileURLToPath(
              new URL('../../packages/platform-styles/src', import.meta.url),
            ),
          },
          {
            find: '@st/platform-types',
            replacement: fileURLToPath(
              new URL('../../packages/platform-types/src', import.meta.url),
            ),
          },
          {
            find: '@st/platform-ui',
            replacement: fileURLToPath(
              new URL('../../packages/platform-ui/src', import.meta.url),
            ),
          },
          {
            find: '@st/platform-utils',
            replacement: fileURLToPath(
              new URL('../../packages/platform-utils/src', import.meta.url),
            ),
          },
          {
            find: '@vben/plugins',
            replacement: fileURLToPath(
              new URL('../../packages/effects/plugins/src', import.meta.url),
            ),
          },
          {
            find: '@vben/icons',
            replacement: fileURLToPath(
              new URL('node_modules/@vben/icons', import.meta.url),
            ),
          },
        ],
      },
      server: {
        proxy: {
          '/api': {
            changeOrigin: true,
            rewrite: (path) => path.replace(/^\/api/, ''),
            // mock代理目标地址
            target: 'http://localhost:8080',
            ws: true,
          },
        },
      },
    },
  };
}) as any;
