# R3 — Tweakium live peripheral and upgrade behavior

Status: COMPLETE — card-local R3 live peripheral/player/upgrade milestone. Historical incomplete checkpoints below are chronology, not the final result. Full migration/release acceptance remains R4–R7/F.

## Final acceptance

Source candidate: `7d26ea574406888e758335c54d5a9c7c0253ad32`, cumulative over planner `b8fd1ddfd5134c45402040fe2c1ca5be343ac48f` and immediate predecessor `aacc2d955b185d813fd0f9781ad3e1ccf80e56c7`; ancestry reverified. Exact target remains Minecraft26.1.2, Fabric Loader0.19.5/API0.155.3+26.1.2, NeoForge26.1.2.122, CC:Tweaked1.120.0 (`-forge` artifact), FLK1.13.12+kotlin.2.4.0/KFF6.4.0/energy5.0.0, Loom1.17.11/MDG2.0.142/KGP2.4.0, Java25.0.3+9, Gradle9.5.0 and Stonecutter0.9.8. Old Java17/21 remain isolated. No version bump, disabled integration or new persistence ownership. Settings, properties, catalog and all four controllers are byte-identical to R2; canonical1.20.1 retained.

The task evidence ZIP contains `final-receipt.json`, exact fresh report paths/testcase identities/skip messages,72 changed source hashes, five release artifact hashes,77-row modern audit with source/target hashes and full diffs, all regression reds/logs, deterministic datagen inventories and the executable collection script. Final cumulative documentation SHA and durable attachment are in the task handoff. No push, primary merge, release or remote publication.

### Fresh units and native behavior

| Version | Tweakium common | Fabric | Forge-family |
| --- | ---: | ---: | ---: |
| 1.20.1 | 14 | 4 | Not claimed: no owning unit suite run |
| 1.21.1 | 20 | 4 | Not claimed: no owning unit suite run |
| 26.1.2 | 43 | 4 | 1 |

Adjacent changed Broccolium target adapters: Fabric241 and NeoForge135 units. All nine explicit executed nodes total466 cases, zero failures/errors/skips. `final-units-jars-guards-2.log` exits0 after each node's `cleanTest test`, five owning `jar` tasks, actual report-guard probes and scoped lint. Common target units include default/missing storage, failed encoding without write/notification, explicit removals/unrelated patches, immutable template/default independence and raw Lua integer boundaries. NeoForge's native EnergyHandler query retains3,000,000,000 amount/4,000,000,000 capacity without narrowing or transfer. No stale unversioned XML is counted. Earlier heartbeat's unparsed484 unit estimate is corrected to466.

| Native command through `scripts/verification/run-gametest.sh` (900s bound) | Cases | Skips |
| --- | ---: | ---: |
| `:tweakium:fabric:1.20.1:runPeripheralGameTest` | 23 | 0 |
| `:tweakium:forge:1.20.1:runGameTestServer` | 23 | 0 |
| `:tweakium:fabric:1.21.1:runPeripheralGameTest` | 25 | 0 |
| `:tweakium:forge:1.21.1:runGameTestServer` | 26 | 0 |
| `:tweakium:fabric:26.1.2:runPeripheralGameTest` | 31 | 0 |
| `:tweakium:forge:26.1.2:runGameTestServer` | 31 | 0 |
| `:broccolium:fabric:26.1.2:runStorageGameTest` | 37 | 0 |
| `:broccolium:forge:26.1.2:runStorageGameTest` | 14 | 0 |
| `:testiarium:fabric:26.1.2:runCctGameTest` | 18 | 4 intentional |

All listed runs exit0. Tweakium totals159 passing cases; each target route has30 owning cases plus one vanilla always_pass, once-only discovery, no skipped capability. Logs: `final-target-native-3.log`, `final-adjacent-native.log`, `final-broccolium-forge-native.log` point to exact bounded native logs. Testiarium's four skips preserve exact requiredFailure/optionalFailure/failFastFailureCannotBecomeSuccess/luaFailureCannotBecomeSuccess sentinels; they are not capability skips. The transport cut off the last adjacent NeoForge run at420s; that interrupted attempt is not counted and was explicitly rerun successfully in final-broccolium-forge-native.log. No shared processes were restarted.

Actual TurtleBrain slots0/8/9/15 exercise hotbar swap/remap, changed selection, replacement tools, leftovers and prior permanent attributes on success and exception. Native owner changes invalidate cached identity; pocket reconciliation returns tools to the original slot. Real chest-minecart loans return tools and drop only exact overflow. Native use/break dispatch preserves both loaders' cancellation/protection and invokes use once; native game mode owns the hook dispatch. Global capture and task-owned mock players/chunk tickets are cleaned up. Actual installed turtle/pocket side settings survive removal/reinstall, serialized item reconstruction and completed same-process chunk disk unload/reload.

Real Lua on a native CC computer exercises public method names/results for inventory/query/fluid/energy transfers, destination validation, malformed identifiers/predicates, raw integer overflow/nonfinite refusal, nonfinite/negative fluid refusal, native conservation, operation payment once, cooldown, insufficient fuel and permission refusal before mutation. Unit tests additionally cover fractional/raw signed-boundary arguments. Integer wrappers validate raw IArguments before CC's generated Optional<Int> coercion; existing Kotlin/Java method signatures and Lua spellings remain. Native long energy queries are separately covered; no every-provider integration certification is claimed.

