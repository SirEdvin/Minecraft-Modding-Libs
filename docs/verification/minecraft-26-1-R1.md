# R1 — Testiarium 26.1.2 registry and native harness

Status: COMPLETE for the R1 capability only. Real native, CCT Lua and client harness acceptance passed on both target loaders. This is not completion of the downstream gameplay, publication, server/save/network or visual-integration campaign.

## Source custody and toolchain

Task: t_728ae4df. Planner b8fd1ddfd5134c45402040fe2c1ca5be343ac48f and predecessor cumulative e07fcc12175bef26bac0f7403d901db07cab67a9 were merged in order and verified as ancestors. The original implementation/planning/coverage/OpenWiki ancestors were also verified by the evidence collector. The card's 2026-10-17 execution release supersedes immutable planning-document holds. Local commits only; no push, primary merge, release, remote publication, service restart or profile changes.

Exact retained target: Minecraft26.1.2, NeoForge26.1.2.122, Fabric Loader0.19.5/API0.155.3+26.1.2, CC:Tweaked1.120.0 (`-forge` coordinate on NeoForge), Loom1.17.11, MDG2.0.142, Stonecutter0.9.8, Gradle9.5.0, KGP2.4.0, FLK1.13.12+kotlin.2.4.0 and KFF6.4.0. Launcher verified as Temurin25.0.3+9; worker memory.max was8589934592 bytes. Old Java17/21 nodes and canonical1.20.1 controllers/settings/properties were preserved without switching. Companion projects were not changed.

Evidence root: `/home/siredvin/.hermes/profiles/albina/cache/scratch/t_728ae4df-validation`. The attached `t_728ae4df-R1-evidence.zip` is the durable receipt: exact report/test identities, skip maps, source hashes, built artifact hashes, real screenshots, failed-run XML and command logs. Final cumulative commit and archive checksum are in the Kanban completion metadata rather than a self-referential hash here.

## Implemented capability

- Target-only `site.siredvin.testiarium.api.GameTest` replaces the removed vanilla annotation, not a net.minecraft shim. Existing `Testiarium.register`, client annotations, groups/tags, reflection/constructor errors and metadata remain available. Target consumers must change the annotation import. IDs are lowercase `testiarium:<simpleclass>.<method>`; human-readable report names retain original class/method case. Identifier and normalized batch collisions fail before static-registry mutation.
- Native TEST_FUNCTION registration is one-shot. FunctionGameTestInstance and native TEST_ENVIRONMENT/TEST_INSTANCE registries are populated per world/reload. Fabric's registry-load hook excludes NetworkRegistryLoadTask, avoiding re-registration of server-synced client entries. NeoForge uses RegisterEvent and RegisterGameTestsEvent. No removed GameTestRegistry calls are retained in target code.
- Native batch-owned SetGameRules setup disables time advancement/mob spawning and native teardown restores rules; a required test also verifies nested activation/restoration. This replaces target use of the old global daylight mutation. Native test structures use `data/minecraft/structure/*.snbt`, staged from authored fixtures by ProcessResources. Isolated real dispatcher import/export checks verify the matching target directory without changing authored fixtures.
- Native fail() now records an error without immediately finishing. Target fail-fast records the original/native-wrapped error, finishes in the same tick and stops the current event iterator. Required outer observers prove assertions, ordinary exceptions and assertion errors execute once, never run later callbacks, and survive delayed competing success. Full native stack data remains in reports; stable XML messages use the original assertion Component rather than its localized tick suffix.
- CCT keeps the real selected CC Lua machine and verified `context`, `computerQueueSize`, `workerCount`, `idleWorkers`, parent/testInfo reflection. The native server task pump uses an invoker on the actual BlockableEventLoop owner. Real Lua tests cover successful filesystem/event/API execution, markers, startup queued actions, post-startup queued actions, exact error sentinels, and a required outer failure observer. ILuaAPI.update dispatches later actions; error state is published before DONE.
- Generic `testMod` and optional `cctTestMod` have separate common/loader features and source artifacts. CC is on the CCT feature rather than target Testiarium production implementation. Production mod models exclude private fixtures. Exact compiled test/testMod/cctTestMod class identities were checked against all three target main JARs: none leaked. Runtime computer assets also remain outside main JARs. Full publication metadata/source merging/consumer acceptance remains R5.
- `scripts/verification/run-gametest.sh` runs discovered loader tasks through the wrapper with `--no-daemon`, scratch logs, native900s/client300s deadlines and Xvfb for clients, preserving exit codes. It is reusable by subsequent owning modules. No tests are simulated by this runner.

