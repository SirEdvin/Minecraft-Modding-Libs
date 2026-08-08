import site.siredvin.peripheralium.gradle.mavenDependencies
import net.neoforged.moddevgradle.dsl.ModDevExtension
import net.neoforged.moddevgradle.legacyforge.dsl.LegacyForgeExtension

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.publishing")
    id("site.siredvin.mod-publishing")
    id("site.siredvin.neoforge") apply false
    id("net.neoforged.moddev.legacyforge") apply false
}

val tweakiumVersion: String by extra
val broccoliumVersion: String by extra
val modernForge = sc.current.parsed >= "1.21"

apply(plugin = if (modernForge) "site.siredvin.neoforge" else "net.neoforged.moddev.legacyforge")
apply(plugin = "kotlin")
apply(plugin = "site.siredvin.base")
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(if (modernForge) 21 else 17))
}
evaluationDependsOn(":broccolium:${sc.current.project}")
evaluationDependsOn(":broccolium:forge:${sc.current.project}")
evaluationDependsOn(":testiarium:${sc.current.project}")
evaluationDependsOn(":testiarium:forge:${sc.current.project}")
evaluationDependsOn(":tweakium:${sc.current.project}")

base.archivesName.set("tweakium-forge-${sc.current.version}")
version = tweakiumVersion
sourceSets.main.configure { resources.srcDir("src/generated/resources") }

if (modernForge) {
    val shaking = extensions.getByName("neoforgeShaking")
    fun set(name: String, value: Any) {
        @Suppress("UNCHECKED_CAST")
        if (value is Map<*, *>) {
            (shaking.javaClass.getMethod("get$name").invoke(shaking) as MapProperty<String, String>)
                .set(value as Map<String, String>)
        } else {
            (shaking.javaClass.getMethod("get$name").invoke(shaking) as Property<Any>).set(value)
        }
    }
    set("CommonProjectName", "tweakium:${sc.current.project}")
    set("ProjectName", "tweakium")
    set("UseAT", true)
    set("UseMixins", true)
    set(
        "ExtraRawVersionMappings",
        mapOf("computercraft" to libs.versions.ccTweaked121.get(), "broccolium" to broccoliumVersion),
    )
    shaking.javaClass.getMethod("shake").invoke(shaking)
    tasks.named("createMinecraftArtifacts") { dependsOn("stonecutterGenerate") }
} else {
    extensions.configure<LegacyForgeExtension>("legacyForge") {
        enable {
            forgeVersion = "1.20.1-47.1.0"
            setDisableRecompilation(false)
        }
        parchment {
            minecraftVersion = "1.20.1"
            mappingsVersion = "2023.07.16"
        }
        validateAccessTransformers.set(true)
        runs {
            create("client") { client(); gameDirectory = file("run") }
            create("server") { server(); gameDirectory = file("run/server"); programArgument("--nogui") }
            create("data") {
                data()
                gameDirectory = file("run")
                programArguments.addAll(
                    "--mod", "tweakium", "--all", "--output", file("src/generated/resources").absolutePath,
                    "--existing", rootProject.project(":tweakium:${sc.current.project}").file("src/main/resources").absolutePath,
                    "--existing", file("src/main/resources").absolutePath,
                )
            }
        }
        mods.create("tweakium") {
            sourceSet(sourceSets.main.get())
            sourceSet(project(":tweakium:${sc.current.project}").sourceSets.main.get())
        }
    }
    dependencies {
        compileOnly(project(":tweakium:${sc.current.project}"))
        annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    }
    tasks.processResources {
        from(project(":tweakium:${sc.current.project}").sourceSets.main.get().resources)
        inputs.properties("version" to project.version, "forgeVersion" to "47.1.0", "computercraftVersion" to "1.113.1")
        filesMatching("META-INF/mods.toml") {
            expand(
                "forgeVersion" to "47.1.0", "computercraftVersion" to "1.113.1",
                "broccoliumVersion" to broccoliumVersion,
                "file" to mapOf("jarVersion" to project.version), "version" to project.version,
            )
        }
        exclude(".cache")
    }
    tasks.named<Jar>("jar") {
        from(project(":tweakium:${sc.current.project}").sourceSets.main.get().output.classesDirs)
        manifest.attributes["MixinConfigs"] = "tweakium.mixins.json"
    }
    extra["releaseJar"] = "jar"
}

