import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    java
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.architectury.plugin)
    alias(libs.plugins.architectury.loom) apply false
    alias(libs.plugins.spotless)
}

architectury {
    minecraft = libs.versions.minecraft.get()
}

spotless {
    kotlin {
        ktlint()
    }
}

subprojects {
    apply(
        plugin =
            rootProject.libs.plugins.architectury.loom
                .get()
                .pluginId,
    )

    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")

    dependencies {
        with(rootProject) {
            "minecraft"(libs.minecraft)

            // The following line declares the yarn mappings you may select this one as well.
            @Suppress("UnstableApiUsage")
            "mappings"(
                loom.layered {
                    mappings(libs.yarn.mapping)
                    mappings(libs.yarn.patch)
                },
            )
        }
    }
}

allprojects {
    fun applyPlugin(plugin: Provider<PluginDependency>) {
        apply(plugin = plugin.get().pluginId)
    }
    apply(plugin = "java")
    with(rootProject) {
        applyPlugin(libs.plugins.kotlin.asProvider())
        applyPlugin(libs.plugins.kotlin.serialization)
        applyPlugin(libs.plugins.architectury.plugin)
    }

    base.archivesName.set(rootProject.property("archives_base_name").toString())
    version = rootProject.property("mod_version").toString()
    group = rootProject.property("group").toString()

    dependencies {
        api(kotlin("stdlib"))
        api(kotlin("serialization"))
        api(kotlin("reflect"))
        with(rootProject) {
            api(libs.kotlinx.serialization)
            api(libs.kotlinx.coroutines)
        }
    }

    java {
        withSourcesJar()
    }
}
