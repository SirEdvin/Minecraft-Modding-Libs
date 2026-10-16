# Additive Minecraft 26.1 Implementation Plan

> For Hermes: implement in the existing dependent card t_16b4e35f, respecting its owner and local-only contract; no external coding agents.

Goal: add 26.1-family Fabric/NeoForge support without removing existing targets.

Architecture: preserve four Stonecutter common/loader trees and the independent TypeScript contract project. Prefer target full-file overrides and coarse typed platform boundaries over generated rewrites.

Tech Stack: Kotlin, Gradle, Stonecutter, Fabric Loom, ModDevGradle, CC:Tweaked; target Java 25 and retained Java 17/21.

Status: executable planning handoff, 2026-10-17 Europe/Kyiv; not a completed port. See [migration brief](../minecraft-26-1-migration-brief.md) for decisions/inventory/pins and [source ledger](../minecraft-26-1-sources.md) for provenance.

## Operating protocol and decision ownership

Work in t_16b4e35f's assigned isolated worktree, never this planning worktree or a companion primary checkout. Read AGENTS.md, this plan, the brief, docs/module-test-coverage.md, docs/stonecutter-migration-audit.md, openwiki/index.md and the applicable source files. Receive the exact cumulative SHA through the parent completion metadata; run `git merge-base --is-ancestor <planning-sha> HEAD` and incorporate that commit if needed. A dependency edge does not transfer Git changes. Preserve dirty work; do not reset it.

The selected shipping matrix is 1.20.1 Forge/Fabric (Java17), 1.21.1 NeoForge/Fabric (Java21), 26.1.2 NeoForge/Fabric (Java25), plus common artifacts and independent typed-peripheral-api. Keep Stonecutter0.9.8, Gradle9.5.0 and canonical1.20.1. Pins are in the brief. No silent loader/integration/save/public-contract removal, global prerelease upgrade, primary mutation, remote push or release. Routine implementation is authorized; only a material scope change needs a decision.

Use cohesive milestones below, sequential where they share build files or platform/persistence APIs. Do not parallelize root/settings/catalog/controller edits, the storage contract or registry facade. Fixtures/reporting may proceed after the build and public seams are stable, but keep one integration owner. No external coding agents are required or authorized. Save checkpoints and actual evidence after each coherent milestone.

For behavior changes: add the focused failing test to the owning existing suite, execute it and preserve red output, make the smallest root-cause port, execute green and adjacent regressions. A compile error establishes API mismatch, not a behavioral test. New integration paths require native tests. Do not guess full target API implementations from this plan; inspect target sources/bytecode first. Each milestone is a reviewable local commit after its checks; record exact hashes/commands/outcomes. Never commit unverified generated output or secrets.

Before the first probe, initialize scratch logs once and reuse this location through all milestones:

    LOG_DIR="${TMPDIR:?}/t_16b4e35f-validation"; mkdir -p "$LOG_DIR"

## Milestone 1 — Resolve the build boundary without changing gameplay

Objective: prove the selected plugins, compiler and source graph can coexist, before porting algorithms.

Existing files: `settings.gradle.kts`, `build.gradle.kts`, `gradle/libs.versions.toml`, `gradle.properties`, `gradle/wrapper/gradle-wrapper.properties`; each `projects/{broccolium,tweakium,peripheralium,testiarium}/stonecutter.gradle.kts`; all twelve common/fabric/forge build scripts. Proposed new repository-owned convention: `gradle/minecraft-26-1.gradle.kts`. Do not add an unpublished external toolkit dependency.

