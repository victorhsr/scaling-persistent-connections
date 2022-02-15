package io.github.victorhsr.tracking.trackingredis.integrationtests

import io.github.victorhsr.tracking.core.TrackingData
import io.github.victorhsr.tracking.trackingredis.infrastructure.redis.RedisConfiguration
import org.springframework.data.redis.core.ReactiveRedisOperations

@Throws(IllegalStateException::class)
fun RedisTestContainer.getTrackingDataRedisTemplate(): ReactiveRedisOperations<String, TrackingData> {
    if (!this.isRunning)
        throw IllegalStateException()

    val redisConfiguration = RedisConfiguration()

    val redisConnectionFactory =
        redisConfiguration.reactiveRedisConnectionFactory(this.containerIpAddress, this.firstMappedPort)
    redisConnectionFactory.afterPropertiesSet()

    return redisConfiguration.customerSessionRedisOperations(redisConnectionFactory)
}