#!/usr/bin/env python3
"""Real old-node graphical join against an isolated production dedicated server.

Client uses Gradle's unchanged development launch classpath (recorded), not a
claim that release JARs are byte-identical to named development class outputs.
Run under xvfb-run. Never overwrites source assemblies or source worlds.
"""
import argparse
import ctypes
import ctypes.util
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import socket
import subprocess
import time
from PIL import ImageGrab


def close_window(window):
    """Send the native WM_DELETE_WINDOW message to this task's GLFW window."""
    xlib = ctypes.CDLL(ctypes.util.find_library('X11'))
    xlib.XOpenDisplay.argtypes = [ctypes.c_char_p]
    xlib.XOpenDisplay.restype = ctypes.c_void_p
    display = xlib.XOpenDisplay(None)
    if not display:
        raise RuntimeError('No X display')
    xlib.XInternAtom.argtypes = [ctypes.c_void_p, ctypes.c_char_p, ctypes.c_int]
    xlib.XInternAtom.restype = ctypes.c_ulong
    class MessageData(ctypes.Union):
        _fields_ = [('b', ctypes.c_char * 20), ('s', ctypes.c_short * 10), ('l', ctypes.c_long * 5)]
    class Message(ctypes.Structure):
        _fields_ = [('type', ctypes.c_int), ('serial', ctypes.c_ulong), ('send_event', ctypes.c_int), ('display', ctypes.c_void_p), ('window', ctypes.c_ulong), ('message_type', ctypes.c_ulong), ('format', ctypes.c_int), ('data', MessageData)]
    class Event(ctypes.Union):
        _fields_ = [('message', Message), ('padding', ctypes.c_long * 24)]
    event = Event()
    event.message.type = 33
    event.message.display = display
    event.message.window = window
    event.message.message_type = xlib.XInternAtom(display, b'WM_PROTOCOLS', 0)
    event.message.format = 32
    event.message.data.l[0] = xlib.XInternAtom(display, b'WM_DELETE_WINDOW', 0)
    xlib.XSendEvent.argtypes = [ctypes.c_void_p, ctypes.c_ulong, ctypes.c_int, ctypes.c_long, ctypes.POINTER(Event)]
    xlib.XFlush.argtypes = [ctypes.c_void_p]
    xlib.XCloseDisplay.argtypes = [ctypes.c_void_p]
    if not xlib.XSendEvent(display, window, 0, 0, ctypes.byref(event)):
        raise RuntimeError('Window close event rejected')
    xlib.XFlush(display)
    xlib.XCloseDisplay(display)


def open_inventory(window):
    """Press the real E binding in this fixture's GLFW window via XTest."""
    xlib = ctypes.CDLL(ctypes.util.find_library('X11'))
    xtest = ctypes.CDLL(ctypes.util.find_library('Xtst'))
    xlib.XOpenDisplay.argtypes = [ctypes.c_char_p]
    xlib.XOpenDisplay.restype = ctypes.c_void_p
    display = xlib.XOpenDisplay(None)
    if not display:
        raise RuntimeError('No X display')
    xlib.XSetInputFocus.argtypes = [ctypes.c_void_p, ctypes.c_ulong, ctypes.c_int, ctypes.c_ulong]
    xlib.XKeysymToKeycode.argtypes = [ctypes.c_void_p, ctypes.c_ulong]
    xlib.XKeysymToKeycode.restype = ctypes.c_ubyte
    xlib.XFlush.argtypes = [ctypes.c_void_p]
    xlib.XCloseDisplay.argtypes = [ctypes.c_void_p]
    xtest.XTestFakeKeyEvent.argtypes = [ctypes.c_void_p, ctypes.c_uint, ctypes.c_int, ctypes.c_ulong]
    try:
        xlib.XSetInputFocus(display, window, 1, 0)
        code = xlib.XKeysymToKeycode(display, ord('e'))
        if not code:
            raise RuntimeError('No E key binding in X keymap')
        if not xtest.XTestFakeKeyEvent(display, code, 1, 0) or not xtest.XTestFakeKeyEvent(display, code, 0, 0):
            raise RuntimeError('Native inventory key event rejected')
        xlib.XFlush(display)
    finally:
        xlib.XCloseDisplay(display)


