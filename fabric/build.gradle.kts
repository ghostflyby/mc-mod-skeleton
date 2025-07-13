plugins {
    `mc-targets-convention`
}

dependencies {
    modRuntimeOnly(libs.fabric.loader)
    modApi(libs.fabric.api)
    // Remove the next line if you don't want to depend on the API
    // modApi("dev.architectury:architectury-fabric:${rootProject.property("architectury_version")}")

    // Fabric Kotlin
    modRuntimeOnly(libs.fabric.kotlin)
}

val tokens =
    mapOf(
        "version" to version,
        "mod_id" to providers.gradleProperty("mod_id").get(),
        "minecraft_version" to libs.versions.minecraft.get(),
        "fabric_kotlin_version" to
            libs.versions.fabric.kotlin
                .get(),
        "fabric_api_version" to
            libs.versions.fabric.api
                .get(),
    )
tasks.processResources {
    inputs.properties(tokens)

    filesMatching("fabric.mod.json") {
        expand(tokens)
    }
}
