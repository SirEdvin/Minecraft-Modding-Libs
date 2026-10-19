# R7 target interactions — t_a4740f06

Status: SCOPED IMPLEMENTATION/REGRESSION PHASE VERIFIED on 2026-10-18. Both target base clients pass the final strict interaction workflow, including corrected visible Fabric creative selection. Completion satisfies this card's dependency edges, not R7/F or all-content appearance acceptance. Matrix children remain gated by the rendering lane's explicit missing Creative Filler design decision.

## Source, ownership and commits

Assigned branch `wt/t_a4740f06` fast-forwarded the verified preparation `d43491fb943e1458c8984b539d56aa57d28a206d`; planner/R6/task0 ancestry retained. Local interaction checkpoints: `1ec8a021f485ae13ca3a248cbb6592bde187871c`, `3c6927233619bb2e7afd7abc324b61862aac627f`, `03476ca30a505722e1217a7e8d52c43cc5868097`, strict visible guard `5f2b36bf39892f75c1ae9871e77252533910261a`, pocket/state checkpoint `479d68bc385d84536f1baaa61562c4582dbf1ee3`, and actual page-selection fix `e4e4bfa685f3906948a9a450bfd9167801a452bb`. The final report-only SHA is recorded in the card handoff.

Rendering-owned hooks `c13c9a94a429848b0f4666808cc27d0f8425f016` were inspected and merged at `6a78eba07e2f87c818f90b1906171faa1e85d007`; rendering report/checkers `92ce65e5e1fd208fe0da1067ebd060004d26914c` merged at `b1ebf182df65af612b9ed5b711a867e5de5334f7`. That later merge changes no runtime Java from the tested e4e4bfa. No concurrent ClientProof.java edits: its two hooks came from the owner. The private NeoForge consumer fixture adapter change was explicitly coordinated (comments158/160). Shared runner/schema/build/catalog/report guards/controllers and production `src/main` are unchanged. No external coding agent, model override, remote publication/push, primary merge, protected F access or profile/service changes.

Final readback also merged rendering's report-only correction `4bd7d6cbcf049018b8a528992fcbef8cccf21580` at `7c14c68cb30895d86a9e511d2d1850603e368969`: its older material/menu receipts do not prove visible Fabric creative acceptance. No runtime source changed. The earlier c81b939 delivery ZIP is retained immutable; the completion handoff names the superseding v2 archive and final documentation SHA.

## Implemented assertions and observed fixture fixes

- `InteractionMenus` consumes the real Broccolium menu registration/opening API. Its private schema is exactly BlockPos(3,65,3)+Int(27),12 bytes, decoded with the actual player's RegistryFriendlyByteBuf registry context. No custom packet protocol or weakened trust-boundary validation was added.
- `ClientFixture` seeds16 diamonds/7 emeralds, retains existing gallery slots0..6 and exact two-page book text. `InteractionProof` checks the vanilla chest's synchronized seed, opens the extended menu, performs native QUICK_MOVE, waits for an independently written server-thread conservation ACK, checks client state/cursor, closes/reopens with a fresh menu id, and reopens again after an actual dedicated reconnect. All diamonds remain in the player's inventory and untouched emeralds remain in the chest.
- Exact registry/tab identities replace sample/cardinality-only acceptance. Checks run on integrated world, after resource reload, dedicated join and reconnect. Synchronized book title/author/pages and equipped pocket holder/custom-name `R7 state` are checked at all four checkpoints. Native descriptive tooltip appears once; registered hidden probe shows the Left Shift prompt and does not leak its hook without the key.
- Added two book JUnit regressions for styled Unicode/raw versus filtered text, empty pages, page order, foreign components and no ItemStack mutation. Extended the existing native registered-tooltip testcase with real registered untranslated item/block and hidden block probes: no raw key leakage, ordered/lazy hook computation, live enable supplier and server-safe always-show path. These are new assertions inside the existing exact-discovery identity, not invented extra native testcase counts.
- Actual fixture failures were corrected rather than misdiagnosed as production defects: post-freeze unregistered Item construction became initialization-time registered probes; NeoForge's intentionally optional menu registry became a consumer-owned DeferredRegister through its existing typed platform extension; nested screenshot directories are created and checked, callback visibility is volatile, and capture waits for stable rendered frames.
- Visual inspection caught a real acceptance defect missed by the original validator: Fabric's old private vanilla selectTab left the visible screenshot on Building Blocks. A fresh strict-guard run naturally fails at phase6 (`red-visible/fabric-base/control/failure.txt`). The fix is wholly inside interaction-owned phase6: invoke the pinned public Fabric page-aware `FabricCreativeModeInventoryScreen.setSelectedTab`, wait five ticks, require exact visible menu contents and selectedTab, then allow the unchanged rendering-owned screenshot phase. This optional public-interface reflection is confined to the two-loader private test consumer, not a production/render adapter. Final corrected Fabric screenshot visibly shows Peripheralium and all three entries; NeoForge retains its native path.

