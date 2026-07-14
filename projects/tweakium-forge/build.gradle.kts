import site.siredvin.peripheralium.gradle.mavenDependencies

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.publishing")
    id("site.siredvin.forge")
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

forgeShaking {
    commonProjectName.set("tweakium-core")
    projectName.set("tweakium")
    useAT.set(true)
    useMixins.set(true)
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

configurations.create("raw") {
    isCanBeConsumed = true
}

tasks.register<Jar>("rawJar") {
    dependsOn(tasks.named("jar"))
    archiveBaseName.set(archiveBaseName.get() + "-raw")
    archiveClassifier.set("raw")
    from(sourceSets["main"].output)
}

tasks.named("jar") { finalizedBy("rawJar") }

artifacts {
    add("raw", tasks["rawJar"]) {
        classifier = "raw"
    }
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.forge.raw)
    libs.bundles.forge.base.get().map { implementation(fg.deobf(it)) }
    libs.bundles.forge.cc.get().map { implementation(fg.deobf(it)) }

    libs.bundles.externalMods.forge.runtime.get().map { runtimeOnly(fg.deobf(it)) }

    implementation(project(":broccolium-forge", configuration = "raw"))

    testImplementation(kotlin("test"))
    testCompileOnly(libs.autoService)
    testAnnotationProcessor(libs.autoService)
    testImplementation(libs.byteBuddy)
    testImplementation(libs.byteBuddyAgent)
    testImplementation(libs.bundles.test)
    add(testMod.compileOnlyConfigurationName, project(":testiarium-forge"))
}

minecraft {
    runs {
        create("gameTestServer") {
            workingDirectory(file("run/peripheral-gametest"))
            property("forge.enabledGameTestNamespaces", "tweakium_testmod")
            property("testiarium.tags", "tweakium")
            property("testiarium.structures", layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            property("testiarium.fixture-source", project(":tweakium-core").file("src/testMod/resources/gameteststructures").absolutePath)
            property("testiarium.cct-fixtures", project(":tweakium-core").file("src/testMod/resources/computer").absolutePath)
            property("testiarium.gametest-report", layout.buildDirectory.file("test-results/peripheral-gametest.xml").get().asFile.absolutePath)
            jvmArgs("-ea")
            args("--nogui")
            mods {
                create("tweakium") { source(sourceSets.main.get()) }
                create("tweakium_testmod") {
                    source(testMod)
                    source(project(":tweakium-core").sourceSets["testMod"])
                }
                create("testiarium") {
                    source(project(":testiarium-forge").sourceSets.main.get())
                    source(project(":testiarium-core").sourceSets.main.get())
                }
                create("testiarium_testmod") {
                    source(project(":testiarium-forge").sourceSets["testMod"])
                    source(project(":testiarium-core").sourceSets["testMod"])
                }
                create("testiarium_cct_testmod") {
                    source(cctTestMod)
                    source(project(":testiarium-forge").sourceSets["cctTestMod"])
                    source(project(":testiarium-core").sourceSets["cctTestMod"])
                }
            }
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
                fg.component(this)
                mavenDependencies {
                    exclude(dependencies.create("site.siredvin:"))
                    exclude(libs.jei.forge.get())
                }
            }
        }
    }
}
