package io.github.victorhsr.tracking.kafka.infrastructure.kafka

import io.github.victorhsr.tracking.core.TrackingData
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory

@Configuration
class KafkaConsumerConfiguration {

    @Bean
    fun consumerFactory(@Value("\${kafka.bootstrap-servers}") bootstrapServers: String): ConsumerFactory<String, TrackingData> {
        val props: MutableMap<String, Any> = HashMap()

        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = TrackingDataDeserializer::class.java

        return DefaultKafkaConsumerFactory(props)
    }

    @Bean("trackingKListenerContainerFactory")
    fun kafkaListenerContainerFactory(consumerFactory: ConsumerFactory<String, TrackingData>): ConcurrentKafkaListenerContainerFactory<String, TrackingData> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, TrackingData>()
        factory.consumerFactory = consumerFactory
        return factory
    }
}