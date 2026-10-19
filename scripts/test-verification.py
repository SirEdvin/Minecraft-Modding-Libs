#!/usr/bin/env python3
"""Adversarial checks for the release-report guards, not Minecraft execution."""
import importlib.util
import io
from pathlib import Path
import tempfile
import unittest
from zipfile import ZipFile


def load(filename):
    spec = importlib.util.spec_from_file_location(filename, Path(__file__).with_name(filename + ".py"))
    assert spec is not None and spec.loader is not None
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


reports = load("check-test-reports")
publications = load("check-publications")


class ReportGuards(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.marker = self.root / "start"
        self.marker.touch()
        self.paths = []
        for key in reports.EXPECTED:
            library, loader, version = key.split("/")
            branch = self.root / "projects" / library
            if loader != "common":
                branch /= loader
            path = branch / "versions" / version / "build/test-results/test/TEST-one.xml"
            path.parent.mkdir(parents=True)
            path.write_text('<testsuite tests="1"><testcase classname="Probe" name="one"/></testsuite>')
            self.paths.append(path)

    def check(self):
        return reports.check_reports(self.root, self.marker)

    def test_valid_exact_nodes(self):
        self.assertEqual(self.check()["tests"], len(reports.EXPECTED))

    def test_missing(self):
        self.paths[0].unlink()
        with self.assertRaises(AssertionError):
            self.check()

    def test_stale(self):
        import os
        os.utime(self.paths[0], ns=(0, 0))
        with self.assertRaises(AssertionError):
            self.check()

    def test_invalid_reports(self):
        for body in (
            '<testsuite tests="0"/>',
            '<testsuite tests="2"><testcase/></testsuite>',
            '<testsuite tests="1" failures="1"><testcase><failure/></testcase></testsuite>',
            '<testsuite tests="1" errors="1"><testcase><error/></testcase></testsuite>',
            '<testsuite tests="1" skipped="1"><testcase><skipped/></testcase></testsuite>',
            '<!DOCTYPE testsuite><testsuite tests="1"><testcase/></testsuite>',
            '<testsuite tests="1" skipped="1"><testcase/></testsuite>',
        ):
            with self.subTest(body=body):
                self.paths[0].write_text(body)
                with self.assertRaises(AssertionError):
                    self.check()

    def test_parameterized_display_names_are_not_invocation_ids(self):
        self.paths[0].write_text('<testsuite tests="2"><testcase name="same"/><testcase name="same"/></testsuite>')
        self.assertEqual(self.check()["tests"], len(reports.EXPECTED) + 1)


class JarGuards(unittest.TestCase):
    def test_majors_and_private_fixtures(self):
        for version, major in publications.MAJORS.items():
            for invalid in (None, "major", "fixture", "mixin"):
                with self.subTest(version=version, invalid=invalid):
                    data = io.BytesIO()
                    with ZipFile(data, "w") as jar:
                        actual = major + 1 if invalid == "major" else major
                        jar.writestr("public/Extension.class", b"\xca\xfe\xba\xbe\0\0" + actual.to_bytes(2, "big"))
                        if invalid == "fixture":
                            jar.writestr("private/fixture/Leak.class", b"unused")
                        if invalid == "mixin":
                            jar.writestr("missing.mixins.json", '{"package":"missing", "mixins":["Absent"]}')
                    with ZipFile(data) as jar:
                        if invalid is None:
                            publications.check_jar(jar, "probe", version, "common", "probe", "2.0.0")
                        else:
                            with self.assertRaises(AssertionError):
                                publications.check_jar(jar, "probe", version, "common", "probe", "2.0.0")


if __name__ == "__main__":
    unittest.main()
