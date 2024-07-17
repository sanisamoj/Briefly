package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class IpInfo(
    val ip: String,
    val hostname: String?,
    val city: String?,
    val region: String?,
    val country: String?,
    val loc: String?,
    val org: String?,
    val postal: String?,
    val bogon: Boolean = false,
    val timezone: String = LocalDateTime.now().toString()
)