# R2 — Broccolium native behavior, access and data parity

Status: INCOMPLETE; initial reconnaissance checkpoint. No R2 acceptance is claimed.

## Baseline and authorization

Execution release recorded on t_eb583016 supersedes planning-document holds. Worktree is isolated; no push, primary merge, release or remote publication is permitted.

Merged planner b8fd1ddfd5134c45402040fe2c1ca5be343ac48f, then cumulative R1 455ae9d22da32dd3467a687faeaae087aec36972 by fast-forward; both ancestry checks returned 0. Original brief, source ledger, remaining plan and R2 coverage rows read. Accepted exact pins and target-only observer signature remain unchanged. Old causal-position API remains required.

## Initial observations

- Target convention has only Broccolium private unit fixtures, no native storage testmod/run or datagen runs. Old Fabric storage fixture imports removed vanilla GameTest and FabricGameTest types and falls back to legacy ItemBridgeTestData without a target override.
- Target LevelChunkMixin redirects native removal from setBlockState; this needs actual replacement/unload/drop proof, not merely construction tests.
- Common access widener contains seven rules; packaging and transformed visibility still require checks on both loaders.
- Modern override inventory and BlockEntityTypeBuilder disposition remain to be recorded.

## Commands and evidence

Scratch logs: `${TMPDIR}/t_eb583016-validation/`.

- Both merge/ancestry commands: exit 0.
- `timeout --foreground 300s ./gradlew :broccolium:fabric:26.1.2:tasks --all :broccolium:forge:26.1.2:tasks --all --no-daemon --console=plain`: exit 0, discovery.log. No native tests executed by this command.
- execute_code batch read was denied before execution in unattended mode; ordinary supported file tools were used instead. No approval settings changed.

## Phase 1 — Native routes and first behavior proof (incomplete acceptance)

Both Broccolium-owned `runStorageGameTest` routes now execute without Tweakium. They consume R1's generic fixture feature privately. Target Fabric shares the existing conservation fixtures through version-selected annotation/container imports; the modern component-bearing stack helper has a target override. Forge has its own testmod, registrations and positive native capability tests. Both use no-skips guards.

Observed red/green:

- Fabric native startup failed on the common canonical `broccolium.accesswidener` shadowing the loader's official file (named vs official). Excluded only that obsolete target common resource; the next native run passed.
- Native `removedWorldChestIsNotExposed` executed and failed with its exact stale-inventory sentinel in native-red-removed.xml. Added the target item's removed-entity guard; subsequent native runs passed. The original red report also contains two fixture-precondition failures, retained honestly: native chest slot499 is required to create horse cargo, and a detached chunk needs its block state before accepting the entity. Neither was a production fix.
- Native exact block/nullable orientation, scheduled tick, same-block change vs replacement, one destroy after removal, retained cargo drops, root save/load/type ID, keyed block/item registration and complete horse cargo vs saddle now execute on both loaders. Native chunk-clear exercises the real unload cleanup on a task-owned detached chunk; it is NOT a disk unload/reload or process restart claim (R6).
- Positive native Forge item/fluid/energy providers, sides, simulation, bounds, nested fluid rollback, world-origin bucket replacement and unsupported reverse rejection pass. Positive Fabric fluid/energy, units, nested rollback and world-origin bucket replacement pass alongside original item conservation cases.

Latest run commands are `scripts/verification/run-gametest.sh :broccolium:{fabric,forge}:26.1.2:runStorageGameTest`: both exit0, logs ending iAxpdS and qyAalb. Earlier green phase reports are retained as fabric-phase1.xml (31 cases) and forge-phase1.xml (7 cases), zero failures/skips; newer provider counts will be reconciled in final evidence, not inferred here. Compiler/setup failures remain in scratch logs, not counted as behavioral reds.

Modern inventory in modern-overrides.txt: all target counterparts present except the package-private old Forge BlockEntityTypeBuilder. It is replaced by the target toolkit's native `BlockEntityType(factory, block)` constructor (ForgeInnerPlatformToolkit.kt:94), exercised by the registered world fixture on NeoForge. No removed Builder shim is needed. Model/data semantics and final release-access checks remain pending.

