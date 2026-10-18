# R6 production server lifecycle verification

Task: t_b52730bf. Status: COMPLETE for this production-lifecycle child. The historical checkpoints below retain their original limitations; final acceptance is recorded in the last section. This is not completion of the broader R6 campaign.

## Verified inputs

- Assigned isolated worktree/branch: `.worktrees/t_b52730bf`, `wt/t_b52730bf`; initially clean at 27798ce.
- Predecessor t_f0a99e8c is done. Fast-forward merged planning b8fd1ddfd5134c45402040fe2c1ca5be343ac48f, then exact cumulative R5 4a07553673bcd51576e0857301dda867bc176a2c. Both are verified ancestors. No source edits, primary merge, push or publication.
- Read remaining plan, original migration brief, source ledger, R5 receipt and OpenWiki index. Parsed coverage JSON: 204 rows; R6:1 is the parent-owned production assembly/lifecycle requirement implemented by this child. Full native/save/network acceptance belongs to sibling cards and integrated replay to t_fa26ffb6.
- Supported production routes: 1.20.1 Fabric/Forge (Java17), 1.21.1 Fabric/NeoForge (Java21), 26.1.2 Fabric/NeoForge (Java25). Keep exact accepted dependency pins; do not substitute current latest.
- Independently hashed the retained R5 evidence ZIP at `/home/siredvin/projects/Minecraft-Modding-Libs/.worktrees/t_f0a99e8c/build/verification/t_f0a99e8c-R5-evidence.zip`: SHA256 d8da14a41279d99cf95227f8b3012585989009144d7fd94ab0aff39bd501f4dc. It opens successfully and contains `evidence/artifacts-final.json`, `evidence/final-receipt.json`, `evidence/consumer-results.json` and the scratch file-Maven input. Full individual artifact extraction/hash verification remains pending.

## EULA preflight — actionable blocker

Read-only filesystem traversal of the project (including assigned/prior worker trees, excluding `.git`, node_modules and protected final-owner t_16b4e35f) found 32 eula.txt files, all explicitly `eula=false`. No accepted EULA file exists in the inspected project paths. No server was started and no EULA setting was changed.

Session-history queries for EULA authorization found approvals scoped to other projects (GregTech-True-Age-of-Steam and ArcaneArchivesRecrafted), not this project's scratch production assemblies. Those are not treated as transferable authorization. Execution approval for R6 does not silently accept legal terms; the task expressly requires obtaining EULA authorization if absent.

Required user input: confirm acceptance of Minecraft's EULA and authorize setting `eula=true` only in this task's isolated scratch production-server directories for the six routes. Alternatively provide the path/record of existing authorization applicable to this project. No redundant general execution approval is requested.

## Exact resumption procedure

1. Read card and this checkpoint, verify clean Git state and cumulative ancestry; read the newly supplied EULA authorization before writing any acceptance setting.
2. Extract the verified R5 ZIP into this task's scratch directory; validate individual publication artifacts against its final manifest and source receipt. Build assemblies using only production main artifacts and real runtime dependencies, excluding test-mod/CCT/private fixtures and producer class directories.
3. Discover existing loader launch contracts; use actual production distributions/processes or external file-Maven consumers. Testiarium Fabric native runServer is not a production route. Preserve launcher commands, selected JDK and all dependency/artifact hashes per assembly.
4. Bind scratch servers to loopback on available ports, retain explicit startup/shutdown deadlines, wait for normal ready, send `stop` through stdin, require natural successful exit plus save/shutdown signals and actual saved world files. Timeout kill and GameTest exit are failures/incomplete, never clean-stop receipts. Do not touch live servers/worlds.
5. Fix only demonstrated lifecycle defects, with owning red/green tests and refreshed affected artifacts. Produce per-route receipts and local commits, then complete this child to release existing integration gating. No same-card duplicate review, push, release, public publication or service restart.

No Gradle/runtime verification was executed in this checkpoint. The evidence ZIP and ancestry checks are input validation only, not production readiness, clean shutdown, save/restart or broader compatibility evidence.

## Resumed production execution checkpoint

The project owner explicitly authorized `eula=true` in this card's comment thread: "I confirm, set EULA true for this project too". This supersedes the above legal blocker, not the local-only execution boundary. Acceptance was written only in this task's isolated scratch production directories.

