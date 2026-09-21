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
      {
        children: [],
        component: 'platform/blank/index',
        hidden: false,
        meta: { icon: 'lucide:users-2', noCache: false, title: '通讯录' },
        name: 'WorkbenchContacts',
        path: '/workbench/contacts',
      },
    ],
    component: 'Layout',
    hidden: false,
    meta: { icon: 'lucide:layout-dashboard', noCache: false, order: 0, title: '工作台' },
    name: 'WorkbenchMenu',
    path: '/workbench',
  },
  // ====== 一级：系统管理 ======
  {
    children: [
      {
        children: [],
        component: 'platform/blank/index',
        hidden: false,
        meta: { icon: 'lucide:building-2', noCache: false, title: '机构管理' },
        name: 'SystemDept',
        path: '/system/dept',
      },
      {
        children: [],
        component: 'platform/blank/index',
        hidden: false,
        meta: { icon: 'lucide:menu', noCache: false, title: '菜单管理' },
        name: 'SystemMenu',
        path: '/system/menu',
      },
      {
        children: [],
        component: 'platform/blank/index',
        hidden: false,
        meta: { icon: 'lucide:user', noCache: false, title: '用户管理' },
        name: 'SystemUser',
        path: '/system/user',
      },
      {
        children: [],
        component: 'platform/blank/index',
        hidden: false,
        meta: { icon: 'lucide:shield-check', noCache: false, title: '数据权限规则管理' },
        name: 'SystemDataScope',
        path: '/system/data-scope',
      },
      {
        children: [],
        component: 'platform/blank/index',
        hidden: false,
        meta: { icon: 'lucide:book-copy', noCache: false, title: '数据字典' },
        name: 'SystemDict',
        path: '/system/dict',
      },
      {
        children: [],
        component: 'platform/blank/index',
        hidden: false,
        meta: { icon: 'lucide:users-round', noCache: false, title: '角色管理' },
        name: 'SystemRole',
        path: '/system/role',
      },
      {
        children: [],
        component: 'platform/blank/index',
        hidden: false,
        meta: { icon: 'lucide:user-cog', noCache: false, title: '岗位管理' },
        name: 'SystemPost',
        path: '/system/post',
      },
      {
        children: [],
        component: 'platform/blank/index',
        hidden: false,
        meta: { icon: 'lucide:scroll-text', noCache: false, title: '日志管理' },
        name: 'SystemOperlog',
        path: '/system/operlog',
      },
      {
        children: [],
        component: 'platform/blank/index',
        hidden: false,
        meta: { icon: 'lucide:git-branch', noCache: false, title: '工作流管理' },
        name: 'SystemWorkflow',
        path: '/system/workflow',
      },
    ],
    component: 'Layout',
    hidden: false,
    meta: { icon: 'lucide:settings', noCache: false, order: 1, title: '系统管理' },
    name: 'SystemManagementMenu',
    path: '/system-management',
  },
  // ====== 一级：项目全景管理 ======
  {
    children: [
      {
        children: [
          {
            children: [],
            component: 'project/information/index',
            hidden: false,
            meta: { icon: 'icon-xiangmuxinxiguanli', noCache: false, title: '项目基础信息管理' },
            name: 'ProjectBasicInfo',
            path: 'basic-info',
          },
          {
            children: [],
            component: 'platform/blank/index',
            hidden: false,
            meta: { icon: 'lucide:archive', noCache: false, title: '项目归档' },
            name: 'ProjectArchive',
            path: 'archive',
          },
          {
            children: [],
            component: 'project/progress/index',
            hidden: false,
            meta: { icon: 'icon-jindukeshihuagenzong', noCache: false, title: '项目进度管理' },
            name: 'ProjectProgress',
            path: 'progress',
          },
        ],
        component: 'ParentView',
        hidden: false,
        meta: { icon: 'lucide:folder-open', noCache: false, title: '项目管理' },
        name: 'ProjectManageGroup',
        path: '/project/manage',
      },
      {
        children: [
          {
            children: [],
            component: 'project/contract/index',
            hidden: false,
            meta: { icon: 'icon-hetongyufukuanguanli', noCache: false, title: '合同管理' },
            name: 'ContractManage',
            path: 'contract',
          },
        ],
        component: 'ParentView',
        hidden: false,
        meta: { icon: 'lucide:file-text', noCache: false, title: '合同与付款管理' },
        name: 'ContractPaymentGroup',
        path: '/project/contract-payment',
      },
    ],
    component: 'Layout',
    hidden: false,
    meta: { icon: 'lucide:layout-grid', noCache: false, order: 2, title: '项目全景管理' },
    name: 'ProjectPanoramaMenu',
    path: '/project-panorama',
  },
  // ====== 一级：商/承包商全流程管理 ======
  {
    children: [
      {
        children: [],
        component: 'platform/blank/index',
        hidden: false,
        meta: { icon: 'lucide:building', noCache: false, title: '承包商信息管理' },
        name: 'ContractorInfo',
        path: '/contractor-full-process/info',
      },
      {
        children: [],
        component: 'platform/blank/index',
        hidden: false,
        meta: { icon: 'lucide:clipboard-check', noCache: false, title: '考核管理' },
        name: 'ContractorAssessment',
        path: '/contractor-full-process/assessment',
      },
      {
        children: [],
        component: 'platform/blank/index',
        hidden: false,
        meta: { icon: 'lucide:ban', noCache: false, title: '黑名单管理' },
        name: 'ContractorBlacklist',
        path: '/contractor-full-process/blacklist',
      },
      {
        children: [],
        component: 'platform/blank/index',
        hidden: false,
        meta: { icon: 'lucide:split', noCache: false, title: '分包与进场管理' },
        name: 'ContractorSubcontract',
        path: '/contractor-full-process/subcontract',
      },
    ],
    component: 'Layout',
    hidden: false,
    meta: { icon: 'lucide:truck', noCache: false, order: 3, title: '商/承包商全流程管理' },
    name: 'ContractorFullProcessMenu',
    path: '/contractor-full-process',
  },
  // ====== 一级：人员管理 ======
  {
    children: [
      {
        children: [],
        component: 'personnel/overview/index',
        hidden: false,
        meta: { icon: 'icon-renyuanzonglan', noCache: false, title: '人员实名制管理' },
        name: 'PersonnelRealName',
        path: '/lifecycle-center/realname',
      },
      {
        children: [],
        component: 'platform/blank/index',
        hidden: false,
        meta: { icon: 'lucide:clock', noCache: false, title: '考勤处理' },
        name: 'PersonnelAttendance',
        path: '/lifecycle-center/attendance',
      },
      {
        children: [],
        component: 'personnel/turnover/index',
        hidden: false,
        meta: { icon: 'icon-biandongyuliushishuaitongji', noCache: false, title: '查询与统计' },
        name: 'PersonnelStatistics',
        path: '/lifecycle-center/statistics',
      },
    ],
    component: 'Layout',
    hidden: false,
    meta: { icon: 'lucide:heart-pulse', noCache: false, order: 4, title: '人员管理' },
    name: 'LifecycleCenterMenu',
    path: '/lifecycle-center',
  },
  // ====== 一级：智能考勤管理 ======
  {
    children: [
      {
        children: [],
        component: 'platform/blank/index',
        hidden: false,
        meta: { icon: 'lucide:calendar-off', noCache: false, title: '假勤申请' },
        name: 'AttendanceLeaveApply',
        path: '/smart-attendance/leave-apply',
      },
      {
        children: [],
        component: 'platform/blank/index',
        hidden: false,
        meta: { icon: 'lucide:bar-chart-2', noCache: false, title: '查询统计' },
        name: 'AttendanceQueryStats',
        path: '/smart-attendance/query-stats',
      },
      {
        children: [],
        component: 'platform/blank/index',
        hidden: false,
        meta: { icon: 'lucide:sliders-horizontal', noCache: false, title: '基础配置' },
        name: 'AttendanceConfig',
        path: '/smart-attendance/config',
      },
    ],
    component: 'Layout',
    hidden: false,
    meta: { icon: 'lucide:battery', noCache: false, order: 5, title: '智能考勤管理' },
    name: 'SmartAttendanceMenu',
    path: '/smart-attendance',
  },
  // ====== 一级：培训与认证管理 ======
  {
    children: [
      {
        children: [],
        component: 'platform/blank/index',
        hidden: false,
        meta: { icon: 'lucide:graduation-cap', noCache: false, title: '考试课程管理' },
        name: 'TrainingExamCourse',
        path: '/training-cert/exam-course',
      },
    ],
    component: 'Layout',
    hidden: false,
    meta: { icon: 'lucide:award', noCache: false, order: 6, title: '培训与认证管理' },
    name: 'TrainingCertMenu',
    path: '/training-cert',
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
