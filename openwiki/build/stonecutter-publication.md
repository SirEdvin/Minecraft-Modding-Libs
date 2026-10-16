---
type: architecture
title: Stonecutter source ownership and publications
description: Actual Gradle subproject graph, effective version overrides, loader identity, Java targets, publication validation and source-mutation hazards.
tags: [build, stonecutter, versions, publication, gradle]
sources:
  - id: openwiki-source-5ffc733a0005c17b6fb5b9ea
    resource: repo://.github/workflows/publish-maven.yml
  - id: openwiki-source-2a9daaac1604f238ef4c63fb
    resource: repo://build.gradle.kts
  - id: openwiki-source-b68c69ba290f0fee793ec69b
    resource: repo://gradle.properties
  - id: openwiki-source-b60ea7c86eca8a8a8a1b36ac
    resource: repo://projects/broccolium/forge/build.gradle.kts
  - id: openwiki-source-3461c43c395091f547a438ae
    resource: repo://projects/broccolium/stonecutter.gradle.kts
  - id: openwiki-source-0e18fe1e8a3fb313dbba48ae
    resource: repo://projects/peripheralium/build.gradle.kts
  - id: openwiki-source-4ddbd0907e10e62bd7b67a8b
    resource: repo://projects/testiarium/build.gradle.kts
  - id: openwiki-source-b21bfc1ffdd367853ce150ec
    resource: repo://projects/tweakium/build.gradle.kts
  - id: openwiki-source-18505126b4fc17c6fe763eea
    resource: repo://projects/typed-peripheral-api/build.gradle.kts
  - id: openwiki-source-23775c3de52f3ab95a13cb8b
    resource: repo://README.md
  - id: openwiki-source-58b7ee33c55d27584a4162db
    resource: repo://scripts/check-publications.py
  - id: openwiki-source-e620d7484b72a53c7fa812cd
    resource: repo://settings.gradle.kts
generated: { by: "hermes", at: "2026-10-16T19:56:35.545Z" }
verified:
  - by: openwiki/0.7.1
    at: 2026-10-16T19:56:35.545Z
---

# Stonecutter source ownership and publications

## Actual graph and canonical source

settings.gradle.kts includes broccolium, tweakium, peripheralium, testiarium and typed-peripheral-api under projects/. Stonecutter 0.9.8 creates 1.20.1 and 1.21.1 nodes plus fabric and forge branches for the first four; typed-peripheral-api is an independent Node-backed Gradle project, not another Minecraft matrix tree.

Common leaf tasks are :<library>:<version>:<task>, loader tasks :<library>:<loader>:<version>:<task>. The common controller has an empty branch ID mapped to common. forge + version >= 1.21 sets neoforge; earlier forge sets legacyforge. Java targets are 17 for 1.20.1 and 21 for 1.21.1, including explicit toolchains in Forge build scripts. The active controller/VCS state is 1.20.1. This is not the old projects/<library>-core layout.

Shared source is projects/<library>/src or projects/<library>/<loader>/src. Full-file versions/<version>/src overrides take precedence. Root build exclusions suppress matching shared Java, Kotlin and resource files even when that version is active. Do not edit build/generated/stonecutter. Switching is repository-wide work: README directs switching from the root so all four separate controllers move together, then restoring 1.20.1 before committing.

## Dependencies and build boundaries

Tweakium common depends on matching-version Broccolium; Peripheralium common depends on matching-version Broccolium and Tweakium. Testiarium's testMod and cctTestMod features are test-harness dependencies, not a production dependency on ComputerCraft in every library. See [harness ownership](../testiarium/harness.md).

Fabric scripts select version-specific loader/API/runtime bundles and use common outputs. Legacy Forge enables recompilation, registers common and loader source sets in its mod, incorporates common classes/resources into its JAR, and expands loader metadata. The 1.21.1 forge branch applies NeoForge conventions. Root configuration wires access-transformer resources from the effective source set and makes createMinecraftArtifacts depend on stonecutterGenerate; ordering is part of correctness, not an optional speed tweak.

## Publication identity

gradle.properties centralizes the four library versions and group. Build internals give leaves unique groups derived from project paths to avoid identical Gradle identities; MavenPublication resets groupId to site.siredvin. Historical forge artifact names remain forge even for NeoForge. Common artifacts use <library>-common-<minecraft>, except Testiarium uses testiarium-core-<minecraft>.

Root source JAR wiring includes common sources in loader source publications and enables withSourcesJar after Kotlin configuration. The read-only scripts/check-publications.py verifies generated POM/module identities, required common dependency edges, absence of leaked internal identities, source/artifact existence, common-source inclusion in loader source JARs, loader metadata, bytecode major versions, and Testiarium feature capabilities. It expects the whole configured library/version/loader product, not merely the currently selected node. Generate artifacts before running it; it is not a network publication command.

## Commands and side effects

The checked-in verification workflow is:

    ./gradlew cleanTest build generatePomFileForMavenPublication generateMetadataFileForMavenPublication --no-daemon
    python3 scripts/check-publications.py
    ./gradlew stonecutterSwitchTo1.21.1 --no-daemon
    ./gradlew cleanTest build generatePomFileForMavenPublication generateMetadataFileForMavenPublication --no-daemon
    python3 scripts/check-publications.py
    ./gradlew stonecutterSwitchTo1.20.1 --no-daemon

These are implementation verification commands, not wiki-only checks. Switching may transform shared source/controller state. Datagen writes generated resources. Root assemble also reaches TypeScript compilation, whose outputs are generated beside source. Inspect task wiring before using a broad build as a documentation linter. Root formatting is ratcheted against origin/multiversion and excludes generated source; leaf formatting targets are cleared so shared source is checked once.

typed-peripheral-api now connects testContracts to check. The contract task depends on npmInstall and mustRunAfter compileTypeScript, because a combined build writes declaration files beside the contract inputs. Ordering avoids overlapping task reads/writes without forcing emission for a standalone contract check.

The coverage acceptance ran both active-state builds and all 24 publication checks, then restored 1.20.1. A standalone exact-source clone with the same ratchet ref was necessary for Spotless/JGit linked-worktree discovery; it did not bypass formatting. Fresh JUnit counting excludes obsolete unversioned output trees. [The coverage matrix](../../docs/module-test-coverage.md) records source-hash checks, artifact isolation and exact runtime/JUnit totals.

Existing Maven CI publishes on library-prefixed release tags or explicit workflow dispatch. It validates tag library version against gradle.properties, maps 1.20/1.21 to exact nodes, requires repository credentials, and chooses common/fabric/forge or all. Do not push matching tags or invoke publish for local validation. No publishing workflow, scheduled automation or release was run or enabled by this documentation task.

See [verification workflows](../testing/verification.md) for focused tests and fresh-report guards, [typed API packaging](../typed-peripheral-api/contracts.md) for Node artifacts, and [quickstart](../quickstart.md) for ownership routing.
