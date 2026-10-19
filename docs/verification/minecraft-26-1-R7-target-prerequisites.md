# R7 target prerequisites — t_a5cdac1f

Status: PREPARATION VERIFIED, 2026-10-18. Runtime/model acceptance is NOT complete. An explicitly incomplete draft was saved before this final bounded audit. This report corrects stale automatic decomposition requirements without rewriting the immutable coverage ledger.

## Verified source and decision chain

Clean assigned branch wt/t_a5cdac1f fast-forwarded from27798ce to target checkpoint `c68817f8cac46db8ae06a326058e48fd68a7f113`. Explicit ancestry checks pass for task0 t_41a3580e baseline `0783e34fe381cad4abe4b66a98f28648a26e64af`, planner `b8fd1ddfd5134c45402040fe2c1ca5be343ac48f` and R6 `66436bcdf1b6729dd86c471b5f8906c0a5f43aea`. Use this verified continuation, not an earlier baseline that discards the approved target JEI implementation. The final documentation-only cumulative SHA is in this card's completion handoff; merge that exact SHA before work. A dependency edge does not transfer Git changes.

Read [task0 baseline](minecraft-26-1-R7-baseline.md), [target continuation](minecraft-26-1-R7-target.md), [R0](minecraft-26-1-R0.md), [R5](minecraft-26-1-R5.md), remaining plan R7 section, source ledger, coverage map, module coverage and OpenWiki navigation; inspect source/runner contracts rather than treating wiki prose as implementation evidence. R0 custody is already in cumulative ancestry; never repeat import/read/copy/write against protected F t_16b4e35f.

Authority: dashboard on t_14e20aca removed REI on all maintained lines; dashboard on t_9c4d988f explicitly said “Okey, use JEI for 26.1 since EMI is not present”. Consequently target JEI29.47.0.111 on BOTH loaders is approved and implemented at c68817f; do not request target EMI again or restore REI. Legacy EMI remains unchanged. Archived publisher evidence supplies provenance; this preparation makes no new internet availability assertion.

R0:77/R5:117 actual config decision: no authored ForgeConfigSpec/ModConfigSpec/fuzs API consumer; IConfigHandler formats names only; selected graphs contain no forgeconfigapiport. Config-port26.1.5 is conditional N/A, not a required unresolved artifact silently dropped. CC/NeoForge keep their Night Config transitives. The original [source ledger](../minecraft-26-1-sources.md) records Fabric Modrinth version ID `jUe0ucoE`, not a verified Maven coordinate. If new concrete API usage appears, resolve its actual compile/runtime artifact/provenance before acceptance; failed guessed Maven lookup does not establish absent runtime support.

No production edit, external coding agent, model/provider override, profile change, push, primary merge, release, public publication or service restart. No new Gradle/Minecraft execution is claimed by this preparation.

## Exact target dependencies and matrix

“26.1” in the card means accepted Minecraft26.1.2, not another patch. Both target loaders require Java25 (observed Temurin25.0.3+9), KGP/stdlib2.4.0, CC:Tweaked1.120.0 and all four library main adapter JARs. Wrapper Gradle9.5.0; Stonecutter0.9.8, Loom1.17.11, MDG2.0.142. Preserve Java17/21 old nodes and canonical1.20.1.

| Target | Required runtime identities | Optional identities |
| --- | --- | --- |
| Fabric | Loader0.19.5; Fabric API0.155.3+26.1.2; FLK1.13.12+kotlin.2.4.0; teamreborn energy5.0.0; cc-tweaked-26.1.2-fabric1.120.0 | JEI29.47.0.111 Fabric; ModMenu18.0.2 plus required Placeholder API3.0.0+26.1 |
| NeoForge (`forge` path) | NeoForge26.1.2.122; KFF6.4.0 and actual kfflang/kfflib/kffmod6.4.0; cc-tweaked-26.1.2-forge1.120.0 | JEI29.47.0.111 NeoForge; native energy API, no fabricated extra provider mod |

Original requested Cartesian matrix is explicitly enumerated, not silently omitted:

