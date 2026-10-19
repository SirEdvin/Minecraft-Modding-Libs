#!/usr/bin/env python3
"""Synthetic evidence-validator negatives; NEVER Minecraft execution receipts."""
import importlib.util
import json
import os
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest

spec = importlib.util.spec_from_file_location('client_runner', Path(__file__).with_name('run-client.py'))
assert spec is not None and spec.loader is not None
runner = importlib.util.module_from_spec(spec)
spec.loader.exec_module(runner)

legacy_spec = importlib.util.spec_from_file_location('legacy_client_runner', Path(__file__).with_name('run-legacy-client.py'))
assert legacy_spec is not None and legacy_spec.loader is not None
legacy_runner = importlib.util.module_from_spec(legacy_spec)
legacy_spec.loader.exec_module(legacy_runner)


class ClientEvidenceGuards(unittest.TestCase):
    def setUp(self):
        self.directory = tempfile.TemporaryDirectory(dir=os.environ['TMPDIR'], prefix='r7-synthetic-guard-')
        self.addCleanup(self.directory.cleanup)
        self.root = Path(self.directory.name)
        control = self.root / 'control'
        (control / 'screenshots').mkdir(parents=True)
        (control / 'result.json').write_text(json.dumps({'passed': True, 'phases': 26}))
        (control / 'client.log').write_text(''.join(f'phase={i} screen=fixture\n' for i in range(27)))
        names = ['01-title', '02-integrated-world', '03-content-models-tooltip', '04-creative-tab', '05-book-page-one', '06-book-page-two', '07-reloaded-models', '08-dedicated-join', '09-native-menu', '10-equipped-pocket-menu', '11-dedicated-reconnect', '12-reconnected-models']
        for name in names:
            (control / 'screenshots' / (name + '.png')).write_bytes(b'SYNTHETIC GUARD ONLY')
        (control / 'models.log').write_text('synthetic extraction\n' * 28)
        (self.root / 'server.log').write_text('R7Viewer joined the game\nR7Viewer left the game\n' * 2 + 'Stopping server\nAll dimensions are saved\n')

    def test_complete_schema(self):
        result = runner.validate_evidence(self.root, [0, 0])
        self.assertEqual(result['phase_count'], 27)
        self.assertEqual(result['actual_dedicated_joins'], 2)
        self.assertEqual(len(result['screenshots']), 12)

    def test_reject_nonzero_exit(self):
        with self.assertRaises(ValueError):
            runner.validate_evidence(self.root, [0, 124])

    def test_reject_missing_or_duplicate_phase(self):
        path = self.root / 'control/client.log'
        for content in ('phase=0 screen=fixture\n', path.read_text() + 'phase=26 screen=fixture\n'):
            with self.subTest(content=content[-60:]):
                path.write_text(content)
                with self.assertRaises(ValueError):
                    runner.validate_evidence(self.root, [0, 0])

    def test_reject_wrong_screenshot_identity(self):
        (self.root / 'control/screenshots/01-title.png').rename(self.root / 'control/screenshots/wrong.png')
        with self.assertRaises(ValueError):
            runner.validate_evidence(self.root, [0, 0])

    def test_reject_missing_reconnect_or_shutdown(self):
        for content in ('R7Viewer joined the game\nStopping server\nAll dimensions are saved', 'R7Viewer joined the game\nR7Viewer left the game\n' * 2):
            with self.subTest(content=content):
                (self.root / 'server.log').write_text(content)
                with self.assertRaises(ValueError):
                    runner.validate_evidence(self.root, [0, 0])

    def test_reject_missing_model_proof(self):
        (self.root / 'control/models.log').write_text('')
        with self.assertRaises(ValueError):
            runner.validate_evidence(self.root, [0, 0])

    def test_reject_explicit_fixture_failure(self):
        (self.root / 'control/failure.txt').write_text('SYNTHETIC GUARD')
        with self.assertRaises(ValueError):
            runner.validate_evidence(self.root, [0, 0])


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
