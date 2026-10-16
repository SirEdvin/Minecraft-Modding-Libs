---
type: module
title: Tweakium registration, data and creative filler
description: Computer-specific registration tracking, serializer-to-UpgradeType migration, generated upgrade registries/language keys, and the creative filler end-to-end flow.
tags: [tweakium, platform, datagen, upgrades, creative-filler]
verified:
  - by: openwiki/0.7.1
    at: 2026-10-16T16:31:45.808Z
sources:
  - id: openwiki-source-326cf83fe902c9cb5fc2b170
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/data/ComputerLanguageProvider.kt
  - id: openwiki-source-3e0634914368c5bb807943fa
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/minecraft/blockentity/CreativeFillerBlockEntity.kt
  - id: openwiki-source-f496f70ed632eb742b114254
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/minecraft/computercraft/CreativeFillerPeripheral.kt
  - id: openwiki-source-6860c2c7b214f835f9c3b809
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/minecraft/setup/TweakiumBlocks.kt
  - id: openwiki-source-70f6a80e70084aa8b40c0a2e
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/minecraft/xplat/TweakiumCommonHooks.kt
  - id: openwiki-source-cf355555b7c4339c4b0f63fd
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/platform/ComputerBasePlatform.kt
  - id: openwiki-source-6ffcba0b5c5a2ebeb898ad3c
    resource: repo://projects/tweakium/src/testMod/kotlin/site/siredvin/tweakium/testmod/CreativeFillerGameTests.kt
  - id: openwiki-source-b492484d5e85181c8386488f
    resource: repo://projects/tweakium/src/testMod/resources/computer/tests/creativefillergametests.fillsinventory.lua
  - id: openwiki-source-68fc30845d045147991932e6
    resource: repo://projects/tweakium/versions/1.20.1/src/main/kotlin/site/siredvin/tweakium/modules/data/LibPocketUpgradeDataProvider.kt
  - id: openwiki-source-675aa593b4001d49a732393f
    resource: repo://projects/tweakium/versions/1.21.1/src/main/kotlin/site/siredvin/tweakium/modules/data/ComputerLanguageProvider.kt
  - id: openwiki-source-8488946f1c46fbc867f61039
    resource: repo://projects/tweakium/versions/1.21.1/src/main/kotlin/site/siredvin/tweakium/modules/data/UpgradeDataProviders.kt
  - id: openwiki-source-4c6415eadec10b788c8f88c5
    resource: repo://projects/tweakium/versions/1.21.1/src/main/kotlin/site/siredvin/tweakium/modules/minecraft/computercraft/CreativeFillerPeripheral.kt
  - id: openwiki-source-48d28208163ce1462435f74f
    resource: repo://projects/tweakium/versions/1.21.1/src/main/kotlin/site/siredvin/tweakium/modules/platform/ComputerBasePlatform.kt
generated: { by: "hermes", at: "2026-10-16T16:31:45.808Z" }
---

# Tweakium registration, data and creative filler

## Computer-specific registration façade

ComputerBasePlatform extends Broccolium BasePlatform and owns a ComputerModInformationTracker. In 1.20.1 it registers turtle/pocket serializers through InnerComputerBasePlatform and records their suppliers in the tracker. ComputerLanguageProvider derives expected adjective keys from those serializer IDs in addition to base block/item/stat/text keys. Consumer registration and language validation are connected; bypassing tracking can hide omissions from that validation.

The 1.21.1 full-file override registers UpgradeType and returns keyed RegistryEntry values. Custom-item helpers create types whose codec encodes an item and constructs the upgrade with its own type reference, or use reduced factories with UpgradeType.simpleWithCustomItem. IDs and factories are meaningful extension contracts, not interchangeable serializers. ComputerLanguageProvider now reads keyed tracked upgrade entries directly.

## Upgrade data flow across versions

The 1.20.1 LibPocketUpgradeDataProvider collects serializers touched by registerUpgrades, passes each generated definition to CC's provider, and rejects expected serializers with no default item definition. That catches missing definitions, not arbitrary gameplay correctness. Turtle provider is the sibling seam; use effective version-specific source before extending it.

In 1.21.1 GeneratorSink.upgrades instead installs a registry patch over IPocketUpgrade.REGISTRY and ITurtleUpgrade.REGISTRY with supplied bootstrap functions. Registry-aware data flows through [Broccolium sinks](../../broccolium/modules/data.md). Runtime loader registration of UpgradeType remains a distinct step from generating data-defined upgrades. See [upgrade lifecycle](upgrades-persistence.md) and [loader adapters](../integrations/loaders.md).

## Shipped creative-filler path

CommonHooks.onRegister forces TweakiumBlocks and TweakiumBlockEntityTypes initialization. creative_filler is a nonticking GenericBlockEntityBlock; its entity lazily creates CreativeFillerPeripheral with a BlockEntityPeripheralOwner. The same entity exposes item/fluid/energy void sinks, so inserting resources into it is deliberately destructive rather than persistent storage.

CreativeFillerPeripheral has type creative_filler and is always enabled. Its main-thread put(mode,target,id,limit=64) chooses item/fluid/energy strategy, resolves a named available peripheral, derives a side when ISidedPeripheral is present, and asks Broccolium's sink lookup to adapt the target. It resolves the item/fluid/energy ID, constructs the resource and stores it directly. It does not transfer from an existing source, return an accepted amount or retain a remainder. Item quantities are upper-clamped to the item stack limit; this path does not share plugins' general nonnegative argument validator. Missing target/sink/resource and invalid mode raise errors.

The 1.21.1 implementation uses ResourceLocation.parse and ItemStack construction with defaultMaxStackSize rather than the shared legacy constructors. Creation is a privileged creative/testing-style capability; the implementation itself does not add a per-call player ownership permission check merely because the peripheral has an owner.

## Tests and changes

The Lua fixture finds creative_filler and a barrel, requires it empty, creates one stone, verifies exactly slot 1 with one stone, then checks invalid mode, missing target, wrong target-argument type and missing item failures. Its GameTest waits for Lua completion using Testiarium. This is concrete item-path coverage, not fluid/energy/negative-limit/world-reload coverage. No fixture was run for wiki creation.

When extending the public surface, update the owning Kotlin API, datagen/language inputs, [typed client contracts](../../typed-peripheral-api/contracts.md) where relevant, and focused fixtures together. Do not manually edit generated resources.