| ID | Loader | REI26.1.819 | ModMenu18.0.2 | JEI29.47.0.111 | Disposition |
| --- | --- | --- | --- | --- | --- |
| H-F00 | Fabric | absent | absent | absent | Supported base; inherited execution |
| H-F01 | Fabric | absent | present (+Placeholder) | absent | Supported ModMenu; inherited execution |
| H-F10 | Fabric | present | absent | absent | Historical initialization failure; superseded by explicit REI removal, NOT a pass |
| H-F11 | Fabric | present | present (+Placeholder) | absent | Historical initialization failure; same explicit supersession |
| H-N0 | NeoForge | absent | N/A | absent | Supported base; inherited execution |
| H-N1 | NeoForge | absent | N/A | present | Approved viewer; inherited continuation execution |

Current required matrix is SIX combinations: C-F00 Fabric base; C-F01 Fabric+ModMenu+Placeholder; C-F10 Fabric+JEI; C-F11 Fabric+JEI+ModMenu+Placeholder; C-N0 NeoForge base; C-N1 NeoForge+JEI. REI absent in all six. These are preparation aliases, not new coverage IDs. Three earlier fresh JEI executions and earlier base receipts are inherited, not new executions here or all-content certification. Preserve historical REI failures with Architectury20.1.17/Cloth Config26.1.154; they are not current dependencies.

Default development runtime now includes JEI. For WITHOUT cases filter JEI/Mezzdev from BOTH runtime and classpath arrays: run-client.py shares that classpath with its private server. Then add only selected physical client extras. Fabric target uses non-remapping Loom localRuntime, NOT obsolete modLocalRuntime. NeoForge's custom localRuntime extends runtimeClasspath and is non-consumable/non-resolvable. Verify all POM and GMM variants exclude viewer/transitives, not only POM.

Exact dependency coordinates/paths/hashes/transitives and optional JAR copies: evidence dependency-manifest.json and inherited-viewer-resolution.json. JEI loader SHA256 Fabric `cc5f75892fb9055fe7efcfb8dc1daccb94b12a51a4a930f01b90d7608c509098`; NeoForge `06365155a29b06199aa2160489349948cb44072d335125e5f0a5003dcb76c386`. Both loader JARs include mezz_config0.6.6; resolution includes lib/gui/common/common-api. Do not replace accepted pins with latest versions.

### Positive energy providers

Classpath presence is not behavioral proof. Target FabricProviderGameTests registers real SimpleEnergyStorage(100,100,100) through EnergyStorage lookup; simulation/insertion/extraction/conservation and denied DOWN side are asserted. Target ForgeProviderGameTests registers native SimpleEnergyHandler(100); simulation, accepted100/remainder50, extraction, negative refusal and sided absence are asserted. Tweakium NativePluginGameTests/nativeplugins.transfers.lua supply actual peripheral contracts. These are positive testmod providers, not a claim a production energy machine mod is installed in the UI world.

R2/R3/R6 positive receipts stay source/artifact-bound. Replay target storage routes19/24 and peripheral routes22/27 after affected changes, correlate core/provider hashes with every applicable optional combination; if a combination changes those identities, replay that combination's provider route. The existing visual fixture alone cannot establish positive energy behavior. Legacy Lua strengthening `12696448274129313f7616a51e130f1d8c291953` is deliberately not merged here: integration merges it once and replays target22/27; do not claim its receipt already applies to this branch.

## Evidence and compatible servers

Verified writable, Git-ignored evidence root outside tracked source:
`/home/siredvin/projects/Minecraft-Modding-Libs/.worktrees/t_a5cdac1f/build/verification/prerequisites`

Audit passes: JEI archive SHA256/CRC/all145 payloads (146 members); six unique viewer artifacts; all17 production mod hashes (8 Fabric/9 NeoForge), two existing server assemblies;204 unique coverage rows/14 R7-owned rows; all historical prerequisite paths still present; required missing tools/files NONE. Initial audit.log honestly fails because it assumed this newer integrated assembly had the earlier child's distribution.json. Corrected audit-retry.log uses actual assembly.json and observed JAR inventory, not a fabricated absent manifest. Observed JAR inventory is not exhaustive traversal of symlinked library trees or a fresh runtime check.

Read-only server-source inputs:
`$TMPDIR/t_fa26ffb6-validation/servers/26.1.2-fabric`
`$TMPDIR/t_fa26ffb6-validation/servers/26.1.2-forge`

