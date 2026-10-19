# R7 Fabric acceptance matrix — t_2bef9bbc

Status: VERIFIED FABRIC MATRIX PHASE, 2026-10-18 UTC (2026-10-19 Europe/Kyiv). All four current graphical combinations and five fresh Fabric native routes pass. Runtime source candidate: fb5b3a9432b250466e30cb67d22d5cdd25d157fb. Only this report is added afterward; the final local documentation SHA is in the task handoff/delivery manifest. No production, fixture, shared script, controller or dependency source change was necessary. This completion releases existing aggregate children, NOT R7/F release.

## Source and policy custody

Assigned isolated worktree wt/t_2bef9bbc started clean at27798ce and fast-forwarded to fb5b3a9432b250466e30cb67d22d5cdd25d157fb. Rendering cumulative already contains completed interaction bbbd1dcbe9dc8373be0bd5d4c283b54afc0fe3e5; the explicit ancestor check passes, so no duplicate merge/cherry-pick is needed. Verified R7 baseline0783e34, preparation d43491f, approved JEI continuation c68817f and R6 remain ancestors. Source changes are not transferred by task dependencies.

Prerequisite report minecraft-26-1-R7-target-prerequisites.md records authoritative dashboard decisions superseding the decomposition's obsolete REI requirement. Historical H-F10/H-F11 REI26.1.819 cases remain superseded failures, NOT passes or current required combinations. Current four required Fabric combinations are C-F00 base, C-F01 ModMenu18.0.2 (+Placeholder3.0.0+26.1), C-F10 JEI29.47.0.111, C-F11 both. Required Minecraft26.1.2, Java25, Loader0.19.5, FabricAPI0.155.3+26.1.2, FLK1.13.12+kotlin.2.4.0, CC1.120.0 and TeamReborn energy5.0.0 are preserved. Config-port26.1.5 remains conditional N/A per actual R0/R5 source/graph decision, not silently removed.

## Execution checklist

- [x] Own fresh Fabric main artifacts/private fixture and launch manifest built by wrapper, Java25/420s bound/--no-daemon. inputs/manifest.json freezes1125 tracked source files,136 classpath files,8 main mods, private fixture and copied optional JARs before launching.
- [x] Four separate480s/Xvfb actual title/world/content/creative/tooltip/book/menu/reload/dedicated join/reconnect workflows. Each naturally exits server/client0/0,27 ordered phases,two real joins,12 primary plus3 extended-menu PNGs. All60 runtime PNGs inspected via contact sheets; four originals additionally inspected at full resolution.
- [x] Current positive rendering/material/snapshot and strict interaction validators pass every case; current interaction evidence mutation guards also pass per case. Exact outputs remain accepted/<route>/{commands.json,receipt.json,rendering-check.json,interaction-check.json,control/}.
- [x] Fresh positive Fabric storage/provider, standalone/CCT, peripheral/Lua and native client harness routes19–23 pass. Native production445 compiled class payloads match retained main JARs; dependency identity is correlated to all four graphical combinations.
- [x] Retained setup failures, canonical1.20.1 source state,66 evidence/policy guard invocations, whitespace and independent source/runtime audit verified. Documentation-only local commit and read-back-verified archive identities are in the delivery handoff.

Evidence lives in ignored build/verification/fabric-matrix/ outside tracked source. No upstream receipt substitutes for fresh candidate acceptance. Shared scripts/build/catalog/controllers are unchanged. NeoForge matrix belongs to t_d3586e8f; aggregate integration/publication/consumer/R6/F gates belong to existing downstream children t_07f62912/t_9c4d988f. No external coding agents, push, release, protected F access, or service/profile changes.

## Fresh graphical results

| Case | Directory | JEI | ModMenu/Placeholder | Graphical/checker result |
| --- | --- | --- | --- | --- |
| C-F00 | accepted/fabric-base | absent | absent | PASS |
| C-F01 | accepted/fabric-modmenu | absent | present | PASS |
| C-F10 | accepted/fabric-jei | present | absent | PASS |
| C-F11 | accepted/fabric-jei-modmenu | present | present | PASS |

