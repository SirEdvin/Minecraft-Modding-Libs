# Stonecutter migration audit

Reviewed 2026-10-16. Baselines are local historical branch tips `1.20` (`54a9c1dfa2e1db4ebaeb6f53dfce3f57cb3d005e`) and `1.21` (`da613f40a35bd2eb5a490aff9bc5860fe776a3bf`), compared with migration-contract commit `47a80ca9349e009cf2e21b517b22b5ee99b739b2` and the fixes described below.

## Scope and conclusion

Mapped all tracked source/resource paths from the historical core/Fabric/Forge modules into each Stonecutter branch, applying version-local full-file override precedence. This is a migration-fidelity review, not a claim that all existing gameplay or third-party integrations are bug-free.

Before this audit's changes:

| Target | Identical files | Changed | Added | Removed |
| --- | ---: | ---: | ---: | ---: |
| 1.20.1 | 417 | 9 | 0 | 1 |
| 1.21.1 | 433 | 10 | 2 | 0 |

On 1.20.1, production `main` sources/resources are unchanged except for a final newline in the access widener: 327 byte-identical files and that one formatting-only change. Remaining changes affect tests; the removed file is the obsolete Fabric test bootstrap, not gameplay. No production files were removed on either version.

On 1.21.1, 337 production `main` files are identical and eight differ. The migration is not behavior-neutral: the following differences matter to consumers.

## Significant pre-existing migration differences

1. **Unsided NeoForge storage lookup.** `projects/broccolium/forge/versions/1.21.1/src/main/kotlin/site/siredvin/broccolium/modules/storage/ForgeStorageUtils.kt` now passes a nullable direction through for block item/fluid/energy and entity fluid/energy capabilities, instead of replacing `null` with `Direction.UP`. A provider can expose different content or access rules for unsided versus top-side access. Callers that require the old top-side behavior should pass `UP` explicitly. Retained the migration's native nullable-context behavior.
2. **Fake-player equipment attributes.** The four 1.21.1 `FakePlayerProvider*` implementations use `ItemStack.forEachModifier(EquipmentSlot.MAINHAND)` instead of adding every entry in the attribute-modifier component. This respects main-hand slot applicability and the native stack iteration path. Combat/tool attribute results can differ for slot-specific modifiers; the old implementation could incorrectly apply armor/offhand modifiers. Retained the scoped behavior.
3. **Enchantment representation API.** `LuaRepresentation.forEnchantment` now accepts `Holder<Enchantment>`; its `name` becomes the registered identifier, while `displayName` uses the full level-qualified name. `forEnchantments` likewise expects holder keys. This changes Lua output and the public Kotlin/Java contract (including the single-enchantment JVM signature); downstream callers compiled against the old API must be updated/recompiled. Retained the registry-aware API, but it should be called out in release notes.
4. **Component-patch persistence and encoding errors.** `CompoundTagDataStorage.patch` now notifies persistence after a successful write/clear, and `DataComponentUtil.patchToNBT` throws on encoding failure rather than silently returning no encoded value. Existing tests verify successful notification and that failed encoding preserves the stored tag without notification. This is deliberate correctness behavior, but callers can now observe an encoding exception.

The 1.21.1 Testiarium CCT mixin configuration is also newly connected. This affects the optional test harness rather than shipped gameplay. No recipe, texture, registration, or Peripheralium production-source divergence was found in the path-normalized comparison.

## Confirmed bugs fixed by this audit

### High: upgrade writes lost component-removal entries

Both pocket and turtle custom-data setters copied only present patch values. A data-component patch also uses empty optionals to mean an explicit removal; discarding those entries could restore a removed default component when the upgrade is reconstructed. The bug already existed on historical `1.21`.

Fixed both setters in `projects/tweakium/versions/1.21.1/src/main/kotlin/site/siredvin/tweakium/modules/peripheral/util/DataStorageUtil.kt` to copy removal entries with `builder.remove`. Added a parameterized test exercising the real adapters through narrow CC API proxies, including repeated writes, key removal, and mutation. Both cases failed before the fix and passed afterward. This is adapter regression coverage, not a live computer save/reload test.

### High: NeoForge entity inventory lookup threw before querying

`null as Void` is a non-null Kotlin cast and throws before the entity item capability lookup. This also existed on historical `1.21`. Pass plain nullable `null` instead. A loader-run GameTest verifies a chest minecart's native item capability and its diamond contents; it failed before the fix and passed afterward.

### Medium: misleading or broken peripheral GameTest wiring

