# R5 — artifacts, consumers and CI

Status: R5 CARD-LOCAL ACCEPTANCE VERIFIED, 2026-10-18 (Europe/Kyiv), task t_f0a99e8c. Execution authorized by the card's superseding release. Historical incomplete checkpoints below retain failures and recovery context; the final receipt section supersedes their pending actions. The overall migration remains incomplete pending R6/R7/F.

## Baseline

Merged planner b8fd1ddfd5134c45402040fe2c1ca5be343ac48f, then immediate predecessor cumulative 98f746e52237d73c1ce6042ec7abec5b68104f11. Both ancestry checks pass. Read remaining plan, original brief/source ledger, inherited R0 boundary receipt and R4 handoff. Retain exact 26.1.2 pins, old JVM/Kotlin isolation, canonical 1.20.1, public coordinates and local-only publication policy.

## Initial observation

Java launcher verified: Temurin 25.0.3+9 at /home/siredvin/.gradle/jdks/eclipse_adoptium-25-amd64-linux.2. `timeout --foreground 300s ./gradlew tasks --all --no-daemon` exits 1 after configuration: Spotless cannot find a Git repository from the linked worktree. Log: `$TMPDIR/t_f0a99e8c-validation/tasks.log`. This is not a test pass. Use an exact-source standalone checkout for the unchanged lint rules as explicitly authorized; do not disable lint.

Target convention currently disables Maven task execution and has no Maven publication declaration. Existing checker covers only 24 old publications. Existing target Testiarium features require publication/dependency/source acceptance; target sources merging already has root wiring. Full unit/build/datagen/consumer/CI acceptance remains outstanding.

## Phase 1 checkpoint — publication foundations (acceptance incomplete)

Scratch exact-source standalone checkout: `$TMPDIR/t_f0a99e8c-validation/standalone`, based on R4 SHA above with original `origin/multiversion` ratchet ref restored. No lint was disabled. Initial full build found 12 inherited target Kotlin formatting violations; applied only the formatter's import/whitespace changes. Root `cleanTest build generatePomFileForMavenPublication generateMetadataFileForMavenPublication --no-daemon --console=plain` then exits0 (2m57s,952 tasks); log `build-active-1.20.1-second.log`. Fresh report counting is still pending; do not infer totals here.

Publication model regression executed red: missing target Maven publication (`publication-model-red.log`,exit1). Minimal target Maven publication and raw configurations added; model probe green for36 public identities (`publication-model-green.log`,exit0). Expanded actual JAR/POM/module checker passes36 publications, checking61/65/69 bytecode, source merging, checksums, fixture exclusions, metadata/entrypoints, AW namespace, AT, Mixins and nested dependency paths.

Owning feature regression then executed red against generated metadata: Testiarium test-mod lacked its core dependency (`feature-dependencies-red.log`,exit1). Added explicit core/test-mod feature dependency edges and all-version feature source publications/merged sources. Build and publication checker now exit0 (`feature-dependencies-green-3.log`). Two script API compile failures and a subsequent formatter finding are retained in logs; neither counts as test execution.

Scratch `file-maven.init.gradle` adds only a file repository and a narrowly named aggregate; `r5Publish -I ... -Pr5.maven=$LOG_DIR/maven --no-daemon` exits0 (`file-maven.log`) for all36 publications. No remote or mavenLocal task invoked. Consumers, reproducibility, incremental metadata, remaining active states/datagen/CI are NOT yet verified. The first background invocation lacked the foreground shell's environment and failed before Gradle; retained process output, retried with explicit launcher/log exports.

## Next phase

Inspect the existing public publishing convention and normalized dependency rules, add owning publication regressions, implement surgical target contracts, verify scratch file-Maven artifacts and consumers. Then run all active-state builds, fresh report reconciliation, owning datagen and canonical source hashes; update CI using actual task maps. Preserve failed runs and evidence.

## Phase 2 checkpoint — external consumers and incremental metadata