## Verified task/report matrix

All rows below are actual successful executions with fresh versioned reports and reconciled testcase/declared counts. Server/client report guards run inside Gradle; process exit alone was not accepted.

| Version/loader | Exact task (prefix `:testiarium:`) | Report | Cases / passes / skips |
| --- | --- | --- | --- |
|26.1.2 Fabric|`fabric:26.1.2:runServer`|gametest.xml|14 / 11 / 3|
|26.1.2 NeoForge|`forge:26.1.2:runGameTestServer`|gametest.xml|14 / 11 / 3|
|26.1.2 Fabric CCT|`fabric:26.1.2:runCctGameTest`|cct-gametest.xml|18 / 14 / 4|
|26.1.2 NeoForge CCT|`forge:26.1.2:runGameTestServer -Ptestiarium.cct`|cct-gametest.xml|18 / 14 / 4|
|26.1.2 Fabric client|`fabric:26.1.2:runClientGameTest`|client-gametest.xml|1 / 1 / 0|
|26.1.2 NeoForge client|`forge:26.1.2:runClientGameTest`|client-gametest.xml|1 / 1 / 0|
|1.20.1 Fabric|`fabric:1.20.1:runServer`|gametest.xml|12 / 9 / 3|
|1.20.1 Forge|`forge:1.20.1:runGameTestServer`|gametest.xml|12 / 9 / 3|
|1.21.1 Fabric|`fabric:1.21.1:runServer`|gametest.xml|12 / 9 / 3|
|1.21.1 NeoForge|`forge:1.21.1:runGameTestServer`|gametest.xml|12 / 9 / 3|

Report paths are `projects/testiarium/<loader>/versions/<version>/build/test-results/<report>`. These ten native reports contain114 cases:88 passes,26 intentional skips, zero failures/errors. Target standalone/CCT counts each include one vanilla `minecraft:always_pass`; the exact Testiarium inventory is checked separately, so it cannot hide missing project tests. Two client fixtures actually joined an integrated world and captured `build/screenshots/fixture.png`; this is harness proof, not downstream visual acceptance.

Fresh common registration suites also passed: `:testiarium:{1.20.1,1.21.1,26.1.2}:cleanTest` plus corresponding `test` tasks:3/3/5 cases,11 total, no failures/errors/skips. Reports are in each version's `build/test-results/test/`. Target units were rerun after final source changes.

Resume verification reran the exact command below with exit0 (`resume-units-guards.log`), without changing implementation sources:

```sh
timeout --foreground 300s ./gradlew :testiarium:1.20.1:cleanTest :testiarium:1.20.1:test :testiarium:1.21.1:cleanTest :testiarium:1.21.1:test :testiarium:26.1.2:cleanTest :testiarium:26.1.2:test r1ReportGuardProbe -I scripts/verification/gametest-report-guards.init.gradle --no-daemon --console=plain
```

Each native matrix row above was run as `scripts/verification/run-gametest.sh <full-task> [the listed -P flag]`; the runner records deadline/log/exit, invokes the wrapper with `--no-daemon --console=plain`, and applies Xvfb to client tasks. Final successful logs are retained as `_testiarium_<loader>_<version>_<task>.*.log`. Source/report mtimes confirm the target acceptance reports were generated after the final source changes. Resume inspection also found and terminated the task-owned orphan wrapper/daemon/client from the initial timed-out Fabric client launch; no shared daemon or service was stopped. That abandoned run is not acceptance evidence.

### Exact intentional skips

Every standalone/CCT report requires exactly one of each:

- `StandaloneGameTests.requiredFailure` → `required failure fixture`
- `StandaloneGameTests.optionalFailure` → `optional failure fixture`
- `StandaloneGameTests.failFastFailureCannotBecomeSuccess` → `fail-fast sentinel`

