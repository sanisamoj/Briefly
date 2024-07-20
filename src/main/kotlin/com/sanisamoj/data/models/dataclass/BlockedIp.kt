package com.sanisamoj.data.models.dataclass

import java.time.LocalDateTime

data class BlockedIp(
    val ip: String,
    val route: String? = null,
    val createdAt: String = LocalDateTime.now().toString()
)
