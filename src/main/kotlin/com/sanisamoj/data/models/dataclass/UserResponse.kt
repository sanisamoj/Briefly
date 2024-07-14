package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val username: String,
    val email: String,
    val phone: String,
    val linkEntryList: List<LinkEntryResponse>,
    val createdAt: String
)
