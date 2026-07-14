## Why

Tweakium exposes ComputerCraft peripheral behavior but has no automated in-game coverage for it. Testiarium provides only part of CC:Tweaked's GameTest framework, so consumers cannot reproduce its computer, Lua, fixture, assertion, client-render, or testmod workflow coverage.

## What Changes

- Put the complete loader-neutral Minecraft GameTest engine in Testiarium core: registration, lifecycle, structures, assertions, fixtures, commands, client runner, screenshots, reporting, and Forge/Fabric runs.
- Add `site.siredvin.testiarium.cct` as Testiarium's optional CC:Tweaked package, with functional parity for CC:Tweaked computer, Lua, peripheral, component, and testmod features.
- Add a Tweakium testmod that uses the harness with a per-test SNBT structure containing the computer, Creative Filler, network, and target inventory.
- Exercise the Creative Filler from Lua, including successful item transfer and invalid-target failures.
- Configure the loader GameTest runs to include Tweakium, Testiarium, and the matching CC:Tweaked runtime and emit JUnit XML reports.

## Capabilities

### New Capabilities
- `tweakium-peripheral-gametesting`: Automated Forge and Fabric integration coverage for Tweakium ComputerCraft peripherals.
- `gametest-assertion-helpers`: Reusable GameTest sequencing and world-state assertions.
- `gametest-structure-tooling`: Consumer fixture loading, import, export, and compact structure support.
- `client-gametest-runner`: Opt-in deterministic client GameTest execution and screenshot assertions.

### Modified Capabilities

- `optional-cct-peripheral-testing`: Reusable CC:Tweaked computer and Lua GameTest support.

## Impact

Affected modules: Testiarium core, its `site.siredvin.testiarium.cct` optional package, loader integration, mixins, commands, and run configurations, plus `tweakium-core`, `tweakium-forge`, and `tweakium-fabric` testmods and Gradle configurations. The test-only runtime gains Testiarium and CC:Tweaked; published Tweakium APIs and runtime dependencies remain unchanged.
