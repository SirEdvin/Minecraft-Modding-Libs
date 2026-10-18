# R7 final target client integration — t_07f62912

Status: VERIFIED CARD-LOCAL INTEGRATION AND FINAL CLIENT REPLAY, 2026-10-18 UTC / 2026-10-19 Europe/Kyiv. This delivers the requested isolated local integration, reproducible receipts and explicit downstream gates. It does NOT close original R7, combined publication, R6 or F. No card-local missing dependency/server/required-interaction blocker remains.

## Exact source custody and constituent SHAs

Assigned worktree: `/home/siredvin/projects/Minecraft-Modding-Libs/.worktrees/t_07f62912`, branch `wt/t_07f62912`. Started clean at27798ce; fast-forwarded to Fabric matrix `c48dca08c4109b8504028f61e38377f83efa01e1`, then merged NeoForge matrix `86701c9a266e66f735c100ab535c4023228f2086` without conflicts. Final integrated RUNTIME SHA: `293098d81f654ec979db0853ab54fcc88a002736`. Final documentation-only delivery SHA is recorded in the adjacent delivery JSON and Kanban completion, avoiding a self-referential document hash.

Verified ancestry includes:

- Task0 R7 baseline `0783e34fe381cad4abe4b66a98f28648a26e64af`, planner b8fd1dd and cumulative R6 66436bc.
- Preparation `d43491fb943e1458c8984b539d56aa57d28a206d`, approved target-JEI continuation `c68817f8cac46db8ae06a326058e48fd68a7f113`.
- Interaction `bbbd1dcbe9dc8373be0bd5d4c283b54afc0fe3e5`, already contained by rendering `fb5b3a9432b250466e30cb67d22d5cdd25d157fb`; no duplicate merge/cherry-pick.
- Purple-barrel resources `b334cbf58b596788e2b82caf7e569b9f86cb69de` and owning NeoForge event-bus fix `dacdbe7` in that rendering ancestry.
- Legacy strengthened Lua/contracts/helper `12696448274129313f7616a51e130f1d8c291953`, integrated ONCE through NeoForge runtime `6b74217389bb6d68536a06adaff3c7c4a07da81f`.
- Both complete report-only matrix heads above. No new production or private-fixture fix was necessary in this integration.

`build/verification/client-integration/custody.json` independently verifies both parent archive SHA256/CRC and every3249 payload hash (1982 Fabric,1267 NeoForge). NeoForge's1128 source hashes match the final source exactly. Fabric's1125 hashes differ only in the four classified legacy changes: shared stronger Lua, TypeScript test assertions, legacy client runner and evidence guard. Full Git diff additionally checks additions/deletions, not only existing manifest entries. Production/build/current target fixtures are identical to the frozen lane inputs. All final graphical executions therefore use real immutable source-identical production/private-fixture JARs, NOT a fabricated fresh full-artifact rebuild. Final `final-source-hashes.json` contains1128 current source/build/helper hashes.

The changed Lua affects target native22; it was freshly rebuilt and executed HERE. It is not present in the graphical private fixture, so no false claim that a cached gallery run proves the stronger Lua. All six graphical combinations were nevertheless freshly replayed on the integrated candidate. Both lane heads changed documentation only after their implementation acceptance; no untested cross-loader production fix was discovered or silently carried forward.

## Approved policy and dependency matrix

Read [target prerequisites](minecraft-26-1-R7-target-prerequisites.md), [Fabric matrix](minecraft-26-1-R7-fabric-matrix.md), [NeoForge matrix](minecraft-26-1-R7-neoforge-matrix.md), [rendering](minecraft-26-1-R7-rendering.md) and [interactions](minecraft-26-1-R7-interactions.md). Explicit dashboard policy removes REI for all supported nodes and authorizes JEI on26.1 because EMI is unavailable there. Historical REI26.1.819 present combinations are SUPERSEDED, not passes and not reopened requirements. Legacy EMI is unchanged. The explicitly approved toxic-purple barrel appearance is implemented, not an unresolved design question.

Required common pins: Minecraft26.1.2, Temurin25.0.3+9, CC:Tweaked1.120.0, Kotlin2.4.0 and four actual library adapters. Fabric pins: Loader0.19.5, API0.155.3+26.1.2, FLK1.13.12+kotlin.2.4.0, Team Reborn energy5.0.0. NeoForge pins:26.1.2.122, KFF6.4.0 (actual kfflang/kfflib/kffmod). Optional JEI29.47.0.111 is loader-matched and physically client-only; ModMenu18.0.2 plus Placeholder3.0.0+26.1 is Fabric-client-only.

