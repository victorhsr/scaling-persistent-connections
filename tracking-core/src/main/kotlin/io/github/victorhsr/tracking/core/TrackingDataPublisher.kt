package io.github.victorhsr.tracking.core

interface TrackingDataPublisher {

    suspend fun publish(trackingData: TrackingData)

}
