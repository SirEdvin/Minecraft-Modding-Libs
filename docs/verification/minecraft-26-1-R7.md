# R7 — client models, interactions and integration variants

Status: INCOMPLETE — REI removed; maintained-node EMI replacement verified; compatible target EMI artifact unavailable, task t_14e20aca, 2026-10-18. The dashboard instruction to remove REI across 1.20/1.21/26.1 and use EMI supersedes the historical required-REI gate below. Historical failures remain evidence, not current requirements. Full R7 acceptance and final-candidate release are not established.

## Baseline

Assigned worktree was clean. Fast-forward merged planner b8fd1ddfd5134c45402040fe2c1ca5be343ac48f then completed R6 cumulative 66436bcdf1b6729dd86c471b5f8906c0a5f43aea; both explicit ancestry checks exit0. Read remaining plan, original brief/source ledger, R5 artifact and R6 network receipts and OpenWiki index. Execution release supersedes historic planning holds. No reads or modifications of the protected final-owner worktree.

Verified Java25 launcher: /home/siredvin/.gradle/jdks/eclipse_adoptium-25-amd64-linux.2 (Temurin25.0.3+9). Scratch evidence root: $TMPDIR/t_14e20aca-validation. All maintained nodes and selected pins remain unchanged. R6 network evidence is inherited, not a visual acceptance run by this card.

## Initial trace and next action

Target FacingBlockTurtleModeller resolves an immutable crafting item into child render state during extraction and submits only that state. Its extents are currently empty; whether that impacts actual output remains an unverified investigation, not an asserted defect. Target Testiarium client runner creates/joins an integrated flat world after resource loading, then executes registered client tests. Existing network runner supplies real dedicated-server/two-client process wiring but bypasses title-screen and visual assertions. Need independently exercise title, content/models/tooltips/books/menus, reload/reconnect and optional-mod variants against exact artifact identities, plus four old-node client routes and representative joins.

Next: recover retained assembly/launch/artifact manifests read-only; discover existing client tests, loader registration and actual model APIs; execute bounded client harness baseline, then add only missing owning client verification fixtures. No production behavioral change before a real red regression.

Hotspots carried forward: gradle/minecraft-26-1.gradle.kts (target convention); gradle/gametest-reports.gradle.kts (exact report guards). Neither changed yet.

No push, primary merge, release, public publication, service restart or profile change.

## Phase 1 — executable client evidence (overall acceptance incomplete)

Created exact-source standalone clone at $TMPDIR/t_14e20aca-validation/standalone with unchanged origin/multiversion lint ratchet. `tasks --all --no-daemon` exits0. `run-native-matrix.py --only 5 9 14 18 23 28` executes all six client harness routes; every route exits0 with fresh accepted XML and screenshot. Exact testcase/count reconciliation is pending final aggregation, not guessed here. Initial background shell lacked LOG_DIR; failed before running Gradle and was retried explicitly.

Added opt-in private clientTestMod definitions/resources and fixture compilation/visual runner. No production source, ordinary publication or target convention changed. Fixture builds execute successfully on both target loaders. Expanded Fabric attempt1 reached title, integrated world, content gallery, creative tab, two book pages, reload, actual dedicated TCP join and native chest menu, but waited for an incorrect PocketComputer-specific screen name. CC actually uses AbstractComputerScreen/ComputerScreen. Retained interrupted attempt is not acceptance; corrected predicate and waiting for actual world screen before captures. Attempt2 executing at this checkpoint.

The real gallery screenshot was inspected: three Peripheralium resources, equipped facing-block turtle models, pocket and book render with no visible missing texture. Left-facing upgrade is occluded from the standard GUI viewing angle; world/multiple-view evidence remains required. Existing production item has no extra-description translation, so its title-only tooltip is expected. Primary CC ItemUpgradeModel at pinned 5403b707 also intentionally leaves special-renderer getExtents empty; no unsupported production defect/fix is claimed.

