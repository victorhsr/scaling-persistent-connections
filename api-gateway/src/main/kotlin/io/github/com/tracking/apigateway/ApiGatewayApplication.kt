package io.github.com.tracking.apigateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@EnableEurekaClient
@SpringBootApplication
class ApiGatewayApplication

fun main(args: Array<String>) {
    runApplication<ApiGatewayApplication>(*args)
}