Nine isolated external consumer routes resolve all36 main libraries plus18 Testiarium fixture capabilities from exclusive scratch file-Maven (no producer class/output directories). Both Java extension and Kotlin extension compile and execute on every route; actual Kotlin2.0 compiler/JDK17 or21 on six old routes, Kotlin2.4/JDK25 on three target routes. Sources exercise platform MenuBuilder, typed storage, OwnedPeripheral/IPeripheralPlugin, CC MethodResult, Peripheralium configure and both Testiarium features. `consumer-resolve-kotlin.log` and `consumer-compile-2.log` exit0; exact commands, native Minecraft/CC dependencies and class hashes in `consumer-results.json` and per-route receipt/source/log files. These are API/compiler consumers, not server/client startup acceptance.

Consumer probes exposed a real feature-only Kotlin stdlib2.4 leak on old nodes; checker red (`feature-stdlib-red.log`), explicit old feature API stdlib2.0 dependency, rebuilt/re-published checker green (`stdlib-publish-classpaths-2.log`). Inherited NeoForge runtime2.1 remains unchanged. Initial consumer setup mistakes are retained: wrong relative file repository (fixed with exclusiveContent), asserting pre-transform identities on Loom outputs, missing Mojang repository, and absent Kotlin-plugin metadata remapper in the scratch Fabric consumer. Enabling normal Kotlin-aware Loom transforms fixes the latter; compilation still uses actual old2.0 CLI, not the producer's compiler. No metadata was manually rewritten or ignored.

Exact dependency metadata incremental regression found7 stale/hardcoded outputs among12 Tweakium/Peripheralium loader/version routes (`incremental-red-exact.json`). Added repository-owned raw-version input tracking/shared build-file inputs and target dependency placeholders/expansion. Probe now updates12/12 without clean/rerun (`incremental-green.json`); restored normal values (`incremental-restore.log`,exit0). Final acceptance must repeat after restoration without source changes and check default metadata.

CI now defines three active-state full builds with Java25 launcher and17/21/25 toolchains, fresh19-owning-unit-node guards and36-publication checks; six loader/version native jobs retain exact report guards, CCT routes, client Xvfb/deadlines and failure logs. Remote CI has NOT run. Local workflow/task-map checks, full fresh active-state matrix, all-provider datagen, reproducibility and durable evidence remain INCOMPLETE.

Hotspot: gradle/minecraft-26-1.gradle.kts — serial target publication/feature wiring.
Hotspot: gradle/gametest-reports.gradle.kts — retain R1–R4 exact native guard contracts.

No push, primary merge, release, remote Maven publication, companion edit or shared-service/profile change.

## Phase 3 in progress — fresh matrix and reproducibility

Initial fresh matrix attempts retained: owning-unit report guard incorrectly treated duplicate parameterized display names as duplicate invocations. Corrected identity to report/index/class/name and added regression; six guard tests now pass (including stale/missing/empty/error/failure/skip/DOCTYPE/declared-count cases and all three class-major/private-fixture/Mixin rejection paths). Actual Gradle builds for1.20.1 and1.21.1 active exit0; first reconciled state has1734 invocations/108 XML/19 owning nodes, zero failures/errors/skips. These are provisional until the final exact-source matrix.

The expanded raw-artifact check executed red at missing target Broccolium raw adapter artifact. All four target Forge-family raw configurations now follow the existing modern raw archive-name/classifier convention and build during assemble; final matrix must verify their real outputs. Raw configurations are not falsely described as separately published Maven feature variants.

Workflow definition checks: actionlint1.7.11 (publisher release/checksum verified), all extracted shell snippets `bash -n`, exact Java17/21/25 ordering, three active states and28 native/CCT/client task routes against discovered tasks pass. Routing checks are not native execution; no remote CI claim. Repeat12/12 incremental probe after restoring defaults exits0 with no source change, then normal metadata restore exits0.

Next: synchronize final candidate to standalone, execute all three fresh full active-state build/publication campaigns, record every report/invocation and canonical hashes; run all concrete-provider datagen twice on both loaders/versions, explicit TypeScript check, refreshed file-Maven consumers/checksums and archive evidence. Old Tweakium has abstract language/upgrade utilities but no concrete old datagen entrypoint/provider; document that distinction instead of calling an empty generator a substantive pass.

