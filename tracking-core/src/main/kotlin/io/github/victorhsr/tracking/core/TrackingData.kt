package io.github.victorhsr.tracking.core

import java.time.LocalDateTime

data class TrackingData(
    val id: String,
    val workerId: String,
    val team: String,
    val location: Location,
    val timeStamp: LocalDateTime,
) {
    companion object
}

data class Location(val latitude: String, val longitude: String) {
    companion object
}