1. Record clean/dirty status, ancestry, Java25 launcher plus installed/resolved Java17/21 toolchains and wrapper version. Snapshot authored source hashes and current controller state. Use a scratch evidence directory, not tracked logs.
2. Add exact `26.1.2` graph leaves for all four libraries and both loader branches; retain existing nodes/vcsVersion. Add explicit target constants if needed. Verify 26.x numeric comparison does not route to old `>=1.21` catalog choices. Replace two-way dependency/toolchain selection with three explicit maintained versions and reject unknown versions.
3. Align a single KGP2.4.0 and compatible single MDG implementation across convention-plugin/root classpaths; keep initial MDG2.0.142. Preserve old-node Kotlin language/API2.0, stdlib/runtime pins and JVM17/21. Target Kotlin/JVM25 and compatible runtime2.4.0 must not drag newer runtime artifacts into old publications. Do not treat catalog text as proof of the plugin actually loaded; inspect dependency resolution and Kotlin metadata.
4. Target common and Fabric use non-remapping `net.fabricmc.fabric-loom`1.17.11, vanilla dependency with no mappings, normal configurations and normal jar release task. Change unconditional old common/Fabric convention plugin application to declared-but-not-applied plus explicit node selection; skipping only shake calls is insufficient if plugin application already installs remapping/vanilla setup. Do not apply both Loom variants to one target project. Old nodes retain existing setup. Establish complete common+loader official-namespace access rules and source/resource merging without pulling Fabric API into common.
5. NeoForge target uses26.1.2.122, no mismatched Parchment. Select target dependencies from brief, including `cc-tweaked-26.1.2-forge` (not guessed neoforge coordinate). FLK/KFF runtime pins are target-only. Inspect config imports/actually selected dependencies: no ForgeConfigSpec/ModConfigSpec imports were found in planning, so distinguish unused catalog entries from required transitives. Resolve any necessary config compile artifact from publisher metadata before locking CI. Keep required runtime integrations.
6. Run only configuration/dependency tasks first. Proposed bounded probe: `timeout --foreground 180s ./gradlew help --no-daemon > "$LOG_DIR/configuration.log" 2>&1`. Then `./gradlew :broccolium:26.1.2:dependencies :broccolium:fabric:26.1.2:dependencies :broccolium:forge:26.1.2:dependencies --no-daemon > "$LOG_DIR/target-dependencies.log" 2>&1`. These paths are proposed new nodes; verify them after graph creation.
7. Prove a minimal target classpath in scratch if full production code cannot yet compile; no stub production feature may be published. Exercise a minimal consumer on both old nodes to prove old compiler metadata/runtime preservation. Commit build graph/configuration only after expected dependency/toolchain/override ownership is established; full target feature compilation may remain red for named source API differences.

Completion: configuration succeeds, all selected coordinates resolve or a precisely scoped external blocker is raised, old leaves retain identities/toolchains/runtime, target override strategy is proven. If legacy convention classpaths prevent coexistence, resolve within checked-in build logic or an isolated toolkit worktree; do not disable lint or rely on local Maven state in committed CI. Companion work uses the brief's own-wrapper/scratch-file-Maven consumer protocol; no automatic template rewrite.

## Milestone 2 — Mechanical names, registration and loader metadata

Objective: make target registration/entrypoints load with correct public identities and sides.

Existing boundaries: Broccolium `modules/platform/{BasePlatform.kt,api/InnerBasePlatform.kt,api/RegistryWrapper.kt}` and loader counterparts; modern source overrides under `projects/broccolium/versions/1.21.1/src/main`; Peripheralium `Items.kt`, `Blocks.kt`, `PeripheraliumCore.kt`, `PeripheraliumPlatform.kt`; Tweakium `modules/minecraft/setup`; all adapter entrypoints/resources.

Proposed target paths: matching `projects/<library>[/fabric|/forge]/versions/26.1.2/src/main/...` overrides, including target fabric.mod.json and META-INF/neoforge.mods.toml, access files and Mixin JSON. These do not exist yet.

