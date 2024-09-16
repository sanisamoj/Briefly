package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class ApplicationServiceLoginRequest(
    val applicationName: String,
    val password: String
)