### Follow-up packaging/datagen finding — acceptance still incomplete

Candidate71ea407 completed all three fresh active-state root builds: each1734 invocations/108 XML/19 nodes, zero failures/errors/skips;36 publication contracts and118 JARs per state;1052 authored source/controller hashes restored. Exact receipts/logs in `matrix-commands.json`, `units-active-*.json`, `matrix-reports/`, `artifacts-active-*.json`. These builds are real, but reproducibility comparison then found active-state artifact drift, so they are NOT final acceptance for the repaired candidate.

Root cause traced using `resources-canonical.json` versus `resources-active26.json`: Stonecutter redirects the extra `src/generated/resources` root on inactive nodes without generating its counterpart. Old Broccolium languages disappear; target Peripheralium can inherit a stale legacy block-item model. Owning checker regression fails at missing old language (`generated-resource-red.log`). Proposed root fix explicitly selects authored generated roots after project evaluation: shared+overrides for old nodes, target overrides only for26.1.2; excludes datagen caches from all archives and declares own/common generation ordering. Must verify exact bytes across active states, not merely rerun tests and ignore the drift.

Datagen pass1 Fabric1.20.1 exits0; old Forge Peripheralium exits1 because Broccolium/Tweakium were absent from its loaded production-mod model (`datagen-1-forge-1.20.1.log`). Added only production dependency source sets to its client/server/data runs. Forge1.20.1 now executes providers and writes14 resources; subsequent1.21.1 route exposed implicit resource task ordering from the explicit-root change. Latest unverified correction adds the common generation dependency to loader processResources/JAR tasks. Logs `old-forge-datagen-fixed*.log` retain setup compile/receiver and ordering failures; no later result is claimed.

Exact next action: sync root build.gradle.kts and retry bounded old Forge datagen, then complete two-pass data inventory and rerun the final all-state matrix plus artifact checksum equality. No generated JSON was hand-edited, no acceptance waived, no external blocker invented.

## Final verified receipt

Implementation candidate: `12d687a` and its cumulative ancestors. Final documentation-only cumulative SHA and complete changed-source SHA-256 ledger are in the card handoff and `evidence/final-receipt.json` (avoids a self-referential committed hash). Planner and R4 ancestry were verified before work; the final evidence packager checks the complete R0–R4/original-plan/coverage/OpenWiki ancestry again.

### Deliverable and source boundaries

- All36 publications now exist and publish only to the explicitly invoked scratch file repository for acceptance. Gradle's native project-publication identity mapping produces public `site.siredvin` dependencies; no custom POM/module rewriting or unpublished toolkit pin was required.
- Testiarium main/core, test-mod and CCT-test-mod feature contracts include their required core/test-mod dependencies and merged common feature sources on all three versions. Private Broccolium/Tweakium fixtures remain unpublished and all main binaries exclude test/CCT fixtures, Minecraft classes and datagen caches.
- Ten actual raw-configuration consumers resolve the existing six old raw adapters and four target Forge-family raw adapters. Existing archive-name/classifier conventions are preserved. These are raw outgoing configurations, not invented Maven feature variants.
- Target ordinary `jar` versus old remapped/reobfuscated outputs remains intentional. Checker verifies actual POM/module identity/dependencies, artifact sizes/digests, class majors61/65/69, source merging, feature binaries/sources/capabilities, metadata, entrypoints, AW namespace, AT, Mixin classes, nested-dependency paths and selected generated bytes.
- Root resource selection now uses shared+local generated roots for old nodes and local target-generated roots only for26.1.2, with explicit own/common generation ordering. This fixes the observed inactive-node missing-language/stale-model packaging defect without rewriting sources or hand-editing generated JSON. Old Forge Peripheralium production run models include its real production dependencies, fixing the observed datagen startup failure without adding private fixtures.
- Exact accepted pins remain unchanged: Minecraft26.1.2, Gradle9.5.0, Stonecutter0.9.8, Loom1.17.11, MDG2.0.142, KGP2.4.0, NeoForge26.1.2.122, Loader0.19.5/API0.155.3+26.1.2, CC1.120.0 (`-forge` for target NeoForge), FLK1.13.12+kotlin.2.4.0, KFF6.4.0 and energy5.0.0. Actual launcher: Temurin25.0.3+9;17/21 retained for old nodes,25 for target. No library release bump, coordinate/ID/save/network redesign or companion repository edit.

