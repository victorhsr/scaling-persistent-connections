package io.github.victorhsr.tracking.trackingredis.infrastructure.redis

import io.github.victorhsr.tracking.core.TrackingData
import io.github.victorhsr.tracking.trackingredis.infrastructure.json.jackson.getSystemObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
class RedisConfiguration {

    @Bean
    fun reactiveRedisConnectionFactory(
        @Value("\${spring.redis.host}") host: String,
        @Value("\${spring.redis.port}") port: Int,
    ): LettuceConnectionFactory {
        return LettuceConnectionFactory(host, port)
    }

    @Bean
    fun customerSessionRedisOperations(factory: ReactiveRedisConnectionFactory): ReactiveRedisOperations<String, TrackingData> {

        val jackson2JsonRedisSerializer = Jackson2JsonRedisSerializer(TrackingData::class.java)
        jackson2JsonRedisSerializer.setObjectMapper(getSystemObjectMapper())

        val context = RedisSerializationContext
            .newSerializationContext<String, TrackingData>(StringRedisSerializer())
            .value(jackson2JsonRedisSerializer)
            .build()

        return ReactiveRedisTemplate(factory, context)
    }

}