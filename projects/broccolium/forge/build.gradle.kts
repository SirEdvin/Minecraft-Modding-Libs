import site.siredvin.peripheralium.gradle.mavenDependencies
import net.neoforged.moddevgradle.legacyforge.dsl.LegacyForgeExtension

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.publishing")
    id("site.siredvin.mod-publishing")
    id("site.siredvin.neoforge") apply false
    id("net.neoforged.moddev.legacyforge") apply false
}

val broccoliumVersion: String by extra
val modernForge = sc.current.parsed >= "1.21"

apply(plugin = if (modernForge) "site.siredvin.neoforge" else "net.neoforged.moddev.legacyforge")
apply(plugin = "kotlin")
apply(plugin = "site.siredvin.base")
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(if (modernForge) 21 else 17))
}
evaluationDependsOn(":broccolium:${sc.current.project}")
evaluationDependsOn(":testiarium:${sc.current.project}")
evaluationDependsOn(":testiarium:forge:${sc.current.project}")

base.archivesName.set("broccolium-forge-${sc.current.version}")
version = broccoliumVersion
sourceSets.main.configure { resources.srcDir("src/generated/resources") }

if (modernForge) {
    val shaking = extensions.getByName("neoforgeShaking")
    @Suppress("UNCHECKED_CAST")
    (shaking.javaClass.getMethod("getCommonProjectName").invoke(shaking) as Property<String>)
        .set("broccolium:${sc.current.project}")
    @Suppress("UNCHECKED_CAST")
    (shaking.javaClass.getMethod("getProjectName").invoke(shaking) as Property<String>).set("broccolium")
    @Suppress("UNCHECKED_CAST")
    (shaking.javaClass.getMethod("getUseAT").invoke(shaking) as Property<Boolean>).set(true)
    @Suppress("UNCHECKED_CAST")
    (shaking.javaClass.getMethod("getUseMixins").invoke(shaking) as Property<Boolean>).set(true)
    shaking.javaClass.getMethod("shake").invoke(shaking)
    tasks.named("createMinecraftArtifacts") { dependsOn("stonecutterGenerate") }
} else {
    extensions.configure<LegacyForgeExtension>("legacyForge") {
        version = "1.20.1-47.1.0"
        parchment {
            minecraftVersion = libs.versions.parchmentMc.forge.get()
            mappingsVersion = libs.versions.parchment.forge.get()
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
                    "--mod", "broccolium", "--all",
                    "--output", file("src/generated/resources").absolutePath,
                    "--existing", rootProject.project(":broccolium:${sc.current.project}").file("src/main/resources").absolutePath,
                    "--existing", file("src/main/resources").absolutePath,
                )
            }
        }
        mods.create("broccolium") {
            sourceSet(sourceSets.main.get())
            sourceSet(project(":broccolium:${sc.current.project}").sourceSets.main.get())
        }
    }
    dependencies {
        compileOnly(project(":broccolium:${sc.current.project}"))
        annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    }
    tasks.processResources {
        from(project(":broccolium:${sc.current.project}").sourceSets.main.get().resources)
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
        from(project(":broccolium:${sc.current.project}").sourceSets.main.get().output.classesDirs)
        manifest.attributes["MixinConfigs"] = "broccolium.mixins.json"
    }
    extra["releaseJar"] = "jar"
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

sourceSets {
    create("testMod") {
        compileClasspath += main.get().compileClasspath
        compileClasspath += main.get().output
        compileClasspath += project(":testiarium:${sc.current.project}").sourceSets.main.get().output
        compileClasspath += project(":testiarium:forge:${sc.current.project}").sourceSets.main.get().output
        runtimeClasspath += main.get().runtimeClasspath
        runtimeClasspath += main.get().output
        runtimeClasspath += project(":testiarium:${sc.current.project}").sourceSets.main.get().output
        runtimeClasspath += project(":testiarium:forge:${sc.current.project}").sourceSets.main.get().output
    }
    test {
        compileClasspath += project(":broccolium:${sc.current.project}").sourceSets["testFixtures"].output
        runtimeClasspath += project(":broccolium:${sc.current.project}").sourceSets["testFixtures"].output
    }
}

sourceSets.test.configure {
    compileClasspath += sourceSets.main.get().compileClasspath + sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath + sourceSets.main.get().output
    compileClasspath += project(":broccolium:${sc.current.project}").sourceSets.main.get().output
    runtimeClasspath += project(":broccolium:${sc.current.project}").sourceSets.main.get().output
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
    if (modernForge) {
        implementation(libs.forge.kotlin.neoforge)
        runtimeOnly(libs.jei.neoforge)
    } else {
        implementation(libs.forge.kotlin)
        add("modRuntimeOnly", libs.jei.forge)
    }

    testImplementation(kotlin("test"))
    testCompileOnly(libs.autoService)
    testAnnotationProcessor(libs.autoService)
    testImplementation(libs.byteBuddy)
    testImplementation(libs.byteBuddyAgent)
    testImplementation(libs.bundles.test)
}

tasks.test {
    useJUnitPlatform()
}

publishingShaking {
    projectVersion.set(broccoliumVersion)
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
