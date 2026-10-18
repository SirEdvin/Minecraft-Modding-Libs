# R6 network subtask verification

Status: CARD-LOCAL NETWORK ACCEPTANCE VERIFIED (2026-10-18). Task t_72f16ae4; parent R6 t_997f2d4d. This is the network subtask's receipt, not the aggregate R6 or campaign report.

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

The following final runtime receipt supersedes that checkpoint's pending real-client gates.

## Real target-loader acceptance

The final private fixture registers a menu through each loader's actual Broccolium platform and opens it through PlatformToolkit.openMenu/MenuProvider/SavingFunction. Two full graphical Minecraft processes (R6Alice and R6Bob) join a regular dedicated server over real loopback TCP. These are NOT mock players, protocol bots, GameTest clients or invented server messages. Test-only files coordinate phases, but the actions use native packets sent by the real clients and observed after the real server packet handler. Both clients remain connected for simultaneous access to shared inventory. Offline mode is scoped to loopback disposable fixtures; this does not certify online account authentication.

Final candidate receipts:

| Loader / Minecraft | Real clients | Interaction phases / received packets | Server-side valid / invalid menu-data cases | Exit codes (server/Alice/Bob) |
| --- | ---: | ---: | ---: | --- |
| Fabric / 26.1.2 | 2 | 12 / 24 | 1 / 16 | 0 / 0 / 0 |
| NeoForge / 26.1.2 | 2 | 12 / 24 | 1 / 16 | 0 / 0 / 0 |

Exact-source final runs are `fabric-barrier-final` and `forge-barrier-final` under this task's evidence root. Both drivers exit 0. Every interaction uses a two-client readiness barrier and one shared future send timestamp; each actual client asserts it sends within 250ms of that timestamp, rather than merely acting whenever its world finishes loading. Both barrier and real server receive ticks are retained. Each driver requires the complete ordered phase inventory, actual server receipt count, server thread, native codec checks, real-client registry-aware decoding on Render thread and all three natural zero process exits. Callback schema is exactly BlockPos(0,65,0) followed by Int(27), twelve bytes. Server-side native menu-factory tests reject all twelve incomplete prefixes, count -1, count 0x7f7f7f7f, an invalid position and 32,601-byte input before container allocation/accepted-decode mutation. Nonempty temporary buffers are released on success/failure; NeoForge keeps caller ownership until the caller's finally, matching its native ClientPayloadHandler. Netty's zero-capacity EMPTY_BUFFER is an unreleasable singleton, not an allocation leak.

Native NeoForge AdvancedOpenScreenPayload uses an unbounded byte-array codec; its server API documents a 32,600-byte extra-data limit, and its client handler releases its temporary buffer in finally. This subtask does not falsely claim to replace that loader-owned transport codec. The owning example consumer enforces its exact twelve-byte schema before allocation, while Broccolium's Fabric outer framing additionally enforces MAX_BYTES. MenuBuilder remains a public extension callback whose consumer must validate its own schema. Opening data is server-to-client; rejection of client actions is separately exercised through native serverbound handling.

### Exact actions and state evidence

Each action is sent by both real clients; exact native packet record strings, session UUIDs, numeric menu IDs, receive ticks and threads are retained in client/server event logs.

1. Concurrent QUICK_MOVE of slot 0 from a shared 32-diamond chest: aggregate destination is exactly 32, source zero, no duplication.
2. Numeric menu ID = current ID + 100: reject with exact source/inventory/carried-slot/component equality.
3. Slot 32767: reject unchanged.
4. Slot -2: reject unchanged.
5. PICKUP button 127: reject unchanged.
6. Changed-slot 0 and carried HashedStack claim Integer.MAX_VALUE diamonds on an otherwise valid PICKUP: server-authoritative result conserves exactly 32, never the forged count. The valid click may execute; it is not misreported as whole-action rejection.
7. Closed menu ID reused after opening a new menu: reject unchanged.
8. Open menu followed by server teleport to (100.5,66,100.5): reject unchanged.
9. Native removal of the backing block entity after opening: reject unchanged.
10. Spectator permission after opening: reject unchanged.
11. UseItemOn at chest BlockPos(0,65,0), hit (0.5,65.5,0.5), UP/MAIN_HAND, from the distant location: reject unchanged and leave both players in inventoryMenu.
12. Equal numeric IDs with distinct per-player menu objects: Alice's private 32-diamond and Bob's private 17-emerald chest transfer only to their own sessions, never to the other player's inventory.

