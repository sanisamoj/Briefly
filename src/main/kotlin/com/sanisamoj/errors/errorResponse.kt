package com.sanisamoj.errors

import com.sanisamoj.data.models.dataclass.ErrorResponse
import com.sanisamoj.data.models.enums.Errors
import io.ktor.http.*

fun errorResponse(errorMessage: String?): Pair<HttpStatusCode, ErrorResponse> {
    val response = when(errorMessage) {
        Errors.UserAlreadyExists.description -> {
            HttpStatusCode.Conflict to ErrorResponse(Errors.UserAlreadyExists.description)
        }

        Errors.DataIsMissing.description -> {
            HttpStatusCode.BadRequest to ErrorResponse(Errors.DataIsMissing.description)
        }

        Errors.UserNotFound.description -> {
            HttpStatusCode.Conflict to ErrorResponse(Errors.UserNotFound.description)
        }

        else -> {
            HttpStatusCode.InternalServerError to ErrorResponse(Errors.InternalServerError.description)
        }
    }

    return response
}