### Fresh full build/unit matrix

For each active state1.20.1,1.21.1,26.1.2, `run-matrix.py` executes the discovered `Set active project to <version>` task, creates a fresh marker, then runs the checked-in wrapper with:

```sh
timeout --foreground 1800s ./gradlew cleanTest build \
  generatePomFileForMavenPublication generateMetadataFileForMavenPublication \
  --no-daemon --console=plain
python3 scripts/check-test-reports.py --since <fresh-marker> --output <receipt.json>
python3 scripts/check-publications.py
```

All commands exit0 on the exact candidate. Each campaign has1734 executed invocations/108 fresh versioned XML across19 nonempty owning unit nodes, zero failures/errors/skips. Total:5202 invocations/324 fresh XML across the three active campaigns. Repeated parameterized display names retain report/index identities; no deduplication loses real invocations. Other leaf test tasks with no authored units remain explicitly NO-SOURCE, not invented passing tests. Per campaign:

| Owning unit node | 1.20.1 | 1.21.1 | 26.1.2 |
| --- | ---: | ---: | ---: |
| Broccolium common | 175 | 175 | 186 |
| Broccolium Fabric | 232 | 233 | 241 |
| Broccolium Forge-family | 128 | 128 | 135 |
| Tweakium common | 14 | 20 | 43 |
| Tweakium Fabric | 4 | 4 | 4 |
| Tweakium Forge-family | NO-SOURCE | NO-SOURCE | 1 |
| Testiarium common | 3 | 3 | 5 |

Actual task commands/statuses/timestamps: `matrix-commands.json`. Exact testcase identities and skip maps: `units-active-*.json`; actual XML copies: `matrix-reports/<active>/projects/.../versions/<version>/build/test-results/test/`. Skip maps are empty. These are unit/loader-aware fixture results, not production server readiness, copied saves or multiplayer acceptance.

### Reproducibility, data and incremental metadata

All118 actual JAR hashes are byte-identical across the three active states. Forced archive task re-execution (scratch `rebuild-archives.init.gradle`, archive up-to-date checks disabled only to force real rebuilding) and refreshed file-Maven publication preserve the same118 hashes. Source compilation/lint/validation was never disabled. `artifacts-active-*.json`, `artifacts-final.json` and `reproducibility-active-states.json` retain every path/digest and zero cross-state differences. Final publication checker exits0 for36 contracts after the forced rebuild.

Corrected selected-root datagen replay runs40 actual tasks in12 bounded batch commands (two passes over six loader/version routes), all exit0; both target NeoForge client/server tasks and target upgrade-fixture tasks are included. There are96 selected production JSON resources: Broccolium2 per loader/version; Peripheralium14 per legacy loader,12 per1.21.1 loader,14 per target loader; target Tweakium2 per loader. Old Tweakium has no concrete old production provider/entrypoint, so its abstract utilities are N/A rather than empty claimed passes. Testiarium has no production datagen owner. All selected production JSON is byte-identical across passes, old generated JSON has no byte or semantic diff, target matches authored resources, and no cross-node output drift remains. Upgrade-fixture provider semantics retain the unchanged R3 receipt; their tasks also execute here.

An earlier inventory helper incorrectly unioned shared legacy generated roots into target counts (98 rather than96); retained as `datagen-unselected-root-inventory/`, explicitly not the final selected/package inventory. Fixed the inventory to match the actual verified source roots and reran both full datagen passes. Final actual receipts: `datagen-before.json`, `datagen-after-1.json`, `datagen-after-2.json`, `datagen-diffs-*.json` (empty), `datagen-commands.json` and `datagen-final-driver.log`.

