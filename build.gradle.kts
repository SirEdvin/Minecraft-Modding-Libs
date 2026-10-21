import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

plugins {
    java
    alias(libs.plugins.loom) apply false
    id("net.fabricmc.fabric-loom") version "1.17.11" apply false
    id("org.jetbrains.kotlin.jvm") version "2.4.0" apply false
    id("net.neoforged.moddev.legacyforge") version "2.0.142" apply false
    id("site.siredvin.root") version "0.9.0"
    id("site.siredvin.release") version "0.9.0"
    id("site.siredvin.linting") version "0.9.0"
    id("com.github.ben-manes.versions") version "0.51.0"
}

subprojectShaking {
    withKotlin.set(true)
    kotlinVersion.set("2.0.0")
    javaVersion.set(JavaVersion.VERSION_21)
}

val setupSubproject = subprojectShaking::setupSubproject
val subprojectConfig = subprojectShaking
val projectGroup: String by project

apply(from = "gradle/gametest-reports.gradle.kts")

subprojects {
    if (projectDir.parentFile.name == "versions") {
        extra["minecraftVersion"] = name
        if (name != "26.1.2" && !path.contains(":forge:")) {
            subprojectConfig.javaVersion.set(
                if (name == "1.20.1") JavaVersion.VERSION_17 else JavaVersion.VERSION_21,
            )
            setupSubproject(this)
            // Shared sources are checked once, from the repository root that owns them.
            extensions.configure<SpotlessExtension> {
                java { target(emptyList<String>()) }
                kotlin { target(emptyList<String>()) }
                kotlinGradle { target(emptyList<String>()) }
            }
            if (!path.contains(":fabric:")) {
                pluginManager.apply("net.fabricmc.fabric-loom-companion")
            }
        }

        val minecraftVersion = name
        // Gradle identifies projects by group/name/version; every leaf has the same version name.
        // Keep internal module identities unique while publishing under projectGroup below.
        group = "$projectGroup.${path.substringBeforeLast(':').removePrefix(":").replace(':', '.')}"
        // Stonecutter excludes overrides from generated sources, but not the active shared tree.
        val sharedSources = projectDir.parentFile.parentFile.resolve("src").toPath()
        val localSources = projectDir.resolve("src")
        val overridden = Spec<FileTreeElement> { entry ->
            val source = entry.file.toPath()
            source.startsWith(sharedSources) && localSources.resolve(sharedSources.relativize(source).toString()).isFile
        }
        pluginManager.withPlugin("java") {
            tasks.withType<Jar>().configureEach {
                exclude("**/.cache/**")
                dependsOn("stonecutterGenerate")
            }
            tasks.named("processResources") { dependsOn("stonecutterGenerate") }
            tasks.withType<ProcessResources>().configureEach {
                if (minecraftVersion == "26.1.2" && name != "processResources") {
                    val fixtureVersions = listOf("broccolium", "testiarium", "tweakium", "peripheralium")
                        .associate { "${it}Version" to providers.gradleProperty("${it}Version").get() }
                    inputs.properties(fixtureVersions)
                    filesMatching("META-INF/neoforge.mods.toml") { expand(fixtureVersions) }
                }
                // Published 0.9.0 conventions do not track raw dependency mappings.
                for (library in listOf("broccolium", "testiarium", "tweakium", "peripheralium")) {
                    inputs.property("${library}Version", providers.gradleProperty("${library}Version"))
                }
                inputs.file(project.buildFile)
            }
            extensions.getByType<SourceSetContainer>().configureEach {
                java.exclude(overridden)
                resources.exclude(overridden)
                allJava.exclude(overridden)
                allSource.exclude(overridden)
            }
            val mainResources = extensions.getByType<SourceSetContainer>().named("main").get().resources
            gradle.projectsEvaluated {
                // Generated JSON has no version comments. Stonecutter redirects the
                // extra generated root for inactive nodes without generating it;
                // select the authored resource roots explicitly instead.
                val ordinaryRoots = mainResources.srcDirs.filterNot { it.parentFile.name == "generated" }
                val generatedRoots = mutableListOf(localSources.resolve("generated/resources"))
                if (minecraftVersion != "26.1.2") generatedRoots.add(sharedSources.resolve("generated/resources").toFile())
                mainResources.setSrcDirs(ordinaryRoots + generatedRoots)
            }
        }
        pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            if (minecraftVersion != "26.1.2") {
                tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
                    compilerOptions.languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
                    compilerOptions.apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
                    // Old consumers deliberately retain 2.0 metadata. Keep source
                    // warnings fatal; only acknowledge this chosen version floor.
                    compilerOptions.freeCompilerArgs.add("-Xsuppress-version-warnings")
                }
            }
            extensions.getByType<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension>().sourceSets.configureEach {
                kotlin.exclude(overridden)
            }
        }
        afterEvaluate {
            // Enable after Kotlin: enabling earlier publishes a competing kotlinSourcesJar.
            extensions.configure<JavaPluginExtension> { withSourcesJar() }
            if (path.split(':')[1] == "testiarium") {
                val part = if (path.contains(":fabric:")) {
                    "fabric"
                } else if (path.contains(":forge:")) {
                    "forge"
                } else {
                    "core"
                }
                for (feature in listOf("testMod", "cctTestMod")) {
                    if (minecraftVersion != "26.1.2") {
                        // KGP's new compiler must not choose the feature runtime floor.
                        dependencies.add("${feature}Api", "org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
                    }
                    // Feature JARs contain only feature output, not the core API.
                    // Old Loom runtimeElements carries intermediary mod dependencies.
                    // Local feature runs already inherit the named main classpath.
                    val coreDependency = if (part == "fabric" && minecraftVersion != "26.1.2") {
                        dependencies.project(mapOf("path" to path, "configuration" to "namedElements"))
                    } else {
                        project(path)
                    }
                    dependencies.add("${feature}Api", coreDependency)
                    if (feature == "cctTestMod") {
                        val testModDependency = (dependencies.project(mapOf("path" to path)) as ProjectDependency).apply {
                            capabilities {
                                requireCapability("$projectGroup:testiarium-$part-test-mod")
                            }
                        }
                        dependencies.add("${feature}Api", testModDependency)
                    }
                    if (part != "core") {
                        val commonProject = project(":testiarium:$minecraftVersion")
                        tasks.named<Jar>("${feature}SourcesJar") {
                            from(commonProject.extensions.getByType<SourceSetContainer>().named(feature).map { it.allSource })
                            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                        }
                    }
                }
            }
        }
        if (path.contains(":fabric:") || path.contains(":forge:")) {
            val commonProject = project(":${path.split(':')[1]}:$minecraftVersion")
            afterEvaluate {
                tasks.named("processResources") { dependsOn("${commonProject.path}:stonecutterGenerate") }
                tasks.withType<Jar>().configureEach { dependsOn("${commonProject.path}:stonecutterGenerate") }
                tasks.named<Jar>("sourcesJar") {
                    from(commonProject.extensions.getByType<SourceSetContainer>().named("main").map { it.allSource })
                    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                }
                if (path.contains(":forge:")) {
                    extensions.getByType<net.neoforged.moddevgradle.dsl.ModDevExtension>().setAccessTransformers(
                        extensions.getByType<SourceSetContainer>().named("main").map {
                            it.resources.matching { include("META-INF/accesstransformer.cfg") }
                        },
                    )
                    tasks.named("createMinecraftArtifacts") { dependsOn("stonecutterGenerate") }
                }
            }
        }
        pluginManager.withPlugin("maven-publish") {
            extensions.configure<PublishingExtension> {
                publications.withType<MavenPublication>().configureEach {
                    val publication = this
                    groupId = projectGroup
                    pom {
                        name.set(provider { "${publication.artifactId} for Minecraft $minecraftVersion" })
                        description.set(provider { "${publication.artifactId} library module for Minecraft $minecraftVersion" })
                        url.set("https://github.com/SirEdvin/Minecraft-Modding-Libs")
                        licenses {
                            license {
                                name.set("MIT License")
                                url.set("https://opensource.org/licenses/MIT")
                            }
                        }
                        developers {
                            developer {
                                name.set("SirEdvin")
                                email.set("me@siredvin.site")
                                organization.set("SirEdvin")
                                organizationUrl.set("https://siredvin.site")
                            }
                        }
                        scm {
                            connection.set("scm:git:https://github.com/SirEdvin/Minecraft-Modding-Libs.git")
                            developerConnection.set("scm:git:ssh://git@github.com/SirEdvin/Minecraft-Modding-Libs.git")
                            url.set("https://github.com/SirEdvin/Minecraft-Modding-Libs")
                        }
                    }
                }
            }
        }
    }
}

