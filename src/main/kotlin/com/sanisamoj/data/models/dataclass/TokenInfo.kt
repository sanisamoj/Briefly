package com.sanisamoj.data.models.dataclass

data class TokenInfo(
    val id: String,
    val email: String,
    val sessionId: String,
    val secret: String,
    val time: Long
)