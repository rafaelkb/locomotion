@file:Suppress("UnstableApiUsage")

plugins {
	id("dev.architectury.loom")
	id("architectury-plugin")
}

val minecraft = stonecutter.current.version

version = "${prop("mod.version")}+$minecraft-playtesting"
base {
	archivesName.set("${prop("mod.id")}-common")
}

architectury.common(stonecutter.tree.branches.mapNotNull {
	if (stonecutter.current.project !in it) null
	else it.project.prop("loom.platform")
})

loom {
	silentMojangMappingsLicense()
	accessWidenerPath = rootProject.file("src/main/resources/${prop("mod.id")}.accesswidener")

	decompilers {
		get("vineflower").apply { // Adds names to lambdas - useful for mixins
			options.put("mark-corresponding-synthetics", "1")
		}
	}
}

repositories {
	maven("https://maven.parchmentmc.org/")

	maven("https://maven.terraformersmc.com/")
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
//		mappings("dev.lambdaurora:${versionProp("yalmm")}")
	})
	modImplementation("net.fabricmc:fabric-loader:${versionProp("fabric_loader")}")

	// Mod implementations
	modCompileOnly("dev.isxander:yet-another-config-lib:${versionProp("yacl_version")}-fabric")
}

tasks.processResources {
	applyProperties(project, listOf("${prop("mod.id")}-common.mixins.json"))
}

// The installed Stonecutter plugin (0.5.1) has no global text-replacement API, so for versions before 1.21.11
// the sources are rewritten here, at build time, into a generated directory that is compiled instead.
//  - 1.21.11 renamed ResourceLocation to Identifier
//  - 1.21.2 renamed UseAnim to ItemUseAnimation
//  - Camera#getPosition() became Camera#position()
if (stonecutter.eval(minecraft, "<1.21.11")) {
	val renameToLegacyNames = fun(line: String): String = line
		.replace(Regex("\\bIdentifier\\b"), "ResourceLocation")
		.replace(Regex("\\bItemUseAnimation\\b"), "UseAnim")
		.replace("camera.position()", "camera.getPosition()")

	val rewriteLegacySources = tasks.register<Sync>("rewriteLegacySources") {
		// Stonecutter writes the versioned, preprocessed source tree to
		// versions/<minecraft>/build/chiseledSrc before Java compilation.  Do not
		// copy rootProject/src here: that is the unprocessed source and still
		// contains the version guards intended for other Minecraft versions.
		from(layout.buildDirectory.dir("chiseledSrc/main/java")) {
			include("**/*.java")
			filter { line: String -> renameToLegacyNames(line) }
		}
		into(layout.buildDirectory.dir("generated/legacy-sources"))
	}

	sourceSets.named("main") {
		java.setSrcDirs(listOf(rewriteLegacySources))
	}
	tasks.matching { it.name == "compileJava" || it.name == "sourcesJar" }.configureEach {
		dependsOn(rewriteLegacySources)
	}
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