import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  java
  alias(libs.plugins.kotlin)
  alias(libs.plugins.kotlin.serialization) apply false
  alias(libs.plugins.architectury.plugin)
  alias(libs.plugins.architectury.loom) apply false
  alias(libs.plugins.spotless)
}

architectury {
  minecraft = libs.versions.minecraft.get()
}

subprojects {
  fun applyPlugin(plugin: Provider<PluginDependency>) {
    apply(plugin = plugin.get().pluginId)
  }
  apply(plugin = "java")
  val libs = rootProject.libs
  applyPlugin(libs.plugins.kotlin.asProvider())
  applyPlugin(libs.plugins.kotlin.serialization)
  applyPlugin(libs.plugins.architectury.plugin)
  applyPlugin(libs.plugins.architectury.loom)
  applyPlugin(libs.plugins.spotless)

  base.archivesName.set(rootProject.property("archives_base_name").toString())
  version = rootProject.property("mod_version").toString()
  group = rootProject.property("group").toString()

  val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")

  repositories {
    maven {
      name = "ParchmentMC"
      setUrl("https://maven.parchmentmc.org")
    }
  }

  dependencies {
    "minecraft"(libs.minecraft)

    // The following line declares the yarn mappings you may select this one as well.
    @Suppress("UnstableApiUsage")
    "mappings"(
      loom.layered {
        officialMojangMappings()
        parchment(libs.parchment.get().toString())
      },
    )

    api(libs.kotlinx.serialization)
    api(libs.kotlinx.coroutines)
    api(kotlin("stdlib"))
    api(kotlin("reflect"))

    testImplementation(kotlin("test"))
  }

  java {
    withSourcesJar()
    targetCompatibility = JavaVersion.toVersion(rootProject.property("jvm_version")!!)
    sourceCompatibility = targetCompatibility
  }

  kotlin {
    target {
      @OptIn(ExperimentalKotlinGradlePluginApi::class)
      compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(rootProject.property("jvm_version").toString()))
      }
    }
  }
}

allprojects {

  spotless {
    kotlin {
      ktlint()
    }
    kotlinGradle {
      ktlint()
    }
  }
}

val targetProjects
  get() =
    rootProject
      .property("enabled_platforms")
      .toString()
      .split(",")
      .map { project(it) }

fun targetProjects(action: Action<Project>) {
  targetProjects
    .forEach {
      action.execute(it)
    }
}

tasks.create("githubRelease") {
  group = "publishing"
  description = "Publishes the mod to GitHub Releases"

  val dependencyTasks =
    targetProjects.map { it.tasks.getByName("remapJar") } + targetProjects.map { it.tasks.getByName("sourcesJar") }

  dependencyTasks.forEach { dependsOn(it) }

  fun getOutputForRelease() = dependencyTasks.flatMap { it.outputs.files }.map { it.absolutePath }

  doFirst {
    System.getenv("CI") ?: logger.error("This task should only be run in CI")
    System.getenv("TAG") ?: logger.error("TAG environment variable not set")
  }

  doLast {

    val tag = System.getenv("TAG").replace("refs/tags/", "")
    exec {
      getOutputForRelease().forEach {
        println(it)
      }
      commandLine =
        listOf(
          "gh",
          "release",
          "create",
          tag,
        ) +
        getOutputForRelease()
    }
  }
}
