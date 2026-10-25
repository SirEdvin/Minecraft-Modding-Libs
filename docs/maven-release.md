# Maven release procedure

Only JVM Maven publications are handled here. `publishAllPublicationsToSirEdvinRepository`
does not invoke npm, mod distribution or GitHub release tasks. Release candidates belong
in `https://mvn.siredvin.site/minecraft`; only literal `-SNAPSHOT` versions use snapshots.
The common Testiarium artifact retains `core`, and Forge-family modules retain `forge`.

## Before publishing

1. Enumerate the real model with `-I scripts/maven-release.init.gradle`. The JSON model is
   `build/verification/maven-model.json`; it includes repository URLs but no credentials.
2. Stage all required nodes with `publishAllPublicationsToReleaseStageRepository` using
   that init script. This writes only `build/maven-stage`, not the public repository.
3. Run the existing build/check, report and publication guards, inspect dependency
   versions and capabilities, and check every expected public file for collisions.
4. Review and merge publication changes into `multiversion`; retain exact-head CI and
   the immutable merge source SHA. Never publish from an unreviewed or dirty checkout.

## Approved tag route

The default branch need not expose dispatch. Tags match
`<library>-<1.20|1.21|26.1.2>-<configured-library-version>`; each publishes common,
Fabric and Forge-family modules for that library/node. Never move an existing tag.

**Create and push only one publication tag at a time. Wait for its run and artifact
readback to finish before creating the next tag.** The global concurrency group prevents
simultaneous uploads, but GitHub's pending-run replacement is not a release queue.
Stop the sequence on a failed or cancelled run and inspect its ledger first.

Each run installs Java 17/21 toolchains and a Java 25 launcher, runs scoped checks,
stages the actual Gradle publications, then probes every POM/GMM/JAR/source/feature file.
It publishes only completely absent coordinates via the repository-specific task.
Completely identical existing coordinates are verified without uploading. Mixed,
differing or uncertain remote states fail closed and require a decision; do not rerun
blindly. No workflow or script bypasses repository policies or changes credentials.

The workflow preserves `build/verification` and staged bytes as an Actions artifact.
The final manifest includes the exact source SHA, run URL, file URLs, SHA-256 and remote
verification status. Readback is attempted after an upload failure too, preserving the
partial remote state while the original job remains failed. A green workflow is not
release acceptance: download the receipt and check public bytes plus fresh-cache
external consumers, including Testiarium `testMod`/`cctTestMod` capabilities.

`python3 scripts/test-verification.py` exercises fail-closed HTTP/digest classification
and partial-ledger/readback logic using explicitly synthetic fixtures, not Minecraft.
