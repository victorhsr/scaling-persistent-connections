package io.github.victorhsr.tracking.core.tracking

import io.github.victorhsr.tracking.core.Location
import io.github.victorhsr.tracking.core.TrackingData
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.random.Random

fun TrackingData.Companion.createRandom(team: String) =
    TrackingData(id = UUID.randomUUID().toString(), workerId = UUID.randomUUID().toString(),
        team = team,
        location = Location.createRandom(),
        timeStamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
    )

fun Location.Companion.createRandom() = Location(latitude = Random.nextDouble(-90.0, 90.0).toString(),
    longitude = Random.nextDouble(-90.0, 90.0).toString())