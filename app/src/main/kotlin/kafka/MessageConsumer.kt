package com.example.kafka

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.*

class MessageConsumer(
    private val bootstrapServers: String,
    private val groupId: String,
    topics: List<String>
) {
    private val consumer: KafkaConsumer<String, String> = KafkaConsumer(Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    })

    init {
        consumer.subscribe(topics)
    }

    suspend fun startConsuming(onMessage: suspend (ConsumerRecord<String, String>) -> Unit) = coroutineScope {
        launch(Dispatchers.IO) {
            try {
                while (isActive) {
                    val records = consumer.poll(Duration.ofMillis(1000))
                    records.forEach { record -> onMessage(record) }
                }
            } catch (e: Exception) {
                println("Consumer error: ${e.message}")
            } finally {
                consumer.close()
            }
        }
    }

    fun close() {
        consumer.close()
    }
}