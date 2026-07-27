import net.neoforged.moddevgradle.dsl.ModDevExtension
import net.neoforged.moddevgradle.legacyforge.dsl.LegacyForgeExtension
import net.neoforged.moddevgradle.legacyforge.dsl.ObfuscationExtension

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.publishing")
    id("site.siredvin.mod-publishing")
    id("site.siredvin.neoforge") apply false
    id("net.neoforged.moddev.legacyforge") apply false
}

val testiariumVersion: String by extra
val cctTests = providers.gradleProperty("testiarium.cct").isPresent
val modernForge = sc.current.parsed >= "1.21"

repositories {
    maven("https://thedarkcolour.github.io/KotlinForForge/") { content { includeGroup("thedarkcolour") } }
}

apply(plugin = if (modernForge) "site.siredvin.neoforge" else "net.neoforged.moddev.legacyforge")
apply(plugin = "kotlin")
apply(plugin = "site.siredvin.base")
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(if (modernForge) 21 else 17))
}
evaluationDependsOn(":testiarium:${sc.current.project}")

base.archivesName.set("testiarium-forge-${sc.current.version}")
version = testiariumVersion
sourceSets.main.configure { resources.srcDir("src/generated/resources") }

if (modernForge) {
    val shaking = extensions.getByName("neoforgeShaking")
    @Suppress("UNCHECKED_CAST")
    (shaking.javaClass.getMethod("getCommonProjectName").invoke(shaking) as Property<String>)
        .set("testiarium:${sc.current.project}")
    @Suppress("UNCHECKED_CAST")
    (shaking.javaClass.getMethod("getProjectName").invoke(shaking) as Property<String>).set("testiarium")
    @Suppress("UNCHECKED_CAST")
    (shaking.javaClass.getMethod("getUseAT").invoke(shaking) as Property<Boolean>).set(true)
    @Suppress("UNCHECKED_CAST")
    (shaking.javaClass.getMethod("getUseMixins").invoke(shaking) as Property<Boolean>).set(true)
    @Suppress("UNCHECKED_CAST")
    (shaking.javaClass.getMethod("getUseRawJar").invoke(shaking) as Property<Boolean>).set(true)
    shaking.javaClass.getMethod("shake").invoke(shaking)
    tasks.named("createMinecraftArtifacts") { dependsOn("stonecutterGenerate") }
} else {
    extensions.configure<LegacyForgeExtension>("legacyForge") {
        version = "1.20.1-47.1.0"
        parchment {
            minecraftVersion = "1.20.1"
            mappingsVersion = "2023.07.16"
        }
        validateAccessTransformers.set(true)
        runs {
            create("client") {
                client()
                gameDirectory = file("run")
            }
            create("server") {
                server()
                gameDirectory = file("run/server")
                programArgument("--nogui")
            }
            create("data") {
                data()
                gameDirectory = file("run")
                programArguments.addAll(
                    "--mod", "testiarium", "--all",
                    "--output", file("src/generated/resources").absolutePath,
                    "--existing", rootProject.project(":testiarium:${sc.current.project}").file("src/main/resources").absolutePath,
                    "--existing", file("src/main/resources").absolutePath,
                )
            }
        }
        mods.create("testiarium") {
            sourceSet(sourceSets.main.get())
            sourceSet(project(":testiarium:${sc.current.project}").sourceSets.main.get())
        }
    }
    dependencies {
        compileOnly(project(":testiarium:${sc.current.project}"))
        annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    }
    tasks.processResources {
        from(project(":testiarium:${sc.current.project}").sourceSets.main.get().resources)
        inputs.property("version", project.version)
        inputs.property("forgeVersion", "47.1.0")
        filesMatching("META-INF/mods.toml") {
            expand(
                "forgeVersion" to "47.1.0",
                "file" to mapOf("jarVersion" to project.version),
                "version" to project.version,
            )
        }
        exclude(".cache")
    }
    tasks.named<Jar>("jar") {
        from(project(":testiarium:${sc.current.project}").sourceSets.main.get().output.classesDirs)
        manifest.attributes["MixinConfigs"] = "testiarium.mixins.json"
    }
    extra["releaseJar"] = "jar"
}