| Case | Exact final evidence directory | Optional client UI | Outcome |
| --- | --- | --- | --- |
| C-F00 | accepted/fabric-base | none | PASS |
| C-F01 | accepted/fabric-modmenu | ModMenu + Placeholder | PASS |
| C-F10 | accepted/fabric-jei | JEI Fabric | PASS |
| C-F11 | accepted/fabric-jei-modmenu | JEI Fabric + ModMenu + Placeholder | PASS |
| C-N0 | accepted/forge-base | none, including no JEI/Mezzdev shared classpath | PASS |
| C-N1 | accepted/forge-jei | JEI NeoForge | PASS |

Every directory has `dependency-manifest.json`: exact integrated SHA, required/client/server physical mod hashes, optional keys, actual client/server JVM argv, all actual classpath SHA256 values, original manifest provenance and config-port/provider disposition. `fabric-parent-manifest.json` and `forge-parent-manifest.json` retain complete source/runtime inventories and original pinned download/cache paths. Server and client library inputs are unchanged frozen lane copies, not mutable Gradle outputs. No UI viewer/transitive JAR is smuggled into the shared/server classpath.

Config-port26.1.5 stays CONDITIONAL N/A under actual R0/R5 source/graph findings (`minecraft-26-1-R0.md:77`, `minecraft-26-1-R5.md:117`). Fresh authored Kotlin scan finds no ForgeConfigSpec/ModConfigSpec/fuzs consumer; no selected final classpath contains forgeconfigapiport. IConfigHandler only formats names. Both loaders retain Night Config transitives. No required provider/dependency was silently removed and no unused config API was invented.

## Fresh final runtime and focused regressions

All six workflows use Xvfb,480s foreground external deadline and420s internal workflow deadline, separate disposable loopback dedicated/integrated worlds, and the scoped project EULA authorization already recorded on t_b52730bf. Actual offline synthetic user R7Viewer only; no live save/server or online authentication claim.

Each final case naturally exits server0/client0, records27 ordered phases and TWO actual dedicated joins/leaves, checks exact content/creative inventory, native tooltip/book, both upgraded turtle samples and equipped pocket, menu open/transfer/reopen/persistence, reload and reconnect. Fresh totals:6 workflows,12 joins,72 primary plus18 extended-menu raw PNGs =90. All90 inspected in12 labeled contact sheets; the Fabric both-optionals gallery and NeoForge+JEI reconnected-menu originals additionally inspected at full1100x700 resolution. Raw PNGs remain authoritative, outside tracked source.

Current positive rendering and strict interaction validators pass all six cases.192 actual positive submitted-model checks include24 positive Creative Filler checkpoints, with native atlas/blend/material/no-missingno evidence and snapshot immutability/pose restoration. GUI material proof exercises all three approved barrel faces and both diamond turtle attachments; it does not prove arbitrary third-party world-space geometry. Source trace at `FacingBlockTurtleModeller.kt:40-51` resolves a fresh crafting-definition render state during extraction; submission at61-69 only consumes extracted state/PoseStack/typed collector, no live mutable world/player read. Owning NeoForge event-bus selection remains at `ForgeTweakium.kt:30-40`.

Visually: exact three-entry Peripheralium tab, native dust description, vivid textured purple barrel without missing checkerboard, both book pages, actual CraftOS1.9 pocket, and emerald7 retained with diamond16 moved to the player's hotbar through reopen/reconnect. ModMenu title Mods button appears only in its Fabric cases; JEI sidebar appears in the selected three cases. Offline/tutorial/recipe toasts partly occlude text, world filler is cropped on the right and far turtle sides are occluded. NeoForge base title screenshot catches its natural fade, not a title-launch failure. Actual material receipts supplement those limitations, not invented full-face image coverage.

| Fresh check | Exact result/evidence |
| --- | --- |
| Affected final Fabric22 peripheral/Lua |36 executed tests,0 failures/errors/skips; `native/commands.json`, native.log/native-logs and copied versioned XML |
| Stronger Lua actually processed | authored and fresh build resource SHA256 `13cc44538e21959984bd564f5912e53ab0d5361fd8d5947d79a2e81890a08510`; fillsInventory plus realLuaPluginQueriesTransfersRefusalsAndPaidOperations actually execute |
| Tweakium common/Fabric/NeoForge JVM |45+4+1 =50 fresh cases in11 versioned XML,0 failures/errors/skips; `focused.log`, copied junit XML and audit.json |
| TypeScript strengthened contracts | actual wrapper testContracts/npm test/tsc exits0, focused.log |
| Evidence guards |11 shared +8 rendering +4 viewer-policy +11 strict interaction mutations per each of6 actual cases =89 passing checks |
| Packaged filler | both frozen target main JARs and generated asset sets pass check-creative-filler; no new datagen-determinism claim |
| Native runtime/artifact correlation | fresh effective Fabric inputs inventoried;782 runtime/source-output hashes,445 effective compiled main `.class` payloads match actual frozen graphical main JARs; CC/FLK/energy physical hashes match |
| Independent final audit | audit.py exits0; fresh native/JVM XML, all90 PNG/receipt hashes, per-case classpaths/mods, ancestry,1128 source hashes and strict validators rechecked |

