# R7 cumulative baseline and execution checklist

Status: BASELINE PREPARATION VERIFIED, task t_41a3580e, 2026-10-18. This card verifies source custody, requirements and evidence navigation; it does not execute or certify R7 runtime acceptance. The original incomplete checkpoint below is preserved as chronology; the verified checklist supersedes its pending actions.

## Recovery checkpoint

Initially clean assigned worktree at 27798ce, branch wt/t_41a3580e. Merged exact planner b8fd1ddfd5134c45402040fe2c1ca5be343ac48f, then completed R6 cumulative 66436bcdf1b6729dd86c471b5f8906c0a5f43aea; both ancestry checks exit 0. Also fast-forwarded the existing R7 partial cumulative 550443be641ca4854a4724e22d4ce9622aae68b4 to preserve the already authorized EMI implementation and private client fixtures. No reset, cherry-pick, protected final-owner read/copy/write or production edit occurred.

The 2026-10-17 execution release supersedes historic planning holds. The dashboard instruction on t_14e20aca on 2026-10-18 removes REI on every maintained line and chooses EMI as primary. Consequently the decomposed target card's copied REI requirement is obsolete: do not restore REI or request its old CC packaging repair. Retain historical failed runs. Target EMI availability remains an unresolved dependency/policy gate, not waived by decomposition. Creative Filler missing models remain an all-content gate. R7 report docs/verification/minecraft-26-1-R7.md and its immutable partial/EMI archives are recovery inputs, not final acceptance.

Read: remaining plan, original brief/source ledger/implementation plan, graph, R0/R5/R6 network/integration and current R7 reports; coverage map inspection/validation in progress. Exact downstream commands, row mapping, archive/source integrity and environment inventory remain to be completed. No Gradle or Minecraft run is claimed here.

Next action: finish coverage-map inspection, verify predecessor/partial evidence archive manifests and exact network binding, recover full launch commands from preserved ledgers, then publish the six-node matrix and executable checklist. Preserve canonical 1.20.1. Hotspots: build.gradle.kts; gradle/minecraft-26-1.gradle.kts; gradle/gametest-reports.gradle.kts. Existing F/Chrono report ownership remains unchanged.

## Verified cumulative source and authority

Runtime source baseline: `550443be641ca4854a4724e22d4ce9622aae68b4`. This includes planner `b8fd1ddfd5134c45402040fe2c1ca5be343ac48f`, immediate completed R6 `66436bcdf1b6729dd86c471b5f8906c0a5f43aea`, R5 `4a07553673bcd51576e0857301dda867bc176a2c` and network lane `f8fd99a006e085bc780ec77fb674a0fc801ce4ee`. All nine ancestry identities in `baseline-audit.json` pass. The final documentation-only cumulative commit is in this card's completion metadata. Downstream workers merge that exact commit, not only R6 or an isolated helper. No source custody transfer from F is repeated: R0's immutable import `d46bab9` and repair `2717ffa42c34332825cd74e04f122590d68f1f9c` already travel through the cumulative history.

Read the full 204-row map, original brief/source ledger/plan, remaining plan, graph and R0/R5/R6/R7 reports. Parsed row IDs are unique and every row has one owner; 14 rows are R7-owned. Historical graph `execution_authorized:false`, obsolete held statuses and historical REI constraints are provenance, not current dispatch instructions. The live original R7 parents planner/R6 are done with no unsatisfied predecessor dependencies. This baseline card's external runtime decisions are explicitly reportable outputs, not reasons to claim runtime success or reopen execution approval.

No model/provider overrides or profile changes; Albina performs implementation directly. No push, primary merge, releases, public Maven, service restart or companion edits. F owns campaign/OpenWiki/skill/Obsidian completion, Chrono personal reading tasks. No protected F files were read, copied or changed here.

## Exact six-node matrix

The `forge` Gradle branch means legacy Forge only on 1.20.1, NeoForge on 1.21.1/26.1.2. Four libraries each retain common/Fabric/Forge-family publications: 36 main contracts, plus existing Testiarium features/raw/source contracts. TypeScript stays independent. Canonical source version remains 1.20.1.