Evidence fabric-assembly.json/forge-assembly.json retain exact installed commands, installer/provenance fields and all main mod hashes. Ordinary Fabric launch: pinned Java25 `-Xms256M -Xmx1536M -jar launcher.jar nogui`; NeoForge uses its exact `@libraries/net/neoforged/neoforge/26.1.2.122/unix_args.txt` from assembly.json. Run only disposable copies with ordinary observer `python3 scripts/verification/production-lifecycle.py <disposable-assembly> --label <new-label>` (startup240s/shutdown90s). Never mutate original worlds. Missing assets/libraries/installers must be reprovisioned from pinned immutable R6 evidence, not guessed or counted as success; [production receipt](minecraft-26-1-R6-production-lifecycle.md) and [R6 integration](minecraft-26-1-R6-integration.md) retain assembly recipes. Optional JAR copies survive prior scratch expiry; full installed distributions are not embedded in this small preparation delivery.

After production changes build fresh main artifacts and disposable assemblies. An old server with a new fixture does not certify new production code. Target same-version compatibility only; no cross-version wire/ABI claim.

## Exact future setup and launch commands

From each worker's OWN worktree after merging the final preparation cumulative SHA:

```sh
export JAVA_HOME=/home/siredvin/.gradle/jdks/eclipse_adoptium-25-amd64-linux.2
export PATH="$JAVA_HOME/bin:$PATH"
export PREREQ=/home/siredvin/projects/Minecraft-Modding-Libs/.worktrees/t_a5cdac1f/build/verification/prerequisites
export LOG_DIR="${TMPDIR:?}/${HERMES_KANBAN_TASK:?}-target-$(date +%s)"
mkdir -p "$LOG_DIR/launch"
timeout --foreground 300s ./gradlew -I scripts/verification/client-fixture.init.gradle -I scripts/verification/network-launch-info.init.gradle clientFixtureJars networkClientLaunchInfo networkForgeLaunchInfo -Pnetwork.launch-dir="$LOG_DIR/launch" --no-daemon --console=plain > "$LOG_DIR/prepare.log" 2>&1
for route in fabric-base fabric-modmenu fabric-jei fabric-jei-modmenu forge-base forge-jei; do
  bash "$PREREQ/launch-case.sh" "$route" > "$LOG_DIR/$route.log" 2>&1 || break
done
```

Future replay commands, NOT claimed executed here. Helper launch-case.sh is supplied in evidence: Bash/JQ syntax and exact runner CLI verified,480s timeout/Xvfb, distinct outputs, hash-checked client extras, server baseline mod guard and freshly filtered manifests. Unknown cases fail. Runner refuses existing output directories. Reconcile all six receipts/exits: a loop break is NOT matrix success. Changed production requires a freshly assembled and independently verified manifest binding, retaining old/new identities; do not bypass guard. Fixture JARs/manifests rebuild from each lane's source, not predecessor output.

run-client.py:65-95 makes a disposable private visual assembly, adds private fixture to client AND server and optional UI mods client-only. It creates fresh integrated/dedicated flat worlds, binds127.0.0.1 ephemeral port, uses synthetic offline R7Viewer (no real account/password/token required), creative/gallery/book/chest/pocket fixtures, two actual joins/reconnect. EULA authorization is explicit t_b52730bf dashboard acceptance scoped to disposable project instances. No online-authentication certification. R6 adversarial Alice/Bob fixtures are separate; visual joins cannot replace them. Ordinary main publications/production assemblies must exclude all private fixtures.

Affected harness/provider commands:

```sh
python3 scripts/verification/run-native-matrix.py --output "$LOG_DIR/native" --only 19 22 23 24 27 28
python3 scripts/verification/test-client-evidence.py
python3 scripts/verification/test-recipe-viewer-policy.py
```

Native helper supplies wrapper --no-daemon, Xvfb/deadlines/fresh versioned XML. Full six-client indices5,9,14,18,23,28; assembled native matrix28 routes. Task0 report contains full build/publication/consumer/datagen and actual two-client R6 cases1-12 commands/bindings. Preserve exact negative skips and no storage/Tweakium skips. For JGit/Spotless linked-worktree failure use approved exact-source standalone with unchanged ratchet; never disable lint.

## Parallel ownership and integration

