pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://mvn.siredvin.site/minecraft") {
            name = "SirEdvin's Minecraft repository"
            content {
                includeGroup("net.minecraftforge")
                includeGroup("net.minecraftforge.gradle")
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
        }
    }
}

val minecraftVersion: String by settings
rootProject.name = "Modding libs $minecraftVersion"

include(":broccolium-core")
//include(":broccolium-forge")
include(":broccolium-fabric")
include(":tweakium-core")
//include(":tweakium-forge")
include(":tweakium-fabric")
include(":peripheralium-core")
//include(":peripheralium-forge")
include(":peripheralium-fabric")


for (project in rootProject.children) {
    project.projectDir = file("projects/${project.name}")
}