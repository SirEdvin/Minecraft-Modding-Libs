import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm")
}

// Only the exact unobfuscated target is routed here by settings.gradle.kts.
require(project.name == "26.1.2")
val library = path.split(':')[1]
val loader = when {
    path.contains(":fabric:") -> "fabric"
    path.contains(":forge:") -> "forge"
    else -> "common"
}
val minecraftVersion = "26.1.2"
val libraryVersion = providers.gradleProperty("${library}Version").get()
version = libraryVersion
base.archivesName.set("$library-${if (library == "testiarium" && loader == "common") "core" else loader}-$minecraftVersion")
extra["releaseJar"] = "jar"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    withSourcesJar()
}
extensions.configure<KotlinJvmProjectExtension> {
    jvmToolchain(25)
}
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
}
tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_25)
    compilerOptions.allWarningsAsErrors.set(true)
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.squiddev.cc/")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
    maven("https://api.modrinth.com/maven")
    maven("https://maven.shedaniel.me/")
    maven("https://maven.blamejared.com/")
}

if (loader == "forge") {
    apply(plugin = "net.neoforged.moddev")
    extensions.configure<NeoForgeExtension> {
        version = "26.1.2.122"
    }
} else {
    apply(plugin = "net.fabricmc.fabric-loom")
    // No mappings/remapping configuration: target Minecraft uses official names.
    dependencies.add("minecraft", "com.mojang:minecraft:$minecraftVersion")
    val targetAccessWidener = rootProject.file("projects/$library/versions/$minecraftVersion/src/main/resources/$library-common.accesswidener")
    if (targetAccessWidener.exists()) {
        extensions.configure<LoomGradleExtensionAPI> {
            accessWidenerPath.set(targetAccessWidener)
        }
    }
}