| Minecraft | Loader | Java runtime | Required runtime identities | Client/harness and join obligation |
| --- | --- | --- | --- | --- |
| 1.20.1 | Fabric 0.18.4 | 17 | API 0.92.3+1.20.1; FLK 1.11.0+kotlin.2.0.0; CC 1.113.1; energy 3.0.0 | client route 5; real same-version join; EMI 1.1.24+1.20.1 Fabric; retained ModMenu 7.2.1 |
| 1.20.1 | Forge 47.1.0 | 17 | KFF 4.4.0; CC 1.113.1 | client route 9; real same-version join; EMI 1.1.24+1.20.1 Forge |
| 1.21.1 | Fabric 0.18.4 | 21 | API 0.116.14+1.21.1; same old FLK; CC 1.120.0; energy 4.1.0 | client route 14; real same-version join; EMI 1.1.24+1.21.1 Fabric; retained ModMenu 11.0.1 |
| 1.21.1 | NeoForge 21.1.9 | 21 | KFF 5.7.0; CC 1.120.0 using -forge artifact | client route 18; real same-version join; EMI 1.1.24+1.21.1 NeoForge |
| 26.1.2 | Fabric 0.19.5 | 25 | API 0.155.3+26.1.2; FLK 1.13.12+kotlin.2.4.0; CC 1.120.0; energy 5.0.0 | client route 23; full title/world/gallery/UI/reload/reconnect; optional table below |
| 26.1.2 | NeoForge 26.1.2.122 | 25 | KFF 6.4.0; CC 1.120.0 using -forge artifact; native energy providers | client route 28; same full visual workflow; optional table below |

Launcher Java25/Gradle9.5.0; compile targets17/21/25 (majors61/65/69). Stonecutter0.9.8, KGP2.4.0, Loom1.17.11 (non-remapping target only), MDG2.0.142 remain pinned. Old language/API2.0/runtime isolation remains in force; old NeoForge inherited Kotlin runtime2.1 is not a newly introduced leak.

## Optional combinations: historical and current policy

| Target loader / combination | Inherited evidence | Current disposition |
| --- | --- | --- |
| Fabric base | final-fabric-base, audited 27 phases/12 screenshots | Retain only with source/artifact binding; replay if changed |
| Fabric + ModMenu18.0.2 + Placeholder API3.0.0+26.1 | final-fabric-modmenu, 27 phases/12 screenshots | Required supported optional path; same binding rule |
| Fabric + REI26.1.819 | actual failed initialization | Superseded by explicit REI removal; NOT required to restore or repair |
| Fabric + REI26.1.819 + ModMenu18.0.2 | same actual failure | Superseded; preserve failure archive, never count as a pass |
| NeoForge base | final-forge-base, 27 phases/12 screenshots | Required; same binding rule |
| NeoForge + JEI29.47.0.111 | final-forge-jei, 27 phases/12 screenshots | Retain independently required optional combination; JEI is not automatically approved as target EMI substitute |
| Both target loaders + requested primary EMI (and Fabric EMI+ModMenu) | No compatible artifact supplied; inherited official publisher snapshot contains no26.1.x release | Unresolved dependency/policy gate; no guessed coordinates, incompatible1.21.1 JAR, forced load or implicit deferment |

The target owner must resolve the last row using an actual compatible artifact/source with approved provenance or an explicit target-only alternative/deferment decision. This baseline does not refresh or claim today's global publisher availability; it verifies the immutable same-day research snapshot and records the unresolved requirement. Do not rerun obsolete REI work merely because the auto-decomposed body copied old text.

R0/R5 config decision: no authored target ForgeConfigSpec/ModConfigSpec/fuzs config API usage or selected target config-port dependency; IConfigHandler formats names only. CC/NeoForge retain their own Night Config. Config-port26.1.5 is conditional N/A, not silently removed or an unresolved required Maven coordinate. If new concrete API usage appears, resolve/verify it before declaring a dependency. Native positive item/fluid/energy providers remain required; startup/classpath presence alone cannot replace inherited R2/R3/R6 proofs.

