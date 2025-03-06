import site.siredvin.peripheralium.gradle.mavenDependencies

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.publishing")
    id("site.siredvin.forge")
}

val modVersion: String by extra

baseShaking {
    projectPart.set("forge")
    projectName.set("tweakium")
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
    shake()
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
}

tasks.test {
    useJUnitPlatform()
}

publishingShaking {
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
