# Minecraft 26.1 planning source ledger

Task t_bd56f66a; research observed 2026-10-16 UTC / completed 2026-10-17 Europe/Kyiv. Read with the [brief](minecraft-26-1-migration-brief.md) and [implementation plan](plans/minecraft-26-1.md). This is primary-source availability/contract evidence, not a successful port.

## Reproduction and provenance

The task's evidence attachment `t_bd56f66a-planning-evidence.zip` preserves selected primary responses, original `sources.json`, `refreshed-sources.json`, `pin-sources.json`, `pinned-primers.json`, validation results and their SHA-256 ledger. Each manifest records exact URL, response status/error, hash, fetch time when recorded, and file path when available. Original cache root: `/home/siredvin/.hermes/profiles/albina/cache/scratch/t_bd56f66a/`; scratch paths may expire, so use the attachment for durable bodies. Sources below use the same IDs as those manifests. Preserve bodies unmodified when checking hashes; HTML is not a rendered/cleaned-text snapshot.

Validated every original manifest entry with an explicit file path: 52 bodies matched stored hashes. The later refreshed source manifest records 18 additional HTTP200 responses; six pin/source responses also returned200. All ten primers were refetched at the fixed revision below and matched cached body hashes. Entries lacking timestamps or local file paths are weaker provenance, not silently treated as complete snapshots. Publisher metadata from Modrinth is publisher-owned release evidence, not an independently certified compatibility test.

## Vanilla and cumulative NeoForge boundaries

- `mojang`: https://piston-meta.mojang.com/mc/game/version_manifest_v2.json — target26.1.2 release availability. Refreshed hash `ac37f92737df1a9d7660047136fedd3edebd90bd6e552f53ddc3f222079bc8fa`.
- `primer-<version>`: https://github.com/neoforged/.github/tree/382381b116e4a107c09fcd1fe7afaf48626d1ef3/primers — pinned repository revision for all reviewed boundaries. For each row below the raw URL is `https://raw.githubusercontent.com/neoforged/.github/382381b116e4a107c09fcd1fe7afaf48626d1ef3/primers/<version>/index.md`.

| Version | SHA-256 of verified pinned body | Scope used |
| --- | --- | --- |
| 1.21.2 | 4ea90a4383764edae33f18e7910767ebd17f56f81124753f69076604f97eca27 | keyed properties, recipes, render states |
| 1.21.4 | 67569aaea382cc5aa1fa874dff60786cf6f2e4c6e685e40a4857c64c6d8d68fb | client items/models, resource codecs |
| 1.21.5 | a8269dee6c1ff8b9dfb406f5ac360df1692ff573b73515cf8078664da4ccdeb5 | GameTest registry, save types, removal, GPU |
| 1.21.6 | a1b8a426715a20f159c7caab091ded767e4be1f02fb14e69f40eb3a92a36dbdd | ValueInput/Output, GUI extraction |
| 1.21.7 | a3bc8f5411ee8b0462f92b9ab2aa81ff06e66aa27c87dfbd79bc86cdccf47b7a | patch delta |
| 1.21.8 | 4a3ff01de435c3e829fd013a417b05eb663c6eb02973fcc4f998825d0a37a9b4 | patch delta |
| 1.21.9 | 99f38f90f3e3ffe75dabce613e7075cdf1ff674f4cd818d2baf2a9a5f090dc2f | extraction/submission, item/data changes |
| 1.21.10 | c8ce0c6ec1cc84bb98adf5022afa05d8e5db513f412f86f76c3b2e95fcddc8ff | patch delta |
| 1.21.11 | 4c346b1c4ee159fb8496b17bfec6f8457d9bc71851526e22675dc6e5e96d25d8 | Identifier, rendering/materials |
| 26.1 | f2c4fb3fc551239ff3e96240800f7919168d17de096649eaf0c378afe728985b | templates, SavedData/layout, GUI, test environments |

