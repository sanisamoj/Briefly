package com.sanisamoj.data.models.dataclass
import kotlinx.serialization.Serializable

@Serializable
data class LinkEntryFromLoginResponse(
    val active: Boolean,
    val shortLink: String,
    val originalLink: String,
    val expiresAt: String,
)
