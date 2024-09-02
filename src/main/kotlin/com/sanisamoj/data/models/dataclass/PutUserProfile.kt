package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class PutUserProfile(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null
)
