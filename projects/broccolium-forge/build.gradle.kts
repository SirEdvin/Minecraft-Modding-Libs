import site.siredvin.peripheralium.gradle.mavenDependencies

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.neoforge")
    id("site.siredvin.publishing")
    id("site.siredvin.mod-publishing")
    id("idea")
}

val broccoliumVersion: String by extra

baseShaking {
    projectPart.set("forge")
    projectName.set("broccolium")
    projectVersion.set(broccoliumVersion)
    shake()
}

neoforgeShaking {
    projectName.set("broccolium")
    commonProjectName.set("broccolium-core")
    useAT.set(true)
    useRawJar.set(true)
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
        compileClasspath += project(":broccolium-core").sourceSets["testFixtures"].output
        runtimeClasspath += project(":broccolium-core").sourceSets["testFixtures"].output
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
