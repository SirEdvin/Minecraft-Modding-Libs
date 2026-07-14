## 1. Testiarium Core Minecraft GameTest Engine

- [ ] 1.1 Add generic fail-fast GameTest sequences and typed block, block-entity, container, entity, item, and recipe assertion helpers adapted from CC:Tweaked where applicable, with MPL-2.0 provenance.
- [ ] 1.2 Add consumer SNBT fixture discovery plus testmod import/export commands and compact-air structure handling.
- [ ] 1.3 Add server lifecycle hooks for deterministic world setup, stale-test cleanup, assertion failure conversion, and report completion, exposed through Testiarium core APIs.

## 2. Client GameTest Parity

- [ ] 2.1 Add the opt-in client runner, deterministic client-world setup, client-thread sequencing, and result-driven exit on Forge and Fabric.
- [ ] 2.2 Add render-idle waiting, player/menu assertions, screenshot capture, and client test resource cleanup.
- [ ] 2.3 Isolate required client mixins and loader hooks from dedicated-server startup.

## 3. `site.siredvin.testiarium.cct` Package Parity

- [ ] 3.1 Create the optional `site.siredvin.testiarium.cct` package and isolate its CC:Tweaked dependencies from Testiarium core.
- [ ] 3.2 Adapt the MPL-2.0 managed-computer factory, completion-reporting Lua API, and CC fixture-import components into the CCT package, with provenance notices.
- [ ] 3.3 Expose `thenStartComputer`, `thenOnComputer`, computer-completion operations, and labeled Lua test-file discovery, execution, assertion, and error reporting from the CCT package.
- [ ] 3.4 Add CCT package helpers needed for computer, disk, disk drive, inventory, modem, monitor, pocket computer, printer, printout, relay, speaker, turtle, recipe, CraftOS, and loot GameTests.
- [ ] 3.5 Add CCT package testmod commands and Forge/Fabric run configuration that initialize CCT services, import fixtures, wait for computer work, and run optional client cases.

## 4. Tweakium Lua Peripheral GameTest

- [ ] 4.1 Add Tweakium core testmod sources that register Creative Filler GameTests and include a named SNBT fixture with a labeled computer, networked Creative Filler, and target inventory.
- [ ] 4.2 Add the Lua test file that discovers Creative Filler, verifies item filling, and verifies invalid mode, missing target, incompatible target, and unknown item failures.
- [ ] 4.3 Add Kotlin GameTest assertions that start the fixture computer through Testiarium and verify the target inventory after Lua completion.
- [ ] 4.4 Configure Forge and Fabric Tweakium testmods, CC:Tweaked and Testiarium test dependencies, GameTest launches, namespaces, structures, assertions, and JUnit XML report paths.
- [ ] 4.5 Add Forge and Fabric testmod entrypoints that register the shared tests through Testiarium's loader adapters.

## 5. Validation

- [ ] 5.1 Add Testiarium fixtures that exercise every parity category: server assertions, structures, client rendering, managed computers, Lua, and all CC component helper families.
- [ ] 5.2 Run the Forge server and client GameTest configurations and verify JUnit XML reports and screenshots.
- [ ] 5.3 Run the Fabric server and client GameTest configurations and verify JUnit XML reports and screenshots.
- [ ] 5.4 Run the Tweakium Forge and Fabric peripheral GameTest configurations and verify their JUnit XML reports.
- [ ] 5.5 Run the affected normal build or test tasks and confirm published Testiarium and Tweakium dependency metadata excludes test-only CC:Tweaked dependencies.
