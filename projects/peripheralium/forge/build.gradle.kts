import net.neoforged.moddevgradle.legacyforge.dsl.LegacyForgeExtension
import site.siredvin.peripheralium.gradle.mavenDependencies

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.publishing")
    id("site.siredvin.mod-publishing")
    id("site.siredvin.neoforge") apply false
    id("net.neoforged.moddev.legacyforge") apply false
}

val peripheraliumVersion: String by extra
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
evaluationDependsOn(":tweakium:${sc.current.project}")
evaluationDependsOn(":tweakium:forge:${sc.current.project}")
evaluationDependsOn(":peripheralium:${sc.current.project}")

base.archivesName.set("peripheralium-forge-${sc.current.version}")
version = peripheraliumVersion
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
    set("CommonProjectName", "peripheralium:${sc.current.project}")
    set("ProjectName", "peripheralium")
    set("UseAT", true)
    set("UseMixins", true)
    set("UseRawJar", true)
    set(
        "ExtraRawVersionMappings",
        mapOf(
            "computercraft" to libs.versions.ccTweaked121.get(),
            "tweakium" to tweakiumVersion,
            "broccolium" to broccoliumVersion,
        ),
    )
    shaking.javaClass.getMethod("shake").invoke(shaking)
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
                    "--mod", "peripheralium", "--all", "--output", file("src/generated/resources").absolutePath,
                    "--existing", rootProject.project(":peripheralium:${sc.current.project}").file("src/main/resources").absolutePath,
                    "--existing", file("src/main/resources").absolutePath,
                )
            }
        }
        mods.create("peripheralium") {
            sourceSet(sourceSets.main.get())
            sourceSet(project(":peripheralium:${sc.current.project}").sourceSets.main.get())
        }
    }
    dependencies {
        compileOnly(project(":peripheralium:${sc.current.project}"))
        annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    }
    tasks.processResources {
        from(project(":peripheralium:${sc.current.project}").sourceSets.main.get().resources)
        inputs.properties("version" to project.version, "forgeVersion" to "47.1.0", "computercraftVersion" to "1.113.1", "broccoliumVersion" to broccoliumVersion, "tweakiumVersion" to tweakiumVersion)
        filesMatching("META-INF/mods.toml") {
            expand(
                "forgeVersion" to "47.1.0",
                "computercraftVersion" to "1.113.1",
                "tweakiumVersion" to tweakiumVersion,
                "broccoliumVersion" to broccoliumVersion,
                "file" to mapOf("jarVersion" to project.version),
                "version" to project.version,
            )
        }
        exclude(".cache")
    }
    tasks.named<Jar>("jar") {
        from(project(":peripheralium:${sc.current.project}").sourceSets.main.get().output.classesDirs)
        manifest.attributes["MixinConfigs"] = "peripheralium.mixins.json"
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

sourceSets.test.configure {
    compileClasspath += sourceSets.main.get().compileClasspath + sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath + sourceSets.main.get().output
    compileClasspath += project(":peripheralium:${sc.current.project}").sourceSets.main.get().output
    runtimeClasspath += project(":peripheralium:${sc.current.project}").sourceSets.main.get().output
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(if (modernForge) libs.forge.kotlin.neoforge else libs.forge.kotlin)
    add(if (modernForge) "implementation" else "modImplementation", if (modernForge) libs.cc.tweaked.neoforge else libs.cc.tweaked.forge)
    add(if (modernForge) "runtimeOnly" else "modRuntimeOnly", if (modernForge) libs.jei.neoforge else libs.jei.forge)

    implementation(project.project(":broccolium:forge:${sc.current.project}").sourceSets.main.get().output)
    implementation(project.project(":tweakium:forge:${sc.current.project}").sourceSets.main.get().output)
    compileOnly(project.project(":broccolium:${sc.current.project}").sourceSets.main.get().output)
    compileOnly(project.project(":tweakium:${sc.current.project}").sourceSets.main.get().output)

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
    projectVersion.set(peripheraliumVersion)
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
