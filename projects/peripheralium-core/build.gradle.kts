@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.vanilla")
    id("site.siredvin.publishing")
}

val modVersion: String by extra
val minecraftVersion: String by extra

baseShaking {
    projectPart.set("common")
    projectName.set("peripheralium")
    shake()
}

vanillaShaking {
    accessWideners.set(
        listOf(
            "src/main/resources/peripheralium-common.accesswidener",
            "src/main/resources/peripheralium.accesswidener",
        ),
    )
    shake()
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.cccommon)
    implementation(libs.bundles.onlycore)
    api(libs.bundles.apicommon)
    compileOnly(libs.mixin)

    implementation(project(":broccolium-core"))
    implementation(project(":tweakium-core"))

    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.test)
}

tasks.test {
    useJUnitPlatform()
}

publishingShaking {
    shake()
}
