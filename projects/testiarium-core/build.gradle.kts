@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.vanilla")
    id("site.siredvin.publishing")
}

val testiariumVersion: String by extra

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

dependencies {
    implementation(libs.bundles.kotlin)
}

publishingShaking {
    projectVersion.set(testiariumVersion)
    shake()
}