EMI is local development runtime only: Fabric modLocalRuntime; NeoForge non-consumable localRuntime -> runtimeClasspath; legacy Forge modRuntimeOnly. Do not return to the rejected MDG clientAdditionalRuntimeClasspath experiment: a library can be on the classpath without being discoverable as a mod. Preserve POM AND all GMM-variant isolation, not only POM exclusions. No project-owned REI implementation existed; upstream CC JARs remain untouched.

## Acceptance rows and downstream responsibilities

| Rows / constraints | Concrete execution/evidence checklist | Owner |
| --- | --- | --- |
| R7:1; PLAN:L107,L109,L111,L113,L116,L119 | Inspect target FacingBlockTurtleModeller/RML and client entrypoints; immutable extraction, submission/material proof, no mutable world reads on submission or early common client linkage. Actual title, integrated world, every registered content/creative tab/tooltip/book/model, both equipped turtle sides and pocket; menu interactions, resource reload, two dedicated joins/reconnect; real screenshots and raw logs | t_9c4d988f |
| R7:2; DECISION:06,10,12 | Exact current optional table and hashes/transitives; required CC/Kotlin/positive energy; config conditional N/A; resolve target EMI and Creative Filler missing-model gate, do not invent intended appearance | t_9c4d988f |
| R7:3; PLAN:L159,L161,L162,L164 | Six client harness routes, including target routes as combined baseline; four old graphical joins/EMI plugin reload/actual overlay and real Lua/client behavior correlated with unchanged TypeScript contracts; scope limited receipts honestly | t_85706b6f; target route replay coordinated with target owner |
| R7:4; CARD:L019-L023 / A1-A5; FINAL:SOURCE | Owning behavioral red/green before fixes, affected old/target/datagen/native/server/consumer/publication replay, fixture exclusion and exact cumulative artifacts/source/commands; common-side production server startup after client changes | t_49598f9c integrates both lanes; t_14e20aca aggregate |
| PLAN:L114,L115; DECISION:09; R6:4 | Retain exact R6 network binding below; identify and replay affected actual two-client cases, never substitute mock/UI-only proof | Existing R6 receipt, affected replay by R7 integration |
| CARD:L024-L025 / A6-A7; FINAL:* | Final campaign source/receipt audit, OpenWiki lifecycle, skill guidance, sole Obsidian report; preserve existing Chrono | F t_16b4e35f, not these workers |

All 14 R7-owned rows are exported unchanged in `r7-coverage-rows.json`; the table splits execution providers without altering the immutable ownership map. Direct R7 rows remain owned by t_14e20aca. Acceptance aggregate A1-A7 corresponds to original CARD:L019-L025; no partial child closes the campaign.

Inherited R7 proof is not exhaustive acceptance: Creative Filler lacks its intended model resources across maintained lines and is not covered by the three-item Peripheralium gallery. Fix from actual source/design evidence; if intended appearance cannot be inferred, surface a focused decision. Old EMI receipts prove joins/plugin reload/sidebar, not exhaustive recipe transfers/search/correctness. Keep the old NeoForge shutdown timeout and successful unmodified retry both visible; no root-cause repair is established. Preserve upstream EMI dev warning counts and offline/audio limitations.

## Complete commands recovered from repository and preserved ledgers

These are executable future replay commands, NOT commands run by this baseline card. Use an exact-source isolated standalone checkout only if the known linked-worktree Spotless/JGit ratchet fails; retain origin/multiversion and compare hashes, never disable lint. Initialize in the worker's own checkout:

```sh
export JAVA_HOME=/home/siredvin/.gradle/jdks/eclipse_adoptium-25-amd64-linux.2
export PATH="$JAVA_HOME/bin:$PATH"
export LOG_DIR="${TMPDIR:?}/${HERMES_KANBAN_TASK:?}-validation"
mkdir -p "$LOG_DIR"
timeout --foreground 300s ./gradlew tasks --all --no-daemon > "$LOG_DIR/tasks.log" 2>&1
```

