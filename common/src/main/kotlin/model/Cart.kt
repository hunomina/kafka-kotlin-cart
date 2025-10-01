package com.example.common.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
class Cart(val id: String = UUID.randomUUID().toString(), val ownerId: OwnerId) {
    val items: HashMap<Product, Int> = HashMap()

    fun addItem(product: Product, quantity: Int) {
        items[product] = items.getOrDefault(product, 0) + quantity
    }

    fun removeItem(product: Product) {
        items.remove(product)
    }
}