---
type: module
title: Tweakium fake players and world interactions
description: Host-specific fake-player caches and inventory preparation, main-hand attributes, proxy hit/dig flow, and exception-sensitive cleanup boundaries.
tags: [tweakium, player, interactions, inventory, drops]
verified:
  - by: openwiki/0.7.1
    at: 2026-10-16T16:31:45.808Z
sources:
  - id: openwiki-source-a63c6161858a70520940fad4
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/player/FakePlayerProviderBlockEntity.kt
  - id: openwiki-source-aaa308efb2dee25311f3bc2b
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/player/FakePlayerProviderEntity.kt
  - id: openwiki-source-d48dd7400e6e37861b1ac53b
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/player/FakePlayerProviderPocket.kt
  - id: openwiki-source-c2fece5eec6823937c3048f0
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/player/FakePlayerProviderTurtle.kt
  - id: openwiki-source-0d11a825da3bbc1368988a47
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/player/FakePlayerProxy.kt
  - id: openwiki-source-9c424ad9c5f875bb2f8d5dff
    resource: repo://projects/tweakium/versions/1.21.1/src/main/kotlin/site/siredvin/tweakium/modules/player/FakePlayerProviderBlockEntity.kt
  - id: openwiki-source-d3b211fe60f1f817d6765f5c
    resource: repo://projects/tweakium/versions/1.21.1/src/main/kotlin/site/siredvin/tweakium/modules/player/FakePlayerProviderEntity.kt
  - id: openwiki-source-a0104039806642ed4b9ad74f
    resource: repo://projects/tweakium/versions/1.21.1/src/main/kotlin/site/siredvin/tweakium/modules/player/FakePlayerProviderPocket.kt
  - id: openwiki-source-d859105f4ce84b0fb0d4bfc6
    resource: repo://projects/tweakium/versions/1.21.1/src/main/kotlin/site/siredvin/tweakium/modules/player/FakePlayerProviderTurtle.kt
generated: { by: "hermes", at: "2026-10-16T16:31:45.808Z" }
---

# Tweakium fake players and world interactions

## Host providers own state preparation

Pocket, turtle, block-entity and entity providers obtain a FakePlayerProxy through ComputerPlatformToolkit. Each uses a bounded weak-key cache with expire-after-write policy rather than creating a fresh actor for every action. Proxy state, including dig progress, can therefore survive successive calls for a retained cache key; do not assume an entirely reset player each invocation.

Pocket construction uses the carried ServerPlayer's profile when available, otherwise DUMMY_PROFILE; action entry requires a Player carrier. Turtle uses owningPlayer or the dummy profile, turtle level/position/direction and selected inventory slot. Block-entity construction requires IOwnedBlockEntity with a resolvable player. Its load routine uses that real player's level and position, not the block entity's location. Entity construction keys a Pair(entity,realPlayer) and likewise prepares position/level from realPlayer while drawing inventory from the entity. Names alone do not prove actions originate at the block/entity host.

Block/entity action entry requires slotted Broccolium storage unless skipInventory is true. Load prepares fake inventory/selected slot and attributes; unload writes items back and handles extra contents. Turtle copies native slots back and inserts/drops extras. Pocket copies player inventory back. Block/entity unload calls storage.store into existing slots rather than an explicit slot-replacement operation, so it should not be described as an atomic inventory swap.

## Attributes and cleanup limits

Legacy load/unload adds/removes main-hand attribute modifiers. Every 1.21.1 provider override uses ItemStack.forEachModifier(EquipmentSlot.MAINHAND), respecting slot applicability rather than iterating an entire component blindly. Attribute application and action behavior depend on the effective version and loader fake player.

The wrappers perform load, user function and unload sequentially, without try/finally. An exception skips unload. skipInventory suppresses load/copyback paths but unload still inspects/removes the current main-hand modifiers. The code resets selected slot before some cleanup, so do not assert perfect modifier cleanup if an action changes selection or held item. These are inspection limits requiring targeted regression tests, not behavior repaired here.

## Proxy action flow

FakePlayerProxy keeps digPosition, digBlock and currentDamage. Changing target sends ABORT_DESTROY_BLOCK to the old target and resets progress. findHit casts from fake position/look up to its configured range, traverses nonempty blocks and optionally considers LivingEntity hitboxes with the optional predicate; it chooses an eligible nearby entity only when not occluded by the block hit. This is a custom ray path, not a promise of vanilla shape-perfect targeting.

use attempts toolkit useOn for a noncrouching block action, then native useItemOn and useItem; entity hits delegate toolkit interaction. swing returns failure text for miss/invalid targets, routes block digging through protection checks and unbreakable detection, accumulates damage and eventually calls native block destruction, or attacks a LivingEntity with a nonempty tool and applies a short cooldown.

withConsumer installs Broccolium drop capture targeting the action area, stores drops into the fake inventory and resets/drops leftovers afterwards. It too lacks finally. Global capture and provider cleanup can both remain active/incomplete on an exception. Nested/concurrent capture is not isolated; see [lookup and drop lifecycle](../../broccolium/modules/lookup-drops.md).

## Extension and verification

Peripheral owners bridge host actions to these providers. Keep host identity, source inventory, operation/fuel checks and world permissions separate; a fake player existing is not sufficient authorization for an action. Loader subclasses affect harm/menu/riding/eye behavior as described in [loader boundaries](../integrations/loaders.md).

The migration audit explicitly excludes exhaustive combat and live world/upgrade persistence coverage. No fake-player runtime tests were run for this wiki. Focus future coverage on exceptions, held-item changes, skipInventory, block/entity origin semantics and actual protection-event behavior, alongside [upgrade storage](upgrades-persistence.md).
