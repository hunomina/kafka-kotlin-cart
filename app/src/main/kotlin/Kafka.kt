package com.example

import com.example.kafka.MessageConsumer
import com.example.kafka.MessageProducer
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.util.*
import kotlinx.coroutines.launch

fun Application.configureKafka() {
    install(KafkaPlugin)
}

val ProducerKey = AttributeKey<MessageProducer>("MessageProducer")
val ConsumerKey = AttributeKey<MessageConsumer>("MessageConsumer")

val KafkaPlugin = createApplicationPlugin(name = "KafkaPlugin") {
    val bootstrapServers = "localhost:9092"
    val producer = MessageProducer(bootstrapServers)
    val consumer = MessageConsumer(
        bootstrapServers = bootstrapServers,
        groupId = "ktor-consumer-group",
        topics = listOf("product")
    )

    // Store in application attributes
    application.attributes.put(ProducerKey, producer)
    application.attributes.put(ConsumerKey, consumer)

    // Start consumer
    application.launch {
        consumer.startConsuming { record ->
            println("Received: key=${record.key()}, value=${record.value()}, offset=${record.offset()}")
        }
    }

    // Cleanup on shutdown
    on(MonitoringEvent(ApplicationStopped)) { application ->
        println("Application stopping! Closing Kafka consumer and producer...")
        producer.close()
        consumer.close()
    }
}

enum class Topic {
    PRODUCT
}