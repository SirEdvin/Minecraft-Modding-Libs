import site.siredvin.peripheralium.gradle.mavenDependencies

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.fabric")
    id("site.siredvin.publishing")
}

val tweakiumVersion: String by extra
val minecraftVersion: String by extra
val broccoliumVersion: String by extra
val is121 = sc.current.parsed >= "1.21"
val ccTweakedVersion = if (is121) libs.versions.ccTweaked121.get() else libs.versions.ccTweaked120.get()
val fabric = if (is121) libs.bundles.fabric121 else libs.bundles.fabric120
val ccFabric = if (is121) libs.bundles.ccfabric121 else libs.bundles.ccfabric120
val runtimeMods = if (is121) libs.bundles.externalModsFabricRuntime121 else libs.bundles.externalModsFabricRuntime120
val rei = if (is121) libs.reiFabric121 else libs.reiFabric120
val commonProject = project(":tweakium:${sc.current.project}")

baseShaking {
    projectPart.set("fabric")
    projectName.set("tweakium")
    projectVersion.set(tweakiumVersion)
    integrationRepositories.set(true)
    shake()
}

fabricShaking {
    commonProjectName.set("tweakium:${sc.current.project}")
    projectName.set("tweakium")
    accessWidener.set(project(":tweakium").file("versions/${sc.current.project}/src/main/resources/tweakium.accesswidener"))
    extraRawVersionMappings.set(
        mapOf(
            "computercraft" to ccTweakedVersion,
            "broccolium" to broccoliumVersion,
        ),
    )
    shake()
}

val testMod = sourceSets.create("testMod") {
    resources.srcDir(rootProject.file("projects/tweakium/src/testMod/resources"))
    compileClasspath += sourceSets.main.get().compileClasspath
    compileClasspath += sourceSets.main.get().output
    compileClasspath += project(":tweakium:${sc.current.project}").sourceSets["testMod"].output
    compileClasspath += project(":testiarium:${sc.current.project}").sourceSets["testMod"].output
    compileClasspath += project(":testiarium:${sc.current.project}").sourceSets["cctTestMod"].output
    compileClasspath += project(":testiarium:fabric:${sc.current.project}").sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    runtimeClasspath += sourceSets.main.get().output
    runtimeClasspath += project(":tweakium:${sc.current.project}").sourceSets["testMod"].output
    runtimeClasspath += project(":testiarium:${sc.current.project}").sourceSets["testMod"].output
    runtimeClasspath += project(":testiarium:${sc.current.project}").sourceSets["cctTestMod"].output
    runtimeClasspath += project(":testiarium:fabric:${sc.current.project}").sourceSets.main.get().output
}

net.fabricmc.loom.configuration.RemapConfigurations.setupForSourceSet(project, testMod)

sourceSets {
    create("testFixtures") {
        compileClasspath += main.get().compileClasspath
        compileClasspath += main.get().output
        runtimeClasspath += main.get().output
    }
    test {
        compileClasspath += project(":broccolium:${sc.current.project}").sourceSets["testFixtures"].output
        runtimeClasspath += project(":broccolium:${sc.current.project}").sourceSets["testFixtures"].output
        compileClasspath += sourceSets["testFixtures"].output
        runtimeClasspath += sourceSets["testFixtures"].output
    }
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

    implementation(project.project(":broccolium:fabric:${sc.current.project}").sourceSets.main.get().output)

    modRuntimeOnly(runtimeMods) {
        exclude("net.fabricmc.fabric-api")
        exclude("net.fabricmc", "fabric-loader")
    }

    add(sourceSets["testFixtures"].compileOnlyConfigurationName, kotlin("test"))
    add(sourceSets["testFixtures"].compileOnlyConfigurationName, libs.bundles.test)

    testImplementation(kotlin("test"))
    testCompileOnly(libs.autoService)
    testAnnotationProcessor(libs.autoService)
    testImplementation(libs.byteBuddy)
    testImplementation(libs.byteBuddyAgent)
    testImplementation(libs.bundles.test)
    add("modTestModImplementation", libs.bundles.fabric.core)
    add("modTestModImplementation", fabric)
    add("modTestModImplementation", ccFabric)
}

loom {
    mods {
        register("tweakium-testmod") {
            sourceSet(testMod)
        }
    }
    runs {
        create("peripheralGameTest") {
            server()
            source(testMod)
            property("fabric-api.gametest", "true")
            if (is121) property("fabric.debug.loadLate", "testiarium_testmod")
            property("testiarium.tags", "tweakium")
            property("testiarium.structures", layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            property("testiarium.fixture-source", rootProject.file("projects/tweakium/src/testMod/resources/gameteststructures").absolutePath)
            property("testiarium.cct-fixtures", rootProject.file("projects/tweakium/src/testMod/resources/computer").absolutePath)
            property("testiarium.gametest-report", layout.buildDirectory.file("test-results/peripheral-gametest.xml").get().asFile.absolutePath)
            vmArg("-ea")
            runDir("run/peripheral-gametest")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

publishingShaking {
    projectVersion.set(tweakiumVersion)
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
