package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class LinkEntryRequest(
    val userId: String = "",
    val link: String,
    val personalizedCode: String? = null,
    val password: String? = null,
    val active: Boolean = true,
    val expiresIn: String? = null
)
