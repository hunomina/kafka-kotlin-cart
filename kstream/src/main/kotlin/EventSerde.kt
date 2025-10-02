import com.example.common.events.Event
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.Serializer

class EventSerde : Serdes.WrapperSerde<Event> {
    constructor() : super(EventSerializer(), EventDeserializer())
}

private val json = Json(Json.Default) {
    classDiscriminator = "type"   // JSON field to decide which subclass to use
    ignoreUnknownKeys = true      // ignore unexpected fields
}

class EventSerializer : Serializer<Event> {
    override fun serialize(topic: String?, data: Event?): ByteArray? {
        if (data === null) return null
        return json.encodeToString(data).toByteArray()
    }
}

class EventDeserializer : Deserializer<Event> {
    override fun deserialize(topic: String?, data: ByteArray?): Event? {
        if (data == null) return null
        return json.decodeFromString<Event>(String(data))
    }
}