package com.example.kafka

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties

class MessageProducer(private val bootstrapServers: String) {
    private val producer: KafkaProducer<String, String>

    init {
        val props = Properties().apply {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
            put(ProducerConfig.ACKS_CONFIG, "all")
        }
        producer = KafkaProducer(props)
    }

    fun sendMessage(topic: String, key: String?, message: String) {
        val record = ProducerRecord(topic, key, message)
        producer.send(record) { metadata, exception ->
            if (exception != null) {
                println("Error sending message: ${exception.message}")
            } else {
                println("Message sent to topic ${metadata.topic()} partition ${metadata.partition()}")
            }
        }
    }

    fun close() {
        producer.close()
    }
}