Inherited native evidence is separately identified in `inherited-native-binding.json`, NOT added to fresh execution totals: Fabric routes19/20/21/23 and NeoForge24/27/28 in both base+JEI. Original archived Fabric22 is superseded specifically for stronger Lua by this fresh final36-case replay. Fabric's positive Team Reborn provider executes in a real registered testmod world; NeoForge uses actual registered native capability stores. The native provider/Lua receipts and graphical server core/CC/Kotlin/energy bytes are identity-correlated across client-only optional combinations, not falsely described as six independent provider executions or installed production-machine UI tests. NeoForge's prior stronger Lua source already matches final exactly. Seven explicit expected negative-fixture skips remain ONLY in inherited Fabric standalone/CCT routes20/21; exact identities are exported, never hidden as ordinary passes.

No final graphical/native/JVM failure occurred. One read-only inheritance audit initially guessed a NeoForge test would contain the word Energy; its actual accepted identity is ForgeProviderGameTests.worldPositiveProvidersConserveAndRejectRemoved. Initial `inherited-native.log` remains, corrected `inherited-native-retry.log` passes with exact identity. This was an audit assertion mistake, not a production defect. Other inherited failed setup/JPMS/JGit runs remain in original archives, not accepted runtime.

Fresh npm ci reports ONE high-severity dependency warning in focused.log:93 while contracts pass. No lockfile mutation or npm audit fix was performed. Read-only security triage is routed to verified profile serica in child `t_470a1973`; no advisory identity/exposure or security clearance is invented here. Generated native `.fabric`/two jqwik files were moved into ignored `generated-runtime/` after confirming they are not selected input paths; tracked canonical source/controllers remain unchanged and clean after the report commit.

## Exact commands and reproduction

Evidence root below is absolute once expanded: `$PWD/build/verification/client-integration`. Actual machine-readable final graphical argv/timestamps/exits: `commands.json` plus each case's `commands.json`/`process-exits.json`. Fresh native command/time/XML ledger: `native/commands.json`; wrapper command reproduction: `focused.sh`. All wrapper invocations use --no-daemon. All helper/audit source is included in delivery.

Executed from the assigned worktree:

```sh
export JAVA_HOME=/home/siredvin/.gradle/jdks/eclipse_adoptium-25-amd64-linux.2
export PATH="$JAVA_HOME/bin:$PATH"
EVIDENCE="$PWD/build/verification/client-integration"
PYTHONDONTWRITEBYTECODE=1 python3 "$EVIDENCE/replay.py" > "$EVIDENCE/replay.log" 2>&1
export LOG_DIR="$EVIDENCE/native-logs"
python3 scripts/verification/run-native-matrix.py --output "$EVIDENCE/native" --only 22 > "$EVIDENCE/native.log" 2>&1
timeout --foreground 420s ./gradlew :typed-peripheral-api:testContracts :tweakium:26.1.2:test :tweakium:fabric:26.1.2:test :tweakium:forge:26.1.2:test --no-daemon --console=plain > "$EVIDENCE/focused.log" 2>&1
timeout --foreground 180s ./gradlew -I "$EVIDENCE/native-inputs.init.gradle" integrationInputs --no-daemon --console=plain > "$EVIDENCE/native-inputs.log" 2>&1
PYTHONDONTWRITEBYTECODE=1 python3 "$EVIDENCE/bind-inherited.py" > "$EVIDENCE/inherited-native-retry.log"
PYTHONDONTWRITEBYTECODE=1 python3 "$EVIDENCE/audit.py" > "$EVIDENCE/audit.log" 2>&1
```

Do NOT rerun those existing accepted paths. After the accepted runs the wrapper gained only an optional `--output` argument and an overwrite guard (CLI/help/syntax checked); use `python3 "$EVIDENCE/replay.py" --output "$PWD/build/verification/client-replay-$(date +%s)"` for fresh six-case reproduction. Its actual inner command is timeout480s xvfb-run python3 scripts/verification/run-client.py with loader-specific immutable server/client-info/fixture/java paths and only the case's explicit --extra-mod arguments. It runs both strict validators plus strict mutations and records each actual process argv. Existing evidence/audit must remain immutable. For independent fresh native replay select a NEW --output and LOG_DIR; test runner owns900s server/client300s Xvfb deadlines and rejects stale/nonpassing reports.

