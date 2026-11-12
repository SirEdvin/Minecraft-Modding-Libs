# CC:Tweaked 1.121.0 migration

Scope: Minecraft 1.20.1 (CC:Tweaked 1.113.1 → 1.121.0) and 1.21.1 (1.120.0 → 1.121.0).
Consumer-facing notes are in `CHANGELOG.md` under 2.1.0; this file records why.

## Sources

API shapes were read from the published artifacts on `https://maven.squiddev.cc/cc/tweaked/`,
not inferred:

- new: `cc-tweaked-{1.20.1,1.21.1}-{common-api,common,core,forge,forge-api,fabric,fabric-api}:1.121.0`, sources and binary JARs
- old, for diffs: `cc-tweaked-1.20.1-{common-api,common}:1.113.1`, `cc-tweaked-1.21.1-{common-api,common}:1.120.0`

## Forced toolchain and platform changes

Each row is required by the row above it; none is optional.

| Pin | Before | After | Why |
| --- | --- | --- | --- |
| `ccTweaked120` | 1.113.1 | 1.121.0 | target |
| `ccTweaked121` | 1.120.0 | 1.121.0 | target |
| Loom (`loom`, `net.fabricmc.fabric-loom`) | 1.17.11 | 1.18.4 | the Fabric JARs are built with Loom 1.18.1 and Loom refuses mods from a newer minor line |
| Gradle wrapper | 9.5.0 | 9.9.1 | every Loom 1.18.x declares Gradle plugin API 9.7.0; 9.9.1 is what the template project runs |
| Gradle launcher JVM | 21 or 25 | 25 | every Loom 1.18.x declares JVM 25; CI already launched with 25 |
| NeoForge (`neoforge`) | 21.1.9 | 21.1.265 | the NeoForge JAR requires `[21.1.265,21.2)`; this is also the libraries' declared minimum |
| Kotlin for Forge, NeoForge (`forge-kotlin-neoforge`) | 5.7.0 | 5.9.0 | 5.7.0 and 5.8.0 call `net.neoforged.fml.Bindings`, which that NeoForge no longer has |

Stonecutter stays on 0.9.8. Minecraft toolchains stay Java 17 (1.20.1) and 21 (1.21.1). Forge
47.1.0 and the Fabric API pins already satisfy CC's 1.121.0 metadata.

## Old-to-new API map

| Area | Before | 1.121.0 | Old form still works? |
| --- | --- | --- | --- |
| Equipped item | not stored; rebuilt by `UpgradeBase.getUpgradeItem(data)` | stored in `UpgradeData` and on disk | the rebuild is kept for old saves and for legacy data writes |
| `UpgradeData` | record | final class; `of(upgrade, stack, data)` is new; `of(upgrade, data)` and the constructor are deprecated | yes; `upgrade()`/`holder()`, `data()`, `getUpgradeItem()` are unchanged |
| `UpgradeData` validation | none | with a stack: count must be 1 and `getCraftingIngredient().test(stack) && isItemSuitable(stack)`, else `IllegalArgumentException` | new failure mode |
| Upgrade data access | `getUpgradeNBTData`/`updateUpgradeNBTData` (1.20.1), `getUpgradeData`/`setUpgradeData` (1.21.1) on `ITurtleAccess` and `IPocketAccess` | deprecated; `getUpgradeHandle()` returns an `UpgradeHandle` (`getItem`, `setItem`, `getData`, `setData` or `setDataChanged`, `remove`) | yes; a legacy write also recomputes the stored item with `getUpgradeItem(data)` |
| `ITurtleAccess.getUpgradeWithData` | default method (1.20.1) | abstract; `getUpgradeHandle(side)` added | implementers must add both |
| `IPocketAccess` | – | `getUpgradeHandle()` added | implementers must add it |
| Crafting item | `UpgradeBase.getCraftingItem()` | deprecated; `getCraftingIngredient()` defaults to `Ingredient.of(getCraftingItem())` | yes |
| `UpgradeBase.getUpgradeData(stack)` | extracts data on equip | deprecated; still called on equip | yes |
| Base classes | `AbstractTurtleUpgrade(…, ItemStack)`, `AbstractPocketUpgrade(…, ItemStack)` | deprecated; the new `StackAware` subclasses take an `Ingredient`, accept any stack and make `getUpgradeData` final | yes |
| Serialisers and types | `simpleWithCustomItem` | `simpleWithCustomIngredient` added (1.20.1 `TurtleUpgradeSerialiser`/`PocketUpgradeSerialiser`, 1.21.1 `UpgradeType`) | yes |
| Modeller | `getModel(upgrade, data, side)` (1.20.1) | deprecated; `getModel(UpgradeData, side)` added. 1.21.1 adds `getModel(upgrade, UpgradeData, side)` | yes |
| Nullability | `javax.annotation` | JSpecify; the API packages are `@NullMarked` | Kotlin now rejects nullable arguments |
| Peripheral creation | only for `TurtleUpgradeType.isPeripheral()` upgrades | `createPeripheral` is called for every upgrade, once per equip | behaviour change |
| Disk, 1.20.1 | turtle `LeftUpgrade` + `LeftUpgradeNbt`, pocket `Upgrade` + `UpgradeInfo` | plus `LeftUpgradeItem`, `RightUpgradeItem`, `UpgradeItem` | old tags load; the item is rebuilt, and the upgrade is dropped if `isItemSuitable` rejects the rebuilt item |
| Disk, 1.21.1 | `{id, components}` | `{id, item, components}` | old values load through the item-less codec branch |
| Data fixer, 1.21.1 | the turtle upgrade field was typed as string-or-map and the fixer only looked in the untyped remainder, so `LeftUpgradeNbt`/`RightUpgradeNbt` were lost | before schema 3818.5 the field is a plain string plus an optional item, and the fixer rewrites the whole turtle | fixed upstream |