Next: registry/supplier codec and text/model proofs, negative bounds/removal coverage, owning unit matrix, deterministic Broccolium datagen twice per target loader and release-resource/bytecode checks. R2 remains incomplete.

## Ownership boundaries
## Phase 2 recovery checkpoint — acceptance remains incomplete

Owning nine-node unit campaign exited0: common/Fabric/Forge counts are 175/232/128 on1.20.1, 175/232/128 on1.21.1, 186/240/134 on26.1.2. Fresh versioned XML counts matched testcases with no skips/failures/errors (unit-receipt.json). Target common adds book component/raw page order, translation keys and native model collectors, material textures, all four direction rotations, five flat layers and invalid-layer rejection. Native client codec interface is initialized before its implementing record to avoid vanilla's circular static codec initialization in isolated JUnit; no production suppression.

Native world registered tooltip/lazy hooks and registry entry roundtrip/missing-key rejection execute. Supplier resolution is now measured on the registered block; constructing extra unregistered blocks at runtime is prohibited by frozen NeoForge registries. The original exploratory Forge failure (7zilVj) is retained; it is a fixture lifecycle error, not evidence that registry lookup failed. Registered fixture passed afterward (I2hrvB/tUC0lb).

World-origin NeoForge ItemAccess bucket nested commit/outer abort verifies exact custom-name/component-removal restoration using a test-only Java bridge to Kotlin-internal code. No bridge is published.

Broccolium datagen ran twice on each target loader: Fabric runDatagen and NeoForge runClientData/runServerData, all explicit300s Xvfb wrapper commands exit0. Four generated language JSON files have identical pass1/pass2 hashes (datagen-pass1.json/datagen-pass2.json); resources are generated, not hand-authored. Target JAR tasks exit0 (release-jars.log). Exact packaged access/bytecode and compiled-fixture exclusion inspection is still pending.

Old Fabric storage runs on1.20.1 and1.21.1 both exit0 (fWG6x5/hgK5xz), no skips. Latest target suites before the additional entity fixture passed (I2hrvB/tUC0lb). Additional complete player/minecart inventory fixture is being exercised; its first compile failed only on native mock-player deprecation under-Werror, now suppressed at that test-only native call. This is not a new behavioral pass until rerun.

Remaining acceptance before completion: parse final exact native inventories/reports, finish positive provider invalid-bound/removal/nullable-side coverage and sibling stale-provider audit; release seven-rule resources/transformed visibility and LimitedInventory bytecode checks; review/hash generated resources and modern overrides into durable evidence; formatting/source-hash checks; cumulative local commit and evidence attachment. Full copied-world/process/network/visual gates remain downstream, not waived. R2 must not complete or release R3 from this checkpoint.

Latest post-checkpoint execution: Fabric ZsUoaO exit0 with36 cases/zero skips. Forge ssxNKE exit1 with12 cases/one required failure in `NativeBehaviorGameTests.entityInventoriesExposeFirstAndLastNativeSlots` (generic Check failed). All other11 Forge cases pass. Added precise minecart/player assertion messages; these message-only edits are not yet rerun. Do not treat the earlier green11-case report as current acceptance. Investigate native entity capability precedence vs complete player inventory before choosing a production change; no cause is established yet.

checkpoint-receipt.json records exact four native reports including the failure, three target JAR hashes and packaged access text. Compiled testMod class identities are excluded from each owning release JAR. javap confirms target LimitedInventory is public/non-final. Visibility checks for every transformed native access target and common-fixture identity checks remain pending. Native receipts intentionally retain failing state.

Exact next command: scripts/verification/run-gametest.sh :broccolium:forge:26.1.2:runStorageGameTest, then read the new precise failing assertion and trace native entity capability/provider selection. Use existing work/evidence rather than repeat reconnaissance.

## Downstream boundaries

R3 plugins depend on this completed baseline. Full publications/consumers remain R5; production server/save/multiplayer R6; full client visuals R7. This checkpoint does not release downstream tasks.

Hotspots: gradle/minecraft-26-1.gradle.kts and gradle/gametest-reports.gradle.kts; only Broccolium-owned sections will change here.
