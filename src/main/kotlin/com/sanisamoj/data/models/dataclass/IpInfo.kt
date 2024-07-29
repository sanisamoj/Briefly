package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class IpInfo(
    val ip: String,
    val hostname: String? = null,
    val city: String? = null,
    val cityIsoCode: String? = null,
    val country: String? = null,
    val countryIsoCode: String? = null,
    val continent: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val postal: String? = null,
    val timezone: String? = null
)