// The approved 26.1 viewer is development-only, never part of published variants.
// NeoForge discovers mods from runtimeClasspath, not clientAdditionalRuntimeClasspath.
if (loader == "fabric") {
    dependencies.add("localRuntime", libs.jei261.fabric)
} else if (loader == "forge") {
    val localRuntime = configurations.create("localRuntime") {
        isCanBeConsumed = false
        isCanBeResolved = false
    }
    configurations.named("runtimeClasspath") { extendsFrom(localRuntime) }
    dependencies.add(localRuntime.name, libs.jei261.neoforge)
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:atomicfu-jvm:0.20.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:2.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.9.2")
    testImplementation("net.bytebuddy:byte-buddy:1.17.8")
    testImplementation("net.bytebuddy:byte-buddy-agent:1.17.8")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("net.jqwik:jqwik-api:1.7.2")
    testRuntimeOnly("net.jqwik:jqwik-engine:1.7.2")
    if (loader == "fabric") {
        implementation("net.fabricmc:fabric-loader:0.19.5")
        implementation("net.fabricmc.fabric-api:fabric-api:0.155.3+26.1.2")
        implementation("net.fabricmc:fabric-language-kotlin:1.13.12+kotlin.2.4.0")
        testImplementation("net.fabricmc:fabric-loader-junit:0.19.5")
        api("teamreborn:energy:5.0.0")
    } else if (loader == "forge") {
        implementation("thedarkcolour:kotlinforforge-neoforge:6.4.0")
    } else {
        compileOnly("org.spongepowered:mixin:0.8.7")
    }
    if (library == "tweakium" || library == "peripheralium") {
        api(project(":broccolium${if (loader == "common") "" else ":$loader"}:$minecraftVersion"))
    }
    if (library == "peripheralium") {
        api(project(":tweakium${if (loader == "common") "" else ":$loader"}:$minecraftVersion"))
        if (loader != "common") {
            api(project(":broccolium:$minecraftVersion"))
            api(project(":tweakium:$minecraftVersion"))
        }
    }
    if (library == "tweakium") {
        if (loader != "common") {
            api(project(":broccolium:$minecraftVersion"))
        }
        if (loader == "common") {
            for (part in listOf("common", "common-api", "core")) {
                compileOnly("cc.tweaked:cc-tweaked-$minecraftVersion-$part:1.120.0")
            }
        } else {
            implementation("cc.tweaked:cc-tweaked-$minecraftVersion-$loader:1.120.0")
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

}

if (library == "tweakium" && loader == "common") {
    val broccolium = project(":broccolium:$minecraftVersion")
    evaluationDependsOn(broccolium.path)
    val fixtures = broccolium.extensions.getByType<SourceSetContainer>().named("testFixtures")
    sourceSets.test {
        compileClasspath += fixtures.get().output
        runtimeClasspath += fixtures.get().output
    }
    for (part in listOf("common", "common-api", "core")) {
        dependencies.add("testImplementation", "cc.tweaked:cc-tweaked-$minecraftVersion-$part:1.120.0")
    }
}

if (library == "broccolium" && loader == "common") {
    val fixtures = sourceSets.create("testFixtures") {
        compileClasspath += sourceSets.main.get().compileClasspath
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
    sourceSets.test {
        compileClasspath += fixtures.output
        runtimeClasspath += fixtures.output
    }
    dependencies.add(fixtures.compileOnlyConfigurationName, "org.jetbrains.kotlin:kotlin-test-junit5:2.4.0")
    dependencies.add(fixtures.compileOnlyConfigurationName, "org.junit.jupiter:junit-jupiter:5.9.2")
    dependencies.add(fixtures.compileOnlyConfigurationName, "org.hamcrest:hamcrest:2.2")
    dependencies.add(fixtures.compileOnlyConfigurationName, "net.jqwik:jqwik-api:1.7.2")
    java.registerFeature("testFixtures") {
        usingSourceSet(fixtures)
        disablePublication()
    }
}

tasks.named<ProcessResources>("processResources") {
    inputs.property("version", libraryVersion)
    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml")) {
        expand(
            "version" to libraryVersion,
            "broccoliumVersion" to providers.gradleProperty("broccoliumVersion").get(),
            "tweakiumVersion" to providers.gradleProperty("tweakiumVersion").get(),
        )
    }
    exclude(".cache")
    // The canonical common resource is a legacy named-namespace widener. The
    // target loader owns the merged official widener; do not shadow it at runtime.
    if (library == "broccolium" && loader == "common") exclude("broccolium.accesswidener")
    if (library == "peripheralium" && loader == "common") exclude("peripheralium.accesswidener")
}

if (loader != "common") {
    val commonProject = project(":$library:$minecraftVersion")
    evaluationDependsOn(commonProject.path)
    val commonSourceSets = commonProject.extensions.getByType<SourceSetContainer>()
    dependencies.add("implementation", commonSourceSets.named("main").map { it.output })
    if (library == "broccolium") {
        sourceSets.test {
            compileClasspath += commonSourceSets.named("testFixtures").get().output
            runtimeClasspath += commonSourceSets.named("testFixtures").get().output
        }
        if (loader == "forge") {
            extensions.configure<NeoForgeExtension> {
                val tested = mods.create(library) {
                    sourceSet(sourceSets.main.get())
                    sourceSet(commonSourceSets.named("main").get())
                    sourceSet(commonSourceSets.named("testFixtures").get())
                }
                unitTest {
                    enable()
                    testedMod.set(tested)
                }
            }
        }
    }
    tasks.named<Jar>("jar") {
        from(commonProject.extensions.getByType<SourceSetContainer>().named("main").map { it.output })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

if (library == "broccolium") {
    val testiarium = project(":testiarium${if (loader == "common") "" else ":$loader"}:$minecraftVersion")
    evaluationDependsOn(testiarium.path)
    val harnessSets = testiarium.extensions.getByType<SourceSetContainer>()
    val common = if (loader == "common") project else project(":broccolium:$minecraftVersion")
    val commonSets = common.extensions.getByType<SourceSetContainer>()
    val testMod = sourceSets.create("testMod") {
        compileClasspath += sourceSets.main.get().compileClasspath + sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().runtimeClasspath + sourceSets.main.get().output
        for (name in listOf("main", "testMod")) {
            compileClasspath += harnessSets.named(name).get().output
            runtimeClasspath += harnessSets.named(name).get().output
        }
        if (loader != "common") {
            val coreHarness = project(":testiarium:$minecraftVersion").extensions.getByType<SourceSetContainer>()
            for (name in listOf("main", "testMod")) {
                compileClasspath += coreHarness.named(name).get().output
                runtimeClasspath += coreHarness.named(name).get().output
            }
            compileClasspath += commonSets.named("testMod").get().output
            runtimeClasspath += commonSets.named("testMod").get().output
        }
    }
    configurations.named(testMod.implementationConfigurationName) { extendsFrom(configurations.implementation.get()) }
    if (loader != "common") {
        val coreHarness = project(":testiarium:$minecraftVersion").extensions.getByType<SourceSetContainer>()
        val report = layout.buildDirectory.file("test-results/storage-gametest.xml").get().asFile.absolutePath
        val structures = rootProject.file("projects/testiarium/src/testMod/resources/gameteststructures").absolutePath
        val tags = "broccolium_reverse,broccolium_slots,broccolium_integrations,broccolium_native"
        val generated = rootProject.file("projects/broccolium/$loader/versions/$minecraftVersion/src/generated/resources")
        sourceSets.main.get().resources.srcDir(generated)
        if (loader == "fabric") extensions.configure<LoomGradleExtensionAPI> {
            mods {
                register("broccolium") { sourceSet(sourceSets.main.get()); sourceSet(commonSets.named("main").get()) }
                register("broccolium_testmod") { sourceSet(testMod); sourceSet(commonSets.named("testMod").get()) }
                register("testiarium") { sourceSet(harnessSets.named("main").get()); sourceSet(coreHarness.named("main").get()) }
                register("testiarium_testmod") { sourceSet(harnessSets.named("testMod").get()); sourceSet(coreHarness.named("testMod").get()) }
            }
            runs.create("datagen") {
                client(); source(sourceSets.main.get())
                property("fabric-api.datagen", "true")
                property("fabric-api.datagen.modid", "broccolium")
                property("fabric-api.datagen.output-dir", generated.absolutePath)
                runDir("run/datagen")
            }
            runs.create("storageGameTest") {
                server(); source(testMod)
                property("testiarium.fixture.consumer", "true")
                property("fabric-api.gametest", "true")
                property("testiarium.tags", tags)
                property("testiarium.structures", structures)
                property("testiarium.gametest-report", report)
                vmArg("-ea")
                runDir("run/storage-gametest")
            }
        } else extensions.configure<NeoForgeExtension> {
            val production = mods.getByName("broccolium")
            val fixture = mods.create("broccolium_testmod") { sourceSet(testMod); sourceSet(commonSets.named("testMod").get()) }
            val harness = mods.create("testiarium") { sourceSet(harnessSets.named("main").get()); sourceSet(coreHarness.named("main").get()) }
            val generic = mods.create("testiarium_testmod") { sourceSet(harnessSets.named("testMod").get()); sourceSet(coreHarness.named("testMod").get()) }
            for (clientData in listOf(true, false)) runs.create(if (clientData) "clientData" else "serverData") {
                if (clientData) clientData() else serverData()
                sourceSet.set(sourceSets.main.get())
                loadedMods.add(production)
                programArguments.addAll("--mod", "broccolium", "--output", generated.absolutePath)
                gameDirectory = file("run/datagen")
            }
            runs.create("storageGameTest") {
                type = "gameTestServer"
                sourceSet.set(testMod)
                loadedMods.addAll(production, fixture, harness, generic)
                systemProperty("testiarium.tags", tags)
                systemProperty("testiarium.structures", structures)
                systemProperty("testiarium.gametest-report", report)
                jvmArgument("-ea")
                gameDirectory = file("run/storage-gametest")
            }
        }
    }
}

if (library == "tweakium") {
    val harness = project(":testiarium${if (loader == "common") "" else ":$loader"}:$minecraftVersion")
    evaluationDependsOn(harness.path)
    val harnessSets = harness.extensions.getByType<SourceSetContainer>()
    val coreHarness = project(":testiarium:$minecraftVersion").extensions.getByType<SourceSetContainer>()
    val common = if (loader == "common") project else project(":tweakium:$minecraftVersion")
    val commonSets = common.extensions.getByType<SourceSetContainer>()
    val testMod = sourceSets.create("testMod") {
        compileClasspath += sourceSets.main.get().compileClasspath + sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().runtimeClasspath + sourceSets.main.get().output
        for (sets in listOf(harnessSets, coreHarness).distinct()) {
            for (name in listOf("main", "testMod", "cctTestMod")) {
                compileClasspath += sets.named(name).get().output
                runtimeClasspath += sets.named(name).get().output
            }
        }
        if (loader != "common") {
            compileClasspath += commonSets.named("testMod").get().output
            runtimeClasspath += commonSets.named("testMod").get().output
            resources.srcDir(rootProject.file("projects/tweakium/src/testMod/resources"))
            resources.srcDir(rootProject.file("projects/tweakium/versions/$minecraftVersion/src/testMod/resources"))
        }
    }
    configurations.named(testMod.implementationConfigurationName) { extendsFrom(configurations.implementation.get()) }
    if (loader == "common") {
        for (part in listOf("common", "common-api", "core")) {
            dependencies.add(testMod.compileOnlyConfigurationName, "cc.tweaked:cc-tweaked-$minecraftVersion-$part:1.120.0")
        }
    } else {
        tasks.named<ProcessResources>(testMod.processResourcesTaskName) {
            from(rootProject.file("projects/tweakium/src/testMod/resources/gameteststructures")) {
                into("data/minecraft/structure")
            }
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
        if (loader == "fabric") {
            val fixtures = sourceSets.create("testFixtures") {
                compileClasspath += sourceSets.main.get().compileClasspath + sourceSets.main.get().output
            }
            dependencies.add(fixtures.compileOnlyConfigurationName, "org.junit.jupiter:junit-jupiter:5.9.2")
            sourceSets.test { compileClasspath += fixtures.output; runtimeClasspath += fixtures.output }
        }
        val broccolium = project(":broccolium:$loader:$minecraftVersion")
        val brocSets = broccolium.extensions.getByType<SourceSetContainer>()
        val brocCore = project(":broccolium:$minecraftVersion").extensions.getByType<SourceSetContainer>()
        val report = layout.buildDirectory.file("test-results/peripheral-gametest.xml").get().asFile.absolutePath
        val structures = layout.buildDirectory.dir("resources/testMod").get().asFile.absolutePath
        val computers = rootProject.file("projects/tweakium/src/testMod/resources/computer").absolutePath
        val generated = rootProject.file("projects/tweakium/$loader/versions/$minecraftVersion/src/generated/resources")
        sourceSets.main.get().resources.srcDir(generated)
        sourceSets.test {
            val brocFixtures = brocCore.named("testFixtures").get().output
            compileClasspath += brocFixtures
            runtimeClasspath += brocFixtures
        }
        if (loader == "fabric") extensions.configure<LoomGradleExtensionAPI> {
            mods {
                register("tweakium") { sourceSet(sourceSets.main.get()); sourceSet(commonSets.named("main").get()) }
                register("tweakium_testmod") { sourceSet(testMod); sourceSet(commonSets.named("testMod").get()) }
                register("broccolium") { sourceSet(brocSets.named("main").get()); sourceSet(brocCore.named("main").get()) }
                register("testiarium") { sourceSet(harnessSets.named("main").get()); sourceSet(coreHarness.named("main").get()) }
                register("testiarium_testmod") { sourceSet(harnessSets.named("testMod").get()); sourceSet(coreHarness.named("testMod").get()) }
                register("testiarium_cct_testmod") { sourceSet(harnessSets.named("cctTestMod").get()); sourceSet(coreHarness.named("cctTestMod").get()) }
            }
            runs.create("peripheralGameTest") {
                server(); source(testMod)
                property("testiarium.fixture.consumer", "true")
                property("fabric-api.gametest", "true")
                property("testiarium.tags", "tweakium")
                property("testiarium.structures", structures)
                property("testiarium.cct-fixtures", computers)
                property("testiarium.gametest-report", report)
                vmArg("-ea")
                runDir("run/peripheral-gametest")
            }
            runs.create("datagen") {
                client(); source(sourceSets.main.get())
                property("fabric-api.datagen", "true")
                property("fabric-api.datagen.modid", "tweakium")
                property("fabric-api.datagen.output-dir", generated.absolutePath)
                runDir("run/datagen")
            }
            runs.create("upgradeDatagen") {
                client(); source(testMod)
                property("testiarium.fixture.consumer", "true")
                property("fabric-api.datagen", "true")
                property("fabric-api.datagen.modid", "tweakium_testmod")
                property("fabric-api.datagen.output-dir", layout.buildDirectory.dir("upgrade-datagen").get().asFile.absolutePath)
                runDir("run/upgrade-datagen")
            }
        } else extensions.configure<NeoForgeExtension> {
            val production = mods.create("tweakium") { sourceSet(sourceSets.main.get()); sourceSet(commonSets.named("main").get()) }
            val fixture = mods.create("tweakium_testmod") { sourceSet(testMod); sourceSet(commonSets.named("testMod").get()) }
            val base = mods.create("broccolium") { sourceSet(brocSets.named("main").get()); sourceSet(brocCore.named("main").get()) }
            val core = mods.create("testiarium") { sourceSet(harnessSets.named("main").get()); sourceSet(coreHarness.named("main").get()) }
            val generic = mods.create("testiarium_testmod") { sourceSet(harnessSets.named("testMod").get()); sourceSet(coreHarness.named("testMod").get()) }
            val cct = mods.create("testiarium_cct_testmod") { sourceSet(harnessSets.named("cctTestMod").get()); sourceSet(coreHarness.named("cctTestMod").get()) }
            unitTest { enable(); testedMod.set(production) }
            runs.create("gameTestServer") {
                type = "gameTestServer"
                sourceSet.set(testMod)
                loadedMods.addAll(production, fixture, base, core, generic, cct)
                systemProperty("testiarium.tags", "tweakium")
                systemProperty("testiarium.structures", structures)
                systemProperty("testiarium.cct-fixtures", computers)
                systemProperty("testiarium.gametest-report", report)
                jvmArgument("-ea")
                gameDirectory = file("run/peripheral-gametest")
            }
            for (clientData in listOf(true, false)) runs.create(if (clientData) "clientData" else "serverData") {
                if (clientData) clientData() else serverData()
                sourceSet.set(sourceSets.main.get())
                loadedMods.addAll(production, base)
                programArguments.addAll("--mod", "tweakium", "--output", generated.absolutePath)
                gameDirectory = file("run/datagen")
            }
            for (clientData in listOf(true, false)) runs.create(if (clientData) "upgradeClientData" else "upgradeServerData") {
                if (clientData) clientData() else serverData()
                sourceSet.set(testMod)
                loadedMods.addAll(production, fixture, base, core, generic, cct)
                programArguments.addAll("--mod", "tweakium_testmod", "--output", layout.buildDirectory.dir("upgrade-datagen").get().asFile.absolutePath)
                gameDirectory = file("run/upgrade-datagen")
            }
        }
    }
}

if (library == "peripheralium") {
    val coreHarness = project(":testiarium:$minecraftVersion")
    evaluationDependsOn(coreHarness.path)
    val harnessSets = coreHarness.extensions.getByType<SourceSetContainer>()
    val testMod = sourceSets.create("testMod") {
        compileClasspath += sourceSets.main.get().compileClasspath + sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().runtimeClasspath + sourceSets.main.get().output
        for (name in listOf("main", "testMod")) {
            compileClasspath += harnessSets.named(name).get().output
            runtimeClasspath += harnessSets.named(name).get().output
        }
    }
    configurations.named(testMod.implementationConfigurationName) { extendsFrom(configurations.implementation.get()) }
    if (loader != "common") {
        val commonSets = project(":peripheralium:$minecraftVersion").extensions.getByType<SourceSetContainer>()
        val generated = rootProject.file("projects/peripheralium/$loader/versions/$minecraftVersion/src/generated/resources")
        sourceSets.main.get().resources.srcDir(generated)
        val dependencySets = listOf("broccolium", "tweakium").associateWith { dependency ->
            listOf(project(":$dependency:$loader:$minecraftVersion"), project(":$dependency:$minecraftVersion")).map {
                evaluationDependsOn(it.path)
                it.extensions.getByType<SourceSetContainer>().named("main").get()
            }
        }
        if (loader == "fabric") extensions.configure<LoomGradleExtensionAPI> {
            mods {
                register("peripheralium") { sourceSet(sourceSets.main.get()); sourceSet(commonSets.named("main").get()) }
                dependencySets.forEach { (name, sets) -> register(name) { sets.forEach { sourceSet(it) } } }
            }
            runs.create("datagen") {
                client(); source(sourceSets.main.get())
                property("fabric-api.datagen", "true")
                property("fabric-api.datagen.modid", "peripheralium")
                property("fabric-api.datagen.output-dir", generated.absolutePath)
                runDir("run/datagen")
            }
        } else extensions.configure<NeoForgeExtension> {
            val production = mods.create("peripheralium") { sourceSet(sourceSets.main.get()); sourceSet(commonSets.named("main").get()) }
            val dependencies = dependencySets.map { (name, sets) -> mods.create(name) { sets.forEach { sourceSet(it) } } }
            for (clientData in listOf(true, false)) runs.create(if (clientData) "clientData" else "serverData") {
                if (clientData) clientData() else serverData()
                sourceSet.set(sourceSets.main.get())
                loadedMods.add(production)
                loadedMods.addAll(dependencies)
                programArguments.addAll("--mod", "peripheralium", "--output", generated.absolutePath)
                gameDirectory = file("run/datagen")
            }
        }
    }
}

// Content belongs only on the private native runner, never Tweakium main outputs.
if (library == "tweakium" && loader != "common") gradle.projectsEvaluated {
    val content = project(":peripheralium:$loader:$minecraftVersion")
    val contentSets = content.extensions.getByType<SourceSetContainer>()
    val contentCore = project(":peripheralium:$minecraftVersion").extensions.getByType<SourceSetContainer>()
    val testMod = sourceSets.named("testMod").get()
    for (sets in listOf(contentSets, contentCore)) {
        testMod.compileClasspath += sets.named("main").get().output
        testMod.runtimeClasspath += sets.named("main").get().output
    }
    testMod.compileClasspath += contentCore.named("testMod").get().output
    testMod.runtimeClasspath += contentCore.named("testMod").get().output
    if (loader == "fabric") project.extensions.configure<LoomGradleExtensionAPI> {
        mods.register("peripheralium") { sourceSet(contentSets.named("main").get()); sourceSet(contentCore.named("main").get()) }
        mods.named("tweakium_testmod") { sourceSet(contentCore.named("testMod").get()) }
    } else project.extensions.configure<NeoForgeExtension> {
        val mod = mods.create("peripheralium") { sourceSet(contentSets.named("main").get()); sourceSet(contentCore.named("main").get()) }
        mods.named("tweakium_testmod") { sourceSet(contentCore.named("testMod").get()) }
        runs.named("gameTestServer") { loadedMods.add(mod) }
    }
}

if (library == "testiarium") {
    val common = if (loader == "common") project else project(":testiarium:$minecraftVersion").also { evaluationDependsOn(it.path) }
    val commonSets = common.extensions.getByType<SourceSetContainer>()
    val testMod = sourceSets.create("testMod") {
        compileClasspath += sourceSets.main.get().compileClasspath + sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().runtimeClasspath + sourceSets.main.get().output
        if (loader != "common") {
            compileClasspath += commonSets.named("testMod").get().output
            runtimeClasspath += commonSets.named("testMod").get().output
            resources.srcDir(rootProject.file("projects/testiarium/src/testMod/resources"))
        }
    }
    val cctTestMod = sourceSets.create("cctTestMod") {
        compileClasspath += testMod.compileClasspath + testMod.output
        runtimeClasspath += testMod.runtimeClasspath + testMod.output
        if (loader != "common") {
            compileClasspath += commonSets.named("cctTestMod").get().output
            runtimeClasspath += commonSets.named("cctTestMod").get().output
        }
    }
    for (feature in listOf(testMod, cctTestMod)) {
        configurations.named(feature.implementationConfigurationName) {
            extendsFrom(configurations.implementation.get())
        }
        java.registerFeature(feature.name) {
            capability("site.siredvin", "testiarium-${if (loader == "common") "core" else loader}-${if (feature == testMod) "test-mod" else "cct-test-mod"}", libraryVersion)
            usingSourceSet(feature)
            withSourcesJar()
        }
        tasks.named<ProcessResources>(feature.processResourcesTaskName) {
            inputs.property("version", libraryVersion)
            filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml")) { expand("version" to libraryVersion) }
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            if (feature == testMod) from(rootProject.file("projects/testiarium/src/testMod/resources/gameteststructures")) {
                into("data/minecraft/structure")
            }
        }
        if (loader != "common") tasks.named<Jar>(feature.jarTaskName) {
            from(commonSets.named(feature.name).map { it.output })
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
    }
    if (loader == "common") {
        for (part in listOf("common", "common-api", "core")) {
            dependencies.add(cctTestMod.compileOnlyConfigurationName, "cc.tweaked:cc-tweaked-$minecraftVersion-$part:1.120.0")
        }
    } else {
        dependencies.add(cctTestMod.implementationConfigurationName, "cc.tweaked:cc-tweaked-$minecraftVersion-$loader:1.120.0")
        val structures = layout.buildDirectory.dir("resources/testMod").get().asFile.absolutePath
        val fixtureSource = rootProject.file("projects/testiarium/src/testMod/resources/gameteststructures").absolutePath
        val cctFixtures = rootProject.file("projects/testiarium/src/cctTestMod/resources/computer").absolutePath
        fun runProperties(report: String, client: Boolean = false) = mapOf(
            "testiarium.tags" to if (client) "client" else "common",
            "testiarium.structures" to structures,
            "testiarium.fixture-source" to fixtureSource,
            "testiarium.cct-fixtures" to cctFixtures,
            "testiarium.gametest-report" to layout.buildDirectory.file("test-results/$report.xml").get().asFile.absolutePath,
        )
        if (loader == "fabric") extensions.configure<LoomGradleExtensionAPI> {
            mods {
                register("testiarium") { sourceSet(sourceSets.main.get()); sourceSet(commonSets.named("main").get()) }
                register("testiarium_testmod") { sourceSet(testMod); sourceSet(commonSets.named("testMod").get()) }
                register("testiarium_cct_testmod") { sourceSet(cctTestMod); sourceSet(commonSets.named("cctTestMod").get()) }
            }
            runs {
                create("productionServer") {
                    server()
                    source(sourceSets.main.get())
                    runDir("run/server")
                }
                named("server") {
                    source(testMod)
                    property("fabric-api.gametest", "true")
                    runProperties("gametest").forEach { (key, value) -> property(key, value) }
                    vmArg("-ea")
                    runDir("run/gametest")
                }
                create("cctGameTest") {
                    server(); source(cctTestMod)
                    property("fabric-api.gametest", "true")
                    runProperties("cct-gametest").forEach { (key, value) -> property(key, value) }
                    vmArg("-ea")
                    runDir("run/cct-gametest")
                }
                create("clientGameTest") {
                    client(); source(testMod)
                    runProperties("client-gametest", true).forEach { (key, value) -> property(key, value) }
                    property("testiarium.client", "true")
                    property("testiarium.screenshots", layout.buildDirectory.get().asFile.absolutePath)
                    vmArg("-ea")
                    runDir("run/client-gametest")
                }
            }
        } else extensions.configure<NeoForgeExtension> {
            val production = mods.create("testiarium") { sourceSet(sourceSets.main.get()); sourceSet(commonSets.named("main").get()) }
            val generic = mods.create("testiarium_testmod") { sourceSet(testMod); sourceSet(commonSets.named("testMod").get()) }
            val cct = mods.create("testiarium_cct_testmod") { sourceSet(cctTestMod); sourceSet(commonSets.named("cctTestMod").get()) }
            runs {
                create("server") {
                    server()
                    sourceSet.set(sourceSets.main.get())
                    loadedMods.add(production)
                    programArgument("--nogui")
                    gameDirectory = file("run/server")
                }
                create("gameTestServer") {
                    type = "gameTestServer"
                    val withCct = providers.gradleProperty("testiarium.cct").isPresent
                    sourceSet.set(if (withCct) cctTestMod else testMod)
                    loadedMods.addAll(production, generic)
                    if (withCct) loadedMods.add(cct)
                    runProperties(if (withCct) "cct-gametest" else "gametest").forEach { (key, value) -> systemProperty(key, value) }
                    jvmArgument("-ea")
                    gameDirectory = file("run/gametest")
                }
                create("clientGameTest") {
                    client(); sourceSet.set(testMod)
                    loadedMods.addAll(production, generic)
                    runProperties("client-gametest", true).forEach { (key, value) -> systemProperty(key, value) }
                    systemProperty("testiarium.client", "true")
                    systemProperty("neoforge.enableGameTest", "true")
                    systemProperty("testiarium.screenshots", layout.buildDirectory.get().asFile.absolutePath)
                    jvmArgument("-ea")
                    gameDirectory = file("run/client-gametest")
                }
            }
        }
    }
}

if (loader == "forge") {
    val rawJar = tasks.register<Jar>("rawJar") {
        archiveBaseName.set("${base.archivesName.get()}-raw")
        archiveClassifier.set("raw")
        from(sourceSets.main.get().output)
    }
    configurations.create("raw") { isCanBeConsumed = true }
    artifacts.add("raw", rawJar)
    tasks.named("assemble") { dependsOn(rawJar) }
}

publishing {
    publications.register<MavenPublication>("maven") {
        artifactId = base.archivesName.get()
        from(components["java"])
    }
}
