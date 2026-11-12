# CC:Tweaked 1.121.0 migration

Status: **INCOMPLETE draft** (first research pass; verification pending).

Scope: Minecraft 1.20.1 (CC:Tweaked 1.113.1 → 1.121.0) and 1.21.1 (1.120.0 → 1.121.0).

## Sources

API shapes below were read from the published artifacts on `https://maven.squiddev.cc/cc/tweaked/`,
not inferred:

- new: `cc-tweaked-{1.20.1,1.21.1}-{common-api,common,core,forge,forge-api,fabric,fabric-api}:1.121.0` (sources and binary JARs)
- old, for diffs: `cc-tweaked-1.20.1-{common-api,common}:1.113.1`, `cc-tweaked-1.21.1-{common-api,common}:1.120.0`

## Build toolchain consequence

The Fabric 1.121.0 JARs are built with Loom 1.18.1, and Loom refuses mods built with a newer
minor line than itself. Every Loom 1.18.x release declares Gradle plugin API 9.7.0 and JVM 25.

| Pin | Before | After | Why |
| --- | --- | --- | --- |
| `ccTweaked120` | 1.113.1 | 1.121.0 | target |
| `ccTweaked121` | 1.120.0 | 1.121.0 | target |
| Loom (`loom`, `net.fabricmc.fabric-loom`) | 1.17.11 | 1.18.4 | required by the CC Fabric JARs |
| Gradle wrapper | 9.5.0 | 9.9.1 | Loom 1.18 needs Gradle ≥ 9.7; 9.9.1 is the version the template project already runs |
| Gradle launcher JVM | 21 or 25 | 25 | Loom 1.18 needs it; CI already launches with 25 |

Stonecutter stays on 0.9.8. Minecraft toolchains stay Java 17 (1.20.1) and 21 (1.21.1).

## Old-to-new API map

| Area | Before | 1.121.0 | Still works? |
| --- | --- | --- | --- |
| Equipped item | not stored; rebuilt by `UpgradeBase.getUpgradeItem(data)` | stored in `UpgradeData` and on disk | rebuild path kept for old saves and legacy data writes |
| `UpgradeData` | record | final class; `of(upgrade, stack, data)` is new; `of(upgrade, data)` and the constructor are deprecated | yes; accessors (`upgrade()`/`holder()`, `data()`, `getUpgradeItem()`) unchanged |
| `UpgradeData` validation | none | with a stack: count must be 1 and `getCraftingIngredient().test(stack) && isItemSuitable(stack)`, else `IllegalArgumentException` | new failure mode |
| Upgrade data access | `ITurtleAccess.getUpgradeNBTData/updateUpgradeNBTData` (1.20.1), `getUpgradeData/setUpgradeData` (1.21.1), same on `IPocketAccess` | deprecated; `getUpgradeHandle()` returns `UpgradeHandle` (`getItem/setItem/getData/setData` or `setDataChanged`/`remove`) | yes; a legacy write also recomputes the stored item with `getUpgradeItem(data)` |
| `ITurtleAccess.getUpgradeWithData` | default method (1.20.1) | abstract; `getUpgradeHandle(side)` added | implementers must add both |
| `IPocketAccess` | – | `getUpgradeHandle()` added | implementers must add it |
| Crafting item | `UpgradeBase.getCraftingItem()` | deprecated; `getCraftingIngredient()` (defaults to `Ingredient.of(getCraftingItem())`) | yes |
| `UpgradeBase.getUpgradeData(stack)` | extract data on equip | deprecated; still called on equip | yes |
| Base classes | `AbstractTurtleUpgrade(…, ItemStack)`, `AbstractPocketUpgrade(…, ItemStack)` | deprecated; `AbstractTurtleUpgrade.StackAware` / `AbstractPocketUpgrade.StackAware` take an `Ingredient`, accept any stack, and make `getUpgradeData` final | yes |
| Serialisers/types | `simpleWithCustomItem` | `simpleWithCustomIngredient` added (1.20.1 `TurtleUpgradeSerialiser`/`PocketUpgradeSerialiser`, 1.21.1 `UpgradeType`) | yes |
| Modeller | `getModel(upgrade, data, side)` (1.20.1) | deprecated; `getModel(UpgradeData, side)` added. 1.21.1 adds `getModel(upgrade, UpgradeData, side)` | yes |
| Nullability | `javax.annotation` | JSpecify, packages are `@NullMarked` | Kotlin now rejects nullable arguments |
| Peripheral creation | only for `TurtleUpgradeType.isPeripheral()` upgrades | `createPeripheral` is called for every upgrade, once per equip | behaviour change |
| Disk, 1.20.1 | `LeftUpgrade` id + `LeftUpgradeNbt`; pocket `Upgrade` + `UpgradeInfo` | plus `LeftUpgradeItem`/`RightUpgradeItem`/`UpgradeItem` | old tags load; item rebuilt, and the upgrade is dropped if `isItemSuitable` rejects it |
| Disk, 1.21.1 | `{id, components}` | `{id, item, components}` | old values load through the item-less codec branch |
| DFU, 1.21.1 | turtle upgrade typed as string-or-map; legacy `*UpgradeNbt` lost | pre-3818.5 schema types the id as a string plus optional item; the block-entity fixer rewrites the whole turtle | see open question 1 |

## Affected library classes

| Class | Adaptation |
| --- | --- |
| Tweakium `BaseTurtleUpgrade`, `BasePocketUpgrade` (both versions) | keep the `ItemStack` constructors; suppress the deprecation |
| Tweakium `StatefulTurtleUpgrade`, `StatefulPocketUpgrade` (both versions) | keep `getUpgradeData`/`getUpgradeItem`/`isItemSuitable`; suppress the deprecation |
| Tweakium `DataStorageUtil` and the pocket/turtle data storages | keep the legacy setters, because they are the ones that rewrite the stored item; suppress the deprecation |
| Tweakium `DataStorageUtil.getDataStorage(ITurtleAccess, TurtleSide?)` (1.20.1) | parameter becomes non-null (JSpecify) |
| Tweakium `FacingBlockTurtleModeller` (both versions) | unchanged behaviour; suppress `craftingItem` deprecation |
| Tweakium `ComputerBasePlatform.createWithSelfCustomItem` (1.21.1) | unchanged; suppress `craftingItem` deprecation |
| Tweakium `CreativeTabUtil`, `LuaRepresentation` (1.21.1) | nullability fixes only |
| Tweakium `TurtleUpgradeDataMixin`, `LegacyTurtleUpgradeFix`, `LegacyTurtleUpgradeData` (1.21.1) | open question 1 |
| Peripheralium, Testiarium (main, `testMod`, `cctTestMod`) | compile unchanged |
| Loader metadata | Forge 1.20.1 minimum was hard-coded to 1.113.1; all three loaders now read the catalog version |

Why the legacy path is kept: Tweakium stateful upgrades only accept the crafting item plus their
state, and a legacy data write recomputes the stored item from that state. The equipped item
therefore survives exactly, and consumers keep their constructors and overrides.

## Open questions

1. Does CC 1.121.0 migrate 1.20.1 `LeftUpgradeNbt`/`RightUpgradeNbt` on its own, and does
   Tweakium's `TurtleUpgradeDataMixin` still work against the new pre-3818.5 schema (the upgrade id
   field is now a plain string type)? To be answered with the real data fixer in a GameTest.
2. Runtime: GameTests, CCT harness and client runs on all four loader leaves.

## Persistence verdict

Pending.
