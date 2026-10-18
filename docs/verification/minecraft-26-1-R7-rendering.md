# R7 rendering lane — t_5eb14799

Status: INCOMPLETE checkpoint, 2026-10-18. Creative Filler appearance/resources remain unapproved and missing. Combined interaction hooks have two observed runtime failures handed back to their owner. No all-content, final-candidate or R7/F acceptance is claimed.

## Source custody and ownership

Assigned worktree fast-forwarded from27798ce to exact preparation d43491fb943e1458c8984b539d56aa57d28a206d; ancestry verified. Material proof checkpoint d281b6e; coordinated interaction source1ec8a021f485ae13ca3a248cbb6592bde187871c merged at39b95d7; exact owner-requested hooks committed atc13c9a94a429848b0f4666808cc27d0f8425f016. No external coding agents, remote publication, primary merge, production edits, dependency changes or shared runner/schema changes.

Rendering owns ClientProof/RenderProof and its new check-rendering.py/test-rendering-evidence.py extension. Interaction owns ClientFixture/InteractionProof/InteractionMenus and its regressions; owner was notified of exact defects and asked for correction SHAs. Root build/catalog/controller/report guards remain untouched. Canonical1.20.1 unchanged.

## Inspected contracts and implemented regression

- Target FacingBlockTurtleModeller resolves crafting ItemStackTemplate only during extraction into a fresh TrackingItemStackRenderState; the special submit callback accepts extracted state, pose and typed submission collector only, with no live world/player access. No production adapter defect was demonstrated; avoid speculative replacement.
- Both actual typed registration adapters are retained: FabricComputerCraftAPIClient model registration in physical client entrypoint and NeoForge client-only RegisterTurtleModelEvent subscriber. Real codec decode and actual diamond-block upgrade quads on both sides exercise registration/baking, beyond merely constructing the class.
- Shared RML operations/parser only transform PoseStack; no live world reads. Two focused target RML cases execute freshly with0 failures/errors/skips (XML timestamp2026-10-18T20:15:04.303Z). No RML language behavior changed.
- Peripheralium target provider emits two flat item models and block provider supplies the block; submitted quads resolve actual item/block atlases on both loaders. No generated file was hand edited.
- Inspected target Testiarium ClientTestHooks and CctComputers. These are harness scheduling/client-world bridges, not production model submission adapters; no changes necessary for scoped proof. Interaction lane owns their native/CCT replay.
- Fixed a real test gap: existing ClientProof accepted any nonempty state, including the missing Creative Filler model. New target-only RenderProof uses typed vanilla SubmitNodeStorage.submitItem with BakedQuad.MaterialInfo. It rejects empty geometry, missing sprites and absent item render types; asserts actual diamond materials for both turtle sides and CC advanced-pocket materials; mutates the original input stack after extraction, resubmits and compares submitted materials/poses; asserts balanced successful submission poses. No private model-field reflection or loader-class imports into common production.
- Four lifecycle checkpoints2/12/16/23 cover integrated world, resource reload, dedicated join and reconnect. Each records seven models plus explicit expected Creative Filler missing-material rejection in materials.log. The latter is a negative proving an open defect, NOT all-content success. Pocket CC resources render its computer frame/state, not an arbitrary crafting-item upgrade icon; no false diamond requirement remains.
- New checker independently enforces ordered exact checkpoints/items/materials/snapshots, receipts/process exits, mod identities and screenshot hashes. Seven guard tests cover valid scoped proof, empty quads, nonempty missing sprite, omitted upgrade, lost reload checkpoint, changed snapshot and attempted blocker waiver. Synthetic guard inputs are only unit fixtures, never Minecraft evidence. Ten existing shared evidence guards also pass.

## Real execution and retained failures

Evidence root: this worktree's ignored build/verification/rendering/. All screenshots are outside tracked source.

1. prepare.log: both loader private fixtures and launch manifests compile via wrapper, Java25,300s bound, --no-daemon. proof-compile.log also executes the two focused target RML JUnit cases. proof-compile-retry.log/proof-compile-final.log/hook-compile.log succeed.
2. Initial fabric-base/forge-base fail at phase2 because an intentionally throwing sink expected whole-stack exceptional restoration. Pinned vanilla LayerRenderState pushes its own frame without finally; that whole-stack assertion exceeds the custom adapter boundary. Removed the invalid assertion, not production safety. The custom callback's own finally and normal balanced poses remain source/runtime checked. No exceptional-stack certification is claimed.
3. retry/*-base fail at phase2 because the test incorrectly demanded diamond crafting-item material from the pocket computer. Inspected pinned CC pocket item definitions: they render frame/computer state, not the turtle crafting model. Corrected expected materials; no production workaround.
4. final/fabric-base and final/forge-base execute naturally to exit0 for both client/server. Each completed27 phases,12 exact screenshots and two actual dedicated joins. Combined:24 screenshots/four joins. Each raw materials.log has28 positive model assertions/four explicit missing-material negatives, all stable snapshots and balanced successful poses. Exact receipts/process-exits/commands/logs remain. Independent material-log checks pass for both.
5. IMPORTANT evidence limitation: run-client.py symlinks fixture inputs. Subsequent hook fixture rebuild changed earlier final/*/client/mods/tweakium-client-*.jar targets. Their receipts retain original execution SHA, but no immutable old JAR copy was preserved. Full new material checker correctly rejects those retained mutated input paths. These real pre-hook executions are NOT fully preserved exact-JAR/final-candidate acceptance. Do not rewrite receipt hashes to make them pass. Subsequent runs MUST use immutable copied fixtures. Hotspot reported to both matrix owners; shared runner not edited.
6. combined/fabric-jei on actualc13c9a9 fails phase2: InteractionProof.verify constructs HiddenDescriptiveItemItem after frozen registry, throwing 'This registry can\'t create intrusive holders'. Rendering material proof completes before this failure. combined/forge-jei fails dedicated-server construction: InteractionMenus.register -> ForgeInnerBasePlatform.registerMenu:59 NPE; no natural ready signal. Both exact failures reported to interaction owner (comments152/153), no pass claimed. c13c9a9 is compile-verified, NOT runtime-accepted.