The complete previously abbreviated client command is below; repeat both loaders for each v=1.20.1,1.21.1,26.1.2. Source authority: original plan:L161-L162, CI:L130 and run-native-matrix.py:L18-L30. The collector explicitly maps client indices5,9,14,18,23,28 and copies fresh XML/screenshots. Direct invocations and collector are alternatives, not duplicate evidence counts.

```sh
timeout --foreground 300s xvfb-run --auto-servernum ./gradlew ":testiarium:fabric:$v:runClientGameTest" --no-daemon > "$LOG_DIR/client-fabric-$v.log" 2>&1
timeout --foreground 300s xvfb-run --auto-servernum ./gradlew ":testiarium:forge:$v:runClientGameTest" --no-daemon > "$LOG_DIR/client-forge-$v.log" 2>&1
python3 scripts/verification/run-native-matrix.py --output "$LOG_DIR/client-fresh" --only 5 9 14 18 23 28
```

Fresh report: `projects/testiarium/<loader>/versions/<v>/build/test-results/client-gametest.xml`; native screenshot under that node's `build/screenshots/fixture.png`. Exact StandaloneGameTests.clientFixture, nonempty fresh testcase inventory, reconciled declarations, zero failures/errors/skips. Harness success alone proves neither title/visual nor optional integrations. `scripts/verification/run-gametest.sh` owns actual timeout/Xvfb/fresh-report setup. Never reuse an old output directory as a new execution receipt.

Prepare current target fixture and launch manifests (the combined flags below are from the inspected opt-in init scripts):

```sh
timeout --foreground 300s ./gradlew -I scripts/verification/client-fixture.init.gradle clientFixtureJars --no-daemon > "$LOG_DIR/client-fixtures.log" 2>&1
timeout --foreground 300s ./gradlew -I scripts/verification/network-launch-info.init.gradle networkClientLaunchInfo networkForgeLaunchInfo -Pnetwork.launch-dir="$LOG_DIR/target-launch" --no-daemon > "$LOG_DIR/target-launch.log" 2>&1
timeout --foreground 180s ./gradlew -I scripts/verification/legacy-client-launch.init.gradle legacyClientLaunchInfo -Pclient.launch-dir="$LOG_DIR/legacy-launch" --no-daemon > "$LOG_DIR/legacy-launch.log" 2>&1
```

Resolve `SERVER_SOURCE` to a task-local disposable hash-bound production assembly for the selected route; retained R6 assemblies are read-only inputs at `$TMPDIR/t_fa26ffb6-validation/servers/<v>-<loader>` while present. Missing installer/libraries must be reprovisioned from the archive's assembly.json/pinned distributions, not guessed or counted as success. Ordinary production assemblies have no private fixtures. Target visual runner adds its private fixture explicitly and keeps optional UI mods client-only:

```sh
timeout --foreground 480s xvfb-run --auto-servernum python3 scripts/verification/run-client.py --server-source "$SERVER_SOURCE" --client-info "$LOG_DIR/target-launch/$loader-client-info.json" --fixture "$PWD/build/client-fixture/tweakium-client-$loader.jar" --java "$JAVA_HOME/bin/java" --output "$LOG_DIR/visual-$loader-base" --loader "$loader" --eula-accepted > "$LOG_DIR/visual-$loader-base.log" 2>&1
timeout --foreground 360s xvfb-run --auto-servernum --server-args='-screen 0 1280x900x24' python3 scripts/verification/run-legacy-client.py --server-source "$SERVER_SOURCE" --client-info "$LOG_DIR/legacy-launch/$v-$loader.json" --java "$NODE_JAVA" --output "$LOG_DIR/legacy-$v-$loader" --inventory-screenshot --require-emi --eula-accepted > "$LOG_DIR/legacy-$v-$loader.log" 2>&1
```

