---
type: module
title: Broccolium storage transfers
description: Agnostic storage/sink contracts, simulated transfer planning, remainder recovery, fluid and energy units, and adapter-specific limits.
tags: [broccolium, storage, items, fluids, energy, transactions]
verified:
  - by: openwiki/0.7.1
    at: 2026-10-16T16:31:45.808Z
sources:
  - id: openwiki-source-4f6ae296d25e67d889ff620e
    resource: repo://projects/broccolium/fabric/src/main/kotlin/site/siredvin/broccolium/modules/storage/item/FabricStorageWrapper.kt
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
  - id: openwiki-source-eb86e89f1dad91b8b8d0c82f
    resource: repo://projects/broccolium/src/test/kotlin/site/siredvin/broccolium/test/StorageTests.kt
  - id: openwiki-source-ee56033eb06576f74a001b20
    resource: repo://projects/broccolium/src/testFixtures/kotlin/site/siredvin/broccolium/test/StorageTests.kt
  - id: openwiki-source-c569e0d7cd3a867f9fc5c48c
    resource: repo://projects/broccolium/versions/1.21.1/src/main/kotlin/site/siredvin/broccolium/modules/storage/fluid/AgnosticFluidStack.kt
generated: { by: "hermes", at: "2026-10-16T16:31:45.808Z" }
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

## Adapter cautions and tests

The shared FabricStorageWrapper contains two important branch limits: moveFrom restricts a Fabric slotted source only when fromSlot > 0, so zero follows its whole-storage route; moveTo's non-slotted-target guard checks toSlot < -1, not the ordinary nonnegative requested-slot range. Do not infer a universal slot guarantee from the interfaces. These are source-observed candidates for focused regression work, not fixes made by this wiki.

Common StorageTests fixtures assert storage invariance during simulation, expected real mutations, both movement directions, predicates, and nonoverlapping content. Concrete tests instantiate dummy, no-takeback and vanilla-container combinations; loader test suites exercise their wrappers. These tests are source coverage, not evidence that every side/capability/native transaction path ran here. See [verification](../../testing/verification.md) and [loader discovery](../integrations/loaders.md).

Lua-facing validation and slot conversion are owned by [Tweakium plugins](../../tweakium/modules/plugins.md), not by this generic layer. No application tests were executed for documentation generation.