Scratch root: `/home/siredvin/.hermes/profiles/albina/cache/scratch/t_b52730bf-validation`. Extracted R5 archive under `R5`; independently verified all 108 file-Maven JARs against `evidence/artifacts-final.json`. Assembly scripts use only four main adapter artifacts per route plus real pinned production runtime dependencies, never test-mod/CCT classifiers or producer classes. Java17/21/25 and loader pins are retained. Each server binds loopback with an ephemeral port.

New reproducible observer: `scripts/verification/production-lifecycle.py`, explicit assembly command/hash input, bounded startup/shutdown, actual stdin `stop`, natural exit plus ready/save signals and saved files. Guard tests run separately and never count as server proof. Six guard tests now pass; modern world-region layout regression failed first, then passed after adding the observed `dimensions/minecraft/overworld/region` layout. Earlier narrow region-path observer receipts remain failed, not rewritten as successful executions.

Both target production distributions have already reached normal ready, accepted console stop, saved all dimensions and naturally exited0. Their first observers rejected the new region layout despite real files; final observer replays are underway. Fabric1.21.1 is a full observer pass. NeoForge's first scratch assemblies lacked transitive KFF language components; traced exact pinned POM/JAR contents and added kfflang/kfflib/kffmod, with no library-source change or version substitution. These failed runs are retained. Python HTTP download of Forge installer hit Cloudflare1010; standard curl fetched the actual pinned installer successfully, and installation is underway. No policy/runtime setting changes.

Exact next actions: finish six-route observer acceptance and same-world restart replays, collect per-assembly artifact and distribution hashes/commands/logs/world hashes, run guard tests and whitespace, archive evidence in this worktree's ignored build/verification directory and locally commit completed receipt. Broader native/copied-save/multiplayer work remains outside this child.

## Final verified acceptance

All six production distributions consumed the exact R5 main adapter publications for Broccolium, Tweakium, Peripheralium and Testiarium, plus pinned ordinary runtime dependencies. No test-mod/CCT classifiers, private fixtures, producer class directories, dev launcher or GameTest mode were used. Every accepted run reached ordinary `Done (...)! For help, type "help"`, received `stop` on its own stdin, logged server stop/player save/world save/all-dimensions-saved and naturally exited0 before the deadline. Actual level.dat and region files were hashed. A separate same-world restart repeated this sequence on all six routes; each level.dat was rewritten and the hashed pre-restart backup remained unchanged.

| Minecraft | Loader | Java | Initial ready seconds | Restart ready seconds | Initial / restart |
| --- | --- | --- | --- | --- | --- |
| 1.20.1 | Fabric 0.18.4 | 17 | 26.342 | 13.940 | PASS / PASS |
| 1.20.1 | Forge 47.1.0 | 17 | 22.737 | 22.089 | PASS / PASS |
| 1.21.1 | Fabric 0.18.4 | 21 | 26.720 | 11.145 | PASS / PASS |
| 1.21.1 | NeoForge 21.1.9 | 21 | 20.079 | 13.448 | PASS / PASS |
| 26.1.2 | Fabric 0.19.5 | 25 | 17.324 | 14.042 | PASS / PASS |
| 26.1.2 | NeoForge 26.1.2.122 | 25 | 16.390 | 15.836 | PASS / PASS |

Twelve accepted production process executions, all exit0, zero timeouts/forced termination. Additional failed scratch-assembly/observer attempts are preserved and are not added to the accepted total. The Forge-family filesystem route is still named `forge`, intentionally also for NeoForge.

### Exact artifacts, source and launch contracts

