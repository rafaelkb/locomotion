plugins {
    `kotlin-dsl`
    kotlin("jvm") version "2.0.20"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("dev.kikugie.stonecutter:dev.kikugie.stonecutter.gradle.plugin:0.8")
}
