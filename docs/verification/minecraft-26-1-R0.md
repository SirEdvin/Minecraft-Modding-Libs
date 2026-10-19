# R0 — immutable WIP and compiler boundary

Status: R0 CARD-LOCAL ACCEPTANCE VERIFIED, 2026-10-17, task `t_a544bb9d`. Full migration remains INCOMPLETE. The current card's user execution release supersedes the immutable planning documents' obsolete dispatch holds.

## Cumulative baseline and custody

Branch: `minecraft-modding-libs/t_a544bb9d-26.1-r0-preserve-cumulative-wip-and-stab`.

Merged planning `b8fd1ddfd5134c45402040fe2c1ca5be343ac48f`, then cumulative implementation `f347dfc7f5a794693711b75856d1df253866b24a`. Verified both plus original plan `9d282ebae09126bb9da1342f58ae4213de429836`, coverage `e69b7218f5bb077e49e76787168a596f49f26716`, OpenWiki `327f845797e24a3447f4f248f334dc77ebdd35dd`, instructions `27798ce` and audit `f6952bf` are ancestors. Read original brief, source ledger, remaining plan and all R0-owned coverage obligations.

F (`t_16b4e35f`) is inactive and dependency-gated, with no current run; run19 systemd scope is inactive. Its live HEAD and all 57 dirty-file hashes match the planner's manifest. Read-only export imported nine tracked changes plus 32 authored untracked Tweakium sources; excluded 16 Fabric/jqwik runtime files. The complete Git-visible tracked/untracked snapshot (991 files), exact status and HEAD matched before/after transfer and again after verification. F's worktree was never modified or committed by R0. Ignored build outputs are not the source custody inventory.

The imported files were committed BEFORE repairs as explicitly incomplete WIP `d46bab9`. They retain both old StringTag no-op conversion cleanups, shared exact LongTag assertion and cumulative target sources. No generated/runtime/credential content was copied. Compiler repair and owning tests are committed as `2717ffa42c34332825cd74e04f122590d68f1f9c`; the final documentation commit is recorded in the card handoff, avoiding a self-referential hash here.

Full file/hash receipts, exported tracked patch, verification scripts, logs, XML identities, compiler models and consumer results are preserved in the card's evidence archive. Scratch working directory: `$TMPDIR/t_a544bb9d-validation`. Important receipt files:

- `custody-receipt.json`: all 41 imported and 16 excluded paths/hashes, original HEAD, patch checksum and source snapshot checksum.
- `source-before.json`: original Git-visible source/status receipt.
- `final-authored-source-hashes.json`: 775 authored source/build/controller hashes.
- `final-receipt.json`: final F equality, changed source hashes, controller hashes and reconciled test totals.

## Compiler correction, not changed UUID behavior

Initial command, Java25 launcher:

```sh
timeout --foreground 300s ./gradlew :tweakium:1.21.1:cleanTest :tweakium:1.21.1:test --no-daemon
```

Exit 1, log `legacy-common-initial.log`: DataStorageUtil.kt:183 passed null to native non-null Tag; the resulting Java type warning was fatal under `-Werror`. No tests executed. This is compiler-red evidence, NOT a behavioral red test.

Native 1.21.1 `NbtUtils.loadUUID` dereferences its argument; `CompoundTag.getUUID` directly calls it with the stored tag. An absent value therefore throws NullPointerException, malformed type/length throws IllegalArgumentException, and valid UUID reads succeed. Original `f347dfc` DataStorageUtil was separately compiled with actual Kotlin2.0/Java21 and exercised in `uuid-original-characterization/`: compile/run both exit 0, preserving absent custom data, absent key, wrong type, wrong array length, valid UUID, unrelated primitive defaults and zero writes. This independently establishes original semantics rather than inferring desired behavior from the compiler warning.

Minimal correction: route absent custom data through `CompoundTag().getUUID(key)`, remove unused NbtUtils import and obsolete nullability suppression. The same native missing-value exception remains; no zero/default UUID, warning suppression or old API change was introduced. Two new 1.21.1 DataStorageTests cover missing/invalid/valid UUIDs and no-write/default contracts for custom/compound storage. No gameplay behavior was redesigned, so the compiler failure is not misrepresented as behavioral red/green.

