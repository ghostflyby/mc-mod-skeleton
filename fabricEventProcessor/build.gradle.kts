plugins {
    kotlin("jvm")
}

group = "net.examplemod"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    ksp("dev.zacsweers.autoservice:auto-service-ksp:1.2.0")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${rootProject.property("fabric_api_version")}")

    compileOnly("com.google.auto.service:auto-service-annotations:1.1.1")
    implementation("com.google.devtools.ksp:symbol-processing-api:2.0.0-1.0.21")
}
