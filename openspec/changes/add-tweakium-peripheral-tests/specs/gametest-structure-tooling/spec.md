## ADDED Requirements

### Requirement: Consumer SNBT fixture workflow
Testiarium SHALL load consumer-provided named SNBT GameTest fixtures and provide test-only tooling to import fixture resources, export a fixture, and regenerate configured structures. Compact fixtures with omitted air blocks MUST load with their intended air volume restored.

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