All case receipt.mods maps preserve exact physical dependency hashes. Optional UI JARs exist only in client/mods and are filtered (including JEI/Mezzdev transitives) out of both shared launch classpath arrays. Main server/client mods and fixture are immutable copies, not rebuildable outputs. Per-case directories contain their actual process argv arrays. The fresh Tweakium JAR SHA differs from the rendering parent's archive, but all233 entry payloads match exactly; the other three own main archives match outright. parent-payload-comparison.json retains this distinction instead of rewriting receipts.

Each accepted/<route>/dependency-manifest.json records the exact case, required/optional physical mod hashes, actual server/client argv, config-port disposition and positive-provider/native correlation. Frozen inputs/manifest.json and actual-classpath-hashes.json retain full runtime paths/hashes; selected cache pins and provenance come from the inherited verified preparation manifest, not guessed latest coordinates. Current Tweakium main SHA256 is1f148fa85183635cddcb4f44108d68b96475e9bbf763659726fd8d0c320b23c5; private fixture232fca3854fede1a4a51c20271ac8c996aa808aa6ad437b9cd8435e6e612e86b. Other mod/optional hashes are fully enumerated in every case manifest, not omitted from the archived record.

Visual details: actual Peripheralium-selected tab displays its three production entries, GUI sample slot7 has the approved purple barrel's hoops/grain and no missing checkerboard; native descriptive dust tooltip and both written book pages render. Both upgraded turtle samples and pocket frame are visible; live pocket terminal opens and remains held after reconnect. Extended menu first shows16 diamonds and7 emeralds, then removes only diamonds into player hotbar, preserves emeralds through close/reopen and reconnect. Server acknowledgements independently assert those counts. ModMenu title Mods button and JEI sidebar appear precisely in selected combinations. JEI explicitly warns server-side JEI is absent; server recipe synchronization/transfer is NOT certified.

Sampled world barrel is right-edge cropped, far-side turtle geometry partly occluded, and tutorial/offline-chat toasts overlay parts of images. Actual submitted quad/material/snapshot assertions independently exercise both upgrades and all three barrel faces at integrated/reload/dedicated/reconnect checkpoints; they do not certify arbitrary third-party world geometry. ModMenu config pages, physical tooltip modifiers/alternate locales, book editing/signing and manual crafting/placement remain outside this scoped test. Exact review record: visual-review.txt. Native providers use real registered testmod energy stores rather than claiming a production UI energy-machine mod is installed.

Setup diagnostics are retained in setup-diagnostics.txt: initial background shell lacked exported LOG_DIR and failed before Minecraft launch; explicit-environment retry supplies its own pinned Java/log root. No failed/old/stale report is counted as success.

## Fresh native/provider and regression results

| Route | Fresh reported cases | Passed | Expected negative skips | Failures/errors |
| --- | ---: | ---: | ---: | ---: |
|19 Broccolium Fabric storage |37 |37 |0 |0 |
|20 Testiarium Fabric standalone |14 |11 |3 |0 |
|21 Testiarium Fabric CCT |18 |14 |4 |0 |
|22 Tweakium Fabric peripheral |36 |36 |0 |0 |
|23 Testiarium Fabric native client |1 |1 |0 |0 |
|Total |106 |99 |7 |0 |

Only fresh versioned XML is counted; native/commands.json records invocation start/end, exact commands, process exits, case names and XML hashes. Intentional skipped cases on20/21 are StandaloneGameTests.optionalFailure, failFastFailureCannotBecomeSuccess and requiredFailure (each route);21 additionally CctHarnessGameTests.luaFailureCannotBecomeSuccess. Required outer observing tests succeed; no storage/peripheral skip is hidden. Native fixture.png visually shows the flat-world client/vertical pillar scenario, not additional menu certification. Its raw PNG is retained separately from the60 interaction images.

