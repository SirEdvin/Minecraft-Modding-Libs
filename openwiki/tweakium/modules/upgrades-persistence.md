---
type: module
title: Tweakium pocket and turtle upgrade persistence
description: Peripheral construction, stateful upgrade item transport, and version-specific storage adapters with component-removal and encoding failure boundaries.
tags: [tweakium, pocket, turtle, persistence, components]
sources:
  - id: openwiki-source-433a5a8f61ef03e88c1fd1ae
    resource: repo://docs/verification/minecraft-26-1-R3.md
  - id: openwiki-source-805224ca8622b6a9abf34e78
    resource: repo://docs/verification/minecraft-26-1-R6-integration.md
  - id: openwiki-source-8baf1147a53cafceec55d056
    resource: repo://docs/verification/minecraft-26-1-R6-saves.md
  - id: openwiki-source-851986a2438c3c75fd1c9bb7
    resource: repo://docs/verification/minecraft-26-1-R7-final-integration.md
  - id: openwiki-source-19d334151b9cbf920f37889d
    resource: repo://projects/tweakium/versions/26.1.2/src/main/kotlin/site/siredvin/tweakium/modules/peripheral/util/DataStorageUtil.kt
generated: { by: "hermes", at: "2026-10-19T00:45:42.798Z" }
verified:
  - by: openwiki/0.7.1
    at: 2026-10-19T13:48:57.965Z
---

# Tweakium pocket and turtle upgrade persistence

## Current template and persistence acceptance (2026-10-19)

On26.1.2 upgrade definitions/crafting items use immutable ItemStackTemplate resolved with registry/world context; runtime inventories stay ItemStack. Important-component whitelists distinguish absent values from explicit removals relative to the crafting template. Preserve CUSTOM_DATA, __patch_storage__, unrelated patches, peripheralData/ownerProfile and legacy storedData. Encoding failure must not write or notify. Public PockerUpgradePeripheralBuilder spelling is retained.

[R3](../../../docs/verification/minecraft-26-1-R3.md) includes target modern storage suites and actual registered pocket/turtle install/remove/reinstall, chunk availability/unload/reload and disk serialization checks. [R6 saves](../../../docs/verification/minecraft-26-1-R6-saves.md) and [integration](../../../docs/verification/minecraft-26-1-R6-integration.md) add copied/staged world upgrades with immutable originals/backups, semantic state and process restart. [Final R7](../../../docs/verification/minecraft-26-1-R7-final-integration.md) refreshes affected candidate save opens/restarts. These probes do not invent concrete persistent production upgrades/tanks absent from the library, promise downgrade, or certify arbitrary saves/cross-loader conversion.

## Historical source notes — October16 baseline

The detailed327f845/e69b721 item/proxy tests below remain historical provenance, not a claim that later live install/process/save checks were unperformed. Current target API and bounded compatibility above supersede them.

## Construction and ownership

BasePocketUpgrade and BaseTurtleUpgrade adapt ComputerCraft upgrade creation to IOwnedPeripheral. They build a peripheral and return DisabledPeripheral when it is not enabled. PeripheralPocketUpgrade delegates to PockerUpgradePeripheralBuilder (the public spelling is intentional); PeripheralTurtleUpgrade.dynamic captures an item, ID supplier and TurtleUpgradePeripheralBuilder. Stateful peripheral subclasses use the same builder seams but inherit explicit item-data transport.

These classes do not themselves supply every owner service. Peripheral implementations receive access/side through builders and use the [owner/data APIs](peripheral.md) and [platform toolkit](platform-data.md). Choose a stateful upgrade when removing/reinstalling an upgrade must carry configured data, rather than assuming a peripheral instance field survives reconstruction.

## Item transport is version-specific

In canonical 1.20.1 source, StatefulPocketUpgrade and StatefulTurtleUpgrade read the storedData child CompoundTag. Converting upgrade data back to an item returns the crafting item for empty data, otherwise copies it and adds storedData. Suitability checks copy and strip that child before delegating to CC's base matching. Ordinary peripheral upgrades do not add this transport behavior.

The 1.21.1 overrides whitelist importantComponents, defaulting to CUSTOM_DATA. Reconstruction distinguishes an absent patch entry from an explicitly empty Optional: absent leaves the crafting default unchanged, present replaces it, and explicit removal removes it from a copy. Extraction records present values or removal when the crafting template has the missing component or the stack has an explicit removal patch. That template-relative check preserves removal after native item serialization normalizes the item's own patch. The crafting template remains unchanged. Non-whitelisted components are still outside this transport contract; suitability checking removes important components on a copy before delegation.

## Mutable settings and persistence notification

DataStorageUtil chooses storage by owner. On 1.20.1, it wraps the pocket's upgradeNBTData or turtle's side-specific upgrade NBT with callbacks to updateUpgradeNBTData. Peripheral block-entity settings notify markSettingsChanged; a raw CompoundTag wrapper intentionally has a no-op callback. CompoundTagDataStorage mutates the supplied tag and invokes its callback after mutating operations. Directly mutating a compound returned by a getter is not the same as calling a notifying setter or mutate.

On 1.21.1, pocket and turtle adapters expose CC's upgrade DataComponentPatch. CustomDataComputerDataStorage copies CUSTOM_DATA's tag (or creates one), mutates the copy and writes a new CustomData. Both concrete setters rebuild the patch: they preserve present values AND explicit removal entries for other components, then replace CUSTOM_DATA. Turtle access is side-specific. A patch getter/setter also permits direct replacement of the whole patch; callers own that replacement's content.

CompoundTagDataStorage encodes its patch into __patch_storage__ before modifying the tag, then triggers persistence. A failed encoding therefore leaves existing tag data and callback count intact; an empty patch write still notifies. AbstractDataObject returns null for a missing key, removes invalid decoded values, and removes the key for a null value or failed write. Consumers must treat these as stateful cleanup paths, not pure reads.

## Evidence and verification boundary

UpgradeDataStorageTests uses narrow CC API proxies with the real pocket/turtle adapters. It checks repeated custom-data writes, removal and mutation retain another component's value and explicit CUSTOM_NAME removal. DataStorageTests checks patch write/clear notifications and failed encoding preserving storage without notification. Both remain passing in the final builds.

Native UpgradeBehaviorGameTests additionally exercise pocket/turtle item conversion, real item serialization/readback, unchanged crafting templates and rejection of unrelated data; modern cases distinguish explicit removal from an unspecified component. Both modern removal regressions failed before the paired fix. These are serialized item round trips, not live upgrade uninstall/reinstall or world disk reload. See [verification and coverage gaps](../../testing/verification.md) and the [coverage matrix](../../../docs/module-test-coverage.md) before expanding compatibility claims.
