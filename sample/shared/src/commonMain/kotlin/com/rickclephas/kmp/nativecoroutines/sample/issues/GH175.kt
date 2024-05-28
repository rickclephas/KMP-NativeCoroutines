package com.rickclephas.kmp.nativecoroutines.sample.issues

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone

public data class Timeline(
    val records: List<Record>,
)

public data class Record(
    public val checkInId: String,
    public val checkInTime: Instant,
    public val checkInLocalTime: LocalTime?,
    public val checkInLocalDate: LocalDate?,
    public val checkInTimeZone: TimeZone?,
    public val checkInPlace: String,
    public val checkInDescription: String?,
    public val checkInTagId: String?,
    public val checkInTagName: String?,
    public val checkInTagEmoji: String?,
    public val placeId: String,
    public val placeLongitude: Double,
    public val placeLatitude: Double,
    public val placeDisplay: String?,
    public val placeDescription: String?,
    public val placeAdminArea: String?,
    public val placeLocality: String?,
    public val placeAddressString: String?,
    public val placeLink: String?,
    public val placeRating: Int,
    public val placeCountryCode: String?,
    public val tags: String?,
)

public data class LocationHistoryFilter(
    val test: String
)

public class Database {
    @NativeCoroutines
    public fun observeTimeline(locationHistoryFilter: LocationHistoryFilter): Flow<Timeline> =
        flowOf(Timeline(emptyList()))
}
