import { existsSync, readFileSync, readdirSync, statSync } from 'node:fs';
import { extname, join, relative } from 'node:path';

const root = new URL('..', import.meta.url).pathname;
const manifestPath = join(root, 'docs/ai-kit/components-manifest.json');
const demoRegistryPath = join(root, 'apps/demokit/src/registry.ts');
const platformUiRoot = join(root, 'packages/platform-ui/src');
const tokenPath = join(root, 'packages/platform-styles/src/tokens/index.css');
const viewRoots = [
  join(root, 'apps/web-antd/src/views'),
  join(root, 'apps/demokit/src'),
];

const errors = [];
const warnings = [];

function read(path) {
  return readFileSync(path, 'utf8');
}

function walk(dir, files = []) {
  if (!existsSync(dir)) return files;
  for (const entry of readdirSync(dir)) {
    const full = join(dir, entry);
    const stat = statSync(full);
    if (stat.isDirectory()) {
      if (entry === 'dist' || entry === 'node_modules') continue;
      walk(full, files);
    } else if (['.vue', '.ts', '.tsx'].includes(extname(full))) {
      files.push(full);
    }
  }
  return files;
}

function lineFor(content, index) {
  return content.slice(0, index).split('\n').length;
}

function report(kind, file, line, message) {
  const target = `${relative(root, file)}:${line}`;
  const item = `${target} ${message}`;
  if (kind === 'error') errors.push(item);
  else warnings.push(item);
}

if (!existsSync(manifestPath)) {
  errors.push('docs/ai-kit/components-manifest.json is missing.');
} else {
  const manifest = JSON.parse(read(manifestPath));
  for (const component of manifest.components ?? []) {
    for (const key of ['sourcePath', 'demoPath']) {
      const target = join(root, component[key]);
      if (!existsSync(target)) {
        errors.push(`Manifest component ${component.name} has missing ${key}: ${component[key]}`);
      }
    }
  }
}

if (!existsSync(demoRegistryPath)) {
  errors.push('apps/demokit/src/registry.ts is missing.');
} else {
  const registrySource = read(demoRegistryPath);
  const registeredComponents = new Set(
    [...registrySource.matchAll(/name: '([A-Z][A-Za-z0-9]+)'/g)].map(
      (match) => match[1],
    ),
  );
  const exportedComponents = new Set();

  for (const file of walk(platformUiRoot).filter((item) => item.endsWith('index.ts'))) {
    const source = read(file);
    for (const match of source.matchAll(/export \{ default as (Platform[A-Za-z0-9]+) \}/g)) {
      exportedComponents.add(match[1]);
    }
  }

  for (const component of exportedComponents) {
    if (!registeredComponents.has(component)) {
      warnings.push(
        `Platform component ${component} is exported but not registered in apps/demokit/src/registry.ts.`,
      );
    }
  }
}

if (!existsSync(tokenPath)) {
  errors.push('packages/platform-styles/src/tokens/index.css is missing.');
}

