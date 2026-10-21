---
type: architecture
title: Stonecutter source ownership and publications
description: Exact maintained graph, target convention, runtime and publication isolation.
tags: [build, stonecutter, publication]
sources:
  - id: openwiki-source-2a9daaac1604f238ef4c63fb
    resource: repo://build.gradle.kts
  - id: openwiki-source-147b52b8cb45927ff6a31892
    resource: repo://docs/verification/minecraft-26-1-R0.md
  - id: openwiki-source-d6a84e4b4f1771cf32182cf7
    resource: repo://docs/verification/minecraft-26-1-R5.md
  - id: openwiki-source-851986a2438c3c75fd1c9bb7
    resource: repo://docs/verification/minecraft-26-1-R7-final-integration.md
  - id: openwiki-source-d7497543e084877c0f613811
    resource: repo://gradle/minecraft-26-1.gradle.kts
  - id: openwiki-source-58b7ee33c55d27584a4162db
    resource: repo://scripts/check-publications.py
  - id: openwiki-source-e620d7484b72a53c7fa812cd
    resource: repo://settings.gradle.kts
generated: { by: "hermes", at: "2026-10-21T16:30:44.003Z" }
verified:
  - by: openwiki/0.7.1
    at: 2026-10-21T16:30:44.003Z
---

# Stonecutter source ownership and publications

[Architecture overview](../architecture/overview.md) · [Version/loader topology](../architecture/loaders.md)

## Effective graph

Four libraries have common/Fabric/forge nodes at1.20.1,1.21.1 and26.1.2; TypeScript is independent. Forge means legacy Forge on1.20.1, NeoForge on newer nodes. Canonical/VCS state stays1.20.1. Tasks use `:<library>:<version>:<task>` or `:<library>:<loader>:<version>:<task>`. Full-file authored version overrides suppress matching shared code/resources/sources, including active nodes. Explicit generated-resource roots prevent inactive-node language loss or legacy model leakage.

## Target-only tooling

`gradle/minecraft-26-1.gradle.kts` owns12 target leaves. Common/Fabric use Loom1.17.11 non-remapping, official access rules, no mappings dependency and ordinary jar/configurations. Target NeoForge uses MDG2.0.142 without old Parchment. Old remapping paths remain. Java25 launcher/KGP2.4.0 preserve old JVM17/21, language/API2.0 and runtime pins; target JVM25. [R0](../../docs/verification/minecraft-26-1-R0.md) verifies loaded plugins and72 resolved graphs, not catalog intent.

## Artifacts and evidence

Public group stays site.siredvin; read current library versions from gradle.properties rather than treating the historical R5 version2.0.0 as a permanent pin. Historical forge suffix stays, Testiarium common is core. Separate main/testMod/cctTestMod capabilities and sources remain. Check exact compiled fixture identities and all POM/GMM variants; viewers/private fixtures must not leak into production metadata.

[R5](../../docs/verification/minecraft-26-1-R5.md) proves36 publications,118 JARs,9 external Java/Kotlin routes,10 raw outgoing consumers, three active-state full builds and canonical restoration. [Final R7 integration](../../docs/verification/minecraft-26-1-R7-final-integration.md) refreshes affected final artifacts/consumers/publications. Two source-ZIP order limits remain: no blanket final118-byte reproducibility claim.

The current [aggregate release wiring](../../build.gradle.kts#L210-L245) selects every version leaf, using remapJar for old Fabric, reobfJar for Forge1.20.1 and jar otherwise, plus sourcesJar. Configuration is not proof of an executed remote release. R5/R7 evidence above describes local validation at those historical source revisions, not current remote publication state.

Use wrapper --no-daemon, deadlines and fresh reports. Switching/datagen/builds mutate state and are not documentation linters. No public publish/push/release was performed during this documentation update. See [verification](../testing/verification.md), [harness](../testiarium/harness.md) and [quickstart](../quickstart.md).
