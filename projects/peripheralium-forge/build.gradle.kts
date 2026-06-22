import site.siredvin.peripheralium.gradle.mavenDependencies

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.neoforge")
    id("site.siredvin.publishing")
    id("site.siredvin.mod-publishing")
    id("idea")
}

val peripheraliumVersion: String by extra
val tweakiumVersion: String by extra
val broccoliumVersion: String by extra

baseShaking {
    projectPart.set("forge")
    projectName.set("peripheralium")
    projectVersion.set(peripheraliumVersion)
    integrationRepositories.set(true)
    shake()
}

neoforgeShaking {
    projectName.set("peripheralium")
    commonProjectName.set("peripheralium-core")
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
            "tweakium" to tweakiumVersion,
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

sourceSets {
    test {
        compileClasspath += sourceSets["main"].compileClasspath + sourceSets["main"].output
        runtimeClasspath += sourceSets["main"].runtimeClasspath + sourceSets["main"].output
        compileClasspath += project(":peripheralium-core").sourceSets["main"].output
        runtimeClasspath += project(":peripheralium-core").sourceSets["main"].output
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

    compileOnly(project(":tweakium-core")) {
        exclude("cc.tweaked")
        exclude("fuzs.forgeconfigapiport")
        exclude("dan200.computercraft")
    }
    compileOnly(project(":broccolium-core")) {
        exclude("cc.tweaked")
        exclude("fuzs.forgeconfigapiport")
        exclude("dan200.computercraft")
    }
    implementation(project(":broccolium-forge", configuration = "raw"))
    implementation(project(":tweakium-forge", configuration = "raw"))

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
    projectVersion.set(peripheraliumVersion)
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
