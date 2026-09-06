import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

plugins {
    java
    alias(libs.plugins.loom) apply false
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

subprojects {
    if (projectDir.parentFile.name == "versions") {
        extra["minecraftVersion"] = name
        if (!path.contains(":forge:")) {
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
            extensions.getByType<SourceSetContainer>().configureEach {
                java.exclude(overridden)
                resources.exclude(overridden)
                allJava.exclude(overridden)
                allSource.exclude(overridden)
            }
        }
        pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            extensions.getByType<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension>().sourceSets.configureEach {
                kotlin.exclude(overridden)
            }
        }
        afterEvaluate {
            // Enable after Kotlin: enabling earlier publishes a competing kotlinSourcesJar.
            extensions.configure<JavaPluginExtension> { withSourcesJar() }
        }
        if (path.contains(":fabric:") || path.contains(":forge:")) {
            val commonProject = project(":${path.split(':')[1]}:$minecraftVersion")
            afterEvaluate {
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
