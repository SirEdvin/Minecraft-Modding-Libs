# Remaining Minecraft 26.1.2 migration plan

Status: PARTIAL HANDOFF — design and eight-card held chain created; required task-body restructuring is blocked by the installed API permission boundary. Planning only, 2026-10-17 Europe/Kyiv. Not approved or ready for execution.

Goal: turn the remaining additive migration into bounded, independently acceptable Kanban milestones while retaining one cumulative final artifact.

Architecture: retain the existing common/Fabric/Forge-family Stonecutter trees and exact 26.1.2 target. Split by owned behavior and proof, not individual helpers. Keep t_16b4e35f as final integration owner, with new prerequisite cards held for review.

Tech stack: Kotlin/Gradle/Stonecutter, Fabric/NeoForge, CC:Tweaked; existing target convention and maintained Java 17/21/25 nodes.

Ownership and authorization: this card t_936409e9 writes and locally commits only its own planning documents and constructs a non-dispatchable graph. No implementation, push, primary merge, release, public Maven publication, shared-service restart or profile setting changes. Future cards are Albina-owned with inherited model/provider/reasoning settings and local-only contracts. Chrono t_878aa68c retains personal reading-task ownership.

## Baseline, evidence and actual remaining work

Source root inspected read-only: `/home/siredvin/projects/Minecraft-Modding-Libs/.worktrees/t_16b4e35f`. Relative implementation paths below refer to that cumulative tree, not an assertion that they already exist in this planning checkout. Planning HEAD is `27798ce`; the cumulative implementation HEAD is `f347dfc7f5a794693711b75856d1df253866b24a`. Verified ancestors there: instructions `27798ce`, plan `9d282ebae09126bb9da1342f58ae4213de429836`, coverage `e69b7218f5bb077e49e76787168a596f49f26716`, OpenWiki `327f845797e24a3447f4f248f334dc77ebdd35dd`, audit `f6952bf23a4bcfa01ecf2e0d0640b8aef2977292`.

Read full original implementation body/comments/events/runs and existing Chrono edges through supported Kanban APIs; saved originals before edits. The final owner is blocked, with no current run/PID; run19's systemd scope is inactive/dead. Runs18/19 exhausted150 iterations. Earlier observer approval and the earlier memory repair remain resolved; neither justifies a new product hold or another infrastructure restart.

Live WIP has nine tracked modified files and48 untracked files, including32 authored Tweakium files. The rest includes Fabric/jqwik runtime state and must not enter commits. Hash/path manifests are saved in the scratch recovery directory identified below, not yet attached. No source content was copied into this planning branch. The nine tracked changes include the target convention, both old NBTUtil variants, a shared LongTag assertion, and target data/registration/representation helpers.

Saved exact versioned XML was parsed now, with declared counts matching testcase elements. These are inherited executions, not tests rerun by this planner:

| Scope | Saved cases / XML | Failures / errors / skips | Limit |
| --- | --- | --- | --- |
| Broccolium target common | 182 / 12 | 0 / 0 / 0 | Vanilla construction fixture, not world dispatch |
| Broccolium target Fabric | 240 / 11 | 0 / 0 / 0 | Knot transformations, not real server startup |
| Broccolium target NeoForge | 134 / 7 | 0 / 0 / 0 | FML/WorldLoader data bootstrap, not tick/world acceptance |
| Tweakium target common | 31 / 5 | 0 / 0 / 0 | Missing modern data suites and native CC/live-player proof |
| Tweakium 1.20.1 common latest retry | 14 / 3 | 0 / 0 / 0 | Bounded common tests only |
| Tweakium 1.21.1 latest retry | no XML | not executed | Real compile failure, NOT a pass |

Important correction to the last comment: `run19-tweakium-legacy-tests-2.log:125-149` shows that both redundant String.toString edits were already made; 1.20.1 tests executed, then 1.21.1 failed at `projects/tweakium/versions/1.21.1/src/main/kotlin/site/siredvin/tweakium/modules/peripheral/util/DataStorageUtil.kt:183` (`NbtUtils.loadUUID(null)`, Java nullability warning under -Werror). Fix that precise compatibility issue without changing missing/invalid UUID behavior; do not simply rerun the obsolete NBTUtil action or disable warnings.

The original checkpoint document ends before the last run19 work. Reference both its chronology and later comments/logs. Accepted old coverage remains historical (1,116 JUnit invocations per active state;177 native passes and12 intentional skips), not current full-matrix evidence after KGP changes.

Source-grounded remaining inventory:

