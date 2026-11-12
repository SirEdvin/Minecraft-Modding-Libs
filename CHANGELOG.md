# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.1.0]

Minecraft 1.20.1 (Forge/Fabric) and 1.21.1 (NeoForge/Fabric) on CC:Tweaked 1.121.0, with unchanged
Maven coordinates for those nodes. Published to Repsy `https://repo.repsy.io/siredvin/default`, now
the only `site.siredvin` repository; consumers drop `https://mvn.siredvin.site` and resolve from Repsy.

### Changed

- CC:Tweaked 1.121.0 is the minimum on both Minecraft versions (1.20.1 was 1.113.1, 1.21.1 was 1.120.0). Migration: depend on `cc-tweaked-*:1.121.0`
- 1.21.1: NeoForge 21.1.265 is the minimum, because CC:Tweaked 1.121.0 requires it; that NeoForge needs Kotlin for Forge 5.9.0+. Migration: raise both
- Fabric builds against CC:Tweaked 1.121.0 need Loom 1.18.1+, which needs Gradle 9.7+ started on Java 25. Migration: raise Loom, the Gradle wrapper and the launcher JVM together; Minecraft toolchains stay 17/21
- Tweakium upgrade bases (`BaseTurtleUpgrade`, `BasePocketUpgrade`, `Stateful*Upgrade`, `Peripheral*Upgrade`) keep their `ItemStack` constructors and `getUpgradeData`/`getUpgradeItem` overrides on CC's now-deprecated item-less API. Migration: none; code that calls the deprecated CC members itself (`craftingItem`, `getUpgradeData`, `upgradeNBTData`/`upgradeData` setters) under `-Werror` adds `@Suppress("DEPRECATION")` or moves to `UpgradeHandle`
- CC now stores the equipped item and validates it: an upgrade whose `isItemSuitable` accepts more than its base class must return an accepted item from `getUpgradeItem(data)`; otherwise CC throws `IllegalArgumentException` after a data write or old-save load (1.20.1 drops the upgrade on load instead). Migration: keep `getUpgradeItem` and `isItemSuitable` consistent
- CC calls `createPeripheral` for every equipped turtle upgrade, not only those whose `TurtleUpgradeType` is a peripheral. Migration: return `null` from tool-only upgrades
- 1.20.1: `DataStorageUtil.getDataStorage(ITurtleAccess, TurtleSide)` takes a non-null side (CC's API is null-marked). Migration: pass a non-null `TurtleSide`

### Removed

- 1.21.1: Tweakium's `LegacyTurtleUpgradeData`, `LegacyTurtleUpgradeFix` and its data-fixer mixin; CC:Tweaked 1.121.0 migrates 1.20.1 turtle upgrade data itself and the old hook breaks its fixer. Migration: delete references, there is no replacement
- Minecraft 26.1.2 support; 2.0.1 is the last release with 26.1.2 artifacts
- The retired SirEdvin Maven repository; Repsy (`https://repo.repsy.io/siredvin/default`) is the only `site.siredvin` repository

## [2.0.1]

First release from the Stonecutter `multiversion` branch: Minecraft 1.20.1, 1.21.1 and 26.1.2
from one tree, with unchanged Maven coordinates. Published to the Repsy backup repository
`https://repo.repsy.io/siredvin/default`.

### Changed

- Fabric reverse item adapters (`SlottedAgnosticItemStorageWrapper`) require `ReplaceableItemStorage`; other backings throw instead of corrupting counts
- Item/block queries reject unknown keys, empty logical tables and non-string `in`/`not_in` values
- 1.21.1: `LuaRepresentation.forEnchantment(s)` takes `Holder<Enchantment>`; `name` is the registry id
- 1.21.1: NeoForge storage lookups pass a `null` side through instead of substituting `UP`
- 1.21.1: `DataComponentUtil.patchToNBT` throws on encoding failure
- 1.21.1: fake players apply only main-hand attribute modifiers

### Fixed

- 1.21.1: pocket/turtle upgrade data keeps explicit component removals
- 1.21.1: NeoForge entity inventory lookup no longer throws
- Fabric slot moves to/from slot 0 and slotted targets, relative sides in any letter case, `toSlot` for query pulls
- Drop capture is released on exceptions and targets the configured entity; exact XP level thresholds

## [1.3.1]

### Changed

- Use block center for entity scanning

## [1.3.0] 

### Changed

- Storage system rework, it is directional now

## [1.1.19]

### Added

- New field for peripheral ownership

## [1.1.13]

### Fixed

- `null` error in some rare cases when you throw pocket computer on the ground, and it still performs scan calls

## [1.1.12]z

### Added

- Even more plugin peripheral types
- support for `in` and `not_in` in tag query

## [1.1.9]

### Added

- `not`, `in` and `not_in` filters

### Fixed

- Correct extract of energy in turtle energy storage

## [1.1.8]

### Added

- `list()` and `items()` support for filters. `list()` also now support details

### Changed

- Use expiring cache for fake players rather than caching them forever

## [1.1.6] - 2025-09-24

### Fixed

- `inventory` API being broken

## [1.1.6] - 2025-09-23

### Fixed

- `putCompound` code

## [1.1.1] - 2025-09-21

### Added

- Creative filler

### Changed

- Scan API now is simpler, gone old format

## [1.1.0] - 2025-09-20

### Changed

- Energy Storage API updates

## [1.0.14] - 2025-09-20

### Added

- Rudimentary inventory storages, that has just list methods

## [1.0.13] - 2025-09-16

### Fixed

- `or` and `and` predicate logic
- `OwnedPeripheral` constructor

### [1.0.11] - 2025-09-16

### Added
- `or` and `and` statements for itemQuery
- blockQuery and its support in scan boon

### Fixed

- `naiveMove` can move more than stack now

## [1.0.3] - 2025-09-15

### Added

- `format` option for block scan boon

## [1.0.2] - 2025-09-14

### Fixed

- `getItemSlot` for fabric

## [1.0.1] - 2025-09-14

### Fixed

- `inventory` now support item slot limit correctly
- Add configuration information for plugins, exposing limits for `item_storage`, `fluid_storage
 and add inventoryAPIVersion for `inventory`
## [1.0.0] - 2025-03-17

### Added

- Everything from old peripheralium

### Changed

- Like, a lot?