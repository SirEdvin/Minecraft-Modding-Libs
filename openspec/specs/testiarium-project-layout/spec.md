## Purpose

Define Testiarium's independent multi-loader project structure and upstream attribution requirements.

## Requirements

### Requirement: Independent multi-loader project
Testiarium SHALL be a separately publishable artifact family with no Broccolium production dependency. It MUST follow the repository's core, Forge, and Fabric subproject layout and provide a `testMod` source set for Minecraft 1.20.1.

#### Scenario: Build the Minecraft 1.20 release line
- **WHEN** a developer builds Testiarium for Minecraft 1.20
- **THEN** the core, Forge, and Fabric artifacts and testmod launch configuration are produced without requiring a Broccolium production dependency

### Requirement: Upstream provenance
Testiarium MUST record the upstream CC:Tweaked source path and revision for every adapted source file or resource and MUST retain its MPL-2.0 notices and other attribution obligations.

#### Scenario: Review copied framework source
- **WHEN** a maintainer reviews a copied CC:Tweaked-derived file
- **THEN** the repository identifies its upstream source and revision and includes its MPL-2.0 notices
