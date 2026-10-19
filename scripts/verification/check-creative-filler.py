#!/usr/bin/env python3
"""Check both target datagen outputs (and optional packaged JARs), never hand-author resources."""
import hashlib
import json
from pathlib import Path
import struct
import sys
import zipfile

ROOT = Path(__file__).resolve().parents[2]
BASE = "assets/tweakium/"
FACES = ("side", "top", "bottom")


def check(read):
    state = json.loads(read(BASE + "blockstates/creative_filler.json"))
    assert state == {"variants": {"": {"model": "tweakium:block/creative_filler"}}}, state
    item = json.loads(read(BASE + "items/creative_filler.json"))
    assert item["model"] == {"type": "minecraft:model", "model": "tweakium:block/creative_filler"}, item
    model = json.loads(read(BASE + "models/block/creative_filler.json"))
    assert model["parent"] == "minecraft:block/cube_bottom_top", model
    for face in FACES:
        value = model["textures"][face]
        # Target Material codec may encode a texture as an object rather than a bare identifier.
        if isinstance(value, dict):
            value = value["texture"]
        assert value == f"tweakium:block/creative_filler_{face}", model
        png = read(BASE + f"textures/block/creative_filler_{face}.png")
        assert png.startswith(b"\x89PNG\r\n\x1a\n"), face
        assert struct.unpack(">II", png[16:24]) == (16, 16), face
    paths = [BASE + suffix for suffix in ("blockstates/creative_filler.json", "items/creative_filler.json", "models/block/creative_filler.json")]
    paths += [BASE + f"textures/block/creative_filler_{face}.png" for face in FACES]
    return {path: hashlib.sha256(read(path)).hexdigest() for path in paths}


def main():
    results = {}
    for loader in ("fabric", "forge"):
        directory = ROOT / f"projects/tweakium/{loader}/versions/26.1.2/src/generated/resources"
        results[loader] = check(lambda name: (directory / name).read_bytes())
    assert results["fabric"] == results["forge"], "Loader assets differ"
    for jar in sys.argv[1:]:
        with zipfile.ZipFile(jar) as archive:
            actual = check(archive.read)
            assert actual == results["fabric"], f"Packaged assets differ: {jar}"
            assert not any("clienttest" in name for name in archive.namelist()), "Private fixture leaked"
        results[jar] = actual
    print(json.dumps({"passed": True, "resources": results}, indent=2))


if __name__ == "__main__":
    main()
