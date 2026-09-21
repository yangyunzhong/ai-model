# @st/platform-adapter

`@st/platform-adapter` is the future home for ST-design adapters around Vben, Vxe, ECharts, Upload, and similar integration layers.

Current status:

1. Package boundary is reserved.
2. `@st/platform-adapter/vxe-table` re-exports the base Vben Vxe plugin and now owns stable platform Vxe rules.
3. Platform Vxe defaults currently include the seq index column, toolbar defaults, header/body row heights, refresh behavior, checkbox state helper, and sort parameter helper.
4. `@st/platform-adapter/echarts` re-exports the base Vben ECharts plugin so platform and app chart consumers use the ST adapter boundary first.
5. `@st/platform-adapter/upload` owns upload-only types, default accept lists, the default non-image file preview helper, upload info/feedback abstractions, and cropper upload contract types.
6. App-specific initialization still stays in `apps/web-antd/src/adapter/vxe-table.ts`, including `useVbenForm` wiring and custom `CellImage` / `CellLink` renderers.
7. App-specific Upload components, APIs, OSS metadata lookups, cropper UI flows, messages, and modals stay in `apps/web-antd` until those dependencies are explicitly decoupled.
8. Do not move app-specific adapter code here until the public API and affected pages are clear.

Migration order:

1. Continue extracting stable adapter types and factory functions.
2. Move Vxe style and theme variables only after confirming the current effective layer.
3. Continue ECharts migration from no-behavior re-export toward stable chart defaults only after page-level verification.
4. Keep business API calls, route state, page mocks, and page-only renderers out of this package.
5. Continue Upload migration by extracting stable contracts first; do not move app-side Upload or cropper UI until business API, OSS metadata, message, and modal dependencies are separated from the platform boundary.
