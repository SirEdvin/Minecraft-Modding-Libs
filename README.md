# SirEdvin's Minecraft modding libs

Minecraft libraries built for 1.20.1 (Java 17, Fabric/Forge) and 1.21.1
(Java 21, Fabric/NeoForge) with Stonecutter. The historical `forge` project
paths and Maven artifact names also cover NeoForge on 1.21.1.

## Source ownership

- Commit with 1.20.1 active; it is the canonical VCS state.
- Shared sources live in `projects/<library>/src` and its loader branches.
- `versions/<version>/src` contains intentional version-specific overrides.
  Those files take precedence, including when that version is active.
- Do not edit `versions/<version>/build/generated/stonecutter` or other build outputs.
- The libraries currently use separate trees. Run switching commands from the
  repository root so all four trees switch together.

## Verification

```sh
./gradlew cleanTest build generatePomFileForMavenPublication generateMetadataFileForMavenPublication --no-daemon
python3 scripts/check-publications.py
./gradlew 'Set active project to 1.21.1' --no-daemon
./gradlew build generatePomFileForMavenPublication generateMetadataFileForMavenPublication --no-daemon
python3 scripts/check-publications.py
./gradlew 'Set active project to 1.20.1' --no-daemon
```

The publication check inspects all 24 main publications, including dependencies,
feature capabilities, source JARs, loader metadata and Java bytecode targets.
CI also runs server GameTests on all four version/loader combinations and checks
that switching back leaves tracked files unchanged. Formatting is checked once
from the repository root, ratcheted against `origin/multiversion`.

Use scratch log files for verbose local Gradle output (`> "$TMPDIR/build.log" 2>&1`).
The server matrix includes Testiarium and Tweakium's peripheral tests. The report
guard removes old XML before launch and requires a fresh, nonempty report with no
failures/errors; Minecraft startup can fail while the process still exits zero.

Gradle must be started on Java 25: CC:Tweaked 1.121.0's Fabric artifacts need Loom
1.18, which needs Gradle 9.7+ and a Java 25 launcher. Minecraft still compiles on
the Java 17/21 toolchains. Stonecutter is pinned to stable 0.9.8; 0.10 is not
adopted because it needs a separate preprocessing/API migration. See
[the migration audit](docs/stonecutter-migration-audit.md) for gameplay/API
differences, inherited bugs, fixes, and verification scope.