Optional pinned REI26.1.819, ModMenu18.0.2 and JEI29.47.0.111 downloaded from publisher metadata/Maven with hashes. Required transitive client mods resolved from publisher release metadata: Architectury20.1.17, Cloth26.1.154 and Placeholder API3.0.0+26.1; runtime combinations not yet accepted. Config-port remains source/graph-grounded N/A from R0/R5, not a removed integration.

## Phase 2 — partial visual proof and genuine upstream failure

Checkpoint commit f2479d7 is explicitly incomplete. Final Fabric base run now exits0 naturally for both dedicated server and actual graphical client. It executes phases0–26 (27 observed phases, not26 tests), captures12 exact screenshot identities, resolves seven real synchronized item models at four lifecycle checkpoints (28 extraction assertions), creates/joins an integrated world, selects the real three-item Peripheralium creative tab, displays/interacts with both book pages, opens a native chest and equipped CC pocket menu, reloads resources, and joins/leaves/rejoins the regular dedicated server over TCP. Stronger post-execution audit validates exact phase/screenshot/model inventories and two real server join/leave pairs; no new execution is invented by that audit. Raw receipt's historical `phases:26` is the maximum phase index; audited receipt separates maximum_phase26 from phase_count27.

The expanded screenshots were actually inspected. Native world view shows both turquoise block upgrades on their turtle sides and the Peripheralium block with real materials; the pocket screen renders CraftOS1.9. A fixture-only language resource supplies an external consumer description for the existing DescriptiveItem hook; native tooltip assertions and screenshots verify it before/after reload. This is not a newly added production description. R6 Lua/network behavior remains inherited against unchanged production hashes, not newly executed by these UI screenshots.

### Required REI combination is blocked, not waived

Actual REI-only client reaches an initialization error screen rather than title. Log cause: `ClassNotFoundException: dan200.computercraft.shared.integration.rei.REIComputerCraft`. A fresh direct fetch of the exact publisher artifact `cc.tweaked:cc-tweaked-26.1.2-fabric:1.120.0` has SHA256 `670446c134d9c3a1ec20f7637b63f681525ebb523fb2a2384ceda53de2b0c1f0`, identical to the accepted runtime. Its fabric.mod.json declares both `rei_common` and `rei_client`, but neither REI implementation is present. Nested JARs are Cobalt/NightConfig/Netty dependencies, not missing REI code. Evidence: primary/cc-rei-packaging.json, final-fabric-rei/client.log and rei-live.png. The original attempt reaches its explicit deadline; termination is a failure, not graceful acceptance. A new runner check fails promptly on the observed REI initialization error.

No entrypoint was suppressed, fake compatibility class introduced, mandatory integration removed or approved pin changed. A corrected compatible CC artifact/upstream fix or an explicitly approved dependency-policy change is needed for this required combination. Independent ModMenu/NeoForge and old joins are still being collected; they cannot turn REI's failure into a full R7 pass.

### Additional inherited visual gap

Native client logs report missing creative_filler variants. Source trace confirms a registered MODEL block and corresponding item; none of the six maintained Tweakium loader JARs contains its blockstate/item/block-model resources. The source/datagen omission predates this card on all nodes. `creative-filler-resource-audit.json` records actual packaged inventories. The main gallery currently covers all three Peripheralium entries plus equipped CC consumer examples, NOT a claim that Creative Filler has passed visual acceptance. Its intended appearance is not invented here; this remains an explicit all-content acceptance gap.

### Boundaries rechecked

All1042 inherited production/build/test-fixture source paths checked match parent66436bc byte-for-byte. Exact compiled R7 private fixture class identities (nine) are absent from all36 inherited main Maven JARs and24 production loader runtime JARs, with artifact digests rechecked. No production edit invalidates R6's candidate/network/save/positive-provider receipts; no full rebuild/datagen rerun is claimed or needed merely for these new opt-in fixture paths. Seven synthetic guard tests pass; they validate failure detection, not Minecraft behavior. Canonical1.20.1 and both hotspot files remain unchanged.

## Final partial matrix — not a completed milestone

All runs below are actual executions. Exact commands, native process exits, artifact hashes, screenshot hashes, XML identities and retained failures are indexed in `partial-receipt.json`, `client-baseline-summary.json`, `independent-commands.json`, `legacy-scene-commands.json` and the durable partial evidence archive. Relative evidence names resolve beneath the scratch evidence root or its archive `evidence/` directory.

