## 1. Publishing Workflow

- [x] 1.1 Confirm the existing `site.siredvin.publishing` plugin's credential names and publication task paths.
- [x] 1.2 Add one GitHub Actions workflow for the three project tag prefixes with Java setup, least-privilege permissions, and Maven secrets.
- [x] 1.3 Parse `<project>-<minecraft-release>-<library-version>` and validate the Minecraft release and library version against `minecraftVersion` and the selected `<project>Version` before publishing.
- [x] 1.4 Invoke only the selected project's core, Forge, and Fabric publish tasks.

## 2. Verification

- [x] 2.1 Validate the workflow syntax and exercise tag parsing with matching, Minecraft-mismatching, library-mismatching, and unsupported tag examples.
- [x] 2.2 Use a Gradle dry run to verify each supported project selects exactly its three publication task paths without contacting the Maven repository.
- [x] 2.3 Select Java 17 for Minecraft 1.20 and Java 21 for Minecraft 1.21, and verify the workflow against both branches' version properties.
