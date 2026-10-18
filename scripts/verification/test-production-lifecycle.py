#!/usr/bin/env python3
"""Guard tests only; these fixtures are never production execution evidence."""
import importlib.util
from pathlib import Path
import tempfile
import unittest

spec = importlib.util.spec_from_file_location("lifecycle", Path(__file__).with_name("production-lifecycle.py"))
assert spec is not None and spec.loader is not None
lifecycle = importlib.util.module_from_spec(spec)
spec.loader.exec_module(lifecycle)


class LifecycleGuards(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.world = Path(self.temp.name)
        (self.world / "region").mkdir()
        (self.world / "region/r.0.0.mca").touch()
        (self.world / "level.dat").touch()
        self.text = '\n'.join([
            'Done (1.23s)! For help, type "help"', 'Stopping server',
            'Saving players', 'Saving worlds', 'All dimensions are saved',
        ])

    def verify(self, text=None, rc=0, stopped=True, timeout=False):
        return lifecycle.verify_exit(self.text if text is None else text, rc, stopped, timeout, self.world)

    def test_complete_signals(self):
        self.assertTrue(all(self.verify().values()))

    def test_each_required_signal(self):
        for line in self.text.splitlines():
            with self.subTest(line=line):
                self.assertFalse(all(self.verify(self.text.replace(line, '')).values()))

    def test_failure_timeout_and_no_console_stop(self):
        self.assertFalse(all(self.verify(rc=1).values()))
        self.assertFalse(all(self.verify(timeout=True).values()))
        self.assertFalse(all(self.verify(stopped=False).values()))

    def test_modern_dimension_region_layout(self):
        old = self.world / 'region/r.0.0.mca'
        modern = self.world / 'dimensions/minecraft/overworld/region/r.0.0.mca'
        modern.parent.mkdir(parents=True)
        old.rename(modern)
        self.assertTrue(all(self.verify().values()))

    def test_gametest_is_not_production(self):
        self.assertFalse(all(self.verify(self.text + '\nRunning GameTests').values()))

    def test_actual_world_files_required(self):
        (self.world / 'level.dat').unlink()
        self.assertFalse(all(self.verify().values()))
        (self.world / 'level.dat').touch()
        (self.world / 'region/r.0.0.mca').unlink()
        self.assertFalse(all(self.verify().values()))


if __name__ == '__main__':
    unittest.main()
