package io.github.victorhsr.tracking.kafka.infrastructure.json.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.LocalDateTime

fun getSystemObjectMapper(): ObjectMapper {
    val module = JavaTimeModule()

    module.addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer())
    module.addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer())

    val objectMapper = jacksonObjectMapper()
    objectMapper.registerModule(module)

    return objectMapper
}