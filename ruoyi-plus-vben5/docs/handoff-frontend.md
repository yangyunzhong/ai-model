# 前端联调交付说明

本文档面向接收 `/Users/zhuguangxiang/Desktop/ST-design` 前端源码包的开发同学，用于本地启动、页面接续开发、Mock 数据定位、接口联调替换和交付验收。

## 1. 交付目标

本次交付不是单纯的 `dist` 预览包，而是一个可继续开发、可继续联调、可继续上线的完整前端项目快照。

交付包必须同时满足：

| 目标 | 说明 |
| --- | --- |
| 可继续开发 | 保留 monorepo 源码结构，而不是只交一个 `apps/web-antd` 子目录 |
| 可直接预览 | 交付包根目录 `index.html` 可双击打开并直接进入登录页；如本地浏览器拦截 `file://` 资源，再双击 `打开预览.command` |
| 可继续联调 | 保留页面源码、平台组件、Mock 菜单、页面专用 source 文件和联调说明 |
| 可定位问题 | 明确每个页面的路由、源码文件、数据源、字段映射、交互入口和待接 API |

## 2. 交付结构

建议交付结构如下：

| 路径 | 用途 |
| --- | --- |
| `index.html` | 根预览入口页，双击后直接跳到登录页 |
| `打开预览.command` | 兼容兜底入口；双击启动本地静态服务，自动打开登录页 |
| `README.md` | 交付包总说明、启动方式和使用建议 |
| `docs/handoff-frontend.md` | 本文档，作为接口联调交接主文档 |
| `assets/screenshots/*` | 浏览器自检截图，供快速核对页面成果 |
| `preview/` | 离线预览资源目录 |
| `source/` | 可继续开发的源码项目快照 |

## 3. 源码项目说明

| 项 | 说明 |
| --- | --- |
| 主应用 | `apps/web-antd` |
| 项目形态 | Vben Admin 5 monorepo |
| 技术栈 | Vue 3、Vite、Vben Admin 5、ant-design-vue、Vxe Table、ECharts |
| 包管理 | `pnpm workspace` |
| 平台组件入口 | `apps/web-antd/src/components/platform` |
| Vxe 适配层 | `apps/web-antd/src/adapter/vxe-table.ts` |
| 全局样式入口 | `packages/platform-styles/src/antd/index.css` |

注意：

1. 不能只解压 `apps/web-antd` 单独开发。
2. 页面依赖根目录 `pnpm-workspace.yaml`、`pnpm-lock.yaml`、`packages/**`、`internal/**` 等 workspace 资源。
3. 当前源码快照默认保留已完成静态页面与平台组件体系，便于后续继续联调。

## 4. 本地启动

建议 Node.js 版本遵循仓库约束：

```text
^20.19.0 || ^22.18.0 || ^24.0.0
```

标准启动方式：

```bash
pnpm install
pnpm dev:antd
```

如果依赖已就绪但本机 `pnpm` 暂不可用，也可以在 `apps/web-antd` 下临时使用本地 Vite 二进制：

```bash
cd apps/web-antd
../../node_modules/.bin/vite --mode development
```

开发访问地址通常为：

```text
http://127.0.0.1:5173/
```

常用验证页：

```text
http://127.0.0.1:5173/workbench/index
http://127.0.0.1:5173/project/information
http://127.0.0.1:5173/personnel/overview
```

## 5. 离线预览说明

根目录 `index.html` 是静态入口页，已改为双击后直接跳到登录页。

离线预览使用方式：

1. 双击根目录 `index.html`。
2. 页面会直接进入 `preview/index.html#/auth/login`。
3. 登录、用户信息、菜单和权限均走前端 Mock，可进入系统查看已完成页面。
4. 如果本地浏览器对 `file://` 模块资源有额外拦截，再双击根目录 `打开预览.command`。
5. `打开预览.command` 会自动避开 `8002 / 1994 / 5173`，并从 `8012-8020 / 5174` 中选择可用端口。

说明：

1. 根目录 `index.html` 不再停留在说明页。
2. `preview/` 已使用 `VITE_BASE=./`、hash 路由和 Mock 模式构建，适合作为交付预览快照。
3. 真正的前后端联调、登录守卫、路由流转和 Mock/真实接口切换，仍以 `source/` 启动后的开发模式为准。

## 6. Mock、登录与接口切换边界

当前静态开发阶段默认使用 Mock。

