@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.fabric")
    id("site.siredvin.publishing")
}

val testiariumVersion: String by extra
val is121 = sc.current.parsed >= "1.21"
val fabric = if (is121) libs.bundles.fabric121 else libs.bundles.fabric120
val ccFabric = if (is121) libs.bundles.ccfabric121 else libs.bundles.ccfabric120
val commonProject = project(":testiarium:${sc.current.project}")

java.toolchain.languageVersion.set(JavaLanguageVersion.of(if (is121) 21 else 17))

baseShaking {
    projectPart.set("fabric")
    projectName.set("testiarium")
    integrationRepositories.set(true)
    projectVersion.set(testiariumVersion)
    shake()
}

fabricShaking {
    commonProjectName.set("testiarium:${sc.current.project}")
    projectName.set("testiarium")
    shake()
}

val testMod = sourceSets.create("testMod") {
    resources.srcDir(rootProject.file("projects/testiarium/src/testMod/resources"))
    compileClasspath += sourceSets.main.get().compileClasspath
    compileClasspath += sourceSets.main.get().output
    compileClasspath += project(":testiarium:${sc.current.project}").sourceSets["testMod"].output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    runtimeClasspath += sourceSets.main.get().output
    runtimeClasspath += project(":testiarium:${sc.current.project}").sourceSets["testMod"].output
}

net.fabricmc.loom.configuration.RemapConfigurations.setupForSourceSet(project, testMod)

val cctTestMod = sourceSets.create("cctTestMod") {
    compileClasspath += testMod.compileClasspath
    compileClasspath += testMod.output
    runtimeClasspath += testMod.runtimeClasspath
    runtimeClasspath += testMod.output
    compileClasspath += project(":testiarium:${sc.current.project}").sourceSets["cctTestMod"].output
    runtimeClasspath += project(":testiarium:${sc.current.project}").sourceSets["cctTestMod"].output
}

net.fabricmc.loom.configuration.RemapConfigurations.setupForSourceSet(project, cctTestMod)

java.registerFeature("testMod") {
    usingSourceSet(testMod)
}

java.registerFeature("cctTestMod") {
    usingSourceSet(cctTestMod)
}

tasks.named<Jar>(testMod.jarTaskName) {
    from(project(":testiarium:${sc.current.project}").sourceSets["testMod"].output.classesDirs)
}

tasks.named<Jar>(cctTestMod.jarTaskName) {
    from(project(":testiarium:${sc.current.project}").sourceSets["cctTestMod"].output)
}

dependencies {
    configurations.implementation.get().dependencies.removeIf {
        it is org.gradle.api.artifacts.ProjectDependency && it.path == commonProject.path
    }
    implementation(commonProject.sourceSets.main.get().output)
    implementation(libs.bundles.kotlin)
    modImplementation(libs.bundles.fabric.core)
    modImplementation(fabric)
    add("modTestModImplementation", libs.bundles.kotlin)
    add("modTestModImplementation", libs.bundles.fabric.core)
    add("modTestModImplementation", fabric)
    add("modCctTestModImplementation", libs.bundles.kotlin)
    add("modCctTestModImplementation", libs.bundles.fabric.core)
    add("modCctTestModImplementation", fabric)
    add("modCctTestModImplementation", ccFabric)
}

loom {
    mods {
        register("testiarium-testmod") {
            sourceSet(sourceSets["testMod"])
            sourceSet(project(":testiarium:${sc.current.project}").sourceSets["testMod"])
        }
        register("testiarium-cct-testmod") {
            sourceSet(cctTestMod)
            sourceSet(project(":testiarium:${sc.current.project}").sourceSets["cctTestMod"])
        }
    }
    runs {
        named("server") {
            source(sourceSets["testMod"])
            property("fabric-api.gametest", "true")
            if (is121) property("fabric.debug.loadLate", "testiarium_testmod")
            property("testiarium.tags", "common")
            property("testiarium.structures", layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            property("testiarium.fixture-source", rootProject.file("projects/testiarium/src/testMod/resources/gameteststructures").absolutePath)
            property("testiarium.cct-fixtures", rootProject.file("projects/testiarium/src/cctTestMod/resources/computer").absolutePath)
            property("testiarium.gametest-report", layout.buildDirectory.file("test-results/gametest.xml").get().asFile.absolutePath)
            vmArg("-ea")
            runDir("run/gametest")
        }
        create("cctGameTest") {
            server()
            source(testMod)
            source(cctTestMod)
            property("fabric-api.gametest", "true")
            if (is121) property("fabric.debug.loadLate", "testiarium_cct_testmod")
            property("testiarium.tags", "common")
            property("testiarium.structures", layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            property("testiarium.fixture-source", rootProject.file("projects/testiarium/src/testMod/resources/gameteststructures").absolutePath)
            property("testiarium.cct-fixtures", rootProject.file("projects/testiarium/src/cctTestMod/resources/computer").absolutePath)
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

if (!is121) {
    tasks.named<JavaExec>("runServer") {
        executable(javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(17))
        }.get().executablePath.asFile.absolutePath)
    }
    tasks.named<JavaExec>("runCctGameTest") {
        executable(javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(17))
        }.get().executablePath.asFile.absolutePath)
    }
}

publishingShaking {
    projectVersion.set(testiariumVersion)
    shake()
}
