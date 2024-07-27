package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class MidLinkEntryResponse(
    val active: Boolean,
    val shortLink: String,
    val qrCodeLink: String,
    val originalLink: String,
    val totalVisits: List<ClickerResponse>,
    val expiresAt: String,
    val createAt: String
)
