package com.example.common.events

import com.example.common.model.OwnerId
import kotlinx.serialization.Serializable

@Serializable
data class CartCreated(
    val ownerId: OwnerId,
    val cartId: String,
)