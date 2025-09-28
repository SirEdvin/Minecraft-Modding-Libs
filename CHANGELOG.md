# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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