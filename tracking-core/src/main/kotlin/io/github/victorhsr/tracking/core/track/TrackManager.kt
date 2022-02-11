package io.github.victorhsr.tracking.core.track

import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory

class TrackManager(private val teamFlowKeeper: TeamFlowKeeper, private val trackPublisher: TrackPublisher) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TrackManager::class.java)
    }

    suspend fun publishNewTrackAcrossInstances(track: Track) = coroutineScope {
        LOGGER.info("Publishing a new track across the service instances {}", track)
        this@TrackManager.trackPublisher.publish(track)
    }

    suspend fun pushTrackToActiveStream(track: Track) {
        LOGGER.info("Pushing track {}", track)
        this.teamFlowKeeper.pushTrack(track)
    }

    suspend fun retrieveTeamFlow(teamName: String) = this.teamFlowKeeper.getTeamFlow(teamName)

}