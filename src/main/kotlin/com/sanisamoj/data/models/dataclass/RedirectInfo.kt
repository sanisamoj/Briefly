package com.sanisamoj.data.models.dataclass

data class RedirectInfo(
    val ip: String,
    val shortLink: String,
    val userAgent: UserAgentInfo
)