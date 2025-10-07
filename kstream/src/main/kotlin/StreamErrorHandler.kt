import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.streams.errors.DeserializationExceptionHandler
import org.apache.kafka.streams.errors.ProductionExceptionHandler
import org.apache.kafka.streams.processor.ProcessorContext
import java.lang.Exception

class CustomDeserializationExceptionHandler : DeserializationExceptionHandler {
    override fun handle(
        context: ProcessorContext?,
        record: ConsumerRecord<ByteArray?, ByteArray?>?,
        exception: Exception?
    ): DeserializationExceptionHandler.DeserializationHandlerResponse {
        println("====> Deserialization exception")
        println("💣 $context")
        println("💣 $record")
        println("💣 $exception")
        println("<==== Deserialization exception")

        return DeserializationExceptionHandler.DeserializationHandlerResponse.CONTINUE
    }

    override fun configure(configs: Map<String?, *>?) {}
}

class CustomProductionExceptionHandler : ProductionExceptionHandler {
    override fun handle(
        record: ProducerRecord<ByteArray?, ByteArray?>?,
        exception: Exception?
    ): ProductionExceptionHandler.ProductionExceptionHandlerResponse {
        println("====> Production exception")
        println("💣 $record")
        println("💣 $exception")
        println("<==== Production exception")

        return ProductionExceptionHandler.ProductionExceptionHandlerResponse.FAIL
    }

    override fun configure(configs: Map<String?, *>?) {}
}