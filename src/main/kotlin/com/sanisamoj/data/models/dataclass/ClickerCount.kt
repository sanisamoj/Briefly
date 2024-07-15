package com.sanisamoj.data.models.dataclass

import java.time.LocalDateTime

data class ClickerCount(
    val ip: String,
    val route: String,
    val clickedAt: String = LocalDateTime.now().toString()
)
