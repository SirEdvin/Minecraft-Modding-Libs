---
type: quickstart
title: Minecraft Modding Libs ownership and task map
description: Current three-version graph, module ownership and final evidence routes.
tags: [quickstart, ownership, minecraft]
verified:
  - by: openwiki/0.7.1
    at: 2026-10-19T00:45:42.798Z
sources:
  - id: openwiki-source-2a9daaac1604f238ef4c63fb
    resource: repo://build.gradle.kts
  - id: openwiki-source-d0f34221b5d3b91db6e90b7f
    resource: repo://docs/minecraft-26-1-verification.md
  - id: openwiki-source-cc02d3f8883805a8e9de0ccf
    resource: repo://docs/module-test-coverage.md
  - id: openwiki-source-8e467af86822c4e36a35b699
    resource: repo://gradle/gametest-reports.gradle.kts
  - id: openwiki-source-e620d7484b72a53c7fa812cd
    resource: repo://settings.gradle.kts
generated: { by: "hermes", at: "2026-10-19T00:45:42.798Z" }
---

# Minecraft Modding Libs ownership and task map

## Effective source first

Four libraries share Stonecutter common/Fabric/forge trees; typed-peripheral-api stays independent. Maintained pairs are1.20.1 Forge/Fabric (JVM17),1.21.1 NeoForge/Fabric (JVM21), exact26.1.2 NeoForge/Fabric (JVM25). The historical forge path is not Forge on modern versions. Canonical/VCS source remains1.20.1. Read matching full-file `versions/<version>/src` overrides, not processed outputs. [Build/publication](build/stonecutter-publication.md) explains target-only non-remapping tooling and preserved old runtimes.

## Ownership routes

- Broccolium: [platform/lifecycle](broccolium/modules/platform-base.md), [storage](broccolium/modules/storage.md), [lookup/drops](broccolium/modules/lookup-drops.md), [data](broccolium/modules/data.md), [loaders](broccolium/integrations/loaders.md).
- Tweakium: [peripherals](tweakium/modules/peripheral.md), [plugins/operations](tweakium/modules/plugins.md), [fake players](tweakium/modules/player.md), [platform/data](tweakium/modules/platform-data.md), [representation/rendering](tweakium/modules/representation-rml.md), [upgrades](tweakium/modules/upgrades-persistence.md), [loaders](tweakium/integrations/loaders.md).
- [Peripheralium content](peripheralium/content.md) and [adapters](peripheralium/integrations/loaders.md) own materials/resources, not generic plugins. Blend remains legacy-only.
- [Testiarium harness](testiarium/harness.md) and [client/CCT](testiarium/client-cct.md) separate main API from fixtures.
- [Typed contracts](typed-peripheral-api/contracts.md) remain independently maintained and correlated with real Lua, not generated from Kotlin.

## Evidence and limits

[Final verification](../docs/minecraft-26-1-verification.md) records exact pins, approved observer/JEI/barrel changes, receipts and compatibility limits. [Coverage](../docs/module-test-coverage.md) separates historical full builds, final1,757 units,28 native routes and affected replays without double-counting. [Workflow](testing/verification.md) routes fresh nonempty XML, exact negative sentinels, copied saves, actual-two-client packets and graphical proofs.

Use wrapper --no-daemon, scratch logs/deadlines and canonical restoration. A successful compile/process is not runtime acceptance. F's audits inherit exact-source executions; remote CI/release/publication and cross-device wiki sync were not performed. No cross-version wire/ABI, downgrade or arbitrary third-party certification is implied.