The new entity regression initially revealed incomplete test execution rather than just the gameplay failure: NeoForge lacked Broccolium in the loaded-mod set; the fixture was missing; and the 1.20.1 Forge testmod's compile classpath lacked Testiarium's common main output. Fixed these test-only inputs and retained test-only isolation.

Added `gradle/gametest-reports.gradle.kts`: remove the exact previous XML before launch, require a new report with at least one non-skipped case, reject failure/error elements, and check declared counts. Some startup failures exit zero, so a successful Gradle process alone is insufficient. CI now runs Tweakium peripheral tests alongside the existing Testiarium matrix.

## Stonecutter and companion toolkit

- Upgraded Minecraft-Modding-Libs and TemplateProject from **0.9.7 to stable 0.9.8**. The stable release adds IDE parameter annotations and updated KDoc links, not gameplay transformations or fixes for consumer-specific build contracts.
- Inspected **0.10-beta.1**, the latest prerelease in the checked snapshot metadata. Did not adopt it: it requires a Java 25 Gradle launcher and Gradle 9.7+, and changes preprocessing, property accessors, flags, task APIs and model schemas. Alpha.8 restored structured-property exposure removed earlier in the prerelease series; the removal is not current beta behavior.
- TemplateProject's legacy Forge artifact-generation task now depends on `stonecutterGenerate`, matching its existing NeoForge ordering.
- modding-buildenv candidate **0.9.3** tracks the complete metadata expansion map and shared build script for Fabric/Forge/NeoForge resource processing. Built and published it only to a scratch file Maven repository, then consumed it through an uncommitted init script. Changing the raw Broccolium dependency property regenerated all four Tweakium loader/version metadata outputs without `clean` or `--rerun-tasks`; their contents were inspected and the normal values restored.
- A Gradle 9 composite build of the toolkit fails in legacy ForgeGradle accessor generation. Its own Gradle 8 wrapper plus a file-Maven consumer works; this audit does not upgrade that wrapper or publish the candidate remotely. Production consumers retain published toolkit pins until a separate release.
- Updated the Stonecutter skill and its release/migration-contract reference in `minecraft-modding-skills`, plus local Testiarium guidance on fresh-report validation and source-set/loaded-mod wiring.

Upstream evidence: Plugin Portal plus release/snapshot Maven metadata; `stonecutter/docs` commit `2f685fdf78a83c5453e007177b5e4dc4f14b29e6` and stable source commit `73632f3266a1e81683dedb4a52168ec53c583442`, compared with published source JARs. Stable metadata reports 0.9.8 (2026-08-31); snapshot metadata reports 0.10-beta.1 (2026-10-14).

## Verification and limits

- Root build, fresh JUnit execution, publication generation and verification with 1.20.1 active: **1,064 cases in versioned Stonecutter reports, zero failures/errors/skips; 24 publications verified**. The originally reported 1,598 total mistakenly included 534 cases from 26 old unversioned report files. The coverage follow-up identified this aggregation error by reconciling the saved report inventory against a fresh standalone checkout; it was not a loss of tests.
- Server matrix: Testiarium and Tweakium on Forge/Fabric 1.20.1 and NeoForge/Fabric 1.21.1: **eight reports, 21 cases, zero failures/errors, eight expected optional skips**. Skips are not counted as passes.
- TemplateProject: full build with its canonical `1.21.1-fabric` node active, switch to `1.20.1-forge`, another full build, and reset.
- modding-buildenv: own-wrapper `check`, local publication, and incremental consumer metadata verification on all four nodes. Fabric/NeoForge exercise the candidate conventions; the consumer's 1.20.1 Forge node uses its existing ModDevGradle resource wiring. The toolkit's legacy ForgeGradle convention was compiled by `check`, not separately runtime-tested in a ForgeGradle consumer.
- Repeated the root build, fresh JUnit run and publication verification with **1.21.1 active**, with zero failures/errors/skips and 24 verified publications. Its originally reported 1,598 aggregate used the same overly broad report scan and is not a valid fresh-case count. Use the coverage follow-up's explicitly versioned, fresh-report counts for current acceptance. Restored all four controllers to 1.20.1; no switch-only source/controller changes remain. TemplateProject is likewise reset without source churn.
- The skill repository validator passed using an isolated `uv run --with pyyaml --with jsonschema` environment; `git diff --check` passed in all four repositories.

All Gradle/Minecraft runs use `--no-daemon`, with stdout/stderr redirected to scratch logs and explicit deadlines for runtime tests. The audit did not perform interactive client/rendering checks, world save/reload coverage, exhaustive fake-player combat scenarios, live upgrade reinstallation, a complete CCT/client runtime matrix, or downstream binary-compatibility tests. These limits should not be inferred from compilation or generic server smoke results.
