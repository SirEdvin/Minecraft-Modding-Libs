---
type: module
title: Tweakium Lua representations and RML transforms
description: Lua table conversion/enrichment and version-sensitive item/enchantment output, contrasted with the separate Render Markup Language parser and item-query filters.
tags: [tweakium, lua, representation, rml, rendering]
sources:
  - id: openwiki-source-433a5a8f61ef03e88c1fd1ae
    resource: repo://docs/verification/minecraft-26-1-R3.md
  - id: openwiki-source-851986a2438c3c75fd1c9bb7
    resource: repo://docs/verification/minecraft-26-1-R7-final-integration.md
  - id: openwiki-source-3e43e3d811046ad6cc4504b3
    resource: repo://docs/verification/minecraft-26-1-R7-rendering.md
  - id: openwiki-source-e0b17fde9ce9ac9863c411f7
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/rml1/Toolkit.kt
  - id: openwiki-source-876ec6bd97620a0a31754e9f
    resource: repo://projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/turtle/client/FacingBlockTurtleModeller.kt
  - id: openwiki-source-fc119dc4f1d7dd9d5d1589dc
    resource: repo://projects/tweakium/versions/1.21.1/src/main/kotlin/site/siredvin/tweakium/modules/turtle/client/FacingBlockTurtleModeller.kt
  - id: openwiki-source-f7ba39413002c6f9b1dcc87a
    resource: repo://projects/tweakium/versions/26.1.2/src/main/kotlin/site/siredvin/tweakium/modules/peripheral/representation/LuaRepresentation.kt
  - id: openwiki-source-531e2ca6d281bec2a589e8c4
    resource: repo://projects/tweakium/versions/26.1.2/src/main/kotlin/site/siredvin/tweakium/modules/turtle/client/FacingBlockTurtleModeller.kt
generated: { by: "hermes", at: "2026-10-21T17:25:53.459Z" }
verified:
  - by: openwiki/0.7.1
    at: 2026-10-21T17:25:53.459Z
---

# Tweakium Lua representations and RML transforms

