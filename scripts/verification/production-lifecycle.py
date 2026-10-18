#!/usr/bin/env python3
"""Observe a real isolated server's ready -> console stop -> saved exit.

The assembly must already contain an authorized eula=true and assembly.json
with an explicit command list. Never accepts the EULA or installs dependencies.
"""
import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import selectors
import signal
import subprocess
import time

READY = re.compile(r'Done \([^\n]*\)! For help, type ["\']help["\']')


def verify_exit(text, returncode, stopped, timed_out, world):
    """Fail closed: a zero exit, GameTest finish or kill is not a lifecycle pass."""
    checks = {
        "ready": bool(READY.search(text)),
        "console_stop_sent": stopped,
        "natural_zero_exit": returncode == 0 and not timed_out,
        "stopping_server": "Stopping server" in text,
        "saving_players": "Saving players" in text,
        "saving_worlds": "Saving worlds" in text,
        "chunks_saved": "All dimensions are saved" in text,
        "level_dat": (world / "level.dat").is_file(),
        "region_files": any(world.glob("region/*.mca")) or any(
            world.glob("dimensions/*/*/region/*.mca")
        ),
        "not_gametest": "Running GameTests" not in text and "test server" not in text.lower(),
    }
    return checks


def run(directory, startup_timeout=240, shutdown_timeout=90, label="lifecycle"):
    directory = directory.resolve()
    assembly = json.loads((directory / "assembly.json").read_text())
    command = assembly["command"]
    if not isinstance(command, list) or not command or not all(isinstance(x, str) for x in command):
        raise ValueError("assembly.command must be an explicit nonempty string list")
    if not re.search(r"(?m)^eula=true\s*$", (directory / "eula.txt").read_text()):
        raise ValueError("Existing explicit EULA authorization is required")
    for name, expected in assembly["mods"].items():
        actual = hashlib.sha256((directory / "mods" / name).read_bytes()).hexdigest()
        if actual != expected:
            raise ValueError(f"Assembly artifact hash mismatch: {name}")
    log_path = directory / f"{label}.log"
    start = time.monotonic()
    stopped = False
    timed_out = False
    ready_seconds = None
    deadline = start + startup_timeout
    with log_path.open("wb") as log:
        process = subprocess.Popen(
            command, cwd=directory, stdin=subprocess.PIPE, stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT, start_new_session=True,
        )
        assert process.stdout is not None and process.stdin is not None
        selector = selectors.DefaultSelector()
        selector.register(process.stdout.fileno(), selectors.EVENT_READ)
        output = bytearray()
        try:
            while True:
                for key, _ in selector.select(timeout=0.25):
                    data = os.read(key.fd, 65536)
                    if data:
                        log.write(data)
                        log.flush()
                        output.extend(data)
                    else:
                        selector.unregister(key.fileobj)
                if not stopped and READY.search(output.decode("utf-8", errors="replace")):
                    process.stdin.write(b"stop\n")
                    process.stdin.flush()
                    stopped = True
                    ready_seconds = time.monotonic() - start
                    deadline = time.monotonic() + shutdown_timeout
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
                os.killpg(process.pid, signal.SIGKILL)
                process.wait(timeout=10)
            selector.close()
            process.stdout.close()
            process.stdin.close()
    text = log_path.read_text(errors="replace")
    checks = verify_exit(text, process.returncode, stopped, timed_out, directory / "world")
    receipt = {
        "route": assembly["route"], "command": command,
        "assembly_sha256": hashlib.sha256((directory / "assembly.json").read_bytes()).hexdigest(),
        "log_sha256": hashlib.sha256(log_path.read_bytes()).hexdigest(),
        "exit": process.returncode, "timeout": timed_out, "ready_seconds": ready_seconds,
        "elapsed_seconds": time.monotonic() - start,
        "checks": checks, "passed": all(checks.values()),
        "saved_files": {
            str(p.relative_to(directory)): hashlib.sha256(p.read_bytes()).hexdigest()
            for p in (directory / "world").rglob("*")
            if p.is_file() and (p.suffix == ".mca" or p.name in ("level.dat", "level.dat_old"))
        },
    }
    (directory / f"{label}.json").write_text(json.dumps(receipt, indent=2) + "\n")
    return receipt


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("assembly", type=Path)
    parser.add_argument("--startup-timeout", type=float, default=240)
    parser.add_argument("--shutdown-timeout", type=float, default=90)
    parser.add_argument("--label", default="lifecycle")
    args = parser.parse_args()
    result = run(args.assembly, args.startup_timeout, args.shutdown_timeout, args.label)
    print(json.dumps(result, indent=2))
    raise SystemExit(0 if result["passed"] else 1)
