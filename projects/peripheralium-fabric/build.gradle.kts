import site.siredvin.peripheralium.gradle.mavenDependencies

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.fabric")
    id("site.siredvin.publishing")
    id("site.siredvin.mod-publishing")
}

val peripheraliumVersion: String by extra
val tweakiumVersion: String by extra
val broccoliumVersion: String by extra
val minecraftVersion: String by extra

baseShaking {
    projectPart.set("fabric")
    projectName.set("peripheralium")
    projectVersion.set(peripheraliumVersion)
    integrationRepositories.set(true)
    shake()
}

fabricShaking {
    commonProjectName.set("peripheralium-core")
    projectName.set("peripheralium")
    accessWidener.set(project(":peripheralium-core").file("src/main/resources/peripheralium.accesswidener"))
    extraVersionMappings.set(
        mapOf(
            "computercraft" to "cc-tweaked",
        ),
    )
    extraRawVersionMappings.set(
        mapOf(
            "tweakium" to tweakiumVersion,
            "broccolium" to broccoliumVersion,
        ),
    )
    shake()
}

repositories {
    // location of the maven that hosts JEI files since January 2023
    maven {
        name = "Jared's maven"
        url = uri("https://maven.blamejared.com/")
        content {
            includeGroup("mezz.jei")
        }
    }
    maven {
        name = "ModMenu maven"
        url = uri("https://maven.terraformersmc.com/releases")
        content {
            includeGroup("com.terraformersmc")
        }
    }
}

dependencies {
    implementation(libs.bundles.kotlin)

    modImplementation(libs.bundles.fabric.core)
    modImplementation(libs.bundles.fabric)
    modImplementation(libs.bundles.ccfabric) {
        exclude("net.fabricmc.fabric-api")
        exclude("net.fabricmc", "fabric-loader")
    }

    modRuntimeOnly(libs.bundles.externalMods.fabric.runtime) {
        exclude("net.fabricmc.fabric-api")
        exclude("net.fabricmc", "fabric-loader")
    }

    implementation(project(":broccolium-fabric").dependencyProject.sourceSets.main.get().output)
    implementation(project(":tweakium-fabric").dependencyProject.sourceSets.main.get().output)

    testImplementation(kotlin("test-junit5"))
    testCompileOnly(libs.autoService)
    testAnnotationProcessor(libs.autoService)
    testImplementation(libs.byteBuddy)
    testImplementation(libs.byteBuddyAgent)
    testImplementation(libs.bundles.test)
}

tasks.test {
    dependsOn(tasks.generateDLIConfig)
    useJUnitPlatform()
    systemProperty("junit.jupiter.extensions.autodetection.enabled", true)
}

publishingShaking {
    projectVersion.set(peripheraliumVersion)
    shake()
    project.publishing {
        publications {
            named<MavenPublication>("maven") {
                mavenDependencies {
                    exclude(project.dependencies.create("site.siredvin:"))
                    exclude(libs.rei.fabric.get())
                }
            }
        }
    }
}