- `gradle/minecraft-26-1.gradle.kts:129-191` has partial private-fixture wiring, a NeoForge tested-mod model containing fixtures, merged binary output and disabled target publishing. It lacks complete production runs, Testiarium feature/source artifacts and target publication contracts. Root CI still launches Java21 and checks only old active states; `scripts/check-publications.py:14,87` still knows two versions/class majors.
- Broccolium has target counterparts for all37 modern common overrides. Target Fabric lacks the modern ItemBridgeTestData override; target NeoForge lacks modern testmod entrypoint/metadata. A missing old BlockEntityTypeBuilder counterpart is an audit item, not automatic proof it must be copied. Seven target access rules and LimitedInventory openness were already authored; native release/horse/model/observer/removal/persistence proof is not done.
- Tweakium target common has63 authored overrides, partly uncommitted. Eight modern paths lack target counterparts, notably DataStorageTests, UpgradeDataStorageTests, CreativeFillerGameTests, UpgradeBehaviorGameTests and WorldReloadFixture. Fabric has zero target overrides versus seven modern; Forge-family zero versus13. A target file count is not semantic parity proof.
- Peripheralium has no target overrides; modern inventory is nine common, nine Fabric and13 Forge-family paths, including generated assets. Port providers and generate, never copy-edit generated JSON. Preserve the intentional modern absence of legacy blend.
- Testiarium has no target overrides versus14 common, three Fabric and eight Forge-family. Existing registration directly calls GameTestRegistry; fail-fast/CCT paths access parent/context/computerQueueSize/workerCount/idleWorkers. Its registry harness is a prerequisite to native gameplay acceptance, not a final afterthought.
- typed-peripheral-api stays independent of Stonecutter. Run contracts and Lua correlation; change declarations only for a demonstrated API delta.

Evidence provenance: original `docs/minecraft-26-1-sources.md` and its attached `t_bd56f66a-planning-evidence.zip` retain pinned primary-source hashes/revisions; no release-pin research was restarted. This plan adds local source/Git/XML/board evidence, not new compatibility claims. `board-before.json`, `t_16b4e35f-original.json`, `wip-manifest.json`, `override-inventory.json` and `inherited-xml.json` are saved in this task's scratch recovery directory; the latest logs remain in the original implementation evidence directory. Scratch cache alone is not durable evidence; preservation/promotion remains in the recovery handoff.

## Settled decisions and shared worker contract

1. Keep exact26.1.2 Fabric/NeoForge additions, old1.20.1 Forge/Fabric and1.21.1 NeoForge/Fabric; canonical1.20.1; four libraries and independent TypeScript. Keep site.siredvin coordinates, version ownership and no release bump. No net.minecraft shims, permanent regex rewriting, new persistence ownership or integration removal.
2. Keep accepted pins: Stonecutter0.9.8, Gradle9.5.0, Loom1.17.11 non-remapping target only, MDG2.0.142, KGP2.4.0; target NeoForge26.1.2.122, Fabric Loader0.19.5/API0.155.3+26.1.2, CC1.120.0 using the `-forge` coordinate, FLK1.13.12+kotlin.2.4.0, KFF6.4.0, energy5.0.0. Old runtime/language/API2.0/JVM17/21 remain isolated; target JVM25. Do not replace selected pins with today's global latest.
3. Approved target observer callback is `onNeighbourChange(neighbourBlock: Block, orientation: Orientation?)`; forward actual nullable orientation, never invent a position. Old callback stays `onNeighbourChange(neighbour: BlockPos)`. No repeat approval for this scoped change.
4. Runtime values remain ItemStack; target CC definitions use immutable ItemStackTemplate with registry-aware resolution. Retain `__patch_storage__`, CUSTOM_DATA, legacy storedData, peripheralData, ownerProfile, component removals/unrelated patches and no-write/no-notification on encode failure. Native transactions must conserve variants/counts and replace container origin slots; unsupported reverse backings reject before mutation. Callback effects are not claimed transactional.
5. Use repository-owned target build convention. Do not extract a new toolkit merely for cleanliness. Companion changes, if concretely required, use separate isolated worktrees at TemplateProject `f0c84163b06a86d83a8481e15b8968416888110d`, modding-buildenv `55b844ff7a6ef2c08244378e4e72b44bac3b4b81`, minecraft-modding-skills `734962deb8d23abf5363a4b6453dc7b157a98ce6`; verify live ancestry. Use companion wrapper and scratch file Maven/init-script consumers. Never require unpublished local coordinates in committed CI.
6. Every new card is explicitly blocked for planning review, Albina-owned, project-linked isolated worktree, local-only, NULL model/provider/reasoning overrides. No implementation until user approval releases holds. Dependencies alone never authorize release or transfer Git changes. Existing final/Chrono owners and notifications remain intact.
7. After release, every worker reads this plan and original brief/ledger/coverage; merges the verified planner commit plus immediate predecessor cumulative SHA, and verifies all earlier ancestors. R0 additionally imports the frozen WIP as described below. Never cherry-pick only a convenient helper and omit parent changes.
8. Before behavior edits, add an owning regression, execute red, implement minimally, execute green plus adjacent old-node checks. A compiler error is not a behavioral red test. Keep warnings, validation, permissions, data conservation and physical-client isolation. Use actual dependencies/bytecode for signatures.
9. Once per coherent phase, update a proposed `docs/verification/minecraft-26-1-Rn.md` and post a checkpoint with exact SHA/dirty paths, commands, exit codes, report identities/counts, limits and next command. Do not have all workers append competing diaries to the final report. Three phases per capability (contract/port, native proof, bounded acceptance), checkpoint before roughly100 calls and preserve another before the150 cap. These are sizing heuristics, not guarantees or permission to call unfinished work done.
10. All Gradle commands use the wrapper, `--no-daemon`, explicit deadlines and scratch logs. Parse only fresh versioned XML, reconcile tests and declared counts, retain real failed runs, reject zero-test/skip-only results. Negative fixtures retain exact names/sentinels; storage skips are forbidden. Inspect live worker memory limit before heavy decompile; do not bypass containment or restart shared services.
11. This is one serial dependency chain deliberately: the target convention, report guard and fixture classpaths are hotspots. Each owner changes only its module/run section; no simultaneous root/catalog/controller rewrites. `hotspot: gradle/minecraft-26-1.gradle.kts` and `hotspot: gradle/gametest-reports.gradle.kts` must be carried in handoffs. Serial execution avoids collisions, not a justification for a generic framework rewrite.
12. New cards finish only their local acceptance and hand off cumulative commits, source hashes, exact commands/reports and residual downstream gates. They do not owe final OpenWiki/skills/Obsidian reports; F owns those once. Do not create personal reading tasks or duplicate the existing Chrono card.

