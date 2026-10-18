# R6 copied-save compatibility evidence

Task: `t_4ecc7749`, split from R6 `t_997f2d4d` on 2026-10-18.
Status: **COMPLETE for this copied-save child.** Final acceptance is recorded below.
Historical checkpoints retain their original limitations and failed hypotheses;
they are not the final status. This is not completion of R6 or the campaign.

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

## Actual staged loss / compatibility repair checkpoint — incomplete

All four v2 sources passed independent expected-state checks. Four exclusive
source backups were hashed before opening copies. Fabric same-version1.20.1
and1.21.1 each passed two copied-world boots; direct1.21.1→26.1.2 passed
save/stop/restart with semantic equality after a narrowly tested normalization
of native GameProfile `{UpperId,LowerId,Name}` to `{id,name}`. Raw NBT is
retained. Guard red/green proves actual UUID/name changes are still rejected.

The staged1.20.1→1.21.1 copy exposed a real data-loss gate: installed turtle
upgrade IDs survived, but both legacy `LeftUpgradeNbt`/`RightUpgradeNbt`
custom settings disappeared. Pocket state, filler owner/settings, inventory
and fuel did not show this loss. `matrix-v2/save-matrix.json` remains
INCOMPLETE with the exact false comparison and original hashes; the loss
has not been normalized away or waived as an expected migration.

Traced the exact1.120.0 CC source JAR: `V1460Mixin` makes the upgrade ID a
typed field; `ComponentizationFixers.makeBlockEntityRewrites` looks for that
ID only in the remainder, so its legacy data migration does not run. Modern
`TurtleBrain.readCommon` accepts the string ID as a default upgrade and drops
the separate legacy data when saving. The failed real process is the
behavioral red, not a compile error.

Scoped repair candidate (not yet verified):1.21.1-only loader Mixins restore
legacy data immediately before the actual CC reader using a common helper.
Only string IDs paired with nonempty compound legacy data are converted;
modern patches and removals win, malformed/missing/removed upgrades are not
invented, original legacy input fields remain untouched. No new persistent
file ownership, datapack registry, version pin or root build wiring. Target
code and1.20.1 are unchanged; the required staged route passes through the
repaired1.21.1 boundary. Direct1.20.1→26.1.2 is not a supported/tested route.

Next: compile/test/format the owning helper+adapters, rebuild the three1.21.1
Tweakium publications, re-run actual staged copies from the immutable old
backups with refreshed artifact hashes on both loaders. Re-run direct and
same-version routes against the refreshed cumulative candidate. Preserve all
prior failed receipts; card remains incomplete until the full matrix passes.

### Refined root cause and successful focused repair

The first load-time candidate built and passed its helper tests but remained
RED in an actual copied-world run. Temporary, task-only instrumentation
confirmed the legacy fields were already absent at reader entry; that trace
code is removed. A rejected whole-BE serialization candidate exposed DFU's
adjacent item-componentization transient type mismatch (Either/Pair); its
failed world/log remain retained, not rewritten as passing.

The refined repair prepends conversion of just the typed left/right upgrade
fields to CC's original schema rewrite, preserving native item/overlay work.
`LegacyTurtleUpgradeFix` does not serialize the whole BE or traverse inventory
types mid-migration. The generic Dynamic helper handles only the two real CC
turtle IDs and preserves modern patches, originals and absent/malformed data.
The original native rewrite remains in the composition. Native schema output
decoding failures remain fatal; no fallback to a successful empty world.

Actual focused fresh1.20.1→1.21.1 copies now pass expected semantic equality
and normal ready/console save/stop/natural0 on both Fabric and NeoForge.
NeoForge additionally required declaring its previously empty Mixin config
in1.21.1 metadata, confirmed against the pinned CC JAR's real `[[mixins]]`
contract. Both1.21.1 public loader artifacts contain the new helper/hook;
no fixture class enters production. Five owning Dynamic helper cases pass,
including both sides, modern explicit removals, malformed data, idempotence
and unrelated BE identity. Scoped formatter uses unchanged project rules on
all five authored Java/Kotlin files and passes. Canonical1.20.1 and all target
sources remain unchanged. Full candidate matrix/publication refresh is still
pending; this is not card completion.

## Final verified acceptance

Implementation candidate: `90eec90bcdf9323c5e47ec7d27aab4b9ba402183`.
Planner `b8fd1ddfd5134c45402040fe2c1ca5be343ac48f`, exact R5
`4a07553673bcd51576e0857301dda867bc176a2c` and completed production sibling
`7771c48431a2ed7f8208a8a62bead8d1c42496fd` are verified ancestors.
Later changes only harden probe creation/path guards and document evidence;
the tested production sources and seven refreshed JAR hashes are unchanged.