| Target26.1.2 / UI combination | Driver | Natural server/client exits | Observed result / evidence directory |
| --- | --- | --- | --- |
| Fabric, no optional UI | 0 | 0/0 | 27 phases,12 screenshots; final-fabric-base (stronger audited receipt retained) |
| Fabric + ModMenu18.0.2 | 0 | 0/0 | 27 phases,12 screenshots; final-fabric-modmenu |
| Fabric + REI26.1.819 | 1 | terminated, not accepted | Missing CC REI implementation; final-fabric-rei |
| Fabric + REI26.1.819 + ModMenu18.0.2 | 1 | terminated, not accepted | Same actual initialization failure; final-fabric-both |
| NeoForge, no optional UI | 0 | 0/0 | 27 phases,12 screenshots; final-forge-base |
| NeoForge + JEI29.47.0.111 | 0 | 0/0 | 27 phases,12 screenshots; final-forge-jei |

Four successful target combinations yield48 exact screenshots, not six passing combinations. ModMenu's actual Mods title control and JEI's actual native chest overlay were visually inspected. JEI is client-only in this matrix; its warning that a non-JEI server may have different recipes is retained. No server-side JEI recipe-sync certification is claimed. Required CC/Kotlin/energy runtime identities match R6; actual positive native provider/Lua/network proofs are inherited only against unchanged source and binary digests. Target optional-dependency SHA256:

| Artifact | SHA256 |
| --- | --- |
| RoughlyEnoughItems-26.1.819.jar | 45d8f488d3c295e97c88a0ad8d0167348f1e3453f22f274f9d7f7c7c97d799cc |
| modmenu-18.0.2.jar | 87ab58f8907dad8346ecc4500a522d66dd4c907462df5bff23c60b07caca8c96 |
| jei-26.1.2-neoforge-29.47.0.111.jar | 06365155a29b06199aa2160489349948cb44072d335125e5f0a5003dcb76c386 |
| architectury-fabric-20.1.17.jar | ec2d4553938f72d176e898a87621b37ef2146a92f58b8c35308f72bf3e013a8e |
| cloth-config-26.1.154.jar | fb7a5d869ce10eaac2c2fe9ab84b4fc011a1fbfa575a93be9845a386a7f0c7d3 |
| placeholder-api-3.0.0+26.1.jar | 7c4893195ab4bec56d05a5fd4521bd9bc8619ef11aa0a5c83a426a99d2191062 |

### Maintained-node client smoke and real joins

All six fresh client harness XML reports contain exactly one executed `StandaloneGameTests.clientFixture`, no failures/errors/skips, with a fresh screenshot: Forge/Fabric1.20.1, NeoForge/Fabric1.21.1 and NeoForge/Fabric26.1.2. Six cases/six reports are a harness pass, not six all-content visual passes.

Four old-node graphical clients also join real same-version isolated production dedicated servers, show native joined-world screenshots, close their real GLFW windows through WM_DELETE_WINDOW, disconnect, then observe natural server save/exit0. Final selected receipts: `legacy-scene-1.20.1-{fabric,forge}`, `legacy-scene-1.21.1-fabric` and `legacy-scene-1.21.1-forge-retry`. Native console chunk-loaded and scene-ready barriers precede the captures. These old clients use actual Gradle named development outputs; their full classpath artifact and development-output hashes are in `legacy-client-classpath-bindings.json`. This is source-bound old client smoke, not invented release-JAR/client-byte equality. All old servers and target clients use the exact retained production JARs.

The old Forge47 loader reads MOD_CLASSES from its environment (verified pinned CommonLaunchHandler bytecode), unlike modern fml.modFolders. The opt-in direct-launch driver now supplies that explicit existing-source environment. Its first missing-registry handshake was a driver/discovery mistake, not a production registration defect. First placement commands also ran before a teleported chunk loaded; final scene fixtures use task-owned forceload tickets and native loaded/block assertions, releasing only their tickets. The old scene may be underground/dim depending on generated terrain; it proves a joined world and reveals the inherited purple/black Creative Filler model, not a polished all-content visual sign-off.