- Artifact source receipt: R5 cumulative `4a07553673bcd51576e0857301dda867bc176a2c`, implementation candidate `12d687a`. All27 R5 changed-source hashes were independently rechecked in this checkout after execution. No production source/build/resource/dependency changed in this child, so no candidate rebuild or release bump was needed.
- All108 retained file-Maven JARs independently matched the R5 final manifest. The24 selected main production JARs additionally passed the existing `check_jar` checks: metadata, expected class major61/65/69, access rules/Mixins, no Minecraft classes/private fixtures. No new full Gradle build/unit-matrix claim; those same-source R5 receipts remain upstream.
- Each `servers/<version>-<loader>/assembly.json` records exact command, source SHA, installer URL and every selected mod SHA256. Each `distribution.json` records actual JDK version, all distribution JAR sizes/hashes (including the launcher/installer), properties/EULA/launch-argument hashes and fixture-isolation check. Runtime artifact inventory is 109/113/107/121/113/81 JAR files respectively in the table's order, not an assertion that each is a loaded mod.
- Java paths: `/usr/lib/jvm/java-17-openjdk-amd64/bin/java`, `/home/siredvin/.gradle/jdks/eclipse_adoptium-21-amd64-linux.2/bin/java`, `/home/siredvin/.gradle/jdks/eclipse_adoptium-25-amd64-linux.2/bin/java`.
- Fabric command: selected Java, `-Xms256M -Xmx1536M -jar launcher.jar nogui`. Forge-family command: selected Java, same memory arguments, `@libraries/net/minecraftforge/forge/1.20.1-47.1.0/unix_args.txt` or `@libraries/net/neoforged/neoforge/<selected-version>/unix_args.txt`, `nogui`. Each cwd is only its isolated scratch assembly. Installers were run with `--installServer`; no services were restarted.
- All six servers used `server-ip=127.0.0.1`, `server-port=0`, `online-mode=true`, view/simulation distance2; no remote network exposure or account credentials were needed for console lifecycle verification.

Observer invocation: `python3 scripts/verification/production-lifecycle.py <assembly-directory> --label <receipt-label>`. Bounds default to startup240s and shutdown90s. Accepted first receipt labels: `lifecycle` for old Fabric, `verified` for both target loaders and both Forge-family old routes. All explicit restart receipts are `restart`. `restart.py` retains backup manifests, checks untouched backups and actual level.dat rewrites. Guards: `PYTHONDONTWRITEBYTECODE=1 python3 scripts/verification/test-production-lifecycle.py` (six pass), existing `scripts/test-verification.py` (six pass), `git diff --check` (exit0). Guard tests are tooling checks, not Minecraft cases. No fresh GameTest XML is claimed by this child.

### Diagnostics and limits

Initial scratch NeoForge assemblies omitted the KFF POM's kfflang/kfflib/kffmod components; corrected at exactly5.7.0/6.4.0, never by changing dependency pins or library code. The initial modern-region observer assumption was fixed with an executed red/green test; earlier receipts remain explicitly false. Forge installer Python fetch failed with Cloudflare1010, whereas ordinary curl fetched the pinned production installer; no policy override or special credentials were used.

The successful lifecycle does not mean log-silent startup. Old scratch flat-generator properties without explicit layers emitted `No key layers in MapLike[{}]`; vanilla used its fallback and saved worlds. Forge1.20.1 logs inherited missing Mixin `minVersion` metadata notices. NeoForge26.1.2's upstream DebugFile appender can report a Netty KQueue OSX/BSD-probe exception on Linux during socket setup; it subsequently binds, reaches ready, saves, stops and exits0. These retained diagnostics do not originate in an observed library lifecycle failure and have not been suppressed or "fixed" through unrelated production changes. Reproduction should supply explicit flat layers if flat generation itself is under test.

No demonstrated library lifecycle defect remained, and no code/build hotspot was touched. Carry the unchanged upstream hotspot boundaries: `gradle/minecraft-26-1.gradle.kts` and `gradle/gametest-reports.gradle.kts`. No campaign-wide compatibility, cross-version content migration, downgrade, actual client join, adversarial menu/network, native observer matrix or client-rendering acceptance is claimed. Those belong to siblings and t_fa26ffb6; integrated artifact changes must replay affected lifecycle checks rather than reuse divergent-artifact results.

### Durable evidence and handoff

Evidence is retained outside the pruned scratch cache at `build/verification/t_b52730bf-R6-production-evidence.zip` in this assigned worktree. It contains actual24 production main JARs, assembly/distribution manifests, launcher argument files, server properties/EULA authorization, initial/restart and failed logs/receipts, pre-restart worlds and manifests, final saved worlds, exact R5 input receipts and reproduction/guard scripts. No native board attachment is asserted; the adjacent delivery JSON records the archive SHA256, size and member count after a successful ZIP integrity check.

The JSON receipt is `build/verification/t_b52730bf-R6-production-delivery.json`. Downstream integration should merge this child's final cumulative commit, retain the R5 source/artifact mapping, and use the archived scripts/assemblies or rebuild them from the pinned installer/dependency identities. This completes only this child's production ready/stop/save/restart requirement. No push, primary merge, release, remote Maven publication, shared-service/profile change or protected final-owner worktree access occurred.
