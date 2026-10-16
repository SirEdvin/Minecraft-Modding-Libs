---
type: workflow
title: Verification workflows and coverage boundaries
description: Focused JVM/compiler contracts, the ten-target native server matrix, publication checks, fresh-report guards and measured coverage boundaries.
tags: [testing, verification, junit, gametest, publication]
sources:
  - id: openwiki-source-164e2da859b5277df81c7d94
    resource: repo://.github/workflows/ci.yml
  - id: openwiki-source-cc02d3f8883805a8e9de0ccf
    resource: repo://docs/module-test-coverage.md
  - id: openwiki-source-cbb7c5cdfcf9bd6b6f9e6326
    resource: repo://docs/stonecutter-migration-audit.md
  - id: openwiki-source-8e467af86822c4e36a35b699
    resource: repo://gradle/gametest-reports.gradle.kts
  - id: openwiki-source-f0a8151a4d37d83aff26b3ca
    resource: repo://projects/testiarium/src/main/kotlin/site/siredvin/testiarium/report/TestReporters.kt
  - id: openwiki-source-98f986c548dcbe7600b9d49c
    resource: repo://projects/tweakium/forge/versions/1.21.1/src/testMod/java/site/siredvin/tweakium/testmod/EntityStorageGameTests.java
  - id: openwiki-source-6ffcba0b5c5a2ebeb898ad3c
    resource: repo://projects/tweakium/src/testMod/kotlin/site/siredvin/tweakium/testmod/CreativeFillerGameTests.kt
  - id: openwiki-source-9bd2e62953be55b4341193eb
    resource: repo://projects/tweakium/versions/1.21.1/src/test/kotlin/site/siredvin/tweakium/DataStorageTests.kt
  - id: openwiki-source-0484d02f58a681318a0a683e
    resource: repo://projects/tweakium/versions/1.21.1/src/test/kotlin/site/siredvin/tweakium/UpgradeDataStorageTests.kt
  - id: openwiki-source-18505126b4fc17c6fe763eea
    resource: repo://projects/typed-peripheral-api/build.gradle.kts
  - id: openwiki-source-c2f29cd02016f121c3def0cf
    resource: repo://projects/typed-peripheral-api/package.json
generated: { by: "hermes", at: "2026-10-16T19:56:35.545Z" }
verified:
  - by: openwiki/0.7.1
    at: 2026-10-16T19:56:35.545Z
---

# Verification workflows and coverage boundaries

## Select the owning check

Use the wrapper with --no-daemon and exact Stonecutter task paths, not the stale pre-migration core project names:

| Change | Small meaningful check |
| --- | --- |
| Broccolium common storage/lookup | ./gradlew :broccolium:1.20.1:test --no-daemon |
| Fabric item/fluid adapters | ./gradlew :broccolium:fabric:1.20.1:test --no-daemon |
| Reverse Fabric transactional item bridge | ./gradlew :broccolium:fabric:1.20.1:runStorageGameTest --no-daemon |
| Forge wrappers | ./gradlew :broccolium:forge:1.20.1:test --no-daemon |
| Component/upgrade adapters | ./gradlew :tweakium:1.21.1:test --no-daemon |
| RML filters | ./gradlew :tweakium:1.20.1:test --no-daemon |
| Typed public contracts | ./gradlew :typed-peripheral-api:check --no-daemon |
| JVM changes spanning libraries/loaders | ./gradlew test --no-daemon, then runtime checks appropriate to the change |

Use both Minecraft versions for shared changes and loader-native runs for transformed behavior. Build setup may download dependencies and transform/generated-source outputs. Inspect [source ownership and publication wiring](../build/stonecutter-publication.md) before switching nodes or using a root build for wiki work. Executed coverage-task evidence is summarized below; documentation validation itself does not rerun application tests.

## Actual source coverage

Broccolium common fixtures cover extraction/insertion simulation, real changes, transfer directions, predicates, slot behavior and nonaliasing. Loader suites reuse fixtures with native wrappers. They are not a complete in-world capability/access-policy test.

Tweakium 1.21.1 DataStorageTests checks successful patch write/clear persistence notifications and an invalid encoding preserving the previous stored patch without notification. UpgradeDataStorageTests parameterizes pocket/turtle adapters through narrow CC API proxies, preserving explicit component removals plus present components across repeated writes, removal and mutate. Those are real adapters, but not live computers or world save/reload.

