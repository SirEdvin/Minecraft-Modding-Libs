import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

plugins {
    java
    alias(libs.plugins.loom) apply false
    id("net.neoforged.moddev.legacyforge") version "2.0.142" apply false
    id("site.siredvin.root") version "0.9.0"
    id("site.siredvin.release") version "0.9.0"
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
            // ponytail: Spotless rejects Stonecutter's shared source directories outside leaf projects.
            tasks.matching { it.name.startsWith("spotless") }.configureEach { enabled = false }
            if (!path.contains(":fabric:")) {
                pluginManager.apply("net.fabricmc.fabric-loom-companion")
            }
        }

        val minecraftVersion = name
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
