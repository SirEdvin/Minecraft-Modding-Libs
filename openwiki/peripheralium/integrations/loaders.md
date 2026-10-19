---
type: integration
title: Peripheralium loader integration
description: Platform-first registration, correct metadata and test-only fixture ownership.
tags: [peripheralium, loaders, publication]
sources:
  - id: openwiki-source-46cc0af9773fbef0cab9dc18
    resource: repo://docs/verification/minecraft-26-1-R4.md
  - id: openwiki-source-d6a84e4b4f1771cf32182cf7
    resource: repo://docs/verification/minecraft-26-1-R5.md
  - id: openwiki-source-851986a2438c3c75fd1c9bb7
    resource: repo://docs/verification/minecraft-26-1-R7-final-integration.md
  - id: openwiki-source-cdb7aafc87320c7c89edfb0b
    resource: repo://projects/peripheralium/fabric/versions/26.1.2/src/main/resources/fabric.mod.json
  - id: openwiki-source-eae6966e6bd879d2f745efae
    resource: repo://projects/peripheralium/forge/versions/26.1.2/src/main/resources/META-INF/neoforge.mods.toml
generated: { by: "hermes", at: "2026-10-19T00:45:42.798Z" }
verified:
  - by: openwiki/0.7.1
    at: 2026-10-19T13:48:57.965Z
---

# Peripheralium loader integration

## Registration and metadata

Fabric configures the platform before common registration and forces dependency initialization through established adapters. Forge-family supplies deferred block/item/tab/criterion registries and attaches them to its loader bus. Forge means legacy Forge only on1.20.1;1.21.1/26.1.2 use NeoForge. Keep loader imports out of common content and read matching version overrides.

Target keyed construction, loader schema and Java25 metadata are in26.1.2 overrides. Check actual packaged metadata/JVM69/dependencies: an old permissive metadata bound is not evidence old Java can load modern bytecode. Modern content intentionally omits legacy blend/smelting.

## Fixture and publication isolation

Peripheralium owns behavior/recipe fixtures. Native Tweakium runners load actual Peripheralium common/loader mod outputs plus matching test features; reverse integration stays test-only. Main JAR scans compare exact compiled fixture identities, and all POM/GMM variants are checked for test/viewer leakage.

[R4](../../../docs/verification/minecraft-26-1-R4.md) verifies both target adapters and deterministic data. [R5](../../../docs/verification/minecraft-26-1-R5.md) establishes full publication/consumer/active-state checks; [R7 integration](../../../docs/verification/minecraft-26-1-R7-final-integration.md) refreshes final publication/graphical/process proof. Development viewers follow approved EMI-old/JEI-target policy, no active REI dependency; they are not publication runtime requirements.

See [content](../content.md), [build](../../build/stonecutter-publication.md) and [verification](../../testing/verification.md).