## Complete target content inventory

The executable `interaction-inventory.json` in each accepted run records these exact sets, not only counts:

| Owner | Production items | Production blocks | Creative expectation |
| --- | --- | --- | --- |
| Broccolium | none | none | Extension APIs, no concrete production tab |
| Testiarium | none | none | Harness APIs, no concrete production tab |
| Peripheralium | `peripheralium:peripheralium_dust`, `peripheralium:peripheralium_upgrade_template`, `peripheralium:peripheralium_block` | `peripheralium:peripheralium_block` | `peripheralium:tab` displays exactly these3 identities |
| Tweakium | `tweakium:creative_filler` | `tweakium:creative_filler` | No automatically registered production Tweakium tab; appearance is NOT accepted |

Peripheralium blend is deliberately legacy-only, not missing target content. Its target items are passive DescriptiveItem/DescriptiveBlockItem consumers, not an invented active interaction API. Production libraries supply upgrade APIs, not concrete installed upgrade definitions. The private `tweakium:r7_visual` consumer verifies eight ordered CreativeTabUtil outputs: normal/advanced pocket and turtle, each base and right/back upgraded variant. Gallery additionally covers both advanced turtle sides. `tweakium:r7_hidden_probe` is explicitly the one private item excluded from the production inventory and is listed in privateConsumers; `tweakium:r7_interaction_menu` is also private. Ordinary main mods contain neither private client nor native testmod classes (all17 retained main-mod JARs scanned).

## Fresh verification results

Evidence root (all PNGs Git-ignored, outside tracked source):
`/home/siredvin/projects/Minecraft-Modding-Libs/.worktrees/t_a4740f06/build/verification/interactions`

| Execution | Fresh result |
| --- | --- |
| Common TargetTextTests |4 JUnit cases,0 failures/errors/skips |
| Fabric FabricMenuOpeningDataTests |18 JUnit cases,0 failures/errors/skips |
| Native19 Fabric storage/provider/tooltip |37 reported cases,0 failures/errors/skips; exact discovery guard passed |
| Native24 NeoForge storage/provider/tooltip |14 reported cases,0 failures/errors/skips; exact discovery guard passed |
| Native21/26 Testiarium CCT |18 reported cases each,4 exact intended negative skips each,0 failures/errors |
| Native23/28 Testiarium actual clients |1 case each,0 failures/errors/skips; real world screenshot each |
| `accepted/fabric-base`, `accepted/forge-base` |Each server/client exit0,27 core phases,12 core+3 interaction PNGs,2 actual dedicated joins, natural server save/shutdown; strict visible creative, menu ACK/reopen/reconnect, book/tooltip/pocket checks pass |
| Supplemental interaction evidence mutations |11 per accepted run;22 checks pass, using disposable copies of actual runtime artifacts, never fabricated execution evidence |
| Rendering validator regressions |7 pass; both accepted runs also pass the rendering owner's ordered material/snapshot checker |
| Ratcheted Spotless Java/Kotlin |Pass in exact-source standalone;806 Java/Kotlin files and unchanged origin/multiversion ratchet independently compared |

