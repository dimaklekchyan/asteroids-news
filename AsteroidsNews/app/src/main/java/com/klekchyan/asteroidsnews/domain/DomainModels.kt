package com.klekchyan.asteroidsnews.domain

import com.klekchyan.asteroidsnews.database.DatabaseFavoriteAsteroid
import com.klekchyan.asteroidsnews.network.AverageSize
import java.util.*

data class SimpleAsteroid(
    val id: Long,
    val name: String,
    val averageSize: AverageSize,
    val isHazardous: Boolean,
    val closeApproachDate: Date,
    val orbitingBody: String
)

data class ExtendedAsteroid(
    val id: Long,
    val name: String,
    val nasaJplUrl: String,
    val absoluteMagnitude: Double,
    val minDiameterMeters: Double,
    val maxDiameterMeters: Double,
    val isHazardous: Boolean,
    val closeApproachData: List<CloseApproachData>,
    val orbitId: String,
    val firstObservationDate: String,
    val lastObservationDate: String,
    val dataArcInDays: String,
    val orbitUncertainly: String,
    val minimumOrbitIntersection: String,
    val jupiterTisserandInvariant: String,
    val eccentricity: String,
    val semiMajorAxis: String,
    val inclination: String,
    val ascendingNodeLongitude: String,
    val orbitalPeriod: String,
    val perihelionDistance: String,
    val perihelionArgument: String,
    val aphelionDistance: String,
    val perihelionTime: String,
    val meanAnomaly: String,
    val meanMotion: String,
    val equinox: String,
    val orbitClassType: String,
    val orbitClassDescription: String,
    val orbitClassRange: String,
    val isSentryObject: Boolean
)

data class CloseApproachData(
    val closeApproachDate: Date,
    val kilometersPerHourVelocity: Double,
    val astronomicalMissDistance: Double,
    val kilometersMissDistance: Double,
    val orbitingBody: String
) {
    val planet: Planet
        get() = Planet.valueOf(orbitingBody.uppercase())
}

enum class Planet(val nameOfPlanet: String ){
    MERC("Mercury"),
    VENUS("Venus"),
    EARTH("Earth"),
    MARS("Mars"),
    JUPTR("Jupiter"),
    MOON("Moon")
//    SATURN,
//    URANUS,
//    NEPTUNE
}

fun SimpleAsteroid.asDatabaseFavoriteModel(): DatabaseFavoriteAsteroid{
    return DatabaseFavoriteAsteroid(
        id = this.id,
        name = this.name,
        averageSize = this.averageSize,
        isHazardous = this.isHazardous,
        closeApproachDate = this.closeApproachDate.time,
        orbitingBody = this.orbitingBody
    )
}