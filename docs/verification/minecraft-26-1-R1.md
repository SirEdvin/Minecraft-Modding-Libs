# R1 Testiarium registry and native harness verification

Status: INCOMPLETE — reconnaissance checkpoint, no target runtime acceptance yet.

## Custody and scope

Task t_728ae4df owns Testiarium registration, generic/CCT/client fixtures and bounded runner/report wiring only. Merged planner b8fd1ddfd5134c45402040fe2c1ca5be343ac48f then R0 cumulative e07fcc12175bef26bac0f7403d901db07cab67a9 by fast-forward; both ancestry checks returned 0. The card's 2026-10-17 execution release supersedes planning-document holds. No push/publication/primary checkout changes authorized.

Read the remaining plan, original brief/source ledger and inspected the 204-row coverage map. Accepted exact pins and canonical 1.20.1 remain unchanged. Java launcher verified: Temurin25.0.3+9, worker memory.max 8589934592 bytes. Evidence directory: `/home/siredvin/.hermes/profiles/albina/cache/scratch/t_728ae4df-validation`.

## Phase 1: contract and source port (incomplete)

- `timeout --foreground 300s ./gradlew :testiarium:26.1.2:tasks --all --no-daemon --console=plain` returned 0 (`initial-tasks.log`). This is configuration/task discovery, not test evidence.
- Inspected actual cached target Minecraft bytecode with JDK25 javap (`native-signatures.txt`). `GameTest` annotation and `GameTestRegistry` are absent. Native `FunctionGameTestInstance` takes a function ResourceKey plus `TestData<Holder<TestEnvironmentDefinition<?>>>`; GameTestInfo uses a registry holder. Its errors now use GameTestException and Component-based assertions.
- Existing public facade/groups/tags and testMod/cctTestMod separation must survive. A target Testiarium-owned annotation is needed instead of a forbidden net.minecraft shim; exact adapter API is still under investigation.
- No behavior changes or behavioral red/green executions yet. All native/CCT/client gates remain unperformed.

## Recovery and next action

Checkpoint 3: both target standalone native suites passed (each 13 cases: 10 passes including vanilla always_pass, three exact intentional negative skips), and both target Xvfb client harnesses passed (one real world/screenshot case each). Logs: `native-fabric-green-2.log`, `native-forge-2.log`, `client-fabric-2.log`, `client-forge-initial.log`, all exit0 and Gradle report guards verified. Initial Fabric native run was a genuine behavioral red: 13 cases, four required failures (three immediate fail-fast observers and reporter tick-suffix contract), three intended skips. XML retained in `native-fabric-red`. Native fail() now only records errors; target fail-fast explicitly finishes and blocks the iterator in the same tick. Reporter extracts the original assertion Component for stable machine-readable sentinels while retaining native stack details.

Initial client run exit124 exposed duplicate registration while loading network-synced registries (not a client pass); the Fabric hook now skips native NetworkRegistryLoadTask. Subsequent world/client report passes validate that correction. CCT remains incomplete: target source compiles after ItemInput/API changes; first launch exposed duplicate old/new Fabric Loader classpaths, corrected by feature implementation inheritance. The next launch exposed an inherited protected-method Mixin shadow; replaced by an invoker on its actual BlockableEventLoop owner. No CCT success claimed yet. Next exact command: bounded target Fabric `runCctGameTest`, then target NeoForge CCT and guard-negative regression probes. Full candidate replays/old standalone regressions and evidence packaging remain required.

Checkpoint 2: target common/Fabric/NeoForge main compilation returned 0 (`main-compile.log`). Five target registration tests executed; initial behavioral red was `batchNormalizationCollisionsAreRejectedBeforeStaticRegistryMutation` (5 tests, 1 failed, exit1, `registration-red.log`, preserved XML SHA256 `316ea5db1dfdddf0e1a2485d4958499438c575e1cd23a305d640b0a9d0a8650a`). Added pre-mutation batch-ID collision validation. Fresh target plus both old registration suites then returned 0 (`registration-green-old.log`); exact report aggregation pending. Target-only annotation `site.siredvin.testiarium.api.GameTest` retains metadata; native function/instance/environment adapters compile. Fabric uses a registry-load-before-freeze Mixin, NeoForge uses static RegisterEvent plus RegisterGameTestsEvent. Private generic/CCT features and candidate run tasks configured, not runtime-verified yet. No downstream source changed.

Current dirty source roots: target common/Fabric/forge overrides plus target convention. Modern overrides have been seeded for migration and some remain intentionally unported. Native fixture compilation is in progress; these compile failures are NOT behavioral red or acceptance evidence. Next exact command after finishing target fixture API updates: bounded `:testiarium:fabric:26.1.2:runServer` to exercise real native outer observers before changing fail-fast behavior.

Inspect actual loader function/environment/instance registration hooks and native exception/sequence contracts. Add owning target registration regressions, execute a behavioral red once target compilation is bootstrapped, then implement and run both loaders' standalone/CCT/client suites plus old registration/native checks.

Hotspots carried from R0: `gradle/minecraft-26-1.gradle.kts` (serial target fixture/run wiring) and `gradle/gametest-reports.gradle.kts` (fresh/negative-fixture guards). Do not rerun downstream gameplay, visual, save/network or publication campaigns as R1 acceptance.
