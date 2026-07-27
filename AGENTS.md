# AGENTS.md

## Project overview

Minecraft 1.20.1 libraries written primarily in Kotlin. The build publishes three libraries for both Forge and Fabric:

- `broccolium`: platform and storage abstractions.
- `tweakium`: ComputerCraft pocket and gameplay utilities; depends on Broccolium.
- `peripheralium`: ComputerCraft peripherals; depends on Tweakium and Broccolium.

Each library has a shared `*-core` module plus `*-forge` and `*-fabric` loader adapters. Keep loader-neutral code and APIs in `*-core`; put Forge or Fabric API calls, entrypoints, mixins, access transformers, and loader metadata in the matching adapter module.

## Layout

- `projects/<library>-core`: common code and shared assets.
- `projects/<library>-forge`: Forge implementation; metadata is in `src/main/resources/META-INF/mods.toml`.
- `projects/<library>-fabric`: Fabric implementation; metadata is in `src/main/resources/fabric.mod.json`.
- `gradle/libs.versions.toml`: dependency and plugin versions.
- `gradle.properties`: Minecraft and library versions.

Shared modules use access wideners. Forge modules use access transformers and may use mixins; Fabric modules may use mixins. Update the relevant loader configuration and metadata when changing either integration boundary.

## Build and test

Use the checked-in Gradle wrapper:

```sh
./gradlew test
./gradlew build
./gradlew :broccolium:1.20.1:test
./gradlew :broccolium:forge:1.20.1:test
./gradlew :broccolium:fabric:1.20.1:test
```

Run the smallest affected module test task while iterating, then `./gradlew test` for changes spanning modules or loaders. Tests use JUnit 5 and live under `src/test`; common test fixtures are provided by the Broccolium and Tweakium core modules.

Minecraft client GameTests require a virtual display and an explicit timeout in headless environments:

```sh
timeout --foreground 180s xvfb-run --auto-servernum ./gradlew :testiarium:forge:1.20.1:runClientGameTest --no-daemon
timeout --foreground 180s xvfb-run --auto-servernum ./gradlew :testiarium:fabric:1.20.1:runClientGameTest --no-daemon
```

## Code conventions

- Write Kotlin using the official Kotlin style configured in `gradle.properties`: four-space indentation and trailing commas in multiline declarations.
- Preserve the existing `site.siredvin.<library>` package structure and use Kotlin `object` singletons for mod entrypoints and shared registries where established.
- Use the existing platform abstraction instead of importing Forge or Fabric classes into core code.
- Keep Fabric client-only initialization isolated from common/server code.
- Do not edit `src/generated` resources by hand; change the applicable data generator instead.
- Keep version changes centralized in `gradle.properties` or `gradle/libs.versions.toml`.
