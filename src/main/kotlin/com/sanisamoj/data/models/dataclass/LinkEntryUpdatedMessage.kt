package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class LinkEntryUpdatedMessage(
    val userId: String,
    val shortLink: String,
    val clicker: ClickerResponse
)
