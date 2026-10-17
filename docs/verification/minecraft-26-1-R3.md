# R3 — Tweakium live peripheral and upgrade behavior

Status: INCOMPLETE. This document is a recovery checkpoint, not acceptance.

## Baseline and scope

Execution release on t_ebab5372 supersedes the historical planning holds. Fast-forwarded planner b8fd1ddfd5134c45402040fe2c1ca5be343ac48f, then cumulative R2 aacc2d955b185d813fd0f9781ad3e1ccf80e56c7; both ancestry checks exit0. Original brief, source ledger, remaining plan and ownership map inspected. Exact 26.1.2 pins and old version/API boundaries remain settled. No push, primary merge, release, remote publication or shared-service changes authorized.

## Initial bounded investigation

Logs: `${TMPDIR}/t_ebab5372-validation/`. Launcher: verified Temurin25.0.3+9; worker cgroup memory.max8589934592. Java17/21 toolchains retained.

`timeout --foreground 300s ./gradlew :tweakium:26.1.2:test :tweakium:fabric:26.1.2:compileKotlin :tweakium:forge:26.1.2:compileKotlin --no-daemon --console=plain` exited1, initial-compile.log. Common production compilation works; loader compilation falls back to obsolete canonical/modern signatures because neither target loader has overrides. This is a compiler baseline, NOT a behavioral red. Missing modern data tests and native fixture/run wiring remain unverified.

Pinned CC bytecode inspected: UpgradeManager.get accepts registries plus stack; target pocket components are BACK_POCKET_UPGRADE/BOTTOM_POCKET_UPGRADE; DataComponentUtil.createStack remains available. Native NeoForge sources confirm transactional EnergyHandler long amounts/int transfer bounds. No speculative shim or release-pin changes needed.

## Next actions

## Phase 3 checkpoint — capability green, final bounded campaign pending

Both target native routes now pass30 cases each (29 owning+1 vanilla), zero failures/skips, in native-lua-green-{fabric,forge}.log. Actual native minecart inventory loans/remainders, exception/finally cleanup, selected turtles, loader protection/veto and one native interaction, live pocket/turtle upgrades, same-process disk chunk reload and real CC Lua plugin item/fluid/energy/payment/cooldown/permission tests execute. Mock player network configuration is test-only; fixtures remove/disconnect players and release task-owned chunks. Real socket negotiation/process restart remain R6.

Real Lua red native-red-lua-overflow.xml: CC's generated Optional<Int> coercion saturated1e100 and transferred items instead of refusing. Target wrappers now validate raw Lua integer bounds before generated coercion, retaining existing Kotlin/Java method signatures and Lua spellings; the sibling item/energy paths use the same explicit boundary. Real Lua red native-red-lua-fluid.xml: infinity mutated native buckets; target fluid methods reject nonfinite/negative limits before lookup/transfer. Final Lua assertions compare actual counts, bucket variants, energy conservation, successful payment once, insufficient-fuel/permission refusal and cooldown with native state. Old APIs/runtime lines retain their existing signatures.

Owning production EN/UA datagen and private upgrade/model datagen are implemented. Production contains no upgrades to publish; private fixtures exercise both public upgrade bootstraps and left/right model helpers. Datagen exposed two previously unexercised Broccolium target sink bugs: Fabric assumed a dimension registry in every patch and filtered by the output mod instead of the requested namespace; NeoForge's server pass deleted prior client models because skipped model providers were not registered with HashCache. Concrete missing-output/failing runs retained. Corrected those adapter boundaries, without disabling validation or exporting fixture assets in release artifacts.

datagen-4.log exit0 for all six real Xvfb tasks,12 JSON outputs (four production language files plus four private upgrade/model resources per loader), semantic/hash parity across loaders. A second final pass, output inventory/assertions, current units/old native regressions, fixture exclusion, source audit/hashes and scoped formatting are still required before completion. Recent source cleanup/test assertions are not yet included in final native receipts.

Exact next command: repeat the six datagen tasks with600s Xvfb bound, compare full12-file inventory/hashes, then execute the focused final unit/JAR/native campaigns. Do not treat the earlier datagen-3.log zero exit with missing output as acceptance.

## Phase 2 checkpoint — live capability proof in progress

Predicate validation behavioral reds now green on target Fabric: native malformed queries reject with LuaException and unknown registry values no longer escape native exceptions. Same validation fix applied to legacy/modern variants; final old reruns pending. Fresh common units1.20.1/1.21.1/26.1.2 and target Fabric units pass (`units-3.log`, exit0), counts not yet final.

