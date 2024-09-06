package com.sanisamoj.data.models.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class GlobalWarnings(
    val systemName: String,
    val activateYourAccount: String,
    val welcomeToBriefly: String,
    val removedLink: String,
    val suggestion: String,
    val abuse: String,
    val accountRemoval: String,
    val thisYourValidationCode: String,
    val linkDeletedMail: String,
    val someExpirationLinkTime: String
)