Initialize `LOG_DIR="${TMPDIR:?}/$HERMES_KANBAN_TASK-validation"; mkdir -p "$LOG_DIR"` and select the verified Java25 launcher (observed path `/home/siredvin/.gradle/jdks/eclipse_adoptium-25-amd64-linux.2`; recheck availability) while retaining Java17/21 toolchains. Suggested deadlines are240–300s for focused checks,900s for native batches and an explicit measured bound for full builds; an elapsed deadline is a failed/incomplete check, never a pass. Preserve shell exit status with redirected logs. Do not issue broad clean operations in another owner's worktree.

## Graph and why eight new cards

Labels R0–R7 resolve to real IDs in the adjacent task-graph JSON. Every new card also depends on this planner; original completed edges OpenWiki→coverage→original plan→F are retained. The created implementation chain is:

    planner → R0 → R1 → R2 → R3 → R4 → R5 → R6 → R7 → F(t_16b4e35f) → Chrono(t_878aa68c)

| Label | Created task |
| --- | --- |
| R0 | t_a544bb9d |
| R1 | t_728ae4df |
| R2 | t_eb583016 |
| R3 | t_ebab5372 |
| R4 | t_d2dea3b5 |
| R5 | t_f0a99e8c |
| R6 | t_997f2d4d |
| R7 | t_14e20aca |

R0 isolates WIP custody and the demonstrated compiler break. R1 establishes a working native test capability before more gameplay ports. R2 closes the mostly compiled base library. R3 completes the already-started higher-risk state/player capability. R4 is a cohesive dependent content/data port. R5 delivers usable consumable artifacts/tooling and full build checks. R6/R7 separate server/save/security behavior from client/model/integration behavior, each with fixes and fixtures, not empty test-only tickets. F consolidates receipts and required documentation rather than repeating all implementation. This is eight new cards plus the existing final owner, not eight duplicate campaigns or a card per helper. The original monolith mixed all these feedback loops into every150-iteration attempt.

## R0 — Immutable WIP handoff and compatible build foundation

Objective: give every later worker an immutable cumulative source baseline and a working old/target compiler boundary.

Existing files: settings/root/catalog/properties and `gradle/minecraft-26-1.gradle.kts`; exact WIP manifest paths; both old NBTUtil files; modern DataStorageUtil; shared PeripheralContractTests. Proposed checkpoint: `docs/verification/minecraft-26-1-R0.md`.

