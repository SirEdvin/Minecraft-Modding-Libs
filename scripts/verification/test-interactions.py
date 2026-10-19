#!/usr/bin/env python3
"""Mutation-check interaction evidence using an actual successful run (never synthetic runtime evidence).

python3 scripts/verification/test-interactions.py <accepted-run-directory>
"""
import importlib.util
import json
import os
from pathlib import Path
import shutil
import sys
import tempfile
import unittest

spec = importlib.util.spec_from_file_location("interaction_checks", Path(__file__).with_name("check-interactions.py"))
assert spec is not None and spec.loader is not None
checks = importlib.util.module_from_spec(spec)
spec.loader.exec_module(checks)
SOURCE: Path


class InteractionEvidenceTests(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory(prefix="interaction-evidence-", dir=os.environ["TMPDIR"])
        self.addCleanup(self.temporary.cleanup)
        self.run_path = Path(self.temporary.name)
        shutil.copy2(SOURCE / "receipt.json", self.run_path / "receipt.json")
        shutil.copytree(SOURCE / "control", self.run_path / "control")
        # Preserve read-only input links. A mutation test must unlink before replacing a linked file.
        shutil.copytree(SOURCE / "client/mods", self.run_path / "client/mods", symlinks=True)

    def inventory(self, change):
        path = self.run_path / "control/interaction-inventory.json"
        value = json.loads(path.read_text())
        change(value)
        path.write_text(json.dumps(value))

    def rejected(self):
        with self.assertRaises((ValueError, FileNotFoundError)):
            checks.validate(self.run_path)

    def test_actual_run_is_accepted(self):
        self.assertTrue(checks.validate(self.run_path)["passed"])

    def test_same_count_wrong_item_identity_is_rejected(self):
        self.inventory(lambda value: value["items"].__setitem__(0, "peripheralium:not_the_registered_item"))
        self.rejected()

    def test_same_count_wrong_creative_identity_is_rejected(self):
        self.inventory(lambda value: value["peripheraliumCreativeDisplay"].__setitem__(0, "minecraft:stone"))
        self.rejected()

    def test_book_page_reordering_is_rejected(self):
        self.inventory(lambda value: value["bookPages"].reverse())
        self.rejected()

    def test_wrong_upgrade_variant_is_rejected(self):
        self.inventory(lambda value: value["consumerUpgradeCreative"].__setitem__(1, "computercraft:pocket_computer_normal|base"))
        self.rejected()

    def test_missing_authoritative_ack_is_rejected(self):
        (self.run_path / "control/interaction-transfer.ack").unlink()
        self.rejected()

    def test_missing_menu_decode_is_rejected(self):
        path = self.run_path / "control/interactions.log"
        path.write_text(path.read_text().replace("menu-decode registry-context=true", "omitted-decode", 1))
        self.rejected()

    def test_missing_visible_creative_selection_is_rejected(self):
        path = self.run_path / "control/interactions.log"
        path.write_text(path.read_text().replace("PASS visible-creative-tab peripheralium:tab exact-display=3", "omitted-selection", 1))
        self.rejected()

    def test_missing_screenshot_is_rejected(self):
        (self.run_path / "control/interaction/screenshots/02-reopened-menu.png").unlink()
        self.rejected()

    def test_mutated_fixture_input_is_rejected(self):
        path = next((self.run_path / "client/mods").glob("tweakium-client-*.jar"))
        path.unlink()
        path.write_bytes(b"changed fixture")
        self.rejected()

    def test_failed_shared_workflow_is_rejected(self):
        path = self.run_path / "receipt.json"
        value = json.loads(path.read_text())
        value["exits"] = [0, 1]
        path.write_text(json.dumps(value))
        self.rejected()


if __name__ == "__main__":
    if len(sys.argv) != 2:
        raise SystemExit(__doc__)
    SOURCE = Path(sys.argv.pop()).resolve()
    if not (SOURCE / "receipt.json").is_file():
        raise SystemExit("Requires actual completed workflow receipt")
    unittest.main()
