import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    java
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "1.6-SNAPSHOT" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.diffplug.spotless") version "7.0.0.BETA1"
}

architectury {
    minecraft = property("minecraft_version").toString()
}

spotless {
    kotlin {
        ktlint()
    }
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "architectury-plugin")

    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")

    dependencies {
        "minecraft"("com.mojang:minecraft:${property("minecraft_version")}")

        // The following line declares the yarn mappings you may select this one as well.
        @Suppress("UnstableApiUsage")
        "mappings"(
            loom.layered {
                mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
                mappings("dev.architectury:yarn-mappings-patch-neoforge:1.20.6+build.4")
            },
        )
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "maven-publish")

    base.archivesName.set(rootProject.property("archives_base_name").toString())
    version = rootProject.property("mod_version").toString()
    group = rootProject.property("group").toString()

    repositories {
        // Add repositories to retrieve artifacts from in here.
        // You should only use this when depending on other mods because
        // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
        // See https://docs.gradle.org/current/userguide/declaring_repositories.html
        // for more information about repositories.
    }

    dependencies {
        compileOnly(kotlin("stdlib"))
        compileOnly(kotlin("serialization"))
        compileOnly(kotlin("reflect"))
        compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
        compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1-Beta")
    }

    java {
        withSourcesJar()
    }
}
