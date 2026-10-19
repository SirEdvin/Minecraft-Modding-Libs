# R6 — dedicated-server, save and network compatibility

Status: PASS — aggregate R6 milestone, task `t_997f2d4d`, 2026-10-18. All four capability lanes and their cumulative integration are complete; final evidence was independently audited in this worktree. The earlier EULA blocker below is historical and resolved by explicit dashboard acceptance recorded on the production/save cards. R7 visual/optional integrations and final campaign reporting remain downstream; this is not whole-campaign completion.

Sections through “Resume sequence after input” preserve the initial incomplete checkpoint, not current acceptance. Use “Final aggregate acceptance” below and the linked integration report for current results.

## Verified baseline and provenance

In the assigned isolated worktree, merged planner `b8fd1ddfd5134c45402040fe2c1ca5be343ac48f`, then immediate predecessor cumulative `4a07553673bcd51576e0857301dda867bc176a2c`; both fast-forward merges exited 0. Verified both ancestry relationships and the R5 receipt's earlier R0–R4/original-plan/coverage/OpenWiki ancestors with `git merge-base --is-ancestor` (all exit 0). Initial and post-merge tracked trees were clean. No source change, version switch, push, remote publication or primary merge was performed.

Read the remaining plan, original migration brief, source ledger, 204-row coverage map, OpenWiki index and R5 verification receipt. The card's superseding execution authorization governs historical planning holds. Retain all six loader/version pairs, exact 26.1.2 pins, canonical 1.20.1, runtime ItemStacks, existing persistence ownership and local-only publication boundaries.

R5 evidence archive:

- Path: `$TMPDIR/t_f0a99e8c-validation/t_f0a99e8c-R5-evidence.zip`.
- SHA-256 verified: `d8da14a41279d99cf95227f8b3012585989009144d7fd94ab0aff39bd501f4dc`.
- ZIP CRC integrity check returned no bad member.
- `evidence/final-receipt.json` identifies cumulative R5 SHA above. All 27 handed-off changed-source SHA-256 values match this checkout; mismatch list is empty.
- Archive contains the file-Maven candidate; no archived EULA path was found. Do not mistake inherited unit/publication receipts for R6 server/save/network execution.

Temurin Java 25.0.3+9 verified at `/home/siredvin/.gradle/jdks/eclipse_adoptium-25-amd64-linux.2/bin/java`. No heavy build/decompile was started; initial `/sys/fs/cgroup/memory.max` and `memory.current` paths were not readable in this tool environment, so no memory-limit value is inferred.

## EULA preflight and actionable input

The card explicitly requires respecting existing EULA authorization and obtaining it when absent. A filesystem walk including ignored runtime files found no `eula.txt` in the assigned checkout or R5 scratch evidence tree. A read-only walk of the primary repository (excluding `.worktrees`, `.git`, `.gradle` and `node_modules`) found nine existing EULA files; all contain `eula=false`. No EULA file was edited, copied or created.

Repository documentation, scripts and task authorization contain no project-specific acceptance. Targeted conversation searches did not locate acceptance for Minecraft-Modding-Libs; results describing isolated acceptance in other projects are not extended to this campaign. This is a bounded search result, not a claim that no acceptance exists anywhere.

Required operator response: confirm acceptance of the Minecraft EULA for the disposable R6 verification servers on 1.20.1 Forge/Fabric, 1.21.1 NeoForge/Fabric and 26.1.2 NeoForge/Fabric, or identify an existing acceptance record applicable to these runs. Migration execution approval alone is not substituted for this explicit prerequisite. No fresh global implementation approval, credentials in chat, scope reduction or release approval is requested.

## Source inspection and evidence limits

Located MenuBuilder/SavingFunction definitions and loader usages. Target Fabric opening data is detached and bounded to 32600 bytes, with temporary buffers released in `finally`; existing unit fixtures cover boundary round-trip, oversize/truncated/negative lengths and encode-buffer cleanup. These are inspected source contracts, not freshly executed tests or proof of adversarial multiplayer safety. Menu opening is server-to-client; do not label arbitrary malformed opening bytes as a new client-to-server protocol.

`projects/tweakium/versions/26.1.2/src/testMod/kotlin/site/siredvin/tweakium/testmod/WorldReloadFixture.kt` reloads a saved tag within a process. It does not prove clean dedicated shutdown/restart, disk upgrade or copied-world preservation.

No R6 Gradle command, GameTest, production server, client, save upgrade, network rejection or conservation scenario has executed in this checkpoint. Fresh report inventory is empty; there is no passing count or skip waiver. Production source and R5 artifacts remain unchanged, so no R5 artifact refresh has yet been triggered.