const files = viewRoots.flatMap((dir) => walk(dir));
const nativePatterns = [
  { pattern: /<a-modal\b/g, replacement: 'PlatformModal' },
  { pattern: /<a-drawer\b/g, replacement: 'PlatformDrawer' },
  { pattern: /<a-table\b/g, replacement: 'PlatformTable or useVbenVxeGrid' },
];
const spacingPattern = /\b(?:gap|padding|margin|padding-inline|padding-block|margin-top|margin-bottom|row-gap|column-gap)\s*:\s*(?:12|16|18|20|24|32)px\b/g;
const platformImportCompatPattern = /from\s+['"][^'"]*components\/platform[^'"]*['"]/g;
const handwrittenShellPatterns = [
  {
    pattern: /class\s*=\s*["'][^"']*(?:search|filter)[-_](?:panel|row|form|item|actions|bar)[^"']*["']/g,
    message: 'filter/search shell looks handwritten; use PlatformQueryPanel or PlatformSearchForm when the platform capability exists.',
  },
  {
    pattern: /class\s*=\s*["'][^"']*(?:table|list)[-_](?:toolbar|actions|operation|operation-bar|wrapper|panel)[^"']*["']/g,
    message: 'table toolbar/panel shell looks handwritten; use PlatformTableToolbar inside platform-surface or PlatformSection.',
  },
  {
    pattern: /class\s*=\s*["'][^"']*(?:pagination|pager)[^"']*["']/g,
    message: 'pagination shell looks handwritten; let PlatformTable or the confirmed Vben/Vxe table shell own pagination spacing.',
  },
];
const paginationCssPattern = /\.(?:ant-)?(?:table-)?pagination\b|\.pager\b/g;

for (const file of files) {
  const content = read(file);
  const relativePath = relative(root, file);
  const isDemoKit = relativePath.startsWith('apps/demokit/');
  const isWebView = relativePath.startsWith('apps/web-antd/src/views/');

  for (const { pattern, replacement } of nativePatterns) {
    for (const match of content.matchAll(pattern)) {
      report('warning', file, lineFor(content, match.index), `prefer ${replacement} before using ${match[0]}.`);
    }
  }

  if ((isWebView || isDemoKit) && /<style[^>]*scoped/.test(content)) {
    for (const match of content.matchAll(spacingPattern)) {
      report('warning', file, lineFor(content, match.index), `standard spacing should use platform tokens when possible: ${match[0]}`);
    }
  }

  if (isDemoKit && /<template>[\s\S]*<Platform/.test(content) && !content.includes("from '@st/platform-ui'")) {
    report('warning', file, 1, 'DemoKit platform examples should import components from @st/platform-ui.');
  }

  if (isWebView) {
    if (/<Platform[A-Z][A-Za-z0-9]*/.test(content)) {
      for (const match of content.matchAll(platformImportCompatPattern)) {
        report('warning', file, lineFor(content, match.index), 'new pages should import platform components directly from @st/platform-ui; apps/web-antd/src/components/platform is only a compatibility exit.');
      }
    }

    for (const { pattern, message } of handwrittenShellPatterns) {
      for (const match of content.matchAll(pattern)) {
        report('warning', file, lineFor(content, match.index), message);
      }
    }

    const hasTableShell = /<PlatformTable\b|useVbenVxeGrid\s*\(/.test(content);
    const hasNativeTable = /<a-table\b/.test(content);
    const hasPlatformTable = /<PlatformTable\b/.test(content);
    const hasPlatformToolbar = /<PlatformTableToolbar\b/.test(content);
    const hasPlatformSurface = /class\s*=\s*["'][^"']*platform-surface[^"']*["']|<PlatformSection\b/.test(content);
    const hasHandwrittenFilter = /class\s*=\s*["'][^"']*(?:search|filter)[-_](?:panel|row|form|actions|bar)[^"']*["']/.test(content);
    const hasPlatformFilter = /<PlatformQueryPanel\b|<PlatformSearchForm\b/.test(content);

    if ((hasTableShell || hasNativeTable) && hasHandwrittenFilter && !hasPlatformFilter) {
      report('warning', file, 1, 'list page has table content plus a handwritten filter shell; replace it with PlatformQueryPanel/PlatformSearchForm before adjusting fields.');
    }

    if (hasPlatformTable && !hasPlatformToolbar) {
      report('warning', file, 1, 'PlatformTable is used without PlatformTableToolbar; standard list pages should keep the table toolbar shell unless there is a documented exception.');
    }

    if (hasPlatformToolbar && !hasPlatformSurface) {
      report('warning', file, 1, 'PlatformTableToolbar should live inside one platform-surface or PlatformSection with the table so width and horizontal padding stay consistent.');
    }

    if (/<style[^>]*scoped/.test(content)) {
      for (const match of content.matchAll(paginationCssPattern)) {
        report('warning', file, lineFor(content, match.index), 'avoid page-level pagination CSS; pagination spacing should be owned by PlatformTable or the confirmed Vben/Vxe table shell.');
      }
    }
  }
}

for (const item of warnings) {
  console.warn(`WARN ${item}`);
}

for (const item of errors) {
  console.error(`ERROR ${item}`);
}

if (errors.length > 0) {
  process.exitCode = 1;
} else {
  console.log(`AI Kit check completed with ${warnings.length} warning(s).`);
}