1. Enumerate modern1.21.1 full-file overrides before creating target files; port those behaviors, not legacy fallbacks. Separate official-name/nullability rename diff from behavior changes. Audit ResourceLocation→Identifier without flattening ResourceKey/Holder distinctions.
2. Supply item/block resource keys before constructing properties. Preserve public supplier-based platform entrypoints where possible, using typed factory additions only when required; do not silently change old-node registration API. Validate real registries/key/id round trips and missing membership behavior.
3. Port target CC upgrade registration/type contracts; keep native loader lifecycle and common registration separate. Preserve modern Peripheralium content/recipe absence of legacy blend.
4. Update metadata exact Minecraft/Java/loader/runtime/dependency requirements and official access namespace. Replace old Fabric `fabric` dependency alias with `fabric-api` on target only. Validate expanded metadata inputs change incrementally without clean/rerun-tasks.
5. Audit Mixins/AT/AW strings, service files and reflection targets against target bytecode. Keep client Mixins physically client-gated. Do not retain old refmap processing on target just to satisfy an obsolete task name.
6. Add native registration/entrypoint smoke tests in existing Broccolium/Tweakium/Peripheralium testmod suites; compile common and adapters, then launch the smallest target native runner once Testiarium adapter (milestone5) is ready. Before that, report compile-only evidence honestly.

Completion: correct metadata and transformed registrations; old IDs and public contracts preserved, no common initialization resolves client classes. Local commit boundary: mechanical names then registration/metadata, not one combined rewrite.

## Milestone 3 — Persistence, upgrades and data I/O

Objective: retain settings and equipped-upgrade state across runtime reconstruction and disk reload.

Existing source anchors:

- `projects/broccolium/versions/1.21.1/src/main/kotlin/site/siredvin/broccolium/modules/base/blockentity/MutableNBTBlockEntity.kt` and base block removal/codec helpers.
- `projects/tweakium/versions/1.21.1/src/main/kotlin/site/siredvin/tweakium/modules/peripheral/{api/IDataStorage.kt,util/DataStorageUtil.kt}`.
- `projects/tweakium/versions/1.21.1/src/main/kotlin/site/siredvin/tweakium/modules/{pocket/StatefulPocketUpgrade.kt,turtle/StatefulTurtleUpgrade.kt}` and shared BasePocketUpgrade/BaseTurtleUpgrade factories.
- Existing modern `DataStorageTests`, `UpgradeDataStorageTests`, `UpgradeBehaviorGameTests` and runtime settings fixtures in the same owning test trees.

1. Add target tests for default/missing values, failed encoding without write/notification, unrelated component retention, explicit removal and copied crafting templates. Round-trip through real target item serialization with registry access.
2. Adapt vanilla block-entity/entity serialization hooks to ValueInput/ValueOutput and target codec/nullability changes while retaining public settings payload keys. Do not convert runtime owner caches into attachments/SavedData. Port removal callbacks without duplicate drops or forgotten cleanup.
3. Adapt target CC UpgradeBase crafting/upgrade-data signatures to ItemStackTemplate. Keep runtime inventory values as ItemStack. Resolve templates only at valid registry/world boundaries, including client model requests; preserve importantComponents whitelist/removals and suitability semantics.
4. Add real install/remove/reinstall, chunk unload/reload and server disk save/restart cases for configured pocket and turtle peripherals plus peripheral block-entity settings. Explicitly assert owner/side association and unchanged template/defaults.
5. Preserve exact custom-data/config IDs. No project custom SavedData migration was discovered. If implementation finds one, classify global/dimension ownership before choosing storage or moving any file; escalate destructive schema/layout changes. Vanilla File Fixer Upper is covered by copied-save gate regardless.

Checks: focused owning common tests and target native runtime/upgrade fixtures, then old modern regression fixtures. Completion: real data round trips and copied-save scenario later in milestone7; no blanket world compatibility claim from unit proxies. Commit persistence and upgrade adaptation together with its regression evidence.

## Milestone 4 — Transactional storage and platform/gameplay behavior

Objective: integrate NeoForge's new transfer model and Fabric target APIs without losing resource or rollback semantics.

