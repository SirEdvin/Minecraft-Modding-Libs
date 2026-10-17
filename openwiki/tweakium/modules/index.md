# Files

- [Tweakium peripheral dispatch and ownership](peripheral.md) - Dynamic Lua method construction, plugins and owner boons, connection lifecycle, block-entity persistence and explicit extension ordering limits.
- [Tweakium registration, data and creative filler](platform-data.md) - Computer-specific registration tracking, serializer-to-UpgradeType migration, generated upgrade registries/language keys, and the creative filler end-to-end flow.
- [Tweakium fake players and world interactions](player.md) - Host-specific fake-player caches and inventory preparation, main-hand attributes, proxy hit/dig flow, and exception-sensitive cleanup boundaries.
- [Tweakium storage plugins and operations](plugins.md) - Lua transfer dispatch, target adaptation, slot/query validation differences, observable units and operation fuel/cooldown partial-update semantics.
- [Tweakium Lua representations and RML transforms](representation-rml.md) - Lua table conversion/enrichment and version-sensitive item/enchantment output, contrasted with the separate Render Markup Language parser and item-query filters.
- [Tweakium pocket and turtle upgrade persistence](upgrades-persistence.md) - Peripheral construction, stateful upgrade item transport, and version-specific storage adapters with component-removal and encoding failure boundaries.
