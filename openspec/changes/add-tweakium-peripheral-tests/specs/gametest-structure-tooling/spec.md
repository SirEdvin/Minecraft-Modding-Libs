## ADDED Requirements

### Requirement: Consumer SNBT fixture workflow
Testiarium SHALL load consumer-provided named SNBT GameTest fixtures and provide test-only tooling to import fixture resources, export a fixture, and regenerate configured structures. Compact fixtures with omitted air blocks MUST load with their intended air volume restored.

The Testiarium testmod SHALL own the required version-sensitive mixins and register `/testiarium import`, `/testiarium export`, `/testiarium regen-structures`, and `/testiarium marker`. The import and export operations SHALL synchronize the configured consumer fixture source; `regen-structures` SHALL re-import and export every registered GameTest structure; and `marker` SHALL mark the nearest test fixture position.

#### Scenario: Load a compact named fixture
- **WHEN** a consumer GameTest names a compact SNBT fixture
- **THEN** the GameTest loads the fixture with omitted positions treated as air

#### Scenario: Export a fixture
- **WHEN** a developer invokes the configured fixture export operation for a named test area
- **THEN** Testiarium writes the fixture to the configured consumer structure source

### Requirement: Test-only fixture operations
Fixture commands and version-sensitive mixins SHALL be isolated to Testiarium testmod execution and MUST NOT affect normal mod runtime behavior.

#### Scenario: Run a normal production server
- **WHEN** a production server loads a mod that depends on Testiarium's published artifacts
- **THEN** test fixture commands and structure hooks are not registered
