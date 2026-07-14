## Context

Tweakium's Creative Filler is its concrete block peripheral, but its Lua-facing behavior is untested in a loaded Minecraft and CC:Tweaked environment. Testiarium supplies explicit GameTest registration, loader adapters, tags, structures, and JUnit XML reporting, but its optional CC:Tweaked adapter only verifies that the public API loads.

CC:Tweaked's testmod has 109 annotated tests across component, computer, CraftOS, disk, disk drive, inventory, modem, monitor, pocket computer, printer, printout, recipe, relay, speaker, turtle, and loot behavior. Its reusable framework comprises server/client runners, managed computers, Lua fixtures, typed assertions, fixture import/export, commands, mixins, and loader hooks. This change provides functional parity for those facilities, not a copy of CC:Tweaked's own 109 product behavior tests. Tweakium is the first consumer and validates the resulting Lua peripheral workflow.

Testiarium core is a complete CC:Tweaked-independent Minecraft GameTest engine. The optional `site.siredvin.testiarium.cct` package is a cohesive extension of that engine, not a minimal adapter or a consumer-local test utility. It carries every CC:Tweaked-dependent type and implementation required to write tests in the style of CC:Tweaked's own suite.

## Goals / Non-Goals

**Goals:**

- Run the same Creative Filler peripheral GameTests against Forge and Fabric 1.20.1.
- Provide reusable generic server and client GameTest helpers equivalent to CC:Tweaked's testmod facilities.
- Provide an optional CC:Tweaked harness for Kotlin computer actions, Lua execution, peripheral/component assertions, and testmod commands.
- Verify a fixture-provided computer can discover the Creative Filler and fill a compatible target storage from Lua.
- Cover invalid mode, missing target, incompatible target, and unknown resource errors as Lua failures.
- Produce loader-specific JUnit XML reports through Testiarium's normal GameTest runs.

**Non-Goals:**

- Test unrelated Tweakium modules, pocket or turtle upgrades, or every storage backend.
- Add Testiarium or CC:Tweaked as a published Tweakium runtime dependency.
- Generalize CC:Tweaked internals into Testiarium's base API or support arbitrary external computer implementations.
- Copy CC:Tweaked's product-specific test classes, registries, loot data, upgrades, or component behavior into Testiarium.

## Decisions

### Make Testiarium core the complete Minecraft GameTest engine

Core owns all functionality that can operate without CC:Tweaked: registration, tags, server lifecycle, reporting, fail-fast sequence behavior, world assertions, SNBT fixture workflow, import/export tooling, deterministic client execution, screenshots, and loader hooks. It exposes these features as consumer APIs rather than keeping them as Testiarium-internal test utilities.

Moving generic logic into the CC:Tweaked package is rejected because it would make ordinary Minecraft GameTest consumers depend on a CC:Tweaked installation.

### Add `site.siredvin.testiarium.cct` as the full CC:Tweaked testing package

The `site.siredvin.testiarium.cct` package extends core with every CC:Tweaked-dependent facility required by the upstream test style: managed computers, Kotlin computer actions, Lua test-file execution, completion reporting, component/peripheral assertions, CC fixture support, and testmod commands. The package may use CC:Tweaked internals because it is optional and version-specific; it will retain MPL-2.0 notices and provenance for adapted source.

Copying CC:Tweaked's own product test cases, registries, or fixture data is rejected. Directly calling peripheral methods with mocks is rejected because it does not validate the Lua contract.

### Port reusable server, fixture, and assertion facilities

Testiarium core will add fail-fast sequences and typed helpers for block entities, containers, entities, block state, items, and recipes. Its testmod tooling will load consumer SNBT fixtures, support import/export commands for test resources, and preserve compact fixtures by restoring omitted air blocks at load time. CC:Tweaked peripheral and computer-specific assertions live exclusively in `site.siredvin.testiarium.cct`.

Reimplementing each consumer's assertions and fixture handling is rejected because it would duplicate the same version-sensitive GameTest behavior across every testmod.

### Add an opt-in client GameTest runner

Testiarium will provide a client-only runner that creates a deterministic test world, executes registered client tests on the client thread, waits for rendering to stabilize, supports player/menu assertions and screenshots, writes JUnit XML, and terminates with the test result. Client hooks and mixins remain loader- and environment-isolated.

Making client behavior part of the normal dedicated-server test path is rejected because it would require client classes and rendering initialization on servers.

### Add isolated Tweakium testmod source sets and fixtures

Put shared GameTest cases, Lua test files, and SNBT structure fixtures in Tweakium testmod sources, with Forge and Fabric entrypoints that use Testiarium's loader adapters. Each fixture will place and label a computer alongside the Creative Filler, its network connection, and an inventory target. Loader builds will add Testiarium and the matching CC:Tweaked runtime only to testmod/GameTest configurations.

Reusing Testiarium's own adapter testmod was rejected because it only proves CC:Tweaked's public API is available, not Tweakium's peripheral behavior. Duplicating the framework setup was rejected because Testiarium already owns registration and reporting.

### Test the Creative Filler from Lua

The test will enqueue the labeled fixture computer and execute a Lua file that discovers the Creative Filler and calls its exposed method. Kotlin GameTest assertions will check the target inventory after Lua reports success. The Lua test will assert errors for invalid requests so failures surface through Testiarium's GameTest and JUnit reporting.

Direct unit tests of `FillerStrategy` were rejected because they bypass block exposure, loader storage lookup, and CC:Tweaked peripheral integration.

### Follow Testiarium's existing GameTest report contract

The new Forge and Fabric GameTest launch configurations will enable the Tweakium test namespace, set the shared structure and report paths, enable assertions, and write a JUnit XML report under each module's build directory. Testiarium core initializes generic lifecycle and fixture services; `site.siredvin.testiarium.cct` initializes computer and Lua services before tests run. Tweakium supplies named SNBT fixtures instead of relying on the empty template.

Custom reporting or Gradle test-task integration was rejected because the GameTest process and Testiarium reporter already expose CI-compatible results.

## Risks / Trade-offs

- [CC:Tweaked test internals change independently] -> Adapt only the harness components required by the consumer API, preserve MPL-2.0 provenance, and keep them isolated from Testiarium's base artifacts.
- [Client test runs are graphics-driver dependent] -> Use deterministic client setup and explicit render-idle checks; retain opt-in execution and screenshots as diagnostics.
- [Structure import/export is version-sensitive] -> Keep commands and mixins in testmod sources and validate compact SNBT fixtures on both loaders.
- [Fixture import or managed computers fail to become idle] -> Surface Lua completion and failures through the test API and make the GameTest wait for the managed computer result.
- [Test runtime dependencies leak into publication] -> Restrict Testiarium and CC:Tweaked dependencies to testmod configurations and verify normal production builds remain unchanged.
- [Structure fixtures are loader-sensitive] -> Use a shared SNBT fixture with only common block states and validate it on both loaders.
