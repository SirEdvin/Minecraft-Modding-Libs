---
type: module
title: Broccolium storage transfers
description: Agnostic storage/sink contracts, simulated transfer planning, remainder recovery, fluid and energy units, and adapter-specific limits.
tags: [broccolium, storage, items, fluids, energy, transactions]
sources:
  - id: openwiki-source-d0e6990b3e6ef4362ed686f4
    resource: repo://projects/broccolium/fabric/src/main/kotlin/site/siredvin/broccolium/modules/storage/FabricStorageUtils.kt
  - id: openwiki-source-4f6ae296d25e67d889ff620e
    resource: repo://projects/broccolium/fabric/src/main/kotlin/site/siredvin/broccolium/modules/storage/item/FabricStorageWrapper.kt
  - id: openwiki-source-aff0c4176fcdd0267229693f
    resource: repo://projects/broccolium/fabric/src/main/kotlin/site/siredvin/broccolium/modules/storage/item/SlottedAgnosticItemStorageSlotWrapper.kt
  - id: openwiki-source-8132ab9cf332de0696af0b1d
    resource: repo://projects/broccolium/fabric/src/main/kotlin/site/siredvin/broccolium/modules/storage/item/SlottedAgnosticItemStorageWrapper.kt
  - id: openwiki-source-84ca7cec36b0a5056cd3a053
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/storage/base/api/AgnosticSink.kt
  - id: openwiki-source-5f08381c71ffb911e18d07aa
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/storage/base/api/AgnosticStorage.kt
  - id: openwiki-source-4363ef0c32dc95681f3a71f3
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/storage/base/api/SlottedAgnosticStorage.kt
  - id: openwiki-source-aaca5fb092351d69ebceac13
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/storage/base/StorageUtils.kt
  - id: openwiki-source-66cbc36662a98389a2143d28
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/storage/energy/EnergyRegistry.kt
  - id: openwiki-source-6d075a851e7cc9082f681941
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/storage/fluid/AgnosticFluidStack.kt
  - id: openwiki-source-ba72c49d785326dc786904fb
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/storage/item/ContainerWrapper.kt
  - id: openwiki-source-f932a1242f67f177056d6452
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/storage/item/ReplaceableItemStorage.kt
  - id: openwiki-source-eb86e89f1dad91b8b8d0c82f
    resource: repo://projects/broccolium/src/test/kotlin/site/siredvin/broccolium/test/StorageTests.kt
  - id: openwiki-source-ee56033eb06576f74a001b20
    resource: repo://projects/broccolium/src/testFixtures/kotlin/site/siredvin/broccolium/test/StorageTests.kt
  - id: openwiki-source-c569e0d7cd3a867f9fc5c48c
    resource: repo://projects/broccolium/versions/1.21.1/src/main/kotlin/site/siredvin/broccolium/modules/storage/fluid/AgnosticFluidStack.kt
generated: { by: "hermes", at: "2026-10-16T19:56:35.545Z" }
verified:
  - by: openwiki/0.7.1
    at: 2026-10-16T19:56:35.545Z
---

# Broccolium storage transfers

## Public contracts and ownership

AgnosticSink accepts a stack and returns the unaccepted remainder; AgnosticStorage adds extraction and content iteration. SomethingOperator supplies stack and numeric operations so the same transfer loop works with ItemStack/Int, fluid/Double and energy/Long. SlottedAgnosticStorage adds accessible slots, placement checks, limits and inclusive start/end extraction; its whole-storage extraction uses 0 through size - 1. Negative transfer slot values select an unspecified slot rather than a Lua-facing index.

ContainerWrapper adapts a vanilla Container via ContainerUtils, including its mutation notification. Loader wrappers belong to their adapters, while lookup providers and transfer interfaces stay common. [Lookup precedence](lookup-drops.md) decides which adapter callers actually receive.

## Transfer flow and failure behavior

For ordinary movableType == null peers, moveTo/moveFrom route into StorageUtils.notSoNaiveMove:

1. Simulate a singular extraction and insertion to find an acceptable amount.
2. Stop if the simulated move is zero.
3. Repeat the singular move for real with that amount.
4. Reduce remaining limit by the actual movement and stop on no progress.

A singular move takes one matching stack, stores into the destination, computes the accepted amount from the remainder, and tries returning leftovers to the source. Failure to return a nonempty remainder during a real move logs that items were voided; it is not a rollback exception or a guarantee of conservation against adversarial providers. Some unsupported slot requests are logged and ignored.

A non-null movableType is an adapter protocol: default move methods throw if that adapter fails to override them, and ordinary peers delegate to the specialized peer. Fabric wrappers use fabricTransaction and native StorageUtil.move for compatible peers; cross-adapter transfers cannot make the external peer part of the Fabric transaction.

Simulation means storage is expected to remain unchanged, not that caller-owned stack arguments are immutable. FabricStorageWrapper.store shrinks its supplied stack even when aborting the simulated transaction. Pass a copy when retaining the input.

## Units, identity and persistence

AgnosticFluidStack.amount is compact Double quantity; platformAmount multiplies by the configured toolkit's fluidCompactDivider. Conversion at loader boundaries is essential, including native integer rounding. In 1.20.1 fluid identity includes optional NBT tags and copies duplicate that tag. In 1.21.1 the full-file override uses DataComponentPatch and serializes the patch into the tag field through DataComponentUtil. Both persist registered fluid ID and compact amount, not a native loader storage object.

EnergyRegistry maintains named units and explicit fractional conversion edges. Reversible registration creates the inverse edge; a nonreversible inverse is stored separately as banned. convert only consults banned conversions when allowBanned is true, throws for missing edges and rounds the resulting amount to Long. It does not search transitive conversion paths, and ordinary isConvertible only sees allowed direct edges.

## Exact replacement for reverse Fabric adapters

Ordinary store is additive and get may return a detached copy; neither operation can restore a transactional snapshot. ReplaceableItemStorage is an explicit loader-neutral opt-in for exact slot replacement, including clearing, variant/data changes and restoration despite changed insertion policy. Implementations must reject invalid sizes before writing, copy caller values and leave other slots untouched. ContainerWrapper implements that contract through exact Container.setItem semantics; custom containers with non-restorable effects must use an appropriate native adapter instead.

SlottedAgnosticItemStorageWrapper rejects non-opted-in backings before inventory access. Each slot reads independent stack copies, refuses an out-of-range existing snapshot before mutation, and restores via replaceSlot rather than additive store. Insertion is bounded by slot admission, per-slot limit, storage maximum and item maximum. A snapshot participant calls setChanged on final commit. Container setter notifications can still occur during speculative changes and rollback; notifications are not rolled back.

The contract requires stable backing contents, layout and maximum while a transaction is open, extraction permission and restorable variants. It does not turn arbitrary slotted backends into safe transactions. FabricStorageUtils.getSlot preserves a FabricSlottedStorageWrapper's native slot rather than rewrapping it; inventory-item fluid and energy contexts use that routing.

## Adapter boundaries and tests

FabricStorageWrapper.moveFrom now selects a native slotted source for every nonnegative fromSlot, including zero. A requested slot on a nonslotted destination is logged and explicitly falls back to whole-storage insertion instead of being cast to a slotted sink. This is documented fallback behavior, not a promise that nonslotted storage honors a destination slot.

Common StorageTests fixtures cover simulation, real mutations, both movement directions, predicates and nonoverlapping content. Native Fabric ItemBridgeGameTests and ItemBridgeContractGameTests cover occupied slots, commit/abort/nested rollback, detached getters, data restoration, oversized snapshot refusal, unsupported backings and native slot identity; all 23 adapter cases pass per version. The additional energy lookup case is separate. See [verification](../../testing/verification.md) for the executed matrix and [loader discovery](../integrations/loaders.md) for sided lookup limits.

Lua-facing validation and slot conversion are owned by [Tweakium plugins](../../tweakium/modules/plugins.md), not by this generic layer. Cross-adapter mutation outside the transaction and independently changing backing inventories remain outside these guarantees.