For old NODE_JAVA select verified17/21, not launcher25. For optional target combinations repeat the first command with a new output and one `--extra-mod <hash-checked-jar>` per required optional/transitive artifact (ModMenu+Placeholder or JEI). Target EMI arguments cannot be supplied until its gate resolves. Raw executed command arrays, exact resolved path inputs and exits are recovered in `recovered-command-ledger.json` (31 entries, including failed/historical variants; NOT31 accepted runs). Original authoritative members: partial ZIP `evidence/independent-commands.json`, `evidence/legacy-scene-commands.json`; EMI ZIP `evidence/smoke-final-commands.json`, `evidence/smoke-accepted-commands.json`. Use only the accepted route names in the R7 report; notably reject smoke-final-1.21.1-forge as EMI evidence and select smoke-accepted-1.21.1-forge. Old client classpath uses named development outputs and native MOD_CLASSES on Forge47; do not assert release-JAR/client byte equality.

After behavior/resource edits, resolve owning tasks from source/CI and replay affected gates. Full native runner already enumerates all28 routes including target Forge storage/CCT observers. Full artifact/TypeScript commands are complete, not truncated:

```sh
python3 scripts/verification/run-native-matrix.py --output "$LOG_DIR/native-fresh"
timeout --foreground 1800s ./gradlew cleanTest build generatePomFileForMavenPublication generateMetadataFileForMavenPublication --no-daemon --console=plain > "$LOG_DIR/build.log" 2>&1
python3 scripts/check-publications.py > "$LOG_DIR/publications.log" 2>&1
timeout --foreground 300s ./gradlew :typed-peripheral-api:check --no-daemon > "$LOG_DIR/typescript.log" 2>&1
python3 scripts/verification/test-client-evidence.py
python3 scripts/verification/test-recipe-viewer-policy.py
```

Only if affected, repeat root build/publications for all active states using literal `Set active project to <version>` as CI does, then restore1.20.1 and compare source/controller hashes. Generate via actual owning providers twice; never hand-edit generated files. R5 retains exact datagen task ledger and conditional old Tweakium N/A. Preserve shell exit status in redirected logs; deadlines/forced exits are failures. Count fresh executed versioned XML only, retain exact native negative identities/messages, zero storage/Tweakium skips, no skip-only/zero-test passes.

## Exact-candidate R6 networking receipt and replay triggers

`r6-network-binding.json` preserves the parsed values from R6 ZIP `evidence/network-reuse-decision.json` (JSON serialization is not asserted byte-identical). Verified eight production target JAR hashes against embedded final artifacts, exact nested network ZIP hash `eab0de96e057d0e15ef6345a519673a4f21dc1847a0669fe2f710131debdb647`, both actual receipt hashes and both fixture hashes. Fabric receipt b42aac50fc7111bb43fd58305ac0da17ced9e7ad790e7c5d921b4cc878547b51; NeoForge receipt fe66c335af88d61ee96bdb650bc15bc31438c3ec389af53b494bdbe8ca737e81. Each has two real graphical clients,12 synchronized phases/24 received packets, natural server/Alice/Bob exits0. This is retained R6 execution, NOT a new run by this card.

Cases1-12 respectively: concurrent QUICK_MOVE conservation; wrong menu ID; slot32767; slot-2; button127; forged Integer.MAX_VALUE count (valid action remains server-authoritative); stale/reopened menu; distance; backing-BE removal; spectator; distant use-position refusal; equal numeric IDs but distinct per-player menus. Also owning12-byte registry-aware schema,12 truncated prefixes/invalid counts/position/32601-byte input, caller/temporary-buffer lifetime, Server-thread action/Render-thread decode. NeoForge outer codec remains loader-owned/unbounded; no blanket third-party menu hardening claim.

Replay affected target loader(s) whenever menu codec/opening/platform/action paths, packet validation, fixture behavior or target production binaries/CC/runtime dependencies change. Pure immutable model/resource changes need client/production/artifact replay and explicit unaffected-network rationale, not automatic invented network success; changed binary hashes require renewed binding or replay. Use full cases1-12 if impact is uncertain. Preserve all17 network source hashes and both fixture hashes before retaining unchanged evidence. Exact runner commands and preparation are in R6-network.md:L73-L75, not an invented protocol. No cross-version wire/ABI, online-auth or arbitrary protection certification.

