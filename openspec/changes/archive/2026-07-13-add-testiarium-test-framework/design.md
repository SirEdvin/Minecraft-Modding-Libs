## Context

The existing libraries are built together in this repository, and Testiarium will follow that established core, Forge, and Fabric subproject layout. It remains a separate production artifact family with no Broccolium production dependency. CC:Tweaked implements its test framework as a `testMod` source set: loader bootstrap registers GameTests; shared code replaces the GameTest reporter with a JUnit XML writer; Gradle launch profiles configure structures, tags, assertion status, and reports. Its test classes, computer scheduler, peripheral helpers, file importer, and client interactions are CC:Tweaked-specific.

The initial upstream baseline is CC:Tweaked `mc-1.20.x` at `6f16cd6b0e4b74afff5462d463bedba65764970e`. Its testing sources use MPL-2.0. The initial Testiarium release targets Minecraft 1.20.1 on Forge and Fabric. Minecraft 1.21 and NeoForge are deferred to a subsequent change.

## Goals / Non-Goals

**Goals:**

- Ship core, Forge, and Fabric Testiarium artifacts for Minecraft 1.20.1.
- Provide a `testMod` source set with explicit GameTest class registration, tag filtering, helper utilities, loader bootstrap, and JUnit XML result output.
- Preserve the ability to run a separate CC:Tweaked adapter that tests its public peripheral APIs.
- Preserve MPL-2.0 notices and record provenance for every adapted CC:Tweaked source or resource.

**Non-Goals:**

- Making Testiarium a Broccolium subproject or runtime dependency.
- Recreating CC:Tweaked's computer scheduler, file importer, commands, client UI automation, or test suite.
- Requiring CC:Tweaked for Testiarium compilation, testmod startup, ordinary GameTests, or the generic helper API.
- Supporting Minecraft versions outside 1.20.1 in the initial release.

## Decisions

### Follow the repository's core and loader subproject layout

Testiarium will use the repository's Gradle wrapper, version catalog, version properties, publication configuration, and core/Forge/Fabric subproject layout used by Broccolium. It will provide a `testMod` source set for Minecraft 1.20.1, with Forge and Fabric loader modules resolving the compatible mappings, loader dependencies, and GameTest launches.

Making Testiarium a Broccolium module was rejected because it must remain a separately published artifact with no Broccolium production dependency.

### Adapt only the generic CC:Tweaked testmod components

The common testmod code will adapt the JUnit reporter, multi-reporter, tag filtering, `ClientGameTest` annotation, generic GameTest sequence/helper utilities, and only the GameTest mixins required by those utilities. It will expose explicit test-class registration rather than copying CC:Tweaked's fixed list of its own test classes. Forge and Fabric modules will register that API with their native GameTest event or registry.

Copying CC:Tweaked's complete `TestHooks`, `TestExtensions`, commands, and test classes was rejected because they directly depend on CC:Tweaked internals such as `ComputerCraftAPI`, `ServerContext`, `ManagedComputers`, `IPeripheral`, and platform helpers.

### Keep CC:Tweaked integration optional and loader-specific

The base common API will not reference `dan200.computercraft` types. A dedicated optional testmod adapter will compile against CC:Tweaked's public API and register only when CC:Tweaked is present. Forge and Fabric development runs that exercise peripherals will include the matching CC:Tweaked runtime artifact; ordinary Testiarium runs will not.

Using a required CC:Tweaked dependency was rejected because it prevents testing mods that do not use CC:Tweaked and couples Testiarium releases to CC:Tweaked availability.

### Use JUnit XML as the Gradle-facing test result interface

Testiarium will use the platform GameTest runner as the execution authority. Its testmod initialization will replace the global reporter with a multi-reporter that preserves log output and writes JUnit XML to the configured system-property path. Gradle game-test launch profiles will set the structure source directory, enabled tags, assertions, loader-specific GameTest flag where required, and result path. Test fixtures will validate a passing test, a required failing test captured in XML, and a non-required failure reported as skipped.

A separate custom test runner was rejected because GameTest already provides world setup, scheduling, commands, and CI-friendly process status.

## Risks / Trade-offs

- [CC:Tweaked test code changes independently] -> Record the inspected upstream revision and keep adapted MPL-2.0 code limited to the generic source set.
- [Minecraft or loader APIs differ in future release lines] -> Isolate the 1.20.1 Forge/Fabric bootstrap and adapters; add later version-specific modules in a separate change.
- [CC:Tweaked is absent or differs from the tested version] -> Guard integration loading by mod presence and declare compatible API/runtime versions in the integration test configuration.
- [A GameTest failure is hard to consume in CI] -> Write JUnit XML with test identifiers, error messages, and stack traces while retaining standard GameTest logs.

## Migration Plan

1. Create and publish Testiarium as its own artifact family without modifying Broccolium's production dependencies.
2. Add a Broccolium testmod that consumes the Testiarium artifacts and migrate its in-game checks incrementally.
3. Remove any temporary Broccolium-local GameTest scaffolding only after the Testiarium testmod runs on both supported release lines.

## Open Questions

- Which generic helper extensions and mixins are required for the initial API after excluding every CC:Tweaked-dependent helper?
- Which CC:Tweaked peripheral scenarios are required for the first optional adapter?
