import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("all-projects-convention")
    java
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.architectury.plugin)
    alias(libs.plugins.architectury.loom)
}

base.archivesName.set(rootProject.property("archives_base_name").toString())
version = rootProject.property("version").toString()
group = rootProject.property("group").toString()
dependencies {
    "minecraft"(libs.minecraft)

    // The following line declares the yarn mappings you may select this one as well.
    @Suppress("UnstableApiUsage")
    "mappings"(
        loom.layered {
            mappings("net.fabricmc:yarn:${libs.versions.yarn.asProvider().get()}")
            mappings(libs.yarn.patch)
        },
    )

    api(libs.kotlinx.serialization)
    api(libs.kotlinx.coroutines)
    api(kotlin("stdlib"))
    api(kotlin("reflect"))

    testImplementation(kotlin("test"))
}

@Suppress("LocalVariableName", "ktlint:standard:property-naming")
val jvm_version: String by properties

java {
    withSourcesJar()
    targetCompatibility = JavaVersion.toVersion(jvm_version)
    sourceCompatibility = targetCompatibility
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(jvm_version))
    }
}