One NeoForge1.21.1 strict scene replay saved all dimensions but exceeded the60s JVM-exit deadline. It is retained as a failed run, never counted as a clean shutdown. A fresh unmodified-production replay exits0 before even the40s diagnostic threshold; no thread dump was triggered and no root-cause fix is claimed. The intermittent shutdown limit remains an explicit risk, not silently erased by the passing retry.

Launchers actually used: Ubuntu OpenJDK17.0.20.1, Temurin21.0.12+8 and Temurin25.0.3+9. Exact maintained/target loader/runtime pins remain unchanged. Offline identities are confined to loopback disposable fixtures under the already recorded project EULA acceptance. Realms/profile-key401 and absent audio-device warnings are not online-authentication or audio acceptance. No original world, shared service, primary checkout, companion repository or profile setting changed.

### Reproduction and preservation

Use the checked-in wrapper with Java25 and retained17/21 toolchains. All Gradle invocations use --no-daemon, explicit deadlines and scratch logs. Representative executed paths:

    python3 scripts/verification/run-native-matrix.py --output <fresh-path> --only 5 9 14 18 23 28
    timeout --foreground 300s ./gradlew -I scripts/verification/client-fixture.init.gradle clientFixtureJars --no-daemon
    timeout --foreground 180s ./gradlew -I scripts/verification/legacy-client-launch.init.gradle legacyClientLaunchInfo -Pclient.launch-dir=<scratch-path> --no-daemon
    timeout --foreground 480s xvfb-run --auto-servernum python3 scripts/verification/run-client.py <exact arguments in commands ledger>
    timeout --foreground 360s xvfb-run --auto-servernum python3 scripts/verification/run-legacy-client.py <exact arguments in commands ledger>
    python3 scripts/verification/test-client-evidence.py

Fixture compilation and unchanged scoped site.siredvin.linting0.9.0 Java rules pass; seven synthetic evidence guards pass. The standalone checkout avoids the inherited linked-worktree/JGit ratchet limitation without disabling lint. Retained failed setup paths include missing background shell exports, the read-only zsh status variable after a successful base run, the two scoped-lint configuration mistakes, an unavailable initially selected Java17 path, the private screen/disconnect predicates and the old mod-discovery/chunk-order errors. None contributes passing test evidence. All14 known Git identities recovered from original plan/coverage/wiki and R0–R6 receipts are verified ancestors. Source/binary boundaries are preserved as above; R6 evidence archive identity is rechecked in production-fixture-isolation.json.

Durable output is `build/verification/t_14e20aca-R7-partial-evidence.zip`, with a per-member SHA256 manifest and independently read-back CRC/hash verification; adjacent `t_14e20aca-R7-partial-delivery.json` identifies the exact cumulative local commit and archive digest. These are explicitly partial receipts, not an accepted final candidate for F. Existing final owner t_16b4e35f was inspected and remains dependency-gated on this unfinished milestone; Chrono ownership is unchanged.

## Required next decision and remaining gates

The original REI decision request is superseded by the dashboard instruction recorded below. Creative Filler still needs a real intended model/resource implementation and owning visual regression; no appearance was invented. Resolve remaining gates, replay affected artifact/datagen/native/server/client combinations against exact new identities, retain the intermittent shutdown finding, then produce the actual final R7 candidate receipt before releasing F. No push, primary merge, release or remote publication is authorized or performed.

## Resume — authorized REI removal / EMI primary

Dashboard instruction: "Remove REI completely, just use EMI as primary mod, remove REI support from 1.20, 1.21 and 26.1". This supersedes the earlier REI requirement and permits the recipe-viewer dependency change; it does not change Minecraft/loader/CC pins or authorize forcing incompatible artifacts. Prior failed REI runs and immutable planning documents are retained as historical evidence rather than rewritten.

