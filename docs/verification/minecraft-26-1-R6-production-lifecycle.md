# R6 production lifecycle — incomplete checkpoint

Task: t_b52730bf. Status: IN PROGRESS; EULA prerequisite resolved, full matrix not yet accepted.

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
