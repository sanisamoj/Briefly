package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class ClickerResponse(
    val region: Region,
    val deviceInfo: DeviceInfo,
    val clickCount: Int,
    val clickedAt: String
)
