---
type: subproject
title: Testiarium registration and reporting harness
description: Cross-loader GameTest discovery, API/fixture feature separation, tag precedence, reflective invocation, report generation and fresh-report validation.
tags: [testiarium, gametest, reports, testing, lifecycle]
verified:
  - by: openwiki/0.7.1
    at: 2026-10-16T16:31:45.808Z
sources:
  - id: openwiki-source-8e467af86822c4e36a35b699
    resource: repo://gradle/gametest-reports.gradle.kts
  - id: openwiki-source-4ddbd0907e10e62bd7b67a8b
    resource: repo://projects/testiarium/build.gradle.kts
  - id: openwiki-source-9970ed5ede1bafad13dad351
    resource: repo://projects/testiarium/fabric/src/main/kotlin/site/siredvin/testiarium/FabricTestiarium.kt
  - id: openwiki-source-6e53d6dcf2154ff9280575b9
    resource: repo://projects/testiarium/forge/src/main/kotlin/site/siredvin/testiarium/ForgeTestiarium.kt
  - id: openwiki-source-81f14455e23f9569fede8484
    resource: repo://projects/testiarium/forge/versions/1.21.1/src/main/kotlin/site/siredvin/testiarium/ForgeTestiarium.kt
  - id: openwiki-source-70bc640127f0fb448b708e0a
    resource: repo://projects/testiarium/src/main/kotlin/site/siredvin/testiarium/api/Assertions.kt
  - id: openwiki-source-df90949cf5ecd5abd8750b11
    resource: repo://projects/testiarium/src/main/kotlin/site/siredvin/testiarium/api/GameTests.kt
  - id: openwiki-source-f0a8151a4d37d83aff26b3ca
    resource: repo://projects/testiarium/src/main/kotlin/site/siredvin/testiarium/report/TestReporters.kt
  - id: openwiki-source-5c995101f4d15764e6e6bad1
    resource: repo://projects/testiarium/src/main/kotlin/site/siredvin/testiarium/Testiarium.kt
  - id: openwiki-source-58839584b723383a5a3f8fea
    resource: repo://projects/testiarium/src/testMod/kotlin/site/siredvin/testiarium/fixture/StandaloneGameTests.kt
  - id: openwiki-source-ad69366c415d4ac2663d3100
    resource: repo://projects/testiarium/versions/1.21.1/src/main/kotlin/site/siredvin/testiarium/Testiarium.kt
generated: { by: "hermes", at: "2026-10-16T16:31:45.808Z" }
---

# Testiarium registration and reporting harness

## Responsibility and feature boundaries

Testiarium main owns registration, lightweight test annotations/assertions and reporters. testMod owns executable standalone fixtures, commands and client helpers. cctTestMod adds optional ComputerCraft fixtures and depends on testMod output. Common feature capabilities are testiarium-core-test-mod and testiarium-core-cct-test-mod; loaders publish corresponding loader-named features. A consumer needs the matching fixture feature and loaded-mod wiring as well as a compilation dependency.

## Registration flow

Testiarium.register adds classes to a linked set. loadTests traverses their declared methods; it does not scan the classpath. Group selection is method TestGroup, then class TestGroup, then client for ClientGameTest or common otherwise. TestTags reads comma-separated testiarium.tags once, defaulting to common; changing it after object initialization is not a dynamic filter.

For an enabled method, GameTest is handled first and converted to TestFunction with name <simpleClass>.<method>, template defaulting to that name and native settings forwarded. ClientGameTest has a shorter required-test construction path. Other methods are delegated to the supplied loader fallback. Invocation accepts static methods or constructs a new public no-argument instance per call. InvocationTargetException causes are unwrapped/wrapped into runtime failures; bad reflective construction fails rather than silently skipping a class.

Fabric's adapter initializes lifecycle hooks and has a guarded registerTests call into GameTestRegistry. Legacy Forge's guarded call installs a RegisterGameTestsEvent listener. NeoForge 1.21.1 instead uses a mod class with injected IEventBus and installs its register listener in construction. Consumers must register classes in time for the loader event; “the jar compiles” does not prove tests were discovered.

## World and assertion lifecycle

init optionally installs a structure directory and replaces the global reporter with JUnit plus native log reporters. onServerStarted fixes time/daylight and clears old test space. 1.20.1 uses the fixed origin (0,-60,0); 1.21.1 derives x/z from shared spawn, uses y=-59 and clears located structure bounds. onServerStopped finalizes reporting.

sequence appends thenSucceed; immediate runs an action and succeeds. thenExecuteFailFast preserves GameTestAssertException but converts other Throwables into one using the error message/type, so a sequence failure becomes visible to GameTest rather than escaping as an unrelated task error. Assertion helpers retrieve typed entities/containers/block entities and inspect world state; they do not bootstrap a missing fixture or dependency mod.

## XML/HTML reports and false success prevention

JunitTestReporter records required failures as failure elements and optional failures as skipped. finish is idempotent, emits counted XML plus a sibling HTML report, and prints locations. Optional failures therefore are not passes. StandaloneGameTests even has a method named requiredFailure marked required=false: inspect the annotation, not the test name.

Root gradle/gametest-reports.gradle.kts guards specific loader runs. Before launch it deletes the exact prior XML or fails; afterwards it requires a report, disables DOCTYPE parsing, requires more cases than skips and no failure/error elements, and compares the declared tests count to actual testcase elements. This does not compare every possible declared XML attribute. The guard catches startup paths that exit zero without running tests, but it only applies to the task/report map it configures.

See [client and CCT protocols](client-cct.md) for asynchronous completion and [verification workflows](../testing/verification.md) for exact task routes. This page describes inspected coverage mechanisms; no application or GameTest run was executed during wiki creation.
