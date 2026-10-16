---
type: integration
title: Tweakium ComputerCraft loader boundaries
description: Peripheral lookup registration, bootstrap services, upgrade registry changes, fake-player implementations and legacy Forge energy integration.
tags: [tweakium, computercraft, loaders, capabilities, upgrades]
verified:
  - by: openwiki/0.7.1
    at: 2026-10-16T16:31:45.808Z
sources:
  - id: openwiki-source-5c06591034e05f2277e4a0fe
    resource: repo://projects/tweakium/fabric/src/main/kotlin/site/siredvin/tweakium/modules/FabricTweakium.kt
  - id: openwiki-source-222c729101aa1dcdb47d1ef9
    resource: repo://projects/tweakium/fabric/src/main/kotlin/site/siredvin/tweakium/modules/platform/FabricComputerPlatformToolkit.kt
  - id: openwiki-source-d08cd914b0e68a4e0379d742
    resource: repo://projects/tweakium/fabric/src/main/kotlin/site/siredvin/tweakium/modules/platform/FabricInnerComputerBasePlatform.kt
  - id: openwiki-source-c6ef953493afc478eed66114
    resource: repo://projects/tweakium/fabric/src/main/kotlin/site/siredvin/tweakium/modules/player/FabricFakePlayer.kt
  - id: openwiki-source-571e17c4d974e19000a55d13
    resource: repo://projects/tweakium/fabric/versions/1.21.1/src/main/kotlin/site/siredvin/tweakium/modules/platform/FabricInnerComputerBasePlatform.kt
  - id: openwiki-source-6fff2fde55cecc07217fd623
    resource: repo://projects/tweakium/forge/src/main/kotlin/site/siredvin/tweakium/ForgeTweakium.kt
  - id: openwiki-source-bdf80effa3e88ebf06ffdcea
    resource: repo://projects/tweakium/forge/src/main/kotlin/site/siredvin/tweakium/modules/platform/ForgeComputerPlatformToolkit.kt
  - id: openwiki-source-b49a4e46ae5dd607ef6feeee
    resource: repo://projects/tweakium/forge/src/main/kotlin/site/siredvin/tweakium/modules/player/ForgeFakePlayer.kt
  - id: openwiki-source-d8463538c6bc5ef5dba6fda3
    resource: repo://projects/tweakium/forge/src/main/kotlin/site/siredvin/tweakium/modules/plugins/ForgeEnergyPlugin.kt
  - id: openwiki-source-ce710907239f04fecb9e4f3e
    resource: repo://projects/tweakium/forge/versions/1.21.1/src/main/kotlin/site/siredvin/tweakium/ForgeTweakium.kt
  - id: openwiki-source-77b5d1abaadb4600d8caa3e1
    resource: repo://projects/tweakium/forge/versions/1.21.1/src/main/kotlin/site/siredvin/tweakium/modules/platform/ForgeComputerPlatformToolkit.kt
  - id: openwiki-source-2bb4b650a7733af7fb0948e5
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/TweakiumCore.kt
generated: { by: "hermes", at: "2026-10-16T16:31:45.808Z" }
---

# Tweakium ComputerCraft loader boundaries

## Bootstrap and service ownership

TweakiumCore.configure installs ComputerPlatformToolkit and the mod's TweakiumPlatform, adds a Broccolium block energy callback wrapping turtle fuel, and registers generic peripheral discovery. FabricTweakium configures services during object initialization and forces FabricBroccolium before common content registration in onInitialize. ForgeTweakium configures services and common registrations, then attaches deferred block/item/block-entity registries to the KotlinForForge bus. Its sayHi forces Broccolium initialization when invoked by dependents.

The 1.21.1 NeoForge override additionally attaches data-component, pocket/turtle UpgradeType and criterion-trigger registers. Names still say Forge, but imports, event bus, capabilities and upgrade APIs are NeoForge/current-version specific. Do not move these calls into common code.

## Discovering a peripheral

Common block entities expose IPeripheralProvider.getPeripheral(side); adapters bridge it to ComputerCraft:

- Fabric registers a PeripheralLookup fallback once. If its supplied block entity implements IPeripheralProvider, it forwards the context side; otherwise it returns null. Direct getPeripheral uses PeripheralLookup.find.
- Legacy Forge registers a ForgeComputerCraftAPI provider once, resolves the block entity and returns its peripheral inside LazyOptional or empty. Direct lookup uses ComputerCraft's Peripherals helper.
- NeoForge 1.21.1 installs a RegisterCapabilitiesEvent listener once and registers a peripheral capability factory for each block-entity type present when the event runs. It only forwards IPeripheralProvider when side is non-null. Direct lookup first queries PeripheralCapability and then asks for a generic peripheral.

Registering the listener too late or forgetting the owning loaded mod is not repaired by a Kotlin interface alone. Null-side behavior is also not identical across these implementations.

## Upgrade and fake-player API boundary

Fabric 1.20.1 registers TurtleUpgradeSerialiser/PocketUpgradeSerialiser into ComputerCraft registries obtained from BuiltInRegistries; missing registries throw IllegalStateException. Its 1.21.1 override registers UpgradeType values instead. The NeoForge toolkit's upgrade resolution accepts HolderLookup.Provider and creates upgraded item stacks using CC data components. Treat it as a public API/version boundary, not a loader import substitution. [Registration and providers](../modules/platform-data.md) and [upgrade persistence](../modules/upgrades-persistence.md) trace the shared consumers.

Fake-player construction routes through the toolkit. FabricFakePlayer specializes Fabric's FakePlayer, restores the current player's advancement player binding when one exists, ignores die, allows player harm and uses custom eye/attack-strength behavior. Legacy ForgeFakePlayer specializes Forge's class, disables menu opening and riding, and also changes eye/attack-strength behavior. Host identity and inventory preparation are owned by [player providers](../modules/player.md), not by peripheral lookup.

## Energy and test isolation

The legacy ForgeEnergyPlugin is a small IEnergyStorage reader: Lua methods getEnergy/getEnergyCapacity return Int and getEnergyUnit returns FE; all are main-thread methods. It is not the generic transferable [energy plugin](../modules/plugins.md) or proof of every 1.21.1 backend's API shape.

Loader test-mod source sets and loaded-mod declarations live in build scripts separately from production entrypoints. The [verification guide](../../testing/verification.md) covers the native NeoForge inventory regression and Lua fixture runs. No loader startup or application tests were run for this wiki.
