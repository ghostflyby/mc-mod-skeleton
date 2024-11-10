pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    maven { setUrl("https://maven.architectury.dev/") }
    maven { setUrl("https://maven.fabricmc.net/") }
    maven { setUrl("https://maven.neoforged.net/releases/") }
  }
}

dependencyResolutionManagement {
  @Suppress("UnstableApiUsage")
  repositories {
    mavenCentral()
    maven {
      name = "ParchmentMC"
      setUrl("https://maven.parchmentmc.org")
    }
    maven {
      setUrl("https://maven.neoforged.net/releases/")
    }
    maven { setUrl("https://maven.architectury.dev/") }
    maven { setUrl("https://maven.fabricmc.net/") }
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
}

include(":common")
include(":neoforge")
include(":fabric")

rootProject.name = "examplemod"
