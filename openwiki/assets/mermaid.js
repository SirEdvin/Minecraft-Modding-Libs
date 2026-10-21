// Keep the renderer pinned; diagrams also remain readable as fenced source.
(async () => {
  const blocks = document.querySelectorAll('pre > code.language-mermaid');
  if (!blocks.length) return;
  try {
    const { default: mermaid } = await import('https://cdn.jsdelivr.net/npm/mermaid@12.2.0/dist/mermaid.esm.min.mjs');
    mermaid.initialize({ startOnLoad: false, securityLevel: 'strict', theme: 'neutral' });
    for (const [index, block] of Array.from(blocks).entries()) {
      // Render before replacing the fallback, so parse/network failures retain text.
      const { svg } = await mermaid.render(`wiki-diagram-${index}`, block.textContent);
      const figure = document.createElement('div');
      figure.className = 'mermaid-diagram';
      figure.tabIndex = 0;
      figure.setAttribute('role', 'region');
      figure.setAttribute('aria-label', 'Architecture diagram; scroll horizontally if needed');
      figure.innerHTML = svg;
      // Mermaid emits width=100%. Use its natural viewBox width so small
      // charts do not balloon on desktop or shrink to unreadable phone text.
      const drawing = figure.querySelector('svg');
      drawing.style.width = `${drawing.viewBox.baseVal.width}px`;
      drawing.style.height = 'auto';
      block.parentElement.replaceWith(figure);
    }
    document.documentElement.dataset.mermaid = 'ready';
  } catch (error) {
    document.documentElement.dataset.mermaid = 'error';
    console.error('OpenWiki Mermaid rendering failed; fenced source retained.', error);
  }
})();
