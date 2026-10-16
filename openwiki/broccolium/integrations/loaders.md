---
type: integration
title: Broccolium loader and energy integrations
description: Bootstrap and discovery boundaries for Fabric Transfer API, Forge capabilities, NeoForge capabilities, and optional Team Reborn energy.
tags: [broccolium, loaders, fabric, forge, neoforge, storage]
verified:
  - by: openwiki/0.7.1
    at: 2026-10-16T19:56:35.545Z
sources:
  - id: openwiki-source-cc02d3f8883805a8e9de0ccf
    resource: repo://docs/module-test-coverage.md
  - id: openwiki-source-81d5f1627e19148569f46f81
    resource: repo://gradle/libs.versions.toml
  - id: openwiki-source-5a932ff48527d11e7d8ba2b6
    resource: repo://projects/broccolium/fabric/src/main/kotlin/site/siredvin/broccolium/FabricBroccolium.kt
  - id: openwiki-source-7f00c000fe16d021a295417f
    resource: repo://projects/broccolium/fabric/src/main/kotlin/site/siredvin/broccolium/FabricBroccoliumClient.kt
  - id: openwiki-source-60861bd1eefabb7cafccbace
    resource: repo://projects/broccolium/fabric/src/main/kotlin/site/siredvin/broccolium/integrations/team_reborn_energy/Integration.kt
  - id: openwiki-source-6ea4411bef5ba9741f17d928
    resource: repo://projects/broccolium/fabric/src/main/kotlin/site/siredvin/broccolium/modules/base/FabricIntegrationLoader.kt
  - id: openwiki-source-d0e6990b3e6ef4362ed686f4
    resource: repo://projects/broccolium/fabric/src/main/kotlin/site/siredvin/broccolium/modules/storage/FabricStorageUtils.kt
  - id: openwiki-source-cf5c641493e267f8c915e510
    resource: repo://projects/broccolium/fabric/src/main/resources/fabric.mod.json
  - id: openwiki-source-622eee4d037ac81b5d241203
    resource: repo://projects/broccolium/fabric/src/testMod/kotlin/site/siredvin/broccolium/testmod/EnergyIntegrationGameTests.kt
  - id: openwiki-source-beb741f3cea2ac482ee415cd
    resource: repo://projects/broccolium/forge/src/main/kotlin/site/siredvin/broccolium/ForgeBroccolium.kt
  - id: openwiki-source-6e04833b48db0ec4b34ae9cd
    resource: repo://projects/broccolium/forge/src/main/kotlin/site/siredvin/broccolium/modules/storage/ForgeStorageUtils.kt
  - id: openwiki-source-0cdd3c9cf441e0c9e6af23fa
    resource: repo://projects/broccolium/forge/versions/1.21.1/src/main/kotlin/site/siredvin/broccolium/ForgeBroccolium.kt
  - id: openwiki-source-cee561019cf531a869b28282
    resource: repo://projects/broccolium/forge/versions/1.21.1/src/main/kotlin/site/siredvin/broccolium/modules/storage/ForgeStorageUtils.kt
  - id: openwiki-source-cb14f0824f9fc19f59ad8f02
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/base/BaseIntegrationLoader.kt
generated: { by: "hermes", at: "2026-10-16T19:56:35.545Z" }
---

# Broccolium loader and energy integrations

## Bootstrap ownership

The common library owns lookup chains and agnostic storage contracts; loader entrypoints populate those chains. FabricBroccolium's object initialization configures toolkit/tags/ingredients, installs block item/fluid and inventory-item fluid lookup callbacks, records the Minecraft server on ServerWorldEvents.LOAD, and attempts the optional energy integration. Its onInitialize is empty: forcing object initialization is consequential even when calling the empty sayHi method.

ForgeBroccolium also configures the common services in object initialization, then installs item, fluid and energy callbacks for blocks, entities and inventory items. The 1.21.1 full-file override imports NeoForge but preserves that registration shape. “forge” is therefore a historical branch identity, not proof that the runtime is Forge on every supported version.

Fabric metadata separates main, client and datagen entrypoints, declares the access widener and mixin config, and targets Java 17 / Minecraft 1.20 in the shared snapshot. FabricBroccoliumClient is deliberately empty and client-scoped. Consult version-local metadata rather than extending shared version constraints blindly.

## Storage discovery is not equivalent across loaders

- Fabric block item/fluid discovery tries the unsided lookup first and only falls back to the requested non-null direction when it misses. Item wrappers preserve native slotting when available. Fluid-containing inventory items are looked up through ContainerItemContext tied to the origin slot, allowing changes to the containing item.
- Forge 1.20.1 block capability helpers likewise try unsided, then sided lookup, reject removed block entities, and finally accept direct handler implementations. Item helpers can wrap a vanilla Container. Entity entrypoints differ: they require the entity itself to implement the handler, rather than using the block helper's capability path.
- NeoForge 1.21.1 queries typed level/entity/item capabilities. Block lookups require a nonremoved block entity and pass direction through, including null. Fluid and energy entity lookups also pass direction through. Entity item lookup uses nullable null context, not a forced Kotlin non-null Void cast.

These differences matter to providers with sided access policies. Do not promise that null means UP or that requesting a side always excludes unsided providers. A NeoForge chest-minecart regression is routed from the [verification guide](../../testing/verification.md); compilation alone does not exercise capability discovery.

## Optional energy integration

FabricIntegrationLoader checks FabricLoader's loaded mod IDs. BaseIntegrationLoader constructs the conventional integrations.<modID>.Integration class reflectively only when the mod exists; reflective construction exceptions are logged and yield Optional.empty. This is not a blanket catch of every linkage Error, and the subsequent Runnable.run is outside that construction catch.

For team_reborn_energy, run installs block and inventory-item energy callbacks, not an entity callback. Block discovery tries unsided then requested direction. Item energy uses a ContainerItemContext with FabricStorageUtils.getSlot: native Fabric origins retain their native slot/transaction; other origins use the agnostic bridge's explicit replacement contract. Missing providers return null so lookup fallback remains possible.

The dependency catalog pins energy API 3.0.0 for 1.20.1 and 4.1.0 for 1.21.1. A native lookup with 3.0.0 on modern Minecraft reproduced a ResourceLocation constructor linkage error; the modern-only pin fixes that path without changing the legacy dependency. Required EnergyIntegrationGameTests check absent block/item providers and unchanged native/agnostic item contents on both versions. This proves lookup compatibility, not every energy-provider transfer implementation.

## Transactions and extension cautions

FabricStorageUtils identifies transactional storage with movableType = fabricTransaction. Cross-adapter helpers explicitly extract, insert, return remainder and commit a Fabric transaction, but the other storage is mutated outside that transaction. This is not a distributed atomic transfer guarantee. Item/fluid side and unit conversions should be reviewed alongside the [storage contracts](../modules/storage.md), especially when adding a non-Fabric sink.

Add loader-native APIs in this branch, register the resulting agnostic callback at bootstrap, and keep callers in common code on the [platform boundary](../modules/platform-base.md). Follow [source precedence](../../build/stonecutter-publication.md) before editing a 1.21.1 override. The coverage task executed the native lookup regression; the [verification guide](../../testing/verification.md) distinguishes that evidence from broader integration claims.
