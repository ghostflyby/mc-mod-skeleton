architectury {
  common(rootProject.property("enabled_platforms").toString().split(","))
}

loom {
  accessWidenerPath.set(file("src/main/resources/examplemod.accesswidener"))
}

dependencies {
  // We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
  // Do NOT use other classes from fabric loader
  modImplementation(libs.fabric.loader)
  // Remove the next line if you don't want to depend on the API
  // modApi("dev.architectury:architectury:${property("architectury_version")}")
}
