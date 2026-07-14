## Why

Publishing the libraries currently requires a manual local release even though every subproject already has Maven publishing configured. A tag-driven workflow will make releases repeatable and limit each release to the named library family.

## What Changes

- Add a GitHub Actions workflow triggered by `<project>-<minecraft-release>-<library-version>` tags such as `tweakium-1.20-1.4.6`.
- Validate that the tag's Minecraft release and library version match `minecraftVersion` and the selected library version in `gradle.properties`.
- Build and publish only the core, Forge, and Fabric subprojects belonging to the tagged library.
- Use repository credentials stored as GitHub Actions secrets.
- Select the Java version required by the tagged Minecraft 1.20 or 1.21 release line.

## Capabilities

### New Capabilities

- `tagged-maven-publishing`: Publish one library family to the configured Maven repository from a tag identifying the project, Minecraft release, and library version.

### Modified Capabilities

None.

## Impact

- Adds a workflow under `.github/workflows/`.
- Uses the existing Gradle wrapper, publishing plugins, project versions, and Maven repository configuration.
- Requires Maven publishing credentials to be configured as repository secrets.
- Does not change published coordinates, library code, or runtime dependencies.
