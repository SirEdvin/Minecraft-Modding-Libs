# R6 copied-save workstream — incomplete prerequisite checkpoint

Task: `t_4ecc7749`, split from R6 `t_997f2d4d` on 2026-10-18.
Status: **INCOMPLETE — needs explicit applicable Minecraft EULA acceptance before runtime.**
No save compatibility result, immutable world backup, or migration pass is claimed.

## Verified inputs

In the assigned isolated worktree, executed in order (each exit 0):

```sh
git merge --no-edit b8fd1ddfd5134c45402040fe2c1ca5be343ac48f
git merge --no-edit 4a07553673bcd51576e0857301dda867bc176a2c
git merge-base --is-ancestor b8fd1ddfd5134c45402040fe2c1ca5be343ac48f HEAD
git merge-base --is-ancestor 4a07553673bcd51576e0857301dda867bc176a2c HEAD
python3 build/verification/save-preflight.py
git diff --check
```

Both merges fast-forwarded; production sources remain exactly R5. Read the approved remaining plan, original brief/source ledger, OpenWiki navigation and existing module coverage; parsed all 204 ownership rows, including `PLAN:L166` and `R6:3`. Execution release recorded in the R6 card supersedes the immutable plan's old planning-only hold, but is not acceptance of legal terms.

Read-only preflight independently verified:

- R5 archive SHA-256 `d8da14a41279d99cf95227f8b3012585989009144d7fd94ab0aff39bd501f4dc` and ZIP CRC integrity.
- R5 cumulative source SHA `4a07553673bcd51576e0857301dda867bc176a2c` and every ancestor listed in its final receipt.
- All 27 changed-source hashes against this checkout.
- All 118 artifact manifest hashes against actual archived JAR bytes, not just a copied digest list.
- All three existing Tweakium `WorldReloadFixture.kt` files, recording their individual hashes.
- No `eula.txt` in this assigned worktree.

Read-only input archive (not copied or modified):
`/home/siredvin/projects/Minecraft-Modding-Libs/.worktrees/t_f0a99e8c/build/verification/t_f0a99e8c-R5-evidence.zip`.

Local ignored evidence/reproducer, retained outside the pruned scratch cache:

- `build/verification/save-preflight.py`
- `build/verification/save-preflight.json`

These checks are prerequisite evidence only. No new Gradle, Minecraft or GameTest process ran. Runtime command list, world-fixture list, backup hashes, migration results, fresh XML and skip map are explicitly empty in the receipt. No production behavior or R5 artifact was changed.

## Source-grounded fixture boundary

Existing `projects/tweakium/src/testMod/kotlin/site/siredvin/tweakium/testmod/WorldReloadFixture.kt:6` serializes and loads an in-memory Creative Filler. The 1.21.1 override uses registry-aware NBT; the 26.1.2 override uses `TagValueInput`. None stops a process, writes a representative world, opens it on another version, or proves disk restart. Do not relabel these as migration evidence.

Target `NativeUpgradeFixtures.kt` registers `tweakium:r3_native` only in the private testmod. It can support focused fixture development but cannot silently become a production dependency or imply that the old saves already contain that registration. Before writing the runtime fixture, establish stable owning fixture IDs across represented versions and keep any private harness distinct from the sibling production-only lifecycle assemblies.

## Required resume action and acceptance

R6 parent comments already record the missing EULA authorization, including previously inspected repository files with `eula=false`; decomposition supplied no new acceptance record. This card does not infer acceptance from dispatch, generic execution approval or unrelated projects. Obtain confirmation accepting the Minecraft EULA for disposable R6 verification servers across 1.20.1 Forge/Fabric, 1.21.1 NeoForge/Fabric and 26.1.2 NeoForge/Fabric, or an applicable existing acceptance record. No EULA value was written or altered.

After that input:

1. Coordinate with lifecycle owner `t_b52730bf` for its exact production assembly manifests/commands; retain R5 source/artifact traceability. Do not concurrently change root convention/report wiring.
2. Implement the smallest owning, version-aware fixture that creates and semantically inventories represented registered blocks, settings/custom names, pocket/turtle state, explicit component removals, owner/side associations and item/fluid/energy data. Mark genuine representation limits explicitly; do not replace required content with invented NBT or fabricate expected observations.
3. Independently per Fabric and Forge-family route, create and cleanly stop a 1.21.1 save. Preserve an untouched backup with per-file and archive digests before opening only a scratch copy on 26.1.2. Record IDs, counts/variants, settings, removals, associations, recipes/configs; clean save/stop, restart and compare again.
4. Exercise 1.20.1 save/reload and staged 1.20.1 → 1.21.1 → 26.1.2 copies where content exists. Recheck backup digests after each migration. Never overwrite originals, promise downgrade, certify cross-loader conversion, or automatically remove migration files.
5. Preserve exact commands/exits, source/assembly/fixture hashes and before/after semantic receipts per route. Add owning behavioral red/green regressions for any compatibility defect, rerun affected old nodes, and refresh changed artifacts before integration.

All runtime acceptance above remains outstanding. This checkpoint does not complete this card or R6, and does not waive any downstream integration requirement. No push, primary-branch merge, release, public publication, service restart or profile change occurred; `t_16b4e35f` was not copied or modified.

Hotspots carried without edits: `gradle/minecraft-26-1.gradle.kts` and `gradle/gametest-reports.gradle.kts`. Sibling workers own other R6 lanes; this checkpoint deliberately uses a distinct report path to avoid competing edits to the aggregate R6 report.
