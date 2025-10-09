package com.example

import com.example.common.events.Event
import com.example.common.events.ProductAddedToCart
import com.example.common.events.ProductCreated
import com.example.common.serde.json
import com.example.kafka.MessageConsumer
import com.example.kafka.MessageProducer
import io.ktor.events.EventDefinition
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.util.*
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

fun Application.configureKafka() {
    install(KafkaPlugin)
}

val ProducerKey = AttributeKey<MessageProducer>("MessageProducer")
val ConsumerKey = AttributeKey<MessageConsumer>("MessageConsumer")

val ProductCreatedEventDefinition = EventDefinition<ProductCreated>()
val ProductAddedToCartEventDefinition = EventDefinition<ProductAddedToCart>()

val eventDefinitionMap: Map<KClass<out Event>, EventDefinition<out Event>> = mapOf(
    ProductCreated::class to ProductCreatedEventDefinition,
    ProductAddedToCart::class to ProductAddedToCartEventDefinition
)

val KafkaPlugin = createApplicationPlugin(name = "KafkaPlugin") {
    val bootstrapServers = "localhost:9092"
    val producer = MessageProducer(bootstrapServers)
    val consumer = MessageConsumer(
        bootstrapServers = bootstrapServers, groupId = "ktor-consumer-group", topics = listOf("product")
    )

    // Store in application attributes
    application.attributes.put(ProducerKey, producer)
    application.attributes.put(ConsumerKey, consumer)

    // Start consumer
    application.launch {
        consumer.startConsuming { record ->
            println("Received: key=${record.key()}, value=${record.value()}, offset=${record.offset()}")

            val event = json.decodeFromString<Event>(record.value())
            val eventDefinition = eventDefinitionMap[event::class] ?: throw Exception("Event not supported")

            application.monitor.raise(eventDefinition as EventDefinition<Event>, event)
        }
    }

    // Cleanup on shutdown
    on(MonitoringEvent(ApplicationStopped)) { _ ->
        println("Application stopping! Closing Kafka consumer and producer...")
        producer.close()
        consumer.close()
    }
}

enum class Topic {
    PRODUCT
}

