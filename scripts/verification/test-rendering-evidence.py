#!/usr/bin/env python3
"""Negative guards for the rendering-specific receipt extension (not runtime results)."""
import importlib.util
from pathlib import Path
import unittest

spec = importlib.util.spec_from_file_location("rendering", Path(__file__).with_name("check-rendering.py"))
assert spec is not None and spec.loader is not None
rendering = importlib.util.module_from_spec(spec)
spec.loader.exec_module(rendering)


class RenderingEvidenceTests(unittest.TestCase):
    def sample(self):
        lines = []
        for phase in (2, 12, 16, 23):
            for slot, item in enumerate(rendering.ITEMS):
                if slot == 7:
                    materials = ", ".join(f"tweakium:block/creative_filler_{face}|SOLID|atlas=minecraft:textures/atlas/blocks.png|blend=false" for face in ("side", "top", "bottom"))
                    lines.append(f"checkpoint=test:{phase} slot=7 item={item} quads=6 materials=[{materials}] snapshot=stable pose=restored")
                    continue
                sprite = "minecraft:block/diamond_block" if slot in (3, 4) else (
                    "computercraft:item/pocket_computer_advanced" if slot == 5 else
                    item.replace(":", ":block/" if slot == 2 else ":item/", 1))
                lines.append(f"checkpoint=test:{phase} slot={slot} item={item} quads=6 materials=[{sprite}|SOLID|atlas=minecraft:textures/atlas/blocks.png|blend=false] snapshot=stable pose=restored")

        return "\n".join(lines)

    def test_complete_scoped_proof(self):
        self.assertEqual(rendering.validate_materials(self.sample(), "test")["models"], 32)

    def test_empty_quad_state_rejected(self):
        with self.assertRaises(ValueError):
            rendering.validate_materials(self.sample().replace("quads=6", "quads=0", 1), "test")

    def test_nonempty_missing_material_rejected(self):
        with self.assertRaises(ValueError):
            rendering.validate_materials(self.sample().replace("peripheralium:item/peripheralium_dust|", "minecraft:missingno|", 1), "test")

    def test_upgrade_omitted_rejected(self):
        with self.assertRaises(ValueError):
            rendering.validate_materials(self.sample().replace("minecraft:block/diamond_block|", "minecraft:block/stone|", 1), "test")

    def test_missing_reload_checkpoint_rejected(self):
        with self.assertRaises(ValueError):
            rendering.validate_materials(self.sample().replace("checkpoint=test:12", "checkpoint=test:2"), "test")

    def test_snapshot_instability_rejected(self):
        with self.assertRaises(ValueError):
            rendering.validate_materials(self.sample().replace("snapshot=stable", "snapshot=changed", 1), "test")

    def test_barrel_face_cannot_be_omitted(self):
        with self.assertRaises(ValueError):
            rendering.validate_materials(self.sample().replace("tweakium:block/creative_filler_bottom|", "minecraft:block/barrel_bottom|", 1), "test")

    def test_historical_negative_is_not_positive_acceptance(self):
        lines = self.sample().splitlines()
        lines[7] = "checkpoint=test:2 negative=creative_filler missing-material-rejected all-content=BLOCKED"
        with self.assertRaises(ValueError):
            rendering.validate_materials("\n".join(lines), "test")


if __name__ == "__main__":
    unittest.main()
