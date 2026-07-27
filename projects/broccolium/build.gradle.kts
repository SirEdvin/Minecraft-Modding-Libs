@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.vanilla")
    id("site.siredvin.publishing")
}

val broccoliumVersion: String by extra
val minecraftVersion: String by extra

baseShaking {
    projectPart.set("common")
    projectName.set("broccolium")
    projectVersion.set(broccoliumVersion)
    shake()
}

vanillaShaking {
    accessWideners.set(
        listOf(
            project(":broccolium").file("src/main/resources/broccolium-common.accesswidener").absolutePath,
            project(":broccolium").file("src/main/resources/broccolium.accesswidener").absolutePath,
        ),
    )
    shake()
}

sourceSets {
    create("testFixtures") {
        compileClasspath += main.get().compileClasspath
        compileClasspath += main.get().output
        runtimeClasspath += main.get().output
    }
    test {
        compileClasspath += sourceSets["testFixtures"].output
        runtimeClasspath += sourceSets["testFixtures"].output
    }
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.onlycore)
    api(libs.bundles.apicommon)
    compileOnly(libs.mixin)

    add(sourceSets["testFixtures"].compileOnlyConfigurationName, kotlin(if (sc.current.parsed >= "1.21") "test-junit5" else "test"))
    add(sourceSets["testFixtures"].compileOnlyConfigurationName, libs.bundles.test)

    testImplementation(kotlin(if (sc.current.parsed >= "1.21") "test-junit5" else "test"))
    testImplementation(libs.bundles.test)
}

java.registerFeature("testFixtures") {
    usingSourceSet(sourceSets.getByName("testFixtures"))
    disablePublication()
}

tasks.test {
    useJUnitPlatform()
}

publishingShaking {
    projectVersion.set(broccoliumVersion)
    shake()
}
