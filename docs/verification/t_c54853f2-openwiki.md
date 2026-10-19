# Scoped OpenWiki closure — t_c54853f2

Scope: authorized local cleanup and stable-source OpenWiki closure, 2026-10-19. This is not final campaign acceptance. Final ledger reconciliation, Obsidian report and cumulative delivery remain owned by dependent t_b9190fc7. The historical blocker in [F](minecraft-26-1-F.md) is superseded only for this scoped lifecycle: the user explicitly authorized retaining OpenWiki's normal managed AGENTS.md section.

## Custody and supported lifecycle

Work remains on the preserved t_16b4e35f branch/worktree. Baseline ad311e9628bff02c0d0bab118f64891fd56e9e13 includes tested R7 281cee3ed4958bd845e6911321f9a24eaee9eae8 and the original authored-WIP custody. No reset, clean, primary-checkout mutation, production port, push or publication is part of this work. Five original untracked runtime/cache paths are retained.

Inspected installed OpenWiki 0.7.1 README (managed setup and durable Claims semantics), current MCP schemas and lifecycle implementation. Supported `openwiki_begin(root=<assigned absolute worktree>, mode="update", language="en")` retains the normal managed AGENTS block without creating an init-only scheduled workflow. Original project instructions outside the block are unchanged. No CLAUDE.md exists or was created. No installed tooling, service, configuration or automation was changed.

Important discrepancy: the retained generated section asserts that a scheduled GitHub Actions workflow refreshes the wiki. That assertion is inaccurate here: `.github/workflows/openwiki-update.yml` is absent. Retaining the standard managed section does not authorize creating automation.

The first begin call lost MCP transport. State inspection proved no file changes or active run; a read-only workspace listing succeeded before a fresh begin. Update run 22f7d4fc-c5ff-4350-87f7-b60e19ae816c then accepted the complete 20-page plan and all 20 ordered submissions, retaining current issue-free Claims with no additions/retractions. `openwiki_finish` returned `{"status":"complete"}` without `sourceChanged`; persisted status became complete and `.run.json` was absent. The earlier interrupted metadata remains historical input, not a successful receipt.

## Verification and source-snapshot semantics

Runnable scoped audit: `python3 build/verification/check-stable-openwiki.py`. Receipt: `build/verification/t_c54853f2-openwiki-audit.json`. It independently verifies all 1,128 tested source/build/resource hashes, all 118 preserved exported artifact hashes, 20 page/manifest/sidecar versions, unchanged 44 unique Claims and 111 current evidence hashes, relative Markdown link targets, four canonical 1.20.1 controllers, original AGENTS bytes and setup/automation boundaries. `git diff --check` is also required. These are integrity/documentation checks, not new Minecraft tests.

The historical `python3 build/verification/verify-inherited-r7.py` was attempted and exits 1 before audit execution: its t_49598f9c sibling worktree delivery prerequisite no longer exists. Failure is retained at `build/verification/t_c54853f2-r7-audit.log`; it is not a fresh pass. Existing accepted R0–R7 execution remains inherited. The local final-source and exported-artifact manifests are independently checked instead; no fabricated archive or runtime evidence replaces the missing sibling path. The old `verify-documentation.py` explicitly requires interrupted status and obsolete cleanup approval, so it is historical rather than a valid finished-lifecycle gate.

After committing the authorized non-generated source changes, use another supported update/plan/submission/finish cycle against that stable source commit. Generated OpenWiki metadata is then committed separately. Metadata intentionally names the source snapshot, not the later generated-wiki-only commit containing that metadata: verify that every intervening tracked change is under `openwiki/`, all page baselines agree, and source/build/resource inputs remain unchanged. This avoids a self-referential commit loop. Exact source/metadata commit SHAs, final run receipt and audit output are recorded in the card handoff and attached closure receipt rather than predicted here.

## Retained limits and downstream handoff

No broad runtime campaign was repeated for documentation. Retain the old NeoForge 1.21.1 shutdown 143 followed by unchanged successful retry, two source-JAR ZIP-order limitations, existing npm advisory/strict-null limitation, and absence of cross-version wire/ABI/downgrade/authentication/arbitrary-third-party certification. No npm fix or security-triage revival occurred. Hosted CI, primary merge, public Maven/release publication and remote push remain unperformed and out of scope.

The downstream owner must reconcile its final documentation/ledger/report with this receipt, and use supported OpenWiki source-snapshot semantics again if its source documentation changes. This card does not mark the 204-row campaign ledger, personal reading tasks or whole campaign complete.