### Modern gaps, client/data registration and boundaries

Audited all77 modern1.21.1 override rows:70 direct target counterparts and seven explicit dispositions. The seven are canonical whitespace-equivalent DisabledPeripheralOwner; stronger native minecart loan fixture replacing the old entity-read test; R1-owned generic/CCT mixin resources and CCT metadata; canonical empty production mixin config; renamed target common AW replacing obsolete getBees access with public BEES components. Source hashes/full diffs are attached. Compilation alone is not called semantic parity.

Audit found and restored missing public CreativeTabUtil, retaining its method signature and namespace filtering, using registered default UpgradeData and target BACK_POCKET_UPGRADE. Native tests on both loaders verify exactly four base/four owning-upgraded independent stacks and exclusion of unrelated namespaces. Target Output/TabVisibility classes require explicit access expansion to preserve that public API. Native modifiers verify public Output/horse inventory without setAccessible; real model datagen exercises ItemModelGenerators.modelOutput access. Both loader JARs package all four corresponding class/field rules. Public `PockerUpgradePeripheralBuilder` spelling is preserved in all three release JARs. Model codecs/client entrypoints are registered through the pinned loader APIs; full baked/rendered views remain R7.

Native Lua/disk fixtures are private testMod outputs. Release JARs exclude exact compiled fixture identities (68 common,74 Fabric,73 NeoForge), all Testiarium package classes, computer fixture scripts, r3_native resources and datagen caches. Five inspected release JARs contain only class-major69 bytecode; hashes are in final-receipt.json. Fixture/model output in build/upgrade-datagen is not a production upgrade pack. Full publication metadata/features/consumer checks remain R5.

### Datagen and formatter

Final candidate datagen ran twice with `timeout --foreground 600s xvfb-run --auto-servernum ./gradlew` and `--no-daemon --console=plain`, invoking:

- `:tweakium:fabric:26.1.2:runDatagen`
- `:tweakium:forge:26.1.2:runClientData` and `runServerData`
- `:tweakium:fabric:26.1.2:runUpgradeDatagen`
- `:tweakium:forge:26.1.2:runUpgradeClientData` and `runUpgradeServerData`
- Adjacent `:broccolium:fabric:26.1.2:runDatagen` and NeoForge `runClientData`/`runServerData` regression.

`final-datagen-1.log` and `final-datagen-2.log`: both successful; all12 owning JSON paths/hashes identical across passes and paired loaders (four production EN/UA language files plus eight private turtle/pocket upgrade and left/right model files). Generated upgrade JSON is semantically identical to the fixture JSON loaded by both native routes. Fixed real Fabric patch dimension/namespace and NeoForge skipped-client-provider cache defects; no hand-edited generated JSON. Cache files accidentally included in an intermediate WIP checkpoint are removed from the final tracked tree.

Scoped55 changed Java/Kotlin sources pass the unchanged project formatter/rules (`scoped-format-final-2.log` and final combined gate); explicit full-file targets replace only the Git ratchet in the temporary init script, avoiding linked-worktree JGit discovery and checking more than line-ratcheted lint. No lint rule is removed. Shared Gradle conventions/global inherited formatting remain the existing R5 gate, not a blanket global lint pass. `git diff --check` passes.24 actual Tweakium verifier scenarios (valid inventory, nine corrupt reports, missing report and stale-report deletion on each loader) pass via `tweakium-report-guards.init.gradle`; these are guard probes, not fabricated Minecraft execution.

### Ownership and remaining gates

All16 R3-owned coverage rows are tied to this receipt: PLAN:L060/L062/L064/L066–L069/L071/L073/L077 to data/template/registered native upgrade/disk tests and unit suites; PLAN:L089 to native session/interaction/Lua fixtures; R3:1–5 to the modern audit, adapters/access/model registration, native behavior matrix and deterministic datagen above. The immutable204-row ownership ledger is not rewritten into a campaign-wide pass.

R4 (`t_d2dea3b5`) must merge the final cumulative SHA, add Peripheralium-only content/data fixtures to these existing native routes, and extend exact once-only report inventories without dropping Tweakium cases. Hotspots: `gradle/minecraft-26-1.gradle.kts` (Tweakium test/run/datagen classpaths) and `gradle/gametest-reports.gradle.kts` (exact capability inventories). Broccolium sink fixes are cumulative prerequisites for R4 client/server generation.

R5 owns full builds/publications/consumers/global lint; R6 owns production dedicated processes, copied saves/process restart/adversarial multiplayer and real negotiation; R7 owns full client views/optional integrations; F owns aggregate OpenWiki/skills/Obsidian reporting. Same-process disk reload is not process restart, native mock dispatch is not a real socket handshake, hook cancellation is not certification of every protection mod, and callback side effects are not claimed transactional. No blanket save/network/add-on compatibility claim or campaign completion is made here.

## Historical checkpoints (superseded by final acceptance above)

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
