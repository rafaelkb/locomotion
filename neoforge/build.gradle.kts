@file:Suppress("UnstableApiUsage")

import java.util.concurrent.Callable

plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
}

val loader = prop("loom.platform")!!
val minecraft: String = stonecutter.current.version
val common: Project = requireNotNull(stonecutter.node.sibling("")) {
    "No common project for $project"
}.project

version = "${prop("mod.version")}+$minecraft-playtesting"
base {
    archivesName.set("${prop("mod.id")}-$loader")
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

loom {
    silentMojangMappingsLicense()

    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    runs {
        val runDir = "../../../.runs"

        named("client") {
            client()
            configName = "Client"
            runDir("$runDir/client")
            source(sourceSets["main"])
            programArgs("--username=Dev")
        }
        named("server") {
            server()
            configName = "Server"
            runDir("$runDir/server")
            source(sourceSets["main"])
        }
    }

    runConfigs.all {
        isIdeConfigGenerated = true
    }
}

val commonBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

val shadowBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

configurations {
    compileClasspath.get().extendsFrom(commonBundle)
    runtimeClasspath.get().extendsFrom(commonBundle)
    get("developmentNeoForge").extendsFrom(commonBundle)
}

repositories {
    maven("https://maven.parchmentmc.org/")
    maven("https://maven.neoforged.net/releases/")

    maven("https://maven.isxander.dev/releases")
    maven("https://api.modrinth.com/maven")
    maven {
        name = "Gegy"
        url = uri("https://maven.gegy.dev/releases/")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${versionProp("parchment_minecraft_version")}:${versionProp("parchment_mappings_version")}@zip")
//        mappings("dev.lambdaurora:${versionProp("yalmm")}")
    })
    neoForge("net.neoforged:neoforge:${versionProp("neoforge_loader")}")

    commonBundle(project(common.path, "namedElements")) { isTransitive = false }
    shadowBundle(project(common.path, "transformProductionNeoForge")) { isTransitive = false }

    // Mod implementations
    //runtimeOnly("dev.isxander:yet-another-config-lib:${versionProp("yacl_version")}-neoforge")
}

tasks.processResources {
    applyProperties(project, listOf("META-INF/neoforge.mods.toml", "${prop("mod.id")}-neoforge.mixins.json", "pack.mcmeta"))
}

// Bundles the common project into the mod jar. This replaces the Shadow plugin, whose 8.x releases
// crash on Gradle 9 ("No such property: mode for class ...FileCopyDetails").
val bundleJar = tasks.register<Jar>("bundleJar") {
    group = "build"
    archiveClassifier = "dev-shadow"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn(shadowBundle)
    from(sourceSets.main.get().output)
    from(Callable { shadowBundle.files.map { zipTree(it) } })
}

tasks.remapJar {
    injectAccessWidener = true
    input = bundleJar.get().archiveFile
    archiveClassifier = null
    dependsOn(bundleJar)
}

tasks.jar {
    archiveClassifier = "dev"
}

java {
    withSourcesJar()
    val java = if (stonecutter.eval(minecraft, ">=1.20.5"))
        JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.build {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
}

tasks.register<Copy>("buildAndCollect") {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
    from(tasks.remapJar.get().archiveFile, tasks.remapSourcesJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${prop("mod.version")}/$loader"))
    dependsOn("build")
}

stonecutter {
    // Constants should be given a key and a boolean value
    const("forge", loader == "forge")
    const("neoforge", loader == "neoforge")
}