| 项 | 位置 | 当前状态 |
| --- | --- | --- |
| 登录 / 菜单 Mock | `apps/web-antd/src/mock/index.ts` | 已启用 |
| 开发环境开关 | `apps/web-antd/.env.development` | `VITE_USE_MOCK=true` |
| 交付预览环境 | `apps/web-antd/.env.handoff` | `VITE_BASE=./`、`VITE_ROUTER_HISTORY=hash`、`VITE_USE_MOCK=true` |
| 开发接口前缀 | `apps/web-antd/.env.development` | `VITE_GLOB_API_URL=/api` |
| 生产接口前缀 | `apps/web-antd/.env.production` | `VITE_GLOB_API_URL=/prod-api` |
| 路由模式 | `apps/web-antd/src/router/index.ts` | 根据 `VITE_ROUTER_HISTORY` 切换 |

联调时建议按以下顺序切换：

1. 先保留 Vben 登录、用户信息、权限、菜单和动态路由主链路。
2. 再将 `VITE_USE_MOCK` 从 `true` 调整为 `false`。
3. 最后逐页把 `*-source.ts` 中的本地数据读取替换为 API 请求。

不建议：

1. 新起一套登录逻辑。
2. 绕开当前菜单和路由守卫体系。
3. 在页面里直接散写新的通用表格、弹窗、工具栏样式。

## 7. 页面总表

| 页面 | 路由 | 主要源码 | 数据源/Mock | 当前状态 |
| --- | --- | --- | --- | --- |
| 项目总览 | `/workbench/index` | `apps/web-antd/src/views/project/overview/index.vue` | `project-overview-source.ts` | 已完成 |
| 项目信息管理 | `/project/information` | `apps/web-antd/src/views/project/information/index.vue` | `project-information-source.ts` | 已完成 |
| 进度可视化跟踪 | `/project/progress` | `apps/web-antd/src/views/project/progress/index.vue` | `project-progress-source.ts` | 已完成 |
| 合同与付款管理 | `/project/contract` | `apps/web-antd/src/views/project/contract/index.vue` | `project-contract-source.ts` | 已完成 |
| 文档与台账管理 | `/project/document` | `apps/web-antd/src/views/project/document/index.vue` | `project-document-source.ts` | 已完成 |
| 中期评估与验收管理 | `/project/evaluation` | `apps/web-antd/src/views/project/evaluation/index.vue` | `project-evaluation-source.ts` | 已完成 |
| 人员总览 | `/personnel/overview` | `apps/web-antd/src/views/personnel/overview/index.vue` | `personnel-overview-source.ts` | 已完成 |
| 人员详情壳 | `/personnel/overview/detail` | `apps/web-antd/src/views/personnel/detail/index.vue` | 路由 query + 页面壳 | 半成品壳，正文待补 |
| 资质与准入管控 | `/personnel/qualification` | `apps/web-antd/src/views/personnel/qualification/index.vue` | `personnel-qualification-source.ts` | 已完成 |
| 变动与流失率统计 | `/personnel/turnover` | `apps/web-antd/src/views/personnel/turnover/index.vue` | `personnel-turnover-source.ts` | 已完成 |
| 工时与兼职管控 | `/personnel/worktime` | `apps/web-antd/src/views/personnel/worktime/index.vue` | `personnel-worktime-source.ts` | 已完成 |
| 施工管理 | `/battery/construction` | `apps/web-antd/src/views/battery/construction/index.vue` | `construction-source.ts` | 已完成 |
| 人员档案管理典型页 | `/platform/typical-page` | `apps/web-antd/src/views/platform/typical-page/index.vue` | `user-demo-source.ts` | 已完成 |

## 8. 页面级联调说明

### 8.1 项目总览

| 项 | 内容 |
| --- | --- |
| 路由 | `/workbench/index` |
| 页面源码 | `apps/web-antd/src/views/project/overview/index.vue` |
| 数据源 | `apps/web-antd/src/views/project/overview/project-overview-source.ts` |
| 核心组件 | `PlatformStatCard`、`PlatformTable`、`PlatformTableToolbar`、`PlatformModal`、`PlatformDrawer` |
| 主要交互 | 搜索、表头筛选、查看进度看板、新建、编辑、查看详情、归档、删除 |

字段映射建议：