Target CCT additionally requires `CctHarnessGameTests.luaFailureCannotBecomeSuccess` → `cct lua sentinel`. All other optional failures are rejected. Client/storage reports permit no skips. `reporter-contract.xml` intentionally contains a required failure to test reporter formatting/counts and is excluded from acceptance aggregation.

## Behavioral red/green and bounded diagnostics

1. Registration: initial target execution5 cases/1 failure at `batchNormalizationCollisionsAreRejectedBeforeStaticRegistryMutation`; pre-mutation collision validation made all5 pass, followed by both old3-case suites.
2. Native fail-fast/reporting: initial Fabric run13 cases/4 required failures/3 intended skips. Three immediate terminal observers and stable reporter-message regression failed. Native finish/iterator handling and original Component extraction made them pass on both loaders.
3. Post-startup CCT action: real Fabric run17 cases/1 failure/4 skips, exact `Post-startup action never dispatched` sentinel. Native API update dispatch made it pass; both CCT loaders passed final replay.
4. Environment: real Fabric run14 cases/1 failure/3 skips, exact `The native batch must own its rule setup/teardown`. Native SetGameRules batch setup made it pass, including restore assertions on both loaders.
5. Report guards: `r1ReportGuardProbe -I scripts/verification/gametest-report-guards.init.gradle` initially exposed four false acceptances (unexpected optional skip; duplicate, missing and unexpected positive discovery). All15 scenarios subsequently passed, including deletion of stale evidence on every scenario, missing/empty/skip-only/failed/error/mismatched reports, exact negative identities/messages, once-only discovery and DOCTYPE rejection. This probe invokes only real guard actions and restores the original XML in finally; its synthetic fixtures are NOT native test evidence.

Compilation/startup failures were also retained, not counted as red behavioral tests or passes: removed API signatures; duplicate old/new Fabric Loader classpaths (fixed by feature dependency inheritance); protected inherited Mixin method shadow (fixed by declaring-class invoker). Initial Fabric client exited124 after a network-registry duplicate-key disconnect; excluding native network load tasks fixed it. Subsequent final client runs exited0 and produced nonempty passing XML. Headless audio/Realms warnings were nonfatal and are not integration acceptance.

## Build, formatting and handoff limits

Target main/feature JARs and feature sources were built with real wrapper tasks (no publication). `receipt.json` lists artifact/source SHA256 values and exact fixture-class exclusion results. Fresh `tasks --all` output verifies the run names. Ordinary target production-server tasks are separately configured as Fabric `runProductionServer` and NeoForge `runServer`, with main-only models/no native-test selection. They are NOT claimed launched; R6 owns ready/stop/save/restart acceptance.

Formatting ran in an isolated standalone clone to avoid the inherited linked-worktree JGit ratchet problem. First Spotless run found the empty target fixture override; it was replaced with a real native metadata assertion helper. Final `spotlessApply spotlessCheck` returned0. Only R1-owned formatting changes were imported; unrelated inherited Broccolium/Tweakium formatting differences were not changed. Final target native/CCT/client runs and units were replayed after formatting. `git diff --check` passed. Generated `.jqwik-database` is left untracked and is not a deliverable.

R2 should merge the cumulative completion SHA and planner SHA, use the target-owned annotation/native registry adapter, stage namespaced SNBT resources, and add its own fixture/run/report sections. Do not widen Testiarium's exact standalone inventory to admit unrelated tests. R5 still owns full36-publication contracts, consumer/source merging/CI and broad builds; R6 production startup/save/network/multiplayer; R7 full content/client/integration visuals; F aggregate OpenWiki/skills/Obsidian reporting. Interactive marker/regeneration commands were ported/compiled but not manually exercised; real import/export and native structure loading were exercised. No save, cross-version network, release-consumer or downstream gameplay compatibility is certified by R1.

Hotspots carried forward: `gradle/minecraft-26-1.gradle.kts` (serial target fixtures/runs) and `gradle/gametest-reports.gradle.kts` (serial report/task guards). Broader campaign work remains with existing owners; no follow-up cards or personal reading tasks were duplicated.
