@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.vanilla")
    id("site.siredvin.publishing")
}

val tweakiumVersion: String by extra
val minecraftVersion: String by extra
val ccCommon = if (sc.current.parsed >= "1.21") libs.bundles.cccommon121 else libs.bundles.cccommon120

baseShaking {
    projectPart.set("common")
    projectName.set("tweakium")
    projectVersion.set(tweakiumVersion)
    shake()
}

vanillaShaking {
    accessWideners.set(
        listOf(
            project(":tweakium").file("src/main/resources/tweakium-common.accesswidener").absolutePath,
            if (sc.current.isActive) {
                project(":tweakium").file("src/main/resources/tweakium.accesswidener").absolutePath
            } else {
                project(":tweakium").file("versions/${sc.current.project}/src/main/resources/tweakium.accesswidener").absolutePath
            },
        ),
    )
    shake()
}

sourceSets {
    create("testMod") {
        compileClasspath += main.get().compileClasspath
        compileClasspath += main.get().output
        runtimeClasspath += main.get().output
        compileClasspath += project(":testiarium:${sc.current.project}").sourceSets["testMod"].output
        runtimeClasspath += project(":testiarium:${sc.current.project}").sourceSets["testMod"].output
        compileClasspath += project(":testiarium:${sc.current.project}").sourceSets["cctTestMod"].output
        runtimeClasspath += project(":testiarium:${sc.current.project}").sourceSets["cctTestMod"].output
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
    implementation(ccCommon)
    implementation(libs.bundles.onlycore)
    api(libs.bundles.apicommon)
    compileOnly(libs.mixin)

    implementation(project.project(":broccolium:${sc.current.project}").sourceSets.main.get().output)

    add(sourceSets["testFixtures"].compileOnlyConfigurationName, kotlin("test"))
    add(sourceSets["testFixtures"].compileOnlyConfigurationName, libs.bundles.test)
    add(sourceSets["testMod"].compileOnlyConfigurationName, project(":testiarium:${sc.current.project}"))
    add(sourceSets["testMod"].compileOnlyConfigurationName, ccCommon)

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
