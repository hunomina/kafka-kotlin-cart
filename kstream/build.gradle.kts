plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
    id("application")
}

group = "com.example.kstream"
version = "0.0.1"

application {
    mainClass.set("com.example.kstream.MainKt")
}

dependencies {
    implementation(project(":common"))

    implementation("org.apache.kafka:kafka-streams:3.8.0") // Kafka Streams API
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3") // JSON (if needed later)
    implementation("io.lettuce:lettuce-core:6.8.1.RELEASE") // Redis client
}