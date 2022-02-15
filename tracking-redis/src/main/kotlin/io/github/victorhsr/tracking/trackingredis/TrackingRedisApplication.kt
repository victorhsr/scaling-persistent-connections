package io.github.victorhsr.tracking.trackingredis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Configuration
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
class CORSGlobalConfiguration : WebFluxConfigurer {

	override fun addCorsMappings(corsRegistry: CorsRegistry) {
		corsRegistry.addMapping("/**")
			.allowedOrigins("*")
			.allowedMethods("GET", "POST", "PATCH")
			.maxAge(3600)
	}

}