At resume HEAD4490efb was clean; planner b8fd1dd and R6 66436bc ancestry checks exit0. Primary-source EMI release inventory is cached under `$TMPDIR/t_14e20aca-validation/emi-policy`: official Modrinth project `emi` version API, Sleeping Town `dev/emi/emi-fabric/maven-metadata.xml`, official repository branches and README. Their retrieval commands and hashes will be retained with this phase's receipt. Official1.1.24 artifacts exist for Fabric/Forge1.20.1 and Fabric/NeoForge1.21.1. Neither publisher inventory advertises a26.1.x artifact, and the official repository has no26.1 branch. Do not force-load a1.21.1 artifact into26.1.2 or invent target EMI acceptance.

Added an owning static policy regression; executed red exit1 (existing REI aliases/defaults), then replaced the catalog aliases/bundles and six old-loader build sections with pinned EMI1.1.24 and its publisher Maven, preserving publication exclusions. All three policy tests now pass; seven preexisting synthetic client-evidence guards still pass. These are policy/validator tests, not Minecraft execution. Target build convention has no REI dependency; removed the obsolete REI-specific failure shortcut from the opt-in driver. There was no project-owned REI source integration or entrypoint to remove. Upstream CC JARs remain byte-for-byte untouched. JEI aliases remain available for the independently required optional NeoForge combination, but are no longer the old-line default.

### Verified maintained-node replacement

Actual wrapper `legacyClientLaunchInfo` resolves and compiles all four old-node client routes. All six adapter build sections and catalog bundles select EMI as primary; no active Gradle/catalog REI dependency remains. Fabric uses Loom `modLocalRuntime` for EMI/ModMenu. NeoForge uses a non-consumable `localRuntime` extended only into `runtimeClasspath`; legacy Forge's existing remapped `modRuntimeOnly` already stays out of published metadata. No incompatible target EMI coordinate was added, and no upstream CC entrypoint or artifact was patched.

Two real failures were caught rather than hidden:

- Merely replacing the POM exclusion still leaked EMI into Gradle Module Metadata on nine of twelve affected publications. `publication-isolation-red.json` retains that failure. Moving the dependency to local development configurations fixes the actual boundary; fresh POM/GMM checks across all twelve publications pass.
- MDG `clientAdditionalRuntimeClasspath` added the library to its legacy classpath file without making EMI discoverable as a mod on this pinned run. The NeoForge1.21.1 join receipt still passed, but the actual inventory screenshot had no EMI sidebar and its log had no EMI plugin. That run (`smoke-final-1.21.1-forge`) is explicitly rejected as EMI evidence. Added `--require-emi`, exercised its red result against that actual failed log, then changed the dependency boundary to `localRuntime`. A fresh `smoke-accepted-1.21.1-forge` run exits0, completes the actual EMI and CC plugin reload and displays the real EMI overlay. No injected plugin or fabricated compatibility class is used.

Selected real executions:

| Minecraft / loader | Evidence directory under emi-policy | Actual server/client exits | EMI evidence |
| --- | --- | --- | --- |
| 1.20.1 Fabric | smoke-final-1.20.1-fabric | 0/0 | CC plugin initialized, EMI reload complete, real native creative inventory/sidebar/search inspected |
| 1.20.1 Forge | smoke-final-1.20.1-forge | 0/0 | Same actual inventory/plugin proof |
| 1.21.1 Fabric | smoke-final-1.21.1-fabric | 0/0 | Same actual inventory/plugin proof |
| 1.21.1 NeoForge | smoke-accepted-1.21.1-forge | 0/0 | Fresh post-fix replay; same actual inventory/plugin proof |

All selected clients join and leave real isolated same-version production dedicated servers, whose save/shutdown completes naturally. For the three retained executions, `retained-execution-audit.json` independently verifies identical final launch manifests, every captured classpath/development-output/argument-file hash, receipt/screenshot hashes and the stricter plugin guard. No rerun of those three after an unchanged-launch audit is invented. The target convention and both target artifact sets remain untouched; earlier target base/ModMenu/JEI client results and R6 network/save proofs are inherited against unchanged production identities, not newly executed EMI target runs.