Totals from `audit.json`:22 fresh JUnit cases;89 native reported cases including8 intentional negative skips,0 failures/errors;2 accepted extended client workflows,4 actual dedicated joins,24 core+6 interaction PNGs. Native CCT skips are exactly `StandaloneGameTests.failFastFailureCannotBecomeSuccess` (fail-fast sentinel), `optionalFailure` (optional failure fixture), `requiredFailure` (required failure fixture), and `CctHarnessGameTests.luaFailureCannotBecomeSuccess` (cct lua sentinel), on each loader. Those skips are not passes.

Each accepted client also records28 positive GUI material/snapshot submissions and4 explicit Creative Filler missing-material negatives across four checkpoints. This is scoped rendering evidence inherited through the coordinated hook, not all-content certification. Final six menu crops, four book page crops, both creative tabs and the gallery tooltip images were visually inspected; Testiarium PNGs show actual world/beam/grass/slimes, not blank/crash/loading screens. `menus-contact.png` and `books-contact.png` retain unmodified crops of the original independently hashed PNGs.

## Executable acceptance checklist and exact commands

Run from the assigned checkout with pinned Java25; every Gradle command uses the checked-in wrapper and `--no-daemon`. The build/verification helper `launch-base.sh` is included in the delivery archive. It refuses existing input paths, hash-checks and copies main/fixture JARs before launch, filters optional viewer artifacts from both classpath arrays for the base cases, supplies480s/Xvfb and scoped existing EULA authorization. It never mutates source server worlds. For future replay use a NEW LOG_DIR and generate launch manifests there first; a stale directory is not acceptance.

```sh
export JAVA_HOME=/home/siredvin/.gradle/jdks/eclipse_adoptium-25-amd64-linux.2
export PATH="$JAVA_HOME/bin:$PATH"
export LOG_DIR="$PWD/build/verification/interactions-replay-$(date +%s)"
mkdir -p "$LOG_DIR/launch" "$LOG_DIR/accepted"
timeout --foreground 300s ./gradlew -I scripts/verification/client-fixture.init.gradle -I scripts/verification/network-launch-info.init.gradle clientFixtureJars networkClientLaunchInfo networkForgeLaunchInfo -Pnetwork.launch-dir="$LOG_DIR/launch" --no-daemon --console=plain > "$LOG_DIR/prepare.log" 2>&1
bash build/verification/interactions/launch-base.sh fabric > "$LOG_DIR/fabric.log" 2>&1
bash build/verification/interactions/launch-base.sh forge > "$LOG_DIR/forge.log" 2>&1
for loader in fabric forge; do
  python3 scripts/verification/check-interactions.py "$LOG_DIR/accepted/$loader-base"
  python3 scripts/verification/test-interactions.py "$LOG_DIR/accepted/$loader-base"
  python3 scripts/verification/check-rendering.py "$LOG_DIR/accepted/$loader-base"
done
```

Freshly executed commands/logs for this lane:

```sh
timeout --foreground 300s ./gradlew -I scripts/verification/client-fixture.init.gradle clientFixtureJars :broccolium:26.1.2:test --tests '*TargetTextTests' :broccolium:fabric:26.1.2:test --tests '*FabricMenuOpeningDataTests' --no-daemon --console=plain
python3 scripts/verification/run-native-matrix.py --output build/verification/interactions/native --only 21 23 26 28
python3 scripts/verification/run-native-matrix.py --output build/verification/interactions/native-tooltips-retry --only 19 24
python3 scripts/verification/test-rendering-evidence.py
python3 build/verification/interactions/audit.py
```

