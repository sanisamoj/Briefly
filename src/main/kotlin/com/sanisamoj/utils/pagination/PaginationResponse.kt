package com.sanisamoj.utils.pagination

import kotlinx.serialization.Serializable

@Serializable
data class PaginationResponse(
    val totalPages: Int,
    val remainingPage: Int,
)