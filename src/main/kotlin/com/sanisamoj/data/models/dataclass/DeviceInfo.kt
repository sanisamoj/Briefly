package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class DeviceInfo(
    val deviceType: String,
    val operatingSystem: String,
    val browser: String
)