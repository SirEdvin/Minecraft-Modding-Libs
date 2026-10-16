---
type: testing
title: Testiarium client and ComputerCraft fixtures
description: Optional client-world automation, client-thread sequences, labeled Lua fixtures, completion markers and version-sensitive ComputerCraft scheduler integration.
tags: [testiarium, gametest, client, computercraft, fixtures]
verified:
  - by: openwiki/0.7.1
    at: 2026-10-16T16:31:45.808Z
sources:
  - id: openwiki-source-78d9e651c014808cce53de96
    resource: repo://projects/testiarium/fabric/build.gradle.kts
  - id: openwiki-source-c90c70cd177bf12be0ce4597
    resource: repo://projects/testiarium/fabric/versions/1.21.1/src/cctTestMod/resources/fabric.mod.json
  - id: openwiki-source-63f684453deb4104f6ffc9fc
    resource: repo://projects/testiarium/forge/build.gradle.kts
  - id: openwiki-source-34b4e3cb76e8cae775da11b2
    resource: repo://projects/testiarium/src/cctTestMod/kotlin/site/siredvin/testiarium/cct/CctComputers.kt
  - id: openwiki-source-19d1f3f8dd3972e2f153394d
    resource: repo://projects/testiarium/src/cctTestMod/kotlin/site/siredvin/testiarium/cct/CctFixtureCommands.kt
  - id: openwiki-source-9e87a609d2c3b2b0a152f8a6
    resource: repo://projects/testiarium/src/cctTestMod/kotlin/site/siredvin/testiarium/cct/CctGameTestExtensions.kt
  - id: openwiki-source-0eb02588b82775d215e085dd
    resource: repo://projects/testiarium/src/cctTestMod/resources/computer/startup.lua
  - id: openwiki-source-e7848ec809fb31dd0ccd80d4
    resource: repo://projects/testiarium/src/testMod/kotlin/site/siredvin/testiarium/fixture/client/ClientTestExtensions.kt
  - id: openwiki-source-0b20698912892a5b2798f009
    resource: repo://projects/testiarium/src/testMod/kotlin/site/siredvin/testiarium/fixture/client/ClientTestHooks.kt
  - id: openwiki-source-97bcc7f7dc56fb2fb2928e51
    resource: repo://projects/testiarium/versions/1.21.1/src/cctTestMod/java/site/siredvin/testiarium/cct/mixin/GameTestServerMixin.java
  - id: openwiki-source-98b1c36bf216af716fbf7281
    resource: repo://projects/testiarium/versions/1.21.1/src/cctTestMod/kotlin/site/siredvin/testiarium/cct/CctComputers.kt
generated: { by: "hermes", at: "2026-10-16T16:31:45.808Z" }
---

# Testiarium client and ComputerCraft fixtures

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
