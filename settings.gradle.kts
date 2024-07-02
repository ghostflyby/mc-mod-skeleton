pluginManagement {
    repositories {
        mavenCentral()
        maven { setUrl("https://maven.fabricmc.net/") }
        maven { setUrl("https://maven.architectury.dev/") }
        maven { setUrl("https://maven.neoforged.net/releases/") }
        gradlePluginPortal()
    }
}

include("common")
include("neoforge")
include("fabric")

include("fabricEventProcessor")

rootProject.name = "examplemod"
