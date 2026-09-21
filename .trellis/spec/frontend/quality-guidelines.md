# 前端质量规范

## 必须遵守

- 只使用 pnpm 管理依赖。
- 新页面使用 `<script setup lang="ts">`。
- 接口统一通过 `alovaInstance` 封装。
- CRUD 页面优先复用 `useVbenVxeGrid`、`useVbenForm`、`useVbenModal`。
- 权限码与后端保持一致。
- 复杂页面先找相似页面和现有组件，不直接重写一套。

## 禁止模式

- 禁止修改 `node_modules`。
- 禁止引入新的 UI 组件库。
- 禁止在页面组件里散落接口 URL。
- 禁止直接绕过路由/菜单/权限体系做静态导航。
- 禁止把 mock 逻辑伪装成真实接口已完成。
- 禁止未验证就声明构建、类型检查或页面效果通过。

## 验证要求

- 文档/规范改动：运行 `git diff --check`。
- 前端代码改动：优先运行 `pnpm lint`、`pnpm build:antd` 或目标包已有脚本；如耗时或环境阻塞，说明原因。
- UI 改动：需要浏览器验证；涉及 3D/canvas/复杂交互时需要截图或像素检查。
- 接口联调改动：至少核对请求路径、方法、参数、返回类型、权限码。

## 常见问题

### Vite 8 / Rolldown 预构建缺失 Vue init binding

- 现象：mock/dev 启动后浏览器报 `init_runtime_dom_esm_bundler is not defined`。
- 先检查：用浏览器 `pageerror` 或源码链接定位实际报错的 `.vite/deps/*.js`，再确认该文件是否调用了 `init_*` 但顶部没有对应 import。
- 修复：优先在应用级 `vite.optimizeDeps.exclude` 排除坏的 ESM 依赖；如果排除上层包后暴露 CJS 子依赖 default export 问题，再把具体 CJS 子依赖加入 `optimizeDeps.include`。
- 不要只凭猜测改全局 `minify`，除非已确认坏产物来自压缩阶段且复验通过。

## 评审清单

- 是否放在 `apps/web-antd` 正确目录。
- API 是否有 `enum Api` 和明确泛型。
- 页面是否复用 Vben/Vxe/Form/Modal 体系。
- 权限、字典、租户、加密接口是否按既有方式处理。
- 是否引入重复状态或重复 schema。