Positive provider is real registered FabricProviders SimpleEnergyStorage(100,100,100), not an absent-provider lookup. FabricProviderGameTests.worldPositiveFluidEnergyProvidersAndNestedRollback executes actual simulated and committed insertion/extraction/conservation through the production Team Reborn adapter. NativePluginGameTests.realLuaPluginQueriesTransfersRefusalsAndPaidOperations executes actual CC Lua energy/fluid/item/paid-operation contracts and asserts final native fuel/inventory state. Provider route19 and peripheral route22 each executed ONCE: all four client combinations have byte-identical core/CC/Kotlin/energy mods and identical server inputs, with only client-side UI extras differing. native-inputs.json/native-hashes.json bind813 actual runtime/source-output files, and445 native effective main classes are independently matched to retained production JAR payloads. This is identity-correlated positive-server-provider evidence, not four invented provider executions or a UI energy-machine mod claim.

Evidence guards:10 shared workflow,8 positive rendering,4 viewer policy and11 strict interaction mutations per real case (44 interaction;66 total), all pass. check-creative-filler.py verifies both checked-in generated loader asset sets and all six packaged assets in this lane's actual Fabric main JAR; this does not claim a new NeoForge build or datagen execution. Independent audit.py exits0 and rechecks1125 source hashes,136 actual file classpath entries, all60 graphical PNGs/receipts, ordered material/menu assertions, five fresh native reports, expected provider/Lua case identities and compiled-main/artifact bindings.128 positive submitted model checks include16 positive Creative Filler lifecycle checkpoints. audit.json carries exact counts, seven skipped identities and limitations.

Source review confirms target FacingBlockTurtleModeller resolves crafting definition during extraction; submission receives only extracted state/poses/collector, never live mutable world/player state. Fabric physical client entrypoint registers the typed model codec through FabricComputerCraftAPIClient; actual upgrade materials exercise the registration. No speculative adapter rewrite was made. Canonical1.20.1/settings/controller sources remain byte-identical, verified by Git/source audit after all native routes. No source changes require a fresh lint ratchet beyond existing accepted parent lint; report/helper Bash syntax and whitespace are checked here.

## Exact reproduction and evidence

Actual command ledger: build/verification/fabric-matrix/commands.json. Main build log: prepare.log (62 executed tasks); native.log plus native-logs/ and native/<route>/{runner.log,receipt.json,*gametest.xml}; graphical outer logs fabric-*.log and accepted/<route>/{client.log,server.log,commands.json,process-exits.json,receipt.json}. Core PNGs are control/screenshots/; three extra menu PNGs are control/interaction/screenshots/. Contact sheets are convenience derivatives; raw PNGs/hashes remain authoritative.

Run from this assigned worktree with pinned Java25. The actual first command used EVIDENCE=$PWD/build/verification/fabric-matrix; choose a NEW absolute directory to replay (never overwrite accepted inputs/receipts):

```sh
export JAVA_HOME=/home/siredvin/.gradle/jdks/eclipse_adoptium-25-amd64-linux.2
export PATH="$JAVA_HOME/bin:$PATH"
HELPERS="$PWD/build/verification/fabric-matrix"
EVIDENCE="$PWD/build/verification/fabric-replay-$(date +%s)"
mkdir -p "$EVIDENCE/launch"
timeout --foreground 420s ./gradlew -I scripts/verification/client-fixture.init.gradle -I scripts/verification/network-launch-info.init.gradle :broccolium:fabric:26.1.2:jar :tweakium:fabric:26.1.2:jar :peripheralium:fabric:26.1.2:jar :testiarium:fabric:26.1.2:jar :tweakium:fabric:26.1.2:clientFixtureJar networkClientLaunchInfo -Pnetwork.launch-dir="$EVIDENCE/launch" --no-daemon --console=plain > "$EVIDENCE/prepare.log" 2>&1
python3 "$HELPERS/freeze-inputs.py" "$EVIDENCE" > "$EVIDENCE/freeze.log"
for route in fabric-base fabric-modmenu fabric-jei fabric-jei-modmenu; do
  bash "$HELPERS/launch-case.sh" "$route" "$EVIDENCE" > "$EVIDENCE/$route.log" 2>&1 || exit "$?"
done
export LOG_DIR="$EVIDENCE/native-logs"
python3 scripts/verification/run-native-matrix.py --output "$EVIDENCE/native" --only 19 20 21 22 23 > "$EVIDENCE/native.log" 2>&1
```

