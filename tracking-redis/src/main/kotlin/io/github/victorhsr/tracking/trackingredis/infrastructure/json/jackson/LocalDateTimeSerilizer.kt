package io.github.victorhsr.tracking.trackingredis.infrastructure.json.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun localDateTimeToString(localDateTime: LocalDateTime, datePattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(datePattern)
    return localDateTime.format(formatter)
}

fun dateStringToLocalDateTime(localDateTime: String, datePattern: String): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern(datePattern)
    return LocalDateTime.parse(localDateTime, formatter)
}


class LocalDateTimeSerializer(t: Class<LocalDateTime?>? = null) : StdSerializer<LocalDateTime>(t) {

    override fun serialize(value: LocalDateTime, gen: JsonGenerator, provider: SerializerProvider) {
        val localDateTime: String = localDateTimeToString(value, LOCAL_DATE_TIME_FORMAT)
        gen.writeString(localDateTime)
    }

    companion object {
        const val LOCAL_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss"
    }
}

class LocalDateTimeDeserializer(t: Class<LocalDateTime?>? = null) : StdDeserializer<LocalDateTime?>(t) {

    override fun deserialize(jsonparser: JsonParser, ctxt: DeserializationContext): LocalDateTime {
        val date = jsonparser.text
        return dateStringToLocalDateTime(date, LocalDateTimeSerializer.LOCAL_DATE_TIME_FORMAT)
    }
}