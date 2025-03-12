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
            "src/main/resources/broccolium-common.accesswidener",
            "src/main/resources/broccolium.accesswidener",
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

    add(sourceSets["testFixtures"].compileOnlyConfigurationName, kotlin("test-junit5"))
    add(sourceSets["testFixtures"].compileOnlyConfigurationName, libs.bundles.test)

    testImplementation(kotlin("test-junit5"))
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
