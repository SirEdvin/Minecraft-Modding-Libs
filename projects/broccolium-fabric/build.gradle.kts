import site.siredvin.peripheralium.gradle.mavenDependencies

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.fabric")
    id("site.siredvin.publishing")
}

val minecraftVersion: String by extra
val broccoliumVersion: String by extra

baseShaking {
    projectPart.set("fabric")
    projectName.set("broccolium")
    integrationRepositories.set(true)
    projectVersion.set(broccoliumVersion)
    shake()
}

fabricShaking {
    commonProjectName.set("broccolium-core")
    projectName.set("broccolium")
    accessWidener.set(project(":broccolium-core").file("src/main/resources/broccolium.accesswidener"))
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
    maven {
        name = "ModMenu maven"
        url = uri("https://maven.terraformersmc.com/releases")
        content {
            includeGroup("com.terraformersmc")
        }
    }
}

sourceSets {
    test {
        compileClasspath += project(":broccolium-core").sourceSets["testFixtures"].output
        runtimeClasspath += project(":broccolium-core").sourceSets["testFixtures"].output
    }
}

dependencies {
    implementation(libs.bundles.kotlin)

    modImplementation(libs.bundles.fabric.core)
    modImplementation(libs.bundles.fabric)

    modRuntimeOnly(libs.bundles.externalMods.fabric.runtime) {
        exclude("net.fabricmc.fabric-api")
        exclude("net.fabricmc", "fabric-loader")
    }

    testImplementation(kotlin("test"))
    testCompileOnly(libs.autoService)
    testAnnotationProcessor(libs.autoService)
    testImplementation(libs.byteBuddy)
    testImplementation(libs.byteBuddyAgent)
    testImplementation(libs.bundles.test)
}

tasks.test {
    dependsOn(tasks.generateDLIConfig)
    useJUnitPlatform()
    systemProperty("junit.jupiter.extensions.autodetection.enabled", true)
}

publishingShaking {
    projectVersion.set(broccoliumVersion)
    shake()
    project.publishing {
        publications {
            named<MavenPublication>("maven") {
                mavenDependencies {
                    exclude(project.dependencies.create("site.siredvin:"))
                    exclude(libs.rei.fabric.get())
                }
            }
        }
    }
}
