## Why

Minecraft library changes need repeatable in-game verification on both maintained release lines. Testiarium will provide a standalone testmod framework so Broccolium and other projects can run and inspect GameTests without depending on CC:Tweaked, while still allowing CC:T peripheral tests when it is present.

## What Changes

- Create the Testiarium library family alongside the existing libraries with core, Forge, and Fabric projects for Minecraft 1.20. Minecraft 1.21 support will be added separately.
- Adapt CC:Tweaked's MPL-2.0 testmod components for test registration, tags, GameTest reporting, and launch configuration.
- Provide a testmod source-set API that lets downstream mods explicitly register GameTest classes, filter test groups, and emit JUnit XML results.
- Make CC:Tweaked computer and peripheral helpers an optional integration with no CC:Tweaked dependency for ordinary Testiarium consumers.
- Keep Testiarium's production artifacts independent from Broccolium while following the repository's established multi-project build and publication conventions.

## Capabilities

### New Capabilities

- `testiarium-project-layout`: Provide core, Forge, and Fabric Testiarium artifacts for Minecraft 1.20.
- `testmod-gametest-framework`: Let mod developers register, filter, execute, and report cross-loader GameTests without CC:Tweaked.
- `optional-cct-peripheral-testing`: Let testmods use CC:Tweaked peripherals when CC:Tweaked is installed, without making it required for other Testiarium users.

### Modified Capabilities

None.

## Impact

- Adds Testiarium core, Forge, and Fabric projects, loader metadata, and testmod sources for Minecraft 1.20.
- Establishes public testmod registration, tagging, helper, and JUnit result-reporting APIs for downstream mod testmods.
- Adapts selected CC:Tweaked testing sources under MPL-2.0 with retained notices and source provenance.
- Does not add Testiarium as a Broccolium dependency; Broccolium will consume it only from its own testmod setup.
