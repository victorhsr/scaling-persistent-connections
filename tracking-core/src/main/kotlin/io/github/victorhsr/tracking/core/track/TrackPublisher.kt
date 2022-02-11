package io.github.victorhsr.tracking.core.track

interface TrackPublisher {

    suspend fun publish(track: Track)

}
