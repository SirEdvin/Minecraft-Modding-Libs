# Minecraft 26.1.2 cumulative local delivery

Closure owner: t_b9190fc7, 2026-10-19 (Europe/Kyiv). This delivery supersedes the incomplete F documentation checkpoint; archived t_16b4e35f/t_470a1973 stay archived. Local-only: no hosted CI, push, primary merge, release or public Maven publication.

## Source and companion identities

Cumulative branch: `minecraft-modding-libs/t_16b4e35f-implement-and-verify-additional-minecraf`. Tested R7 `281cee3ed4958bd845e6911321f9a24eaee9eae8`, original-WIP `aef78f0`, merge `60049c0`, checkpoint `ad311e9628bff02c0d0bab118f64891fd56e9e13` and stable-wiki parent `c39df5c03c78d45080cf7b54b7e2929e9fa5c5e1` are ancestors. Exact final source and generated-wiki commit SHAs are in the delivery receipt, not predicted inside their own committed contents. Generated-wiki-only commits refer to the preceding stable source snapshot; this is intentional, not stale runtime evidence.

Companion `minecraft-modding-skills`: isolated `.worktrees/t_16b4e35f`, branch `minecraft-26-1-final-guidance`, commit `b0093e25458b90868e9ddffe2bc23b87630b270b` over `734962d`; four documentation files. Actual tree and example source references are checked, including `python3 scripts/verification/test-recipe-viewer-policy.py`. No installed/profile skill changes. TemplateProject `f0c84163b06a86d83a8481e15b8968416888110d` and modding-buildenv `55b844ff7a6ef2c08244378e4e72b44bac3b4b81` remain unchanged.

## Acceptance and commands

[Final ledger](minecraft-26-1-final-coverage.json) retains all 204 immutable requirement identities/text/owners: 197 inherited/local evidence dispositions and seven final-documentation dispositions, not 204 runnable test passes. [Verification](../minecraft-26-1-verification.md) indexes actual full builds, unit/native/client/datagen/production/save/network/consumer/publication receipts. [F](minecraft-26-1-F.md) preserves custody and prior audits. [Parent lifecycle](t_c54853f2-openwiki.md) records the approved managed setup and stable finish. Four controllers remain canonical 1.20.1.

Run from this preserved worktree, without rerunning Minecraft for documentation:

    python3 build/verification/check-final-closure.py
    python3 scripts/verification/test-recipe-viewer-policy.py
    git diff --check
    git merge-base --is-ancestor 281cee3ed4958bd845e6911321f9a24eaee9eae8 HEAD

The final closure script, exact command receipts, 1,128 tested source/build/resource hashes, 118 actual exported JAR hashes, per-owner evidence bindings, final ledger and vault report snapshot are retained in `build/verification/t_b9190fc7-final-local-delivery.zip`. Its manifest covers every payload; outer hash/member count and exact commit IDs are recorded separately in `build/verification/t_b9190fc7-final-delivery.json`. The delivery receipt is attached to this card; the archive is preserved as a final deliverable. JARs are under `artifacts/<original-project-relative-path>` inside the archive and under `build/verification/artifacts/` locally. Reconstruct the audit tree by placing archive `audit/*` under `build/verification/`, `artifacts/*` under `build/verification/artifacts/`, and using the receipt's cumulative Git checkout. Do not extract over user work without comparison.

## Evidence custody boundary

The original incomplete checkpoint ZIP remains immutable at SHA-256 `cb1de06d06ed3b775ea3093d48509c9b312f97b8857eaca6a2557420df5a0c17`; all 165 payloads and CRC are freshly checked. It is not the final delivery. Parent closure archive remains `64eed28db88c7a93fa50a74aea58c39d4d098784aafd3b6aeecc5e055f51ca52`.

Prior runtime acceptance is inherited from independently audited exact-candidate R0–R7 receipts. Historical sibling worktree ZIP locations have since been removed; fresh execution of `verify-inherited-r7.py` is unavailable and is NOT claimed passed. Their SHA-256/member inventories and accepted audits are retained as provenance in the delivery receipt/archive; missing original raw archives are not implied embedded, recoverable, or freshly revalidated. Current 1,128 source hashes and 118 exported JAR bytes are independently verified. No fabricated XML, screenshots or API results substitute for historical evidence. This is the accepted receipt-reuse boundary, not new runtime certification.

## Report and limits

Sole completed Obsidian report: `/home/siredvin/vault/Knowledge/LLM-Wiki/minecraft-modding-libs-progress/minecraft-26-1-implementation.md`; link `[[Knowledge/LLM-Wiki/minecraft-modding-libs-progress/minecraft-26-1-implementation]]`. Overview/index/log are updated in place. Chrono t_878aa68c owns existing personal reading tasks; this closure only notifies readiness, preserving checkboxes/statuses and archival history. Local save is not cross-device sync.

Retain unrepaired NeoForge 1.21.1 shutdown143/unchanged successful retry, two source-JAR ZIP-order limits, existing npm advisory/strict-null limitation (triage dropped, not fixed), legacy Filler appearance and unsupported cross-version wire/ABI/downgrade/authentication/arbitrary-third-party guarantees. Managed AGENTS still contains the standard scheduled-refresh assertion, but no scheduled workflow exists or is authorized; this discrepancy is documented, not silently repaired.
