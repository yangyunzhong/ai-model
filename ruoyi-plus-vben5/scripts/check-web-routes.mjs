import { existsSync, readFileSync, readdirSync, statSync } from 'node:fs';
import { join, relative } from 'node:path';

import ts from 'typescript';

const root = new URL('..', import.meta.url).pathname;
const routeRoot = join(root, 'apps/web-antd/src/router/routes/modules');
const viewRoot = join(root, 'apps/web-antd/src/views');
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
      walk(full, files);
    } else if (full.endsWith('.ts')) {
      files.push(full);
    }
  }
  return files;
}

function lineFor(content, index) {
  return content.slice(0, index).split('\n').length;
}

function routeFileLabel(file) {
  return relative(root, file);
}

const seenNames = new Map();
const seenPaths = new Map();

for (const file of walk(routeRoot)) {
  const source = read(file);
  const label = routeFileLabel(file);
  const result = ts.transpileModule(source, {
    compilerOptions: {
      module: ts.ModuleKind.ESNext,
      target: ts.ScriptTarget.ESNext,
    },
    fileName: file,
    reportDiagnostics: true,
  });

  for (const diagnostic of result.diagnostics ?? []) {
    if (!diagnostic.file || diagnostic.start === undefined) {
      errors.push(`${label}: ${ts.flattenDiagnosticMessageText(diagnostic.messageText, '\n')}`);
      continue;
    }
    const position = diagnostic.file.getLineAndCharacterOfPosition(diagnostic.start);
    errors.push(
      `${label}:${position.line + 1}:${position.character + 1} ${ts.flattenDiagnosticMessageText(
        diagnostic.messageText,
        '\n',
      )}`,
    );
  }

  if (!/export default routes/.test(source)) {
    errors.push(`${label}: route module must export default routes.`);
  }

  for (const match of source.matchAll(/name:\s*['"`]([^'"`]+)['"`]/g)) {
    const name = match[1];
    if (seenNames.has(name)) {
      errors.push(`${label}:${lineFor(source, match.index)} duplicate route name "${name}" also in ${seenNames.get(name)}.`);
    } else {
      seenNames.set(name, label);
    }
  }

  for (const match of source.matchAll(/path:\s*['"`]([^'"`]+)['"`]/g)) {
    const path = match[1];
    if (seenPaths.has(path)) {
      warnings.push(`${label}:${lineFor(source, match.index)} duplicate route path "${path}" also in ${seenPaths.get(path)}.`);
    } else {
      seenPaths.set(path, label);
    }
  }

  for (const match of source.matchAll(/import\(['"`]#\/views\/([^'"`]+\.vue)['"`]\)/g)) {
    const viewPath = join(viewRoot, match[1]);
    if (!existsSync(viewPath)) {
      errors.push(`${label}:${lineFor(source, match.index)} missing route component: #/views/${match[1]}`);
    }
  }

  const routeBlocks = [...source.matchAll(/\{[\s\S]*?name:\s*['"`][^'"`]+['"`][\s\S]*?\}/g)];
  for (const block of routeBlocks) {
    const text = block[0];
    const line = lineFor(source, block.index);
    if (!/meta:\s*\{/.test(text)) {
      warnings.push(`${label}:${line} route should include meta.`);
    }
    if (!/title:\s*[^,\n}]+/.test(text)) {
      warnings.push(`${label}:${line} route should include meta.title.`);
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
  console.log(`Route check completed with ${warnings.length} warning(s).`);
}
