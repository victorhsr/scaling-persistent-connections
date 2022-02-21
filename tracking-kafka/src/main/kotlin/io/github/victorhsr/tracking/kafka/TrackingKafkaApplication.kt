package io.github.victorhsr.tracking.kafka

import io.github.victorhsr.tracking.kafka.infrastructure.json.jackson.getSystemObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer

@SpringBootApplication
class TrackingKafkaApplication

fun main(args: Array<String>) {
    runApplication<TrackingKafkaApplication>(*args)
}

@Configuration
@EnableWebFlux
class CustomWebFluxConfigurer : WebFluxConfigurer {

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