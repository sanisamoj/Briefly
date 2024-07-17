package com.sanisamoj.data.models.dataclass

import com.sanisamoj.utils.pagination.PaginationResponse
import kotlinx.serialization.Serializable

@Serializable
data class UsersWithPaginationResponse(
    val users: List<UserResponse>,
    val paginationResponse: PaginationResponse
)
