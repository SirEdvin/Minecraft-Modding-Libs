import site.siredvin.peripheralium.gradle.mavenDependencies

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.publishing")
    id("site.siredvin.mod-publishing")
    id("site.siredvin.forge")
}

val broccoliumVersion: String by extra

baseShaking {
    projectPart.set("forge")
    projectName.set("broccolium")
    projectVersion.set(broccoliumVersion)
    shake()
}

forgeShaking {
    commonProjectName.set("broccolium-core")
    projectName.set("broccolium")
    useAT.set(true)
    useMixins.set(true) // So, we need this for correct task order in gradle, like, what?
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
}

sourceSets {
    create("testMod") {
        compileClasspath += main.get().compileClasspath
        compileClasspath += main.get().output
        compileClasspath += project(":testiarium-core").sourceSets.main.get().output
        compileClasspath += project(":testiarium-forge").sourceSets.main.get().output
        runtimeClasspath += main.get().runtimeClasspath
        runtimeClasspath += main.get().output
        runtimeClasspath += project(":testiarium-core").sourceSets.main.get().output
        runtimeClasspath += project(":testiarium-forge").sourceSets.main.get().output
    }
    test {
        compileClasspath += project(":broccolium-core").sourceSets["testFixtures"].output
        runtimeClasspath += project(":broccolium-core").sourceSets["testFixtures"].output
    }
}

configurations.create("raw") {
    isCanBeConsumed = true
}

tasks.register<Jar>("rawJar") {
    dependsOn(tasks.named("jar"))
    archiveBaseName.set(archiveBaseName.get() + "-raw")
    archiveClassifier.set("raw")
    from(sourceSets["main"].output)
}

tasks.named("jar") { finalizedBy("rawJar") }

artifacts {
    add("raw", tasks["rawJar"]) {
        classifier = "raw"
    }
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.forge.raw)
    libs.bundles.forge.base.get().map { implementation(fg.deobf(it)) }

    libs.bundles.externalMods.forge.runtime.get().map { runtimeOnly(fg.deobf(it)) }

    testImplementation(kotlin("test"))
    testCompileOnly(libs.autoService)
    testAnnotationProcessor(libs.autoService)
    testImplementation(libs.byteBuddy)
    testImplementation(libs.byteBuddyAgent)
    testImplementation(libs.bundles.test)
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
                fg.component(this)
                mavenDependencies {
                    exclude(dependencies.create("site.siredvin:"))
                    exclude(libs.jei.forge.get())
                }
            }
        }
    }
}
