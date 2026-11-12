#!/usr/bin/env python3
"""Static development-dependency policy, not Minecraft runtime evidence."""
from pathlib import Path

import tomllib
import unittest

ROOT = Path(__file__).resolve().parents[2]


class RecipeViewerPolicy(unittest.TestCase):
    def setUp(self):
        self.catalog = tomllib.loads((ROOT / 'gradle/libs.versions.toml').read_text())

    def test_no_rei_dependency_or_alias(self):
        for section in ('versions', 'libraries', 'bundles'):
            for name, value in self.catalog[section].items():
                with self.subTest(section=section, name=name):
                    self.assertNotRegex(f'{name} {value}'.lower(), r'\brei|roughlyenoughitems')
        for path in ROOT.glob('projects/*/*/build.gradle.kts'):
            self.assertNotRegex(path.read_text(), r'libs\.rei|val rei\b', str(path))

    def test_fabric_primary_is_pinned_emi(self):
        for suffix, minecraft in (('120', '1.20.1'), ('121', '1.21.1')):
            alias = f'emiFabric{suffix}'
            self.assertEqual(self.catalog['libraries'][alias], {
                'module': 'dev.emi:emi-fabric', 'version': {'ref': f'emi{suffix}'},
            })
            self.assertEqual(self.catalog['versions'][f'emi{suffix}'], f'1.1.24+{minecraft}')
            self.assertEqual(self.catalog['bundles'][f'externalModsFabricRuntime{suffix}'], [alias, f'modmenu{suffix}'])
        for library in ('broccolium', 'tweakium', 'peripheralium'):
            self.assertIn('modLocalRuntime(runtimeMods)', (ROOT / f'projects/{library}/fabric/build.gradle.kts').read_text())

    def test_forge_primary_is_emi_and_excluded_from_publication(self):
        self.assertEqual(self.catalog['libraries']['emi-forge']['module'], 'dev.emi:emi-forge')
        self.assertEqual(self.catalog['libraries']['emi-neoforge']['module'], 'dev.emi:emi-neoforge')
        for library in ('broccolium', 'tweakium', 'peripheralium'):
            text = (ROOT / f'projects/{library}/forge/build.gradle.kts').read_text()
            with self.subTest(library=library):
                self.assertNotIn('libs.jei.', text)
                self.assertIn('libs.emi.neoforge', text)
                self.assertIn('libs.emi.forge', text)
                self.assertIn('isCanBeConsumed = false', text)
                self.assertIn('isCanBeResolved = false', text)
                self.assertRegex(text, r'exclude\(if \(modernForge\) libs\.emi\.neoforge\.get\(\) else libs\.emi\.forge\.get\(\)\)')


if __name__ == '__main__':
    unittest.main()
