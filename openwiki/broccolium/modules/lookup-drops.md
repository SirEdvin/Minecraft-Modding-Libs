---
type: module
title: Broccolium lookup and drop capture
description: Ordered provider discovery, complete modern entity inventories and bounded global capture.
tags: [broccolium, lookup, drops]
verified:
  - by: openwiki/0.7.1
    at: 2026-10-19T00:45:42.798Z
sources:
  - id: openwiki-source-8e194b0e3daa57910d9a7fd0
    resource: repo://docs/verification/minecraft-26-1-R2.md
  - id: openwiki-source-433a5a8f61ef03e88c1fd1ae
    resource: repo://docs/verification/minecraft-26-1-R3.md
  - id: openwiki-source-dec1566019eddd9c7a8d1d07
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/lookup/BaseLookup.kt
  - id: openwiki-source-c1737c3dd7959fcf59892814
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/lookup/ChainedBaseLookup.kt
  - id: openwiki-source-e90ab02061cf2dd82aa378d6
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/tricks/DropConsumer.kt
  - id: openwiki-source-bb09eb55658a45f09e5b9944
    resource: repo://projects/tweakium/versions/26.1.2/src/main/kotlin/site/siredvin/tweakium/modules/player/FakePlayerProxy.kt
generated: { by: "hermes", at: "2026-10-19T00:45:42.798Z" }
---

# Broccolium lookup and drop capture

## Lookup contract

BaseLookup keeps separate block/entity/inventory-item callback lists, tries registered callbacks in order and returns the first non-null result. Null permits fallback; an extractor exception aborts resolution. Unknown-object dispatch handles supported positions/block entities/entities, not arbitrary objects as implicit containers. Chained lookups prefer upstream results, while explicit agnostic sink/storage providers have their own priority.

Read the selected version's entity fallback. Target horse cargo is the full native cargo container; equipment is separate and must not be removed by subtracting legacy slots. Native chest-minecart capability and player/container inventory cases remain explicit regressions. [R2](../../../docs/verification/minecraft-26-1-R2.md) audits modern overrides and proves horse/equipment/cargo/native lookup behavior rather than inferring it from a file count.

## Capture and cleanup

DropConsumer remains process-global mutable capture state, not thread-local or a nested capture service. Entity configuration retains the exact target; positional configuration clears it. Reset clears state before dropping remainders so leftovers do not recapture themselves. Captured contents follow the sink's remainder contract.

Fabric interception uses loader Mixins; Forge-family uses native spawn/living-drop hooks. Tweakium action wrappers release capture in finally on successful and exceptional actions. Native tests retain target identity, unrelated-entity refusal, exact remainder and one-drop behavior. Session inventory/attribute reconciliation is a separate [fake-player contract](../../tweakium/modules/player.md), not implied by capture cleanup.

Nested/concurrent capture and arbitrary third-party event behavior are not certified. See [storage](storage.md), [loader integration](../integrations/loaders.md) and [final evidence/limits](../../../docs/minecraft-26-1-verification.md).
