package io.github.victorhsr.tracking.kafka.application

import io.github.victorhsr.tracking.core.TrackingData
import io.github.victorhsr.tracking.core.TrackingDataManager
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class KafkaTrackingDataListener(private val trackingDataManager: TrackingDataManager) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(KafkaTrackingDataListener::class.java)
    }

    @KafkaListener(topicPattern = "#{trackingTopicNameResolver.resolveTrackingTopicPatternMatching()}", groupId = "\${random.uuid}", containerFactory = "trackingKListenerContainerFactory")
    fun listenForTrackingData(@Header(KafkaHeaders.RECEIVED_TOPIC) topic: String, @Payload trackingData: TrackingData) =
        runBlocking(CoroutineName("kotlin-tracking-consumer")) {
            LOGGER.info("Receiving message from topic {}, content: {}", topic, trackingData)
            this@KafkaTrackingDataListener.trackingDataManager.pushTrackingDataToActiveStream(trackingData)
        }

}