The NeoForge EntityStorageGameTests invokes native chest-minecart item capability lookup and checks three diamonds. CreativeFillerGameTests waits on a labeled Lua fixture rather than succeeding merely because its block registered. Testiarium standalone fixtures exercise passing/fail-fast paths, optional failures and a client screenshot path. Optional failures are skips; they must not be counted as passes.

The [module coverage matrix](../../docs/module-test-coverage.md) inventories logical source families and maps each major area to assertions and residual limits. New unit/native checks cover XP boundaries, lookup dispatch, drop cleanup, plugin destination/conservation, real fake-player breaks, dynamic CC binding, operation/fuel order, world scans and settings reload. Modern upgrade tests cross actual item serialization while preserving removed versus unspecified components. Reverse Fabric storage tests exercise exact replacement and rollback, not merely an empty-slot smoke test.

Peripheralium owns native registration/recipe/language/loot tests, executed through the four Tweakium runners without reverse production dependencies. typed-peripheral-api now runs exact no-emission compiler contracts through npm test and Gradle check. It verifies inferred/provider/overload types and invalid arguments, not emitted-Lua runtime conformance. Mutation replay detected 66 of 67 changes and independently validated 12 expected negative diagnostics; nullable-return weakening remains undetected with the inherited non-strict-null configuration.

## CI server matrix and reports

.github/workflows/ci.yml builds/generates publication metadata, runs the publication checker, then invokes:

    timeout --foreground 600s ./gradlew \
      :broccolium:fabric:1.20.1:runStorageGameTest \
      :broccolium:fabric:1.21.1:runStorageGameTest \
      :testiarium:forge:1.20.1:runGameTestServer \
      :testiarium:forge:1.21.1:runGameTestServer \
      :testiarium:fabric:1.20.1:runServer \
      :testiarium:fabric:1.21.1:runServer \
      :tweakium:forge:1.20.1:runGameTestServer \
      :tweakium:forge:1.21.1:runGameTestServer \
      :tweakium:fabric:1.20.1:runPeripheralGameTest \
      :tweakium:fabric:1.21.1:runPeripheralGameTest \
      --no-daemon

The workflow also builds with 1.21.1 active and switches back to 1.20.1, requiring no tracked source diff. These are checked-in workflow intentions, not evidence this branch's CI has run.

Root GameTest guards remove each mapped stale XML before launch, then require a fresh report with at least one non-skipped testcase, no failure/error elements and an accurate declared tests count. Storage reports allow no skips; standalone Testiarium negative fixtures must each appear once with the exact expected skipped sentinel. Startup can exit zero without tests; inspect reports, not only the process status. Reports live under effective version leaf build/test-results directories, with storage-gametest.xml, gametest.xml, peripheral-gametest.xml and optional client/CCT names as configured. Testiarium also emits sibling HTML for humans.

Client tasks require xvfb-run and explicit deadlines in headless operation. Optional CCT tests require the correct feature, loaded-mod set and fixture files. The generic server matrix above is not exhaustive client/CCT coverage; see [client/CCT lifecycle](../testiarium/client-cct.md) and [harness discovery](../testiarium/harness.md).

## Evidence provenance and remaining work

The October 16, 2026 coverage acceptance passed both active-state clean root builds: 1,116 JUnit invocations in 62 current versioned reports per run, no failures/errors/skips, and 24 verified publications. The final ten-target native run reported 189 cases with zero failures/errors and 12 intentional negative-fixture skips. Broccolium Fabric has 24 required cases per version; Testiarium has 12 cases/three skips on each of four targets; Tweakium has 22/22 legacy Fabric/Forge and 24/25 modern Fabric/NeoForge cases. No main-JAR test leakage was found against 136 concrete test-class identities. Exact commands, per-node JUnit totals and saved evidence paths are in the matrix.

The migration audit's earlier 1,598 JUnit total was corrected: 534 cases came from obsolete unversioned directories, leaving a 1,064-case versioned baseline. Do not recursively count stale build trees or add repeated active-state runs as distinct coverage. Legitimate parameterized JUnit display names can repeat across methods; native testcase names were checked for uniqueness separately. Both active-state builds used an exact-source standalone clone for Spotless/JGit compatibility, then restored canonical 1.20.1.

Remaining risk areas include true disk/chunk reload, installed upgrade transitions, provider cleanup/attributes/combat, client/render/network behavior, optional CCT scheduling and typed API/runtime schema parity. Strict-null checking exposes five inherited dependency-source errors; npm audit separately reports one inherited high-severity picomatch advisory. Neither was hidden by test suppression. Wiki structure, Claim evidence and retrieval are checked independently; no remote CI run, publication, release or Minecraft 26.1 support is implied.