Repaired full 1.21.1 common command above exits 0 (`legacy-common-repaired.log`), then the full focused batch below executes fresh reports.

## Fresh focused acceptance

All commands use checked-in Gradle9.5.0 wrapper, `--no-daemon`, explicit deadlines and scratch logs. Launcher: Temurin25.0.3+9 at `/home/siredvin/.gradle/jdks/eclipse_adoptium-25-amd64-linux.2`; Java17.0.20.1 and21.0.12.1 toolchains are available. R0 scope MemoryMax observed as 8589934592 bytes; no settings, containment or service changes.

```sh
timeout --foreground 300s ./gradlew \
  :tweakium:1.20.1:cleanTest :tweakium:1.20.1:test \
  :tweakium:1.21.1:cleanTest :tweakium:1.21.1:test \
  :tweakium:26.1.2:cleanTest :tweakium:26.1.2:test \
  :broccolium:26.1.2:cleanTest :broccolium:26.1.2:test \
  :broccolium:fabric:26.1.2:cleanTest :broccolium:fabric:26.1.2:test \
  :broccolium:forge:26.1.2:cleanTest :broccolium:forge:26.1.2:test \
  --no-daemon --console=plain
```

Exit 0; `focused-matrix.log` records BUILD SUCCESSFUL in 2m2s and actual execution of all six test tasks (not UP-TO-DATE). Each cleanTest precedes its test. Parsed exact versioned `projects/<library>[/<loader>]/versions/<version>/build/test-results/test/TEST-*.xml`; counts reconcile against testcase elements. Copies and every testcase identity are in `focused-xml/` and `focused-matrix-results.json`.

| Scope | Executed cases | XML files | Failures / errors / skips |
| --- | ---: | ---: | --- |
| Tweakium common 1.20.1 | 14 | 3 | 0 / 0 / 0 |
| Tweakium common 1.21.1 | 20 | 5 | 0 / 0 / 0 |
| Tweakium common 26.1.2 | 31 | 5 | 0 / 0 / 0 |
| Broccolium common 26.1.2 | 182 | 12 | 0 / 0 / 0 |
| Broccolium Fabric 26.1.2 | 240 | 11 | 0 / 0 / 0 |
| Broccolium NeoForge 26.1.2 | 134 | 7 | 0 / 0 / 0 |
| Total | 621 | 43 | 0 / 0 / 0 |

Skip map is empty; no negative fixture or storage case was skipped. Common tests use bounded vanilla construction fixtures; Fabric uses supported Knot transformations, NeoForge MDG/FML plus WorldLoader fixture. These are not real server readiness, native world dispatch, GameTests, save upgrade, multiplayer or client acceptance. Modern target DataStorageTests/UpgradeDataStorageTests parity remains R3, not silently claimed by the 31-case target suite.

## Build/source/runtime isolation

`timeout --foreground 300s ./gradlew r0BoundaryProbe -I "$LOG_DIR/boundary-probe.init.gradle" --no-daemon --console=plain` exits 0 in all three invocations. `models.json`, `plugins.json`, `plugin-sources.json`, `classpaths.json` and `boundary-results.json` record actual configuration, not catalog intent.

