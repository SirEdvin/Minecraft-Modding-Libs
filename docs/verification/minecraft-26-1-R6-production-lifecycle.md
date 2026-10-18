# R6 production lifecycle — incomplete checkpoint

Task: t_b52730bf. Status: BLOCKED BEFORE SERVER EXECUTION; no lifecycle acceptance claimed.

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