Both lanes use Albina defaults, NULL model/provider overrides, direct implementation. Expected separate worktrees `.worktrees/t_5eb14799` (wt/t_5eb14799 rendering), `.worktrees/t_a4740f06` (wt/t_a4740f06 interaction). Dispatcher provisions them; retain a project-named branch if already provided. In own checkout: `git status --short`, `git merge --ff-only <final-preparation-SHA>`, then explicit0783e34/c68817f/planner/R6 ancestor checks. Divergent/dirty work is preserved and explicitly reconciled, never reset. Preparation itself is already isolated at verified cumulative source.

| Owner | Exclusive areas |
| --- | --- |
| Rendering t_5eb14799 | Target Tweakium FacingBlockTurtleModeller/RML, loader modules/client entrypoints/registration; target Peripheralium model providers/resources via datagen; Creative Filler resource provider only after design approval; ClientProof.java render/gallery/material assertions; rendering fixture registration JSON; focused render/resource tests; new lane report |
| Interaction t_a4740f06 | Target Broccolium menus/tooltips/TextBookUtils/tests; Testiarium target client/CCT helpers; ClientFixture.java book/world/menu/inventory data; interaction regressions and all-content inventory checklist; new lane report |
| Integration t_2bef9bbc, matrix t_d3586e8f | Shared run-client.py/evidence schema and test-client-evidence.py; init scripts/root build/target convention/catalog/report guards/controllers; aggregate target report and combined artifact/source bindings |

ClientProof.java is rendering-owned even for UI phase requests: interaction sends proposed phase changes, does not edit concurrently. ClientFixture.java is interaction-owned even for new gallery setup: rendering sends precise registration/data requests or obtains explicit ownership transfer. Integration serializes schema/validator/regressions. Each lane creates its own report rather than editing shared R7/baseline/target reports. Generated outputs change only via providers. Unexpected shared production edits require `hotspot: <path> — <reason>` and serialized ownership before modifications.

Integration: read both handoffs, verify exact clean commits/ancestry, merge cumulative rendering THEN interaction SHAs in own isolated worktree (no cherry-picked helpers), resolve overlaps with owner, merge legacy1269644 once. Record changed source/JAR/controller hashes, restore canonical1.20.1. Build fresh artifacts/fixtures, owning datagen twice, affected target/old native, all six current optional clients, ordinary no-fixture production lifecycle, publication isolation/consumers. Any affected menu/packet/CC or production identity change requires affected R6 two-client cases or explicit renewed payload binding under original R6 rules; unrelated UI joins are not a replacement. Final t_07f62912 evidence/original t_9c4d988f acceptance stays gated on actual combined receipts; existing t_49598f9c/t_14e20aca/F ownership remains intact.

## Explicit blockers, rows and downstream gates

External product input STILL MISSING: Creative Filler intended appearance, or explicit approval of proposed vanilla dropper model/texture reuse. Current target datagen registers languages only and seven-entry gallery omits it. JEI approval and a generic “fix rendering” card do not authorize invented appearance. Rendering can audit/contracts/inventory and fix unrelated demonstrated defects, but missing-resource/all-content acceptance remains BLOCKED on that precise input, not another EMI request. No card-local prerequisite to publishing this preparation is missing. Do not repeatedly auto-decompose the missing human decision or label R7/F complete.

Coverage unchanged: R7:1 all content/render/UI; R7:2 optional/providers; R7:3 six old/target harness/joins/Lua; R7:4 regressions/replay. Existing related IDs PLAN:L107,L109,L111,L113,L116,L119,L159,L161,L162,L164; DECISION:06,10,12; networking PLAN:L114,L115/DECISION:09/R6:4; CARD:L019-L023/FINAL:SOURCE. Exact14 R7-owned rows exported;204 total unique. New combination aliases do not alter original owners or dispositions.

Downstream gates: owning red/green; all-content actual materials (not merely nonempty render state); target+legacy client matrix; positive providers/Lua/native; datagen determinism; artifact/publication/consumer/fixture isolation; ordinary dedicated lifecycle; affected exact-candidate R6 networking; combined integration then root/F. Screenshots require visual inspection and source/case hashes, not just existence. Preparation output contains audit.py/audit.json/failure+retry logs, exact dependency and assembly manifests, optional JAR copies, inherited commands/resolution/launch inputs, coverage rows, R6 binding and launch-case.sh. Archive identity and final cumulative SHA appear in completion handoff. Checks are source/input integrity, Bash/JQ/CLI-shape and whitespace only: ZERO new Gradle/Minecraft executions or runtime passes claimed.
