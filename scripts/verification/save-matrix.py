#!/usr/bin/env python3
"""Execute the R6 same-loader copied-save matrix using prepared assemblies.

Input root/servers contains six explicit hash-checked production assemblies.
Four 1.20.1/1.21.1 source fixtures must already have create-v2 receipts.
Original worlds are read-only inputs: only exclusive new copies are opened.
Receipts persist after every run. Re-running refuses existing destinations.
"""
import argparse
import json
from pathlib import Path
import runpy
import shutil
import tarfile

HERE = Path(__file__).resolve().parent
PROBE = runpy.run_path(str(HERE / 'save-fixture.py'))


def save(path, data):
    path.write_text(json.dumps(data, indent=2) + '\n')


def assembly_copy(template, destination):
    destination.mkdir(parents=True, exist_ok=False)
    for name in ('mods', 'libraries', 'versions', 'launcher.jar', 'fabric-server-launcher.properties', 'assembly.json', 'eula.txt', 'server.properties'):
        source = template / name
        if source.is_dir():
            shutil.copytree(source, destination / name)
        elif source.is_file():
            shutil.copy2(source, destination / name)
    # Never inherit enabled remote administration or an ephemeral generated secret.
    properties = template / 'server.properties'
    lines = properties.read_text().splitlines()
    lines = [line for line in lines if not line.startswith(('management-server-', 'rcon.'))]
    lines += ['management-server-enabled=false', 'enable-rcon=false', 'server-ip=127.0.0.1', 'server-port=0']
    (destination / 'server.properties').write_text('\n'.join(lines) + '\n')


def execute(root, assemblies=None):
    assemblies = assemblies or root / 'servers'
    root.mkdir(parents=True, exist_ok=True)
    sources = {}
    receipt = {'status': 'INCOMPLETE', 'source_worlds': {}, 'backups': {}, 'runs': [], 'comparisons': []}
    receipt_path = root / 'save-matrix.json'
    if receipt_path.exists():
        raise FileExistsError(receipt_path)
    save(receipt_path, receipt)

    def backup(name, source):
        destination = root / 'backups' / name
        hashes = PROBE['preserve'](source, destination)
        archive = root / 'backups' / (name + '.tar.gz')
        with tarfile.open(archive, 'x:gz') as out:
            out.add(destination, arcname='world')
        result = {'source': str(source), 'directory': str(destination), 'files': hashes, 'archive': str(archive), 'sha256': PROBE['digest'](archive)}
        receipt['backups'][name] = result
        save(receipt_path, receipt)
        return destination

    def recheck_backups():
        for data in receipt['backups'].values():
            if PROBE['manifest'](Path(data['directory'])) != data['files'] or PROBE['digest'](Path(data['archive'])) != data['sha256']:
                raise ValueError('Backup changed')
        for data in receipt['source_worlds'].values():
            if PROBE['manifest'](Path(data['directory'])) != data['files']:
                raise ValueError('Original source world changed')

    def copied_run(name, version, loader, source, expected):
        destination = root / 'runs' / name
        assembly_copy(assemblies / (version + '-' + loader), destination)
        input_files = PROBE['preserve'](source, destination / 'world')
        save(destination / 'input-world.json', {'source': str(source), 'files': input_files})
        for label in ('open', 'restart'):
            result = PROBE['run'](destination, label)
            receipt['runs'].append({'name': name, 'label': label, 'receipt': str(destination / (label + '.json')), 'runtime_passed': result['passed'], 'exit': result['exit']})
            save(receipt_path, receipt)
            if not result['passed']:
                raise ValueError(f'Runtime/inspection failed: {name}/{label}')
            same = result['semantic'] == expected
            comparison = {'name': name, 'label': label, 'semantic_equal': same, 'data_version': json.loads((destination / (label + '-nbt.json')).read_text())['data_version']}
            receipt['comparisons'].append(comparison)
            save(receipt_path, receipt)
            recheck_backups()
            if not same:
                save(destination / (label + '-expected.json'), expected)
                raise ValueError(f'Semantic state changed: {name}/{label}; expected and actual retained')
        return destination / 'world'

    try:
        (root / 'backups').mkdir(exist_ok=False)
        for version in ('1.20.1', '1.21.1'):
            for loader in ('fabric', 'forge'):
                name = version + '-' + loader
                source = assemblies / name
                result = json.loads((source / 'create-v2.json').read_text())
                if not result['passed'] or PROBE['manifest'](source / 'world') != result['world_files']:
                    raise ValueError(f'Creation receipt or current source mismatch: {name}')
                raw, semantic = PROBE['inspect'](source / 'world')
                PROBE['validate'](semantic, version != '1.20.1')
                sources[name] = {'backup': backup(name, source / 'world'), 'semantic': semantic}
                receipt['source_worlds'][name] = {'directory': str(source / 'world'), 'files': result['world_files'], 'expected_state_validated': True, 'creation_receipt': str(source / 'create-v2.json'), 'semantic': semantic, 'data_version': raw['data_version']}
                save(receipt_path, receipt)
        for loader in ('fabric', 'forge'):
            for version in ('1.20.1', '1.21.1'):
                name = version + '-' + loader
                source = sources[name]
                copied_run(name + '-same', version, loader, source['backup'], source['semantic'])
            modern = sources['1.21.1-' + loader]
            copied_run(loader + '-direct-target', '26.1.2', loader, modern['backup'], modern['semantic'])
            old = sources['1.20.1-' + loader]
            intermediate = copied_run(loader + '-staged-modern', '1.21.1', loader, old['backup'], old['semantic'])
            intermediate_backup = backup(loader + '-staged-modern', intermediate)
            copied_run(loader + '-staged-target', '26.1.2', loader, intermediate_backup, old['semantic'])
        recheck_backups()
        receipt['status'] = 'PASS'
        receipt['originals_and_backups_unchanged'] = True
    except Exception as error:
        receipt['error'] = repr(error)
        raise
    finally:
        save(receipt_path, receipt)
    return receipt


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('root', type=Path)
    parser.add_argument('--assemblies', type=Path)
    args = parser.parse_args()
    result = execute(args.root.resolve(), args.assemblies.resolve() if args.assemblies else None)
    print(json.dumps({'status': result['status'], 'runs': len(result['runs']), 'comparisons': len(result['comparisons']), 'backups': len(result['backups'])}, indent=2))