| 页面字段 | 当前 source 字段 | 后端建议字段 |
| --- | --- | --- |
| 项目编号 | `code` | `projectCode` |
| 项目名称 | `name` | `projectName` |
| 项目类型 | `type` | `projectType` |
| 所属部门 | `department` | `departmentName` |
| 合同金额 | `amount` | `contractAmount` |
| 工期 | `durationDays` | `durationDays` |
| 负责人 | `manager` | `managerName` |
| 进度 | `progress` | `progressPercent` |
| 状态 | `status` | `projectStatus` |

查询参数建议：

| 当前查询 | 当前字段 | 后端建议 |
| --- | --- | --- |
| 关键字 | `keyword` | `keyword` |
| 状态 | `status` | `status` |
| 类型 | `type` | `type` |

待接接口建议：

| 用途 | 建议方法 | 建议路径 |
| --- | --- | --- |
| 列表 | `GET` | `/project/overview/list` |
| 统计卡 | `GET` | `/project/overview/stats` |
| 新增/编辑 | `POST` / `PUT` | `/project/overview` |
| 删除 | `DELETE` | `/project/overview/{id}` |
| 归档 | `POST` | `/project/overview/{id}/archive` |

### 8.2 项目信息管理

| 项 | 内容 |
| --- | --- |
| 路由 | `/project/information` |
| 页面源码 | `apps/web-antd/src/views/project/information/index.vue` |
| 数据源 | `apps/web-antd/src/views/project/information/project-information-source.ts` |
| 核心交互 | 列表筛选、表头筛选、新建、编辑、归档、删除 |
| 弹窗结构 | `基础信息 / 招采信息 / 进场信息` 三段式 `PlatformSegmented` |

主要表格字段：

| 页面字段 | source 字段 | 后端建议字段 |
| --- | --- | --- |
| 项目编号 | `code` | `projectCode` |
| 项目名称 | `name` | `projectName` |
| 项目类型 | `type` | `projectType` |
| 承包商 | `contractor` | `contractorName` |
| 合同金额 | `amount` | `contractAmount` |
| 招采方式 | `procurementMethod` | `procurementMethod` |
| 状态 | `status` | `status` |

表单字段建议：

| 页面字段 | source 字段 | 后端建议字段 |
| --- | --- | --- |
| 招标开标日期 | `bidOpenDate` | `bidOpenDate` |
| 招采结果 | `biddingResult` | `biddingResult` |
| 招采代理机构 | `procurementAgency` | `procurementAgency` |
| 单一来源备案编号 | `singleSourceFilingCode` | `singleSourceFilingCode` |
| 进场日期 | `entryDate` | `entryDate` |
| 计划退场日期 | `plannedExitDate` | `plannedExitDate` |
| 进场设备清单 | `equipmentList` | `equipmentList` |
| 进场人员清单 | `staffing` | `staffing` |

注意事项：

1. `equipmentList`、`staffing` 当前在前端以字符串序列化方式维护。
2. 联调时建议后端改为结构化数组返回，前端再在 `parseEquipmentRows / parseStaffingRows` 处适配。

### 8.3 进度可视化跟踪

| 项 | 内容 |
| --- | --- |
| 路由 | `/project/progress` |
| 页面源码 | `apps/web-antd/src/views/project/progress/index.vue` |
| 数据源 | `apps/web-antd/src/views/project/progress/project-progress-source.ts` |
| 视图模式 | 甘特图 / 看板 |
| 主要交互 | 视图切换、进度预警展示 |

字段映射建议：

| 页面字段 | source 字段 | 后端建议字段 |
| --- | --- | --- |
| 项目名称 | `name` | `projectName` |
| 承接单位/部门 | `department` | `departmentName` |
| 负责人 | `manager` | `managerName` |
| 开始月份 | `startMonth` | `startMonth` |
| 结束月份 | `endMonth` | `endMonth` |
| 进度 | `progress` | `progressPercent` |
| 状态 | `status` | `status` |
| 里程碑 | `milestones` | `milestones[]` |

待接接口建议：

| 用途 | 建议方法 | 建议路径 |
| --- | --- | --- |
| 甘特图数据 | `GET` | `/project/progress/gantt` |
| 看板数据 | `GET` | `/project/progress/board` |
| 预警列表 | `GET` | `/project/progress/warnings` |

### 8.4 合同与付款管理

| 项 | 内容 |
| --- | --- |
| 路由 | `/project/contract` |
| 页面源码 | `apps/web-antd/src/views/project/contract/index.vue` |
| 数据源 | `apps/web-antd/src/views/project/contract/project-contract-source.ts` |
| 页面结构 | 统计卡 + 合同卡片列表 + 付款节点进度条 |

字段映射建议：

