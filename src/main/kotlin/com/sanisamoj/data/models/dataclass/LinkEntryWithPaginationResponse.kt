package com.sanisamoj.data.models.dataclass

import com.sanisamoj.utils.pagination.PaginationResponse
import kotlinx.serialization.Serializable

@Serializable
data class LinkEntryWithPaginationResponse(
    val linkEntryList: List<LinkEntryResponse>,
    val paginationResponse: PaginationResponse
)