1. After explicit release, verify F still blocked/inactive, compare live HEAD/status/content hashes to the planning manifest. Do not modify or commit F's worktree. If it changed, inventory the delta and obtain a coherent new snapshot, never guess which files to discard.
2. In R0's own isolated worktree, merge f347dfc and the exact planning completion SHA. Export tracked patch and explicit authored untracked files read-only from F; apply/copy only reviewed source paths into R0, excluding runtime `.fabric`, jqwik, build/generated outputs and credentials. Record pre/post hashes and verify F remains byte-for-byte unchanged. Commit this as an explicitly incomplete WIP checkpoint before repairs. This transfer is a future reviewed action, not performed by the planner.
3. Reproduce the1.21.1 DataStorageUtil nullability failure and add/retain missing/invalid UUID contract tests. Repair minimally while preserving exceptions/default semantics; retain both existing NBTUtil no-op cleanups and the stronger LongTag assertion. Do not suppress all warnings or change old APIs.
4. Verify target graph/dependencies, source override precedence, old stdlib/Kotlin metadata/JVM isolation and selected config dependency necessity. Generalize only essential private unit-fixture/common-output plumbing needed by later ports; reserve Testiarium features/runners for R1 and production packaging for R5. Preserve target publication disablement until R5.
5. Execute fresh Tweakium common tests on1.20.1/1.21.1/26.1.2 and existing Broccolium target unit/loader-aware suites; verify current source snapshot, representative old Kotlin2.0 consumers and bytecode majors. Reuse valid72-classpath/minimal consumer evidence where unchanged, but rerun affected paths after fixes. No full runtime campaign here.

Suggested first command after transfer: `timeout --foreground 300s ./gradlew :tweakium:1.21.1:cleanTest :tweakium:1.21.1:test --no-daemon > "$LOG_DIR/legacy-common.log" 2>&1`. Expected initially red at the known compiler site; subsequent green is future evidence. Acceptance: immutable source custody receipt, fresh focused tests, exact ancestry, compiler/runtime boundaries intact. Later module/native/publication gates remain downstream.

## R1 — Executable Testiarium registry/CCT harness

Objective: make target native tests genuinely executable on both loaders, with reliable fail-fast/reporting and separated fixture features.

Existing source: `projects/testiarium/src/main/kotlin/site/siredvin/testiarium/{Testiarium.kt,api/GameTests.kt,api/Assertions.kt,report/TestReporters.kt}`; modern overrides; `src/testMod`, `src/cctTestMod`, loader build scripts/metadata. Existing shared build anchors: target convention and `gradle/gametest-reports.gradle.kts`. Proposed target overrides under matching common/Fabric/forge `versions/26.1.2/src`.

1. Port registration to native function/instance/environment registries while preserving facade, groups, tags, deduplication, constructor/reflection errors and metadata. Inspect target sources; do not retain removed GameTestRegistry calls via shims.
2. Port real sequence fail-fast and CCT scheduler/reflection hooks with outer observers for assertion/exception/assertion-error, single execution and delayed success. Port target test structures/environment setup and teardown; preserve testMod vs cctTestMod capabilities and client-only helpers/Mixins.
3. Configure target common/loader features and native/CCT/client harness tasks with production mod models separate from private fixtures. Provide reusable bounded runner wiring for later owning modules, without requiring unported Tweakium/Peripheralium to run Testiarium itself. Keep ordinary production dedicated-server startup distinct from Fabric Testiarium runServer.
4. Preserve fresh-report deletion, exact expected negative fixture identities/messages and no-zero-test acceptance. Prove discovery exactly once and guard failure for stale/empty/failed reports; retain DOCTYPE rejection.
5. Run target standalone native and real CCT Lua suites on both loaders and target client harness via Xvfb; rerun affected old standalone/registration tests. This proves harness behavior, not downstream visual/gameplay acceptance.

Expected target task contracts, proposed until created: `:testiarium:fabric:26.1.2:runServer`, `:testiarium:forge:26.1.2:runGameTestServer`, Fabric `runCctGameTest`, Forge `runGameTestServer -Ptestiarium.cct`, both `runClientGameTest`. Verify exact names with `tasks --all`; native deadline900s, client300s. Acceptance: source/fixture port plus actual nonempty native/CCT/client reports and skip map; hand off task/report matrix. No full content/client integration or publication campaign.

## R2 — Broccolium native behavior, access and data parity

Objective: close base-library behavior gaps left by passing construction/loader-unit fixtures.

