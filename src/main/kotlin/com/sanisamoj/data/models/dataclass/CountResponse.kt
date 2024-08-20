package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class CountResponse(
    val item: String,
    val count: Int
)
