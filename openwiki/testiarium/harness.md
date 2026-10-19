---
type: subproject
title: Testiarium registration and reporting harness
description: Target function/instance/environment registry, old adapters and strict fresh-report gates.
tags: [testiarium, gametest, reports]
verified:
  - by: openwiki/0.7.1
    at: 2026-10-19T00:45:42.798Z
sources:
  - id: openwiki-source-d7335994858335a909e6674b
    resource: repo://docs/verification/minecraft-26-1-R1.md
  - id: openwiki-source-f6b3458a12876dad0171a34e
    resource: repo://docs/verification/minecraft-26-1-R6.md
  - id: openwiki-source-8e467af86822c4e36a35b699
    resource: repo://gradle/gametest-reports.gradle.kts
  - id: openwiki-source-d2f388e2bcce994d6d2afe5e
    resource: repo://projects/testiarium/versions/26.1.2/src/main/kotlin/site/siredvin/testiarium/Testiarium.kt
generated: { by: "hermes", at: "2026-10-19T00:45:42.798Z" }
---

# Testiarium registration and reporting harness

## Main API versus fixture features

Main owns explicit registration, annotations/assertions and reporters. testMod owns standalone/client fixtures; cctTestMod adds optional CC tooling and depends on testMod. Common uses testiarium-core feature identities; matching loader capabilities/sources stay separate. A compile dependency does not ensure loaded mods, structures or client Mixins are present.

## Registration across versions

Old1.20.1/1.21.1 keep their native annotation/loader registration paths. Target26.1.2 uses `site.siredvin.testiarium.api.GameTest` and translates explicit classes to function/instance/environment registries. Collect before the static function registry freezes; instances/environments register per world reload. Methods and batch IDs are normalized and collision-checked. Tags retain method/class/default precedence; unknown enabled methods use loader fallback. Static methods or public no-argument constructors are required; reflection failures are not silent skips.

## Failure/report contract

sequence appends terminal success; raw sequences need their own. Native required outer observers prove fail-fast assertion/exception/assertion-error behavior, one action, blocked later callbacks and competing delayed success. Container assertions reject nonempty expectations beyond capacity, allowing empty padding. Registry test cleanup restores native and private state.

Mapped runs delete old XML, require fresh nonempty non-skip evidence, reconcile testcase counts and reject failures/errors/DOCTYPE. Storage permits no skips. Optional negative fixtures must match exact unique identities and failure sentinels; optional failures are skips, not passes. Reporter finish emits XML/HTML idempotently.

[R1](../../docs/verification/minecraft-26-1-R1.md) verifies target registry/CCT/client capability; [R6](../../docs/verification/minecraft-26-1-R6.md) verifies the full28 native routes,434 cases including38 intended skips and zero storage skips. Final affected replays are separate, not extra unique tests. See [client/CCT](client-cct.md), [verification](../testing/verification.md) and [publication](../build/stonecutter-publication.md).