Existing target source anchors: `projects/broccolium/versions/26.1.2/src/main` base blocks/entities/codecs, `modules/data`, `modules/platform`, lookup/storage, LevelChunkMixin; common/Fabric wideners, Forge AT; loader native testmods. Target counterpart paths derive from the modern inventory, not canonical fallback assumptions.

1. Audit every modern override; classify the old BlockEntityTypeBuilder as replaced or still required with evidence. Verify LimitedInventory remains open and all seven common access rules reach both packaged loaders.
2. Add native observer/removal tests: exact block/nullable orientation, same-block state change vs replacement, one destroy after native removal, retained drops, no destruction on unload; preserve old causal-position APIs. Exercise horse cargo/equipment distinction and complete entity inventories in real worlds.
3. Exercise MutableNBTBlockEntity/root payload and registry codecs (supplier resolution included), book/tooltip/translation behavior, model collector direction/rotations/materials and key-bearing registration. Retain encode-failure no-mutation and component-removal invariants.
4. Expand positive item/fluid/energy provider tests including native ItemAccess bucket replacement, nested rollback, bounds/units, removed entities, nullable sides and unsupported reverse backing rejection. Existing unit results are evidence to preserve, not substitute for world hooks.
5. Port Fabric storage fixture; add a target Forge-family Broccolium-owned native storage runner (proposed `runStorageGameTest`) using R1, rather than depending on unported Tweakium. Update guard map with no-skips. Run both target suites, owning unit suites on all versions and Broccolium datagen twice on each target loader with reviewed resources.

Acceptance: real native dispatch/conservation/access plus deterministic owning datagen and old regressions; record exact release-access bytecode/resource checks. R3 must inherit this baseline before its plugin tests. General menu multiplayer and full release assembly remain R6/R5.

## R3 — Tweakium live peripheral, fake-player and upgrade capability

Objective: finish common/loader behavior and prove stateful peripherals operate through real CC/live-player APIs.

Existing common WIP: target `modules/{player,plugins,platform,pocket,turtle,data,minecraft,peripheral}` including FakePlayerSessionUtil/FakePlayerStorageSession and FacingBlockTurtleModeller. Existing tests: modern DataStorageTests/UpgradeDataStorageTests and common testmod UpgradeBehaviorGameTests/WorldBehaviorGameTests/RuntimeBehaviorGameTests/ModuleBehaviorGameTests. Proposed target Fabric/forge override trees correspond to modern adapters, fake players, entrypoints, EnergyPlugin and metadata/access/Mixins.

1. Audit all modern gaps, port both loaders, preserve target UpgradeType registration and public spellings (including PockerUpgradePeripheralBuilder), server ownership/protection/cancellation and single native interaction. Register model codecs/client entrypoints and port upgrade datagen/resources; R7 owns full visual integration acceptance, not basic compilation.
2. Bring modern data suites to target. Prove immutable template/default independence, removal/unrelated patches, valid registry resolution, owner/side settings, failed encode no write/notification, actual install/remove/reinstall and chunk unload/reload. Preserve old storedData/custom keys and unchanged old APIs.
3. Native fake-player cases must use selected turtle slots0,8,9,15 with hotbar swap/remap rather than clamping. Verify storage loans/remainders, changed/replaced tools, previous permanent attributes, owner/drop capture cleanup and exception/finally paths on success and failure; no duplicate/drop/discard. Helper SimpleContainer tests alone are insufficient.
4. Exercise plugin item/fluid/energy query and transfer, destination validation, amount/overflow/malformed predicate refusal, operation payment/cooldown and permissions before mutation; compare real Lua results to public API.
5. Execute fresh common/loader units and both target native Tweakium runners via R1, targeted old upgrade/player/plugin regressions, and Tweakium datagen twice per target loader. Keep its own fixtures here; Peripheralium fixtures join only in R4.

Expected tasks after port: `:tweakium:26.1.2:test`, `:tweakium:fabric:26.1.2:test`, `:tweakium:forge:26.1.2:test`, Fabric `runPeripheralGameTest`, Forge `runGameTestServer` (900s). Acceptance is working native capability and preserved contracts; true process save/restart, copied upgrades, adversarial multiplayer and full client views are R6/R7, not falsely passed by this card.

## R4 — Peripheralium content and generated-resource parity

Objective: deliver the dependent content library with correct registrations, resources and native behavior.

Existing anchors: `projects/peripheralium/src/main/kotlin/site/siredvin/peripheralium/{PeripheraliumCore.kt,PeripheraliumPlatform.kt,Items.kt,Blocks.kt,data/}` and modern overrides; Fabric/forge entrypoints/metadata/access; `src/testMod/.../{PeripheraliumBehaviorGameTests,RecipeContractAssertions,RecipeAssertions}.kt`. Proposed target source/metadata/provider overrides and generated outputs in matching26.1.2 trees.

