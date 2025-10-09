package com.example.common.serde

import kotlinx.serialization.json.Json

val json = Json(Json.Default) {
    classDiscriminator = "type"   // JSON field to decide which subclass to use
    ignoreUnknownKeys = true      // ignore unexpected fields
}
