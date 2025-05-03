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
  inputs.property("group", rootProject.property("group"))
  inputs.property("version", project.version)
  inputs.property("mod_id", rootProject.property("mod_id"))
  inputs.property("minecraft_version", libs.versions.minecraft.get())
  inputs.property("fabric_kotlin_version", libs.versions.fabric.kotlin.get())
  inputs.property("fabric_api_version", libs.versions.fabric.api.get())
  inputs.file("src/main/resources/fabric.mod.json")

  filesMatching("fabric.mod.json") {
    expand(
      mutableMapOf(
        Pair("group", rootProject.property("group")),
        Pair("version", project.version),
        Pair("mod_id", rootProject.property("mod_id")),
        Pair("minecraft_version", libs.versions.minecraft.get()),
        Pair(
          "fabric_kotlin_version",
          libs.versions.fabric.kotlin
            .get(),
        ),
        Pair(
          "fabric_api_version",
          libs.versions.fabric.api
            .get(),
        ),
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

  val metadata = listOf(project.name,"mc${libs.versions.minecraft.get()}").joinToString(".")
  archiveVersion.set("$version+$metadata")
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
