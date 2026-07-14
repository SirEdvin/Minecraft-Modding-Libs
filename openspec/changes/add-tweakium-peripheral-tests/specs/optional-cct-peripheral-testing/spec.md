## ADDED Requirements

### Requirement: Full CC:Tweaked testing package
Testiarium SHALL provide `site.siredvin.testiarium.cct` as an optional cohesive package with functional parity for CC:Tweaked GameTest development. The package SHALL contain all CC:Tweaked-dependent computer, Lua, peripheral, component, fixture, and testmod operations. Testiarium core SHALL contain the complete CC:Tweaked-independent Minecraft GameTest engine and MUST NOT reference CC:Tweaked types or internals.

#### Scenario: Use Testiarium core without CC:Tweaked
- **WHEN** a Minecraft testmod uses Testiarium core without adding the CCT package
- **THEN** it can use Testiarium's complete generic GameTest engine without CC:Tweaked installed

#### Scenario: Use the CCT package
- **WHEN** a CC:Tweaked testmod adds `site.siredvin.testiarium.cct`
- **THEN** it can write GameTests using the full CC:Tweaked computer, Lua, peripheral, component, fixture, and testmod feature set

### Requirement: CC:Tweaked managed computer GameTest harness
The CCT package SHALL provide reusable GameTest sequence operations that execute Kotlin actions on a labeled in-game computer and report completion or failure to the owning GameTest.

#### Scenario: Execute Kotlin action on a fixture computer
- **WHEN** an adapter consumer schedules a Kotlin action for a labeled computer in a GameTest fixture
- **THEN** the action executes in that computer's CC:Tweaked runtime and its success or failure completes the GameTest sequence

#### Scenario: Run without CC:Tweaked
- **WHEN** Testiarium runs without CC:Tweaked installed
- **THEN** standalone Testiarium GameTests remain available and the managed computer harness is not loaded

### Requirement: CC:Tweaked Lua test-file execution
The CCT package SHALL import consumer-provided Lua test resources and execute the Lua file selected by a fixture computer's label. Lua completion and Lua errors MUST be reported to the owning GameTest.

#### Scenario: Execute a Lua test file
- **WHEN** a labeled fixture computer is started for a GameTest with a matching imported Lua test file
- **THEN** the computer executes that file and successful completion marks the scheduled test action complete

#### Scenario: Report a Lua test failure
- **WHEN** an imported Lua test file raises an error or explicitly reports failure
- **THEN** its owning GameTest fails with the Lua error details

### Requirement: CC:Tweaked fixture support and provenance
The CCT package SHALL support consumer-provided SNBT GameTest fixtures containing CC:Tweaked computers and peripherals. Any Testiarium source adapted from CC:Tweaked's test-only harness MUST preserve MPL-2.0 licensing notices and record the inspected upstream source revision.

#### Scenario: Load a computer fixture on both loaders
- **WHEN** Forge and Fabric launch a GameTest that names a consumer-provided SNBT computer fixture
- **THEN** the fixture is available to the test and its labeled computer can execute a managed action

#### Scenario: Inspect adapted test harness source
- **WHEN** a maintainer reviews adapted CC:Tweaked harness source
- **THEN** each adapted source records its MPL-2.0 license and upstream provenance

### Requirement: CC:Tweaked component GameTest helpers
The CCT package SHALL provide the fixture setup, assertions, and sequence operations needed for consumer GameTests equivalent to CC:Tweaked's component test families: component/peripheral exposure, computers and redstone, CraftOS, disks, disk drives, inventories, wired modems, monitors, pocket computers, printers, printouts, recipes, relays, speakers, turtles, and loot. These helpers MUST validate consumer-owned content and MUST NOT copy CC:Tweaked's component behavior tests, registries, or test datapack content into Testiarium.

#### Scenario: Test a CC:Tweaked peripheral component
- **WHEN** a consumer supplies an SNBT fixture with a computer and a CC:Tweaked peripheral component
- **THEN** the adapter provides operations to invoke it from the fixture computer and assert the resulting world, inventory, event, or peripheral state

#### Scenario: Test a CC:Tweaked turtle or pocket upgrade
- **WHEN** a consumer supplies an SNBT fixture with a turtle or pocket computer and a consumer-owned upgrade
- **THEN** the adapter provides computer execution and state assertions needed to verify the upgrade behavior

#### Scenario: Test a CC:Tweaked client component
- **WHEN** a consumer registers a client GameTest for a monitor, pocket computer, printout, or turtle rendering case
- **THEN** the optional adapter interoperates with Testiarium's client runner to execute the case and capture its screenshot assertion

### Requirement: CC:Tweaked testmod operations
The CCT package SHALL provide `/testiarium cct import`, `/testiarium cct export`, and `/testiarium cct give-computer` test-only commands. The import and export operations SHALL synchronize consumer computer files, and `give-computer` SHALL create a labeled fixture computer. Generic structure import, export, regeneration, and marking commands belong to Testiarium core's testmod. These commands MUST be unavailable from Testiarium's published runtime artifacts.

#### Scenario: Import consumer Lua fixtures
- **WHEN** a developer runs the adapter's import operation for a consumer testmod
- **THEN** the consumer Lua files are available to labeled fixture computers for the next GameTest run

#### Scenario: Export a structure fixture
- **WHEN** a developer runs the adapter's structure export operation for a named test
- **THEN** it writes an SNBT fixture suitable for the consumer's configured test structure source
