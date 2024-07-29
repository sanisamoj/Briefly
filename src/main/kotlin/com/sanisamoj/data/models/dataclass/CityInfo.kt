package com.sanisamoj.data.models.dataclass

data class CityInfo(
    val continentCode: String?,
    val continentName: String?,
    val countryIsoCode: String?,
    val countryName: String?,
    val cityGeonameId: Long?,
    val cityName: String?,
    val latitude: Double?,
    val longitude: Double?,
    val timeZone: String?,
    val postalCode: String?,
    val subdivisions: List<SubdivisionInfo>
)