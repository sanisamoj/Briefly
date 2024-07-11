package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class IpInfo(
    val ip: String,
    val hostname: String,
    val city: String,
    val region: String,
    val country: String,
    val loc: String,
    val org: String,
    val postal: String,
    val timezone: String
)