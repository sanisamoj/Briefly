package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class SavedMediaResponse(
    val filename: String,
    val fileLink: String
)
