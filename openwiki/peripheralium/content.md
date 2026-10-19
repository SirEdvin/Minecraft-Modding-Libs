---
type: subproject
title: Peripheralium content and resources
description: Material identities, keyed registration and native/data/client evidence.
tags: [peripheralium, content, datagen]
verified:
  - by: openwiki/0.7.1
    at: 2026-10-19T00:45:42.798Z
sources:
  - id: openwiki-source-46cc0af9773fbef0cab9dc18
    resource: repo://docs/verification/minecraft-26-1-R4.md
  - id: openwiki-source-851986a2438c3c75fd1c9bb7
    resource: repo://docs/verification/minecraft-26-1-R7-final-integration.md
  - id: openwiki-source-a20cada7dd4dd8789477a256
    resource: repo://projects/peripheralium/versions/26.1.2/src/main/kotlin/site/siredvin/peripheralium/Items.kt
generated: { by: "hermes", at: "2026-10-19T00:45:42.798Z" }
---

# Peripheralium content and resources

## Ownership

Peripheralium supplies materials and depends on matching Broccolium/Tweakium; generic peripheral dispatch belongs to Tweakium. All maintained lines contain peripheralium_block, peripheralium_dust and peripheralium_upgrade_template. Blend and its smelting recipe remain1.20.1-only; target26.1.2 preserves modern absence rather than inheriting legacy fallback.

Configure its platform before common content/tab registration. Target factories supply keys before native block/item construction, and owning loader adapters attach deferred registries. The holder drives creative entries and data validation.

## Providers and actual behavior

Owning providers compose recipes, self-drop loot, block/item/client definitions and EN/UA languages through Broccolium. Dust crafting, block compression/decompression and four-template crafting retain their IDs/inputs/counts. Update generators, never hand-edit generated resources.

[R4](../../docs/verification/minecraft-26-1-R4.md) ports all target branches and verifies native registration/tracker identity, loaded/emitted recipes, loot and deterministic language/model inventories on both loaders. Peripheralium owns fixtures that execute through Tweakium's native runner; no reverse production cycle.

[Final R7 integration](../../docs/verification/minecraft-26-1-R7-final-integration.md) also verifies actual creative/content/book/menu interactions and refreshes affected data/publication/process evidence. External datapacks and recipe-transfer automation are not certified.

See [loaders](integrations/loaders.md), [Broccolium data](../broccolium/modules/data.md), [Tweakium dispatch](../tweakium/modules/peripheral.md) and [final limits](../../docs/minecraft-26-1-verification.md).
