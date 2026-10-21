# OpenWiki diagram campaign

## Milestone 3 — t_6fb747f5

Imported cumulative c5123e9055b5a10632597bfd1810d119e2348880 by fast-forward; verified both that parent and 0e9f0681bf70032e4a9e94c1b7423994c270d9b1 ancestry. Completed the seven remaining factual subsystem/workflow pages and overview/quickstart drill-down navigation. Added 17 diagrams; cumulative total is 48 across 21 diagram-bearing pages plus the quickstart task map. The complete inventory below covers all meaningful existing modules, not an arbitrary diagram quota. No runtime/library, build/dependency, renderer/config, AGENTS or workflow changes on this card. Independent review/publication remains t_5f8c95c0.

Source reconciliation preserves 1.20.1, 1.21.1 and 26.1.2 distinctions: blend/smelting only on legacy Peripheralium; keyed target construction and modern criterion registers; target Testiarium function/instance/environment registration versus old TestFunctions; overlay-gated target client ticks; Java action monitor versus Lua marker/error state; separate fixture features and production output; TypeScript contracts independently maintained outside Stonecutter. Production arrows were rechecked against actual direct dependencies, including Peripheralium → Broccolium. No reverse production Testiarium edge was added.

### Cumulative verification and limits

Actual checks reuse pinned MkDocs 1.6.1, Playwright 1.56.0/Chromium1194 and Mermaid 12.2.0:

```sh
PYTHONDONTWRITEBYTECODE=1 "$TMPDIR/openwiki-diagrams-venv/bin/python" -m mkdocs build --strict --site-dir "$TMPDIR/t_6fb747f5-diagrams-evidence/pages/openwiki"
PYTHONDONTWRITEBYTECODE=1 "$TMPDIR/openwiki-diagrams-venv/bin/python" scripts/verification/check-openwiki-diagrams.py --site "$TMPDIR/t_6fb747f5-diagrams-evidence/pages" --output "$TMPDIR/t_6fb747f5-diagrams-evidence"
PYTHONDONTWRITEBYTECODE=1 "$TMPDIR/openwiki-diagrams-venv/bin/python" "$TMPDIR/t_6fb747f5-visual-navigation.py"
git diff --check
```

Strict build and all-diagram audit exit 0. All 48 blocks render at 1440×1000 and 390×1000: 42 page/viewport runs, 96 SVG observations, titles/descriptions, viewBox containment, natural-size fonts/pixels and complete scroll-region reach pass. Static audit passes 22 factual pages, 70 Claims, 179 evidence references, bounded repository spans and manifest/Claims version agreement; 2,105 built links/anchors pass. Normal console/page errors: zero. Deliberate CDN failure retains fenced source and ordinary navigation. Parent charts were included in every cumulative replay. No renderer support changes required new regression machinery.

Actual browser navigation passes all six overview → Peripheralium/Testiarium/TypeScript entry → focused heading → overview routes (three at each width), plus quickstart → CCT detail → harness → overview at both widths. Targeted left/right screenshots cover content/platform, registration, harness/reporter, client/CCT, TypeScript and publication/report charts, plus inherited storage/dispatch; full-page screenshots cover every chart. Visual inspection checked representative classes and sequences, edge labels, grayscale contrast and non-color semantics. The dense TypeScript class chart was split into two smaller models after inspection. Long sequences intentionally scroll horizontally in the fixed desktop content column and phone viewport; a single screenshot cannot show every lifeline. Adjacent summaries and accessible SVG descriptions retain the whole meaning.

Supported initial run dd239424-469b-43d7-8b34-d7ac02139e69 submitted nine sequential jobs; finish returned complete/sourceChanged=true because the report changed during authoring, and persisted interrupted. Supported polish run 44bb9d01-355b-463e-a8b0-7867978d63ad submitted four sequential jobs and finished complete. It split the dense class model, corrected the target fixture wiring citation and qualified modern criterion registration; Claims were inspected before clarifying existing prose, with current IDs retained. Final stable-source closure and exact cumulative commit/ref/SHA are recorded in the Kanban handoff and durable evidence receipt after this report is committed, avoiding a self-referential hash loop. Retrieval search and exact section readback passed for CCT channels and source-to-artifact selection.

