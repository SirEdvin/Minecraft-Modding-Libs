# tagged-maven-publishing Specification

## Purpose
TBD - created by archiving change publish-project-on-tag. Update Purpose after archive.
## Requirements
### Requirement: Project-specific release tags
The release workflow SHALL run for tags matching `<project>-<minecraft-release>-<library-version>`, where project is Broccolium, Peripheralium, Testiarium, or Tweakium, and SHALL derive all three values from the tag. The Minecraft release SHALL select the matching Stonecutter node.

#### Scenario: Supported project tag is pushed
- **WHEN** a `tweakium-1.20-1.4.6` tag is pushed
- **THEN** the workflow selects Tweakium for Minecraft release `1.20` and library version `1.4.6`

#### Scenario: Unrelated tag is pushed
- **WHEN** a tag outside the four supported project prefixes is pushed
- **THEN** the Maven publishing workflow does not run

### Requirement: Minecraft-compatible Java
The release workflow SHALL run Gradle on Java 21 and SHALL compile each selected node with its configured Java toolchain.

#### Scenario: Minecraft 1.20 release
- **WHEN** a valid tag targets Minecraft release line `1.20`
- **THEN** Gradle compiles the selected node with its Java 17 toolchain

#### Scenario: Minecraft 1.21 release
- **WHEN** a valid tag targets Minecraft release line `1.21`
- **THEN** Gradle compiles the selected node with its Java 21 toolchain

### Requirement: Release version validation
The release workflow MUST verify before invoking any publish task that the tag names a supported Minecraft release and its library version exactly matches the selected library and release version in `gradle.properties`.

#### Scenario: Tag and configured version match
- **WHEN** `broccolium-1.20-1.4.6` is pushed and `broccoliumVersion` is `1.4.6`
- **THEN** the workflow proceeds to publication

#### Scenario: Tag and configured library version differ
- **WHEN** `broccolium-1.20-1.4.6` is pushed and `broccoliumVersion` is not `1.4.6`
- **THEN** the workflow fails before invoking any publish task

#### Scenario: Tag names an unsupported Minecraft release
- **WHEN** a project tag targets a release other than `1.20` or `1.21`
- **THEN** the workflow fails before invoking any publish task

### Requirement: Isolated family publication
The release workflow SHALL publish the selected library's common, Forge-family, and Fabric projects for only the selected Stonecutter node.

#### Scenario: Peripheralium release
- **WHEN** a valid `peripheralium-<minecraft-release>-<library-version>` tag is processed
- **THEN** only the common, Forge-family, and Fabric publication tasks for the tagged Peripheralium Minecraft node are invoked

### Requirement: Protected Maven credentials
The release workflow MUST obtain Maven credentials from GitHub Actions secrets and MUST use only read access to repository contents.

#### Scenario: Publishing with configured credentials
- **WHEN** a valid project tag is processed and the required repository secrets are available
- **THEN** the credentials are supplied to the existing Gradle publishing configuration without being stored in the repository or printed in logs

#### Scenario: Credentials are unavailable
- **WHEN** a valid project tag is processed without the required repository secrets
- **THEN** publication fails without exposing credential values
