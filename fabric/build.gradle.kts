plugins {
  id("com.github.johnrengelman.shadow")
}

architectury {
  platformSetupLoomIde()
  fabric()
}

repositories {
  maven {
    name = "Terraformers"
    setUrl("https://maven.terraformersmc.com/")
  }
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
  modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")
  modApi("net.fabricmc.fabric-api:fabric-api:${rootProject.property("fabric_api_version")}")
  // Remove the next line if you don't want to depend on the API
  // modApi("dev.architectury:architectury-fabric:${rootProject.property("architectury_version")}")


  common(project(":common", "namedElements")) { isTransitive = false }
  shadowCommon(project(":common", "transformProductionFabric")) { isTransitive = false }

  // Fabric Kotlin
  modRuntimeOnly("net.fabricmc:fabric-language-kotlin:${rootProject.property("fabric_kotlin_version")}")
}

tasks.processResources {
  inputs.property("group", rootProject.property("group"))
  inputs.property("version", project.version)

  filesMatching("fabric.mod.json") {
    expand(
      mutableMapOf(
        Pair("group", rootProject.property("group")),
        Pair("version", project.version),

        Pair("mod_id", rootProject.property("mod_id")),
        Pair("minecraft_version", rootProject.property("minecraft_version")),
        Pair("architectury_version", rootProject.property("architectury_version")),
        Pair("fabric_kotlin_version", rootProject.property("fabric_kotlin_version"))
      )
    )
  }
}

tasks.shadowJar {
  exclude("architectury.common.json")
  configurations = listOf(shadowCommon)
  archiveClassifier.set("dev-shadow")
}

tasks.remapJar {
  injectAccessWidener.set(true)
  inputFile.set(tasks.shadowJar.get().archiveFile)
  dependsOn(tasks.shadowJar)
  archiveClassifier.set(null as String?)
}

tasks.jar {
  archiveClassifier.set("dev")
}

tasks.sourcesJar {
  val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
  dependsOn(commonSources)
  from(commonSources.archiveFile.map { zipTree(it) })
}
