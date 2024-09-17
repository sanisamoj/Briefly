package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val username: String,
    val profileImageUrl: String,
    val email: String,
    val phone: String,
    val type: String,
    val createdAt: String
)
