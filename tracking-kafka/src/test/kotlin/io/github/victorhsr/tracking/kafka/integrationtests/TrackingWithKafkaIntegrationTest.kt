package io.github.victorhsr.tracking.kafka.integrationtests

import com.fasterxml.jackson.module.kotlin.readValue
import io.github.victorhsr.tracking.core.TrackingData
import io.github.victorhsr.tracking.core.tracking.createRandom
import io.github.victorhsr.tracking.kafka.TrackingKafkaApplication
import io.github.victorhsr.tracking.kafka.infrastructure.json.jackson.getSystemObjectMapper
import io.github.victorhsr.tracking.kafka.infrastructure.kafka.KafkaConsumerConfiguration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.admin.Admin
import org.apache.kafka.clients.admin.NewTopic
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.http.codec.ServerSentEvent
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import reactor.core.publisher.Flux
import java.time.Duration

@Testcontainers
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [TrackingKafkaApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class TrackingWithKafkaIntegrationTest {

    companion object {
        @JvmStatic
        @Container
        val KAFKA = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))

        private fun resolveBootstrapServers() = "PLAINTEXT://localhost:${KAFKA.getMappedPort(9093)}"

        @JvmStatic
        @DynamicPropertySource
        fun setBootstrapServers(registry: DynamicPropertyRegistry) {
            registry.add("kafka.bootstrap-servers") {
                resolveBootstrapServers()
            }
        }

        @JvmStatic
        @BeforeAll
        fun createTopics() {

            val properties = KafkaConsumerConfiguration()
                .consumerFactory(resolveBootstrapServers())
                .configurationProperties

            Admin.create(properties).use { admin ->

                val partitions = 1
                val replicationFactor: Short = 1
                val teamOneTopic = NewTopic("TRACKING_team_one", partitions, replicationFactor)
                val teamTwoTopic = NewTopic("TRACKING_team_two", partitions, replicationFactor)
                val teamThreeTopic = NewTopic("TRACKING_team_three", partitions, replicationFactor)

                admin.createTopics(setOf(teamOneTopic, teamTwoTopic, teamThreeTopic))
                    .values().values.forEach { topicFuture -> topicFuture.get() }
            }
        }
    }

    @LocalServerPort
    val serverPort = 0
    val objectMapper = getSystemObjectMapper()

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `should subscribe into multiple tracking topics, publish tracking-data, get notified about these data, and push them to correspondent streams`() =
        runBlocking {

            println("serverPort = ${serverPort}")

            val teamOne = "team_one"
            val teamTwo = "team_two"
            val teamThree = "team_three"

            // subscribing (SSE) to get notified about all the teams
            val listenerFlow = listOf(teamOne, teamTwo, teamThree).map { team ->
                createRequestToListenToTrackingData(team)
            }.merge()

            // publishing some tracking data
            val trackingDataToPublish = arrayOf(TrackingData.createRandom(teamOne),
                TrackingData.createRandom(teamTwo),
                TrackingData.createRandom(teamTwo),
                TrackingData.createRandom(teamThree),
                TrackingData.createRandom(teamThree),
                TrackingData.createRandom(teamThree))
            val delayBetweenItems = 100L

            val deferredReceivedTrackingData = async { listenerFlow.take(trackingDataToPublish.size).toList() }
            delay(200)
            publishTrackingData(trackingDataToPublish, delayBetweenItems)

            // verifying if the listener has received all the tracking data
            val receivedTrackingData = deferredReceivedTrackingData.await()

            assertEquals(trackingDataToPublish.size, receivedTrackingData.size)
            assertTrue(receivedTrackingData.containsAll(trackingDataToPublish.toList()))
        }

    private suspend fun createRequestToListenToTrackingData(team: String): Flow<TrackingData> {

        val type: ParameterizedTypeReference<ServerSentEvent<String>> =
            object : ParameterizedTypeReference<ServerSentEvent<String>>() {}

        return getWebClientForSSE()
            .get()
            .uri("${resolveTrackingBaseUrl()}/$team")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(type)
            .map { it.data()!! }
            .map { this.objectMapper.readValue<TrackingData>(it) }
            .asFlow()
    }

    private suspend fun publishTrackingData(trackingData: Array<TrackingData>, delayBetweenItems: Long) {

        val publishingFlux =
            Flux.fromArray(trackingData).delayElements(Duration.ofMillis(delayBetweenItems))


        getWebClientForNDJSON()
            .post()
            .uri((resolveTrackingBaseUrl()))
            .contentType(MediaType.APPLICATION_NDJSON)
            .body(publishingFlux, TrackingData::class.java)
            .retrieve()
            .awaitBody<Unit>()
    }

    private fun resolveTrackingBaseUrl() = "http://localhost:$serverPort/tracking"

    private fun getWebClientForNDJSON(): WebClient {
        val strategies = ExchangeStrategies
            .builder()
            .codecs { clientCodecConfigurer: ClientCodecConfigurer ->
                clientCodecConfigurer.defaultCodecs()
                    .jackson2JsonEncoder(Jackson2JsonEncoder(this.objectMapper,
                        MediaType.APPLICATION_NDJSON))
                clientCodecConfigurer.defaultCodecs()
                    .jackson2JsonDecoder(Jackson2JsonDecoder(this.objectMapper,
                        MediaType.APPLICATION_NDJSON))
            }.build()

        return WebClient.builder().exchangeStrategies(strategies).build()
    }

    private fun getWebClientForSSE(): WebClient {
        return WebClient.create()
    }


}

