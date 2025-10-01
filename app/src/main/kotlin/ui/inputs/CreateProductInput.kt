package com.example.ui.inputs

import com.example.common.model.Product
import kotlinx.serialization.Serializable

@Serializable
data class CreateProductInput (
    val name: String,
    val description: String,
    val price: Double,
) {
    fun toProduct(): Product {
        return Product(
            name = name,
            description = description,
            price = price,
        )
    }
}