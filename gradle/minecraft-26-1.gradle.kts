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
    }
    if (library == "tweakium" || library == "testiarium") {
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
        expand("version" to libraryVersion)
    }
    exclude(".cache")
}

if (loader != "common") {
    val commonProject = project(":$library:$minecraftVersion")
    evaluationDependsOn(commonProject.path)
    dependencies.add("compileOnly", commonProject)
    tasks.named<Jar>("jar") {
        from(commonProject.extensions.getByType<SourceSetContainer>().named("main").map { it.output })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

// Publication and test/run wiring is completed with the subsystem port. Do not
// expose an unverified target skeleton through a publish task during bootstrap.
tasks.withType<AbstractPublishToMaven>().configureEach {
    enabled = false
}
