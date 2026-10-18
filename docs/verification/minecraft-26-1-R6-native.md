# R6 assembled native/observer matrix

Status: native matrix VERIFIED; artifact/consumer refresh INCOMPLETE — task t_2d0802de, 2026-10-18. Historical checkpoints below retain failed/nonfinal attempts.

## Scope and baseline

This decomposed card owns only the complete native/CCT/client-harness matrix, not production readiness, copied-world migration, two-client networking or R7 visual acceptance. Its pre-created integration child t_fa26ffb6 reconciles the four R6 workstreams; t_997f2d4d retains aggregate R6 acceptance.

Merged planning commit b8fd1ddfd5134c45402040fe2c1ca5be343ac48f followed by exact R5 cumulative 4a07553673bcd51576e0857301dda867bc176a2c; both ancestry checks exit0 and source tree initially clean. R5 is done. Read approved remaining plan, original brief/source ledger, R1/R3/R5 receipts, module coverage and 204-row ownership map. Existing scoped target observer approval is unchanged; no product scope expansion.

Verified R5 evidence archive SHA256 d8da14a41279d99cf95227f8b3012585989009144d7fd94ab0aff39bd501f4dc at the predecessor's retained build/verification path (read-only). This card will retain its own manifests and correlate rebuilt outputs with those identities; fixture execution is not a replacement for release artifact hash checks.

Launcher verified Temurin25.0.3+9; Java17/21 retained; Xvfb available. Evidence root: `$TMPDIR/t_2d0802de-validation`. All Gradle uses wrapper, --no-daemon, explicit deadlines and recorded logs/exits. No EULA acceptance is inferred or written: native GameTest routes are distinct from ordinary production servers.

## Required matrix

Use the checked-in CI's six loader/version routes: Fabric storage, standalone, CCT, Tweakium, client for each version; Forge-family standalone, CCT, Tweakium, client for each version plus target Forge storage. Exact native report guards remain enabled. Storage skips forbidden; Testiarium's three exact negative fixture sentinels (four on target CCT) must match; no empty or stale report acceptance. Client fixtures prove the harness only, not complete visual/integration behavior.

## Next action

Create an exact-source standalone scratch checkout for the known linked-worktree JGit/Spotless issue without disabling lint. Verify full tracked source identity, archive CRC/source hashes and discover task routes. Execute all bounded runs serially with fresh report copies and command/identity receipts. Retain failures, fix only owning issues, rerun affected checks and correlate final artifacts before handoff. No push, remote publication, primary merge, shared-service restart or profile change.

## Phase 1 checkpoint — baseline failures retained, acceptance incomplete

Local checkpoint3345c83 contains the initial explicitly incomplete scope. Standalone scratch clone has1166 identical tracked source files, real origin/multiversion lint ratchet ref and successful `tasks --all` discovery (exit0). R5 ZIP CRC and all27 changed-source hashes verified. Eight collector self-checks pass; these use synthetic XML only to test rejection and are not native evidence. Initial background launch omitted environment exports and failed before Gradle; retained terminal output, then explicitly exported launcher/log paths for the real campaign.

Actual baseline campaign: `python3 <standalone>/scripts/verification/run-native-matrix.py --output <evidence>/matrix-1`. Each route invokes the existing bounded runner with unchanged native guards; per-command XML/identity/exit receipts are flushed under matrix-1. Existing legacy Fabric storage and Tweakium routes and legacy Forge standalone/CCT/Tweakium/client pass. Fabric Testiarium standalone/CCT/client fail before reports on both old versions with `Namespace (intermediary) does not match current runtime namespace (named)`; these are real startup failures, not passing native tests. Complete baseline still running.

Root traced to R5's feature API self-dependency in root build.gradle.kts: it selects the remapped Fabric runtime variant and leaks raw intermediary dependencies into named feature runs. Proposed minimal fix selects the existing namedElements configuration only for the two old Fabric core edges; preserves all target/Forge/core feature wiring, exact negative fixtures and runtime pins. Authored fix has NOT yet been synced into the running baseline or verified. Must replay affected native routes and publication/consumer contracts, compare artifacts before acceptance. build.gradle.kts is flagged as a serial integration hotspot on this card; original target convention/report guard hotspots remain unchanged.

