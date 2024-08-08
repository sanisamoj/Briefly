package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class GlobalWarnings(
    val activateYourAccount: String,
    val welcomeToBriefly: String,
    val removedLink: String
)