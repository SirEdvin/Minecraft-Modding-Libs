# R4 — Peripheralium content and generated resources

Status: COMPLETE — card-local R4 content/resource capability. Historical incomplete checkpoints below are chronology. Full migration acceptance remains R5–R7/F.

## Final source and compatibility

Source commit: `3c3b5d4c9ac5a3dcc4130e4cc75008bd112bf826`, cumulative over immediate predecessor `ec9f18a9df1092afde543886c5d50829ce87cc05` and planner `b8fd1ddfd5134c45402040fe2c1ca5be343ac48f`. All earlier R0/R1/R2/original plan/coverage/OpenWiki ancestors verified. Exact final documentation/cumulative commit is in the task completion handoff.

Target stays Minecraft26.1.2, NeoForge26.1.2.122, Fabric Loader0.19.5/API0.155.3+26.1.2, CC1.120.0 (`-forge` coordinate), FLK1.13.12+kotlin.2.4.0/KFF6.4.0, energy5.0.0, Stonecutter0.9.8, Gradle9.5.0, Loom1.17.11, MDG2.0.142, KGP2.4.0 and verified Temurin25.0.3+9. Worker memory limit8GiB observed before bytecode inspection; old17/21 toolchains retained. Settings, properties, catalog and four controllers are byte-identical to R3; canonical1.20.1 never switched. No release bump, disabled integration, new persistence ownership, push, primary merge, publication or service change.

Preserved modern three-item inventory (dust/template/block item), one block, creative tab and four recipe identities; target intentionally has no legacy blend/smelting recipe. Registration supplies native resource keys before block/item construction. Existing common core/creative builder and Fabric lifecycle are reused; NeoForge registries attach to its native mod bus. Runtime/native server tests resolve these classes without physical-client linkage failures; that is not a production ready/stop receipt.

Reviewed all31 modern Peripheralium override paths, each with a direct target counterpart. `modern-audit.json` retains source/target hashes and full diffs. Target model helpers emit native client item definitions, block state/model and flat-item models; recipes use registry-aware native runners/templates; loot uses native codecs. Existing language values/keys remain complete. Empty Forge mixin descriptor is target-versioned with the correct own package; there are no active Peripheralium mixins to migrate. Obsolete old beehive/model access rules are replaced by the used target helper access from Broccolium plus owning creative-output/visibility and RecipeProvider.Runner rules. Datagen and native launch exercise those boundaries.

Only target Broccolium ingredient API/adapters change outside the content port: explicit registry lookup overloads let providers resolve tag holders before a live server exists, while existing getter contracts remain. Both original loader-specific glowstone policies are preserved. No old source or generated resource is changed. Save/world upgrades, cross-version network peers and external add-on binaries are not certified here; no persistence/network format redesign was introduced.

## Actual execution gates

All commands use the wrapper, `--no-daemon --console=plain`, explicit240/300s bounds and scratch logs under `${TMPDIR}/t_d2dea3b5-validation`. Datagen additionally uses `xvfb-run --auto-servernum`. `commands.json` retains exact argv/status/log mapping, including failures and the interrupted first attempt; `final-receipt.json` records exact report identities/counts/hashes and59 changed non-document source/resource hashes.

### Loaded native content and old regressions

| Version | Fabric task / cases | Forge-family task / cases |
| --- | --- | --- |
|1.20.1|`:tweakium:fabric:1.20.1:runPeripheralGameTest` /23|`:tweakium:forge:1.20.1:runGameTestServer` /23|
|1.21.1|`:tweakium:fabric:1.21.1:runPeripheralGameTest` /25|`:tweakium:forge:1.21.1:runGameTestServer` /26|
|26.1.2|`:tweakium:fabric:26.1.2:runPeripheralGameTest` /36|`:tweakium:forge:26.1.2:runGameTestServer` /36|

All six routes exit0:169 actual cases, zero failures/errors/skips. Exactly five Peripheralium fixtures execute on each route (30 owning invocations total). Target exact inventory retains all30 R3 cases plus five content cases and vanilla always_pass, once each. Fresh versioned XML is reconciled against declared summaries and task/run freshness; no stale/unversioned or interrupted report is counted. Logs: native-old1201.log, native-old1211.log and final-target-build-native-format-guards.log.

Target fixtures exercise native registry identities/tracker membership and creative tab contents/icon; actual loaded dust/block/uncraft/template crafting and reverse shapeless inputs; wrong-center refusal; four loaded recipe displays; provider codec output/no unintended advancement output/duplicate definition and malformed shape refusal; both complete packaged EN/UA files compared with providers; actual world placement/native block destruction and exactly one registered block-item drop from loaded loot. Old routes execute their own version-appropriate registered content/provider/crafting/loot/language regressions unchanged.