Every rejected phase compares detached before/after copies of ALL owning chest slots, player inventory slots and carried stacks using native ItemStack.matches (including components/counts), not just aggregate totals. The receipts retain beforeSlots/afterSlots and exactSlotsUnchanged. Every observed handler completion asserts the actual server thread; actual menu-opening callback logs show Render thread on both graphical clients. Generic third-party protection hooks and arbitrary external menu implementations are not certified by this representative owning consumer.

## Reproduction

Use the approved Java25 launcher, retained target pins and R5 production assemblies (or their preserved distribution manifest). All commands use the checked-in wrapper and --no-daemon. Start with fresh evidence/output paths; the runner refuses existing output directories and insufficient scratch disk. The explicit --eula-accepted flag refers to the recorded project acceptance above, not implicit consent.

    ./gradlew -I scripts/verification/network-fixture.init.gradle -I scripts/verification/network-launch-info.init.gradle networkFixtureJars networkClientLaunchInfo networkForgeLaunchInfo -Pnetwork.launch-dir="$LOG_DIR/final-launch" --no-daemon --console=plain
    timeout --foreground 240s xvfb-run --auto-servernum python3 scripts/verification/run-network.py --server-source "$R5_FABRIC_ASSEMBLY" --client-info "$LOG_DIR/final-launch/fabric-client-info.json" --output "$LOG_DIR/fabric-exact-final" --fixture "$PWD/build/network-fixture/broccolium-network-fabric.jar" --java "$JAVA_HOME/bin/java" --eula-accepted
    timeout --foreground 240s xvfb-run --auto-servernum python3 scripts/verification/run-network.py --loader forge --server-source "$R5_NEOFORGE_ASSEMBLY" --client-info "$LOG_DIR/final-launch/forge-client-info.json" --output "$LOG_DIR/forge-exact-final" --fixture "$PWD/build/network-fixture/broccolium-network-forge.jar" --java "$JAVA_HOME/bin/java" --eula-accepted

Only library/dependency JARs are reused read-only; no source assembly configuration, original world or live server is modified. The runtime classpath excludes producer class/resource directories and private fixtures other than the explicitly built network fixture. R5 library JAR hashes are rechecked against its archive manifest. The private source tree is not attached to any production source set or publication; opt-in init scripts compile its JARs separately.

## Verification, retained failures and downstream limits

- Server-free unit evidence remains 720 invocations / 31 fresh XML, zero failures/errors/skips; its exact test source is unchanged after those executions.
- Scoped source copies pass the same pinned site.siredvin.linting 0.9.0 Java/Kotlin rules; no formatter rule or warning policy is disabled. The initial complete standalone Kotlin ratchet check also passed. Python helper compiles and the committed launch/fixture preparation tasks execute successfully.
- No production source, build convention, registry ID, packet format or release version changes. There was no confirmed production defect in the tested scope, so no fabricated behavioral red/green fix is claimed. R5 production artifacts remain byte-identical; new fixtures are excluded from those JARs. Canonical 1.20.1 stays active.
- Failed setup runs are retained: shared disk exhaustion (only this task's disposable lint/Loom backup caches reclaimed), a source assembly whose installer dependencies had been pruned, and the initial fixture's incorrect refcount assertion for Netty EMPTY_BUFFER. No failed/terminated run contributes to final acceptance. No approval/runtime/profile policy was changed.
- Client logs retain inherited offline profile-key/audio/model warnings. Visual correctness and optional integration variants remain R7; ordinary production lifecycle, assembled native matrix and copied-save compatibility remain the sibling R6 owners. This is not cross-version wire/ABI, cross-loader save, online-authentication or arbitrary downstream-mod certification.
- No hotspot edits: gradle/minecraft-26-1.gradle.kts and gradle/gametest-reports.gradle.kts are unchanged. No push, primary merge, release, public publication, shared-service restart or protected final-owner-tree edit.

Durable delivery is this task's build/verification/t_72f16ae4-R6-network-evidence.zip. Its exact checksum, source manifest, commands, final raw client/server logs, rejection/state receipts, retained failures, fresh XML, fixture JARs and R5 production identities are indexed by final-receipt.json and the Kanban handoff. Integration owner t_fa26ffb6 must retain this receipt against the cumulative production source/artifact identities and rerun affected routes if those change.