## Phase 2 checkpoint — real regression green, final replay in progress

Baseline28 routes finished:15 accepted, six old Fabric Testiarium failures (including1.21.1 client exit124 after class-tweaker startup error), seven target routes22–28 failed from shared filesystem exhaustion. Route22's real log reports `java.io.IOException: No space left on device` extracting Kotlin; later configuration attempts fail for the same reason. These seven are environment failures, not behavioral incompatibility or passes. Baseline XML,54 runtime logs/screenshots and command receipts retained before any replay. df reports248GiB filesystem,247GiB used, initially958MiB then792MiB available; inode use11%. Task standalone uses2.1GiB. No other owner's files/cache or shared service was touched.

Synced minimal root fix44d4334 into task standalone only after baseline finished. Direct bounded `:testiarium:fabric:1.20.1:runServer --no-daemon --console=plain` now exits0 with real guarded XML (`named-core-green-probe.log`), providing actual startup red→green. Full fresh28-route matrix-2 is running on the repaired build. Collector now also preserves each fresh client screenshot and has a360s outer client watchdog; normal checked-in300s Xvfb deadline remains unchanged. Artifact/publication/consumer refresh is still pending; disk pressure remains a named risk, not waived acceptance.

## Phase 3 recovery — 2026-10-18, acceptance incomplete

Recovered after the two interrupted worker attempts. Matrix-2 retained22 complete route receipts, but route3 failed with `No test batches were given!`; the collector then failed while writing route23 after another ENOSPC. Neither that truncated run nor the new foreground probe interrupted by the tool's420s ceiling is final acceptance. Disk now has73GiB free. No external owner's files or services were changed.

Root of route3: old Fabric CCT initializer scans the one-shot registry before the standalone owning testmod adds its classes. Remove only that premature scan; the owning testmod still registers classes then scans. Target's separate initializer is unchanged. Actual repaired route3 in fresh matrix-3 now exits0 with12 discovered cases and exactly the three unchanged negative sentinels. Routes1–5 are freshly green, including storage24/zero skips and a real client screenshot. Full28-route matrix-3 remains in progress with the exact collector/Gradle guards, not a replacement by selected passes.

Explicit dashboard confirmation now authorizes `eula=true` for disposable project verification instances if required. This supersedes the historical no-acceptance observation above; it does not widen observer/product scope or authorize production/shared-service changes. No EULA write was needed for these native routes.

Exact source identity is recorded in `$TMPDIR/t_2d0802de-validation/resume-source-hashes.json`:1166 tracked files match the assigned worktree and standalone byte-for-byte at replay launch. `refresh-artifacts.py` in that evidence root will replay the existing full build/unit/publication checks, compare all118 R5 JAR identities, and resolve/compile/execute all nine isolated file-Maven Java/Kotlin consumers. Artifact and consumer acceptance is still pending. Next: finish matrix-3, reconcile every exact identity/skip/exit/screenshot, then execute the artifact refresh serially and preserve durable evidence.

## Fresh complete native receipt

Implementation source:2660281 (same implementation bytes as the matrix launch; documentation alone changed since `resume-source-hashes.json`). `matrix-3/commands.json` has all28 routes in order, each actual Gradle invocation exit0, accepted by unchanged exact native guards. `summarize-native.py` independently reconciles copied XML hashes, freshness, every negative identity/message and all six client screenshot hashes; exits0. Total434 reported cases:396 positive cases and38 intentional negative-fixture skips, zero failures/errors. Storage and Tweakium have zero skips. Target native suites also include Minecraft's built-in `minecraft:always_pass` where emitted; the exact once-only guard excludes only that named built-in from owning inventories.

The outer interactive Zsh wrapper subsequently exits1 because its bookkeeping variable `status` is read-only; it ran after the collector printed `Accepted 28 actual native routes`. This shell bookkeeping error is preserved, not misrepresented as a passing shell invocation or a failing native route. All28 individual runner exit codes and reports are complete and independently verified. Subsequent shell bookkeeping uses `rc`.

