## 1. Independent Project Setup

- [x] 1.1 Create Testiarium core, Forge, and Fabric subprojects with a `testMod` source set, version properties, and publication configuration following the Broccolium project layout.
- [x] 1.2 Configure Minecraft 1.20.1 Forge/Fabric with the required Java toolchain, mappings, loader dependencies, and development launch configurations.
- [x] 1.3 Add loader metadata and minimal standalone testmods for Forge and Fabric.

## 2. CC:Tweaked Framework Extraction

- [x] 2.1 Add MPL-2.0 notices and provenance for the inspected CC:Tweaked 1.20 source paths selected for adaptation.
- [x] 2.2 Port the generic tag filter, client-test annotation, helper/sequence utilities, multi-reporter, and JUnit XML reporter into Testiarium without `dan200.computercraft` references.
- [x] 2.3 Define explicit test-class registration and implement Forge and Fabric adapters that register those tests through each loader's GameTest support.

## 3. Standalone GameTest Verification

- [x] 3.1 Configure game-test launch profiles with assertion status, structure source path, enabled test groups, loader-required GameTest flags, and JUnit XML result paths.
- [x] 3.2 Add passing, required-failure, and non-required-failure fixtures that verify explicit registration, group filtering, logs, and JUnit XML output without CC:Tweaked.
- [x] 3.3 Run the standalone testmods on Forge/Fabric 1.20.1 and verify JUnit XML result semantics.

## 4. Optional CC:Tweaked Integration

- [x] 4.1 Add isolated Forge and Fabric adapter test configurations with matching CC:Tweaked public API and runtime artifacts.
- [x] 4.2 Implement the CC:Tweaked adapter's peripheral helpers and guard registration so its absence does not affect standalone Testiarium testmods.
- [x] 4.3 Add and run a peripheral GameTest using only CC:Tweaked public APIs on Forge/Fabric 1.20.1.

## 5. Consumer Validation

- [x] 5.1 Add concise consumer documentation showing how an external mod declares standalone and optional CC:Tweaked GameTests.
- [x] 5.2 Validate Testiarium from a Broccolium testmod dependency without adding a Broccolium production dependency.
