package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class UserCreateRequest(
    val username: String,
    val email: String,
    val password: String,
    val phone: String
)