## Affected library classes

| Class | Adaptation |
| --- | --- |
| Tweakium `BaseTurtleUpgrade`, `BasePocketUpgrade` (both versions) | keep the `ItemStack` constructors; suppress the deprecation |
| Tweakium `StatefulTurtleUpgrade`, `StatefulPocketUpgrade` (both versions) | keep `getUpgradeData`/`getUpgradeItem`/`isItemSuitable`; suppress the deprecation |
| Tweakium `DataStorageUtil`, `TurtleComputerDataStorage`, `PocketComputerDataStorage` | keep the legacy setters; suppress the deprecation |
| Tweakium `DataStorageUtil.getDataStorage(ITurtleAccess, TurtleSide?)` (1.20.1) | parameter becomes non-null |
| Tweakium `FacingBlockTurtleModeller` (both versions), `ComputerBasePlatform.createWithSelfCustomItem` (1.21.1) | unchanged behaviour; suppress the `craftingItem` deprecation |
| Tweakium `CreativeTabUtil`, `LuaRepresentation` (1.21.1) | nullability fixes only |
| Tweakium `TurtleUpgradeDataMixin`, `LegacyTurtleUpgradeFix`, `LegacyTurtleUpgradeData` (1.21.1) | removed, see below |
| Peripheralium; Testiarium main, `testMod` and `cctTestMod` | no source change |
| Loader metadata | the Forge 1.20.1 CC minimum was hard-coded to 1.113.1; all three loaders now read the catalog version |

### Why Tweakium stays on the deprecated item-less API

The build treats warnings as errors, so the deprecated calls are suppressed rather than left to
warn. The alternative was to move the bases to `StackAware` and `UpgradeHandle`. That would change
every consumer constructor from `ItemStack` to `Ingredient` and remove the `getUpgradeData`
override point, for no behavioural gain:

- Tweakium stateful upgrades accept only the crafting item plus their own state.
- A legacy data write recomputes the stored item from that state.

The equipped item therefore survives exactly, and the legacy setters are the ones that keep the
stored item in step with the data; `UpgradeHandle.setData` alone would leave it stale. CC may
remove the deprecated API in a later release; the move to `StackAware` is the follow-up then.

### Why the 1.21.1 data-fixer hook is removed

Tweakium 2.0.x prepended a step to CC's turtle block-entity fixer that turned the typed
`LeftUpgrade`/`RightUpgrade` string into `{id, components}`. Against the 1.121.0 schema that field
is a plain string type before 3818.5, so the step fails with `Not a string`. CC's own fixer now
carries the legacy data. `upgradeDataFromMinecraft120SurvivesTheDataFixer` failed with the hook
in place and passes without it.

## Persistence

Covered by GameTests in `UpgradeBehaviorGameTests`, on the real CC 1.121.0 runtime of every
loader leaf:

- `upgradeDataSavedWithoutItemStillLoads`: a turtle block entity and a pocket computer item in the
  disk format of libs 2.0.x (CC speaker, upgrade id and data, no item) load with their data, and
  the turtle saves the item afterwards. Item-less data of a Tweakium stateful upgrade rebuilds an
  item CC accepts.
- `turtleKeepsEquippedItemThroughDataWrites` and the pocket equivalent: the equipped item is the
  stored item, a Tweakium data write changes only the state inside it, and on 1.20.1 the saved
  tag carries the item.
- `upgradeDataFromMinecraft120SurvivesTheDataFixer` (1.21.1): a 1.20.1 turtle block entity and
  pocket computer go through Minecraft's real data fixer and keep their upgrade data.
- Kept: `UpgradeDataStorageTests.customDataWritesPreserveComponentRemovals` and the explicit-removal
  GameTests for the pocket/turtle data setters.

Not tested: opening a world that was actually written by libs 2.0.1 with CC:Tweaked 1.113.1 or
1.120.0. The copied-save matrix (`scripts/verification/save-matrix.py`) needs prepared production
server assemblies and was not rerun. The tests above feed the old on-disk shapes to the real
loading code instead; they do not cover a consumer mod's own upgrades.
