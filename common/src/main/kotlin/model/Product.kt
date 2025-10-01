package com.example.common.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
class Product(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var description: String,
    var price: Double
) {
}