package io.github.victorhsr.tracking.kafka.infrastructure.kafka

import io.github.victorhsr.tracking.core.TrackingData
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class KafkaProducerConfig() {

    @Bean
    fun producerFactory(@Value("\${kafka.bootstrap-servers}") bootstrapServers: String): ProducerFactory<String, TrackingData> {
        val configProps: MutableMap<String, Any> = HashMap()

        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = TrackingDataSerializer::class.java
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, TrackingData>): KafkaTemplate<String, TrackingData> {
        return KafkaTemplate(producerFactory)
    }

}