import type { Menu } from '#/api/core/menu';

import { DEFAULT_TENANT_ID } from '@vben/constants';

const MOCK_ACCESS_TOKEN = 'mock-access-token';

function isMockMode() {
  return import.meta.env.VITE_USE_MOCK === 'true';
}

const mockLoginResult = {
  access_token: MOCK_ACCESS_TOKEN,
  client_id: import.meta.env.VITE_GLOB_APP_CLIENT_ID || 'mock-client-id',
  expire_in: 7200,
};

const mockTenantResp = {
  tenantEnabled: false,
  voList: [
    {
      companyName: 'Mock 默认租户',
      tenantId: DEFAULT_TENANT_ID,
    },
  ],
};

const mockCaptchaResp = {
  captchaEnabled: false,
  img: '',
  uuid: 'mock-captcha-uuid',
};

const mockUserInfoResp = {
  permissions: ['*:*:*'],
  roles: ['admin'],
  user: {
    avatar: '',
    createTime: '2026-05-05 00:00:00',
    deptId: 100,
    deptName: '平台组件中心',
    email: 'mock.admin@gzzr.local',
    loginDate: '2026-05-05 00:00:00',
    loginIp: '127.0.0.1',
    nickName: 'Mock 管理员',
    phonenumber: '13800000000',
    remark: '前端静态开发 Mock 用户',
    roles: [
      {
        dataScope: '1',
        flag: true,
        roleId: 1,
        roleKey: 'admin',
        roleName: '管理员',
        roleSort: 1,
        status: '0',
        superAdmin: false,
      },
    ],
    sex: '0',
    status: '0',
    tenantId: DEFAULT_TENANT_ID,
    userId: 1,
    userName: 'admin',
    userType: 'sys_user',
  },
};

const mockBackendMenuList = [
  // ====== 一级：工作台 ======
  {
    children: [
      {
        children: [],
        component: 'workbench/home/index',
        hidden: false,
        meta: { icon: 'lucide:home', noCache: false, title: '首页' },
        name: 'WorkbenchHome',
        path: '/workbench/index',
      },
    ],
    component: 'Layout',
    hidden: false,
    meta: { icon: 'lucide:layout-dashboard', noCache: false, order: 0, title: '工作台' },
    name: 'WorkbenchMenu',
    path: '/workbench',
  },
  // ====== 一级：模型概览 ======
  {
    children: [
      {
        children: [],
        component: 'model/overview/index',
        hidden: false,
        meta: { icon: 'lucide:gauge', noCache: false, title: '总览看板' },
        name: 'ModelOverviewDashboard',
        path: '/model-overview/dashboard',
      },
    ],
    component: 'Layout',
    hidden: false,
    meta: { icon: 'lucide:chart-pie', noCache: false, order: 7, title: '模型概览' },
    name: 'ModelOverviewMenu',
    path: '/model-overview',
  },
  // ====== 一级：模型管理 ======
  {
    children: [
      {
        children: [],
        component: 'model/manage/index',
        hidden: false,
        meta: { icon: 'lucide:list', noCache: false, title: '模型列表' },
        name: 'ModelList',
        path: '/model-manage/list',
      },
    ],
    component: 'Layout',
    hidden: false,
    meta: { icon: 'lucide:boxes', noCache: false, order: 8, title: '模型管理' },
    name: 'ModelManageMenu',
    path: '/model-manage',
  },
  // ====== 一级：数据比对 ======
  {
    children: [
      {
        children: [],
        component: 'model/compare/index',
        hidden: false,
        meta: { icon: 'lucide:git-compare', noCache: false, title: '比对任务' },
        name: 'DataComparisonTask',
        path: '/model-comparison/task',
      },
    ],
    component: 'Layout',
    hidden: false,
    meta: { icon: 'lucide:git-compare-arrows', noCache: false, order: 9, title: '数据比对' },
    name: 'DataComparisonMenu',
    path: '/model-comparison',
  },
  // ====== 一级：模型任务 ======
  {
    children: [
      {
        children: [],
        component: 'model/task/index',
        hidden: false,
        meta: { icon: 'lucide:clipboard-list', noCache: false, title: '任务列表' },
        name: 'ModelTaskList',
        path: '/model-task/list',
      },
    ],
    component: 'Layout',
    hidden: false,
    meta: { icon: 'lucide:list-checks', noCache: false, order: 10, title: '模型任务' },
    name: 'ModelTaskMenu',
    path: '/model-task',
  },
  // ====== 一级：模型类别 ======
  {
    children: [
      {
        children: [],
        component: 'model/category/index',
        hidden: false,
        meta: { icon: 'lucide:tags', noCache: false, title: '类别管理' },
        name: 'ModelCategoryManage',
        path: '/model-category/manage',
      },
    ],
    component: 'Layout',
    hidden: false,
    meta: { icon: 'lucide:folder-tree', noCache: false, order: 11, title: '模型类别' },
    name: 'ModelCategoryMenu',
    path: '/model-category',
  },
] satisfies Menu[];

function getMockBackendMenuList() {
  return structuredClone(mockBackendMenuList) as Menu[];
}

export {
  getMockBackendMenuList,
  isMockMode,
  mockBackendMenuList,
  mockCaptchaResp,
  mockLoginResult,
  mockTenantResp,
  mockUserInfoResp,
};
