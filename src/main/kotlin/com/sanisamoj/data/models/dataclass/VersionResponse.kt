package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class VersionResponse(
    val serverVersion: String,
    val mobileMinVersion: String,
    val mobileTargetVersion: String
)
