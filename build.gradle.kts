import net.fabricmc.loom.api.LoomGradleExtensionAPI

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

    dependencies {
        "minecraft"(libs.minecraft)

        // The following line declares the yarn mappings you may select this one as well.
        @Suppress("UnstableApiUsage")
        "mappings"(
            loom.layered {
                mappings(libs.yarn.mapping)
                mappings(libs.yarn.patch)
            },
        )

        api(libs.kotlinx.serialization)
        api(libs.kotlinx.coroutines)
        api(kotlin("stdlib"))
        api(kotlin("serialization"))
        api(kotlin("reflect"))
    }

    java {
        withSourcesJar()
    }

    spotless {
        kotlin {
            ktlint()
        }
    }
}
