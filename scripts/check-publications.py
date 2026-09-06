#!/usr/bin/env python3
"""Check local Maven publication contracts without publishing anything.

Run after ./gradlew generatePomFileForMavenPublication
    generateMetadataFileForMavenPublication --no-daemon.
"""
import json
from pathlib import Path
import xml.etree.ElementTree as ET
from zipfile import ZipFile

ROOT = Path(__file__).resolve().parents[1]
LIBRARIES = ("broccolium", "testiarium", "tweakium", "peripheralium")
VERSIONS = ("1.20.1", "1.21.1")
LOADERS = ("common", "fabric", "forge")
NS = {"m": "http://maven.apache.org/POM/4.0.0"}


def check_publications():
    checked = 0
    for library in LIBRARIES:
        for minecraft in VERSIONS:
            common_sources = set()
            for loader in LOADERS:
                part = "core" if library == "testiarium" and loader == "common" else loader
                artifact = f"{library}-{part}-{minecraft}"
                branch = ROOT / "projects" / library
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
                if loader == "common":
                    required = {"tweakium": ("broccolium",), "peripheralium": ("broccolium", "tweakium")}.get(library, ())
                    for dependency in required:
                        coordinate = ("site.siredvin", f"{dependency}-common-{minecraft}")
                        assert coordinate in dependencies, (artifact, "missing POM dependency", coordinate)
                        assert coordinate in runtime_dependencies, (artifact, "missing module dependency", coordinate)
                assert "sourcesElements" in variants, (artifact, "missing source publication")
                for variant in variants.values():
                    for file in variant.get("files", []):
                        assert (build / "libs" / file["url"]).is_file(), (artifact, "missing artifact", file["url"])
                with ZipFile(build / "libs" / f"{artifact}-{version}-sources.jar") as archive:
                    sources = {name for name in archive.namelist() if name.endswith((".kt", ".java"))}
                    assert sources, (artifact, "empty sources")
                    if loader == "common":
                        common_sources = sources
                    else:
                        assert common_sources <= sources, (artifact, "missing common sources", common_sources - sources)
                with ZipFile(build / "libs" / f"{artifact}-{version}.jar") as archive:
                    names = set(archive.namelist())
                    if loader == "fabric":
                        assert "fabric.mod.json" in names, artifact
                        assert not {"META-INF/mods.toml", "META-INF/neoforge.mods.toml"} & names, artifact
                    elif loader == "forge":
                        expected = "META-INF/mods.toml" if minecraft == "1.20.1" else "META-INF/neoforge.mods.toml"
                        assert expected in names and "fabric.mod.json" not in names, artifact
                    for name in names:
                        if name.endswith(".class"):
                            major = int.from_bytes(archive.read(name)[6:8], "big")
                            assert major == (61 if minecraft == "1.20.1" else 65), (artifact, name, major)
                if library == "testiarium":
                    for feature, suffix in (("testMod", "test-mod"), ("cctTestMod", "cct-test-mod")):
                        for usage in ("Api", "Runtime"):
                            capabilities = variants[f"{feature}{usage}Elements"]["capabilities"]
                            expected = {"group": "site.siredvin", "name": f"testiarium-{part}-{suffix}", "version": version}
                            assert expected in capabilities, (artifact, capabilities)
                checked += 1
    assert checked == len(LIBRARIES) * len(VERSIONS) * len(LOADERS)
    print(f"Verified {checked} publications: dependencies, capabilities, sources, artifacts and loader/JVM identity")


if __name__ == "__main__":
    check_publications()
