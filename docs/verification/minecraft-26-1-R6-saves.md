# R6 copied-save workstream — incomplete prerequisite checkpoint

Task: `t_4ecc7749`, split from R6 `t_997f2d4d` on 2026-10-18.
Status: **INCOMPLETE — authorized runtime underway; migration acceptance pending.**
The historical prerequisite checkpoint below is retained; the EULA blocker is now cleared.

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

## Authorized resumption / owning fixture checkpoint

The user explicitly confirmed EULA acceptance in this card on 2026-10-18:
"retry it again, I confirm minecraft eula should be true". Also read production
owner t_b52730bf's original project acceptance. Set true only in six new,
exclusive task-local scratch assemblies. Merged completed production sibling
`7771c48431a2ed7f8208a8a62bead8d1c42496fd` to reuse its actual bounded
ready/stop/save verifier, not its empty-world results as migration proof.
Re-executed R5 preflight: all118 artifact and27 source hashes match, ancestry
unchanged. Read the plan, brief, ledger and owning source again at resumption.

Scratch root: `$TMPDIR/t_4ecc7749-validation`. Production assembly manifests
and installed distributions were read/copied from the completed sibling, never
modified there. Its scratch cache had lost old distribution libraries; the
copied NeoForge1.21.1 attempt really failed `BootstrapLauncher` before ready.
Re-executed all three exact Forge-family installers in this card's assemblies,
all exit0. No server/world or service outside this task was restarted.

New owning helpers: `scripts/verification/save-fixture.py`, `save-nbt.py` and
`test-save-fixture.py`. Twelve tooling guards pass; these are not Minecraft
cases. The probe validates exact R5 main JAR hashes, uses actual console
creation and `/data get block`, requires ordinary ready/save/stop/natural0,
reads actual compressed Anvil/NBT and records raw plus normalized semantic
state. Backup copying refuses an existing destination and checks both sides.
No helper accepts terms, rewrites NBT, runs a data fixer, deletes migration
files, downgrades or converts loaders.

Fixture v1 was correctly rejected by independent semantic checks after real
Fabric1.21.1 save: a native modem overwrites its own managed data, and vanilla
ItemStack normalizes away a removal for a component absent from defaults.
Retained this failed draft world/log/NBT and its hashed untouched backup under
`attempts`; never counted it as accepted. Fixture v2 instead uses non-ticking
speaker upgrades with distinct left/right/back custom data, meaningful removal
of the pickaxe's default `minecraft:tool`, explicit-removal entries inside
upgrade DataComponentPatch and owning `__patch_storage__`. Actual Fabric
v2 creation reached ready/save/stop/natural0, raw disk evidence and independent
hardcoded expected-state validation pass. Cross-version opening/restarts
remain outstanding at this checkpoint.

Production representation boundary (not a silent waiver): Tweakium registers
the real creative_filler/settings/owner BE, but no concrete stateful upgrade
definitions. Existing `r3_native` is private and target-only; do not fabricate
an old registration or introduce it into production assemblies. Use real
CC speaker pockets/turtles for installed upgrade disk schemas and side data.
CreativeFiller's item/fluid/energy sinks are void sinks, not persistent tanks
or batteries; native storage acceptance is a separate R6 lane. Represent real
item/fluid containers (water/lava buckets) and turtle fuel; do not label fuel
as a Broccolium energy store or invent saved fluid/energy providers. Stable
Peripheralium block/dust/template IDs are represented; legacy-only blend is
intentionally excluded from the staged shared-content fixture.

Next exact action: create/independently validate NeoForge1.21.1 and both1.20.1
fixtures; hash exclusive untouched backups, same-version restarts, then
independent copied1.21.1→26.1.2 and staged1.20.1→1.21.1→26.1.2 save/stop/
restart comparisons per loader. Preserve failures, commands and native
layout migration files. No production source change or migration pass claimed.
