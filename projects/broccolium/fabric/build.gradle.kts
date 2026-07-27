import site.siredvin.peripheralium.gradle.mavenDependencies

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.fabric")
    id("site.siredvin.publishing")
}

val minecraftVersion: String by extra
val broccoliumVersion: String by extra
val is121 = sc.current.parsed >= "1.21"
val fabric = if (is121) libs.bundles.fabric121 else libs.bundles.fabric120
val fabricApi = if (is121) libs.bundles.fabricApiBundle121 else libs.bundles.fabricApiBundle120
val fabricTest = if (is121) libs.bundles.fabricTest121 else libs.bundles.fabricTest120
val runtimeMods = if (is121) libs.bundles.externalModsFabricRuntime121 else libs.bundles.externalModsFabricRuntime120
val rei = if (is121) libs.reiFabric121 else libs.reiFabric120
val commonProject = project(":broccolium:${sc.current.project}")

baseShaking {
    projectPart.set("fabric")
    projectName.set("broccolium")
    integrationRepositories.set(true)
    projectVersion.set(broccoliumVersion)
    shake()
}

fabricShaking {
    commonProjectName.set("broccolium:${sc.current.project}")
    projectName.set("broccolium")
    accessWidener.set(project(":broccolium").file("src/main/resources/broccolium.accesswidener"))
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
        name = if (is121) "Modrinth maven" else "ModMenu maven"
        url = uri(if (is121) "https://api.modrinth.com/maven" else "https://maven.terraformersmc.com/releases")
        content {
            includeGroup(if (is121) "maven.modrinth" else "com.terraformersmc")
        }
    }
}

sourceSets {
    create("testMod") {
        compileClasspath += main.get().compileClasspath
        compileClasspath += main.get().output
        compileClasspath += project(":testiarium:${sc.current.project}").sourceSets.main.get().output
        compileClasspath += project(":testiarium:fabric:${sc.current.project}").sourceSets.main.get().output
        runtimeClasspath += main.get().runtimeClasspath
        runtimeClasspath += main.get().output
        runtimeClasspath += project(":testiarium:${sc.current.project}").sourceSets.main.get().output
        runtimeClasspath += project(":testiarium:fabric:${sc.current.project}").sourceSets.main.get().output
    }
    test {
        compileClasspath += project(":broccolium:${sc.current.project}").sourceSets["testFixtures"].output
        runtimeClasspath += project(":broccolium:${sc.current.project}").sourceSets["testFixtures"].output
    }
}

dependencies {
    configurations.implementation.get().dependencies.removeIf {
        it is org.gradle.api.artifacts.ProjectDependency && it.path == commonProject.path
    }
    implementation(commonProject.sourceSets.main.get().output)
    implementation(libs.bundles.kotlin)

    modImplementation(libs.bundles.fabric.core)
    modImplementation(fabric)

    modApi(fabricApi) {
        exclude("net.fabricmc.fabric-api")
    }

    modRuntimeOnly(runtimeMods) {
        exclude("net.fabricmc.fabric-api")
        exclude("net.fabricmc", "fabric-loader")
    }

    testImplementation(kotlin(if (is121) "test-junit5" else "test"))
    testImplementation(libs.byteBuddy)
    testImplementation(libs.byteBuddyAgent)
    testImplementation(libs.bundles.test)
    testImplementation(fabricTest)
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
                    exclude(project.dependencies.create("site.siredvin:"))
                    exclude(rei.get())
                }
            }
        }
    }
}
