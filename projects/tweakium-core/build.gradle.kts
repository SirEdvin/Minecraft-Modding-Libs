@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.vanilla")
    id("site.siredvin.publishing")
}

val tweakiumVersion: String by extra
val minecraftVersion: String by extra

baseShaking {
    projectPart.set("common")
    projectName.set("tweakium")
    projectVersion.set(tweakiumVersion)
    shake()
}

vanillaShaking {
    accessWideners.set(
        listOf(
            "src/main/resources/tweakium-common.accesswidener",
            "src/main/resources/tweakium.accesswidener",
        ),
    )
    shake()
}

sourceSets {
    create("testMod") {
        compileClasspath += main.get().compileClasspath
        compileClasspath += main.get().output
        runtimeClasspath += main.get().output
        compileClasspath += project(":testiarium-core").sourceSets["testMod"].output
        runtimeClasspath += project(":testiarium-core").sourceSets["testMod"].output
        compileClasspath += project(":testiarium-core").sourceSets["cctTestMod"].output
        runtimeClasspath += project(":testiarium-core").sourceSets["cctTestMod"].output
    }
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
    implementation(libs.bundles.cccommon)
    implementation(libs.bundles.onlycore)
    api(libs.bundles.apicommon)
    compileOnly(libs.mixin)

    implementation(project(":broccolium-core"))

    add(sourceSets["testFixtures"].compileOnlyConfigurationName, kotlin("test"))
    add(sourceSets["testFixtures"].compileOnlyConfigurationName, libs.bundles.test)
    add(sourceSets["testMod"].compileOnlyConfigurationName, project(":testiarium-core"))
    add(sourceSets["testMod"].compileOnlyConfigurationName, libs.bundles.cccommon)

    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.test)
}

tasks.test {
    useJUnitPlatform()
}

publishingShaking {
    projectVersion.set(tweakiumVersion)
    shake()
}
