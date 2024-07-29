package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class Region(
    val city: String,
    val cityIsoCode: String,
    val country: String,
    val countryIsoCode: String,
    val continent: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val postal: String,
    val timezone: String
)
