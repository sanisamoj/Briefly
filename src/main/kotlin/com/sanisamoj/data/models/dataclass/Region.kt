package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class Region(
    val city: String,
    val zipcode: String,
)