Existing files: Broccolium `modules/storage` public interfaces, `item/ReplaceableItemStorage.kt`, `fabric/src/main/.../modules/storage/{FabricStorageUtils.kt,item/*Wrapper.kt}`, `forge/versions/1.21.1/src/main/.../modules/storage/ForgeStorageUtils.kt`, Forge item/fluid/energy wrappers; Team Reborn energy integration. Tests: common `src/testFixtures/.../{StorageTests,SlottedStorageTests,FluidStorageTests,EnergyStorageTests}.kt`, loader `src/test`, native Fabric ItemBridge/energy tests; Tweakium `modules/plugins`, `modules/player`, lookup/drop-capture and operation native fixtures.

1. Write positive-provider item/fluid/energy cases, not merely absence lookup. Cover component-bearing variants, zero/negative/overflow amounts, partial insert/extract, nested abort/commit, rejected mutation, nullable sides, and removed block entities. Verify conservation before/after every path.
2. Implement target-only ResourceHandler/ItemResource/FluidResource/EnergyHandler adapters after inspecting selected NeoForge sources. Simulation must not commit. ItemAccess must replace the origin slot when a container changes; abort restores stack and components. Document native fluid/energy units and int/long bounds.
3. Keep old public agnostic interfaces; distinguish bounded simulate/execute behavior from native transactions. Reverse adapters use native slot transaction or explicit exact-replacement support. Reject unsupported backings before mutation, including partial/refused restoration. Do not claim callbacks are rolled back.
4. Port Fabric imports/energy5.0.0 and retain detached snapshot/native-slot behavior. Run common/loader/native suites for all three MC versions after source changes affecting shared contracts.
5. Port Tweakium transfer/query plugins and fake-player providers without changing destination validation, operation payment, cleanup or protection semantics. Exercise successful and exceptional drop capture plus attributes/owner cleanup. Scope known inherited gaps explicitly rather than claiming every third-party storage/protection provider is certified.

Completion: no data loss/conservation regression, actual native target providers and aborted item-container mutation pass, old nodes stay green. Commit storage bridge before higher-level plugin integration if separate review is useful, but hand off one usable milestone.

## Milestone 5 — Testiarium registry harness, resources and datagen

Objective: make target behavior executable and validate regenerated data, not just Kotlin compilation.

Existing files: `projects/testiarium/src/main/.../{Testiarium.kt,api/GameTests.kt,api/Assertions.kt,report/TestReporters.kt}`, matching modern common/loader overrides; `src/testMod`, `src/cctTestMod`, loader feature wiring and Mixin resources. Also `gradle/gametest-reports.gradle.kts`, Broccolium `modules/data`, loader GeneratorSink, Peripheralium `data/*Provider.kt`, Tweakium `modules/data`, existing test structure/computer resource trees.

1. Translate existing Testiarium registration/annotations into target GameTest function/instance/environment registry contracts. Preserve grouping/tags/deduplication, constructor/reflection failures and metadata. Inspect current Fabric/NeoForge test APIs instead of calling removed GameTestRegistry by habit.
2. Port sequence fail-fast and actual target parent/reflection access; run required outer observers for assertion, exception and assertion-error, single execution and competing delayed success. CCT private scheduler hooks require target CC bytecode validation and real Lua tests.
3. Port test structure/resource directory/schema changes and environment setup/teardown restoration. Preserve testMod and cctTestMod feature separation and Peripheralium-owned fixtures in the Tweakium runner. Confirm each intended test is discovered exactly once.
4. Extend report guard to target task/report paths. Delete exact prior reports before launch; require fresh nonempty XML, reconcile declared/testcase counts, reject failures/errors, enforce no storage skips and exact expected Testiarium negative identities/sentinels. A zero process status or skip-only run is not acceptance.
5. Adapt providers and client item definitions first, then run target Fabric and NeoForge datagen. NeoForge may need client/server runs; discover actual tasks from selected MDG and checked-in configuration, record exact names. Review output diff for recipes/loot/tags/languages/models/pack formats and both UA/EN completeness. Regenerate twice to prove deterministic output. Never hand-edit generated assets.

