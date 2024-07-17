package com.sanisamoj.utils.pagination

import kotlin.math.ceil

fun paginationMethod(totalItems: Double, pageSize: Int, page: Int): PaginationResponse {
    val totalPages = ceil(totalItems / pageSize).toInt()
    val remainingPage = totalPages - page

    val response = PaginationResponse(
        totalPages = totalPages,
        remainingPage = remainingPage
    )

    return response
}