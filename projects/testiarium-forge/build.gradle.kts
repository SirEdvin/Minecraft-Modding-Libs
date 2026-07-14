@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.publishing")
    id("site.siredvin.mod-publishing")
    id("site.siredvin.neoforge")
}

val testiariumVersion: String by extra
val cctTests = providers.gradleProperty("testiarium.cct").isPresent

repositories {
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
        content {
            includeGroup("thedarkcolour")
        }
    }
}

baseShaking {
    projectPart.set("forge")
    projectName.set("testiarium")
    projectVersion.set(testiariumVersion)
    shake()
}

neoforgeShaking {
    commonProjectName.set("testiarium-core")
    projectName.set("testiarium")
    useAT.set(true)
    useRawJar.set(true)
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
    libs.bundles.forge.base.get().map { implementation(it) }
    libs.bundles.forge.cc.get().map { add(cctTestMod.compileOnlyConfigurationName, it) }
    libs.bundles.forge.cc.get().map { add(cctTestMod.runtimeOnlyConfigurationName, it) }
}

neoForge {
    val testiarium = mods.named("testiarium")
    val testMod by mods.registering {
        sourceSet(sourceSets["testMod"])
        sourceSet(project(":testiarium-core").sourceSets["testMod"])
    }
    val cctMod by mods.registering {
        sourceSet(cctTestMod)
        sourceSet(project(":testiarium-core").sourceSets["cctTestMod"])
    }
    runs {
        register("gameTestServer") {
            type = "gameTestServer"
            gameDirectory = file("run/gametest")
            systemProperty("testiarium.tags", "common")
            systemProperty("testiarium.structures", layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            systemProperty("testiarium.fixture-source", project.project(":testiarium-core").file("src/testMod/resources/gameteststructures").absolutePath)
            systemProperty("testiarium.cct-fixtures", project.project(":testiarium-core").file("src/cctTestMod/resources/computer").absolutePath)
            systemProperty("testiarium.gametest-report", layout.buildDirectory.file(if (cctTests) "test-results/cct-gametest.xml" else "test-results/gametest.xml").get().asFile.absolutePath)
            jvmArgument("-ea")
            programArgument("--nogui")
            loadedMods.add(testiarium.get())
            loadedMods.add(testMod.get())
            if (cctTests) loadedMods.add(cctMod.get())
        }
        register("clientGameTest") {
            client()
            gameDirectory = file("run/client-gametest")
            systemProperty("testiarium.client", "true")
            systemProperty("testiarium.tags", "client")
            systemProperty("testiarium.structures", layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            systemProperty("testiarium.gametest-report", layout.buildDirectory.file("test-results/client-gametest.xml").get().asFile.absolutePath)
            systemProperty("testiarium.screenshots", layout.buildDirectory.get().asFile.absolutePath)
            jvmArgument("-ea")
            programArgument("--mixin.config=testiarium-testmod.mixins.json")
            loadedMods.add(testiarium.get())
            loadedMods.add(testMod.get())
        }
    }
}

publishingShaking {
    projectVersion.set(testiariumVersion)
    shake()
}
