package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class LinkEntryRequest(
    val userId: String = "",
    val link: String,
    val active: Boolean = true,
    val expiresIn: String = LocalDateTime.now().toString()
)
