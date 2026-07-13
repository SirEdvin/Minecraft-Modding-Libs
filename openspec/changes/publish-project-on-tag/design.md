## Context

The repository contains three library families: Broccolium, Peripheralium, and Tweakium. Each family has core, Forge, and Fabric Gradle subprojects that already apply `site.siredvin.publishing`; their release versions are declared independently in the root `gradle.properties`. The repository does not currently contain a GitHub Actions workflow.

## Goals / Non-Goals

**Goals:**

- Publish exactly one complete library family when its version tag is pushed.
- Ensure the tag identifies the same Minecraft release and library version configured in the tagged commit.
- Keep Maven credentials in GitHub Actions secrets and grant the workflow no unnecessary GitHub permissions.
- Reuse the existing Gradle publication configuration without duplicating repository or artifact settings.
- Run Minecraft 1.20 releases on Java 17 and Minecraft 1.21 releases on Java 21.

**Non-Goals:**

- Publishing GitHub releases, changelogs, or artifacts to mod distribution sites.
- Updating versions or creating tags automatically.
- Changing Maven coordinates or Gradle publishing configuration.
- Publishing a subset of a library family's core, Forge, and Fabric artifacts.

## Decisions

### Use one workflow with project, Minecraft release, and library version tags

The workflow will listen for `broccolium-*`, `peripheralium-*`, and `tweakium-*`. It will parse `github.ref_name` as `<project>-<minecraft-release>-<library-version>`, making tags such as `broccolium-1.20-1.4.6` self-describing. The Minecraft field represents the major/minor release line because this repository targets `1.20.1` while its release tags use `1.20`.

Separate workflow files were considered, but would duplicate checkout, Java, validation, credentials, and Gradle steps.

### Validate the tag against gradle.properties before publishing

The workflow will read `minecraftVersion` and `<family>Version` from `gradle.properties`. It will fail unless the tag's Minecraft release exactly equals the first two components of `minecraftVersion` and its library version exactly equals `<family>Version`. This prevents a mistyped or stale tag from publishing artifacts for an unintended Minecraft or library version.

Using the tag as a Gradle version override was considered, but the existing project version properties remain the repository's source of truth and should be reviewed in the release commit.

### Select explicit family publication tasks

After validation, the workflow will invoke the `publish` task for only `:<family>-core`, `:<family>-forge`, and `:<family>-fabric`. Explicit task paths prevent publications from the other two families and preserve the publication behavior supplied by the existing Gradle plugins.

Running the root `publish` task was rejected because it could publish all nine subprojects.

### Select Java from the validated Minecraft release

Tag validation will output Java 17 for Minecraft 1.20 and Java 21 for Minecraft 1.21. Java setup runs after validation, allowing the same workflow to be used unchanged on both maintained branches while rejecting unsupported release lines.

### Use repository secrets through the existing publishing plugin interface

The job will expose the Maven username and password secrets using the environment or Gradle property names expected by `site.siredvin.publishing`. The implementation will confirm those names before adding the workflow. GitHub permissions will be limited to `contents: read`, and concurrent runs for the same tag will be serialized.

## Risks / Trade-offs

- [A tag points at a commit with an incorrect Minecraft or library version] -> Tag-to-property validation stops publication before credentials are used.
- [One publication succeeds before a later publication fails] -> Re-running the same tag job must rely on the Maven repository accepting identical artifact uploads; verify repository behavior before release use.
- [Publishing plugin credential names are assumed incorrectly] -> Inspect the applied plugin or run a credential-free publication check to confirm its interface during implementation.
- [GitHub-hosted runner or action upgrades change the environment] -> Pin action major versions and explicitly select Java 17 for Minecraft 1.20 or Java 21 for Minecraft 1.21.
