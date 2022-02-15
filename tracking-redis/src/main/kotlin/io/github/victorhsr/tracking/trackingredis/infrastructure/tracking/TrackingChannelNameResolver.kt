package io.github.victorhsr.tracking.trackingredis.infrastructure.tracking

import io.github.victorhsr.tracking.core.TrackingData
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TrackingChannelNameResolver(@Value("\${tracking.infrastructure.redis.channel-prefix}") private val channelPrefix: String) {

    suspend fun resolveTrackingChannel(trackingData: TrackingData) = "$channelPrefix${trackingData.team}"

    suspend fun resolveTrackingChannelPatternMatching() = "$channelPrefix*"

}