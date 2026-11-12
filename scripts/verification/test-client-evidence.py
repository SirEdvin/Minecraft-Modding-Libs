#!/usr/bin/env python3
"""Synthetic evidence-validator negatives; NEVER Minecraft execution receipts."""
import importlib.util
from pathlib import Path
import subprocess
import sys
import unittest

legacy_spec = importlib.util.spec_from_file_location('legacy_client_runner', Path(__file__).with_name('run-legacy-client.py'))
assert legacy_spec is not None and legacy_spec.loader is not None
legacy_runner = importlib.util.module_from_spec(legacy_spec)
legacy_spec.loader.exec_module(legacy_runner)


class EmiEvidenceGuards(unittest.TestCase):
    def test_reject_search_without_inventory_and_plugin_guards(self):
        for flags in ([], ['--inventory-screenshot'], ['--require-emi']):
            with self.subTest(flags=flags):
                result = subprocess.run(
                    [sys.executable, str(Path(__file__).with_name('run-legacy-client.py')),
                     '--server-source', 'unused', '--client-info', 'unused', '--java', 'unused',
                     '--output', 'unused', '--eula-accepted', '--emi-search', *flags],
                    capture_output=True, text=True, check=False,
                )
                self.assertEqual(result.returncode, 2)
                self.assertIn('--emi-search requires --inventory-screenshot and --require-emi', result.stderr)

    def test_reject_join_without_emi(self):
        with self.assertRaises(ValueError):
            legacy_runner.validate_emi('SYNTHETIC: player joined the game')

    def test_reject_incomplete_plugin_reload(self):
        for log in ('[EMI] Initialized plugin from computercraft', '[EMI] Reloaded EMI in 100ms'):
            with self.subTest(log=log), self.assertRaises(ValueError):
                legacy_runner.validate_emi(log)

    def test_complete_synthetic_plugin_log(self):
        legacy_runner.validate_emi('SYNTHETIC GUARD ONLY\n[EMI] Initialized plugin from computercraft\n[EMI] Reloaded EMI in 100ms')


if __name__ == '__main__':
    unittest.main()
