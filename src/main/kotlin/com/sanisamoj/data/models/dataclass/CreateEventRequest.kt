package com.sanisamoj.data.models.dataclass

import com.sanisamoj.data.models.enums.EventSeverity
import kotlinx.serialization.Serializable

@Serializable
data class CreateEventRequest(
    val serviceName: String? = null,
    val eventType: String,
    val errorCode: String? = null,
    val message: String,
    val description: String? = null,
    val severity: String = EventSeverity.LOW.name,
    val stackTrace: String? = null,
    val additionalData: Map<String, String>? = null,
)