- All 36 version/loader/library leaves configure; 72 compile/runtime resolution graphs preserve stdlib2.0.0 on old common/Fabric/Forge1.20.1, inherited2.1.0 on NeoForge1.21.1, and2.4.0 throughout target.
- Actual loaded KGP implementation is2.4.0 for every leaf, MDG implementation is one2.0.142 JAR for old/target Forge-family, and Loom implementation is one1.17.11 JAR. Old language/API2.0 and JVM17/21 remain; target JVM25. Old Forge-family's inherited nonfatal warning policy is recorded, not changed; old common/Fabric and all target source warnings remain fatal.
- Twelve target leaves use only the target script. Common/Fabric install LoomNoRemapGradlePlugin, have no mappings configuration and no remapJar; target NeoForge uses MDG without Parchment. Loom's legacy ID is an alias also reported by `hasPlugin` on non-remapping projects: asserting its absence was an invalid probe assumption, corrected using actual implementation/tasks/configurations. Old remapping paths remain intact.
- Target exact pins resolve: NeoForge26.1.2.122, Fabric Loader0.19.5/API0.155.3+26.1.2, FLK1.13.12+kotlin.2.4.0, KFF6.4.0, energy5.0.0; Tweakium/Testiarium use CC1.120.0, including actual `cc-tweaked-26.1.2-forge`. Full graphs, not merely matching substrings, are archived. Resolution evidence is not every optional-mod launch or publication acceptance.
- Selected Kotlin input files have no duplicate relative identities; 247 local overrides across the inspected Broccolium/common-Tweakium nodes are explicitly present exactly once. All three Tweakium DataStorageUtil implementations come from their own selected source roots. This proves selection precedence, not semantic parity of unported modules. No Fabric API components enter target common compile/runtime graphs.
- Existing private Broccolium fixture feature/output plumbing and imported common-Tweakium test wiring suffice for these suites. No speculative generic fixture framework, production run model or Testiarium feature/runners were added. NeoForge's fixture-bearing tested-mod model remains for unit tests only; R1 must keep production/native run models separate. Target publication remains disabled; production/source/feature packaging remains R5.
- Config necessity: searched authored Kotlin/Java/JSON/TOML for ForgeConfigSpec, ModConfigSpec, fuzs/config-port and loader config imports, finding none. IConfigHandler is only a name-formatting interface. No selected target graph contains forgeconfigapiport. CC/NeoForge bring their own Night Config transitives. Therefore no unused mandatory Config API Port coordinate was added or integration removed. A later concrete config-API consumer must declare and verify its own dependency; optional runtime combination proof remains R7.

`python3 "$LOG_DIR/verify-boundaries.py"` final exit 0 (`verify-boundaries-3.log`): independent consumers compile and execute with actual Kotlin2.0 on Java17/21 against rebuilt Broccolium XP and Tweakium compound UUID storage; compile/run exits0, majors61/65. Native target Identifier consumer uses Kotlin2.4/Java25, exit0, major69. These are minimal compiler consumers, not R5 published extension consumers. JVM `javap -v` checks rebuilt Broccolium XPUtil and Tweakium CompoundTagDataStorage: old metadata2.0.0/majors61,65; target metadata2.4.0/major69; class hashes recorded.

Probe-only failed assumptions are retained honestly: an initial oldLoom-ID assertion, an overbroad all-old-Forge-warnings-fatal assertion, and consumer `-Werror` on a nonexistent empty Java output directory. Corrected probes inspect actual non-remap implementation, preserve inherited warning policy and omit only absent empty Java output dirs (no dependency/error suppression). These were not failed production tests and are not counted as passes.

## Restoration, safety and downstream handoff

Canonical state remains1.20.1 in all four controllers/settings/properties. No version switching or broad cleaning occurred. Old authored production differences from F are only the two imported no-op StringTag cleanups and the documented1.21.1 UUID compiler correction; shared LongTag strengthening and new UUID tests remain. All imported target authored sources are immutable in the WIP commit. `git diff --check` passes. Generated build/Knot/jqwik state remains untracked and excluded from commits.

Hotspot: `gradle/minecraft-26-1.gradle.kts` — imported private fixture/common output wiring; serialize subsequent subsystem edits. Hotspot: `gradle/gametest-reports.gradle.kts` — unchanged by R0, owned next by R1 harness/report work.

R1 `t_728ae4df` must merge the final cumulative R0 SHA from completion metadata plus verify planner ancestry, then start its Testiarium registry/CCT/native harness port; do not copy F again. R2 owns Broccolium native access/observer/persistence/data proof; R3 target Tweakium loader/live-player/data parity; R4 Peripheralium content/dependencies/datagen (its common CC compile dependencies remain part of that unported module); R5 production artifacts/publications/full matrix/extension consumers/CI; R6–R7 runtime save/network/client/integration. F alone owns final campaign/OpenWiki/skills/Obsidian reports. No companion repository edits, push, primary merge, release, remote Maven publication or service restart occurred.
