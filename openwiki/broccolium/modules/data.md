---
type: module
title: Broccolium data generation
description: Loader-neutral provider composition, registry-backed language validation, models, recipes and version-aware generator sinks.
tags: [broccolium, datagen, resources, registries]
verified:
  - by: openwiki/0.7.1
    at: 2026-10-16T16:31:45.808Z
sources:
  - id: openwiki-source-9b54a3227ec60bc9c3ad43a3
    resource: repo://projects/broccolium/fabric/src/main/kotlin/site/siredvin/broccolium/modules/data/FabricGeneratorSink.kt
  - id: openwiki-source-b4571e30ea705482a3af1d58
    resource: repo://projects/broccolium/fabric/versions/1.21.1/src/main/kotlin/site/siredvin/broccolium/modules/data/AutomaticDynamicRegistryProvider.kt
  - id: openwiki-source-562813cd5789db32570b1e00
    resource: repo://projects/broccolium/forge/versions/1.21.1/src/main/kotlin/site/siredvin/broccolium/modules/data/ForgeGeneratorSink.kt
  - id: openwiki-source-2ec9d76a648b9608b6f2c337
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/data/api/GeneratorSink.kt
  - id: openwiki-source-90c8b318fc2f5139061b7577
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/data/lang/LanguageProvider.kt
  - id: openwiki-source-03c1e844c19ad421f3a9512d
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/data/LibDataProviders.kt
  - id: openwiki-source-491ac05b8a09135fd450f434
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/data/model/ModelProvider.kt
  - id: openwiki-source-1d44d265dd15013291f4cc18
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/data/recipe/TweakedShapedRecipeBuilder.kt
  - id: openwiki-source-eca924910363f436f4ad9770
    resource: repo://projects/broccolium/versions/1.21.1/src/main/kotlin/site/siredvin/broccolium/modules/data/api/GeneratorSink.kt
generated: { by: "hermes", at: "2026-10-16T16:31:45.808Z" }
---

# Broccolium data generation

## Composition and ownership

GeneratorSink is the common extension seam: register DataProvider factories, loot subproviders, block/entity/item tags, and block/item model consumers without importing a loader API. LibDataProviders itself only adds English and Ukrainian language providers. Consumer libraries compose their own provider lists; Broccolium does not automatically generate every dependent mod's content.

Fabric's LibDataGenerator creates a pack and passes a FabricGeneratorSink to that list. Forge's GatherDataEvent subscriber creates its sink from the vanilla pack, existing-file helper and registry lookup future. Fabric tag adapters build LibTagAppender instances against platform registries; item tags retain a dependency on the block provider so copy operations can delegate correctly. The Fabric implementation casts the supplied block provider to its native provider type: do not mix sinks when wiring that dependency.

## Language and model failure contracts

LanguageProvider runs addTranslations, then checks every expected key before saving assets/<modID>/lang/<locale>.json. Expected keys derive from tracked blocks, items, custom stats and explicitly supplied TextRecords. Duplicate add calls fail immediately; missing expected keys fail generation. This is validation of the holder's known set, not an exhaustive check of every string a mod might display. Registration tracking details live in [platform and base](platform-base.md).

ModelProvider collects caller-generated blockstates and models into maps, rejects duplicate block/model definitions, and creates delegated item models for generated blocks unless an explicit item model was requested. It writes stable JSON using registry keys and returns the fail-fast combination of output futures. It does not generate blockstates for all registered blocks merely because they exist.

The 1.20.1 TweakedShapedRecipeBuilder validates duplicate/reserved symbols, row width, missing pattern, undefined/unused ingredients and the single-item shaped case. Its FinishedRecipe emits no advancement. Treat its behavior as version-specific rather than assuming vanilla advancement-unlock behavior or the newer registry-aware recipe API.

## 1.21.1 registry-aware extension

The version override adds a TweakedDataProviderFactory overload receiving a registry future and addRegistryPatch to GeneratorSink. NeoForge delegates patches to DatapackBuiltinEntriesProvider and uses event.includeServer for provider inclusion. Fabric's AutomaticDynamicRegistryProvider takes the patched registry view and adds entries from Fabric's dynamic registry list. This is an API boundary change, not just an import rename: factories needing registries belong on the version-aware path.

## Changing generated assets safely

Change provider code and its registration inputs, not src/generated output by hand. Then run the owning loader's datagen task in an intentional implementation task and review generated resources. This documentation run did not execute datagen, builds or language/model tests; the failure contracts above are established from source.

For downstream examples see [Peripheralium material content](../../peripheralium/content.md) and [Tweakium upgrade providers](../../tweakium/modules/platform-data.md). For source overlays and build-output boundaries see [Stonecutter ownership](../../build/stonecutter-publication.md).
