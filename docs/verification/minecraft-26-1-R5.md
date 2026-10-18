# R5 — artifacts, consumers and CI

Status: INCOMPLETE implementation checkpoint. Task t_f0a99e8c; execution authorized by the card's superseding release. No publication or full-matrix acceptance is claimed.

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
