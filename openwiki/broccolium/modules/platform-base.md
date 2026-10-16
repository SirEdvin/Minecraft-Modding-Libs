---
type: module
title: Broccolium platform and block lifecycle
description: Loader-neutral registration façades, initialization ordering, and block-entity ticking, ownership, persistence and synchronization contracts.
tags: [broccolium, platform, registries, persistence, lifecycle]
verified:
  - by: openwiki/0.7.1
    at: 2026-10-16T16:31:45.808Z
sources:
  - id: openwiki-source-c2498158d28666cbb872bf40
    resource: repo://projects/broccolium/fabric/src/main/kotlin/site/siredvin/broccolium/modules/platform/FabricInnerBasePlatform.kt
  - id: openwiki-source-7861b87440ff69a70450d903
    resource: repo://projects/broccolium/forge/versions/1.21.1/src/main/kotlin/site/siredvin/broccolium/modules/platform/ForgeInnerBasePlatform.kt
  - id: openwiki-source-3cc0124e56f62a35dd81b8e6
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/BroccoliumCore.kt
  - id: openwiki-source-b90203cbe6e964f582dd32c8
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/base/api/IOwnedBlockEntity.kt
  - id: openwiki-source-5b46d60c63ec6c7025ab0493
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/base/api/ISyncingBlockEntity.kt
  - id: openwiki-source-b5cef50dbdba781304368b6e
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/base/block/BaseBlockEntityBlock.kt
  - id: openwiki-source-94a303ac899082e0671fdd80
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/base/block/BaseNBTBlock.kt
  - id: openwiki-source-389cbbb614e6cf038006f810
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/base/blockentity/MutableNBTBlockEntity.kt
  - id: openwiki-source-219dd933171238a711b30685
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/platform/BasePlatform.kt
  - id: openwiki-source-10df6286f08f5c99a4d647ba
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/platform/PlatformRegistries.kt
  - id: openwiki-source-c89303bba0efa95094a3ad2c
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/platform/PlatformToolkit.kt
  - id: openwiki-source-496476f3c08fee3a8dc4833c
    resource: repo://projects/broccolium/versions/1.21.1/src/main/kotlin/site/siredvin/broccolium/modules/base/block/BaseNBTBlock.kt
  - id: openwiki-source-a5af0c6b95ccba7792a6b8b4
    resource: repo://projects/broccolium/versions/1.21.1/src/main/kotlin/site/siredvin/broccolium/modules/base/blockentity/MutableNBTBlockEntity.kt
generated: { by: "hermes", at: "2026-10-16T16:31:45.808Z" }
---

# Broccolium platform and block lifecycle

## Two platform responsibilities

BroccoliumCore.configure installs toolkit, tag and ingredient implementations. PlatformToolkit.get fails with IllegalStateException before configuration; it does not discover a loader itself. PlatformRegistries lazily asks that configured toolkit for registry wrappers, so initialize services before first registry-dependent access. configure replaces the implementation; it is not a one-time guard.

BasePlatform instead belongs to a consuming mod: it delegates registrations to an InnerBasePlatform and tracks selected registrations in ModInformationTracker for datagen and creative-tab consumers. Items, name-based blocks and custom stats are tracked. The ResourceLocation block overload delegates directly without adding the block to the tracker, unlike the name overload. Registration through arbitrary APIs is therefore not guaranteed to appear in the holder.

FabricInnerBasePlatform immediately registers supplied objects and returns suppliers of those registered objects. NeoForge's ForgeInnerBasePlatform registers deferred suppliers and assumes the relevant DeferredRegister fields were overridden: their default null values are force-unwrapped. The owning mod must provide and attach the required registries, not merely subclass the adapter.

## Block lifecycle delegation

BaseBlockEntityBlock supplies a server-only ticker when belongToTickingEntity is enabled; each tick still checks ITickingBlockEntity. It forwards neighbor changes, scheduled block ticks, placement and destruction through IObservingBlockEntity. Removal of the same block type returns without destroy. Player placement on the server also assigns IOwnedBlockEntity.player; the interface itself does not implement authorization or UUID persistence.

These interfaces let a consumer opt into lifecycle behaviors without a single giant base entity type. They are dispatch hooks, not proof that an implementation handles every callback safely.

## Persistence and client sync

ISyncingBlockEntity bridges item-save methods to saveInternalData/loadInternalData. MutableNBTBlockEntity calls these methods for world save/load and update tags and supplies a block-entity update packet. pushInternalDataChangeToClient returns when no level exists and only on the server marks dirty, updates block state and sends a block update. triggerRenderUpdate delegates to the toolkit separately.

The 1.21.1 override uses registry-provider-aware getUpdateTag, loadAdditional and saveAdditional signatures while keeping the internal-data bridge. Never copy the 1.20.1 load signature into that override.

BaseNBTBlock handles a separate item round-trip: noncreative server-side player destruction explicitly emits the prepared stack; placement restores opted-in savableProperties and internal data, then syncs an ISyncingBlockEntity when internal data is present. In 1.20.1 it stores internalData and blockState NBT. In 1.21.1 it uses CUSTOM_DATA and BLOCK_STATE components. The prepared block-state payload can contain more properties than the savableProperties restoration list; that list governs what is restored. Do not infer every loot/explosion/piston path uses this player-destruction hook, or that a world save and an item save have identical schemas.

## Extension and verification route

Use common contracts for shared behavior; implement loader primitives under the matching library branch and preserve full-file [version override precedence](../../build/stonecutter-publication.md). Consumers building peripherals add their ownership and state layer in [Tweakium](../../tweakium/modules/peripheral.md). Providers relying on registration tracking are explained in [data generation](data.md), and service installation in [loader integration](../integrations/loaders.md).

This run inspected implementation, not a live placement/save/reload/client synchronization test. The [verification guide](../../testing/verification.md) keeps those coverage limits explicit.
