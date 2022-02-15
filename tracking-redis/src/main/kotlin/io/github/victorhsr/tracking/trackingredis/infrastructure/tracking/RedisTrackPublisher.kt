package io.github.victorhsr.tracking.trackingredis.infrastructure.tracking

import io.github.victorhsr.tracking.core.TrackingData
import io.github.victorhsr.tracking.core.TrackingDataPublisher
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.stereotype.Component

@Component
class RedisTrackingPublisher(
    private val redisTemplate: ReactiveRedisOperations<String, TrackingData>,
    private val trackingChannelNameResolver: TrackingChannelNameResolver,
) : TrackingDataPublisher {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(RedisTrackingPublisher::class.java)
    }

    override suspend fun publish(trackingData: TrackingData) {
        val trackingChannel = this.trackingChannelNameResolver.resolveTrackingChannel(trackingData)
        LOGGER.info("Publishing tracking-data {} on channel {}", trackingData, trackingChannel)

        this.redisTemplate.convertAndSend(trackingChannel, trackingData).awaitSingle()
    }
}