#!/usr/bin/env python3
"""Preflight/read back staged Maven bytes; never upload or overwrite anything."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import subprocess
import tempfile

BASE = "https://mvn.siredvin.site/minecraft/"


def classify(status, expected, actual):
    if status == 404:
        return "absent"
    if status != 200:
        raise ValueError(f"Uncertain HTTP response: {status}")
    if expected != actual:
        raise ValueError("Existing bytes differ; publication requires a decision")
    return "verified-identical"


def inspect(model, stage, project, minecraft, target, verify, output):
    selected = [p for p in model if p["project"].split(":")[1] == project
                and p["project"].split(":")[-1] == minecraft
                and (target == "all" or ("common" if len(p["project"].split(":")) == 3
                     else p["project"].split(":")[2]) == target)]
    assert len(selected) == (3 if target == "all" else 1), selected
    receipt = {"source_sha": os.environ.get("GITHUB_SHA") or subprocess.check_output(
        ["git", "rev-parse", "HEAD"], text=True).strip(),
        "run_url": os.environ.get("GITHUB_SERVER_URL", "https://github.com") + "/"
        + os.environ.get("GITHUB_REPOSITORY", "SirEdvin/Minecraft-Modding-Libs")
        + "/actions/runs/" + os.environ.get("GITHUB_RUN_ID", "local"), "publications": []}
    tasks = []
    errors = []
    try:
        for p in selected:
            assert p["group"] == "site.siredvin" and not p["version"].endswith("-SNAPSHOT"), p
            destination = [r for r in p["repositories"] if r["name"] == "SirEdvin"]
            assert len(destination) == 1 and destination[0]["url"].rstrip("/") == BASE.rstrip("/"), destination
            relative = f"{p['group'].replace('.', '/')}/{p['artifact']}/{p['version']}/"
            prefix = f"{p['artifact']}-{p['version']}"
            names = {prefix + ".pom", prefix + ".module"}
            names.update(prefix + ("-" + a["classifier"] if a["classifier"] else "")
                         + "." + a["extension"] for a in p["artifacts"])
            record = {"coordinate": f"{p['group']}:{p['artifact']}:{p['version']}",
                      "project": p["project"], "files": []}
            receipt["publications"].append(record)
            module = json.loads((stage / relative / (prefix + ".module")).read_text())
            assert all(module["component"][key] == value for key, value in
                       (("group", p["group"]), ("module", p["artifact"]), ("version", p["version"]))), module["component"]
            for variant in module["variants"]:
                for file in variant.get("files", []):
                    assert file["url"] in names, file
                    payload = (stage / relative / file["url"]).read_bytes()
                    assert hashlib.sha256(payload).hexdigest() == file["sha256"], file
            for name in sorted(names):
                local = stage / relative / name
                expected = hashlib.sha256(local.read_bytes()).hexdigest()
                entry = {"url": BASE + relative + name, "sha256": expected,
                         "kind": "metadata" if name.endswith((".pom", ".module")) else
                         "fixture-source" if "-mod-sources.jar" in name else
                         "fixture" if "-mod.jar" in name else
                         "source" if name.endswith("-sources.jar") else "main"}
                record["files"].append(entry)
                with tempfile.TemporaryDirectory() as temp:
                    downloaded = Path(temp) / "artifact"
                    try:
                        result = subprocess.run(["curl", "--silent", "--show-error", "--location", "--max-time", "60",
                                                 "--output", str(downloaded), "--write-out", "%{http_code}", entry["url"]],
                                                check=True, capture_output=True, text=True)
                    except subprocess.CalledProcessError as error:
                        entry["status"] = "transport-error"
                        entry["curl_exit_code"] = error.returncode
                        errors.append(f"{entry['url']}: curl failed with exit {error.returncode}")
                        continue
                    entry["http_status"] = int(result.stdout)
                    actual = hashlib.sha256(downloaded.read_bytes()).hexdigest()
                    try:
                        entry["status"] = classify(entry["http_status"], expected, actual)
                    except ValueError as error:
                        entry["status"] = "conflict-or-uncertain"
                        errors.append(f"{entry['url']}: {error}")
                if verify and entry["status"] != "verified-identical":
                    errors.append(f"Unverified published file: {entry['url']}")
            states = {f["status"] for f in record["files"]}
            if states == {"absent"}:
                record["status"] = "ready"
                tasks.append(p["project"] + ":publishAllPublicationsToSirEdvinRepository")
            elif states == {"verified-identical"}:
                record["status"] = "verified-identical"
            else:
                record["status"] = "partial-or-conflicting"
                errors.append(f"Partial publication requires a decision: {record['coordinate']}")
    finally:
        receipt["errors"] = errors
        output.parent.mkdir(parents=True, exist_ok=True)
        output.write_text(json.dumps(receipt, indent=2) + "\n")
    if errors:
        raise ValueError("\n".join(errors))
    return tasks


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--project", required=True, choices=("broccolium", "testiarium", "tweakium", "peripheralium"))
    parser.add_argument("--minecraft", required=True, choices=("1.20.1", "1.21.1", "26.1.2"))
    parser.add_argument("--target", default="all", choices=("all", "common", "fabric", "forge"))
    parser.add_argument("--verify", action="store_true")
    parser.add_argument("--model", type=Path, default=Path("build/verification/maven-model.json"))
    parser.add_argument("--stage", type=Path, default=Path("build/maven-stage"))
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    tasks = inspect(json.loads(args.model.read_text()), args.stage, args.project, args.minecraft,
                    args.target, args.verify, args.output)
    for task in tasks:
        print(task)
