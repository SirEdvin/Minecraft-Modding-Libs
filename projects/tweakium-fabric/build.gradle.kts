import site.siredvin.peripheralium.gradle.mavenDependencies

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.fabric")
    id("site.siredvin.publishing")
}

val tweakiumVersion: String by extra
val minecraftVersion: String by extra
val broccoliumVersion: String by extra

baseShaking {
    projectPart.set("fabric")
    projectName.set("tweakium")
    projectVersion.set(tweakiumVersion)
    integrationRepositories.set(true)
    shake()
}

fabricShaking {
    commonProjectName.set("tweakium-core")
    projectName.set("tweakium")
    accessWidener.set(project(":tweakium-core").file("src/main/resources/tweakium.accesswidener"))
    extraVersionMappings.set(
        mapOf(
            "computercraft" to "cc-tweaked",
        ),
    )
    extraRawVersionMappings.set(
        mapOf(
            "broccolium" to broccoliumVersion,
        ),
    )
    shake()
}

val testMod = sourceSets.create("testMod") {
    resources.srcDir(project(":tweakium-core").file("src/testMod/resources"))
    compileClasspath += sourceSets.main.get().compileClasspath
    compileClasspath += sourceSets.main.get().output
    compileClasspath += project(":tweakium-core").sourceSets["testMod"].output
    compileClasspath += project(":testiarium-core").sourceSets["testMod"].output
    compileClasspath += project(":testiarium-core").sourceSets["cctTestMod"].output
    compileClasspath += project(":testiarium-fabric").sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    runtimeClasspath += sourceSets.main.get().output
    runtimeClasspath += project(":tweakium-core").sourceSets["testMod"].output
    runtimeClasspath += project(":testiarium-core").sourceSets["testMod"].output
    runtimeClasspath += project(":testiarium-core").sourceSets["cctTestMod"].output
    runtimeClasspath += project(":testiarium-fabric").sourceSets.main.get().output
}

net.fabricmc.loom.configuration.RemapConfigurations.setupForSourceSet(project, testMod)

sourceSets {
    create("testFixtures") {
        compileClasspath += main.get().compileClasspath
        compileClasspath += main.get().output
        runtimeClasspath += main.get().output
    }
    test {
        compileClasspath += project(":broccolium-core").sourceSets["testFixtures"].output
        runtimeClasspath += project(":broccolium-core").sourceSets["testFixtures"].output
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

    implementation(rootProject.project(":broccolium-fabric").sourceSets.main.get().output)

    modRuntimeOnly(libs.bundles.externalMods.fabric.runtime) {
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
    add("modTestModImplementation", libs.bundles.fabric)
    add("modTestModImplementation", libs.bundles.ccfabric)
}

loom {
    mods {
        register("testiarium") {
            sourceSet(project(":testiarium-fabric").sourceSets.main.get())
            sourceSet(project(":testiarium-core").sourceSets.main.get())
        }
        register("tweakium-testmod") {
            sourceSet(testMod)
            sourceSet(project(":tweakium-core").sourceSets["testMod"])
        }
        register("testiarium-testmod") {
            sourceSet(project(":testiarium-fabric").sourceSets["testMod"])
            sourceSet(project(":testiarium-core").sourceSets["testMod"])
            sourceSet(project(":testiarium-core").sourceSets["cctTestMod"])
        }
    }
    runs {
        create("peripheralGameTest") {
            server()
            source(testMod)
            property("fabric-api.gametest", "true")
            property("fabric.debug.loadLate", "testiarium_testmod")
            property("testiarium.tags", "tweakium")
            property("testiarium.structures", layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            property("testiarium.fixture-source", project(":tweakium-core").file("src/testMod/resources/gameteststructures").absolutePath)
            property("testiarium.cct-fixtures", project(":tweakium-core").file("src/testMod/resources/computer").absolutePath)
            property("testiarium.gametest-report", layout.buildDirectory.file("test-results/peripheral-gametest.xml").get().asFile.absolutePath)
            vmArg("-ea")
            runDir("run/peripheral-gametest")
        }
    }
}

tasks.test {
    dependsOn(tasks.generateDLIConfig)
    useJUnitPlatform()
    systemProperty("junit.jupiter.extensions.autodetection.enabled", true)
}

publishingShaking {
    projectVersion.set(tweakiumVersion)
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
