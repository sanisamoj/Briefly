package com.sanisamoj.data.models.dataclass

import com.sanisamoj.data.models.enums.EventSeverity
import com.sanisamoj.data.models.enums.EventType
import kotlinx.serialization.Serializable

@Serializable
data class Log(
    val serviceName: String? = null,
    val eventType: String = EventType.ERROR.name,
    val errorCode: String? = null,
    val message: String,
    val description: String? = null,
    val severity: String = EventSeverity.LOW.name,
    val stackTrace: String? = null,
    val additionalData: Map<String, String>? = null,
)
