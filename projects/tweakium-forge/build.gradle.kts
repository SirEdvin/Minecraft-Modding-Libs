import site.siredvin.peripheralium.gradle.mavenDependencies

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.neoforge")
    id("site.siredvin.publishing")
    id("site.siredvin.mod-publishing")
    id("idea")
}

val tweakiumVersion: String by extra
val broccoliumVersion: String by extra

baseShaking {
    projectPart.set("forge")
    projectName.set("tweakium")
    projectVersion.set(tweakiumVersion)
    integrationRepositories.set(true)
    shake()
}

neoforgeShaking {
    projectName.set("tweakium")
    commonProjectName.set("tweakium-core")
    useAT.set(true)
    useRawJar.set(true)
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
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    runtimeClasspath += sourceSets.main.get().output
    runtimeClasspath += project(":tweakium-core").sourceSets["testMod"].output
    runtimeClasspath += project(":testiarium-core").sourceSets["testMod"].output
    runtimeClasspath += project(":testiarium-core").sourceSets["cctTestMod"].output
}

val cctTestMod = sourceSets.create("cctTestMod") {
    resources.srcDir(project(":testiarium-core").file("src/cctTestMod/resources"))
    compileClasspath += testMod.compileClasspath
    compileClasspath += project(":testiarium-core").sourceSets["cctTestMod"].output
    runtimeClasspath += testMod.runtimeClasspath
    runtimeClasspath += project(":testiarium-core").sourceSets["cctTestMod"].output
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
}

sourceSets {
    test {
        compileClasspath += sourceSets["main"].compileClasspath + sourceSets["main"].output
        runtimeClasspath += sourceSets["main"].runtimeClasspath + sourceSets["main"].output
        compileClasspath += project(":tweakium-core").sourceSets["main"].output
        runtimeClasspath += project(":tweakium-core").sourceSets["main"].output
        compileClasspath += project(":broccolium-core").sourceSets["main"].output
        runtimeClasspath += project(":broccolium-core").sourceSets["main"].output
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

repositories {
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
        content {
            includeGroup("thedarkcolour")
        }
    }
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.forge.raw)
    libs.bundles.forge.base.get().map { implementation(it) }
    libs.bundles.forge.cc.get().map { implementation(it) }

    compileOnly(project(":broccolium-core")) {
        exclude("cc.tweaked")
        exclude("fuzs.forgeconfigapiport")
        exclude("dan200.computercraft")
    }
    implementation(project(":broccolium-forge", configuration = "raw"))

    libs.bundles.externalMods.forge.runtime.get().map { runtimeOnly(it) }

    testImplementation(kotlin("test"))
    testCompileOnly(libs.autoService)
    testAnnotationProcessor(libs.autoService)
    testImplementation(libs.byteBuddy)
    testImplementation(libs.byteBuddyAgent)
    testImplementation(libs.bundles.test)
    add(testMod.compileOnlyConfigurationName, project(":testiarium-forge"))
}

neoForge {
    val tweakium = mods.named("tweakium")
    val tweakiumTestMod by mods.registering {
        sourceSet(testMod)
        sourceSet(project(":tweakium-core").sourceSets["testMod"])
    }
    val testiarium by mods.registering {
        sourceSet(project(":testiarium-forge").sourceSets.main.get())
        sourceSet(project(":testiarium-core").sourceSets.main.get())
    }
    val testiariumTestMod by mods.registering {
        sourceSet(project(":testiarium-forge").sourceSets["testMod"])
        sourceSet(project(":testiarium-core").sourceSets["testMod"])
    }
    val testiariumCctTestMod by mods.registering {
        sourceSet(cctTestMod)
        sourceSet(project(":testiarium-forge").sourceSets["cctTestMod"])
        sourceSet(project(":testiarium-core").sourceSets["cctTestMod"])
    }
    runs {
        register("gameTestServer") {
            type = "gameTestServer"
            gameDirectory = file("run/peripheral-gametest")
            systemProperty("testiarium.tags", "tweakium")
            systemProperty("testiarium.structures", layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            systemProperty("testiarium.fixture-source", project.project(":tweakium-core").file("src/testMod/resources/gameteststructures").absolutePath)
            systemProperty("testiarium.cct-fixtures", project.project(":tweakium-core").file("src/testMod/resources/computer").absolutePath)
            systemProperty("testiarium.gametest-report", layout.buildDirectory.file("test-results/peripheral-gametest.xml").get().asFile.absolutePath)
            jvmArgument("-ea")
            programArgument("--nogui")
            loadedMods.add(tweakium.get())
            loadedMods.add(tweakiumTestMod.get())
            loadedMods.add(testiarium.get())
            loadedMods.add(testiariumTestMod.get())
            loadedMods.add(testiariumCctTestMod.get())
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
                    exclude(dependencies.create("site.siredvin:"))
                    exclude(libs.jei.forge.get())
                }
            }
        }
    }
}
