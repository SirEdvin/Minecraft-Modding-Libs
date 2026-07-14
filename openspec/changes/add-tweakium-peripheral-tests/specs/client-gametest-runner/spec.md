## ADDED Requirements

### Requirement: Opt-in client GameTest execution
Testiarium SHALL provide an opt-in Forge and Fabric client GameTest runner that initializes a deterministic test world, executes registered client tests on the client thread, writes JUnit XML results, and exits with a status that reflects test success or failure.

#### Scenario: Run client tests successfully
- **WHEN** a developer launches the configured client GameTest run
- **THEN** Testiarium executes the registered client tests and writes a passing JUnit XML report before exit

#### Scenario: Report a client test failure
- **WHEN** a client test assertion fails
- **THEN** the runner records the failure in JUnit XML and exits unsuccessfully

### Requirement: Client interaction and rendering assertions
The client runner SHALL provide client-thread actions, render-idle waiting, player reset and positioning, open-menu assertions, and screenshot capture for registered client tests. Client-only classes and hooks MUST NOT load on dedicated servers.

#### Scenario: Capture a stable screenshot
- **WHEN** a client test waits for rendering to become idle and requests a screenshot
- **THEN** Testiarium captures the rendered test state at the configured output location

#### Scenario: Start a dedicated server
- **WHEN** a dedicated server loads Testiarium
- **THEN** client runner classes and client-only mixins are not initialized
