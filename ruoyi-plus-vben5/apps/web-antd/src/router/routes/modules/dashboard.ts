import type { RouteRecordRaw } from 'vue-router';

const PLATFORM_HOME_PATH = '/workbench/index';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      hideInMenu: true,
      order: -1,
      title: '旧概览入口',
    },
    name: 'Dashboard',
    path: '/dashboard',
    redirect: PLATFORM_HOME_PATH,
    children: [
      {
        name: 'Analytics',
        path: '/analytics',
        redirect: PLATFORM_HOME_PATH,
        meta: {
          hideInMenu: true,
          title: '旧分析页入口',
        },
      },
      {
        name: 'Workspace',
        path: '/workspace',
        redirect: PLATFORM_HOME_PATH,
        meta: {
          hideInMenu: true,
          title: '旧工作台入口',
        },
      },
      {
        name: 'VbenDocument',
        path: '/vben-admin/document',
        redirect: PLATFORM_HOME_PATH,
        meta: {
          hideInMenu: true,
          title: '旧文档入口',
        },
      },
      {
        name: 'V5UpdateLog',
        path: '/changelog',
        redirect: PLATFORM_HOME_PATH,
        meta: {
          hideInMenu: true,
          title: '旧更新记录入口',
        },
      },
    ],
  },
  {
    redirect: PLATFORM_HOME_PATH,
    meta: {
      hideInMenu: true,
      title: '旧关于入口',
    },
    name: 'About',
    path: '/vben-admin/about',
  },
];

export default routes;
