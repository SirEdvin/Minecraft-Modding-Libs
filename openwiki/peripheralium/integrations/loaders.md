---
type: integration
title: Peripheralium loader bootstrap
description: Fabric immediate registration versus Forge/NeoForge deferred registries, bootstrap ordering, loader metadata and version-local content boundaries.
tags: [peripheralium, loaders, fabric, forge, neoforge]
sources:
  - id: openwiki-source-2a9daaac1604f238ef4c63fb
    resource: repo://build.gradle.kts
  - id: openwiki-source-0e18fe1e8a3fb313dbba48ae
    resource: repo://projects/peripheralium/build.gradle.kts
  - id: openwiki-source-e353c9fb3e0d64fda950d8a4
    resource: repo://projects/peripheralium/fabric/build.gradle.kts
  - id: openwiki-source-398c88e7fbcc98a060729f03
    resource: repo://projects/peripheralium/fabric/src/main/kotlin/site/siredvin/peripheralium/FabricInnerBasePeripheraliumPlatform.kt
  - id: openwiki-source-2c8a91b99fe2b1a1f912349e
    resource: repo://projects/peripheralium/fabric/src/main/kotlin/site/siredvin/peripheralium/FabricPeripheralium.kt
  - id: openwiki-source-dabba8e77c3a68533e0c6ca6
    resource: repo://projects/peripheralium/fabric/src/main/resources/fabric.mod.json
  - id: openwiki-source-3517b365146b73928d5347bf
    resource: repo://projects/peripheralium/fabric/versions/1.21.1/src/main/resources/fabric.mod.json
  - id: openwiki-source-3e894cc6715c9011217a07a3
    resource: repo://projects/peripheralium/forge/build.gradle.kts
  - id: openwiki-source-920ea6b16d6a154fe6fe9a2e
    resource: repo://projects/peripheralium/forge/src/main/kotlin/site/siredvin/peripheralium/ForgePeripheralium.kt
  - id: openwiki-source-2291a53fce44f1b3d50b2d16
    resource: repo://projects/peripheralium/forge/src/main/kotlin/site/siredvin/peripheralium/ForgePeripheraliumPlatform.kt
  - id: openwiki-source-3c53f8cc471c857ce7ab13a7
    resource: repo://projects/peripheralium/forge/versions/1.20.1/src/main/resources/META-INF/mods.toml
  - id: openwiki-source-6e5ef1183e9088a55329aef6
    resource: repo://projects/peripheralium/forge/versions/1.21.1/src/main/kotlin/site/siredvin/peripheralium/ForgePeripheralium.kt
  - id: openwiki-source-b9f23e6002181e4fd12cff47
    resource: repo://projects/peripheralium/forge/versions/1.21.1/src/main/kotlin/site/siredvin/peripheralium/ForgePeripheraliumPlatform.kt
  - id: openwiki-source-ab9ee3169466f40b777b8345
    resource: repo://projects/peripheralium/forge/versions/1.21.1/src/main/resources/META-INF/neoforge.mods.toml
  - id: openwiki-source-dc4cb15007714eab0b3c3f9f
    resource: repo://projects/peripheralium/src/main/kotlin/site/siredvin/peripheralium/Items.kt
  - id: openwiki-source-840eb0c8862ea4c55aa3aecd
    resource: repo://projects/peripheralium/versions/1.21.1/src/main/kotlin/site/siredvin/peripheralium/data/PeripheraliumRecipeProvider.kt
  - id: openwiki-source-5372e85818afbf529f1d42be
    resource: repo://projects/peripheralium/versions/1.21.1/src/main/kotlin/site/siredvin/peripheralium/Items.kt
generated: { by: "hermes", at: "2026-10-16T19:56:35.545Z" }
verified:
  - by: openwiki/0.7.1
    at: 2026-10-16T19:56:35.545Z
---

# Peripheralium loader bootstrap

## Initialization order

FabricPeripheralium configures PeripheraliumCore with FabricInnerBasePeripheraliumPlatform during object initialization. onInitialize calls sayHi (which forces FabricTweakium initialization) before CommonHooks.onRegister. The adapter only supplies modID and inherits Broccolium's immediate registry implementation. The apparently empty dependency greeting is an initialization dependency, not content registration by itself.

ForgePeripheralium owns block, item and creative-tab DeferredRegisters. Its initializer forces ForgeTweakium initialization, configures the platform, obtains the KotlinForForge event bus, invokes common registration, and attaches registries to that bus. ForgePeripheraliumPlatform forwards the needed registry fields to this object. Content suppliers are resolved by deferred registration, not eagerly by common code.

The 1.21.1 full-file ForgePeripheralium override uses NeoForge and KotlinForForge's NeoForge context. It additionally creates and attaches a criterion-trigger register and exposes it through the platform adapter. Preserve the source split even though class/package names still say Forge.

## Metadata and dependencies

Fabric metadata names the common mod initializer and datagen entrypoint, declares the access widener, has an empty mixin list, and requires ComputerCraft, Broccolium and Tweakium versions through expansion placeholders. Shared metadata has Minecraft >=1.20 <1.21; the 1.21.1 override changes it to >=1.21 <1.22. Both currently declare Java >=17, while the actual 1.21.1 build targets Java 21. That metadata lower bound is not evidence a Java 17 runtime can load Java 21 bytecode.

Legacy Forge uses version-local META-INF/mods.toml, KotlinForForge loader range [4,), mandatory=true dependencies and a Forge range capped below 48. NeoForge uses META-INF/neoforge.mods.toml, loader range [5,), type=required entries and a neoforge dependency. Keep build-time placeholder expansion and loader schema changes aligned; renaming only imports does not produce a valid loader artifact.

## Effective content and extension boundary

The version-local Items override removes peripheralium_blend on 1.21.1; only dust and upgrade template remain as standalone material items alongside the shared block. Its recipe override also omits blend smelting. Source ownership is full-file replacement, not additive merging with the 1.20.1 file. New registrations, holder-driven creative entries and datagen need review against the effective version, not just the shared source snapshot.

To add a registry kind on Forge/NeoForge, provide the DeferredRegister field, forward it through the platform adapter and attach it to the correct event bus. Fabric often needs only the inherited immediate implementation. Keep content and recipes in common/version-specific ownership described in [material content](../content.md), and loader APIs here.

## Test-only integration ownership

Peripheralium owns a common testMod source set compiled against Testiarium's main API. Its loader builds attach those test outputs to the existing Tweakium integration runner rather than introduce a second server launcher. Fabric adds the owning common test output and both production outputs to testMod classpaths. Forge/NeoForge attach the actual Peripheralium common/loader source sets as a loaded mod and add common tests to tweakium_testmod. The reverse dependency is confined to test configuration; ordinary publication dependencies still point from Peripheralium to Tweakium and Broccolium.

The four native Tweakium runs execute Peripheralium's registration, language, recipe and loot behavior tests with the real loader entrypoints. Both active-state builds and artifact-isolation checks also passed; none of that is client creative-tab rendering evidence. [Verification](../../testing/verification.md) records exact counts and [build contracts](../../build/stonecutter-publication.md) explain publication identity.
