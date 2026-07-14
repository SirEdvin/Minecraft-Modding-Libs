## ADDED Requirements

### Requirement: Server GameTest sequence and assertion helpers
Testiarium SHALL provide reusable server-side GameTest sequence operations that fail the owning test for action errors and helpers to assert block state, block entities, containers, entities, item stacks, and recipe results within a fixture.

#### Scenario: Assert a fixture container
- **WHEN** a consumer asserts a fixture container's expected item stacks
- **THEN** a mismatch fails the GameTest with the fixture position and expected and actual contents

#### Scenario: Fail a sequence action
- **WHEN** a consumer sequence action throws an assertion or runtime error
- **THEN** the owning GameTest fails rather than crashing the server process

### Requirement: CC-free generic API
Generic assertion and sequence helpers SHALL NOT reference CC:Tweaked classes or internals.

#### Scenario: Use generic helpers without CC:Tweaked
- **WHEN** a testmod without CC:Tweaked uses Testiarium's generic assertion helpers
- **THEN** it compiles and runs without CC:Tweaked installed