| 页面字段 | source 字段 | 后端建议字段 |
| --- | --- | --- |
| 项目名称 | `name` | `projectName` |
| 合同金额 | `contractAmount` | `contractAmount` |
| 页面状态 | `status` | `contractStatus` |
| 付款节点数组 | `nodes` | `paymentNodes[]` |
| 节点名称 | `label` | `nodeName` |
| 节点比例 | `percent` | `paymentPercent` |
| 节点金额 | `amount` | `paymentAmount` |
| 节点状态 | `status` | `paymentStatus` |

### 8.5 文档与台账管理

| 项 | 内容 |
| --- | --- |
| 路由 | `/project/document` |
| 页面源码 | `apps/web-antd/src/views/project/document/index.vue` |
| 数据源 | `apps/web-antd/src/views/project/document/project-document-source.ts` |
| 主要交互 | 批量上传、下载、导出台账 |

字段映射建议：

| 页面字段 | source 字段 | 后端建议字段 |
| --- | --- | --- |
| 文件名 | `name` | `fileName` |
| 项目名称 | `projectName` | `projectName` |
| 项目编号 | `projectCode` | `projectCode` |
| 分类 | `category` | `category` |
| 文件类型 | `type` | `fileType` |
| 文件大小 | `size` | `fileSizeText` |
| 上传日期 | `date` | `uploadDate` |

待接接口建议：

| 用途 | 建议方法 | 建议路径 |
| --- | --- | --- |
| 列表 | `GET` | `/project/document/list` |
| 统计 | `GET` | `/project/document/stats` |
| 上传 | `POST` | `/project/document/upload` |
| 下载 | `GET` | `/project/document/{id}/download` |
| 导出台账 | `GET` | `/project/document/export` |

### 8.6 中期评估与验收管理

| 项 | 内容 |
| --- | --- |
| 路由 | `/project/evaluation` |
| 页面源码 | `apps/web-antd/src/views/project/evaluation/index.vue` |
| 数据源 | `apps/web-antd/src/views/project/evaluation/project-evaluation-source.ts` |
| 页面结构 | 左侧待评估任务卡，右侧评估记录表格 |
| 主要交互 | 搜索、筛选、发起评估、生成记录 |

字段映射建议：

| 页面字段 | source 字段 | 后端建议字段 |
| --- | --- | --- |
| 待评估项目名称 | `name` | `projectName` |
| 负责部门 | `department` | `departmentName` |
| 负责人 | `manager` | `managerName` |
| 截止日期 | `dueDate` | `dueDate` |
| 当前进度 | `progress` | `progressPercent` |
| 评估阶段 | `stage` | `evaluationStage` |
| 评估状态 | `status` | `evaluationStatus` |
| 评估组 | `evaluator` | `evaluator` |
| 得分 | `score` | `score` |
| 结果 | `result` | `result` |

评估提交参数建议：

| 提交项 | source 字段 | 后端建议字段 |
| --- | --- | --- |
| 项目 ID | `projectId` | `projectId` |
| 评估组 | `evaluator` | `evaluator` |
| 评语 | `remark` | `remark` |
| 得分 | `score` | `score` |

### 8.7 人员总览

| 项 | 内容 |
| --- | --- |
| 路由 | `/personnel/overview` |
| 页面源码 | `apps/web-antd/src/views/personnel/overview/index.vue` |
| 数据源 | `apps/web-antd/src/views/personnel/overview/personnel-overview-source.ts` |
| 主要交互 | 搜索、状态筛选、资质筛选、新增、编辑、删除、查看详情壳 |

字段映射建议：

| 页面字段 | source 字段 | 后端建议字段 |
| --- | --- | --- |
| 编号 | `code` | `personCode` |
| 姓名 | `name` | `personName` |
| 性别 | `gender` | `gender` |
| 年龄 | `age` | `age` |
| 承包商 | `contractor` | `contractorName` |
| 所属项目 | `project` | `projectName` |
| 岗位 | `position` | `positionName` |
| 入职日期 | `startDate` | `entryDate` |
| 资质名称 | `qualificationName` | `qualificationName` |
| 资质到期日 | `qualificationExpireDate` | `qualificationExpireDate` |
| 资质状态 | `qualificationStatus` | `qualificationStatus` |
| 在岗状态 | `status` | `employmentStatus` |

表单字段当前已可直接复用为后端提交 DTO。

### 8.8 人员详情壳

