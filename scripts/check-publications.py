#!/usr/bin/env python3
"""Check local Maven publication contracts without publishing anything.

Run after ./gradlew generatePomFileForMavenPublication
    generateMetadataFileForMavenPublication --no-daemon.
"""
import argparse
import hashlib
import json
import re
from pathlib import Path
import xml.etree.ElementTree as ET
from zipfile import ZipFile

ROOT = Path(__file__).resolve().parents[1]
LIBRARIES = ("broccolium", "testiarium", "tweakium", "peripheralium")
VERSIONS = ("1.20.1", "1.21.1", "26.1.2")
MAJORS = {"1.20.1": 61, "1.21.1": 65, "26.1.2": 69}
LOADERS = ("common", "fabric", "forge")
NS = {"m": "http://maven.apache.org/POM/4.0.0"}


def check_jar(archive, artifact, minecraft, loader, library, version):
    names = set(archive.namelist())
    assert len(names) == len(archive.namelist()), (artifact, "duplicate ZIP entries")
    forbidden = ("/testmod/", "/test/", "/fixture/", "/cct/", "computer/tests/")
    assert not any(any(part in name for part in forbidden) for name in names), (artifact, "private fixture leak")
    assert not any(name.startswith("net/minecraft/") for name in names), (artifact, "Minecraft classes bundled")
    classes = [name for name in names if name.endswith(".class")]
    assert classes, (artifact, "empty binary")
    for name in classes:
        major = int.from_bytes(archive.read(name)[6:8], "big")
        assert major == MAJORS[minecraft], (artifact, name, major)
    if loader == "fabric":
        assert "fabric.mod.json" in names, artifact
        assert not {"META-INF/mods.toml", "META-INF/neoforge.mods.toml"} & names, artifact
        mod = json.loads(archive.read("fabric.mod.json"))
        assert mod["id"] == library and mod["version"] == version, (artifact, mod)
        if "accessWidener" in mod:
            widener = mod["accessWidener"]
            assert widener in names, (artifact, widener)
            namespace = "official" if minecraft == "26.1.2" else "intermediary"
            assert archive.read(widener).decode().splitlines()[0].split()[-1] == namespace, (artifact, widener)
        for nested in mod.get("jars", []):
            assert nested["file"] in names, (artifact, "missing nested dependency", nested)
        for entries in mod.get("entrypoints", {}).values():
            for entry in entries:
                value = entry if isinstance(entry, str) else entry["value"]
                assert value.split("::")[0].replace(".", "/") + ".class" in names, (artifact, "missing entrypoint", value)
    elif loader == "forge":
        expected = "META-INF/mods.toml" if minecraft == "1.20.1" else "META-INF/neoforge.mods.toml"
        assert expected in names and "fabric.mod.json" not in names, artifact
        metadata = archive.read(expected).decode()
        assert "${" not in metadata, (artifact, "unexpanded metadata")
        assert re.search(r'modId\s*=\s*"' + library + '"', metadata), artifact
        if library != "testiarium":
            assert "META-INF/accesstransformer.cfg" in names, (artifact, "missing AT")
        if "META-INF/jarjar/metadata.json" in names:
            for nested in json.loads(archive.read("META-INF/jarjar/metadata.json"))["jars"]:
                assert nested["path"] in names, (artifact, "missing jarjar dependency", nested)
    for name in names:
        if name.endswith(".mixins.json"):
            mixin = json.loads(archive.read(name))
            for key in ("mixins", "client", "server"):
                for value in mixin.get(key, []):
                    path = (mixin["package"] + "." + value).replace(".", "/") + ".class"
                    assert path in names, (artifact, "missing Mixin", path)
        if name.endswith((".json", ".toml", ".accesswidener", ".cfg")):
            assert b"${" not in archive.read(name), (artifact, "unexpanded resource", name)


