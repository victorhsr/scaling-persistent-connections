package io.github.victorhsr.tracking.kafka.infrastructure.kafka

import io.github.victorhsr.tracking.core.TrackingData
import io.github.victorhsr.tracking.kafka.infrastructure.json.jackson.getSystemObjectMapper
import org.apache.kafka.common.serialization.Deserializer

open class JsonDeserializer<T>(private val className: Class<T>) : Deserializer<T> {

    companion object {
        private val OBJECT_MAPPER = getSystemObjectMapper()
    }

    override fun deserialize(topic: String, data: ByteArray): T {
        return OBJECT_MAPPER.readValue(data, className)
    }

}

class TrackingDataDeserializer : JsonDeserializer<TrackingData>(TrackingData::class.java)