import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import org.gradle.api.Project
import org.gradle.language.jvm.tasks.ProcessResources

fun Project.prop(key: String): String? = findProperty(key)?.toString()

fun Project.versionProp(key: String): String? {
    val specificKey = "version.${stonecutter(project).current.version.replace(".", "_")}.$key"
    val wildcardKey = "version.*.$key"

    return when {
        project.prop(specificKey) != null -> project.prop(specificKey)!!
        project.prop(wildcardKey) != null -> project.prop(wildcardKey)!!
        else -> throw IllegalArgumentException("Missing '$specificKey' or '$wildcardKey'")
    }
}

fun Project.versionPropOrNull(key: String): String? {
    val specificKey = "version.${stonecutter(project).current.version.replace(".", "_")}.$key"
    val wildcardKey = "version.*.$key"

    return when {
        project.prop(specificKey) != null -> project.prop(specificKey)!!
        project.prop(wildcardKey) != null -> project.prop(wildcardKey)!!
        else -> null
    }
}

fun ProcessResources.applyProperties(project: Project, files: Iterable<String>) {
    val props = mutableMapOf(
            "mod_version" to project.prop("mod.version"),
            "mod_group" to project.prop("mod.group"),
            "mod_id" to project.prop("mod.id"),

            "mod_name" to project.prop("mod.name"),
            "mod_description" to project.prop("mod.description"),
            "mod_license" to project.prop("mod.license"),

            "minecraft_version" to stonecutter(project).current.version,
    )
    fun addNullable(name: String, value: String?) {
        if (value != null) props[name] = value
    }

    addNullable("fabric_loader_version", project.versionPropOrNull("fabric_loader"))
    addNullable("forge_loader_version", project.versionPropOrNull("forge_loader"))
    addNullable("neoforge_loader_version", project.versionPropOrNull("neoforge_loader"))

    inputs.properties(props)
    filesMatching(files) {
        expand(props)
    }
}

fun stonecutter(project: Project): StonecutterBuildExtension {
    return requireNotNull(project.extensions.findByType(StonecutterBuildExtension::class.java)) { "Stonecutter build extension not found" }
}