def check_publications(root=ROOT, versions=VERSIONS):
    checked = 0
    for library in LIBRARIES:
        for minecraft in versions:
            common_sources = set()
            for loader in LOADERS:
                part = "core" if library == "testiarium" and loader == "common" else loader
                artifact = f"{library}-{part}-{minecraft}"
                branch = root / "projects" / library
                if loader != "common":
                    branch /= loader
                build = branch / "versions" / minecraft / "build"
                publication = build / "publications" / "maven"
                metadata = json.loads((publication / "module.json").read_text())
                component = metadata["component"]
                assert component["group"] == "site.siredvin", component
                assert component["module"] == artifact, component
                version = component["version"]
                pom = ET.parse(publication / "pom-default.xml").getroot()
                assert pom.findtext("m:groupId", namespaces=NS) == "site.siredvin", artifact
                assert pom.findtext("m:artifactId", namespaces=NS) == artifact, artifact
                dependencies = {
                    (dep.findtext("m:groupId", namespaces=NS), dep.findtext("m:artifactId", namespaces=NS))
                    for dep in pom.findall("m:dependencies/m:dependency", NS)
                }
                assert all(group and module for group, module in dependencies), (artifact, dependencies)
                variants = {variant["name"]: variant for variant in metadata["variants"]}
                runtime = [
                    variant for variant in variants.values()
                    if variant["attributes"].get("org.gradle.usage") == "java-runtime"
                    and variant["attributes"].get("org.gradle.category") == "library"
                    and not variant.get("capabilities")
                ]
                assert len(runtime) == 1, (artifact, "ambiguous main runtime variant", runtime)
                runtime_dependencies = {
                    (dep["group"], dep["module"])
                    for dep in runtime[0].get("dependencies", [])
                }
                for dep in dependencies | runtime_dependencies:
                    assert dep[0] is not None and not dep[0].startswith("site.siredvin."), (artifact, "internal identity leaked", dep)
                for variant in variants.values():
                    for dep in variant.get("dependencies", []):
                        assert not dep["group"].startswith("site.siredvin."), (artifact, variant["name"], dep)
                        if minecraft != "26.1.2" and dep["group"] == "org.jetbrains.kotlin" and dep["module"].startswith("kotlin-stdlib"):
                            assert dep["version"]["requires"] in ("2.0.0", "2.1.0"), (artifact, "target stdlib leaked", dep)
                    assert "testFixtures" not in variant["name"], (artifact, "private fixture variant")
                if loader == "common":
                    required = {"tweakium": ("broccolium",), "peripheralium": ("broccolium", "tweakium")}.get(library, ())
                    for dependency in required:
                        coordinate = ("site.siredvin", f"{dependency}-common-{minecraft}")
                        assert coordinate in dependencies, (artifact, "missing POM dependency", coordinate)
                        assert coordinate in runtime_dependencies, (artifact, "missing module dependency", coordinate)
                assert "sourcesElements" in variants, (artifact, "missing source publication")
                for variant in variants.values():
                    for file in variant.get("files", []):
                        binary = build / "libs" / file["url"]
                        assert binary.is_file(), (artifact, "missing artifact", file["url"])
                        payload = binary.read_bytes()
                        assert len(payload) == file["size"], (artifact, "artifact size", file)
                        for algorithm in ("sha256", "sha512"):
                            assert hashlib.new(algorithm, payload).hexdigest() == file[algorithm], (artifact, "artifact digest", file)
                with ZipFile(build / "libs" / f"{artifact}-{version}-sources.jar") as archive:
                    sources = {name for name in archive.namelist() if name.endswith((".kt", ".java"))}
                    assert sources, (artifact, "empty sources")
                    if loader == "common":
                        common_sources = sources
                    else:
                        assert common_sources <= sources, (artifact, "missing common sources", common_sources - sources)
                with ZipFile(build / "libs" / f"{artifact}-{version}.jar") as archive:
                    check_jar(archive, artifact, minecraft, loader, library, version)
                if library == "testiarium":
                    for feature, suffix in (("testMod", "test-mod"), ("cctTestMod", "cct-test-mod")):
                        for usage in ("Api", "Runtime"):
                            feature_variant = variants[f"{feature}{usage}Elements"]
                            capabilities = feature_variant["capabilities"]
                            expected = {"group": "site.siredvin", "name": f"testiarium-{part}-{suffix}", "version": version}
                            assert expected in capabilities, (artifact, capabilities)
                            feature_dependencies = feature_variant.get("dependencies", [])
                            assert any(dep["group"] == "site.siredvin" and dep["module"] == artifact
                                       and not dep.get("requestedCapabilities") for dep in feature_dependencies), (artifact, feature, "missing core dependency")
                            if feature == "cctTestMod":
                                assert any(any(cap["name"] == f"testiarium-{part}-test-mod"
                                               for cap in dep.get("requestedCapabilities", []))
                                           for dep in feature_dependencies), (artifact, feature, "missing test-mod dependency")
                        assert f"{feature}SourcesElements" in variants, (artifact, feature, "missing feature sources")
                checked += 1
    assert checked == len(LIBRARIES) * len(versions) * len(LOADERS)
    print(f"Verified {checked} publications: dependencies, capabilities, sources, artifacts and loader/JVM identity")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, default=ROOT)
    parser.add_argument("--versions", nargs="+", choices=VERSIONS, default=VERSIONS)
    args = parser.parse_args()
    check_publications(args.root.resolve(), args.versions)
