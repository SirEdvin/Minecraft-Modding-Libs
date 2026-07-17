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
./gradlew :broccolium-core:test
./gradlew :broccolium-forge:test
./gradlew :broccolium-fabric:test
```

Run the smallest affected module test task while iterating, then `./gradlew test` for changes spanning modules or loaders. Tests use JUnit 5 and live under `src/test`; common test fixtures are provided by the Broccolium and Tweakium core modules.

Minecraft client GameTests require a virtual display and an explicit timeout in headless environments:

```sh
timeout --foreground 180s xvfb-run --auto-servernum ./gradlew :testiarium-forge:runClientGameTest --no-daemon
timeout --foreground 180s xvfb-run --auto-servernum ./gradlew :testiarium-fabric:runClientGameTest --no-daemon
```

## Code conventions

- Write Kotlin using the official Kotlin style configured in `gradle.properties`: four-space indentation and trailing commas in multiline declarations.
- Preserve the existing `site.siredvin.<library>` package structure and use Kotlin `object` singletons for mod entrypoints and shared registries where established.
- Use the existing platform abstraction instead of importing Forge or Fabric classes into core code.
- Keep Fabric client-only initialization isolated from common/server code.
- Do not edit `src/generated` resources by hand; change the applicable data generator instead.
- Keep version changes centralized in `gradle.properties` or `gradle/libs.versions.toml`.

## Java process output and token budget

When work in this repository requires Java, Minecraft, a mod loader, Gradle, or an installer process:

- Treat complete stdout/stderr as a file-backed artifact, not chat context. Redirect it to a timestamped file under a disposable `build/`, `run/`, or `work/logs/` directory and preserve the real exit code.
- Return only a compact summary to the agent: command, exit code, duration, full-log path, readiness/startup time, unique ERROR/FATAL/exception signatures, relevant test or content counts, and clean-shutdown evidence.
- Read a narrow window around a concrete failure from the saved log only when diagnosis requires it. Do not paste an entire `latest.log`, Gradle transcript, download list, or crash report into the conversation.
- Keep stdin attached for long-running servers so they can receive `stop`. Prefer a wrapper that writes every line to disk while emitting only rare readiness/failure markers, and prefer completion/readiness notifications over frequent polling.
- Apply the same rule to Forge/NeoForge/Fabric and Packwiz installers: retain full download and patch progress on disk, but report only success/failure, item counts, and the log path.
- Terminal-side truncation is not filtering: a truncated 50 KiB Java log can still consume roughly 15k model tokens while omitting the line that matters.
- Batch modpack migrations and related fixes before launching Minecraft, then perform consolidated runtime validation instead of restarting after every edit.
