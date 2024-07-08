package com.sanisamoj.data.models.dataclass

data class Sessions(
    val accountId: String,
    val liveSessions: List<SessionEntry> = emptyList(),
    val revokedSessions: List<SessionEntry> = emptyList()
)
