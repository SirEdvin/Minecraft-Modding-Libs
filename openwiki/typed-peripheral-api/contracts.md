---
type: contracts
title: Typed peripheral API contracts and packaging
description: Independently maintained TypeScript-to-Lua contracts, provider identities, extension patterns and runtime mismatch risks outside the Stonecutter loader graph.
tags: [typescript, lua, contracts, packaging, peripherals]
sources:
  - id: openwiki-source-cc02d3f8883805a8e9de0ccf
    resource: repo://docs/module-test-coverage.md
  - id: openwiki-source-433a5a8f61ef03e88c1fd1ae
    resource: repo://docs/verification/minecraft-26-1-R3.md
  - id: openwiki-source-851986a2438c3c75fd1c9bb7
    resource: repo://docs/verification/minecraft-26-1-R7-final-integration.md
  - id: openwiki-source-ffeb994ab0f25d59b017758e
    resource: repo://docs/verification/minecraft-26-1-R7-legacy.md
  - id: openwiki-source-18505126b4fc17c6fe763eea
    resource: repo://projects/typed-peripheral-api/build.gradle.kts
  - id: openwiki-source-b2992bb4316ae5243156d682
    resource: repo://projects/typed-peripheral-api/tests/contracts.ts
  - id: openwiki-source-e620d7484b72a53c7fa812cd
    resource: repo://settings.gradle.kts
generated: { by: "hermes", at: "2026-10-19T00:45:42.798Z" }
verified:
  - by: openwiki/0.7.1
    at: 2026-10-19T13:32:11.388Z
---

# Typed peripheral API contracts and packaging

## Current compiler/runtime correlation (2026-10-19)

This package remains independent of Stonecutter and library2.0.0 versions. Gradle check runs the no-emission compiler contracts, ordered after emission only when both tasks are selected. Runtime Lua names, schemas, error results and numeric/unit limits remain separate obligations: TypeScript number is not a conservation or precision guarantee.

[R3](../../docs/verification/minecraft-26-1-R3.md) and [R7 legacy](../../docs/verification/minecraft-26-1-R7-legacy.md) correlate native CC/Lua behavior with declarations; final integration replays the stronger Creative Filler fixture on both target loaders and forced TypeScript contracts. Maintained predicates now reject malformed/unknown/empty operations; the historical parser-ignore finding below is not current behavior. [Final R7](../../docs/verification/minecraft-26-1-R7-final-integration.md) binds actual source/artifacts, not just matching method names.

Inherited strict-null dependency errors and npm advisory remain explicit in existing triage t_470a1973. Compiler mutation evidence is not a clean dependency audit or universal emitted-Lua conformance. No npm release was performed.

## Historical source notes — October16 baseline

The following327f845/e69b721 package/type inventory and mutation results remain historical provenance; later real Lua correlation above supersedes its then-unperformed-runtime statement without inventing complete schema certification.

## Separate consumer-facing boundary

projects/typed-peripheral-api is included by settings.gradle.kts as its own Gradle subproject, but is not among the four Stonecutter trees. It supplies TypeScript interfaces and a few executable provider/dummy helpers for ComputerCraft clients, not Minecraft registry or loader entrypoints. The package is @siredvin/typed-peripheral-api, version 0.2.2 in the inspected manifest. Do not infer its version from the Kotlin libraries' gradle.properties.

The Gradle base/Node build downloads Node 22.14.0 into root .gradle directories, uses npm ci, and makes assemble depend on compileTypeScript (npm run build). TypeScript-to-Lua emits declarations and Lua alongside the source; clean deletes those .d.ts/.lua outputs. tsconfig targets Lua 5.1 library mode with require-minimal support and TypeScript declarations enabled, but strict checking disabled. The npm files list publishes root .d.ts and .lua files; Gradle assembly is not proof of npm publication. Dependency ranges include wildcard typed-peripheral-base, TypeScript and TypeScript-to-Lua, so preserve/review package-lock.json for reproducibility rather than deriving exact resolution from package.json.

## Interface composition and runtime entrypoints

Interfaces marked @noSelf express peripheral-style calls without an implicit Lua self argument. InventoryAPI extends InventoryViewAPI and declares numeric-slot pushItems/pullItems. ExtendedInventoryAPI combines that contract with ConfigurationAPI<ExtendedInventoryConfiguration>, adds detailed/query list overloads and accepts either numeric slots or ItemQuery filters in transfers. Its configuration includes inventoryTransferLimit. ItemStorageAPI provides unslotted query-based items/pushItem/pullItem. Fluid and energy extended interfaces compose their base APIs and add precision/capacity or transfer methods.

Exported IPeripheralProvider instances use wire type names such as inventory, inventory_extended, item_storage, fluid_storage_extended and energy_storage_extended. Their fallback callbacks return null; lookup/discovery behavior belongs to the external typed-peripheral-base dependency, not an implementation in this repository. Changing a Kotlin plugin's additional type name without changing this package can break discovery even when method signatures still compile.

Most files declare contracts rather than real peripherals. DummyFuelApi is an exception: it returns fixed fuel levels and stores the consumption rate in a module-global variable shared between instances. Its setter returns a successful Lua multi-result without the bounds checks of the Kotlin FuelBoon. It is a dummy, not a simulator proving production fuel behavior. OperationApi separately declares getOperations/getCooldown; ConfigurationAPI only declares getConfiguration, not a generic configuration mutation method.

## Match effective Lua behavior, not just names

ItemQuery and BlockQuery describe optional aliases and recursively composed OR/AND/NONE/NOT maps. They are client-side types, not runtime validators. The Kotlin parser ignores unknown map fields and has empty-composition failure edges; callers must still handle Lua errors. The TS declarations are not generated from Kotlin, and TResult/LuaTable types cannot prove runtime shape, slot semantics, numeric range or transactional behavior. See [storage plugins and operations](../tweakium/modules/plugins.md).

ScanApi gives overloads for item, block, entity, player and xp results. Its block-state type allows string/number/boolean, while the shared LuaRepresentation.forBlockState stringifies values. This is a broader client type rather than evidence of all possible runtime values. Position orientation, component/enchantment version differences and rawNBT semantics remain owned by [representation](../tweakium/modules/representation-rml.md). Kotlin Long/Double narrowing and fluid units need separate review even though TypeScript calls them all number.

## Extension and verification

For a new API, coordinate the Kotlin plugin's method names, additional type name, table schema and error/multi-result behavior with its TypeScript interface and provider. Add a root .ts file to tsconfig's explicit include list when appropriate; preserve existing consumer contracts.

npm test now invokes tsc with tests/tsconfig.json and noEmit. Gradle check depends on testContracts; it mustRunAfter compileTypeScript when both are selected, avoiding declaration-file read/write overlap without requiring emission for standalone checks. The contract suite compares inferred types in both directions and rejects any erasure, calls every inventory/item-storage list overload, all five scan modes, and both wrapping overloads of all nine exported providers. Twelve expected-error directives protect invalid arguments and scan filters.

The coverage task passed standalone and combined builds and replayed compiler mutations: 66 of 67 detected, all 12 negative diagnostics verified for the intended reason. Removing null remains undetected with inherited strictNullChecks disabled; enabling it exposes five errors in dependency TypeScript source. No suppression conceals this limit. The inherited dependency audit also reports one high-severity picomatch advisory; dependency remediation was not bundled into the contract-test patch. These checks do not execute emitted Lua or prove runtime client/server schema parity. [Build/publication](../build/stonecutter-publication.md) and [verification](../testing/verification.md) distinguish packaging, compiler assertions and runtime evidence.