The final `matrix-final/save-matrix.json` is PASS: **20 actual production
server executions and20 exact semantic comparisons**, all ready, live block
queries, console `save-all flush`, console `stop`, complete dimension save and
natural exit0, zero timeouts/forced termination. Four original fixture saves
were created and independently validated before this matrix. Rejected draft,
debug and failed repair executions are not included in the accepted20.

| Same-loader route | Fabric | Forge-family | Boots per loader |
| --- | --- | --- | --- |
| 1.20.1 copied save, open + restart | PASS | PASS | 2 |
| 1.21.1 copied save, open + restart | PASS | PASS | 2 |
| 1.21.1 →26.1.2, save/stop + restart | PASS | PASS | 2 |
| 1.20.1 →1.21.1, save/stop + restart | PASS | PASS | 2 |
| preserved intermediate1.21.1 →26.1.2, save/stop + restart | PASS | PASS | 2 |

Actual disk DataVersions are3465,3955,4790 respectively. Native26.1 world
layout moved to `dimensions/minecraft/overworld/region`; probes read actual
regions, not merely level.dat. All four true original worlds and six final
backup copies/archives were rehashed after the matrix and native replay and
remain unchanged. No original world was reopened. Only scratch copies were
upgraded; all failed copies and any native migration outputs are retained.

### Fixture inventory and semantic checks

At fixed positions `(0..3,80,0)`: the real Tweakium creative_filler with
`ownerProfile=[I;1,2,3,4]`, enabled/limit/nested peripheral settings and modern
`__patch_storage__`; the real Peripheralium block; a chest containing dust17,
template3, an installed-speaker pocket, damaged pickaxe, water/lava buckets
and diamonds23; a north-facing installed-speaker turtle with diamonds11,
water bucket, fuel1234, selected slot2, ID42, label and real owner UUID/name.
Left/right/back upgrade settings are independently distinguishable. Modern
upgrade patches carry explicit CUSTOM_NAME removals and unrelated DAMAGE;
the pickaxe removes its real default TOOL component. Legacy `storedData`
survives componentization inside CUSTOM_DATA. Comparison covers exact block
states, IDs, slot/count/variant data, settings/patches/removals, owner identity,
side association, labels, fuel and inventory. It does not equate empty/default
snapshots to success. Only known structural encodings are normalized (item
Count/tag→count/components, upgrade component/back-pocket key, native owner
UUID encoding); raw before/after NBT accompanies every accepted receipt.

The production representation limits documented above remain explicit:
no private `r3_native` registration is invented on old nodes, no persistent
fluid tank/battery exists in this production content, no fuel-as-energy
claim, no legacy-only blend in a shared-ID staged fixture. Water/lava buckets
are actual fluid containers, not proof of a nonexistent library tank. The
owning library settings are tested through the real filler BE save/load;
the registered native CC upgrades test actual installed disk schemas, not a
claim that every downstream custom upgrade or unrelated mod is compatible.

### Untouched backup archive SHA-256

Per-file manifests, source paths and archive lengths are in the final matrix.

| Backup | SHA-256 |
| --- | --- |
| original1.20.1 Fabric | `d5e8566ad3438135a24f7a0d6db26125bfe17d7a1e7264cfea0445dcc6715d75` |
| original1.20.1 Forge | `c6892b239450d7f07de63775e331d33000f1fe5e55ac69202dabf7e1953f1c48` |
| original1.21.1 Fabric | `68e87cb75858a97bd10312b24799111045ccc760d959079307a76bafda73b4e0` |
| original1.21.1 NeoForge | `8e81cdf666ee782c804961291b2317a4fc206e42b054f7d14c177c2aa88c983a` |
| staged intermediate Fabric | `bdb9754ebd9beb78aeabf6fca59c14302cb3ec382eb2f82f80bd5fabe13bcf33` |
| staged intermediate NeoForge | `ff19f3c5079a5ba757331cba31bd5191e6bab69f96547f73cb1a73aeb73365d9` |

### Refreshed artifacts and owning verification

Only Tweakium1.21.1 common/Fabric/NeoForge production artifacts change; old
1.20.1 and target production artifacts remain exact R5 inputs. Refreshed
seven JARs comprise three main, three merged-source and one raw adapter JAR.
All three owning publication contracts pass the existing checker (same
dependency/capability/source/metadata/access/JVM/fixture-isolation checks,
explicitly selecting only the owning library/version). Main SHA-256:

- common: `a7426bbbf01d2b6d62192b6c4f4bc6e6f2ce12e844f4cccb8fbb7d774f8b2df9`
- Fabric: `ae053c3e5d60f4301fddfc614d666ea998fe2711f588c81234f98f3a249c70f2`
- NeoForge (and raw): `fbc11ad27e98819acc10a0da71dcbbb710a5b4432102a11ada21ee55cad9231a`

Exact-source standalone wrapper build invokes all nine Tweakium version/branch
test tasks with `cleanTest`, all three affected1.21.1 `build`/POM/module tasks
and root `spotlessCheck`; exit0 in140.448s. Fresh reports: **95 cases /21 XML /
seven nonempty nodes**, zero failure/error/skip. The two old Forge-family unit
tasks explicitly report NO-SOURCE and are not counted as tests. Five new
owning repair cases are included. Actual1.21.1 native replay then passes
`:tweakium:fabric:1.21.1:runPeripheralGameTest` and
`:tweakium:forge:1.21.1:runGameTestServer`: **25+26 cases**, zero failure/error/
skip, both task executions exit0. Root report guards remained enabled.
Fifteen Python probe guards pass (including observed owner-schema red→green,
exclusive backups, truncation, removals and refusing existing-world creation).
Probe guards are tooling tests, not additional Minecraft cases. Seven actual
JAR hashes were rechecked unchanged after native replay; no task JVM remains.

### Exact reproduction / evidence custody

Scratch root: `$TMPDIR/t_4ecc7749-validation`. Each `assembly.json` records
the exact Java17/21/25 command, pinned loader installer URL, R5/candidate SHA
and every production mod hash. Forge-family installers were run with the
recorded Java and `-jar installer.jar --installServer`; all three exit0.
Fabric production launchers resolve their pinned distribution. Assemblies
bind loopback with ephemeral ports, online mode true, no enabled remote
administration and explicit project-authorized EULA. No test-only mod or dev
launcher is used for the copied-save20. Recipes/content registrations are
also covered by the separate51-case owning native replay.

Creation command (run once per prepared empty1.20.1/1.21.1 assembly):
`python3 scripts/verification/save-fixture.py <assembly> --label create-v2 --create`.
The complete console SNBT commands and live readback are retained, not
hand-edited region files. Matrix command:
`PYTHONDONTWRITEBYTECODE=1 python3 scripts/verification/save-matrix.py "$TMPDIR/t_4ecc7749-validation/matrix-final" --assemblies "$TMPDIR/t_4ecc7749-validation/servers-candidate"`.
Use a new output directory for a replay; the helper intentionally refuses
existing receipt/backup/output paths. Every matrix copy opens/saves/stops
then restarts without reapplying fixture state. Full Gradle argv/status is in
`candidate-build-command.json`, with `--no-daemon`, explicit540s bound; native
argv has480s bound. Exact standalone source90eec90 is recorded, not an
uncommitted unrelated checkout. The prior failed nine-nonempty-unit-node
bookkeeping assumption is corrected above; it did not invalidate the real
exit0 matrix and is not reported as nine executed test suites.

Durable evidence: this assigned worktree's ignored
`build/verification/t_4ecc7749-R6-save-evidence.zip`, with adjacent
`t_4ecc7749-R6-save-delivery.json` containing its verified CRC/hash/size/member
receipt. Archive holds actual six backup archives, final saved worlds, raw
and semantic observations, manifests/commands/logs, refreshed seven artifacts,
actual consumed R5 main artifacts, POM/module metadata, fresh XML, unchanged
source hashes and failed probes. It excludes distribution caches, account
credentials and generated administration secrets. This document does not
assert that the full ZIP is a native board attachment.

### Integration limits

No direct1.20.1→26.1.2, Fabric↔Forge world conversion, arbitrary third-party
save compatibility or downgrade is certified. Forge-family staging follows
the specified maintained nodes (Forge47.1.0→NeoForge21.1.9→NeoForge26.1.2.122),
not a generic loader-conversion promise. No version/dependency pin, primary
branch, release/public publication, service or profile changed; protected
`t_16b4e35f` was neither copied nor modified. Canonical1.20.1 source state
remains intact. Build/report hotspots remain unchanged.

Existing integration child t_fa26ffb6 must merge this cumulative local commit
and the other R6 branches, retain these exact artifact/source mappings and
replay affected checks if combined artifact bytes/behavior change. The
1.21.1 Tweakium fix invalidates old R5/sibling native receipts for those three
artifacts; this child supplies refreshed builds/publications/native/save
proof, not a blanket118-JAR reproducibility or final integrated-matrix pass.
Network target artifacts are unchanged here. R7 client/integration and final
campaign requirements remain downstream; no new duplicate follow-up card.
