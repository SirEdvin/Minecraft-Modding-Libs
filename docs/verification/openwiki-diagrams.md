# OpenWiki diagram campaign

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
| Complete entry — 1/4; final navigation 3/4 | quickstart.md | Routes to overview, topology and all five projects; settings.gradle.kts |
| Pending — 2/4 t_9cf32b07 | broccolium/modules/platform-base.md | Platform/lifecycle composition; modules/platform and modules/base source families |
| Pending — 2/4 t_9cf32b07 | broccolium/modules/storage.md | Storage item/fluid/energy abstractions and transfer/commit/rollback flows; modules/storage and adapter implementations |
| Pending — 2/4 t_9cf32b07 | broccolium/modules/lookup-drops.md | Lookup/drop resolution composition and selection; matching module source families |
| Pending — 2/4 t_9cf32b07 | broccolium/modules/data.md | Provider composition/data flow; modules/data and loader datagen routes |
| Pending — 2/4 t_9cf32b07 | broccolium/integrations/loaders.md | Native platform/storage delegation; fabric/forge and version overrides |
| Pending — 2/4 t_9cf32b07 | tweakium/modules/peripheral.md | Owner/plugin composition, Lua dispatch and attach/detach sequences; modules/peripheral |
| Pending — 2/4 t_9cf32b07 | tweakium/modules/plugins.md | Plugin/boon/operation composition and failure/ordering flows; modules/peripheral source neighbors |
| Pending — 2/4 t_9cf32b07 | tweakium/modules/player.md | Fake-player ownership/context; player implementations and their callers |
| Pending — 2/4 t_9cf32b07 | tweakium/modules/platform-data.md | Platform/provider composition and data flow; loader registration and generators |
| Pending — 2/4 t_9cf32b07 | tweakium/modules/representation-rml.md | Lua representation versus client rendering/model pipeline; matching-version implementations |
| Pending — 2/4 t_9cf32b07 | tweakium/modules/upgrades-persistence.md | Upgrade composition and save/load lifecycle; upgrades, settings and native hooks |
| Pending — 2/4 t_9cf32b07 | tweakium/integrations/loaders.md | Adapter integration/lookup and client boundaries; loader sources and metadata |
| Pending — 3/4 t_6fb747f5 | peripheralium/content.md | Content/registration/provider composition and lifecycle; content holders and generators |
| Pending — 3/4 t_6fb747f5 | peripheralium/integrations/loaders.md | Loader entrypoint/registry flow and version-limited integrations |
| Pending — 3/4 t_6fb747f5 | testiarium/harness.md | Harness composition, registration/execution/report flows; main API versus fixture features |
| Pending — 3/4 t_6fb747f5 | testiarium/client-cct.md | Client/CCT fixture sequences; optional fixture registrations and scripts |
| Pending — 3/4 t_6fb747f5 | typed-peripheral-api/contracts.md | Interface/provider composition and compiler/Lua contract flow; package scripts, types and tests |
| Pending detail — 3/4 t_6fb747f5 | build/stonecutter-publication.md | Artifact/publication boundaries; build.gradle.kts and check-publications.py; current source topology already complete above |
| Pending — 3/4 t_6fb747f5 | testing/verification.md | Fresh report/acceptance flow; gametest-reports.gradle.kts and verification scripts |

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
