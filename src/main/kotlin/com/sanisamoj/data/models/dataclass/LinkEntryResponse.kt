package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class LinkEntryResponse(
    val id: String,
    val userId: String,
    val active: Boolean,
    val shortLink: String,
    val originalLink: String,
    val totalVisits: List<ClickerResponse>,
    val expiresAt: String
)