| # | Native task / flags | Cases | Expected negative skips | Exit |
| --- | --- | ---: | ---: | ---: |
| 1 | `:broccolium:fabric:1.20.1:runStorageGameTest` | 24 | 0 | 0 |
| 2 | `:testiarium:fabric:1.20.1:runServer` | 12 | 3 | 0 |
| 3 | `:testiarium:fabric:1.20.1:runCctGameTest` | 12 | 3 | 0 |
| 4 | `:tweakium:fabric:1.20.1:runPeripheralGameTest` | 23 | 0 | 0 |
| 5 | `:testiarium:fabric:1.20.1:runClientGameTest` | 1 | 0 | 0 |
| 6 | `:testiarium:forge:1.20.1:runGameTestServer` | 12 | 3 | 0 |
| 7 | `:testiarium:forge:1.20.1:runGameTestServer -Ptestiarium.cct` | 12 | 3 | 0 |
| 8 | `:tweakium:forge:1.20.1:runGameTestServer` | 23 | 0 | 0 |
| 9 | `:testiarium:forge:1.20.1:runClientGameTest` | 1 | 0 | 0 |
| 10 | `:broccolium:fabric:1.21.1:runStorageGameTest` | 24 | 0 | 0 |
| 11 | `:testiarium:fabric:1.21.1:runServer` | 12 | 3 | 0 |
| 12 | `:testiarium:fabric:1.21.1:runCctGameTest` | 12 | 3 | 0 |
| 13 | `:tweakium:fabric:1.21.1:runPeripheralGameTest` | 25 | 0 | 0 |
| 14 | `:testiarium:fabric:1.21.1:runClientGameTest` | 1 | 0 | 0 |
| 15 | `:testiarium:forge:1.21.1:runGameTestServer` | 12 | 3 | 0 |
| 16 | `:testiarium:forge:1.21.1:runGameTestServer -Ptestiarium.cct` | 12 | 3 | 0 |
| 17 | `:tweakium:forge:1.21.1:runGameTestServer` | 26 | 0 | 0 |
| 18 | `:testiarium:forge:1.21.1:runClientGameTest` | 1 | 0 | 0 |
| 19 | `:broccolium:fabric:26.1.2:runStorageGameTest` | 37 | 0 | 0 |
| 20 | `:testiarium:fabric:26.1.2:runServer` | 14 | 3 | 0 |
| 21 | `:testiarium:fabric:26.1.2:runCctGameTest` | 18 | 4 | 0 |
| 22 | `:tweakium:fabric:26.1.2:runPeripheralGameTest` | 36 | 0 | 0 |
| 23 | `:testiarium:fabric:26.1.2:runClientGameTest` | 1 | 0 | 0 |
| 24 | `:broccolium:forge:26.1.2:runStorageGameTest` | 14 | 0 | 0 |
| 25 | `:testiarium:forge:26.1.2:runGameTestServer` | 14 | 3 | 0 |
| 26 | `:testiarium:forge:26.1.2:runGameTestServer -Ptestiarium.cct` | 18 | 4 | 0 |
| 27 | `:tweakium:forge:26.1.2:runGameTestServer` | 36 | 0 | 0 |
| 28 | `:testiarium:forge:26.1.2:runClientGameTest` | 1 | 0 | 0 |

Every standalone/CCT report preserves `StandaloneGameTests.requiredFailure` → `required failure fixture`, `StandaloneGameTests.optionalFailure` → `optional failure fixture`, and `StandaloneGameTests.failFastFailureCannotBecomeSuccess` → `fail-fast sentinel`. Only target CCT adds `CctHarnessGameTests.luaFailureCannotBecomeSuccess` → `cct lua sentinel`. No other skip is accepted. The required outer assertion/exception/error and target real-Lua queued-action observers pass; their exact identities are retained in each receipt.

Commands: `python3 <standalone>/scripts/verification/run-native-matrix.py --output <evidence>/matrix-3`; it invokes checked-in `run-gametest.sh` for every table row, with `--no-daemon --console=plain`, native900s/client300s deadlines and Xvfb for clients. Collector outer watchdogs are960s/360s. Six existing report/publication guard tests also exit0 (`guard-tests-resumed.log`); these are collector/contract checks, not additional native gameplay cases. Actual environment: Temurin25.0.3+9 launcher, retained native Java17/21, worker `MemoryMax=8589934592`, `/usr/bin/xvfb-run`, restored disk capacity. Artifact refresh is now running as tracked process `proc_2ad532add69f`; no completion claimed yet.
