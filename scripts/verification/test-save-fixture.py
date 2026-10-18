#!/usr/bin/env python3
"""Owning probe guards. These are NOT Minecraft runtime/migration evidence."""
import json
from pathlib import Path
import runpy
import struct
import tempfile
import unittest

HERE = Path(__file__).resolve().parent
FIXTURE = runpy.run_path(str(HERE / 'save-fixture.py'))
NBT = runpy.run_path(str(HERE / 'save-nbt.py'))


class SaveProbeTests(unittest.TestCase):
    def test_named_compound_and_numeric_array(self):
        data = b'\x0a\x00\x00\x0b\x00\x01x' + struct.pack('>iiiii', 4, 1, 2, 3, 4) + b'\x00'
        self.assertEqual(NBT['Reader'](data).root(), {'x': [1, 2, 3, 4]})

    def test_truncation_is_not_empty_success(self):
        with self.assertRaises(ValueError):
            NBT['Reader'](b'\x0a\x00\x00\x03\x00\x01x\x00').root()

    def test_trailing_data_rejected(self):
        with self.assertRaises(ValueError):
            NBT['Reader'](b'\x0a\x00\x00\x00garbage').root()

    def test_negative_array_length_rejected(self):
        with self.assertRaises(ValueError):
            NBT['Reader'](b'\x0a\x00\x00\x0b\x00\x01x\xff\xff\xff\xff').root()

    def test_duplicate_keys_rejected(self):
        with self.assertRaises(ValueError):
            NBT['Reader'](b'\x0a\x00\x00' + 2 * (b'\x03\x00\x01x\x00\x00\x00\x01') + b'\x00').root()

    def test_backup_is_exclusive_and_source_unchanged(self):
        with tempfile.TemporaryDirectory() as root:
            root = Path(root)
            source = root / 'source'; source.mkdir()
            (source / 'level.dat').write_bytes(b'immutable original')
            backup = root / 'backup'
            result = FIXTURE['preserve'](source, backup)
            self.assertEqual(result, FIXTURE['manifest'](source))
            self.assertEqual(result, FIXTURE['manifest'](backup))
            with self.assertRaises(FileExistsError):
                FIXTURE['preserve'](source, backup)
            (source / 'level.dat').write_bytes(b'new generation')
            self.assertEqual(result, FIXTURE['manifest'](backup))

    def test_symlink_input_rejected(self):
        with tempfile.TemporaryDirectory() as root:
            root = Path(root)
            (root / 'file').write_text('data')
            (root / 'link').symlink_to(root / 'file')
            with self.assertRaises(ValueError):
                FIXTURE['manifest'](root)

    def test_removed_components_and_unrelated_patch_retained(self):
        item = {'Slot': 2, 'id': 'computercraft:pocket_computer_normal', 'count': 1, 'components': {'!minecraft:custom_name': {}, 'computercraft:pocket_upgrade': {'id': 'computercraft:wireless_modem_normal', 'components': {'minecraft:custom_data': {'setting': 37}, '!minecraft:custom_name': {}, 'minecraft:damage': 5}}}}
        first = FIXTURE['normalize_item'](item)
        item['components']['computercraft:back_pocket_upgrade'] = item['components'].pop('computercraft:pocket_upgrade')
        self.assertEqual(first, FIXTURE['normalize_item'](item))
        self.assertEqual(first['patch'], {'!minecraft:custom_name': {}})
        self.assertEqual(first['pocket']['patch'], {'!minecraft:custom_name': {}, 'minecraft:damage': 5})

    def test_missing_installed_upgrade_is_not_defaulted(self):
        with self.assertRaises(ValueError):
            FIXTURE['normalize_upgrade'](None)

    def test_empty_snapshot_is_not_a_fixture_pass(self):
        with self.assertRaises((KeyError, AssertionError)):
            FIXTURE['validate']({}, True)

    def test_commands_have_version_specific_fields(self):
        old = '\n'.join(FIXTURE['creation_commands']('1.20.1'))
        modern = '\n'.join(FIXTURE['creation_commands']('1.21.1'))
        self.assertIn('LeftUpgradeNbt:', old)
        self.assertNotIn('LeftUpgradeNbt:', modern)
        self.assertIn('"!minecraft:custom_name":{}', modern)
        self.assertNotIn('peripheralium_blend', old + modern)
        self.assertNotIn('r3_native', old + modern)

    def test_padded_block_palette(self):
        palette = [{'Name': f'fixture:block_{x}'} for x in range(17)]
        values = [0] * 342
        values[0] = 16 << 60  # x=12 is first value of next long, not this high tail.
        values[1] = 7
        chunk = {'sections': [{'Y': 5, 'block_states': {'palette': palette, 'data': values}}]}
        self.assertEqual(NBT['block_at'](chunk, 12, 80, 0), palette[7])


if __name__ == '__main__':
    unittest.main()
