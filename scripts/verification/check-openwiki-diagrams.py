"""Audit wiki links/Claims and render every Mermaid page in built MkDocs.

Requires the same MkDocs environment plus playwright==1.56.0 and its Chromium.
Usage: python scripts/verification/check-openwiki-diagrams.py --site SITE --output OUT
SITE is a complete Pages artifact containing openwiki/ (build there first).
"""

import argparse
import functools
import http.server
import json
import re
import threading
from html.parser import HTMLParser
from pathlib import Path
from urllib.parse import unquote, urlsplit

from playwright.sync_api import sync_playwright
import yaml


ROOT = Path(__file__).resolve().parents[2]


class PageLinks(HTMLParser):
    def __init__(self):
        super().__init__()
        self.links = []
        self.ids = set()

    def handle_starttag(self, tag, attrs):
        attrs = dict(attrs)
        if "id" in attrs:
            self.ids.add(attrs["id"])
        if tag == "a" and "href" in attrs:
            self.links.append(attrs["href"])


def built_link_audit(site):
    config = yaml.safe_load((ROOT / "mkdocs.yml").read_text())
    site_prefix = str(Path(urlsplit(config["site_url"]).path.rstrip("/")).parent)
    pages = {}
    for path in (site / "openwiki").rglob("*.html"):
        parsed = PageLinks()
        parsed.feed(path.read_text())
        pages[path.resolve()] = parsed
    count = 0
    for path, parsed in pages.items():
        for target in parsed.links:
            url = urlsplit(target)
            if url.scheme or url.netloc:
                continue
            if url.path.startswith("/"):
                local_path = url.path.removeprefix(site_prefix + "/")
                destination = (site / unquote(local_path)).resolve()
            else:
                destination = (path.parent / unquote(url.path)).resolve() if url.path else path
            if destination.is_dir():
                destination /= "index.html"
            assert destination.exists(), (path, target)
            if url.fragment and destination in pages:
                assert unquote(url.fragment) in pages[destination].ids, (path, target)
            count += 1
    return count


def static_audit():
    manifest = json.loads((ROOT / "openwiki/.page-manifest.json").read_text())["pages"]
    errors = []
    claim_count = 0
    evidence_count = 0
    diagrams = {}
    for path in sorted((ROOT / "openwiki").rglob("*.md")):
        text = path.read_text()
        for target in re.findall(r"\]\(([^\s)]+)\)", text):
            url = urlsplit(target)
            if url.scheme or url.netloc or not url.path:
                continue
            destination = (path.parent / unquote(url.path)).resolve()
            if not destination.exists():
                errors.append(f"Missing link: {path.relative_to(ROOT)} -> {target}")
            span = re.fullmatch(r"L(\d+)(?:-L(\d+))?", url.fragment)
            if span and destination.is_file():
                start, end = int(span[1]), int(span[2] or span[1])
                if not 1 <= start <= end <= len(destination.read_text().splitlines()):
                    errors.append(f"Out-of-bounds link: {target}")
        blocks = re.findall(r"^```mermaid\n(.*?)^```", text, re.M | re.S)
        if blocks:
            diagrams[path.relative_to(ROOT / "openwiki").as_posix()] = len(blocks)
            for block in blocks:
                if "accTitle:" not in block or "accDescr:" not in block:
                    errors.append(f"Missing accessible diagram text: {path}")
    for name, entry in manifest.items():
        page = ROOT / name.lstrip("/")
        sidecar = ROOT / "openwiki/.claims" / (str(page.relative_to(ROOT / "openwiki"))[:-3] + ".json")
        if not page.is_file() or not sidecar.is_file():
            errors.append(f"Missing manifest page/Claims: {name}")
            continue
        claims = json.loads(sidecar.read_text())
        if claims["pageVersion"] != entry["pageVersion"]:
            errors.append(f"Manifest/Claims version mismatch: {name}")
        claim_count += len(claims["claims"])
        for claim in claims["claims"]:
            for evidence in claim["evidence"]:
                evidence_count += 1
                resource = evidence["resource"]
                if not resource.startswith("repo://"):
                    errors.append(f"Non-repository evidence: {resource}")
                    continue
                resource_path, _, fragment = resource[7:].partition("#")
                source = ROOT / resource_path
                if not source.is_file():
                    errors.append(f"Missing evidence: {resource}")
                    continue
                span = re.fullmatch(r"L(\d+)(?:-L(\d+))?", fragment)
                if fragment and not span:
                    errors.append(f"Unknown evidence span: {resource}")
                if span:
                    start, end = int(span[1]), int(span[2] or span[1])
                    if not 1 <= start <= end <= len(source.read_text().splitlines()):
                        errors.append(f"Out-of-bounds evidence: {resource}")
    assert not errors, "\n".join(errors)
    return {"pages": len(manifest), "claims": claim_count, "evidence_resources": evidence_count, "diagrams": diagrams}


