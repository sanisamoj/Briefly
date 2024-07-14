package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class Region(
    val city: String,
    val region: String,
    val country: String,
    val zipcode: String
)
