package io.github.victorhsr.tracking.core.track

import java.time.LocalDateTime

data class Track(val id: String, val team: String, val location: Location, val timeStamp: LocalDateTime) {
    companion object
}

data class Location(val latitude: String, val longitude: String)