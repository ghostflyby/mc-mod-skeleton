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

@Suppress("UnstableApiUsage")
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


tasks.processResources {
  val modVersion = version.toString()
  val modId = rootProject.property("mod_id").toString()
  val mcVersion = libs.versions.minecraft.get()
  val kffVersion = libs.versions.kotlinforforge.get()
  inputs.property("version", modVersion)
  inputs.property("mod_id", modId)
  inputs.property("minecraft_version", mcVersion)
  inputs.property("kotlin_for_forge_version", kffVersion)
  inputs.file("src/main/resources/META-INF/neoforge.mods.toml")

  filesMatching("META-INF/neoforge.mods.toml") {
    expand(
      mapOf(
        "version" to modVersion,
        "mod_id" to modId,
        "minecraft_version" to mcVersion,
        "kotlin_for_forge_version" to kffVersion,
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
  dependsOn(tasks.shadowJar)

  inputFile.set(tasks.shadowJar.get().archiveFile)

  injectAccessWidener.set(true)

  val metadata = listOf(project.name, "mc${libs.versions.minecraft.get()}").joinToString(".")
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
