---
type: module
title: Tweakium storage plugins and operations
description: Lua transfer dispatch, target adaptation, slot/query validation differences, observable units and operation fuel/cooldown partial-update semantics.
tags: [tweakium, plugins, storage, lua, fuel, operations]
verified:
  - by: openwiki/0.7.1
    at: 2026-10-16T16:31:45.808Z
sources:
  - id: openwiki-source-4233540ba9ca00ff82292190
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/peripheral/boon/FuelBoon.kt
  - id: openwiki-source-7cc6f1ec3710d00eb2a24718
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/peripheral/boon/OperationBoon.kt
  - id: openwiki-source-4b1926bfe0f2356c44c4c5c0
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/plugins/AbstractFluidStoragePlugin.kt
  - id: openwiki-source-7d4e57126298126480ce6a5a
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/plugins/AbstractInventoryPlugin.kt
  - id: openwiki-source-c6f244c08db93d9925fc7095
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/plugins/AbstractItemStoragePlugin.kt
  - id: openwiki-source-990ed430c39ce271623c8161
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/plugins/EnergyPlugin.kt
  - id: openwiki-source-99b16004e0f26c4853d6024b
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/plugins/FullEnergyPlugin.kt
  - id: openwiki-source-2e03eec861dfd4943498974e
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/plugins/PeripheralPluginUtils.kt
generated: { by: "hermes", at: "2026-10-16T16:31:45.808Z" }
---

# Tweakium storage plugins and operations

## Lua request to storage transfer

Storage plugins expose main-thread Lua methods and additional ComputerCraft types. A typical call resolves computer.getAvailablePeripheral(name), obtains its target and optional ISidedPeripheral.side, adapts it through Broccolium storage/sink lookup, caps the requested amount and invokes moveTo/moveFrom. Missing peripheral or incompatible target becomes LuaException. Generic lookup can still throw for an unsupported target object; it is not a universal container coercion layer.

AbstractItemStoragePlugin handles unslotted item queries and compact/detailed listing through LuaRepresentation. AbstractInventoryPlugin adds slotted transfer and advertises inventory/inventory_extended plus inventoryTransferLimit and inventoryAPI=[1,1]. Fluid plugins advertise fluid_storage/fluid_storage_extended, tank contents/capacities and fluidStorageAPIVersion=[1,2]. These configuration keys are exact wire contracts, not an automatically enforced version negotiation protocol.

## Validation is caller-specific

Inventory pushItems resolves target before checking a nonpositive limit, returns zero for such limits, validates explicit destination slots against a slotted sink, and converts Lua one-based numeric source/destination slots to zero-based indices. A nonnumeric source argument becomes an item predicate.

pullItems is asymmetric: its numeric branch checks source slot/range and passes an optional destination slot without the destination range check used in the query branch. Its query branch validates an explicit toSlot but calls moveFrom without passing that slot. Do not document universal slot honoring or equivalent push/pull validation.

Unslotted item, fluid and full energy transfers upper-cap limits but do not share the inventory method's explicit nonpositive short-circuit. There is no single global nonnegative/finite limit validator at this layer. Downstream storage behavior is part of the result, especially for negative, nonfinite or conversion-edge values.

Item predicates accept null as match-all, a string registry ID, or maps of composable operations. Recognized fields aggregate with AND; unknown fields and null values are ignored. OR/AND/NONE builders filter child values to maps and reduce without an empty identity, so an empty effective child collection can fail. This is a separate map-query language from [RML](representation-rml.md), not JSON-schema validation.

## Units and representation pitfalls

Fluid transfer calls cap with fluidStorageTransferLimit directly, while configuration reports that limit divided by platformCompactDivider. The code exposes both the divider and compact API version, but the descriptive field is not the exact same expression used by transfer. Review effective units end-to-end with [Broccolium fluid conversions](../../broccolium/modules/storage.md).

EnergyPlugin reads the first content stack and converts energy/capacity from Long to Int, potentially narrowing large values. FullEnergyPlugin pushes a Long result but returns pull results as Double; it caps an Int configured transfer limit promoted to Long. TypeScript's number type does not eliminate these runtime precision boundaries. [Typed contracts](../../typed-peripheral-api/contracts.md) are independently maintained.

## Operations, fuel and persistent cooldowns

Owner boons are plugin-like capabilities collected during [peripheral initialization](peripheral.md). OperationBoon checks cooldown first, then an optional context check, computes cost/cooldown, consumes real fuel when cost is nonzero, runs the method and success callback, then persists cooldown when above threshold. Failures before execution return MethodResult nil/error and optional reason callback. Fuel already consumed is not refunded if method/callback throws; cooldown is written afterwards. This is partial-update sequencing, not an atomic operation transaction.

Cooldowns are absolute local wall-clock timestamps stored in owner dataStorage under cooldowns and operation settingsName. They are milliseconds, not Minecraft tick counters. Registry names can overwrite earlier allowedOperations entries. FuelBoon persists the configured consumption rate, initializes zero to default one, multiplies cost exponentially by rate and divides cooldown by rate. Lua setter rejects out-of-range rates; internal setting clamps. Concrete pocket/turtle fuel boons determine actual consumption and whether fuel is disabled.

No plugin/operation tests were executed during documentation. Existing storage fixtures and predicate tests cover portions of this stack, not every API branch above. Prioritize asymmetric slot handling, limit/unit boundaries and exception-after-fuel behavior in downstream coverage rather than inferring correctness from shared base classes.