sourceSets.create("testMod") {
    resources.srcDir(rootProject.file("projects/testiarium/src/testMod/resources"))
    compileClasspath += sourceSets.main.get().compileClasspath
    compileClasspath += sourceSets.main.get().output
    compileClasspath += project(":testiarium:${sc.current.project}").sourceSets["testMod"].output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    runtimeClasspath += sourceSets.main.get().output
    runtimeClasspath += project(":testiarium:${sc.current.project}").sourceSets["testMod"].output
}

val cctTestMod = sourceSets.create("cctTestMod") {
    compileClasspath += sourceSets["testMod"].compileClasspath
    compileClasspath += sourceSets["testMod"].output
    runtimeClasspath += sourceSets["testMod"].runtimeClasspath
    runtimeClasspath += sourceSets["testMod"].output
    compileClasspath += project(":testiarium:${sc.current.project}").sourceSets["cctTestMod"].output
    runtimeClasspath += project(":testiarium:${sc.current.project}").sourceSets["cctTestMod"].output
}

sourceSets.test.configure {
    compileClasspath += sourceSets.main.get().compileClasspath + sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath + sourceSets.main.get().output
    compileClasspath += project(":testiarium:${sc.current.project}").sourceSets.main.get().output
    runtimeClasspath += project(":testiarium:${sc.current.project}").sourceSets.main.get().output
}

java.registerFeature("testMod") {
    usingSourceSet(sourceSets["testMod"])
}

java.registerFeature("cctTestMod") {
    usingSourceSet(cctTestMod)
}

tasks.named<Jar>(sourceSets["testMod"].jarTaskName) {
    from(project(":testiarium:${sc.current.project}").sourceSets["testMod"].output.classesDirs)
}

tasks.named<Jar>(cctTestMod.jarTaskName) {
    from(project(":testiarium:${sc.current.project}").sourceSets["cctTestMod"].output)
}

val cctCompileOnly = if (modernForge) {
    cctTestMod.compileOnlyConfigurationName
} else {
    extensions.getByType<ObfuscationExtension>()
        .createRemappingConfiguration(configurations.getByName(cctTestMod.compileOnlyConfigurationName)).name
}
val cctRuntimeOnly = if (modernForge) {
    cctTestMod.runtimeOnlyConfigurationName
} else {
    extensions.getByType<ObfuscationExtension>()
        .createRemappingConfiguration(configurations.getByName(cctTestMod.runtimeOnlyConfigurationName)).name
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(if (modernForge) libs.forge.kotlin.neoforge else libs.forge.kotlin)
    add(
        cctCompileOnly,
        if (modernForge) libs.cc.tweaked.neoforge else libs.cc.tweaked.forge,
    )
    add(
        cctRuntimeOnly,
        if (modernForge) libs.cc.tweaked.neoforge else libs.cc.tweaked.forge,
    )
}

extensions.configure<ModDevExtension>(if (modernForge) "neoForge" else "legacyForge") {
    val testiarium = mods.named("testiarium")
    val testMod = mods.register("testiarium_testmod") {
        sourceSet(sourceSets["testMod"])
        sourceSet(project(":testiarium:${sc.current.project}").sourceSets["testMod"])
    }
    val cctMod = mods.register("testiarium_cct_testmod") {
        sourceSet(cctTestMod)
        sourceSet(project(":testiarium:${sc.current.project}").sourceSets["cctTestMod"])
    }
    runs {
        register("gameTestServer") {
            type = "gameTestServer"
            gameDirectory = file("run/gametest")
            systemProperty("testiarium.tags", "common")
            systemProperty("testiarium.structures", layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            systemProperty("testiarium.fixture-source", rootProject.file("projects/testiarium/src/testMod/resources/gameteststructures").absolutePath)
            systemProperty("testiarium.cct-fixtures", rootProject.file("projects/testiarium/src/cctTestMod/resources/computer").absolutePath)
            systemProperty("testiarium.gametest-report", layout.buildDirectory.file(if (cctTests) "test-results/cct-gametest.xml" else "test-results/gametest.xml").get().asFile.absolutePath)
            jvmArgument("-ea")
            programArgument("--nogui")
            loadedMods.add(testiarium)
            loadedMods.add(testMod)
            if (cctTests) loadedMods.add(cctMod)
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
            loadedMods.add(testiarium)
            loadedMods.add(testMod)
        }
    }
}

publishingShaking {
    projectVersion.set(testiariumVersion)
    shake()
}
