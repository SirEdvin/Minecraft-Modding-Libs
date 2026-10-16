---
type: module
title: Broccolium lookups and drop capture
description: Ordered storage-provider resolution and the global drop interception lifecycle shared with Tweakium fake-player interactions.
tags: [broccolium, lookup, storage, drops, lifecycle]
verified:
  - by: openwiki/0.7.1
    at: 2026-10-16T16:31:45.808Z
sources:
  - id: openwiki-source-0d8149374ff7aef373a16c01
    resource: repo://projects/broccolium/fabric/src/main/java/site/siredvin/broccolium/mixins/EntityMixin.java
  - id: openwiki-source-4795a4cb42ec1f629bf1e1c7
    resource: repo://projects/broccolium/fabric/src/main/java/site/siredvin/broccolium/mixins/ServerLevelMixin.java
  - id: openwiki-source-c83b7c91ea9388857bb44553
    resource: repo://projects/broccolium/forge/src/main/kotlin/site/siredvin/broccolium/ForgeCommonHooks.kt
  - id: openwiki-source-606f6179e92cc34eb7d87a0f
    resource: repo://projects/broccolium/forge/versions/1.21.1/src/main/kotlin/site/siredvin/broccolium/ForgeCommonHooks.kt
  - id: openwiki-source-dec1566019eddd9c7a8d1d07
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/lookup/BaseLookup.kt
  - id: openwiki-source-c1737c3dd7959fcf59892814
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/lookup/ChainedBaseLookup.kt
  - id: openwiki-source-15cfff96cdf2299e3ec68c60
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/storage/item/AgnosticItemSinkLookup.kt
  - id: openwiki-source-06dc5c2b570cd76c5d3dc39e
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/storage/item/AgnosticItemStorageLookup.kt
  - id: openwiki-source-e90ab02061cf2dd82aa378d6
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/tricks/DropConsumer.kt
  - id: openwiki-source-0d11a825da3bbc1368988a47
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/player/FakePlayerProxy.kt
generated: { by: "hermes", at: "2026-10-16T16:31:45.808Z" }
---

# Broccolium lookups and drop capture

## Provider resolution

BaseLookup stores separate block, entity and inventory-item callback lists. Registration appends; extraction returns the first non-null result in registration order. It does not catch extractor exceptions. Returning null means “try another provider”; throwing aborts resolution.

extractFromUnknown dispatches BlockPos through the level's block entity, BlockEntity through its own position, and Entity through entity lookup. Null returns null. Unsupported objects throw IllegalArgumentException; arbitrary containers or ItemStacks are not implicitly accepted by this entrypoint. Inventory-item lookup requires the explicit slotted origin and slot path.

Specialized lookups add meaningful precedence:

- Item block lookup first accepts AgnosticItemStorageProvider, then registered callbacks, then vanilla container fallback. Chest fallback asks ChestBlock for its combined container. Entity lookup runs callbacks first, then handles player inventory, chested-horse inventory excluding its first two slots, and container minecarts.
- Fluid and energy block lookups accept their explicit agnostic provider interfaces before callbacks.
- ChainedBaseLookup tries its upstream lookup before its own callbacks. AgnosticItemSinkLookup chains full storage into sink discovery, but its block override gives an explicit AgnosticItemSinkProvider first priority. Do not summarize this as “all sinks always prefer full storage.”

Install callbacks in loader initialization, return null for genuinely unsupported targets, and avoid registering a broad provider ahead of more specific ones unless that precedence is deliberate. [Loader adapters](../integrations/loaders.md) describe the sided/native capability differences.

## Drop interception state

DropConsumer is a singleton, not a per-player or thread-local capture. configure replaces its consumer, pending remainders, level and bounds. The entity overload currently sets targetEntity to null, just like the position overload; despite its name it captures spawned ItemEntities near the entity rather than selecting that entity for onLivingDrop.

onEntitySpawn intercepts an ItemEntity only in the configured level and bounds, passes its stack to the consumer, and records unaccepted remainder. reset clears all capture state and returns remainders; a second reset throws IllegalCallerException. resetAndDrop resets first, then drops remainder at the supplied position, avoiding interception of its own leftovers.

onLivingDrop only consumes when the dropping entity equals targetEntity. With the current public configure methods leaving that field null, ordinary living-entity calls do not match that path. This is an observed implementation limitation, not a promise of entity-specific collection.

## Loader hooks and failure boundaries

LibCommonHooks forwards spawn/drop hooks to DropConsumer. Fabric mixins intercept ServerLevel.addEntity and Entity.spawnAtLocation; Forge and the NeoForge override cancel EntityJoinLevelEvent for captured items and remove consumed living drops from the event list. The spawn route is therefore important independently of the living-drop route.

Tweakium's FakePlayerProxy.withConsumer configures capture, executes the action, and resets/drops afterwards without try/finally. An exception can leave global capture installed. Nested capture also overwrites global state. Callers should not assume exception safety, isolation or concurrent capture support; this documentation task records these limits without repairing them.

See [fake-player world interactions](../../tweakium/modules/player.md) for the caller and [storage transfers](storage.md) for the consumer's remainder contract. Dedicated exception/nesting drop-capture tests were not identified in the inspected test paths, and none were executed here.
