package com.example.common.events

import com.example.common.model.ProductId
import kotlinx.serialization.Serializable

@Serializable
data class ProductRemovedFromCart (
    val productId: ProductId,
    val quantity: Int,
): Event()