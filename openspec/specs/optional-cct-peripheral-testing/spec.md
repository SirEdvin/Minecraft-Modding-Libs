## Purpose

Define optional CC:Tweaked peripheral integration testing for Testiarium.

## Requirements

### Requirement: Optional CC:Tweaked peripheral integration
Testiarium SHALL provide an optional adapter testmod for scenarios that use CC:Tweaked public peripheral APIs when a compatible CC:Tweaked installation is present. The base Testiarium artifacts and generic helper API MUST NOT require CC:Tweaked.

#### Scenario: Run without CC:Tweaked
- **WHEN** a Testiarium testmod runs without CC:Tweaked installed
- **THEN** standalone Testiarium GameTests run and the CC:Tweaked adapter does not register its integration tests

#### Scenario: Run with CC:Tweaked
- **WHEN** a compatible CC:Tweaked installation is present with the Testiarium adapter testmod
- **THEN** the adapter can execute GameTests against CC:Tweaked public peripheral APIs

### Requirement: Loader-correct CC:Tweaked test runtime
The Forge and Fabric peripheral integration test configurations MUST resolve the corresponding CC:Tweaked API and runtime artifacts for Minecraft 1.20.1.

#### Scenario: Launch peripheral test on Fabric
- **WHEN** a developer launches the Fabric peripheral integration test configuration
- **THEN** it uses Fabric-compatible CC:Tweaked artifacts and executes the integration tests

#### Scenario: Launch peripheral test on Forge
- **WHEN** a developer launches the Forge peripheral integration test configuration
- **THEN** it uses Forge-compatible CC:Tweaked artifacts and executes the integration tests
