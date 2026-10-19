---
type: integration
title: Broccolium loader integration
description: Maintained loader pairs, native lookup and transactional provider boundaries.
tags: [broccolium, loaders, storage]
verified:
  - by: openwiki/0.7.1
    at: 2026-10-19T13:32:11.388Z
sources:
  - id: openwiki-source-d0f34221b5d3b91db6e90b7f
    resource: repo://docs/minecraft-26-1-verification.md
  - id: openwiki-source-8e194b0e3daa57910d9a7fd0
    resource: repo://docs/verification/minecraft-26-1-R2.md
  - id: openwiki-source-81d5f1627e19148569f46f81
    resource: repo://gradle/libs.versions.toml
  - id: openwiki-source-d7497543e084877c0f613811
    resource: repo://gradle/minecraft-26-1.gradle.kts
  - id: openwiki-source-3a5094630bd2098f098b5677
    resource: repo://projects/broccolium/forge/versions/26.1.2/src/main/kotlin/site/siredvin/broccolium/modules/storage/ForgeStorageUtils.kt
  - id: openwiki-source-e620d7484b72a53c7fa812cd
    resource: repo://settings.gradle.kts
generated: { by: "hermes", at: "2026-10-19T00:45:42.798Z" }
---

# Broccolium loader integration

## Maintained source boundaries

The four library trees retain Fabric/Forge1.20.1 and Fabric/NeoForge1.21.1, adding exact26.1.2 Fabric/NeoForge. The `forge` branch name remains historical. Inspect the matching `versions/<version>/src` override before changing an adapter; common callers use platform/storage abstractions, not loader imports.

Fabric bootstrap configures common toolkit/lookups and optional Team Reborn integration. Forge-family bootstrap installs native capability lookups. Nullable sides are real lookup contexts, not an invented UP direction. Target adapters reject removed block entities. Native origin slots are preserved for item-contained resources; target NeoForge uses transactional ResourceHandler/ItemAccess/EnergyHandler rather than mechanically wrapping old handlers.

## Positive integration evidence

[R2 native acceptance](../../../docs/verification/minecraft-26-1-R2.md) covers actual positive item/fluid/energy providers, nested rollback, component-bearing variants, origin bucket replacement, nullable sides and invalid/removed providers. Energy dependencies remain3.0.0 on1.20.1,4.1.0 on1.21.1 and5.0.0 on26.1.2. The earlier absent-provider smoke is not the whole proof.

Official target wideners/transformers and native removal descriptors are checked against packaged/transformed classes. Universal artifacts contain gated client APIs; dedicated-server readiness is separately demonstrated in [final verification](../../../docs/minecraft-26-1-verification.md), not inferred from compilation.

## Limits and navigation

Exact replacement requires stable/restorable backing storage; setter notifications and arbitrary cross-adapter peers are not automatically transactional. Do not infer certification of every optional provider or protection mod. See [storage contracts](../modules/storage.md), [lookup/drop capture](../modules/lookup-drops.md) and [build/publication](../../build/stonecutter-publication.md).
