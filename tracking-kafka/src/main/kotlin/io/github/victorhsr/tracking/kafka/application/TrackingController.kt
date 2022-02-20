package io.github.victorhsr.tracking.kafka.application

import io.github.victorhsr.tracking.core.TrackingData
import io.github.victorhsr.tracking.core.TrackingDataManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.reactive.asFlow
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/tracking")
class TrackingController(private val trackingDataManager: TrackingDataManager) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TrackingController::class.java)
    }

    @GetMapping("/{team}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    suspend fun subscribeToCollectTrackingData(@PathVariable("team") team: String): Flow<TrackingData> {
        LOGGER.info("Registering to collect the task stream from team {}", team)
        return this.trackingDataManager.retrieveTeamFlow(team)!!
    }

    @PostMapping(consumes = [MediaType.APPLICATION_NDJSON_VALUE])
    suspend fun insertTrackingData(@RequestBody trackingFlux: Flux<TrackingData>) {
        trackingFlux.asFlow().collect(trackingDataManager::publishNewTrackingDataAcrossInstances)
    }

}