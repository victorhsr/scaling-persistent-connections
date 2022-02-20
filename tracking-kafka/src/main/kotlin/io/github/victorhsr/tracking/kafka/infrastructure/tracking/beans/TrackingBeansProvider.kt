package io.github.victorhsr.tracking.kafka.infrastructure.tracking.beans

import io.github.victorhsr.tracking.core.TeamFlowKeeper
import io.github.victorhsr.tracking.core.TrackingDataManager
import io.github.victorhsr.tracking.core.TrackingDataPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TrackingBeansProvider {

    @Bean
    fun getTeamFlowKeeper() = TeamFlowKeeper()

    @Bean
    fun getTrackingManager(teamFlowKeeper: TeamFlowKeeper, trackingDataPublisher: TrackingDataPublisher) =
        TrackingDataManager(teamFlowKeeper, trackingDataPublisher)

}