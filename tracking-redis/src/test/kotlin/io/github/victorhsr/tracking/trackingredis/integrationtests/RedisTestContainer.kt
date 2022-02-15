package io.github.victorhsr.tracking.trackingredis.integrationtests

import org.testcontainers.containers.GenericContainer

class RedisTestContainer(dockerImage: String) : GenericContainer<RedisTestContainer>(dockerImage) {

    companion object {
        const val PORT = 6379
    }

    constructor() : this("redis:latest") {
        this.withExposedPorts(PORT)
    }

}