Peripheralium-owned fixtures remain common testMod outputs, contributed only to Tweakium private runner classpaths and loaded-mod models. `dependency-boundaries.log` exits0 after inspecting six actual production runtime classpaths: no fixture/harness leakage and no reverse Tweakium→Peripheralium production dependency; both private loader runners contain the required content outputs.32 actual owning report-guard scenarios pass, including missing/duplicate/unexpected content cases and missing entire content suite. These isolated corrupt-report probes are not native execution evidence.

### Deterministic datagen, builds, units and artifacts

datagen-3.log and datagen-4.log both exit0 for `:peripheralium:fabric:26.1.2:runDatagen`, `:peripheralium:forge:26.1.2:runClientData` and `runServerData`.14 exact JSON resources per loader are byte-identical between passes: three client item definitions, three model files, block state, two languages, four recipes and one loot table. Paired loaders differ only in inherited dust glowstone policy and NeoForge's native loot random_sequence; loot payloads otherwise match. No owning tag provider exists: conventional tag references are preserved, loaded and successfully craft. All output was generated through providers; datagen caches are untracked/excluded, and no cross-version tree changed.

`build-target.log` and final-target-build-native-format-guards.log exit0 for `:peripheralium:26.1.2:build`, `:peripheralium:fabric:26.1.2:build` and `:peripheralium:forge:26.1.2:build`. Peripheralium has no unit test bodies; its native tests above are the owning behavioral evidence, not a zero-test unit claim. Adjacent changed Broccolium target nodes ran explicit cleanTest/test: common186, Fabric241, NeoForge135, total562 executed units, no failures/errors/skips (adjacent-units-guards.log).22 scoped Java/Kotlin source files pass unchanged formatter rules; explicit full-file targets replace only the worktree JGit ratchet. `git diff --check` passes. Global formatter/build/publication campaign remains R5.

Three release JARs pass ZIP integrity, class-major69, production class/metadata/access rules and generated-resource byte equality checks.181 actual unique compiled private test/testMod/testFixtures/CCT class identities are excluded, as are Testiarium classes, computer scripts, caches and legacy blend/smelting resources. This is owning release-JAR isolation, not full Maven publication/consumer proof.

| Artifact | SHA-256 |
| --- | --- |
|peripheralium-common-26.1.2-2.0.0.jar|fc23d5e4db8e6287b4e77b5675c1c223b3a4f915d1ea0b6fb934871fb4ba4803|
|peripheralium-fabric-26.1.2-2.0.0.jar|8f05b6c77f9cd9c062da7f29549d1815f35daa1073ee44a3b41fcf40f6c24857|
|peripheralium-forge-26.1.2-2.0.0.jar|f1504f6199ed6006ae62b988b51988e67547844c781b334c64727fa23e674b0a|

The task evidence ZIP contains full command ledger/logs, executable collectors, exact XML identities and preserved failed XML, datagen inventories,31-row modern audit/full diffs, source hashes, scoped-lint/boundary checks and these three JARs. Durable artifact location/checksum is in the task handoff.

## Ownership and downstream limits

All four owning coverage rows R4:1–4 map to the source/native/data/build receipts above. Immutable204-row campaign ownership map is not rewritten into a campaign-wide pass. R5 (`t_f0a99e8c`) must merge the final cumulative SHA and owns full all-version builds/global lint, source/publication metadata, file-Maven consumers and CI. The additive target ingredient API changes invalidate earlier exact-source Broccolium artifact receipts; refreshed units and target native routes are provided here, and R5 must assemble refreshed cumulative artifacts rather than reuse old JAR hashes blindly.

R6 owns production dedicated ready/stop, copied saves/process restart and multiplayer/adversarial network checks; R7 owns visual/client/optional integration combinations. Native GameTests are not production startup, copied-save migration or visual proof. F alone owns final OpenWiki/skills/Obsidian reporting. Hotspots remain gradle/minecraft-26-1.gradle.kts and gradle/gametest-reports.gradle.kts; keep the exact extended fixtures/inventory when modifying them.

## Historical initial reconnaissance and phase checkpoints

Merged planner b8fd1ddfd5134c45402040fe2c1ca5be343ac48f and immediate predecessor ec9f18a9df1092afde543886c5d50829ce87cc05 by fast-forward; both ancestry checks exit0. Read original brief, source ledger, remaining plan and R3 receipt. Execution authorization on the card supersedes historical planning holds. Canonical1.20.1 retained; no push/publication/release authorized.

Target exact26.1.2 with inherited pins and Java25.0.3+9 launcher verified. Existing Peripheralium has no target overrides. Modern intentionally has three items including block item, four recipes, no blend; canonical fallback is not parity. Providers must generate target resources; no generated JSON editing.

