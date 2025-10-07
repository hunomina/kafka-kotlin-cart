import com.example.common.events.Event
import com.example.common.events.ProductAddedToCart
import com.example.common.events.ProductRemovedFromCart
import com.example.common.model.ProductId
import io.lettuce.core.RedisClient
import io.lettuce.core.api.sync.RedisCommands
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import java.util.*

const val redisNamespaceTotal = "product:in-cart:count-total"
const val redisNamespaceUnique = "product:in-cart:count-unique"

fun main() {


    val redisClient = RedisClient.create("redis://127.0.0.1:6379")
    val redisConnection = redisClient.connect()
    val redis = redisConnection.sync()

    deleteRedisNamespace(redis, "$redisNamespaceTotal:*")
    deleteRedisNamespace(redis, "$redisNamespaceUnique:*")

    val builder = StreamsBuilder()

    builder
        .stream<String, Event>("product")
        // would be nice to not have to map here and instead configure a custom serializer in `props`
        //.mapValues { value ->
        //    println("value: $value")
        //    try {
        //        Json.decodeFromString<Event>(value)
        //    } catch (t: Throwable) {
        //        t.printStackTrace()
        //        null // throw away undecodable messages
        //    }
        //}
        .filter { _, value -> value is ProductAddedToCart || value is ProductRemovedFromCart }
        .foreach { _, value ->
            val productId: ProductId = when (value!!::class) {
                ProductAddedToCart::class -> (value as ProductAddedToCart).productId
                ProductRemovedFromCart::class -> (value as ProductRemovedFromCart).productId
                else -> return@foreach
            }

            redis.setnx("$redisNamespaceUnique:${productId.value}", "")

            val quantity = when (value::class) {
                ProductAddedToCart::class -> (value as ProductAddedToCart).quantity
                ProductRemovedFromCart::class -> -(value as ProductRemovedFromCart).quantity
                else -> return@foreach
            }.toLong()

            redis.incrby("$redisNamespaceTotal:${productId.value}", quantity)

            dumpRedis(redis)
        }

    val props = Properties().apply {
        put(StreamsConfig.APPLICATION_ID_CONFIG, "redis-aggregator")
        put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde::class.java.name)
        put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, EventSerde::class.java.name)
        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        put(
            StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
            CustomDeserializationExceptionHandler::class.java
        )
        put(
            StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG,
            CustomProductionExceptionHandler::class.java
        )
    }

    val streams = KafkaStreams(builder.build(), props)

    streams.setStateListener { newState, oldState ->
        println("⭐️ Kafka Streams state changed from $oldState to $newState")
    }

    onShutdown {
        streams.close()
        redisConnection.close()
        redisClient.shutdown()
    }

    streams.start()
}

fun deleteRedisNamespace(redis: RedisCommands<String?, String?>, namespace: String) {
    val keys = redis.keys(namespace)
    if (keys.isNotEmpty()) {
        redis.del(*keys.toTypedArray())
    }
    println("Deleted ${keys.size} keys in namespace $namespace")
}

fun dumpRedis(redis: RedisCommands<String?, String?>) {
    redis.keys("*").forEach { key -> println("$key: ${redis.get(key)}") }
    println("===================================")
}

fun onShutdown(task: Runnable) {
    Runtime.getRuntime().addShutdownHook(Thread(task))
}