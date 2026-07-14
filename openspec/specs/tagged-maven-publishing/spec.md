# tagged-maven-publishing Specification

## Purpose
TBD - created by archiving change publish-project-on-tag. Update Purpose after archive.
## Requirements
### Requirement: Project-specific release tags
The release workflow SHALL run for tags matching `broccolium-<minecraft-release>-<library-version>`, `peripheralium-<minecraft-release>-<library-version>`, or `tweakium-<minecraft-release>-<library-version>` and SHALL derive all three values from the tag. The Minecraft release SHALL be the major/minor release line, such as `1.20` for configured Minecraft version `1.20.1`.

#### Scenario: Supported project tag is pushed
- **WHEN** a `tweakium-1.20-1.4.6` tag is pushed
- **THEN** the workflow selects Tweakium for Minecraft release `1.20` and library version `1.4.6`

#### Scenario: Unrelated tag is pushed
- **WHEN** a tag outside the three supported project prefixes is pushed
- **THEN** the Maven publishing workflow does not run

### Requirement: Minecraft-compatible Java
The release workflow SHALL use Java 17 for Minecraft release line `1.20` and Java 21 for Minecraft release line `1.21`.

#### Scenario: Minecraft 1.20 release
- **WHEN** a valid tag targets Minecraft release line `1.20`
- **THEN** the workflow configures Java 17 before invoking Gradle

#### Scenario: Minecraft 1.21 release
- **WHEN** a valid tag targets Minecraft release line `1.21`
- **THEN** the workflow configures Java 21 before invoking Gradle

### Requirement: Release version validation
The release workflow MUST verify before invoking any publish task that the tag's Minecraft release exactly matches the first two components of `minecraftVersion` and that its library version exactly matches the selected library's version in `gradle.properties`.

#### Scenario: Tag and configured version match
- **WHEN** `broccolium-1.20-1.4.6` is pushed, `minecraftVersion` is `1.20.1`, and `broccoliumVersion` is `1.4.6`
- **THEN** the workflow proceeds to publication

#### Scenario: Tag and configured library version differ
- **WHEN** `broccolium-1.20-1.4.6` is pushed and `broccoliumVersion` is not `1.4.6`
- **THEN** the workflow fails before invoking any publish task

#### Scenario: Tag and configured Minecraft release differ
- **WHEN** `broccolium-1.20-1.4.6` is pushed and `minecraftVersion` does not belong to release line `1.20`
- **THEN** the workflow fails before invoking any publish task

### Requirement: Isolated family publication
The release workflow SHALL publish the selected library family's core, Forge, and Fabric subprojects and SHALL NOT invoke publication tasks for either unselected family.

#### Scenario: Peripheralium release
- **WHEN** a valid `peripheralium-<minecraft-release>-<library-version>` tag is processed
- **THEN** only `peripheralium-core`, `peripheralium-forge`, and `peripheralium-fabric` publication tasks are invoked

### Requirement: Protected Maven credentials
The release workflow MUST obtain Maven credentials from GitHub Actions secrets and MUST use only read access to repository contents.

#### Scenario: Publishing with configured credentials
- **WHEN** a valid project tag is processed and the required repository secrets are available
- **THEN** the credentials are supplied to the existing Gradle publishing configuration without being stored in the repository or printed in logs

#### Scenario: Credentials are unavailable
- **WHEN** a valid project tag is processed without the required repository secrets
- **THEN** publication fails without exposing credential values

