plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.1") // Última versión disponible
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(15) // Cambiar a JDK 11 o 8, dependiendo de lo que prefieras
}
