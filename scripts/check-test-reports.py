#!/usr/bin/env python3
"""Require fresh, executed versioned JUnit reports after a root cleanTest build."""
import argparse
import json
from pathlib import Path
import xml.etree.ElementTree as ET

VERSIONS = ("1.20.1", "1.21.1", "26.1.2")
EXPECTED = {
    *(f"broccolium/{loader}/{version}" for loader in ("common", "fabric", "forge") for version in VERSIONS),
    *(f"tweakium/{loader}/{version}" for loader in ("common", "fabric") for version in VERSIONS),
    "tweakium/forge/26.1.2",
    *(f"testiarium/common/{version}" for version in VERSIONS),
}


def check_reports(root, since):
    rows = []
    seen = set()
    for library, loader, version in sorted(key.split("/") for key in EXPECTED):
        branch = root / "projects" / library
        if loader != "common":
            branch /= loader
        directory = branch / "versions" / version / "build/test-results/test"
        reports = sorted(directory.glob("TEST-*.xml"))
        assert reports, (directory, "missing owning unit suite")
        count = 0
        for path in reports:
            assert path.stat().st_mtime_ns >= since.stat().st_mtime_ns, (path, "stale report")
            data = path.read_bytes()
            assert b"<!DOCTYPE" not in data.upper(), (path, "DOCTYPE forbidden")
            suite = ET.fromstring(data)
            cases = suite.findall(".//testcase")
            assert int(suite.attrib["tests"]) == len(cases), (path, "count mismatch")
            for attribute, tag in (("failures", "failure"), ("errors", "error"), ("skipped", "skipped")):
                actual = len(suite.findall(".//" + tag))
                assert int(suite.get(attribute, "0")) == actual == 0, (path, attribute, actual)
            for case in cases:
                identity = (library, loader, version, case.get("classname"), case.get("name"))
                assert identity not in seen, (path, "duplicate case", identity)
                seen.add(identity)
            count += len(cases)
            rows.append({"path": str(path.relative_to(root)), "tests": len(cases), "failures": 0, "errors": 0, "skipped": 0})
        assert count > 0, (directory, "empty owning unit suite")
    return {"nodes": len(EXPECTED), "reports": len(rows), "tests": len(seen), "failures": 0, "errors": 0, "skipped": 0, "rows": rows}


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, default=Path(__file__).resolve().parents[1])
    parser.add_argument("--since", type=Path, required=True, help="Marker created before cleanTest build")
    parser.add_argument("--output", type=Path)
    args = parser.parse_args()
    receipt = check_reports(args.root.resolve(), args.since)
    if args.output:
        args.output.write_text(json.dumps(receipt, indent=2) + "\n")
    print(json.dumps({key: value for key, value in receipt.items() if key != "rows"}))