Completion: target native server suites actually execute, all required observers pass, output resources load and match intended content. Existing negative fixtures remain explicit skips, not passes. Commit harness+fixtures and provider/resource changes as separate reviewable boundaries if necessary.

## Milestone 6 — Client rendering, menus and public contracts

Objective: load both target clients and exercise UI/CC visual/network behavior with server authority intact.

Existing anchors: modern Tweakium `modules/turtle/client/FacingBlockTurtleModeller.kt`, common `modules/rml`, Fabric client entrypoints, NeoForge client listeners; Testiarium `src/testMod`/`src/cctTestMod` client helpers; Broccolium `modules/platform/api/{MenuBuilder.kt,SavingFunction.kt}` and FabricPlatformToolkit/FabricInnerBasePlatform modern overrides. TypeScript `projects/typed-peripheral-api/tests/contracts.ts` and root source modules.

1. Inspect CC's exact target modeller API and port extraction/submission/material model requests. Prefer target JSON/model facilities over ad hoc render compatibility wrappers. No mutable world reads during render submission, no common/client linkage leak.
2. Port Fabric menu names (ExtendedMenuType/ExtendedMenuProvider) and registry-aware opening codecs. Bound consumed bytes/counts, define buffer ownership, validate invalid/truncated input without leaking buffers or crashing server. Vanilla block-entity update packets remain play server→client; no custom packet framework is required.
3. Test menu authorization, distance/player/menu identity and malformed actions server-side. Client must not dictate trusted position/count/operation. Run real two-client multiplayer with simultaneous interactions and reject invalid/unauthorized actions without state mutation.
4. Start each target client to title screen, create/join a world, view creative tabs/content/models and equipped turtle/pocket upgrades, reload resources and reconnect to a dedicated server. Repeat with/without REI/ModMenu or JEI as appropriate. Do not disable integrations based on release-day historical warnings.
5. Exercise a small external Kotlin/Java consumer for platform/storage/peripheral extension points and target CC API. Old-node Kotlin2.0 consumer proves metadata remains consumable; compare old signatures/dependency graph. Run typed-peripheral-api compiler contracts and real Lua behavior; no TypeScript source change absent an actual API delta.

Completion: both target clients and dedicated server joins succeed with correct visual/interaction behavior, malformed input rejected, public contracts and dependency/runtime isolation verified. Commit client/menu adaptation without unrelated gameplay/API redesign.

## Milestone 7 — Full acceptance and reproducible handoff

Objective: verify all supported nodes and publication contracts, restore canonical state and report only observed results.

Existing files to extend: `.github/workflows/ci.yml`, `scripts/check-publications.py`, `gradle/gametest-reports.gradle.kts`, `docs/module-test-coverage.md`; proposed new implementation evidence report `docs/minecraft-26-1-verification.md`. Preserve toolchain/canonical contracts and source hash evidence.

All Gradle commands below are future implementation checks, NOT executed by planning. Reuse the LOG_DIR initialized in the operating protocol.

Do not assume a task exists because listed as proposed here. After milestone1 inspect `./gradlew tasks --all --no-daemon > "$LOG_DIR/tasks.log" 2>&1`, compare actual build scripts/CI and record final target run names. Retain old names unless an explicit API necessity requires change; update guard and CI together.

### Per-node verification matrix

| Node | Build/unit | Native harness | Datagen/server/client/save |
| --- | --- | --- | --- |
| 1.20.1 common + Forge/Fabric | All four common/loader builds; fresh unit reports, old Kotlin/runtime/JVM17 consumer | Existing five native tasks below, retaining intentional skips | Datagen no unintended diff; dedicated server/client smoke with fixture mods; representative save/reload regression |
| 1.21.1 common + NeoForge/Fabric | Same on JVM21 with existing dependencies | Same existing five tasks, modern upgrade/persistence cases | Same, including modern component removals and true disk reload |
| 26.1.2 common + NeoForge/Fabric | All four common/loader builds on JVM25, plus TypeScript check once | Proposed same five target tasks, registry harness and transaction/upgrade additions | Both loaders: datagen, server ready+clean stop, title/world/client→dedicated-server join, copied1.21.1 save/config upgrade, multiplayer/invalid input and integration variants |

