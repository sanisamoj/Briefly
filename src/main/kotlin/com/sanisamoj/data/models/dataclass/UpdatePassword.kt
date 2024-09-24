package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePassword(
    val newPassword: String
)