The helper uses480s foreground timeout/Xvfb and both strict validators, and copies hash-checked main/fixture/optional inputs before launching. freeze-inputs.py requires the retained parent rendering assembly and prerequisite dependency manifest at their recorded worktree paths; do not substitute an unavailable server/dependency and claim success. The native runner owns per-route900s deadlines (client300s+Xvfb), --no-daemon and fresh report guards. Scoped EULA authorization is inherited from explicit t_b52730bf dashboard project acceptance; only disposable offline loopback R7Viewer worlds are used. Online authentication/cross-version compatibility is not tested.

For existing retained evidence: python3 build/verification/fabric-matrix/audit.py. Collect native effective inputs with timeout180s ./gradlew -I build/verification/fabric-matrix/native-inputs.init.gradle fabricMatrixInputs --no-daemon --console=plain. Final delivery is build/verification/t_2bef9bbc-R7-fabric-matrix-v2.zip and t_2bef9bbc-R7-fabric-matrix-delivery-v2.json; the delivery manifest records final local commit, archive SHA/size and independently verified payload count. Archive contains raw receipts/logs/PNGs, all own main/private/optional JARs, source snapshots, native output snapshots, exact manifests, helpers and validators. The initial unsuffixed ZIP is retained immutably; compact v2 omits only four redundant contact-sheet PNG derivatives to remain below the attachment limit, preserving every raw runtime PNG. Unchanged third-party binaries/distribution libraries remain hash-bound pinned references, not redundantly bundled. No remote publication.

## Coverage and downstream gates

No immutable coverage row is rewritten. The17 exported affected rows are affected-coverage-rows.json, with the following contribution/evidence map:

| Existing rows | New Fabric contribution | Still required downstream |
| --- | --- | --- |
|R7:1; PLAN:L107,L109,L111,L113,L116,L119; DECISION:06,10,12 | Four exact-content/visible-tab/book/menu/material/reload/reconnect cases, raw images and both strict checkers | Combine NeoForge result and exact-source/artifact identity; replay any affected case after changes |
|R7:2; PLAN:L159,L161,L162,L164 | Four approved optional combinations with exact dependency manifests; positive-provider/Lua/native hash correlation; config-port actual conditional N/A preserved | Aggregate both loaders, no fabricated REI pass or server recipe-sync claim |
|R7:3 | Five fresh owning Fabric native routes, native client screenshot and eight actual same-version joins | Legacy lane integration12696448274129313f7616a51e130f1d8c291953 and strengthened target22/27 Lua replay |
|R7:4 |66 evidence/policy mutations/checks, source/compiled-main/JAR/screenshot audit and clean canonical source state | Final combined publication/consumer/datagen/production and affected R6 replay/binding before R7/F |

No card-local missing dependency, server or required interaction remains. There are no new shared production fixes to forward. Existing t_07f62912 integrates this report-only commit plus sibling NeoForge output; t_9c4d988f/root/F remain gated. The NeoForge owning-event-bus fix already in parent fb5 still requires final consumer/production/adversarial R6 correlation under the existing rules; this lane's ordinary visual joins cannot replace it. Legacy strengthening is intentionally not cherry-picked here; integration merges it once and replays affected target routes. Physical modifier/alternate-locale/edit-sign/manual crafting and arbitrary third-party geometry remain explicit untested extensions, not unavailable required dependencies being marked passed.
