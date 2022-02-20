package io.github.victorhsr.tracking.kafka.infrastructure.tracking

import io.github.victorhsr.tracking.core.TrackingData
import io.github.victorhsr.tracking.core.TrackingDataPublisher
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaTrackingDataPublisher(
    private val kafkaTemplate: KafkaTemplate<String, TrackingData>,
    private val trackingTopicNameResolver: TrackingTopicNameResolver,
) : TrackingDataPublisher {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(KafkaTrackingDataPublisher::class.java)
    }

    override suspend fun publish(trackingData: TrackingData) {
        val trackTopic = this.trackingTopicNameResolver.resolveTrackingTopic(trackingData)
        LOGGER.info("Publishing tracking-data {} on topic {}", trackingData, trackTopic)
        val sendResult = this.kafkaTemplate.send(trackTopic, trackingData.id, trackingData).get()
        println("Tracking-data published on partition ${sendResult.recordMetadata.partition()}")
    }

}