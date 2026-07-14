@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.publishing")
    id("site.siredvin.mod-publishing")
    id("site.siredvin.forge")
}

val testiariumVersion: String by extra
val cctTests = providers.gradleProperty("testiarium.cct").isPresent

baseShaking {
    projectPart.set("forge")
    projectName.set("testiarium")
    projectVersion.set(testiariumVersion)
    shake()
}

forgeShaking {
    commonProjectName.set("testiarium-core")
    projectName.set("testiarium")
    useAT.set(true)
    useMixins.set(true)
    shake()
}

sourceSets.create("testMod") {
    resources.srcDir(project(":testiarium-core").file("src/testMod/resources"))
    compileClasspath += sourceSets.main.get().compileClasspath
    compileClasspath += sourceSets.main.get().output
    compileClasspath += project(":testiarium-core").sourceSets["testMod"].output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    runtimeClasspath += sourceSets.main.get().output
    runtimeClasspath += project(":testiarium-core").sourceSets["testMod"].output
}

val cctTestMod = sourceSets.create("cctTestMod") {
    compileClasspath += sourceSets["testMod"].compileClasspath
    compileClasspath += sourceSets["testMod"].output
    runtimeClasspath += sourceSets["testMod"].runtimeClasspath
    runtimeClasspath += sourceSets["testMod"].output
    compileClasspath += project(":testiarium-core").sourceSets["cctTestMod"].output
    runtimeClasspath += project(":testiarium-core").sourceSets["cctTestMod"].output
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.forge.raw)
    libs.bundles.forge.base.get().map { implementation(fg.deobf(it)) }
    libs.bundles.forge.cc.get().map { add(cctTestMod.compileOnlyConfigurationName, fg.deobf(it)) }
    libs.bundles.forge.cc.get().map { add(cctTestMod.runtimeOnlyConfigurationName, fg.deobf(it)) }
}

minecraft {
    runs {
        create("gameTestServer") {
            workingDirectory(file("run/gametest"))
            property("forge.enabledGameTestNamespaces", if (cctTests) "testiarium_testmod,testiarium_cct_testmod" else "testiarium_testmod")
            property("testiarium.tags", "common")
            property("testiarium.structures", layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            property("testiarium.fixture-source", project(":testiarium-core").file("src/testMod/resources/gameteststructures").absolutePath)
            property("testiarium.cct-fixtures", project(":testiarium-core").file("src/cctTestMod/resources/computer").absolutePath)
            property("testiarium.gametest-report", layout.buildDirectory.file(if (cctTests) "test-results/cct-gametest.xml" else "test-results/gametest.xml").get().asFile.absolutePath)
            jvmArgs("-ea")
            args("--nogui")
            mods {
                create("testiarium") {
                    source(sourceSets.main.get())
                }
                create("testiarium_testmod") {
                    source(sourceSets["testMod"])
                    source(project(":testiarium-core").sourceSets["testMod"])
                }
                if (cctTests) {
                    create("testiarium_cct_testmod") {
                        source(cctTestMod)
                        source(project(":testiarium-core").sourceSets["cctTestMod"])
                    }
                }
            }
        }
        create("clientGameTest") {
            parent(runs.getByName("client"))
            workingDirectory(file("run/client-gametest"))
            property("forge.enabledGameTestNamespaces", "testiarium_testmod")
            property("testiarium.client", "true")
            property("testiarium.tags", "client")
            property("testiarium.structures", layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            property("testiarium.gametest-report", layout.buildDirectory.file("test-results/client-gametest.xml").get().asFile.absolutePath)
            property("testiarium.screenshots", layout.buildDirectory.get().asFile.absolutePath)
            jvmArgs("-ea")
            arg("--mixin.config=testiarium-testmod.mixins.json")
            mods {
                create("testiarium") {
                    source(sourceSets.main.get())
                }
                create("testiarium_testmod") {
                    source(sourceSets["testMod"])
                    source(project(":testiarium-core").sourceSets["testMod"])
                }
            }
        }
    }
}

publishingShaking {
    projectVersion.set(testiariumVersion)
    shake()
}
