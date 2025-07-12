plugins {
    alias(libs.plugins.shadow)
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating

configurations {
    compileClasspath.get().extendsFrom(common)
    runtimeClasspath.get().extendsFrom(common)
    val developmentNeoForge by getting
    developmentNeoForge.extendsFrom(common)
}

repositories {
    maven {
        setUrl("https://maven.neoforged.net/releases/")
    }
    // KFF
    maven {
        name = "Kotlin for Forge"
        setUrl("https://thedarkcolour.github.io/KotlinForForge/")
    }
    // forgified fabric api
    maven {
        setUrl("https://maven.su5ed.dev/releases")
    }
}

dependencies {
    neoForge(libs.neoforge)
    // Remove the next line if you don't want to depend on the API
    // modApi("dev.architectury:architectury-neoforge:${rootProject.property("architectury_version")}")
    modApi(libs.forgified.fabric.api)

    common(project(":common", "namedElements")) { isTransitive = false }
    shadowCommon(project(":common", "transformProductionNeoForge")) { isTransitive = false }

    // Kotlin For Forge
    runtimeOnly(libs.kotlinforforge) {
        exclude(group = "net.neoforged.fancymodloader", module = "loader")
    }
}

val tokens =
    mapOf(
        "version" to version,
        "mod_id" to providers.gradleProperty("mod_id"),
        "minecraft_version" to libs.versions.minecraft,
        "kotlin_for_forge_version" to libs.versions.kotlinforforge,
    )
tasks.processResources {
    inputs.properties(tokens)

    filesMatching("META-INF/neoforge.mods.toml") {
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
