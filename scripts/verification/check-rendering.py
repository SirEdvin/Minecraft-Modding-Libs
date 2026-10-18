#!/usr/bin/env python3
"""Validate target submitted-material proof in addition to the shared visual receipt.

Usage: python3 scripts/verification/check-rendering.py <run-directory>
This is scoped proof for the seven-entry gallery, NOT all-content certification.
"""
import hashlib
import json
from pathlib import Path
import re
import sys


ITEMS = (
    "peripheralium:peripheralium_dust",
    "peripheralium:peripheralium_upgrade_template",
    "peripheralium:peripheralium_block",
    "computercraft:turtle_advanced",
    "computercraft:turtle_advanced",
    "computercraft:pocket_computer_advanced",
    "minecraft:written_book",
)


def validate_materials(text, route):
    lines = text.splitlines()
    if len(lines) != 32:
        raise ValueError("Expected exactly four checkpoints, each seven models and one explicit negative")
    for index, phase in enumerate((2, 12, 16, 23)):
        for slot, item in enumerate(ITEMS):
            line = lines[index * 8 + slot]
            prefix = f"checkpoint={route}:{phase} slot={slot} item={item} quads="
            if not line.startswith(prefix) or not line.endswith(" snapshot=stable pose=restored"):
                raise ValueError(f"Missing ordered material/snapshot proof phase={phase} slot={slot}")
            count = re.match(r"(\d+) materials=\[(.*)\] snapshot=stable pose=restored$", line[len(prefix):])
            if not count or int(count[1]) <= 0 or not count[2] or "missingno" in count[2]:
                raise ValueError(f"Invalid submitted materials phase={phase} slot={slot}")
            material = count[2]
            if "|atlas=minecraft:textures/atlas/" not in material or "|blend=" not in material:
                raise ValueError("Missing actual atlas/material contract")
            expected = "minecraft:block/diamond_block" if slot in (3, 4) else (
                "computercraft:item/pocket_computer_advanced" if slot == 5 else
                (item.replace(":", ":block/", 1) if slot == 2 else item.replace(":", ":item/", 1)))
            if expected + "|" not in material:
                raise ValueError(f"Expected material omitted: {expected}")
        negative = lines[index * 8 + 7]
        if negative != f"checkpoint={route}:{phase} negative=creative_filler missing-material-rejected all-content=BLOCKED":
            raise ValueError("Creative Filler missing-material gate was hidden or waived")
    return {"checkpoints": 4, "models": 28, "explicit_missing_material_negatives": 4}


def validate(run):
    receipt = json.loads((run / "receipt.json").read_text())
    if not receipt.get("passed") or receipt.get("exits") != [0, 0] or receipt.get("actual_dedicated_joins") != 2 or receipt.get("phase_count") != 27:
        raise ValueError("Shared real-client workflow did not pass")
    if (run / "control/failure.txt").exists():
        raise ValueError("Fixture failure present")
    proof = validate_materials((run / "control/materials.log").read_text(), receipt["route"])
    for name, expected in receipt["mods"].items():
        if hashlib.sha256((run / "client/mods" / name).read_bytes()).hexdigest() != expected:
            raise ValueError(f"Run mod changed: {name}")
    images = list((run / "control/screenshots").glob("*.png"))
    if len(images) != 12 or {p.name for p in images} != set(receipt["screenshots"]):
        raise ValueError("Screenshot inventory mismatch")
    for path in images:
        if hashlib.sha256(path.read_bytes()).hexdigest() != receipt["screenshots"][path.name]:
            raise ValueError(f"Screenshot changed: {path}")
    return {"passed": True, "run": str(run.resolve()), "route": receipt["route"], **proof,
            "screenshots": len(images), "mods": receipt["mods"], "limitations": [
                "Creative Filler remains blocked pending approved appearance/resources",
                "submission proof is GUI item extraction, not exhaustive world block-entity geometry",
                "screenshots need independent visual inspection; positive energy and adversarial networking are separate gates"]}


if __name__ == "__main__":
    if len(sys.argv) != 2:
        raise SystemExit(__doc__)
    print(json.dumps(validate(Path(sys.argv[1])), indent=2))
