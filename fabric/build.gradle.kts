plugins {
    alias(libs.plugins.shadow)
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating

configurations {
    compileClasspath.get().extendsFrom(common)
    runtimeClasspath.get().extendsFrom(common)
    val developmentFabric by getting
    developmentFabric.extendsFrom(common)
}

dependencies {
    modRuntimeOnly(libs.fabric.loader)
    modApi(libs.fabric.api)
    // Remove the next line if you don't want to depend on the API
    // modApi("dev.architectury:architectury-fabric:${rootProject.property("architectury_version")}")

    common(project(":common", "namedElements")) { isTransitive = false }
    shadowCommon(project(":common", "transformProductionFabric")) { isTransitive = false }

    // Fabric Kotlin
    modRuntimeOnly(libs.fabric.kotlin)
}

val tokens =
    mapOf(
        "version" to version,
        "mod_id" to providers.gradleProperty("mod_id"),
        "minecraft_version" to libs.versions.minecraft,
        "fabric_kotlin_version" to libs.versions.fabric.kotlin,
        "fabric_api_version" to libs.versions.fabric.api,
    )
tasks.processResources {
    inputs.properties(tokens)

    filesMatching("fabric.mod.json") {
        expand(tokens)
    }
}

tasks.shadowJar {
    exclude("architectury.common.json")
    configurations = listOf(shadowCommon)
    archiveClassifier.set("dev-shadow")
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

tasks.sourcesJar {
    val commonSources = project(":common").tasks.sourcesJar
    dependsOn(commonSources)
    from(commonSources.flatMap { it.archiveFile }.map { zipTree(it) })
}

tasks.remapSourcesJar {
    val metadata = listOf(project.name, "mc${libs.versions.minecraft.get()}").joinToString(".")
    archiveVersion.set("$version+$metadata")
}