## Verified input artifacts, environment and remaining prerequisites

All three input archives independently pass SHA256, CRC and every manifest payload hash:

| Archive owner / name | Bytes / members / verified payloads | SHA256 |
| --- | --- | --- |
| t_997f2d4d R6-evidence.zip | 112478648 /1784 /1783 | 33d25ebbeb0e8fb2630fdf0d5c5ff26ee918f0508ffa7a1f05da88416be52775 |
| t_14e20aca R7-partial-evidence.zip | 23502785 /327 /326 | f6f5287fda2d6abbb967b2f10d48dff776b2ff77200ee17c84f70ad5504a516a |
| t_14e20aca R7-emi-evidence.zip | 4134641 /186 /185 | b752e1afe61c407d43bdd3780dbdc2f09600e00cb1372cd478b7958597d6a160 |

Full paths are each owner's `build/verification/t_<id>-<name>` and are recorded verbatim in baseline-audit.json. R6 ZIP preserves118 JARs, file-Maven metadata, five original input archives, native/unit/production/save/raw networking receipts; no need to rebuild accepted baselines merely to prepare this checklist. R6 inherited counts1753 units/109 XML,28 native routes/434 cases/38 intended negative skips,12 production and20 copied-save executions are historical upstream evidence, not newly executed here. Preserve the two source-JAR ZIP-order-only reproducibility limits.

This card's audit checks all14 EMI source hashes against the merged baseline with zero deltas;36 recovered path prerequisites (assemblies/manifests/fixtures/JDKs/optional JARs) exist at audit time. Existence is NOT deep assembly integrity or fresh runtime acceptance; workers recheck exact launch classpaths, manifests and production hashes before use. Scratch may expire; archives preserve identity/commands, not guaranteed installed launcher libraries/assets. No target EMI artifact or approved target alternative is supplied. No Creative Filler appearance decision is inferred.

Live preflight: Java25 Temurin25.0.3+9; xvfb-run, timeout, jq, unzip and java are available. Disk41GiB free at the observed check (recheck before heavy runs; no shared caches deleted). This was not an X-server/Minecraft launch or JDK17/21 re-execution. Prior project EULA acceptance is recorded in R6-network.md:L27 from t_b52730bf dashboard; apply only disposable instances and do not re-request it as implementation approval.

## Required delivery from each execution lane

- Exact local cumulative commit and planner/R6/R7 ancestry; source/controller hashes and canonical1.20.1 restoration; source diff and identified hot paths. Merge both sibling cumulative SHAs before final integration, not cherry-picked helpers.
- Per-case/combination status including failed/unperformed/conditional/superseded; exact dependency/JAR/fixture hashes, full command arrays/exits/timestamps, fresh XML identity/count/skip maps; actual screenshots/logs outside tracked source.
- Ordinary main publications and production assemblies exclude nine private R7 client fixture identities plus all other private test/CCT assets; explicit positive dedicated ready/stop/save proof after client-source changes. Private visual assemblies are distinguished from production assemblies.
- Named coverage rows, inherited receipt/hash reuse rationale and affected replay gates; retained failed REI/EMI/shutdown attempts; no automated assertion that a screenshot merely existing proves correct visuals.
- Integration t_49598f9c provides a final receipt only when target/legacy gates genuinely meet current policy. t_14e20aca remains incomplete until unresolved required target EMI/Creative Filler gates resolve. Baseline completion releases preparation dependencies, not F/campaign acceptance.

Audit deliverables under this worktree's `build/verification/`: verify-r7-baseline.py, baseline-audit.json, r7-coverage-rows.json, r6-network-binding.json, recovered-command-ledger.json and runtime-prerequisites.json. All are baseline evidence, not generated passing Minecraft output. Audit script exits0; git diff --check passes. An initial audit failed because EMI manifest wraps its payload dictionary; corrected the audit after inspecting actual schema, retained failure in card history and did not reinterpret it as runtime failure. No Gradle/Minecraft/remote CI was executed in this preparation card.
