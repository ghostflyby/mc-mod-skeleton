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

tasks.processResources {
  inputs.property("group", rootProject.property("group"))
  inputs.property("version", project.version)

  filesMatching("META-INF/neoforge.mods.toml") {
    expand(
      mutableMapOf(
        Pair("group", rootProject.property("group")),
        Pair("version", project.version),
        Pair("mod_id", rootProject.property("mod_id")),
        Pair("minecraft_version", libs.versions.minecraft.get()),
        Pair("kotlin_for_forge_version", libs.versions.kotlinforforge.get()),
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
  injectAccessWidener.set(true)
  inputFile.set(tasks.shadowJar.get().archiveFile)
  dependsOn(tasks.shadowJar)
  archiveAppendix.set("${project.name}.mc${libs.versions.minecraft.get()}")
}

tasks.jar {
  archiveClassifier.set("dev")
}

tasks.sourcesJar {
  val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
  dependsOn(commonSources)
  archiveAppendix.set("${project.name}.mc${libs.versions.minecraft.get()}")
  from(commonSources.archiveFile.map { zipTree(it) })
}

tasks.remapSourcesJar {
  archiveAppendix.set("${project.name}.mc${libs.versions.minecraft.get()}")
}
