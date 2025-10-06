
// pluginManagement {
//    repositories {
//        gradlePluginPortal() // Gradle's default plugin repository
//        mavenCentral() // General-purpose repository
//        maven { setUrl("https://maven.architectury.dev/") }
//        maven { setUrl("https://maven.fabricmc.net/") }
//        maven { setUrl("https://maven.neoforged.net/releases/") }
//    }
// }

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://maven.architectury.dev/") }
        maven { setUrl("https://maven.fabricmc.net/") }
        maven { setUrl("https://maven.neoforged.net/releases/") }
    }
}

plugins {
    id("dev.panuszewski.typesafe-conventions") version "0.8.1"
}