Prerequisites are explicit in copied parent manifests: sibling task-owned immutable assemblies/mods/private-fixture/launch-info, shared pinned third-party cache JARs, Java25, Linux Xvfb and native libraries, game asset indexes. `custody.json` gives parent ZIP paths and exact hashes. For a fresh host, first restore both parent archives (including source snapshots and own main/private/optional JARs) and reprovision hash-pinned third-party/server libraries/assets through the original prerequisite/build recipes in both matrix reports; update the task-local input manifest paths and verify hashes before replay. Archives do not pretend to bundle Mojang/game/third-party runtime distributions. Unavailable input is a concrete replay blocker, never permission to substitute artifacts and mark pass.

Guard commands are actual `python3 scripts/verification/test-client-evidence.py`, `test-rendering-evidence.py`, `test-recipe-viewer-policy.py`; per-case `test-interactions.py`, `check-rendering.py`, `check-interactions.py`; and check-creative-filler against BOTH actual frozen target Tweakium JARs. Logs/validator JSONs are retained. Source code, server/client logs, control JSON/material/menu acknowledgements and90 raw PNGs are indexed in the final compact ZIP. Visual-review derivatives are optional and excluded from compact packaging; all originals remain. Adjacent delivery JSON records final documentation commit, exact archive SHA/size/payload readback verification and prerequisite archive references.

Final delivery is `build/verification/t_07f62912-R7-client-integration-v2.zip` with companion `t_07f62912-R7-client-integration-delivery-v2.json`. The first unsuffixed ZIP is preserved immutably (30,085,850 bytes), but exceeds the attachment limit. Compact v2 omits ONLY the four redundant optional-UI JAR copies, already SHA/CRC verified in the two prerequisite lane archives and still present at exact frozen input paths. Both main/private fixture sets, source/native snapshots, every raw log/receipt and all90 real runtime PNGs remain. Optional JAR references/hashes/provenance/manifests are NOT omitted; unavailable prerequisite archives or runtime JARs would be an explicit replay blocker. This is packaging deduplication, not optional-dependency removal or a weakened acceptance claim.

## Coverage contribution and remaining downstream gates

The immutable204-row ledger is NOT rewritten. `coverage-contributions.json` maps all17 client-support rows individually plus4 affected R6 rows to evidence and downstream disposition, preserving original owner/text. No partial contribution is relabeled aggregate completion.

| Rows | Completed final contribution | Still required, existing downstream ownership |
| --- | --- | --- |
| R7:1; PLAN:L107,L109,L111,L113,L116,L119; DECISION:06,10,12 | six exact-source title/content/creative/tooltip/book/menu/material/reload/reconnect workflows,90 inspected PNGs and192 material submissions | original R7/root reconciles entire requested six-node/legacy scope and any broader contract requirements; later production changes invalidate affected receipts |
| R7:2; PLAN:L159,L161,L162,L164 | approved four Fabric/two NeoForge optional cases, exact manifests, config conditional N/A, positive-provider source/artifact correlation | final local publication metadata/all-variant viewer isolation/external consumers/private fixture exclusion must be checked on combined candidate; historical REI is superseded, NOT passed |
| R7:3 | fresh stronger Fabric22 Lua36 plus50 JVM/TypeScript; inherited actual target client/provider/standalone/CCT and NeoForge stronger-Lua receipts explicitly bound | existing R7/root/F reconcile legacy1269644 receipts and all unaffected native rows; not a new all-node execution claim |
| R7:4 | local integration order, final source/JAR/class/PNG custody,89 guards and independent audit | combined artifact/datagen/consumer, ordinary NO-FIXTURE dedicated lifecycle and affected save gates remain t_49598f9c/root/F; visual fixture startup is not ordinary production acceptance |
| PLAN:L114,L115; DECISION:09; R6:4 | explicit invalidation notice: upstream NeoForge owning-event-bus production binary differs from old R6 candidate; current visual joins do not prove adversarial networking | REQUIRED exact-candidate two-real-client adversarial R6 replay or defensible renewed binding before final release; original network report documents12 phases/24 receives per loader and full rejection/conservation requirements |

Card-local result is PASS, not a release waiver. Unresolved downstream gates are NOT TESTED HERE, not “unavailable dependency passed.” Existing original child t_9c4d988f was inspected and is released by this phase completion; its existing downstream aggregate t_49598f9c/root/F retain those gates. Npm warning triage remains separate. No push, primary-tree merge, remote publication, release, protected F modification, live server restart, profile/model override or external coding agent was used.

Untested extensions remain explicit: online authentication/cross-version wire compatibility, JEI server recipe sync/search/transfer (real missing-server warning retained), ModMenu configuration screens, physical modifier/alternate-locale checks, book edit/sign, manual crafting/placement and arbitrary full-face third-party world geometry. None is fabricated from a title screenshot or the submitted-material test.
