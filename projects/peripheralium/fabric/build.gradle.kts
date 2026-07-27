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
val is121 = sc.current.parsed >= "1.21"
val ccTweakedVersion = if (is121) libs.versions.ccTweaked121.get() else libs.versions.ccTweaked120.get()
val fabric = if (is121) libs.bundles.fabric121 else libs.bundles.fabric120
val ccFabric = if (is121) libs.bundles.ccfabric121 else libs.bundles.ccfabric120
val runtimeMods = if (is121) libs.bundles.externalModsFabricRuntime121 else libs.bundles.externalModsFabricRuntime120
val rei = if (is121) libs.reiFabric121 else libs.reiFabric120
val commonProject = project(":peripheralium:${sc.current.project}")

baseShaking {
    projectPart.set("fabric")
    projectName.set("peripheralium")
    projectVersion.set(peripheraliumVersion)
    integrationRepositories.set(true)
    shake()
}

fabricShaking {
    commonProjectName.set("peripheralium:${sc.current.project}")
    projectName.set("peripheralium")
    accessWidener.set(project(":peripheralium").file("src/main/resources/peripheralium.accesswidener"))
    extraRawVersionMappings.set(
        mapOf(
            "computercraft" to ccTweakedVersion,
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
        name = if (is121) "Modrinth maven" else "ModMenu maven"
        url = uri(if (is121) "https://api.modrinth.com/maven" else "https://maven.terraformersmc.com/releases")
        content {
            includeGroup(if (is121) "maven.modrinth" else "com.terraformersmc")
        }
    }
}

dependencies {
    configurations.implementation.get().dependencies.removeIf {
        it is org.gradle.api.artifacts.ProjectDependency && it.path == commonProject.path
    }
    implementation(commonProject.sourceSets.main.get().output)
    implementation(libs.bundles.kotlin)

    modImplementation(libs.bundles.fabric.core)
    modImplementation(fabric)
    modImplementation(ccFabric) {
        exclude("net.fabricmc.fabric-api")
        exclude("net.fabricmc", "fabric-loader")
    }

    modRuntimeOnly(runtimeMods) {
        exclude("net.fabricmc.fabric-api")
        exclude("net.fabricmc", "fabric-loader")
    }

    implementation(project.project(":broccolium:fabric:${sc.current.project}").sourceSets.main.get().output)
    implementation(project.project(":tweakium:fabric:${sc.current.project}").sourceSets.main.get().output)

    testImplementation(kotlin(if (is121) "test-junit5" else "test"))
    testCompileOnly(libs.autoService)
    testAnnotationProcessor(libs.autoService)
    testImplementation(libs.byteBuddy)
    testImplementation(libs.byteBuddyAgent)
    testImplementation(libs.bundles.test)
}

tasks.test {
    useJUnitPlatform()
}

publishingShaking {
    projectVersion.set(peripheraliumVersion)
    shake()
    project.publishing {
        publications {
            named<MavenPublication>("maven") {
                mavenDependencies {
                    exclude(project.dependencies.create("site.siredvin:"))
                    exclude(rei.get())
                }
            }
        }
    }
}
