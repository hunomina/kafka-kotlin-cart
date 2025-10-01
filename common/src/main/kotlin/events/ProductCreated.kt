package com.example.common.events

import com.example.common.model.Product
import kotlinx.serialization.Serializable

@Serializable
data class ProductCreated(
    val id: String,
    val name: String,
    val description: String,
    val price: Double
) : Event() {
    companion object {
        fun fromModel(product: Product): ProductCreated {
            return ProductCreated(
                product.id,
                product.name,
                product.description,
                product.price
            )
        }
    }
}