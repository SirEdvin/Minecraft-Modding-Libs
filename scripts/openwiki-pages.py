"""Keep repository evidence links working without changing generated wiki sources."""

import re
from pathlib import Path
from urllib.parse import urlsplit


REPOSITORY = "https://github.com/SirEdvin/Minecraft-Modding-Libs/blob/multiversion/"


def on_page_markdown(markdown, *, page, config, files):
    docs = Path(config.docs_dir).resolve()
    source = Path(page.file.abs_src_path).parent

    def rewrite(match):
        target = match.group(2)
        url = urlsplit(target)
        if url.scheme or url.netloc or not url.path or url.path.startswith("/"):
            return match.group(0)
        destination = (source / url.path).resolve()
        if destination.is_relative_to(docs):
            # MkDocs resolves Markdown links, including directory indexes.
            if destination.is_dir() and (destination / "index.md").is_file():
                target = url._replace(path=url.path.rstrip("/") + "/index.md").geturl()
            return match.group(1) + target + match.group(3)
        if destination.is_relative_to(docs.parent):
            target = REPOSITORY + destination.relative_to(docs.parent).as_posix()
            if url.fragment:
                target += "#" + url.fragment
            return match.group(1) + target + match.group(3)
        return match.group(0)

    return re.sub(r"(\]\()([^\s)]+)(\))", rewrite, markdown)
