## ADDED Requirements

### Requirement: Cross-loader Tweakium peripheral GameTest runtime
Tweakium SHALL provide Forge and Fabric GameTest testmods that load Tweakium, Testiarium, and the compatible CC:Tweaked runtime without adding either test dependency to published Tweakium artifacts. Each loader run MUST register the Tweakium peripheral tests and write a JUnit XML result report.

#### Scenario: Run Tweakium peripheral GameTests on Forge
- **WHEN** a developer launches Tweakium's Forge peripheral GameTest configuration
- **THEN** the Tweakium tests execute with Forge CC:Tweaked and produce a Forge JUnit XML report

#### Scenario: Run Tweakium peripheral GameTests on Fabric
- **WHEN** a developer launches Tweakium's Fabric peripheral GameTest configuration
- **THEN** the Tweakium tests execute with Fabric CC:Tweaked and produce a Fabric JUnit XML report

#### Scenario: Build published Tweakium artifacts
- **WHEN** Tweakium's normal production artifacts are built
- **THEN** Testiarium and CC:Tweaked testmod dependencies are not published as Tweakium runtime dependencies

### Requirement: Creative Filler Lua behavior coverage
The Tweakium peripheral GameTests SHALL use a named SNBT fixture that places and labels a computer, Creative Filler, its network connection, and a compatible target inventory. The fixture computer MUST execute a Lua test file through Testiarium's CC:Tweaked harness. The Lua test MUST verify successful item filling into the target and failures for an invalid mode, a missing target, an incompatible target, and an unknown item identifier.

#### Scenario: Fill a compatible inventory from Lua
- **WHEN** the fixture computer discovers the Creative Filler and its Lua test requests an item fill for the compatible target inventory
- **THEN** the target inventory contains the requested item up to the applicable stack limit

#### Scenario: Reject invalid fill requests from Lua
- **WHEN** the Lua test sends the Creative Filler an invalid mode, missing target, incompatible target, or unknown item identifier
- **THEN** the Lua call fails with the corresponding error and does not mutate the target inventory
