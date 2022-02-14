package io.github.victorhsr.tracking.core

import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory

class TrackingDataManager(private val teamFlowKeeper: TeamFlowKeeper, private val trackingDataPublisher: TrackingDataPublisher) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TrackingDataManager::class.java)
    }

    suspend fun publishNewTrackAcrossInstances(trackingData: TrackingData) = coroutineScope {
        LOGGER.info("Publishing a new tracking-data across the service instances {}", trackingData)
        this@TrackingDataManager.trackingDataPublisher.publish(trackingData)
    }

    suspend fun pushTrackToActiveStream(trackingData: TrackingData) {
        LOGGER.info("Pushing tracking-data {}", trackingData)
        this.teamFlowKeeper.pushTrack(trackingData)
    }

    suspend fun retrieveTeamFlow(teamName: String) = this.teamFlowKeeper.getTeamFlow(teamName)

}