import { Marked } from 'marked';
import { codeToHtml } from 'shiki';

const marked = new Marked({ async: true });

const highlightCache = new Map<string, string>();

async function highlightCode(
  code: string,
  lang: string,
): Promise<string> {
  const cacheKey = `${lang}::${code}`;
  const cached = highlightCache.get(cacheKey);
  if (cached) {
    return cached;
  }

  try {
    const html = await codeToHtml(code, {
      lang: lang || 'text',
      theme: 'github-light',
    });
    highlightCache.set(cacheKey, html);
    return html;
  } catch {
    const escaped = code
      .replaceAll('&', '&amp;')
      .replaceAll('<', '&lt;')
      .replaceAll('>', '&gt;');
    const result = `<pre><code class="language-${lang}">${escaped}</code></pre>`;
    highlightCache.set(cacheKey, result);
    return result;
  }
}

export async function renderMarkdown(markdown: string): Promise<string> {
  if (!markdown.trim()) {
    return '';
  }

  const html = await marked.parse(markdown);

  if (typeof html !== 'string') {
    return '';
  }

  const codeBlockRegex =
    /<pre><code class="language-(\w+)">([\s\S]*?)<\/code><\/pre>/g;
  const codeBlocks: { lang: string; code: string; placeholder: string }[] = [];
  let processedHtml = html.replace(
    codeBlockRegex,
    (_match, lang: string, code: string) => {
      const placeholder = `<!--CODE_BLOCK_${codeBlocks.length}-->`;
      const decoded = code
        .replaceAll('&amp;', '&')
        .replaceAll('&lt;', '<')
        .replaceAll('&gt;', '>')
        .replaceAll('&quot;', '"')
        .replaceAll('&#39;', "'");
      codeBlocks.push({ lang, code: decoded, placeholder });
      return placeholder;
    },
  );

  const highlightedBlocks = await Promise.all(
    codeBlocks.map((block) => highlightCode(block.code, block.lang)),
  );

  for (let i = 0; i < codeBlocks.length; i++) {
    processedHtml = processedHtml.replace(
      codeBlocks[i]!.placeholder,
      highlightedBlocks[i]!,
    );
  }

  return processedHtml;
}
