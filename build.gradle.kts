import java.text.SimpleDateFormat
import java.util.*
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

plugins {
    java
    alias(libs.plugins.loom) apply false
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
val broccoliumVersion: String by project.extra
val testiariumVersion: String by project.extra

subprojects {
    setupSubproject(this)
    val module = this

    if (name.endsWith("-core")) {
        pluginManager.apply("net.fabricmc.fabric-loom-companion")
    }

    pluginManager.withPlugin("maven-publish") {
        extensions.configure<PublishingExtension> {
            publications.withType<MavenPublication>().configureEach {
                pom {
                    name.set(module.name)
                    description.set("Minecraft 1.21.1 library module ${module.name}")
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
//
//githubShaking {
//    modBranch.set("1.20")
//    projectRepo.set("Minecraft-Modding-Libs")
//    projectVersion.set(broccoliumVersion)
//    shake()
//}

repositories {
    mavenCentral()
}