1. Port modern keyed content registration and native loader lifecycle, creative tabs and resource identities. Retain modern intentional lack of legacy blend; do not accidentally seed target from legacy Items/recipe implementation.
2. Adapt providers for item definitions/models, recipe/display/loot codecs, tags and EN/UA language completeness. Resolve actual Fabric data and NeoForge client/server data tasks; generate through providers twice, inspect deterministic diffs and no cross-version leakage.
3. Port and run Peripheralium-owned behavior/recipe fixtures through Tweakium's target native runners using test-only dependencies. No reverse production dependency/cycle; verify loaded content/recipes/loot, not only JSON existence.
4. Build all three target branches, exercise target loaded content on both loaders and affected old content/resource regressions. Preserve public IDs and all metadata/client gates.

Acceptance: usable registered content, native recipe/loot proof, deterministic resources and clean dependency boundaries. Full artifact matrix belongs R5; visual appearance R7. No unrelated plugin redesign.

## R5 — Reproducible artifacts, consumers and CI matrix

Objective: produce all-version consumable libraries and locally verify the expanded build/publication contract.

Existing files: target convention, root/settings/catalog/properties, four controllers/twelve leaf scripts as needed, `scripts/check-publications.py`, `.github/workflows/ci.yml`, TypeScript Gradle bridge/package/tests. Keep changes surgical; this is not a tooling rewrite.

1. Complete target Maven publications and source merging, Testiarium core/test-mod/cct-test-mod capabilities, raw classifiers where already public, dependency identity normalization, incremental metadata expansion and test-only isolation. Remove bootstrap disablement only for verified local file-Maven acceptance; never publish remotely or rely on mavenLocal in committed CI.
2. Expand checker to all36 publications and explicit majors61/65/69; verify actual jars/POM/module deps/metadata/AW/AT/Mixins/nested deps/source jars. Old remapped outputs and target ordinary jars differ intentionally. No internal site.siredvin.* groups or private fixtures in main artifacts.
3. Build file-Maven Kotlin/Java extension consumers for common and each loader/capability, covering platform/storage/peripheral and CC APIs; real old Kotlin2.0 consumers prove metadata/runtime isolation. Use scratch init configuration, not unpublished companion toolkit pins. Resolve required config/integration dependency paths with evidence, not guessed artifacts.
4. Execute complete per-version build/unit matrix and root builds/publications for active1.20.1,1.21.1,26.1.2; record nonempty exact reports and counts. Verify datagen for every owning provider/library on both loaders at all three versions: old resources have no unintended diff and target generation is deterministic with no cross-node leakage. Reuse same-source R2–R4 target receipts where valid. Run `:typed-peripheral-api:check`, correlate declarations with R3 Lua behavior and update only demonstrated deltas.
5. Update CI to Java25 launcher plus17/21/25 toolchains, all active states, native report guards/task maps, timeouts/Xvfb and failure evidence. Validate locally; do not claim remote CI ran without push. Use exact-source standalone checkout for the known JGit/Spotless linked-worktree ratchet issue if necessary; never disable lint. Restore canonical1.20.1 with source-hash comparison.

Future commands: `./gradlew cleanTest build generatePomFileForMavenPublication generateMetadataFileForMavenPublication --no-daemon`, `python3 scripts/check-publications.py`, `./gradlew :typed-peripheral-api:check --no-daemon`; apply explicit timeout/log wrappers and repeat active states using discovered switch tasks. Acceptance: all36 local publication contracts and consumers, fresh full build/unit evidence, reproducible candidate artifacts/checksums, CI definition and canonical restoration. No remote publication or claim of full runtime acceptance yet.

## R6 — Dedicated-server, save and network compatibility

Objective: make the cumulative artifact safe in real world/server lifecycles and adversarial multiplayer paths, with reproducible fixtures.

Existing anchors: Tweakium modern WorldReloadFixture and settings/upgrades, Broccolium MutableNBTBlockEntity/MenuBuilder/SavingFunction and Fabric menu codec/platform, R1 native runners. Proposed `scripts/verification/` helpers and owning testmod fixtures are allowed only as small reproducible checks, not a general automation framework.

