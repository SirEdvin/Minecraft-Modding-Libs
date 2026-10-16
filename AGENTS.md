# AGENTS.md

## Project overview

Kotlin Minecraft libraries maintained together with Stonecutter: 1.20.1 on Forge/Fabric and 1.21.1 on NeoForge/Fabric. Preserve both version/loader pairs unless explicitly authorized otherwise.

- `broccolium`: platform and storage abstractions.
- `tweakium`: ComputerCraft pocket and gameplay utilities; depends on Broccolium.
- `peripheralium`: ComputerCraft peripherals; depends on Tweakium and Broccolium.
- `testiarium`: shared and loader-specific GameTest harnesses.
- `typed-peripheral-api`: TypeScript peripheral declarations and contract checks; not a Minecraft version node.

Keep loader-neutral code and APIs in the common library source tree; loader APIs, entrypoints, Mixins, access transformers and metadata belong in the matching adapter. The `forge` branch means Forge for 1.20.1 and NeoForge for 1.21.1.

## Layout

- `settings.gradle.kts`: authoritative subproject/version/loader graph; canonical VCS version is 1.20.1.
- `projects/<library>/src`: shared common sources; `fabric/src` and `forge/src` beneath that library hold loader sources.
- `projects/<library>/stonecutter.gradle.kts`: per-library controller; there is no root controller file.
- `projects/<library>/{build.gradle.kts,fabric/build.gradle.kts,forge/build.gradle.kts}`: shared build scripts for the version leaves.
- `versions/<version>/src` beneath each common/loader root: authored full-file overrides; do not edit processed `build/stonecutter` outputs.
- Loader metadata: Fabric `src/main/resources/fabric.mod.json`; Forge-family version overrides contain `META-INF/mods.toml` or `META-INF/neoforge.mods.toml`.
- `projects/typed-peripheral-api`: TypeScript sources, package manifest and Gradle bridge.
- `gradle/libs.versions.toml`: dependency and plugin versions.
- `gradle.properties`: library versions/default properties; `build.gradle.kts` and loader scripts select Java 17/21 per Minecraft node.
- [Migration audit](docs/stonecutter-migration-audit.md): build/publication contracts. If present in the assigned checkout, start repository wiki navigation at `openwiki/index.md` and coverage evidence at `docs/module-test-coverage.md`.

Shared modules use access wideners. Forge modules use access transformers and may use mixins; Fabric modules may use mixins. Update the relevant loader configuration and metadata when changing either integration boundary.

## Build and test

For implementation changes, use the checked-in wrapper and the smallest affected versioned task. These examples redirect verbose output to scratch logs; inspect exit status and bounded diagnostics:

```sh
LOG_DIR="${TMPDIR:?}/mml-validation"; mkdir -p "$LOG_DIR"
./gradlew :broccolium:1.20.1:test --no-daemon > "$LOG_DIR/common-test.log" 2>&1
./gradlew :broccolium:forge:1.20.1:test --no-daemon > "$LOG_DIR/forge-test.log" 2>&1
./gradlew :broccolium:fabric:1.21.1:test --no-daemon > "$LOG_DIR/fabric-test.log" 2>&1
./gradlew test --no-daemon > "$LOG_DIR/test.log" 2>&1
./gradlew build --no-daemon > "$LOG_DIR/build.log" 2>&1
```

Tests use JUnit 5 under `src/test`; Broccolium/Tweakium provide `src/testFixtures`, and native fixtures use `src/testMod`. Cross-module/loader implementation needs the relevant full matrix, not only the active node. Discover run names from the actual build scripts and `.github/workflows/ci.yml` before invoking them.

Count only fresh, executed versioned XML reports; old unversioned reports and an interrupted process are not acceptance evidence. Keep expected negative-fixture skips explicit. Preserve canonical source state after version switching and keep test-only outputs out of publications.

Minecraft client GameTests require a virtual display and an explicit timeout in headless environments:

```sh
timeout --foreground 180s xvfb-run --auto-servernum ./gradlew :testiarium:forge:1.20.1:runClientGameTest --no-daemon > "$LOG_DIR/forge-client.log" 2>&1
timeout --foreground 180s xvfb-run --auto-servernum ./gradlew :testiarium:fabric:1.20.1:runClientGameTest --no-daemon > "$LOG_DIR/fabric-client.log" 2>&1
```

## Code conventions

- Write Kotlin using the official Kotlin style configured in `gradle.properties`: four-space indentation and trailing commas in multiline declarations.
- Preserve the existing `site.siredvin.<library>` package structure and use Kotlin `object` singletons for mod entrypoints and shared registries where established.
- Use the existing platform abstraction instead of importing loader classes into common code.
- Keep Fabric client-only initialization isolated from common/server code.
- Do not edit `src/generated` resources by hand; change the applicable data generator instead.
- Keep version changes centralized in `gradle.properties` or `gradle/libs.versions.toml`.

## Task scope and resumable worker execution

- Start from the assigned card, parent handoff, latest checkpoint and live Git status. Verify parent commit ancestry; a dependency edge does not transfer Git changes. Preserve dirty work and stay in the assigned worktree.
- Search for paths/symbols before reading them; after a missing path, discover the real location rather than guessing another layout. Wiki pages are navigation, not a substitute for source evidence.
- Planning-only cards produce the requested brief, matrix and executable checklist, not implementation. Define future build/runtime gates; do not rerun an accepted baseline merely because a generic skill says to build after documentation edits. Run a narrow probe only to answer a named unresolved planning question.
- Save an explicitly incomplete draft after the first bounded research pass. After each coherent phase, update it and add a task comment with evidence paths, decisions, unresolved questions and the exact next action; heartbeats alone are not recovery checkpoints. Resume these artifacts instead of restarting research.
- Research to resolve specific decisions. Retain source URLs/revisions and cache provenance, then synthesize; refresh missing, changed or time-sensitive evidence, not every previously read document. Unverified compatibility remains a named risk, never an invented pass.
- Use available, authorized tools. An unavailable fetch backend differs from a denied operation: use an allowed alternative for the former, but never replay an explicit denial through another tool or change approval/runtime settings to evade it.
- Match verification to the card's phase: documentation links, source citations and whitespace for plans; real builds/tests/runtime acceptance for implementation. Preserve all explicit acceptance and report requirements, including local-only publication boundaries.
- While still able to act, finish with the lifecycle tool matching reality: complete only verified card-local work, request review for a verified review handoff, or block for an actionable external requirement. Save a checkpoint before yielding with unfinished work; never label an interruption or budget exhaustion as test success or invent a blocker to end a run.
