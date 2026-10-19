---
type: module
title: Tweakium fake players and world interactions
description: Host-specific fake-player caches and inventory preparation, main-hand attributes, proxy hit/dig flow, and exception-sensitive cleanup boundaries.
tags: [tweakium, player, interactions, inventory, drops]
sources:
  - id: openwiki-source-433a5a8f61ef03e88c1fd1ae
    resource: repo://docs/verification/minecraft-26-1-R3.md
  - id: openwiki-source-805224ca8622b6a9abf34e78
    resource: repo://docs/verification/minecraft-26-1-R6-integration.md
  - id: openwiki-source-d266ff18e680c312b56ad9c7
    resource: repo://projects/tweakium/versions/26.1.2/src/main/kotlin/site/siredvin/tweakium/modules/player/FakePlayerProviderTurtle.kt
  - id: openwiki-source-bb09eb55658a45f09e5b9944
    resource: repo://projects/tweakium/versions/26.1.2/src/main/kotlin/site/siredvin/tweakium/modules/player/FakePlayerProxy.kt
generated: { by: "hermes", at: "2026-10-19T00:45:42.798Z" }
verified:
  - by: openwiki/0.7.1
    at: 2026-10-19T13:48:57.965Z
---

# Tweakium fake players and world interactions

## Current native session contract (2026-10-19)

Target sessions have scoped tool/attribute cleanup and load/action/unload reconciliation in finally, native storage loans and owner-cache refresh. Turtle slots9/15 are not hotbar selections: swap into native slot0, retain the original selected slot across callbacks and restore it. Never clamp/drop contents. R6 also repairs the1.21.1 selection path without importing target APIs. Target block use dispatches native game mode once; a preliminary manual pass can duplicate interaction or retry a loader veto.

[R3](../../../docs/verification/minecraft-26-1-R3.md) verifies actual host/player sessions at slots0/8/9/15, replaced tools, permanent attributes, loan/remainder/owner cleanup and exceptional paths. [R6 integration](../../../docs/verification/minecraft-26-1-R6-integration.md) refreshes assembled/old selection proof; [R7](../../../docs/verification/minecraft-26-1-R7-final-integration.md) binds final source/artifacts. Drop capture remains separately finally-released, process-global and not nested/concurrent isolation. No blanket certification of all legacy provider paths or arbitrary protection/combat mods.

## Historical source notes — October16 baseline

The details below describe327f845/e69b721, before target sessions and the later selection repair. In particular the old no-finally/additive-reconciliation observations are not claims about current26.1.2 source. Use the effective version override and current evidence above.

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

Both withConsumer overloads install Broccolium capture, store drops into the fake inventory and reset/drop leftovers in finally, including when the action throws. Entity capture retains the specific target; positional capture keeps its spatial scope. This repairs global capture release, not the distinct provider load/action/unload sequence above. Nested/concurrent capture remains unisolated; see [lookup and drop lifecycle](../../broccolium/modules/lookup-drops.md).

## Extension and verification

Peripheral owners bridge host actions to these providers. Keep host identity, source inventory, operation/fuel checks and world permissions separate; a fake player existing is not sufficient authorization for an action. Loader subclasses affect harm/menu/riding/eye behavior as described in [loader boundaries](../integrations/loaders.md).

ModuleBehaviorGameTests exercises native miss/unbreakable refusal and both exceptional capture wrappers. RuntimeBehaviorGameTests checks a successful native block break with exactly one captured drop plus target-specific entity/remainder behavior. These checks passed in all four Tweakium loader/version runners. They do not prove provider reconciliation, held-item attribute cleanup, skipInventory, combat or third-party protection behavior; those remain focused follow-ups alongside [upgrade storage](upgrades-persistence.md). See the [coverage matrix](../../../docs/module-test-coverage.md) for exact evidence boundaries.
