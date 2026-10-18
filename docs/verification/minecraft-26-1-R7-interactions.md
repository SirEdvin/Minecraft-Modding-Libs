# R7 interactions — t_a4740f06

Status: INCOMPLETE checkpoint2. Preparation cumulative d43491fb943e1458c8984b539d56aa57d28a206d fast-forwarded into the assigned wt/t_a4740f06; working tree initially clean. Real native CCT/client receipts now exist; extended-menu client chain is running, not yet accepted.

## Scope and ownership

Interaction owns target Broccolium menus/tooltips/books/tests, Testiarium client/CCT helpers, ClientFixture.java world/inventory data and new interaction assertions. Rendering owns ClientProof.java; requested hooks via comment141 on t_5eb14799. Integration owns runner/evidence schema/build/catalog. No shared rendering/runner edits here.

## Initial observations

- Current client workflow opens a vanilla empty chest once and a pocket once; it checks screen presence, not menu contents, server mutations, close/reopen, or post-reconnect menu state. Broccolium extended menu codec has separate R6 adversarial tests; a vanilla chest is not that API.
- Peripheralium target registers two descriptive items and one descriptive block item; blend is legacy-only. Creative tab is built from registered items and blocks; current client checks size3 only, not identity.
- Tweakium target registers Creative Filler and its block item; intended resource appearance remains a separate human decision owned by rendering. Production libraries contain no concrete turtle/pocket upgrade definitions: private r7_visual consumers cover these APIs, not production content.
- Book fixture uses two pages but checks page count only; raw/filtered native component behavior has existing TargetTextTests.
- Hidden description explicitly advertises Left Shift, so Right Shift absence is not inferred to be a bug. Always-show short-circuits client input access. No demonstrated production defect yet.

## Next action

Add exact content/creative identity manifest and private interaction assertions, seed persistent chest state and strengthen synchronized book contract without changing the rendering gallery slots. Build both fixtures with Java25 and execute fresh bounded Xvfb target client routes; inspect real screenshots. Run focused text/tooltip regressions and Testiarium client/CCT routes as affected. Record checked/unchecked/blocked explicitly and send phase hook contract to rendering/integration.

## Checkpoint2 evidence

Local checkpoint1ec8a021f485ae13ca3a248cbb6592bde187871c added proof;3c6927233619bb2e7afd7abc324b61862aac627f corrected tooltip probe lifecycle. Rendering-owned exact hook SHA c13c9a94a429848b0f4666808cc27d0f8425f016 inspected/merged at6a78eba07e2f87c818f90b1906171faa1e85d007. No concurrent ClientProof edit. Production code remains unchanged.

Evidence root `build/verification/interactions` (Git-ignored; all screenshots outside tracked source). `prepare.log` records two Java fixture compile errors corrected in `prepare-retry.log`. `focused-tests.log` records intentional deprecated native callback compilation warning in experimental JUnit probe; `focused-tests-retry.log` then proves unregistered Item construction cannot occur after registry freeze. Rather than unfreeze registries, moved probes into registered native testmod fixtures; removed experimental JUnit class. `focused-native-placement.log` passes four TargetTextTests and the existing Fabric menu codec suite plus both fixture compilations. No production bug inferred from fixture lifecycle errors.

`native/commands.json`: routes21/26 each18 tests,4 intentional skips,0 failures/errors; routes23/28 each1 test,0 skips/failures/errors. Each command exit0/accepted=true. Native fixture PNGs visually inspected: actual downward-looking world/beam/grass/slimes, no crash/loading screen. This is harness evidence, not menu/content appearance proof.

`native-tooltips/commands.json` first attempt is honestly rejected: all game assertions pass but two added public tests violate exact shared discovery guard (39 Fabric/16 NeoForge instead of expected37/14). New tooltip assertions now execute as private helpers inside existing registeredTooltipHooksAreLazyAndDoNotLoadClientOnServer; no guard edits/bypass and no claim of extra testcase identities. Fresh replay pending.

Runtime proof now checks exact4 production item IDs/2 blocks, exact Peripheralium tab3 and eight ordered private-consumer creative variants (normal/advanced pocket/turtle, base/upgraded), registered hidden-tooltip prompt, synchronized exact two book pages/title/author. It registers a real Broccolium extended ChestMenu with exact12-byte consumer schema and actual player registry context, verifies vanilla chest seed16diamonds/7emeralds, transfers through native client input, requires independent server-thread ACK, closes/reopens with fresh menu id, then reopens again after dedicated reconnect. Three extra screenshots live under `control/interaction/screenshots`; shared phase/screenshot/model schema unchanged. `check-interactions.py` is executable supplemental acceptance; cannot certify absent hooks or missing ACK.

## Acceptance checklist (pending executable implementation)

- [ ] Exact registry and creative entries, not cardinality alone.
- [ ] Descriptive/hidden tooltip behavior, absent translation, hidden component semantics.
- [ ] Written/writable book raw/filtered page order and native visible page navigation.
- [ ] Menu open, server-origin contents, client mutation acknowledgement, close/reopen persistence.
- [ ] Resource reload and dedicated reconnect preserve relevant content/state.
- [ ] Both target loaders; explicit deadlines, real screenshots and manifests.
- [ ] Testiarium client/CCT fresh report evidence; retain expected negative skips.
- [ ] Local cumulative SHA, exact commands/logs, coverage rows/downstream gates.
