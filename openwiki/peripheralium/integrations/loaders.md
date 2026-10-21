---
type: integration
title: Peripheralium loader integration
description: Platform-first registration, correct metadata and test-only fixture ownership.
tags: [peripheralium, loaders, publication]
sources:
  - id: openwiki-source-46cc0af9773fbef0cab9dc18
    resource: repo://docs/verification/minecraft-26-1-R4.md
  - id: openwiki-source-d6a84e4b4f1771cf32182cf7
    resource: repo://docs/verification/minecraft-26-1-R5.md
  - id: openwiki-source-851986a2438c3c75fd1c9bb7
    resource: repo://docs/verification/minecraft-26-1-R7-final-integration.md
  - id: openwiki-source-2c8a91b99fe2b1a1f912349e
    resource: repo://projects/peripheralium/fabric/src/main/kotlin/site/siredvin/peripheralium/FabricPeripheralium.kt
  - id: openwiki-source-cdb7aafc87320c7c89edfb0b
    resource: repo://projects/peripheralium/fabric/versions/26.1.2/src/main/resources/fabric.mod.json
  - id: openwiki-source-920ea6b16d6a154fe6fe9a2e
    resource: repo://projects/peripheralium/forge/src/main/kotlin/site/siredvin/peripheralium/ForgePeripheralium.kt
  - id: openwiki-source-1caeff3947e244f41f2b5b57
    resource: repo://projects/peripheralium/forge/versions/26.1.2/src/main/kotlin/site/siredvin/peripheralium/ForgePeripheralium.kt
  - id: openwiki-source-eae6966e6bd879d2f745efae
    resource: repo://projects/peripheralium/forge/versions/26.1.2/src/main/resources/META-INF/neoforge.mods.toml
generated: { by: "hermes", at: "2026-10-21T17:56:43.998Z" }
verified:
  - by: openwiki/0.7.1
    at: 2026-10-21T17:56:43.998Z
---

# Peripheralium loader integration

[Architecture overview](../../architecture/overview.md) · [Content/provider map](../content.md#content-and-provider-map)

## Registration sequence

```mermaid
sequenceDiagram
  accTitle: Peripheralium platform-first registration
  accDescr: Fabric configures its adapter during singleton initialization, then initializes Tweakium and calls common registration. Forge-family initializes dependencies, configures its adapter, calls common registration, and attaches deferred registries to the mod event bus.
  participant L as Loader entrypoint
  participant D as Tweakium adapter
  participant P as Peripheralium platform
  participant H as Common hook
  participant R as Native registry
  alt Fabric
    L->>P: Configure Fabric adapter in init
    L->>D: sayHi in onInitialize
    L->>H: onRegister
    H->>P: Initialize Blocks, Items and tab
    P->>R: Register through Fabric platform
  else Forge family
    L->>D: sayHi
    L->>P: Configure Forge-family adapter
    L->>H: onRegister
    H->>P: Enqueue Blocks, Items and tab
    L->>R: Attach deferred registers to mod bus
  end
```

The branches deliberately preserve the different dependency/configuration order in [Fabric](../../../projects/peripheralium/fabric/src/main/kotlin/site/siredvin/peripheralium/FabricPeripheralium.kt#L6-L19), [Forge 1.20.1](../../../projects/peripheralium/forge/src/main/kotlin/site/siredvin/peripheralium/ForgePeripheralium.kt#L18-L37), [NeoForge 1.21.1](../../../projects/peripheralium/forge/versions/1.21.1/src/main/kotlin/site/siredvin/peripheralium/ForgePeripheralium.kt#L16-L38) and [26.1.2](../../../projects/peripheralium/forge/versions/26.1.2/src/main/kotlin/site/siredvin/peripheralium/ForgePeripheralium.kt#L15-L33). Modern adapters also attach a criterion-trigger register; the old entrypoint does not have that register. Configuring before the common hook is the shared invariant, not identical loader execution. Target [block](../../../projects/peripheralium/versions/26.1.2/src/main/kotlin/site/siredvin/peripheralium/Blocks.kt#L9-L16) and [item](../../../projects/peripheralium/versions/26.1.2/src/main/kotlin/site/siredvin/peripheralium/Items.kt#L9-L19) factories provide registry keys before construction.

### Runtime, datagen and test entrypoints are different routes

```mermaid
flowchart TB
  accTitle: Loader integrations without a reverse production dependency
  accDescr: Main entrypoints register Peripheralium content. Separate datagen callbacks emit resources. Test-only wiring loads Peripheralium and its fixtures into Tweakium native runners, which use Testiarium; this is not a production dependency on the harness.
  MD["Matching loader metadata"] --> M["Main Peripheralium entrypoint"]
  M --> C["Registered material content"]
  DG["Fabric datagen / NeoForge GatherData"] --> DP["Peripheralium providers via Broccolium sinks"]
  DP --> J["Generated resource JSON"]
  PT["Peripheralium testMod"] -.->|test-only integration| TR["Tweakium native runner"]
  C -.->|loaded production mod under test| TR
  TR -.->|harness and optional fixtures| H["Testiarium"]
```

Solid arrows are production/datagen routes; dashed arrows are test-runner assembly. [Fabric metadata](../../../projects/peripheralium/fabric/versions/26.1.2/src/main/resources/fabric.mod.json#L17-L29) separates main from fabric-datagen and pins the target Java/Minecraft boundary. [Legacy Fabric test wiring](../../../projects/peripheralium/fabric/build.gradle.kts#L93-L100) adds fixture output only to the integration runner; target content/fixture runner assembly is in the [shared convention](../../../gradle/minecraft-26-1.gradle.kts#L473-L492). See [feature/artifact isolation](../../build/stonecutter-publication.md#main-output-versus-test-features), not a new production Testiarium edge.

## Registration and metadata

Fabric configures the platform before common registration and forces dependency initialization through established adapters. Forge-family supplies deferred block/item/tab registries (plus criterion triggers on modern nodes) and attaches them to its loader bus. Forge means legacy Forge only on1.20.1;1.21.1/26.1.2 use NeoForge. Keep loader imports out of common content and read matching version overrides.

Target keyed construction, loader schema and Java25 metadata are in26.1.2 overrides. Check actual packaged metadata/JVM69/dependencies: an old permissive metadata bound is not evidence old Java can load modern bytecode. Modern content intentionally omits legacy blend/smelting.

## Fixture and publication isolation

Peripheralium owns behavior/recipe fixtures. Native Tweakium runners load actual Peripheralium common/loader mod outputs plus matching test features; reverse integration stays test-only. Main JAR scans compare exact compiled fixture identities, and all POM/GMM variants are checked for test/viewer leakage.

[R4](../../../docs/verification/minecraft-26-1-R4.md) verifies both target adapters and deterministic data. [R5](../../../docs/verification/minecraft-26-1-R5.md) establishes full publication/consumer/active-state checks; [R7 integration](../../../docs/verification/minecraft-26-1-R7-final-integration.md) refreshes final publication/graphical/process proof. Development viewers follow approved EMI-old/JEI-target policy, no active REI dependency; they are not publication runtime requirements.

See [content](../content.md), [build](../../build/stonecutter-publication.md) and [verification](../../testing/verification.md).
