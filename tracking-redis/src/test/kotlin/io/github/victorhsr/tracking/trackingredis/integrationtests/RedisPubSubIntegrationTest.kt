package io.github.victorhsr.tracking.trackingredis.integrationtests

import io.github.victorhsr.tracking.trackingredis.application.tracking.RedisTrackingSubscriber
import io.github.victorhsr.tracking.core.TeamFlowKeeper
import io.github.victorhsr.tracking.core.TrackingData
import io.github.victorhsr.tracking.core.TrackingDataManager
import io.github.victorhsr.tracking.core.createRandom
import io.github.victorhsr.tracking.trackingredis.infrastructure.tracking.RedisTrackingPublisher
import io.github.victorhsr.tracking.trackingredis.infrastructure.tracking.TrackingChannelNameResolver
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
internal class RedisPubSubIntegrationTest {

    companion object {
        @JvmStatic
        @Container
        private val redis: RedisTestContainer = RedisTestContainer()
    }

    @Test
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun `should subscribe into multiple tracking channels, publish tracking-data, get notified about these data, and push them to correspondent streams`() =
        runBlocking {

            val redisOperations = redis.getTrackingDataRedisTemplate()
            val trackingChannelNameResolver = TrackingChannelNameResolver("TRACKING_")
            val teamFlowKeeper = TeamFlowKeeper()
            val trackingDataPublisher =
                RedisTrackingPublisher(redisOperations, trackingChannelNameResolver)
            val trackingManager = TrackingDataManager(teamFlowKeeper, trackingDataPublisher)

            // starting the redis-sub
            val redisTrackingSubscriber = RedisTrackingSubscriber(redisOperations, trackingChannelNameResolver, trackingManager)
            redisTrackingSubscriber.startListening()

            // retrieving the stream where the track-data got by the redis-sub will be pushed
            val teamOne = "team_one"
            val teamTwo = "team_two"
            val teamThree = "team_three"

            val teamFlows = listOf(teamFlowKeeper.getTeamFlow(teamOne)!!,
                teamFlowKeeper.getTeamFlow(teamTwo)!!,
                teamFlowKeeper.getTeamFlow(teamThree)!!)


            // publishing some tracking-data
            val trackingDataToBePublished = listOf(TrackingData.createRandom(teamOne),
                TrackingData.createRandom(teamTwo),
                TrackingData.createRandom(teamTwo),
                TrackingData.createRandom(teamThree),
                TrackingData.createRandom(teamThree),
                TrackingData.createRandom(teamThree))

            val deferredTrackingDataFromRedisSub = async { teamFlows.merge().take(trackingDataToBePublished.size).toList() }

            trackingDataToBePublished.forEach { data -> trackingManager.publishNewTrackingDataAcrossInstances(data) }

            delay(200)

            // verifying if the tracking-data were gotten by the redis-sub and if it was pushed to the team stream
            val trackingDataFromRedisSub = deferredTrackingDataFromRedisSub.await()
            assertTrue(trackingDataFromRedisSub.containsAll(trackingDataToBePublished))
            redisTrackingSubscriber.onDestroy()
        }

}