Only documentation checks were executed: Minecraft, TypeScript compiler contracts and historical native/production campaigns were inspected/inherited, not rerun. No hosted CI/deployment, GitHub-native Mermaid rendering, npm/Maven release, push, PR or merge on this card. The renderer still needs its pinned CDN for SVG and is not an offline/vendor/security audit. Existing AGENTS scheduled-refresh wording remains inherited despite no generator workflow; supported refresh instructions below do not promise automatic permanent diagram preservation.

### Coverage decisions

- All five projects have overview → subsystem → focused model/flow routes and ordinary backlinks. All twelve Broccolium/Tweakium module pages remain covered; this card completes Peripheralium, Testiarium, TypeScript, build and verification.
- Peripheralium is material/registration/provider composition, not a new peripheral implementation hierarchy. Its focused platform class model and two separate registration/datagen sequences avoid inventing classes or merging runtime and generation.
- Testiarium's feature graph, target definition/reporter model and registry/client/CCT sequences cover main versus fixtures and execution. Native outer-observer/registration tests were read; no runtime pass is invented.
- TypeScript interfaces and typed provider instances are independent contracts, not generated Kotlin types. Inventory/query and fluid/energy families are separate focused models. Other behavior API declarations have no shared runtime inheritance tree; existing prose and compiler contract calls cover their composition without giant exhaustive UML.
- Build/verification use source/artifact/evidence flowcharts rather than artificial class or save-state diagrams. World persistence belongs to Broccolium lifecycle and Tweakium upgrade pages. Generated indexes and quickstart are navigation, not separate class subsystems; no diagram is needed merely to fill those files.
- Full evidence archive includes machine-readable per-block inventory and direct repository evidence URLs, rendered site, JSON results, full/targeted screenshots, scripts and lifecycle/Git receipt. Those artifacts remain outside tracked source; the handoff identifies their durable attachment.

### Full cumulative diagram inventory

Each row is one rendered block; linked headings contain its adjacent explanation and full repository-relative evidence links. Evidence below abbreviates source filenames/spans; explicit version prefixes distinguish overrides, and module ownership follows the page. Same-heading rows distinguish separate blocks by title. All rows passed both wide and narrow rendering, accessibility/bounds and scroll checks in the cumulative audit above; application tests cited as source are not new test executions.

