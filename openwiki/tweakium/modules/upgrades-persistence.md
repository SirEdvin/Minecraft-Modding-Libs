---
type: module
title: Tweakium pocket and turtle upgrade persistence
description: Peripheral construction, stateful upgrade item transport, and version-specific storage adapters with component-removal and encoding failure boundaries.
tags: [tweakium, pocket, turtle, persistence, components]
verified:
  - by: openwiki/0.7.1
    at: 2026-10-16T16:31:45.808Z
sources:
  - id: openwiki-source-d237f85d7c193303ade52120
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/peripheral/util/DataStorageUtil.kt
  - id: openwiki-source-3cb56346ea1c03a2323cc986
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/pocket/BasePocketUpgrade.kt
  - id: openwiki-source-67278f47cee863a33439ce17
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/pocket/StatefulPocketUpgrade.kt
  - id: openwiki-source-67c4a0c7bc4ce4a8e4fce026
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/turtle/BaseTurtleUpgrade.kt
  - id: openwiki-source-d948762c7e87899d0011e085
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/turtle/StatefulTurtleUpgrade.kt
  - id: openwiki-source-af4c55ea0cd37358f1755d92
    resource: repo://projects/tweakium/versions/1.21.1/src/main/kotlin/site/siredvin/tweakium/modules/peripheral/util/DataStorageUtil.kt
  - id: openwiki-source-8f533dc820521dd8d1a4cd4a
    resource: repo://projects/tweakium/versions/1.21.1/src/main/kotlin/site/siredvin/tweakium/modules/pocket/StatefulPocketUpgrade.kt
  - id: openwiki-source-b80399758032f5afdc373e78
    resource: repo://projects/tweakium/versions/1.21.1/src/main/kotlin/site/siredvin/tweakium/modules/turtle/StatefulTurtleUpgrade.kt
  - id: openwiki-source-9bd2e62953be55b4341193eb
    resource: repo://projects/tweakium/versions/1.21.1/src/test/kotlin/site/siredvin/tweakium/DataStorageTests.kt
  - id: openwiki-source-0484d02f58a681318a0a683e
    resource: repo://projects/tweakium/versions/1.21.1/src/test/kotlin/site/siredvin/tweakium/UpgradeDataStorageTests.kt
generated: { by: "hermes", at: "2026-10-16T16:31:45.808Z" }
---

# Tweakium pocket and turtle upgrade persistence

## Construction and ownership

BasePocketUpgrade and BaseTurtleUpgrade adapt ComputerCraft upgrade creation to IOwnedPeripheral. They build a peripheral and return DisabledPeripheral when it is not enabled. PeripheralPocketUpgrade delegates to PockerUpgradePeripheralBuilder (the public spelling is intentional); PeripheralTurtleUpgrade.dynamic captures an item, ID supplier and TurtleUpgradePeripheralBuilder. Stateful peripheral subclasses use the same builder seams but inherit explicit item-data transport.

These classes do not themselves supply every owner service. Peripheral implementations receive access/side through builders and use the [owner/data APIs](peripheral.md) and [platform toolkit](platform-data.md). Choose a stateful upgrade when removing/reinstalling an upgrade must carry configured data, rather than assuming a peripheral instance field survives reconstruction.

## Item transport is version-specific

In canonical 1.20.1 source, StatefulPocketUpgrade and StatefulTurtleUpgrade read the storedData child CompoundTag. Converting upgrade data back to an item returns the crafting item for empty data, otherwise copies it and adds storedData. Suitability checks copy and strip that child before delegating to CC's base matching. Ordinary peripheral upgrades do not add this transport behavior.

The 1.21.1 overrides instead whitelist importantComponents, defaulting to CUSTOM_DATA. They extract only present whitelisted component values and restore only present patch entries onto a copy of the crafting item. Suitability checking removes those components on a copy. This is not a general lossless transport for arbitrary component patches: neither non-whitelisted components nor explicit component-removal entries are replayed by these item conversion loops. That boundary differs from the removal-preserving storage setters below; do not extrapolate the adapter regression test into a guarantee about upgrade reinstallation.

## Mutable settings and persistence notification

DataStorageUtil chooses storage by owner. On 1.20.1, it wraps the pocket's upgradeNBTData or turtle's side-specific upgrade NBT with callbacks to updateUpgradeNBTData. Peripheral block-entity settings notify markSettingsChanged; a raw CompoundTag wrapper intentionally has a no-op callback. CompoundTagDataStorage mutates the supplied tag and invokes its callback after mutating operations. Directly mutating a compound returned by a getter is not the same as calling a notifying setter or mutate.

On 1.21.1, pocket and turtle adapters expose CC's upgrade DataComponentPatch. CustomDataComputerDataStorage copies CUSTOM_DATA's tag (or creates one), mutates the copy and writes a new CustomData. Both concrete setters rebuild the patch: they preserve present values AND explicit removal entries for other components, then replace CUSTOM_DATA. Turtle access is side-specific. A patch getter/setter also permits direct replacement of the whole patch; callers own that replacement's content.

CompoundTagDataStorage encodes its patch into __patch_storage__ before modifying the tag, then triggers persistence. A failed encoding therefore leaves existing tag data and callback count intact; an empty patch write still notifies. AbstractDataObject returns null for a missing key, removes invalid decoded values, and removes the key for a null value or failed write. Consumers must treat these as stateful cleanup paths, not pure reads.

## Evidence and verification boundary

UpgradeDataStorageTests uses narrow CC API proxies with the real pocket/turtle adapters. It checks repeated custom-data writes, removal and mutation retain another component's value and explicit CUSTOM_NAME removal. DataStorageTests checks patch write/clear notifications and failed encoding preserving storage without notification. These are existing JUnit tests inspected in source, not live computer save/reload or reinstall tests, and were not executed during this documentation task.

See [verification and coverage gaps](../../testing/verification.md) and the [migration audit](../../../docs/stonecutter-migration-audit.md) before expanding compatibility claims. No application fixes are part of this wiki run.
