#!/usr/bin/env python3
"""Replay CI's native matrix; retain per-invocation XML, identities and exits.

Run in an exact-source checkout with JAVA_HOME and LOG_DIR set. Minecraft and
its real report guards remain the authority; this does not synthesize reports.
"""
import argparse
import hashlib
import json
import os
from pathlib import Path
import signal
import subprocess
import time
import xml.etree.ElementTree as ET


def routes():
    for version in ("1.20.1", "1.21.1", "26.1.2"):
        for loader in ("fabric", "forge"):
            if loader == "fabric" or version == "26.1.2":
                yield "broccolium", loader, version, "runStorageGameTest", [], "storage-gametest.xml"
            standalone = "runServer" if loader == "fabric" else "runGameTestServer"
            yield "testiarium", loader, version, standalone, [], "gametest.xml"
            cct = "runCctGameTest" if loader == "fabric" else "runGameTestServer"
            flags = [] if loader == "fabric" else ["-Ptestiarium.cct"]
            yield "testiarium", loader, version, cct, flags, "cct-gametest.xml"
            peripheral = "runPeripheralGameTest" if loader == "fabric" else "runGameTestServer"
            yield "tweakium", loader, version, peripheral, [], "peripheral-gametest.xml"
            yield "testiarium", loader, version, "runClientGameTest", [], "client-gametest.xml"


def inspect_report(report, started, destination, storage):
    if not report.is_file() or report.stat().st_mtime_ns < started:
        raise ValueError(f"Missing or stale report: {report}")
    data = report.read_bytes()
    destination.write_bytes(data)
    if b"<!DOCTYPE" in data.upper():
        raise ValueError("DOCTYPE forbidden")
    root = ET.fromstring(data)
    cases = root.findall(".//testcase")
    outcomes = {"tests": len(cases), "failures": 0, "errors": 0, "skipped": 0}
    identities = []
    for case in cases:
        entry: dict[str, object] = dict(case.attrib)
        for tag in ("failure", "error", "skipped"):
            elements = case.findall(tag)
            outcomes[{"failure": "failures", "error": "errors", "skipped": "skipped"}[tag]] += len(elements)
            if elements:
                entry[tag] = [dict(element.attrib) for element in elements]
        identities.append(entry)
    for key, actual in outcomes.items():
        if int(root.get(key, "0")) != actual:
            raise ValueError(f"Declared {key} mismatch: {root.attrib} versus {outcomes}")
    names = [case.get("name") for case in cases]
    if not cases or len(set(names)) != len(names):
        raise ValueError("Empty or duplicate discovery")
    if outcomes["failures"] or outcomes["errors"] or outcomes["tests"] <= outcomes["skipped"]:
        raise ValueError(f"Nonpassing report: {outcomes}")
    if storage and outcomes["skipped"]:
        raise ValueError("Storage skips forbidden")
    return {"sha256": hashlib.sha256(data).hexdigest(), **outcomes, "cases": identities}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--only", type=int, nargs="*", help="One-based route indices for an explicit replay")
    args = parser.parse_args()
    root = Path(__file__).resolve().parents[2]
    output = args.output.resolve()
    output.mkdir(parents=True, exist_ok=True)
    manifest = []
    selected = list(enumerate(routes(), 1))
    if args.only is not None:
        if not set(args.only) <= {index for index, _ in selected}:
            parser.error("Unknown route index")
        selected = [(index, route) for index, route in selected if index in args.only]
    for index, (library, loader, version, task, flags, filename) in selected:
        label = f"{index:02d}-{library}-{loader}-{version}-{filename.removesuffix('.xml')}"
        directory = output / label
        directory.mkdir(exist_ok=True)
        command = [str(root / "scripts/verification/run-gametest.sh"), f":{library}:{loader}:{version}:{task}", *flags]
        report = root / f"projects/{library}/{loader}/versions/{version}/build/test-results/{filename}"
        entry = {"index": index, "command": command, "cwd": str(root), "report": str(report), "started_ns": time.time_ns()}
        print(f"START {label}", flush=True)
        with (directory / "runner.log").open("w") as log:
            process = subprocess.Popen(command, cwd=root, stdout=log, stderr=subprocess.STDOUT, start_new_session=True)
            try:
                entry["exit"] = process.wait(timeout=960)
            except subprocess.TimeoutExpired:
                entry["exit"] = 124
                entry["timeout"] = True
            finally:
                # Only this invocation's process group, including abandoned JVMs.
                try:
                    os.killpg(process.pid, signal.SIGTERM)
                    time.sleep(1)
                    os.killpg(process.pid, signal.SIGKILL)
                except ProcessLookupError:
                    pass
        entry["finished_ns"] = time.time_ns()
        try:
            entry["result"] = inspect_report(report, entry["started_ns"], directory / filename, library == "broccolium")
            entry["accepted"] = entry["exit"] == 0
        except (ValueError, OSError, ET.ParseError) as error:
            entry["accepted"] = False
            entry["diagnostic"] = str(error)
        (directory / "receipt.json").write_text(json.dumps(entry, indent=2) + "\n")
        manifest.append(entry)
        (output / "commands.json").write_text(json.dumps(manifest, indent=2) + "\n")
        print(f"END {label} exit={entry['exit']} accepted={entry['accepted']}", flush=True)
    if not manifest or not all(entry["accepted"] for entry in manifest):
        raise SystemExit(1)
    print(f"Accepted {len(manifest)} actual native routes", flush=True)


if __name__ == "__main__":
    main()
