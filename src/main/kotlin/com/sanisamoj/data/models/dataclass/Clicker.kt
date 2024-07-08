package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Clicker(
    val ip: String,
    val region: Region,
    val deviceInfo: DeviceInfo,
    val clickCount: Int,
    val clickedAt: String = LocalDateTime.now().toString()
)
