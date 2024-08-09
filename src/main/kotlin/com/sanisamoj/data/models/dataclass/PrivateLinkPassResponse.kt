package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class PrivateLinkPassResponse(
    val originalLink: String
)