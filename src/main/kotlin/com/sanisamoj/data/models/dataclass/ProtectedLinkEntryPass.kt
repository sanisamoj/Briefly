package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class ProtectedLinkEntryPass(
    val shortLink: String,
    val password: String
)