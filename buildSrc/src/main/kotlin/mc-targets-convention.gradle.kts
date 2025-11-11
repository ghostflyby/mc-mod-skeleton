import dev.architectury.plugin.ModLoader

plugins {
    id("sub-projects-convention")
    alias(libs.plugins.architectury.plugin)
    java
//    alias(libs.plugins.kotlin.jvm)
//    alias(libs.plugins.architectury.plugin)
//    alias(libs.plugins.architectury.loom)
    alias(libs.plugins.shadow)
}

val id =
    project.projectDir.name.run {
        when {
            startsWith("fabric") -> ModLoader.FABRIC
            startsWith("neoforge") -> ModLoader.NEOFORGE
            else -> throw IllegalArgumentException("Project directory name must start with 'fabric' or 'neoforge'")
        }
    }

architectury {
    platformSetupLoomIde()
    loader(id)
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating

configurations {
    compileClasspath.get().extendsFrom(common)
    runtimeClasspath.get().extendsFrom(common)
    val dev = configurations.getByName("development${id.titledId}")
    dev.extendsFrom(common)
}

dependencies {
    common(project(":common", "namedElements")) { isTransitive = false }
    shadowCommon(project(":common", "transformProduction${id.titledId}")) { isTransitive = false }
}

tasks.shadowJar {
    exclude("architectury.common.json")
    configurations = listOf(shadowCommon)
    archiveClassifier.set("dev-shadow")
    destinationDirectory.set(layout.projectDirectory.dir("devlibs"))
}

tasks.remapJar {
    inputFile.set(tasks.shadowJar.flatMap { it.archiveFile })

    injectAccessWidener.set(true)

    val metadata = listOf(project.name, libs.versions.minecraft.get()).joinToString("-")
    archiveAppendix.set(metadata)
}

tasks.jar {
    archiveClassifier.set("dev")
}

tasks.named<Jar>("sourcesJar") {
    val commonSources = project(":common").tasks.named<Jar>("sourcesJar")
    dependsOn(commonSources)
    from(commonSources.flatMap { it.archiveFile }.map { zipTree(it) })
}

tasks.remapSourcesJar {
    val metadata = listOf(project.name, "${libs.versions.minecraft.get()}").joinToString("-")
    archiveAppendix.set(metadata)
}
