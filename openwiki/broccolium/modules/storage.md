---
type: module
title: Broccolium storage transfers
description: Runtime storage contracts, transactional target adapters and explicit limits.
tags: [broccolium, storage, transactions]
verified:
  - by: openwiki/0.7.1
    at: 2026-10-19T00:45:42.798Z
sources:
  - id: openwiki-source-8e194b0e3daa57910d9a7fd0
    resource: repo://docs/verification/minecraft-26-1-R2.md
  - id: openwiki-source-f6b3458a12876dad0171a34e
    resource: repo://docs/verification/minecraft-26-1-R6.md
  - id: openwiki-source-851986a2438c3c75fd1c9bb7
    resource: repo://docs/verification/minecraft-26-1-R7-final-integration.md
  - id: openwiki-source-b7f290a6d9cf73fb9621e0aa
    resource: repo://projects/broccolium/forge/versions/26.1.2/src/main/kotlin/site/siredvin/broccolium/modules/storage/item/AgnosticItemAccess.kt
  - id: openwiki-source-aaca5fb092351d69ebceac13
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/storage/base/StorageUtils.kt
  - id: openwiki-source-f932a1242f67f177056d6452
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/storage/item/ReplaceableItemStorage.kt
generated: { by: "hermes", at: "2026-10-19T00:45:42.798Z" }
---

# Broccolium storage transfers

## Common runtime contract

AgnosticSink returns an unaccepted remainder; AgnosticStorage adds extraction/content iteration and slotted variants add admission/capacity/range contracts. Runtime item values stay ItemStack, not ItemStackTemplate. Fluid amounts are compact Double quantities converted at native boundaries; energy is Long with explicit conversion edges. Review units and rounding at the adapter, not only Lua's number type.

Ordinary generic transfers simulate before real mutation and attempt to restore remainders. This is not universal atomicity: adverse peers can reject restoration. A native transaction on one side does not enlist an arbitrary external backend.

## Exact replacement and target transactions

ReplaceableItemStorage is an explicit opt-in to exact copied slot replacement, including empty/data changes and restoration. Reverse adapters reject unsupported backings before mutation; ordinary additive store cannot restore a snapshot. Backing layout/contents/limits must remain stable and captured variants restorable. Setter notifications/callbacks are not themselves rolled back.

Fabric preserves native slot transaction participants instead of wrapping origins unnecessarily. Target NeoForge adapts ResourceHandler, ItemResource/FluidResource and EnergyHandler with native transaction ownership. ItemAccess replaces the origin slot when containers change, including restoring components/counts on abort. Simulation does not commit; nested abort/commit is exercised rather than inferred from an interface name.

## Evidence and limits

[R2](../../../docs/verification/minecraft-26-1-R2.md) covers positive providers, nested rollback, limits, component variants, nullable sides, removed providers and container replacement. The assembled [R6 matrix](../../../docs/verification/minecraft-26-1-R6.md) contains99 storage cases with zero skips. Final candidate sources/artifacts are rebound through [R7 integration](../../../docs/verification/minecraft-26-1-R7-final-integration.md). This is not certification of arbitrary third-party or independently mutating backings.

See [lookup](lookup-drops.md), [loaders](../integrations/loaders.md) and [Lua validation/units](../../tweakium/modules/plugins.md).
