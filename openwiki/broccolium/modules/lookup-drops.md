---
type: module
title: Broccolium lookup and drop capture
description: Ordered provider discovery, complete modern entity inventories and bounded global capture.
tags: [broccolium, lookup, drops]
sources:
  - id: openwiki-source-8e194b0e3daa57910d9a7fd0
    resource: repo://docs/verification/minecraft-26-1-R2.md
  - id: openwiki-source-433a5a8f61ef03e88c1fd1ae
    resource: repo://docs/verification/minecraft-26-1-R3.md
  - id: openwiki-source-dec1566019eddd9c7a8d1d07
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/lookup/BaseLookup.kt
  - id: openwiki-source-c1737c3dd7959fcf59892814
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/lookup/ChainedBaseLookup.kt
  - id: openwiki-source-15cfff96cdf2299e3ec68c60
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/storage/item/AgnosticItemSinkLookup.kt
  - id: openwiki-source-e90ab02061cf2dd82aa378d6
    resource: repo://projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/tricks/DropConsumer.kt
  - id: openwiki-source-59e88a28b5ae0bb13e742942
    resource: repo://projects/broccolium/versions/26.1.2/src/main/kotlin/site/siredvin/broccolium/modules/storage/item/AgnosticItemStorageLookup.kt
  - id: openwiki-source-bb09eb55658a45f09e5b9944
    resource: repo://projects/tweakium/versions/26.1.2/src/main/kotlin/site/siredvin/tweakium/modules/player/FakePlayerProxy.kt
generated: { by: "hermes", at: "2026-10-21T16:59:46.460Z" }
verified:
  - by: openwiki/0.7.1
    at: 2026-10-21T16:59:46.460Z
---

# Broccolium lookup and drop capture

[Architecture overview](../../architecture/overview.md) · [Broccolium platform map](platform-base.md) · [Storage families](storage.md)

## Resolution drill-down

The item-block path below spells out priority rather than suggesting all lookups run in parallel. Solid arrows are control flow. `ChainedBaseLookup` owns an upstream lookup reference; concrete item sink lookup specializes it with the storage lookup. Fluid/energy have their own lookup families and must not be inferred to share every item fallback.

```mermaid
flowchart TB
    accTitle: Item sink resolution priority
    accDescr: Explicit sink providers precede the chained storage lookup. Storage providers precede ordered native callbacks and container fallback. Null continues; exceptions propagate.
    S[Item sink lookup for block] --> P{Explicit sink provider?}
    P -- yes --> A[Return its sink]
    P -- no --> U[Upstream item storage lookup]
    U --> Q{Explicit storage provider?}
    Q -- yes --> B[Return its storage]
    Q -- no --> C[BaseLookup callbacks in registration order]
    C -- first non-null --> B
    C -- all null --> D[Container fallback / merged chest]
    D --> E[ContainerWrapper or null]
    C -. exception .-> X[Propagate - no fallback]
```

Evidence: [sink override](../../../projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/storage/item/AgnosticItemSinkLookup.kt#L13-L24), [upstream-first chaining](../../../projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/lookup/ChainedBaseLookup.kt#L11-L41), [ordered dispatch and unsupported-object refusal](../../../projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/lookup/BaseLookup.kt#L28-L71), [target block fallback](../../../projects/broccolium/versions/26.1.2/src/main/kotlin/site/siredvin/broccolium/modules/storage/item/AgnosticItemStorageLookup.kt#L33-L63). Target storage lookup refuses removed entities/block entities; the diagram starts after that guard. The sink-provider override is a separate priority path, not proof every provider is guarded there.

Entity fallback is version-sensitive: [canonical horse inventory](../../../projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/storage/item/AgnosticItemStorageLookup.kt#L20-L34) excludes the first two legacy slots, whereas [26.1.2](../../../projects/broccolium/versions/26.1.2/src/main/kotlin/site/siredvin/broccolium/modules/storage/item/AgnosticItemStorageLookup.kt#L20-L30) wraps all cargo slots because equipment moved elsewhere. Loader callbacks may return before either fallback.

## Capture state flow

```mermaid
sequenceDiagram
    accTitle: Scoped action using global drop capture
    accDescr: FakePlayerProxy configures global capture, intercepted drops enter the inventory sink, and finally resets capture before dropping remainders even when the action throws.
    participant P as FakePlayerProxy
    participant D as DropConsumer singleton
    participant W as Native action / loader hook
    participant I as Fake inventory sink
    P->>D: configure entity or level/position
    P->>W: execute action
    W->>D: eligible spawn / exact living target drop
    D->>I: store stack
    I-->>D: unaccepted remainder
    Note over D: accumulate leftovers
    W-->>P: result or exception
    Note over P,D: finally path
    P->>D: resetAndDrop
    D->>D: clear all capture state first
    D->>W: spawn leftovers without recapture
```

The two [action wrappers](../../../projects/tweakium/versions/26.1.2/src/main/kotlin/site/siredvin/tweakium/modules/player/FakePlayerProxy.kt#L60-L75) release capture in `finally`; [DropConsumer](../../../projects/broccolium/src/main/kotlin/site/siredvin/broccolium/modules/tricks/DropConsumer.kt#L18-L73) owns the global state, exact entity check and reset-before-spawn ordering. This is not a stack of nested sessions or thread-local isolation. The sequence does not promise callback compensation or inventory rollback. [Player session reconciliation](../../tweakium/modules/player.md) is another scope entirely.

## Lookup contract

BaseLookup keeps separate block/entity/inventory-item callback lists, tries registered callbacks in order and returns the first non-null result. Null permits fallback; an extractor exception aborts resolution. Unknown-object dispatch handles supported positions/block entities/entities, not arbitrary objects as implicit containers. Chained lookups prefer upstream results, while explicit agnostic sink/storage providers have their own priority.

Read the selected version's entity fallback. Target horse cargo is the full native cargo container; equipment is separate and must not be removed by subtracting legacy slots. Native chest-minecart capability and player/container inventory cases remain explicit regressions. [R2](../../../docs/verification/minecraft-26-1-R2.md) audits modern overrides and proves horse/equipment/cargo/native lookup behavior rather than inferring it from a file count.

## Capture and cleanup

DropConsumer remains process-global mutable capture state, not thread-local or a nested capture service. Entity configuration retains the exact target; positional configuration clears it. Reset clears state before dropping remainders so leftovers do not recapture themselves. Captured contents follow the sink's remainder contract.

Fabric interception uses loader Mixins; Forge-family uses native spawn/living-drop hooks. Tweakium action wrappers release capture in finally on successful and exceptional actions. Native tests retain target identity, unrelated-entity refusal, exact remainder and one-drop behavior. Session inventory/attribute reconciliation is a separate [fake-player contract](../../tweakium/modules/player.md), not implied by capture cleanup.

Nested/concurrent capture and arbitrary third-party event behavior are not certified. See [storage](storage.md), [loader integration](../integrations/loaders.md) and [final evidence/limits](../../../docs/minecraft-26-1-verification.md).
