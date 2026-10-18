# R7 interactions — t_a4740f06

Status: INCOMPLETE checkpoint. Preparation cumulative d43491fb943e1458c8984b539d56aa57d28a206d fast-forwarded into the assigned wt/t_a4740f06; working tree initially clean. No new runtime pass yet.

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

## Acceptance checklist (pending executable implementation)

- [ ] Exact registry and creative entries, not cardinality alone.
- [ ] Descriptive/hidden tooltip behavior, absent translation, hidden component semantics.
- [ ] Written/writable book raw/filtered page order and native visible page navigation.
- [ ] Menu open, server-origin contents, client mutation acknowledgement, close/reopen persistence.
- [ ] Resource reload and dedicated reconnect preserve relevant content/state.
- [ ] Both target loaders; explicit deadlines, real screenshots and manifests.
- [ ] Testiarium client/CCT fresh report evidence; retain expected negative skips.
- [ ] Local cumulative SHA, exact commands/logs, coverage rows/downstream gates.
