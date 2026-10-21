---
type: subproject
title: Peripheralium content and resources
description: Material identities, keyed registration and native/data/client evidence.
tags: [peripheralium, content, datagen]
sources:
  - id: openwiki-source-46cc0af9773fbef0cab9dc18
    resource: repo://docs/verification/minecraft-26-1-R4.md
  - id: openwiki-source-851986a2438c3c75fd1c9bb7
    resource: repo://docs/verification/minecraft-26-1-R7-final-integration.md
  - id: openwiki-source-6ab7f119adb84fcadf0efff5
    resource: repo://projects/peripheralium/fabric/versions/26.1.2/src/main/kotlin/site/siredvin/peripheralium/PeripheraliumDataGenerator.kt
  - id: openwiki-source-9a4236219718115897ad5d0b
    resource: repo://projects/peripheralium/forge/versions/26.1.2/src/main/kotlin/site/siredvin/peripheralium/ForgePeripheraliumDataGenerators.kt
  - id: openwiki-source-27916c71ae1e8e7d160f278c
    resource: repo://projects/peripheralium/src/main/kotlin/site/siredvin/peripheralium/PeripheraliumCore.kt
  - id: openwiki-source-ac93722eebadeb16b68f7171
    resource: repo://projects/peripheralium/src/main/kotlin/site/siredvin/peripheralium/PeripheraliumPlatform.kt
  - id: openwiki-source-7380d36f7ad5fb3df8b2983e
    resource: repo://projects/peripheralium/versions/26.1.2/src/main/kotlin/site/siredvin/peripheralium/data/PeripheraliumDataProviders.kt
  - id: openwiki-source-a20cada7dd4dd8789477a256
    resource: repo://projects/peripheralium/versions/26.1.2/src/main/kotlin/site/siredvin/peripheralium/Items.kt
generated: { by: "hermes", at: "2026-10-21T17:43:06.090Z" }
verified:
  - by: openwiki/0.7.1
    at: 2026-10-21T17:43:06.090Z
---

# Peripheralium content and resources

[Architecture overview](../architecture/overview.md) · [Version/loader topology](../architecture/loaders.md)

## Ownership

Peripheralium supplies materials and depends on matching Broccolium/Tweakium; generic peripheral dispatch belongs to Tweakium. All maintained lines contain peripheralium_block, peripheralium_dust and peripheralium_upgrade_template. Blend and its smelting recipe remain1.20.1-only; target26.1.2 preserves modern absence rather than inheriting legacy fallback.

Configure its platform before common content/tab registration. Target factories supply keys before native block/item construction, and owning loader adapters attach deferred registries. The holder drives creative entries and data validation.

## Content and provider map

```mermaid
flowchart TB
  accTitle: Peripheralium content ownership and reusable services
  accDescr: Peripheralium owns material objects, creative entries and data providers. Broccolium supplies registration, tracking and generator adapters. Tweakium supplies reusable ComputerCraft facilities rather than Peripheralium material definitions.
  H["Common registration hook"] --> B["Blocks: peripheralium_block"]
  H --> I["Items: dust and upgrade template"]
  H --> C["Creative tab from tracked holder"]
  B --> P["PeripheraliumPlatform"]
  I --> P
  P -->|reuses| BR["Broccolium platform and tracker"]
  D["PeripheraliumDataProviders"] -->|reuses| GS["Broccolium GeneratorSink"]
  D --> R["Recipes and self-drop loot"]
  D --> M["Block/item client definitions and EN/UA"]
  TW["Tweakium: separate peripheral and upgrade facilities"]
  OLD["1.20.1 only: blend and smelting"] -.-> I
```

