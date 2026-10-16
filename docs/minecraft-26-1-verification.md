# Minecraft 26.1.2 implementation checkpoint

Status: INCOMPLETE. Task `t_16b4e35f`. This document is a resumable checkpoint, not a support announcement or acceptance report.

## Inheritance and scope

The assigned branch `minecraft-modding-libs/t_16b4e35f-implement-and-verify-additional-minecraf` merged planning commit `9d282ebae09126bb9da1342f58ae4213de429836` as `d42dfcfb109b7fdd250734d9de188da6a0735973`. Verified coverage `e69b7218f5bb077e49e76787168a596f49f26716` and OpenWiki `327f845797e24a3447f4f248f334dc77ebdd35dd` are ancestors of the plan. The branch also retains worker-instruction correction `27798ce`.

Target and acceptance remain those in [the brief](minecraft-26-1-migration-brief.md) and [seven-milestone plan](plans/minecraft-26-1.md). No loader, integration, persistence guarantee or required acceptance gate is silently removed. No remote push/publication is authorized.

## Observed checks

- Java 25 launcher: `/home/siredvin/.gradle/jdks/eclipse_adoptium-25-amd64-linux.2`, Temurin 25.0.3+9. Java 17 and 21 installations are also present; their target compilation has not been rerun here.
- `./gradlew --version`: exit 0 on Java 25.
- Inherited configuration: `timeout --foreground 180s ./gradlew help --no-daemon`, exit 0. This tests the inherited two-version build, not target support.
- Snapshotted tracked authored source hashes, controllers and modern full-file override inventory before target edits.

Evidence directory: `$TMPDIR/t_16b4e35f-validation`. Files: `wrapper.log`, `inherited-configuration.log`, `modern-overrides.txt`, `canonical-source-hashes.txt`. No target XML/artifact/runtime evidence exists yet.

## Build bootstrap observations

`settings.gradle.kts` now routes twelve exact target leaves to checked-in `gradle/minecraft-26-1.gradle.kts`. Old build scripts are unchanged; target common/Fabric use non-remapping Loom, target Forge-family uses NeoForge, and target publication is disabled during bootstrap. The script is incomplete: it does not yet establish access rules, complete fixture/run wiring, publication features or acceptance. No target production sources were changed.

Root plugins explicitly request KGP 2.4.0 alongside MDG 2.0.142; complete per-node classpath/runtime isolation is still a gate. Old leaves explicitly use language/API 2.0. The first old-node compilation failed because KGP 2.4 reports the deliberately retained language version as deprecated under `-Werror`. The narrowly scoped `-Xsuppress-version-warnings` acknowledges that version choice only; source warnings remain fatal.

- `timeout --foreground 240s ./gradlew help --no-daemon`: exit 0 after correcting the bootstrap's `ModDevExtension` receiver to the actual `NeoForgeExtension` setter contract. First script-compilation attempt failed; neither failed attempt is a pass.
- `timeout --foreground 300s ./gradlew :broccolium:26.1.2:dependencies :broccolium:fabric:26.1.2:dependencies :broccolium:forge:26.1.2:dependencies :tweakium:26.1.2:dependencies :tweakium:fabric:26.1.2:dependencies :tweakium:forge:26.1.2:dependencies --no-daemon`: exit 0, no `FAILED` dependency entries. This is dependency-report evidence, not proof that every runtime artifact/optional integration loads.
- Fresh Broccolium common tests on 1.20.1/1.21.1: 10 versioned XML reports and 175 tests each; zero failures/errors/skips, declared/testcase counts agree. Commands were `:broccolium:1.20.1:cleanTest :broccolium:1.20.1:test :broccolium:1.21.1:cleanTest :broccolium:1.21.1:test` (first attempt failed at language-version warning), followed by `:broccolium:1.20.1:test :broccolium:1.21.1:cleanTest :broccolium:1.21.1:test` after the scoped correction; both invocations used `timeout --foreground 300s ./gradlew ... --no-daemon`. The first attempt's `cleanTest` removed the legacy report before the successful retry.
- `javap -v` on the rebuilt `XPUtil` confirms class majors 61/65 and Kotlin metadata `mv=[2,0,0]`. This is representative bytecode inspection, not the planned independent old-Kotlin consumer test or complete old-runtime/publication isolation.
- `timeout --foreground 300s ./gradlew :broccolium:26.1.2:compileKotlin --no-daemon`: exit 1. Existing canonical sources are not a target port. Reported mismatches include neighbor notifications, removal lifecycle, item NBT access and direction properties. No target tests ran.

## Material observer-API decision required

Existing public `IObservingBlockEntity.onNeighbourChange(neighbour: BlockPos)` receives the actual causal position from `BaseBlockEntityBlock.neighborChanged`; see `projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/base/api/IObservingBlockEntity.kt:10` and the 1.21.1 block override at lines 46–59.

Inspection of the resolved target JAR shows `BlockBehaviour.neighborChanged(BlockState, Level, BlockPos, Block, Orientation, boolean)`. Its sole BlockPos identifies the receiving block; the old causal-neighbor position parameter is gone. `NeighborUpdater` also passes Orientation instead, and Orientation contains directions/bias, not a causal position. Consequently this hook cannot truthfully forward the old position for arbitrary native notifications, particularly a null Orientation. Passing the receiver position, guessing one direction, or issuing six synthetic notifications would change the public behavioral contract.

Proposed decision for approval: on 26.1.2 only, adapt the observer callback to `onNeighbourChange(neighbourBlock: Block, orientation: Orientation?)` and document the target consumer migration; keep 1.20.1/1.21.1 API and behavior unchanged. This proposal has NOT been implemented. A generic reconstruction shim or silently lost callback is not authorized. Approval of the target-only public callback change is needed before this production subsystem can be ported.

Evidence: `target-base-api.txt`, `target-neighbor-api.txt`, `broccolium-target-first-compile.log`, `old-common-compiler-probe-results.json`, `old-metadata-120.txt`, `old-metadata-121.txt`, `target-configuration-*.log`, `target-dependencies.log` in the evidence directory. Resolved target vanilla JAR SHA-256: `7b8ccf96f90c036980c0ef8a3f149c2166ae0d24e0d924a94115399879d8f4b0`.

Resume after the explicit API decision: finish milestone 1's minimal target/old consumer and full classpath-isolation checks, then seed modern full-file behavior into authored target overrides and port the approved observer/removal lifecycle with native regression tests. Do not treat the dependency report or this bootstrap as completed support.

## Unperformed acceptance

All target production ports, new regression suites, full target/legacy acceptance builds/tests, datagen, clients/servers, copied-save and multiplayer checks, independent public consumers, publication isolation, source-grounded OpenWiki updates and final local LLM-Wiki completion report remain unperformed. Only the bounded old common test probe above ran freshly here. Canonical controllers and authored gameplay sources remain unchanged. Inherited coverage is not a fresh implementation pass.
