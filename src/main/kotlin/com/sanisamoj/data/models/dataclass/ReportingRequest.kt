package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class ReportingRequest(
    val username: String? = null,
    val email: String? = null,
    val reportType: String,
    val reporting: String
)
