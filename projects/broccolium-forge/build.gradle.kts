import site.siredvin.peripheralium.gradle.mavenDependencies

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.publishing")
    id("site.siredvin.mod-publishing")
    id("net.neoforged.moddev") version "2.0.115"
    id("idea")
}

val broccoliumVersion: String by extra

baseShaking {
    projectPart.set("forge")
    projectName.set("broccolium")
    projectVersion.set(broccoliumVersion)
    shake()
}

val extractedLibs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
val projectName = "broccolium"
val commonProjectName = "broccolium-core"

neoForge {
    // Specify the version of NeoForge to use.
    version = extractedLibs.findVersion("neoforge").get().toString()

    parchment {
        mappingsVersion = extractedLibs.findVersion("parchment").get().toString()
        minecraftVersion = extractedLibs.findVersion("parchmentMc").get().toString()
    }

    // This line is optional. Access Transformers are automatically detected
    // accessTransformers = project.files('src/main/resources/META-INF/accesstransformer.cfg')

    // Default run configurations.
    // These can be tweaked, removed, or duplicated as needed.
    runs {
        all {
            systemProperty("neoforge.logging.markers", "REGISTRIES")
            systemProperty("neoforge.logging.console.level", "debug")
        }

        val client by registering {
            client()
            gameDirectory = project.file("run")
        }

        val server by registering {
            server()
            gameDirectory = project.file("run/server")
            programArguments.addAll("--nogui")
        }

        val data by registering {
            data()
            gameDirectory = project.file("run")
            programArguments.addAll(
                "--mod", projectName, "--all",
                "--output", project.file("src/generated/resources/").absolutePath,
                "--existing", project.project(":broccolium-core").file("src/main/resources/").absolutePath,
                "--existing", project.file("src/main/resources/").absolutePath,
            )
        }
    }
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
        compileClasspath += project(":broccolium-core").sourceSets["testFixtures"].output
        runtimeClasspath += project(":broccolium-core").sourceSets["testFixtures"].output
        compileClasspath += project(":broccolium-core").sourceSets["main"].output
        runtimeClasspath += project(":broccolium-core").sourceSets["main"].output
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

    compileOnly(project(":$commonProjectName")) {
        exclude("cc.tweaked")
        exclude("fuzs.forgeconfigapiport")
        exclude("dan200.computercraft")
    }

    libs.bundles.externalMods.forge.runtime.get().map { runtimeOnly(it) }

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
                    exclude(libs.jei.forge.get())
                }
            }
        }
    }
}