Incremental dependency metadata regression is red→green across all12 Tweakium/Peripheralium loader/version outputs. Repeating the probe after normal restoration without source changes updates12/12 without clean or `--rerun-tasks`; defaults are then restored. `incremental-green-repeat.json` and restoration logs retain exact emitted metadata, including both library dependency versions. No probe version enters the final Maven candidate.

### External consumers, TypeScript and CI

After the final file-Maven refresh, all nine external routes resolve all36 main libraries and18 fixture capabilities from exclusive scratch file-Maven, then both Java and Kotlin extensions compile and execute. Java covers fixture/core/CCT entry points as well as typed platform/storage/peripheral/CC APIs; Kotlin covers corresponding extension/default-method/metadata paths. Six old routes use actual Kotlin2.0 CLI/JDK17 or21, three target routes Kotlin2.4/JDK25. Normal Kotlin-aware Loom transforms old Fabric consumers; no producer classes/resources, skipped metadata checks, manual metadata rewriting or mavenLocal substitute is used. Ten separate raw outgoing configurations also resolve their actual JARs. Exact source/build scripts, classpaths, commands/exits, class hashes and outputs are in `consumer-results.json`, `raw-consumers.json` and `consumers/`.

The real old feature stdlib2.4 leak is fixed and guarded; inherited NeoForge1.21.1 runtime2.1 remains unchanged, old main language/API2.0 remains consumable. Required CC/Kotlin/energy graphs resolve. Config necessity is the source/graph-grounded R0 finding: no target config-port API is used or required; no guessed mandatory config artifact was added. Optional target REI/ModMenu/JEI runtime combinations remain explicitly R7's gate, not a claimed R5 launch.

Explicit `:typed-peripheral-api:check` exits0 after the final rebuild (`rebuild-publish-typescript-raw.log`). Correlated `tests/contracts.ts`, inventory/item/fluid/energy declarations with unchanged R3 `nativeplugins.transfers.lua`: names, argument order, optional limits, query/slot overloads, one-based slots and numeric transfer returns agree. Malformed/overflow/refusal behavior remains runtime validation, not a new TypeScript signature. No demonstrated declaration delta, so no TypeScript source edit. Inherited strict-null dependency limitation/advisory are not silently declared fixed.

Local CI definition checks pass: actionlint, extracted shell syntax, Java17/21/25 ordering, three active build states and28 discovered native/CCT/client task routes. Six adversarial guard tests pass (including their subcases). CI retains exact native report guards, Xvfb, deadlines and failure logs. This is local workflow validation and actual local full builds, NOT a remote CI run or a newly executed full native matrix.

### Restoration and downstream gates

Canonical1.20.1 is restored. All1052 authored source/controller/build-property hashes match the pre-switch canonical snapshot and the assigned checkout; intentional implementation edits are retained. `matrix-canonical-before.json`, `matrix-canonical-after.json` and empty `matrix-canonical-delta.json` prove restoration. Final code passes unchanged lint and `git diff --check`; only task-created Python cache is removed locally.

Durable evidence: `t_f0a99e8c-R5-evidence.zip`, attached to this card, contains all candidate file-Maven artifacts, raw adapters, POM/module outputs, command/status ledgers, retained failures, exact fresh XML/identities, sources/patch/hashes and reproducible consumer/generator scripts. The archive checksum/size and exact cumulative SHA are in completion metadata. Scratch file-Maven remains at `$TMPDIR/t_f0a99e8c-validation/maven`; the archive preserves it against cache expiry.

R6 `t_997f2d4d` is the inspected pre-created downstream owner: merge the final cumulative SHA plus planner ancestry; use these exact candidate artifacts/hashes for production ready/stop/save/restart, assembled native matrix, copied-save upgrades and real adversarial multiplayer/menu checks. R7 owns real client/title/world/render/optional-mod combinations; F alone owns final campaign/OpenWiki/skills/Obsidian reports. Any later source change invalidates affected artifact/consumer/runtime receipts and requires refresh. No integration was approved removed, no blanket save/network/add-on binary compatibility is certified, and no push/primary merge/release/remote Maven publication/shared-service/profile change occurred.
