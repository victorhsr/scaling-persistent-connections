package io.github.victorhsr.tracking.kafka.infrastructure.kafka

import io.github.victorhsr.tracking.core.TrackingData
import io.github.victorhsr.tracking.kafka.infrastructure.json.jackson.getSystemObjectMapper
import org.apache.kafka.common.serialization.Serializer

open class JsonSerializer<T>(private val className: Class<T>) : Serializer<T> {

    companion object {
        private val OBJECT_MAPPER = getSystemObjectMapper()
    }

    override fun serialize(topic: String, data: T): ByteArray {
        return OBJECT_MAPPER.writeValueAsBytes(data)
    }

}

class TrackingDataSerializer : JsonSerializer<TrackingData>(TrackingData::class.java)