Arrows mean registration/composition or labeled reuse, not a new dependency graph. The unconnected Tweakium box is intentionally not a content-registration call. The [common hook](../../projects/peripheralium/versions/26.1.2/src/main/kotlin/site/siredvin/peripheralium/PeripheraliumCommonHooks.kt#L6-L14) forces block/item singleton initialization before tab registration. [Creative entries](../../projects/peripheralium/src/main/kotlin/site/siredvin/peripheralium/PeripheraliumCore.kt#L14-L23) enumerate the platform holder rather than a second manual item list. Legacy [items](../../projects/peripheralium/src/main/kotlin/site/siredvin/peripheralium/Items.kt#L6-L15) and [smelting](../../projects/peripheralium/src/main/kotlin/site/siredvin/peripheralium/data/PeripheraliumRecipeProvider.kt#L20-L42) differ from [1.21.1 items](../../projects/peripheralium/versions/1.21.1/src/main/kotlin/site/siredvin/peripheralium/Items.kt#L6-L14) and [target recipes](../../projects/peripheralium/versions/26.1.2/src/main/kotlin/site/siredvin/peripheralium/data/PeripheraliumRecipeProvider.kt#L23-L41).

### Focused platform composition

```mermaid
classDiagram
  accTitle: Peripheralium platform delegates rather than reimplements
  accDescr: PeripheraliumPlatform extends Broccolium BasePlatform, owns its tracker, and delegates native operations through a configured InnerBasePlatform. PeripheraliumCore supplies configuration and creative-tab construction.
  BasePlatform <|-- PeripheraliumPlatform
  PeripheraliumPlatform *-- ModInformationTracker : owns
  PeripheraliumPlatform o-- InnerBasePlatform : configured adapter
  PeripheraliumCore ..> PeripheraliumPlatform : configure and creative entries
  class PeripheraliumPlatform {
    +configure(impl)
    +baseInnerPlatform
    +modInformationTracker
  }
```

[PeripheraliumPlatform](../../projects/peripheralium/src/main/kotlin/site/siredvin/peripheralium/PeripheraliumPlatform.kt#L7-L17) requires configuration before its non-null native delegate is read. This small class model is the extension seam; material objects are registry content, not an invented peripheral hierarchy. Continue to the [loader registration sequence](integrations/loaders.md#registration-sequence) and reusable [Broccolium platform](../broccolium/modules/platform-base.md#service-and-module-map).

### Data generation is a separate lifecycle

```mermaid
sequenceDiagram
  accTitle: Peripheralium build-time resource generation
  accDescr: Loader datagen creates a Broccolium sink, registers Peripheralium providers, and emits recipe, loot, model and language resources. Generated resources are build inputs, not runtime peripheral dispatch.
  participant L as Loader datagen
  participant S as GeneratorSink
  participant P as Peripheralium providers
  participant O as Generated resources
  L->>S: Construct matching native sink
  L->>P: add(sink)
  P->>S: Recipes, loot, models, EN and UA
  S->>O: Execute native provider output
```

[Provider composition](../../projects/peripheralium/versions/26.1.2/src/main/kotlin/site/siredvin/peripheralium/data/PeripheraliumDataProviders.kt#L5-L12) is shared by the target [Fabric entrypoint](../../projects/peripheralium/fabric/versions/26.1.2/src/main/kotlin/site/siredvin/peripheralium/PeripheraliumDataGenerator.kt#L8-L11) and [NeoForge events](../../projects/peripheralium/forge/versions/26.1.2/src/main/kotlin/site/siredvin/peripheralium/ForgePeripheraliumDataGenerators.kt#L9-L19). These model definitions are resource generation, not Tweakium's client render execution. Native fixture checks are evidence of behavior only when actually run.

## Provider behavior and inherited evidence

Owning providers compose recipes, self-drop loot, block/item/client definitions and EN/UA languages through Broccolium. Dust crafting, block compression/decompression and four-template crafting retain their IDs/inputs/counts. Update generators, never hand-edit generated resources.

[R4](../../docs/verification/minecraft-26-1-R4.md) ports all target branches and verifies native registration/tracker identity, loaded/emitted recipes, loot and deterministic language/model inventories on both loaders. Peripheralium owns fixtures that execute through Tweakium's native runner; no reverse production cycle.

[Final R7 integration](../../docs/verification/minecraft-26-1-R7-final-integration.md) also verifies actual creative/content/book/menu interactions and refreshes affected data/publication/process evidence. External datapacks and recipe-transfer automation are not certified.

See [loaders](integrations/loaders.md), [Broccolium data](../broccolium/modules/data.md), [Tweakium dispatch](../tweakium/modules/peripheral.md) and [final limits](../../docs/minecraft-26-1-verification.md).
