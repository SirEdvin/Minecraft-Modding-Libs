@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.vanilla")
    id("site.siredvin.publishing")
}

val testiariumVersion: String by extra
val ccCommon = if (sc.current.parsed >= "1.21") libs.bundles.cccommon121 else libs.bundles.cccommon120

baseShaking {
    projectPart.set("core")
    projectName.set("testiarium")
    projectVersion.set(testiariumVersion)
    shake()
}

vanillaShaking {
    shake()
}

sourceSets.create("testMod") {
    compileClasspath += sourceSets.main.get().compileClasspath
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}

val cctTestMod = sourceSets.create("cctTestMod") {
    compileClasspath += sourceSets["testMod"].compileClasspath
    compileClasspath += sourceSets["testMod"].output
    runtimeClasspath += sourceSets["testMod"].runtimeClasspath
    runtimeClasspath += sourceSets["testMod"].output
}

java.registerFeature("testMod") {
    usingSourceSet(sourceSets["testMod"])
}

java.registerFeature("cctTestMod") {
    usingSourceSet(cctTestMod)
}

dependencies {
    implementation(libs.bundles.kotlin)
    add(sourceSets["testMod"].implementationConfigurationName, files(sourceSets.main.get().output))
    add(sourceSets["testMod"].compileOnlyConfigurationName, libs.mixin)
    add(cctTestMod.implementationConfigurationName, files(sourceSets.main.get().output))
    add(cctTestMod.compileOnlyConfigurationName, ccCommon)
}

publishingShaking {
    projectVersion.set(testiariumVersion)
    shake()
}