## Resume sequence after input

1. Read the saved checkpoint and exact R5 receipt; recheck live Git state and archive/source hashes rather than repeating planning research. Record the supplied acceptance scope and keep EULA files in disposable runtime directories only.
2. Assemble scratch production consumers/servers from the exact R5 file-Maven JARs and pinned required dependencies, excluding all test-only features. For each of six nodes record resolved artifact hashes, normal ready, issued `stop`, clean exit and world-save receipts. Bind only task-owned local test endpoints; never touch a live world or service.
3. Execute the complete discovered native/CCT/observer matrix with wrapper, `--no-daemon`, deadlines and fresh exact report guards, including target Forge storage and zero storage skips. Preserve required negative identities.
4. Create representative old worlds, clean-stop and hash untouched backups; migrate scratch copies per loader, verify/save/stop/restart/recheck including staged legacy content where represented. Never promise downgrade or cross-loader conversion.
5. Add owning adversarial menu/action fixtures, execute behavioral red before any root-cause fix, then green/affected old checks. Exercise simultaneous interactions using two actual clients on each target loader. Native mock players, fabricated protocol messages and unit codec cases are not acceptance substitutes. Client provisioning is still to be established, not asserted impossible by this checkpoint.
6. Refresh affected R5 artifacts/consumers after any production change; restore canonical sources, preserve receipts and complete only when all card-local gates pass. R7 retains title/world/render/optional integrations; F retains final campaign/wiki/Obsidian reporting.

Hotspots carried forward (unchanged here): `gradle/minecraft-26-1.gradle.kts` and `gradle/gametest-reports.gradle.kts`.

## Final aggregate acceptance

Merged exact cumulative integration `01bee5a3ac989eb8b4c0039cd2df25fc27568f52` into this assigned worktree without conflicts; aggregate merge is `194046bc364c4c6e6a2dfd9f589ddf6ec1be0987`. Independently verified planner/R5/production/native/save/network/integration ancestry (seven checks, exit 0). Tested implementation is `39e77875c3f5e1cdeb3a06867a0d99e8d8816544`, tested source snapshot is `32f6c4322d891565b94a2e0a0d1fd2acfc3d3cfb`. Only the integration index and this aggregate report differ from the tested commit. No new production, fixture, build or dependency edit was made by this aggregate audit; no further artifact invalidation occurs.

Completed capability reports:

- [Production lifecycle](minecraft-26-1-R6-production-lifecycle.md): all six supported loader/version routes ready, console stop, save, natural exit 0 and same-world restart, using production-only assemblies.
- [Native matrix](minecraft-26-1-R6-native.md): legacy Fabric feature classpath and registration repairs, retained actual failures and refreshed builds/consumers.
- [Copied saves](minecraft-26-1-R6-saves.md): repaired actual legacy turtle-upgrade settings loss in the 1.21.1 conversion, preserved native inventory/overlay rewriting and immutable originals/backups.
- [Network](minecraft-26-1-R6-network.md): both target loaders, two actual graphical TCP clients each, malformed/truncated/oversized schema and authorization/conservation checks; no mocked networking substitute.
- [Cumulative integration](minecraft-26-1-R6-integration.md): full combined-artifact replay and all source/artifact/receipt bindings. Its command ledger is authoritative for actual runtime execution; the aggregate did not redundantly rerun that campaign.

Final combined-candidate results, not sums of overlapping lane runs:

| Gate | Result |
| --- | --- |
| Full wrapper build and publication contracts | Exit 0; 36 contracts; nine exclusive file-Maven Java/Kotlin consumer routes, 27 external compile/execute commands, all exit 0 |
| Fresh versioned unit XML | 109 reports, 19 nodes, 1,753 cases; zero failures/errors/skips |
| Native matrix | 28 routes, each and outer collector exit 0; 434 cases, 396 positive, 38 exact intended negative skips; zero failures/errors |
| Native storage/Tweakium | 99 storage cases, zero storage skips; zero Tweakium skips |
| Production lifecycle | Six routes, 12 ready/stop/save/restart executions, all natural exit 0, no private fixtures |
| Copied-save matrix | 20 executions and 20 exact semantic comparisons; same-version/direct/staged routes for both loader families, all exit 0 |
| Preservation | Six new and six original save backup archives, four original worlds and six production restart backups unchanged; no original reopened |
| Real two-client networking | Two target loaders, two graphical clients each, 12 synchronized phases and 24 received packets each; natural server/client exits 0, rejected state conserved |
| Client harness | Six fresh native harness screenshots; not R7 title/world/render/integration acceptance |