def validate_emi(log):
    if '[EMI] Initialized plugin from computercraft' not in log or '[EMI] Reloaded EMI in ' not in log:
        raise ValueError('No actual completed EMI/ComputerCraft plugin reload; a joined world alone is not EMI acceptance')


def inspect_emi_search(window, output):
    """Native mouse/key input only; screenshots still require visual inspection."""
    geometry = subprocess.check_output(['xwininfo', '-id', hex(window)], text=True)
    def value(label):
        match = re.search(rf'{label}:\s+(-?\d+)', geometry)
        if not match:
            raise ValueError(f'Missing native window geometry: {label}')
        return int(match.group(1))
    left, top = value('Absolute upper-left X'), value('Absolute upper-left Y')
    width, height = value('Width'), value('Height')
    xlib = ctypes.CDLL(ctypes.util.find_library('X11'))
    xtest = ctypes.CDLL(ctypes.util.find_library('Xtst'))
    xlib.XOpenDisplay.argtypes = [ctypes.c_char_p]
    xlib.XOpenDisplay.restype = ctypes.c_void_p
    display = xlib.XOpenDisplay(None)
    if not display:
        raise RuntimeError('No X display')
    xlib.XKeysymToKeycode.argtypes = [ctypes.c_void_p, ctypes.c_ulong]
    xlib.XKeysymToKeycode.restype = ctypes.c_ubyte
    xlib.XFlush.argtypes = [ctypes.c_void_p]
    xlib.XCloseDisplay.argtypes = [ctypes.c_void_p]
    xtest.XTestFakeMotionEvent.argtypes = [ctypes.c_void_p, ctypes.c_int, ctypes.c_int, ctypes.c_int, ctypes.c_ulong]
    xtest.XTestFakeButtonEvent.argtypes = [ctypes.c_void_p, ctypes.c_uint, ctypes.c_int, ctypes.c_ulong]
    xtest.XTestFakeKeyEvent.argtypes = [ctypes.c_void_p, ctypes.c_uint, ctypes.c_int, ctypes.c_ulong]
    def click(x, y):
        assert xtest.XTestFakeMotionEvent(display, -1, left + x, top + y, 0)
        for down in (1, 0):
            assert xtest.XTestFakeButtonEvent(display, 1, down, 0)
        xlib.XFlush(display)
        time.sleep(0.5)
    try:
        # Existing fixed 1100x700 launch, GUI auto scale2: EMI's bottom search.
        if (width, height) != (1100, 700):
            raise ValueError(f'Unexpected native search geometry: {width}x{height}')
        click(width // 2, height - 25)
        for char in 'peripheralium':
            key = xlib.XKeysymToKeycode(display, ord(char))
            if not key:
                raise RuntimeError(f'Unmapped native key: {char}')
            for down in (1, 0):
                assert xtest.XTestFakeKeyEvent(display, key, down, 0)
            xlib.XFlush(display)
            time.sleep(0.05)
        time.sleep(3)
        ImageGrab.grab().save(output / 'emi-search.png')
        # The first result may be a compatibility item without a recipe.
        # This capture proves neither recipe availability nor transfer support.
        click(width - 310, 58)
        time.sleep(3)
        ImageGrab.grab().save(output / 'emi-recipe.png')
    finally:
        xlib.XCloseDisplay(display)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--server-source', type=Path, required=True)
    parser.add_argument('--client-info', type=Path, required=True)
    parser.add_argument('--java', type=Path, required=True)
    parser.add_argument('--output', type=Path, required=True)
    parser.add_argument('--inventory-screenshot', action='store_true', help='Open the native inventory; screenshot requires separate visual inspection')
    parser.add_argument('--require-emi', action='store_true', help='Require actual EMI and CC plugin reload evidence')
    parser.add_argument('--emi-search', action='store_true', help='Type a native search and click its first result; requires separate screenshot inspection')
    parser.add_argument('--eula-accepted', action='store_true')
    args = parser.parse_args()
    if not args.eula_accepted:
        parser.error('Requires explicit recorded project EULA acceptance')
    if args.emi_search and not (args.inventory_screenshot and args.require_emi):
        parser.error('--emi-search requires --inventory-screenshot and --require-emi')
    if not args.java.is_file():
        parser.error('Selected Java launcher does not exist')
    output = args.output.resolve()
    output.mkdir(parents=True, exist_ok=False)
    server = output / 'server'
    game = output / 'client'
    server.mkdir()
    game.mkdir()
    assembly = json.loads((args.server_source / 'assembly.json').read_text())
    mods = {}
    for item in args.server_source.iterdir():
        if item.name == 'libraries' or item.suffix == '.jar':
            (server / item.name).symlink_to(item.resolve(), target_is_directory=item.is_dir())
    (server / 'mods').mkdir()
    for jar in (args.server_source / 'mods').glob('*.jar'):
        actual = hashlib.sha256(jar.read_bytes()).hexdigest()
        assert actual == assembly['mods'][jar.name]
        mods[jar.name] = actual
        (server / 'mods' / jar.name).symlink_to(jar.resolve())
    with socket.socket() as sock:
        sock.bind(('127.0.0.1', 0))
        port = sock.getsockname()[1]
    (server / 'eula.txt').write_text('eula=true\n')
    (server / 'server.properties').write_text(f'server-ip=127.0.0.1\nserver-port={port}\nonline-mode=false\nenforce-secure-profile=false\nview-distance=2\nsimulation-distance=5\nspawn-protection=0\nmax-players=1\ngenerate-structures=false\n')
    (game / 'options.txt').write_text('renderDistance:3\nsimulationDistance:5\nmaxFps:30\nfullscreen:false\nonboardAccessibility:false\npauseOnLostFocus:false\nsoundCategory_master:0.0\n')
    info = json.loads(args.client_info.read_text())
    jvm = [arg for arg in info['jvm'] if not arg.startswith(('-Xmx', '-Xms'))]
    client_environment = os.environ.copy()
    mod_classes = info.get('modClasses')
    if assembly['route'] == '1.20.1-forge' and not mod_classes:
        # Forge47's pinned CommonLaunchHandler reads MOD_CLASSES, not the
        # modern fml.modFolders property emitted by this launch-info snapshot.
        mod_classes = next(arg.split('=', 1)[1] for arg in jvm if arg.startswith('-Dfml.modFolders='))
    if mod_classes:
        client_environment['MOD_CLASSES'] = mod_classes
    client_command = [str(args.java), '-Xms256M', '-Xmx1800M', *jvm, '-cp', os.pathsep.join(info['classpath']), info['main'], *info['args'], '--username', 'R7Legacy', '--accessToken', '0', '--width', '1100', '--height', '700', '--quickPlayMultiplayer', f'127.0.0.1:{port}']
    commands = {'server': assembly['command'], 'client': client_command, 'client_MOD_CLASSES': mod_classes, 'client_model': 'Gradle development launch with explicit native legacy mod-discovery environment; production-source binding required separately'}
    (output / 'commands.json').write_text(json.dumps(commands, indent=2) + '\n')
    processes = []
    started = time.monotonic()
    try:
        def launch(command, cwd, log, environment=None):
            handle = log.open('w')
            process = subprocess.Popen(command, cwd=cwd, stdout=handle, stderr=subprocess.STDOUT, stdin=subprocess.PIPE, text=True, env=environment)
            processes.append((process, handle))
            return process
        server_process = launch(assembly['command'], server, output / 'server.log')
        def await_text(file, expected, limit):
            deadline = time.monotonic() + limit
            while expected not in file.read_text(errors='replace'):
                if any(p.poll() is not None for p, _ in processes):
                    raise RuntimeError(f'Process exited before {expected}')
                if time.monotonic() > deadline:
                    raise TimeoutError(f'No {expected} within {limit}s')
                time.sleep(0.5)
        await_text(output / 'server.log', 'Done (', 120)
        client = launch(client_command, game, output / 'client.log', client_environment)
        await_text(output / 'server.log', 'R7Legacy joined the game', 150)
        assert server_process.stdin is not None
        server_process.stdin.write('gamemode creative R7Legacy\nforceload add -16 -16 16 16\n')
        server_process.stdin.flush()
        chunk_deadline = time.monotonic() + 30
        while 'R7_CHUNK_READY' not in (output / 'server.log').read_text(errors='replace'):
            if time.monotonic() > chunk_deadline:
                raise TimeoutError('Task-owned view chunk never became loaded')
            server_process.stdin.write('execute if loaded 0 69 3 run say R7_CHUNK_READY\n')
            server_process.stdin.flush()
            time.sleep(0.5)
        server_process.stdin.write('fill -4 68 -2 4 68 6 minecraft:stone\nfill -4 69 -2 4 73 6 minecraft:air\nsetblock 0 69 3 peripheralium:peripheralium_block\nsetblock -2 69 3 tweakium:creative_filler\ngive R7Legacy peripheralium:peripheralium_dust\ntime set day\nweather clear\ntp R7Legacy 0.5 69 0.5 0 0\nexecute if block 0 69 3 peripheralium:peripheralium_block run say R7_SCENE_READY\n')
        server_process.stdin.flush()
        await_text(output / 'server.log', 'R7_SCENE_READY', 30)
        time.sleep(15)
        ImageGrab.grab().save(output / 'joined.png')
        windows = subprocess.check_output(['xwininfo', '-root', '-tree'], text=True)
        (output / 'windows.txt').write_text(windows)
        match = re.search(r'(0x[0-9a-f]+) "Minecraft[^\n]*', windows)
        if not match:
            raise RuntimeError('No actual Minecraft GLFW window')
        if args.inventory_screenshot:
            open_inventory(int(match.group(1), 16))
            time.sleep(5)
            ImageGrab.grab().save(output / 'inventory.png')
        if args.emi_search:
            inspect_emi_search(int(match.group(1), 16), output)
        close_window(int(match.group(1), 16))
        client.wait(timeout=40)
        await_text(output / 'server.log', 'R7Legacy left the game', 40)
        server_process.stdin.write('forceload remove -16 -16 16 16\nstop\n')
        server_process.stdin.flush()
        try:
            server_process.wait(timeout=40)
        except subprocess.TimeoutExpired:
            # Preserve native thread evidence before a bounded shutdown fails.
            with (output / 'shutdown-threads.txt').open('w') as threads:
                subprocess.run([str(args.java.parent / 'jcmd'), str(server_process.pid), 'Thread.print'], stdout=threads, stderr=subprocess.STDOUT, timeout=15, check=False)
            server_process.wait(timeout=50)
        assert client.returncode == server_process.returncode == 0
        text = (output / 'server.log').read_text()
        assert 'Saving worlds' in text and 'All dimensions are saved' in text
        if args.require_emi:
            validate_emi((output / 'client.log').read_text(errors='replace'))
        receipt = {'route': assembly['route'], 'passed': True, 'mods': mods, 'client_launch_manifest_sha256': hashlib.sha256(args.client_info.read_bytes()).hexdigest(), 'screenshot_sha256': hashlib.sha256((output / 'joined.png').read_bytes()).hexdigest(), 'exits': [server_process.returncode, client.returncode], 'actual_join_leave': True, 'elapsed_seconds': time.monotonic() - started, 'client_model': commands['client_model']}
        if args.inventory_screenshot:
            receipt['inventory_screenshot_sha256'] = hashlib.sha256((output / 'inventory.png').read_bytes()).hexdigest()
            receipt['inventory_visual_acceptance'] = 'Separate inspection required; capture alone is not an assertion'
        receipt['emi_plugin_reload_verified'] = args.require_emi
        if args.emi_search:
            receipt['emi_native_input_screenshots'] = {name: hashlib.sha256((output / name).read_bytes()).hexdigest() for name in ('emi-search.png', 'emi-recipe.png')}
            receipt['emi_search_visual_acceptance'] = 'Separate inspection required; native input and capture alone are not assertions'
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
