package io.github.victorhsr.tracking.trackingredis

import io.github.victorhsr.tracking.trackingredis.infrastructure.json.jackson.getSystemObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer


@EnableEurekaClient
@SpringBootApplication
class TrackingRedisApplication

fun main(args: Array<String>) {
    runApplication<TrackingRedisApplication>(*args)
}

@Configuration
@EnableWebFlux
class CustomWebFluxConfigurer : WebFluxConfigurer {

    override fun addCorsMappings(corsRegistry: CorsRegistry) {
        corsRegistry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PATCH")
            .maxAge(3600)
    }

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        val systemObjectMapper = getSystemObjectMapper()

        configurer.defaultCodecs().jackson2JsonDecoder(
            Jackson2JsonDecoder(systemObjectMapper)
        )
        configurer.defaultCodecs().jackson2JsonEncoder(
            Jackson2JsonEncoder(systemObjectMapper)
        )
    }
}