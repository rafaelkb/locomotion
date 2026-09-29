pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases/")
//		maven("https://maven.kikugie.dev/snapshots")
    }
}

plugins {
    // Make sure the version here is the same as the dependency in buildSrc/build.gradle.kts.kts
    id("dev.kikugie.stonecutter") version "0.5.1"
}

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true
    create(rootProject) {
        // The root branch holds the common (shared) source set. Every version that a
        // loader branch targets has to be registered here as well, otherwise that
        // branch's `stonecutter.node.sibling("")` lookup finds no common project and
        // the build fails to configure.
        versions("1.21.11", "1.21.1")
        vcsVersion = "1.21.11"
        // Fabric only targets the current development version; 1.21.1 is NeoForge only.
        branch("fabric") { versions("1.21.11") }
        //branch("forge") { versions("1.21.5") }+
        // Keep the current release on the active version while also building a 1.21.1 port.
        branch("neoforge") { versions("1.21.11", "1.21.1") }
    }
}

rootProject.name = "Locomotion"