Exact negative identities/messages remain `StandaloneGameTests.requiredFailure` / `required failure fixture`, `StandaloneGameTests.optionalFailure` / `optional failure fixture`, `StandaloneGameTests.failFastFailureCannotBecomeSuccess` / `fail-fast sentinel`, and target CCT `CctHarnessGameTests.luaFailureCannotBecomeSuccess` / `cct lua sentinel`. The aggregate audit reparsed every native XML, matched report hashes and exact per-route skip maps, reconciled declared/testcase counts, and rejected storage/Tweakium skips. The complete identity/skip map is in `build/verification/t_997f2d4d-R6-aggregate-audit.json`.

### Independent aggregate verification

`python3 build/verification/verify-r6-aggregate.py` — final exit 0. Recomputed integration archive SHA-256, size, ZIP CRC, all 1,783 payload hashes, all 118 artifact hashes and 1,198 source bindings. The only source-manifest mismatch is the documented final integration index; Git confirms no behavior difference from the tested source. Reparsed all 109 unit and 28 native XML, correlated both network receipts to the identical eight target production binaries, and verified the six-route/20-save runtime summary. The first audit attempt exited 1 because its documentation-only diff allowlist omitted this pre-existing aggregate report; corrected the allowlist, then reran successfully. That was an audit assertion correction, not a runtime defect or acceptance pass.

`python3 scripts/verification/test-production-lifecycle.py` — exit 0, six cases. `python3 scripts/verification/test-save-fixture.py` — exit 0, 15 cases. Both executed again in this aggregate worktree. `git diff --check` — exit 0. Canonical source state remains 1.20.1. All inherited Gradle runs use the checked-in wrapper, `--no-daemon`, verified Java 25 launcher with isolated Java 17/21 nodes, deadlines and real logs. No web/version refresh is needed or performed: selected campaign pins are retained, not replaced by global latest.

Evidence pack copied byte-for-byte into this worktree at `build/verification/t_997f2d4d-R6-evidence.zip`: SHA-256 `33d25ebbeb0e8fb2630fdf0d5c5ff26ee918f0508ffa7a1f05da88416be52775`, 112,478,648 bytes, 1,784 members. It includes all five immutable input packs, final JARs/Maven metadata, source snapshot/diff, commands, fresh XML, runtime receipts, original failed attempts and explicit limits. Local aggregate audit script/JSON and delivery receipt accompany it. This exceeds the 25 MB native attachment cap; the completion artifact path is the deliverable, with the small receipt attached separately.

### Artifact reuse and remaining boundaries

Of 118 final JARs, 105 are byte-identical to R5; seven intentionally represent the 1.21.1 turtle repair, four represent old Fabric CCT fixture changes, and two Peripheralium Forge-family source archives differ only in ZIP entry order. Three of 36 production main binaries changed. Do not claim blanket 118-byte reproducibility. The integration rebuilt/refreshed affected artifacts, publication contracts and consumers and reran the entire native/production/save combination.

Actual-two-client receipts are retained executions, not newly run in integration or aggregate: all eight target main binaries, 17 network source hashes and both private fixture hashes are identical. The network reuse decision and exact packet/schema/thread/buffer receipts are embedded. NeoForge's outer menu framing remains loader-owned/unbounded; the owning consumer enforces its 12-byte schema. Offline loopback identities do not certify online authentication or arbitrary third-party permissions/protection.

Represented production save content and explicit limits are unchanged from the save lane: real registered content, pockets/turtles, owner/side data, component removals, unrelated patches, item/fluid-item content and turtle fuel. No concrete Tweakium production upgrade, persistent tank/battery or fuel-as-energy claim is invented. Separate native tests cover live item/fluid/energy capabilities. No cross-loader conversion, downgrade, cross-version wire/ABI or blanket add-on compatibility claim is made. No migration files deleted, remote publication, push, primary merge, release, service restart or profile change occurred.

Pre-created R7 child `t_14e20aca` was inspected and remains dependency-gated on this aggregate. Completion releases it; merge the exact aggregate completion SHA before work, retain these artifact identities and rerun affected R6 checks only if R7 changes them. Final owner `t_16b4e35f` and Chrono `t_878aa68c` remain unchanged. Hotspots: `build.gradle.kts` (old Fabric named feature core edges), `gradle/minecraft-26-1.gradle.kts` (target convention), `gradle/gametest-reports.gradle.kts` (exact guards).
