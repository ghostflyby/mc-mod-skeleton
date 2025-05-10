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

@Suppress("UnstableApiUsage")
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

tasks.processResources {
  val modVersion = project.version.toString()
  val modId = rootProject.property("mod_id").toString()
  val mcVersion = libs.versions.minecraft.get()
  val fabricKotlinVersion =
    libs.versions.fabric.kotlin
      .get()
  val fabricApiVersion =
    libs.versions.fabric.api
      .get()

  inputs.property("version", modVersion)
  inputs.property("mod_id", modId)
  inputs.property("minecraft_version", mcVersion)
  inputs.property("fabric_kotlin_version", fabricKotlinVersion)
  inputs.property("fabric_api_version", fabricApiVersion)
  inputs.file("src/main/resources/fabric.mod.json")

  filesMatching("fabric.mod.json") {
    expand(
      mapOf(
        "version" to modVersion,
        "mod_id" to modId,
        "minecraft_version" to mcVersion,
        "fabric_kotlin_version" to fabricKotlinVersion,
        "fabric_api_version" to fabricApiVersion,
      ),
    )
  }
}

tasks.shadowJar {
  exclude("architectury.common.json")
  configurations = listOf(shadowCommon)
  archiveClassifier.set("dev-shadow")
}

tasks.remapJar {
  dependsOn(tasks.shadowJar)

  inputFile.set(tasks.shadowJar.get().archiveFile)

  injectAccessWidener.set(true)

  val metadata = listOf(project.name, libs.versions.minecraft.get()).joinToString("-")
  archiveAppendix.set(metadata)
}

tasks.jar {
  archiveClassifier.set("dev")
}

tasks.sourcesJar {
  val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
  dependsOn(commonSources)
  from(commonSources.archiveFile.map { zipTree(it) })
}

tasks.remapSourcesJar {
  val metadata = listOf(project.name, "mc${libs.versions.minecraft.get()}").joinToString(".")
  archiveVersion.set("$version+$metadata")
}
