#!/usr/bin/env python3
"""R6 owning copied-save probe. Operates only on explicit scratch assemblies.

Production JARs are hash-checked. Creation uses real server console commands;
inspection is read-only disk NBT plus live /data queries, never synthetic saves.
No EULA acceptance, installation, deletion, downgrade or cross-loader conversion.
"""
import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import runpy
import selectors
import shutil
import signal
import subprocess
import time

HERE = Path(__file__).resolve().parent
LIFECYCLE = runpy.run_path(str(HERE / "production-lifecycle.py"))
NBT = runpy.run_path(str(HERE / "save-nbt.py"))
POSITIONS = {"filler": 0, "content": 1, "chest": 2, "turtle": 3}
OWNER = [1, 2, 3, 4]


def digest(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


def manifest(directory):
    files = sorted(directory.rglob("*"))
    if not directory.is_dir() or any(p.is_symlink() for p in files):
        raise ValueError("Expected real directory, no symlinks")
    return {str(p.relative_to(directory)): digest(p) for p in files if p.is_file()}


def preserve(source, destination):
    """Exclusive new destination; never opens an existing backup for writing."""
    if destination.exists() or destination.is_symlink():
        raise FileExistsError(destination)
    before = manifest(source)
    shutil.copytree(source, destination, symlinks=False)
    if manifest(source) != before or manifest(destination) != before:
        raise ValueError("Source changed during copy or copy differs")
    return before


def creation_commands(version):
    legacy = version == "1.20.1"
    def item(slot, name, count=1, data=""):
        if legacy:
            return '{Slot:%sb,id:"%s",Count:%sb%s}' % (slot, name, count, ',tag:' + data if data else '')
        return '{Slot:%sb,id:"%s",count:%s%s}' % (slot, name, count, ',components:' + data if data else '')
    def custom(side):
        return '{ownerProfile:"r6-' + side + '",setting:37,storedData:{mode:"retained"}}'
    def upgrade(name, side):
        return '{id:"' + name + '",components:{"minecraft:custom_data":' + custom(side) + ',"!minecraft:custom_name":{},"minecraft:damage":5}}'
    settings = '{mode:"r6",enabled:1b,limit:37,nested:{text:"retain",ratio:1.25d}'
    if not legacy:
        settings += ',__patch_storage__:{"!minecraft:custom_name":{},"minecraft:damage":7,"minecraft:custom_data":{token:"r6"}}'
    settings += '}'
    pocket = ('{Upgrade:"computercraft:speaker",UpgradeInfo:' + custom('back') + ',storedData:{legacy:"retained"}}') if legacy else ('{"computercraft:pocket_upgrade":' + upgrade('computercraft:speaker', 'back') + ',"minecraft:custom_data":{storedData:{legacy:"retained"}}}')
    tool = '{Damage:5,storedData:{legacy:"retained"}}' if legacy else '{"minecraft:damage":5,"minecraft:custom_data":{storedData:{legacy:"retained"}},"!minecraft:tool":{}}'
    items = [item(0, 'peripheralium:peripheralium_dust', 17), item(1, 'peripheralium:peripheralium_upgrade_template', 3), item(2, 'computercraft:pocket_computer_normal', data=pocket), item(3, 'minecraft:diamond_pickaxe', data=tool), item(4, 'minecraft:water_bucket'), item(5, 'minecraft:lava_bucket'), item(6, 'minecraft:diamond', 23)]
    turtle = 'ComputerId:42,Label:"R6 turtle",On:0b,Fuel:1234,Slot:2,Owner:{UpperId:4294967298L,LowerId:12884901892L,Name:"R6Owner"},Items:[' + item(0, 'minecraft:diamond', 11) + ',' + item(1, 'minecraft:water_bucket') + ']'
    if legacy:
        turtle += ',LeftUpgrade:"computercraft:speaker",LeftUpgradeNbt:' + custom('left') + ',RightUpgrade:"computercraft:speaker",RightUpgradeNbt:' + custom('right')
    else:
        turtle += ',LeftUpgrade:' + upgrade('computercraft:speaker', 'left') + ',RightUpgrade:' + upgrade('computercraft:speaker', 'right')
    return [
        'setblock 0 80 0 tweakium:creative_filler{ownerProfile:[I;1,2,3,4],peripheralData:' + settings + '}',
        'setblock 1 80 0 peripheralium:peripheralium_block',
        'setblock 2 80 0 minecraft:chest[facing=north]{Items:[' + ','.join(items) + ']}',
        'setblock 3 80 0 computercraft:turtle_normal[facing=north]{' + turtle + '}',
    ]


def run(directory, label, create=False):
    assembly = json.loads((directory / "assembly.json").read_text())
    if not re.search(r"(?m)^eula=true\s*$", (directory / "eula.txt").read_text()):
        raise ValueError("Explicit existing EULA authorization required")
    for name, expected in assembly["mods"].items():
        if digest(directory / "mods" / name) != expected:
            raise ValueError(f"Artifact mismatch: {name}")
    if (directory / f"{label}.json").exists() or (directory / f"{label}.log").exists():
        raise FileExistsError(f"Receipt label already used: {label}")
    version = assembly["route"].rsplit('-', 1)[0]
    commands = ['forceload add 0 0', 'say R6_CHUNK_REQUESTED']
    fixture = creation_commands(version) if create else []
    queries = [f'data get block {x} 80 0' for name, x in POSITIONS.items() if name != 'content']
    queries += ['execute if block 1 80 0 peripheralium:peripheralium_block run say R6_CONTENT_PRESENT', 'save-all flush', 'say R6_COMMANDS_FINISHED']
    all_commands = commands + fixture + queries + ['stop']
    start = time.monotonic()
    deadline = start + 240
    output = bytearray()
    stage = 0
    stopped = timed_out = False
    ready_seconds = None
    chunk_ready_at = float('inf')
    with (directory / f"{label}.log").open('xb') as log:
        process = subprocess.Popen(assembly["command"], cwd=directory, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, start_new_session=True)
        assert process.stdout is not None and process.stdin is not None
        selector = selectors.DefaultSelector()
        selector.register(process.stdout.fileno(), selectors.EVENT_READ)
        def send(lines):
            process.stdin.write(('\n'.join(lines) + '\n').encode())
            process.stdin.flush()
        try:
            while True:
                for key, _ in selector.select(0.25):
                    data = os.read(key.fd, 65536)
                    if data:
                        log.write(data); log.flush(); output.extend(data)
                    else:
                        selector.unregister(key.fileobj)
                text = output.decode(errors='replace')
                if stage == 0 and LIFECYCLE['READY'].search(text):
                    ready_seconds = time.monotonic() - start
                    send(commands); stage = 1; deadline = time.monotonic() + 120
                elif stage == 1 and '[Server] R6_CHUNK_REQUESTED' in text:
                    # Forced chunks load asynchronously; give the real tick loop time.
                    chunk_ready_at = time.monotonic() + 2
                    stage = 2
                elif stage == 2 and time.monotonic() >= chunk_ready_at:
                    send(fixture + queries); stage = 3
                elif stage == 3 and '[Server] R6_COMMANDS_FINISHED' in text:
                    send(['stop']); stopped = True; stage = 4; deadline = time.monotonic() + 90
                if process.poll() is not None and not selector.get_map():
                    break
                if time.monotonic() > deadline:
                    timed_out = True
                    os.killpg(process.pid, signal.SIGTERM)
                    try:
                        process.wait(timeout=10)
                    except subprocess.TimeoutExpired:
                        os.killpg(process.pid, signal.SIGKILL)
                    break
            process.wait(timeout=10)
        finally:
            if process.poll() is None:
                os.killpg(process.pid, signal.SIGKILL); process.wait(timeout=10)
            selector.close(); process.stdin.close(); process.stdout.close()
    checks = LIFECYCLE['verify_exit'](text, process.returncode, stopped, timed_out, directory / 'world')
    checks['commands_finished'] = '[Server] R6_COMMANDS_FINISHED' in text
    checks['content_block_live'] = '[Server] R6_CONTENT_PRESENT' in text
    checks['three_live_nbt_queries'] = text.count('has the following block data:') == 3
    result = {'route': assembly['route'], 'command': assembly['command'], 'console_commands': all_commands, 'create': create, 'exit': process.returncode, 'timeout': timed_out, 'ready_seconds': ready_seconds, 'elapsed_seconds': time.monotonic() - start, 'assembly_sha256': digest(directory / 'assembly.json'), 'log_sha256': digest(directory / f'{label}.log'), 'checks': checks, 'passed': all(checks.values())}
    try:
        raw, semantic = inspect(directory / 'world')
        result['semantic'] = semantic
        (directory / f'{label}-nbt.json').write_text(json.dumps(raw, indent=2) + '\n')
        result['nbt_sha256'] = digest(directory / f'{label}-nbt.json')
    except Exception as error:
        result['inspection_error'] = repr(error)
        result['passed'] = False
    result['world_files'] = manifest(directory / 'world') if (directory / 'world').is_dir() else {}
    (directory / f'{label}.json').write_text(json.dumps(result, indent=2) + '\n')
    return result


def normalize_upgrade(value, legacy_data=None):
    if isinstance(value, str):
        return {'id': value, 'data': legacy_data or {}, 'patch': {}}
    if not isinstance(value, dict) or 'id' not in value:
        raise ValueError(f"Missing installed upgrade: {value!r}")
    patch = dict(value.get('components', {}))
    data = patch.pop('minecraft:custom_data', {})
    return {'id': value['id'], 'data': data, 'patch': patch}


def normalize_item(item):
    components = dict(item.get('components', {}))
    legacy = dict(item.get('tag', {}))
    pocket = None
    for key in ('computercraft:pocket_upgrade', 'computercraft:back_pocket_upgrade'):
        if key in components:
            if pocket is not None:
                raise ValueError('Ambiguous pocket back upgrade')
            pocket = normalize_upgrade(components.pop(key))
    if 'Upgrade' in legacy:
        pocket = normalize_upgrade(legacy.pop('Upgrade'), legacy.pop('UpgradeInfo', {}))
    custom = components.pop('minecraft:custom_data', {})
    damage = components.pop('minecraft:damage', legacy.pop('Damage', None))
    legacy.update(custom)
    result = {'slot': item['Slot'], 'id': item['id'], 'count': item.get('count', item.get('Count')), 'custom': legacy, 'patch': components}
    if damage is not None:
        result['damage'] = damage
    if pocket is not None:
        result['pocket'] = pocket
    return result


def inspect(world):
    chunk = NBT['chunk'](world)
    entities = {(be['x'], be['y'], be['z']): be for be in chunk['block_entities']}
    raw = {'data_version': NBT['level'](world)['Data']['DataVersion'], 'blocks': {name: NBT['block_at'](chunk, x, 80, 0) for name, x in POSITIONS.items()}, 'entities': {name: entities[(x, 80, 0)] for name, x in POSITIONS.items() if name != 'content'}}
    filler, chest, turtle = (raw['entities'][name] for name in ('filler', 'chest', 'turtle'))
    result = {'blocks': raw['blocks'], 'filler': {'id': filler['id'], 'owner': filler.get('ownerProfile'), 'settings': filler.get('peripheralData')}, 'chest': sorted((normalize_item(x) for x in chest.get('Items', [])), key=lambda x: x['slot']), 'turtle': {'id': turtle['id'], 'label': turtle.get('Label'), 'computer': turtle.get('ComputerId'), 'fuel': turtle.get('Fuel'), 'slot': turtle.get('Slot'), 'owner': turtle.get('Owner'), 'items': sorted((normalize_item(x) for x in turtle.get('Items', [])), key=lambda x: x['slot']), 'left': normalize_upgrade(turtle.get('LeftUpgrade'), turtle.get('LeftUpgradeNbt')), 'right': normalize_upgrade(turtle.get('RightUpgrade'), turtle.get('RightUpgradeNbt'))}}
    return raw, result


def validate(semantic, modern):
    """Independent fixture expectations, not just two potentially empty snapshots."""
    assert semantic['blocks']['filler']['Name'] == 'tweakium:creative_filler'
    assert semantic['blocks']['content']['Name'] == 'peripheralium:peripheralium_block'
    filler = semantic['filler']
    assert filler['owner'] == OWNER
    assert filler['settings']['mode'] == 'r6' and filler['settings']['limit'] == 37
    assert filler['settings']['enabled'] == 1 and filler['settings']['nested'] == {'text': 'retain', 'ratio': 1.25}
    if modern:
        assert filler['settings']['__patch_storage__'] == {'!minecraft:custom_name': {}, 'minecraft:damage': 7, 'minecraft:custom_data': {'token': 'r6'}}
    inventory = semantic['chest']
    assert [(x['slot'], x['id'], x['count']) for x in inventory] == [(0, 'peripheralium:peripheralium_dust', 17), (1, 'peripheralium:peripheralium_upgrade_template', 3), (2, 'computercraft:pocket_computer_normal', 1), (3, 'minecraft:diamond_pickaxe', 1), (4, 'minecraft:water_bucket', 1), (5, 'minecraft:lava_bucket', 1), (6, 'minecraft:diamond', 23)]
    assert inventory[2]['custom']['storedData'] == {'legacy': 'retained'}
    assert inventory[3]['damage'] == 5 and inventory[3]['custom']['storedData'] == {'legacy': 'retained'}
    if modern:
        assert inventory[3]['patch']['!minecraft:tool'] == {}
    turtle = semantic['turtle']
    assert (turtle['fuel'], turtle['slot'], turtle['computer'], turtle['label']) == (1234, 2, 42, 'R6 turtle')
    assert turtle['owner'] == {'UpperId': 4294967298, 'LowerId': 12884901892, 'Name': 'R6Owner'}
    assert [(x['id'], x['count']) for x in turtle['items']] == [('minecraft:diamond', 11), ('minecraft:water_bucket', 1)]
    for side, upgrade, expected_id in [('left', turtle['left'], 'computercraft:speaker'), ('right', turtle['right'], 'computercraft:speaker'), ('back', inventory[2]['pocket'], 'computercraft:speaker')]:
        assert upgrade['id'] == expected_id
        assert upgrade['data'] == {'ownerProfile': 'r6-' + side, 'setting': 37, 'storedData': {'mode': 'retained'}}
        if modern:
            assert upgrade['patch'] == {'!minecraft:custom_name': {}, 'minecraft:damage': 5}


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('assembly', type=Path)
    parser.add_argument('--label', required=True)
    parser.add_argument('--create', action='store_true')
    args = parser.parse_args()
    result = run(args.assembly.resolve(), args.label, args.create)
    print(json.dumps({key: result.get(key) for key in ('route', 'passed', 'exit', 'checks', 'inspection_error')}, indent=2))
    raise SystemExit(0 if result['passed'] else 1)
