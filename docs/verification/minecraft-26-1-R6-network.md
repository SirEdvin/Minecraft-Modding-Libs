# R6 network subtask verification

Status: INCOMPLETE. Task t_72f16ae4; parent R6 t_997f2d4d. This is the network subtask's checkpoint, not the aggregate R6 report.

## Baseline and recovered acceptance

Merged planning b8fd1ddfd5134c45402040fe2c1ca5be343ac48f then exact completed R5 cumulative 4a07553673bcd51576e0857301dda867bc176a2c; both ancestry checks exit 0. Assigned worktree initially clean. No reads/copies/edits of t_16b4e35f.

Read original brief, source ledger, approved remaining plan, original implementation plan, coverage matrix and OpenWiki index. The 204-row map retains network requirements in PLAN:L114, PLAN:L115, DECISION:09 and R6:4. Full requirements are recoverable despite the truncated supplied-card warning:

- Bound consumed bytes/counts and use registry-aware menu-opening codecs; preserve public MenuBuilder/SavingFunction signatures and vanilla block-entity update packets.
- Define buffer ownership and appropriate-thread client decode; reject malformed/truncated/oversized data without buffer leaks or server crashes.
- Authorize client-to-server actions using server-side player/menu association, permissions, distance, positions/counts and current/nonremoved state. Rejection must not mutate state.
- Use two actual clients simultaneously on each target loader, with real identities, messages, rejection/conservation receipts and client/server logs. Mock players, codec unit tests and fabricated messages do not satisfy that gate.
- No invented custom protocol or cross-version wire compatibility promise. Preserve approved observer scope and all old nodes.

No additional missing product criterion was found in these authoritative documents. Concrete limits/permissions are menu-owner-specific, not a newly prescribed generic library protocol.

## Source trace

MutableNBTBlockEntity target publishes ClientboundBlockEntityDataPacket and root update tags, not a serverbound action receiver. Shared MenuBuilder/SavingFunction are functional extension interfaces; no concrete production action/menu implementation was found by repository-wide symbol searches. Target Fabric registration delegates ExtendedMenuType's factory through FabricMenuOpeningData; openMenu produces detached bounded bytes. Target NeoForge delegates IMenuTypeExtension and ServerPlayer.openMenu. Therefore generic opening data cannot establish authorization for arbitrary downstream actions: native server listeners and each concrete menu owner remain trust boundaries.

Pinned primary Fabric ExtendedMenuType source inspected at revision 1abe2820db1f3304fe06785fee88497bf593aeb2, fabric-menu-api-v1/src/main/java/net/fabricmc/fabric/api/menu/v1/ExtendedMenuType.java. It identifies extra data as server-to-client and factory use as primarily client-side (also callable on server). It does not define a new serverbound protocol.

## Runtime prerequisite

Initial parent R6 history recorded missing applicable Minecraft EULA acceptance. A fresh read of sibling t_b52730bf subsequently recovered dashboard confirmation: "I confirm, set EULA true for this project too" (2026-10-18). This resolves the project prerequisite; do not re-request it. Only task-local disposable server configurations may use this acceptance. No existing server or original save is modified.

## Checkpoint and next action

Codec phase complete: 18 owning target Fabric codec/lifetime cases pass. Eight exact malformed frames, all 258 incomplete prefixes of a valid 256-byte frame, maximum payload, overflow-before-write, detachment, caller ownership and temporary-buffer release are exercised. Complete Fabric units: 232 cases/9 XML on 1.20.1, 233/10 on 1.21.1 and 255/12 on 26.1.2; total 720/31 fresh XML, zero failures/errors/skips. Exact inputs and per-report identities are in task scratch receipt.json. Unchanged Spotless Kotlin gate passes in an exact-source standalone snapshot (linked-worktree Git ratchet workaround). A first formatting failure is retained and corrected; no production defect/red-green fix is claimed. Only test source changed, so R5 production binaries are not invalidated. R5 ZIP SHA256/CRC independently rechecked; 708 current production/build source hashes retained.

Next: owning test-only actual-client fixture, regular dedicated server and two real graphical client processes per target loader, native action/rejection/state/thread receipts. Real two-client, native authorization and threading acceptance remains unverified, not waived by the server-free tests.
