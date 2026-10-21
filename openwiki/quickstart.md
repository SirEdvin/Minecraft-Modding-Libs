---
type: quickstart
title: Minecraft Modding Libs ownership and task map
description: Current three-version graph, module ownership and final evidence routes.
tags: [quickstart, ownership, minecraft]
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
generated: { by: "hermes", at: "2026-10-21T17:43:06.090Z" }
verified:
  - by: openwiki/0.7.1
    at: 2026-10-21T17:43:06.090Z
---

# Minecraft Modding Libs ownership and task map

## Visual entry points

Start with the [architecture overview](architecture/overview.md) for all five projects, production arrows, separate fixture/contract relationships and module drill-downs. Continue with [version and loader topology](architecture/loaders.md) for matching common/adapter leaves and canonical versus overridden sources. These logical diagrams are not Minecraft client render models; those belong to [representation/RML](tweakium/modules/representation-rml.md). Normal links and prose remain usable without diagram rendering.

### Follow one visual route

The [overview responsibility table](architecture/overview.md#drill-down-by-responsibility) reaches every meaningful existing module. Each entry has small diagrams with text summaries and source evidence; no SVG clicking is required. Pick a subsystem, then a focused model or sequence:

| Start | Module/model | Behavior or boundary |
| --- | --- | --- |
| [Broccolium](architecture/overview.md#production-dependencies) | [Storage class model](broccolium/modules/storage.md#focused-storage-class-model) | Generic transfer on that page, then [native adapters](broccolium/integrations/loaders.md#native-delegation-map); platform/data/lookup remain in ownership routes below |
| [Tweakium](architecture/overview.md#production-dependencies) | [Peripheral composition](tweakium/modules/peripheral.md#composition-model) | Dispatch/connections on that page, [plugin policy](tweakium/modules/plugins.md#small-plugin-family-model), [upgrade transport](tweakium/modules/upgrades-persistence.md#pocket-construction-class-view) or [client rendering](tweakium/modules/representation-rml.md#logical-representation-boundary) |
| [Peripheralium](peripheralium/content.md#content-and-provider-map) | Platform composition and provider lifecycle on the content page | [Loader registration sequence](peripheralium/integrations/loaders.md#registration-sequence) |
| [Testiarium](testiarium/harness.md#harness-and-feature-model) | Registration/reporter model and execution sequence on the harness page | [Client sequence](testiarium/client-cct.md#target-client-sequence), [CCT channels](testiarium/client-cct.md#labeled-cct-action-and-lua-completion), [report acceptance](testing/verification.md#fresh-report-acceptance) |
| [typed-peripheral-api](typed-peripheral-api/contracts.md#interface-and-provider-model) | Maintained interface/provider composition | [Compiler/emission/runtime flow](typed-peripheral-api/contracts.md#compiler-checks-emission-and-runtime-correlation) |
| [Build](architecture/loaders.md) | [Source-to-artifact flow](build/stonecutter-publication.md#source-to-artifact-flow) | [Feature isolation](build/stonecutter-publication.md#main-output-versus-test-features) → [owning verification](testing/verification.md#verification-route-map) |

The [coverage inventory](../docs/verification/openwiki-diagrams.md) records diagram kinds, evidence and actual render/build checks. Source inspection is not execution of Minecraft tests, TypeScript contracts, release publication or hosted deployment.

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

Use wrapper --no-daemon, scratch logs/deadlines and canonical restoration. A successful compile/process is not runtime acceptance. F's audits inherit exact-source executions; final local closure is owned by t_b9190fc7 after stable-wiki parent t_c54853f2. The [delivery index](../docs/verification/minecraft-26-1-delivery.md) separates197 inherited/local evidence dispositions from7 final-documentation dispositions, not204 test passes, and locates all118 actual artifact hashes. Historical raw sibling ZIP locations were removed; retained accepted receipts are not freshly replayed archives. Remote CI/release/publication and cross-device wiki sync were not performed. No cross-version wire/ABI, downgrade or arbitrary third-party certification is implied.
