---
type: architecture
title: Architecture overview
description: Production dependency directions, test and contract boundaries, and drill-down routes across all five projects in Minecraft Modding Libs.
tags: [architecture, dependencies, navigation]
verified:
  - by: openwiki/0.7.1
    at: 2026-10-21T16:53:49.649Z
sources:
  - id: openwiki-source-d7497543e084877c0f613811
    resource: repo://gradle/minecraft-26-1.gradle.kts
  - id: openwiki-source-9d0a6257df77a1a5c8a563fe
    resource: repo://projects/broccolium/fabric/build.gradle.kts
  - id: openwiki-source-0e18fe1e8a3fb313dbba48ae
    resource: repo://projects/peripheralium/build.gradle.kts
  - id: openwiki-source-4ddbd0907e10e62bd7b67a8b
    resource: repo://projects/testiarium/build.gradle.kts
  - id: openwiki-source-b21bfc1ffdd367853ce150ec
    resource: repo://projects/tweakium/build.gradle.kts
  - id: openwiki-source-8f0473f7bfdf3d93a0ee4a62
    resource: repo://projects/tweakium/fabric/build.gradle.kts
  - id: openwiki-source-18505126b4fc17c6fe763eea
    resource: repo://projects/typed-peripheral-api/build.gradle.kts
  - id: openwiki-source-b2992bb4316ae5243156d682
    resource: repo://projects/typed-peripheral-api/tests/contracts.ts
generated: { by: "hermes", at: "2026-10-21T16:53:49.649Z" }
---

# Architecture overview

[Task map](../quickstart.md) · [Version and loader topology](loaders.md) · [Build/publication](../build/stonecutter-publication.md)

These are logical module and source-ownership diagrams, not Minecraft block/item rendering models. Follow [representation/RML](../tweakium/modules/representation-rml.md) for client rendering. Each library box collapses matching-version common and loader leaves; expand those on the [loader topology page](loaders.md).

## Production dependencies

```mermaid
flowchart TB
  accTitle: Production consumer to provider dependencies
  accDescr: Peripheralium depends directly on both Tweakium and Broccolium. Tweakium depends on Broccolium. Testiarium and TypeScript are not links in that production chain.
  P["Peripheralium\nMaterials and concrete content"] -->|depends on| T["Tweakium\nCC peripherals, plugins and upgrades"]
  P -->|direct dependency| B["Broccolium\nPlatform, storage, lookup and data"]
  T -->|depends on| B
```

Solid arrows point from consumer to provider. Peripheralium declares **both** dependencies; the direct Broccolium edge must not disappear into an assumed chain. The [legacy common declarations](../../projects/peripheralium/build.gradle.kts#L40-L45), [Tweakium declaration](../../projects/tweakium/build.gradle.kts#L51-L60) and [26.1.2 convention](../../gradle/minecraft-26-1.gradle.kts#L107-L128) establish these directions. Configuration details differ (legacy common `implementation`, target `api`, adapter common outputs); the boxes are not a claim that every Gradle configuration is identical.

## Tests and consumer contracts are different edges

```mermaid
flowchart TB
  accTitle: Fixture consumption and TypeScript contract correlation
  accDescr: Broccolium and Tweakium test sources use Testiarium. Tweakium tests also consume Broccolium fixtures. TypeScript contracts correlate with Tweakium Lua APIs without a production Kotlin dependency.
  BT["Broccolium tests"] -.->|test harness| H["Testiarium\nGameTest API, reports and fixtures"]
  TT["Tweakium tests"] -.->|test harness and CCT fixtures| H
  TT -.->|test fixtures| BF["Broccolium testFixtures"]
  TS["typed-peripheral-api\nTypeScript interfaces and compiler contracts"] ==>|contract correlation| L["Tweakium Lua peripheral surface"]
  classDef test stroke-dasharray: 5 5
  class BT,TT,BF,H test
```

Dashed arrows mean test-only consumption, not runtime library dependencies; dashed boxes identify test surfaces. The thick arrow means independently maintained consumer-contract correlation, not generated declarations, Kotlin compilation dependency or runtime certification. Read [Broccolium's test wiring](../../projects/broccolium/fabric/build.gradle.kts#L54-L67), [Tweakium fixture wiring](../../projects/tweakium/fabric/build.gradle.kts#L43-L69), [target wiring](../../gradle/minecraft-26-1.gradle.kts#L136-L165) and [TypeScript contracts](../../projects/typed-peripheral-api/tests/contracts.ts#L60-L99). Testiarium's [main API and optional fixture features](../../projects/testiarium/build.gradle.kts#L21-L54) are separate outputs. Peripheralium content tests can execute through Tweakium's runner; this does not create a reverse production dependency.

## Drill down by responsibility

These normal Markdown links work without JavaScript, color perception or diagram clicking. Each project entry page links back here.

| Project | Entry and focused modules | Boundary to keep in mind |
| --- | --- | --- |
| Broccolium | [Platform/lifecycle](../broccolium/modules/platform-base.md), [storage](../broccolium/modules/storage.md), [lookup/drops](../broccolium/modules/lookup-drops.md), [data](../broccolium/modules/data.md), [adapters](../broccolium/integrations/loaders.md) | Loader-neutral abstractions versus native storage/registration |
| Tweakium | [Peripheral ownership/dispatch](../tweakium/modules/peripheral.md), [plugins/operations](../tweakium/modules/plugins.md), [fake player](../tweakium/modules/player.md), [platform/data](../tweakium/modules/platform-data.md), [representation/RML](../tweakium/modules/representation-rml.md), [upgrade persistence](../tweakium/modules/upgrades-persistence.md), [adapters](../tweakium/integrations/loaders.md) | Logical owner/plugin model is separate from client render models |
| Peripheralium | [Content/resources](../peripheralium/content.md), [adapters](../peripheralium/integrations/loaders.md) | Concrete materials/content, not generic Lua dispatch |
| Testiarium | [Harness and reports](../testiarium/harness.md), [client/CCT](../testiarium/client-cct.md) | Main API, testMod and cctTestMod are distinct |
| typed-peripheral-api | [Consumer contracts](../typed-peripheral-api/contracts.md) | Independent Node/TypeScript package, outside Stonecutter |

## Reading and verification routes

Start with [version/loader topology](loaders.md) before inspecting implementation overrides. Use [verification](../testing/verification.md) for executable gates and the [diagram coverage checklist](../../docs/verification/openwiki-diagrams.md) for completed overview scope versus later class/flow ownership. Current diagrams summarize build declarations; no application tests were run merely to draw them.
