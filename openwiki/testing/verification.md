---
type: workflow
title: Verification workflows and coverage boundaries
description: Focused JVM tests, the actual server GameTest matrix, publication checks, fresh-report guards and explicit limits of documentation-only verification.
tags: [testing, verification, junit, gametest, publication]
verified:
  - by: openwiki/0.7.1
    at: 2026-10-16T16:31:45.808Z
sources:
  - id: openwiki-source-164e2da859b5277df81c7d94
    resource: repo://.github/workflows/ci.yml
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
generated: { by: "hermes", at: "2026-10-16T16:31:45.808Z" }
---

# Verification workflows and coverage boundaries

## Select the owning check

Use the wrapper with --no-daemon and exact Stonecutter task paths, not the stale pre-migration core project names:

| Change | Small meaningful check |
| --- | --- |
| Broccolium common storage/lookup | ./gradlew :broccolium:1.20.1:test --no-daemon |
| Fabric item/fluid adapters | ./gradlew :broccolium:fabric:1.20.1:test --no-daemon |
| Forge wrappers | ./gradlew :broccolium:forge:1.20.1:test --no-daemon |
| Component/upgrade adapters | ./gradlew :tweakium:1.21.1:test --no-daemon |
| RML filters | ./gradlew :tweakium:1.20.1:test --no-daemon |
| JVM changes spanning libraries/loaders | ./gradlew test --no-daemon, then runtime checks appropriate to the change |

These are routes derived from project/build/test source, not commands executed during this documentation task. Build setup may download dependencies and transform/generated-source outputs. Inspect [source ownership and publication wiring](../build/stonecutter-publication.md) before switching nodes or using a root build for wiki work.

## Actual source coverage

Broccolium common fixtures cover extraction/insertion simulation, real changes, transfer directions, predicates, slot behavior and nonaliasing. Loader suites reuse fixtures with native wrappers. They are not a complete in-world capability/access-policy test.

Tweakium 1.21.1 DataStorageTests checks successful patch write/clear persistence notifications and an invalid encoding preserving the previous stored patch without notification. UpgradeDataStorageTests parameterizes pocket/turtle adapters through narrow CC API proxies, preserving explicit component removals plus present components across repeated writes, removal and mutate. Those are real adapters, but not live computers or world save/reload.

The NeoForge EntityStorageGameTests invokes native chest-minecart item capability lookup and checks three diamonds. CreativeFillerGameTests waits on a labeled Lua fixture rather than succeeding merely because its block registered. Testiarium standalone fixtures exercise passing/fail-fast paths, optional failures and a client screenshot path. Optional failures are skips; they must not be counted as passes.

No dedicated Peripheralium test-named files were found in the inspected tree. typed-peripheral-api has an intentionally failing placeholder npm test script; successful TypeScript compilation is a packaging/type check, not runtime API conformance.

## CI server matrix and reports

.github/workflows/ci.yml builds/generates publication metadata, runs the publication checker, then invokes:

    timeout --foreground 600s ./gradlew \
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

Root GameTest guards remove each mapped stale XML before launch, then require a fresh report with at least one non-skipped testcase, no failure/error elements and an accurate declared tests count. Startup can exit zero without tests; inspect reports, not only the process status. Reports live under effective version leaf build/test-results directories, with gametest.xml, peripheral-gametest.xml and optional client/CCT names as configured. Testiarium also emits sibling HTML for humans.

Client tasks require xvfb-run and explicit deadlines in headless operation. Optional CCT tests require the correct feature, loaded-mod set and fixture files. The generic server matrix above is not exhaustive client/CCT coverage; see [client/CCT lifecycle](../testiarium/client-cct.md) and [harness discovery](../testiarium/harness.md).

## Evidence provenance and remaining work

The migration audit records earlier successful JVM, publication and server-matrix runs at its stated commits, with explicit exclusions. Those historical results are not rerun results for this wiki. This documentation run intentionally did not execute application builds, JUnit, GameTests, npm build/test, datagen or publishing; it verifies wiki structure, Claims and retrieval separately.

Remaining risk areas include live save/reload and upgrade reinstall, fake-player combat/attributes and exceptional drop cleanup, client/render behavior, full CCT/loader runtime coverage, typed API/runtime schema parity and effective version-specific content. No gameplay fix or test expansion is part of the wiki generation; downstream coverage work must decide and test those boundaries.
