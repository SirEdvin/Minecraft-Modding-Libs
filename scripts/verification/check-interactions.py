#!/usr/bin/env python3
"""Validate one fresh R7 interaction run, in addition to the shared visual receipt.

Usage: python3 scripts/verification/check-interactions.py <run-directory>
This verifies artifacts, not visual appearance or hostile-packet coverage.
"""
import hashlib
import json
from pathlib import Path
import struct
import sys


def validate(run):
    control = run / "control"
    receipt = json.loads((run / "receipt.json").read_text())
    if not receipt.get("passed") or receipt.get("exits") != [0, 0] or receipt.get("actual_dedicated_joins") != 2:
        raise ValueError("Shared visual workflow did not pass")
    inventory = json.loads((control / "interaction-inventory.json").read_text())
    items = {"peripheralium:peripheralium_block", "peripheralium:peripheralium_dust", "peripheralium:peripheralium_upgrade_template", "tweakium:creative_filler"}
    if set(inventory["items"]) != items or len(inventory["items"]) != 4:
        raise ValueError("Exact item inventory mismatch")
    if set(inventory["blocks"]) != {"peripheralium:peripheralium_block", "tweakium:creative_filler"} or len(inventory["blocks"]) != 2:
        raise ValueError("Exact block inventory mismatch")
    if set(inventory["peripheraliumCreativeDisplay"]) != items - {"tweakium:creative_filler"} or len(inventory["peripheraliumCreativeDisplay"]) != 3:
        raise ValueError("Creative identity mismatch")
    if inventory["bookPages"] != ["Broccolium book text\nPeripheralium content\nTweakium facing models", "Second page: resource reload and reconnect."]:
        raise ValueError("Synchronized book mismatch")
    expected_upgrades = [f"computercraft:{item}|{variant}" for item in ("pocket_computer_normal", "pocket_computer_advanced", "turtle_normal", "turtle_advanced") for variant in ("base", "tweakium:r7_visual")]
    if inventory["consumerUpgradeCreative"] != expected_upgrades:
        raise ValueError("Exact consumer upgrade creative variants mismatch")
    log = (control / "interactions.log").read_text()
    expected = {
        "menu-decode registry-context=true schema=BlockPos,Int": 3,
        "server-open token=initial ": 1,
        "server-open token=reopen ": 1,
        "server-open token=reconnect ": 1,
        "server-ack exact-conservation diamond=16 emerald=7": 1,
        "PASS extended-open transfer-server-ack exact-conservation close-reopen fresh-menu-id": 1,
        "PASS reconnect authoritative-chest-and-player-state": 1,
        "verify dedicated=": 4,
        "exact-items=4 exact-blocks=2 creative=3 book-pages=2 descriptive-tooltip=1 hidden-prompt=true": 4,
    }
    for text, count in expected.items():
        if log.count(text) != count:
            raise ValueError(f"Missing/duplicated interaction event {text!r}, expected {count}")
    ack = (control / "interaction-transfer.ack").read_text()
    if ack != "diamond=16 emerald=7 chest-slot0=empty server-thread=Server thread":
        raise ValueError("Independent authoritative transfer acknowledgement missing")
    images = {}
    directory = control / "interaction/screenshots"
    expected_shots = {"01-extended-menu.png", "02-reopened-menu.png", "03-reconnected-menu.png"}
    if {path.name for path in directory.glob("*.png")} != expected_shots:
        raise ValueError("Interaction screenshot inventory mismatch")
    for path in sorted(directory.glob("*.png")):
        data = path.read_bytes()
        if data[:8] != b"\x89PNG\r\n\x1a\n" or len(data) < 1024:
            raise ValueError(f"Invalid/empty screenshot {path}")
        width, height = struct.unpack(">II", data[16:24])
        if width < 500 or height < 300:
            raise ValueError(f"Unexpected screenshot dimensions {width}x{height}")
        images[path.name] = {"sha256": hashlib.sha256(data).hexdigest(), "width": width, "height": height}
    for name, expected_hash in receipt["mods"].items():
        if hashlib.sha256((run / "client/mods" / name).read_bytes()).hexdigest() != expected_hash:
            raise ValueError(f"Run mod changed after acceptance: {name}")
    if (control / "failure.txt").exists():
        raise ValueError("Fixture failure present")
    return {"passed": True, "run": str(run.resolve()), "inventory": inventory, "events": expected, "screenshots": images, "mods": receipt["mods"], "limitations": ["visual appearance requires human/image inspection", "not the adversarial R6 two-client packet suite", "no Creative Filler appearance certification"]}


if __name__ == "__main__":
    if len(sys.argv) != 2:
        raise SystemExit(__doc__)
    result = validate(Path(sys.argv[1]))
    print(json.dumps(result, indent=2))
