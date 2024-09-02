package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class PutUserProfile(
    val name: String? = null,
    val phone: String? = null,
    val password: String? = null,
    val validationCode: Int? = null
)