| 项 | 内容 |
| --- | --- |
| 路由 | `/personnel/overview/detail` |
| 页面源码 | `apps/web-antd/src/views/personnel/detail/index.vue` |
| 当前状态 | 仅返回头部和空白正文容器，供后续继续扩展 |

说明：

1. 当前该页不能视为完整交付页面。
2. 仅作为人员总览“查看”链路的路由占位。

### 8.9 资质与准入管控

| 项 | 内容 |
| --- | --- |
| 路由 | `/personnel/qualification` |
| 页面源码 | `apps/web-antd/src/views/personnel/qualification/index.vue` |
| 数据源 | `apps/web-antd/src/views/personnel/qualification/personnel-qualification-source.ts` |
| 主要交互 | 查看预警、发送提醒、查看准入规则 |

字段映射建议：

| 页面字段 | source 字段 | 后端建议字段 |
| --- | --- | --- |
| 预警人员 | `title` | `personName` |
| 证书/到期信息 | `meta` | `certificateName` + `expireDate` |
| 提醒状态 | `description` / `actionText` | `remindedAt` / `remindStatus` |
| 准入规则标题 | `title` | `ruleName` |
| 准入规则说明 | `description` | `ruleDescription` |
| 启用状态 | `statusText` | `enabled` |

### 8.10 变动与流失率统计

| 项 | 内容 |
| --- | --- |
| 路由 | `/personnel/turnover` |
| 页面源码 | `apps/web-antd/src/views/personnel/turnover/index.vue` |
| 数据源 | `apps/web-antd/src/views/personnel/turnover/personnel-turnover-source.ts` |
| 页面结构 | 统计卡 + 承包商流失率 ECharts 排名图 |

字段映射建议：

| 页面字段 | source 字段 | 后端建议字段 |
| --- | --- | --- |
| 承包商名称 | `contractor` | `contractorName` |
| 流失率 | `lossRate` / `rate` | `turnoverRate` |
| 离职人数 | `lostCount` / `resigned` | `resignedCount` |
| 总人数 | `totalCount` / `total` | `totalCount` |

### 8.11 工时与兼职管控

| 项 | 内容 |
| --- | --- |
| 路由 | `/personnel/worktime` |
| 页面源码 | `apps/web-antd/src/views/personnel/worktime/index.vue` |
| 数据源 | `apps/web-antd/src/views/personnel/worktime/personnel-worktime-source.ts` |
| 页面结构 | 统计卡 + 超工时预警卡片 |
| 主要交互 | 查看阈值、发送整改通知 |

字段映射建议：

| 页面字段 | source 字段 | 后端建议字段 |
| --- | --- | --- |
| 人员姓名 | `name` | `personName` |
| 承包商 | `contractor` | `contractorName` |
| 岗位 | `position` | `positionName` |
| 所属项目 | `project` | `projectName` |
| 本月工时 | `monthlyHours` | `monthlyHours` |
| 超出时长 | `overHours` | `overHours` |
| 预警等级 | `level` | `alertLevel` |
| 已通知时间 | `notifiedAt` | `notifiedAt` |

### 8.12 施工管理

| 项 | 内容 |
| --- | --- |
| 路由 | `/battery/construction` |
| 页面源码 | `apps/web-antd/src/views/battery/construction/index.vue` |
| 数据源 | `apps/web-antd/src/views/battery/construction/construction-source.ts` |
| 页面结构 | 查询面板 + 列表表格 + 查看弹窗 + 审批进度侧栏 |
| 主要交互 | 查询、重置、查看、打印占位、拖拽审批进度宽度 |

字段映射建议：

| 页面字段 | source 字段 | 后端建议字段 |
| --- | --- | --- |
| 许可证编号 | `permitCode` | `permitCode` |
| 业务类型 | `businessType` | `businessType` |
| 项目编号 | `projectCode` | `projectCode` |
| 项目名称 | `projectName` | `projectName` |
| 项目年度 | `projectYear` | `projectYear` |
| 计划开工时间 | `plannedStartDate` | `plannedStartDate` |
| 计划结束时间 | `plannedEndDate` | `plannedEndDate` |
| 提交人 | `submitter` | `submitterName` |
| 提交时间 | `submittedAt` | `submittedAt` |
| 状态 | `status` | `status` |
| 审批进度 | `approvalItems[]` | `approvalItems[]` |

查询参数建议：

