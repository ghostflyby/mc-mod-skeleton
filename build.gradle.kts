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
        parchment("org.parchmentmc.data:parchment-1.20.6:2024.06.16@zip")
      },
    )

    api(libs.kotlinx.serialization)
    api(libs.kotlinx.coroutines)
    api(kotlin("stdlib"))
    api(kotlin("serialization"))
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
