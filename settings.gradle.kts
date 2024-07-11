pluginManagement {
    repositories {
        mavenCentral()
        maven { setUrl("https://maven.fabricmc.net/") }
        maven { setUrl("https://maven.architectury.dev/") }
        maven { setUrl("https://maven.neoforged.net/releases/") }
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

include("common")
include("neoforge")
include("fabric")

rootProject.name = "examplemod"
