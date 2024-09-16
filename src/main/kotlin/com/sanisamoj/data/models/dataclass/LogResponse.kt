package com.sanisamoj.data.models.dataclass

data class LogResponse(
    val id: String,
    val applicationId: String,
    val number: Int,
    val applicationName: String,
    val serviceName: String?,
    val eventType: String,
    val errorCode: String?,
    val message: String,
    val description: String?,
    val severity: String,
    val stackTrace: String?,
    val additionalData: Map<String, String>?,
    val read: Boolean,
    val timestamp: String
)