| Page / heading (diagram) | Kind | Representative source evidence |
| --- | --- | --- |
| [Topology: matching leaves](../../openwiki/architecture/loaders.md#one-pattern-four-libraries) | Flow | settings.gradle.kts:44–75; target convention:185–213 |
| [Topology: effective sources](../../openwiki/architecture/loaders.md#authored-sources-versus-processed-output) | Flow | root build.gradle.kts:52–107 |
| [Overview: production](../../openwiki/architecture/overview.md#production-dependencies) | Flow | Peripheralium build:40–45; Tweakium build:51–60; target convention:107–128 |
| [Overview: tests/contracts](../../openwiki/architecture/overview.md#tests-and-consumer-contracts-are-different-edges) | Flow | Broccolium Fabric build:54–67; Tweakium Fabric build:43–69 |
| [Broccolium adapters: families](../../openwiki/broccolium/integrations/loaders.md#native-delegation-map) | Flow | FabricStorageWrapper:24–103; FabricStorageUtils:48–109 |
| [Broccolium adapters: native mutation](../../openwiki/broccolium/integrations/loaders.md#one-target-native-mutation-not-a-distributed-transaction) | Sequence | 26.1.2 AgnosticItemHandlerWrapper:11–53 |
| [Broccolium data: providers](../../openwiki/broccolium/modules/data.md#provider-composition-and-build-time-flow) | Flow | LibDataProviders:5–9; LanguageProvider:14–50 |
| [Broccolium lookup: priority](../../openwiki/broccolium/modules/lookup-drops.md#resolution-drill-down) | Flow | AgnosticItemSinkLookup:13–24; ChainedBaseLookup:11–41 |
| [Broccolium capture](../../openwiki/broccolium/modules/lookup-drops.md#capture-state-flow) | Sequence | 26.1.2 FakePlayerProxy:60–75; DropConsumer:18–73 |
| [Broccolium platform: services](../../openwiki/broccolium/modules/platform-base.md#service-and-module-map) | Flow | BroccoliumCore:17–20; PlatformToolkit:5–16 |
| [Broccolium platform: lifecycle](../../openwiki/broccolium/modules/platform-base.md#persistence-and-sync) | Flow | 26.1.2 BaseBlockEntityBlock:30–92; LevelChunkMixin:14–29 |
| [Broccolium storage: contracts](../../openwiki/broccolium/modules/storage.md#focused-storage-class-model) | Class | AgnosticSink:7–30; AgnosticStorage:7–25 |
| [Broccolium storage: families](../../openwiki/broccolium/modules/storage.md#resource-families) | Flow | AgnosticFluidStorage:3–7; AgnosticEnergyStorage:3–13 |
| [Broccolium storage: generic transfer](../../openwiki/broccolium/modules/storage.md#generic-transfer-sequence) | Sequence | StorageUtils:13–71; StorageTests:130–202 |
| [Build: source/artifact selection](../../openwiki/build/stonecutter-publication.md#source-to-artifact-flow) | Flow | root build.gradle.kts:52–107, 152–169, 231–245 |
| [Build: publication features](../../openwiki/build/stonecutter-publication.md#main-output-versus-test-features) | Flow | target convention:149–166; Testiarium build:21–51; root build:112–148 |
| [Peripheralium: content/providers](../../openwiki/peripheralium/content.md#content-and-provider-map) | Flow | 26.1.2 PeripheraliumCommonHooks:6–14; PeripheraliumCore:14–23 |
| [Peripheralium: platform](../../openwiki/peripheralium/content.md#focused-platform-composition) | Class | PeripheraliumPlatform:7–17 |
| [Peripheralium: datagen](../../openwiki/peripheralium/content.md#data-generation-is-a-separate-lifecycle) | Sequence | 26.1.2 PeripheraliumDataProviders:5–12; loader generators |
| [Peripheralium adapters: registration](../../openwiki/peripheralium/integrations/loaders.md#registration-sequence) | Sequence | FabricPeripheralium:6–19; ForgePeripheralium legacy:18–37, 1.21.1:16–38, 26.1.2:15–33 |
| [Peripheralium adapters: isolated routes](../../openwiki/peripheralium/integrations/loaders.md#runtime-datagen-and-test-entrypoints-are-different-routes) | Flow | target fabric.mod.json:17–29; target convention:473–492 |
| [Testiarium: client](../../openwiki/testiarium/client-cct.md#target-client-sequence) | Sequence | 26.1.2 ClientTestHooks:49–145; ClientTestExtensions:31–67 |
| [Testiarium: CCT channels](../../openwiki/testiarium/client-cct.md#labeled-cct-action-and-lua-completion) | Sequence | 26.1.2 CctComputers:37–82, 126–185; CctGameTestExtensions:14–50 |
| [Testiarium harness: features](../../openwiki/testiarium/harness.md#harness-and-feature-model) | Flow | Testiarium build:21–51; root build:112–148 |
| [Testiarium harness: definitions/reporters](../../openwiki/testiarium/harness.md#focused-registration-and-reporter-model) | Class | 26.1.2 Testiarium:33–69; TestReporters:21–79 |
| [Testiarium harness: registration](../../openwiki/testiarium/harness.md#version-specific-registration-and-execution) | Sequence | 26.1.2 Testiarium:63–147; ForgeTestiarium:13–25 |
| [Verification: route map](../../openwiki/testing/verification.md#verification-route-map) | Flow | run-native-matrix.py:18–30; check-publications.py:23–70 |
| [Verification: report acceptance](../../openwiki/testing/verification.md#fresh-report-acceptance) | Flow | gametest-reports.gradle.kts:20–179; run-native-matrix.py:33–109 |
| [Tweakium adapters: discovery](../../openwiki/tweakium/integrations/loaders.md#logical-discovery-adapters) | Flow | TweakiumCore:17–27; 26.1.2 FabricComputerPlatformToolkit:49–54 |
| [Tweakium adapters: client](../../openwiki/tweakium/integrations/loaders.md#physically-client-only-model-registration) | Flow | 26.1.2 FabricTweakiumClient:7–10; fabric.mod.json:12–16 |
| [Tweakium peripheral: ownership](../../openwiki/tweakium/modules/peripheral.md#composition-model) | Class | OwnedPeripheral:19–100; IPeripheralOwner:19–54 |
| [Tweakium peripheral: dispatch](../../openwiki/tweakium/modules/peripheral.md#lua-dispatch-lifecycle) | Sequence | OwnedPeripheral:137–160; BoundMethod:11–18 |
| [Tweakium peripheral: connections](../../openwiki/tweakium/modules/peripheral.md#connectionconfiguration-behavior) | Sequence | OwnedPeripheral:103–134 |
| [Tweakium platform/data: composition](../../openwiki/tweakium/modules/platform-data.md#platform-and-definition-composition) | Class | 26.1.2 ComputerBasePlatform:17–49; UpgradeDataProviders:9–21 |
| [Tweakium platform/data: generation versus Filler](../../openwiki/tweakium/modules/platform-data.md#platform-and-definition-composition) | Flow | 26.1.2 UpgradeDataProviders:9–21; TweakiumDataProviders:20–40; CreativeFillerPeripheral:38–79 |
| [Tweakium player: responsibilities](../../openwiki/tweakium/modules/player.md#host-and-actor-responsibility-map) | Flow | 26.1.2 FakePlayerProviderBlockEntity:17–39; FakePlayerProviderTurtle:16–20 |
| [Tweakium player: session](../../openwiki/tweakium/modules/player.md#target-session-sequence) | Sequence | 26.1.2 FakePlayerProviderTurtle:43–88; FakePlayerStorageSession:12–62 |
| [Tweakium plugins: families](../../openwiki/tweakium/modules/plugins.md#small-plugin-family-model) | Class | AbstractRudimentInventoryPlugin:14–36; AbstractInventoryPlugin:17–89 |
| [Tweakium plugins: operation policy](../../openwiki/tweakium/modules/plugins.md#boons-and-operation-policy) | Flow | IPeripheralOwner:43–54; OperationBoon:39–96, target:39–91 |
| [Tweakium representation: boundary](../../openwiki/tweakium/modules/representation-rml.md#logical-representation-boundary) | Flow | 26.1.2 LuaRepresentation:101–135 |
| [Tweakium RML: transform](../../openwiki/tweakium/modules/representation-rml.md#rml-transform-flow) | Flow | rml1/Toolkit:29–55; Operations:7–101 |
| [Tweakium rendering: extraction/submission](../../openwiki/tweakium/modules/representation-rml.md#client-model-extraction-and-submission-2612) | Sequence | 26.1.2 FacingBlockTurtleModeller:26–73; legacy:12–24 |
| [Tweakium upgrades: construction](../../openwiki/tweakium/modules/upgrades-persistence.md#pocket-construction-class-view) | Class | PeripheralPocketUpgrade:10–30; StatefulPeripheralPocketUpgrade:10–24 |
| [Tweakium upgrades: transport](../../openwiki/tweakium/modules/upgrades-persistence.md#save-to-item-and-load-into-a-reconstructed-upgrade) | Sequence | StatefulPocketUpgrade legacy:25–40, 1.21.1:27–60; target UpgradeTemplateUtil:7–45 |
| [Tweakium upgrades: notification](../../openwiki/tweakium/modules/upgrades-persistence.md#settings-write-notification-is-a-separate-flow) | Sequence | 26.1.2 DataStorageUtil:215–275, 29–40 |
| [TypeScript: inventory composition](../../openwiki/typed-peripheral-api/contracts.md#interface-and-provider-model) | Class | inventory.ts:4–21; inventory_extended.ts:10–54; item_storage.ts |
| [TypeScript: fluid/energy providers](../../openwiki/typed-peripheral-api/contracts.md#native-resource-interface-and-provider-families) | Class | fluid_storage_extended.ts:4–14; energy_storage_extended.ts:4–14 |
| [TypeScript: compiler/emission/runtime](../../openwiki/typed-peripheral-api/contracts.md#compiler-checks-emission-and-runtime-correlation) | Flow | package build.gradle.kts:17–50; package.json:5–19; tests/tsconfig.json:1–7 |

Computed inventory: 25 flowcharts, 9 class models, 14 sequences = 48 blocks. Quickstart has no redundant chart: its visual-route table connects the diagrams above. All new/changed source and navigation links are checked through the strict build, static audit and additional report-link/anchor audit.

## Milestone 2 — t_9cf32b07

Imported exact parent commit 0e9f0681bf70032e4a9e94c1b7423994c270d9b1 by fast-forward and verified ancestry in the assigned worktree. Completed the twelve existing Broccolium/Tweakium factual pages with 27 new diagrams; cumulative total is 31, including the four parent overview/topology charts. All are accessible, source-grounded and linked through ordinary Markdown. Existing accurate prose and historical/current evidence distinctions remain. No application, dependency, renderer, MkDocs config, AGENTS or workflow changes were made on milestone 2. The verification script only gains immediate renderer-error reporting instead of waiting for a timeout.

Important source-grounded distinctions: generic transfers simulate/execute/restore remainders, not universal atomicity; Fabric native-peer movement differs from mixed-peer bridges; old Forge-family handler simulation differs from target native per-call transactions. Peripheral explicit plugins precede owner boons in collection, but connection callbacks visit only explicit plugins and run under the lock on the caller's thread. Repeated empty detach can repeat last-detach callbacks. Fake-player load precedes the action try/finally; scoped attribute/loan cleanup is not arbitrary-backend rollback. Stateful item transport differs across storedData, mutable components and immutable templates. Client model extraction/submission is separate from logical owner composition and from RML parsing.

### Milestone-2 checks and evidence

Actual commands (reused parent-pinned MkDocs 1.6.1, Playwright 1.56.0/Chromium1194 and Mermaid 12.2.0):

```sh
PYTHONDONTWRITEBYTECODE=1 "$TMPDIR/openwiki-diagrams-venv/bin/python" -m mkdocs build --strict --site-dir "$TMPDIR/t_9cf32b07-diagrams-evidence/pages/openwiki"
PYTHONDONTWRITEBYTECODE=1 "$TMPDIR/openwiki-diagrams-venv/bin/python" scripts/verification/check-openwiki-diagrams.py --site "$TMPDIR/t_9cf32b07-diagrams-evidence/pages" --output "$TMPDIR/t_9cf32b07-diagrams-evidence"
git diff --check
```

Strict build and cumulative audit exit 0: 22 factual pages, 61 Claims, 155 evidence references, manifest/sidecar version agreement, existing bounded repository paths/spans and 2,034 built internal links/anchors pass. All 31 Mermaid blocks parse/render at both 1440×1000 and 390×1000 (28 page/viewport runs, 62 SVG render observations), with titles/descriptions, viewBox containment, natural font/pixel sizing and full keyboard-scroll-region reach. Zero console/page errors in normal runs; deliberate CDN failure keeps readable source and navigation. No hosted CI/deployment or GitHub-native Mermaid execution is claimed.

Actual rendered storage/peripheral class diagrams and transfer, dispatch, attach/detach, upgrade transport/settings and client sequences have wide/narrow screenshot evidence outside tracked source. Visual review shortened the dispatch, connection, client and upgrade labels; full adjacent explanations retain exact API/ordering details. Long figures intentionally scroll horizontally even in the theme's fixed-width desktop content column (694px; phone 336px), rather than shrink labels into unreadability. SVG-internal bounds and left/right reach pass; a single viewport screenshot is not a claim that all lifelines fit simultaneously. Text and edge shapes convey semantics without color.

The first audit found real sequence parse failures: literal semicolons in message labels terminate Mermaid statements. Corrected the affected four pages through supported sequential jobs, retained the failure logs, and reran the full suite successfully. Retrieval smoke tests found and read back the exact generic-transfer, connection and stateful-transport sections. Source/test fixtures were inspected, not executed: no Minecraft matrices were rerun for documentation.

Lifecycle: initial 1a924228-c12d-4b0f-8e54-8b2b7dbfcced completed all twelve jobs but sourceChanged=true persisted interrupted (report changed during authoring). Supported correction run 4a69a192-ea0c-4b0e-b920-bdcca24d8a5d and visual-polish run 54663bc4-efae-4786-85ef-d19f6a939115 both finished complete. Current issue-free Claims were retained; new material propositions cite bounded source. A compound datagen Claim was deliberately split via inspect/submit, preserving its original ID for the retained proposition. Final stable-source run, local cumulative commit/ref/SHA and durable evidence archive are recorded in the task handoff, avoiding a self-referential report hash. Publication/review/remaining ecosystem scopes stay downstream.

## Milestone 1 — t_90d65a9c

Card-local scope: top-down logical architecture, matching version/loader source topology, navigable project entry points and Mermaid rendering. Class/runtime drill-downs remain explicitly owned below. This report is not whole-campaign acceptance or publication.

Baseline: fetched origin/multiversion at f71be14b6b98de2d95b1d3c4a36fefcec0e50305; assigned worktree reconciliation returned already up to date. Local commits only; exact final source/metadata commit identities and durable evidence attachment are recorded in the Kanban handoff to avoid self-referential hashes here. No push, PR, merge or deployment on this card.

## Completed views and conventions

- [Production dependency view](../../openwiki/architecture/overview.md#production-dependencies): consumer → provider, including Peripheralium's direct Broccolium edge as well as Tweakium. Build evidence: projects/peripheralium/build.gradle.kts:40–45, projects/tweakium/build.gradle.kts:51–60, gradle/minecraft-26-1.gradle.kts:107–128.
- [Fixture and contract view](../../openwiki/architecture/overview.md#tests-and-consumer-contracts-are-different-edges): dashed test edges/boxes, thick independently maintained TypeScript contract correlation, not a production cycle. Evidence: projects/broccolium/fabric/build.gradle.kts:54–67, projects/tweakium/fabric/build.gradle.kts:43–69, projects/testiarium/build.gradle.kts:21–54, projects/typed-peripheral-api/tests/contracts.ts:60–99.
- [Matching common/adapter leaves](../../openwiki/architecture/loaders.md#one-pattern-four-libraries): four libraries × three versions × common/Fabric/forge; `forge` is Forge on 1.20.1 and NeoForge on 1.21.1/26.1.2. TypeScript is outside that graph. Evidence: settings.gradle.kts:44–75, build.gradle.kts:152–169, gradle/minecraft-26-1.gradle.kts:185–213.
- [Effective source selection](../../openwiki/architecture/loaders.md#authored-sources-versus-processed-output): canonical 1.20.1 shared sources versus matching full-file overrides, not editable processed outputs. Evidence: build.gradle.kts:52–107.
- All four charts have accTitle/accDescr, adjacent prose and non-color-only edge explanations. These are logical module/source models, not Minecraft rendering internals. Quickstart and all five project entry pages have ordinary Markdown navigation/backlinks; no clickable SVG security exception is needed.

## Coverage checklist and remaining ownership

“Pending” means a later card's class/composition/runtime diagram, not missing milestone-1 entry navigation. Source seeds are navigation starting points, not substitutes for tracing callers/tests and version overrides. Keep client models separate from logical ownership models. All listed page paths are relative to openwiki/.

| State / owner | Page or subsystem | Diagram kind / next evidence route |
| --- | --- | --- |
| Complete — 1/4 | architecture/overview.md | Production and test/contract module graphs; build declarations cited above |
| Complete — 1/4 | architecture/loaders.md | Matching-version and effective-source flowcharts; source selection cited above |
| Complete — 3/4 | quickstart.md | Overview-to-subsystem-to-focused visual routes for all five projects; topology, artifacts and verification linked |
| Complete — 2/4 | [Broccolium platform/base](../../openwiki/broccolium/modules/platform-base.md#service-and-module-map) | 2 composition/lifecycle flows; BasePlatform:24–46, target BaseBlockEntityBlock:30–92 and MutableNBTBlockEntity:24–62. Render/link/Claims pass. |
| Complete — 2/4 | [Storage classes/families/transfer](../../openwiki/broccolium/modules/storage.md#focused-storage-class-model) | Class model, resource-family flow, generic sequence; AgnosticSink:7–30, SlottedAgnosticStorage:8–39, StorageUtils:13–71, ReplaceableItemStorage:6–31. Render/link/Claims pass. |
| Complete — 2/4 | [Lookup/drop capture](../../openwiki/broccolium/modules/lookup-drops.md#resolution-drill-down) | Priority flow and capture sequence; BaseLookup:28–71, ChainedBaseLookup:11–41, DropConsumer:18–73, target FakePlayerProxy:60–75. Render/link/Claims pass. |
| Complete — 2/4 | [Broccolium data](../../openwiki/broccolium/modules/data.md#provider-composition-and-build-time-flow) | Provider composition/data flow; LibDataProviders:5–9, LanguageProvider:14–50, target ForgeGeneratorSink:88–91. World-save sequence N/A: build-time JSON generation, not game persistence. Render/link/Claims pass. |
| Complete — 2/4 | [Broccolium native delegation](../../openwiki/broccolium/integrations/loaders.md#native-delegation-map) | Adapter-family flow and target commit sequence; FabricStorageWrapper:24–103, mixed FabricStorageUtils:48–109, modern AgnosticItemHandlerWrapper:11–53, old 1.21.1 handler:10–60. Render/link/Claims pass. |
| Complete — 2/4 | [Peripheral ownership/dispatch/connections](../../openwiki/tweakium/modules/peripheral.md#composition-model) | Class model and 2 sequences; OwnedPeripheral:19–160, BasePeripheralOwner:10–33, BoundMethod:11–18. Render/link/Claims pass. |
| Complete — 2/4 | [Plugins/boons/operations](../../openwiki/tweakium/modules/plugins.md#small-plugin-family-model) | Plugin class model and policy/failure flow; AbstractInventoryPlugin:17–89, AbstractRudimentInventoryPlugin:14–36, OperationBoon:39–96 (target:39–91). Render/link/Claims pass. |
| Complete — 2/4 | [Fake-player responsibilities](../../openwiki/tweakium/modules/player.md#host-and-actor-responsibility-map) | Host/proxy composition and target session sequence; target FakePlayerProviderTurtle:43–88, FakePlayerStorageSession:12–62, FakePlayerSessionUtil:9–74. No blanket legacy-session parity. Render/link/Claims pass. |
| Complete — 2/4 | [Computer platform/data/Filler](../../openwiki/tweakium/modules/platform-data.md#platform-and-definition-composition) | Class/composition and separate datagen/runtime flow; target ComputerBasePlatform:17–49, UpgradeDataProviders:9–21, TweakiumDataProviders:20–40, CreativeFillerPeripheral:38–79. Render/link/Claims pass. |
| Complete — 2/4 | [Representation/RML/client rendering](../../openwiki/tweakium/modules/representation-rml.md#logical-representation-boundary) | 2 separate language/transform flows and target client sequence; LuaRepresentation:101–135, rml1/Toolkit:29–55, Operations:7–101, target FacingBlockTurtleModeller:26–73. Render/link/Claims pass. |
| Complete — 2/4 | [Upgrade persistence](../../openwiki/tweakium/modules/upgrades-persistence.md#pocket-construction-class-view) | Construction class model, versioned item transport and settings-write sequences; StatefulPocketUpgrade (1.20.1:25–40, 1.21.1:27–60), target UpgradeTemplateUtil:7–45, DataStorageUtil:29–40/215–275. Turtle sibling seam explained without duplicated exhaustive UML. Render/link/Claims pass. |
| Complete — 2/4 | [Tweakium loader/client boundaries](../../openwiki/tweakium/integrations/loaders.md#logical-discovery-adapters) | Discovery and isolated client registration flows; target ForgeComputerPlatformToolkit:56–66, FabricComputerPlatformToolkit:49–54, client initializers and Fabric metadata:12–16. No redundant loader class diagram: actual common composition/sequence pages linked. Render/link/Claims pass. |
| Complete — 3/4 | peripheralium/content.md | Content/provider map, platform class and separate datagen sequence; source/render details in full inventory above |
| Complete — 3/4 | peripheralium/integrations/loaders.md | Platform-first sequence, metadata/datagen/test routing and modern/legacy differences |
| Complete — 3/4 | testiarium/harness.md | Feature composition, definition/reporter class model and version-specific registration/execution sequence |
| Complete — 3/4 | testiarium/client-cct.md | Separate target client and CCT monitor/marker sequences; historical runtime evidence retained |
| Complete — 3/4 | typed-peripheral-api/contracts.md | Two focused interface/provider models plus no-emission/compiler/Lua boundary flow |
| Complete — 3/4 | build/stonecutter-publication.md | Source/artifact selection and main/private/optional feature publication boundaries |
| Complete — 3/4 | testing/verification.md | Owning verification route map and layered fresh-report acceptance flow |

Final independent review/publication belongs to pre-created 4/4 t_5f8c95c0, not this card. The next owner must import the exact local handoff commit and verify ancestry; task dependencies do not transfer Git changes.

## Renderer and reproducible checks

MkDocs stays pinned at 1.6.1 in the existing deployment. Built-in fenced_code emits source blocks; assets/mermaid.js imports exactly mermaid@12.2.0 from jsDelivr (npm registry version queried during this work), initializes strict security and neutral theme, renders before replacing source and retains readable source on import/render failure. No new Python rendering plugin, action upgrade or workflow change is necessary. assets/mermaid.css makes charts keyboard-scrollable; natural viewBox sizing prevents desktop ballooning and phone text shrinkage. Network access to the pinned CDN and its modules remains necessary for SVG; offline source and normal navigation still work. This is not a vendored/offline renderer or a complete dependency security audit.

Commands actually exercised (scratch paths use the assigned TMPDIR):

```sh
python -m venv "$TMPDIR/openwiki-diagrams-venv"
"$TMPDIR/openwiki-diagrams-venv/bin/pip" install 'mkdocs==1.6.1' 'playwright==1.56.0'
"$TMPDIR/openwiki-diagrams-venv/bin/playwright" install chromium
PYTHONDONTWRITEBYTECODE=1 "$TMPDIR/openwiki-diagrams-venv/bin/python" -m mkdocs build --strict --site-dir "$TMPDIR/openwiki-diagrams-evidence/pages/openwiki"
PYTHONDONTWRITEBYTECODE=1 "$TMPDIR/openwiki-diagrams-venv/bin/python" scripts/verification/check-openwiki-diagrams.py --site "$TMPDIR/openwiki-diagrams-evidence/pages" --output "$TMPDIR/openwiki-diagrams-evidence"
git diff --check
```

Results: strict build exit 0. Browser audit exit 0; all four Mermaid blocks parsed/rendered through the actual pinned browser module at 1440×1000 and 390×1000, both pages showing two SVGs. Accessible SVG titles/descriptions and viewBox containment passed; natural pixel size and full horizontal reach passed; zero console/page errors on normal rendering. A deliberately aborted CDN request retained every fenced block and normal overview navigation. Visual inspection of desktop overview and phone loader charts led to the natural-sizing fix; screenshots include both initial and fully scrolled positions. One expanded link-audit attempt initially failed on the 404 page's absolute GitHub project prefix; the audit now resolves the configured site_url prefix rather than assuming domain-root hosting. The retained failure log is not a pass.

Static audit: 22 factual pages, 50 Claims, 126 evidence resource references; all repository evidence paths/spans exist and each manifest pageVersion agrees with its Claims sidecar. All Markdown targets and bounded line links pass; 1,965 built internal links/anchors pass. Generated structural indexes are not counted as factual pages. Retrieval smoke tests returned overview dependency/fixture sections and loader override/matching-version sections; exact returned sections were read back through OpenWiki.

The existing scripts/openwiki-pages.py evidence-link rewriting and .github/workflows/openwiki-pages.yml subfolder/source-commit marker logic remain unchanged. A final source-snapshot marker is written into the disposable built artifact and checked at handoff. No Minecraft builds/runtime matrices were rerun for documentation. No hosted deployment/CI/GitHub-native Mermaid execution is claimed; syntax is conservative flowchart syntax intended to be portable.

## Supported lifecycle and refresh preservation

Initial supported force update e2c40f43-95dc-4309-a7f6-923cfed62ca6 accepted ten sequential jobs, retained current issue-free Claims, confirmed three stale Claims after rechecking root build/evidence, and added six material Claims. Finish returned complete with sourceChanged=true, but persisted last-update status was interrupted: this is an authored checkpoint, not the final stable-source lifecycle receipt. Renderer/report changes were still in flight.

After committing non-generated source/config/verification inputs, run the supported update again against that stable source snapshot, supply the overview job for final compact-label polish if needed, and finish. Exact final run ID/receipt and source SHA are in the task handoff and attached evidence. Acceptance requires persisted complete status, no active run and no non-wiki changes between source snapshot and final metadata commit. This source/metadata split avoids a self-referential commit loop.

For future refreshes, use openwiki_begin(mode=update, force=true) when source-based detection would otherwise be noop but visual enrichment is needed. Supply a focused plan with the existing canonical page paths and relatedPages; explicitly include instructions to retain the accessible small diagrams, separate production/test/contract edge semantics, ordinary navigation and source-backed version distinctions. Use sequential page jobs and sparse Claim decisions; revise diagrams with real source changes, then finish and rerun the checked-in audit. Do not edit Claims/indexes/manifests directly or fabricate source drift. Per-job instructions are supported inputs, not a promise of a permanently stored content-preservation policy; later generators must receive and honor the same scope. Preserve accurate prose on every refresh.

Existing managed AGENTS text mentions a scheduled wiki refresh, but .github/workflows/openwiki-update.yml is absent: the existing Pages workflow renders/deploys content, it does not generate/refresh it. No AGENTS.md, scheduled automation, protection or service setup change is part of this card.