[Architecture overview](../../architecture/overview.md) · [Peripheral module map](peripheral.md) · [Client registration](../integrations/loaders.md#physically-client-only-model-registration)

## Logical representation boundary

```mermaid
flowchart TB
    accTitle: Lua representation is not RML or a round-trip schema
    accDescr: Storage plugins send runtime values through LuaRepresentation and ComputerCraft detail registries to Lua maps. Optional enrichers mutate those maps. Input interpretation and query predicates are separate APIs; RML only builds rendering transforms.
    P[Storage plugin runtime values] --> L[LuaRepresentation]
    L --> D[CC item / block detail registries]
    D --> M[Mutable Lua maps / lists]
    E[Optional matching enrichers] --> M
    I[LuaInterpretation input] --> V[Native values / relative coordinates]
    Q[Plugin query map] --> F[Predicates]
    R[RML text] --> T[Render instructions / PoseStack]
```

Arrows show data flow, not automatic reversible conversion. Target item detail conversion requires [an active registry context](../../../projects/tweakium/versions/26.1.2/src/main/kotlin/site/siredvin/tweakium/modules/peripheral/representation/LuaRepresentation.kt#L101-L116); BASE/DETAILED delegate CC, FULL adds component-derived `rawNBT`. [Fluid output](../../../projects/tweakium/versions/26.1.2/src/main/kotlin/site/siredvin/tweakium/modules/peripheral/representation/LuaRepresentation.kt#L125-L135) keeps both truncated `amount` and `precise_amount`. These values are not an untyped lossless persistence protocol. Schema/version and enricher details remain below; [storage plugin listing](../../../projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/plugins/AbstractItemStoragePlugin.kt#L30-L41) is a concrete caller.

## RML transform flow

```mermaid
flowchart TB
    accTitle: RML parse and explicit transform processing
    accDescr: Registered lexemes turn semicolon-separated text into render instructions. A caller must process those instructions on a PoseStack. Parsing errors stop the path; the facing-block modeller uses fixed transforms instead of invoking this parser.
    D[injectDefault or addLexeme] --> P[RMLParser lexeme map]
    T[Text split at semicolons] --> P
    P --> A[Regex find / lexeme lookup / argument parsing]
    A -- valid --> I[Translate / Rotation / Scale instructions]
    A -- invalid --> X[Instruction / lexeme / argument exception]
    I -- caller processes in order --> S[Mutate supplied PoseStack]
    S --> C[Clamp shifts / scales / pivots, not angle]
```

[Parser](../../../projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/rml1/Toolkit.kt#L29-L55) owns registration and instruction creation; [operations](../../../projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/rml1/Operations.kt#L7-L101) own processing. [RMLTests](../../../projects/tweakium/src/test/kotlin/site/siredvin/tweakium/RMLTests.kt#L14-L34) checks parse and unknown lexeme, not rendered geometry. RML is not a hardened full-string grammar, and no implicit connection to every modeller is asserted.

## Client model extraction and submission (26.1.2)

```mermaid
sequenceDiagram
    accTitle: Target facing-block turtle model extraction then submission
    accDescr: Native baking creates the modeller. Item extraction resolves an immutable crafting definition into child render state, records model identities and installs a side-specific special renderer. Submission only consumes the extracted state and restores PoseStack in finally.
    participant N as Native client
    participant M as Modeller
    participant R as Resolver
    participant S as Child state
    participant T as Renderer
    N->>M: Unbaked.bake
    N->>M: renderForItem
    M->>R: resolve crafting item
    R-->>M: child state
    opt child not empty
        M->>M: append identities
        M->>T: layer + child state
        N->>T: submit state
        T->>T: pushPose + matrix
        T->>S: submit to collector
        T->>T: finally popPose
    end
```

This is client rendering, not owner/plugin composition. [Target extraction/bake/submission](../../../projects/tweakium/versions/26.1.2/src/main/kotlin/site/siredvin/tweakium/modules/turtle/client/FacingBlockTurtleModeller.kt#L26-L73) uses fixed side matrices and child state, not a live world read during submission; no new worker-thread scheduler is introduced by this class. [1.20.1](../../../projects/tweakium/src/main/kotlin/site/siredvin/tweakium/modules/turtle/client/FacingBlockTurtleModeller.kt#L12-L24) and [1.21.1](../../../projects/tweakium/versions/1.21.1/src/main/kotlin/site/siredvin/tweakium/modules/turtle/client/FacingBlockTurtleModeller.kt#L13-L25) instead return `TransformedModel` (the latter receives a component patch argument). Neither calls RMLParser. [Loader client gates](../integrations/loaders.md#physically-client-only-model-registration) own codec installation, while historical/current graphical acceptance remains below. This diagram is not a fresh Minecraft client execution.

## Current target rendering and schema boundary (2026-10-19)

Lua representation, map predicates and RML remain separate languages; output maps are not universally reversible input schemas. Target registry-aware item/enchantment/component adapters retain public Lua field identities while native Kotlin/CC signatures require target consumer recompilation. Runtime values remain ItemStack, definition templates resolve only with valid context.

FacingBlockTurtleModeller and RML target adapters use native extracted/model/material submission paths, not a permanent compatibility rewrite or live mutable world reads during submission. [R7 rendering](../../../docs/verification/minecraft-26-1-R7-rendering.md) and [final integration](../../../docs/verification/minecraft-26-1-R7-final-integration.md) retain six actual workflows,90 original PNGs,192 positive material submissions and equipped upgrade views across reload/reconnect. Parser unit tests alone are not this proof. No arbitrary third-party geometry, all numeric parser edges or hardened untrusted-RML grammar certification is implied; see [final limits](../../../docs/minecraft-26-1-verification.md).

## Historical source notes — October16 baseline

The following327f845/e69b721 notes remain useful grammar/schema background, but their unperformed-rendering statement is historical. Current target overrides and explicit rendering evidence above take precedence.

## Representation is a boundary, not a universal schema

LuaRepresentation converts blocks, entities, items, fluids, energy and other world values into mutable maps/lists. Item BASE/DETAILED modes delegate to ComputerCraft's detail registries; FULL adds rawNBT. Block V2 similarly delegates to the block-in-world detail registry and adds displayName. Thus extension data supplied by CC detail registries can affect results; not every table is hard-coded here.

Position helpers subtract a center and transform to facing-relative coordinates. LuaInterpretation reverses that orientation for relative positions/vectors, requires numeric x/y/z and converts block coordinates to Int. It accepts item IDs or maps with item/count, validates resource IDs through asID, and builds block states from named properties. These are distinct input schemas: the item output's name field is not the input map's item field. There is no promise every output table can round-trip unchanged.

Enrichers are BiConsumers mutating caller maps when entity/block types match: animal, effects, merchant/villager, properties/crop/honey and beehive data. They are reusable optional functions rather than a global automatic enrichment pipeline. Preserve exact output keys when changing APIs; for example the shared animal enricher emits shareable, not a corrected spelling inferred by a client.

## Version and precision contracts

In 1.20.1 forEnchantment accepts Enchantment and derives name from the description ID. The 1.21.1 override accepts Holder<Enchantment>, emits registeredName and uses Enchantment.getFullname(holder,level). Both enchantment collection signatures also change their key type. Consumers need source/binary compatibility review, not just an unchanged Lua field name.

FULL item rawNBT comes from legacy stack.tag in 1.20.1 and a converted nonempty componentsPatch in 1.21.1. The name rawNBT remains even when the representation underneath is components. Fluid output provides amount truncated to Long and precise_amount retaining Double, with optional nbt hash derived from tag or component patch by version. Do not use rounded amount to reconstruct fractional fluid state.

## Three different languages

PeripheralPluginUtils map filters accept item/block query maps with predicates; they are described in [plugins](plugins.md). They are not RML.

RML means Render Markup Language. RMLParser splits instructions by semicolon, regex-extracts lexeme(arguments), resolves registered lexemes and builds RenderInstructions. injectDefault adds t, r and s. Examples matching current tests/source are:

    t(1, 2, 3);r(x, 90, 0, 0, 0)
    s(0.3, 0.3, 0.3)

The package comment's draft dotted syntax is not the implemented grammar. Translation and scale require three floats; rotation takes lower-case x/y/z, angle in degrees and three pivot offsets. Processing mutates the supplied PoseStack in sequence. Translation, scale and rotation pivots clamp to -4..4 at processing time; angle is not clamped. Lexeme registration replaces by name. An empty parser has no defaults until injected.

The parser uses regex find, not a complete tokenizer or full-string match. It has no explicit finite-number check, robust nested grammar or trailing-semicolon normalization. Wrong counts/numbers, malformed instruction and unknown lexeme have distinct error routes. Do not treat it as a hardened untrusted-input language merely because coordinate values are clamped.

## Tests and extension seams

RMLTests checks a translation/rotation parse and unknown-lexeme failure. It does not exercise PoseStack effects, all parser errors or every numeric edge. The inspected FacingBlockTurtleModeller directly builds a fixed PoseStack transform and returns a transformed crafting-item model; that file does not invoke RML, so parser existence is not evidence of automatic integration into every renderer.

Add conversions/enrichers where the owning API requires them and update [typed client contracts](../../typed-peripheral-api/contracts.md) when exposed. A new RMLLexeme must be registered deliberately. No JVM/client/rendering tests were executed during wiki creation.