Visual inspection: final Fabric03-content-models-tooltip and NeoForge12-reconnected-models visibly show all seven sample icons with no magenta missing texture, native dust tooltip and the near-side diamond upgrade. The far-side attachment is partly occluded at the gallery angle; actual submitted quad material checks cover both. Pocket state changes from blank/off to terminal/on after reconnect. These screenshots do not show Creative Filler and do not certify every world-space geometry/material. Offline chat/tutorial overlays remain visible, not hidden.

## Exact commands and manifests

Preparation command (300s foreground timeout):

    ./gradlew -I scripts/verification/client-fixture.init.gradle -I scripts/verification/network-launch-info.init.gradle clientFixtureJars networkClientLaunchInfo networkForgeLaunchInfo -Pnetwork.launch-dir="$PWD/build/verification/rendering/launch" --no-daemon --console=plain

Focused test/compile command (300s foreground timeout):

    ./gradlew -I scripts/verification/client-fixture.init.gradle clientFixtureJars :tweakium:26.1.2:test --tests site.siredvin.tweakium.RMLTests --no-daemon --console=plain

With JAVA_HOME=/home/siredvin/.gradle/jdks/eclipse_adoptium-25-amd64-linux.2 and PREREQ=/home/siredvin/projects/Minecraft-Modding-Libs/.worktrees/t_a5cdac1f/build/verification/prerequisites, executed launch-case.sh for fabric-base/forge-base at initial, retry and final roots, then fabric-jei/forge-jei at combined root. LOG_DIR selected each root; original fresh launch manifests copied into its launch/. Helper supplies480s timeout/Xvfb and filters all viewer/transitive paths from shared server/client classpath before physical client-only extras. Exact actual process argument arrays are each case's commands.json; execution failures are retained in outer case.log, process-exits.json and control/failure.txt or server.log. All inherited production server mod identities guarded by prerequisite helper before launch; production JARs unchanged. Current accepted dependency policy remains JEI29.47.0.111 both targets, no REI, legacy EMI unchanged, config-port conditional N/A.

    PYTHONDONTWRITEBYTECODE=1 python3 scripts/verification/test-rendering-evidence.py
    PYTHONDONTWRITEBYTECODE=1 python3 scripts/verification/test-client-evidence.py
    PYTHONDONTWRITEBYTECODE=1 python3 scripts/verification/check-rendering.py <run-directory>

Seven rendering guards and ten shared guards pass. Full checker against historical final/*-base correctly FAILS changed fixture identity as above; isolated validate_materials over actual raw logs passes4/28/4 per loader. Do not mislabel these as full receipt audit passes. Launch manifests are launch/*-client-info.json plus per-root filtered copies; prerequisite dependency-manifest.json has immutable coordinate/hash provenance. Bytecode inspection logs retained, including failed guessed class names before actual discovery.

## Open gates and exact next actions

1. Human input: specify Creative Filler's intended appearance, or explicitly approve vanilla dropper-model/texture reuse. Current target generator registers only languages and missing blockstate/item/model assets cannot be invented. After approval add owning failing resource/material regression, implement datagen providers, regenerate deterministically, expand gallery and replace expected negative with positive assertion.
2. Interaction owner must correct the actual registered private tooltip probe and NeoForge menu registration ordering, provide exact local SHA, then merge correction without overwriting ClientProof/RenderProof ownership.
3. Before rebuilding again save fixture/main JAR copies under immutable evidence paths; use copied --fixture and assembly inputs for both-loader fresh scoped smoke. Run both check-rendering.py and check-interactions.py on those exact receipts. Do not count pre-hook or failed combined attempts as current passes.
4. Integration merges rendering then final interaction cumulative SHAs, legacy1269644 once, and replays six optional combinations, target Lua22/27/providers19/24, affected native/client/CCT, datagen/publication/fixture-isolation and ordinary production/affected R6 bindings. Scoped rendering proof does not replace positive energy or adversarial networking.

Coverage rows remain open: R7:1/R7:4 rendering/regressions; related PLAN:L107,L109,L111,L113,L116,L119 and DECISION:06,10,12, plus R7:2/R7:3 matrix gates. Immutable coverage map was not edited. Both matrix children and parent were inspected; no downstream release/final-card completion before actual missing resource/design gate is resolved. Local commits/evidence archive identities are recorded in card comments to avoid a self-referential report hash.
