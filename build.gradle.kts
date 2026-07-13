plugins {
    java
    id("site.siredvin.root") version "0.8.18"
    id("site.siredvin.release") version "0.8.18"
    id("com.github.ben-manes.versions") version "0.51.0"
}

subprojectShaking {
    withKotlin.set(true)
    kotlinVersion.set("2.0.0")
}

val setupSubproject = subprojectShaking::setupSubproject
val broccoliumVersion: String by project.extra
val testiariumVersion: String by project.extra

subprojects {
    setupSubproject(this)
}
//
//githubShaking {
//    modBranch.set("1.20")
//    projectRepo.set("Minecraft-Modding-Libs")
//    projectVersion.set(broccoliumVersion)
//    shake()
//}

repositories {
    mavenCentral()
}