- `neo-transfer`: https://neoforged.net/news/21.9-transfer-rework/ — native ResourceHandler, resource identity, ItemAccess and EnergyHandler semantics; snapshot hash `ee74afa1aa7d9b9ec56372c5197a66a2b0013ea73e6916db1bc43c6a8fd5dd85`.
- `neo-release`: https://neoforged.net/news/26.1release/ — cumulative migration and templates; snapshot hash `fda366c3c06742f94ea7f8c7f4cd3f7b8b92f4853d19b2cdb36cd10d98280271`.
- `neoforge`: https://maven.neoforged.net/releases/net/neoforged/neoforge/maven-metadata.xml — family-scoped26.1.2.122, not globally latest26.x. `neo-pin`: https://maven.neoforged.net/releases/net/neoforged/neoforge/26.1.2.122/neoforge-26.1.2.122.pom — retrieved hash `d70925174514c63154d4426e9f20d0271cb1d9d0430b6f806e47211ffee1eb0b`.
- `neo-template-rev`: https://github.com/NeoForgeMDKs/MDK-26.1.2-ModDevGradle/tree/992ae7fda4efc0e85887ca966d188bf1e8efc61c — exact inspected MDK revision; cached gradle.properties/build.gradle are navigation examples. Its NeoForge26.1.2.116 is older than selected metadata pin; template values are not claimed latest.

## Fabric cumulative API and tooling evidence

Maintained source tags (not historical announcement guesses):

- API0.92.3+1.20.1: https://github.com/FabricMC/fabric/tree/634caade79f6d06201c6939a76299d2362726fd5
- API0.116.14+1.21.1: https://github.com/FabricMC/fabric/tree/80e63a7be61dff7658861e1c7df8643c3e676c5b
- API0.155.3+26.1.2: https://github.com/FabricMC/fabric/tree/1abe2820db1f3304fe06785fee88497bf593aeb2
- `fabric-compare`: https://api.github.com/repos/FabricMC/fabric/compare/0.116.14%2B1.21.1...0.155.3%2B26.1.2 — large compare response is a navigation aid with API truncation limits, not exhaustive source review. Focused `fabric-transfer-old/new` and `fabric-screen-old` snapshots are in the evidence archive; old screenhandler path no longer exists on target. Target renamed API is documented below.

Versioned official migration guidance (timestamped snapshots, not immutable Git revisions):

- `fabric-port`: https://docs.fabricmc.net/26.1.2/develop/porting/ — hash `f770f072f52363d13bdc6264eaa2246d90e3fc5529d004468060bb345ca5fb7b`.
- `fabric-api-port`: https://docs.fabricmc.net/26.1.2/develop/porting/fabric-api — hash `0a49f98a7fcedbab14594cd601ed9e0a8cb036deacd136f77b570f313fafe322`.
- `fabric-loom-doc`: https://docs.fabricmc.net/develop/loom/ — plugin variants/source-set/access contract, hash in original manifest.

Official historical release notes reviewed in order, using IDs `fabric-news-1212`, `1214`, `1215`, `1216`, `1219`, `12111`, `261` in evidence manifests:

- https://fabricmc.net/2024/10/14/1212.html — Loom1.8/Loader0.16.7, resource/loot/module changes.
- https://fabricmc.net/2024/12/02/1214.html — Loom1.9/Loader0.16.9, item/model API removals.
- https://fabricmc.net/2025/03/24/1215.html — Loom1.10/Loader0.16.10, client tests and deprecations.
- https://fabricmc.net/2025/06/15/1216.html — Loom1.10/Loader0.16.14, module merges/material removal.
- https://fabricmc.net/2025/09/23/1219.html — Loom1.11/Loader0.17.2, resource/world rendering changes.
- https://fabricmc.net/2025/12/05/12111.html — Loom1.14/Loader0.18.1, rendering reintroduction, networking, unobfuscation preparation.
- https://fabricmc.net/2026/03/14/261.html — release-day Java25/non-remapping transition; historical Loom1.15/Gradle9.4 recommendations are superseded by exact selected target metadata, not copied blindly.

Exact selected tooling evidence:

- `fabric-api-pin`: https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/0.155.3+26.1.2/fabric-api-0.155.3+26.1.2.pom — hash `710128ef466f92ec32d488d01cae734fc3f12714d7443f8213b7d6597d1927a5`.
- `fabric-loader`: https://meta.fabricmc.net/v2/versions/loader — stable flag distinguishes selected0.19.5 from unstable0.20.0. `loader-releases`: https://api.github.com/repos/FabricMC/fabric-loader/releases?per_page=10 — official release bodies replace nonexistent guessed CHANGELOG.md. Relevant Mixin/ASM/environment changes are not vanilla-mapping changes.
- `loom-unobfuscated-marker`: https://maven.fabricmc.net/net/fabricmc/fabric-loom/net.fabricmc.fabric-loom.gradle.plugin/1.17.11/net.fabricmc.fabric-loom.gradle.plugin-1.17.11.pom — hash `5e39fb09e65e0d37025dc59b1ef2e48769c367a0011a2371718e3a2738804c7d`. `loom-source`: https://raw.githubusercontent.com/FabricMC/fabric-loom/1.17/build.gradle — source branch snapshot/hash in pin manifest, not an immutable release tag. Guessed GitHub1.17.11 release/tag endpoints returned404; Maven marker plus CC's pinned release catalog establishes selected-version availability.
- `fabric-template-rev`: https://github.com/FabricMC/fabric-example-mod/tree/090c8db532463d1135db21c1eaf46cf073c03def — inspected26.1 branch example uses1.18-SNAPSHOT; not selected. Use exact target pin rather than automatic snapshot adoption.

## CC:Tweaked and runtimes

- `cc-release-commit`: https://github.com/cc-tweaked/CC-Tweaked/tree/5403b7070bc9834b1a5d5fe432a9810ff6de9232 — tag v26.1.2-1.120.0 resolves here. `cc-release-catalog`, `cc-properties`, `cc-release-build` were read from that tag. Catalog selects Loom1.17.11 and supports the target loader split; no promise that every old API signature remains unchanged.
- `cc-api-sources`: https://maven.squiddev.cc/cc/tweaked/cc-tweaked-26.1.2-common-api/1.120.0/cc-tweaked-26.1.2-common-api-1.120.0-sources.jar — retrieved source hash `de8e1da61e63a7c350e8ac620c4c93ff18ca712de6740973bad5b9e929332cfa`. UpgradeBase's crafting/upgrade-item/data APIs use ItemStackTemplate; ITurtleUpgrade persists component patches.
- `cc-pom-*`: base https://maven.squiddev.cc/cc/tweaked/ — exact artifact family `cc-tweaked-26.1.2-{common,common-api,core,fabric,fabric-api,forge}:1.120.0`. POM bodies/status/hashes retained. `cc-forge-jar` URL ends `cc-tweaked-26.1.2-forge/1.120.0/cc-tweaked-26.1.2-forge-1.120.0.jar`; hash `298265ac55a43aa3ea05f00af0b2e31805f2afb4757273cd1091ebc8c35f534d`. Inspected neoforge.mods.toml confirms this is NeoForge, not legacy Forge. Old guessed -neoforge and26.1 coordinates returned404.
- `fabric-kotlin-pom`: https://maven.fabricmc.net/net/fabricmc/fabric-language-kotlin/1.13.12+kotlin.2.4.0/fabric-language-kotlin-1.13.12+kotlin.2.4.0.pom — hash `513cc7ed507c0fb784a7c6b46c6f667711a7ddea6ed970dcba80f9b3309effbf`.
- `kff-pom-6.4.0`: https://thedarkcolour.github.io/KotlinForForge/thedarkcolour/kotlinforforge-neoforge/6.4.0/kotlinforforge-neoforge-6.4.0.pom — Kotlin2.4.0 dependency alignment; hash `3a177a3adf2af3d0f13a01cb198beb8190d44f77010a70fdf0d2d368ad3f0722`.
- `kff64-jar`: https://thedarkcolour.github.io/KotlinForForge/thedarkcolour/kffmod-neoforge/6.4.0/kffmod-neoforge-6.4.0.jar — hash `237304b79f46a5ddfa3745fe6df0af42dc7130bbd39f06874ba821b37516ce36`; manifest Minecraft range includes26.1.2. Runtime launch remains future evidence.
- `kotlin-compat`: https://kotlinlang.org/docs/gradle-configure-project.html — table explicitly covers KGP2.4.0–2.4.10 through Gradle9.5.0. Cached hash `f9816bd381c55b698a97a5e7647ac50a1be5624fdd07c61832454c23bb3fd92b`.
- `kotlin-23`: https://kotlinlang.org/docs/whatsnew23.html — Java25 target support begins before selected2.4.0; not evidence that compiler2.0.0 supports JVM25.
- `gradle-java`: https://docs.gradle.org/9.5.0/userguide/compatibility.html — launcher/toolchain table; hash `eaa4be80bad31ca51954c8ee67a3e8750cb0fd955220dfc62aad4a8626f213cf`.
- `mdg`: https://maven.neoforged.net/releases/net/neoforged/moddev/net.neoforged.moddev.gradle.plugin/maven-metadata.xml — shared2.0.142 already used locally;2.0.148 available. `mdg-breaking`: https://raw.githubusercontent.com/neoforged/ModDevGradle/main/BREAKING_CHANGES.md — timestamped hash `f411a54a2bf48006876588904d216575155ee9b7b396076f488789cfa7a51f65`; enable-once, Parchment pair, source-set/loadedMods/AT publication changes considered.
- `stonecutter`: https://maven.kikugie.dev/releases/dev/kikugie/stonecutter/maven-metadata.xml — refreshed stable0.9.8; hash `dd46f9bfe3bfd7972764c3e6d58b84b34fc25164c4cfb0cf1a69732aa1a099bd`. Prerelease review belongs to inherited audit and is not a reason to adopt it here.

