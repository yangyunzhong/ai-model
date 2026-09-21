import { fileURLToPath, URL } from 'node:url';

import tailwindcss from '@tailwindcss/vite';
import vue from '@vitejs/plugin-vue';
import { defineConfig } from 'vite';

import { viteTailwindReferencePlugin } from '../../internal/vite-config/src/plugins/tailwind-reference';

export default defineConfig({
  plugins: [vue(), viteTailwindReferencePlugin(), tailwindcss()],
  preview: {
    host: '127.0.0.1',
    port: 4174,
    strictPort: true,
  },
  resolve: {
    alias: [
      {
        find: '@st/platform-styles/antd',
        replacement: fileURLToPath(
          new URL('../../packages/platform-styles/src/antd/index.css', import.meta.url),
        ),
      },
      {
        find: '@st/platform-styles',
        replacement: fileURLToPath(
          new URL('../../packages/platform-styles/src', import.meta.url),
        ),
      },
      {
        find: '@st/platform-ui',
        replacement: fileURLToPath(
          new URL('../../packages/platform-ui/src', import.meta.url),
        ),
      },
      {
        find: '@vben/icons',
        replacement: fileURLToPath(
          new URL('../../packages/icons/src', import.meta.url),
        ),
      },
      {
        find: '@vben/styles',
        replacement: fileURLToPath(
          new URL('../../packages/styles/src', import.meta.url),
        ),
      },
    ],
  },
  server: {
    host: '127.0.0.1',
    port: 5174,
    strictPort: true,
  },
});
