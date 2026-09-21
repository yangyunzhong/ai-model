# @st/platform-styles

`@st/platform-styles` is the future home for ST-design platform tokens, theme variables, Ant Design Vue overrides, and global platform CSS.

Current status:

1. Package boundary is reserved.
2. `@st/platform-styles/tokens` owns ST-design platform CSS variables such as brand colors, spacing, radius, table dimensions, and component sizing.
3. `@st/platform-styles/antd` owns the current Ant Design Vue global overrides and platform CSS entry, imports the token entry first, and keeps component/global overrides split by responsibility under `src/antd/*.css`.
4. `@vben/styles/antd` now only bridges to `packages/platform-styles/src/antd/index.css` for compatibility.
5. `@st/platform-styles/vxe-table` owns stable Vxe table variables and global overrides, and imports the token entry first; the Vben Vxe plugin style file only imports this platform entry.
6. `apps/web-antd` already imports `@st/platform-styles/antd`; continue moving stable platform tokens and overrides into this package in small verified steps.

Migration order:

1. Keep stable platform tokens in `src/tokens/index.css`.
2. Keep Ant Design Vue global overrides under `src/antd/*.css`; `src/antd/index.css` only imports tokens and ordered override files.
3. Keep Vxe table variables and Vxe-only global overrides in `src/vxe-table/index.css`.
4. Keep app-specific layout CSS in `apps/web-antd`.