For each version v, concrete current build/test routes generalized to new v after graph creation:

    ./gradlew :broccolium:$v:build :broccolium:fabric:$v:build :broccolium:forge:$v:build :tweakium:$v:build :tweakium:fabric:$v:build :tweakium:forge:$v:build :peripheralium:$v:build :peripheralium:fabric:$v:build :peripheralium:forge:$v:build :testiarium:$v:build :testiarium:fabric:$v:build :testiarium:forge:$v:build --no-daemon > "$LOG_DIR/build-$v.log" 2>&1

Run `cleanTest` before the final unit campaign; count only fresh executed versioned XML for that campaign, not stale unversioned directories. Root final build includes `:typed-peripheral-api:check` (also run explicitly for attribution):

    ./gradlew :typed-peripheral-api:check --no-daemon > "$LOG_DIR/typescript.log" 2>&1
    ./gradlew cleanTest build generatePomFileForMavenPublication generateMetadataFileForMavenPublication --no-daemon > "$LOG_DIR/final-build.log" 2>&1
    python3 scripts/check-publications.py > "$LOG_DIR/publications.log" 2>&1

Existing native task matrix, execute once per v in 1.20.1,1.21.1,26.1.2 after target equivalents are implemented:

    timeout --foreground 900s ./gradlew :broccolium:fabric:$v:runStorageGameTest :testiarium:forge:$v:runGameTestServer :testiarium:fabric:$v:runServer :tweakium:forge:$v:runGameTestServer :tweakium:fabric:$v:runPeripheralGameTest --no-daemon > "$LOG_DIR/native-$v.log" 2>&1

Broccolium Forge-family storage scenarios must be added to the loaded Tweakium/native integration suite or a separately verified native runner; do not invent coverage from Fabric-only storage tests. Target native report counts are determined by actual tests, not old hard-coded totals. Old baseline reference: 1,116 JUnit invocations per active-state build; ten native reports with177 passes/12 intentional skips. New counts may grow but dropped required identities need explanation.

Datagen/run commands: existing loader conventions define `runData`, `runServer`, `runClient`; run them per owning adapter/version after task discovery. Example future target `timeout --foreground 300s ./gradlew :peripheralium:fabric:26.1.2:runData --no-daemon > "$LOG_DIR/datagen-peripheralium-fabric.log" 2>&1`. For target NeoForge split data runs, use the actual client/server datagen task names established in milestone5 and record both commands before execution. Datagen every owning project with providers, not only Peripheralium. Verify no-op second generation, loaded recipes/loot/tag correctness and no cross-version generated asset leakage.

Dedicated-server smoke is distinct from GameTests: provision a scratch universal fixture/consumer containing all production modules and CC, with appropriately accepted EULA and no test-only dependencies. Launch each loader/version's real server task/process with explicit timeout and file logs, wait for its normal ready signal, issue `stop`, require clean exit and inspect world save. Timeout termination is not clean shutdown. Never modify live server data.

Headless client fixtures use Xvfb and timeout. Current optional harness routes (verify target equivalent):

    timeout --foreground 300s xvfb-run --auto-servernum ./gradlew :testiarium:fabric:$v:runClientGameTest --no-daemon > "$LOG_DIR/client-fabric-$v.log" 2>&1
    timeout --foreground 300s xvfb-run --auto-servernum ./gradlew :testiarium:forge:$v:runClientGameTest --no-daemon > "$LOG_DIR/client-forge-$v.log" 2>&1

A fixture client pass is not automatically title-screen/world-join/render acceptance. Capture actual screenshots/log signals for content, CC upgrades, menu and multiplayer joins as specified in milestone6; store screenshots outside tracked repo. Unavailable display/runtime is an honest external blocker, not a simulated pass.

