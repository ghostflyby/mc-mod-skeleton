plugins {
    `mc-targets-convention`
}

repositories {
    maven {
        setUrl("https://maven.neoforged.net/releases/")
    }
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

dependencies {
    neoForge(libs.neoforge)
    // Remove the next line if you don't want to depend on the API
    // modApi("dev.architectury:architectury-neoforge:${rootProject.property("architectury_version")}")
    modApi(libs.forgified.fabric.api)

    // Kotlin For Forge
    runtimeOnly(libs.kotlinforforge) {
        exclude(group = "net.neoforged.fancymodloader", module = "loader")
    }
}

val tokens =
    mapOf(
        "version" to version,
        "mod_id" to providers.gradleProperty("mod_id").get(),
        "minecraft_version" to libs.versions.minecraft.get(),
        "kotlin_for_forge_version" to libs.versions.kotlinforforge.get(),
    )
tasks.processResources {
    inputs.properties(tokens)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(tokens)
    }
}