Native session regressions executed red in native-red-session-2.xml: actual TurtleBrain selected-slot change remapped the tool to the wrong source slot; changing native owner reused the prior fake-player profile. Snapshot selected slot for reconciliation and invalidate cached identity on ownership change. Adjacent pocket reconciliation/identity and block owner cache paths audited/fixed too. Actual native selected slots0/8/9/15, success/exception, replaced tools, extras and prior permanent modifiers pass; native-session-green.log exit0.

Registered test-only UpgradeTypes now load real dynamic upgrades on both loaders. Target Fabric native-upgrade-2.log exit0 with27 cases/zero skips: real turtle left/right and pocket back/bottom install/remove/reinstall, side storage/removal/unrelated patch preservation, native pocket player inventory, registry-aware item resolution and actual distant chunk disk unload/reload are executed. This is same-process chunk reload, NOT process restart/copied-save acceptance. NeoForge's first run had a chunk-reload assertion without a precise sentinel, retained in native-red-interaction-forge.xml; added exact diagnostics. Later NeoForge run crashed on an unnegotiated pocket deleted payload after a leaked mock player: test-only native mock connection setup and player-list/disconnect cleanup are now authored, not yet verified. No production network validation weakened.

Native interaction red on BOTH loaders: one fake-player action fired two loader right-click hooks (native-red-interaction-{fabric,forge}.xml). Target proxy now delegates once to native gameMode.useItemOn (which owns loader hooks/protection/crouch/item semantics), returns veto results rather than retrying, then performs item fallback only on PASS. Green rerun pending. Client-only Fabric entrypoint and NeoForge client subscriber register FacingBlockTurtleModeller codec; client visuals remain R7.

Next commands: run both target native routes, inspect exact NeoForge chunk diagnostics if any, finish native storage-loan/plugin real-Lua query/transfer/refusal coverage and deterministic owning datagen; full acceptance still incomplete. Shared convention/report guard remain serial hotspots. Existing compile/setup failures and false-green report are not current passing evidence.

## Phase 1 checkpoint — loader/fixture port established, acceptance incomplete

Both target loader production compilations now pass (`loader-port-3.log`, exit0); modern UpgradeType entrypoints, platform adapters, fake players, EnergyHandler query adapter and metadata/access files have target overrides. Broccolium common API output is explicitly on the Tweakium loader classpath. Target common data/upgrade suites compile/run; exact final counts remain to be reconciled. Fabric loader units exposed an inherited kotlin.test contributor classloader mismatch; use the existing JUnit Assertions convention, not a suppressed bootstrap.

Tweakium-only native routes now discover the existing19 owning cases on both target loaders. Initial Fabric native run returned0 with only vanilla always_pass: NOT acceptance. Root cause was CCT fixture scanning before consumer registration; consumer ownership now applies to that entrypoint too, with a strict once-only Tweakium inventory guard. Initial19-case structure placement failures were setup failures, not behavior reds: target native StructureTemplateManager expects the test resource root with data/minecraft/structure, not the old flat directory. Corrected the owning run configuration.

Native latest baseline: both loaders20 cases (19 owning+1 vanilla), one genuine required failure: missing item lookup escapes as IllegalArgumentException instead of LuaException. Saved native-red-query-{fabric,forge}.xml. Added malformed-input refusal regression; target native-red-malformed.xml has21 cases/two failures (missing item and invalid identifier). Old1.21.1 native old-red-malformed.xml has25 cases/one same invalid-identifier failure. No production predicate fix applied yet. Remaining native cases, including actual Lua creative filler, item query/destination conservation, operation policy, native successful break/drop cleanup, template serialization/removals and owner/settings reconstruction, pass in those failed reports; this is partial evidence only.

Exact next action: repair predicate validation/exception boundary across owning versions with these behavioral reds, rerun target/old routes; then live native turtle/pocket/fake-player storage and lifecycle coverage, model codec/client/datagen registration, owning unit matrix, deterministic datagen and final receipts.

Local HEAD remains aacc2d9; dirty paths are the R3 verification draft, Tweakium target loader/test trees, shared testmod import/native API compatibility edits, owning Gradle wiring/report guards and Testiarium Fabric CCT consumer guard. Runtime .jqwik-database and build/run outputs are not source and must not enter commits. No completion or downstream release yet.

Port modern loader implementations/metadata and target data tests; establish Tweakium-only native runner using R1's private fixtures; execute owning behavioral reds before changing demonstrated behavior. Then native fake-player/upgrade/plugin proof, old regressions, twice-per-loader datagen, fresh report reconciliation and cumulative local commit/evidence handoff.

Hotspots carried from R2: gradle/minecraft-26-1.gradle.kts and gradle/gametest-reports.gradle.kts. Only Tweakium-owned sections belong here. R4 owns dependent content; R5 full publication/global build/lint; R6 real process/save/multiplayer; R7 full client visuals/integration variants. None is passed by this checkpoint.