val testMod = sourceSets.create("testMod") {
    resources.srcDir(rootProject.file("projects/tweakium/src/testMod/resources"))
    compileClasspath += sourceSets.main.get().compileClasspath
    compileClasspath += sourceSets.main.get().output
    compileClasspath += project(":tweakium:${sc.current.project}").sourceSets["testMod"].output
    compileClasspath += project(":testiarium:${sc.current.project}").sourceSets["testMod"].output
    compileClasspath += project(":testiarium:${sc.current.project}").sourceSets["cctTestMod"].output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    runtimeClasspath += sourceSets.main.get().output
    runtimeClasspath += project(":tweakium:${sc.current.project}").sourceSets["testMod"].output
    runtimeClasspath += project(":testiarium:${sc.current.project}").sourceSets["testMod"].output
    runtimeClasspath += project(":testiarium:${sc.current.project}").sourceSets["cctTestMod"].output
}

val cctTestMod = sourceSets.create("cctTestMod") {
    resources.srcDir(rootProject.file("projects/testiarium/src/cctTestMod/resources"))
    compileClasspath += testMod.compileClasspath
    compileClasspath += project(":testiarium:${sc.current.project}").sourceSets["cctTestMod"].output
    runtimeClasspath += testMod.runtimeClasspath
    runtimeClasspath += project(":testiarium:${sc.current.project}").sourceSets["cctTestMod"].output
}

sourceSets.test.configure {
    compileClasspath += sourceSets.main.get().compileClasspath + sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath + sourceSets.main.get().output
    compileClasspath += project(":tweakium:${sc.current.project}").sourceSets.main.get().output
    runtimeClasspath += project(":tweakium:${sc.current.project}").sourceSets.main.get().output
}

repositories {
    maven("https://thedarkcolour.github.io/KotlinForForge/") { content { includeGroup("thedarkcolour") } }
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
    implementation(if (modernForge) libs.forge.kotlin.neoforge else libs.forge.kotlin)
    add(if (modernForge) "implementation" else "modImplementation", if (modernForge) libs.cc.tweaked.neoforge else libs.cc.tweaked.forge)
    add(if (modernForge) "runtimeOnly" else "modRuntimeOnly", if (modernForge) libs.jei.neoforge else libs.jei.forge)

    implementation(project.project(":broccolium:forge:${sc.current.project}").sourceSets.main.get().output)
    compileOnly(project.project(":broccolium:${sc.current.project}").sourceSets.main.get().output)
    compileOnly(project(":broccolium:${sc.current.project}"))

    testImplementation(kotlin("test"))
    testCompileOnly(libs.autoService)
    testAnnotationProcessor(libs.autoService)
    testImplementation(libs.byteBuddy)
    testImplementation(libs.byteBuddyAgent)
    testImplementation(libs.bundles.test)
    add(testMod.compileOnlyConfigurationName, project(":testiarium:forge:${sc.current.project}"))
}

extensions.configure<ModDevExtension>(if (modernForge) "neoForge" else "legacyForge") {
    val tweakium = mods.named("tweakium")
    val tweakiumTestMod = mods.register("tweakium_testmod") {
        sourceSet(testMod)
        sourceSet(project(":tweakium:${sc.current.project}").sourceSets["testMod"])
    }
    val testiarium = mods.register("testiarium") {
        sourceSet(project(":testiarium:forge:${sc.current.project}").sourceSets.main.get())
        sourceSet(project(":testiarium:${sc.current.project}").sourceSets.main.get())
    }
    val testiariumTestMod = mods.register("testiarium_testmod") {
        sourceSet(project(":testiarium:forge:${sc.current.project}").sourceSets["testMod"])
        sourceSet(project(":testiarium:${sc.current.project}").sourceSets["testMod"])
    }
    val testiariumCctTestMod = mods.register("testiarium_cct_testmod") {
        sourceSet(cctTestMod)
        sourceSet(project(":testiarium:forge:${sc.current.project}").sourceSets["cctTestMod"])
        sourceSet(project(":testiarium:${sc.current.project}").sourceSets["cctTestMod"])
    }
    runs {
        register("gameTestServer") {
            type = "gameTestServer"
            gameDirectory = file("run/peripheral-gametest")
            systemProperty("testiarium.tags", "tweakium")
            systemProperty("testiarium.structures", layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            systemProperty("testiarium.fixture-source", rootProject.file("projects/tweakium/src/testMod/resources/gameteststructures").absolutePath)
            systemProperty("testiarium.cct-fixtures", rootProject.file("projects/tweakium/src/testMod/resources/computer").absolutePath)
            systemProperty("testiarium.gametest-report", layout.buildDirectory.file("test-results/peripheral-gametest.xml").get().asFile.absolutePath)
            jvmArgument("-ea")
            programArgument("--nogui")
            loadedMods.add(tweakium)
            loadedMods.add(tweakiumTestMod)
            loadedMods.add(testiarium)
            loadedMods.add(testiariumTestMod)
            loadedMods.add(testiariumCctTestMod)
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
                    exclude(if (modernForge) libs.jei.neoforge.get() else libs.jei.forge.get())
                }
            }
        }
    }
}
