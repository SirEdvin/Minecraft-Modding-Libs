---
type: subproject
title: Peripheralium material content and data
description: Actual material registrations, creative inventory, generated recipes and resources, including the version-specific smelting difference.
tags: [peripheralium, content, recipes, datagen]
sources:
  - id: openwiki-source-0e18fe1e8a3fb313dbba48ae
    resource: repo://projects/peripheralium/build.gradle.kts
  - id: openwiki-source-babb23fa62ec9812463d96ee
    resource: repo://projects/peripheralium/src/main/kotlin/site/siredvin/peripheralium/Blocks.kt
  - id: openwiki-source-9dbf4e1e83f92eb68e8f2619
    resource: repo://projects/peripheralium/src/main/kotlin/site/siredvin/peripheralium/data/PeripheraliumBlockModelProvider.kt
  - id: openwiki-source-965e81411f8caacc081fccd5
    resource: repo://projects/peripheralium/src/main/kotlin/site/siredvin/peripheralium/data/PeripheraliumDataProviders.kt
  - id: openwiki-source-fc73064481993d1693bbb085
    resource: repo://projects/peripheralium/src/main/kotlin/site/siredvin/peripheralium/data/PeripheraliumItemModelProvider.kt
  - id: openwiki-source-db3536677f9a08df37e5d4e6
    resource: repo://projects/peripheralium/src/main/kotlin/site/siredvin/peripheralium/data/PeripheraliumLootTableProvider.kt
  - id: openwiki-source-62c44a8b89b7c2d15ffb6682
    resource: repo://projects/peripheralium/src/main/kotlin/site/siredvin/peripheralium/data/PeripheraliumRecipeProvider.kt
  - id: openwiki-source-dc4cb15007714eab0b3c3f9f
    resource: repo://projects/peripheralium/src/main/kotlin/site/siredvin/peripheralium/Items.kt
  - id: openwiki-source-7e531e71449f83936bf3c83e
    resource: repo://projects/peripheralium/src/main/kotlin/site/siredvin/peripheralium/PeripheraliumCommonHooks.kt
  - id: openwiki-source-27916c71ae1e8e7d160f278c
    resource: repo://projects/peripheralium/src/main/kotlin/site/siredvin/peripheralium/PeripheraliumCore.kt
  - id: openwiki-source-ac93722eebadeb16b68f7171
    resource: repo://projects/peripheralium/src/main/kotlin/site/siredvin/peripheralium/PeripheraliumPlatform.kt
  - id: openwiki-source-43613e48de6cc126a43231ad
    resource: repo://projects/peripheralium/src/testMod/kotlin/site/siredvin/peripheralium/testmod/PeripheraliumBehaviorGameTests.kt
  - id: openwiki-source-ef48181080f96946c9ce80c2
    resource: repo://projects/peripheralium/src/testMod/kotlin/site/siredvin/peripheralium/testmod/RecipeContractAssertions.kt
  - id: openwiki-source-840eb0c8862ea4c55aa3aecd
    resource: repo://projects/peripheralium/versions/1.21.1/src/main/kotlin/site/siredvin/peripheralium/data/PeripheraliumRecipeProvider.kt
  - id: openwiki-source-5372e85818afbf529f1d42be
    resource: repo://projects/peripheralium/versions/1.21.1/src/main/kotlin/site/siredvin/peripheralium/Items.kt
generated: { by: "hermes", at: "2026-10-16T19:56:35.545Z" }
verified:
  - by: openwiki/0.7.1
    at: 2026-10-16T19:56:35.545Z
---

# Peripheralium material content and data

## What this subproject owns

Peripheralium's runtime is a material/content layer: peripheralium_block, peripheralium_dust and peripheralium_upgrade_template on both versions, plus peripheralium_blend on 1.20.1 only. The 1.21.1 Items override replaces rather than augments the shared registrations. Do not infer a peripheral method-dispatch implementation from the library name; that framework lives in [Tweakium's peripheral module](../tweakium/modules/peripheral.md). The common build depends on Broccolium and Tweakium at the same Minecraft version.

PeripheraliumCore.configure installs the mod's InnerBasePlatform. PeripheraliumPlatform delegates through an initially-null implementation and owns a ModInformationTracker. CommonHooks.onRegister forces Blocks and Items object initialization via their empty doSomething methods, then registers a creative tab. The tab uses dust as icon and enumerates tracked items and blocks. Configuration must precede registration; the platform getter force-unwraps its implementation rather than delaying registration automatically.

## Content and data flow

PeripheraliumDataProviders composes recipes, block loot, models and English/Ukrainian language generation through Broccolium's GeneratorSink. The block gets a cube-all model, a blockstate variant and delegated item model; standalone items get flat models (the shared legacy provider includes blend). Loot generation drops the registered block itself and invokes holder-backed validation.

The recipe flow uses platform ingredients, so redstone, glowstone dust and experience-bottle inputs come from the loader-neutral ingredient boundary. Both version implementations define dust from redstone plus glowstone, nine-dust block compression, nine-dust block decompression under peripheralium_block_uncraft, and four upgrade templates using the PPP / PXP / P P shaped pattern.

## Version-specific recipe boundary

The 1.20.1 recipe provider additionally smelts blend into dust with experience 0.7 and cooking time 200 under peripheralium_dust_smelting. The 1.21.1 full-file override uses registry-aware RecipeOutput and omits that recipe; its Items override also omits blend. Native registration and emitted-recipe tests preserve this existing version difference rather than inventing modern parity. These checks do not describe every external datapack in an arbitrary live world.

## Extending and verifying

Add shared materials through the owning Blocks/Items objects, then update recipes, loot/models and both language providers together. Name-based BasePlatform registration feeds the tracker used by datagen and creative listing. For lifecycle ordering and loader registry attachment see [loader bootstrap](integrations/loaders.md); for duplicate/missing data failures see [Broccolium datagen](../broccolium/modules/data.md).

Peripheralium now owns testMod behavior tests and shared/version-specific recipe assertions. They run inside the existing Tweakium loader runners with actual Peripheralium mod registration: exact IDs/tracker identity, emitted recipe shapes and invalid-pattern rejection, both language outputs and real block-loot results. Loaded crafting tests use real ingredients, accept shapeless input reordering and reject a wrong template center. The four-target [coverage acceptance](../../docs/module-test-coverage.md) passed; it does not establish creative-tab rendering or all downstream recipes. Test-only wiring is described under [loader bootstrap](integrations/loaders.md).