1. Use R5 artifact hashes/source SHA in scratch production consumer/server assemblies without test-only dependencies. For every supported loader/version, observe normal ready, issue stop and require clean shutdown/save. GameTest exit is not production readiness; timeout kill is not clean stop. Respect existing EULA authorization; if not present, obtain it rather than silently accepting legal terms.
2. Execute complete existing/native matrix for all versions, including target Broccolium Forge storage route and all required Tweakium/Testiarium observers. Preserve exact negative identities and zero storage skips. R1–R4 prove capability-local cases; this verifies the assembled candidate.
3. For each target loader, build representative1.21.1 saves containing registered content, configured blocks/pockets/turtles, component removals, owner/side associations and item/fluid/energy data. Clean stop, hash untouched backup, upgrade a scratch copy to26.1.2, verify/save/stop/restart/recheck. Exercise old1.20.1 save/reload and staged1.20.1→1.21.1→26.1.2 copy when that content is represented. Never overwrite originals, claim cross-loader conversion, promise downgrade or auto-delete migration files.
4. Implement and execute server-side malformed/truncated/oversized menu codec and unauthorized actions: player/menu identity, distance, positions/counts, stale/removal state, no mutation on rejection, buffer lifetime, threading. Use two actual clients for simultaneous interactions on each target loader; retain test identities/receipts. No fabricated client messages or mocked networking acceptance.
5. Fix root causes in owning source with focused red/green tests, then rerun affected old and artifact checks. If required real runtime/credentials are unavailable, report an actionable external blocker and save partial evidence; do not waive the gate.

Acceptance: ready/stop/save/restart receipts, copied-save hashes and real multiplayer rejection/conservation evidence across required nodes, plus reproducible fixtures. R7 owns title/world/render/optional-mod UI matrix. New source changes invalidate affected R5 evidence and require refreshed artifacts before handoff.

## R7 — Client models, interactions and integration variants

Objective: complete universal client-facing behavior and required optional integration combinations against the same cumulative artifacts.

Existing source anchors: Tweakium target FacingBlockTurtleModeller and RML, loader client entrypoints/model registration; Peripheralium generated client items; Broccolium menus/tooltips/book handling; Testiarium client/CCT helpers. Use loader/version-specific typed adapters, never live mutable world reads during render submission.

1. Launch both target clients to title, create/join a world, inspect all content/creative tabs/tooltips/books/models and equipped pocket/turtle state, open/interact with menus, resource-reload and reconnect to dedicated servers. Verify render extraction/submission/material contracts and no common early client-class linkage. Headless uses Xvfb, explicit timeouts and real screenshots outside tracked source.
2. Repeat target Fabric with/without REI26.1.819 and ModMenu18.0.2, NeoForge with/without JEI29.47.0.111; verify required CC, Kotlin and positive energy providers. Resolve config-port26.1.5 only where actual dependency/feature usage requires it; no silent removal or fake integration success. R0/R5 establish dependency necessity/resolution; this card establishes runtime combinations.
3. Run both old-version client smoke/harness matrices and representative same-version server joins; use real Lua/client behavior to expose declaration mismatches, preserving TypeScript contracts. R6 already owns full adversarial network acceptance; reference its exact candidate receipt and rerun affected cases if changed.
4. Fix observed rendering/interaction/adapter issues with focused regressions and replay affected datagen/artifact/native/server gates. Recheck ordinary production artifacts exclude fixtures and can launch on dedicated servers after client changes.

Future commands: verified `:testiarium:{fabric,forge}:$v:runClientGameTest` through `timeout --foreground 300s xvfb-run --auto-servernum ./gradlew ... --no-daemon`; actual title/world/multiplayer visuals need separate evidence, not just harness XML. Acceptance: client/integration combination table with exact mods/artifact hashes, logs/screenshots and honest failures/manual limits. No required feature is silently deferred. Produce final candidate receipt used by F.

## F — Existing final cumulative owner, not another implementation monolith

Keep `t_16b4e35f`, completed original parent and Chrono child. Preserve original seven acceptance paragraphs and report instructions verbatim as campaign background, with this graph superseding only the old monolithic execution/no-new-cards wording and obsolete initial-hold wording. It stays blocked for planning review even when this planner completes.

After all prerequisites and explicit release: compare original WIP to R0 transfer receipt. The original owner may locally commit its own unchanged authored WIP as an explicitly incomplete checkpoint, preserving runtime state; if divergent, reconcile deliberately. Merge R7's verified cumulative head plus planner SHA without reset/clean/discard. Verify original plan/coverage/OpenWiki/instruction ancestry and every stage commit. Final integration must not strand the copied WIP or helper heads.

