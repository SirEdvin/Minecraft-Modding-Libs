#!/usr/bin/env python3
"""R7 target visual proof: actual title/integrated world/production TCP join/reload/rejoin.

Must be run under xvfb-run with an external deadline. Requires explicit scoped
EULA acceptance. Every new run refuses to overwrite an existing evidence path.
"""
import argparse
import hashlib
import json
import os
from pathlib import Path
import shutil
import socket
import subprocess
import time


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--server-source', type=Path, required=True)
    parser.add_argument('--client-info', type=Path, required=True)
    parser.add_argument('--fixture', type=Path, required=True)
    parser.add_argument('--java', type=Path, required=True)
    parser.add_argument('--output', type=Path, required=True)
    parser.add_argument('--loader', choices=('fabric', 'forge'), required=True)
    parser.add_argument('--extra-mod', type=Path, action='append', default=[])
    parser.add_argument('--eula-accepted', action='store_true')
    args = parser.parse_args()
    if not args.eula_accepted:
        parser.error('Requires recorded explicit scoped Minecraft EULA acceptance')
    output = args.output.resolve()
    output.mkdir(parents=True, exist_ok=False)
    control = output / 'control'
    control.mkdir()
    server = output / 'server'
    game = output / 'client'
    server.mkdir()
    game.mkdir()
    for item in args.server_source.iterdir():
        if item.name == 'libraries' or item.suffix == '.jar':
            (server / item.name).symlink_to(item.resolve(), target_is_directory=item.is_dir())
    for directory in (server, game):
        (directory / 'mods').mkdir()
        for jar in (args.server_source / 'mods').glob('*.jar'):
            (directory / 'mods' / jar.name).symlink_to(jar.resolve())
        (directory / 'mods' / args.fixture.name).symlink_to(args.fixture.resolve())
    # Optional UI mods are intentionally physically client-only.
    for jar in args.extra_mod:
        (game / 'mods' / jar.name).symlink_to(jar.resolve())
    mods = {p.name: hashlib.sha256(p.read_bytes()).hexdigest() for p in (game / 'mods').glob('*.jar')}
    with socket.socket() as sock:
        sock.bind(('127.0.0.1', 0))
        port = sock.getsockname()[1]
    (server / 'eula.txt').write_text('eula=true\n')
    (server / 'server.properties').write_text(f'server-ip=127.0.0.1\nserver-port={port}\nonline-mode=false\nenforce-secure-profile=false\nview-distance=2\nsimulation-distance=2\nspawn-protection=0\nmax-players=1\nlevel-type=minecraft:flat\ngenerate-structures=false\nallow-flight=true\n')
    (game / 'options.txt').write_text('renderDistance:3\nsimulationDistance:2\nmaxFps:30\nfullscreen:false\ncloudStatus:0\nparticles:2\nonboardAccessibility:false\npauseOnLostFocus:false\nsoundCategory_master:0.0\n')
    info = json.loads(args.client_info.read_text())
    paths = info['runtime' if args.loader == 'fabric' else 'classpath']
    classpath = [p for p in paths if Path(p).is_file() and '/.gradle/loom-cache/minecraftMaven/' not in p]
    if args.loader == 'fabric':
        classpath.append(str(Path.home() / '.gradle/caches/fabric-loom/26.1.2/minecraft-merged.jar'))
    base = [str(args.java), '-Xms256M', '-Xmx1800M', '--enable-native-access=ALL-UNNAMED', '--sun-misc-unsafe-memory-access=allow', f'-Dtweakium.client.dir={control}', f'-Dtweakium.client.route={output.name}', f'-Dtweakium.client.address=127.0.0.1:{port}']
    if args.loader == 'forge':
        base += ['--add-opens=java.base/java.lang.invoke=ALL-UNNAMED', '--add-exports=jdk.naming.dns/com.sun.jndi.dns=java.naming', f'-DlibraryDirectory={server / "libraries"}']
    base += ['-cp', os.pathsep.join(classpath)]
    launcher = 'net.fabricmc.loader.impl.launch.knot.Knot' if args.loader == 'fabric' else 'net.neoforged.fml.startup.'
    server_command = base + [launcher + ('Server'), 'nogui']
    asset = Path.home() / ('.gradle/caches/fabric-loom/assets' if args.loader == 'fabric' else '.gradle/caches/neoformruntime/assets')
    client_command = base + [launcher + 'Client', '--username', 'R7Viewer', '--version', '26.1.2', '--gameDir', str(game), '--assetsDir', str(asset), '--assetIndex', '26.1.2-30' if args.loader == 'fabric' else '30', '--accessToken', '0', '--versionType', 'release', '--width', '1100', '--height', '700']
    (output / 'commands.json').write_text(json.dumps({'server': server_command, 'client': client_command}, indent=2) + '\n')
    processes = []
    started = time.monotonic()
    try:
        def launch(command, cwd, log):
            handle = log.open('w')
            process = subprocess.Popen(command, cwd=cwd, stdout=handle, stderr=subprocess.STDOUT, stdin=subprocess.PIPE, text=True)
            processes.append((process, handle))
            return process
        server_process = launch(server_command, server, output / 'server.log')
        while 'Done (' not in (output / 'server.log').read_text(errors='replace'):
            if server_process.poll() is not None or time.monotonic() - started > 120:
                raise RuntimeError('No natural dedicated-server ready signal')
            time.sleep(0.5)
        client = launch(client_command, game, output / 'client.log')
        while not (control / 'result.json').exists():
            if (control / 'failure.txt').exists():
                raise RuntimeError((control / 'failure.txt').read_text())
            if client.poll() is not None or server_process.poll() is not None:
                raise RuntimeError('Process exited before visual acceptance')
            if time.monotonic() - started > 420:
                raise TimeoutError('Visual workflow exceeded 420s')
            time.sleep(0.5)
        client.wait(timeout=40)
        server_process.stdin.write('stop\n')
        server_process.stdin.flush()
        server_process.wait(timeout=40)
        receipt = json.loads((control / 'result.json').read_text())
        shots = list((control / 'screenshots').glob('*.png'))
        assert receipt['passed'] and len(shots) == 12
        assert client.returncode == server_process.returncode == 0
        receipt.update(mods=mods, screenshots={p.name: hashlib.sha256(p.read_bytes()).hexdigest() for p in shots}, exits=[server_process.returncode, client.returncode], elapsed_seconds=time.monotonic() - started, eula_authorization='t_b52730bf explicit dashboard project acceptance; disposable fixture only')
        (output / 'receipt.json').write_text(json.dumps(receipt, indent=2) + '\n')
        print(json.dumps(receipt, indent=2))
    finally:
        for process, handle in processes:
            if process.poll() is None:
                process.terminate()
                try:
                    process.wait(timeout=15)
                except subprocess.TimeoutExpired:
                    process.kill()
                    process.wait()
            handle.close()
        (output / 'process-exits.json').write_text(json.dumps([p.returncode for p, _ in processes]) + '\n')


if __name__ == '__main__':
    main()