## Integrations and tests

- `energy-jar`: https://maven.fabricmc.net/teamreborn/energy/5.0.0/energy-5.0.0.jar — hash `889afc438d3e4add5cfdac76517da7987a2c495e4731690a56f2c5dee775db59`; inspected fabric.mod.json requiresJava25/MC26.1/transferAPI>=5.1.0.
- `config-modrinth`: https://api.modrinth.com/v2/project/forge-config-api-port/version?game_versions=%5B%2226.1.2%22%5D — select Fabric26.1.5 IDjUe0ucoE, not similarly named Forge/NeoForge release. Guessed Maven coordinates failed; compile coordinate is unresolved if actually necessary. Do not infer runtime absence from DNS/404 errors.
- `rei-pom`: https://maven.shedaniel.me/me/shedaniel/RoughlyEnoughItems-fabric/26.1.819/RoughlyEnoughItems-fabric-26.1.819.pom — retrieved; `rei-modrinth` publisher release ID EAlqUmgQ also lists MC26.1.2.
- `modmenu-modrinth`: https://api.modrinth.com/v2/project/modmenu/version?game_versions=%5B%2226.1.2%22%5D —18.0.2 release ZJ6YTrMY, Fabric client integration.
- `jei-pom`: https://maven.blamejared.com/mezz/jei/jei-26.1.2-neoforge/29.47.0.111/jei-26.1.2-neoforge-29.47.0.111.pom — hash `0e748004654a1d56358c9fbf39c18364bf60b758ea483d285e9ecf50bf8dd5df`.
- `bytebuddy`: https://repo.maven.apache.org/maven2/net/bytebuddy/byte-buddy/1.17.8/byte-buddy-1.17.8.pom — retrieved target candidate; actual instrumentation/agent verification is required, not inferred from artifact existence.

## Repository evidence and planning validation

Source state: instructions e3a7adb94492aedc7abda5680d3bda8b72990351 descends from coverage e69b7218f5bb077e49e76787168a596f49f26716, including OpenWiki327f845797e24a3447f4f248f334dc77ebdd35dd. Exact final cumulative planning SHA is in task completion metadata and the local LLM-Wiki report (avoids a self-referential commit hash in these committed documents).

Authoritative code inspected: settings/root/catalog/properties/wrapper; Broccolium common/Fabric and Tweakium common/Forge scripts; all-library layout and access/Mixin paths; modern MutableNBTBlockEntity, IDataStorage/DataStorageUtil, ForgeStorageUtils, FabricInnerBasePlatform, FacingBlockTurtleModeller; PeripheraliumCore/Items; Testiarium GameTests/Fabric registration; TypeScript manifest/contracts and publication checker/CI. Focused searches covered networking, saved data/attachments, config imports, reflection, client/render paths, registry APIs and modern overrides. Wiki supplied navigation; source and accepted parent evidence supplied claims.

Companion HEADs verified read-only: TemplateProject f0c84163b06a86d83a8481e15b8968416888110d; modding-buildenv55b844ff7a6ef2c08244378e4e72b44bac3b4b81; minecraft-modding-skills734962deb8d23abf5363a4b6453dc7b157a98ce6. Their primary checkouts were not modified. The accepted parent coverage attachment remains separate, not regenerated.

Planning checks: ancestry, source snapshot hashes, primary HTTP statuses/JAR metadata, existing-vs-proposed path verification, local Markdown links, shell command syntax, acceptance row coverage and git diff --check. No Gradle or runtime acceptance campaign was run on this planning card. A blocked optional search backend was not bypassed or represented as source evidence; direct primary metadata retrieval supplied the required research.
