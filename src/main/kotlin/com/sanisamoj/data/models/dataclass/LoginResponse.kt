package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val account: UserResponse,
    val token: String
)
