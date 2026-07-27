@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.vanilla")
    id("site.siredvin.publishing")
}

val peripheraliumVersion: String by extra
val minecraftVersion: String by extra
val ccCommon = if (sc.current.parsed >= "1.21") libs.bundles.cccommon121 else libs.bundles.cccommon120

baseShaking {
    projectPart.set("common")
    projectName.set("peripheralium")
    projectVersion.set(peripheraliumVersion)
    shake()
}

vanillaShaking {
    accessWideners.set(
        listOf(
            project(":peripheralium").file("src/main/resources/peripheralium-common.accesswidener").absolutePath,
            project(":peripheralium").file("src/main/resources/peripheralium.accesswidener").absolutePath,
        ),
    )
    shake()
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(ccCommon)
    implementation(libs.bundles.onlycore)
    api(libs.bundles.apicommon)
    compileOnly(libs.mixin)

    implementation(project.project(":broccolium:${sc.current.project}").sourceSets.main.get().output)
    implementation(project.project(":tweakium:${sc.current.project}").sourceSets.main.get().output)

    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.test)
}

tasks.test {
    useJUnitPlatform()
}

publishingShaking {
    projectVersion.set(peripheraliumVersion)
    shake()
}
