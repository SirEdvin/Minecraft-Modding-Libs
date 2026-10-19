---
type: testing
title: Testiarium client and ComputerCraft fixtures
description: Optional client-world automation, client-thread sequences, labeled Lua fixtures, completion markers and version-sensitive ComputerCraft scheduler integration.
tags: [testiarium, gametest, client, computercraft, fixtures]
sources:
  - id: openwiki-source-d7335994858335a909e6674b
    resource: repo://docs/verification/minecraft-26-1-R1.md
  - id: openwiki-source-851986a2438c3c75fd1c9bb7
    resource: repo://docs/verification/minecraft-26-1-R7-final-integration.md
  - id: openwiki-source-d58fdb154260d30c430b8820
    resource: repo://docs/verification/minecraft-26-1-R7.md
  - id: openwiki-source-d7497543e084877c0f613811
    resource: repo://gradle/minecraft-26-1.gradle.kts
generated: { by: "hermes", at: "2026-10-19T00:45:42.798Z" }
verified:
  - by: openwiki/0.7.1
    at: 2026-10-19T00:45:42.798Z
---

# Testiarium client and ComputerCraft fixtures

## Current executed capability (2026-10-19)

[R1](../../docs/verification/minecraft-26-1-R1.md) ports target registry/environment/sequence/CCT scheduler hooks and verifies both loaders with real Lua, client harness XML and screenshots. [R6](../../docs/verification/minecraft-26-1-R6.md) retains full28 native routes; [R7](../../docs/verification/minecraft-26-1-R7.md) adds all six target optional graphical workflows and four old graphical joins. Main/testMod/cctTestMod feature separation remains mandatory. Ordinary Fabric production readiness uses runProductionServer, not Testiarium's standalone runServer.

Use target Testiarium GameTest annotation/registry adapters rather than removed vanilla registration. Headless clients require Xvfb/deadlines and real rendering readiness; title/world/reload/menu/book/equipped-upgrade/real dedicated reconnect receipts are distinct from one harness test. CCT completion/error markers and scheduler-private hooks need real execution after pin changes; avoid one-shot signals that can be discarded while Lua awaits task_complete. No remote CI, online-auth or every UI path is implied by [final acceptance](../../docs/minecraft-26-1-verification.md).

## Historical source notes — October16 baseline

The detailed327f845/e69b721 protocol notes below are retained as background; their unperformed client/CCT statements are historical and superseded within the exact current evidence above.

## Separate optional surfaces

The base [registration harness](harness.md) is main output; client helpers belong to testMod and ComputerCraft integration to cctTestMod. Fabric publishes each as a named feature and includes common fixture output in the corresponding loader JAR. The CCT source set depends on ComputerCraft; normal main output does not acquire that entire test integration automatically.

Fabric runs separate runServer, runCctGameTest and runClientGameTest tasks with distinct report files/directories and common versus client tags. Forge/NeoForge uses runGameTestServer, with -Ptestiarium.cct selecting the optional CCT loaded mod and cct report. Its client run loads base Testiarium plus testMod, not cctTestMod.

## Client world lifecycle

ClientTestHooks is enabled by the presence of testiarium.client. It intercepts the title/accessibility screen once, loads or creates the named testiarium-client flat world, configures low-noise client options, and disables world spawning/daylight/weather changes on creation. Existing named worlds are reused, so this is not a fresh-world guarantee.

The shared 1.20.1 start path waits for an overworld player, sets flying/position/inventory state, waits for stable rendering and a startup delay, then runs registered test batches. It reports progress and finalizes the reporter on completion. Zero tests exit 1, required failures exit 2, otherwise exit 0; shutdown is scheduled onto the client. Client helpers submit actions to the client thread, wait for completion and unwrap ExecutionException causes. Screenshots wait for render idle and a callback before restoring GUI visibility; they are observations, not automatic pixel assertions.

Client hooks and mixins have 1.21.1 full-file overrides; engine-private world/render integration is version-sensitive. Use the owning version path rather than copying old mixin descriptors.

## Labeled ComputerCraft protocol

CctComputers.initialize replaces ServerContext.luaMachine and registers a test Lua API before ComputerCraft creates its server context. Actions are queued per label; enqueue wakes an already-known matching computer. A monitor stores success or the thrown Throwable and check rethrows failures. Reset clears action and marker maps.

The test API records state at computer startup, executes a queued action, and exposes test.ok, test.fail and test.log. A default DONE marker signals completion; duplicate ok for the same marker raises LuaException. fail records an error and DONE, so waiting completes but subsequent checking fails instead of hanging indefinitely.

startup.lua requires a computer label, loads tests/<label>.lua, runs it under pcall and calls test.ok on success. thenLua validates a fixture file when testiarium.cct-fixtures is configured and waits for the label's completion/error state; without that property require performs no file validation. thenOnComputer waits for the Java-action monitor; thenStartComputer only enqueues, so it does not by itself establish successful completion.

CctFixtureCommands imports configured fixture files into world/computercraft/computer/1 and exports them back. Copy replaces existing files and does not delete extra target files. These commands mutate fixture/world files; do not use export as a harmless diagnostic. Labels and disk placement must agree with the fixture computer.

## 1.21.1 scheduling boundary

The CCT version override reflects into ServerContext and ComputerThread to detect a zero queue and all workers idle. Its GameTestServerMixin replaces waitUntilNextTick with task pumping and brief parks until tests have not started or computers are idle. Fabric's 1.21.1 CCT metadata explicitly loads that mixin configuration. This avoids racing tick advancement against asynchronous computers but couples the harness to private ComputerCraft fields/methods; dependency upgrades need real runtime verification.

## Running and interpreting evidence

Headless client tasks require a virtual display and a deadline, for example:

    timeout --foreground 180s xvfb-run --auto-servernum ./gradlew :testiarium:fabric:1.20.1:runClientGameTest --no-daemon
    timeout --foreground 180s xvfb-run --auto-servernum ./gradlew :testiarium:forge:1.20.1:runClientGameTest --no-daemon

Use the exact version node desired; keep logs/screenshots as scratch artifacts. Require a fresh nonempty report, not just process success. See [verification boundaries](../testing/verification.md): no client/CCT runtime or screenshot checks were executed by this documentation run, and the checked-in generic server CI matrix is not a complete CCT/client matrix.