All four selected native inventory screenshots were inspected. EMI dev mode displays one untranslated upstream `computercraft:turtle_can_place` tag warning on Fabric1.20.1; Forge1.20.1 displays161 warnings (that tag plus upstream synthetic brewing recipe-ID validation). Their logs and screenshots are retained, not filtered or disabled.1.21.1 inventories show EMI dev mode without that warning count. Old Creative Filler purple/black geometry is still visible in the scene, matching its separate inherited missing-resource defect. These runs prove startup/join/plugin reload/inventory overlay, not exhaustive EMI recipe transfer, search interaction or recipe correctness. A missing optional JEI mixin target, headless audio failures and offline-authentication warnings are also retained, not presented as new production defects.

Publisher artifacts (official SHA512 also checked against release metadata); Fabric/legacy Forge remapped runtime digests are separately bound in the command ledger:

| EMI artifact | SHA256 |
| --- | --- |
| 1.1.24+1.20.1+fabric | 537b062e0dc6f028cf84cf66a473ed1b8842219a708d17cdb292401441b0f367 |
| 1.1.24+1.20.1+forge | 92784dcf5222d72983a420c90f2e9240468ee0fece96f757c82d7884b6cbdb72 |
| 1.1.24+1.21.1+fabric | 56ecd418e988cba23dd653d498309fd7b886265708253d5ca7a8fb621dcc5db4 |
| 1.1.24+1.21.1+neoforge | b68691f94f0727fc517cac2ab55bd6658d1179cc8cc715fe610b60c37469bc4c |

### Commands, publication boundary and remaining decision

Exact commands/exits are in `publications-v6-command.json`, `smoke-final-commands.json`, `smoke-accepted-commands.json` and raw build logs. All Gradle runs used the checked-in wrapper, --no-daemon, verified Java25 and explicit180/300-second deadlines; real graphical launches used Xvfb/360-second deadlines with Java17/21 selected per old node. Final affected-publication/launch batch exits0 (205 tasks:56 executed,149 up-to-date). Explicit non-consumable/non-resolvable local dependency roles leave all four final launch manifests identical to those already bound above. The full existing publication checker is applied to the affected three libraries, two loaders and two old versions:12 actual publications pass its dependency/capability/source/artifact/JVM/loader guards, including the added recipe-viewer leak guard. An independent audit checks every variant in all twelve POM/GMM pairs. Each of the twelve rebuilt production JARs has exactly the same entry payloads as its inherited real production-server artifact (ZIP byte equality is not asserted); no private fixture or EMI code can have been added to those identical payloads.

Three static recipe-policy tests and ten synthetic evidence-validator tests pass. The latter includes rejecting a mere join without EMI and rejecting incomplete plugin reload; none is counted as Minecraft execution. Scoped KotlinGradle Spotless checks pass in this task's exact-source standalone checkout with the original ratchet intact; no lint setting was weakened. Production Kotlin/Java/resources, both hotspot scripts and canonical1.20.1 state remain unchanged. No new datagen/native-unit campaign is claimed for this development-dependency-only change; inherited native/save/network receipts remain explicitly inherited.

`emi-receipt.json` binds current source hashes, publisher metadata/JAR identities,12 publication/artifact proofs, four selected real executions, rejected runs and explicit remaining gates. Durable phase archive: `build/verification/t_14e20aca-R7-emi-evidence.zip`, with adjacent delivery JSON and per-member SHA256/CRC verification. This supplements, not replaces, the earlier R7 partial archive. F t_16b4e35f was read back: todo, dependency-gated on this unfinished card; Chrono ownership unchanged.

Actionable external requirement: provide an actual compatible EMI26.1.2 artifact/source (and approve its provenance if not official), or explicitly choose a target-only alternative/deferment policy. Official EMI's714-entry release inventory, publisher Maven metadata and official branches do not advertise a26.1.x build. The old1.21.1 JAR must not be force-loaded into26.1.2. REI removal is implemented and the former CC/REI blocker is superseded, not carried forward as a requirement. Creative Filler's intended appearance/resource regression remains a separate all-content gap. R7 is not complete and no final candidate is released; no push, primary merge, remote publication, release, shared-service restart or profile change occurred.