Copied-save procedure for EACH target loader: create a representative1.21.1 world with all registered content, configured peripherals, item/fluid/energy inventories, upgraded pocket/turtle data (including removal patch), custom names and settings; shut down; hash and preserve untouched backup; copy into scratch; upgrade with26.1.2; check IDs/contents/owner associations/recipes/configs; save, clean stop, restart and recheck. Also exercise existing1.20.1 save/reload and a staged1.20.1→1.21.1→26.1.2 copy if that content is represented. Unsupported cross-loader world conversion is not implicitly certified. Downgrade after vanilla file fixing is unsupported; never overwrite original or auto-delete files to make a test pass.

Publication acceptance: extend checker VERSIONS to target and explicit JVM major mapping17→61/21→65/25→69. Programmatically expect four libraries × three versions × three branches (common/Fabric/Forge-family) rather than leaving the checker at24 publications. Verify Testiarium core naming/capabilities, merged common source JARs, ordinary target Fabric jar versus old remapped jars, exact metadata expansion, access files and Mixin configs, no internal Gradle identities in POM/module deps, no test/CCT class/resource leakage into main artifacts, no new runtime transitives on old leaves. Add file-Maven consumers resolving each loader/common/capability combination, without remote publication or local-only artifacts in CI.

Active-state checks: final root builds/publications with1.20.1 active, then1.21.1 and26.1.2 active. Existing CI uses the literal `'Set active project to <version>'` task; parent evidence also contains `stonecutterSwitchTo<version>`. Discover which alias is present; do not guess. Finish on1.20.1 and compare authored source hashes plus controllers to canonical snapshot. Preserve intentional implementation changes; reject switch-only edits. If linked-worktree Spotless/JGit still cannot locate its ratchet, use an isolated standalone clone of the exact candidate with `origin/multiversion` and verify source hashes, as the accepted baseline did; never disable formatting.

CI must launch with Java25 and provision17/21/25 toolchains, exercise old/new graph and native report validation, retain Xvfb/runtime timeouts and scratch file redirection, upload actual evidence on failure. No credentials or remote publication in this validation. Run git diff --check and inspect scoped Git status before each local commit.

### Required completion report and review

Record exact Minecraft/dependency/toolchain pins actually resolved; cumulative SHA/branch; changed subsystems; architectural/API/storage/save decisions; every command, exit status, fresh XML identity/count/skip map; datagen diff; server ready/clean stop, client/title/world/render/multiplayer evidence; copied-save hashes/results; old-version consumer checks; publication identities/capabilities/test isolation; canonical restoration. Explicitly list disabled/deferred features (none currently authorized), failed/unperformed gates and remaining inherited risks. Update owning OpenWiki/coverage documentation only from verified implementation evidence.

Inspect t_16b4e35f's existing task graph before lifecycle completion/review. No duplicate implementation card and no blanket approval hold. Complete only when its own acceptance is met; an early compiling port is not success. This planning task's required separate LLM-Wiki report does not substitute for an implementation report or future runtime evidence.

## Planning acceptance cross-check

1. Fourteen pre-edit answers, exact pins and cumulative1.21.1→26.1 boundaries: brief and source ledger.
2. All five subprojects, loader/client/persistence/access/fixture/public API inventory: brief ownership table and milestones2–6.
3. Java25 launcher vs old targets, unobfuscated tooling, Stonecutter/overrides, transactions/saves/publication: brief strategies and milestones1/3/4/7.
4. Concrete ordered file boundaries, companion isolation/local-consumer strategy, blockers: milestones1–7; proposed paths labelled.
5. Per-node build/unit/native/datagen/server/client/copied-save/multiplayer/publication checks and restoration: milestone7.
6. Local cumulative documentation commit, exact SHA and wiki report are delivered through t_bd56f66a completion metadata to existing dependents. No application implementation or acceptance campaign was run by planning.
