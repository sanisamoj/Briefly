package com.sanisamoj.data.models.dataclass

import java.time.LocalDateTime

data class SessionEntry(
    val sessionId: String,
    val since: String = LocalDateTime.now().toString()
)