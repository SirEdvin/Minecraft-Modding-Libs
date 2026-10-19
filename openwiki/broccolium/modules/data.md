---
type: module
title: Broccolium data generation
description: Provider ownership, selected source roots and target deterministic resource proof.
tags: [broccolium, datagen, resources]
sources:
  - id: openwiki-source-2a9daaac1604f238ef4c63fb
    resource: repo://build.gradle.kts
  - id: openwiki-source-8e194b0e3daa57910d9a7fd0
    resource: repo://docs/verification/minecraft-26-1-R2.md
  - id: openwiki-source-46cc0af9773fbef0cab9dc18
    resource: repo://docs/verification/minecraft-26-1-R4.md
  - id: openwiki-source-d6a84e4b4f1771cf32182cf7
    resource: repo://docs/verification/minecraft-26-1-R5.md
  - id: openwiki-source-851986a2438c3c75fd1c9bb7
    resource: repo://docs/verification/minecraft-26-1-R7-final-integration.md
  - id: openwiki-source-90c8b318fc2f5139061b7577
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/data/lang/LanguageProvider.kt
generated: { by: "hermes", at: "2026-10-19T00:45:42.798Z" }
verified:
  - by: openwiki/0.7.1
    at: 2026-10-19T13:48:57.965Z
---

# Broccolium data generation

## Ownership and validation

GeneratorSink is the loader-neutral composition boundary for provider factories, tags, loot, models and registry patches. Broccolium's own concrete provider list generates English/Ukrainian language resources; dependent libraries own their content providers. Language generation rejects duplicates and missing expected tracked keys, not every possible untracked runtime string. Registration tracking therefore matters.

Model collectors reject duplicate definitions and synthesize delegated item models only where needed. The target adapts registry-aware recipes, loot and client item/model APIs with typed overrides; NeoForge client/server datagen and Fabric providers are distinct adapter routes. Update the provider rather than hand-editing `src/generated`.

## Effective roots and deterministic output

Stonecutter's full-file overrides apply to generated resources too. An inactive source-root substitution previously omitted old languages or leaked legacy models. The repository now selects authored generated roots explicitly and verifies packaged output in all three active states. This is separate from compilation source exclusion.

[R5](../../../docs/verification/minecraft-26-1-R5.md) executes40 actual generator tasks across12 commands/two passes over all six loader/version pairs, validating96 selected production JSON resources at that baseline. Old Tweakium has no concrete production generator: abstract utility presence is not an executed provider. [Final R7 integration](../../../docs/verification/minecraft-26-1-R7-final-integration.md) repeats24 target tasks after the approved target Filler generator/resource change. Exact before/after inventories agree; historical and replay counts overlap and must not be summed as unique assets.

See [Peripheralium content](../../peripheralium/content.md), [Tweakium generated data](../../tweakium/modules/platform-data.md), [platform tracking](platform-base.md) and [source/publication ownership](../../build/stonecutter-publication.md).
