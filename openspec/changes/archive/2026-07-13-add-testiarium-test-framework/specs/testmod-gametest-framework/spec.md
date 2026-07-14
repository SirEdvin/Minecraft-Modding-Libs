## ADDED Requirements

### Requirement: CC:Tweaked-free testmod bootstrap
Testiarium SHALL provide a core `testMod` API and Forge and Fabric bootstrap support that lets a downstream mod explicitly register and run GameTest classes without CC:Tweaked being present at compile time or runtime.

#### Scenario: Run a standalone testmod
- **WHEN** a testmod using only Testiarium registers GameTest classes and is loaded on its supported loader
- **THEN** its registered GameTests are runnable without CC:Tweaked installed

### Requirement: Test group filtering
Testiarium MUST let a game-test launch enable explicitly named test groups through a system property and MUST register only tests whose group is enabled.

#### Scenario: Run a client test group
- **WHEN** a client game-test launch enables the `client` and `common` groups
- **THEN** Testiarium registers tests in those groups and excludes tests in other groups

### Requirement: Actionable GameTest results
Testiarium MUST retain GameTest log reporting and write a JUnit XML report to its configured output path. The report MUST include each executed test's identifier and outcome, plus an error message and stack trace for a required failed test.

#### Scenario: Report a failed assertion
- **WHEN** a Testiarium GameTest assertion fails
- **THEN** the GameTest output identifies the test and the JUnit XML report contains its failure message and stack trace

#### Scenario: Report a passing test
- **WHEN** a Testiarium GameTest completes successfully
- **THEN** the GameTest runner reports the test as passed and the JUnit XML report contains a passing testcase

#### Scenario: Report a non-required failure
- **WHEN** a non-required Testiarium GameTest fails
- **THEN** the JUnit XML report records the testcase as skipped with its failure message