repositories {
    mavenCentral()
}

// One monorepo release contains every Minecraft node, not the unversioned controllers.
extensions.configure<com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension> {
    val releaseVersion = providers.gradleProperty("broccoliumVersion")
    owner.set("SirEdvin")
    repo.set("Minecraft-Modding-Libs")
    tagName.set(releaseVersion.map { "v$it" })
    releaseName.set(releaseVersion.map { "Minecraft Modding Libraries $it" })
    targetCommitish.set(providers.gradleProperty("releaseCommit").orElse("multiversion"))
    setToken(providers.environmentVariable("GITHUB_TOKEN"))
    prerelease.set(releaseVersion.map { it.contains('-') })
    draft.set(false)
    overwrite.set(false)
    allowUploadToExisting.set(false)
    dryRun.set(false)
    body.set(
        "Multiversion release for Minecraft 1.20.1 (Forge/Fabric), " +
            "1.21.1 (NeoForge/Fabric), and 26.1.2 (NeoForge/Fabric). " +
            "Includes Broccolium, Testiarium, Tweakium, and Peripheralium, " +
            "with common modules and source JARs. Use artifacts matching your Minecraft version and loader. " +
            "This is a release candidate; back up worlds before upgrading.",
    )
    gradle.projectsEvaluated {
        for (library in listOf("broccolium", "testiarium", "tweakium", "peripheralium")) {
            require(providers.gradleProperty("${library}Version").get() == releaseVersion.get()) {
                "The aggregate GitHub release requires matching library versions"
            }
        }
        subprojects.filter { it.projectDir.parentFile.name == "versions" }.forEach { leaf ->
            val binaryTask = when {
                leaf.path.contains(":fabric:") && leaf.name != "26.1.2" -> "remapJar"
                leaf.path.contains(":forge:") && leaf.name == "1.20.1" -> "reobfJar"
                else -> "jar"
            }
            releaseAssets.from(leaf.tasks.named(binaryTask), leaf.tasks.named("sourcesJar"))
        }
    }
}

spotless {
    // Enforce formatting on changed files without rewriting the inherited version snapshots.
    ratchetFrom("origin/multiversion")
    java {
        target(
            fileTree("projects") {
                include("**/src/**/*.java")
                exclude("**/build/**", "**/src/generated/**")
            },
        )
    }
    kotlin {
        target(
            fileTree("projects") {
                include("**/src/**/*.kt")
                exclude("**/build/**", "**/src/generated/**")
            },
        )
    }
    kotlinGradle {
        target("*.gradle.kts", "projects/*/*.gradle.kts", "projects/*/*/*.gradle.kts")
    }
}
