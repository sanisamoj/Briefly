package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePhoneWithValidationCode(
    val phone: String,
    val validationCode: Int
)
