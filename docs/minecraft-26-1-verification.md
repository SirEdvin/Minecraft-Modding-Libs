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

## Approved target-only observer-API decision

### Resumed compiler boundary checks (2026-10-17 UTC)

`timeout --foreground 360s ./gradlew migrationClasspathProbe -I "$LOG_DIR/classpath-probe.init.gradle" --no-daemon` exited 0. The scratch init script collected 72 resolved compile/runtime graphs (36 leaves). Programmatic assertions confirm common/Fabric old stdlib 2.0.0, old Forge 1.20.1 stdlib 2.0.0, NeoForge 1.21.1 stdlib 2.1.0 (inherited KFF 5.7.0 selection), and target stdlib 2.4.0 throughout; target runtimes did not leak to old leaves. This is resolution evidence, not publication/runtime-loader acceptance.

`python3 "$LOG_DIR/consumer-probes.py"` compiled and executed two independent minimal consumers of rebuilt Broccolium XPUtil using compiler 2.0.0 on Java 17/21; both exited 0 and produced class majors 61/65. A minimal native Identifier consumer compiled and executed against the target classpath with compiler 2.4.0 on Java 25, exit 0, major 69. The first probe incorrectly launched old compilers on Java 25 and failed before compilation (`IllegalArgumentException: 25.0.3`); retry used each supported old JDK, without metadata-check suppression. These narrow consumers do not replace full platform/storage/peripheral extension consumers.

Evidence: `classpaths.json`, `classpath-probe.init.gradle`, `classpath-probe.log`, `consumer-probes.py`, `consumer-results.json`, `consumer-{1.20.1,1.21.1,26.1.2}/{compile,run}.log`. Bootstrap access/fixture/publication wiring remains unfinished; milestone 1 as a whole is not yet complete.

Existing public `IObservingBlockEntity.onNeighbourChange(neighbour: BlockPos)` receives the actual causal position from `BaseBlockEntityBlock.neighborChanged`; see `projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/base/api/IObservingBlockEntity.kt:10` and the 1.21.1 block override at lines 46–59.

Inspection of the resolved target JAR shows `BlockBehaviour.neighborChanged(BlockState, Level, BlockPos, Block, Orientation, boolean)`. Its sole BlockPos identifies the receiving block; the old causal-neighbor position parameter is gone. `NeighborUpdater` also passes Orientation instead, and Orientation contains directions/bias, not a causal position. Consequently this hook cannot truthfully forward the old position for arbitrary native notifications, particularly a null Orientation. Passing the receiver position, guessing one direction, or issuing six synthetic notifications would change the public behavioral contract.

Approved by the user on 2026-10-17 (Europe/Kyiv), as recorded on task `t_16b4e35f`: on 26.1.2 only, adapt the observer callback to `onNeighbourChange(neighbourBlock: Block, orientation: Orientation?)`. Forward the actual native block and orientation, including null; never reconstruct a causal position. Keep 1.20.1/1.21.1 API and behavior unchanged. Approval resolves the prior decision hold; implementation and regression verification are still separate outstanding gates. No unrelated public API change, integration removal or save-format change is authorized.

Evidence: `target-base-api.txt`, `target-neighbor-api.txt`, `broccolium-target-first-compile.log`, `old-common-compiler-probe-results.json`, `old-metadata-120.txt`, `old-metadata-121.txt`, `target-configuration-*.log`, `target-dependencies.log` in the evidence directory. Resolved target vanilla JAR SHA-256: `7b8ccf96f90c036980c0ef8a3f149c2166ae0d24e0d924a94115399879d8f4b0`.

Resume: finish milestone 1's minimal target/old consumer and full classpath-isolation checks, then seed modern full-file behavior into authored target overrides and port the approved observer/removal lifecycle with native regression tests. Do not treat the dependency report or this bootstrap as completed support.

## Unperformed acceptance

Full target/legacy acceptance builds/tests, datagen, clients/servers, copied-save and multiplayer checks, full extension consumers, publication isolation, source-grounded OpenWiki updates and final local LLM-Wiki completion report remain unperformed. The bounded old common tests and minimal consumers above are not a fresh full implementation pass. Canonical controllers and old authored gameplay sources remain unchanged.

## Resume checkpoint 3 — partial base port, NOT support acceptance

The scoped observer callback has a target full-file override and forwards the native Block and nullable Orientation. `ObserverCallbackTests` executes four isolated cases: exact block/orientation identity and single notification, null orientation, absent block entity, and ordinary non-observer block entity. `python3 "$LOG_DIR/run-observer-focused.py"` compiles the actual target callback/base sources with Kotlin 2.4.0/JVM25 and `-Werror`, then runs JUnit with native 26.1.2 block/entity/Orientation implementations and a substituted Level lookup. Final result: four tests, zero failures/errors/skips, declared/testcase counts agree in fresh `observer-focused/reports/TEST-junit-jupiter.xml`; `observer-focused/results.json` records counts. This is NOT a loader-run GameTest or real chunk dispatch proof. Early harness attempts exposed protected/native method naming, an unnecessary assertion warning, post-bootstrap registry freeze, and an overloaded ByteBuddy matcher; these were corrected rather than suppressing bootstrap errors. The test now explicitly bypasses block registration/world construction without replacing the production callback body.

Additional authored target WIP: modern-derived BaseNBTBlock, Facing/GenericBlockEntityBlock, registry-entry/codecs, and MutableNBTBlockEntity using ValueInput/Output with a root MapCodec to retain existing payload keys. These ports have NOT passed whole-library compilation or persistence regressions. BlockCodec resolves its supplier before reading the RegistryEntry (the modern baseline cast the supplier itself). Add a focused codec round-trip regression before accepting this correction.

Removal WIP: common LevelChunkMixin redirects only the native setBlockState→removeBlockEntity call, performs native removal, then invokes destroy for observing BaseBlockEntityBlock instances. This avoids treating chunk unload/setRemoved as destruction and retains vanilla pre-remove drops. The mixin and target loader metadata/configs are authored, but descriptor/injection/runtime behavior is NOT verified. Required next regression: same-block state change versus replacement, one destroy after removal, preserved native container drops, client/server paths and no destruction on chunk unload.

Build WIP adds target JUnit/ByteBuddy dependencies and version-input resource expansion. Target Broccolium metadata declares Java25/exact26.1.2 and common removal mixin. Latest bounded Gradle attempt (`:broccolium:26.1.2:compileKotlin :broccolium:fabric:26.1.2:processResources :broccolium:forge:26.1.2:processResources --continue --no-daemon`, 240s timeout) exited1 during configuration: dependent Loom access-widener processor could not find broccolium.accesswidener. A loader-local official-namespace widener has since been authored, but this correction is not yet rechecked. Previous compile reached additional unported GenericBlockEntityBlock, MutableNBTBlockEntity and descriptive-item APIs; new overrides supersede the first two, unverified. Do not mistake this configuration failure for executed compile/tests.

Exact next action: re-run the bounded configuration/resource/compile command and establish complete target common+loader access rule wiring; finish modern-derived common overrides (tooltip/data/platform/storage) based on target bytecode, then run the owning native harness and persistence/removal regressions. Reuse `classpaths.json`, compiler classpaths, `target-{removal,valueio,tag-codecs,registry-properties}.txt`, and focused probe scripts in the evidence directory. No temporary generated compatibility layer or production shim was added. No target artifact is approved for publishing; bootstrap publication remains disabled. Continue the existing seven-milestone card, not a replacement task.
