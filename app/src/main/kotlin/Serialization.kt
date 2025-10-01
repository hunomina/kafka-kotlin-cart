package com.example

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

var json = Json(Json.Default) {
    classDiscriminator = "type"   // JSON field to decide which subclass to use
    ignoreUnknownKeys = true      // ignore unexpected fields
}


fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(json)
    }
    routing {
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}