Scope: target keyed registrations and loader lifecycle, modern content/resource providers, private Peripheralium fixtures in Tweakium native routes, deterministic owning datagen, three target branch builds and adjacent old content checks. R5 owns full artifact/publication matrix, R6 process/save/network, R7 visual integration.

Next: resolve actual target registration/provider signatures, add target overrides and private fixture wiring; run compiler baseline then native behavioral assertions. Record exact commands/fresh reports and any behavioral red before behavior repair. Acceptance remains unperformed.

## Phase 1 — port and initial native proof, incomplete acceptance

Target common/loader content and provider overrides now compile. Real Fabric datagen exposed live-server-only ingredient lookup (`datagen-fabric-2.log`, exit1, FabricPlatformToolkit.lookup null before a server exists). Added target-only registry-aware ingredient overloads and passed the provider's own lookup; both loaders preserve their redstone/glowstone tag policies. Real datagen-3.log exits0 for Fabric runDatagen and NeoForge runClientData/runServerData. Generated14 JSON resources per loader, including three native client item definitions; no hand-edited generated output. Repeated generation and full inventories remain pending.

Native Fabric `timeout --foreground 300s ./gradlew :tweakium:fabric:26.1.2:runPeripheralGameTest --no-daemon --console=plain` exits0, native-fabric-1.log:36 cases, no skips; exact inventory guard includes all30 R3 cases plus five Peripheralium cases and vanilla always_pass. Content registry/creative tab, loaded crafting recipes/displays, wrong-center refusal, provider output/pattern validation, packaged EN/UA and native block-break loot pass. This initial pass is not final acceptance; NeoForge and old regressions remain pending. Initial compile errors and first timed-out/namespace-failed datagen are retained, not behavioral passes.

Private content fixtures join only Tweakium testMod/run mod models, not production dependency configurations. Common target excludes its obsolete canonical named widener so loader official access files actually load. Required target access expands RecipeProvider.Runner and creative output/visibility only.

Next exact actions: capture generated-resource hashes; rerun three data tasks and compare; run NeoForge native route and all four affected old content routes; build owning three branches, inspect fixture isolation and validate fresh XML inventories. HEAD remains ec9f18a; dirty paths are target Peripheralium trees, three Broccolium ingredient API/adapters, two target Tweakium test entrypoints and owning Gradle convention/report sections.

Hotspots: gradle/minecraft-26-1.gradle.kts (owning content fixtures/datagen only); gradle/gametest-reports.gradle.kts (extend inventory without removing Tweakium cases).

## Phase 2 — native capability and bounded acceptance green; receipt pending

All six native content routes executed freshly and passed:1.20.1 Fabric23/Forge23;1.21.1 Fabric25/NeoForge26;26.1.2 Fabric36/NeoForge36. Each includes exactly five Peripheralium owning fixtures; zero failures/errors/skips. Exact target guard retains all30 R3 cases and permits only vanilla always_pass additionally. A first NeoForge fixture failed packaged-language lookup from the wrong Java module (native-forge-language-red.xml); fixture now reads resources through PeripheraliumCore's owning module. Production language output was present; no production relaxation.

datagen-3.log and datagen-4.log exit0 for all three real owning tasks. collect-r4.py verifies14 exact JSON paths/hashes per loader, unchanged after regeneration. Paired loaders differ intentionally only in dust glowstone ingredient (Fabric concrete item versus NeoForge conventional tag, inherited policy) and NeoForge's native loot random_sequence; loot payloads otherwise identical. No Peripheralium-owned tag provider exists to port; existing conventional tag references remain loaded and craft successfully. All generated assets originate from providers.

build-target.log and final-target-build-native-format-guards.log exit0 for all three owning target build tasks; Peripheralium has no unit test bodies, so native proof is the owning execution evidence. Adjacent three Broccolium target cleanTest/test nodes execute successfully in adjacent-units-guards.log (final parsed counts pending). Final target native rerun passes after metadata/fixture formatting changes; expanded actual guard probes reject missing/duplicate/unexpected content suites as well as prior corrupt inventories. Scoped changed Java/Kotlin files pass unchanged formatting rules with explicit full-file targets replacing only the JGit ratchet. Full global lint/publications remain R5.

dependency-boundaries.log exit0: six actual production runtime classpaths contain no private fixture output/harness dependency, Tweakium has no reverse Peripheralium production edge, both loader private runners include content main/testMod outputs. Remaining before completion: reconcile final unit/report counts and modern override/source hashes; inspect three release JARs against actual compiled fixture identities/access/metadata; commit scoped source and attach durable evidence. No campaign-wide acceptance yet.
