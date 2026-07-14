@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.fabric")
    id("site.siredvin.publishing")
}

val testiariumVersion: String by extra

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

baseShaking {
    projectPart.set("fabric")
    projectName.set("testiarium")
    integrationRepositories.set(true)
    projectVersion.set(testiariumVersion)
    shake()
}

fabricShaking {
    commonProjectName.set("testiarium-core")
    projectName.set("testiarium")
    shake()
}

val testMod = sourceSets.create("testMod") {
    resources.srcDir(project(":testiarium-core").file("src/testMod/resources"))
    compileClasspath += sourceSets.main.get().compileClasspath
    compileClasspath += sourceSets.main.get().output
    compileClasspath += project(":testiarium-core").sourceSets["testMod"].output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    runtimeClasspath += sourceSets.main.get().output
    runtimeClasspath += project(":testiarium-core").sourceSets["testMod"].output
}

net.fabricmc.loom.configuration.RemapConfigurations.setupForSourceSet(project, testMod)

val cctTestMod = sourceSets.create("cctTestMod") {
    compileClasspath += testMod.compileClasspath
    compileClasspath += testMod.output
    runtimeClasspath += testMod.runtimeClasspath
    runtimeClasspath += testMod.output
    compileClasspath += project(":testiarium-core").sourceSets["cctTestMod"].output
    runtimeClasspath += project(":testiarium-core").sourceSets["cctTestMod"].output
}

net.fabricmc.loom.configuration.RemapConfigurations.setupForSourceSet(project, cctTestMod)

java.registerFeature("testMod") {
    usingSourceSet(testMod)
}

java.registerFeature("cctTestMod") {
    usingSourceSet(cctTestMod)
}

dependencies {
    implementation(libs.bundles.kotlin)
    modImplementation(libs.bundles.fabric.core)
    modImplementation(libs.bundles.fabric)
    add("modTestModImplementation", libs.bundles.kotlin)
    add("modTestModImplementation", libs.bundles.fabric.core)
    add("modTestModImplementation", libs.bundles.fabric)
    add("modCctTestModImplementation", libs.bundles.kotlin)
    add("modCctTestModImplementation", libs.bundles.fabric.core)
    add("modCctTestModImplementation", libs.bundles.fabric)
    add("modCctTestModImplementation", libs.bundles.ccfabric)
}

loom {
    mods {
        register("testiarium-testmod") {
            sourceSet(sourceSets["testMod"])
            sourceSet(project(":testiarium-core").sourceSets["testMod"])
        }
        register("testiarium-cct-testmod") {
            sourceSet(cctTestMod)
            sourceSet(project(":testiarium-core").sourceSets["cctTestMod"])
        }
    }
    runs {
        named("server") {
            source(sourceSets["testMod"])
            property("fabric-api.gametest", "true")
            property("fabric.debug.loadLate", "testiarium_testmod")
            property("testiarium.tags", "common")
            property("testiarium.structures", layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            property("testiarium.fixture-source", project(":testiarium-core").file("src/testMod/resources/gameteststructures").absolutePath)
            property("testiarium.cct-fixtures", project(":testiarium-core").file("src/cctTestMod/resources/computer").absolutePath)
            property("testiarium.gametest-report", layout.buildDirectory.file("test-results/gametest.xml").get().asFile.absolutePath)
            vmArg("-ea")
            runDir("run/gametest")
        }
        create("cctGameTest") {
            server()
            source(cctTestMod)
            property("fabric-api.gametest", "true")
            property("fabric.debug.loadLate", "testiarium_testmod")
            property("testiarium.tags", "common")
            property("testiarium.structures", layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            property("testiarium.fixture-source", project(":testiarium-core").file("src/testMod/resources/gameteststructures").absolutePath)
            property("testiarium.cct-fixtures", project(":testiarium-core").file("src/cctTestMod/resources/computer").absolutePath)
            property("testiarium.gametest-report", layout.buildDirectory.file("test-results/cct-gametest.xml").get().asFile.absolutePath)
            vmArg("-ea")
            runDir("run/cct-gametest")
        }
        create("clientGameTest") {
            client()
            source(testMod)
            property("testiarium.client", "true")
            property("testiarium.tags", "client")
            property("testiarium.structures", layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            property("testiarium.gametest-report", layout.buildDirectory.file("test-results/client-gametest.xml").get().asFile.absolutePath)
            property("testiarium.screenshots", layout.buildDirectory.get().asFile.absolutePath)
            vmArg("-ea")
            runDir("run/client-gametest")
        }
    }
}

publishingShaking {
    projectVersion.set(testiariumVersion)
    shake()
}
