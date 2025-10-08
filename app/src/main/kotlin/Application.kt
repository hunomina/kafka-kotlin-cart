package com.example

import com.example.common.events.Event
import com.example.common.events.ProductAddedToCart
import com.example.common.events.ProductCreated
import com.example.common.model.ProductId
import com.example.ui.inputs.CreateProductInput
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
    configureKafka()

    routing {
        get("/health") {
            call.respondText("OK", status = HttpStatusCode.OK)
        }

        fun RoutingContext.publishToKafka(
            topic: Topic,
            key: String?,
            event: Event,
        ) {
            call.application.attributes[ProducerKey]
                .sendMessage(topic.toString().lowercase(), key, Json.encodeToString(event))
        }

        route("/api") {
            post("/products") {
                val productInput: CreateProductInput
                try {
                    productInput = call.receive<CreateProductInput>()
                } catch (e: Throwable) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "${e::class} - Invalid request payload: ${e.message}, ${e.printStackTrace()}"
                    )
                    return@post
                }

                val product = productInput.toProduct()
                val event = ProductCreated.fromModel(product)

                publishToKafka(Topic.PRODUCT, product.id, event)

                call.respondText("OK", status = HttpStatusCode.Created)
            }
        }

        // test endpoint
        post("/send") {
            val productId = call.receiveText()
            val event = ProductAddedToCart(ProductId(productId), 10)
            publishToKafka(Topic.PRODUCT, event.productId.value, event)
            call.respondText("Message sent", status = HttpStatusCode.OK)
        }
    }
}