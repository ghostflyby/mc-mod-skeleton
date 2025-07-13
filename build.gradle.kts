plugins {
    id(
        libs.plugins.architectury.plugin
            .get()
            .pluginId,
    )
    `all-projects-convention`
}

architectury {
    minecraft = libs.versions.minecraft.get()
}