- [x] Both target fixture compilers pass: `prepare-accepted.log` (and earlier focused/placement logs).
- [x] Exact content4 items/2 blocks, visible Peripheralium creative3 and private upgrade creative8 verified: each `accepted/<loader>-base/control/interaction-inventory.json`, `interactions.log`.
- [x] Server-origin menu contents, native action, independent ACK, close/reopen/fresh id and reconnect verified: `interaction-transfer.ack`, `interactions.log`, three `control/interaction/screenshots/*.png` per loader.
- [x] Raw/filtered book extraction, metadata, native viewer page navigation and repeated tooltip hooks verified: `junit/*.xml`, native tooltip testcase, book PNGs and logs. Viewer is initialized by the private fixture; this does not establish book right-click/edit/sign/filtering UI.
- [x] Resource reload, two actual dedicated joins, pocket component identity/state and natural shutdown verified: per-run `commands.json`, `receipt.json`, `process-exits.json`, `client.log`, `server.log`, `control/client.log`.
- [x] Default Testiarium CCT/client harnesses executed without source changes: `native/commands.json`, copied XML, screenshots and original bounded runner logs.
- [x] Source/artifact integrity and evidence negatives verified: `audit.py/json/log`, `accepted-inputs/<loader>/sha256.txt`, exact client launch manifests, frozen fixture copies,22 mutation checks, source hashes and main-mod isolation scan.
- [x] Lint/whitespace verified: `spotless-final.log`, `lint-refresh.log`, `git diff --check`. Linked-worktree JGit failure is preserved in `spotless.log`; authorized standalone retains the unchanged ratchet, not a lint bypass.
- [ ] Physical Left Shift reveal, alternate locale/advanced tooltip UI, writable-book edit/sign UI, manually driven placement/crafting, arbitrary menus and third-party consumers: UNCHECKED, not inferred from the scoped assertions.
- [ ] Full optional combinations, legacy replay, changed combined-candidate release gates: DOWNSTREAM; base-only receipts are not the six-case matrix.
- [ ] Creative Filler visible/material completeness: BLOCKED on intended appearance or explicit approved resource reuse. No appearance invented, no missing-material negative promoted to a pass.

## Failures retained and evidence custody

Initial Java compilation mistakes (`prepare.log`), experimental unregistered tooltip JUnit lifecycle failures (`focused-tests*.log`), and first native extra-test discovery failures (`native-tooltips/`) remain failures. New tooltip assertions were folded into the existing case, preserving the exact shared guard instead of disabling it. The earlier `fabric-base/` run completes interactions but lacks nested PNGs; `final/` runs have menu PNGs but predate the strict visible creative correction. Neither directory is final acceptance. `red-visible/` is the real strict-guard failure; `visible-creative-negative.log` shows supplemental rejection of the old receipt. Only `accepted/` is the final strict workflow.

Runner symlinks are a known integration hotspot: rebuilding its fixture path changes earlier retained inputs. Final `accepted-inputs/` contains immutable task-owned copies of both fixture JARs and all17 main mods; earlier rejected fixture bytes are separately preserved. Server library directories remain read-only source links; installed libraries/worlds/main binaries are not embedded in the delivery ZIP. Exact mods, launch classpaths, commands and hashes remain available in the input manifests/receipts. No claim that file existence or a zero exit alone proves appearance or complete behavior.

## Coverage and downstream gates

Supports R7:1 (UI/content/menus), R7:3 (target client/CCT/joins), R7:4 (focused regressions/replay), and linked PLAN:L107,L109,L111,L113,L116,L119,L159,L161,L162,L164 / DECISION:06,10,12. R7:2 optional matrix and positive-provider correlations remain downstream. Immutable204-row ledger is unchanged; no ownership/disposition rewritten. R6 packet rows PLAN:L114,L115/DECISION:09/R6:4 retain their separate adversarial two-client contract: these UI joins do not replace it.

Pre-created children t_2bef9bbc/t_d3586e8f own four Fabric/two NeoForge current combinations (target JEI29.47.0.111, REI explicitly superseded, config-port conditional N/A). Merge exact cumulative lane SHAs, preserve frozen inputs, run all six current cases with BOTH `check-interactions.py` and `check-rendering.py`, inspect actual screenshots, correlate positive providers, merge legacy1269644 once and replay target22/27 Lua. Final integration t_07f62912 owns combined build/publication/consumer/datagen/isolation/ordinary lifecycle and affected exact-candidate R6 binding; no new main-production source changed here. Original root/F gates remain intact. This card's scoped completion satisfies only its own dependency edges; the rendering parent is still blocked on Creative Filler design, and neither that input nor R7/F acceptance is waived.
