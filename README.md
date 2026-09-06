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
