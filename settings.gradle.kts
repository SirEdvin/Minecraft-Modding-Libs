pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://mvn.siredvin.site/minecraft") {
            name = "SirEdvin's Minecraft repository"
            content {
                includeGroup("net.minecraftforge")
                includeGroup("net.minecraftforge.gradle")
                includeGroup("net.neoforged")
                includeGroup("net.neoforged.moddev")
                includeGroup("org.parchmentmc")
                includeGroup("org.parchmentmc.feather")
                includeGroup("org.parchmentmc.data")
                includeGroup("org.spongepowered")
                includeGroup("org.spongepowered.gradle.vanilla")
                includeGroup("net.fabricmc")
                includeGroup("fabric-loom")
                includeGroupByRegex("site.siredvin.*")
            }
        }
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.spongepowered.mixin") {
                useModule("org.spongepowered:mixingradle:${requested.version}")
            }
            if (requested.id.id.startsWith("site.siredvin.")) {
                useModule("site.siredvin:modding-buildenv:${requested.version}")
            }
        }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.8"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "Minecraft Modding Libs"

include(":broccolium")
include(":testiarium")
include(":tweakium")
include(":peripheralium")
include(":typed-peripheral-api")

for (project in rootProject.children) {
    project.projectDir = file("projects/${project.name}")
}

stonecutter {
    create(
        "broccolium",
        "testiarium",
        "tweakium",
        "peripheralium",
    ) {
        versions("1.20.1", "1.21.1", "26.1.2")
        branch("fabric")
        branch("forge")
        vcsVersion = "1.20.1"
    }
}

// Unobfuscated leaves must not apply the legacy remapping conventions.
for (library in listOf("broccolium", "testiarium", "tweakium", "peripheralium")) {
    for (branch in listOf("", ":fabric", ":forge")) {
        val target = project(":$library$branch:26.1.2")
        target.buildFileName = rootDir.resolve("gradle/minecraft-26-1.gradle.kts")
            .relativeTo(target.projectDir).path
    }
}
