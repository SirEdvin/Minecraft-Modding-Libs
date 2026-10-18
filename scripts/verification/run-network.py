#!/usr/bin/env python3
"""Bounded real two-Minecraft-client TCP fixture; never accepts EULA implicitly."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import shutil
import socket
import subprocess
import time

parser = argparse.ArgumentParser()
parser.add_argument('--server-source', type=Path, required=True)
parser.add_argument('--client-info', type=Path, required=True)
parser.add_argument('--output', type=Path, required=True)
parser.add_argument('--fixture', type=Path, required=True)
parser.add_argument('--java', type=Path, required=True)
parser.add_argument('--loader', choices=('fabric', 'forge'), default='fabric')
parser.add_argument('--eula-accepted', action='store_true', help='Requires an existing explicit operator acceptance record')
args = parser.parse_args()
if not args.eula_accepted:
    parser.error('Explicit scoped EULA acceptance is required')
output = args.output.resolve()
if shutil.disk_usage(output.parent).free < 256 * 1024 * 1024:
    raise RuntimeError('Insufficient scratch disk space for real-client execution (need at least 256 MiB)')
output.mkdir(parents=True, exist_ok=False)
server = output / 'server'
server.mkdir()
control = output / 'control'
control.mkdir()
# Dependencies only, never clone/mutate the source world or source configuration.
for item in args.server_source.iterdir():
    if item.name == 'libraries':
        (server/item.name).symlink_to(item.resolve(), target_is_directory=True)
    elif item.name == 'mods':
        (server/'mods').mkdir()
        for jar in item.glob('*.jar'):
            (server/'mods'/jar.name).symlink_to(jar.resolve())
    elif item.suffix == '.jar':
        (server/item.name).symlink_to(item.resolve())
shutil.copy2(args.fixture, server/'mods'/args.fixture.name)
with socket.socket() as sock:
    sock.bind(('127.0.0.1', 0))
    port = sock.getsockname()[1]
(server/'eula.txt').write_text('eula=true\n')
(server/'server.properties').write_text(f'server-ip=127.0.0.1\nserver-port={port}\nonline-mode=false\nenforce-secure-profile=false\nview-distance=2\nsimulation-distance=2\nspawn-protection=0\nmax-players=2\nlevel-type=minecraft:flat\ngenerate-structures=false\nallow-flight=true\n')
info = json.loads(args.client_info.read_text())
classpath = [p for p in info['runtime' if args.loader == 'fabric' else 'classpath'] if Path(p).is_file() and '/.gradle/loom-cache/minecraftMaven/' not in p]
if args.loader == 'fabric':
    classpath.append(str(Path.home()/'.gradle/caches/fabric-loom/26.1.2/minecraft-merged.jar'))
processes = []
commands = []
started = time.monotonic()
try:
    command = [str(args.java), '-Xms256M', '-Xmx1400M', f'-Dbroccolium.network.dir={control}', '--sun-misc-unsafe-memory-access=allow', '-cp', os.pathsep.join(classpath), 'net.fabricmc.loader.impl.launch.knot.KnotServer', 'nogui']
    if args.loader == 'forge':
        command = [str(args.java), '-Xms256M', '-Xmx1400M', f'-Dbroccolium.network.dir={control}', '--sun-misc-unsafe-memory-access=allow', '--enable-native-access=ALL-UNNAMED', '--add-opens=java.base/java.lang.invoke=ALL-UNNAMED', '-cp', os.pathsep.join(classpath), 'net.neoforged.fml.startup.Server', 'nogui']
    log = (output/'server.log').open('w')
    process = subprocess.Popen(command, cwd=server, stdout=log, stderr=subprocess.STDOUT, stdin=subprocess.PIPE, text=True)
    processes.append((process, log))
    commands.append(dict(role='server', command=command, cwd=str(server)))
    while time.monotonic()-started < 120:
        if process.poll() is not None:
            raise RuntimeError('Server exited before readiness')
        if 'Done (' in (output/'server.log').read_text(errors='replace'):
            break
        time.sleep(0.5)
    else:
        raise TimeoutError('No production server readiness within 120s')
    for name in ('R6Alice', 'R6Bob'):
        game = output/name
        game.mkdir()
        (game/'mods').mkdir()
        for jar in (server/'mods').glob('*.jar'):
            (game/'mods'/jar.name).symlink_to(jar.resolve())
        (game/'options.txt').write_text('renderDistance:2\nsimulationDistance:2\nmaxFps:30\nfullscreen:false\ncloudStatus:0\nparticles:2\nonboardAccessibility:false\npauseOnLostFocus:false\nsoundCategory_master:0.0\n')
        command = [str(args.java), '-Xms256M', '-Xmx1600M', '--enable-native-access=ALL-UNNAMED', '--sun-misc-unsafe-memory-access=allow', f'-Dbroccolium.network.dir={control}', '-cp', os.pathsep.join(classpath), 'net.fabricmc.loader.impl.launch.knot.KnotClient', '--username', name, '--version', '26.1.2', '--gameDir', str(game), '--assetsDir', str(Path.home()/'.gradle/caches/fabric-loom/assets'), '--assetIndex', '26.1.2-30', '--accessToken', '0', '--versionType', 'release', '--width', '640', '--height', '480', '--quickPlayMultiplayer', f'127.0.0.1:{port}']
        if args.loader == 'forge':
            command[command.index('net.fabricmc.loader.impl.launch.knot.KnotClient')] = 'net.neoforged.fml.startup.Client'
            command[command.index('--assetIndex') + 1] = '30'
            command[command.index('--assetsDir') + 1] = str(Path.home()/'.gradle/caches/neoformruntime/assets')
            command[command.index('--version') + 1] = '26.1.2.122'
            command[1:1] = ['--add-opens=java.base/java.lang.invoke=ALL-UNNAMED', '--add-exports=jdk.naming.dns/com.sun.jndi.dns=java.naming', f'-DlibraryDirectory={server / "libraries"}']
        log = (output/(name+'.log')).open('w')
        child = subprocess.Popen(command, cwd=game, stdout=log, stderr=subprocess.STDOUT)
        processes.append((child,log))
        commands.append(dict(role=name,command=command,cwd=str(game)))
    (output/'commands.json').write_text(json.dumps(commands,indent=2)+'\n')
    while time.monotonic()-started < 480:
        if (control/'failure.txt').exists():
            raise RuntimeError((control/'failure.txt').read_text())
        if (control/'result.json').exists():
            break
        exited = [(i,p.poll()) for i,(p,_) in enumerate(processes) if p.poll() is not None]
        if exited:
            raise RuntimeError(f'Process exited before acceptance: {exited}')
        time.sleep(0.5)
    else:
        raise TimeoutError('Real multiplayer fixture deadline 480s')
    for p,_ in processes:
        p.wait(timeout=40)
    result = json.loads((control/'result.json').read_text())
    expected_phases = ['concurrent', 'wrong_id', 'slot_high', 'slot_negative', 'button', 'forged_count', 'stale_menu', 'distance', 'removed', 'spectator', 'position', 'player_identity']
    assert [row['phase'] for row in result['phases']] == expected_phases
    assert all(row['received'] == 2 and row['serverThread'] == 'Server thread' for row in result['phases'])
    assert 'PASS valid=1 rejected=16 thread=Server thread' in (control/'codec-checks.log').read_text()
    for name in ('R6Alice', 'R6Bob'):
        assert 'decode thread=Render thread' in (control/(name+'-decode.log')).read_text()
    result['native_codec_checks'] = {'valid': 1, 'rejected': 16, 'buffer_lifetime_verified': True}
    result['actual_client_registry_decode_threads'] = ['Render thread', 'Render thread']
    result.update(process_exits=[p.returncode for p,_ in processes],elapsed_seconds=time.monotonic()-started,fixture_sha256=hashlib.sha256(args.fixture.read_bytes()).hexdigest(),transport='two actual full Minecraft graphical client processes and dedicated TCP server; loopback offline identities',eula_authorization='t_b52730bf dashboard: I confirm, set EULA true for this project too')
    assert result['passed'] and all(code == 0 for code in result['process_exits']), result
    (output/'receipt.json').write_text(json.dumps(result,indent=2)+'\n')
    print(json.dumps(result,indent=2))
finally:
    for p,log in processes:
        if p.poll() is None:
            p.terminate()
            try: p.wait(timeout=15)
            except subprocess.TimeoutExpired:
                p.kill()
                p.wait()
        log.close()
    (output/'process-exits.json').write_text(json.dumps([p.returncode for p,_ in processes])+'\n')
