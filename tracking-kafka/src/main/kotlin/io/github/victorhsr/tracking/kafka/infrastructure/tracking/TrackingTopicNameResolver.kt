package io.github.victorhsr.tracking.kafka.infrastructure.tracking

import io.github.victorhsr.tracking.core.TrackingData
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component("trackingTopicNameResolver")
class TrackingTopicNameResolver(@Value("\${tracking.infrastructure.kafka.topic-prefix}") private val channelPrefix: String) {

    fun resolveTrackingTopic(trackingData: TrackingData) = "$channelPrefix${trackingData.team}"

    fun resolveTrackingTopicPatternMatching(): Pattern {
        return Pattern.compile("$channelPrefix.*")
    }

}