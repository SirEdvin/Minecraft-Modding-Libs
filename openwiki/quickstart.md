---
type: quickstart
title: Minecraft Modding Libs ownership and task map
description: Start here to route changes across five subprojects, meaningful modules, loader/version boundaries and evidence-backed verification workflows.
tags: [quickstart, ownership, minecraft, modules, versions]
verified:
  - by: openwiki/0.7.1
    at: 2026-10-16T16:31:45.808Z
sources:
  - id: openwiki-source-2a9daaac1604f238ef4c63fb
    resource: repo://build.gradle.kts
  - id: openwiki-source-8e467af86822c4e36a35b699
    resource: repo://gradle/gametest-reports.gradle.kts
  - id: openwiki-source-23775c3de52f3ab95a13cb8b
    resource: repo://README.md
  - id: openwiki-source-e620d7484b72a53c7fa812cd
    resource: repo://settings.gradle.kts
generated: { by: "hermes", at: "2026-10-16T16:31:45.808Z" }
---

# Minecraft Modding Libs ownership and task map

## Start with the effective source

The actual Gradle roots are broccolium, tweakium, peripheralium, testiarium and typed-peripheral-api. The first four have Stonecutter common nodes and fabric/forge branches for Minecraft 1.20.1 and 1.21.1. The historical forge name means Forge on 1.20.1 (Java 17) and NeoForge on 1.21.1 (Java 21); it is not a promise of Forge support on both. typed-peripheral-api is a separate TypeScript-to-Lua consumer package.

Shared source lives under projects/<library>/src and loader branches, with full-file versions/<version>/src overrides taking precedence. Read the matching override before reasoning about 1.21.1 behavior. Keep 1.20.1 active for commits and switch all four trees from the repository root. Generated build/stonecutter output is not source ownership. The old *-core/*-fabric/*-forge directory description in AGENTS.md predates this graph; settings.gradle.kts and current builds are authoritative. See [Stonecutter and publication](build/stonecutter-publication.md) before changing build wiring.

## Broccolium: platform and world/storage primitives

- [Platform and base](broccolium/modules/platform-base.md): platform installation, registry access, registration/tracking, config and reusable world utilities.
- [Storage](broccolium/modules/storage.md): item/fluid/energy contracts, simulation versus mutation, transfers, units, sided native adapters and storage fixtures.
- [Lookup and drops](broccolium/modules/lookup-drops.md): lookup specificity/fallback, target adaptation and global drop-capture lifecycle.
- [Data generation](broccolium/modules/data.md): GeneratorSink ownership, model/loot/language/recipe composition, validation and version overrides.
- [Loader integration](broccolium/integrations/loaders.md): Fabric transactions/lookups versus Forge capabilities and NeoForge nullable contexts.

## Tweakium: ComputerCraft framework and gameplay services

- [Peripheral lifecycle](tweakium/modules/peripheral.md): owned peripherals, attachment, dispatch, plugins/boons and settings storage.
- [Storage plugins and operations](tweakium/modules/plugins.md): Lua slot/query transfers, caller-specific validation, units, cooldown/fuel ordering and partial updates.
- [Player and fake-player actions](tweakium/modules/player.md): contextual ownership, fake-player caching, attributes, interactions, drops and cleanup boundaries.
- [Platform, data and configuration](tweakium/modules/platform-data.md): ComputerPlatformToolkit installation, serialization, registries, config getters and generated assets.
- [Representation and RML](tweakium/modules/representation-rml.md): Lua input/output schemas, registry-aware enchantment changes, map filters versus render markup parsing.
- [Pocket/turtle persistence](tweakium/modules/upgrades-persistence.md): builders, stateful upgrade item transport, NBT versus component-patch adapters, removal preservation and encoding failures.
- [Loader integration](tweakium/integrations/loaders.md): bootstrap ordering, upgrades, dedicated client paths and test-only loaded-mod wiring.

## Peripheralium, Testiarium and typed clients

[Peripheralium content](peripheralium/content.md) owns material registrations and generated recipes/assets, not the generic peripheral dispatcher. Its [loader integration](peripheralium/integrations/loaders.md) attaches those registrations and datagen. Check the 1.21.1 recipe override: the inspected source lacks the legacy blend-smelting recipe; this is documented, not repaired here.

[Testiarium harness](testiarium/harness.md) owns explicit test-class discovery, annotations and XML/HTML reporting; testMod/cctTestMod features carry fixtures separately from the main API. [Client/CCT protocols](testiarium/client-cct.md) explain asynchronous Lua labels, screenshots, watchdog/failure handling and optional integration boundaries.

[Typed peripheral API](typed-peripheral-api/contracts.md) owns TypeScript interfaces, provider type names and Lua packaging. It is maintained independently from Kotlin and needs runtime schema/error/precision review when plugins change; a type-check is not API conformance testing.

## Verification and known limits

Use [verification workflows](testing/verification.md) to select exact module/loader tasks and fresh-report guards. Application checks use the checked-in wrapper and --no-daemon. A GameTest process exiting zero is insufficient: its fresh report must contain a non-skipped case and no failure/error. Broad builds can switch/transform source, generate assets or emit TypeScript files, so they are not a harmless wiki linter.

This wiki describes inspected source and existing tests at the baseline; historical successful application runs belong to [the migration audit](../docs/stonecutter-migration-audit.md), not to wiki-generation execution. No new gameplay changes, test expansion, Minecraft 26.1 support, application test run, publishing or scheduled automation is part of this task. Remaining investigation includes live save/reload and upgrade reinstall, fake-player exceptions/combat, full client/CCT coverage and typed-client/runtime parity. Claims and generated provenance are managed by OpenWiki; source remains authoritative.
