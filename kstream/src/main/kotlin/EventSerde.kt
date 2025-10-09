import com.example.common.events.Event
import com.example.common.serde.json
import kotlinx.serialization.encodeToString
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.Serializer

class EventSerde : Serdes.WrapperSerde<Event> {
    constructor() : super(EventSerializer(), EventDeserializer())
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