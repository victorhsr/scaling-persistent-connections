package io.github.victorhsr.tracking.trackingredis.application.tracking

import io.github.victorhsr.tracking.core.TrackingData
import io.github.victorhsr.tracking.core.TrackingDataManager
import io.github.victorhsr.tracking.trackingredis.infrastructure.tracking.TrackingChannelNameResolver
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
class RedisTrackingSubscriber(
    private val reactiveRedisOperations: ReactiveRedisOperations<String, TrackingData>,
    private val trackingChannelNameResolver: TrackingChannelNameResolver,
    private val trackingDataManager: TrackingDataManager,
) {

    private val scope = CoroutineScope(CoroutineName("redis-tracking-subscriber"))

    companion object {
        private val LOGGER = LoggerFactory.getLogger(RedisTrackingSubscriber::class.java)
    }

    @PostConstruct
    fun startListening() {

        this.scope.launch {
            reactiveRedisOperations
                .listenToPattern(trackingChannelNameResolver.resolveTrackingChannelPatternMatching())
                .asFlow()
                .collect { message ->
                    LOGGER.info("Receiving message from channel {}, content: {}", message.channel, message.message)
                    trackingDataManager.pushTrackingDataToActiveStream(message.message)
                }
        }
    }

    @PreDestroy
    fun onDestroy() {
        this.scope.cancel()
    }

}