| 当前查询 | source 字段 | 后端建议字段 |
| --- | --- | --- |
| 项目名称 | `projectName` | `projectName` |
| 提交人 | `submitter` | `submitterName` |
| 业务类型 | `businessType` | `businessType` |
| 项目年度 | `projectYear` | `projectYear` |
| 状态 | `status` | `status` |
| 提交时间区间 | `submittedDateRange` | `submittedStartDate` / `submittedEndDate` |

### 8.13 人员档案管理典型页

| 项 | 内容 |
| --- | --- |
| 路由 | `/platform/typical-page` |
| 页面源码 | `apps/web-antd/src/views/platform/typical-page/index.vue` |
| 数据源 | `apps/web-antd/src/views/platform/typical-page/user-demo-source.ts` |
| 页面结构 | 搜索 + 卡片列表 + 详情抽屉 + 新增人员弹窗 |
| 主要交互 | 搜索、查看详情、新增人员 |

字段映射建议：

| 页面字段 | source 字段 | 后端建议字段 |
| --- | --- | --- |
| 档案编号 | `archiveNo` | `archiveNo` |
| 姓名 | `name` | `personName` |
| 承包商 | `contractor` | `contractorName` |
| 所属项目 | `project` | `projectName` |
| 岗位 | `position` | `positionName` |
| 身体状况 | `healthStatus` | `healthStatus` |
| 资质名称 | `qualificationName` | `qualificationName` |
| 资质到期 | `qualificationExpireDate` | `qualificationExpireDate` |
| 黑名单状态 | `blacklistStatus` | `blacklistStatus` |
| 月工时 | `monthlyHours` | `monthlyHours` |

## 9. 浏览器自检结果

自检时间：`2026-05-12`

自检环境：

| 项 | 结果 |
| --- | --- |
| 本地服务 | `http://127.0.0.1:5173` 可达 |
| 检查方式 | `curl -I` + 隔离 Chrome headless 截图 |
| 登录链路 | 已通过隔离浏览器从 `/auth/login` 进入系统 |

已完成截图与标题核对的页面：

| 页面 | 路由 | 标题 | 截图 |
| --- | --- | --- | --- |
| 项目总览 | `/workbench/index` | `项目总览 - 委外项目综合管理平台` | `assets/screenshots/workbench-index.png` |
| 项目信息管理 | `/project/information` | `项目信息管理 - 委外项目综合管理平台` | `assets/screenshots/project-information.png` |
| 进度可视化跟踪 | `/project/progress` | `进度可视化跟踪 - 委外项目综合管理平台` | `assets/screenshots/project-progress.png` |
| 合同与付款管理 | `/project/contract` | `合同与付款管理 - 委外项目综合管理平台` | `assets/screenshots/project-contract.png` |
| 文档与台账管理 | `/project/document` | `文档与台账管理 - 委外项目综合管理平台` | `assets/screenshots/project-document.png` |
| 中期评估与验收管理 | `/project/evaluation` | `中期评估与验收 - 委外项目综合管理平台` | `assets/screenshots/project-evaluation.png` |
| 人员总览 | `/personnel/overview` | `人员总览 - 委外项目综合管理平台` | `assets/screenshots/personnel-overview.png` |
| 资质与准入管控 | `/personnel/qualification` | `资质与准入管控 - 委外项目综合管理平台` | `assets/screenshots/personnel-qualification.png` |
| 变动与流失率统计 | `/personnel/turnover` | `变动与流失率统计 - 委外项目综合管理平台` | `assets/screenshots/personnel-turnover.png` |
| 工时与兼职管控 | `/personnel/worktime` | `工时与兼职管控 - 委外项目综合管理平台` | `assets/screenshots/personnel-worktime.png` |
| 施工管理 | `/battery/construction` | `施工管理 - 委外项目综合管理平台` | `assets/screenshots/battery-construction.png` |
| 人员档案管理典型页 | `/platform/typical-page` | `人员档案管理 - 委外项目综合管理平台` | `assets/screenshots/platform-typical-page.png` |

## 10. 后续联调建议

1. 先联调登录、用户信息、权限和菜单接口，再联调业务页。
2. 业务页优先替换 `*-source.ts` 内的数据获取函数，不要先拆页面结构。
3. 平台层能力继续优先复用 `PlatformTable`、`PlatformTableToolbar`、`PlatformModal`、`PlatformDrawer`、`PlatformStatCard` 等组件。
4. 如需新增字段，优先在页面 source 类型定义中对齐，再补后端 DTO 映射。
5. `/personnel/overview/detail` 目前仍是壳页面，联调时不要误认为已经具备完整业务详情。
