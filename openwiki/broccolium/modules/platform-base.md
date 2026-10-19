---
type: module
title: Broccolium platform and block lifecycle
description: Keyed registration, approved target observer API and native lifecycle/persistence.
tags: [broccolium, platform, persistence]
sources:
  - id: openwiki-source-9323aa17bbac93f47b38d4cb
    resource: repo://docs/minecraft-26-1-migration-brief.md
  - id: openwiki-source-8e194b0e3daa57910d9a7fd0
    resource: repo://docs/verification/minecraft-26-1-R2.md
  - id: openwiki-source-41442d892bd69196733e32bf
    resource: repo://projects/broccolium/versions/26.1.2/src/main/kotlin/site/siredvin/broccolium/modules/base/api/IObservingBlockEntity.kt
  - id: openwiki-source-2bc8acf85ceeaeeeced01c19
    resource: repo://projects/broccolium/versions/26.1.2/src/main/kotlin/site/siredvin/broccolium/modules/base/blockentity/MutableNBTBlockEntity.kt
generated: { by: "hermes", at: "2026-10-19T00:45:42.798Z" }
verified:
  - by: openwiki/0.7.1
    at: 2026-10-19T13:32:11.388Z
---

# Broccolium platform and block lifecycle

## Platform and registration

Broccolium explicitly installs toolkit/tag/ingredient implementations before lazy registry access. BasePlatform is a consuming mod's registration/tracking facade, not loader autodetection. Loader adapters own immediate Fabric versus deferred Forge-family registration and event-bus attachment. Target block/item properties receive keys before construction. Common services remain loader-neutral.

## Approved observer migration

Only26.1.2 changes `IObservingBlockEntity.onNeighbourChange` to `(neighbourBlock: Block, orientation: Orientation?)`. Forward the exact native data, including null orientation. The native target hook cannot truthfully supply a causal neighbor BlockPos; never substitute the receiving position or synthesize six callbacks. Old1.20.1/1.21.1 consumers retain `(neighbour: BlockPos)` unchanged. Target consumers recompile and implement the new signature.

Target removal interception is scoped to native replacement, not generic `setRemoved`/chunk unload. [R2](../../../docs/verification/minecraft-26-1-R2.md) verifies same-block state updates, one destroy after removal, preserved drops, no unload destruction, supplier/registry codecs and all packaged access rules. LimitedInventory remains extensible.

## Persistence and sync

MutableNBTBlockEntity bridges internal settings into world save/load/update tags. Target ValueInput/ValueOutput uses a root codec to preserve existing payload keys; old version-specific NBT/provider signatures remain. World persistence, prepared block-item transport and client synchronization are separate paths. No new SavedData/attachment owner or custom file relocation was introduced.

Copied/staged save upgrades and process restart evidence live in [final verification](../../../docs/minecraft-26-1-verification.md); they do not guarantee downgrade, arbitrary saves or every loot/piston path. See [data generation](data.md), [menu/network evidence](../../testing/verification.md) and [Tweakium settings](../../tweakium/modules/upgrades-persistence.md).