F checks each atomic coverage-map receipt against final source/artifact hashes. Reuse same-source execution evidence; rerun affected gates after any source change. It is accountable for overall A1–A7 acceptance as receipt gates, not for reimplementing each subsystem. Unmet required runtime checks prevent campaign completion; no new automatic product decisions. Final matrix must cover all versions/branches/active states, datagen determinism, dedicated/server/client/save/network/integration, consumers/publication and canonical restoration.

F alone updates `docs/minecraft-26-1-verification.md`, migration brief/decision notes, `docs/module-test-coverage.md`, source-grounded OpenWiki via begin/plan/page/finish lifecycle and checks claims/links/hashes, and appropriate migration/toolkit skill guidance. Skill guidance changes belong in the separately isolated minecraft-modding-skills companion worktree with exact SHA, not another profile's live skills. Do not enable new scheduled automation as an OpenWiki side effect. List exact supported patches, observer migration, unchanged old APIs, copied-save/downgrade limits, unperformed manual checks and residual inherited risks.

F alone finalizes the existing200–400-word Obsidian report at `/home/siredvin/vault/Knowledge/LLM-Wiki/minecraft-modding-libs-progress/minecraft-26-1-implementation.md`, after reading live schema/index/log and llm-wiki shared maintenance reference. Preserve exact Project/Minecraftmoddinglibs tag, type:summary, tool-derived Kyiv date, project link, overview/index recount/log and unrelated edits. Record llm_wiki_report/link metadata and resolving links; local save is not sync evidence. No other new report/reading-task owner is created. Chrono keeps its four original parents/reports and personal task semantics.

Final handoff: one cumulative local branch/commit; all companion SHAs or explicit unchanged status; actual artifact paths/checksums; source/command/report/skip receipts; canonical1.20.1 restoration; final OpenWiki lifecycle and Obsidian report. No push, primary merge, release, public Maven publication or shared-service/settings edits.

## Acceptance ownership and verification of this plan

Prepared `coverage-candidate.json` in the planning evidence directory maps every original implementation A1–A7, every numbered original milestone step, each brief pre-edit decision, the original planning checklist, final matrix/report prose gates and remaining subsystem obligations to one accountable owner or explicit final receipt gate. It has204 rows:115 original plan lines,30 original body lines,14 decisions,38 stage steps and seven explicit final gates. Compound original obligations retain F as aggregate gate while atomic implementation responsibilities belong to R0–R7; evidence providers are not additional accountable owners. No partial work is marked done by this map. Promotion to the intended adjacent coverage JSON is pending authorized completion of this handoff; do not mistake the proposed filename for a committed file.

Adjacent `2026-10-17-minecraft-26-1-task-graph.json` records exact card IDs and verified held-graph facts. The validation script/readback checks coverage key equality/unique ownership, cycles, terminal ancestry, preserved original parents/Chrono fields, NULL overrides, blocked holds, no child runs/PIDs, unique workspaces and unchanged original bodies. It reports desired expanded-body alignment separately as NOT APPLIED. Only planning artifacts are committed here; no Gradle/runtime/build matrix is run for Markdown changes.

## Actual blocker and safe resume

Native kanban_create/link operations succeeded. The supported installed `kanban_db.edit_task` call rejected the first body edit with `PermissionError: delegate_task child contexts cannot mutate Kanban tasks or boards`, at the fail-closed write transaction guard. Readback verified all nine original bodies unchanged and all eight new cards plus F still blocked/no current runs. No guard/environment/approval policy was changed and no alternate mutation path was attempted. Deferred tool discovery found no task-edit tool.

The separate execute_code file-packaging call was also rejected in unattended mode before executing any tool; no result from it is claimed. Do not relax unattended approval settings to finish this task.

Recovery directory: `/home/siredvin/.hermes/profiles/albina/cache/scratch/t_936409e9/`, including full original card/board snapshots, exact proposed `expected-bodies.json`, coverage candidate, manifests and prepare/apply scripts. Scratch has finite retention; preserve this directory before expiry. An authorized coordinator with a supported body-edit surface must apply/review the prepared nine bodies (especially F's final-integration-local opening), preserve control fields, rerun graph/body/default checks and complete this same planner. Do not recreate cards or unblock implementation. Original acceptance and report text is retained verbatim in the proposed final body. This is an actionable capability blocker, not migration failure or a request to reapprove the observer API.

Approval release procedure for a coordinator: inspect the held graph/plan, obtain user approval once, record superseding release authorization on all held bodies, then remove all review holds (descendants first, roots last), retaining dependency gating and NULL overrides. Do not duplicate cards, increase profile limits or silently start F. A blocked review hold is intentional, not a new failed engineering attempt.
