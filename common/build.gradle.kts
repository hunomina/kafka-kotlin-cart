plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.example.common"
version = "0.0.1"

dependencies {
    implementation(libs.ktor.serialization.kotlinx.json)
    testImplementation(libs.kotlin.test.junit)
}