def browser_audit(site, output, diagrams):
    handler = functools.partial(http.server.SimpleHTTPRequestHandler, directory=str(site))
    server = http.server.ThreadingHTTPServer(("127.0.0.1", 0), handler)
    threading.Thread(target=server.serve_forever, daemon=True).start()
    origin = f"http://127.0.0.1:{server.server_port}"
    results = []
    try:
        with sync_playwright() as pw:
            browser = pw.chromium.launch()
            for width in (1440, 390):
                for name, count in diagrams.items():
                    page = browser.new_page(viewport={"width": width, "height": 1000}, device_scale_factor=1)
                    errors = []
                    page.on("pageerror", lambda error: errors.append(str(error)))
                    page.on("console", lambda msg: errors.append(msg.text) if msg.type == "error" else None)
                    page.goto(origin + "/openwiki/" + name[:-3] + "/", wait_until="networkidle")
                    page.wait_for_function("document.documentElement.dataset.mermaid === 'ready'", timeout=60000)
                    assert page.locator(".mermaid-diagram svg").count() == count, name
                    metrics = page.locator(".mermaid-diagram svg").evaluate_all("""svgs => svgs.map(svg => {
                        const v = svg.viewBox.baseVal, b = svg.getBBox();
                        return {title: svg.querySelector('title')?.textContent,
                            description: svg.querySelector('desc')?.textContent,
                            width: v.width, height: v.height, pixelWidth: svg.getBoundingClientRect().width,
                            contentFits: b.x >= v.x - 2 && b.y >= v.y - 2 &&
                                b.x + b.width <= v.x + v.width + 2 && b.y + b.height <= v.y + v.height + 2};
                    })""")
                    assert all(m["title"] and m["description"] and m["contentFits"] and abs(m["width"] - m["pixelWidth"]) < 2 for m in metrics), metrics
                    assert not errors, errors
                    screenshot = output / f"{name.replace('/', '-')[:-3]}-{width}.png"
                    page.screenshot(path=str(screenshot), full_page=True)
                    # Scroll every region fully in both directions: the narrow view
                    # must expose overflow, not clip hidden labels or shrink text.
                    for chart in page.locator(".mermaid-diagram").all():
                        chart.focus()
                        chart.evaluate("node => { node.scrollLeft = node.scrollWidth; }")
                        assert chart.evaluate("node => node.scrollLeft + node.clientWidth >= node.scrollWidth - 2")
                    page.screenshot(path=str(screenshot.with_stem(screenshot.stem + "-scrolled")), full_page=True)
                    results.append({"page": name, "viewport": width, "svgs": count, "console_errors": errors, "metrics": metrics, "screenshot": screenshot.name})
                    page.close()
            # Exercise a failed CDN import; source text and normal navigation survive.
            page = browser.new_page()
            page.route("https://cdn.jsdelivr.net/**", lambda route: route.abort())
            first = next(iter(diagrams))
            page.goto(origin + "/openwiki/" + first[:-3] + "/", wait_until="networkidle")
            page.wait_for_function("document.documentElement.dataset.mermaid === 'error'")
            assert page.locator("pre > code.language-mermaid").count() == diagrams[first]
            assert page.get_by_role("link", name="Architecture overview", exact=True).count() >= 1
            browser.close()
    finally:
        server.shutdown()
        server.server_close()
    return results


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--site", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    args.output.mkdir(parents=True, exist_ok=True)
    static = static_audit()
    result = {"static": static, "built_links": built_link_audit(args.site), "browser": browser_audit(args.site, args.output, static["diagrams"]), "cdn_failure_fallback": "passed"}
    (args.output / "verification.json").write_text(json.dumps(result, indent=2) + "\n")
    print(json.dumps(result, indent=2))


if __name__ == "__main__":
    main()
